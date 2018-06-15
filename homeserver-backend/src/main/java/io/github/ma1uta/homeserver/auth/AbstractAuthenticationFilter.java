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

package io.github.ma1uta.homeserver.auth;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Skeleton for the standard servlet filter to provide authentication.
 */
public abstract class AbstractAuthenticationFilter implements Filter {

    /**
     * Marker to bypass filtering.
     */
    public static final String APPLIED = AbstractAuthenticationFilter.class.getCanonicalName() + ".APPLIED";

    /**
     * Pattern for bearer header.
     */
    public static final Pattern BEARER = Pattern.compile("^\\s*Bearer\\s+(\\w+)\\s*$");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Boolean applied = (Boolean) request.getAttribute(APPLIED);
        if (applied != null && applied) {
            chain.doFilter(request, response);
            return;
        }

        try {
            request.setAttribute(APPLIED, true);

            String accessToken = null;
            if (request instanceof HttpServletRequest) {
                String header = ((HttpServletRequest) request).getHeader("Authentication");
                if (header != null) {
                    Matcher matcher = BEARER.matcher(header);
                    if (matcher.matches()) {
                        accessToken = matcher.group(1);
                    }
                }
            }

            if (accessToken == null) {
                request.getParameter("access_token");
            }

            if (StringUtils.isNotBlank(accessToken)) {
                // check token
                System.out.print(accessToken);
            }
        } finally {
            request.removeAttribute(APPLIED);
        }
    }
}
