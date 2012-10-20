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

import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.wicket.asset.AssetsPanel;
import gr.abiss.calipso.wicket.regexp.ValidationExpressionFormPanel;
import gr.abiss.calipso.wicket.regexp.ValidationExpressionPanel;


import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanelLink;
import org.apache.wicket.markup.html.link.Link;

/**
 * options menu page
 */
public class OptionsPanel extends BasePanel {

	public String getTitle(){
        return localize("options.optionsMenu");
    }
	
    public OptionsPanel(String id, final IBreadCrumbModel breadCrumbModel) {
    	super(id, breadCrumbModel);
    	
//        setVersioned(false);                      
        final User user = getPrincipal();

		//User Profile
    	add(new BreadCrumbLink("profile", breadCrumbModel){
    		protected IBreadCrumbParticipant getParticipant(String componentId){
				return new UserFormPanel(componentId, breadCrumbModel, getCalipso().loadUser(user.getId()));
    	    }
    	});
	
        final boolean isAdmin = user.isGlobalAdmin();
        final boolean isSpaceAdmin = user.isSpaceAdmin();
        
        add(new BreadCrumbPanelLink("users", this, UserListPanel.class).setVisible(isAdmin || isSpaceAdmin));
        
        add(new BreadCrumbPanelLink("spaces", this, SpaceListPanel.class).setVisible(isAdmin || isSpaceAdmin));

        add(new BreadCrumbPanelLink("settings", this, ConfigListPanel.class).setVisible(isAdmin));

        add(new BreadCrumbPanelLink("indexes", this, IndexRebuildPanel.class).setVisible(isAdmin));
        
        add(new BreadCrumbPanelLink("organizations", this, OrganizationPanel.class).setVisible(isAdmin));
    
        add(new Link("import") {
            public void onClick() {}            
        }.setVisible(false)); 
         
        
    	//Assets
        BreadCrumbLink breadCrumbLink = new BreadCrumbLink("assets", breadCrumbModel){
 	       protected IBreadCrumbParticipant getParticipant(String componentId){
            return new AssetsPanel(componentId, breadCrumbModel);
 	       }
        };
        breadCrumbLink.setVisible(isAdmin || isSpaceAdmin);
        add(breadCrumbLink);
        
        BreadCrumbLink validationLink = new BreadCrumbLink("validations", breadCrumbModel){
   	       protected IBreadCrumbParticipant getParticipant(String componentId){
              return new ValidationExpressionPanel(componentId, breadCrumbModel);
   	       }
          };
         validationLink.setVisible(isAdmin || isSpaceAdmin);
        add(validationLink);
    }
}