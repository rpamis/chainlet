package com.rpamis.chainlet.plugin.template;

import com.rpamis.chainlet.plugin.GenContext;
import com.rpamis.chainlet.plugin.ProcessorContext;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static javax.tools.Diagnostic.Kind.NOTE;

/**
 * 生成代码模版抽象类
 *
 * @author benym
 * @since 2024/1/9 16:14
 */
public abstract class AbstractGenCodeTemplate implements GenCodeTemplate {

    /**
     * 准备需要处理的class类
     *
     * @param genContext 生成上下文
     * @return Set<TypeElement>
     */
    protected abstract Set<TypeElement> prepareClasses(GenContext genContext);

    /**
     * 迭代处理
     *
     * @param processClass     需要处理的class类
     * @param genContext       生成上下文
     * @param processorContext 处理上下文
     */
    private void iterableProcess(Set<TypeElement> processClass, GenContext genContext, ProcessorContext processorContext) {
        boolean verbose = processorContext.isVerbose();
        Messager messager = processorContext.getMessager();
        JavacTrees trees = processorContext.getTrees();
        // 遍历所有需要处理的Class进行代码生成
        for (TypeElement serviceElement : processClass) {
            if (verbose) {
                messager.printMessage(NOTE, "process class: " + serviceElement.getSimpleName(),
                        serviceElement);
            }
            JCTree.JCClassDecl classDecl = trees.getTree(serviceElement);
            TreePath treePath = trees.getPath(serviceElement);
            // 导入必要的包
            importNeedPackage(genContext, treePath);
            // 生成代码
            genCode(classDecl, genContext, processorContext);
        }
    }

    /**
     * 导入需要的包
     *
     * @param genContext 生成上下文
     * @param treePath   树路径
     */
    protected abstract void importNeedPackage(GenContext genContext, TreePath treePath);

    /**
     * 生成代码
     *
     * @param classDecl        JCTree类定义
     * @param genContext       生成上下文
     * @param processorContext 处理上下文
     */
    protected abstract void genCode(JCTree.JCClassDecl classDecl, GenContext genContext, ProcessorContext processorContext);

    /**
     * 执行模版
     *
     * @param genContext       生成上下文
     * @param processorContext 处理上下文
     */
    @Override
    public void execute(GenContext genContext, ProcessorContext processorContext) {
        // 1. 准备需要处理的class类
        Set<TypeElement> processClasses = prepareClasses(genContext);
        // 2. 迭代处理生成
        iterableProcess(processClasses, genContext, processorContext);
    }
}
