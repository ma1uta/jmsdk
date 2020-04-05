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

import io.github.ma1uta.matrix.impl.Deserializer;
import io.github.ma1uta.matrix.impl.RestClientBuilderConfigurer;
import io.github.ma1uta.matrix.support.jackson.JacksonDeserializer;
import io.github.ma1uta.matrix.support.jackson.JacksonRestClientBuilderConfigurer;
import io.github.ma1uta.matrix.support.jackson.ObjectMapperProvider;

module matrix.support.jackson {
    uses ObjectMapperProvider;
    exports io.github.ma1uta.matrix.support.jackson;

    requires transitive matrix.common.api;
    requires transitive matrix.common.impl;
    requires transitive com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive com.fasterxml.jackson.jaxrs.json;
    requires org.slf4j;

    provides Deserializer with JacksonDeserializer;
    provides RestClientBuilderConfigurer with JacksonRestClientBuilderConfigurer;
}
