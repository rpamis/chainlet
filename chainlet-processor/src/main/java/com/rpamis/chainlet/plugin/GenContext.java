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
package com.rpamis.chainlet.plugin;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 生成上下文
 *
 * @author benym
 * @since 2024/1/8 16:53
 */
public class GenContext {

    /**
     * 所有ChainCache类
     */
    private Set<TypeElement> chainCacheClasses;

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
     * 所有ChainBuilderService名称
     */
    private Set<String> builderServiceNameSet;
    /**
     * 所有ChainBuilder名称与包名的映射
     */
    private Map<String, String> builderClassToPackageNameMap;
    /**
     * 所有ChainBuilderService名称与包名的映射
     */
    private Map<String, String> builderServiceToPackageNameMap;

    /**
     * 所有ChainBuilder名称与Service类名的映射
     */
    private Map<String, String> builderNameToServiceMap;

    public GenContext() {
        this.chainCacheClasses = new HashSet<>();
        this.chainDirectorClasses = new HashSet<>();
        this.chainDirectorServiceClasses = new HashSet<>();
        this.factoryClasses = new HashSet<>();
        this.builderNameSet = new HashSet<>();
        this.builderServiceNameSet = new HashSet<>();
        this.builderClassToPackageNameMap = new HashMap<>();
        this.builderServiceToPackageNameMap = new HashMap<>();
        this.builderNameToServiceMap = new HashMap<>();
    }

    public Set<TypeElement> getChainCacheClasses() {
        return chainCacheClasses;
    }

    public void setChainCacheClasses(Set<TypeElement> chainCacheClasses) {
        this.chainCacheClasses = chainCacheClasses;
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

    public Set<String> getBuilderServiceNameSet() {
        return builderServiceNameSet;
    }

    public void setBuilderServiceNameSet(Set<String> builderServiceNameSet) {
        this.builderServiceNameSet = builderServiceNameSet;
    }

    public Map<String, String> getBuilderServiceToPackageNameMap() {
        return builderServiceToPackageNameMap;
    }

    public void setBuilderServiceToPackageNameMap(Map<String, String> builderServiceToPackageNameMap) {
        this.builderServiceToPackageNameMap = builderServiceToPackageNameMap;
    }

    public Map<String, String> getBuilderClassToPackageNameMap() {
        return builderClassToPackageNameMap;
    }

    public void setBuilderClassToPackageNameMap(Map<String, String> builderClassToPackageNameMap) {
        this.builderClassToPackageNameMap = builderClassToPackageNameMap;
    }

    public Map<String, String> getBuilderNameToServiceMap() {
        return builderNameToServiceMap;
    }

    public void setBuilderNameToServiceMap(Map<String, String> builderNameToServiceMap) {
        this.builderNameToServiceMap = builderNameToServiceMap;
    }
}
