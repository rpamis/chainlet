package com.rpamis.chainlet.plugin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记ChainDirector管理者实现类的注解
 *
 * @author benym
 * @date 2024/1/9 17:31
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ChainDirectorService {

}
