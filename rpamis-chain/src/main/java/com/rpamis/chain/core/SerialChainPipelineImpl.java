package com.rpamis.chain.core;

import com.rpamis.chain.plugin.annotations.ChainBuilderService;
import com.rpamis.chain.core.builder.SerialChainPipelineBuilder;
import com.rpamis.chain.core.support.ChainTypeReference;

/**
 * 串行责任链实现类
 *
 * @author benym
 * @date 2023/8/21 17:18
 */
@ChainBuilderService
public class SerialChainPipelineImpl<T> extends AbstractChainPipeline<T> implements SerialChainPipelineBuilder<T> {

    public SerialChainPipelineImpl(ChainTypeReference<T> chainTypeReference) {
        super(chainTypeReference);
    }
}
