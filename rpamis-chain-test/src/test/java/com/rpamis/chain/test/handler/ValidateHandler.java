package com.rpamis.chain.test.handler;


import com.rpamis.chain.core.context.ChainHandlerContext;
import com.rpamis.chain.core.definition.ChainHandler;
import com.rpamis.chain.test.DemoUser;

/**
 * ValidateHandler
 *
 * @author benym
 * @date 2023/5/25 15:49
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
