package com.rpamis.pattern.chain.handler;

import com.rpamis.pattern.chain.AbstractChainHandler;
import com.rpamis.pattern.chain.DemoUser;

/**
 * LoginHandler
 *
 * @author benym
 * @date 2023/5/25 18:15
 */
public class LoginHandler extends AbstractChainHandler<DemoUser> {

    @Override
    protected boolean process(DemoUser demoUser) {
        if ("test".equals(demoUser.getName()) && "123".equals(demoUser.getPwd())) {
            System.out.println("login success");
            return true;
        } else {
            System.out.println("login failed");
            return false;
        }
    }
}
