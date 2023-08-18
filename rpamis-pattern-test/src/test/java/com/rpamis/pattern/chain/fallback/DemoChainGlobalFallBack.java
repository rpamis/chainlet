package com.rpamis.pattern.chain.fallback;

import com.rpamis.pattern.chain.DemoUser;
import com.rpamis.pattern.chain.entity.FallBackContext;
import com.rpamis.pattern.chain.interfaces.GlobalChainFallBack;

/**
 * 全局降级demo类
 *
 * @author benym
 * @date 2023/8/18 15:49
 */
public class DemoChainGlobalFallBack implements GlobalChainFallBack<DemoUser> {
    @Override
    public void fallBack(FallBackContext<DemoUser> fallBackContext) {
        Boolean exceptionOccurred = fallBackContext.getExceptionOccurred();
        System.out.println(1);
    }
}
