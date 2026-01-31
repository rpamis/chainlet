package com.rpamis.chainlet.core.definition;

import java.lang.annotation.*;

/**
 * 责任链执行顺序注解
 *
 * @author benym
 * @since 2023/12/24 15:59
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface ChainOrder {

    /**
     * 指定责任链Handler执行顺序，数字越小越先执行
     *
     * @return int
     */
    int value() default Integer.MAX_VALUE;
}
