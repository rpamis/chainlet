package com.rpamis.pattern.chain.plugin;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;

/**
 * Processor上下文
 *
 * @author benym
 * @date 2024/1/9 16:28
 */
public class ProcessorContext {

    /**
     * 环境
     */
    private RoundEnvironment roundEnv;

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

    public JavacTrees getTrees() {
        return trees;
    }

    public void setTrees(JavacTrees trees) {
        this.trees = trees;
    }

    public TreeMaker getMaker() {
        return maker;
    }

    public void setMaker(TreeMaker maker) {
        this.maker = maker;
    }

    public Names getNames() {
        return names;
    }

    public void setNames(Names names) {
        this.names = names;
    }

    public Messager getMessager() {
        return messager;
    }

    public void setMessager(Messager messager) {
        this.messager = messager;
    }

    public Elements getElements() {
        return elements;
    }

    public void setElements(Elements elements) {
        this.elements = elements;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public RoundEnvironment getRoundEnv() {
        return roundEnv;
    }

    public void setRoundEnv(RoundEnvironment roundEnv) {
        this.roundEnv = roundEnv;
    }

    public static final class ProcessorContextBuilder {
        private final ProcessorContext processorContext;

        private ProcessorContextBuilder() {
            processorContext = new ProcessorContext();
        }

        public static ProcessorContextBuilder aProcessorContext() {
            return new ProcessorContextBuilder();
        }

        public ProcessorContextBuilder withTrees(JavacTrees trees) {
            processorContext.setTrees(trees);
            return this;
        }

        public ProcessorContextBuilder withMaker(TreeMaker maker) {
            processorContext.setMaker(maker);
            return this;
        }

        public ProcessorContextBuilder withNames(Names names) {
            processorContext.setNames(names);
            return this;
        }

        public ProcessorContextBuilder withMessager(Messager messager) {
            processorContext.setMessager(messager);
            return this;
        }

        public ProcessorContextBuilder withElements(Elements elements) {
            processorContext.setElements(elements);
            return this;
        }

        public ProcessorContextBuilder withVerbose(boolean verbose) {
            processorContext.setVerbose(verbose);
            return this;
        }

        public ProcessorContext build() {
            return processorContext;
        }
    }
}
