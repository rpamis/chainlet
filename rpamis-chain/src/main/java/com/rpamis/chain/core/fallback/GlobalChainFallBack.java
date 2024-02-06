package com.rpamis.chain.core.fallback;

import com.rpamis.chain.core.context.GlobalFallBackContext;
import com.rpamis.chain.core.definition.ChainFallBack;

/**
 * 责任链全局降级接口
 *
 * @author benym
 * @date 2023/8/17 15:25
 */
public interface GlobalChainFallBack<T> extends ChainFallBack<T> {

    /**
     * 降级方法
     *
     * @param fallBackContext fallBackContext
     */
    void fallBack(GlobalFallBackContext<T> fallBackContext);
}
