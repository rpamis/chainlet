package com.rpamis.pattern.chain.fallback;

import com.rpamis.pattern.chain.definition.ChainFallBack;
import com.rpamis.pattern.chain.entity.GlobalFallBackContext;

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
