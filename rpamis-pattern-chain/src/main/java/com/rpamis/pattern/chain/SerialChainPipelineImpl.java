package com.rpamis.pattern.chain;

import com.rpamis.pattern.chain.builder.SerialChainPipelineBuilder;
import com.rpamis.pattern.chain.interfaces.*;

/**
 * @author benym
 * @date 2023/8/21 17:18
 */
public class SerialChainPipelineImpl<T> extends AbstractChainPipeline<T> implements SerialChainPipelineBuilder<T>, ChainPipeline<T>, Add<T>, Apply<T>, With<T>, Builder<T> {

}
