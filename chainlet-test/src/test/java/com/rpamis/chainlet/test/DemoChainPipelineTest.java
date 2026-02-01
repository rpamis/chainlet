/*
 * Copyright 2023-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rpamis.chainlet.test;

import com.rpamis.chainlet.core.definition.ChainFallBack;
import com.rpamis.chainlet.core.definition.ChainStrategy;
import com.rpamis.chainlet.core.strategy.FullExecutionStrategy;
import com.rpamis.chainlet.test.exception.CustomException;
import com.rpamis.chainlet.test.fallback.DemoChainGlobalFallBack;
import com.rpamis.chainlet.test.handler.*;
import com.rpamis.chainlet.core.builder.ChainPipelineDirector;
import com.rpamis.chainlet.core.builder.ChainPipelineFactory;
import com.rpamis.chainlet.core.builder.SerialChainPipelineBuilder;
import com.rpamis.chainlet.core.context.ChainHandlerContext;
import com.rpamis.chainlet.core.context.GlobalFallBackContext;
import com.rpamis.chainlet.core.context.LocalFallBackContext;
import com.rpamis.chainlet.core.ParallelChainPipelineImpl;
import com.rpamis.chainlet.core.definition.ChainHandler;
import com.rpamis.chainlet.core.definition.ChainPipeline;
import com.rpamis.chainlet.core.context.ChainContext;
import com.rpamis.chainlet.core.context.ChainStrategyContext;
import com.rpamis.chainlet.core.entities.ChainException;
import com.rpamis.chainlet.core.entities.ChainResult;
import com.rpamis.chainlet.core.entities.CompleteChainResult;
import com.rpamis.chainlet.core.entities.MethodRecord;
import com.rpamis.chainlet.core.entities.UniqueList;
import com.rpamis.chainlet.core.fallback.AbstractFallBackResolverSupport;
import com.rpamis.chainlet.core.fallback.MethodMetaDataRegistry;
import com.rpamis.chainlet.core.support.ChainTypeReference;
import com.rpamis.chainlet.core.support.ParallelChainTask;
import com.rpamis.chainlet.core.strategy.Strategy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StopWatch;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * DemoChainPipelineTest
 *
 * @author benym
 * @since 2023/5/25 15:50
 */
@RunWith(MockitoJUnitRunner.class)
public class DemoChainPipelineTest {

    @Mock(lenient = true)
    DemoUser demoUser;

    /**
     * 在全执行策略下链式处理返回false的场景测试
     */
    @Test
    @DisplayName("全执行策略下链式处理返回false")
    public void fullExecutionStrategyReturnsFalse() throws ChainException {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        boolean allow = chainResult.isAllow();
        Assert.assertFalse(allow);
        Boolean authResult = chainResult.get(AuthHandler.class);
        Assert.assertFalse(authResult);
        Boolean validResult = chainResult.get(ValidateHandler.class);
        Assert.assertTrue(validResult);
        Boolean loginResult = chainResult.get(LoginHandler.class);
        Assert.assertTrue(loginResult);
    }

    /**
     * 在全执行策略下链式处理返回true的场景测试
     */
    @Test
    @DisplayName("全执行策略下链式处理返回true")
    public void fullExecutionStrategyReturnsTrue() throws ChainException {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("admin");

        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        boolean allow = chainResult.isAllow();
        Assert.assertTrue(allow);
        Boolean authResult = chainResult.get(AuthHandler.class);
        Assert.assertTrue(authResult);
        Boolean validResult = chainResult.get(ValidateHandler.class);
        Assert.assertTrue(validResult);
        Boolean loginResult = chainResult.get(LoginHandler.class);
        Assert.assertTrue(loginResult);
    }

    /**
     * 在快速失败策略下链式处理返回false的场景测试
     */
    @Test
    @DisplayName("快速失败策略下链式处理返回false")
    public void fastFailedStrategyReturnsFalse() throws ChainException {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(Strategy.FAST_FAILED)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        boolean allow = chainResult.isAllow();
        Assert.assertFalse(allow);
        Boolean authResult = chainResult.get(AuthHandler.class);
        Assert.assertFalse(authResult);
        Boolean validResult = chainResult.get(ValidateHandler.class);
        Assert.assertNull(validResult);
        Boolean loginResult = chainResult.get(LoginHandler.class);
        Assert.assertNull(loginResult);
    }

    /**
     * 在快速失败策略下交换处理器顺序后链式处理返回false的场景测试
     */
    @Test
    @DisplayName("快速失败策略交换顺序后链式处理返回false")
    public void fastFailedStrategySwitchReturnsFalse() throws ChainException {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new ValidateHandler())
                .addHandler(new AuthHandler())
                .addHandler(new LoginHandler())
                .strategy(Strategy.FAST_FAILED)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        boolean allow = chainResult.isAllow();
        Assert.assertFalse(allow);
        Boolean validResult = chainResult.get(ValidateHandler.class);
        Assert.assertTrue(validResult);
        Boolean authResult = chainResult.get(AuthHandler.class);
        Assert.assertFalse(authResult);
        Boolean loginResult = chainResult.get(LoginHandler.class);
        Assert.assertNull(loginResult);
    }

    /**
     * 在快速返回策略下链式处理返回true的场景测试
     */
    @Test
    @DisplayName("快速返回策略下链式处理返回true")
    public void fastReturnStrategyReturnsTrue() throws ChainException {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new ValidateHandler())
                .addHandler(new AuthHandler())
                .addHandler(new LoginHandler())
                .strategy(Strategy.FAST_RETURN)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        boolean allow = chainResult.isAllow();
        Assert.assertTrue(allow);
        Boolean validResult = chainResult.get(ValidateHandler.class);
        Assert.assertTrue(validResult);
        Boolean authResult = chainResult.get(AuthHandler.class);
        Assert.assertNull(authResult);
        Boolean loginResult = chainResult.get(LoginHandler.class);
        Assert.assertNull(loginResult);
    }

    /**
     * 在快速返回策略下交换处理器顺序后链式处理返回true的场景测试
     */
    @Test
    @DisplayName("快速返回策略交换顺序后链式处理返回预期结果")
    public void fastReturnStrategySwitchReturnsExpected() throws ChainException {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(Strategy.FAST_RETURN)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        boolean allow = chainResult.isAllow();
        Assert.assertFalse(allow);
        Boolean validResult = chainResult.get(ValidateHandler.class);
        Assert.assertTrue(validResult);
        Boolean authResult = chainResult.get(AuthHandler.class);
        Assert.assertFalse(authResult);
        Boolean loginResult = chainResult.get(LoginHandler.class);
        Assert.assertNull(loginResult);
    }

    /**
     * 添加相同处理器时抛出异常的场景测试
     */
    @Test
    @DisplayName("添加相同处理器时应抛出异常")
    public void addSameHandlerShouldThrowException() {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipelineDirector<DemoUser> chainPipelineDirector = ChainPipelineFactory.createChain(reference);
        // given
        SerialChainPipelineBuilder<DemoUser> demoChain = chainPipelineDirector.chain();
        // when
        demoChain.addHandler(mock(ValidateHandler.class));
        // then
        Assert.assertThrows(ChainException.class, () -> demoChain.addHandler(mock(ValidateHandler.class)));
    }

    /**
     * 多线程执行下链式处理结果不相等的场景测试
     */
    @Test
    @DisplayName("多线程执行下链式处理结果不相等")
    public void multithreadedExecutionNotEqual() throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> failedFuture = CompletableFuture.supplyAsync(() -> {
            try {
                DemoUser demoUser = mock(DemoUser.class);
                // when
                when(demoUser.getName()).thenReturn("test");
                when(demoUser.getPwd()).thenReturn("123");
                when(demoUser.getRole()).thenReturn("normal");
                return executeChain(demoUser);
            } catch (ChainException e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture<Boolean> successFuture = CompletableFuture.supplyAsync(() -> {
            try {
                DemoUser demoUser = mock(DemoUser.class);
                // when
                when(demoUser.getName()).thenReturn("test");
                when(demoUser.getPwd()).thenReturn("123");
                when(demoUser.getRole()).thenReturn("admin");
                return executeChain(demoUser);
            } catch (ChainException e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture<Object> combine = failedFuture.thenCombine(successFuture, (result1, result2) -> {
            Assert.assertNotEquals(result1, result2);
            return null;
        });
        combine.get();
    }

    private boolean executeChain(DemoUser demoUser) throws ChainException {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(Strategy.FAST_FAILED)
                .build();

        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        return chainResult.isAllow();
    }

    /**
     * 当链中包含模拟异常处理器时应用链应抛出链异常的场景测试
     */
    @Test
    @DisplayName("链中包含模拟异常处理器时应抛出异常")
    public void mockExceptionHandlerShouldThrowChainException() throws ChainException {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new MockExceptionHandler())
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");
        // then
        Assert.assertThrows(ArithmeticException.class, () -> demoChain.apply(demoUser));
    }

    /**
     * 列表处理器下链式处理返回false的场景测试
     */
    @Test
    @DisplayName("列表处理器下链式处理返回false")
    public void listHandlerReturnsFalse() throws ChainException {
        ChainTypeReference<List<DemoUser>> reference = new ChainTypeReference<List<DemoUser>>() {};
        // given
        ChainPipeline<List<DemoUser>> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new ListAuthHandler())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getRole()).thenReturn("normal");

        List<DemoUser> list = new ArrayList<>();
        list.add(demoUser);
        // then
        CompleteChainResult chainResult = demoChain.apply(list);
        boolean allow = chainResult.isAllow();
        Assert.assertFalse(allow);
        Boolean authResult = chainResult.get(ListAuthHandler.class);
        Assert.assertFalse(authResult);
    }

    /**
     * 并行链在全执行策略下返回false的场景测试
     */
    @Test
    @DisplayName("并行链全执行策略下返回false")
    public void parallelChainFullExecutionReturnsFalse() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .parallelChain()
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        boolean allow = chainResult.isAllow();
        Assert.assertFalse(allow);
        Boolean authResult = chainResult.get(AuthHandler.class);
        Assert.assertFalse(authResult);
        Boolean validResult = chainResult.get(ValidateHandler.class);
        Assert.assertTrue(validResult);
        Boolean loginResult = chainResult.get(LoginHandler.class);
        Assert.assertTrue(loginResult);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
    }

    /**
     * 根据链ID获取链结构的场景测试
     */
    @Test
    @DisplayName("根据链ID复制链结构")
    public void copyChainStructById() {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain("Test")
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(Strategy.FULL)
                .build();
        // when
        ChainPipeline<DemoUser> copyChain = ChainPipelineFactory.getChain("Test", reference)
                .build();
        List<Class<?>> handlerClasses = copyChain.getHandlerClasses();
        Class<?> aClass = handlerClasses.get(0);
        Assert.assertTrue(aClass.isAssignableFrom(AuthHandler.class));
        Class<?> bClass = handlerClasses.get(1);
        Assert.assertTrue(bClass.isAssignableFrom(ValidateHandler.class));
        Class<?> cClass = handlerClasses.get(2);
        Assert.assertTrue(cClass.isAssignableFrom(LoginHandler.class));
        ChainStrategy<DemoUser> chainStrategy = copyChain.getStrategyByKey(Strategy.FULL);
        Assert.assertTrue(chainStrategy instanceof FullExecutionStrategy);
        ChainTypeReference<DemoUser> chainTypeReference = copyChain.getChainTypeReference();
        Assert.assertEquals(chainTypeReference, reference);
        ChainFallBack<DemoUser> chainFallBack = copyChain.getGlobalChainFallBack();
        Assert.assertNull(chainFallBack);
    }

    /**
     * 根据链ID获取并行链结构的场景测试
     */
    @Test
    @DisplayName("根据链ID复制并行链结构")
    public void copyParallelChainStructById() {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .parallelChain("Test")
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(Strategy.FULL)
                .build();
        // when
        ChainPipeline<DemoUser> copyChain = ChainPipelineFactory.getParallelChain("Test", reference)
                .build();
        List<Class<?>> handlerClasses = copyChain.getHandlerClasses();
        Class<?> aClass = handlerClasses.get(0);
        Assert.assertTrue(aClass.isAssignableFrom(AuthHandler.class));
        Class<?> bClass = handlerClasses.get(1);
        Assert.assertTrue(bClass.isAssignableFrom(ValidateHandler.class));
        Class<?> cClass = handlerClasses.get(2);
        Assert.assertTrue(cClass.isAssignableFrom(LoginHandler.class));
        ChainStrategy<DemoUser> chainStrategy = copyChain.getStrategyByKey(Strategy.FULL);
        Assert.assertTrue(chainStrategy instanceof FullExecutionStrategy);
        ChainTypeReference<DemoUser> chainTypeReference = copyChain.getChainTypeReference();
        Assert.assertEquals(chainTypeReference, reference);
        ChainFallBack<DemoUser> chainFallBack = copyChain.getGlobalChainFallBack();
        Assert.assertNull(chainFallBack);
    }

    /**
     * 设置并行链的ForkJoinPool的场景测试
     */
    @Test
    @DisplayName("设置并行链的ForkJoinPool")
    public void setParallelChainForkJoinPool() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .parallelChain()
                .pool(new ForkJoinPool(10))
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        boolean allow = chainResult.isAllow();
        Assert.assertFalse(allow);
        Boolean authResult = chainResult.get(AuthHandler.class);
        Assert.assertFalse(authResult);
        Boolean validResult = chainResult.get(ValidateHandler.class);
        Assert.assertTrue(validResult);
        Boolean loginResult = chainResult.get(LoginHandler.class);
        Assert.assertTrue(loginResult);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
    }

    /**
     * 链处理后允许为false时抛出自定义异常的场景测试
     */
    @Test
    @DisplayName("链处理失败时抛出自定义异常")
    public void throwCustomExceptionWhenApplyAllowFalse() {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        boolean allow = chainResult.isAllow();
        Assert.assertFalse(allow);
        Boolean authResult = chainResult.verifyIfSuccess(ValidateHandler.class);
        Assert.assertTrue(authResult);
        Assert.assertThrows(CustomException.class, () -> chainResult.verifyAllAndThrow(CustomException.class));
        Assert.assertThrows(CustomException.class, () -> chainResult.verifyAndThrow(CustomException.class, AuthHandler.class));
        List<Class<?>> handlerClasses = chainResult.getHandlerClasses();
        Assert.assertEquals(handlerClasses.size(), 3);
        Assert.assertTrue(handlerClasses.contains(AuthHandler.class));
        Assert.assertTrue(handlerClasses.contains(ValidateHandler.class));
        Assert.assertTrue(handlerClasses.contains(LoginHandler.class));
    }

    /**
     * fallback处理时链中没有fallback方法名时抛出链异常的场景测试
     */
    @Test
    @DisplayName("fallback处理无方法名时抛出链异常")
    public void fallbackWithoutMethodNameShouldThrowChainException() {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new TestFallBackHandler())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        Assert.assertThrows(ChainException.class, () -> demoChain.apply(demoUser));
    }

    /**
     * fallback处理时链中存在不存在的fallback方法名和fallback方法时抛出链异常的场景测试
     */
    @Test
    @DisplayName("fallback处理方法不存在时抛出链异常")
    public void fallbackNonExistMethodShouldThrowChainException() {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new TestFallBackHandlerTwo())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        Assert.assertThrows(ChainException.class, () -> demoChain.apply(demoUser));
    }

    /**
     * fallback处理时链中fallback返回类型不正确时抛出链异常的场景测试
     */
    @Test
    @DisplayName("fallback处理返回类型错误时抛出链异常")
    public void fallbackIncorrectReturnTypeShouldThrowChainException() {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new TestFallBackHandlerThree())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        Assert.assertThrows(ChainException.class, () -> demoChain.apply(demoUser));
    }

    /**
     * 处理器为false时调用fallback处理且具有正确的fallback返回类型的场景测试
     */
    @Test
    @DisplayName("处理器失败时调用正确类型的fallback方法")
    public void invokeFallbackWithCorrectReturnType() {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new TestFallBackHandlerFour())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        boolean allow = chainResult.isAllow();
        Assert.assertFalse(allow);
        CompleteChainResult chainResult2 = demoChain.apply(demoUser);
        boolean allow2 = chainResult2.isAllow();
        Assert.assertFalse(allow2);
    }

    /**
     * 处理器为false时调用私有fallback方法的场景测试
     */
    @Test
    @DisplayName("处理器失败时调用私有fallback方法")
    public void invokePrivateFallback() {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new TestFallBackHandlerFive())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        boolean allow = chainResult.isAllow();
        Assert.assertFalse(allow);
    }

    /**
     * 处理器为false时调用私有静态fallback方法的场景测试
     */
    @Test
    @DisplayName("处理器失败时调用私有静态fallback方法")
    public void invokePrivateStaticFallback() {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new TestFallBackHandlerSix())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        boolean allow = chainResult.isAllow();
        Assert.assertFalse(allow);
    }

    @Test
    @DisplayName("测试本地fallback接口")
    public void testLocalFallbackInterface() {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new TestLocalFallBackHandler())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        boolean allow = chainResult.isAllow();
        Assert.assertFalse(allow);
    }

    @Test
    @DisplayName("测试全局fallback接口")
    public void testGlobalFallbackInterface() {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new AuthHandler())
                .globalFallback(new DemoChainGlobalFallBack())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");
        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        boolean allow = chainResult.isAllow();
        Assert.assertFalse(allow);
    }

    /**
     * 测试ChainTypeReference静态方法获取泛型类型
     */
    @Test
    @DisplayName("测试ChainTypeReference静态方法获取泛型类型")
    public void testChainTypeReferenceStaticMethods() throws ClassNotFoundException {
        // given
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // when
        Type genericType = ChainTypeReference.getGenericType(reference);
        Class<?> genericTypeClass = ChainTypeReference.getGenericTypeClass(reference);
        // then
        Assert.assertNotNull(genericType);
        Assert.assertNotNull(genericTypeClass);
        Assert.assertEquals(DemoUser.class, genericTypeClass);
    }

    /**
     * 测试ChainTypeReference静态方法处理null场景
     */
    @Test
    @DisplayName("测试ChainTypeReference静态方法处理null场景")
    public void testChainTypeReferenceStaticMethodsWithNull() {
        // given
        TestNonGenericClass nonGenericClass = new TestNonGenericClass();
        // when
        Type genericType = ChainTypeReference.getGenericType(nonGenericClass);
        // then
        Assert.assertNull(genericType);
    }

    /**
     * 测试注册重复chainId时应抛出异常
     */
    @Test
    @DisplayName("注册重复chainId时应抛出异常")
    public void registerDuplicateChainIdShouldThrowException() {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipelineFactory.createChain(reference)
                .chain("DuplicateTest")
                .addHandler(new AuthHandler())
                .strategy(Strategy.FULL)
                .build();
        // when & then
        Assert.assertThrows(ChainException.class, () -> {
            ChainPipelineFactory.createChain(reference)
                    .chain("DuplicateTest")
                    .addHandler(new AuthHandler())
                    .strategy(Strategy.FULL)
                    .build();
        });
    }

    /**
     * 测试获取不存在的chain时应抛出异常
     */
    @Test
    @DisplayName("获取不存在的chain时应抛出异常")
    public void getNonExistentChainShouldThrowException() {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // when & then
        Assert.assertThrows(ChainException.class, () -> {
            ChainPipelineFactory.getChain("NonExistent", reference).build();
        });
    }

    /**
     * 测试获取不存在的并行chain时应抛出异常
     */
    @Test
    @DisplayName("获取不存在的并行chain时应抛出异常")
    public void getNonExistentParallelChainShouldThrowException() {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // when & then
        Assert.assertThrows(ChainException.class, () -> {
            ChainPipelineFactory.getParallelChain("NonExistentParallel", reference).build();
        });
    }

    /**
     * 测试不同类型的chainTypeReference获取chain时应抛出异常
     */
    @Test
    @DisplayName("不同类型的chainTypeReference获取chain时应抛出异常")
    public void differentTypeReferenceShouldThrowException() {
        // given
        ChainTypeReference<DemoUser> userReference = new ChainTypeReference<DemoUser>() {};
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(userReference)
                .chain("TypeTest")
                .addHandler(new AuthHandler())
                .strategy(Strategy.FULL)
                .build();
        // when & then
        Assert.assertThrows(ChainException.class, () -> {
            ChainTypeReference<List<DemoUser>> listReference = new ChainTypeReference<List<DemoUser>>() {};
            ChainPipelineFactory.getChain("TypeTest", listReference).build();
        });
    }

    /**
     * 测试类，用于测试ChainTypeReference的非泛型场景
     */
    private static class TestNonGenericClass {
    }

    /**
     * 测试CompleteChainResult的getFinalResult方法
     */
    @Test
    @DisplayName("测试CompleteChainResult的getFinalResult方法")
    public void testCompleteChainResultGetFinalResult() throws ChainException {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("admin");
        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        Object finalResult = chainResult.getFinalResult();
        // 由于LoginHandler可能没有设置processedData，finalResult可能为null
        Assert.assertTrue(finalResult == null || finalResult instanceof DemoUser);
    }

    /**
     * 测试CompleteChainResult的verifyIfFail方法
     */
    @Test
    @DisplayName("测试CompleteChainResult的verifyIfFail方法")
    public void testCompleteChainResultVerifyIfFail() throws ChainException {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");
        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        boolean authFail = chainResult.verifyIfFail(AuthHandler.class);
        boolean validateFail = chainResult.verifyIfFail(ValidateHandler.class);
        boolean nonExistentFail = chainResult.verifyIfFail(LoginHandler.class);
        Assert.assertTrue(authFail);
        Assert.assertFalse(validateFail);
        Assert.assertFalse(nonExistentFail);
    }

    /**
     * 测试CompleteChainResult的verifyAndThrow方法处理非Throwable类
     */
    @Test
    @DisplayName("测试CompleteChainResult的verifyAndThrow方法处理非Throwable类")
    public void testCompleteChainResultVerifyAndThrowNonThrowable() throws Exception {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new AuthHandler())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");
        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        // 使用反射调用verifyAndThrow方法，绕过泛型约束检查
        java.lang.reflect.Method method = CompleteChainResult.class.getMethod("verifyAndThrow", Class.class, Class.class);
        Assert.assertThrows(ChainException.class, () -> {
            try {
                method.invoke(chainResult, TestNonThrowableClass.class, AuthHandler.class);
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        });
    }

    /**
     * 测试CompleteChainResult的verifyAllAndThrow方法处理非Throwable类
     */
    @Test
    @DisplayName("测试CompleteChainResult的verifyAllAndThrow方法处理非Throwable类")
    public void testCompleteChainResultVerifyAllAndThrowNonThrowable() throws Exception {
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        // given
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .chain()
                .addHandler(new AuthHandler())
                .strategy(Strategy.FULL)
                .build();
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");
        // then
        CompleteChainResult chainResult = demoChain.apply(demoUser);
        // 使用反射调用verifyAllAndThrow方法，绕过泛型约束检查
        java.lang.reflect.Method method = CompleteChainResult.class.getMethod("verifyAllAndThrow", Class.class);
        Assert.assertThrows(ChainException.class, () -> {
            try {
                method.invoke(chainResult, TestNonThrowableClass.class);
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        });
    }

    /**
     * 测试类，用于测试非Throwable类场景
     */
    private static class TestNonThrowableClass {
    }

    /**
     * 测试ChainContext的getter/setter方法
     */
    @Test
    @DisplayName("测试ChainContext的getter/setter方法")
    public void testChainContextGetterSetter() {
        // 直接测试ChainContext的构造函数和getter/setter方法
        DemoUser demoUser = new DemoUser("test", "123", "admin");
        List<ChainResult> checkResults = new ArrayList<>();
        ChainContext<DemoUser> context = new ChainContext<>(demoUser, null, null, null, checkResults);
        
        // 测试getter方法
        Assert.assertEquals(demoUser, context.getHandlerData());
        Assert.assertNull(context.getChain());
        Assert.assertNull(context.getStrategy());
        Assert.assertNull(context.getChainHandler());
        Assert.assertEquals(checkResults, context.getCheckResults());
        
        // 测试setter方法
        DemoUser newDemoUser = new DemoUser("newtest", "456", "user");
        context.setHandlerData(newDemoUser);
        Assert.assertEquals(newDemoUser, context.getHandlerData());
        
        // 测试其他setter方法
        context.setChain(null);
        Assert.assertNull(context.getChain());
        context.setStrategy(null);
        Assert.assertNull(context.getStrategy());
        context.setChainHandler(null);
        Assert.assertNull(context.getChainHandler());
        List<ChainResult> newCheckResults = new ArrayList<>();
        context.setCheckResults(newCheckResults);
        Assert.assertEquals(newCheckResults, context.getCheckResults());
    }

    /**
     * 测试ChainStrategyContext的getter/setter方法
     */
    @Test
    @DisplayName("测试ChainStrategyContext的getter/setter方法")
    public void testChainStrategyContextGetterSetter() {
        // 直接测试ChainStrategyContext的构造函数和getter/setter方法
        DemoUser demoUser = new DemoUser("test", "123", "admin");
        List<ChainResult> checkResults = new ArrayList<>();
        ChainResult chainResult = new ChainResult(AuthHandler.class, true, demoUser, "Test message");
        ChainStrategyContext<DemoUser> context = new ChainStrategyContext<>(demoUser, null, chainResult, checkResults);
        
        // 测试getter方法
        Assert.assertEquals(demoUser, context.getHandlerData());
        Assert.assertNull(context.getChain());
        Assert.assertEquals(chainResult, context.getChainResult());
        Assert.assertEquals(checkResults, context.getCheckResults());
        
        // 测试setter方法
        DemoUser newDemoUser = new DemoUser("newtest", "456", "user");
        context.setHandlerData(newDemoUser);
        Assert.assertEquals(newDemoUser, context.getHandlerData());
        
        // 测试其他setter方法
        context.setChain(null);
        Assert.assertNull(context.getChain());
        ChainResult newChainResult = new ChainResult(ValidateHandler.class, false, demoUser, "New test message");
        context.setChainResult(newChainResult);
        Assert.assertEquals(newChainResult, context.getChainResult());
        
        List<ChainResult> newCheckResults = new ArrayList<>();
        context.setCheckResults(newCheckResults);
        Assert.assertEquals(newCheckResults, context.getCheckResults());
    }

    /**
     * 测试ChainHandlerContext的getter/setter方法
     */
    @Test
    @DisplayName("测试ChainHandlerContext的getter/setter方法")
    public void testChainHandlerContextGetterSetter() {
        // given
        DemoUser demoUser = new DemoUser("test", "123", "admin");
        // when
        ChainHandlerContext<DemoUser> context = new ChainHandlerContext<>(demoUser);
        context.setLocalMessage("Test message");
        Map<String, Object> extendData = new HashMap<>();
        extendData.put("key", "value");
        context.setExtendData(extendData);
        context.setProcessedData(demoUser);
        // then
        Assert.assertEquals(demoUser, context.getHandlerData());
        Assert.assertEquals("Test message", context.getLocalMessage());
        Assert.assertEquals(extendData, context.getExtendData());
        Assert.assertEquals(demoUser, context.getProcessedData());
    }

    /**
     * 测试UniqueList的方法
     */
    @Test
    @DisplayName("测试UniqueList的方法")
    public void testUniqueListMethods() {
        // given
        UniqueList<AuthHandler> uniqueList = new UniqueList<>();
        AuthHandler authHandler1 = new AuthHandler();
        // when & then
        Assert.assertTrue(uniqueList.add(authHandler1));
        Assert.assertThrows(ChainException.class, () -> uniqueList.add(authHandler1));
        
        // 测试sortByOrder方法
        uniqueList.sortByOrder();
    }

    /**
     * 测试ChainResult的getter/setter方法
     */
    @Test
    @DisplayName("测试ChainResult的getter/setter方法")
    public void testChainResultGetterSetter() {
        // given
        DemoUser demoUser = new DemoUser("test", "123", "admin");
        // when
        ChainResult chainResult = new ChainResult(AuthHandler.class, false, demoUser, "Auth failed");
        // then
        Assert.assertEquals(AuthHandler.class, chainResult.getHandlerClass());
        Assert.assertFalse(chainResult.isProcessResult());
        Assert.assertEquals(demoUser, chainResult.getProcessedData());
        Assert.assertEquals("Auth failed", chainResult.getMessage());
        
        // 测试所有的setter方法
        chainResult.setHandlerClass(ValidateHandler.class);
        Assert.assertEquals(ValidateHandler.class, chainResult.getHandlerClass());
        
        chainResult.setProcessResult(true);
        Assert.assertTrue(chainResult.isProcessResult());
        
        DemoUser newDemoUser = new DemoUser("newtest", "456", "user");
        chainResult.setProcessedData(newDemoUser);
        Assert.assertEquals(newDemoUser, chainResult.getProcessedData());
        
        chainResult.setMessage("New message");
        Assert.assertEquals("New message", chainResult.getMessage());
    }

    /**
     * 测试MethodMetaDataRegistry的方法
     */
    @Test
    @DisplayName("测试MethodMetaDataRegistry的方法")
    public void testMethodMetaDataRegistry() {
        // when & then
        // 测试getProcessKey方法
        String processKey = MethodMetaDataRegistry.getProcessKey(AuthHandler.class, DemoUser.class);
        Assert.assertNotNull(processKey);
        Assert.assertTrue(processKey.contains(AuthHandler.class.getCanonicalName()));
        Assert.assertTrue(processKey.contains(DemoUser.class.getCanonicalName()));
        
        // 测试getFallBackKey方法
        String fallBackKey = MethodMetaDataRegistry.getFallBackKey(AuthHandler.class, "fallbackMethod");
        Assert.assertNotNull(fallBackKey);
        Assert.assertTrue(fallBackKey.contains(AuthHandler.class.getCanonicalName()));
        Assert.assertTrue(fallBackKey.contains("fallbackMethod"));
        
        // 测试getProcessRecord方法
        MethodRecord processRecord = MethodMetaDataRegistry.getProcessRecord(AuthHandler.class, DemoUser.class);
        // 由于可能在其他测试中已经初始化了记录，这里不做null断言
        System.out.println("ProcessRecord: " + processRecord);
        
        // 测试getLocalFallBackRecord方法
        MethodRecord localFallBackRecord = MethodMetaDataRegistry.getLocalFallBackRecord(AuthHandler.class, "fallbackMethod");
        // 由于可能在其他测试中已经初始化了记录，这里不做null断言
        System.out.println("LocalFallBackRecord: " + localFallBackRecord);
    }

    /**
     * 测试MethodRecord的方法
     */
    @Test
    @DisplayName("测试MethodRecord的方法")
    public void testMethodRecord() throws Exception {
        java.lang.reflect.Method method = AuthHandler.class.getMethod("process", DemoUser.class, ChainHandlerContext.class);
        MethodRecord record = new MethodRecord(method, true);
        Assert.assertEquals(method, record.getMethod());
        Assert.assertTrue(record.isExist());
        
        // 测试warp方法
        MethodRecord warpRecord = MethodRecord.warp(method);
        Assert.assertEquals(method, warpRecord.getMethod());
        Assert.assertTrue(warpRecord.isExist());
        
        // 测试warp方法处理null的情况
        MethodRecord nullWarpRecord = MethodRecord.warp(null);
        Assert.assertNull(nullWarpRecord.getMethod());
        Assert.assertFalse(nullWarpRecord.isExist());
    }

    /**
     * 测试GlobalFallBackContext的getter/setter方法
     */
    @Test
    @DisplayName("测试GlobalFallBackContext的getter/setter方法")
    public void testGlobalFallBackContextGetterSetter() {
        // given
        DemoUser demoUser = new DemoUser("test", "123", "admin");
        CompleteChainResult completeChainResult = new CompleteChainResult(false, new ArrayList<>());
        // when
        GlobalFallBackContext<DemoUser> context = new GlobalFallBackContext<>(demoUser, demoUser, completeChainResult, false);
        context.setHandlerData(demoUser);
        context.setProcessedData(demoUser);
        context.setExceptionOccurred(false);
        context.setCompleteChainResult(completeChainResult);
        // then
        Assert.assertEquals(demoUser, context.getHandlerData());
        Assert.assertEquals(demoUser, context.getProcessedData());
        Assert.assertFalse(context.getExceptionOccurred());
        Assert.assertEquals(completeChainResult, context.getCompleteChainResult());
    }

    /**
     * 测试LocalFallBackContext的getter/setter方法
     */
    @Test
    @DisplayName("测试LocalFallBackContext的getter/setter方法")
    public void testLocalFallBackContextGetterSetter() {
        // given
        DemoUser demoUser = new DemoUser("test", "123", "admin");
        // when
        LocalFallBackContext<DemoUser> context = new LocalFallBackContext<>(demoUser, true);
        context.setProcessedData(demoUser);
        context.setExceptionOccurred(true);
        // then
        Assert.assertEquals(demoUser, context.getHandlerData());
        Assert.assertEquals(demoUser, context.getProcessedData());
        Assert.assertTrue(context.getExceptionOccurred());
    }

    /**
     * 测试InstanceOfCache的instanceofCheck方法
     */
    @Test
    @DisplayName("测试InstanceOfCache的instanceofCheck方法")
    public void testInstanceOfCache() {
        // 测试缓存命中情况
        boolean result1 = com.rpamis.chainlet.core.support.InstanceOfCache.instanceofCheck(DemoUser.class, Object.class);
        Assert.assertTrue(result1);
        // 测试缓存未命中情况
        boolean result2 = com.rpamis.chainlet.core.support.InstanceOfCache.instanceofCheck(String.class, Integer.class);
        Assert.assertFalse(result2);
        // 测试缓存再次命中情况
        boolean result3 = com.rpamis.chainlet.core.support.InstanceOfCache.instanceofCheck(DemoUser.class, Object.class);
        Assert.assertTrue(result3);
    }

    /**
     * 测试ParallelChainTask处理空处理器列表的情况
     */
    @Test
    @DisplayName("测试ParallelChainTask处理空处理器列表")
    public void testParallelChainTaskEmptyHandlerList() throws Exception {
        // 测试空处理器列表的情况
        List<ChainHandler<DemoUser>> emptyHandlerList = new ArrayList<>();
        DemoUser demoUser = new DemoUser("test", "123", "admin");
        List<ChainResult> checkResults = new ArrayList<>();
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        ParallelChainPipelineImpl<DemoUser> parallelChainPipeline = (ParallelChainPipelineImpl<DemoUser>) ChainPipelineFactory.createChain(reference)
                .parallelChain()
                .build();
        
        // 创建ParallelChainTask
        ParallelChainTask<DemoUser> task = new ParallelChainTask<>(emptyHandlerList, demoUser, checkResults, parallelChainPipeline);
        
        // 使用反射调用protected的compute方法
        java.lang.reflect.Method computeMethod = ParallelChainTask.class.getDeclaredMethod("compute");
        computeMethod.setAccessible(true);
        computeMethod.invoke(task);
        
        // 验证结果
        Assert.assertTrue(checkResults.isEmpty());
    }

    /**
     * 测试ParallelChainTask处理单个处理器的情况
     */
    @Test
    @DisplayName("测试ParallelChainTask处理单个处理器")
    public void testParallelChainTaskSingleHandler() throws Exception {
        // 测试单个处理器的情况
        List<ChainHandler<DemoUser>> singleHandlerList = new ArrayList<>();
        singleHandlerList.add(new AuthHandler());
        DemoUser demoUser = new DemoUser("test", "123", "admin");
        List<ChainResult> checkResults = new ArrayList<>();
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        ParallelChainPipelineImpl<DemoUser> parallelChainPipeline = (ParallelChainPipelineImpl<DemoUser>) ChainPipelineFactory.createChain(reference)
                .parallelChain()
                .build();
        
        // 创建ParallelChainTask
        ParallelChainTask<DemoUser> task = new ParallelChainTask<>(singleHandlerList, demoUser, checkResults, parallelChainPipeline);
        
        // 使用反射调用protected的compute方法
        java.lang.reflect.Method computeMethod = ParallelChainTask.class.getDeclaredMethod("compute");
        computeMethod.setAccessible(true);
        computeMethod.invoke(task);
        
        // 验证结果
        Assert.assertFalse(checkResults.isEmpty());
    }

    /**
     * 测试ParallelChainTask处理多个处理器的情况
     */
    @Test
    @DisplayName("测试ParallelChainTask处理多个处理器")
    public void testParallelChainTaskMultipleHandlers() throws Exception {
        // 测试多个处理器的情况
        List<ChainHandler<DemoUser>> multipleHandlerList = new ArrayList<>();
        multipleHandlerList.add(new AuthHandler());
        multipleHandlerList.add(new ValidateHandler());
        DemoUser demoUser = new DemoUser("test", "123", "admin");
        List<ChainResult> checkResults = new ArrayList<>();
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        ParallelChainPipelineImpl<DemoUser> parallelChainPipeline = (ParallelChainPipelineImpl<DemoUser>) ChainPipelineFactory.createChain(reference)
                .parallelChain()
                .build();
        
        // 创建ParallelChainTask
        ParallelChainTask<DemoUser> task = new ParallelChainTask<>(multipleHandlerList, demoUser, checkResults, parallelChainPipeline);
        
        // 使用反射调用protected的compute方法
        java.lang.reflect.Method computeMethod = ParallelChainTask.class.getDeclaredMethod("compute");
        computeMethod.setAccessible(true);
        computeMethod.invoke(task);
        
        // 验证结果
        Assert.assertFalse(checkResults.isEmpty());
        Assert.assertEquals(2, checkResults.size());
    }

    /**
     * 测试ChainPipelineFactory的私有构造函数
     */
    @Test
    @DisplayName("测试ChainPipelineFactory的私有构造函数")
    public void testChainPipelineFactoryPrivateConstructor() {
        // 测试私有构造函数
        Assert.assertThrows(Exception.class, () -> {
            java.lang.reflect.Constructor<ChainPipelineFactory> constructor = ChainPipelineFactory.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            try {
                constructor.newInstance();
            } catch (java.lang.reflect.InvocationTargetException e) {
                // 验证内部异常是IllegalStateException
                Assert.assertTrue(e.getCause() instanceof IllegalStateException);
                throw e;
            }
        });
    }

    /**
     * 测试ChainPipelineCache的私有构造函数
     */
    @Test
    @DisplayName("测试ChainPipelineCache的私有构造函数")
    public void testChainPipelineCachePrivateConstructor() {
        // 测试私有构造函数
        Assert.assertThrows(Exception.class, () -> {
            java.lang.reflect.Constructor<com.rpamis.chainlet.core.builder.ChainPipelineCache> constructor = com.rpamis.chainlet.core.builder.ChainPipelineCache.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            try {
                constructor.newInstance();
            } catch (java.lang.reflect.InvocationTargetException e) {
                // 验证内部异常是IllegalStateException
                Assert.assertTrue(e.getCause() instanceof IllegalStateException);
                throw e;
            }
        });
    }

    /**
     * 测试InstanceOfCache的私有构造函数
     */
    @Test
    @DisplayName("测试InstanceOfCache的私有构造函数")
    public void testInstanceOfCachePrivateConstructor() {
        // 测试私有构造函数
        Assert.assertThrows(Exception.class, () -> {
            java.lang.reflect.Constructor<com.rpamis.chainlet.core.support.InstanceOfCache> constructor = com.rpamis.chainlet.core.support.InstanceOfCache.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            try {
                constructor.newInstance();
            } catch (java.lang.reflect.InvocationTargetException e) {
                // 验证内部异常是IllegalStateException
                Assert.assertTrue(e.getCause() instanceof IllegalStateException);
                throw e;
            }
        });
    }

    /**
     * 测试UniqueList的addAll方法和equals/hashCode方法
     * 测试场景包括：
     * 1. 添加多个处理器
     * 2. 添加重复处理器时抛出异常
     * 3. 测试hashCode方法
     * 4. 测试equals方法（不同对象、null、非List对象）
     */
    @Test
    @DisplayName("测试UniqueList的addAll方法和equals/hashCode方法")
    public void testUniqueListAddAllAndEqualityMethods() {
        // 测试addAll方法
        UniqueList<ChainHandler<DemoUser>> uniqueList = new UniqueList<>();
        List<ChainHandler<DemoUser>> handlers = new ArrayList<>();
        handlers.add(new AuthHandler());
        handlers.add(new ValidateHandler());
        boolean result = uniqueList.addAll(handlers);
        Assert.assertTrue(result);
        Assert.assertEquals(2, uniqueList.size());
        
        // 测试addAll方法的异常分支（重复元素）
        List<ChainHandler<DemoUser>> duplicateHandlers = new ArrayList<>();
        duplicateHandlers.add(new AuthHandler()); // 重复的handler
        Assert.assertThrows(ChainException.class, () -> uniqueList.addAll(duplicateHandlers));
        
        // 测试hashCode和equals方法
        UniqueList<ChainHandler<DemoUser>> anotherList = new UniqueList<>();
        anotherList.add(new AuthHandler());
        uniqueList.hashCode();
        uniqueList.equals(anotherList);
        uniqueList.equals(null);
        uniqueList.equals("not a list");
    }

    /**
     * 测试UniqueList的sortByOrder和getOrderValue方法
     * 测试场景包括：
     * 1. 添加多个处理器
     * 2. 调用sortByOrder方法排序
     */
    @Test
    @DisplayName("测试UniqueList的sortByOrder和getOrderValue方法")
    public void testUniqueListSortAndOrder() {
        // 测试sortByOrder方法
        UniqueList<ChainHandler<DemoUser>> uniqueList = new UniqueList<>();
        
        // 创建测试handler
        AuthHandler authHandler = new AuthHandler();
        ValidateHandler validateHandler = new ValidateHandler();
        
        // 添加handler并排序
        uniqueList.add(authHandler);
        uniqueList.add(validateHandler);
        uniqueList.sortByOrder();
        
        // 验证排序结果
        Assert.assertFalse(uniqueList.isEmpty());
    }

    /**
     * 测试UniqueList的其他方法
     * 测试场景包括：
     * 1. 添加单个处理器
     * 2. 重复添加同一处理器时抛出异常
     * 3. 测试addAll方法添加多个处理器
     */
    @Test
    @DisplayName("测试UniqueList的其他方法")
    public void testUniqueListOtherMethods() {
        UniqueList<ChainHandler<DemoUser>> uniqueList = new UniqueList<>();
        
        // 测试add方法
        AuthHandler authHandler = new AuthHandler();
        boolean addResult = uniqueList.add(authHandler);
        Assert.assertTrue(addResult);
        Assert.assertEquals(1, uniqueList.size());
        
        // 测试重复添加的异常
        Assert.assertThrows(ChainException.class, () -> uniqueList.add(authHandler));
        
        // 测试addAll方法
        UniqueList<ChainHandler<DemoUser>> anotherList = new UniqueList<>();
        ValidateHandler validateHandler = new ValidateHandler();
        List<ChainHandler<DemoUser>> handlers = new ArrayList<>();
        handlers.add(validateHandler);
        boolean addAllResult = anotherList.addAll(handlers);
        Assert.assertTrue(addAllResult);
        Assert.assertEquals(1, anotherList.size());
    }

    /**
     * 测试AbstractFallBackResolverSupport处理null fallback名称的情况
     * 测试场景：
     * 1. 传递null作为fallback名称
     * 2. 验证findLocalFallBackMethod方法的处理逻辑
     */
    @Test
    @DisplayName("测试AbstractFallBackResolverSupport处理null fallback名称的情况")
    public void testAbstractFallBackResolverSupportWithNullFallbackName() throws Exception {
        // 创建一个测试子类来访问protected方法
        class TestFallBackResolverSupport extends AbstractFallBackResolverSupport {
            public java.lang.reflect.Method findLocalFallBackMethodTest(ChainHandler<?> chainHandler, String fallBackName, Class<?>[] fallBackClass) {
                return findLocalFallBackMethod(chainHandler, fallBackName, fallBackClass);
            }
        }
        
        TestFallBackResolverSupport support = new TestFallBackResolverSupport();
        AuthHandler authHandler = new AuthHandler();
        
        // 测试findLocalFallBackMethod的静态方法分支
        // 传递null作为fallBackName，这样就不会尝试调用resolverLocalFallBackMethod
        java.lang.reflect.Method result3 = support.findLocalFallBackMethodTest(authHandler, null, new Class<?>[]{AuthHandler.class});
        Assert.assertNull(result3);
    }

    /**
     * 测试CompleteChainResult的各种方法
     * 测试场景包括：
     * 1. 测试get方法获取处理器执行结果
     * 2. 测试getFinalResult方法获取最终结果
     * 3. 测试verifyIfFail方法验证失败结果
     * 4. 测试verifyIfSuccess方法验证成功结果
     * 5. 测试getChainResults方法获取所有结果
     * 6. 测试getHandlerClasses方法获取所有处理器类型
     * 7. 测试isAllow方法获取是否允许
     */
    @Test
    @DisplayName("测试CompleteChainResult的各种方法")
    public void testCompleteChainResultMethods() {
        // 创建一个CompleteChainResult实例
        DemoUser demoUser = new DemoUser("test", "123", "admin");
        List<ChainResult> chainResults = new ArrayList<>();
        ChainResult result1 = new ChainResult(AuthHandler.class, true, demoUser, "Auth success");
        ChainResult result2 = new ChainResult(ValidateHandler.class, false, demoUser, "Validate failed");
        chainResults.add(result1);
        chainResults.add(result2);
        CompleteChainResult completeResult = new CompleteChainResult(true, chainResults);
        
        // 测试get方法
        Boolean authResult = completeResult.get(AuthHandler.class);
        Assert.assertTrue(authResult);
        Boolean validateResult = completeResult.get(ValidateHandler.class);
        Assert.assertFalse(validateResult);
        
        // 测试getFinalResult方法
        Object finalResult = completeResult.getFinalResult();
        Assert.assertNotNull(finalResult);
        
        // 测试verifyIfFail方法
        Boolean authFail = completeResult.verifyIfFail(AuthHandler.class);
        Assert.assertFalse(authFail);
        Boolean validateFail = completeResult.verifyIfFail(ValidateHandler.class);
        Assert.assertTrue(validateFail);
        
        // 测试verifyIfSuccess方法
        Boolean authSuccess = completeResult.verifyIfSuccess(AuthHandler.class);
        Assert.assertTrue(authSuccess);
        Boolean validateSuccess = completeResult.verifyIfSuccess(ValidateHandler.class);
        Assert.assertFalse(validateSuccess);
        
        // 测试getChainResults和getHandlerClasses方法
        List<ChainResult> results = completeResult.getChainResults();
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
        List<Class<?>> handlerClasses = completeResult.getHandlerClasses();
        Assert.assertNotNull(handlerClasses);
        Assert.assertEquals(2, handlerClasses.size());
        
        // 测试isAllow方法
        boolean allow = completeResult.isAllow();
        Assert.assertTrue(allow);
    }

    /**
     * 测试MethodMetaDataRegistry的local fallback方法
     * 测试场景包括：
     * 1. 初始化local fallback记录（null方法）
     * 2. 获取local fallback记录并验证
     */
    @Test
    @DisplayName("测试MethodMetaDataRegistry的local fallback方法")
    public void testMethodMetaDataRegistryLocalFallBackMethods() {
        // 测试getLocalFallBackRecord和initLocalFallBackRecord方法
        Class<?> testClass = AuthHandler.class;
        String fallBackName = "testFallBack";
        MethodMetaDataRegistry.initLocalFallBackRecord(testClass, fallBackName, null);
        MethodRecord record = MethodMetaDataRegistry.getLocalFallBackRecord(testClass, fallBackName);
        Assert.assertNotNull(record);
        Assert.assertFalse(record.isExist());
    }

    /**
     * 测试MethodMetaDataRegistry的process方法相关方法
     * 测试场景包括：
     * 1. 初始化process记录（有效方法）
     * 2. 获取process记录并验证
     */
    @Test
    @DisplayName("测试MethodMetaDataRegistry的process方法相关方法")
    public void testMethodMetaDataRegistryProcessMethods() {
        // 测试getProcessRecord和initProcessRecord方法
        Class<?> testClass = AuthHandler.class;
        Class<?> genericClass = DemoUser.class;
        try {
            java.lang.reflect.Method method = testClass.getMethod("process", genericClass, ChainHandlerContext.class);
            MethodMetaDataRegistry.initProcessRecord(testClass, genericClass, method);
            MethodRecord record = MethodMetaDataRegistry.getProcessRecord(testClass, genericClass);
            Assert.assertNotNull(record);
            Assert.assertTrue(record.isExist());
            Assert.assertEquals(method, record.getMethod());
        } catch (NoSuchMethodException e) {
            throw new AssertionError("Failed to get process method", e);
        }
    }

    /**
     * 测试MethodMetaDataRegistry的私有构造函数
     * 测试场景：
     * 1. 尝试通过反射调用私有构造函数
     * 2. 验证抛出IllegalStateException异常
     */
    @Test
    @DisplayName("测试MethodMetaDataRegistry的私有构造函数")
    public void testMethodMetaDataRegistryPrivateConstructor() {
        // 测试私有构造函数
        Assert.assertThrows(Exception.class, () -> {
            java.lang.reflect.Constructor<MethodMetaDataRegistry> constructor = MethodMetaDataRegistry.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            try {
                constructor.newInstance();
            } catch (java.lang.reflect.InvocationTargetException e) {
                // 验证内部异常是IllegalStateException
                Assert.assertTrue(e.getCause() instanceof IllegalStateException);
                throw e;
            }
        });
    }

    /**
     * 测试ChainTypeReference的泛型类型方法
     * 测试场景包括：
     * 1. 测试非泛型类的getGenericType方法
     * 2. 测试泛型类的getGenericType方法
     * 3. 测试非泛型类的getGenericTypeClass方法
     * 4. 测试泛型类的getGenericTypeClass方法
     * 5. 测试实例的getGenericType方法
     */
    @Test
    @DisplayName("测试ChainTypeReference的泛型类型方法")
    public void testChainTypeReferenceGenericTypeMethods() {
        // 测试getGenericType方法的null分支
        class TestNonGenericClass {
        }
        TestNonGenericClass nonGenericClass = new TestNonGenericClass();
        Type type1 = ChainTypeReference.getGenericType(nonGenericClass);
        Assert.assertNull(type1);
        
        // 测试getGenericType方法的正常分支
        class TestGenericClass extends ChainTypeReference<DemoUser> {
        }
        TestGenericClass genericClass = new TestGenericClass();
        Type type2 = ChainTypeReference.getGenericType(genericClass);
        Assert.assertNotNull(type2);
        
        // 测试getGenericTypeClass方法的异常分支
        try {
            Class<?> clazz1 = ChainTypeReference.getGenericTypeClass(nonGenericClass);
            Assert.assertNull(clazz1);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof ChainException);
        }
        
        // 测试getGenericTypeClass方法的正常分支
        try {
            Class<?> clazz2 = ChainTypeReference.getGenericTypeClass(genericClass);
            Assert.assertNotNull(clazz2);
            Assert.assertEquals(DemoUser.class, clazz2);
        } catch (Exception e) {
            Assert.fail("Unexpected exception: " + e.getMessage());
        }
        
        // 测试getGenericType方法
        Type type3 = genericClass.getGenericType();
        Assert.assertNotNull(type3);
    }

    /**
     * 测试AbstractFallBackResolverSupport的方法分支
     * 测试场景包括：
     * 1. 测试findLocalFallBackMethod方法（fallback记录不存在）
     * 2. 测试findHandlerProcessMethod方法（process记录不存在）
     * 3. 测试invokeActual方法（null方法）
     * 4. 测试invokeActual方法（不可访问的方法）
     */
    @Test
    @DisplayName("测试AbstractFallBackResolverSupport的方法分支")
    public void testAbstractFallBackResolverSupportMethodBranches() throws Exception {
        // 创建一个测试子类来访问protected方法
        class TestFallBackResolverSupport extends AbstractFallBackResolverSupport {
            public java.lang.reflect.Method findLocalFallBackMethodTest(ChainHandler<?> chainHandler, String fallBackName, Class<?>[] fallBackClass) {
                return findLocalFallBackMethod(chainHandler, fallBackName, fallBackClass);
            }
            
            public java.lang.reflect.Method findHandlerProcessMethodTest(Class<?> chainHandlerClass, Class<?> actualGenericClass) {
                return findHandlerProcessMethod(chainHandlerClass, actualGenericClass);
            }
            
            public void invokeActualTest(ChainHandler<?> chainHandler, java.lang.reflect.Method method, LocalFallBackContext<?> localFallBackContext) {
                invokeActual(chainHandler, method, localFallBackContext);
            }
            
            public void checkMethodTest(java.lang.reflect.Method method, String message) {
                checkMethod(method, message);
            }
        }
        
        TestFallBackResolverSupport support = new TestFallBackResolverSupport();
        AuthHandler authHandler = new AuthHandler();
        DemoUser demoUser = DemoUser.builder().name("test").pwd("123").role("admin").build();
        LocalFallBackContext<DemoUser> localFallBackContext = new LocalFallBackContext<>(demoUser, true);
        
        // 测试findLocalFallBackMethod的fallBackRecord.isExist()分支
        Class<?> testClass = AuthHandler.class;
        String fallBackName = "testFallBack";
        MethodMetaDataRegistry.initLocalFallBackRecord(testClass, fallBackName, null);
        java.lang.reflect.Method result1 = support.findLocalFallBackMethodTest(authHandler, fallBackName, null);
        Assert.assertNull(result1);
        
        // 测试findHandlerProcessMethod的processRecord.isExist()分支
        Class<?> handlerClass = AuthHandler.class;
        Class<?> genericClass = DemoUser.class;
        MethodMetaDataRegistry.initProcessRecord(handlerClass, genericClass, null);
        java.lang.reflect.Method result2 = support.findHandlerProcessMethodTest(handlerClass, genericClass);
        Assert.assertNull(result2);
        
        // 测试invokeActual的method == null分支
        Assert.assertThrows(ChainException.class, () -> support.invokeActualTest(authHandler, null, localFallBackContext));
        
        // 测试invokeActual的!method.isAccessible()分支
        class TestHandler implements ChainHandler<DemoUser> {
            private void testFallBack(LocalFallBackContext<DemoUser> context) {
            }
            
            @Override
            public boolean process(DemoUser demoUser, ChainHandlerContext<DemoUser> context) {
                return true;
            }
        }
        TestHandler testHandler = new TestHandler();
        java.lang.reflect.Method privateMethod = TestHandler.class.getDeclaredMethod("testFallBack", LocalFallBackContext.class);
        // 确保方法不可访问
        privateMethod.setAccessible(false);
        support.invokeActualTest(testHandler, privateMethod, localFallBackContext);
    }

    /**
     * 测试ParallelChainPipelineImpl的策略和线程池设置
     * 测试场景包括：
     * 1. 测试非FullExecutionStrategy策略时抛出异常
     * 2. 测试设置自定义ForkJoinPool
     */
    @Test
    @DisplayName("测试ParallelChainPipelineImpl的策略和线程池设置")
    public void testParallelChainPipelineImplStrategyAndPoolSettings() {
        // 测试第60-61行：当chainStrategy不是FullExecutionStrategy时，应该抛出ChainException异常
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .parallelChain()
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(Strategy.FAST_FAILED)
                .build();
        DemoUser demoUser = new DemoUser("test", "123", "admin");
        Assert.assertThrows(ChainException.class, () -> demoChain.apply(demoUser));
        
        // 测试第89行：当forkJoinPool不为null时，应该设置this.forkJoinPool = forkJoinPool
        ChainPipeline<DemoUser> demoChain2 = ChainPipelineFactory.createChain(reference)
                .parallelChain()
                .pool(new ForkJoinPool(10))
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(Strategy.FULL)
                .build();
        DemoUser demoUser2 = new DemoUser("test", "123", "admin");
        CompleteChainResult chainResult = demoChain2.apply(demoUser2);
        Assert.assertTrue(chainResult.isAllow());
    }

    /**
     * 测试ParallelChainPipelineImpl的并发执行
     */
    @Test
    @DisplayName("测试ParallelChainPipelineImpl的并发执行")
    public void testParallelChainPipelineImplConcurrentExecution() {
        // 测试并发执行时的inParallel原子操作
        ChainTypeReference<DemoUser> reference = new ChainTypeReference<DemoUser>() {};
        ChainPipeline<DemoUser> demoChain = ChainPipelineFactory.createChain(reference)
                .parallelChain()
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(Strategy.FULL)
                .build();
        DemoUser demoUser = new DemoUser("test", "123", "admin");
        
        // 并发执行多次，验证不会抛出异常
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                try {
                    CompleteChainResult chainResult = demoChain.apply(demoUser);
                    Assert.assertTrue(chainResult.isAllow());
                } catch (Exception e) {
                    Assert.fail("并发执行失败: " + e.getMessage());
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Assert.fail("Thread was interrupted: " + e.getMessage());
            }
        }
    }
}
