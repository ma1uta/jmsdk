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

package io.github.ma1uta.homeserver.model;

import java.util.Objects;

/**
 * User.
 */
public class User {

    /**
     * MXID.
     */
    private String id;

    /**
     * Password.
     */
    private char[] password;

    /**
     * Display name.
     */
    private String displayName;

    /**
     * Avatar url.
     */
    private String avatarUrl;

    /**
     * Kind user. One of ["user", "guest"].
     */
    private String kind;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Copy of the password.
     *
     * @return password.
     */
    public char[] getPassword() {
        if (this.password == null) {
            return null;
        }
        char[] password = new char[this.password.length];
        System.arraycopy(this.password, 0, password, 0, this.password.length);
        return password;
    }

    /**
     * Set password.
     *
     * @param password new password.
     */
    public void setPassword(char[] password) {
        if (password != null) {
            this.password = new char[password.length];
            System.arraycopy(password, 0, this.password, 0, password.length);
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
