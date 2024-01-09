package com.rpamis.pattern.chain.plugin.template;

import com.rpamis.pattern.chain.plugin.ChainCodeProcessor;
import com.rpamis.pattern.chain.plugin.GenContext;
import com.rpamis.pattern.chain.plugin.ProcessorContext;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
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
 * 责任链指挥者实现类生成模板
 *
 * @author benym
 * @date 2024/1/9 17:27
 */
public class ChainDirectorServiceGenTemplate extends AbstractGenCodeTemplate {
    @Override
    protected void iterableProcess(GenContext genContext, ProcessorContext processorContext) {
        boolean verbose = processorContext.isVerbose();
        Messager messager = processorContext.getMessager();
        JavacTrees trees = processorContext.getTrees();
        Set<TypeElement> chainDirectorServiceClasses = genContext.getChainDirectorServiceClasses();
        // 遍历所有ChainDirector进行代码生成
        for (TypeElement serviceElement : chainDirectorServiceClasses) {
            if (verbose) {
                messager.printMessage(NOTE, "@ChainDirectorService, process class: " + serviceElement.getSimpleName(),
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
        Set<String> builderNameSet = genContext.getBuilderNameSet();
        Map<String, String> buidlerClassToPackageNameMap = genContext.getBuidlerClassToPackageNameMap();
        for (String builderName : builderNameSet) {
            ChainCodeProcessor.importNeedClass(treePath, buidlerClassToPackageNameMap.get(builderName), builderName);
        }
    }

    @Override
    protected void genCode(JCTree.JCClassDecl classDecl, GenContext genContext, ProcessorContext processorContext) {
        Map<String, String> builderNameToServiceMap = genContext.getBuilderNameToServiceMap();
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
            if (!ChainCodeProcessor.methodExists(builderMethodName, classDecl)) {
                String serviceName = builderNameToServiceMap.get(builderName);
                // 不存在则创建
                JCTree.JCMethodDecl methodBuilder = this.createForDirectorService(builderName, builderMethodName, serviceName, processorContext);
                methodDecls = methodDecls.append(methodBuilder);
                if (verbose) {
                    messager.printMessage(NOTE, "createBuilder in ChainDirector: " + methodBuilder);
                }
            } else {
                if (verbose) {
                    messager.printMessage(NOTE, "methodExists in ChainDirector: " + builderMethodName);
                }
            }
        }
        // 追加定义
        classDecl.defs = classDecl.defs.appendList(methodDecls);
    }

    private JCTree.JCMethodDecl createForDirectorService(String builderName, String builderMethodName, String serviceName, ProcessorContext processorContext) {
        TreeMaker maker = processorContext.getMaker();
        Names names = processorContext.getNames();
        // 构造返回类型
        JCTree.JCExpression returnType = maker.Ident(names.fromString(builderName));
        // 构造方法体
        JCTree.JCBlock methodBody = null;
        // 构造方法
        return maker.MethodDef(
                maker.Modifiers(Flags.PUBLIC),
                names.fromString(builderMethodName),
                returnType,
                List.nil(),
                List.nil(),
                List.nil(),
                methodBody,
                null
        );
    }
}
