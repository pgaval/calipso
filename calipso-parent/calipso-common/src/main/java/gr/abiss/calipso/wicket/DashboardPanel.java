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

import gr.abiss.calipso.domain.Counts;
import gr.abiss.calipso.domain.CountsHolder;
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.UserSpaceRole;
import gr.abiss.calipso.wicket.hlpcls.SpaceAssetAdminLink;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.log4j.Logger;


/**
 * @author erasmus
 */
public class DashboardPanel extends BasePanel {
	
	private static final Logger logger = Logger.getLogger(DashboardPanel.class);
	ArrayList<Long> expandedRowsList = new ArrayList<Long>(); 

	/**
	 * 
	 * @param id
	 * @param breadCrumbModel
	 */
	public DashboardPanel(String id, IBreadCrumbModel breadCrumbModel) {
		this(id, breadCrumbModel, false);	
	}
	
	/**
	 * 
	 * @param id
	 * @param breadCrumbModel
	 * @param isSingleSpace
	 */
	@SuppressWarnings({ "unchecked", "serial" })
	public DashboardPanel(String id, IBreadCrumbModel breadCrumbModel, final boolean isSingleSpace) {
		super(id, breadCrumbModel);
		if(!isSingleSpace){
			setCurrentSpace(null);
		}
		
		final User user = getPrincipal();
		// current space???
		List<UserSpaceRole> spaceRoles = new ArrayList<UserSpaceRole>(user.getSpaceRolesNoGlobal());		
		 WebMarkupContainer table = new WebMarkupContainer("table");
        WebMarkupContainer message = new WebMarkupContainer("message");
        
        // if only one space exist for the user, and user has roles in this space
        if(isSingleSpace && spaceRoles.size() > 0){
	        UserSpaceRole singleUSR = null;
	        // if only one space exist then the first space is the the current space
	        final Space singleSpace = getCurrentSpace();//spaceRoles.get(0).getSpaceRole().getSpace();
	        //setCurrentSpace(singleSpace);
	        // try to obtain a non-Guest role for user
	        for(UserSpaceRole u: spaceRoles){
	        	u.getSpaceRole().getSpace();
	        	if(u.getSpaceRole().getSpace().equals(singleSpace)){
	        		singleUSR = u;
	        		break;
	        	}
	        }
	        /* MOVED this to CalipsoServiceImpl for login
        	// if no match was found for a non-guest role but space is open to guests, 
        	// add the Guest role to the user
	        if(singleUSR == null && singleSpace.isGuestAllowed()){
	        	for(SpaceRole spaceRole: singleSpace.getSpaceRoles()){
		        	if(spaceRole.getRoleType().getType().equals(Type.GUEST)){
		        		singleUSR = new UserSpaceRole(user, spaceRole);
		        		break;
		        	}
		        }
	        	if(logger.isDebugEnabled()){
	        		logger.debug("Found no Roles for the user in this space but Guest is allowed, added role: "+singleUSR);
	        	}
	        }
			*/
	        spaceRoles = new ArrayList();
	        spaceRoles.add(singleUSR);
	        
	        if(singleUSR.isAbleToCreateNewItem()) {
	        	add(new BreadCrumbLink("new", breadCrumbModel){
	        		protected IBreadCrumbParticipant getParticipant(String componentId){
	    				return new ItemFormPanel(componentId, getBreadCrumbModel());
	        	    }
	        	});
	        } else {
	            add(new WebMarkupContainer("new").setVisible(false));
	        }

	        if (singleSpace.isAssetEnabled()){
	        	SpaceAssetAdminLink spaceAssetAdminLink = new SpaceAssetAdminLink("asset", getBreadCrumbModel()){
	        		@Override
	        		public void onLinkActivate() {
	        			
	        		}//onLinkActivate
	        	};
	        	spaceAssetAdminLink.setVisible(user.isGlobalAdmin() || user.isSpaceAdmin(singleSpace));
	        	add(spaceAssetAdminLink);
	        }
	        else{
	        	add(new WebMarkupContainer("asset").setVisible(false));
	        }

	        add(new BreadCrumbLink("search", breadCrumbModel){
	    		protected IBreadCrumbParticipant getParticipant(String componentId){
					return new ItemSearchFormPanel(componentId, getBreadCrumbModel());
	    	    }
	    	});
	        
	        String spaceName = localize(getCurrentSpace().getNameTranslationResourceKey());
	        //add overview title for single space	        
	        add(new Label("dashboardTitle", localize("dashboard.title.overview", spaceName)).setRenderBodyOnly(true));
			//remove space name title if single space
	        table.add(new WebMarkupContainer("spaceTitle").setVisible(false));
	        //add help message
	        table.add(new Label("DashboardPanelHelp", localize("DashboardPanel.SingleSpace.help", spaceName)).setRenderBodyOnly(true));
        }
        // many spaces
        else{
        	setCurrentSpace(null);
        	//add overview title for dashboard spaces
        	add(new Label("dashboardTitle", localize("dashboard.title.mySpaces")).setRenderBodyOnly(true));
	    	//add space title for dashboard spaces
        	table.add(new Label("spaceTitle", localize("dashboard.space")));
        	//add help message
        	table.add(new Label("DashboardPanelHelp", localize("DashboardPanel.help")));
        	// hide
        	add(new WebMarkupContainer("new").setVisible(false));
        	add(new WebMarkupContainer("asset").setVisible(false));
        	add(new WebMarkupContainer("search").setVisible(false));
        }
        
        add(table);
        add(message);
        
        // TODO: this should actually present totals for public spaces.
        if(spaceRoles.size() > 0) {   
        	// if many spaces there is no current space
        	// check loggedBy,assignedTo,Unassigned counts
        	
            final CountsHolder countsHolder = getCalipso().loadCountsForUser(user);

            WebMarkupContainer hideLogged = new WebMarkupContainer("hideLogged");
            WebMarkupContainer hideAssigned = new WebMarkupContainer("hideAssigned");
            WebMarkupContainer hideUnassigned = new WebMarkupContainer("hideUnassigned");
            
            if(user.getId() == 0) {
                hideLogged.setVisible(false);
                hideAssigned.setVisible(false);
                hideUnassigned.setVisible(false);
            }
            table.add(hideLogged);
            table.add(hideAssigned);
            table.add(hideUnassigned);
            
            TreeSet<UserSpaceRole> sortedBySpaceCode = new TreeSet<UserSpaceRole>(new UserSpaceRoleComparator());
            sortedBySpaceCode.addAll(spaceRoles);
            List<UserSpaceRole> sortedBySpaceCodeList = new ArrayList<UserSpaceRole>(sortedBySpaceCode.size());
            sortedBySpaceCodeList.addAll(sortedBySpaceCode);
            table.add(new ListView<UserSpaceRole>("dashboardRows", sortedBySpaceCodeList) {
                protected void populateItem(final ListItem listItem) {
                    UserSpaceRole userSpaceRole = (UserSpaceRole) listItem.getModelObject();
                    // TODO: this should happen onclick
                    //logger.info("populateItem, userSpaceRole.getSpaceRole().getSpace(): "+userSpaceRole.getSpaceRole().getSpace());
                    Counts counts = countsHolder.getCounts().get(userSpaceRole.getSpaceRole().getSpace().getId());
                    if (counts == null) {
                        counts = new Counts(false); // this can happen if fresh space
                    }
                    
                    boolean isOddLine;
                    if(listItem.getIndex() % 2 == 1) {
                    	isOddLine = true;
                    }else{
                    	isOddLine = false;
                    }

                    MarkupContainer dashboardRow;
                    
                    //if single space, render expanded row
                    if((isSingleSpace && getCurrentSpace() != null) || isRowExpanded(userSpaceRole)){
                        if (!counts.isDetailed()) {
                        	counts = getCalipso().loadCountsForUserSpace(user, userSpaceRole.getSpaceRole().getSpace());                    
                        }
                    	dashboardRow = new DashboardRowExpandedPanel("dashboardRow", getBreadCrumbModel(), userSpaceRole, counts, isSingleSpace).setOddLine(isOddLine);
                    }
                    else{
                    	dashboardRow = new DashboardRowPanel("dashboardRow", getBreadCrumbModel(), userSpaceRole, counts, isSingleSpace).setOddLine(isOddLine);  
                    }
                    listItem.add(dashboardRow);
                }
            });
            
           //	SimpleAttributeModifier colSpan = new SimpleAttributeModifier("colspan", user.isAdminForAllSpaces()?"3":"2");

            SimpleAttributeModifier colSpan = new SimpleAttributeModifier("colspan", "3");
            Label hAction = new Label("hAction", localize("dashboard.action"));
            hAction.add(colSpan);
            table.add(hAction);

            // TODO panelize totals row and reduce redundant code
            WebMarkupContainer total = new WebMarkupContainer("total");
            total.add(new Label("allSpaces", localize("item_search_form.allSpaces")).setRenderBodyOnly(true).setVisible(!user.isAnonymous()));

            if(spaceRoles.size() > 1) {
            	Label hTotal = new Label("hTotal");
            	hTotal.add(colSpan);
            	total.add(hTotal);
            	total.add(new BreadCrumbLink("search", getBreadCrumbModel()) {
					@Override
					protected IBreadCrumbParticipant getParticipant(String componentId) {
						return new ItemSearchFormPanel(componentId, getBreadCrumbModel());
					}
				
				}.setVisible(!user.isAnonymous()));

                if(user.getId() > 0) {     
                	total.add(new BreadCrumbLink("loggedByMe", breadCrumbModel){
                		protected IBreadCrumbParticipant getParticipant(String componentId){
                            ItemSearch itemSearch = new ItemSearch(user, DashboardPanel.this);
                            itemSearch.setLoggedBy(user);
                            setCurrentItemSearch(itemSearch);
                            
            				return new ItemListPanel(componentId, getBreadCrumbModel());
                	    }
                	}.add(new Label("loggedByMe", new PropertyModel(countsHolder, "totalLoggedByMe"))));
                	

                    total.add(new BreadCrumbLink("assignedToMe", breadCrumbModel){
                		protected IBreadCrumbParticipant getParticipant(String componentId){
                            ItemSearch itemSearch = new ItemSearch(user, DashboardPanel.this);
                            itemSearch.setAssignedTo(user);
                            setCurrentItemSearch(itemSearch);
                            
            				return new ItemListPanel(componentId, getBreadCrumbModel());
                        }
                    }.add(new Label("assignedToMe", new PropertyModel(countsHolder, "totalAssignedToMe"))));
                    
                    
                    total.add(new BreadCrumbLink("unassigned", breadCrumbModel){
                		protected IBreadCrumbParticipant getParticipant(String componentId){
    	        			ItemSearch itemSearch = new ItemSearch(user, DashboardPanel.this);
    	        			itemSearch.setUnassigned();
    	                    setCurrentItemSearch(itemSearch);
    	                           
            				return new ItemListPanel(componentId, getBreadCrumbModel());
                        }
                    }.add(new Label("unassigned", new PropertyModel(countsHolder, "totalUnassigned"))));
                    
                } else {
                    total.add(new WebMarkupContainer("loggedByMe").setVisible(false));
                    total.add(new WebMarkupContainer("assignedToMe").setVisible(false));
                    total.add(new WebMarkupContainer("unassigned").setVisible(false));
                }

                total.add(new BreadCrumbLink("total", breadCrumbModel){
            		protected IBreadCrumbParticipant getParticipant(String componentId){
                        ItemSearch itemSearch = new ItemSearch(user, DashboardPanel.this);
                        setCurrentItemSearch(itemSearch);
                        
        				return new ItemListPanel(componentId, getBreadCrumbModel());
            	    }
                }.add(new Label("total", new PropertyModel(countsHolder, "totalTotal"))).setVisible(!user.isAnonymous()));

            }
            else {             
                total.setVisible(false);
            }   
            table.add(total/*.setVisible(!user.isAnonymous())*/);
            message.setVisible(false);
        } else {
            table.setVisible(false);            
        }

		
	}
	public class UserSpaceRoleComparator implements Comparator<UserSpaceRole>{

		@Override
		public int compare(UserSpaceRole o1, UserSpaceRole o2) {
			int result = 0;
			try{
				
				result = o1.getSpaceRole().getSpace().getPrefixCode().compareTo(o2.getSpaceRole().getSpace().getPrefixCode());
			}
			catch(RuntimeException e){
				logger.error(e);
			}
			return result;
		}
		
	}
	
	public void markRowExpanded(UserSpaceRole usr) {
		if(!expandedRowsList.contains(usr.getId())){
			expandedRowsList.add(usr.getId());
		}		
	}
	
	public boolean isRowExpanded(UserSpaceRole usr) {
		return expandedRowsList.contains(usr.getId());
	}
	
	public void unmarkRowExpanded(UserSpaceRole usr) {
		expandedRowsList.remove(usr.getId());
	}
	
	public String getTitle(){
        return localize("header.dashboard");
    }


}

