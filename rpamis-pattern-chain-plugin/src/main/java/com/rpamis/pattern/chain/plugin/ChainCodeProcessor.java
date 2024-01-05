package com.rpamis.pattern.chain.plugin;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 责任链工厂代码生成
 *
 * @author benym
 * @date 2024/1/3 23:54
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(value = {
        "com.rpamis.pattern.chain.plugin.ChainFactory"
})
public class ChainCodeProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return true;
        }
        System.out.printf("开始了=================, 当前参数是"+ annotations +"另外："+roundEnv.toString());
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
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "@ChainBuilder can only be applied to interfaces", element);
            }
        }
        System.out.printf("当前builder"+builderNameSet.toString());
        checkAndGenerateCode(factoryClasses, builderNameSet, buidlerClassToPackageNameMap);
        return true;
    }

    private void checkAndGenerateCode(Set<TypeElement> factoryClasses, Set<String> builderNameSet, Map<String, String> buidlerClassToPackageNameMap) {
        try {
            JavaFileObject fileObject = processingEnv.getFiler().createSourceFile("com.rpamis.pattern.chain.builder.ChainPipelineFactory");
            try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
                for (TypeElement factoryClass : factoryClasses) {
                    for (String builderClassName : builderNameSet) {
                        String methodName = "get" + builderClassName;
                        if (doesMethodExist(factoryClass, methodName)) {
                            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                                    methodName + " already exists in " + factoryClass.getSimpleName());
                            continue;
                        }
                        String generatedCode = String.format(
                                "import com.rpamis.pattern.chain.generic.ChainTypeReference;\n" +
                                        "import com.rpamis.pattern.chain.builder.SerialChainPipelineBuilder;\n" +
                                        "import com.rpamis.pattern.chain.builder.ChainPipelineCache;\n\n" +
                                        "public class ChainPipelineFactory {\n" +
                                        "public static <T> %s<T> %s(String chainId, ChainTypeReference<T> chainTypeReference) {\n" +
                                        "    return ChainPipelineCache.getChain(chainId, chainTypeReference);\n" +
                                        "}\n" +
                                        "}",
                                builderClassName, builderClassName, builderClassName, methodName, builderClassName,
                                factoryClass.getSimpleName());
                        // Print the generated code for demonstration (you should write it to a file in practice)
                        appendCodeToFactoryClass(fileObject, generatedCode, writer);

                    }
                }
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "Failed to append code to ChainPipelineFactory: " + e.getMessage());
        }
    }

    private boolean doesMethodExist(TypeElement factoryClass, String methodName) {
        for (Element enclosedElement : factoryClass.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.METHOD
                    && enclosedElement.getSimpleName().contentEquals(methodName)) {
                return true;
            }
        }
        return false;
    }

    private String getPackageName(TypeElement typeElement) {
        Element enclosingElement = typeElement.getEnclosingElement();
        while (enclosingElement.getKind() != ElementKind.PACKAGE) {
            enclosingElement = enclosingElement.getEnclosingElement();
        }
        return ((PackageElement) enclosingElement).getQualifiedName().toString();
    }

    private void appendCodeToFactoryClass(JavaFileObject fileObject, String generatedCode, PrintWriter writer) throws IOException {
        // Check if the file already exists
        if (fileObject.getLastModified() > 0) {
            // File already exists, open it for appending
            writer.print(generatedCode);
        } else {
            // File doesn't exist, write the full content
            writer.print(generatedCode);
        }
    }

    private String readExistingContent(TypeElement factoryClass) {
        String factoryClassName = factoryClass.getSimpleName().toString();
        String factoryPackageName = getPackageName(factoryClass);
        String fullClassName = factoryPackageName + "." + factoryClassName;

        try {
            // Read the existing content of ChainPipelineFactory
            return processingEnv.getFiler().getResource(StandardLocation.SOURCE_OUTPUT,
                    factoryPackageName, factoryClassName + ".java").getCharContent(true).toString();
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "Failed to read existing content of " + fullClassName + ": " + e.getMessage());
            return "";
        }
    }
}
