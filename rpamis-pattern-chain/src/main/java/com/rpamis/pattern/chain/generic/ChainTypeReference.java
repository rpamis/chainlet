package com.rpamis.pattern.chain.generic;

import com.rpamis.pattern.chain.entity.ChainException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 责任链TypeReference
 *
 * @author benym
 * @date 2023/8/17 16:29
 */
public abstract class ChainTypeReference<T> {

    /**
     * 获取运行时真实泛型
     * 如获取A<T>时候的T的Type
     * 获取T data时的T的Type
     *
     * @param genericClass 泛型类
     * @param <T>          <T>
     * @return 泛型Type
     */
    public static <T> Type getGenericType(T genericClass) {
        Type type = genericClass.getClass().getGenericSuperclass();
        if (type == null) {
            return null;
        }
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            return types[0];
        }
        return null;
    }

    /**
     * 获取运行时泛型实际类型Class
     * 如获取A<T>时候的T的Class
     * 获取T data时的T的Class
     *
     * @param genericClass 泛型类
     * @param <T>          <T>
     * @return 泛型Class
     * @throws ClassNotFoundException ClassNotFoundException
     */
    public static <T> Class<?> getGenericTypeClass(T genericClass) throws ClassNotFoundException {
        Type type = getGenericType(genericClass);
        if (type == null) {
            throw new ChainException("Unable to get generic class at runtime");
        }
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        String typeName = type.getTypeName();
        return Class.forName(typeName);
    }
}
