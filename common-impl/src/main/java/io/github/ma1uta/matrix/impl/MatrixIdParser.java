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

package io.github.ma1uta.matrix.impl;

import io.github.ma1uta.matrix.AliasId;
import io.github.ma1uta.matrix.EventId;
import io.github.ma1uta.matrix.GroupId;
import io.github.ma1uta.matrix.Id;
import io.github.ma1uta.matrix.IdParser;
import io.github.ma1uta.matrix.IllegalId;
import io.github.ma1uta.matrix.RoomId;
import io.github.ma1uta.matrix.UnknownId;
import io.github.ma1uta.matrix.UserId;

/**
 * Matrix id (MXID) util class.
 */
public class MatrixIdParser extends IdParser {

    @Override
    public Id parse(String mxid) {
        if (mxid == null) {
            return new IllegalId("", "mxid cannot be null.");
        }

        Id illegalId = isBlank(mxid);
        if (illegalId != null) {
            return illegalId;
        }

        int colon = mxid.indexOf(":");
        if (colon == -1) {
            return new IllegalId(mxid, "Missing colon. MXID must follow pattern: <sigil><localpart>:<serverName>");
        }

        char sigil = mxid.charAt(0);
        String localpart = mxid.substring(1, colon);
        String serverName = mxid.substring(colon + 1);

        switch (sigil) {
            case UserId.SIGIL:
                return new UserId(localpart, serverName);
            case EventId.SIGIL:
                return new EventId(localpart, serverName);
            case RoomId.SIGIL:
                return new RoomId(localpart, serverName);
            case AliasId.SIGIL:
                return new AliasId(localpart, serverName);
            case GroupId.SIGIL:
                return new GroupId(localpart, serverName);
            default:
                return new UnknownId(sigil, localpart, serverName);
        }
    }

    private Id isBlank(String mxid) {
        boolean blank = true;
        for (char c : mxid.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                blank = false;
                break;
            }
        }

        return blank ? new IllegalId(mxid, "mxid cannot be blank.") : null;
    }
}
