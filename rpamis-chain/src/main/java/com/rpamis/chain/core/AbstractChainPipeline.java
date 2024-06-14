package com.rpamis.chain.core;


import com.rpamis.chain.core.context.ChainContext;
import com.rpamis.chain.core.context.ChainHandlerContext;
import com.rpamis.chain.core.context.ChainStrategyContext;
import com.rpamis.chain.core.context.LocalFallBackContext;
import com.rpamis.chain.core.definition.*;
import com.rpamis.chain.core.entities.*;
import com.rpamis.chain.core.fallback.FallBackResolver;
import com.rpamis.chain.core.fallback.GlobalChainFallBack;
import com.rpamis.chain.core.fluent.Add;
import com.rpamis.chain.core.fluent.Apply;
import com.rpamis.chain.core.fluent.Builder;
import com.rpamis.chain.core.fluent.With;
import com.rpamis.chain.core.strategy.FullExecutionStrategy;
import com.rpamis.chain.core.strategy.StrategyKey;
import com.rpamis.chain.core.support.ChainTypeReference;
import com.rpamis.chain.core.support.InstanceOfCache;
import com.rpamis.extension.spi.SpiLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 抽象化责任链流水线
 * 责任链模式，可参考
 * AOP {@link org.springframework.aop.framework.ReflectiveMethodInvocation}
 * Tomcat {@link org.apache.catalina.core.ApplicationFilterChain}
 * SpringMVC Interceptor {@link org.springframework.web.servlet.HandlerExecutionChain,org.springframework.web.servlet.HandlerInterceptor}
 * Servlet Filter {@link javax.servlet.FilterChain,javax.servlet.Filter}
 *
 * @param <T> <T>
 * @author benym
 * @date 2023/2/1 17:33
 */
public abstract class AbstractChainPipeline<T> implements ChainInnerPipeline<T>, ChainPipeline<T>, Add<T>, Apply<T>, With<T>, Builder<T> {

    /**
     * 记录当前Handler位置
     */
    protected int pos = 0;
    /**
     * 记录责任链Chain中Handler的个数
     */
    protected int n = 0;

    /**
     * 责任链TypeReference
     */
    protected final ChainTypeReference<T> chainTypeReference;

    /**
     * 执行策略
     */
    protected ChainStrategy<T> chainStrategy = new FullExecutionStrategy<>();

    /**
     * 全局降级方法
     */
    protected GlobalChainFallBack<T> chainFallBack;

    /**
     * 降级解析器
     */
    protected final FallBackResolver<T> fallBackResolver = new FallBackResolver<>();

    /**
     * 存储所有需要执行的handler实现
     */
    protected final UniqueList<ChainHandler<T>> handlerList = new UniqueList<>();

    /**
     * 存储所有责任链校验结果
     */
    protected final List<ChainResult> checkResults = Collections.synchronizedList(new ArrayList<>());

    protected AbstractChainPipeline(ChainTypeReference<T> chainTypeReference) {
        this.chainTypeReference = chainTypeReference;
    }

    /**
     * 设置执行策略
     *
     * @param strategyKey strategyKey
     * @return ChainPipeline
     */
    @Override
    @SuppressWarnings("unchecked")
    public With<T> strategy(StrategyKey strategyKey) {
        this.chainStrategy = SpiLoader.getSpiLoader(ChainStrategy.class).getSpiImpl(strategyKey.getImplCode());
        return this;
    }

    /**
     * 设置执行策略
     *
     * @param chainStrategy chainStrategy
     * @return With<T>
     */
    @Override
    public With<T> strategy(ChainStrategy<T> chainStrategy) {
        this.chainStrategy = chainStrategy;
        return this;
    }

    /**
     * 设置降级处理
     *
     * @param fallBack fallBack
     * @return ChainPipeline
     */
    @Override
    public With<T> globalFallback(GlobalChainFallBack<T> fallBack) {
        this.chainFallBack = fallBack;
        return this;
    }

    /**
     * 添加handler实现到责任链流水线中
     *
     * @param handler handler
     * @return Add<T>
     */
    @Override
    public Add<T> addHandler(ChainHandler<T> handler) {
        this.handlerList.add(handler);
        this.n++;
        return this;
    }

    /**
     * 添加handler实现列表到责任链流水线中
     *
     * @param handlerList 具体的Handler处理类列表
     * @return Add<T>
     */
    @Override
    public Add<T> addHandler(List<ChainHandler<T>> handlerList) {
        if (handlerList != null && !handlerList.isEmpty()) {
            this.handlerList.addAll(handlerList);
            this.n += handlerList.size();
        }
        return this;
    }

    /**
     * 执行责任链
     *
     * @param handlerData  handlerData
     * @param checkResults checkResults
     */
    @Override
    public void doHandler(T handlerData, List<ChainResult> checkResults) {
        ChainHandlerContext<T> handlerContext = new ChainHandlerContext<>(handlerData);
        // 如果当前的handler的位置小于链上所有handler数量，则说明还没执行完，继续向前推进handler
        if (this.pos < this.n) {
            ChainHandler<T> chainHandler = handlerList.get(this.pos++);
            ChainContext<T> chainContext = new ChainContext<>(handlerData, this,
                    this.chainStrategy, chainHandler, checkResults);
            this.handlePipeline(chainContext, handlerContext);
            if (InstanceOfCache.instanceofCheck(chainStrategy.getClass(), ChainStrategy.class)) {
                this.pos = this.n;
            }
        }
    }

    /**
     * handler链式处理
     *
     * @param chainContext   chainContext
     * @param handlerContext handlerContext
     */
    protected void handlePipeline(ChainContext<T> chainContext, ChainHandlerContext<T> handlerContext) {
        ChainStrategy<T> strategy = chainContext.getStrategy();
        T handlerData = chainContext.getHandlerData();
        ChainInnerPipeline<T> chain = chainContext.getChain();
        ChainHandler<T> chainHandler = chainContext.getChainHandler();
        List<ChainResult> checkResults = chainContext.getCheckResults();
        Boolean processResult = this.concreteHandlerProcess(chainContext, handlerContext);
        String message = this.getMessage(handlerContext.getLocalMessage(), chainHandler.globalMessage());
        // 根据策略进行返回值包装
        ChainResult chainResult = this.initSingleChainResult(chainHandler.getClass(), processResult,
                handlerContext.getProcessedData(), message);
        ChainStrategyContext<T> chainStrategyContext = new ChainStrategyContext<>(handlerData, chain, chainResult, checkResults);
        strategy.doStrategy(chainStrategyContext);
    }

    /**
     * 获取消息策略
     *
     * @param localMessage  局部消息
     * @param globalMessage 全局消息
     * @return String
     */
    protected String getMessage(String localMessage, String globalMessage) {
        if (localMessage != null && !localMessage.isEmpty()) {
            return localMessage;
        }
        return globalMessage;
    }

    /**
     * 具体handler实现类处理，如果处理不成功或发生异常则触发局部降级策略
     *
     * @param chainContext   责任链上下文
     * @param handlerContext 责任链handler上下文
     * @return Boolean
     */
    protected Boolean concreteHandlerProcess(ChainContext<T> chainContext, ChainHandlerContext<T> handlerContext) {
        T handlerData = chainContext.getHandlerData();
        ChainHandler<T> chainHandler = chainContext.getChainHandler();
        try {
            boolean processResult = chainHandler.process(handlerData, handlerContext);
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

    /**
     * 单一责任链结果初始化
     *
     * @param handlerClass  责任链具体处理类Class
     * @param processResult 责任链处理结果
     * @param processedData 责任链返回数据
     * @param message       责任链处理自定义消息
     * @return ChainResult
     */
    protected ChainResult initSingleChainResult(Class<?> handlerClass, boolean processResult, Object processedData, String message) {
        return new ChainResult(handlerClass, processResult, processedData, message);
    }

    /**
     * 执行后逻辑
     */
    @Override
    public void afterHandler() {
        this.checkResults.clear();
        this.pos = 0;
    }

    /**
     * 开启整个pipeline执行，并构建返回结果
     * 如果最终结果为false，且fallback不为空，则自动触发全局降级处理
     *
     * @param handlerData 责任链处理主数据
     * @return CompleteChainResult
     * @throws ChainException ChainException
     */
    @Override
    public CompleteChainResult apply(T handlerData) {
        CompleteChainResult completeChainResult;
        try {
            this.doHandler(handlerData, checkResults);
            // 创建一个新列表，包含checkResults的当前内容
            // 因为Collections.unmodifiableList会采用原始列表的内容，afterHandler内会clear掉，导致结果存储丢失
            List<ChainResult> resultsCopy = new ArrayList<>(checkResults);
            completeChainResult = new CompleteChainResult(buildSuccess(resultsCopy), Collections.unmodifiableList(resultsCopy));
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

    /**
     * 构建整个链执行结果，如果存在false则整体为false
     *
     * @param checkResult 责任链整链结果List
     * @return boolean
     */
    public boolean buildSuccess(List<ChainResult> checkResult) {
        return !checkResult.stream().map(ChainResult::isProcessResult)
                .collect(Collectors.toList()).contains(Boolean.FALSE);
    }

    @Override
    public ChainPipeline<T> build() {
        handlerList.sortByOrder();
        return this;
    }

    public ChainTypeReference<T> getChainTypeReference() {
        return chainTypeReference;
    }

    public ChainStrategy<T> getChainStrategy() {
        return chainStrategy;
    }

    public ChainFallBack<T> getChainFallBack() {
        return chainFallBack;
    }

    public UniqueList<ChainHandler<T>> getHandlerList() {
        return handlerList;
    }
}
