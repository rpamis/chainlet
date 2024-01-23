package com.rpamis.chain.test.handler;

import com.rpamis.chain.core.definition.ChainHandler;
import com.rpamis.chain.core.entity.LocalFallBackContext;
import com.rpamis.chain.core.fallback.Fallback;
import com.rpamis.chain.test.DemoUser;

/**
 * LoginHandler
 *
 * @author benym
 * @date 2023/5/25 18:15
 */
public class LoginHandler implements ChainHandler<DemoUser> {

    @Override
    @Fallback(fallbackMethod = "testFallback")
    public boolean process(DemoUser demoUser) {
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
