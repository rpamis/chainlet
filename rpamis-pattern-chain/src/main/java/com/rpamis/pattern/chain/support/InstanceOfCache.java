package com.rpamis.pattern.chain.support;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 用于减少instanceof的次数
 *
 * @author benym
 * @date 2023/12/29 17:43
 */
public class InstanceOfCache {

    private static final Map<Class<?>, Boolean> classCache = new WeakHashMap<>();

    /**
     * 判断一个类是否是另一个类或其子类的实例
     *
     * @param sourceClass 待判断的类
     * @param targetClass 目标类
     * @return 若sourceClass是targetClass或其子类的实例，则返回true；否则返回false
     */
    public static boolean instanceofCheck(Class<?> sourceClass, Class<?> targetClass) {
        Boolean isInstanceOfTarget = classCache.get(sourceClass);
        if (isInstanceOfTarget != null) {
            return isInstanceOfTarget;
        }
        // 判断sourceClass是否是targetClass或者其子类的实例
        if (targetClass.isAssignableFrom(sourceClass)) {
            // 将结果存入缓存中
            classCache.put(targetClass, true);
            return true;
        } else {
            // 将结果存入缓存中
            classCache.put(targetClass, false);
            return false;
        }
    }
}
