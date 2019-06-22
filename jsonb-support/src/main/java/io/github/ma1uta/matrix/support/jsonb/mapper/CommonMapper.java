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

package io.github.ma1uta.matrix.support.jsonb.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

@SuppressWarnings( {"javadocType", "javadocMethod", "javadocVariable"})
public interface CommonMapper {

    Object EMPTY = new Object();

    default boolean isNull(JsonValue jsonValue) {
        return jsonValue == null || jsonValue.getValueType() == JsonValue.ValueType.NULL;
    }

    default boolean isNull(JsonObject jsonObject, String property) {
        return isNull(jsonObject)
            || jsonObject.get(property) == null
            || jsonObject.isNull(property);
    }

    default String toString(JsonObject jsonObject, String property) {
        if (isNull(jsonObject, property)) {
            return null;
        }

        return jsonObject.getString(property);
    }

    default String toString(JsonValue jsonValue) {
        if (isNull(jsonValue)) {
            return null;
        }
        return ((JsonString) jsonValue).getString();
    }

    default Long toLong(JsonObject jsonObject, String property) {
        if (isNull(jsonObject, property)) {
            return null;
        }

        return jsonObject.getJsonNumber(property).longValue();
    }

    default Long toLong(JsonValue jsonValue) {
        if (isNull(jsonValue)) {
            return null;
        }

        return ((JsonNumber) jsonValue).longValue();
    }

    default Boolean toBoolean(JsonObject jsonObject, String property) {
        if (isNull(jsonObject, property)) {
            return null;
        }

        return jsonObject.getBoolean(property) ? Boolean.TRUE : Boolean.FALSE;
    }

    default JsonObject toObject(JsonValue jsonValue) {
        if (isNull(jsonValue)) {
            return null;
        }
        return jsonValue.asJsonObject();
    }

    default List<String> toStringArray(JsonObject jsonObject, String property) {
        if (isNull(jsonObject, property)) {
            return null;
        }
        return jsonObject.getJsonArray(property).stream().filter(Objects::nonNull).map(this::toString).collect(Collectors.toList());
    }

    default List<String> toStringArray(JsonArray jsonArray) {
        if (isNull(jsonArray)) {
            return null;
        }
        return jsonArray.stream().filter(Objects::nonNull).map(this::toString).collect(Collectors.toList());
    }

    default Map<String, String> toStringMap(JsonObject jsonObject) {
        if (isNull(jsonObject)) {
            return null;
        }

        Map<String, String> map = new HashMap<>();

        for (Map.Entry<String, JsonValue> entry : jsonObject.entrySet()) {
            map.put(entry.getKey(), toString(entry.getValue()));
        }

        return map;
    }

    default Map<String, String> toStringMap(JsonObject jsonObject, String property) {
        if (isNull(jsonObject, property)) {
            return null;
        }

        return toStringMap(jsonObject.getJsonObject(property));
    }
}
