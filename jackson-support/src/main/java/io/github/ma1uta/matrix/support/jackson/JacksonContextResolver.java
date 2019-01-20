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

package io.github.ma1uta.matrix.support.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.ma1uta.matrix.Id;
import io.github.ma1uta.matrix.event.Event;
import io.github.ma1uta.matrix.event.content.RoomEncryptedContent;
import io.github.ma1uta.matrix.event.content.RoomMessageContent;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Provides Jackson ObjectMapper with custom deserializers.
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonContextResolver implements ContextResolver<ObjectMapper> {

    private ObjectMapper mapper;

    @Override
    public ObjectMapper getContext(Class<?> type) {
        if (mapper == null) {
            synchronized (JacksonContextResolver.class) {
                if (mapper == null) {
                    mapper = new ObjectMapper();
                    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
                    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

                    SimpleModule eventModule = new SimpleModule("Jackson Matrix Module");
                    eventModule.addDeserializer(Event.class, new EventDeserializer());
                    eventModule.addDeserializer(RoomEncryptedContent.class, new RoomEncryptedContentDeserializer());
                    eventModule.addDeserializer(RoomMessageContent.class, new RoomMessageContentDeserializer());
                    eventModule.addDeserializer(Id.class, new IdDeserializer());
                    eventModule.addSerializer(Id.class, new IdSerializer());

                    mapper.registerModule(eventModule);
                }
            }
        }
        return mapper;
    }
}
