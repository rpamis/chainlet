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
import com.rpamis.chainlet.core.definition.ChainPipeline;
import com.rpamis.chainlet.core.entities.ChainException;
import com.rpamis.chainlet.core.entities.CompleteChainResult;
import com.rpamis.chainlet.core.support.ChainTypeReference;
import com.rpamis.chainlet.core.strategy.Strategy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.StopWatch;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;
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
}
