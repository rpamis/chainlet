package com.rpamis.pattern.chain.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记责任链Builder
 *
 * @author benym
 * @date 2024/1/3 23:37
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChainBuilder {

    /**
     * 责任链变体名称
     *
     * @return String
     */
    String value();
}
