package com.rpamis.pattern.chain;

import com.rpamis.pattern.chain.annotation.LocalChainFallback;
import com.rpamis.pattern.chain.entity.ChainException;
import com.rpamis.pattern.chain.entity.CompleteChainResult;
import com.rpamis.pattern.chain.entity.FallBackContext;
import com.rpamis.pattern.chain.generic.ChainTypeReference;
import com.rpamis.pattern.chain.interfaces.ChainFallBack;
import com.rpamis.pattern.chain.interfaces.ChainHandler;
import com.rpamis.pattern.chain.interfaces.ChainPipeline;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 降级解析器
 *
 * @author benym
 * @date 2023/8/18 14:04
 */
public class FallBackResolver<T> {

    /**
     * 处理责任链局部降级方法
     *
     * @param chainHandler chainHandler
     * @param handlerData  handlerData
     */
    public void handleLocalFallBack(ChainHandler<T> chainHandler, T handlerData, ChainPipeline<T> chainPipeline,
                                    Boolean haveException) {
        try {
            Class<?> actualGenericClass = ChainTypeReference.getGenericTypeClass(chainPipeline);
            // 获取process接口Method
            Method processMethod = chainHandler.getClass().getMethod("process", actualGenericClass);
            if (processMethod.isAnnotationPresent(LocalChainFallback.class)) {
                LocalChainFallback fallbackAnnotation = processMethod.getAnnotation(LocalChainFallback.class);
                String fallbackMethodName = fallbackAnnotation.fallbackMethod();
                boolean enabled = fallbackAnnotation.enable();
                if (!enabled) {
                    return;
                }
                // 获取fallback接口Method
                Method method = chainHandler.getClass().getMethod(fallbackMethodName, actualGenericClass, Boolean.class);
                Class<?> returnType = method.getReturnType();
                if (!returnType.equals(Void.TYPE)) {
                    throw new ChainException("fallback method return value type error, must be void type");
                }
                // 执行fallback
                method.invoke(chainHandler, handlerData, haveException);
            }
        } catch (NoSuchMethodException e) {
            throw new ChainException(chainHandler.getClass().getName() + "has no process interface", e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ChainException(chainHandler.getClass().getName()
                    + "without correct fallback interface, the method signature need to consistent with the process interface", e);
        } catch (ClassNotFoundException e) {
            throw new ChainException("The true generic Class for " + chainHandler.getClass().getName() + " was not found", e);
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
        FallBackContext<T> fallBackContext = new FallBackContext<>(handlerData, completeChainResult, exceptionOccurred);
        if (!exceptionOccurred) {
            boolean allow = completeChainResult.isAllow();
            if (chainFallBack != null && !allow) {
                chainFallBack.fallBack(fallBackContext);
            }
        } else {
            if (chainFallBack != null) {
                chainFallBack.fallBack(fallBackContext);
            }
        }
    }
}
