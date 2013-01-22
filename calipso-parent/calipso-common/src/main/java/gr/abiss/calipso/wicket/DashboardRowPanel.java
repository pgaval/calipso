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

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.Counts;
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.UserSpaceRole;
import gr.abiss.calipso.util.UserUtils;
import gr.abiss.calipso.wicket.asset.AssetSpacePanel;

import org.apache.log4j.Logger;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;

/**
 * Shows overall space view/statistics for a space and offers an expanded view through ajax
 */
public class DashboardRowPanel extends BasePanel {
	
	private static final long serialVersionUID = 1L;
	
	protected static final Logger logger = Logger.getLogger(DashboardRowPanel.class);
	private boolean isOddLine = false;
	
	public DashboardRowPanel(String id, IBreadCrumbModel breadCrumbModel, final UserSpaceRole usr, final Counts counts){
		this(id, breadCrumbModel, usr, counts, false);
	}
	
    public DashboardRowPanel(String id, IBreadCrumbModel breadCrumbModel, final UserSpaceRole userSpaceRole, final Counts counts, final boolean useCurrentSpace) {
        
        super(id, breadCrumbModel);
        setOutputMarkupId(true);
        CalipsoService calipso = getCalipso();
        
        // needed for links like (space, assets)
        // TODO: holds last space
        final Space space = userSpaceRole.getSpaceRole().getSpace();
        
        final User user = userSpaceRole.getUser();
        //setCurrentSpace(space);

        boolean canViewItems = UserUtils.canViewItems(user, space);
        
        // no need to set manually the space, because it has reference
        MarkupContainer spaceLink = canViewItems ? new BreadCrumbLink("spaceName", getBreadCrumbModel()) {
					private static final long serialVersionUID = 1L;
		
					@Override
					protected IBreadCrumbParticipant getParticipant(String componentId) {
						return new SingleSpacePanel(componentId, getBreadCrumbModel(), new ItemSearch(space, getPrincipal(), this, DashboardRowPanel.this.getCalipso()));
					}
				}
	        : (MarkupContainer) new WebMarkupContainer("spaceName").setRenderBodyOnly(true);
		
		Label spaceNameLabel = new Label("spaceNameLabel", localize(space.getNameTranslationResourceKey()));//space name
		if(space.getDescription() != null){
			spaceNameLabel.add(new SimpleAttributeModifier("title",space.getDescription()));//space description
		}
		
		spaceLink.add(spaceNameLabel);
        
		//if in single space, don't render name
        if(useCurrentSpace){
        	spaceLink.setVisible(false);
        }        
        
        add(spaceLink);
        
        if(userSpaceRole.isAbleToCreateNewItem()) {
        	add(new BreadCrumbLink("new", getBreadCrumbModel()){
        	
				private static final long serialVersionUID = 1L;

				protected IBreadCrumbParticipant getParticipant(String componentId){
        			// need to added manually because the item doesn't have space reference
        			setCurrentSpace(space);
    				return new ItemFormPanel(componentId, getBreadCrumbModel());
        	    }
        	});
        } else {
            add(new Label("new").setVisible(false));
        }

        //TODO: For future use
        add(new Link("sla") {
            
			private static final long serialVersionUID = 1L;

			public void onClick() {
            	setCurrentSpace(space);
                setResponsePage(SLAsPage.class);
            }
        }.setVisible(false));

        if (UserUtils.canViewSpaceAssets(user, space, calipso)){
        	add(new BreadCrumbLink("asset", getBreadCrumbModel()){
        	
				private static final long serialVersionUID = 1L;

				protected IBreadCrumbParticipant getParticipant(String componentId){
        			// on click current space is the this panel space
        			setCurrentSpace(space);
    				return new AssetSpacePanel(componentId, getBreadCrumbModel());
        	    }
        	}.setVisible(user.isGlobalAdmin() || user.isSpaceAdmin(space)));
        }
        else{
        	add(new Label("asset").setVisible(false));
        }

        if(canViewItems){
	        add(new BreadCrumbLink("search", getBreadCrumbModel()){
	    		
				private static final long serialVersionUID = 1L;
	
				protected IBreadCrumbParticipant getParticipant(String componentId){
	    			setCurrentSpace(space);
					return new ItemSearchFormPanel(componentId, getBreadCrumbModel());
	    	    }
	    	});
        }
        else{
        	add(new Label("search").setVisible(false));
        }
        
        add(new IndicatingAjaxLink("link") {
        	
            public void onClick(AjaxRequestTarget target) {
            	//mark expanded in DashboardPanel
            	IBreadCrumbParticipant activePanel = getBreadCrumbModel().getActive();
            	if (activePanel instanceof DashboardPanel) {
					((DashboardPanel) activePanel).markRowExpanded(userSpaceRole);									
				}
                Counts tempCounts = counts;                
                // avoid hitting the database again if re-expanding
                if (!tempCounts.isDetailed()) {                    
                    tempCounts = getCalipso().loadCountsForUserSpace(user, space);                    
                }
                DashboardRowExpandedPanel dashboardRow =
                	new DashboardRowExpandedPanel("dashboardRow", getBreadCrumbModel(), 
                			userSpaceRole, tempCounts, useCurrentSpace);
                dashboardRow.setOddLine(isOddLine);
                DashboardRowPanel.this.replaceWith(dashboardRow);
                target.addComponent(dashboardRow);
            }
        }.setVisible(canViewItems));       

        if(canViewItems) {
            WebMarkupContainer loggedByMeContainer;
            WebMarkupContainer assignedToMeContainer;
            WebMarkupContainer unassignedContainer;
            
	        if(useCurrentSpace){//if a space is selected
	        	loggedByMeContainer = new IndicatingAjaxLink("loggedByMe") {
	                public void onClick(AjaxRequestTarget target) {
	                	setCurrentSpace(space);
	                    ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this, DashboardRowPanel.this.getCalipso());
	                    itemSearch.setLoggedBy(user);
	                    setCurrentItemSearch(itemSearch);
	
	                    SingleSpacePanel singleSpacePanel = (SingleSpacePanel) getBreadCrumbModel().getActive();
	                    singleSpacePanel.refreshItemListPanel(target);
	                }
	            };
	            
	            assignedToMeContainer = new IndicatingAjaxLink("assignedToMe") {
	                public void onClick(AjaxRequestTarget target) {
	                    ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this, DashboardRowPanel.this.getCalipso());
	                    itemSearch.setAssignedTo(user);
	                    setCurrentItemSearch(itemSearch);
	
	                    SingleSpacePanel singleSpacePanel = (SingleSpacePanel) getBreadCrumbModel().getActive();
	                    singleSpacePanel.refreshItemListPanel(target);
	                }
	            };
	            
	            unassignedContainer = new IndicatingAjaxLink("unassigned") {
	                public void onClick(AjaxRequestTarget target) {
	        			ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this, DashboardRowPanel.this.getCalipso());
	        			itemSearch.setUnassigned();
	                    setCurrentItemSearch(itemSearch);
	
	                    SingleSpacePanel singleSpacePanel = (SingleSpacePanel) getBreadCrumbModel().getActive();
	                    singleSpacePanel.refreshItemListPanel(target);
	                }
	            };
	        }
	        else{//if no space is selected. i.e. for dashboard
	        	loggedByMeContainer = new BreadCrumbLink("loggedByMe", getBreadCrumbModel()){
	        	
					private static final long serialVersionUID = 1L;

					protected IBreadCrumbParticipant getParticipant(String componentId){
	                    ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this, DashboardRowPanel.this.getCalipso());
	                    itemSearch.setLoggedBy(user);
	                    setCurrentItemSearch(itemSearch);
	                    return new SingleSpacePanel(componentId, getBreadCrumbModel(), itemSearch);
	    				//return new ItemListPanel(componentId, getBreadCrumbModel());
	        	    }
	        	};
	        	
	        	assignedToMeContainer = new BreadCrumbLink("assignedToMe", getBreadCrumbModel()){
	        		
					private static final long serialVersionUID = 1L;

					protected IBreadCrumbParticipant getParticipant(String componentId){
	                    ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this, DashboardRowPanel.this.getCalipso());
	                    itemSearch.setAssignedTo(user);
	                    setCurrentItemSearch(itemSearch);
	                    
	                    return new SingleSpacePanel(componentId, getBreadCrumbModel(), itemSearch);
	    				//return new ItemListPanel(componentId, getBreadCrumbModel());
	        	    }
	        	};
	        	
	        	unassignedContainer = new BreadCrumbLink("unassigned", getBreadCrumbModel()){
	        		
					private static final long serialVersionUID = 1L;

					protected IBreadCrumbParticipant getParticipant(String componentId){
	                    ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this, DashboardRowPanel.this.getCalipso());
	                    itemSearch.setUnassigned();
	                    setCurrentItemSearch(itemSearch);
	
	                    return new SingleSpacePanel(componentId, getBreadCrumbModel(), itemSearch);
	        	    }
	        	}; 
	        }
	        
	        //get Total
	        Long total = new Long(counts.getTotal());
	        
	        //add the numbers
	        loggedByMeContainer.add(new DashboardNumbers("loggedByMeNumbers", new Long(counts.getLoggedByMe()), total));
	        assignedToMeContainer.add(new DashboardNumbers("assignedToMeNumbers", new Long(counts.getAssignedToMe()), total));
	        unassignedContainer.add(new DashboardNumbers("unassignedNumbers", new Long(counts.getUnassigned()), total));
	        
	        //add the containers
	        add(loggedByMeContainer);
	        add(assignedToMeContainer);
	        add(unassignedContainer);
        }
        else {
            add(new WebMarkupContainer("loggedByMe").setVisible(false));
            add(new WebMarkupContainer("assignedToMe").setVisible(false));
            add(new WebMarkupContainer("unassigned").setVisible(false));
        }
        
        
        WebMarkupContainer totalContainer;
        if(useCurrentSpace){//if a space is selected
        	totalContainer = new IndicatingAjaxLink("total") {
                public void onClick(AjaxRequestTarget target) {
                    ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this, DashboardRowPanel.this.getCalipso()); 
                    setCurrentItemSearch(itemSearch);

                    SingleSpacePanel singleSpacePanel = (SingleSpacePanel) getBreadCrumbModel().getActive();
                    singleSpacePanel.refreshItemListPanel(target);
                }
            };
        }
        else{//if no space is selected. i.e. for dashboard
        	totalContainer = new BreadCrumbLink("total", getBreadCrumbModel()){
        		
				private static final long serialVersionUID = 1L;

				protected IBreadCrumbParticipant getParticipant(String componentId){
                    ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this, DashboardRowPanel.this.getCalipso()); 
                    setCurrentItemSearch(itemSearch);
                    
                    return new SingleSpacePanel(componentId, getBreadCrumbModel(), itemSearch);
    				//return new ItemListPanel(componentId, getBreadCrumbModel());
        	    }
        	};
        }
        
        totalContainer.add(new Label("total", new PropertyModel(counts, "total")));
        add(totalContainer.setRenderBodyOnly(user.isAnonymous()));
    }
    
    public DashboardRowPanel setOddLine(boolean isOddLine) {
    	if(isOddLine){
    		this.isOddLine = true;
    		add(new SimpleAttributeModifier("class", "alt"));
    	}
    	return this;
	}
}
