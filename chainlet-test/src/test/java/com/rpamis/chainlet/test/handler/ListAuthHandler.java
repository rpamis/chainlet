package com.rpamis.chainlet.test.handler;

import com.rpamis.chainlet.core.context.ChainHandlerContext;
import com.rpamis.chainlet.core.definition.ChainHandler;
import com.rpamis.chainlet.core.context.LocalFallBackContext;
import com.rpamis.chainlet.core.fallback.Fallback;
import com.rpamis.chainlet.test.DemoUser;

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
    public boolean process(List<DemoUser> demoUserList, ChainHandlerContext<List<DemoUser>> context) {
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