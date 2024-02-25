package com.rpamis.chain.test.handler;

import com.rpamis.chain.core.context.ChainHandlerContext;
import com.rpamis.chain.core.definition.ChainHandler;
import com.rpamis.chain.test.DemoUser;

/**
 * Handler异常模拟类
 *
 * @author benym
 * @date 2023/7/11 16:32
 */
public class MockExceptionHandler implements ChainHandler<DemoUser> {

    @Override
    public boolean process(DemoUser handlerData, ChainHandlerContext<DemoUser> context) {
        int i = 1 / 0;
        return false;
    }
}
