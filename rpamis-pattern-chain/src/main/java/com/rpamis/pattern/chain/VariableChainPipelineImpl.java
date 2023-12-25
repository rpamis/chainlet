package com.rpamis.pattern.chain;

import com.rpamis.pattern.chain.builder.VariableChainPipelineBuilder;
import com.rpamis.pattern.chain.generic.ChainTypeReference;

/**
 * 可变数据责任链实现
 *
 * @author benym
 * @date 2023/12/25 17:26
 */
public class VariableChainPipelineImpl<T> extends AbstractChainPipeline<T> implements VariableChainPipelineBuilder<T> {

    public VariableChainPipelineImpl(ChainTypeReference<T> chainTypeReference) {
        super(chainTypeReference);
    }
}
