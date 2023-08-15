package com.rpamis.pattern.chain.entity;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存储所有责任链执行结果，以及整个链按照策略的计算结果
 *
 * @author benym
 * @date 2023/3/30 20:33
 */
public class CompleteChainResult implements Serializable {

    private static final long serialVersionUID = -8236680440525390183L;

    /**
     * 整个链计算结果，如果有一个false则为false
     */
    private final boolean allow;

    /**
     * 所有处理类->处理结果实体Map
     */
    private final ConcurrentHashMap<Class<?>, ChainResult> chainResultMap = new ConcurrentHashMap<>(16);

    /**
     * 处理结果List列表
     */
    private final List<ChainResult> chainResults;

    private void initMap(List<ChainResult> chainResults) {
        chainResults.forEach(chainResult -> chainResultMap.putIfAbsent(chainResult.getHandlerClass(), chainResult));
    }

    public CompleteChainResult(boolean allow, List<ChainResult> chainResults) {
        this.allow = allow;
        this.chainResults = chainResults;
        this.initMap(chainResults);
    }

    public boolean isAllow() {
        return allow;
    }

    public List<ChainResult> getChainResults() {
        return chainResults;
    }

    /**
     * 根据handlerClass类获取处理结果
     *
     * @param cls handlerClass类
     * @param <T> 泛型T
     * @return 处理结果true/false
     */
    public <T> Boolean get(Class<T> cls) {
        return chainResultMap.get(cls).isProcessResult();
    }

    /**
     * 根据handlerClass类获取处理结果，如果结果非空且处理失败，返回true
     *
     * @param handlerClass handlerClass类
     * @param <T>          泛型T
     * @return 处理结果true/false
     */
    public <T> Boolean getIfFail(Class<T> handlerClass) {
        Boolean result = this.get(handlerClass);
        return result != null && !result;
    }

    /**
     * 根据handlerClass类获取处理结果，如果结果非空且处理成功，返回true
     *
     * @param handlerClass handlerClass类
     * @param <T>          泛型T
     * @return 处理结果true/false
     */
    public <T> Boolean getIfSuccess(Class<T> handlerClass) {
        Boolean result = this.get(handlerClass);
        return result != null && result;
    }

    /**
     * 校验责任链结果，如果为处理不成功则抛出异常
     *
     * @param exceptionClass 任意exception class类
     */
    public void verifyAndThrow(Class<? extends Throwable> exceptionClass) {
        try {
            if (!Throwable.class.isAssignableFrom(exceptionClass)) {
                throw new ChainException("The provided class is not a subclass of Throwable");
            }
            Constructor<? extends Throwable> constructor = exceptionClass.getConstructor(String.class);
            for (ChainResult chainResult : this.getChainResults()) {
                Class<?> handlerClass = chainResult.getHandlerClass();
                if (Boolean.TRUE.equals(this.getIfFail(handlerClass))) {
                    String message = chainResult.getMessage();
                    throw constructor.newInstance(message);
                }
            }
        } catch (NoSuchMethodException | InstantiationException
                 | IllegalAccessException | InvocationTargetException e) {
            throw new ChainException("The provided class have no single string constructor or unable to newInstance by reflection", e);
        } catch (Throwable e) {
            throw new ChainException("unknown exception in validAndThrow", e);
        }
    }

    /**
     * // todo 这个降级策略是否应该写在start里面
     * 校验责任链处理结果，如果失败了触发降级策略
     */
    public void verifyWithFallback() {

    }
}
