/*
 * Copyright 2023-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rpamis.chainlet.core.entities;

import com.rpamis.chainlet.core.definition.ChainHandler;
import com.rpamis.chainlet.core.definition.ChainOrder;

import java.io.Serializable;
import java.util.*;

/**
 * 唯一List
 *
 * @author benym
 * @since 2023/3/30 22:39
 */
public class UniqueList<T> extends ArrayList<T> implements Serializable {

    private static final long serialVersionUID = 8938720319606704517L;

    private final Set<Class<?>> handlerSet = new HashSet<>();

    @Override
    public boolean add(T chainHandler) {
        if (handlerSet.contains(chainHandler.getClass())) {
            throw new ChainException("Class " + chainHandler.getClass().getSimpleName() + " already exists in the ChainPipeline.");
        }
        handlerSet.add(chainHandler.getClass());
        return super.add(chainHandler);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        for (T chainHandler : collection) {
            if (handlerSet.contains(chainHandler.getClass())) {
                throw new ChainException("Class " + chainHandler.getClass().getSimpleName() + " already exists in the ChainPipeline.");
            }
            handlerSet.add(chainHandler.getClass());
        }
        return super.addAll(collection);
    }

    /**
     * 将Handler按照Order进行排序
     */
    @SuppressWarnings("unchecked")
    public void sortByOrder() {
        sort(Comparator.comparingInt(handler -> getOrderValue((ChainHandler<T>) handler)));
    }

    /**
     * 获取Handler的执行顺序
     *
     * @param chainHandler chainHandler
     * @return int
     */
    public int getOrderValue(ChainHandler<T> chainHandler) {
        ChainOrder chainOrder = chainHandler.getClass().getAnnotation(ChainOrder.class);
        return (chainOrder != null) ? chainOrder.value() : Integer.MAX_VALUE;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
