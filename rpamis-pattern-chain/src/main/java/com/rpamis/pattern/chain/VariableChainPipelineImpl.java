package com.rpamis.pattern.chain;

import com.rpamis.pattern.chain.builder.VariableChainPipelineBuilder;
import com.rpamis.pattern.chain.definition.ChainHandler;
import com.rpamis.pattern.chain.entity.*;
import com.rpamis.pattern.chain.generic.ChainTypeReference;
import com.rpamis.pattern.chain.strategy.FastFailedStrategy;
import com.rpamis.pattern.chain.strategy.FastReturnStrategy;
import com.rpamis.pattern.chain.strategy.FullExecutionStrategy;

import java.util.Collections;
import java.util.List;

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

    @Override
    public CompleteChainResult apply(T handlerData) {
        CompleteChainResult completeChainResult;
        try {
            this.doHandler(handlerData, null, checkResults);
            completeChainResult = new CompleteChainResult(buildSuccess(checkResults), Collections.unmodifiableList(checkResults));
            fallBackResolver.handleGlobalFallBack(chainFallBack, handlerData, completeChainResult, false);
            return completeChainResult;
        } catch (ChainException e) {
            throw e;
        } catch (Exception e) {
            fallBackResolver.handleGlobalFallBack(chainFallBack, handlerData, null, true);
            throw e;
        } finally {
            this.afterHandler();
        }
    }

    @Override
    public void doHandler(T handlerData, Object processedData, List<ChainResult> checkResults) {
        // 如果当前的handler的位置小于链上所有handler数量，则说明还没执行完，继续向前推进handler
        if (this.pos < this.n) {
            ChainHandler<T> chainHandler = handlerList.get(this.pos++);
            ChainContext<T> chainContext = new ChainContext<>(handlerData, processedData, this,
                    this.chainStrategy, chainHandler, checkResults);
            this.handlePipeline(chainContext);
            if (this.chainStrategy instanceof FastReturnStrategy
                    || this.chainStrategy instanceof FastFailedStrategy
                    || this.chainStrategy instanceof FullExecutionStrategy) {
                this.pos = this.n;
            }
        }
    }

    @Override
    protected Boolean concreteHandlerProcess(ChainContext<T> chainContext) {
        T handlerData = chainContext.getHandlerData();
        Object processedData = chainContext.getProcessedData();
        ChainHandler<T> chainHandler = chainContext.getChainHandler();
        try {
            boolean processResult = chainHandler.process(handlerData, processedData);
            // 如果处理不成功则调用降级方法，具体是否调用需查看降级注解中enabled值
            if (!processResult) {
                LocalFallBackContext<T> localFallBackContext = new LocalFallBackContext<>(handlerData, false);
                fallBackResolver.handleLocalFallBack(chainHandler, localFallBackContext, chainTypeReference);
            }
            return processResult;
        } catch (ChainException e) {
            throw e;
        } catch (Exception e) {
            LocalFallBackContext<T> localFallBackContext = new LocalFallBackContext<>(handlerData, true);
            fallBackResolver.handleLocalFallBack(chainHandler, localFallBackContext, chainTypeReference);
            throw e;
        }
    }
}
