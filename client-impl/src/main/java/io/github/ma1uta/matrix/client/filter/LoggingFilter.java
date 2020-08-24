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

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.MediaType;

/**
 * Logging client filter.
 */
public class LoggingFilter implements ClientRequestFilter, ClientResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    /**
     * Bearer prefix.
     */
    public static final String BEARER_PREFIX = "bearer ";

    /**
     * Bearer prefix length.
     */
    public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (!LOGGER.isDebugEnabled()) {
            return;
        }
        StringBuilder builder = new StringBuilder("\n------------- Request -------------\n");
        builder.append("  Method: ").append(requestContext.getMethod()).append("\n");
        builder.append("  URI: ").append(requestContext.getUri()).append("\n");
        builder.append("  Headers:\n");
        for (Map.Entry<String, List<Object>> header : requestContext.getHeaders().entrySet()) {
            builder.append("    ").append(header.getKey()).append(": ");
            for (Object headerValue : header.getValue()) {
                builder.append(maskValue(header.getKey(), headerValue.toString()));
            }
            builder.append("\n");
        }
        if (MediaType.APPLICATION_JSON_TYPE.equals(requestContext.getMediaType())) {
            requestContext.setEntityStream(new LoggingStream(requestContext.getEntityStream()));
        }
        builder.append("------------- End Request -------------\n");
        LOGGER.debug(builder.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        if (!LOGGER.isDebugEnabled()) {
            return;
        }
        StringBuilder builder = new StringBuilder("\n---------- Response ----------\n");
        builder.append("  Status: ").append(responseContext.getStatus()).append("\n");
        builder.append("  Headers:\n");
        for (Map.Entry<String, List<String>> header : responseContext.getHeaders().entrySet()) {
            builder.append("    ").append(header.getKey()).append(": ");
            for (Object headerValue : header.getValue()) {
                builder.append(headerValue.toString()).append(",");
            }
            builder.append("\n");
        }
        builder.append("------------- End Response -------------\n");
        LOGGER.debug(builder.toString());
        if (requestContext.getUri().toString().contains("/_matrix/media/r0")) {
            return;
        }
        if (MediaType.APPLICATION_JSON_TYPE.equals(responseContext.getMediaType())) {
            byte[] content = StreamHelper.toByteArray(responseContext.getEntityStream());
            LOGGER.debug("Body:\n" + new String(content, StandardCharsets.UTF_8));
            responseContext.setEntityStream(new ByteArrayInputStream(content));
        }
    }

    private String maskValue(String name, String value) {
        if (AUTHORIZATION.equalsIgnoreCase(name) && value.toLowerCase().startsWith(BEARER_PREFIX)) {
            return value.substring(0, BEARER_PREFIX_LENGTH) + "***";
        }
        return value;
    }

    static class LoggingStream extends FilterOutputStream {

        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        LoggingStream(OutputStream inner) {
            super(inner);
        }

        @Override
        public void write(final int i) throws IOException {
            baos.write(i);
            out.write(i);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            super.write(b, off, len);
            LOGGER.debug("Body:\n" + baos.toString(StandardCharsets.UTF_8.name()));
        }
    }
}
