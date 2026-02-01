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
package com.rpamis.chainlet.core.definition;


import com.rpamis.chainlet.core.context.ChainHandlerContext;

/**
 * 泛型责任链Handler接口
 *
 * @param <T> <T>
 * @author benym
 * @since 2023/3/8 18:24
 */
public interface ChainHandler<T> {

    /**
     * 责任链处理器执行接口
     * 执行具体handler,true表示执行成功,false表示执行失败
     *
     * @param handlerData handlerData
     * @param context     context
     * @return boolean
     */
    boolean process(T handlerData, ChainHandlerContext<T> context);

    /**
     * 责任链处理器自定义全局提示消息
     *
     * @return String
     */
    default String globalMessage() {
        return "";
    }
}
