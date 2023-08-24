package com.rpamis.pattern.chain.fallback;

import com.rpamis.pattern.chain.definition.ChainFallBack;
import com.rpamis.pattern.chain.entity.*;
import com.rpamis.pattern.chain.generic.ChainTypeReference;
import com.rpamis.pattern.chain.definition.ChainHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 降级解析器
 *
 * @author benym
 * @date 2023/8/18 14:04
 */
public class FallBackResolver<T> extends AbstractFallBackResolverSupport {

    /**
     * 处理责任链局部降级方法
     *
     * @param chainHandler         责任链具体处理者
     * @param localFallBackContext 责任链局部降级上下文
     * @param reference            责任链TypeReference
     */
    public void handleLocalFallBack(ChainHandler<T> chainHandler, LocalFallBackContext<T> localFallBackContext, ChainTypeReference<T> reference) {
        try {
            Class<? super T> actualGenericClass = reference.getGenericClass();
            // 获取process接口Method
            Method processMethod = chainHandler.getClass().getMethod("process", actualGenericClass);
            if (processMethod.isAnnotationPresent(LocalChainFallback.class)) {
                LocalChainFallback fallbackAnnotation = processMethod.getAnnotation(LocalChainFallback.class);
                String fallbackMethodName = fallbackAnnotation.fallbackMethod();
                Class<?>[] fallbackClass = fallbackAnnotation.fallbackClass();
                boolean enabled = fallbackAnnotation.enable();
                if (!enabled) {
                    return;
                }
                // 获取fallback接口Method
                Method method;
                if (fallbackClass != null && fallbackClass.length >= 1) {
                    method = findLocalFallBackMethod(fallbackMethodName, fallbackClass[0]);
                } else {
                    method = findLocalFallBackMethod(fallbackMethodName, chainHandler.getClass());
                }
                // 执行fallback
                method.invoke(chainHandler, localFallBackContext);
            }
        } catch (NoSuchMethodException e) {
            throw new ChainException(chainHandler.getClass().getName() + " without correct process or fallback method, the fallback method signature requires at least 2 input parameters", e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ChainException(chainHandler.getClass().getName()
                    + "without correct fallback method, the method signature requires at least 2 input parameters, " +
                    "one for the process method and the other for Boolean type", e);
        }
    }


    /**
     * 处理责任链全局降级方法
     *
     * @param chainFallBack       全局降级方法实现类
     * @param handlerData         责任链处理主数据
     * @param completeChainResult 责任链执行最终结果实体类
     */
    public void handleGlobalFallBack(ChainFallBack<T> chainFallBack, T handlerData, CompleteChainResult completeChainResult, Boolean exceptionOccurred) {
        GlobalFallBackContext<T> globalFallBackContext = new GlobalFallBackContext<>(handlerData, completeChainResult, exceptionOccurred);
        if (Boolean.FALSE.equals(exceptionOccurred)) {
            boolean allow = completeChainResult.isAllow();
            if (chainFallBack != null && !allow) {
                chainFallBack.fallBack(globalFallBackContext);
            }
        } else {
            if (chainFallBack != null) {
                chainFallBack.fallBack(globalFallBackContext);
            }
        }
    }
}
