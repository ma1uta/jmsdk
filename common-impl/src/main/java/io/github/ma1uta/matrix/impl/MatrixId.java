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

import io.github.ma1uta.matrix.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matrix id (MXID) util class.
 */
public class MatrixId extends Id {

    private static final Logger LOGGER = LoggerFactory.getLogger(Id.class);

    /**
     * MXID pattern.
     */
    public static final Pattern PATTERN = Pattern.compile("^[@!$#+]([^:]+):(.+)$");

    /**
     * User localpart pattern.
     */
    public static final Pattern USER = Pattern.compile("[a-z0-9._=\\-/]+");

    /**
     * Domain pattern.
     */
    public static final Pattern DOMAIN = Pattern
        .compile("(\\d{3}.\\d{3}.\\d{3}.\\d{3}|\\[[0-9\\p{Alpha}:]{0,39}]|[0-9\\-.\\p{Alpha}]{1,255})(:[\\d]+)?");

    @Override
    protected Matcher validate(String id) {
        if (id == null || id.trim().isEmpty()) {
            String message = "Empty id";
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }

        Matcher matcher = PATTERN.matcher(id.trim());
        if (!matcher.matches()) {
            String message = String.format("Invalid id: '%s'", id);
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }

        String localpart = matcher.group(1);
        String domain = matcher.group(2);
        LOGGER.trace("localpart: '%s', domain: '%s'", localpart, domain);

        Matcher domainMatcher = DOMAIN.matcher(domain);
        if (!domainMatcher.matches()) {
            String message = String.format("Invalid domain part: '%s'", domain);
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }
        return matcher;
    }

    @Override
    protected boolean userMatchers(Matcher matcher) {
        return USER.matcher(matcher.group(1)).matches();
    }

    @Override
    protected boolean eventMatchers(Matcher matcher) {
        return true;
    }

    @Override
    protected boolean roomMatchers(Matcher matcher) {
        return true;
    }

    @Override
    protected boolean aliasMatchers(Matcher matcher) {
        return true;
    }

    @Override
    protected boolean groupMatchers(Matcher matcher) {
        return true;
    }
}
