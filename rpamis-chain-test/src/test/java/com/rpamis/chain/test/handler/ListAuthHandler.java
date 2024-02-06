package com.rpamis.chain.test.handler;

import com.rpamis.chain.core.definition.ChainHandler;
import com.rpamis.chain.core.context.LocalFallBackContext;
import com.rpamis.chain.core.fallback.Fallback;
import com.rpamis.chain.test.DemoUser;

import java.util.List;

/**
 * ListAuthHandler
 *
 * @author benym
 * @date 2023/8/22 13:47
 */
public class ListAuthHandler implements ChainHandler<List<DemoUser>> {
    @Override
    @Fallback(fallbackMethod = "fallback")
    public boolean process(List<DemoUser> demoUserList) {
        if (!"admin".equals(demoUserList.get(0).getRole())) {
            System.out.println("auth failed");
            return false;
        }
        System.out.println("auth success");
        return true;
    }

    public void fallback(LocalFallBackContext<List<DemoUser>> localFallBackContext) {
        System.out.println("fallback");
    }
}
