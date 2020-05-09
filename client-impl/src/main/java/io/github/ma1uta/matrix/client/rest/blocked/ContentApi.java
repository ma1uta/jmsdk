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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.github.ma1uta.matrix.client.model.content.ContentConfig;
import io.github.ma1uta.matrix.client.model.content.ContentUri;

import java.io.InputStream;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This module allows users to upload content to their homeserver which is retrievable from other homeservers.
 * Its' purpose is to allow users to share attachments in a room. Key locations are represented as Matrix Key (MXC) URIs.
 * They look like:
 * <pre>
 * mxc://(server-name)/(media-id)
 *
 * (server-name) : The name of the homeserver where this content originated, e.g. matrix.org
 * (media-id) : An opaque ID which identifies the content.
 * </pre>
 * Uploads are POSTed to a resource on the user's local homeserver which returns a token which is used to GET the download.
 * Key is downloaded from the recipient's local homeserver, which must first transfer the content from the origin homeserver
 * using the same API (unless the origin and destination homeservers are the same).
 */
@Path("/_matrix/media/r0")
public interface ContentApi {

    /**
     * Upload some content to the content repository.
     * <br>
     * <b>Rate-limited</b>: Yes.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link ContentUri}.
     * <b>Required</b>. The MXC URI to the uploaded content.
     * <p>Status code 200: The MXC URI for the uploaded content.</p>
     * <p>Status code 429: This request was rate-limited.</p>
     *
     * @param inputStream The file content.
     * @param filename    The name of the file being uploaded.
     * @param contentType Mime-type of the content. Uses the non-standard header due some implementations cannot change
     *                    content type header dynamically.
     * @return {@link ContentUri}
     */
    @POST
    @Path("/upload")
    @Consumes(MediaType.WILDCARD)
    ContentUri upload(
        InputStream inputStream,
        @QueryParam("filename") String filename,
        @HeaderParam("X-Content-Type") String contentType
    );

    /**
     * Download content from the content repository.
     * <br>
     * <b>Rate-limited</b>: Yes.
     * <br>
     * Return: {@link InputStream}.
     * <p>Response headers:</p>
     * <table summary="Response header">
     * <tr>
     * <th>Parameter</th>
     * <th>Type</th>
     * <th>Description</th>
     * </tr>
     * <tr>
     * <td>Content-Type</td>
     * <td>string</td>
     * <td>The content type of the file that was previously uploaded.</td>
     * </tr>
     * <tr>
     * <td>Content-Disposition</td>
     * <td>string</td>
     * <td>The name of the file that was previously uploaded, if set.</td>
     * </tr>
     * </table>
     * <p>Status code 200: The content that was previously uploaded.</p>
     * <p>Status code 429: This request was rate-limited.</p>
     *
     * @param serverName  Required. The server name from the mxc:// URI (the authoritory component).
     * @param mediaId     Required. The media ID from the mxc:// URI (the path component).
     * @param allowRemote Indicates to the server that it should not attempt to fetch the media if it is deemed remote.
     *                    This is to prevent routing loops where the server contacts itself. Defaults to true if not provided.
     * @return {@link InputStream}.
     */
    @GET
    @Path("/download/{serverName}/{mediaId}")
    Response download(
        @PathParam("serverName") String serverName,
        @PathParam("mediaId") String mediaId,
        @QueryParam("allow_remote") Boolean allowRemote
    );

    /**
     * Download content from the content repository as a given filename.
     * <br>
     * <b>Rate-limited</b>: Yes.
     * <br>
     * Return: {@link InputStream}.
     * <p>Response headers:</p>
     * <table summary="Response headers">
     * <tr>
     * <th>Parameter</th>
     * <th>Type</th>
     * <th>Description</th>
     * </tr>
     * <tr>
     * <td>Content-Type</td>
     * <td>string</td>
     * <td>The content type of the file that was previously uploaded.</td>
     * </tr>
     * <tr>
     * <td>Content-Disposition</td>
     * <td>string</td>
     * <td>The name of the file that was previously uploaded, if set.</td>
     * </tr>
     * </table>
     * <p>Status code 200: The content that was previously uploaded.</p>
     * <p>Status code 429: This request was rate-limited.</p>
     *
     * @param serverName  Required. The server name from the mxc:// URI (the authoritory component).
     * @param mediaId     Required. The media ID from the mxc:// URI (the path component).
     * @param filename    Required. The filename to give in the Content-Disposition.
     * @param allowRemote Indicates to the server that it should not attempt to fetch the media if it is deemed remote.
     *                    This is to prevent routing loops where the server contacts itself. Defaults to true if not provided.
     * @return {@link InputStream}.
     */
    @GET
    @Path("/download/{serverName}/{mediaId}/{fileName}")
    Response downloadFile(
        @PathParam("serverName") String serverName,
        @PathParam("mediaId") String mediaId,
        @PathParam("fileName") String filename,
        @QueryParam("allow_remote") Boolean allowRemote
    );

    /**
     * Download a thumbnail of the content from the content repository.
     * <br>
     * <b>Rate-limited</b>: Yes.
     * <br>
     * Return: {@link InputStream}.
     * <p>Response headers:</p>
     * <table summary="Response headers">
     * <tr>
     * <th>Parameter</th>
     * <th>Type</th>
     * <th>Description</th>
     * </tr>
     * <tr>
     * <td>Key-Type</td>
     * <td>string</td>
     * <td>The content type of the file that was previously uploaded.</td>
     * </tr>
     * </table>
     * <p>Status code 200: The content that was previously uploaded.</p>
     * <p>Status code 429: This request was rate-limited.</p>
     *
     * @param serverName  Required. The server name from the mxc:// URI (the authoritory component).
     * @param mediaId     Required. The media ID from the mxc:// URI (the path component)
     * @param width       The desired width of the thumbnail. The actual thumbnail may not match the size specified.
     * @param height      The desired height of the thumbnail. The actual thumbnail may not match the size specified.
     * @param method      The desired resizing method. One of: ["crop", "scale"].
     * @param allowRemote Indicates to the server that it should not attempt to fetch the media if it is deemed remote.
     *                    This is to prevent routing loops where the server contacts itself. Defaults to true if not provided.
     * @return {@link InputStream}.
     */
    @GET
    @Path("/thumbnail/{serverName}/{mediaId}")
    Response thumbnail(
        @PathParam("serverName") String serverName,
        @PathParam("mediaId") String mediaId,
        @QueryParam("width") Long width,
        @QueryParam("height") Long height,
        @QueryParam("method") String method,
        @QueryParam("allow_remote") Boolean allowRemote
    );

    /**
     * Get information about a PATH for a client.
     * <br>
     * <b>Rate-limited</b>: Yes.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link Map}.
     * <p>Response headers:</p>
     * <table summary="Response headers">
     * <tr>
     * <th>Parameter</th>
     * <th>Type</th>
     * <th>Description</th>
     * </tr>
     * <tr>
     * <td>matrix:image:size</td>
     * <td>number</td>
     * <td>The byte-size of the image. Omitted if there is no image attached.</td>
     * </tr>
     * <tr>
     * <td>og:image</td>
     * <td>string</td>
     * <td>An MXC URI to the image. Omitted if there is no image.</td>
     * </tr>
     * </table>
     * <p>Status code 200: The content that was previously uploaded.</p>
     * <p>Status code 429: This request was rate-limited.</p>
     *
     * @param url Required. The PATH to get a preview of.
     * @param ts  The preferred point in time to return a preview for. The server may return a newer version if it does not
     *            have the requested version available.
     * @return {@link Map}.
     */
    @GET
    @Path("/preview_url")
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, String> previewUrl(
        @QueryParam("url") String url,
        @QueryParam("ts") String ts
    );

    /**
     * This endpoint allows clients to retrieve the configuration of the content repository, such as upload limitations.
     * Clients SHOULD use this as a guide when using content repository endpoints. All values are intentionally left optional.
     * Clients SHOULD follow the advice given in the field description when the field is not available.
     * <br>
     * NOTE: Both clients and server administrators should be aware that proxies between the client and the server may affect
     * the apparent behaviour of content repository APIs, for example, proxies may enforce a lower upload size limit than is
     * advertised by the server on this endpoint.
     * <br>
     * <b>Rate-limited</b>: Yes.
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link ContentConfig}.
     * <p>Status code 200: The public content repository configuration for the matrix server.</p>
     * <p>Status code 429: This request was rate-limited.</p>
     *
     * @return {@link ContentConfig}.
     */
    @GET
    @Path("/config")
    @Produces(APPLICATION_JSON)
    ContentConfig config();
}
