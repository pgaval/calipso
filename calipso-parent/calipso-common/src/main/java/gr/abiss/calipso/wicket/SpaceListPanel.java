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

import java.util.ArrayList;
import java.util.List;

import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.dto.AssetSearch;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.wicket.asset.AssetSpacePanel;
import gr.abiss.calipso.wicket.hlpcls.SpaceAssetAdminLink;
import gr.abiss.calipso.wicket.space.panel.SpacePanelLanguageSupport;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanelLink;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

/**
 * space management page
 */
public class SpaceListPanel extends BasePanel {
    
	protected static final Logger logger = Logger.getLogger(SpaceListPanel.class);
    
    private long selectedSpaceId;
    private boolean userIsSpaceAdmin;
    private User user;

    
    public String getTitle(){
        return localize("options.manageSpaces");
    }
    
    public void setSelectedSpaceId(long selectedSpaceId) {
        this.selectedSpaceId = selectedSpaceId;
    }
      
    public SpaceListPanel(String id, final IBreadCrumbModel breadCrumbModel) {
    	super(id, breadCrumbModel);
    	//BreadCrumbPanelLink createSpacePanelLink = new BreadCrumbPanelLink("create", this, SpaceFormPanel.class);
    	
    	user = getPrincipal();
    	userIsSpaceAdmin = user.isSpaceAdmin();
    	add(new BreadCrumbLink("create", getBreadCrumbModel()) {

			@Override
			protected IBreadCrumbParticipant getParticipant(String componentId) {
				return new SpacePanelLanguageSupport(componentId, getBreadCrumbModel());
			}
		});
    	//add(createSpacePanelLink.setVisible(user.isAdminForAllSpaces()));

        LoadableDetachableModel spaceListModel = new LoadableDetachableModel() {
            protected Object load() {
                logger.debug("Loading space list from database...");
                
                if (userIsSpaceAdmin && !user.isGlobalAdmin()){
                	return user.getSpacesWhereUserIsAdmin();
                }

                return getCalipso().findAllSpaces();
            }
        };

        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");

        ListView listView = new ListView("spaces", spaceListModel) {
            protected void populateItem(ListItem listItem) {                
                final Space space = (Space) listItem.getModelObject();     
                if (selectedSpaceId == space.getId()) {
                    listItem.add(new SimpleAttributeModifier("class", "selected"));
                } else if(listItem.getIndex() % 2 == 1) {
                    listItem.add(sam);
                }                                 
                listItem.add(new Label("prefixCode", new PropertyModel(space, "prefixCode")));
                listItem.add(new Label("name", localize(space.getNameTranslationResourceKey())));

                //Edit ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                listItem.add(new BreadCrumbLink("edit", getBreadCrumbModel()){
           	 		
					private static final long serialVersionUID = 1L;

					protected IBreadCrumbParticipant getParticipant(String componentId){
           	 			Space temp = getCalipso().loadSpace(space.getId());
           	 			//temp.getMetadata().getXmlString();  // hack to override lazy loading
           	 			temp.getSpaceGroup().getName();
           	 			return new SpacePanelLanguageSupport(componentId, getBreadCrumbModel(), temp);
           	 		}
           	 	});

              //Description ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                listItem.add(new Label("description", new PropertyModel(space, "description")));
                
                // space group name
                
                listItem.add(new Label("spaceGroupName", new PropertyModel(space, "spaceGroup.name")));

              //Allocate ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                listItem.add(new BreadCrumbLink("allocate", getBreadCrumbModel()){
					private static final long serialVersionUID = 1L;

					protected IBreadCrumbParticipant getParticipant(String componentId){
           	 			return new SpaceAllocatePanel(componentId, getBreadCrumbModel(), space.getId());
           	 		}
           	 	});

              //Asset ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                if (space.isAssetEnabled() && (user.isGlobalAdmin() || user.isSpaceAdmin(space))){
                	listItem.add(new BreadCrumbLink("asset", breadCrumbModel){
						private static final long serialVersionUID = 1L;

						protected IBreadCrumbParticipant getParticipant(String componentId){
							Asset asset = new Asset();
							asset.setSpace(space);
               	 			return new AssetSpacePanel("panel", getBreadCrumbModel(), new AssetSearch(asset, this), space.getId());
               	 		}
               	 	});
                }
                else{
                	listItem.add(new BlankPanel("asset").setVisible(true));
                }
            }
        };
        
        @SuppressWarnings("unchecked")
		boolean spacesExist = !((List<Space>)spaceListModel.getObject()).isEmpty();
        add(new WebMarkupContainer("listHead").setVisible(spacesExist));
        add(listView);
        WebMarkupContainer noData = new WebMarkupContainer("noData");
        noData.add(new BreadCrumbPanelLink("create", this, SpaceFormPanel.class).setVisible(user.isGlobalAdmin()));
        noData.setVisible(!spacesExist);
        
        add(noData);
    }
}