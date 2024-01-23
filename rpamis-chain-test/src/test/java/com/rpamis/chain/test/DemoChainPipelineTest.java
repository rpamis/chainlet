package com.rpamis.chain.test;

import com.rpamis.chain.test.handler.*;
import com.rpamis.chain.core.builder.ChainPipelineDirector;
import com.rpamis.chain.core.builder.ChainPipelineFactory;
import com.rpamis.chain.core.builder.SerialChainPipelineBuilder;
import com.rpamis.chain.core.definition.ChainPipeline;
import com.rpamis.chain.core.entity.ChainException;
import com.rpamis.chain.core.entity.CompleteChainResult;
import com.rpamis.chain.core.support.ChainTypeReference;
import com.rpamis.chain.core.strategy.Strategy;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * DemoChainPipelineTest
 *
 * @author benym
 * @date 2023/5/25 15:50
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
}
