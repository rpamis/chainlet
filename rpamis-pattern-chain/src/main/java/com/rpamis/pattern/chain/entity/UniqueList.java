package com.rpamis.pattern.chain.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 唯一List
 *
 * @author benym
 * @date 2023/3/30 22:39
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
}
