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

package io.github.ma1uta.matrix.support.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ma1uta.matrix.event.content.EventContent;
import io.github.ma1uta.matrix.impl.Deserializer;

import java.io.IOException;

/**
 * Matrix Jackson-based deserializer.
 */
public class JacksonDeserializer implements Deserializer {

    private final ObjectMapper mapper = ObjectMapperProvider.getInstance().get();
    private final EventContentDeserializer eventContentDeserializer = new EventContentDeserializer();

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        return mapper.readValue(bytes, clazz);
    }

    @Override
    public EventContent deserializeEventContent(byte[] bytes, String eventType) throws IOException {
        return eventContentDeserializer.deserialize(bytes, eventType, mapper);
    }
}
