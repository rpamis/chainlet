package com.rpamis.pattern.chain.handler;

import com.rpamis.pattern.chain.DemoUser;
import com.rpamis.pattern.chain.fallback.LocalChainFallback;
import com.rpamis.pattern.chain.entity.LocalFallBackContext;
import com.rpamis.pattern.chain.core.ChainHandler;

/**
 * LoginHandler
 *
 * @author benym
 * @date 2023/5/25 18:15
 */
public class LoginHandler implements ChainHandler<DemoUser> {

    @Override
    @LocalChainFallback(fallbackMethod = "testFallback")
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
