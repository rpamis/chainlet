package com.rpamis.pattern.chain.plugin;

import com.google.auto.service.AutoService;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.rpamis.pattern.chain.plugin.ChainCodeProcessor.VERBOSE;
import static javax.tools.Diagnostic.Kind.NOTE;

/**
 * 责任链工厂代码生成
 *
 * @author benym
 * @date 2024/1/3 23:54
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(value = {
        "com.rpamis.pattern.chain.plugin.ChainFactory"
})
@SupportedOptions({VERBOSE})
public class ChainCodeProcessor extends AbstractProcessor {

    protected static final String VERBOSE = "chain.verbose";

    /**
     * Javac树解析工具
     */
    private JavacTrees trees;
    /**
     * TreeMaker实例，用于创建语法树
     */
    private TreeMaker maker;
    /**
     * 名称解析工具
     */
    private Names names;
    /**
     * 日志工具
     */
    private Messager messager;

    /**
     * 元素解析器
     */
    private Elements elements;
    /**
     * 打印详细信息参数
     */
    private boolean verbose;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.trees = JavacTrees.instance(context);
        this.maker = TreeMaker.instance(context);
        this.names = Names.instance(context);
        this.messager = processingEnv.getMessager();
        this.elements = processingEnv.getElementUtils();
        this.verbose = Boolean.parseBoolean(processingEnv.getOptions().get(VERBOSE));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty() || roundEnv.processingOver()) {
            return true;
        }
        // 获取所有ChainFactory类
        Set<TypeElement> factoryClasses = new HashSet<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(ChainFactory.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                factoryClasses.add((TypeElement) element);
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "@ChainFactory can only be applied to class", element);
            }
        }
        // 获取所有ChainBuilder类
        Set<String> builderNameSet = new HashSet<>();
        Map<String, String> buidlerClassToPackageNameMap = new ConcurrentHashMap<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(ChainBuilder.class)) {
            if (element.getKind() == ElementKind.INTERFACE) {
                TypeElement builderTypeElement = (TypeElement) element;
                String builderClassName = builderTypeElement.getSimpleName().toString();
                builderNameSet.add(builderClassName);
                String builderPackageName = ProcessorSupport.getPackageName(builderTypeElement);
                buidlerClassToPackageNameMap.put(builderClassName, builderPackageName);
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "@ChainBuilder can only be applied to interfaces", element);
            }
        }
        // 遍历所有ChainFactory进行代码生成
        for (TypeElement factoryElement : factoryClasses) {
            if (verbose) {
                messager.printMessage(NOTE, "@ChainFactory, process class: " + factoryElement.getSimpleName(), factoryElement);
            }
            JCTree.JCClassDecl classDecl = trees.getTree(factoryElement);
            TreePath treePath = trees.getPath(factoryElement);
            importNeedClass(treePath, "com.rpamis.pattern.chain.builder", "ChainPipelineCache");
            handlerChainFactory(classDecl, builderNameSet);
        }
        return true;
    }

    /**
     * 导入需要的类
     *
     * @param treePath    树路径
     * @param packageName 包名
     * @param className   类名
     */
    private void importNeedClass(TreePath treePath, String packageName, String className) {
        JCTree.JCCompilationUnit jccu = (JCTree.JCCompilationUnit) treePath.getCompilationUnit();
        // 遍历所有的导入语句
        for (JCTree.JCImport jcTree : jccu.getImports()) {
            if (jcTree != null) {
                // 判断是否为字段访问类型
                if (jcTree.qualid instanceof JCTree.JCFieldAccess) {
                    JCTree.JCFieldAccess jcFieldAccess = (JCTree.JCFieldAccess) jcTree.qualid;
                    // 判断包名和类名是否匹配，如果匹配说明方法已经存在，不用生成
                    if (packageName.equals(jcFieldAccess.selected.toString()) && className.equals(jcFieldAccess.name.toString())) {
                        return;
                    }
                }
            }
        }
        // 获取原有的import列表
        java.util.List<JCTree> trees = new ArrayList<>(jccu.defs);
        // 创建需要导入的类的标识符
        JCTree.JCIdent ident = maker.Ident(names.fromString(packageName));
        // 创建导入语句
        JCTree.JCImport jcImport = maker.Import(maker.Select(
                ident, names.fromString(className)), false);
        // 判断是否已经导入
        if (!trees.contains(jcImport)) {
            trees.add(0, jcImport);
        }
        // 更新定义列表
        jccu.defs = List.from(trees);
    }


    /**
     * 处理ChainFactory
     *
     * @param classDecl      classDecl
     * @param builderNameSet builderNameSet
     */
    private void handlerChainFactory(JCTree.JCClassDecl classDecl, Set<String> builderNameSet) {
        List<JCTree> methodDecls = List.nil();
        // 记录初始pos
        int pos = classDecl.pos;
        // 遍历所有builder
        for (String builderName : builderNameSet) {
            String builderMethodName = ProcessorSupport.getChainBuilderMethodName(builderName);
            // 判断该方法是否已经存在
            if (!ProcessorSupport.methodExists(builderMethodName, classDecl)) {
                // 不存在则创建
                JCTree.JCMethodDecl methodBuilder = this.createChainBuilder(builderName, builderMethodName, pos);
                methodDecls = methodDecls.append(methodBuilder);
                if (verbose) {
                    messager.printMessage(NOTE, "createBuilder: " + methodBuilder);
                }
            } else {
                if (verbose) {
                    messager.printMessage(NOTE, "methodExists: " + builderMethodName);
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
    private JCTree.JCMethodDecl createChainBuilder(String builderName, String builderMethodName, int pos) {
        System.out.printf("当前builderName：" + builderName + "当前builderMethodName" + builderMethodName);
        // 创建泛型参数 <T>
        JCTree.JCTypeParameter typeParam = maker.TypeParameter(names.fromString("T"), List.nil(), List.nil());

        // 创建方法参数列表
        List<JCTree.JCVariableDecl> params = List.of(
                maker.VarDef(maker.Modifiers(Flags.PARAMETER), names.fromString("chainId"), maker.Ident(names.fromString("String")), null),
                maker.VarDef(maker.Modifiers(Flags.PARAMETER), names.fromString("chainTypeReference"), createChainTypeReferenceType(), null)
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
                builderMethodName
        ))));
        // 创建方法声明，将泛型参数和参数列表添加到方法中
        return maker.MethodDef(
                maker.Modifiers(Flags.PUBLIC | Flags.STATIC),
                names.fromString(builderMethodName),
                returnType,
                List.of(typeParam), // 将泛型参数添加到类型参数列表中
                params,             // 将参数列表添加到方法中
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
    private JCTree.JCExpression createChainTypeReferenceType() {
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
    private JCTree.JCMethodInvocation createGetChainInvocation(JCTree.JCExpression chainId, JCTree.JCExpression chainTypeReference, String builderMethodName) {
        return maker.Apply(
                List.nil(),
                maker.Select(maker.Ident(names.fromString("ChainPipelineCache")), names.fromString(builderMethodName)),
                List.of(chainId, chainTypeReference)
        );
    }
}
