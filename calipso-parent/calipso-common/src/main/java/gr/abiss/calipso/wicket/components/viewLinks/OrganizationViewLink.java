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

import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.OrganizationViewPanel;

import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

/**
 * @author erasmus
 */
public class OrganizationViewLink extends BasePanel {

	public OrganizationViewLink(String id, final IBreadCrumbModel breadCrumbModel, final Organization organization) {
		super(id, breadCrumbModel);
		
		if(organization == null){
			add(new WebMarkupContainer("organizationName").setVisible(false));
			return;
		}
		
        //organization name label
    	Label organizationNameLabel = new Label("organizationName", organization.getName());
    	
    	add(new BreadCrumbLink("organizationName", getBreadCrumbModel()){
			protected IBreadCrumbParticipant getParticipant(String componentId){
				return new OrganizationViewPanel(componentId, getBreadCrumbModel(), organization.getId());
		    }
    	}.add(organizationNameLabel));
    	
	}	
}

