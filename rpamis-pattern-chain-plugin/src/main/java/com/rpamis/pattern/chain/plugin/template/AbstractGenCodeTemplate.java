package com.rpamis.pattern.chain.plugin.template;

import com.rpamis.pattern.chain.plugin.*;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.JCTree;

/**
 * 生成代码模版抽象类
 *
 * @author benym
 * @date 2024/1/9 16:14
 */
public abstract class AbstractGenCodeTemplate {

    /**
     * 迭代处理
     *
     * @param genContext       生成上下文
     * @param processorContext 处理上下文
     */
    protected abstract void iterableProcess(GenContext genContext, ProcessorContext processorContext);

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
    public void execute(GenContext genContext, ProcessorContext processorContext) {
        iterableProcess(genContext, processorContext);
    }
}
