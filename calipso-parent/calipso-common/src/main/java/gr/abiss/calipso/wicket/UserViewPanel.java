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

import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.wicket.components.viewLinks.OrganizationViewLink;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.hibernate.Hibernate;

/**
 * Renders a User profile
 */
public class UserViewPanel extends BasePanel {
	protected static final Logger logger = Logger.getLogger(UserViewPanel.class);
    private User user;
    
    public UserViewPanel(String id, IBreadCrumbModel breadCrumbModel, User user) {
    	super(id, breadCrumbModel);
    	this.user = user;
    	init(breadCrumbModel, this.user);
    }
    public UserViewPanel(String id, IBreadCrumbModel breadCrumbModel, long userId) {
    	super(id, breadCrumbModel);
    	this.user = getCalipso().loadUser(userId);
    	init(breadCrumbModel, this.user);
    }

    public String getTitle(){
        return localize("user_form.userDetails");
    }
    
    private void init(IBreadCrumbModel breadCrumbModel, final User user){
    	if(user == null){
			logger.error("No user was provided");
		}
    	final List<Space> userSpaces; 
    	if(Hibernate.isInitialized(user.getSpaces())){
    		userSpaces = new ArrayList<Space> (user.getSpaces());
    	}else{// TODO: hql query
    		userSpaces = new ArrayList<Space> (user.getSpaces());
    	}
    	final User currentUser = getPrincipal();
    	
    	WebMarkupContainer editContainer = new WebMarkupContainer("editContainer");
		editContainer.add(new BreadCrumbLink("edit", breadCrumbModel) {
			@Override
			protected IBreadCrumbParticipant getParticipant(String componentId) {
				return new UserFormPanel(componentId, getBreadCrumbModel(), user);
			}
		});    	
    	add(editContainer.setVisible(false));
    	
    	if(currentUser.isGlobalAdmin()){//if isAdmin
    		editContainer.setVisible(true);
    	}
    	else if(currentUser.isSpaceAdmin()){//if isSpaceAdmin
    		//check if current user can edit this user
    		//code from UserListPanel
    		if (!user.isGlobalAdmin()){
        		for (Space us: userSpaces){
        			if (currentUser.getSpacesWhereUserIsAdmin().contains(us)){
        				editContainer.setVisible(true);
        				break;
        			}
        		}
    		}
    	}
    	
    	
    	add(new UserIconPanel("userIcon", user));
    	add(new Label("loginName", user.getLoginName()));
    	add(new Label("name", user.getName()));
    	add(new Label("lastname", user.getLastname()));
    	add(new Label("email", user.getEmail()));
    	add(new Label("country", localize(user.getCountry())));
    	add(new OrganizationViewLink("organization", breadCrumbModel, user.getOrganization()));
    	add(new Label("address", user.getAddress()));
    	add(new Label("phone", user.getPhone()));
    	
    	
        LoadableDetachableModel sharedSpacesListModel = new LoadableDetachableModel() {
            protected Object load() {                
                List<Space> sharedSpacesList = new ArrayList<Space>();
                
                //get spaces for current user
            	Set<Space> currentUserSpaces = currentUser.getSpaces();            	
            	
            	//find shared spaces
            	for (Space currentUserSpace : currentUserSpaces) {
            		if(userSpaces.contains(currentUserSpace)){//if this space is shared for the 2 users
            			sharedSpacesList.add(currentUserSpace);
            		}			
        		}                		
                return sharedSpacesList;
            }
        };        
    	
    	
        if(CollectionUtils.isNotEmpty((List)sharedSpacesListModel.getObject())){

        	logger.debug("user has spaces");
        	if(user.getLoginName().equals(getPrincipal().getLoginName())){
        		add(new Label("sharedSpaces", localize("UserViewPanel.yourSpaces")));
        	}
        	else{
        		add(new Label("sharedSpaces", localize("UserViewPanel.yourSpaces", user.getName())));
        	}
        }
        else{
        	logger.debug("user has no spaces");
    		add(new Label("sharedSpaces", ""));
        }
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
    	
    	ListView listView = new ListView("spaces", sharedSpacesListModel) {
            protected void populateItem(ListItem listItem) {                
                final Space spaceItem = (Space) listItem.getModelObject();                
                
                if(listItem.getIndex() % 2 == 1) {
                    listItem.add(sam);
                }                                 
               
                listItem.add(new Link("space") {
    				@Override
    				public void onClick() {
    					setCurrentSpace(spaceItem);
    					setResponsePage(SpacePage.class);			
    				}
    			}.add(new Label("space", localize(spaceItem.getNameTranslationResourceKey()))));
                               
            }            
        };
        
        add(listView);
    	
    
    	
    
    }
}