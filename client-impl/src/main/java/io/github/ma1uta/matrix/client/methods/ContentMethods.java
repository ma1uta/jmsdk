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

package io.github.ma1uta.matrix.client.methods;

import io.github.ma1uta.matrix.client.model.content.ContentConfig;
import io.github.ma1uta.matrix.client.model.content.ContentUri;
import io.github.ma1uta.matrix.client.rest.ContentApi;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Content methods.
 */
public class ContentMethods {

    private final ContentApi contentApi;

    public ContentMethods(RestClientBuilder restClientBuilder) {
        this.contentApi = restClientBuilder.build(ContentApi.class);
    }

    /**
     * Upload some content to the content repository.
     *
     * @param inputStream The file content.
     * @param filename    The name of the file being uploaded.
     * @param contentType Mime-type.
     * @return The MXC URI to the uploaded content.
     */
    public CompletableFuture<ContentUri> upload(InputStream inputStream, String filename, String contentType) {
        return contentApi.upload(inputStream, filename, contentType).toCompletableFuture();
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
    public CompletableFuture<InputStream> download(String serverName, String mediaId, Boolean allowRemote) {
        Objects.requireNonNull(serverName, "ServerName cannot be empty.");
        Objects.requireNonNull(mediaId, "MediaId cannot be empty.");

        return contentApi.download(serverName, mediaId, allowRemote).toCompletableFuture();
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
    public CompletableFuture<InputStream> thumbnail(String serverName, String mediaId, Long width, Long height, String method,
                                                    Boolean allowRemote) {
        Objects.requireNonNull(serverName, "ServerName cannot be empty.");
        Objects.requireNonNull(mediaId, "MediaId cannot be empty.");

        return contentApi.thumbnail(serverName, mediaId, width, height, method, allowRemote).toCompletableFuture();
    }

    /**
     * Get information about a PATH for a client.
     *
     * @param url The PATH to get a preview of.
     * @param ts  The preferred point in time to return a preview for. The server may return a newer version if it does not
     *            have the requested version available.
     * @return The content that was previously uploaded.
     */
    public CompletableFuture<Map<String, String>> previewInfo(String url, String ts) {
        Objects.requireNonNull(url, "Url cannot be empty.");

        return contentApi.previewUrl(url, ts).toCompletableFuture();
    }

    /**
     * Get supported upload size.
     *
     * @return supported upload size.
     */
    public CompletableFuture<ContentConfig> getUploadSize() {
        return contentApi.config().toCompletableFuture();
    }
}
