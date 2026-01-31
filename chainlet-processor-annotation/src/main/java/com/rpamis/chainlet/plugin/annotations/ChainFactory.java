package com.rpamis.chainlet.plugin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记需要生成责任链get方法的工厂类
 *
 * @author benym
 * @since 2024/1/3 23:35
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ChainFactory {

}
