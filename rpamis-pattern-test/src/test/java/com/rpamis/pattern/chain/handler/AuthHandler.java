package com.rpamis.pattern.chain.handler;

import com.rpamis.pattern.chain.AbstractChainHandler;
import com.rpamis.pattern.chain.DemoUser;

/**
 * AuthHandler
 *
 * @author benym
 * @date 2023/5/25 15:49
 */
public class AuthHandler extends AbstractChainHandler<DemoUser> {
    @Override
    protected boolean process(DemoUser demoUser) {
        if (!"admin".equals(demoUser.getRole())) {
            System.out.println("auth failed");
            return false;
        }
        System.out.println("auth success");
        return true;
    }
}
