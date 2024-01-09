package com.rpamis.pattern.chain.plugin;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 生成上下文
 *
 * @author benym
 * @date 2024/1/8 16:53
 */
public class GenContext {

    /**
     * 所有ChainDirector类
     */
    private Set<TypeElement> chainDirectorClasses;

    /**
     * 所有ChainDirectorService类
     */
    private Set<TypeElement> chainDirectorServiceClasses;

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

    /**
     * 所有ChainBuilder名称与Service类名的映射
     */
    private Map<String, String> builderNameToServiceMap;

    public GenContext() {
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

    public Set<TypeElement> getChainDirectorServiceClasses() {
        return chainDirectorServiceClasses;
    }

    public void setChainDirectorServiceClasses(Set<TypeElement> chainDirectorServiceClasses) {
        this.chainDirectorServiceClasses = chainDirectorServiceClasses;
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

    public Map<String, String> getBuilderNameToServiceMap() {
        return builderNameToServiceMap;
    }

    public void setBuilderNameToServiceMap(Map<String, String> builderNameToServiceMap) {
        this.builderNameToServiceMap = builderNameToServiceMap;
    }
}
