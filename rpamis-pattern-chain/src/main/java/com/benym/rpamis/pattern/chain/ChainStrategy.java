package com.benym.rpamis.pattern.chain;

/**
 * 责任链执行策略
 *
 * @author benym
 * @date 2023/2/1 15:20
 */
public enum ChainStrategy {

    /**
     * 快速返回
     */
    FAST_RETURN,
    /**
     * 快速失败
     */
    FAST_FAILED,
    /**
     * 全执行
     */
    ALL
}
