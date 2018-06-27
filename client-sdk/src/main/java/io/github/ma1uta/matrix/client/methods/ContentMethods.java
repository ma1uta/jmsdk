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

import io.github.ma1uta.matrix.client.MatrixClient;
import io.github.ma1uta.matrix.client.api.ContentApi;
import io.github.ma1uta.matrix.client.model.content.ContentUri;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

/**
 * Content methods.
 */
public class ContentMethods {

    private final MatrixClient matrixClient;

    public ContentMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * Upload some content to the content repository.
     *
     * @param inputStream the file content.
     * @param filename    The name of the file being uploaded.
     * @param contentType The content type of the file being uploaded.
     * @return The MXC URI to the uploaded content.
     */
    public String upload(InputStream inputStream, String filename, String contentType) {
        RequestParams params = new RequestParams().queryParam("filename", filename).headerParam("Content-Type", contentType);
        return getMatrixClient().getRequestMethods().post(ContentApi.class, "upload", params, inputStream, ContentUri.class,
            MediaType.MULTIPART_FORM_DATA).getContentUri();
    }

    /**
     * Download content from the content repository.
     *
     * @param serverName  The server name from the mxc:// URI (the authoritory component).
     * @param mediaId     The media ID from the mxc:// URI (the path component).
     * @param allowRemote Indicates to the server that it should not attempt to fetch the media if it is deemed remote.
     *                    This is to prevent routing loops where the server contacts itself. Defaults to true if not provided.
     * @return The content that was previously uploaded.
     */
    public OutputStream download(String serverName, String mediaId, Boolean allowRemote) {
        RequestParams params = new RequestParams().pathParam("serverName", serverName).pathParam("mediaId", mediaId);
        if (allowRemote != null) {
            params.queryParam("allowRemote", Boolean.toString(allowRemote));
        }
        return getMatrixClient().getRequestMethods().get(ContentApi.class, "download", params, OutputStream.class);
    }

    /**
     * Download a thumbnail of the content from the content repository.
     *
     * @param serverName  The server name from the mxc:// URI (the authoritory component).
     * @param mediaId     The media ID from the mxc:// URI (the path component)
     * @param width       The desired width of the thumbnail. The actual thumbnail may not match the size specified.
     * @param height      The desired height of the thumbnail. The actual thumbnail may not match the size specified.
     * @param method      The desired resizing method. One of: ["crop", "scale"].
     * @param allowRemote Indicates to the server that it should not attempt to fetch the media if it is deemed remote.
     *                    This is to prevent routing loops where the server contacts itself. Defaults to true if not provided.
     * @return The content that was previously uploaded.
     */
    public OutputStream thumbnail(String serverName, String mediaId, Long width, Long height, String method, Boolean allowRemote) {
        RequestParams params = new RequestParams().pathParam("serverName", serverName)
            .pathParam("mediaId", mediaId)
            .queryParam("width", Long.toString(width))
            .queryParam("height", Long.toString(height))
            .queryParam("method", method)
            .queryParam("allowRemote", Boolean.toString(allowRemote));
        return getMatrixClient().getRequestMethods().get(ContentApi.class, "thumbnail", params, OutputStream.class);
    }

    /**
     * Get information about a PATH for a client.
     *
     * @param url The PATH to get a preview of.
     * @param ts  The preferred point in time to return a preview for. The server may return a newer version if it does not
     *            have the requested version available.
     * @return The content that was previously uploaded.
     */
    public Map<String, String> previewInfo(String url, String ts) {
        RequestParams params = new RequestParams().queryParam("url", url).queryParam("ts", ts);
        return getMatrixClient().getRequestMethods().get(ContentApi.class, "previewUrl", params, new GenericType<Map<String, String>>() {
        });
    }
}
