package com.rpamis.pattern.chain.interfaces;


import com.rpamis.pattern.chain.entity.ChainContext;
import com.rpamis.pattern.chain.entity.ChainResult;

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
     * @return boolean
     */
    boolean process(T handlerData);

    /**
     * 责任链处理器自定义提示消息
     *
     * @return String
     */
    default String message() {
        return "";
    }
}
