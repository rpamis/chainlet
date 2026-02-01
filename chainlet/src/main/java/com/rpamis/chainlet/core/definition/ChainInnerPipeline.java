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

import com.rpamis.chainlet.core.entities.ChainResult;

import java.util.List;

/**
 * 泛型责任链内部流水线接口
 *
 * @author benym
 * @since 2023/12/29 14:25
 */
public interface ChainInnerPipeline<T> {

    /**
     * 流水线执行Handler处理
     *
     * @param handlerData  需要处理的数据
     * @param checkResults 责任链结果存储list
     */
    void doHandler(T handlerData, List<ChainResult> checkResults);

    /**
     * 流水线执行Handler后的处理
     */
    void afterHandler();

    /**
     * 整链结果构建
     *
     * @param checkResult 责任链存储List
     * @return boolean
     */
    boolean buildSuccess(List<ChainResult> checkResult);
}
