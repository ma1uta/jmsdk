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

package io.github.ma1uta.matrix.client.methods.blocked;

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.model.push.NotificationResponse;
import io.github.ma1uta.matrix.client.model.push.PushActions;
import io.github.ma1uta.matrix.client.model.push.PushEnable;
import io.github.ma1uta.matrix.client.model.push.PushRulesResponse;
import io.github.ma1uta.matrix.client.model.push.PushUpdateRequest;
import io.github.ma1uta.matrix.client.model.push.PushersRequest;
import io.github.ma1uta.matrix.client.model.push.PushersResponse;
import io.github.ma1uta.matrix.client.rest.blocked.PushApi;
import io.github.ma1uta.matrix.event.nested.PushRule;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.List;
import java.util.Objects;

/**
 * Push methods.
 */
public class PushMethods {

    private final PushApi pushApi;

    public PushMethods(RestClientBuilder restClientBuilder) {
        this.pushApi = restClientBuilder.build(PushApi.class);
    }

    /**
     * Gets all currently active pushers for the authenticated user.
     *
     * @return The pushers for this user.
     */
    public PushersResponse showPushers() {
        return pushApi.showPushers();
    }

    /**
     * This endpoint allows the creation, modification and deletion of pushers for this user ID. The behaviour of this endpoint
     * varies depending on the values in the JSON body.
     *
     * @param request The pusher data.
     * @return The empty response.
     */
    public EmptyResponse setPushers(PushersRequest request) {
        return pushApi.setPushers(request);
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
    public NotificationResponse notifications(String from, String only, Long limit) {
        return pushApi.notifications(from, only, limit);
    }

    /**
     * Retrieve all push rulesets for this user.
     *
     * @return All the push rulesets for this user.
     */
    public PushRulesResponse pushRules() {
        return pushApi.pushRules();
    }

    /**
     * Retrieve a single specified push rule.
     *
     * @param scope  Global to specify global rules.
     * @param kind   The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId The identifier for the rule.
     * @return The specific push rule. This will also include keys specific to the rule itself such as the rule's actions and conditions
     */
    public PushRule pushRule(String scope, String kind, String ruleId) {
        Objects.requireNonNull(scope, "Scope cannot be empty.");
        Objects.requireNonNull(kind, "Kind cannot be empty.");
        Objects.requireNonNull(ruleId, "RuleId cannot be empty.");

        return pushApi.pushRule(scope, kind, ruleId);
    }

    /**
     * This endpoint removes the push rule defined in the path.
     *
     * @param scope  Global to specify global rules.
     * @param kind   The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId The identifier for the rule.
     * @return The empty response.
     */
    public EmptyResponse deleteRule(String scope, String kind, String ruleId) {
        Objects.requireNonNull(scope, "Scope cannot be empty.");
        Objects.requireNonNull(kind, "Kind cannot be empty.");
        Objects.requireNonNull(ruleId, "RuleId cannot be empty.");

        return pushApi.deleteRule(scope, kind, ruleId);
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
    public EmptyResponse updateRule(String scope, String kind, String ruleId, String before, String after,
                                    PushUpdateRequest request) {
        Objects.requireNonNull(scope, "Scope cannot be empty.");
        Objects.requireNonNull(kind, "Kind cannot be empty.");
        Objects.requireNonNull(ruleId, "RuleId cannot be empty.");
        if (request.getActions() == null || request.getActions().isEmpty()) {
            throw new NullPointerException("Actions cannot be empty.");
        }

        return pushApi.updateRule(scope, kind, ruleId, before, after, request);
    }

    /**
     * This endpoint gets whether the specified push rule is enabled.
     *
     * @param scope  Global to specify global rules.
     * @param kind   The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId The identifier for the rule.
     * @return Whether the push rule is enabled.
     */
    public PushEnable getEnabled(String scope, String kind, String ruleId) {
        Objects.requireNonNull(scope, "Scope cannot be empty.");
        Objects.requireNonNull(kind, "Kind cannot be empty.");
        Objects.requireNonNull(ruleId, "RuleId cannot be empty.");

        return pushApi.getEnabled(scope, kind, ruleId);
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
    public EmptyResponse setEnabled(String scope, String kind, String ruleId, boolean enabled) {
        Objects.requireNonNull(scope, "Scope cannot be empty.");
        Objects.requireNonNull(kind, "Kind cannot be empty.");
        Objects.requireNonNull(ruleId, "RuleId cannot be empty.");

        PushEnable request = new PushEnable();
        request.setEnabled(enabled);

        return pushApi.setEnabled(scope, kind, ruleId, request);
    }

    /**
     * This endpoint get the actions for the specified push rule.
     *
     * @param scope  Global to specify global rules.
     * @param kind   The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId The identifier for the rule.
     * @return The actions for this push rule.
     */
    public PushActions getActions(String scope, String kind, String ruleId) {
        Objects.requireNonNull(scope, "Scope cannot be empty.");
        Objects.requireNonNull(kind, "Kind cannot be empty.");
        Objects.requireNonNull(ruleId, "RuleId cannot be empty.");

        return pushApi.getActions(scope, kind, ruleId);
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
    public EmptyResponse setActions(String scope, String kind, String ruleId, List<String> actions) {
        Objects.requireNonNull(scope, "Scope cannot be empty.");
        Objects.requireNonNull(kind, "Kind cannot be empty.");
        Objects.requireNonNull(ruleId, "RuleId cannot be empty.");

        PushActions request = new PushActions();
        request.setActions(actions);

        return pushApi.setActions(scope, kind, ruleId, request);
    }
}
