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

package gr.abiss.calipso.wicket.register;

import org.apache.log4j.Logger;

import gr.abiss.calipso.domain.CalipsoBreadCrumbBar;
import gr.abiss.calipso.wicket.CalipsoBasePage;
import gr.abiss.calipso.wicket.ComponentUtils;

/**
 *
 */
public class RegisterAnonymousUserFormPage extends CalipsoBasePage{

	/**
	 * Please make proper use of logging, see
	 * http://www.owasp.org/index.php/Category
	 * :Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger
			.getLogger(RegisterAnonymousUserFormPage.class);
	private static final long serialVersionUID = 1L;
	private CalipsoBreadCrumbBar breadCrumbBar;
	public RegisterAnonymousUserFormPanel registerUserFormPanel;
	
	public RegisterAnonymousUserFormPage(){
    	super();
        setVersioned(false);
        //add(new Label("title", getLocalizer().getString("login.title", null)));
        //add(new LoginForm("form"));
        //add(new ForgottenPasswordForm("forgottenPasswordForm"));
        //String calipsoVersion = ComponentUtils.getCalipso(this).getReleaseVersion();
        //add(new Label("version", jtracVersion));                
    
        
		//breadcrumb navigation. stays static
		//breadCrumbBar = new CalipsoBreadCrumbBar("breadCrumbBar", this);
	    //add(breadCrumbBar);
	    //refreshHeader();
	    
	    //panels that change with navigation
	    registerUserFormPanel = new RegisterAnonymousUserFormPanel("panel", breadCrumbBar);
	    add(registerUserFormPanel);
	    
	    //breadCrumbBar.setActive(registerUserFormPanel);
	    //refreshMenu(breadCrumbBar);
	}
	
}
