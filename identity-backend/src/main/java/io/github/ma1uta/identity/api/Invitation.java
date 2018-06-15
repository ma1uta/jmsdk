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

import io.github.ma1uta.identity.service.InvitationService;
import io.github.ma1uta.jeon.exception.MatrixException;
import io.github.ma1uta.matrix.ErrorResponse;
import io.github.ma1uta.matrix.identity.api.InvitationApi;
import io.github.ma1uta.matrix.identity.model.invitation.InvitationResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementation of the {@link InvitationApi}.
 */
public class Invitation implements InvitationApi {

    private final InvitationService invitationService;

    public Invitation(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    public InvitationService getInvitationService() {
        return invitationService;
    }

    @Override
    public InvitationResponse invite(String medium, String address, String roomId, String sender, HttpServletRequest servletRequest,
                                     HttpServletResponse servletResponse) {
        if (StringUtils.isAnyBlank(medium, address, roomId, sender)) {
            throw new MatrixException(ErrorResponse.Code.M_BAD_JSON, "Some of the required fields are missing");
        }
        Triple<String, String, List<String>> triple = getInvitationService().create(address, medium, roomId, sender);
        InvitationResponse response = new InvitationResponse();
        response.setDisplayName(triple.getLeft());
        response.setToken(triple.getMiddle());
        response.setPublicKeys(triple.getRight());
        return response;
    }
}
