package com.rpamis.chainlet.core.fallback;

import com.rpamis.chainlet.core.entities.CompleteChainResult;
import com.rpamis.chainlet.core.context.GlobalFallBackContext;
import com.rpamis.chainlet.core.context.LocalFallBackContext;
import com.rpamis.chainlet.core.support.ChainTypeReference;
import com.rpamis.chainlet.core.support.InstanceOfCache;
import com.rpamis.chainlet.core.definition.ChainHandler;

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
    @SuppressWarnings("unchecked")
    public void handleLocalFallBack(ChainHandler<T> chainHandler, LocalFallBackContext<T> localFallBackContext, ChainTypeReference<T> reference) {
        Class<? super T> actualGenericClass = reference.getGenericClass();
        if (InstanceOfCache.instanceofCheck(chainHandler.getClass(), LocalChainFallBack.class)) {
            ((LocalChainFallBack<T>) chainHandler).fallBack(localFallBackContext);
            return;
        }
        // 获取process接口Method
        Method processMethod = findHandlerProcessMethod(chainHandler.getClass(), actualGenericClass);
        checkMethod(processMethod, chainHandler.getClass() + " process method is null, ignore fallback execute");
        if (processMethod.isAnnotationPresent(Fallback.class)) {
            Fallback fallbackAnnotation = processMethod.getAnnotation(Fallback.class);
            String fallbackMethodName = fallbackAnnotation.fallbackMethod();
            Class<?>[] fallbackClass = fallbackAnnotation.fallbackClass();
            boolean enabled = fallbackAnnotation.enable();
            if (!enabled) {
                return;
            }
            // 获取fallback接口Method
            Method method = findLocalFallBackMethod(chainHandler, fallbackMethodName, fallbackClass);
            checkMethod(method, chainHandler.getClass() + " fallback method is null, ignore fallback execute");
            // 执行fallback
            invokeActual(chainHandler, method, localFallBackContext);
        }
    }


    /**
     * 处理责任链全局降级方法
     *
     * @param chainFallBack       全局降级方法实现类
     * @param handlerData         责任链处理主数据
     * @param completeChainResult 责任链执行最终结果实体类
     * @param exceptionOccurred   责任链执行是否发生异常
     */
    public void handleGlobalFallBack(GlobalChainFallBack<T> chainFallBack, T handlerData, CompleteChainResult completeChainResult,
                                     Boolean exceptionOccurred) {
        Object processedResult = null;
        boolean resultHaveValue = completeChainResult != null;
        if (resultHaveValue) {
            processedResult = completeChainResult.getFinalResult();
        }
        GlobalFallBackContext<T> globalFallBackContext = new GlobalFallBackContext<>(handlerData, processedResult,
                completeChainResult, exceptionOccurred);
        if (Boolean.FALSE.equals(exceptionOccurred) && resultHaveValue) {
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
