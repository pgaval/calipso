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

import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
//import org.springframework.security.providers.cas.ticketvalidator.CasProxyTicketValidator;

/**
 * class that exists purely to add a couple of setters to the Acegi CasProxyTicketValidator
 * so that the loginUrl ' logoutUrl can be also included in the applicationContext-acegi-cas.xml
 * since we use Wicket, we don't need the CasProcessingFilterEntryPoint
 * kind of a hack, would have been much better to use the CalipsoConfigurer + properties file
 * but people who want to use CAS are assumed to be good at hacking XML :)
 * plus Acegi seems to be undergoing a major overhaul at the moment as well
 * and haven't yet looked at CAS 3 yet
 */
public class CalipsoCasProxyTicketValidator extends Cas20ProxyTicketValidator {
    
    public CalipsoCasProxyTicketValidator(String casServerUrlPrefix) {
		super(casServerUrlPrefix);
		// TODO Auto-generated constructor stub
	}

	private String loginUrl;
    private String logoutUrl;

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }        

}
