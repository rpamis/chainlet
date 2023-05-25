package com.rpamis.pattern.chain.handler;

import com.rpamis.pattern.chain.AbstractChainHandler;
import com.rpamis.pattern.chain.DemoUser;

/**
 * ValidateHandler
 *
 * @author benym
 * @date 2023/5/25 15:49
 */
public class ValidateHandler extends AbstractChainHandler<DemoUser> {
    @Override
    protected boolean process(DemoUser demoUser) {
        if (demoUser.getName() == null || demoUser.getPwd() == null) {
            System.out.println("validate failed");
            return false;
        }
        System.out.println("validate success");
        return true;
    }
}
