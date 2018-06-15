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

package io.github.ma1uta.identity.configuration;

/**
 * Configuration of the Association Service.
 */
public class AssociationConfiguration {

    /**
     * Default time to live of the association.
     * <p/>
     * 60 seconds * 60 minutes * 24 hours * 30 days * 12 month * 10 years.
     */
    public static final long DEFAULT_ASSOCIATION_TTL = 60 * 60 * 24 * 30 * 12 * 10;

    /**
     * Time to live in seconds of the association.
     * <p/>
     * Default value is 10 years (60 seconds * 60 minutes * 24 hours * 30 days * 12 month * 10 years).
     */
    private long associationTTL = DEFAULT_ASSOCIATION_TTL;

    public long getAssociationTTL() {
        return associationTTL;
    }

    public void setAssociationTTL(long associationTTL) {
        this.associationTTL = associationTTL;
    }
}
