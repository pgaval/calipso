/*
 * Copyright (c) 2007 - 2010 Abiss.gr <info@abiss.gr>  
 *
 *  This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 *  Calipso is free software: you can redistribute it and/or modify 
 *  it under the terms of the GNU Affero General Public License as published by 
 *  the Free Software Foundation, either version 3 of the License, or 
 *  (at your option) any later version.
 * 
 *  Calipso is distributed in the hope that it will be useful, 
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 *  GNU Affero General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License 
 *  along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */

package gr.abiss.calipso.wicket.components.icons;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 * Given an email address (or hash) this will return a valid Gravatar-based URL which
 * if registered with an image, will show that "gravatar"
 */
public class GravatarModel extends AbstractReadOnlyModel/*<String>*/ {

    // Base URL for Gravatar
    private static final String GRAVATAR_URL = "http://www.gravatar.com/avatar/";

    String email;
    String gravatarKey;
    int hsize;

    public GravatarModel(IModel/*<String>*/ model, int hsize) {
        email = (String) model.getObject();
        gravatarKey = DigestUtils.md5Hex(email);
        this.hsize = hsize;
    }
    
    public GravatarModel(String emailHash, int hsize) {
        gravatarKey = emailHash;
        this.hsize = hsize;
    }

    public String getObject() {
        StringBuilder sb = new StringBuilder();
        sb.append(GRAVATAR_URL);
        sb.append(gravatarKey);
        sb.append("?s=");
        sb.append(hsize);
        return sb.toString();
    }
}