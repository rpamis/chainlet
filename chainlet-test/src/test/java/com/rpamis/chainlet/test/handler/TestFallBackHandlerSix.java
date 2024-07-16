package com.rpamis.chainlet.test.handler;

import com.rpamis.chainlet.core.context.ChainHandlerContext;
import com.rpamis.chainlet.core.context.LocalFallBackContext;
import com.rpamis.chainlet.core.definition.ChainHandler;
import com.rpamis.chainlet.core.fallback.Fallback;
import com.rpamis.chainlet.test.DemoUser;

/**
 * TestFallBackHandlerSix
 *
 * @author benym
 * @date 2024/7/14 22:27
 */
public class TestFallBackHandlerSix implements ChainHandler<DemoUser> {
    @Override
    @Fallback(fallbackMethod = "test")
    public boolean process(DemoUser handlerData, ChainHandlerContext<DemoUser> context) {
        return false;
    }

    private static void test(LocalFallBackContext<DemoUser> localFallBackContext) {
        System.out.print("success");
    }
}
