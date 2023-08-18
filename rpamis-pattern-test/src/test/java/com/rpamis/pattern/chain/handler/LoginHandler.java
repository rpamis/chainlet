package com.rpamis.pattern.chain.handler;

import com.rpamis.pattern.chain.DemoUser;
import com.rpamis.pattern.chain.annotation.LocalChainFallback;
import com.rpamis.pattern.chain.entity.ChainException;
import com.rpamis.pattern.chain.interfaces.ChainHandler;

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
            throw new ChainException("2131");
        }
    }

    public void testFallback(DemoUser demoUser, Boolean exceptionOccurred) {
        System.out.println(1);
    }
}
