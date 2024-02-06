package com.rpamis.chain.core.fallback;

import com.rpamis.chain.core.context.LocalFallBackContext;
import com.rpamis.chain.core.definition.ChainFallBack;

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
