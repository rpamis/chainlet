package com.rpamis.chain.plugin.template;

import com.rpamis.chain.plugin.GenContext;
import com.rpamis.chain.plugin.ProcessorContext;

/**
 * 生产代码模版接口
 *
 * @author benym
 * @date 2024/2/23 14:31
 */
public interface GenCodeTemplate {

    /**
     * 执行模版
     *
     * @param genContext       生成上下文
     * @param processorContext 处理上下文
     */
    void execute(GenContext genContext, ProcessorContext processorContext);
}
