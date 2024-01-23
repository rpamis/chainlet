package com.rpamis.chain.test.handler;

import com.rpamis.chain.test.DemoUser;
import com.rpamis.chain.core.interfaces.ChainHandler;

/**
 * LoginHandler
 *
 * @author benym
 * @date 2023/5/25 18:15
 */
public class LoginHandler implements ChainHandler<DemoUser> {

    @Override
    public boolean process(DemoUser demoUser) {
        if ("test".equals(demoUser.getName()) && "123".equals(demoUser.getPwd())) {
            System.out.println("login success");
            return true;
        } else {
            System.out.println("login failed");
            return false;
        }
    }
}
