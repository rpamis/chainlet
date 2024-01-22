package com.rpamis.pattern.chain.builder;


import com.rpamis.pattern.chain.annotations.ChainBuilder;

/**
 * 可变数据责任链Builder
 *
 * @author benym
 * @date 2023/12/25 17:22
 */
@ChainBuilder
public interface VariableChainPipelineBuilder<T> extends ChainPipelineBuilder<T> {

}
