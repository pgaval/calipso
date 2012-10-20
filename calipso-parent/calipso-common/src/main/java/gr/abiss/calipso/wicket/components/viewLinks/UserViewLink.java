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

package gr.abiss.calipso.wicket.components.viewLinks;

import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.UserViewPanel;

import org.apache.log4j.Logger;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

/**
 * @author erasmus
 */
public class UserViewLink extends BasePanel {
	protected static final Logger logger = Logger.getLogger(UserViewLink.class);

	public UserViewLink(String id, final IBreadCrumbModel breadCrumbModel, final User user) {
		this(id, breadCrumbModel, user, true, true, true, true);
	}	
	
	public UserViewLink(String id, final IBreadCrumbModel breadCrumbModel, final User user, 
			boolean showFirstname, boolean showLastname, boolean showLogin, boolean showOrganization) {
		super(id, breadCrumbModel);
		if(user == null){
			add(new WebMarkupContainer("loggedBy").setVisible(false));
			return;
		}
    	StringBuffer userLabel = new StringBuffer();
    	if(showFirstname){
    		userLabel.append(user.getName());
    	}
    	if(showLastname){
    		userLabel.append(" ")
			.append(user.getLastname());
    	}
    	if(showLogin){
    		userLabel.append(" (")
			.append(user.getLoginName())
			.append(")");
    	}
    	if(showOrganization && user.getOrganization() != null){
    		userLabel.append(", ")
    			.append(user.getOrganization().getName());
    	}
    
        //loggedBy link
    	Label loggedByLabel = new Label("loggedBy", userLabel.toString());
    	
    	if(breadCrumbModel != null){
	    	add(new BreadCrumbLink("loggedBy", getBreadCrumbModel()){
				protected IBreadCrumbParticipant getParticipant(String componentId){
					return new UserViewPanel(componentId, getBreadCrumbModel(), user.getId());
			    }
	    	}.add(loggedByLabel));
    	}
    	else{
    		loggedByLabel.setRenderBodyOnly(true);
	    	add(new WebMarkupContainer("loggedBy").add(loggedByLabel));
    	}
	}	
}

