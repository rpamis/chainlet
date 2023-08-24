package com.rpamis.pattern.chain.handler;

import com.rpamis.pattern.chain.DemoUser;
import com.rpamis.pattern.chain.core.ChainHandler;

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
