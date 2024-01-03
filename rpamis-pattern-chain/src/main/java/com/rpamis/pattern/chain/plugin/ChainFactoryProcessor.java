package com.rpamis.pattern.chain.plugin;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

/**
 * 责任链工厂代码生成
 *
 * @author benym
 * @date 2024/1/3 23:54
 */
public class ChainFactoryProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add("com.rpamis.pattern.chain.plugin.ChainFactory");
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }
}
