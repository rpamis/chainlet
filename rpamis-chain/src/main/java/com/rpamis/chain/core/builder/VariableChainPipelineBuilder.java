package com.rpamis.chain.core.builder;


import com.rpamis.chain.plugin.annotations.ChainBuilder;

/**
 * 可变数据责任链Builder
 *
 * @author benym
 * @date 2023/12/25 17:22
 */
@ChainBuilder
public interface VariableChainPipelineBuilder<T> extends ChainPipelineBuilder<T> {

}
