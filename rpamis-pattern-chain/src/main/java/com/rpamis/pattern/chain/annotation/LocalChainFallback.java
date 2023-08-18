package com.rpamis.pattern.chain.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 责任链局部降级注解
 *
 * @author benym
 * @date 2023/8/17 14:59
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LocalChainFallback {

    /**
     * 指定的降级方法
     *
     * @return String
     */
    String fallbackMethod() default "";

    /**
     * 是否开启降级
     *
     * @return boolean
     */
    boolean enable() default true;
}
