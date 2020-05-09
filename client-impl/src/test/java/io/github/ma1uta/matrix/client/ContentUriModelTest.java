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

package io.github.ma1uta.matrix.client;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ContentUriModelTest {

    @Test
    public void parseTest() {
        ContentUriModel uriModel = ContentUriModel.valueOf("mxc://ru-matrix.org/123");

        assertNotNull(uriModel);
        assertEquals("ru-matrix.org", uriModel.getServer());
        assertEquals("123", uriModel.getMediaId());
    }

    @Test
    public void toStringTest() {
        ContentUriModel model = new ContentUriModel("ru-matrix.org", "123");
        assertEquals("mxc://ru-matrix.org/123", model.toString());
    }
}
