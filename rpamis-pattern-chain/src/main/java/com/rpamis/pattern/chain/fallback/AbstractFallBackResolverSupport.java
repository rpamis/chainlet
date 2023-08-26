package com.rpamis.pattern.chain.fallback;

import com.rpamis.pattern.chain.entity.ChainException;
import com.rpamis.pattern.chain.entity.MethodRecord;

import java.lang.reflect.Method;

/**
 * 抽象降级方法支持类
 *
 * @author benym
 * @date 2023/8/24 15:05
 */
public abstract class AbstractFallBackResolverSupport {

    /**
     * 根据降级方法名称，降级方法Class寻找缓存的Method
     *
     * @param fallBackName  降级方法名
     * @param fallBackClass 降级方法Class
     * @return Method
     */
    protected Method findLocalFallBackMethod(String fallBackName, Class<?> fallBackClass) {
        if (fallBackName == null || fallBackName.trim().length() == 0) {
            return null;
        }
        MethodRecord fallBackRecord = MethodMetaDataRegistry.getLocalFallBackRecord(fallBackClass, fallBackName);
        if (fallBackRecord == null) {
            Method method = resolverLocalFallBackMethod(fallBackName, fallBackClass);
            MethodMetaDataRegistry.initLocalFallBackRecord(fallBackClass, fallBackName, method);
            return method;
        }
        if (!fallBackRecord.isExist()) {
            return null;
        }
        return fallBackRecord.getMethod();
    }


    /**
     * 根据责任链处理类Class，主数据泛型Class寻找缓存的Method
     *
     * @param chainHandlerClass  责任链处理类Class
     * @param actualGenericClass 主数据泛型Class
     * @return Method
     */
    protected Method findHandlerProcessMethod(Class<?> chainHandlerClass, Class<?> actualGenericClass) {
        try {
            MethodRecord processRecord = MethodMetaDataRegistry.getProcessRecord(chainHandlerClass, actualGenericClass);
            if (processRecord == null) {
                return chainHandlerClass.getMethod("process", actualGenericClass);
            }
            if (!processRecord.isExist()) {
                return null;
            }
            return processRecord.getMethod();
        } catch (NoSuchMethodException e) {
            throw new ChainException(chainHandlerClass.getName() + " without correct process or fallback method, the fallback method signature requires at least 2 input parameters", e);
        }
    }

    /**
     * 解析局部降级方法
     *
     * @param fallBackName  降级方法名
     * @param fallBackClass 降级方法Class
     * @return Method
     */
    private Method resolverLocalFallBackMethod(String fallBackName, Class<?> fallBackClass) {
        try {
            Method method = fallBackClass.getMethod(fallBackName, LocalChainFallback.class);
            Class<?> returnType = method.getReturnType();
            if (!returnType.equals(Void.TYPE)) {
                throw new ChainException("fallback method return value type error, must be void type");
            }
            return method;
        } catch (NoSuchMethodException e) {
            throw new ChainException(fallBackClass.getName() + " without correct process or fallback method, the fallback method signature requires at least 2 input parameters", e);
        }
    }
}
