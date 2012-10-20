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
 * 
 * This file incorporates work released by the JTrac project and  covered 
 * by the following copyright and permission notice:  
 * 
 *   Copyright 2002-2005 the original author or authors.
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *   
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package gr.abiss.calipso.wicket;

import java.util.Locale;

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;

import org.apache.log4j.Logger;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.springframework.util.StringUtils;
import org.apache.wicket.request.Request;
import org.apache.wicket.protocol.http.WebSession;

/**
 * Custom wicket session for Calipso
 */
public class CalipsoSession extends WebSession {

    private final static Logger logger = Logger.getLogger(CalipsoSession.class);
    private User user;
    private Space currentSpace;
    private ItemSearch itemSearch;
    
    public CalipsoSession(Request request) {
        super(request);
        CalipsoApplication calipsoApp = (CalipsoApplication) this.getApplication();
        CalipsoService calipso = calipsoApp.getCalipso();
        int timeOut = calipso.getSessionTimeoutInMinutes();
        ((ServletWebRequest) request).getContainerRequest().getSession().setMaxInactiveInterval(timeOut * 60);
        this.setLocale(new Locale(calipso.getSupportedOrDefaultLocaleCode(this.getLocale())));
    }

    public void setUser(User user) {
        this.user = user;
        if(user.getLocale() == null) {
            // sets the browser locale for anonymous users. Also 
        	// for downward compatibility, may be null in old versions
            user.setLocale(((CalipsoApplication) getApplication()).getCalipso().getDefaultLocale());
        }
        // flip locale only if different from existing
        if(!getLocale().getDisplayName().equals(user.getLocale())) {
            setLocale(StringUtils.parseLocaleString(user.getLocale()));
        }                   
    }

    public User getUser() {
        return user;
    }

    public boolean isAuthenticated() {
        return user != null;
    }

    public Space getCurrentSpace() {
        return currentSpace;
    }

    public void setCurrentSpace(Space currentSpace) {
        this.currentSpace = currentSpace;
    }    

    public ItemSearch getItemSearch() {
        return itemSearch;
    }

    public void setItemSearch(ItemSearch itemSearch) {
        this.itemSearch = itemSearch;
    }
    
}
