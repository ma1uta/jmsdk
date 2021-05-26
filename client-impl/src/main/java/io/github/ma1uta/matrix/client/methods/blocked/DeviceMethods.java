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

import io.github.ma1uta.matrix.client.ConnectionInfo;
import io.github.ma1uta.matrix.client.model.account.AuthenticationData;
import io.github.ma1uta.matrix.client.model.device.Device;
import io.github.ma1uta.matrix.client.model.device.DeviceUpdateRequest;
import io.github.ma1uta.matrix.client.model.device.DevicesDeleteRequest;
import io.github.ma1uta.matrix.client.model.device.DevicesResponse;
import io.github.ma1uta.matrix.client.rest.blocked.DeviceApi;
import io.github.ma1uta.matrix.common.EmptyResponse;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Admin methods.
 */
public class DeviceMethods {

    private final DeviceApi deviceApi;

    private final ConnectionInfo connectionInfo;

    public DeviceMethods(RestClientBuilder restClientBuilder, ConnectionInfo connectionInfo) {
        this.deviceApi = restClientBuilder.build(DeviceApi.class);
        this.connectionInfo = connectionInfo;
    }

    /**
     * Gets information about all devices for the current user.
     *
     * @return The devices.
     */
    public DevicesResponse devices() {
        return deviceApi.devices();
    }

    /**
     * Gets information on a single device, by device id.
     *
     * @param deviceId The device id.
     * @return The device information.
     */
    public Device device(String deviceId) {
        Objects.requireNonNull(deviceId, "DeviceId cannot be empty.");

        return deviceApi.device(deviceId);
    }

    /**
     * Updates the metadata on the given device.
     *
     * @param deviceId    The device id.
     * @param displayName A new device display name.
     * @return The empty response.
     */
    public EmptyResponse update(String deviceId, String displayName) {
        Objects.requireNonNull(deviceId, "DeviceId cannot be empty.");

        DeviceUpdateRequest request = new DeviceUpdateRequest();
        request.setDisplayName(displayName);

        return deviceApi.updateDevice(deviceId, request);
    }

    /**
     * Delete the current device and invalidate this access token.
     *
     * @param auth Additional authentication information for the user-interactive authentication API.
     * @return The empty response.
     */
    public EmptyResponse delete(AuthenticationData auth) {
        DevicesDeleteRequest request = new DevicesDeleteRequest();
        request.setAuth(auth);
        List<String> devices = Collections.singletonList(connectionInfo.getDeviceId());
        request.setDevices(devices);

        EmptyResponse response = deviceApi.deleteDevices(request);
        connectionInfo.setDeviceId(null);
        connectionInfo.setAccessToken(null);
        return response;
    }

    /**
     * Deletes the given devices, and invalidates any access token associated with them.
     *
     * @param request Devices to delete and additional authentication data.
     * @return The empty response.
     */
    public EmptyResponse deleteDevices(DevicesDeleteRequest request) {
        String error = "Devices cannot be empty.";
        Objects.requireNonNull(request.getDevices(), error);
        if (request.getDevices().isEmpty()) {
            throw new NullPointerException(error);
        }

        EmptyResponse response = deviceApi.deleteDevices(request);
        if (request.getDevices().contains(connectionInfo.getDeviceId())) {
            connectionInfo.setDeviceId(null);
            connectionInfo.setAccessToken(null);
        }
        return response;
    }
}
