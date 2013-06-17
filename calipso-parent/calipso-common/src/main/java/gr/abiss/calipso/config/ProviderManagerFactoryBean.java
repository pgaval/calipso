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

package gr.abiss.calipso.config;

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.acegi.LdapAuthenticationProvider;

import java.util.ArrayList;
import java.util.List;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.ProviderManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;

/**
 * acegi authentication provider manager factory bean
 * conditionally sets up ldap authentication
 */
public class ProviderManagerFactoryBean implements FactoryBean {
    
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(ProviderManagerFactoryBean.class);
    
    private CalipsoService calipsoService;    
    private String ldapUrl;
    private String activeDirectoryDomain;
    private String searchBase;
    private AuthenticationProvider authenticationProvider;   

    public void setCalipsoService(CalipsoService calipsoService) {
        this.calipsoService = calipsoService;
    }    
    
    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public void setActiveDirectoryDomain(String activeDirectoryDomain) {
        this.activeDirectoryDomain = activeDirectoryDomain;
    }

    public void setSearchBase(String searchBase) {
        this.searchBase = searchBase;
    }

    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }    
    
    public Object getObject() throws Exception {        
        List providers = new ArrayList();
        if(ldapUrl.length() > 0) {
            logger.info("switching on ldap authentication provider");
            LdapAuthenticationProvider ldapProvider = new LdapAuthenticationProvider();
            ldapProvider.setLdapUrl(ldapUrl);            
            ldapProvider.setActiveDirectoryDomain(activeDirectoryDomain);        
            ldapProvider.setSearchBase(searchBase);
            ldapProvider.setCalipsoService(calipsoService);
            // **IMPORTANT!** we have to call this one time init ourselves 
            // as we are manually doing the factory stuff not Spring
            ldapProvider.afterPropertiesSet();
            // this is added at the top of the list or providers, and will fall back to local database
            providers.add(ldapProvider);
        } else {
            logger.info("not using ldap authentication");
        }
        // add dependency injected local database based authentication
        providers.add(authenticationProvider);
        ProviderManager mgr = new ProviderManager();
        mgr.setProviders(providers);
        return mgr;
    }

    public Class getObjectType() {
        return ProviderManager.class;
    }

    public boolean isSingleton() {
        return true;
    }
    
    
}
