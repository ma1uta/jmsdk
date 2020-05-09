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

import io.github.ma1uta.matrix.client.ContentUriModel;
import io.github.ma1uta.matrix.client.model.content.ContentConfig;
import io.github.ma1uta.matrix.client.model.content.ContentUri;
import io.github.ma1uta.matrix.client.rest.blocked.ContentApi;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * Content methods.
 */
public class ContentMethods {

    private final ContentApi contentApi;
    private static final Pattern CONTENT_DISPOSITION = Pattern.compile("filename=\"?(.*)\"?");

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
    public ContentUri upload(InputStream inputStream, String filename, String contentType) {
        return contentApi.upload(inputStream, filename, contentType);
    }

    /**
     * Download content from the content repository.
     *
     * @param contentUri  Content URI.
     * @param allowRemote Indicates to the server that it should not attempt to fetch the media if it is deemed remote.
     *                    This is to prevent routing loops where the server contacts itself. Defaults to true if not provided.
     * @return The content that was previously uploaded.
     */
    public Content download(String contentUri, Boolean allowRemote) {
        Objects.requireNonNull(contentUri, "ContentUri cannot be empty.");

        ContentUriModel uriModel = ContentUriModel.valueOf(contentUri);
        return download(uriModel.getServer(), uriModel.getMediaId(), allowRemote);
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
    public Content download(String serverName, String mediaId, Boolean allowRemote) {
        Objects.requireNonNull(serverName, "ServerName cannot be empty.");
        Objects.requireNonNull(mediaId, "MediaId cannot be empty.");

        return toContent(contentApi.download(serverName, mediaId, allowRemote));
    }

    /**
     * Download content from the content repository.
     *
     * @param contentUri  Content URI.
     * @param allowRemote Indicates to the server that it should not attempt to fetch the media if it is deemed remote.
     *                    This is to prevent routing loops where the server contacts itself. Defaults to true if not provided.
     * @param filename    Required. The filename to give in the Content-Disposition.
     * @return The content that was previously uploaded.
     */
    public Content downloadFile(String contentUri, String filename, Boolean allowRemote) {
        Objects.requireNonNull(contentUri, "ContentUri cannot be empty.");

        ContentUriModel uriModel = ContentUriModel.valueOf(contentUri);
        return downloadFile(uriModel.getServer(), uriModel.getMediaId(), filename, allowRemote);
    }

    /**
     * Download content from the content repository.
     *
     * @param serverName  The server name from the mxc:// URI (the authoritory component).
     * @param mediaId     The media ID from the mxc:// URI (the path component).
     * @param allowRemote Indicates to the server that it should not attempt to fetch the media if it is deemed remote.
     *                    This is to prevent routing loops where the server contacts itself. Defaults to true if not provided.
     * @param filename    Required. The filename to give in the Content-Disposition.
     * @return The content that was previously uploaded.
     */
    public Content downloadFile(String serverName, String mediaId, String filename, Boolean allowRemote) {
        Objects.requireNonNull(serverName, "ServerName cannot be empty.");
        Objects.requireNonNull(mediaId, "MediaId cannot be empty.");

        return toContent(contentApi.downloadFile(serverName, mediaId, filename, allowRemote));
    }

    /**
     * Download a thumbnail of the content from the content repository.
     *
     * @param contentUri  Content URI.
     * @param width       The desired width of the thumbnail. The actual thumbnail may not match the size specified.
     * @param height      The desired height of the thumbnail. The actual thumbnail may not match the size specified.
     * @param method      The desired resizing method. One of: ["crop", "scale"].
     * @param allowRemote Indicates to the server that it should not attempt to fetch the media if it is deemed remote.
     *                    This is to prevent routing loops where the server contacts itself. Defaults to true if not provided.
     * @return The content that was previously uploaded.
     */
    public Thumbnail thumbnail(String contentUri, Long width, Long height, String method,
                               Boolean allowRemote) {
        Objects.requireNonNull(contentUri, "ContentUri cannot be empty.");

        ContentUriModel uriModel = ContentUriModel.valueOf(contentUri);
        return toThumbnail(contentApi.thumbnail(uriModel.getServer(), uriModel.getMediaId(), width, height, method, allowRemote));
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
    public Thumbnail thumbnail(String serverName, String mediaId, Long width, Long height, String method,
                               Boolean allowRemote) {
        Objects.requireNonNull(serverName, "ServerName cannot be empty.");
        Objects.requireNonNull(mediaId, "MediaId cannot be empty.");

        return toThumbnail(contentApi.thumbnail(serverName, mediaId, width, height, method, allowRemote));
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
        Objects.requireNonNull(url, "Url cannot be empty.");

        return contentApi.previewUrl(url, ts);
    }

    /**
     * Get supported upload size.
     *
     * @return supported upload size.
     */
    public ContentConfig getUploadSize() {
        return contentApi.config();
    }

    public static Content toContent(Response response) {
        String contentDisposition = response.getHeaderString(HttpHeaders.CONTENT_DISPOSITION);
        Matcher matcher = CONTENT_DISPOSITION.matcher(contentDisposition);
        String filename;
        if (matcher.find()) {
            filename = matcher.group(1);
        } else {
            filename = contentDisposition;
        }
        String contentType = response.getHeaderString(HttpHeaders.CONTENT_TYPE);
        return new Content(response.readEntity(InputStream.class), filename, contentType);
    }

    public static Thumbnail toThumbnail(Response response) {
        String contentType = response.getHeaderString(HttpHeaders.CONTENT_TYPE);
        return new Thumbnail(response.readEntity(InputStream.class), contentType);
    }

    public static class Content {

        private final InputStream inputStream;
        private final String filename;
        private final String contentType;

        public Content(InputStream inputStream, String filename, String contentType) {
            this.inputStream = inputStream;
            this.filename = filename;
            this.contentType = contentType;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public String getFilename() {
            return filename;
        }

        public String getContentType() {
            return contentType;
        }
    }

    public static class Thumbnail {

        private final InputStream inputStream;
        private final String contentType;

        public Thumbnail(InputStream inputStream, String contentType) {
            this.inputStream = inputStream;
            this.contentType = contentType;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public String getContentType() {
            return contentType;
        }
    }
}
