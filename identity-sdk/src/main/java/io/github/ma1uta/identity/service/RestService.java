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

package io.github.ma1uta.identity.service;

/**
 * Rest client to send requests.
 */
public interface RestService {

    /**
     * Send post request.
     *
     * @param url     request url.
     * @param request request data
     * @param <Q>     type of the request.
     * @param <P>     type of the response.
     * @return response.
     */
    <Q, P> P post(String url, Q request);
}
