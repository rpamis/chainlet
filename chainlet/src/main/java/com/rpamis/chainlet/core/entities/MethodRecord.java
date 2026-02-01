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

import java.lang.reflect.Method;

/**
 * Method记录类
 *
 * @author benym
 * @since 2023/8/24 21:23
 */
public class MethodRecord {

    /**
     * 反射Method
     */
    private final Method method;

    /**
     * 是否存在
     */
    private final boolean exist;

    public MethodRecord(Method method, boolean exist) {
        this.method = method;
        this.exist = exist;
    }

    /**
     * 包装Method成MethodRecord对象
     *
     * @param method method
     * @return MethodRecord
     */
    public static MethodRecord warp(Method method) {
        if (method == null) {
            return new MethodRecord(null, false);
        }
        return new MethodRecord(method, true);
    }

    public Method getMethod() {
        return method;
    }

    public boolean isExist() {
        return exist;
    }
}
