package com.rpamis.pattern.chain.handler;

import com.rpamis.pattern.chain.DemoUser;
import com.rpamis.pattern.chain.entity.LocalFallBackContext;
import com.rpamis.pattern.chain.fallback.LocalChainFallback;
import com.rpamis.pattern.chain.definition.ChainHandler;

import java.util.List;

/**
 * ListAuthHandler
 *
 * @author benym
 * @date 2023/8/22 13:47
 */
public class ListAuthHandler implements ChainHandler<List<DemoUser>> {
    @Override
    @LocalChainFallback(fallbackMethod = "fallback")
    public boolean process(List<DemoUser> demoUserList) {
        if (!"admin".equals(demoUserList.get(0).getRole())) {
            System.out.println("auth failed");
            return false;
        }
        System.out.println("auth success");
        return true;
    }

    public void fallback(LocalFallBackContext<DemoUser> localFallBackContext) {
        System.out.println("fallback");
    }
}
