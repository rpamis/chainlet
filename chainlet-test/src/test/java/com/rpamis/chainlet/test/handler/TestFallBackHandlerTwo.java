package com.rpamis.chainlet.test.handler;

import com.rpamis.chainlet.core.context.ChainHandlerContext;
import com.rpamis.chainlet.core.definition.ChainHandler;
import com.rpamis.chainlet.core.fallback.Fallback;
import com.rpamis.chainlet.test.DemoUser;

/**
 * TestFallBackHandlerTwo
 *
 * @author benym
 * @since 2024/7/14 22:27
 */
public class TestFallBackHandlerTwo implements ChainHandler<DemoUser> {
    @Override
    @Fallback(fallbackMethod = "test")
    public boolean process(DemoUser handlerData, ChainHandlerContext<DemoUser> context) {
        return false;
    }
}
