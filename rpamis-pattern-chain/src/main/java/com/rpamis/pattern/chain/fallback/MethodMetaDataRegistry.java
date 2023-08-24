package com.rpamis.pattern.chain.fallback;

import com.rpamis.pattern.chain.entity.MethodRecord;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Method元数据管理器
 *
 * @author benym
 * @date 2023/8/24 20:59
 */
public class MethodMetaDataRegistry {

    private MethodMetaDataRegistry() {
        throw new IllegalStateException("MethodMetaDataRegistry class prohibited instantiation");
    }

    private static final Map<String, MethodRecord> LOCAL_FALLBACK_MAP = new ConcurrentHashMap<>();

    /**
     * 通过Class和降级方法名称获取局部降级方法的MethodRecord信息
     *
     * @param clazz        class
     * @param fallBackName 降级方法名称
     * @return MethodRecord
     */
    public static MethodRecord getLocalFallBackRecord(Class<?> clazz, String fallBackName) {
        return LOCAL_FALLBACK_MAP.get(getKey(clazz, fallBackName));
    }

    /**
     * 初始化局部降级方法MethodRecord信息
     *
     * @param clazz        class
     * @param fallBackName 降级方法名称
     * @param method       Method
     */
    public static void initLocalFallBackRecord(Class<?> clazz, String fallBackName, Method method) {
        LOCAL_FALLBACK_MAP.put(getKey(clazz, fallBackName), MethodRecord.warp(method));
    }

    /**
     * 获取局部降级方法Map key
     *
     * @param clazz        class
     * @param fallBackName 降级方法名称
     * @return String
     */
    public static String getKey(Class<?> clazz, String fallBackName) {
        return String.format("%s:%s", clazz.getCanonicalName(), fallBackName);
    }
}
