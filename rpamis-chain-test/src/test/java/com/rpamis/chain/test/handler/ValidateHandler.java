package com.rpamis.chain.test.handler;

import com.rpamis.chain.test.DemoUser;
import com.rpamis.chain.core.interfaces.ChainHandler;

/**
 * ValidateHandler
 *
 * @author benym
 * @date 2023/5/25 15:49
 */
public class ValidateHandler implements ChainHandler<DemoUser> {
    @Override
    public boolean process(DemoUser demoUser) {
        if (demoUser.getName() == null || demoUser.getPwd() == null) {
            System.out.println("validate failed");
            return false;
        }
        System.out.println("validate success");
        return true;
    }
}
