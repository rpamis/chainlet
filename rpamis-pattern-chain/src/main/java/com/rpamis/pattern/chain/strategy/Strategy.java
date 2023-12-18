package com.rpamis.pattern.chain.strategy;

/**
 * 责任链策略枚举
 *
 * @author benym
 * @date 2023/12/18 18:18
 */
public enum Strategy {
    /**
     * 责任链快速失败模式
     */
    FAST_FAILED("fastFailedStrategy","责任链快速失败模式"),
    /**
     * 责任链快速返回模式
     */
    FAST_RETURN("fastReturnStrategy","责任链快速返回模式"),
    /**
     * 责任链全执行模式
     */
    FULL("fullExecutionStrategy","责任链全执行模式");

    private final String code;

    private final String desc;

    Strategy(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
