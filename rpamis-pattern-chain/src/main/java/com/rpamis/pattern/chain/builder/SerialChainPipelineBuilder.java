package com.rpamis.pattern.chain.builder;


import com.rpamis.pattern.chain.annotations.ChainBuilder;

/**
 * 串行责任链Builder
 *
 * @author benym
 * @date 2023/8/21 17:04
 */
@ChainBuilder
public interface SerialChainPipelineBuilder<T> extends ChainPipelineBuilder<T> {

}
