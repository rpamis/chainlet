package com.rpamis.pattern.chain.plugin.template;

import com.rpamis.pattern.chain.plugin.GenContext;
import com.rpamis.pattern.chain.plugin.ProcessorContext;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static javax.tools.Diagnostic.Kind.NOTE;

/**
 * 责任链缓存生成模版
 *
 * @author benym
 * @date 2024/1/9 16:21
 */
public class ChainCacheGenTemplate extends AbstractGenCodeTemplate {

    @Override
    protected void iterableProcess(GenContext genContext, ProcessorContext processorContext) {
        boolean verbose = processorContext.isVerbose();
        Messager messager = processorContext.getMessager();
        JavacTrees trees = processorContext.getTrees();
        Set<TypeElement> chainCacheClasses = genContext.getChainCacheClasses();
        // 遍历所有ChainCache进行代码生成
        for (TypeElement serviceElement : chainCacheClasses) {
            if (verbose) {
                messager.printMessage(NOTE, "@ChainCache, process class: " + serviceElement.getSimpleName(),
                        serviceElement);
            }
            JCTree.JCClassDecl classDecl = trees.getTree(serviceElement);
            TreePath treePath = trees.getPath(serviceElement);
            importNeedPackage(genContext, treePath);
            genCode(classDecl, genContext, processorContext);
        }
    }

    @Override
    protected void importNeedPackage(GenContext genContext, TreePath treePath) {

    }

    @Override
    protected void genCode(JCTree.JCClassDecl classDecl, GenContext genContext, ProcessorContext processorContext) {

    }
}
