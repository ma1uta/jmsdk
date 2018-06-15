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

package io.github.ma1uta.identity.service.impl;

import static io.github.ma1uta.jeon.exception.MatrixException.M_INTERNAL;

import io.github.ma1uta.identity.configuration.SessionServiceConfiguration;
import io.github.ma1uta.identity.dao.SessionDao;
import io.github.ma1uta.identity.model.Session;
import io.github.ma1uta.identity.service.AssociationService;
import io.github.ma1uta.identity.service.InvitationService;
import io.github.ma1uta.identity.service.NotificationService;
import io.github.ma1uta.identity.service.SessionService;
import io.github.ma1uta.jeon.exception.MatrixException;
import io.github.ma1uta.matrix.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * Default implementation.
 * <p/>
 * There are default implementation for all methods of the {@link SessionService}.
 * <p/>
 * You can create you own class for example with annotation @Transactional (jpa) or another and invoke the same method
 * with suffix "Internal".
 * <pre>
 * {@code
 *     @literal @Service
 *     public class MySessionService extends AbstractSessionService {
 *         ...
 *         @literal @Override
 *         @literal @Transactional
 *         @literal @MyFavouriteAnnotation
 *         public String create(String clientSecret, String email, Long sendAttempt, String nextLink) {
 *             // wrap next link to transaction via annotation or code.
 *             SessionDao dao = ...
 *             return super.createInternal(clientSecret, email, sendAttempt, nextLink, dao);
 *         }
 *         ...
 *     }
 * }
 * </pre>
 */
public abstract class AbstractSessionService implements SessionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSessionService.class);

    private final NotificationService notificationService;
    private final AssociationService associationService;
    private final InvitationService invitationService;
    private final SessionServiceConfiguration configuration;

    public AbstractSessionService(NotificationService notificationService, AssociationService associationService,
                                  InvitationService invitationService,
                                  SessionServiceConfiguration configuration) {
        this.notificationService = notificationService;
        this.associationService = associationService;
        this.invitationService = invitationService;
        this.configuration = configuration;
    }

    protected NotificationService getNotificationService() {
        return notificationService;
    }

    protected SessionServiceConfiguration getConfiguration() {
        return configuration;
    }

    protected AssociationService getAssociationService() {
        return associationService;
    }

    protected InvitationService getInvitationService() {
        return invitationService;
    }

    /**
     * Default implementation.
     * <p/>
     * {@link SessionService#create}.
     */
    protected String createInternal(String clientSecret, String address, String medium, Long sendAttempt, String nextLink, SessionDao dao) {
        boolean create = true;
        if (sendAttempt != null) {
            create = dao.findBySecretEmail(clientSecret, address, medium).stream()
                .anyMatch(s -> s.getSendAttempt() == null || s.getSendAttempt() < sendAttempt);
        }

        if (create) {
            String sid = UUID.randomUUID().toString();
            String token = UUID.randomUUID().toString();
            dao.insertOrUpdate(sid, token, clientSecret, medium, address, sendAttempt != null ? Long.toString(sendAttempt) : null,
                nextLink);

            getNotificationService().send(medium, address, clientSecret, token, sid);
            return sid;
        } else {
            return "";
        }
    }

    /**
     * Default implementation.
     * <p/>
     * {@link SessionService#validate}
     */
    protected String validateInternal(String token, String clientSecret, String sid, SessionDao dao) {
        List<Session> sessionList = dao.findBySecretSidToken(clientSecret, sid, token);
        switch (sessionList.size()) {
            case 0:
                throw new MatrixException(ErrorResponse.Code.M_THREEPID_NOT_FOUND, "Not found 3pid for this email.");
            case 1:
                dao.validate(sid);
                return sessionList.get(0).getNextLink();
            default:
                throw new MatrixException(M_INTERNAL, "Too many sessions with the same id.");
        }
    }

    /**
     * Default implementation.
     * <p/>
     * {@link SessionService#getSession}
     */
    protected Session getSessionInternal(String sid, String clientSecret, SessionDao dao) {
        List<Session> sessionList = dao.findBySecretSid(clientSecret, sid);
        switch (sessionList.size()) {
            case 0:
                throw new MatrixException(ErrorResponse.Code.M_SESSION_NOT_VALIDATED,
                    "This validation session has not yet been completed.");
            case 1:
                return sessionList.get(0);
            default:
                throw new MatrixException(ErrorResponse.Code.M_SESSION_NOT_VALIDATED, "Too many sessions.");
        }
    }

    /**
     * Default implementation.
     * <p/>
     * {@link SessionService#publish}
     */
    protected boolean publishInternal(String sid, String clientSecret, String mxid, SessionDao dao) {
        List<Session> sessionList = dao.findBySecretSid(clientSecret, sid);
        switch (sessionList.size()) {
            case 0:
                throw new MatrixException(ErrorResponse.Code.M_SESSION_NOT_VALIDATED, "This validation session hs not yet been completed.");
            case 1:
                Session session = sessionList.get(0);
                getAssociationService().create(session, mxid);
                getInvitationService().sendInvite(session.getMedium(), session.getAddress(), mxid);
                return true;
            default:
                throw new MatrixException(ErrorResponse.Code.M_SESSION_NOT_VALIDATED, "Too many sessions");
        }
    }

    /**
     * Default implementation.
     * <p/>
     * {@link SessionService#cleanup}
     */
    protected void cleanupInternal(SessionDao dao) {
        dao.deleteOldest();
    }
}
