package com.rpamis.pattern.chain.handler;

import com.rpamis.pattern.chain.DemoUser;
import com.rpamis.pattern.chain.annotation.LocalChainFallback;
import com.rpamis.pattern.chain.interfaces.ChainHandler;

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

    public void fallback(List<DemoUser> demoUserList, Boolean exceptionOccurred) {
        System.out.println("fallback");
    }
}
