package com.rpamis.chainlet.test.handler;

import com.rpamis.chainlet.core.context.ChainHandlerContext;
import com.rpamis.chainlet.core.definition.ChainHandler;
import com.rpamis.chainlet.test.DemoUser;

/**
 * AuthHandler
 *
 * @author benym
 * @since 2023/5/25 15:49
 */
public class AuthHandler implements ChainHandler<DemoUser> {

    @Override
    public boolean process(DemoUser demoUser, ChainHandlerContext<DemoUser> context) {
        if (!"admin".equals(demoUser.getRole())) {
            System.out.println("auth failed");
            return false;
        }
        System.out.println("auth success");
        return true;
    }
}
