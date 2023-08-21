package com.rpamis.pattern.chain;


import com.rpamis.pattern.chain.entity.*;
import com.rpamis.pattern.chain.interfaces.*;
import com.rpamis.pattern.chain.strategy.FastFailedStrategy;
import com.rpamis.pattern.chain.strategy.FastReturnStrategy;
import com.rpamis.pattern.chain.strategy.FullExecutionStrategy;

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
public abstract class AbstractChainPipeline<T> implements ChainPipeline<T>, Add<T>, Apply<T>, With<T>, Builder<T> {

    /**
     * 记录当前Handler位置
     */
    private int pos = 0;
    /**
     * 记录责任链Chain中Handler的个数
     */
    private int n = 0;

    /**
     * 执行策略
     */
    private ChainStrategy<T> chainStrategy = new FullExecutionStrategy<>();

    /**
     * 降级方法
     */
    private ChainFallBack<T> chainFallBack;

    /**
     * 降级解析器
     */
    private final FallBackResolver<T> fallBackResolver = new FallBackResolver<>();

    /**
     * 存储所有需要执行的handler实现
     */
    private final UniqueList<ChainHandler<T>> handlerList = new UniqueList<>();

    /**
     * 存储所有责任链校验结果
     */
    public static final ThreadLocal<List<ChainResult>> CHECK_RESULT = ThreadLocal.withInitial(ArrayList::new);


    /**
     * 设置执行策略，不可子类覆写
     *
     * @param strategy strategy
     * @return ChainPipeline
     */
    @Override
    public With<T> strategy(ChainStrategy<T> strategy) {
        this.chainStrategy = strategy;
        return this;
    }

    /**
     * 设置降级处理，不可子类覆写
     *
     * @param fallBack fallBack
     * @return ChainPipeline
     */
    @Override
    public With<T> globalFallback(ChainFallBack<T> fallBack) {
        this.chainFallBack = fallBack;
        return this;
    }

    /**
     * 添加handler实现到责任链流水线中，可子类覆写
     *
     * @param handler handler
     * @return ChainPipeline<T>
     */
    @Override
    public Add<T> addHandler(ChainHandler<T> handler) {
        this.handlerList.add(handler);
        this.n++;
        return this;
    }

    /**
     * 执行责任链，可子类覆写
     *
     * @param handlerData handlerData
     */
    @Override
    public void doHandler(T handlerData) {
        // 如果当前的handler的位置小于链上所有handler数量，则说明还没执行完，继续向前推进handler
        if (this.pos < this.n) {
            ChainHandler<T> chainHandler = handlerList.get(this.pos++);
            ChainContext<T> chainContext = new ChainContext<>(handlerData, this,
                    this.chainStrategy, chainHandler);
            this.handle(chainContext);
            if (this.chainStrategy instanceof FastReturnStrategy
                    || this.chainStrategy instanceof FastFailedStrategy
                    || this.chainStrategy instanceof FullExecutionStrategy) {
                this.pos = this.n;
            }
        }
    }

    /**
     * handler链式处理
     *
     * @param chainContext chainContext
     */
    private void handle(ChainContext<T> chainContext) {
        ChainStrategy<T> strategy = chainContext.getStrategy();
        T handlerData = chainContext.getHandlerData();
        ChainPipeline<T> chain = chainContext.getChain();
        ChainHandler<T> chainHandler = chainContext.getChainHandler();
        Boolean processResult = this.concreteHandlerProcess(chainHandler, handlerData);
        // 根据策略进行返回值包装
        ChainResult chainResult = this.init(this.getClass(), processResult, chainHandler.message());
        strategy.doStrategy(handlerData, chain, chainResult);
    }

    /**
     * 具体handler实现类处理，如果处理不成功或发生异常则触发局部降级策略
     *
     * @param chainHandler hanlder具体实现类
     * @param handlerData  责任链处理主数据
     * @return Boolean
     */
    private Boolean concreteHandlerProcess(ChainHandler<T> chainHandler, T handlerData) {
        try {
            boolean processResult = chainHandler.process(handlerData);
            // 如果处理不成功则调用降级方法，具体是否调用需查看降级注解中enabled值
            if (!processResult) {
                fallBackResolver.handleLocalFallBack(chainHandler, handlerData, this, false);
            }
            return processResult;
        } catch (ChainException e) {
            throw e;
        } catch (Exception e) {
            fallBackResolver.handleLocalFallBack(chainHandler, handlerData, this, true);
            throw e;
        }
    }

    /**
     * 策略接口初始化
     *
     * @param handlerClass  handlerClass
     * @param processResult processResult
     * @param message       message
     * @return ChainResult
     */
    private ChainResult init(Class<?> handlerClass, boolean processResult, String message) {
        return new ChainResult(handlerClass, processResult, message);
    }

    /**
     * 执行后逻辑，可子类覆写
     */
    @Override
    public void afterHandler() {
        CHECK_RESULT.remove();
        this.pos = 0;
    }

    /**
     * 开启整个pipeline执行，并构建返回结果，可子类覆写
     * 如果最终结果为false，且fallback不为空，则自动触发全局降级处理
     *
     * @param handlerData handlerData
     * @return CompleteChainResult
     * @throws ChainException ChainException
     */
    @Override
    public CompleteChainResult apply(T handlerData) {
        CompleteChainResult completeChainResult = null;
        try {
            this.doHandler(handlerData);
            List<ChainResult> chainResults = CHECK_RESULT.get();
            completeChainResult = new CompleteChainResult(buildSuccess(chainResults), Collections.unmodifiableList(chainResults));
            fallBackResolver.handleGlobalFallBack(chainFallBack, handlerData, completeChainResult, false);
            return completeChainResult;
        } catch (ChainException e) {
            throw e;
        } catch (Exception e) {
            fallBackResolver.handleGlobalFallBack(chainFallBack, handlerData, completeChainResult, true);
            throw e;
        } finally {
            this.afterHandler();
        }
    }

    /**
     * 构建整个链执行结果，如果存在false则整体为false
     *
     * @param checkResult checkResult
     * @return boolean
     */
    private boolean buildSuccess(List<ChainResult> checkResult) {
        return !checkResult.stream().map(ChainResult::isProcessResult)
                .collect(Collectors.toList()).contains(Boolean.FALSE);
    }

    @Override
    public ChainPipeline<T> build() {
        return this;
    }
}
