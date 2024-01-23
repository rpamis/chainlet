package com.rpamis.chain.core.strategy;

/**
 * 责任链策略枚举
 *
 * @author benym
 * @date 2023/12/18 18:18
 */
public enum Strategy implements StrategyKey {
    /**
     * 责任链快速失败模式
     */
    FAST_FAILED("fastFailedStrategy"),
    /**
     * 责任链快速返回模式
     */
    FAST_RETURN("fastReturnStrategy"),
    /**
     * 责任链全执行模式
     */
    FULL("fullExecutionStrategy");

    private final String code;

    Strategy(String code) {
        this.code = code;
    }

    @Override
    public String getImplCode() {
        return code;
    }
}
