package com.rpamis.pattern.chain.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记ChainDirector管理者的注解
 *
 * @author benym
 * @date 2024/1/3 23:46
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ChainDirector {

}
