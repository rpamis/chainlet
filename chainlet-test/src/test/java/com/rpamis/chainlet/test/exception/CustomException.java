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
package com.rpamis.chainlet.test.exception;

/**
 * 自定义异常
 *
 * @author benym
 * @since 2024/7/12 16:23
 */
public class CustomException extends RuntimeException {
    private static final long serialVersionUID = 6936050760890395523L;

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}
