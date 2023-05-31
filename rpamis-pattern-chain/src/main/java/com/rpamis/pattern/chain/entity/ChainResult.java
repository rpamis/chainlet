package com.rpamis.pattern.chain.entity;

import java.io.Serializable;

/**
 * 单个责任链结果存储实体
 *
 * @date 2023/3/7 14:41
 * @author benym
 */
public class ChainResult implements Serializable {

    private static final long serialVersionUID = 4901947213957067898L;

    /**
     * 当前链上的具体处理类Class
     */
    private Class<?> handlerClass;

    /**
     * 当前处理类处理结果
     */
    private boolean processResult;

    public ChainResult(Class<?> handlerClass, boolean processResult) {
        this.handlerClass = handlerClass;
        this.processResult = processResult;
    }

    public Class<?> getHandlerClass() {
        return handlerClass;
    }

    public void setHandlerClass(Class<?> handlerClass) {
        this.handlerClass = handlerClass;
    }

    public boolean isProcessResult() {
        return processResult;
    }

    public void setProcessResult(boolean processResult) {
        this.processResult = processResult;
    }

    @Override
    public String toString() {
        return "ChainResult{" +
                "handlerClass=" + handlerClass +
                ", processResult=" + processResult +
                '}';
    }
}
