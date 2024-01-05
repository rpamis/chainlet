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
import java.util.Set;

/**
 * 责任链工厂代码生成
 *
 * @author benym
 * @date 2024/1/3 23:54
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(value = {
        "com.rpamis.pattern.chain.plugin.ChainFactory",
        "com.rpamis.pattern.chain.plugin.ChainBuilder",
        "com.rpamis.pattern.chain.plugin.ChainCache",
        "com.rpamis.pattern.chain.plugin.ChainDirector"
})
public class ChainCodeProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.printf("开始了=================");
        Set<TypeElement> factoryClasses = new HashSet<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(ChainFactory.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                factoryClasses.add((TypeElement) element);
            }
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(ChainBuilder.class)) {
            if (element.getKind() == ElementKind.INTERFACE) {
                TypeElement builderTypeElement = (TypeElement) element;
                checkAndGenerateCode(factoryClasses, builderTypeElement);
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "@ChainBuilder can only be applied to interfaces", element);
            }
        }
        return true;
    }

    private void checkAndGenerateCode(Set<TypeElement> factoryClasses, TypeElement builderTypeElement) {
        String builderClassName = builderTypeElement.getSimpleName().toString();
        String methodName = "get" + builderClassName;
        for (TypeElement factoryClass : factoryClasses) {
            if (doesMethodExist(factoryClass, methodName)) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                        methodName + " already exists in " + factoryClass.getSimpleName());
                continue;
            }

            String builderPackageName = getPackageName(builderTypeElement);

            String generatedCode = String.format(
                    "public static <%s> %s<%s> %s(String chainId, ChainTypeReference<%s> chainTypeReference) {\n" +
                            "    return %s.getChain(chainId, chainTypeReference);\n" +
                            "}\n",
                    builderClassName, builderClassName, builderClassName, methodName, builderClassName,
                    factoryClass.getSimpleName());
            // Print the generated code for demonstration (you should write it to a file in practice)
            appendCodeToFactoryClass(builderPackageName, factoryClass.getSimpleName().toString(), generatedCode);
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

    private void appendCodeToFactoryClass(String builderPackageName, String factoryClassName, String generatedCode) {
        try {
            // Get the source file for the existing class
            JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(builderPackageName + "." + factoryClassName);

            // Check if the file already exists
            if (fileObject.getLastModified() > 0) {
                // File already exists, open it for appending
                try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
                    writer.print(generatedCode);
                }
            } else {
                // File doesn't exist, write the full content
                try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
                    writer.print(generatedCode);
                }
            }

        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "Failed to append code to ChainPipelineFactory: " + e.getMessage());
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
