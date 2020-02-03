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

package io.github.ma1uta.matrix.support.jsonb;

import io.github.ma1uta.matrix.event.content.EventContent;
import io.github.ma1uta.matrix.impl.Deserializer;
import io.github.ma1uta.matrix.support.jsonb.mapper.EventMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;

/**
 * Matrix Jsonb-based deserializer.
 */
public class JsonbDeserializer implements Deserializer {

    private final Jsonb jsonb = JsonbProvider.getInstance().get();

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        return jsonb.fromJson(new ByteArrayInputStream(bytes), clazz);
    }

    @Override
    public EventContent deserializeEventContent(byte[] bytes, String eventType) throws IOException {
        return EventMapper.INSTANCE.deserializeEventContent(jsonb.fromJson(new ByteArrayInputStream(bytes), JsonObject.class), eventType);
    }
}
