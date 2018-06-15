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

package io.github.ma1uta.jeon.exception;

import java.net.HttpURLConnection;

/**
 * Common matrix implementation exception.
 */
public class MatrixException extends RuntimeException {

    /**
     * Error code for all other unknown exceptions.
     */
    public static final String M_INTERNAL = "M_INTERNAL";

    /**
     * Error code.
     */
    private String errcode;

    /**
     * Timeout to retry send a request.
     */
    private Long retryAfterMs;

    /**
     * Response status.
     */
    private Integer status = HttpURLConnection.HTTP_INTERNAL_ERROR;

    public MatrixException(String errcode, String error) {
        super(error);
        this.errcode = errcode;
    }

    public MatrixException(String errcode, String error, Integer status) {
        this(errcode, error);
        this.status = status;
    }

    public MatrixException(String errcode, String error, Long retryAfterMs) {
        this(errcode, error);
        this.retryAfterMs = retryAfterMs;
    }

    public MatrixException(String errcode, String error, Long retryAfterMs, Integer status) {
        this(errcode, error);
        this.retryAfterMs = retryAfterMs;
        this.status = status;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public Long getRetryAfterMs() {
        return retryAfterMs;
    }

    public void setRetryAfterMs(Long retryAfterMs) {
        this.retryAfterMs = retryAfterMs;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
