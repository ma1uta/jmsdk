/*
 * Copyright Anatoliy Sablin tolya@sablin.xyz
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

package io.github.ma1uta.matrix.client.filter;

import io.github.ma1uta.matrix.ErrorResponse;
import io.github.ma1uta.matrix.RateLimitedErrorResponse;
import io.github.ma1uta.matrix.UserInteractiveResponse;
import io.github.ma1uta.matrix.impl.Deserializer;
import io.github.ma1uta.matrix.impl.exception.MatrixException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.ServiceLoader;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

/**
 * Error filter.
 */
public class ErrorFilter implements ClientResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorFilter.class);

    private volatile Deserializer deserializer;

    /**
     * Rate limit response status.
     */
    public static final int RATE_LIMIT_RESPONSE_STATUS = 429;

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        int status = responseContext.getStatus();
        LOGGER.trace("Response status: {}", status);
        switch (status) {
            case HttpURLConnection.HTTP_OK:
                LOGGER.trace("OK.");
                break;
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                throwUserInteractiveException(responseContext);
                break;
            case RATE_LIMIT_RESPONSE_STATUS:
                throwRateLimitException(responseContext);
                return;
            default:
                throwException(responseContext, status);
        }
    }

    private void throwUserInteractiveException(ClientResponseContext responseContext) throws IOException {
        byte[] response = StreamHelper.toByteArray(responseContext.getEntityStream());
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.error("Response: {}", new String(response, StandardCharsets.UTF_8));
            }
            UserInteractiveResponse userInteractiveResponse = getDeserializer().deserialize(response, UserInteractiveResponse.class);
            LOGGER.error("User-interactive response, session: {}", userInteractiveResponse.getSession());
            throw new MatrixException(String.format("User-interactive response \"%s\"", userInteractiveResponse.getSession()),
                userInteractiveResponse, HttpsURLConnection.HTTP_UNAUTHORIZED);
        } catch (Exception e) {
            LOGGER.error("Response: {}", new String(response, StandardCharsets.UTF_8));
            LOGGER.error("Unable to invoke request", e);
            throw (RuntimeException) (e instanceof MatrixException ? e : new RuntimeException(e));
        }
    }

    private void throwRateLimitException(ClientResponseContext responseContext) throws IOException {
        byte[] response = StreamHelper.toByteArray(responseContext.getEntityStream());
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.error("Response: {}", new String(response, StandardCharsets.UTF_8));
            }
            RateLimitedErrorResponse rateLimitedResponse = getDeserializer().deserialize(response, RateLimitedErrorResponse.class);
            LOGGER.error("Rate limited response, error code: '{}', error: '{}', retry after {} milliseconds",
                rateLimitedResponse.getErrcode(), rateLimitedResponse.getError(), rateLimitedResponse.getRetryAfterMs());
            throw new MatrixException(rateLimitedResponse.getError(), rateLimitedResponse, RATE_LIMIT_RESPONSE_STATUS);
        } catch (Exception e) {
            LOGGER.error("Response: {}", new String(response, StandardCharsets.UTF_8));
            LOGGER.error("Unable to invoke request", e);
            throw (RuntimeException) (e instanceof MatrixException ? e : new RuntimeException(e));
        }
    }

    private void throwException(ClientResponseContext responseContext, int status) throws IOException {
        if (0 == responseContext.getLength()) {
            String message = String.format("Response status: %s, empty content", status);
            LOGGER.error(message);
            throw new RuntimeException(message);
        }
        byte[] response = StreamHelper.toByteArray(responseContext.getEntityStream());
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.error("Response: {}", new String(response, StandardCharsets.UTF_8));
            }
            ErrorResponse errorResponse = getDeserializer().deserialize(response, ErrorResponse.class);
            LOGGER.error("Error response, error code: '{}', error: '{}'", errorResponse.getErrcode(), errorResponse.getError());
            throw new MatrixException(errorResponse.getError(), errorResponse, status);
        } catch (Exception e) {
            LOGGER.error("Response: {}", new String(response, StandardCharsets.UTF_8));
            LOGGER.error("Unable to invoke request", e);
            throw (RuntimeException) (e instanceof MatrixException ? e : new RuntimeException(e));
        }
    }

    private Deserializer getDeserializer() {
        if (deserializer == null) {
            synchronized (this) {
                if (deserializer == null) {
                    Iterator<Deserializer> iterator = ServiceLoader.load(Deserializer.class).iterator();
                    if (iterator.hasNext()) {
                        deserializer = iterator.next();
                    } else {
                        throw new IllegalStateException(
                            "Unable to found the object serializer. Check jackson-support or jsonb-support packages included.");
                    }
                }
            }
        }
        return deserializer;
    }
}
