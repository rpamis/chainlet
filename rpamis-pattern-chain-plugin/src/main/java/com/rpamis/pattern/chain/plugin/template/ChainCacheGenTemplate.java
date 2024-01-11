package com.rpamis.pattern.chain.plugin.template;

import com.rpamis.pattern.chain.plugin.ChainCodeProcessor;
import com.rpamis.pattern.chain.plugin.GenContext;
import com.rpamis.pattern.chain.plugin.ProcessorContext;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
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
        // 导入builder
        Set<String> builderNameSet = genContext.getBuilderNameSet();
        Map<String, String> buidlerClassToPackageNameMap = genContext.getBuidlerClassToPackageNameMap();
        for (String builderName : builderNameSet) {
            ChainCodeProcessor.importNeedClass(treePath, buidlerClassToPackageNameMap.get(builderName), builderName);
        }
        // 导入builderService
        Set<String> builderServiceNameSet = genContext.getBuilderServiceNameSet();
        Map<String, String> builderServiceToPackageNameMap = genContext.getBuilderServiceToPackageNameMap();
        for (String builderServiceName : builderServiceNameSet) {
            ChainCodeProcessor.importNeedClass(treePath, builderServiceToPackageNameMap.get(builderServiceName), builderServiceName);
        }
        // 导入内部包
        ChainCodeProcessor.importNeedClass(treePath, "com.rpamis.pattern.chain.definition", "ChainFallBack");
        ChainCodeProcessor.importNeedClass(treePath, "com.rpamis.pattern.chain.definition", "ChainHandler");
        ChainCodeProcessor.importNeedClass(treePath, "com.rpamis.pattern.chain.definition", "ChainStrategy");
        ChainCodeProcessor.importNeedClass(treePath, "com.rpamis.pattern.chain.entity", "ChainException");
        ChainCodeProcessor.importNeedClass(treePath, "com.rpamis.pattern.chain.entity", "UniqueList");
        ChainCodeProcessor.importNeedClass(treePath, "com.rpamis.pattern.chain.fallback", "GlobalChainFallBack");
        ChainCodeProcessor.importNeedClass(treePath, "com.rpamis.pattern.chain.support", "ChainTypeReference");
        // 导入必要java包
        ChainCodeProcessor.importNeedClass(treePath, "java.util", "Map");
        ChainCodeProcessor.importNeedClass(treePath, "java.util.concurrent", "ConcurrentHashMap");
    }

    @Override
    protected void genCode(JCTree.JCClassDecl classDecl, GenContext genContext, ProcessorContext processorContext) {
        Set<String> builderNameSet = genContext.getBuilderNameSet();
        List<JCTree> methodDecls = List.nil();
        // 记录初始pos
        int pos = classDecl.pos;
        // 遍历所有builder
        for (String builderName : builderNameSet) {
            // 如chain
            String builderMethodName = ChainCodeProcessor.getNameForDirector(builderName);
            // 如CHAIN_MAP
            String cacheMapName = builderMethodName.toUpperCase() + "_MAP";
            // 如registerChain
            String registerName = ChainCodeProcessor.getNameForDirectorServiceCache(builderMethodName);
            // 如getChain
            String builderGetMethodName = ChainCodeProcessor.getNameForFactory(builderName);
            // 如copyChain
            String copyChainName = "copy" + Character.toUpperCase(builderMethodName.charAt(0)) + builderMethodName.substring(1);

            // 生成代码
            genMapDefine(methodDecls, classDecl, processorContext, genContext, builderName, cacheMapName, pos);
            genRegisterChain(methodDecls, classDecl, processorContext, genContext, builderName, cacheMapName, registerName, pos);
            getCopyChain(methodDecls, classDecl, processorContext, genContext, builderName, copyChainName, pos);
            getGetChain(methodDecls, classDecl, processorContext, genContext, builderName, cacheMapName, copyChainName, builderGetMethodName, pos);
        }
        // 追加定义
        classDecl.defs = classDecl.defs.appendList(methodDecls);
    }

    private void genMapDefine(List<JCTree> methodDecls, JCTree.JCClassDecl classDecl, ProcessorContext processorContext,
                              GenContext genContext, String builderName, String cacheMapName, int pos) {
        boolean verbose = processorContext.isVerbose();
        Messager messager = processorContext.getMessager();
        Map<String, String> builderNameToServiceMap = genContext.getBuilderNameToServiceMap();
        String serviceName = builderNameToServiceMap.get(builderName);
        if (ChainCodeProcessor.variableExists(cacheMapName, classDecl)) {
            if (verbose) {
                messager.printMessage(NOTE, "variableExists in ChainCache: " + cacheMapName);
            }
        } else {
            // 不存在则创建
            JCTree.JCVariableDecl methodBuilder = this.createMap(cacheMapName, serviceName, pos, processorContext);
            methodDecls.append(methodBuilder);
            if (verbose) {
                messager.printMessage(NOTE, "createVariable in ChainCache: " + methodBuilder);
            }
        }
    }

    private JCTree.JCVariableDecl createMap(String cacheMapName, String serviceName, int pos, ProcessorContext processorContext) {
        TreeMaker maker = processorContext.getMaker();
        Names names = processorContext.getNames();
        // 构建泛型类型，比如SerialChainPipelineImpl<?>
        JCTree.JCExpression serviceNameType = maker.Ident(names.fromString(serviceName));
        JCTree.JCExpression wildcardType = maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null);
        JCTree.JCTypeApply mapGenericType = maker.TypeApply(serviceNameType, List.of(wildcardType));
        // 构建初始化语句
        JCTree.JCNewClass newMapExpression = maker.NewClass(
                null,
                List.nil(),
                maker.Ident(names.fromString("ConcurrentHashMap")),
                List.of(maker.Ident(names.fromString("String")), mapGenericType),
                null
        );

        JCTree.JCVariableDecl mapInitializer = maker.VarDef(
                maker.Modifiers(Flags.PRIVATE | Flags.STATIC | Flags.FINAL),
                names.fromString(cacheMapName),
                maker.TypeApply(maker.Ident(names.fromString("Map")), List.of(maker.Ident(names.fromString("String")), mapGenericType)),
                newMapExpression
        );
        mapInitializer.pos = pos;
        return mapInitializer;
    }

    private void genRegisterChain(List<JCTree> methodDecls, JCTree.JCClassDecl classDecl, ProcessorContext processorContext,
                                  GenContext genContext, String builderName, String cacheMapName, String registerName, int pos) {
        boolean verbose = processorContext.isVerbose();
        Messager messager = processorContext.getMessager();
        Map<String, String> builderNameToServiceMap = genContext.getBuilderNameToServiceMap();
        String serviceName = builderNameToServiceMap.get(builderName);
        // 判断该方法是否已经存在
        if (ChainCodeProcessor.methodExists(registerName, classDecl)) {
            if (verbose) {
                messager.printMessage(NOTE, "methodExists in ChainCache: " + registerName);
            }
        } else {
            // 不存在则创建
            JCTree.JCMethodDecl methodBuilder = this.createRegister(serviceName, cacheMapName, registerName, pos, processorContext);
            methodDecls.append(methodBuilder);
            if (verbose) {
                messager.printMessage(NOTE, "createRegister in ChainCache: " + methodBuilder);
            }
        }
    }

    private JCTree.JCMethodDecl createRegister(String serviceName, String cacheMapName, String registerName, int pos, ProcessorContext processorContext) {
        TreeMaker maker = processorContext.getMaker();
        Names names = processorContext.getNames();
        // 生成代码
        JCTree.JCExpression serviceNameType = maker.Ident(names.fromString(serviceName));
        JCTree.JCExpression wildcardType = maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null);
        JCTree.JCExpression chainType = maker.TypeApply(serviceNameType, List.of(wildcardType));

        // 构建 registerChain 方法的参数列表
        List<JCTree.JCVariableDecl> parameters = List.of(
                maker.VarDef(
                        maker.Modifiers(Flags.PARAMETER),
                        names.fromString("chain"),
                        chainType, null),
                maker.VarDef(
                        maker.Modifiers(Flags.PARAMETER),
                        names.fromString("chainId"),
                        maker.Ident(names.fromString("String")), null)
        );
        parameters.forEach(param -> param.pos = pos);

        // 构建 if 语句的条件
        JCTree.JCExpression condition = maker.Apply(
                List.nil(),
                maker.Select(maker.Ident(names.fromString(cacheMapName)), names.fromString("containsKey")),
                List.of(maker.Ident(names.fromString("chainId")))
        );

        // 构建 if 语句的异常抛出
        JCTree.JCExpression exceptionMessage = maker.Binary(JCTree.Tag.PLUS,
                maker.Literal("There is already a chain with chainId ["),
                maker.Binary(JCTree.Tag.PLUS, maker.Ident(names.fromString("chainId")),
                        maker.Literal("], please change your chainId"))
        );

        JCTree.JCNewClass chainException = maker.NewClass(
                null,
                List.nil(),
                maker.Ident(names.fromString("ChainException")),
                List.of(exceptionMessage),
                null
        );

        JCTree.JCExpressionStatement throwStatement = maker.Exec(chainException);

        // 构建 if 语句
        JCTree.JCIf ifStatement = maker.If(condition, throwStatement, null);

        // 构建 put 方法调用
        JCTree.JCExpression putMethod = maker.Apply(
                List.nil(),
                maker.Select(maker.Ident(names.fromString(cacheMapName)), names.fromString("put")),
                List.of(maker.Ident(names.fromString("chainId")), maker.Ident(names.fromString("chain")))
        );

        JCTree.JCExpressionStatement putStatement = maker.Exec(putMethod);

        // 构建方法体
        JCTree.JCBlock methodBody = maker.Block(0, List.of(ifStatement, putStatement));

        // 构建 registerChain 方法
        return maker.MethodDef(
                maker.Modifiers(Flags.PUBLIC | Flags.STATIC),
                names.fromString(registerName),
                maker.TypeIdent(TypeTag.VOID),
                List.nil(),
                parameters,
                List.nil(),
                methodBody,
                null
        );
    }

    private void getCopyChain(List<JCTree> methodDecls, JCTree.JCClassDecl classDecl, ProcessorContext processorContext,
                              GenContext genContext, String builderName, String copyChainName, int pos) {
        boolean verbose = processorContext.isVerbose();
        Messager messager = processorContext.getMessager();
        Map<String, String> builderNameToServiceMap = genContext.getBuilderNameToServiceMap();
        String serviceName = builderNameToServiceMap.get(builderName);
        // 判断该方法是否已经存在
        if (ChainCodeProcessor.methodExists(copyChainName, classDecl)) {
            if (verbose) {
                messager.printMessage(NOTE, "methodExists in ChainCache: " + copyChainName);
            }
        } else {
            String directorChainName = ChainCodeProcessor.getNameForDirector(builderName);
            // 不存在则创建
            JCTree.JCMethodDecl methodBuilder = this.createCopyChain(serviceName, builderName, directorChainName, copyChainName, pos, processorContext);
            methodDecls.append(methodBuilder);
            if (verbose) {
                messager.printMessage(NOTE, "createCopyChain in ChainCache: " + methodBuilder);
            }
        }
    }

    private JCTree.JCMethodDecl createCopyChain(String serviceName, String builderName, String directorChainName, String copyChainName, int pos, ProcessorContext processorContext) {
        String verifyMethodName = "verifyChainTypeReference";
        String createChainMethodName = "createChain";
        TreeMaker maker = processorContext.getMaker();
        Names names = processorContext.getNames();
        JCTree.JCExpression serviceNameType = maker.Ident(names.fromString(serviceName));
        JCTree.JCExpression builderType = maker.Ident(names.fromString(builderName));
        JCTree.JCExpression chainTypeReferenceType = maker.Ident(names.fromString("ChainTypeReference"));
        JCTree.JCExpression returnType = maker.TypeApply(builderType, List.of(maker.Ident(names.fromString("T"))));

        // 构建 copyChain 方法的参数列表
        List<JCTree.JCVariableDecl> parameters = List.of(
                maker.VarDef(
                        maker.Modifiers(Flags.PARAMETER),
                        names.fromString("chainId"),
                        maker.Ident(names.fromString("String")), null),
                maker.VarDef(
                        maker.Modifiers(Flags.PARAMETER),
                        names.fromString("chain"),
                        maker.TypeApply(serviceNameType, List.of(maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null))), null),
                maker.VarDef(
                        maker.Modifiers(Flags.PARAMETER),
                        names.fromString("chainTypeReference"),
                        maker.TypeApply(chainTypeReferenceType, List.of(maker.Ident(names.fromString("T")))), null)
        );
        parameters.forEach(param -> param.pos = pos);

        // 构建 verifyChainTypeReference 方法调用
        JCTree.JCExpression verifyMethod = maker.Apply(
                List.nil(),
                maker.Select(maker.Ident(names.fromString("this")), names.fromString(verifyMethodName)),
                List.of(maker.Ident(names.fromString("chainId")),
                        maker.Select(maker.Apply(List.nil(), maker.Select(maker.Ident(names.fromString("chain")),
                                        names.fromString("getChainTypeReference")), List.nil()),
                                names.fromString("chainTypeReference")),
                        maker.Ident(names.fromString("chainTypeReference")))
        );

        JCTree.JCExpressionStatement verifyStatement = maker.Exec(verifyMethod);

        // 构建获取 chainStrategy、chainFallBack、handlerList 的语句
        JCTree.JCExpression chainStrategyMethod = maker.Apply(
                List.nil(),
                maker.Select(maker.Ident(names.fromString("chain")), names.fromString("getChainStrategy")),
                List.nil()
        );
        JCTree.JCExpression chainFallBackMethod = maker.Apply(
                List.nil(),
                maker.Select(maker.Ident(names.fromString("chain")), names.fromString("getChainFallBack")),
                List.nil()
        );
        JCTree.JCExpression handlerListMethod = maker.Apply(
                List.nil(),
                maker.Select(maker.Ident(names.fromString("chain")), names.fromString("getHandlerList")),
                List.nil()
        );

        // 构建创建新链的语句
        JCTree.JCExpression createChainMethod = maker.Select(maker.Apply(List.nil(), maker.Select(maker.Ident(names.fromString("ChainPipelineFactory")), names.fromString(createChainMethodName)), List.of(maker.Ident(names.fromString("chainTypeReference")))), names.fromString(directorChainName));
        JCTree.JCExpression newChainMethod = maker.Select(createChainMethod, names.fromString("chain"));

        // 构建添加 handler、globalFallback、strategy 的语句
        JCTree.JCExpression addHandlerMethod = maker.Apply(List.nil(),
                maker.Select(maker.Ident(names.fromString("newChain")),
                        names.fromString("addHandler")),
                List.of(maker.TypeCast(maker.TypeApply(maker.Ident(names.fromString("ChainHandler")),
                                List.of(maker.Ident(names.fromString("T")))),
                        handlerListMethod)));
        JCTree.JCExpression globalFallbackMethod = maker.Apply(List.nil(),
                maker.Select(maker.Ident(names.fromString("newChain")),
                        names.fromString("globalFallback")),
                List.of(maker.TypeCast(maker.TypeApply(maker.Ident(names.fromString("GlobalChainFallBack")),
                                List.of(maker.Ident(names.fromString("T")))),
                        chainFallBackMethod)));
        JCTree.JCExpression strategyMethod = maker.Apply(List.nil(),
                maker.Select(maker.Ident(names.fromString("newChain")),
                        names.fromString("strategy")),
                List.of(maker.TypeCast(maker.TypeApply(maker.Ident(names.fromString("ChainStrategy")),
                                List.of(maker.Ident(names.fromString("T")))),
                        chainStrategyMethod)));

        // 构建 return 语句
        JCTree.JCReturn returnStatement = maker.Return(newChainMethod);

        // 构建方法体
        List<JCTree.JCStatement> statements = List.of(
                verifyStatement,
                maker.Exec(addHandlerMethod),
                maker.Exec(globalFallbackMethod),
                maker.Exec(strategyMethod),
                returnStatement
        );
        JCTree.JCBlock methodBody = maker.Block(0, statements);

        // 构建 copyChain 方法
        return maker.MethodDef(
                maker.Modifiers(Flags.PUBLIC | Flags.STATIC),
                names.fromString(copyChainName),
                returnType,
                List.nil(),
                parameters,
                List.nil(),
                methodBody,
                null
        );
    }

    private void getGetChain(List<JCTree> methodDecls, JCTree.JCClassDecl classDecl, ProcessorContext processorContext,
                             GenContext genContext, String builderName, String cacheMapName, String copyChainName,
                             String builderGetMethodName, int pos) {
        boolean verbose = processorContext.isVerbose();
        Messager messager = processorContext.getMessager();
        Map<String, String> builderNameToServiceMap = genContext.getBuilderNameToServiceMap();
        String serviceName = builderNameToServiceMap.get(builderName);
        // 判断该方法是否已经存在
        if (ChainCodeProcessor.methodExists(builderGetMethodName, classDecl)) {
            if (verbose) {
                messager.printMessage(NOTE, "methodExists in ChainCache: " + builderGetMethodName);
            }
        } else {
            // 不存在则创建
            JCTree.JCMethodDecl methodBuilder = this.createGetChain(builderName, serviceName, cacheMapName, copyChainName, builderGetMethodName, pos, processorContext);
            methodDecls.append(methodBuilder);
            if (verbose) {
                messager.printMessage(NOTE, "createGetChain in ChainCache: " + methodBuilder);
            }
        }
    }

    private JCTree.JCMethodDecl createGetChain(String builderName, String serviceName, String cacheMapName, String copyChainName,
                                               String builderGetMethodName, int pos, ProcessorContext processorContext) {
        TreeMaker maker = processorContext.getMaker();
        Names names = processorContext.getNames();
        JCTree.JCExpression serviceNameType = maker.Ident(names.fromString(serviceName));
        JCTree.JCExpression builderType = maker.Ident(names.fromString(builderName));
        JCTree.JCExpression cacheMapType = maker.Ident(names.fromString("Map"));
        JCTree.JCExpression chainTypeReferenceType = maker.Ident(names.fromString("ChainTypeReference"));
        JCTree.JCExpression returnType = maker.TypeApply(builderType, List.of(maker.Ident(names.fromString("T"))));

        // 构建 getVariableChain 方法的参数列表
        List<JCTree.JCVariableDecl> parameters = List.of(
                maker.VarDef(
                        maker.Modifiers(Flags.PARAMETER),
                        names.fromString("chainId"),
                        maker.Ident(names.fromString("String")), null),
                maker.VarDef(
                        maker.Modifiers(Flags.PARAMETER),
                        names.fromString("chainTypeReference"),
                        maker.TypeApply(chainTypeReferenceType, List.of(maker.Ident(names.fromString("T")))), null)
        );

        // 构建获取 chain 的语句
        JCTree.JCExpression chainMapMethod = maker.Apply(
                List.nil(),
                maker.Select(maker.Ident(names.fromString(cacheMapName)), names.fromString("get")),
                List.of(maker.Ident(names.fromString("chainId")))
        );
        JCTree.JCVariableDecl chainVar = maker.VarDef(
                maker.Modifiers(Flags.PARAMETER),
                names.fromString("chain"),
                maker.TypeApply(serviceNameType, List.of(maker.Wildcard(maker.TypeBoundKind(BoundKind.UNBOUND), null))),
                chainMapMethod);

        // 构建判断 chain 是否为 null 的语句
        JCTree.JCExpression nullCheck = maker.If(
                maker.Binary(JCTree.Tag.EQ, maker.Ident(names.fromString("chain")),
                        null),
                maker.Throw(
                        maker.NewClass(
                                null,
                                List.nil(),
                                maker.Ident(names.fromString("ChainException")),
                                List.of(maker.Binary(JCTree.Tag.PLUS,
                                        maker.Literal("There is no chain instance for "),
                                        maker.Binary(JCTree.Tag.PLUS,
                                                maker.Ident(names.fromString("chainId")),
                                                maker.Literal(", please create chain with chainId")))),
                                null
                        )
                ),
                null
        ).getCondition();

        // 构建调用 copyChain 方法的语句
        JCTree.JCExpression copyChainMethodCall = maker.Apply(
                List.nil(),
                maker.Select(maker.Ident(names.fromString("this")), names.fromString(copyChainName)),
                List.of(maker.Ident(names.fromString("chainId")),
                        maker.Ident(names.fromString("chain")),
                        maker.Ident(names.fromString("chainTypeReference")))
        );

        // 构建 return 语句
        JCTree.JCExpression returnStatement = maker.Return(copyChainMethodCall).getExpression();

        // 构建方法体
        List<JCTree.JCStatement> statements = List.of(chainVar, maker.Exec(nullCheck), maker.Exec(returnStatement));
        JCTree.JCBlock methodBody = maker.Block(0, statements);

        // 构建 getVariableChain 方法
        return maker.MethodDef(
                maker.Modifiers(Flags.PUBLIC | Flags.STATIC),
                names.fromString(builderGetMethodName),
                returnType,
                List.nil(),
                parameters,
                List.nil(),
                methodBody,
                null
        );
    }
}
