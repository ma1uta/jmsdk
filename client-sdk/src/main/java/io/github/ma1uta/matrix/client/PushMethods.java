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

package io.github.ma1uta.matrix.client;

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.api.PushApi;
import io.github.ma1uta.matrix.client.model.push.NotificationResponse;
import io.github.ma1uta.matrix.client.model.push.PushActions;
import io.github.ma1uta.matrix.client.model.push.PushEnable;
import io.github.ma1uta.matrix.client.model.push.PushRule;
import io.github.ma1uta.matrix.client.model.push.PushRulesResponse;
import io.github.ma1uta.matrix.client.model.push.PushUpdateRequest;
import io.github.ma1uta.matrix.client.model.push.Pusher;
import io.github.ma1uta.matrix.client.model.push.PushersRequest;
import io.github.ma1uta.matrix.client.model.push.PushersResponse;
import io.github.ma1uta.matrix.client.model.push.Ruleset;

import java.util.List;

/**
 * Push methods.
 */
public class PushMethods {

    private final MatrixClient matrixClient;

    PushMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * Gets all currently active pushers for the authenticated user.
     *
     * @return The pushers for this user.
     */
    public List<Pusher> showPushers() {
        return getMatrixClient().getRequestMethods().get(PushApi.class, "showPushers", new RequestParams(), PushersResponse.class)
            .getPushers();
    }

    /**
     * This endpoint allows the creation, modification and deletion of pushers for this user ID. The behaviour of this endpoint
     * varies depending on the values in the JSON body.
     *
     * @param request The pusher data.
     */
    public void setPushers(PushersRequest request) {
        getMatrixClient().getRequestMethods().post(PushApi.class, "setPushers", new RequestParams(), request, EmptyResponse.class);
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
        RequestParams params = new RequestParams().queryParam("from", from).queryParam("only", only).queryParam("limit", limit);
        return getMatrixClient().getRequestMethods().get(PushApi.class, "notifications", params, NotificationResponse.class);
    }

    /**
     * Retrieve all push rulesets for this user.
     *
     * @return All the push rulesets for this user.
     */
    public Ruleset pushRules() {
        return getMatrixClient().getRequestMethods().get(PushApi.class, "pushRules", new RequestParams(), PushRulesResponse.class)
            .getGlobal();
    }

    /**
     * Retrieve a single specified push rule.
     *
     * @param scope  Global to specify global rules.
     * @param kind   The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId The identifier for the rule.
     * @return The specific push rule. This will also include keys specific to the rule itself such as the rule's
     *     actions and conditions if set.
     */
    public PushRule pushRule(String scope, String kind, String ruleId) {
        RequestParams params = new RequestParams().pathParam("scope", scope).pathParam("kind", kind).pathParam("ruleId", ruleId);
        return getMatrixClient().getRequestMethods().get(PushApi.class, "pushRule", params, PushRule.class);
    }

    /**
     * This endpoint removes the push rule defined in the path.
     *
     * @param scope  Global to specify global rules.
     * @param kind   The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId The identifier for the rule.
     */
    public void deleteRule(String scope, String kind, String ruleId) {
        RequestParams params = new RequestParams().pathParam("scope", scope).pathParam("kind", kind).pathParam("ruleId", ruleId);
        getMatrixClient().getRequestMethods().delete(PushApi.class, "deleteRule", params, EmptyResponse.class);
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
     */
    public void updateRule(String scope, String kind, String ruleId, String before, String after, PushUpdateRequest request) {
        RequestParams params = new RequestParams().pathParam("scope", scope).pathParam("kind", kind).pathParam("ruleId", ruleId)
            .queryParam("before", before).queryParam("after", after);
        getMatrixClient().getRequestMethods().put(PushApi.class, "updateRule", params, request, EmptyResponse.class);
    }

    /**
     * This endpoint gets whether the specified push rule is enabled.
     *
     * @param scope  Global to specify global rules.
     * @param kind   The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId The identifier for the rule.
     * @return Whether the push rule is enabled.
     */
    public Boolean getEnabled(String scope, String kind, String ruleId) {
        RequestParams params = new RequestParams().pathParam("scope", scope).pathParam("kind", kind).pathParam("ruleId", ruleId);
        return getMatrixClient().getRequestMethods().get(PushApi.class, "getEnabled", params, PushEnable.class).getEnabled();
    }

    /**
     * This endpoint allows clients to enable or disable the specified push rule.
     *
     * @param scope   Global to specify global rules.
     * @param kind    The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId  The identifier for the rule.
     * @param enabled Whether the push rule is enabled or not.
     */
    public void setEnabled(String scope, String kind, String ruleId, boolean enabled) {
        RequestParams params = new RequestParams().pathParam("scope", scope).pathParam("kind", kind).pathParam("ruleId", ruleId);
        PushEnable request = new PushEnable();
        request.setEnabled(enabled);
        getMatrixClient().getRequestMethods().put(PushApi.class, "setEnabled", params, request, EmptyResponse.class);
    }

    /**
     * This endpoint get the actions for the specified push rule.
     *
     * @param scope  Global to specify global rules.
     * @param kind   The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId The identifier for the rule.
     * @return The actions for this push rule.
     */
    public List<String> getActions(String scope, String kind, String ruleId) {
        RequestParams params = new RequestParams().pathParam("scope", scope).pathParam("kind", kind).pathParam("ruleId", ruleId);
        return getMatrixClient().getRequestMethods().get(PushApi.class, "getActions", params, PushActions.class).getActions();
    }

    /**
     * This endpoint allows clients to change the actions of a push rule. This can be used to change the actions of builtin rules.
     *
     * @param scope   Global to specify global rules.
     * @param kind    The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId  The identifier for the rule.
     * @param actions The actions for this push rule.
     */
    public void setActions(String scope, String kind, String ruleId, List<String> actions) {
        RequestParams params = new RequestParams().pathParam("scope", scope).pathParam("kind", kind).pathParam("ruleId", ruleId);
        PushActions request = new PushActions();
        request.setActions(actions);
        getMatrixClient().getRequestMethods().put(PushApi.class, "setActions", params, request, EmptyResponse.class);
    }
}
