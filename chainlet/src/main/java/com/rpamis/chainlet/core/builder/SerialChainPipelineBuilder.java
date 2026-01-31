package com.rpamis.chainlet.core.builder;


import com.rpamis.chainlet.plugin.annotations.ChainBuilder;

/**
 * 串行责任链Builder
 *
 * @author benym
 * @since 2023/8/21 17:04
 */
@ChainBuilder
public interface SerialChainPipelineBuilder<T> extends ChainPipelineBuilder<T> {

}
