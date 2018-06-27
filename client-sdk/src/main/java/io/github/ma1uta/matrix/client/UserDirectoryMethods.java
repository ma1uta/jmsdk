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

import io.github.ma1uta.matrix.client.api.UserDirectory;
import io.github.ma1uta.matrix.client.model.userdirectory.SearchRequest;
import io.github.ma1uta.matrix.client.model.userdirectory.SearchResponse;

/**
 * User directory methods.
 */
public class UserDirectoryMethods {

    private final MatrixClient matrixClient;

    UserDirectoryMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * This API performs a server-side search over all users registered on the server. It searches user ID and displayname
     * case-insensitively for users that you share a room with or that are in public rooms.
     *
     * @param request search request.
     * @return the result of the search.
     */
    public SearchResponse search(SearchRequest request) {
        return getMatrixClient().getRequestMethods()
            .post(UserDirectory.class, "searchUsers", new RequestParams(), request, SearchResponse.class);
    }
}
