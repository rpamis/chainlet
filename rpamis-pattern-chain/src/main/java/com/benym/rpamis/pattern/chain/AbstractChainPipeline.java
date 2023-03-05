package com.benym.rpamis.pattern.chain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽象化责任链流水线
 * 责任链模式，可参考
 * AOP {@link org.springframework.aop.framework.ReflectiveMethodInvocation}
 * Tomcat {@link org.apache.catalina.core.ApplicationFilterChain}
 * SpringMVC Interceptor {@link org.springframework.web.servlet.HandlerExecutionChain,org.springframework.web.servlet.HandlerInterceptor}
 * Servlet Filter {@link javax.servlet.FilterChain,javax.servlet.Filter}
 *
 * @param <T> <T>
 */
public abstract class AbstractChainPipeline<T> implements ChainPipline<T> {

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
    private ChainStrategy chainStrategy = ChainStrategy.ALL;

    /**
     * 存储所有需要执行的handler实现
     */
    private final List<ChainHandler<T>> handlerList = new ArrayList<>();

    /**
     * 存储所有责任链校验结果
     */
    public static final ThreadLocal<List<Boolean>> CHECK_RESULT = ThreadLocal.withInitial(ArrayList::new);


    public final AbstractChainPipeline<T> strategy(ChainStrategy chainStrategyEnum) {
        this.chainStrategy = chainStrategyEnum;
        return this;
    }

    /**
     * 添加handler实现到责任链流水线中
     *
     * @param handler handler
     * @return AbstractChainPipeline<T>
     */
    @Override
    public AbstractChainPipeline<T> addHandler(AbstractChainHandler<T> handler) {
        this.handlerList.add(handler);
        this.n++;
        return this;
    }

    /**
     * 执行责任链
     *
     * @param handlerData handlerData
     */
    @Override
    public void doHandler(T handlerData) throws IOException, ChainException {
        // 如果当前的handler的位置小于链上所有handler数量，则说明还没执行完，继续向前推进handler
        if (this.pos < this.n) {
            ChainHandler<T> chainHandler = handlerList.get(this.pos++);
            boolean handleResult = chainHandler.handle(handlerData, this, this.chainStrategy, CHECK_RESULT);
            if (handleResult) {
                this.pos = this.n;
            }
        }
    }

    /**
     * 执行后逻辑
     */
    @Override
    public void afterHandler() {

    }

    public boolean start(T handlerData) throws ChainException {
        try {
            this.doHandler(handlerData);
            return buildSuccess(CHECK_RESULT.get());
        } catch (Exception e) {
            throw new ChainException("chain unexpected exception", e);
        } finally {
            CHECK_RESULT.remove();
        }
    }

    private boolean buildSuccess(List<Boolean> checkResult) {
        return !checkResult.contains(Boolean.FALSE);
    }
}
