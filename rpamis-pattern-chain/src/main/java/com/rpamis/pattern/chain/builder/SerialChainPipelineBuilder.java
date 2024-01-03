package com.rpamis.pattern.chain.builder;

import com.rpamis.pattern.chain.fluent.Add;
import com.rpamis.pattern.chain.plugin.ChainBuilder;

/**
 * 串行责任链Builder
 *
 * @author benym
 * @date 2023/8/21 17:04
 */
@ChainBuilder(value = "SerialChain")
public interface SerialChainPipelineBuilder<T> extends Add<T> {

}
