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

import io.github.ma1uta.identity.configuration.InvitationServiceConfiguration;
import io.github.ma1uta.identity.dao.InvitationDao;
import io.github.ma1uta.identity.model.Association;
import io.github.ma1uta.identity.model.Invitation;
import io.github.ma1uta.identity.service.AssociationService;
import io.github.ma1uta.identity.service.InvitationService;
import io.github.ma1uta.identity.service.KeyService;
import io.github.ma1uta.identity.service.RestService;
import io.github.ma1uta.identity.service.SerializerService;
import io.github.ma1uta.jeon.exception.MatrixException;
import io.github.ma1uta.matrix.ErrorResponse;
import io.github.ma1uta.matrix.Id;
import io.github.ma1uta.matrix.server.model.bind.Invite;
import io.github.ma1uta.matrix.server.model.bind.OnBindRequest;
import io.github.ma1uta.matrix.server.model.bind.Signed;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

/**
 * Default implementation.
 * <p/>
 * There are default implementation for all methods of the {@link InvitationService}.
 * <p/>
 * You can create you own class for example with annotation @Transactional (jpa) or another and invoke the same method
 * with suffix "Internal".
 * <pre>
 * {@code
 *     @literal @Service
 *     public class MyInvitationService extends AbstractInvitationService {
 *         ...
 *         @literal @Override
 *         @literal @Transactional
 *         @literal @MyFavouriteAnnotation
 *         public String create(String address, String medium, String roomId, String sender) {
 *             // wrap next link to transaction via annotation or code.
 *             InvitationDao dao = ...
 *             return super.createInternal(address, medium, roomId, sender, dao);
 *         }
 *         ...
 *     }
 * }
 * </pre>
 */
public abstract class AbstractInvitationService implements InvitationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractInvitationService.class);

    private final AssociationService associationService;
    private final KeyService keyService;
    private final SerializerService serializer;
    private final InvitationServiceConfiguration configuration;
    private final RestService restService;

    public AbstractInvitationService(AssociationService associationService, KeyService keyService, SerializerService serializer,
                                     InvitationServiceConfiguration configuration, RestService restService) {
        this.associationService = associationService;
        this.keyService = keyService;
        this.serializer = serializer;
        this.configuration = configuration;
        this.restService = restService;
    }

    protected AssociationService getAssociationService() {
        return associationService;
    }

    protected KeyService getKeyService() {
        return keyService;
    }

    protected SerializerService getSerializer() {
        return serializer;
    }

    protected InvitationServiceConfiguration getConfiguration() {
        return configuration;
    }

    protected RestService getRestService() {
        return restService;
    }

    /**
     * Default implementation.
     * <p/>
     * {@link InvitationService#create(String, String, String, String)}
     */
    protected Triple<String, String, List<String>> createInternal(String address, String medium, String roomId, String sender,
                                                                  InvitationDao dao) {
        if (!"email".equals(medium)) {
            throw new MatrixException(ErrorResponse.Code.M_BAD_JSON, "Wrong medium.", HttpServletResponse.SC_BAD_REQUEST);
        }
        int index = address.indexOf('@');
        if (index == -1) {
            throw new MatrixException(ErrorResponse.Code.M_BAD_JSON, "Wrong address", HttpServletResponse.SC_BAD_REQUEST);
        }
        Optional<Association> association = getAssociationService().lookup(address, medium);
        if (association.isPresent() && association.get().getMxid() != null) {
            throw new MatrixException(ErrorResponse.Code.M_THREEPID_IN_USE, "Medium and address are used.",
                HttpServletResponse.SC_BAD_REQUEST);
        }
        String token = UUID.randomUUID().toString();
        String ephemeralKey;
        String longTermKey;
        ephemeralKey = getKeyService().generateShortTermKey();
        longTermKey = getKeyService().retrieveLongTermKey();
        String displayName = address.substring(0, index);

        dao.insert(address, medium, roomId, sender, token, Arrays.asList(ephemeralKey, longTermKey), displayName);

        return new ImmutableTriple<>(displayName, token, Arrays.asList(ephemeralKey, longTermKey));
    }

    /**
     * Default implementation.
     * <p/>
     * {@link InvitationService#sendInvite(String, String, String)}
     */
    protected void sendInviteInternal(String address, String medium, String mxid, InvitationDao dao) {

        List<Invitation> invitationList = dao.findByAddressMedium(address, medium);

        if (!invitationList.isEmpty()) {
            OnBindRequest request = new OnBindRequest();
            request.setAddress(address);
            request.setMedium(medium);
            request.setMxid(mxid);
            request.setInvites(invitationList.stream().map(item -> {
                Invite invite = new Invite();
                invite.setAddress(item.getAddress());
                invite.setMedium(item.getMedium());
                invite.setMxid(mxid);
                invite.setRoomId(item.getRoomId());
                invite.setSender(item.getSender());

                Signed signed = new Signed();
                signed.setMxid(mxid);
                signed.setToken(item.getToken());
                signed.setSignatures(getKeyService().sign(getSerializer().serialize(signed)));
                invite.setSigned(signed);
                return invite;
            }).collect(Collectors.toList()));
            String domain = Id.domain(mxid);
            String bindUrl = String
                .format("%s://%s:%s/%s", getConfiguration().getOnBindProtocol(), domain, getConfiguration().getOnBindPort(),
                    getConfiguration().getOnBindUrl());
            getRestService().post(bindUrl, request);
        }
    }
}
