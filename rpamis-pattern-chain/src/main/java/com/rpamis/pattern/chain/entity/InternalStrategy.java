package com.rpamis.pattern.chain.entity;

/**
 * 内部策略枚举
 *
 * @author benym
 * @date 2023/10/31 21:37
 */
public enum InternalStrategy {

    /**
     * 快速失败策略
     */
    FAST_FAILED("fastFailed"),
    /**
     * 快速返回策略
     */
    FAST_RETURN("fastReturn"),
    /**
     * 全执行策略
     */
    FULL_EXECUTION("fullExecution");

    /**
     * 对应SPI code
     */
    private String code;

    InternalStrategy(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
