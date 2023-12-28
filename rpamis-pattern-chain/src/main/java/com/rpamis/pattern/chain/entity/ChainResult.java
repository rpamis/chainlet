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
     * 责任链可变数据，处理后返回的数据
     */
    private Object processedData;

    /**
     * 当前处理类消息
     */
    private String message;

    public ChainResult(Class<?> handlerClass, boolean processResult, Object processedData, String message) {
        this.handlerClass = handlerClass;
        this.processResult = processResult;
        this.processedData = processedData;
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

    public Object getProcessedData() {
        return processedData;
    }

    public void setProcessedData(Object processedData) {
        this.processedData = processedData;
    }

    @Override
    public String toString() {
        return "ChainResult{" +
                "handlerClass=" + handlerClass +
                ", processResult=" + processResult +
                ", processedData=" + processedData +
                ", message='" + message + '\'' +
                '}';
    }
}
