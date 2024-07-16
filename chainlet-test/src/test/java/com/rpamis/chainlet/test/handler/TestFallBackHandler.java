package com.rpamis.chainlet.test.handler;

import com.rpamis.chainlet.core.context.ChainHandlerContext;
import com.rpamis.chainlet.core.definition.ChainHandler;
import com.rpamis.chainlet.core.fallback.Fallback;
import com.rpamis.chainlet.test.DemoUser;

/**
 * TestFallBackHandler
 *
 * @author benym
 * @date 2024/7/14 22:18
 */
public class TestFallBackHandler implements ChainHandler<DemoUser> {
    @Override
    @Fallback()
    public boolean process(DemoUser handlerData, ChainHandlerContext<DemoUser> context) {
        return false;
    }


}
