package com.rpamis.pattern.chain.plugin;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Processor上下文
 *
 * @author benym
 * @date 2024/1/8 16:53
 */
public class ProcessorContext {

    /**
     * 所有ChainDirector类
     */
    private Set<TypeElement> chainDirectorClasses;

    /**
     * 所有ChainFactory类
     */
    private Set<TypeElement> factoryClasses;

    /**
     * 所有ChainBuilder名称
     */
    private Set<String> builderNameSet;

    /**
     * 所有ChainBuilder名称与包名的映射
     */
    private Map<String, String> buidlerClassToPackageNameMap;

    public ProcessorContext() {
        this.chainDirectorClasses = new HashSet<>();
        this.factoryClasses = new HashSet<>();
        this.builderNameSet = new HashSet<>();
        this.buidlerClassToPackageNameMap = new HashMap<>();
    }

    public Set<TypeElement> getChainDirectorClasses() {
        return chainDirectorClasses;
    }

    public void setChainDirectorClasses(Set<TypeElement> chainDirectorClasses) {
        this.chainDirectorClasses = chainDirectorClasses;
    }

    public Set<TypeElement> getFactoryClasses() {
        return factoryClasses;
    }

    public void setFactoryClasses(Set<TypeElement> factoryClasses) {
        this.factoryClasses = factoryClasses;
    }

    public Set<String> getBuilderNameSet() {
        return builderNameSet;
    }

    public void setBuilderNameSet(Set<String> builderNameSet) {
        this.builderNameSet = builderNameSet;
    }

    public Map<String, String> getBuidlerClassToPackageNameMap() {
        return buidlerClassToPackageNameMap;
    }

    public void setBuidlerClassToPackageNameMap(Map<String, String> buidlerClassToPackageNameMap) {
        this.buidlerClassToPackageNameMap = buidlerClassToPackageNameMap;
    }
}
