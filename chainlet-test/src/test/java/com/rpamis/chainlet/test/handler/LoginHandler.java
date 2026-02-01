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

/**
 * LoginHandler
 *
 * @author benym
 * @since 2023/5/25 18:15
 */
public class LoginHandler implements ChainHandler<DemoUser> {

    @Override
    @Fallback(fallbackMethod = "testFallback")
    public boolean process(DemoUser demoUser, ChainHandlerContext<DemoUser> context) {
        if ("test".equals(demoUser.getName()) && "123".equals(demoUser.getPwd())) {
            System.out.println("login success");
            return true;
        } else {
            throw new RuntimeException("2131");
        }
    }

    public void testFallback(LocalFallBackContext<DemoUser> localFallBackContext) {
        System.out.println(1);
    }
}
