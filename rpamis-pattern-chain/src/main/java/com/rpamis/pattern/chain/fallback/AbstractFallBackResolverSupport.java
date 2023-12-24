package com.rpamis.pattern.chain.fallback;

import com.rpamis.pattern.chain.definition.ChainHandler;
import com.rpamis.pattern.chain.entity.ChainException;
import com.rpamis.pattern.chain.entity.LocalFallBackContext;
import com.rpamis.pattern.chain.entity.MethodRecord;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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
    protected Method findLocalFallBackMethod(ChainHandler<?> chainHandler, String fallBackName, Class<?>[] fallBackClass) {
        if (fallBackName == null || fallBackName.trim().isEmpty()) {
            return null;
        }
        boolean mustStatic = fallBackClass != null && fallBackClass.length >= 1;
        Class<?> actualFallBackMethodClass = mustStatic ? fallBackClass[0] : chainHandler.getClass();
        MethodRecord fallBackRecord = MethodMetaDataRegistry.getLocalFallBackRecord(actualFallBackMethodClass, fallBackName);
        if (fallBackRecord == null) {
            Method method = resolverLocalFallBackMethod(fallBackName, actualFallBackMethodClass);
            MethodMetaDataRegistry.initLocalFallBackRecord(actualFallBackMethodClass, fallBackName, method);
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
                Method method = chainHandlerClass.getMethod("process", actualGenericClass);
                MethodMetaDataRegistry.initProcessRecord(chainHandlerClass, actualGenericClass, method);
                return method;
            }
            if (!processRecord.isExist()) {
                return null;
            }
            return processRecord.getMethod();
        } catch (NoSuchMethodException e) {
            throw new ChainException(chainHandlerClass.getName() + " without correct process method, " +
                    "the parameter signature of the process method needs to match the generic " + actualGenericClass.getName(), e);
        }
    }

    /**
     * 进行局部降级实际invoke
     *
     * @param chainHandler         责任链具体处理handler
     * @param method               降级方法method
     * @param localFallBackContext 降级方法上下文入参
     */
    protected void invokeActual(ChainHandler<?> chainHandler, Method method, LocalFallBackContext<?> localFallBackContext) {
        try {
            if (method == null) {
                throw new ChainException("The fallback method executed by [" + chainHandler.getClass().getSimpleName() + " was not found. Please verify that the configuration is correct ");
            }
            if (!method.isAccessible()) {
                makeAccessibleIfNecessary(method);
            }
            if (isStatic(method)) {
                method.invoke(null, localFallBackContext);
            }
            method.invoke(chainHandler, localFallBackContext);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ChainException("The invoke local fallback method failed. Check that the parameters of the fallback method are correct and that it is public", e);
        }
    }

    /**
     * 校验Method是否为空
     *
     * @param method  method
     * @param message 异常消息
     */
    protected void checkMethod(Method method, String message) {
        if (method == null) {
            throw new ChainException(message);
        }
    }

    /**
     * 在必要时将Method的可访问性设置为true
     *
     * @param method method
     */
    private static void makeAccessibleIfNecessary(Method method) {
        boolean isNotPublic = !Modifier.isPublic(method.getModifiers()) ||
                !Modifier.isPublic(method.getDeclaringClass().getModifiers());
        if (isNotPublic && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * 判断方式是否是静态方法
     *
     * @param method method
     * @return boolean
     */
    private boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
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
            Method method = fallBackClass.getMethod(fallBackName, LocalFallBackContext.class);
            Class<?> returnType = method.getReturnType();
            if (!returnType.equals(Void.TYPE)) {
                throw new ChainException("fallback method return value type error, must be void type");
            }
            return method;
        } catch (NoSuchMethodException e) {
            throw new ChainException(fallBackClass.getName() + " without correct fallback method, " +
                    "the fallback method argument signature must be LocalFallBackContext", e);
        }
    }
}
