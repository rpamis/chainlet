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
package com.rpamis.chainlet.core.fluent;

import com.rpamis.chainlet.core.definition.ChainHandler;

import java.util.List;

/**
 * Add接口
 *
 * @author benym
 * @since 2023/8/21 15:07
 */
public interface Add<T> extends With<T> {

    /**
     * 添加Handler处理
     *
     * @param handler 具体的Handler处理类
     * @return Add<T>
     */
    Add<T> addHandler(ChainHandler<T> handler);

    /**
     * 添加Handler处理列表
     *
     * @param handlerList 具体的Handler处理类列表
     * @return Add<T>
     */
    Add<T> addHandler(List<ChainHandler<T>> handlerList);
}
