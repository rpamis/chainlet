package com.rpamis.chain.core.entity;

import java.lang.reflect.Method;

/**
 * Method记录类
 *
 * @author benym
 * @date 2023/8/24 21:23
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
