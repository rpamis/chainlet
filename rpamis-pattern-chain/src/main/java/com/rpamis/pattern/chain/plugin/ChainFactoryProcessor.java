package com.rpamis.pattern.chain.plugin;

import com.rpamis.extension.spi.SpiLoader;
import com.rpamis.pattern.chain.builder.ChainPipelineBuilder;
import com.rpamis.pattern.chain.definition.ChainStrategy;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 责任链工厂代码生成
 *
 * @author benym
 * @date 2024/1/3 23:54
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.rpamis.pattern.chain.plugin.ChainFactory")
public class ChainFactoryProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(ChainFactory.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                generateCode((TypeElement) element);
            }
        }
        return true;
    }

    private void generateCode(TypeElement element) {
        ChainStrategy fastFailedStrategy = SpiLoader.getSpiLoader(ChainStrategy.class).getSpiImpl("fastFailedStrategy");
        System.out.printf("-----------123"+fastFailedStrategy.getClass().getName());
        // 获取元素的全限定名
        String packageName = processingEnv.getElementUtils().getPackageOf(element).toString();
        String className = element.getSimpleName().toString();
        String fullClassName = packageName + "." + className;

        // 生成新的类名
//        String newClassName = className + "Generated";
//
//        // 构建生成的类的内容
//        StringBuilder classContent = new StringBuilder();
//        classContent.append("package ").append(packageName).append(";\n\n");
//        classContent.append("public class ").append(newClassName);
//        classContent.append(" implements ").append(className).append(" {\n\n");
//        // 在这里生成需要实现的接口的方法，可以根据需要自定义
//        classContent.append("    // 实现接口中的方法...\n\n");
//        classContent.append("}\n");
//
//        // 通过Filer创建新的Java文件
//        try {
//            Filer filer = processingEnv.getFiler();
//            JavaFileObject fileObject = filer.createSourceFile(packageName + "." + newClassName, element);
//            try (Writer writer = fileObject.openWriter()) {
//                writer.write(classContent.toString());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
