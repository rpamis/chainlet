package com.rpamis.pattern.chain.handler;

import com.rpamis.pattern.chain.DemoUser;
import com.rpamis.pattern.chain.interfaces.ChainHandler;

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
