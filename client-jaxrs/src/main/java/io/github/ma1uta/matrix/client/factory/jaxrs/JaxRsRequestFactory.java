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

package io.github.ma1uta.matrix.client.factory.jaxrs;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.ErrorResponse;
import io.github.ma1uta.matrix.RateLimitedErrorResponse;
import io.github.ma1uta.matrix.Secured;
import io.github.ma1uta.matrix.client.AuthenticationRequred;
import io.github.ma1uta.matrix.client.RequestParams;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.auth.AuthenticationFlows;
import io.github.ma1uta.matrix.event.content.EventContent;
import io.github.ma1uta.matrix.impl.exception.MatrixException;
import io.github.ma1uta.matrix.impl.exception.RateLimitedException;
import io.github.ma1uta.matrix.support.jackson.EventContentDeserializer;
import io.github.ma1uta.matrix.support.jackson.JacksonContextResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.CompletionStageRxInvoker;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * Factory to invoke API using JAX-RS.
 */
public class JaxRsRequestFactory implements RequestFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRsRequestFactory.class);

    private final Client client;
    private final String homeserverUrl;
    private final ScheduledExecutorService service;

    public JaxRsRequestFactory(String homeserverUrl) {
        this(ClientBuilder.newBuilder().register(new JacksonContextResolver()).build(), homeserverUrl);
    }

    public JaxRsRequestFactory(Client client, String homeserverUrl) {
        this(client, homeserverUrl, Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() / 2));
    }

    public JaxRsRequestFactory(Client client, String homeserverUrl, ScheduledExecutorService service) {
        this.client = client;
        this.homeserverUrl = homeserverUrl;
        this.service = service;
    }

    public Client getClient() {
        return client;
    }

    public String getHomeserverUrl() {
        return homeserverUrl;
    }

    /**
     * Build the request.
     *
     * @param apiClass    The target API class.
     * @param apiMethod   The target API method.
     * @param params      The request params (query, path and headers).
     * @param requestType The 'Content-Type' header of the request.
     * @return The prepared request.
     */
    protected Invocation.Builder buildRequest(Class<?> apiClass, String apiMethod, RequestParams params, String requestType) {
        validateMethod(apiClass, apiMethod, params);

        UriBuilder builder = createUriBuilder(apiClass, apiMethod);
        URI uri = buildUri(params, builder);

        WebTarget path = buildWebTarget(uri);
        path = applyQueryParams(params, path);

        Invocation.Builder request = buildInvocationBuilder(requestType, path);
        request = applyHeaderParams(params, request);
        return addAccessToken(params, request);
    }

    /**
     * Check that the access token is provided if the protected resource is requested.
     *
     * @param apiClass  API class.
     * @param apiMethod API method.
     * @param params    The request params.
     * @throws IllegalArgumentException if the access token missing.
     */
    protected void validateMethod(Class<?> apiClass, String apiMethod, RequestParams params) {
        Method[] methods = AccessController.doPrivileged((PrivilegedAction<Method[]>) apiClass::getDeclaredMethods);
        Method method = Arrays.stream(methods).filter(m -> m.getName().equals(apiMethod)).findAny().orElseThrow(
            () -> new IllegalArgumentException(String.format("Cannot find the method %s in the class %s", apiMethod, apiClass.getName())));

        boolean secured = method.getAnnotation(Secured.class) != null;
        if (secured && (params.getAccessToken() == null || params.getAccessToken().trim().isEmpty())) {
            throw new IllegalArgumentException("The `access_token` should be specified in order to access to the secured resource.");
        }
    }

    /**
     * Create an URI builder.
     *
     * @param apiClass  API class.
     * @param apiMethod API method.
     * @return URI builder.
     */
    protected UriBuilder createUriBuilder(Class<?> apiClass, String apiMethod) {
        return UriBuilder.fromResource(apiClass).path(apiClass, apiMethod);
    }

    /**
     * Build the request URI.
     *
     * @param params  The request params.
     * @param builder The URI builder.
     * @return The request URI.
     */
    protected URI buildUri(RequestParams params, UriBuilder builder) {
        Map<String, String> encoded = params.getPathParams().entrySet().stream()
            .collect(toMap(Map.Entry::getKey, e -> encode(e.getValue())));
        return builder.buildFromEncodedMap(encoded);
    }

    /**
     * Build the request Web target.
     *
     * @param uri The request URI.
     * @return The Web target.
     */
    protected WebTarget buildWebTarget(URI uri) {
        return getClient().target(getHomeserverUrl()).path(uri.toString());
    }

    /**
     * Add query params to the request.
     *
     * @param params The request params.
     * @param path   The request target.
     * @return The web target with the query params.
     */
    protected WebTarget applyQueryParams(RequestParams params, WebTarget path) {
        for (Map.Entry<String, String> entry : params.getQueryParams().entrySet()) {
            path = path.queryParam(entry.getKey(), entry.getValue());
        }
        return path;
    }

    /**
     * Build the Invocation builder.
     *
     * @param requestType The MIME-type of the request.
     * @param path        The request target.
     * @return The Invocation builder.
     */
    protected Invocation.Builder buildInvocationBuilder(String requestType, WebTarget path) {
        return path.request(requestType);
    }

    /**
     * Add header params to the request.
     *
     * @param params  The request params.
     * @param request The request.
     * @return The request with the header params.
     */
    protected Invocation.Builder applyHeaderParams(RequestParams params, Invocation.Builder request) {
        for (Map.Entry<String, String> entry : params.getHeaderParams().entrySet()) {
            request = request.header(entry.getKey(), encode(entry.getValue()));
        }
        return request;
    }

    /**
     * Add the access token if available.
     *
     * @param params  The request params.
     * @param request The request.
     * @return The request with the access token.
     */
    protected Invocation.Builder addAccessToken(RequestParams params, Invocation.Builder request) {
        if (params.getAccessToken() != null && !params.getAccessToken().trim().isEmpty()) {
            request = request.header("Authorization", "Bearer " + params.getAccessToken().trim());
        }
        return request;
    }

    /**
     * Translates a string into application/x-www-form-urlencoded format using a UTF-8 encoding scheme.
     *
     * @param origin The original string.
     * @return The translated string.
     * @throws IllegalArgumentException when the origin string is empty.
     * @throws RuntimeException         when JVM doesn't support the UTF-8 (write me if this happens).
     */
    protected String encode(String origin) {
        if (origin == null) {
            String msg = "Empty value.";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        try {
            return URLEncoder.encode(origin, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            String msg = "Unsupported encoding.";
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * Return the function to read an entity with specified class from the response.
     *
     * @param genericType The {@link GenericType} of the entity. Used when the entity is a generic class.
     * @param <R>         The class of the instance.
     * @return the entity extractor.
     */
    protected <R> Function<Response, R> extractor(GenericType<R> genericType) {
        return response -> response.readEntity(genericType);
    }

    /**
     * Return the function to read an entity with specified class from the response.
     *
     * @param responseClass The class instance of the entity.
     * @param <R>           The class of the instance.
     * @return the entity extractor.
     */
    protected <R> Function<Response, R> extractor(Class<R> responseClass) {
        return response -> response.readEntity(responseClass);
    }

    @Override
    public <T, R> CompletableFuture<R> post(Class<?> apiClass, String apiMethod, RequestParams params, T payload, Class<R> responseClass) {
        return post(apiClass, apiMethod, params, payload, responseClass, MediaType.APPLICATION_JSON);
    }

    @Override
    public <T, R> CompletableFuture<R> post(Class<?> apiClass, String apiMethod, RequestParams params, T payload, Class<R> responseClass,
                                            String requestType) {
        CompletionStageRxInvoker rx = buildRequest(apiClass, apiMethod, params, requestType).rx();
        Entity<T> entity = Entity.entity(payload, requestType);
        return invoke(() -> rx.post(entity), extractor(responseClass));
    }

    @Override
    public <R> CompletableFuture<R> get(Class<?> apiClass, String apiMethod, RequestParams params, Class<R> responseClass) {
        CompletionStageRxInvoker rx = buildRequest(apiClass, apiMethod, params, MediaType.APPLICATION_JSON).rx();
        return invoke(rx::get, extractor(responseClass));
    }

    @Override
    public <R> CompletableFuture<R> get(Class<?> apiClass, String apiMethod, RequestParams params, GenericType<R> genericType) {
        CompletionStageRxInvoker rx = buildRequest(apiClass, apiMethod, params, MediaType.APPLICATION_JSON).rx();
        return invoke(rx::get, extractor(genericType));
    }

    @Override
    public <T, R> CompletableFuture<R> put(Class<?> apiClass, String apiMethod, RequestParams params, T payload, Class<R> responseClass) {
        CompletionStageRxInvoker rx = buildRequest(apiClass, apiMethod, params, MediaType.APPLICATION_JSON).rx();
        Entity<T> json = Entity.json(payload);
        return invoke(() -> rx.put(json), extractor(responseClass));
    }

    @Override
    public CompletableFuture<EmptyResponse> delete(Class<?> apiClass, String apiMethod, RequestParams params) {
        CompletionStageRxInvoker rx = buildRequest(apiClass, apiMethod, params, MediaType.APPLICATION_JSON).rx();
        return invoke(rx::delete, extractor(EmptyResponse.class));
    }

    @Override
    public EventContent deserialize(byte[] response, String eventType) {
        try {
            ObjectMapper mapper = getClient()
                .getConfiguration()
                .getInstances()
                .stream()
                .filter(o -> o instanceof JacksonContextResolver)
                .map(JacksonContextResolver.class::cast)
                .findFirst()
                .orElse(new JacksonContextResolver())
                .getContext(EventContent.class);
            return new EventContentDeserializer().deserialize(response, eventType, mapper);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invoke request in async mode.
     *
     * @param <R>       Class of the entity.
     * @param action    The action.
     * @param extractor The function to extract an entity from the response.
     * @return {@link CompletableFuture} the async result.
     */
    protected <R> CompletableFuture<R> invoke(Supplier<CompletionStage<Response>> action, Function<Response, R> extractor) {
        CompletableFuture<R> result = new CompletableFuture<>();

        service.execute(() -> invokeAction(action, extractor, result, 0L));

        return result;
    }

    /**
     * Invoke request in async mode.
     *
     * @param action    The action.
     * @param extractor The function to extract an entity from the response.
     * @param delay     The action delay.
     * @param <R>       Class of the entity.
     */
    protected <R> void invokeAction(Supplier<CompletionStage<Response>> action, Function<Response, R> extractor,
                                    CompletableFuture<R> result, long delay) {
        action.get().handle((response, throwable) -> {
            if (throwable != null) {
                result.completeExceptionally(throwable);
                return null;
            }

            try {
                int status = response.getStatus();
                LOGGER.debug("Response status: {}", status);
                switch (status) {
                    case SUCCESS:
                        success(result, response, extractor);
                        break;

                    case UNAUTHORIZED:
                        unauthorized(result, response);
                        break;

                    case RATE_LIMITED:
                        rateLimited(result, response, action, extractor, delay);
                        break;

                    default:
                        error(result, response);
                }

            } catch (Exception e) {
                LOGGER.error("Unknown exception.", e);
                result.completeExceptionally(e);
            } catch (Throwable e) {
                LOGGER.error("Throwable!", e);
                result.completeExceptionally(e);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Done: {}", result.isDone());
                LOGGER.debug("Cancelled: {}", result.isCancelled());
                LOGGER.debug("Exception: {}", result.isCompletedExceptionally());
            }

            return null;
        });
    }

    protected <R> void success(CompletableFuture<R> result, Response response, Function<Response, R> extractor) {
        LOGGER.debug("Success.");
        result.complete(extractor.apply(response));
    }

    protected <R> void unauthorized(CompletableFuture<R> result, Response response) {
        LOGGER.debug("Authentication required.");
        result.completeExceptionally(new AuthenticationRequred(response.readEntity(AuthenticationFlows.class)));
    }

    protected <R> void rateLimited(CompletableFuture<R> result,
                                   Response response, Supplier<CompletionStage<Response>> action,
                                   Function<Response, R> extractor,
                                   long delay) {
        LOGGER.warn("Rate limited.");
        RateLimitedErrorResponse rateLimited = response.readEntity(RateLimitedErrorResponse.class);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Retry after milliseconds: {}", rateLimited.getRetryAfterMs());
            LOGGER.debug("Errcode: {}", rateLimited.getErrcode());
            LOGGER.debug("Error: {}", rateLimited.getError());
        }

        long newDelay = rateLimited.getRetryAfterMs() != null ? rateLimited.getRetryAfterMs() : delay * DELAY_FACTOR;

        if (delay > MAX_DELAY) {
            LOGGER.error("Cannot send request, maximum delay was reached.");
            result.completeExceptionally(
                new RateLimitedException(rateLimited.getErrcode(), rateLimited.getError(), rateLimited.getRetryAfterMs()));
        } else {
            LOGGER.debug("Sleep milliseconds: {}", delay);
            service.schedule(() -> invokeAction(action, extractor, result, newDelay), newDelay, TimeUnit.MILLISECONDS);
        }
    }

    protected <R> void error(CompletableFuture<R> result, Response response) {
        LOGGER.debug("Error.");
        ErrorResponse error = response.readEntity(ErrorResponse.class);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Errcode: {}", error.getErrcode());
            LOGGER.debug("Error: {}", error.getError());
        }

        int status = response.getStatus();
        if (error == null) {
            result.completeExceptionally(
                new MatrixException(MatrixException.M_INTERNAL, "Missing error response.", status));
        } else {
            result.completeExceptionally(
                new MatrixException(error.getErrcode(), error.getError(), status));
        }
    }
}


