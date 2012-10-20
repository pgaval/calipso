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

import org.apache.wicket.application.ReloadingClassLoader;
import org.apache.wicket.protocol.http.ReloadingWicketFilter;
import org.apache.log4j.Logger;

public class CalipsoReloadingWicketFilter extends ReloadingWicketFilter {        
    
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(CalipsoReloadingWicketFilter.class);
    
    private static final String banner = 
        "\n***********************************************\n"
        + "*** WARNING: Reloading Wicket Filter in use ***\n"
        + "***    This is wrong if production mode.    ***\n"
        + "***********************************************";      
    
    static {
        ReloadingClassLoader.includePattern("gr.abiss.calipso.wicket.*");        
        ReloadingClassLoader.excludePattern("gr.abiss.calipso.wicket.CalipsoApplication");
        ReloadingClassLoader.excludePattern("gr.abiss.calipso.wicket.CalipsoSession");
        ReloadingClassLoader.excludePattern("gr.abiss.calipso.wicket.DashboardPage");
        // ReloadingClassLoader.excludePattern("org.springframework.*");
        // ReloadingClassLoader.excludePattern("org.acegisecurity.*");
    }
    
    public CalipsoReloadingWicketFilter() {
        super();
        logger.warn(banner);
    }
    
}
