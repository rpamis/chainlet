package com.rpamis.pattern.chain;

import com.rpamis.pattern.chain.entity.CompleteChainResult;
import com.rpamis.pattern.chain.fallback.DemoChainGlobalFallBack;
import com.rpamis.pattern.chain.handler.LoginHandler;
import com.rpamis.pattern.chain.interfaces.ChainPipeline;
import com.rpamis.pattern.chain.pipeline.DemoChainPipeline;
import com.rpamis.pattern.chain.strategy.FastReturnStrategy;

/**
 * @author benym
 * @date 2023/8/17 16:13
 */
public class Test {
    public static void main(String[] args) {
        ChainPipeline<DemoUser> demoChain = new DemoChainPipeline()
                .addHandler(new LoginHandler())
                .strategy(new FastReturnStrategy<>())
                .globalFallback(new DemoChainGlobalFallBack())
                .build();
        DemoUser demoUser = new DemoUser("12", "2", "3");
        CompleteChainResult start = demoChain.apply(demoUser);
//        DemoUser demoUser2 = new DemoUser("test", "123", "3");
//        CompleteChainResult start2 = demoChain.start(demoUser2);
        System.out.println(1);
    }
}
