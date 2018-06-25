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
import io.github.ma1uta.matrix.client.api.DeviceApi;
import io.github.ma1uta.matrix.client.model.device.Device;
import io.github.ma1uta.matrix.client.model.device.DeviceDeleteRequest;
import io.github.ma1uta.matrix.client.model.device.DeviceUpdateRequest;
import io.github.ma1uta.matrix.client.model.device.DevicesDeleteRequest;
import io.github.ma1uta.matrix.client.model.device.DevicesResponse;

import java.util.List;

/**
 * Admin methods.
 */
public class DeviceMethods {

    private final MatrixClient matrixClient;

    DeviceMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * Gets information about all devices for the current user.
     *
     * @return devices.
     */
    public List<Device> devices() {
        return getMatrixClient().getRequestMethods().get(DeviceApi.class, "devices", new RequestParams(), DevicesResponse.class)
            .getDevices();
    }

    /**
     * Gets information on a single device, by device id.
     *
     * @param deviceId device id.
     * @return device information.
     */
    public Device device(String deviceId) {
        RequestParams params = new RequestParams().pathParam("deviceId", deviceId);
        return getMatrixClient().getRequestMethods().get(DeviceApi.class, "device", params, Device.class);
    }

    /**
     * Updates the metadata on the given device.
     *
     * @param deviceId    device id.
     * @param displayName new device display name.
     */
    public void update(String deviceId, String displayName) {
        RequestParams params = new RequestParams().pathParam("deviceId", deviceId);
        DeviceUpdateRequest request = new DeviceUpdateRequest();
        request.setDisplayName(displayName);
        getMatrixClient().getRequestMethods().put(DeviceApi.class, "updateDevice", params, request, EmptyResponse.class);
    }

    /**
     * Deletes the given device, and invalidates any access token associated with it.
     *
     * @param deviceId device id.
     * @param request  authentication request.
     */
    public void delete(String deviceId, DeviceDeleteRequest request) {
        RequestMethods requestMethods = getMatrixClient().getRequestMethods();
        RequestParams params = new RequestParams().pathParam("deviceId", deviceId);
        requestMethods.delete(DeviceApi.class, "deleteDevice", params, request);
    }

    /**
     * Deletes the given devices, and invalidates any access token associated with them.
     *
     * @param request Devices to delete and additional authentication data.
     */
    public void deleteDevices(DevicesDeleteRequest request) {
        getMatrixClient().getRequestMethods().post(DeviceApi.class, "deleteDevices", new RequestParams(), request, EmptyResponse.class);
    }
}
