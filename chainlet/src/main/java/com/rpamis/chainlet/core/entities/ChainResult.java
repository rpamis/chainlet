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
package com.rpamis.chainlet.core.entities;

import java.io.Serializable;

/**
 * 单个责任链结果存储实体
 *
 * @author benym
 * @since 2023/3/7 14:41
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
}
