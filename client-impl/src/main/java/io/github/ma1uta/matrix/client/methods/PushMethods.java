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

package io.github.ma1uta.matrix.client.methods;

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.RequestParams;
import io.github.ma1uta.matrix.client.api.PushApi;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.push.NotificationResponse;
import io.github.ma1uta.matrix.client.model.push.PushActions;
import io.github.ma1uta.matrix.client.model.push.PushEnable;
import io.github.ma1uta.matrix.client.model.push.PushRulesResponse;
import io.github.ma1uta.matrix.client.model.push.PushUpdateRequest;
import io.github.ma1uta.matrix.client.model.push.Pusher;
import io.github.ma1uta.matrix.client.model.push.PushersRequest;
import io.github.ma1uta.matrix.client.model.push.PushersResponse;
import io.github.ma1uta.matrix.event.nested.PushRule;
import io.github.ma1uta.matrix.event.nested.Ruleset;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Push methods.
 */
public class PushMethods extends AbstractMethods {

    public PushMethods(RequestFactory factory, RequestParams defaultParams) {
        super(factory, defaultParams);
    }

    /**
     * Gets all currently active pushers for the authenticated user.
     *
     * @return The pushers for this user.
     */
    public CompletableFuture<List<Pusher>> showPushers() {
        return factory().get(PushApi.class, "showPushers", defaults(), PushersResponse.class).thenApply(PushersResponse::getPushers);
    }

    /**
     * This endpoint allows the creation, modification and deletion of pushers for this user ID. The behaviour of this endpoint
     * varies depending on the values in the JSON body.
     *
     * @param request The pusher data.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> setPushers(PushersRequest request) {
        return factory().post(PushApi.class, "setPushers", defaults(), request, EmptyResponse.class);
    }

    /**
     * This API is used to paginate through the list of events that the user has been, or would have been notified about.
     *
     * @param from  Pagination token given to retrieve the next set of events.
     * @param only  Allows basic filtering of events returned. Supply highlight to return only events where the notification had
     *              the highlight tweak set.
     * @param limit Limit on the number of events to return in this request.
     * @return A batch of events is being returned.
     */
    public CompletableFuture<NotificationResponse> notifications(String from, String only, Long limit) {
        RequestParams params = defaults().clone().query("from", from).query("only", only).query("limit", limit);
        return factory().get(PushApi.class, "notifications", params, NotificationResponse.class);
    }

    /**
     * Retrieve all push rulesets for this user.
     *
     * @return All the push rulesets for this user.
     */
    public CompletableFuture<Ruleset> pushRules() {
        return factory().get(PushApi.class, "pushRules", new RequestParams(), PushRulesResponse.class)
            .thenApply(PushRulesResponse::getGlobal);
    }

    /**
     * Retrieve a single specified push rule.
     *
     * @param scope  Global to specify global rules.
     * @param kind   The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId The identifier for the rule.
     * @return The specific push rule. This will also include keys specific to the rule itself such as the rule's actions and conditions
     *     if set.
     */
    public CompletableFuture<PushRule> pushRule(String scope, String kind, String ruleId) {
        Objects.requireNonNull(scope, "Scope cannot be empty.");
        Objects.requireNonNull(kind, "Kind cannot be empty.");
        Objects.requireNonNull(ruleId, "RuleId cannot be empty.");

        RequestParams params = defaults().clone().path("scope", scope).path("kind", kind).path("ruleId", ruleId);
        return factory().get(PushApi.class, "pushRule", params, PushRule.class);
    }

    /**
     * This endpoint removes the push rule defined in the path.
     *
     * @param scope  Global to specify global rules.
     * @param kind   The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId The identifier for the rule.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> deleteRule(String scope, String kind, String ruleId) {
        Objects.requireNonNull(scope, "Scope cannot be empty.");
        Objects.requireNonNull(kind, "Kind cannot be empty.");
        Objects.requireNonNull(ruleId, "RuleId cannot be empty.");

        RequestParams params = defaults().clone().path("scope", scope).path("kind", kind).path("ruleId", ruleId);
        return factory().delete(PushApi.class, "deleteRule", params);
    }

    /**
     * This endpoint allows the creation, modification and deletion of pushers for this user ID. The behaviour of this endpoint
     * varies depending on the values in the JSON body.
     *
     * @param scope   Global to specify global rules.
     * @param kind    The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId  The identifier for the rule.
     * @param before  Use 'before' with a rule_id as its value to make the new rule the next-most important rule with respect
     *                to the given user defined rule. It is not possible to add a rule relative to a predefined server rule.
     * @param after   This makes the new rule the next-less important rule relative to the given user defined rule. It is not
     *                possible to add a rule relative to a predefined server rule.
     * @param request The rule data.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> updateRule(String scope, String kind, String ruleId, String before, String after,
                                                       PushUpdateRequest request) {
        Objects.requireNonNull(scope, "Scope cannot be empty.");
        Objects.requireNonNull(kind, "Kind cannot be empty.");
        Objects.requireNonNull(ruleId, "RuleId cannot be empty.");
        if (request.getActions() == null || request.getActions().isEmpty()) {
            throw new NullPointerException("Actions cannot be empty.");
        }

        RequestParams params = defaults().clone().path("scope", scope).path("kind", kind).path("ruleId", ruleId)
            .query("before", before).query("after", after);
        return factory().put(PushApi.class, "updateRule", params, request, EmptyResponse.class);
    }

    /**
     * This endpoint gets whether the specified push rule is enabled.
     *
     * @param scope  Global to specify global rules.
     * @param kind   The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId The identifier for the rule.
     * @return Whether the push rule is enabled.
     */
    public CompletableFuture<Boolean> getEnabled(String scope, String kind, String ruleId) {
        Objects.requireNonNull(scope, "Scope cannot be empty.");
        Objects.requireNonNull(kind, "Kind cannot be empty.");
        Objects.requireNonNull(ruleId, "RuleId cannot be empty.");

        RequestParams params = defaults().clone().path("scope", scope).path("kind", kind).path("ruleId", ruleId);
        return factory().get(PushApi.class, "getEnabled", params, PushEnable.class).thenApply(PushEnable::getEnabled);
    }

    /**
     * This endpoint allows clients to enable or disable the specified push rule.
     *
     * @param scope   Global to specify global rules.
     * @param kind    The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId  The identifier for the rule.
     * @param enabled Whether the push rule is enabled or not.
     * @return empty response.
     */
    public CompletableFuture<EmptyResponse> setEnabled(String scope, String kind, String ruleId, boolean enabled) {
        Objects.requireNonNull(scope, "Scope cannot be empty.");
        Objects.requireNonNull(kind, "Kind cannot be empty.");
        Objects.requireNonNull(ruleId, "RuleId cannot be empty.");

        RequestParams params = defaults().clone().path("scope", scope).path("kind", kind).path("ruleId", ruleId);
        PushEnable request = new PushEnable();
        request.setEnabled(enabled);
        return factory().put(PushApi.class, "setEnabled", params, request, EmptyResponse.class);
    }

    /**
     * This endpoint get the actions for the specified push rule.
     *
     * @param scope  Global to specify global rules.
     * @param kind   The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId The identifier for the rule.
     * @return The actions for this push rule.
     */
    public CompletableFuture<List<String>> getActions(String scope, String kind, String ruleId) {
        Objects.requireNonNull(scope, "Scope cannot be empty.");
        Objects.requireNonNull(kind, "Kind cannot be empty.");
        Objects.requireNonNull(ruleId, "RuleId cannot be empty.");

        RequestParams params = defaults().clone().path("scope", scope).path("kind", kind).path("ruleId", ruleId);
        return factory().get(PushApi.class, "getActions", params, PushActions.class).thenApply(PushActions::getActions);
    }

    /**
     * This endpoint allows clients to change the actions of a push rule. This can be used to change the actions of builtin rules.
     *
     * @param scope   Global to specify global rules.
     * @param kind    The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId  The identifier for the rule.
     * @param actions The actions for this push rule.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> setActions(String scope, String kind, String ruleId, List<String> actions) {
        Objects.requireNonNull(scope, "Scope cannot be empty.");
        Objects.requireNonNull(kind, "Kind cannot be empty.");
        Objects.requireNonNull(ruleId, "RuleId cannot be empty.");

        RequestParams params = defaults().path("scope", scope).path("kind", kind).path("ruleId", ruleId);
        PushActions request = new PushActions();
        request.setActions(actions);
        return factory().put(PushApi.class, "setActions", params, request, EmptyResponse.class);
    }
}
