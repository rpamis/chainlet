/*
 * Copyright 2023-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rpamis.chainlet.test.handler;

import com.rpamis.chainlet.core.context.ChainHandlerContext;
import com.rpamis.chainlet.core.definition.ChainHandler;
import com.rpamis.chainlet.core.context.LocalFallBackContext;
import com.rpamis.chainlet.core.fallback.Fallback;
import com.rpamis.chainlet.test.DemoUser;

import java.util.List;

/**
 * ListAuthHandler
 *
 * @author benym
 * @since 2023/8/22 13:47
 */
public class ListAuthHandler implements ChainHandler<List<DemoUser>> {
    @Override
    @Fallback(fallbackMethod = "fallback")
    public boolean process(List<DemoUser> demoUserList, ChainHandlerContext<List<DemoUser>> context) {
        if (!"admin".equals(demoUserList.get(0).getRole())) {
            System.out.println("auth failed");
            return false;
        }
        System.out.println("auth success");
        return true;
    }

    public void fallback(LocalFallBackContext<List<DemoUser>> localFallBackContext) {
        System.out.println("fallback");
    }
}
