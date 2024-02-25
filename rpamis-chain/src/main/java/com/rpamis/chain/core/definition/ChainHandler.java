package com.rpamis.chain.core.definition;


import com.rpamis.chain.core.context.ChainHandlerContext;

/**
 * 泛型责任链Handler接口
 *
 * @param <T> <T>
 * @author benym
 * @date 2023/3/8 18:24
 */
public interface ChainHandler<T> {

    /**
     * 责任链处理器执行接口
     * 执行具体handler,true表示执行成功,false表示执行失败
     *
     * @param handlerData handlerData
     * @param context     context
     * @return boolean
     */
    boolean process(T handlerData, ChainHandlerContext<T> context);

    /**
     * 责任链处理器自定义全局提示消息
     *
     * @return String
     */
    default String globalMessage() {
        return "";
    }
}
