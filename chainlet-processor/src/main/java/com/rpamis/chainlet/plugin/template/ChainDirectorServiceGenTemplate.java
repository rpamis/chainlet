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
 * 责任链指挥者实现类生成模板
 *
 * @author benym
 * @date 2024/1/9 17:27
 */
public class ChainDirectorServiceGenTemplate extends AbstractGenCodeTemplate {
    @Override
    protected Set<TypeElement> prepareClasses(GenContext genContext) {
        return genContext.getChainDirectorServiceClasses();
    }

    @Override
    protected void importNeedPackage(GenContext genContext, TreePath treePath) {
        Set<String> builderNameSet = genContext.getBuilderNameSet();
        Map<String, String> buidlerClassToPackageNameMap = genContext.getBuilderClassToPackageNameMap();
        for (String builderName : builderNameSet) {
            ChainCodeProcessor.importNeedClass(treePath, buidlerClassToPackageNameMap.get(builderName), builderName);
        }
        Set<String> builderServiceNameSet = genContext.getBuilderServiceNameSet();
        Map<String, String> builderServiceToPackageNameMap = genContext.getBuilderServiceToPackageNameMap();
        for (String builderServiceName : builderServiceNameSet) {
            ChainCodeProcessor.importNeedClass(treePath, builderServiceToPackageNameMap.get(builderServiceName), builderServiceName);
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
            String registerName = ChainCodeProcessor.getNameForDirectorServiceCache(builderMethodName);
            // 判断该方法是否已经存在
            if (ChainCodeProcessor.methodExists(builderMethodName, classDecl)) {
                if (verbose) {
                    messager.printMessage(NOTE, "methodExists in ChainDirectorService: " + builderMethodName);
                }
            } else {
                String serviceName = builderNameToServiceMap.get(builderName);
                // 不存在则创建
                JCTree.JCMethodDecl methodBuilder = this.createForDirectorService(builderName, builderMethodName, serviceName, processorContext);
                JCTree.JCMethodDecl methodWithParamsBuilder = this.createForDirectorServiceWithParams(builderName, builderMethodName, serviceName, registerName, processorContext, pos);
                methodDecls = methodDecls.append(methodBuilder).append(methodWithParamsBuilder);
                if (verbose) {
                    messager.printMessage(NOTE, "createBuilder in ChainDirectorService: " + methodBuilder);
                }
            }
        }
        // 追加定义
        classDecl.defs = classDecl.defs.appendList(methodDecls);
    }

    /**
     * 生成Director接口实现类，带入参
     * 如
     * <p>@Override</p>
     * public SerialChainPipelineBuilder<T> chain(String chainId) {
     * SerialChainPipelineImpl<T> serialChainPipeline = new SerialChainPipelineImpl<>(chainTypeReference);
     * ChainPipelineCache.registerChain(serialChainPipeline, chainId);
     * return serialChainPipeline;
     * }
     * </p>
     */
    private JCTree.JCMethodDecl createForDirectorServiceWithParams(String builderName, String builderMethodName, String serviceName, String registerName, ProcessorContext processorContext, int pos) {
        TreeMaker maker = processorContext.getMaker();
        Names names = processorContext.getNames();
        // 构造返回类型SerialChainPipelineBuilder<T>
        JCTree.JCExpression returnType = maker.TypeApply(maker.Ident(names.fromString(builderName)),
                List.of(maker.Ident(names.fromString("T"))));
        // 构建方法参数
        JCTree.JCVariableDecl parameter = maker.VarDef(maker.Modifiers(Flags.PARAMETER),
                names.fromString("chainId"),
                maker.Ident(names.fromString("String")), null);
        List<JCTree.JCVariableDecl> params = List.of(parameter);
        params.forEach(param -> param.pos = pos);
        // 构建返回方法
        JCTree.JCExpression chainTypeReference = maker.Ident(names.fromString("chainTypeReference"));
        List<JCTree.JCExpression> constructorArgs = List.of(chainTypeReference);
        // 构建的new SerialChainPipelineImpl<>(chainTypeReference)
        JCTree.JCNewClass newClass = maker.NewClass(null, List.nil(), maker.Ident(names.fromString(serviceName)), constructorArgs, null);
        JCTree.JCExpression newClassExpression = maker.Return(newClass).getExpression();
        // 构建临时变量SerialChainPipelineImpl<T> serialChainPipeline = new SerialChainPipelineImpl<>(chainTypeReference)
        JCTree.JCVariableDecl tempVariable = maker.VarDef(
                maker.Modifiers(Flags.PARAMETER),
                names.fromString(builderMethodName),
                maker.Ident(names.fromString(serviceName)),
                newClassExpression);
        tempVariable.pos = pos;
        // 构建返回之前new的对象的临时变量
        JCTree.JCReturn returnStatement = maker.Return(maker.Ident(tempVariable.name));
        // 构建的ChainPipelineCache.registerChain(serialChainPipeline, chainId);
        JCTree.JCStatement registerChainCall = createRegisterChainCall(registerName, tempVariable, maker, names);
        JCTree.JCBlock methodBody = maker.Block(0, List.of(tempVariable, registerChainCall, returnStatement));
        // 构造方法
        return maker.MethodDef(
                maker.Modifiers(Flags.PUBLIC | Flags.OVERRIDE_BRIDGE),
                names.fromString(builderMethodName),
                returnType,
                List.nil(),
                params,
                List.nil(),
                methodBody,
                null
        );
    }

    /**
     * 构建 ChainPipelineCache.registerChain 方法调用
     */
    private JCTree.JCStatement createRegisterChainCall(String registerName, JCTree.JCVariableDecl variableDecl, TreeMaker maker, Names names) {
        JCTree.JCExpression chainPipelineCacheClass = maker.Ident(names.fromString("ChainPipelineCache"));
        JCTree.JCExpression registerChainMethod = maker.Select(chainPipelineCacheClass, names.fromString(registerName));
        List<JCTree.JCExpression> registerChainArgs = List.of(maker.Ident(variableDecl.name), maker.Ident(names.fromString("chainId")));
        return maker.Exec(maker.Apply(List.nil(), registerChainMethod, registerChainArgs));
    }

    /**
     * 生成Director接口实现类，不带入参
     * 如
     * <p>
     * <p>@Override</p>
     * public SerialChainPipelineBuilder<T> chain() {
     * return new SerialChainPipelineImpl<>(chainTypeReference);
     * }
     * </p>
     */
    private JCTree.JCMethodDecl createForDirectorService(String builderName, String builderMethodName, String serviceName, ProcessorContext processorContext) {
        TreeMaker maker = processorContext.getMaker();
        Names names = processorContext.getNames();
        // 构造返回类型SerialChainPipelineBuilder<T>
        JCTree.JCExpression returnType = maker.TypeApply(maker.Ident(names.fromString(builderName)),
                List.of(maker.Ident(names.fromString("T"))));
        // 构建返回方法
        JCTree.JCExpression chainTypeReference = maker.Ident(names.fromString("chainTypeReference"));
        List<JCTree.JCExpression> constructorArgs = List.of(chainTypeReference);
        // 构建的new SerialChainPipelineImpl<>(chainTypeReference)
        JCTree.JCNewClass newClass = maker.NewClass(null, List.nil(), maker.Ident(names.fromString(serviceName)), constructorArgs, null);
        JCTree.JCStatement returnStatement = maker.Return(newClass);
        JCTree.JCBlock methodBody = maker.Block(0, List.of(returnStatement));
        // 构造方法
        return maker.MethodDef(
                maker.Modifiers(Flags.PUBLIC | Flags.OVERRIDE_BRIDGE),
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
