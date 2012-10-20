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

import gr.abiss.calipso.domain.Counts;
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.State;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.UserSpaceRole;
import gr.abiss.calipso.wicket.asset.AssetSpacePanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

/**
 * panel for expanded view of statistics for a single space 
 */
public class DashboardRowExpandedPanel extends BasePanel {	

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(DashboardRowExpandedPanel.class);
	
	private boolean isOddLine = false;

	public DashboardRowExpandedPanel(String id, IBreadCrumbModel breadCrumbModel, final UserSpaceRole userSpaceRole, final Counts counts) {
		this(id, breadCrumbModel, userSpaceRole, counts, false);
	}
	
    public DashboardRowExpandedPanel(String id, IBreadCrumbModel breadCrumbModel,
    		final UserSpaceRole userSpaceRole, final Counts counts, final boolean isSingleSpace) {        
        super(id, breadCrumbModel);
        
        setOutputMarkupId(true);
        setCurrentSpace(userSpaceRole.getSpaceRole().getSpace());
        final Space space = getCurrentSpace();
        refreshParentMenu(breadCrumbModel);
        
        final User user = userSpaceRole.getUser();
        
        final Map<Integer, String> states = new TreeMap(space.getMetadata().getStatesMap());    
        states.remove(State.NEW);
        int rowspan = states.size() + 1; // add one totals row also
        
        int totalUnassignedItems = getCalipso().loadCountUnassignedItemsForSpace(space);
        
        if (totalUnassignedItems>0){
        	rowspan++; //for unassiged items
        }
        
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("rowspan", rowspan + "");
        final SimpleAttributeModifier altClass = new SimpleAttributeModifier("class", "alt");
        List<Integer> stateKeys = new ArrayList<Integer>(states.keySet());

        add(new ListView("rows", stateKeys) {
            
            protected void populateItem(ListItem listItem) {                                
            	if(listItem.getIndex() % 2 == 1) {
                    listItem.add(altClass);
                }        
                if (listItem.getIndex() == 0) { // rowspan output only for first row                     	
                	BreadCrumbLink spaceLink = new BreadCrumbLink("spaceName", getBreadCrumbModel()) {
            			@Override
            			protected IBreadCrumbParticipant getParticipant(String componentId) {
            				ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);
            				return new SingleSpacePanel(componentId, getBreadCrumbModel(), itemSearch);
            			}
            		};
            		
            		spaceLink.add(new Label("spaceName", localize(space.getNameTranslationResourceKey())));
            		spaceLink.add(sam);
                    
            		//if in single space, don't render name
                    if(isSingleSpace){
                    	spaceLink.setVisible(false);
                    }        
                    
                    listItem.add(spaceLink);
                                                            
                    WebMarkupContainer newColumn = new WebMarkupContainer("new");
                    newColumn.add(sam);   
                    listItem.add(newColumn);
                    
                    if(userSpaceRole.isAbleToCreateNewItem()) {
                    	newColumn.add(new BreadCrumbLink("new", getBreadCrumbModel()){
                    		protected IBreadCrumbParticipant getParticipant(String componentId){
                				return new ItemFormPanel(componentId, getBreadCrumbModel());
                    	    }
                    	});
                    } else {
                        newColumn.add(new WebMarkupContainer("new").setVisible(false));
                    }

                    
                    //TODO: For future use
                    WebMarkupContainer slaColumn = new WebMarkupContainer("sla");
                    slaColumn.add(sam);   
                    
                    
                    slaColumn.add(new Link("sla") {
                            public void onClick() {
                                setResponsePage(SLAsPage.class);
                            }
                        });
                    
                    listItem.add(slaColumn.setVisible(false));
                    
                    //Asset
                    WebMarkupContainer assetColumn = new WebMarkupContainer("asset");
                    assetColumn.add(sam);   
                    listItem.add(assetColumn);
                    
                    if (space.isAssetEnabled() && (user.isGlobalAdmin() || user.isSpaceAdmin(space))){                       	
                    	assetColumn.add(new BreadCrumbLink("asset", getBreadCrumbModel()){
                    		protected IBreadCrumbParticipant getParticipant(String componentId){
                				return new AssetSpacePanel(componentId, getBreadCrumbModel());
                    	    }
                    	}.setVisible(user.isGlobalAdmin() || user.isSpaceAdmin(space)));
                    	
                    } else {
                    	assetColumn.add(new WebMarkupContainer("asset").setVisible(false));
                    }
                        
                    listItem.add(new BreadCrumbLink("search", getBreadCrumbModel()){
                		protected IBreadCrumbParticipant getParticipant(String componentId){
            				return new ItemSearchFormPanel(componentId, getBreadCrumbModel());
                	    }
                	}.add(sam));

                    
                    WebMarkupContainer link = new WebMarkupContainer("link");
                    link.add(sam);
                    listItem.add(link);
                    
                    //if in single space, don't render expand/contract link
                    if(isSingleSpace){
                    	link.add(new WebMarkupContainer("link").setVisible(false));
                    }
                    else{
                    	link.add(new IndicatingAjaxLink("link") {
                            public void onClick(AjaxRequestTarget target) {
                            	//mark contracted in DashboardPanel
                            	IBreadCrumbParticipant activePanel = getBreadCrumbModel().getActive();
                            	if (activePanel instanceof DashboardPanel) {
									((DashboardPanel) activePanel).unmarkRowExpanded(userSpaceRole);									
								}
                            	
                                DashboardRowPanel dashboardRow = new DashboardRowPanel("dashboardRow", getBreadCrumbModel(), userSpaceRole, counts, isSingleSpace).setOddLine(isOddLine);
                                DashboardRowExpandedPanel.this.replaceWith(dashboardRow);
                                target.addComponent(dashboardRow);
                            }
                        });	
                    }
                    
                } else {
                    listItem.add(new WebMarkupContainer("spaceName").add(new WebMarkupContainer("spaceName")).setVisible(false));
                    listItem.add(new WebMarkupContainer("new").setVisible(false));
                    listItem.add(new WebMarkupContainer("sla").setVisible(false));
                    listItem.add(new WebMarkupContainer("asset").setVisible(false));
                    listItem.add(new WebMarkupContainer("search").setVisible(false));
                    listItem.add(new WebMarkupContainer("link").add(new WebMarkupContainer("link")).setVisible(false));
                }
               
                
                final Integer i = (Integer) listItem.getModelObject();
                listItem.add(new Label("status", states.get(i)));
                
                
                if(user.getId() > 0) {
                    WebMarkupContainer loggedByMeContainer;
                    WebMarkupContainer assignedToMeContainer;
                    WebMarkupContainer unassignedContainer;
                    
        	        if(isSingleSpace){//if a space is selected
        	        	loggedByMeContainer = new IndicatingAjaxLink("loggedByMe") {
        	                public void onClick(AjaxRequestTarget target) {
                                ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);
                                itemSearch.setLoggedBy(user);
                                itemSearch.setStatus(i);
                                setCurrentItemSearch(itemSearch);

        	                    SingleSpacePanel singleSpacePanel = (SingleSpacePanel) getBreadCrumbModel().getActive();
        	                    singleSpacePanel.refreshItemListPanel(target);
        	                }
        	            };
        	            
        	            assignedToMeContainer = new IndicatingAjaxLink("assignedToMe") {
        	                public void onClick(AjaxRequestTarget target) {
                                ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);
                                itemSearch.setAssignedTo(user);
                                itemSearch.setStatus(i);
                                setCurrentItemSearch(itemSearch);
                                
        	                    SingleSpacePanel singleSpacePanel = (SingleSpacePanel) getBreadCrumbModel().getActive();
        	                    singleSpacePanel.refreshItemListPanel(target);
        	                }
        	            };
        	            
        	            unassignedContainer = new IndicatingAjaxLink("unassigned") {
        	                public void onClick(AjaxRequestTarget target) {
        	        			ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);
        	        			itemSearch.setUnassigned();
        	                    itemSearch.setStatus(i);
        	                    setCurrentItemSearch(itemSearch);
        	
        	                    SingleSpacePanel singleSpacePanel = (SingleSpacePanel) getBreadCrumbModel().getActive();
        	                    singleSpacePanel.refreshItemListPanel(target);
        	                }
        	            };
        	        }
        	        else{//if no space is selected. i.e. for dashboard
        	        	loggedByMeContainer = new BreadCrumbLink("loggedByMe", getBreadCrumbModel()){
        	        		protected IBreadCrumbParticipant getParticipant(String componentId){
                                ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);
                                itemSearch.setLoggedBy(user);
                                itemSearch.setStatus(i);
                                setCurrentItemSearch(itemSearch);
                                
                                return new SingleSpacePanel(componentId, getBreadCrumbModel(), itemSearch);
                				//return new ItemListPanel(componentId, getBreadCrumbModel());
        	        	    }
        	        	};
        	        	
        	        	assignedToMeContainer = new BreadCrumbLink("assignedToMe", getBreadCrumbModel()){
        	        		protected IBreadCrumbParticipant getParticipant(String componentId){
                                ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);
                                itemSearch.setAssignedTo(user);
                                itemSearch.setStatus(i);
                                setCurrentItemSearch(itemSearch);
                                
                                return new SingleSpacePanel(componentId, getBreadCrumbModel(), itemSearch);
        	        	    }
        	        	};   
        	        	
        	        	unassignedContainer = new BreadCrumbLink("unassigned", getBreadCrumbModel()){
        	        		protected IBreadCrumbParticipant getParticipant(String componentId){
        	                    ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);
        	                    itemSearch.setUnassigned();
        	                    itemSearch.setStatus(i);
        	                    setCurrentItemSearch(itemSearch);
        	
        	                    return new SingleSpacePanel(componentId, getBreadCrumbModel(), itemSearch);
        	        	    }
        	        	};   
        	        }
        	        
        	        //get numbers for loggedByMe and assignedToMe
        	        Long total = counts.getTotalForState(i);
        	        
         	        //add the numbers
         	        loggedByMeContainer.add(new DashboardNumbers("loggedByMeNumbers", counts.getLoggedByMeForState(i), total));
        	        assignedToMeContainer.add(new DashboardNumbers("assignedToMeNumbers", counts.getAssignedToMeForState(i), total));
        	        unassignedContainer.add(new DashboardNumbers("unassignedNumbers", counts.getUnassignedForState(i), total));
        	        
        	        //add the containers
        	        listItem.add(loggedByMeContainer);
        	        listItem.add(assignedToMeContainer);
        	        listItem.add(unassignedContainer);
                }
                else {
                    listItem.add(new WebMarkupContainer("loggedByMe").setVisible(false));
                    listItem.add(new WebMarkupContainer("assignedToMe").setVisible(false));
                    listItem.add(new WebMarkupContainer("unassigned").setVisible(false));
                }
                
                
                WebMarkupContainer totalContainer;
                if(isSingleSpace){//if a space is selected
                	totalContainer = new IndicatingAjaxLink("total") {
                        public void onClick(AjaxRequestTarget target) {
                            ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);                        
                            itemSearch.setStatus(i);
                            setCurrentItemSearch(itemSearch);

                            SingleSpacePanel singleSpacePanel = (SingleSpacePanel) getBreadCrumbModel().getActive();
                            singleSpacePanel.refreshItemListPanel(target);
                        }
                    };
                }
                else{//if no space is selected. i.e. for dashboard
                	totalContainer = new BreadCrumbLink("total", getBreadCrumbModel()){
                		protected IBreadCrumbParticipant getParticipant(String componentId){
                            ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);                        
                            itemSearch.setStatus(i);
                            setCurrentItemSearch(itemSearch);
                            
                            return new SingleSpacePanel(componentId, getBreadCrumbModel(), itemSearch);
            				//return new ItemListPanel(componentId, getBreadCrumbModel());
                	    }
                    };
                }
                
                Long total = counts.getTotalForState(i);
                totalContainer.add(new Label("total", total==null?"":total.toString()));
                listItem.add(totalContainer);
            }            
        });
        
        // sub totals ==========================================================        

        if(user.getId() > 0) {
            WebMarkupContainer loggedByMeTotalContainer;
            WebMarkupContainer assignedToMeTotalContainer;
            WebMarkupContainer unassignedTotalContainer;
            
	        if(isSingleSpace){//if a space is selected
	        	loggedByMeTotalContainer = new IndicatingAjaxLink("loggedByMeTotal") {
	                public void onClick(AjaxRequestTarget target) {
	                    ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);
	                    itemSearch.setLoggedBy(user);
	                    setCurrentItemSearch(itemSearch);
	                    
	                    SingleSpacePanel singleSpacePanel = (SingleSpacePanel) getBreadCrumbModel().getActive();
	                    singleSpacePanel.refreshItemListPanel(target);
	                }
	            };
	            
	            assignedToMeTotalContainer = new IndicatingAjaxLink("assignedToMeTotal") {
	                public void onClick(AjaxRequestTarget target) {
	                    ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);
	                    itemSearch.setAssignedTo(user);
	                    setCurrentItemSearch(itemSearch);
	                    
	                    SingleSpacePanel singleSpacePanel = (SingleSpacePanel) getBreadCrumbModel().getActive();
	                    singleSpacePanel.refreshItemListPanel(target);
	                }
	            };
	            
	            unassignedTotalContainer = new IndicatingAjaxLink("unassignedTotal") {
	                public void onClick(AjaxRequestTarget target) {
	        			ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);
	        			itemSearch.setUnassigned();
	                    setCurrentItemSearch(itemSearch);
	
	                    SingleSpacePanel singleSpacePanel = (SingleSpacePanel) getBreadCrumbModel().getActive();
	                    singleSpacePanel.refreshItemListPanel(target);
	                }
	            };	            
	        }
	        else{//if no space is selected. i.e. for dashboard
	        	loggedByMeTotalContainer = new BreadCrumbLink("loggedByMeTotal", getBreadCrumbModel()){
	        		protected IBreadCrumbParticipant getParticipant(String componentId){
	                    ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);
	                    itemSearch.setLoggedBy(user);
	                    setCurrentItemSearch(itemSearch);
	                    
	                    return new SingleSpacePanel(componentId, getBreadCrumbModel(), itemSearch);
	    				//return new ItemListPanel(componentId, getBreadCrumbModel());
	        	    }
	        	};
	        	
	        	assignedToMeTotalContainer = new BreadCrumbLink("assignedToMeTotal", getBreadCrumbModel()){
	        		protected IBreadCrumbParticipant getParticipant(String componentId){
	                    ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);
	                    itemSearch.setAssignedTo(user);
	                    setCurrentItemSearch(itemSearch);
	                    
	                    return new SingleSpacePanel(componentId, getBreadCrumbModel(), itemSearch);
	    				//return new ItemListPanel(componentId, getBreadCrumbModel());
	        	    }
	        	};   
	        	
	        	unassignedTotalContainer = new BreadCrumbLink("unassignedTotal", getBreadCrumbModel()){
	        		protected IBreadCrumbParticipant getParticipant(String componentId){
	                    ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);
	                    itemSearch.setUnassigned();
	                    setCurrentItemSearch(itemSearch);
	
	                    return new SingleSpacePanel(componentId, getBreadCrumbModel(), itemSearch);
	        	    }
	        	};  
	        }
	        
	        

	        //get total
	        Long total = new Long(counts.getTotal());
	        
	        //add the numbers	        
	        loggedByMeTotalContainer.add(new DashboardNumbers("loggedByMeNumbers", new Long(counts.getLoggedByMe()), total));
	        assignedToMeTotalContainer.add(new DashboardNumbers("assignedToMeNumbers", new Long(counts.getAssignedToMe()), total));
	        unassignedTotalContainer.add(new DashboardNumbers("unassignedNumbers", new Long(counts.getUnassigned()), total));
	        
	        //add the containers
	        add(loggedByMeTotalContainer);
	        add(assignedToMeTotalContainer);
	        add(unassignedTotalContainer);
        }
        else {
            add(new WebMarkupContainer("loggedByMeTotal").setVisible(false));
            add(new WebMarkupContainer("assignedToMeTotal").setVisible(false));           
            add(new WebMarkupContainer("unassignedTotal").setVisible(false));
        }
        
        
        WebMarkupContainer totalTotalContainer;
        if(isSingleSpace){//if a space is selected
        	totalTotalContainer = new IndicatingAjaxLink("totalTotal") {
                public void onClick(AjaxRequestTarget target) {
                    ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);
                    setCurrentItemSearch(itemSearch);

                    SingleSpacePanel singleSpacePanel = (SingleSpacePanel) getBreadCrumbModel().getActive();
                    singleSpacePanel.refreshItemListPanel(target);
                }
            };
        }
        else{//if no space is selected. i.e. for dashboard
        	totalTotalContainer = new BreadCrumbLink("totalTotal", getBreadCrumbModel()){
        		protected IBreadCrumbParticipant getParticipant(String componentId){
                    ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);
                    setCurrentItemSearch(itemSearch);
                    
                    return new SingleSpacePanel(componentId, getBreadCrumbModel(), itemSearch);
    				//return new ItemListPanel(componentId, getBreadCrumbModel());
        	    }
            };
        }
        
        totalTotalContainer.add(new Label("total", new PropertyModel(counts, "total")));
        add(totalTotalContainer);
    }
    
    public DashboardRowExpandedPanel setOddLine(boolean isOddLine) {
    	if(isOddLine){
    		this.isOddLine = true;
    		add(new SimpleAttributeModifier("class", "alt"));
    	}
    	return this;
	}
    
}
