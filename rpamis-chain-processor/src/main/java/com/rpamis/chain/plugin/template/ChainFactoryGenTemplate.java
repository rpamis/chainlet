package com.rpamis.chain.plugin.template;

import com.rpamis.chain.plugin.ChainCodeProcessor;
import com.rpamis.chain.plugin.GenContext;
import com.rpamis.chain.plugin.ProcessorContext;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static javax.tools.Diagnostic.Kind.NOTE;

/**
 * 责任链工厂注解生成模版
 *
 * @author benym
 * @date 2024/1/9 16:20
 */
public class ChainFactoryGenTemplate extends AbstractGenCodeTemplate {

    @Override
    protected Set<TypeElement> prepareClasses(GenContext genContext) {
        return genContext.getFactoryClasses();
    }

    @Override
    protected void importNeedPackage(GenContext genContext, TreePath treePath) {
        ChainCodeProcessor.importNeedClass(treePath, "com.rpamis.chain.core.builder", "ChainPipelineCache");
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
            String builderMethodName = ChainCodeProcessor.getNameForFactory(builderName);
            // 判断该方法是否已经存在
            if (ChainCodeProcessor.methodExists(builderMethodName, classDecl)) {
                if (verbose) {
                    messager.printMessage(NOTE, "methodExists in ChainFactory: " + builderMethodName);
                }
            } else {
                // 不存在则创建
                JCTree.JCMethodDecl methodBuilder = this.createChainBuilder(builderName, builderMethodName, pos, processorContext);
                methodDecls = methodDecls.append(methodBuilder);
                if (verbose) {
                    messager.printMessage(NOTE, "createBuilder in ChainFactory: " + methodBuilder);
                }
            }
        }
        // 追加定义
        classDecl.defs = classDecl.defs.appendList(methodDecls);
    }

    /**
     * 生成get xxx Builder方法，假设需要生成的Builder名称为ParallelChainPipelineBuilder，则生成的getBuilder代码为
     *
     * <p>
     * public static <T> ParallelChainPipelineBuilder<T> getParallelChain(String chainId, ChainTypeReference<T> chainTypeReference) {
     * return ChainPipelineCache.getParallelChain(chainId, chainTypeReference);
     * }
     * </p>
     */
    private JCTree.JCMethodDecl createChainBuilder(String builderName, String builderMethodName, int pos, ProcessorContext processorContext) {
        TreeMaker maker = processorContext.getMaker();
        Names names = processorContext.getNames();
        // 创建泛型参数 <T>
        JCTree.JCTypeParameter typeParam = maker.TypeParameter(names.fromString("T"), List.nil(), List.nil());

        // 创建方法参数列表
        List<JCTree.JCVariableDecl> params = List.of(
                maker.VarDef(maker.Modifiers(Flags.PARAMETER), names.fromString("chainId"), maker.Ident(names.fromString("String")), null),
                maker.VarDef(maker.Modifiers(Flags.PARAMETER), names.fromString("chainTypeReference"), createChainTypeReferenceType(maker, names), null)
        );
        // 创建变量后重置pos，防止编译失败
        // @see
        // <https://stackoverflow.com/questions/46874126/java-lang-assertionerror-thrown-by-compiler-when-adding-generated-method-with-pa>
        params.forEach(param -> param.pos = pos);

        // 获取方法返回类型，这里假设返回类型为 builderName<T>
        JCTree.JCExpression returnType = maker.TypeApply(maker.Ident(names.fromString(builderName)), List.of(maker.Ident(names.fromString("T"))));

        JCTree.JCBlock methodBody = maker.Block(0, List.of(maker.Return(createGetChainInvocation(
                maker.Ident(names.fromString("chainId")),
                maker.Ident(names.fromString("chainTypeReference")),
                builderMethodName, maker, names
        ))));
        // 创建方法声明，将泛型参数和参数列表添加到方法中
        return maker.MethodDef(
                maker.Modifiers(Flags.PUBLIC | Flags.STATIC),
                names.fromString(builderMethodName),
                returnType,
                // 将泛型参数添加到类型参数列表中
                List.of(typeParam),
                // 将参数列表添加到方法中
                params,
                List.nil(),
                methodBody,
                null
        );
    }


    /**
     * 创建ChainTypeReference<T>类型
     *
     * @return JCTree.JCExpression
     */
    private JCTree.JCExpression createChainTypeReferenceType(TreeMaker maker, Names names) {
        return maker.TypeApply(maker.Ident(names.fromString("ChainTypeReference")), List.of(maker.Ident(names.fromString("T"))));
    }


    /**
     * 创建获取链的方法调用
     *
     * @param chainId            chainId表达式
     * @param chainTypeReference chainTypeReference表达式
     * @param builderMethodName  builderMethodName
     * @return JCTree.JCMethodInvocation
     */
    private JCTree.JCMethodInvocation createGetChainInvocation(JCTree.JCExpression chainId, JCTree.JCExpression chainTypeReference,
                                                               String builderMethodName, TreeMaker maker, Names names) {
        return maker.Apply(
                List.nil(),
                maker.Select(maker.Ident(names.fromString("ChainPipelineCache")), names.fromString(builderMethodName)),
                List.of(chainId, chainTypeReference)
        );
    }
}
