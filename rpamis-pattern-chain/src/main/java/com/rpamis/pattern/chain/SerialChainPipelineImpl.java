package com.rpamis.pattern.chain;

import com.rpamis.pattern.chain.builder.SerialChainPipelineBuilder;
import com.rpamis.pattern.chain.support.ChainTypeReference;
import com.rpamis.pattern.chain.plugin.ChainBuilderService;

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
