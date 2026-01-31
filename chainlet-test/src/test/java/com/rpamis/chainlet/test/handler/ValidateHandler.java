package com.rpamis.chainlet.test.handler;


import com.rpamis.chainlet.core.context.ChainHandlerContext;
import com.rpamis.chainlet.core.definition.ChainHandler;
import com.rpamis.chainlet.test.DemoUser;

/**
 * ValidateHandler
 *
 * @author benym
 * @since 2023/5/25 15:49
 */
public class ValidateHandler implements ChainHandler<DemoUser> {
    @Override
    public boolean process(DemoUser demoUser, ChainHandlerContext<DemoUser> context) {
        if (demoUser.getName() == null || demoUser.getPwd() == null) {
            System.out.println("validate failed");
            return false;
        }
        System.out.println("validate success");
        return true;
    }
}
