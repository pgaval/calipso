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

import gr.abiss.calipso.domain.Organization;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;

/**
 * Organization profile
 */
public class OrganizationViewPanel extends BasePanel {
      Organization organization;

    public OrganizationViewPanel(String id, IBreadCrumbModel breadCrumbModel, long organizationId) {
    	super(id, breadCrumbModel);
    	this.organization = getCalipso().loadOrganization(organizationId);
    	init(breadCrumbModel, this.organization);
    }
    
    public OrganizationViewPanel(String id, IBreadCrumbModel breadCrumbModel, Organization organization) {
    	super(id, breadCrumbModel);
    	this.organization = organization;
    	init(breadCrumbModel, this.organization);
    }
    
    public void init(IBreadCrumbModel breadCrumbModel, Organization organization){
    	add(new IconPanel("organizationIcon", new PropertyModel(organization, "id"), "organizations"));
    	add(new Label("organizationName", organization.getName()));
    	add(new Label("organizationVatNumber", organization.getVatNumber()));
    	add(new Label("organizationAddress", organization.getAddress()));
    	add(new Label("organizationZip", organization.getZip()));
    	add(new Label("organizationCountry", localize(organization.getCountry())));
    	add(new Label("organizationPhone", organization.getPhone()));
    	add(new Label("organizationEmail", organization.getEmail()));
    	WebMarkupContainer webLink = new WebMarkupContainer("organizationWeb");
    	String url = organization.getWeb();
    	webLink.add(new SimpleAttributeModifier("href", url));
    	webLink.add(new Label("organizationWeb", organization.getWeb()));
    	add(webLink);
    }
    
    public String getTitle(){
        return localize("organization.organizationDetails");
    }
}