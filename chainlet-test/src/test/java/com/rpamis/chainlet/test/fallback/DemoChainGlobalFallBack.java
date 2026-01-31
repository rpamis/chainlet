package com.rpamis.chainlet.test.fallback;

import com.rpamis.chainlet.core.context.GlobalFallBackContext;
import com.rpamis.chainlet.core.fallback.GlobalChainFallBack;
import com.rpamis.chainlet.test.DemoUser;

/**
 * 全局降级demo类
 *
 * @author benym
 * @since 2023/8/18 15:49
 */
public class DemoChainGlobalFallBack implements GlobalChainFallBack<DemoUser> {

    @Override
    public void fallBack(GlobalFallBackContext<DemoUser> fallBackContext) {
        Boolean exceptionOccurred = fallBackContext.getExceptionOccurred();
        System.out.println(1);
    }
}
