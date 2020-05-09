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

package io.github.ma1uta.matrix.client.rest.async;

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.model.push.NotificationResponse;
import io.github.ma1uta.matrix.client.model.push.PushActions;
import io.github.ma1uta.matrix.client.model.push.PushEnable;
import io.github.ma1uta.matrix.client.model.push.PushRulesResponse;
import io.github.ma1uta.matrix.client.model.push.PushUpdateRequest;
import io.github.ma1uta.matrix.client.model.push.PushersRequest;
import io.github.ma1uta.matrix.client.model.push.PushersResponse;
import io.github.ma1uta.matrix.event.nested.PushRule;

import java.util.concurrent.CompletionStage;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * This module adds support for push notifications. Homeservers send notifications of events to user-configured HTTP endpoints.
 * Users may also configure a number of rules that determine which events generate notifications. These are all stored and managed
 * by the user's homeserver. This allows user-specific push settings to be reused between client applications.
 */
@Path("/_matrix/client/r0")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface PushApi {

    /**
     * Gets all currently active pushers for the authenticated user.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The pushers for this user.</p>
     *
     * @return {@link PushersResponse}.
     */
    @GET
    @Path("/pushers")
    CompletionStage<PushersResponse> showPushers();

    /**
     * This endpoint allows the creation, modification and deletion of pushers for this user ID. The behaviour of this endpoint
     * varies depending on the values in the JSON body.
     * <br>
     * <b>Rate-limited</b>: Yes.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The pusher was set.</p>
     * <p>Status code 400: One or more of the pusher values were invalid.</p>
     * <p>Status code 429: This request was rate-limited.</p>
     *
     * @param pushersRequest JSON body request.
     * @return {@link EmptyResponse}.
     */
    @POST
    @Path("/pushers/set")
    CompletionStage<EmptyResponse> setPushers(
        PushersRequest pushersRequest
    );

    /**
     * This API is used to paginate through the list of events that the user has been, or would have been notified about.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: A batch of events is being returned.</p>
     *
     * @param from  Pagination token given to retrieve the next set of events.
     * @param only  Allows basic filtering of events returned. Supply highlight to return only events where the notification had
     *              the highlight tweak set.
     * @param limit Limit on the number of events to return in this request.
     * @return {@link NotificationResponse}.
     */
    @GET
    @Path("/notifications")
    CompletionStage<NotificationResponse> notifications(
        @QueryParam("from") String from,
        @QueryParam("only") String only,
        @QueryParam("limit") Long limit
    );

    /**
     * Retrieve all push rulesets for this user. Clients can "drill-down" on the rulesets by suffixing a scope to this path e.g.
     * /pushrules/global/. This will return a subset of this data under the specified key e.g. the global key.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: All the push rulesets for this user.</p>
     *
     * @return {@link PushRulesResponse}.
     */
    @GET
    @Path("/pushrules")
    CompletionStage<PushRulesResponse> pushRules();

    /**
     * Retrieve a single specified push rule.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The specific push rule. This will also include keys specific to the rule itself such as the rule's
     * actions and conditions if set.</p>
     *
     * @param scope  Required. Global to specify global rules.
     * @param kind   Required. The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId Required. The identifier for the rule.
     * @return {@link PushRule}.
     */
    @GET
    @Path("/pushrules/{scope}/{kind}/{ruleId}")
    CompletionStage<PushRule> pushRule(
        @PathParam("scope") String scope,
        @PathParam("kind") String kind,
        @PathParam("ruleId") String ruleId
    );

    /**
     * This endpoint removes the push rule defined in the path.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The push rule was deleted.</p>
     *
     * @param scope  Required. Global to specify global rules.
     * @param kind   Required. The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId Required. The identifier for the rule.
     * @return {@link EmptyResponse}.
     */
    @DELETE
    @Path("/pushrules/{scope}/{kind}/{ruleId}")
    CompletionStage<EmptyResponse> deleteRule(
        @PathParam("scope") String scope,
        @PathParam("kind") String kind,
        @PathParam("ruleId") String ruleId
    );

    /**
     * This endpoint allows the creation, modification and deletion of pushers for this user ID. The behaviour of this endpoint
     * varies depending on the values in the JSON body.
     * <br>
     * <b>Rate-limited</b>: Yes.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The pusher was set.</p>
     * <p>Status code 400: There was a problem configuring this push rule.</p>
     * <p>Status code 429: This request was rate-limited.</p>
     *
     * @param scope             Required. Global to specify global rules.
     * @param kind              Required. The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId            Required. The identifier for the rule.
     * @param before            Use 'before' with a rule_id as its value to make the new rule the next-most important rule with respect
     *                          to the given user defined rule. It is not possible to add a rule relative to a predefined server rule.
     * @param after             This makes the new rule the next-less important rule relative to the given user defined rule. It is not
     *                          possible to add a rule relative to a predefined server rule.
     * @param pushUpdateRequest JSON body request.
     * @return {@link EmptyResponse}.
     */
    @PUT
    @Path("/pushrules/{scope}/{kind}/{ruleId}")
    CompletionStage<EmptyResponse> updateRule(
        @PathParam("scope") String scope,
        @PathParam("kind") String kind,
        @PathParam("ruleId") String ruleId,
        @QueryParam("before") String before,
        @QueryParam("after") String after,
        PushUpdateRequest pushUpdateRequest
    );

    /**
     * This endpoint gets whether the specified push rule is enabled.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: Whether the push rule is enabled.</p>
     *
     * @param scope  Required. Either global or device/&lt;profile_tag&gt; to specify global rules or device rules for the given
     *               profile_tag.
     * @param kind   Required. The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId Required. The identifier for the rule.
     * @return {@link PushEnable}.
     */
    @GET
    @Path("/pushrules/{scope}/{kind}/{ruleId}/enabled")
    CompletionStage<PushEnable> getEnabled(
        @PathParam("scope") String scope,
        @PathParam("kind") String kind,
        @PathParam("ruleId") String ruleId
    );

    /**
     * This endpoint allows clients to enable or disable the specified push rule.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The push rule was enabled or disabled.</p>
     *
     * @param scope      Required. Global to specify global rules.
     * @param kind       Required. The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId     Required. The identifier for the rule.
     * @param pushEnable JSON body request.
     * @return {@link EmptyResponse}.
     */
    @PUT
    @Path("/pushrules/{scope}/{kind}/{ruleId}/enabled")
    CompletionStage<EmptyResponse> setEnabled(
        @PathParam("scope") String scope,
        @PathParam("kind") String kind,
        @PathParam("ruleId") String ruleId,
        PushEnable pushEnable
    );

    /**
     * This endpoint get the actions for the specified push rule.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The actions for this push rule.</p>
     *
     * @param scope  Required. Either global or device/&lt;profile_tag&gt; to specify global rules or device rules for the given
     *               profile_tag.
     * @param kind   Required. The kind of rule One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId Required. The identifier for the rule.
     * @return {@link PushActions}.
     */
    @GET
    @Path("/pushrules/{scope}/{kind}/{ruleId}/actions")
    CompletionStage<PushActions> getActions(
        @PathParam("scope") String scope,
        @PathParam("kind") String kind,
        @PathParam("ruleId") String ruleId
    );

    /**
     * This endpoint allows clients to change the actions of a push rule. This can be used to change the actions of builtin rules.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The actions for the push rule were set.</p>
     *
     * @param scope       Required. Global to specify global rules.
     * @param kind        Required. The kind of rule. One of: ["override", "underride", "sender", "room", "content"].
     * @param ruleId      Required. The identifier for the rule.
     * @param pushActions JSON body request.
     * @return {@link EmptyResponse}.
     */
    @PUT
    @Path("/pushrules/{scope}/{kind}/{ruleId}/actions")
    CompletionStage<EmptyResponse> setActions(
        @PathParam("scope") String scope,
        @PathParam("kind") String kind,
        @PathParam("ruleId") String ruleId,
        PushActions pushActions
    );
}
