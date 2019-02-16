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

import java.util.ArrayList;
import java.util.List;

/**
 * Matrix id (MXID) util class.
 */
public class MatrixIdParser extends IdParser {

    /**
     * Max length of the dns name.
     */
    public static final int DNS_NAME_LENGTH = 255;

    /**
     * Max length of the ipv6 address.
     */
    public static final int IPV6_LENGTH = 45;

    /**
     * Count of the ipv4 parts.
     */
    public static final int IPV4_PART_COUNT = 4;

    /**
     * Max length of the ipv4 parts.
     */
    public static final int IPV4_LENGTH = 3;

    @Override
    public Id parse(String mxid) {
        if (mxid == null) {
            return new IllegalId(" ", "mxid cannot be null.");
        }

        if (isBlank(mxid)) {
            return new IllegalId(" ", "mxid cannot be blank.");
        }

        char sigil = mxid.charAt(0);
        if (EventId.SIGIL == sigil) {
            return new EventId(mxid);
        }

        int colon = mxid.indexOf(":");
        if (colon == -1) {
            return new IllegalId(mxid, "Missing colon. MXID must follow pattern: <sigil><localpart>:<serverName>");
        }

        String localpart = mxid.substring(1, colon);
        String serverName = mxid.substring(colon + 1);
        String hostname = serverName;
        int port = -1;
        List<Id.IdParseException> errors = new ArrayList<>();

        if (serverName.charAt(serverName.length() - 1) != ']') {
            int portIndex = serverName.lastIndexOf(":");
            if (portIndex != -1) {
                try {
                    port = Integer.parseInt(serverName.substring(portIndex));
                    hostname = serverName.substring(0, portIndex);
                } catch (NumberFormatException e) {
                    errors.add(new Id.IdParseException("Port must contains only digits."));
                }
            }
        }

        validateHostname(hostname, errors);
        Id id;
        switch (sigil) {
            case UserId.SIGIL:
                validateLocalpart(localpart, errors);
                id = new UserId(localpart, hostname, port);
                break;
            case RoomId.SIGIL:
                id = new RoomId(localpart, hostname, port);
                break;
            case AliasId.SIGIL:
                id = new AliasId(localpart, hostname, port);
                break;
            case GroupId.SIGIL:
                validateLocalpart(localpart, errors);
                id = new GroupId(localpart, hostname, port);
                break;
            default:
                id = new UnknownId(mxid);
        }
        if (!errors.isEmpty()) {
            id.errors(errors);
        }
        return id;
    }

    private boolean isBlank(String mxid) {
        boolean blank = true;
        for (char c : mxid.toCharArray()) {
            if (!Character.isWhitespace(c)) {
                blank = false;
                break;
            }
        }
        return blank;
    }


    protected void validateLocalpart(String localpart, List<Id.IdParseException> errors) {
        String lowerCase = localpart.toLowerCase();
        for (int i = 0; i < lowerCase.length(); i++) {
            char ch = localpart.charAt(i);
            if (!((ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '.' || ch == '_' || ch == '-' || ch == '=')) {
                errors.add(new Id.IdParseException("Char " + ch + " should be from the set: [a-z0-9\\._-=]"));
            }
        }
    }

    protected void validateHostname(String hostname, List<Id.IdParseException> errors) {
        if (validateIpv4Address(hostname, errors) || validateIpv6Address(hostname, errors)) {
            return;
        }

        validateDnsName(hostname, errors);
    }

    protected boolean validateIpv4Address(String ipv4Address, List<Id.IdParseException> errors) {
        String[] ipv4parts = ipv4Address.split("\\.");

        if (ipv4parts.length != IPV4_PART_COUNT) {
            return false;
        }

        for (int i = 0; i < IPV4_PART_COUNT; i++) {
            if (!validateIpv4Part(ipv4parts[i].toCharArray(), errors)) {
                return false;
            }
        }
        return true;
    }

    protected boolean validateIpv4Part(char[] ipv4part, List<Id.IdParseException> errors) {
        if (ipv4part.length > IPV4_LENGTH) {
            errors.add(new Id.IdParseException("Length of the ipv4 part is longer than " + IPV4_LENGTH + " symbols."));
        }
        for (char ipv4char : ipv4part) {
            if (!Character.isDigit(ipv4char)) {
                return false;
            }
        }
        return true;
    }

    protected boolean validateIpv6Address(String ipv6Address, List<Id.IdParseException> errors) {
        if (ipv6Address.charAt(0) != '[' || ipv6Address.charAt(ipv6Address.length() - 1) != ']') {
            return false;
        }

        if (ipv6Address.length() < 2) {
            return false;
        }

        if (ipv6Address.length() > IPV6_LENGTH) {
            return false;
        }

        for (int i = 0; i < ipv6Address.length(); i++) {
            if (!validateIpv6Char(ipv6Address.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    protected boolean validateIpv6Char(char ipv6char) {
        return Character.isDigit(ipv6char)
            || (ipv6char >= 'a' && ipv6char <= 'f')
            || (ipv6char >= 'A' && ipv6char <= 'F')
            || ipv6char == ':' || ipv6char == '.';
    }

    protected void validateDnsName(String dnsName, List<Id.IdParseException> errors) {
        if (dnsName.length() > DNS_NAME_LENGTH) {
            errors.add(new Id.IdParseException("Dns name must be shorter than " + DNS_NAME_LENGTH + " symbols"));
        }

        for (int i = 0; i < dnsName.length(); i++) {
            validateDnsChar(dnsName.charAt(i), errors);
        }
    }

    protected void validateDnsChar(char dnsChar, List<Id.IdParseException> errors) {
        if (!(Character.isDigit(dnsChar) || Character.isAlphabetic(dnsChar) || dnsChar == '-' || dnsChar == '.')) {
            errors.add(new Id.IdParseException("Char '" + dnsChar + "' must be a digit, alphabetic, '-' or '.'"));
        }
    }
}
