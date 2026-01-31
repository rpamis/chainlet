package com.rpamis.chainlet.plugin.factory;

import com.rpamis.chainlet.plugin.template.*;

import java.util.HashSet;
import java.util.Set;

/**
 * 生成代码模版工厂初始化器
 *
 * @author benym
 * @since 2024/2/23 15:00
 */
public class GenCodeInitializer {

    /**
     * 模版集合
     */
    protected static Set<GenCodeTemplate> genCodeTemplates = new HashSet<>();

    static {
        init();
    }

    /**
     * 初始化需要处理的模版
     * 模版处理有顺序关系
     */
    public static void init() {
        genCodeTemplates.add(new ChainDirectorGenTemplate());
        genCodeTemplates.add(new ChainDirectorServiceGenTemplate());
        genCodeTemplates.add(new ChainCacheGenTemplate());
        genCodeTemplates.add(new ChainFactoryGenTemplate());
    }
}
