package com.rpamis.pattern.chain.handler;

import com.rpamis.pattern.chain.AbstractChainHandler;
import com.rpamis.pattern.chain.DemoUser;

/**
 * Handler异常模拟类
 *
 * @author benym
 * @date 2023/7/11 16:32
 */
public class MockExceptionHandler extends AbstractChainHandler<DemoUser> {

    @Override
    protected boolean process(DemoUser handlerData) {
        int i = 1 / 0;
        return false;
    }
}
