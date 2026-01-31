package com.rpamis.chainlet.plugin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 责任链builder注解
 *
 * @author benym
 * @since 2024/1/5 11:31
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ChainBuilder {

}
