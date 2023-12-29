package com.rpamis.pattern.chain;

import com.rpamis.pattern.chain.builder.VariableChainPipelineBuilder;
import com.rpamis.pattern.chain.definition.ChainHandler;
import com.rpamis.pattern.chain.entity.ChainContext;
import com.rpamis.pattern.chain.entity.ChainException;
import com.rpamis.pattern.chain.entity.ChainResult;
import com.rpamis.pattern.chain.entity.LocalFallBackContext;
import com.rpamis.pattern.chain.generic.ChainTypeReference;
import com.rpamis.pattern.chain.strategy.FastFailedStrategy;
import com.rpamis.pattern.chain.strategy.FastReturnStrategy;
import com.rpamis.pattern.chain.strategy.FullExecutionStrategy;

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
    public void doHandler(T handlerData, List<ChainResult> checkResults) {
        // 如果当前的handler的位置小于链上所有handler数量，则说明还没执行完，继续向前推进handler
        if (this.pos < this.n) {
            ChainHandler<T> chainHandler = handlerList.get(this.pos++);
            // 获取结果中的最后一个processedData往下传递
            Object processedData = checkResults.stream().reduce((first, second) -> second)
                    .map(ChainResult::getProcessedData).orElse(null);
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
