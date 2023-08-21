package com.rpamis.pattern.chain.builder;

import com.rpamis.pattern.chain.interfaces.Add;
import com.rpamis.pattern.chain.interfaces.ChainHandler;

/**
 * @author benym
 * @date 2023/8/21 17:09
 */
public interface ParallelChainPipelineBuilder<T> {

    Add<T> addHandler(ChainHandler<T> handler);
}
