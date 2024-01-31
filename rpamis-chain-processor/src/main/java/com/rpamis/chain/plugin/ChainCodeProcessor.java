package com.rpamis.chain.plugin;

import com.google.auto.service.AutoService;
import com.rpamis.chain.plugin.annotations.*;
import com.rpamis.chain.plugin.template.ChainCacheGenTemplate;
import com.rpamis.chain.plugin.template.ChainDirectorGenTemplate;
import com.rpamis.chain.plugin.template.ChainDirectorServiceGenTemplate;
import com.rpamis.chain.plugin.template.ChainFactoryGenTemplate;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static com.rpamis.chain.plugin.ChainCodeProcessor.VERBOSE;

/**
 * 责任链工厂代码生成
 *
 * @author benym
 * @date 2024/1/3 23:54
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(value = {
        "com.rpamis.chain.plugin.annotations.ChainDirector",
        "com.rpamis.chain.plugin.annotations.ChainDirectorService",
        "com.rpamis.chain.plugin.annotations.ChainBuilder",
        "com.rpamis.chain.plugin.annotations.ChainBuilderService",
        "com.rpamis.chain.plugin.annotations.ChainCache",
        "com.rpamis.chain.plugin.annotations.ChainFactory"
})
@SupportedOptions({VERBOSE})
public class ChainCodeProcessor extends AbstractProcessor {

    protected static final String VERBOSE = "chain.verbose";

    /**
     * Javac树解析工具
     */
    private static JavacTrees trees;
    /**
     * TreeMaker实例，用于创建语法树
     */
    private static TreeMaker maker;
    /**
     * 名称解析工具
     */
    private static Names names;
    /**
     * 日志工具
     */
    private static Messager messager;

    /**
     * 元素解析器
     */
    private static Elements elements;

    /**
     * 类型解析器
     */
    private static Types typeUtils;
    /**
     * 打印详细信息参数
     */
    private static boolean verbose;

    /**
     * 生成上下文
     */
    private GenContext genContext;

    /**
     * 处理器上下文
     */
    private ProcessorContext processorContext;

    private static final String PACKAGE_NAME = "com.rpamis.chain.core.builder";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        trees = JavacTrees.instance(context);
        maker = TreeMaker.instance(context);
        names = Names.instance(context);
        messager = processingEnv.getMessager();
        elements = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        verbose = Boolean.parseBoolean(processingEnv.getOptions().get(VERBOSE));
        this.processorContext = ProcessorContext.ProcessorContextBuilder.aProcessorContext()
                .withTrees(trees)
                .withMaker(maker)
                .withNames(names)
                .withMessager(messager)
                .withElements(elements)
                .withVerbose(verbose).build();
    }

    private GenContext initContext(GenContext genContext, ProcessorContext processorContext) {
        RoundEnvironment roundEnv = processorContext.getRoundEnv();
        Messager messager = processorContext.getMessager();
        // 获取内部所有ChainDirector类
        Set<TypeElement> chainDirectorClasses = genContext.getChainDirectorClasses();
        for (Element element : roundEnv.getElementsAnnotatedWith(ChainDirector.class)) {
            Element enclosingElement = element.getEnclosingElement();
            if (element.getKind() == ElementKind.INTERFACE && enclosingElement instanceof PackageElement) {
                String packageName = ((PackageElement) enclosingElement).getQualifiedName().toString();
                if (PACKAGE_NAME.equals(packageName)) {
                    chainDirectorClasses.add((TypeElement) element);
                }
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "@ChainDirector can only be applied to interface", element);
            }
        }
        // 获取内部所有ChainCache类
        Set<TypeElement> chainCacheClasses = genContext.getChainCacheClasses();
        for (Element element : roundEnv.getElementsAnnotatedWith(ChainCache.class)) {
            Element enclosingElement = element.getEnclosingElement();
            if (element.getKind() == ElementKind.CLASS && enclosingElement instanceof PackageElement) {
                String packageName = ((PackageElement) enclosingElement).getQualifiedName().toString();
                if (PACKAGE_NAME.equals(packageName)) {
                    chainCacheClasses.add((TypeElement) element);
                }
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "@ChainCache can only be applied to interface", element);
            }
        }
        // 获取内部所有ChainDirectorService类
        Set<TypeElement> chainDirectorServiceClasses = genContext.getChainDirectorServiceClasses();
        for (Element element : roundEnv.getElementsAnnotatedWith(ChainDirectorService.class)) {
            Element enclosingElement = element.getEnclosingElement();
            if (element.getKind() == ElementKind.CLASS && enclosingElement instanceof PackageElement) {
                String packageName = ((PackageElement) enclosingElement).getQualifiedName().toString();
                if (PACKAGE_NAME.equals(packageName)) {
                    chainDirectorServiceClasses.add((TypeElement) element);
                }
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "@ChainDirectorService can only be applied to class", element);
            }
        }
        // 获取内部所有ChainFactory类
        Set<TypeElement> factoryClasses = genContext.getFactoryClasses();
        for (Element element : roundEnv.getElementsAnnotatedWith(ChainFactory.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                Element enclosingElement = element.getEnclosingElement();
                if (element.getKind() == ElementKind.CLASS && enclosingElement instanceof PackageElement) {
                    String packageName = ((PackageElement) enclosingElement).getQualifiedName().toString();
                    if (PACKAGE_NAME.equals(packageName)) {
                        factoryClasses.add((TypeElement) element);
                    }
                }
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "@ChainFactory can only be applied to class", element);
            }
        }
        // 获取所有ChainBuilder类
        Set<String> builderNameSet = genContext.getBuilderNameSet();
        Map<String, String> buidlerClassToPackageNameMap = genContext.getBuidlerClassToPackageNameMap();
        for (Element element : roundEnv.getElementsAnnotatedWith(ChainBuilder.class)) {
            if (element.getKind() == ElementKind.INTERFACE) {
                TypeElement builderTypeElement = (TypeElement) element;
                String builderClassName = builderTypeElement.getSimpleName().toString();
                builderNameSet.add(builderClassName);
                String builderPackageName = ChainCodeProcessor.getPackageName(builderTypeElement);
                buidlerClassToPackageNameMap.put(builderClassName, builderPackageName);
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "@ChainBuilder can only be applied to interfaces", element);
            }
        }
        // 获取所有ChainBuilderService类
        Set<String> builderServiceNameSet = genContext.getBuilderServiceNameSet();
        Map<String, String> builderNameToServiceMap = genContext.getBuilderNameToServiceMap();
        Map<String, String> builderServiceToPackageNameMap = genContext.getBuilderServiceToPackageNameMap();
        for (Element element : roundEnv.getElementsAnnotatedWith(ChainBuilderService.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement builderServiceTypeElement = (TypeElement) element;
                String builderServiceClassName = builderServiceTypeElement.getSimpleName().toString();
                // 获取ChainBuilderService实现的ChainBuilder接口的类名
                String interfaceName = getInterfaceName(builderServiceTypeElement);
                String builderServicePackageName = ChainCodeProcessor.getPackageName(builderServiceTypeElement);
                if (interfaceName == null) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            "@ChainBuilderService can only be applied to class implements ChainBuilder", element);
                } else if (builderNameToServiceMap.containsKey(interfaceName)) {
                    // 如果已经有另外的Service实现了接口，那么就提示每一个ChainBuilder只能有一个实现类
                    String exitServiceName = builderNameToServiceMap.get(interfaceName);
                    String message = String.format("Each @ChainBuilder can only have one implementation class, interface %s, " +
                            "has an implementation class %s already exists.", interfaceName, exitServiceName);
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            message, element);
                } else {
                    builderNameToServiceMap.put(interfaceName, builderServiceClassName);
                    builderServiceNameSet.add(builderServiceClassName);
                    builderServiceToPackageNameMap.put(builderServiceClassName, builderServicePackageName);
                }
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "@ChainBuilderService can only be applied to class", element);
            }
        }
        return genContext;
    }

    /**
     * 获取ChainBuilderService实现的ChainBuilder接口的类名
     *
     * @param builderServiceTypeElement builderServiceTypeElement
     * @return String
     */
    private String getInterfaceName(TypeElement builderServiceTypeElement) {
        for (TypeMirror interfaceType : builderServiceTypeElement.getInterfaces()) {
            Element interfaceElement = typeUtils.asElement(interfaceType);
            if (interfaceElement.getAnnotation(ChainBuilder.class) != null) {
                return interfaceElement.getSimpleName().toString();
            }
        }
        return null;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty() || roundEnv.processingOver()) {
            return true;
        }
        this.processorContext.setRoundEnv(roundEnv);
        this.genContext = initContext(new GenContext(), processorContext);
        Set<String> builderNameSet = genContext.getBuilderNameSet();
        if (builderNameSet.isEmpty()) {
            return true;
        }
        ChainDirectorGenTemplate directorGenTemplate = new ChainDirectorGenTemplate();
        ChainDirectorServiceGenTemplate directorServiceGenTemplate = new ChainDirectorServiceGenTemplate();
        ChainCacheGenTemplate cacheGenTemplate = new ChainCacheGenTemplate();
        ChainFactoryGenTemplate factoryGenTemplate = new ChainFactoryGenTemplate();
        directorGenTemplate.execute(genContext, processorContext);
        cacheGenTemplate.execute(genContext, processorContext);
        directorServiceGenTemplate.execute(genContext, processorContext);
        factoryGenTemplate.execute(genContext, processorContext);
        return true;
    }

    /**
     * 导入需要的类
     *
     * @param treePath    树路径
     * @param packageName 包名
     * @param className   类名
     */
    public static void importNeedClass(TreePath treePath, String packageName, String className) {
        if (packageName == null || className == null) {
            return;
        }
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
     * 获取ChainFactory中需要生成的Builder方法名
     *
     * @param builderName builderName
     * @return String
     */
    public static String getNameForFactory(String builderName) {
        String name = preCheck(builderName);
        // 将 builderName 的首字母小写，然后拼接 "get"
        return "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * 获取ChainDirectorService中需要生成的Builder方法名
     *
     * @param builderName builderName
     * @return String
     */
    public static String getNameForDirector(String builderName) {
        String name = preCheck(builderName);
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * 获取ChainDirectorService中缓存Builder的方法名
     *
     * @param builderMethodName builderMethodName
     * @return String
     */
    public static String getNameForDirectorServiceCache(String builderMethodName) {
        return "register" + Character.toUpperCase(builderMethodName.charAt(0)) + builderMethodName.substring(1);
    }

    /**
     * builder合法性检查
     *
     * @param builderName builderName
     * @return String
     */
    public static String preCheck(String builderName) {
        // 假设所有的 Builder 类名都以 "Builder" 结尾
        String builderSuffix = "Builder";
        // 如果 builderName 以 "Builder" 结尾，去掉这个后缀
        if (builderName.endsWith(builderSuffix)) {
            builderName = builderName.substring(0, builderName.length() - builderSuffix.length());
        }
        // 如果 builderName 以 "Pipeline" 结尾，去掉这个后缀
        String pipelineSuffix = "Pipeline";
        if (builderName.endsWith(pipelineSuffix)) {
            builderName = builderName.substring(0, builderName.length() - pipelineSuffix.length());
        }
        if ("SerialChain".equals(builderName)) {
            builderName = "Chain";
        }
        return builderName;
    }

    /**
     * 获取包名
     *
     * @param typeElement 类
     * @return 包名
     */
    public static String getPackageName(TypeElement typeElement) {
        Element enclosingElement = typeElement.getEnclosingElement();
        while (enclosingElement.getKind() != ElementKind.PACKAGE) {
            enclosingElement = enclosingElement.getEnclosingElement();
        }
        return ((PackageElement) enclosingElement).getQualifiedName().toString();
    }

    /**
     * 判断方法是否存在
     *
     * @param methodName 方法名
     * @param classDecl  类声明
     * @return 是否存在
     */
    public static boolean methodExists(String methodName, JCTree.JCClassDecl classDecl) {
        for (JCTree def : classDecl.defs) {
            if (def instanceof JCTree.JCMethodDecl) {
                if (((JCTree.JCMethodDecl) def).name.contentEquals(methodName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断类中是否定义了名为variableName的变量
     *
     * @param classDecl 类声明
     * @return 是否存在
     */
    public static boolean variableExists(String variableName, JCTree.JCClassDecl classDecl) {
        for (JCTree def : classDecl.defs) {
            if (def instanceof JCTree.JCVariableDecl) {
                if (((JCTree.JCVariableDecl) def).name.contentEquals(variableName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
