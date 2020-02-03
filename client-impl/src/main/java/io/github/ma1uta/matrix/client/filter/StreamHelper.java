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

package io.github.ma1uta.matrix.client.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Helper to work with streams.
 * <br>
 * Obsolete for java 9+.
 */
public final class StreamHelper {

    private static final int BUFFER_LENGTH = 4096;

    private StreamHelper() {
        // singleton
    }

    /**
     * Read stream to byte array.
     *
     * @param inputStream stream to read.
     * @return stream content.
     * @throws IOException when unable to read from stream.
     */
    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_LENGTH];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            stream.write(buffer, 0, read);
        }
        return stream.toByteArray();
    }
}
