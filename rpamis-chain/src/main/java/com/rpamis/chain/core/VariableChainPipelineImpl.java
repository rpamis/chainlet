package com.rpamis.chain.core;

import com.rpamis.chain.core.context.ChainHandlerContext;
import com.rpamis.chain.core.entities.ChainException;
import com.rpamis.chain.core.entities.ChainResult;
import com.rpamis.chain.plugin.annotations.ChainBuilderService;
import com.rpamis.chain.core.builder.VariableChainPipelineBuilder;
import com.rpamis.chain.core.definition.ChainHandler;
import com.rpamis.chain.core.definition.ChainStrategy;
import com.rpamis.chain.core.context.ChainContext;
import com.rpamis.chain.core.context.LocalFallBackContext;
import com.rpamis.chain.core.support.ChainTypeReference;
import com.rpamis.chain.core.support.InstanceOfCache;

import java.util.List;

/**
 * 可变数据责任链实现
 *
 * @author benym
 * @date 2023/12/25 17:26
 */
@ChainBuilderService
public class VariableChainPipelineImpl<T> extends AbstractChainPipeline<T> implements VariableChainPipelineBuilder<T> {

    public VariableChainPipelineImpl(ChainTypeReference<T> chainTypeReference) {
        super(chainTypeReference);
    }

    @Override
    protected Boolean concreteHandlerProcess(ChainContext<T> chainContext, ChainHandlerContext<T> handlerContext) {
        T handlerData = chainContext.getHandlerData();
        ChainHandler<T> chainHandler = chainContext.getChainHandler();
        Object processedData = handlerContext.getProcessedData();
        try {
            boolean processResult = chainHandler.process(handlerData, handlerContext);
            // 如果处理不成功则调用降级方法，具体是否调用需查看降级注解中enabled值
            if (!processResult) {
                LocalFallBackContext<T> localFallBackContext = new LocalFallBackContext<>(handlerData, false);
                localFallBackContext.setProcessedData(processedData);
                fallBackResolver.handleLocalFallBack(chainHandler, localFallBackContext, chainTypeReference);
            }
            return processResult;
        } catch (ChainException e) {
            throw e;
        } catch (Exception e) {
            LocalFallBackContext<T> localFallBackContext = new LocalFallBackContext<>(handlerData, true);
            localFallBackContext.setProcessedData(processedData);
            fallBackResolver.handleLocalFallBack(chainHandler, localFallBackContext, chainTypeReference);
            throw e;
        }
    }
}
