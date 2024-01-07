package com.rpamis.pattern.chain.plugin;

import com.sun.tools.javac.tree.JCTree;

/**
 * Processor支持类
 *
 * @author benym
 * @date 2024/1/7 19:53
 */
public class ProcessorSupport {

    public static String getChainBuilderMethodName(String builderName) {
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
            return "getChain";
        }

        // 将 builderName 的首字母小写，然后拼接 "get"
        return "get" + Character.toUpperCase(builderName.charAt(0)) + builderName.substring(1);
    }

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
