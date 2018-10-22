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

package io.github.ma1uta.matrix.jackson;

import io.github.ma1uta.matrix.support.RoomEncryptedDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Jackson based the RoomEncrypted deserializer.
 */
public class JacksonRoomEncryptedDeserializer extends RoomEncryptedDeserializer {

    private final JacksonDeserializer deserializer = new JacksonDeserializer();

    @Override
    protected void init() {
        deserializer.init();
    }

    @Override
    protected Map bytesToMap(byte[] bytes) throws IOException {
        return deserializer.bytesToMap(bytes);
    }

    @Override
    protected Map streamToMap(InputStream inputStream) throws IOException {
        return deserializer.streamToMap(inputStream);
    }
}
