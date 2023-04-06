package cn.rpamis.pattern.chain.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 唯一List
 *
 * @date 2023/3/30 22:39
 * @author benym
 */
public class UniqueList<T> extends ArrayList<T> implements Serializable {

    private static final long serialVersionUID = 8938720319606704517L;

    private final Set<Class<?>> handlerSet = new HashSet<>();

    @Override
    public boolean add(T chainHandler) {
        if (handlerSet.contains(chainHandler.getClass())) {
            throw new IllegalArgumentException("Class " + chainHandler.getClass().getSimpleName() + " already exists in the ChainPipeline.");
        }
        handlerSet.add(chainHandler.getClass());
        return super.add(chainHandler);
    }
}
