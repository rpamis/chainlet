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
package com.rpamis.chainlet.core.support;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 用于减少instanceof的次数
 *
 * @author benym
 * @since 2023/12/29 17:43
 */
public class InstanceOfCache {

    private InstanceOfCache() {
        throw new IllegalStateException("InstanceOfCache class prohibited instantiation");
    }

    private static final Map<Class<?>, Boolean> CLASS_CACHE = new WeakHashMap<>();

    /**
     * 判断一个类是否是另一个类或其子类的实例
     *
     * @param sourceClass 待判断的类
     * @param targetClass 目标类
     * @return 若sourceClass是targetClass或其子类的实例，则返回true；否则返回false
     */
    public static boolean instanceofCheck(Class<?> sourceClass, Class<?> targetClass) {
        Boolean isInstanceOfTarget = CLASS_CACHE.get(sourceClass);
        if (isInstanceOfTarget != null) {
            return isInstanceOfTarget;
        }
        // 判断sourceClass是否是targetClass或者其子类的实例
        if (targetClass.isAssignableFrom(sourceClass)) {
            // 将结果存入缓存中
            CLASS_CACHE.put(targetClass, true);
            return true;
        } else {
            // 将结果存入缓存中
            CLASS_CACHE.put(targetClass, false);
            return false;
        }
    }
}
