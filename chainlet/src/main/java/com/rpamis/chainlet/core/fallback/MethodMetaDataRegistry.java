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
package com.rpamis.chainlet.core.fallback;

import com.rpamis.chainlet.core.entities.MethodRecord;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Method元数据管理器
 *
 * @author benym
 * @since 2023/8/24 20:59
 */
public class MethodMetaDataRegistry {

    private MethodMetaDataRegistry() {
        throw new IllegalStateException("MethodMetaDataRegistry class prohibited instantiation");
    }

    private static final Map<String, MethodRecord> LOCAL_FALLBACK_MAP = new ConcurrentHashMap<>();

    private static final Map<String, MethodRecord> HANDLER_PROCESS_MAP = new ConcurrentHashMap<>();

    /**
     * 通过Handler Class获取process方法的MethodRecord信息
     *
     * @param chainHandlerClass  Handler Class
     * @param actualGenericClass 泛型Class
     * @return MethodRecord
     */
    public static MethodRecord getProcessRecord(Class<?> chainHandlerClass, Class<?> actualGenericClass) {
        return HANDLER_PROCESS_MAP.get(getProcessKey(chainHandlerClass, actualGenericClass));
    }

    /**
     * 初始化process方法MethodRecord信息
     *
     * @param chainHandlerClass  Handler Class
     * @param actualGenericClass 泛型Class
     * @param method             Method
     */
    public static void initProcessRecord(Class<?> chainHandlerClass, Class<?> actualGenericClass, Method method) {
        HANDLER_PROCESS_MAP.put(getProcessKey(chainHandlerClass, actualGenericClass), MethodRecord.warp(method));
    }

    /**
     * 通过Class和降级方法名称获取局部降级方法的MethodRecord信息
     *
     * @param clazz        class
     * @param fallBackName 降级方法名称
     * @return MethodRecord
     */
    public static MethodRecord getLocalFallBackRecord(Class<?> clazz, String fallBackName) {
        return LOCAL_FALLBACK_MAP.get(getFallBackKey(clazz, fallBackName));
    }

    /**
     * 初始化局部降级方法MethodRecord信息
     *
     * @param clazz        class
     * @param fallBackName 降级方法名称
     * @param method       Method
     */
    public static void initLocalFallBackRecord(Class<?> clazz, String fallBackName, Method method) {
        LOCAL_FALLBACK_MAP.put(getFallBackKey(clazz, fallBackName), MethodRecord.warp(method));
    }

    /**
     * 获取Process方法Map Key
     *
     * @param chainHandlerClass  Handler Class
     * @param actualGenericClass 泛型Class
     * @return String
     */
    public static String getProcessKey(Class<?> chainHandlerClass, Class<?> actualGenericClass) {
        return String.format("%s:%s", chainHandlerClass.getCanonicalName(), actualGenericClass.getCanonicalName());
    }

    /**
     * 获取局部降级方法Map key
     *
     * @param clazz        Handler Class或FallBack Class
     * @param fallBackName 降级方法名称
     * @return String
     */
    public static String getFallBackKey(Class<?> clazz, String fallBackName) {
        return String.format("%s:%s", clazz.getCanonicalName(), fallBackName);
    }
}
