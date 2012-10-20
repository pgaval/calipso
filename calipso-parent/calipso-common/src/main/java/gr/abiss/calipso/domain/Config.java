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

package gr.abiss.calipso.domain;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Simple name value pair to hold configuration parameters
 * in the database for JTrac, e.g. SMTP e-mail server, etc.
 * TODO better validation, type-safety, masking of mail server password
 */
public class Config implements Serializable {
    
    private String param;  // someone reported that "key" is a reserved word in MySQL
    private String value;

    private static final Set<String> PARAMS;
    
    // set up a static set of valid config key names
    static {
        PARAMS = new LinkedHashSet<String>();
        PARAMS.add("mail.forceVerification");
        PARAMS.add("mail.server.host");
        PARAMS.add("mail.server.port");
        PARAMS.add("mail.server.username");
        PARAMS.add("mail.server.password");
        PARAMS.add("mail.server.starttls.enable");
        PARAMS.add("mail.subject.prefix");
        PARAMS.add("mail.from");
        PARAMS.add("mail.session.jndiname");
        PARAMS.add("calipso.url.base");
        PARAMS.add("calipso.url.logo");
        PARAMS.add("calipso.pageSize");
        PARAMS.add("calipso.hideLoginLink");
        PARAMS.add("calipso.hideRegisterLink");
        PARAMS.add("locale.default");
        PARAMS.add("session.timeout");
        PARAMS.add("attachment.maxsize");
        PARAMS.add("attachment.extentionsAllowed");
        PARAMS.add("mailedItem.mailSubjectPrefix");
        PARAMS.add("mailedItem.mailServer");
        PARAMS.add("mailedItem.mailUserNameAccount");
        PARAMS.add("mailedItem.mailUserNamePassword");
        PARAMS.add("classes.dashboard");
    }
    
    public static Set<String> getParams() {
        return PARAMS;
    }
    
    public Config() {
        // zero arg constructor
    }
    
    public Config(String param, String value) {
        this.param = param;
        this.value = value;
    }
    
    public boolean isMailConfig() {
        return param.startsWith("mail.") || param.startsWith("calipso.url.");
    }
    
    public boolean isAttachmentConfig() {
        return param.startsWith("attachment.");
    }
    
    public boolean isSessionTimeoutConfig() {
        return param.startsWith("session.");
    }
    
    public boolean isLocaleConfig() {
        return param.startsWith("locale.");
    }
    
    public boolean isPageSizeConfig(){
    	return param.endsWith("pageSize");
    }
    
    public boolean isClassesConfig(){
    	return param.startsWith("classes");
    }
    //==========================================================================
    
    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}