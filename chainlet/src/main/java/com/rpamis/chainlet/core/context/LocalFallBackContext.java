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

/**
 * 责任链局部降级上下文
 *
 * @author benym
 * @since 2023/8/23 14:16
 */
public class LocalFallBackContext<T> extends FallBackContext<T> {

    private static final long serialVersionUID = 364337999955615297L;

    public LocalFallBackContext(T handlerData, Boolean exceptionOccurred) {
        this.handlerData = handlerData;
        this.exceptionOccurred = exceptionOccurred;
    }
}
