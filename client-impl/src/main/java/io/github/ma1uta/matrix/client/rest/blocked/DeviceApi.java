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

package io.github.ma1uta.matrix.client.rest.blocked;

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.model.device.Device;
import io.github.ma1uta.matrix.client.model.device.DeviceDeleteRequest;
import io.github.ma1uta.matrix.client.model.device.DeviceUpdateRequest;
import io.github.ma1uta.matrix.client.model.device.DevicesDeleteRequest;
import io.github.ma1uta.matrix.client.model.device.DevicesResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Clients that implement this module should offer the user a list of registered devices, as well as the means to update their
 * display names. Clients should also allow users to delete disused devices.
 */
@Path("/_matrix/client/r0")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface DeviceApi {

    /**
     * Gets information about all devices for the current user.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: A list of all registered devices for this user.</p>
     *
     * @return {@link DevicesResponse}.
     */
    @GET
    @Path("/devices")
    DevicesResponse devices();

    /**
     * Gets information on a single device, by device id.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: Device information.</p>
     * <p>Status code 404: The current user has no device with the given ID.</p>
     *
     * @param deviceId Required. The device to retrieve.
     * @return {@link Device}.
     */
    @GET
    @Path("/devices/{deviceId}")
    Device device(
        @PathParam("deviceId") String deviceId
    );

    /**
     * Updates the metadata on the given device.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The device was successfully updated.</p>
     * <p>Status code 404: The current user has no device with the given ID.</p>
     *
     * @param deviceId            Required. The device to update.
     * @param deviceUpdateRequest The new display name for this device. If not given, the display name is unchanged.
     * @return {@link EmptyResponse}.
     */
    @PUT
    @Path("/devices/{deviceId}")
    EmptyResponse updateDevice(
        @PathParam("deviceId") String deviceId,
        DeviceUpdateRequest deviceUpdateRequest
    );

    /**
     * This API endpoint uses the User-Interactive Authentication API.
     * <br>
     * Deletes the given device, and invalidates any access token associated with it.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The device was successfully removed, or had been removed previously.</p>
     * <p>Status code 401: The homeserver requires additional authentication information.</p>
     *
     * @param deviceId            Required. The device to delete.
     * @param deviceDeleteRequest Additional authentication information for the user-interactive authentication API.
     * @return {@link EmptyResponse}.
     */
    @DELETE
    @Path("/devices/{deviceId}")
    EmptyResponse deleteDevice(
        @PathParam("deviceId") String deviceId,
        DeviceDeleteRequest deviceDeleteRequest
    );

    /**
     * This API endpoint uses the User-Interactive Authentication API.
     * <br>
     * Deletes the given devices, and invalidates any access token associated with them.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The devices were successfully removed, or had been removed previously.</p>
     * <p>Status code 401: The homeserver requires additional authentication information.</p>
     *
     * @param devicesDeleteRequest JSON body request.
     * @return {@link EmptyResponse}.
     */
    @POST
    @Path("/delete_devices")
    EmptyResponse deleteDevices(
        DevicesDeleteRequest devicesDeleteRequest
    );
}
