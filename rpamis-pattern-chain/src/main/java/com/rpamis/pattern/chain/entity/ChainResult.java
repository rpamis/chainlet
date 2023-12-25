package com.rpamis.pattern.chain.entity;

import java.io.Serializable;

/**
 * 单个责任链结果存储实体
 *
 * @author benym
 * @date 2023/3/7 14:41
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

    /**
     * 责任链可变数据
     */
    private Object variableData;

    /**
     * 当前处理类消息
     */
    private String message;

    public ChainResult(Class<?> handlerClass, boolean processResult, Object variableData, String message) {
        this.handlerClass = handlerClass;
        this.processResult = processResult;
        this.variableData = variableData;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getVariableData() {
        return variableData;
    }

    public void setVariableData(Object variableData) {
        this.variableData = variableData;
    }

    @Override
    public String toString() {
        return "ChainResult{" +
                "handlerClass=" + handlerClass +
                ", processResult=" + processResult +
                ", variableData=" + variableData +
                ", message='" + message + '\'' +
                '}';
    }
}
