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

package io.github.ma1uta.matrix.exception;

import io.github.ma1uta.matrix.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Common exception handler.
 */
public class ExceptionHandler implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public Response toResponse(Throwable exception) {

        LOGGER.error("Exception:", exception);

        ErrorResponse message;
        Integer status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        if (exception instanceof MatrixException) {
            MatrixException matrixException = (MatrixException) exception;
            message = new ErrorResponse(matrixException.getErrcode(), matrixException.getMessage(), matrixException.getRetryAfterMs());
            status = matrixException.getStatus();
        } else if (exception instanceof WebApplicationException) {
            WebApplicationException webApplicationException = (WebApplicationException) exception;
            message = new ErrorResponse(Integer.toString(webApplicationException.getResponse().getStatus()), exception.getMessage());
        } else {
            message = new ErrorResponse(MatrixException.M_INTERNAL, exception.getMessage());
        }

        return Response.status(status).entity(message).build();
    }
}
