package com.rpamis.chainlet.test.handler;

import com.rpamis.chainlet.core.context.ChainHandlerContext;
import com.rpamis.chainlet.core.context.LocalFallBackContext;
import com.rpamis.chainlet.core.definition.ChainHandler;
import com.rpamis.chainlet.core.fallback.LocalChainFallBack;
import com.rpamis.chainlet.test.DemoUser;

/**
 * TestLocalFallBackHandler
 *
 * @author benym
 * @since 2026/2/1 11:53
 */
public class TestLocalFallBackHandler implements ChainHandler<DemoUser>, LocalChainFallBack<DemoUser> {

    @Override
    public boolean process(DemoUser handlerData, ChainHandlerContext<DemoUser> context) {
        return false;
    }

    @Override
    public void fallBack(LocalFallBackContext<DemoUser> fallBackContext) {
        System.out.println("local fall back success");
    }
}
