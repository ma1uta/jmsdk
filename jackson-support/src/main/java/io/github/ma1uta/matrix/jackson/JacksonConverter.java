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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.ma1uta.matrix.support.Converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Jackson based converter.
 */
public class JacksonConverter extends Converter {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void init() {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    }

    @Override
    public byte[] serialize(Object object) throws IOException {
        return mapper.writeValueAsBytes(object);
    }

    @Override
    public void serialize(Object object, OutputStream target) throws IOException {
        mapper.writeValue(target, object);
    }

    @Override
    protected Map bytesToMap(byte[] source) throws IOException {
        return mapper.readValue(source, Map.class);
    }

    @Override
    protected Map streamToMap(InputStream source) throws IOException {
        return mapper.readValue(source, Map.class);
    }
}
