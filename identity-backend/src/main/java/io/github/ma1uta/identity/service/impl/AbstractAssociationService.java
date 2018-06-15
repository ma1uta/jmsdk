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

import io.github.ma1uta.identity.configuration.AssociationConfiguration;
import io.github.ma1uta.identity.dao.AssociationDao;
import io.github.ma1uta.identity.model.Association;
import io.github.ma1uta.identity.model.Session;
import io.github.ma1uta.identity.service.AssociationService;
import io.github.ma1uta.jeon.exception.MatrixException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Default implementation.
 * <p/>
 * There are default implementation for all methods of the {@link AssociationService}.
 * <p/>
 * You can create you own class for example with annotation @Transactional (jpa) or another and invoke the same
 * method with suffix "Internal".
 * <pre>
 * {@code
 *     @literal @Service
 *     public class MyAssociationService extends AbstractAssociationService {
 *         ...
 *         @literal @Override
 *         @literal @Transactional
 *         @literal @MyFavouriteAnnotation
 *         public void expire() {
 *             // wrap next link to transaction via annotation or code.
 *             Association dao = ...;
 *             super.expireInternal(dao);
 *         }
 *         ...
 *     }
 * }
 * </pre>
 */
public abstract class AbstractAssociationService implements AssociationService {

    /**
     * Error message when too mant associations.
     */
    public static final String M_TOO_MANY_ASSOCIATIONS = "M_TOO_MANY_ASSOCIATIONS";

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAssociationService.class);

    /**
     * Configuration.
     */
    private final AssociationConfiguration configuration;

    public AbstractAssociationService(AssociationConfiguration configuration) {
        this.configuration = configuration;
    }

    public AssociationConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Default implementation for lookup method.
     * <p/>
     * {@link AssociationService#lookup(String, String)}
     */
    protected Optional<Association> lookupInternal(String address, String medium, AssociationDao dao) {
        List<Association> associationList = dao.findByAddressMedium(address, medium);
        if (associationList.size() > 1) {
            throw new MatrixException(M_TOO_MANY_ASSOCIATIONS, "Too many associations.");
        } else if (associationList.size() == 1) {
            return Optional.of(associationList.get(0));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Default implementation.
     * <p/>
     * {@link AssociationService#lookup(List)}
     */
    protected List<List<String>> lookupInternal(List<List<String>> threepids, AssociationDao dao) {
        return threepids.stream().map(list -> lookupInternal(list.get(0), list.get(1), dao))
            .filter(Optional::isPresent).map(Optional::get)
            .filter(response -> StringUtils.isNoneBlank(response.getAddress(), response.getMedium(), response.getMxid()))
            .map(response -> Arrays.asList(response.getMedium(), response.getAddress(), response.getMxid())).collect(Collectors.toList());
    }

    /**
     * Default implementation.
     * <p/>
     * {@link AssociationService#create(Session, String)}
     */
    protected void createInternal(Session session, String mxid, AssociationDao dao) {
        LocalDateTime expired = LocalDateTime.now().plusSeconds(getConfiguration().getAssociationTTL());
        dao.insertOrIgnore(session.getAddress(), session.getMedium(), mxid, expired);
    }

    /**
     * Default implementation.
     * <p/>
     * {@link AssociationService#expire()}
     */
    protected void expireInternal(AssociationDao dao) {
        dao.expire();
    }
}
