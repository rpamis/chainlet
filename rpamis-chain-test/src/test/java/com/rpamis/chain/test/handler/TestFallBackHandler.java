package com.rpamis.chain.test.handler;

import com.rpamis.chain.core.context.ChainHandlerContext;
import com.rpamis.chain.core.definition.ChainHandler;
import com.rpamis.chain.core.fallback.Fallback;
import com.rpamis.chain.test.DemoUser;

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
