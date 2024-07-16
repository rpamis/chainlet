package com.rpamis.chainlet.plugin.factory;

import com.rpamis.chainlet.plugin.GenContext;
import com.rpamis.chainlet.plugin.ProcessorContext;
import com.rpamis.chainlet.plugin.template.GenCodeTemplate;

/**
 * 生成代码模版工厂
 *
 * @author benym
 * @date 2024/2/23 15:03
 */
public class GenCodeTemplateFactory extends GenCodeInitializer {

    /**
     * 依据模版生成代码
     *
     * @param genContext       genContext
     * @param processorContext processorContext
     */
    public void genCodeWithTemplate(GenContext genContext, ProcessorContext processorContext) {
        for (GenCodeTemplate genCodeTemplate : genCodeTemplates) {
            genCodeTemplate.execute(genContext, processorContext);
        }
    }
}
