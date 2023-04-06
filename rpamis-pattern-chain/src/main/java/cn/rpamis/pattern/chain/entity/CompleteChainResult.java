package cn.rpamis.pattern.chain.entity;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存储所有责任链执行结果，以及整个链按照策略的计算结果
 *
 * @date 2023/3/30 20:33
 * @author benym
 */
public class CompleteChainResult implements Serializable {

    private static final long serialVersionUID = -8236680440525390183L;

    /**
     * 整个链计算结果，如果有一个false则为false
     */
    private boolean allow;

    /**
     * 所有处理类->处理结果Map
     */
    private static final ConcurrentHashMap<Class<?>, Boolean> CHAINRESULT_MAP = new ConcurrentHashMap<>(16);

    private List<ChainResult> chainResults;

    private void initMap(List<ChainResult> chainResults) {
        chainResults.forEach(chainResult -> CHAINRESULT_MAP.putIfAbsent(chainResult.getHandlerClass(), chainResult.isProcessResult()));
    }

    public CompleteChainResult() {
    }

    public CompleteChainResult(boolean allow, List<ChainResult> chainResults) {
        this.allow = allow;
        this.chainResults = chainResults;
        this.initMap(chainResults);
    }

    public boolean isAllow() {
        return allow;
    }

    public List<ChainResult> getChainResults() {
        return chainResults;
    }

    public <T> boolean get(Class<T> cls) {
        return CHAINRESULT_MAP.get(cls);
    }
}
