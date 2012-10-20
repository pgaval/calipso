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

package gr.abiss.calipso.acegi;

import gr.abiss.calipso.CalipsoService;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

/**
 * custom simple LDAP integration approach, where only authentication
 * is expected from LDAP, Space allocations have to be performed within JTrac only
 *
 * we are not using Acegi LDAP support because
 * a) it does not appear to support binding _as_ the user signing in 
 *    as opposed to a "hardcoded" user and password which is not very nice
 * b) easier to configure, customize and extend in the future
 */
public class LdapAuthenticationProvider implements AuthenticationProvider, InitializingBean {

	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(LdapAuthenticationProvider.class);
    
    private CalipsoService calipsoService;
    private String ldapUrl;
    private String activeDirectoryDomain;
    private String searchBase;
    private String searchKey;
    private String displayNameKey = "cn";
    private String mailKey = "mail";
    private String[] otherReturningAttributes;
    private String[] returningAttributes;        

    // please refer http://forum.java.sun.com/thread.jspa?threadID=726601&tstart=0
    // for the Active Directory LDAP Fast Bind Control approach used here
    private Control control = new Control() {
        public byte[] getEncodedValue() {
            return null;
        }
        public String getID() {
            return "1.2.840.113556.1.4.1781";
        }
        public boolean isCritical() {
            return true;
        }            
    };    
    
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

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public void setDisplayNameKey(String displayNameKey) {
        this.displayNameKey = displayNameKey;
    }

    public void setMailKey(String mailKey) {
        this.mailKey = mailKey;
    }    

    public void setOtherReturningAttributes(String[] otherReturningAttributes) {
        this.otherReturningAttributes = otherReturningAttributes;
    }
    
    public boolean supports(Class clazz) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(clazz);
    }    
    
    public Authentication authenticate(Authentication authentication) {
        if (!supports(authentication.getClass())) {
            return null;
        }
        logger.debug("attempting authentication via LDAP");
        Map<String, String> attributes = null;
        try {
            attributes = bind(authentication.getName(), authentication.getCredentials().toString());
        } catch(Exception e) {
            logger.debug("bind failed: " + e);
            logger.debug("returning null from ldap authentication provider");
            return null;            
        }
        logger.debug("user details retrieved from LDAP, now checking local database");
        UserDetails userDetails = null;
        try {
             userDetails = calipsoService.loadUserByUsername(authentication.getName());
        } catch(AuthenticationException ae) { // catch just to log, then re-throw as-is
            logger.debug("ldap user not allocated to any Spaces within Calipso");
            throw ae;
        }
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());     
    }

    /**
     * displayName and mail are returned always, the map allows us to support
     * getting arbitrary properties in the future, hopefully
     */
    public Map<String, String> bind(String loginName, String password) throws Exception {        
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");        
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        LdapContext ctx = null;
        if(activeDirectoryDomain != null) { // we are using Active Directory            
            Control[] controls = new Control[] {control};
            ctx = new InitialLdapContext(env, controls);
            logger.debug("Active Directory LDAP context initialized");            
            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, activeDirectoryDomain + "\\" + loginName);
            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            // javax.naming.AuthenticationException
            ctx.reconnect(controls);
            logger.debug("Active Directory LDAP bind successful");            
        } else { // standard LDAP            
            env.put(Context.SECURITY_PRINCIPAL, searchKey + "=" + loginName + "," + searchBase);
            env.put(Context.SECURITY_CREDENTIALS, password);
            ctx = new InitialLdapContext(env, null);
            logger.debug("Standard LDAP bind successful");
        }              
        SearchControls sc = new SearchControls();
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);        
        sc.setReturningAttributes(returningAttributes);        
        NamingEnumeration results = ctx.search(searchBase, searchKey + "=" + loginName, sc);
        while(results.hasMoreElements()) {
            SearchResult sr = (SearchResult) results.next();
            Attributes attrs = sr.getAttributes();
            logger.debug("attributes: " + attrs);
            Map<String, String> map = new HashMap<String, String>(returningAttributes.length);
            for(String key : returningAttributes) {
                Attribute attr = attrs.get(key);
                if (attr != null) {
                    map.put(key, (String) attr.get());
                }
            }
            return map; // there should be only one anyway            
        }
        // if we reached here, there was no search result
        throw new Exception("no results returned from ldap");
    }    
    
    // one-time init routine normally called by Spring as InitializingBean
    // but when we use a custom FactoryBean, we have to call this manually
    public void afterPropertiesSet() {
        if(otherReturningAttributes != null) {
            List<String> keys = new ArrayList<String>();
            keys.add(mailKey);
            keys.add(displayNameKey);
            for(String s : otherReturningAttributes) {
                keys.add(s);
            }
            returningAttributes = keys.toArray(new String[keys.size()]);
        } else {
            returningAttributes = new String[] {mailKey, displayNameKey};
        }
        if(searchKey == null) {
            if(activeDirectoryDomain != null && activeDirectoryDomain.trim().length() > 0) {
                searchKey = "sAMAccountName";
            } else {
                activeDirectoryDomain = null;
                searchKey = "uid";
            }
        }
        logger.info("ldap authenthication provider initialized searchKey = '" + searchKey + "'"
                + ", searchBase = '" + searchBase + "', activeDirectoryDomain = '" + activeDirectoryDomain + "'"
                + ", ldapUrl = '" + ldapUrl + "'");
    }
    
}
