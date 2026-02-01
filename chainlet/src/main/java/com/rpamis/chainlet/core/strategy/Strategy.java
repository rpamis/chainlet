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
package com.rpamis.chainlet.core.strategy;

/**
 * 责任链策略枚举
 *
 * @author benym
 * @since 2023/12/18 18:18
 */
public enum Strategy implements StrategyKey {
    /**
     * 责任链快速失败模式
     */
    FAST_FAILED("fastFailedStrategy"),
    /**
     * 责任链快速返回模式
     */
    FAST_RETURN("fastReturnStrategy"),
    /**
     * 责任链全执行模式
     */
    FULL("fullExecutionStrategy");

    private final String code;

    Strategy(String code) {
        this.code = code;
    }

    @Override
    public String getImplCode() {
        return code;
    }
}
