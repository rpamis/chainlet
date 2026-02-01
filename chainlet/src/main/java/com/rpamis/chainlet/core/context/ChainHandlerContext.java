/*
 * Copyright 2023-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rpamis.chainlet.core.context;

import java.io.Serializable;
import java.util.Map;

/**
 * 责任链handlerContext
 *
 * @author benym
 * @since 2024/2/17 14:50
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
}
