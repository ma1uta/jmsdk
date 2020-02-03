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

package io.github.ma1uta.matrix.impl;

import io.github.ma1uta.matrix.event.content.EventContent;

import java.io.IOException;

/**
 * Deserializer.
 */
public interface Deserializer {

    /**
     * Deserialize bytes to class.
     *
     * @param bytes instance content.
     * @param clazz class name.
     * @param <T>   class type.
     * @return deserialized object.
     * @throws IOException when unable to deserialize object.
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException;

    /**
     * Deserialize bytes to {@link io.github.ma1uta.matrix.event.content.EventContent}.
     *
     * @param bytes     instance content.
     * @param eventType event type.
     * @return event content.
     * @throws IOException when unable to deserialize object.
     */
    EventContent deserializeEventContent(byte[] bytes, String eventType) throws IOException;
}
