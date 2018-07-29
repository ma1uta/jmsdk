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

import io.github.ma1uta.identity.model.Association;
import io.github.ma1uta.identity.service.AssociationService;
import io.github.ma1uta.identity.service.KeyService;
import io.github.ma1uta.identity.service.SerializerService;
import io.github.ma1uta.jeon.exception.MatrixException;
import io.github.ma1uta.matrix.ErrorResponse;
import io.github.ma1uta.matrix.identity.api.LookupApi;
import io.github.ma1uta.matrix.identity.model.lookup.BulkLookupRequest;
import io.github.ma1uta.matrix.identity.model.lookup.BulkLookupResponse;
import io.github.ma1uta.matrix.identity.model.lookup.LookupResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementation of the {@link LookupApi}.
 */
public class Lookup implements LookupApi {

    private final AssociationService associationService;

    private final KeyService keyService;

    private final SerializerService serializerService;

    public Lookup(AssociationService associationService, KeyService keyService,
                  SerializerService serializerService) {
        this.associationService = associationService;
        this.keyService = keyService;
        this.serializerService = serializerService;
    }

    public AssociationService getAssociationService() {
        return associationService;
    }

    public KeyService getKeyService() {
        return keyService;
    }

    public SerializerService getSerializerService() {
        return serializerService;
    }

    @Override
    public LookupResponse lookup(String medium, String address, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        if (StringUtils.isAnyBlank(medium, address)) {
            throw new MatrixException(ErrorResponse.Code.M_BAD_JSON, "Missing medium or address.");
        }
        Optional<Association> association = getAssociationService().lookup(address, medium);
        LookupResponse response = new LookupResponse();
        association.ifPresent(a -> {
            response.setMxid(a.getMxid());
            response.setMedium(a.getMedium());
            response.setAddress(a.getAddress());
            response.setTs(a.getTs());
            response.setNotBefore(a.getCreated());
            response.setNotAfter(a.getExpired());
            response.setSignatures(getKeyService().sign(getSerializerService().serialize(response)));
        });
        return response;
    }

    @Override
    public BulkLookupResponse bulkLookup(BulkLookupRequest request, HttpServletRequest servletRequest,
                                         HttpServletResponse servletResponse) {
        if (request == null) {
            throw new MatrixException(ErrorResponse.Code.M_BAD_JSON, "Missing medium or address.");
        }
        BulkLookupResponse response = new BulkLookupResponse();
        response.setThreepids(getAssociationService().lookup(request.getThreepids()));
        return response;
    }
}
