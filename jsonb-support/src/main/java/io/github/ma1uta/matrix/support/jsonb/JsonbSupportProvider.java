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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.json.bind.Jsonb;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * Jsonb support provider.
 */
@Provider
@Consumes( {"application/json", "application/*+json", "text/json"})
@Produces( {"application/json", "application/*+json", "text/json"})
public class JsonbSupportProvider implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

    private final Jsonb jsonb = JsonbProvider.getInstance().get();

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isMatchingMediaType(mediaType);
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException,
        WebApplicationException {
        return jsonb.fromJson(entityStream, type);
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return isMatchingMediaType(mediaType);
    }

    @Override
    public void writeTo(Object o, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        jsonb.toJson(o, entityStream);
    }

    private boolean isMatchingMediaType(MediaType mediaType) {
        if (mediaType == null) {
            return true;
        }

        String subtype = mediaType.getSubtype();
        return "json".equalsIgnoreCase(subtype)
            || subtype.endsWith("+json")
            || "javascript".equals(subtype)
            || "x-javascript".equals(subtype)
            || "x-json".equals(subtype);
    }
}
