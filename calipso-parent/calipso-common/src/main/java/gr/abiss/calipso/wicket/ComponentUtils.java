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
import gr.abiss.calipso.domain.Country;
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;

import org.apache.wicket.Component;
import org.apache.wicket.model.StringResourceModel;

/**
 * this common class helps to make things easier for the sub-classes
 * of BasePage and BasePanel to perform common routines and keeps
 * code in one place, reducing duplication
 */
public class ComponentUtils {
	
	private static final String COUNTRY_RESOURCE_PREFIX = "country.";
    
    public static CalipsoService getCalipso(Component c) {
        return ((CalipsoApplication) c.getApplication()).getCalipso();
    }          
    
    public static User getPrincipal(Component c) {
        return ((CalipsoSession) c.getSession()).getUser();
    }
    
    public static void setCurrentSpace(Component c, Space space) {
        ((CalipsoSession) c.getSession()).setCurrentSpace(space);
    }      
    
    public static Space getCurrentSpace(Component c) {
        return ((CalipsoSession) c.getSession()).getCurrentSpace();
    }     
    
    public static void setCurrentItemSearch(Component c, ItemSearch itemSearch) {
        ((CalipsoSession) c.getSession()).setItemSearch(itemSearch);
    }      
    
    public static ItemSearch getCurrentItemSearch(Component c) {
        return ((CalipsoSession) c.getSession()).getItemSearch();
    }    
    
    /**
     * conditional flip of session if same user id
     */
    public static void refreshPrincipal(Component c, User user) {
        if(user.getId() == getPrincipal(c).getId()) {
            refreshPrincipal(c);
        }
    }
    
    public static void refreshPrincipal(Component c) {        
        User temp = getCalipso(c).loadUser(getPrincipal(c).getId());
        // loadUserByUsername forces hibernate eager load
        CalipsoSession session = (CalipsoSession) c.getSession();
        User user = (User) getCalipso(c).loadUserByUsername(temp.getLoginName());
        session.setUser(user); 
    }
    
    /**
     * localization helper
     */
    public static String localize(Component c, String key) {
        return c.getLocalizer().getString(key, null);
    }
    
    public static String localize(Component c, String key, Object... params) {
        // integer params cause problems, go with String only
        StringResourceModel m = new StringResourceModel(key, c, null, params);
        //m.setLocalizer(c.getLocalizer());
        return m.getString();
    }

	public static String localize(BasePanel c, Country country) {
        return c.getLocalizer().getString(COUNTRY_RESOURCE_PREFIX+country.getId(), null);
	}    
    
}
