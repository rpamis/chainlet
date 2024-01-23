package com.rpamis.chain.test.handler;

import com.rpamis.chain.core.definition.ChainHandler;
import com.rpamis.chain.test.DemoUser;

/**
 * AuthHandler
 *
 * @author benym
 * @date 2023/5/25 15:49
 */
public class AuthHandler implements ChainHandler<DemoUser> {

    @Override
    public boolean process(DemoUser demoUser) {
        if (!"admin".equals(demoUser.getRole())) {
            System.out.println("auth failed");
            return false;
        }
        System.out.println("auth success");
        return true;
    }
}
