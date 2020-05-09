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

package io.github.ma1uta.matrix.client;

import java.util.Objects;

/**
 * Matrix content URI.
 */
public class ContentUriModel {

    /**
     * Content URI schema.
     */
    public static final String PREFIX = "mxc://";

    private static final int PREFIX_LENGTH = PREFIX.length();

    /**
     * Delimiter between server and media id.
     */
    public static final String DELIMITER = "/";

    private final String server;
    private final String mediaId;

    public ContentUriModel(String server, String mediaId) {
        Objects.requireNonNull(server, "Server parts must be specified");
        Objects.requireNonNull(mediaId, "Media ID part must be specified");
        this.server = server;
        this.mediaId = mediaId;
    }

    public String getServer() {
        return server;
    }

    public String getMediaId() {
        return mediaId;
    }

    public static ContentUriModel valueOf(String uri) {
        Objects.requireNonNull(uri, "URI must be specified");
        if (!uri.startsWith(PREFIX)) {
            throw new IllegalArgumentException(String.format("%s is not mxc uri, missing schema 'mxc://'", uri));
        }
        int delimiterIndex = uri.indexOf(DELIMITER, PREFIX_LENGTH);
        if (delimiterIndex == -1) {
            throw new IllegalArgumentException(String.format("%s is not mxc uri, unable to split to the server and media id parts", uri));
        }
        return new ContentUriModel(uri.substring(PREFIX_LENGTH, delimiterIndex), uri.substring(delimiterIndex + 1));
    }

    @Override
    public String toString() {
        return PREFIX + server + "/" + mediaId;
    }
}
