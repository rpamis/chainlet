package com.rpamis.pattern.chain;

import com.rpamis.pattern.chain.entity.ChainException;
import com.rpamis.pattern.chain.entity.CompleteChainResult;
import com.rpamis.pattern.chain.handler.AuthHandler;
import com.rpamis.pattern.chain.handler.LoginHandler;
import com.rpamis.pattern.chain.handler.MockExceptionHandler;
import com.rpamis.pattern.chain.handler.ValidateHandler;
import com.rpamis.pattern.chain.interfaces.ChainPipeline;
import com.rpamis.pattern.chain.pipeline.DemoChainPipeline;
import com.rpamis.pattern.chain.strategy.FastFailedStrategy;
import com.rpamis.pattern.chain.strategy.FastReturnStrategy;
import com.rpamis.pattern.chain.strategy.FullExecutionStrategy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

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
        // given
        ChainPipeline<DemoUser> demoChain = new DemoChainPipeline()
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(new FullExecutionStrategy<>());
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        CompleteChainResult chainResult = demoChain.start(demoUser);
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
        // given
        ChainPipeline<DemoUser> demoChain = new DemoChainPipeline()
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(new FullExecutionStrategy<>());
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("admin");

        // then
        CompleteChainResult chainResult = demoChain.start(demoUser);
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
        // given
        ChainPipeline<DemoUser> demoChain = new DemoChainPipeline()
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(new FastFailedStrategy<>());
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        CompleteChainResult chainResult = demoChain.start(demoUser);
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
        // given
        ChainPipeline<DemoUser> demoChain = new DemoChainPipeline()
                .addHandler(new ValidateHandler())
                .addHandler(new AuthHandler())
                .addHandler(new LoginHandler())
                .strategy(new FastFailedStrategy<>());
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        CompleteChainResult chainResult = demoChain.start(demoUser);
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
        // given
        ChainPipeline<DemoUser> demoChain = new DemoChainPipeline()
                .addHandler(new ValidateHandler())
                .addHandler(new AuthHandler())
                .addHandler(new LoginHandler())
                .strategy(new FastReturnStrategy<>());
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        CompleteChainResult chainResult = demoChain.start(demoUser);
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
        // given
        ChainPipeline<DemoUser> demoChain = new DemoChainPipeline()
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(new FastReturnStrategy<>());
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");

        // then
        CompleteChainResult chainResult = demoChain.start(demoUser);
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
        // given
        ChainPipeline<DemoUser> demoChain = spy(new DemoChainPipeline());
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
        // given
        ChainPipeline<DemoUser> demoChain = new DemoChainPipeline()
                .addHandler(new AuthHandler())
                .addHandler(new ValidateHandler())
                .addHandler(new LoginHandler())
                .strategy(new FastFailedStrategy<>());

        // then
        CompleteChainResult chainResult = demoChain.start(demoUser);
        return chainResult.isAllow();
    }

    @Test
    public void should_throwChainException_when_start_given_MockExceptionHandlerInChain() throws ChainException {
        // given
        ChainPipeline<DemoUser> demoChain = new DemoChainPipeline()
                .addHandler(new MockExceptionHandler());
        // when
        when(demoUser.getName()).thenReturn("test");
        when(demoUser.getPwd()).thenReturn("123");
        when(demoUser.getRole()).thenReturn("normal");
        // then
        Assert.assertThrows(ChainException.class, () -> demoChain.start(demoUser));
    }
}
