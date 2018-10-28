/*
 * Copyright sablintolya@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.ma1uta.matrix.impl.exception;

/**
 * {@code RateLimitedException} indicates that server answered with the 427 status code.
 */
public class RateLimitedException extends MatrixException {

    private Long retryAfterMs;

    public RateLimitedException(String errcode, String error, Long retryAfterMs) {
        super(errcode, error);
        this.retryAfterMs = retryAfterMs;
    }

    public RateLimitedException(String errcode, String error, Integer status, Long retryAfterMs) {
        super(errcode, error, status);
        this.retryAfterMs = retryAfterMs;
    }

    public Long getRetryAfterMs() {
        return retryAfterMs;
    }

    public void setRetryAfterMs(Long retryAfterMs) {
        this.retryAfterMs = retryAfterMs;
    }
}
