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

package gr.abiss.calipso.wicket;

import java.io.File;

import gr.abiss.calipso.config.CalipsoPropertiesEditor;
import gr.abiss.calipso.domain.User;

import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.PropertyModel;

/**
 * @author erasmus
 */
public class UserIconPanel extends BasePanel {
	public UserIconPanel(String id, final User user) {
		this(id, null, user, false);
	}
	
	public UserIconPanel(String id, final User user, boolean getSmall) {
		this(id, null, user, false);
	}
	
	public UserIconPanel(String id, final IBreadCrumbModel breadCrumbModel, final User user) {
		this(id, breadCrumbModel, user, false);
	}
	
	public UserIconPanel(String id, final IBreadCrumbModel breadCrumbModel, final User user, boolean getSmall) {
		super(id, breadCrumbModel);
		
		WebMarkupContainer userComponent;
		if(breadCrumbModel != null){//add link to user profile
			userComponent = new BreadCrumbLink("viewLink", breadCrumbModel){
				protected IBreadCrumbParticipant getParticipant(String componentId){
					return new UserViewPanel(componentId, breadCrumbModel, user.getId());
			    }
	    	};
		}
		else{//don't use link
			userComponent = new WebMarkupContainer("viewLink");
			userComponent.setRenderBodyOnly(true);
		}
		add(userComponent);
		
		userComponent.add(new IconPanel("icon", user, getSmall).setRenderBodyOnly(true));
	}
}

