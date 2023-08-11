package com.rpamis.pattern.chain.handler;

import com.rpamis.pattern.chain.DemoUser;
import com.rpamis.pattern.chain.interfaces.ChainHandler;

/**
 * Handler异常模拟类
 *
 * @author benym
 * @date 2023/7/11 16:32
 */
public class MockExceptionHandler implements ChainHandler<DemoUser> {

    @Override
    public boolean process(DemoUser handlerData) {
        int i = 1 / 0;
        return false;
    }
}
