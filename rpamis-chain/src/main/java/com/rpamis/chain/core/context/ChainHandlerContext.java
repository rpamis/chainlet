package com.rpamis.chain.core.context;

import java.io.Serializable;
import java.util.Map;

/**
 * 责任链handlerContext
 *
 * @author benym
 * @date 2024/2/17 14:50
 */
public class ChainHandlerContext<T> implements Serializable {
    private static final long serialVersionUID = -396324593116334776L;

    /**
     * 责任链处理的数据
     */
    private T handlerData;

    /**
     * 责任链可变数据，处理后返回的数据
     */
    private Object processedData;

    /**
     * 扩展数据
     */
    private Map<String, Object> extendData;

    /**
     * 当前Handler处理需要返回的消息
     * 适用于一个handler内多个判断，返回不同的消息
     * 避免handler逻辑简单引起类膨胀
     */
    private String localMessage;

    public ChainHandlerContext(){

    }

    public ChainHandlerContext(T handlerData) {
        this.handlerData = handlerData;
    }

    public T getHandlerData() {
        return handlerData;
    }

    public void setHandlerData(T handlerData) {
        this.handlerData = handlerData;
    }

    public Object getProcessedData() {
        return processedData;
    }

    public void setProcessedData(Object processedData) {
        this.processedData = processedData;
    }

    public Map<String, Object> getExtendData() {
        return extendData;
    }

    public void setExtendData(Map<String, Object> extendData) {
        this.extendData = extendData;
    }

    public String getLocalMessage() {
        return localMessage;
    }

    public void setLocalMessage(String localMessage) {
        this.localMessage = localMessage;
    }

    @Override
    public String toString() {
        return "ChainHandlerContext{" +
                "handlerData=" + handlerData +
                ", processedData=" + processedData +
                ", extendData=" + extendData +
                ", localMessage='" + localMessage + '\'' +
                '}';
    }
}
