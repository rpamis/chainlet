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
import com.rpamis.chainlet.test.DemoUser;

/**
 * ValidateHandler
 *
 * @author benym
 * @since 2023/5/25 15:49
 */
public class ValidateHandler implements ChainHandler<DemoUser> {
    @Override
    public boolean process(DemoUser demoUser, ChainHandlerContext<DemoUser> context) {
        if (demoUser.getName() == null || demoUser.getPwd() == null) {
            System.out.println("validate failed");
            return false;
        }
        System.out.println("validate success");
        return true;
    }
}
