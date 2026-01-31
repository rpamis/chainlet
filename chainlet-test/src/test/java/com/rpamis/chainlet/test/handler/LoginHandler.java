package com.rpamis.chainlet.test.handler;

import com.rpamis.chainlet.core.context.ChainHandlerContext;
import com.rpamis.chainlet.core.definition.ChainHandler;
import com.rpamis.chainlet.core.context.LocalFallBackContext;
import com.rpamis.chainlet.core.fallback.Fallback;
import com.rpamis.chainlet.test.DemoUser;

/**
 * LoginHandler
 *
 * @author benym
 * @since 2023/5/25 18:15
 */
public class LoginHandler implements ChainHandler<DemoUser> {

    @Override
    @Fallback(fallbackMethod = "testFallback")
    public boolean process(DemoUser demoUser, ChainHandlerContext<DemoUser> context) {
        if ("test".equals(demoUser.getName()) && "123".equals(demoUser.getPwd())) {
            System.out.println("login success");
            return true;
        } else {
            throw new RuntimeException("2131");
        }
    }

    public void testFallback(LocalFallBackContext<DemoUser> localFallBackContext) {
        System.out.println(1);
    }
}
