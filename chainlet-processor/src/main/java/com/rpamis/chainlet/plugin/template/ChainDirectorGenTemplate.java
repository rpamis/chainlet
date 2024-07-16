package com.rpamis.chainlet.plugin.template;

import com.rpamis.chainlet.plugin.ChainCodeProcessor;
import com.rpamis.chainlet.plugin.GenContext;
import com.rpamis.chainlet.plugin.ProcessorContext;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;

import java.util.Map;
import java.util.Set;

import static javax.tools.Diagnostic.Kind.NOTE;

/**
 * 责任链指挥者生成模板
 *
 * @author benym
 * @date 2024/1/9 16:20
 */
public class ChainDirectorGenTemplate extends AbstractGenCodeTemplate {

    @Override
    protected Set<TypeElement> prepareClasses(GenContext genContext) {
        return genContext.getChainDirectorClasses();
    }

    @Override
    protected void importNeedPackage(GenContext genContext, TreePath treePath) {
        Set<String> builderNameSet = genContext.getBuilderNameSet();
        Map<String, String> buidlerClassToPackageNameMap = genContext.getBuilderClassToPackageNameMap();
        for (String builderName : builderNameSet) {
            ChainCodeProcessor.importNeedClass(treePath, buidlerClassToPackageNameMap.get(builderName), builderName);
        }
    }

    @Override
    protected void genCode(JCTree.JCClassDecl classDecl, GenContext genContext, ProcessorContext processorContext) {
        Set<String> builderNameSet = genContext.getBuilderNameSet();
        boolean verbose = processorContext.isVerbose();
        Messager messager = processorContext.getMessager();
        List<JCTree> methodDecls = List.nil();
        // 记录初始pos
        int pos = classDecl.pos;
        // 遍历所有builder
        for (String builderName : builderNameSet) {
            String builderMethodName = ChainCodeProcessor.getNameForDirector(builderName);
            // 判断该方法是否已经存在
            if (ChainCodeProcessor.methodExists(builderMethodName, classDecl)) {
                if (verbose) {
                    messager.printMessage(NOTE, "methodExists in ChainDirector: " + builderMethodName);
                }
            } else {
                // 不存在则创建
                JCTree.JCMethodDecl methodBuilder = this.createForDirector(builderName, builderMethodName, processorContext);
                JCTree.JCMethodDecl methodBuilderWithParams = this.createForDirectorWithParams(builderName, builderMethodName, pos, processorContext);
                methodDecls = methodDecls.append(methodBuilder).append(methodBuilderWithParams);
                if (verbose) {
                    messager.printMessage(NOTE, "createBuilder in ChainDirector: " + methodBuilder);
                }
            }
        }
        // 追加定义
        classDecl.defs = classDecl.defs.appendList(methodDecls);
    }

    /**
     * 生成builder同名小写方法接口
     * 如
     * <p>
     * ParallelChainPipelineBuilder<T> parallelChain();
     * <p/>
     */
    private JCTree.JCMethodDecl createForDirector(String builderName, String builderMethodName, ProcessorContext processorContext) {
        TreeMaker maker = processorContext.getMaker();
        Names names = processorContext.getNames();
        JCTree.JCModifiers modifiers = maker.Modifiers(0);
        // 获取方法返回类型，这里假设返回类型为 builderName<T>
        JCTree.JCExpression returnType = maker.TypeApply(maker.Ident(names.fromString(builderName)), List.of(maker.Ident(names.fromString("T"))));
        return maker.MethodDef(
                modifiers,
                names.fromString(builderMethodName),
                returnType,
                // 不再需要参数列表
                List.nil(),
                // 不再需要抛出的异常列表
                List.nil(),
                List.nil(),
                null,
                null
        );
    }

    /**
     * 生成builder同名方法接口，带有chainId参数
     * 如
     * <p>
     * ParallelChainPipelineBuilder<T> parallelChain(String chainId);
     * <p/>
     */
    private JCTree.JCMethodDecl createForDirectorWithParams(String builderName, String builderMethodName, int pos, ProcessorContext processorContext) {
        TreeMaker maker = processorContext.getMaker();
        Names names = processorContext.getNames();
        JCTree.JCModifiers modifiers = maker.Modifiers(0);
        // 获取方法返回类型，这里假设返回类型为 builderName<T>
        JCTree.JCExpression returnType = maker.TypeApply(maker.Ident(names.fromString(builderName)), List.of(maker.Ident(names.fromString("T"))));
        // 创建方法参数列表
        List<JCTree.JCVariableDecl> params = List.of(
                maker.VarDef(maker.Modifiers(Flags.PARAMETER), names.fromString("chainId"), maker.Ident(names.fromString("String")), null)
        );
        params.forEach(param -> param.pos = pos);
        return maker.MethodDef(
                modifiers,
                names.fromString(builderMethodName),
                returnType,
                List.nil(),
                params,
                List.nil(),
                null,
                null
        );
    }
}
