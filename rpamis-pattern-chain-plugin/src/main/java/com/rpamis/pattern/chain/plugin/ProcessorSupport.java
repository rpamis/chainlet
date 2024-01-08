package com.rpamis.pattern.chain.plugin;

import com.sun.tools.javac.tree.JCTree;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * Processor支持类
 *
 * @author benym
 * @date 2024/1/7 19:53
 */
public class ProcessorSupport {

    public static String getNameForFactory(String builderName) {
        String name = preCheck(builderName);
        // 将 builderName 的首字母小写，然后拼接 "get"
        return "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    public static String getNameForDirector(String builderName) {
        String name = preCheck(builderName);
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    public static String preCheck(String builderName){

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
}
