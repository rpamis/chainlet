package com.rpamis.chain.test.handler;

import com.rpamis.chain.core.context.ChainHandlerContext;
import com.rpamis.chain.core.context.LocalFallBackContext;
import com.rpamis.chain.core.definition.ChainHandler;
import com.rpamis.chain.core.fallback.Fallback;
import com.rpamis.chain.test.DemoUser;

/**
 * TestFallBackHandlerFour
 *
 * @author benym
 * @date 2024/7/14 22:27
 */
public class TestFallBackHandlerFour implements ChainHandler<DemoUser> {
    @Override
    @Fallback(fallbackMethod = "test")
    public boolean process(DemoUser handlerData, ChainHandlerContext<DemoUser> context) {
        return false;
    }

    public void test(LocalFallBackContext<DemoUser> localFallBackContext) {
        System.out.print("success");
    }
}
