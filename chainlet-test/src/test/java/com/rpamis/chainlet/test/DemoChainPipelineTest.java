package com.rpamis.chainlet.test;

import com.rpamis.chainlet.core.definition.ChainFallBack;
import com.rpamis.chainlet.core.definition.ChainStrategy;
import com.rpamis.chainlet.core.strategy.FullExecutionStrategy;
import com.rpamis.chainlet.test.exception.CustomException;
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

    @Test
    public void should_returnFalse_when_isAllow_given_chainInFullExecutionStrategy() throws ChainException {
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

    @Test
    public void should_returnTrue_when_isAllow_given_chainInFullExecutionStrategy() throws ChainException {
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

    @Test
    public void should_returnFalse_when_isAllow_given_chainInFastFailedStrategy() throws ChainException {
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

    @Test
    public void should_returnFalse_when_isAllow_given_chainInFastFailedStrategySwitch() throws ChainException {
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

    @Test
    public void should_returnTrue_when_isAllow_given_chainInFastReturnStrategy() throws ChainException {
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

    @Test
    public void should_returnTrue_when_isAllow_given_chainInFastReturnStrategySwitch() throws ChainException {
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

    @Test
    public void should_throwException_when_addSameHandler_given_anyChain() {
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

    @Test
    public void should_notEquals_when_isAllow_given_multithreadedExecution() throws ExecutionException, InterruptedException {
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

    @Test
    public void should_throwChainException_when_apply_given_MockExceptionHandlerInChain() throws ChainException {
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

    @Test
    public void should_returnFalse_when_isAllow_given_chainInListHandler() throws ChainException {
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

    @Test
    public void should_returnFalse_when_isAllow_given_parallelChainInFullExecutionStrategy() {
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

    @Test
    public void should_copyChainStruct_when_have_a_chain_given_getChainWithChainId() {
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

    @Test
    public void should_copyChainStruct_when_have_a_ParallelChain_given_getParallelChainWithChainId() {
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

    @Test
    public void should_replaceForkJoinPool_when_hava_a_ParallelChain_given_setForkJoinPool(){
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

    @Test
    public void should_throwCustomException_when_apply_allow_false_given_any_chain() {
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

    @Test
    public void should_throwChainException_when_fallback_given_any_chain_without_fallbackMethodName() {
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

    @Test
    public void should_throwChainException_when_fallback_given_any_chain_with_not_exist_fallbackMethodName_and_fallbackMethod() {
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

    @Test
    public void should_throwChainException_when_fallback_given_any_chain_with_un_correct_fallback_return_type() {
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

    @Test
    public void should_invokeFallback_when_handler_false_given_any_chain_with_correct_fallback_return_type() {
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

    @Test
    public void should_invokeFallback_when_handler_false_given_any_chain_with_correct_fallback_in_private() {
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

    @Test
    public void should_invokeFallback_when_handler_false_given_any_chain_with_correct_fallback_in_private_static() {
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
}
