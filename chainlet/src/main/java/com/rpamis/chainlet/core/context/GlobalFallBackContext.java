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

import com.rpamis.chainlet.core.entities.CompleteChainResult;

/**
 * 责任链全局降级上下文
 *
 * @author benym
 * @since 2023/8/23 14:17
 */
public class GlobalFallBackContext<T> extends FallBackContext<T> {

    private static final long serialVersionUID = -7138806610509549353L;

    /**
     * 责任链最终结果实体
     */
    CompleteChainResult completeChainResult;

    public GlobalFallBackContext(T handlerData, Object processedData, CompleteChainResult completeChainResult, Boolean exceptionOccurred) {
        this.handlerData = handlerData;
        this.processedData = processedData;
        this.completeChainResult = completeChainResult;
        this.exceptionOccurred = exceptionOccurred;
    }

    public CompleteChainResult getCompleteChainResult() {
        return completeChainResult;
    }

    public void setCompleteChainResult(CompleteChainResult completeChainResult) {
        this.completeChainResult = completeChainResult;
    }
}
