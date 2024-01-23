package com.rpamis.chain.core;


import com.rpamis.chain.core.entity.ChainException;
import com.rpamis.chain.core.entity.ChainResult;
import com.rpamis.chain.core.entity.CompleteChainResult;
import com.rpamis.chain.core.entity.UniqueList;
import com.rpamis.chain.core.interfaces.ChainStrategy;
import com.rpamis.chain.core.strategy.FastFailedStrategy;
import com.rpamis.chain.core.strategy.FastReturnStrategy;
import com.rpamis.chain.core.strategy.FullExecutionStrategy;
import com.rpamis.chain.core.interfaces.ChainHandler;
import com.rpamis.chain.core.interfaces.ChainPipeline;

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
public abstract class AbstractChainPipeline<T> implements ChainPipeline<T> {

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
    public final ChainPipeline<T> strategy(ChainStrategy<T> strategy) {
        this.chainStrategy = strategy;
        return this;
    }

    /**
     * 添加handler实现到责任链流水线中，可子类覆写
     *
     * @param handler handler
     * @return ChainPipeline<T>
     */
    @Override
    public ChainPipeline<T> addHandler(ChainHandler<T> handler) {
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
            chainHandler.handle(handlerData, this, this.chainStrategy);
            if (this.chainStrategy instanceof FastReturnStrategy
                    || this.chainStrategy instanceof FastFailedStrategy
                    || this.chainStrategy instanceof FullExecutionStrategy) {
                this.pos = this.n;
            }
        }
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
     *
     * @param handlerData handlerData
     * @return CompleteChainResult
     * @throws ChainException ChainException
     */
    @Override
    public CompleteChainResult start(T handlerData) throws ChainException {
        try {
            this.doHandler(handlerData);
            List<ChainResult> chainResults = CHECK_RESULT.get();
            return new CompleteChainResult(buildSuccess(chainResults), Collections.unmodifiableList(chainResults));
        } catch (Exception e) {
            throw new ChainException("chain unexpected exception", e);
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
}
