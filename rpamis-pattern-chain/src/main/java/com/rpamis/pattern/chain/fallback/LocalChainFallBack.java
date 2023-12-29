package com.rpamis.pattern.chain.fallback;

import com.rpamis.pattern.chain.definition.ChainFallBack;
import com.rpamis.pattern.chain.entity.LocalFallBackContext;

/**
 * 责任链局部降级接口
 *
 * @author benym
 * @date 2023/12/29 17:33
 */
public interface LocalChainFallBack<T> extends ChainFallBack<T> {

    /**
     * 降级方法
     *
     * @param fallBackContext fallBackContext
     */
    void fallBack(LocalFallBackContext<T> fallBackContext);
}
