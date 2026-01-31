package com.rpamis.chainlet.test.handler;

import com.rpamis.chainlet.core.context.ChainHandlerContext;
import com.rpamis.chainlet.core.definition.ChainHandler;
import com.rpamis.chainlet.test.DemoUser;

/**
 * Handler异常模拟类
 *
 * @author benym
 * @since 2023/7/11 16:32
 */
public class MockExceptionHandler implements ChainHandler<DemoUser> {

    @Override
    public boolean process(DemoUser handlerData, ChainHandlerContext<DemoUser> context) {
        int i = 1 / 0;
        return false;
    }
}
