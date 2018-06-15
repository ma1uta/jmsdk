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

package io.github.ma1uta.identity.api;

import io.github.ma1uta.identity.service.SessionService;
import io.github.ma1uta.jeon.exception.MatrixException;
import io.github.ma1uta.matrix.ErrorResponse;
import io.github.ma1uta.matrix.identity.api.ValidationApi;
import io.github.ma1uta.matrix.identity.model.validation.PublishResponse;
import io.github.ma1uta.matrix.identity.model.validation.ValidationResponse;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementation of the {@link ValidationApi}.
 */
public class Validation implements ValidationApi {

    private final SessionService sessionService;

    public Validation(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    @Override
    public ValidationResponse validate(String sid, String clientSecret, HttpServletRequest servletRequest,
                                       HttpServletResponse servletResponse) {
        if (StringUtils.isAnyBlank(sid, clientSecret)) {
            throw new MatrixException(ErrorResponse.Code.M_BAD_JSON, "Sid or client secret are missing.");
        }
        ValidationResponse response = new ValidationResponse();
        io.github.ma1uta.identity.model.Session session = getSessionService().getSession(sid, clientSecret);
        response.setMedium(session.getMedium());
        response.setAddress(session.getAddress());
        ZoneOffset offset = ZoneOffset.systemDefault().getRules().getOffset(LocalDateTime.now());
        response.setValidatedAt(session.getValidated().toEpochSecond(offset));
        return response;
    }

    @Override
    public PublishResponse publish(String sid, String clientSecret, String mxid, HttpServletRequest servletRequest,
                                   HttpServletResponse servletResponse) {
        if (StringUtils.isAnyBlank(sid, clientSecret, mxid)) {
            throw new MatrixException(ErrorResponse.Code.M_BAD_JSON, "Sid, client secret or mxid are missing.");
        }
        PublishResponse response = new PublishResponse();
        response.setPublished(getSessionService().publish(sid, clientSecret, mxid));
        return response;
    }
}
