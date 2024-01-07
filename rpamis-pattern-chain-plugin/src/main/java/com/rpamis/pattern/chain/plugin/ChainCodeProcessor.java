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

    protected static final String VERBOSE = "lombok.verbose";

    private JavacTrees trees;
    private TreeMaker maker;
    private Names names;
    private Messager messager;

    private Elements elements;
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
        Set<TypeElement> factoryClasses = new HashSet<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(ChainFactory.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                factoryClasses.add((TypeElement) element);
            }
        }
        Set<String> builderNameSet = new HashSet<>();
        Map<String, String> buidlerClassToPackageNameMap = new ConcurrentHashMap<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(ChainBuilder.class)) {
            if (element.getKind() == ElementKind.INTERFACE) {
                TypeElement builderTypeElement = (TypeElement) element;
                String builderClassName = builderTypeElement.getSimpleName().toString();
                builderNameSet.add(builderClassName);
                String builderPackageName = getPackageName(builderTypeElement);
                buidlerClassToPackageNameMap.put(builderClassName, builderPackageName);
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "@ChainBuilder can only be applied to interfaces", element);
            }
        }
        for (TypeElement factoryElement : factoryClasses) {
            if (verbose) {
                messager.printMessage(NOTE, "@ChainFactory, process class: " + factoryElement.getSimpleName(), factoryElement);
            }
            JCTree.JCClassDecl classDecl = trees.getTree(factoryElement);
            addImportInfo(trees.getPath(factoryElement), "com.rpamis.pattern.chain.builder", "ChainPipelineCache");
            handlerChainFactory(classDecl, builderNameSet);
        }
        return true;
    }

    private void addImportInfo(TreePath treePath, String packageName, String className) {
        JCTree.JCCompilationUnit jccu = (JCTree.JCCompilationUnit) treePath.getCompilationUnit();
        for (JCTree jcTree : jccu.getImports()) {
            if (jcTree != null && jcTree instanceof JCTree.JCImport) {
                JCTree.JCImport jcImport = (JCTree.JCImport) jcTree;
                if (jcImport.qualid != null && jcImport.qualid instanceof JCTree.JCFieldAccess) {
                    JCTree.JCFieldAccess jcFieldAccess = (JCTree.JCFieldAccess) jcImport.qualid;
                    try {
                        if (packageName.equals(jcFieldAccess.selected.toString()) && className.equals(jcFieldAccess.name.toString())) {
                            return;
                        }
                    } catch (NullPointerException e) {

                    }
                }
            }
        }
        java.util.List<JCTree> trees = new ArrayList<>();
        trees.addAll(jccu.defs);
        JCTree.JCIdent ident = maker.Ident(names.fromString(packageName));
        JCTree.JCImport jcImport = maker.Import(maker.Select(
                ident, names.fromString(className)), false);
        if (!trees.contains(jcImport)) {
            trees.add(0, jcImport);
        }
        jccu.defs = List.from(trees);
    }


    private void handlerChainFactory(JCTree.JCClassDecl classDecl, Set<String> builderNameSet) {
        List<JCTree> methodDecls = List.nil();
        int pos = classDecl.pos;
        for (String builderName : builderNameSet) {
            String builderMethodName = ProcessorSupport.getChainBuilderMethodName(builderName);
            if (!ProcessorSupport.methodExists(builderMethodName, classDecl)) {
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
        classDecl.defs = classDecl.defs.appendList(methodDecls);
        System.out.printf("=======================");
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
        // @see https://stackoverflow.com/questions/46874126/java-lang-assertionerror-thrown-by-compiler-when-adding-generated-method-with-pa
        params.forEach(param -> param.pos = pos);

        // 获取方法返回类型，这里假设返回类型为 builderName<T>
        JCTree.JCExpression returnType = maker.TypeApply(maker.Ident(names.fromString(builderName)), List.of(maker.Ident(names.fromString("T"))));

        JCTree.JCBlock methodBody = maker.Block(0, List.of(maker.Return(createGetParallelChainInvocation(
                maker.Ident(names.fromString("chainId")),
                maker.Ident(names.fromString("chainTypeReference")),
                builderMethodName
        ))));
        // 创建方法声明，将泛型参数和参数列表添加到方法中
        JCTree.JCMethodDecl methodDecl = maker.MethodDef(
                maker.Modifiers(Flags.PUBLIC | Flags.STATIC),
                names.fromString(builderMethodName),
                returnType,
                List.of(typeParam), // 将泛型参数添加到类型参数列表中
                params,             // 将参数列表添加到方法中
                List.nil(),
                methodBody,
                null
        );
        return methodDecl;
    }

    // 创建 ChainTypeReference<T> 类型
    private JCTree.JCExpression createChainTypeReferenceType() {
        return maker.TypeApply(maker.Ident(names.fromString("ChainTypeReference")), List.of(maker.Ident(names.fromString("T"))));
    }

    private JCTree.JCMethodInvocation createGetParallelChainInvocation(JCTree.JCExpression chainId, JCTree.JCExpression chainTypeReference, String builderMethodName) {
        JCTree.JCMethodInvocation methodInvocation = maker.Apply(
                List.nil(),
                maker.Select(maker.Ident(names.fromString("ChainPipelineCache")), names.fromString(builderMethodName)),
                List.of(chainId, chainTypeReference)
        );
        return methodInvocation;
    }

    private String getPackageName(TypeElement typeElement) {
        Element enclosingElement = typeElement.getEnclosingElement();
        while (enclosingElement.getKind() != ElementKind.PACKAGE) {
            enclosingElement = enclosingElement.getEnclosingElement();
        }
        return ((PackageElement) enclosingElement).getQualifiedName().toString();
    }
}
