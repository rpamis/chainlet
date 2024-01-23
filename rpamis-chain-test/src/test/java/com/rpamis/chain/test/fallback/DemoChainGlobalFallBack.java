package com.rpamis.chain.test.fallback;

import com.rpamis.chain.core.entity.GlobalFallBackContext;
import com.rpamis.chain.core.fallback.GlobalChainFallBack;
import com.rpamis.chain.test.DemoUser;

/**
 * 全局降级demo类
 *
 * @author benym
 * @date 2023/8/18 15:49
 */
public class DemoChainGlobalFallBack implements GlobalChainFallBack<DemoUser> {

    @Override
    public void fallBack(GlobalFallBackContext<DemoUser> fallBackContext) {
        Boolean exceptionOccurred = fallBackContext.getExceptionOccurred();
        System.out.println(1);
    }
}
