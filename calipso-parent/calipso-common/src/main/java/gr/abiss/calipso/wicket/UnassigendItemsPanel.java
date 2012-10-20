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

import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.Space;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
/**
 * @author marcello
 */
public class UnassigendItemsPanel extends BasePanel {

	public UnassigendItemsPanel(String id, final IBreadCrumbModel breadCrumbModel, final Space space, boolean isSingleSpace) {
		super(id, breadCrumbModel);
		
		WebMarkupContainer unassigned = new WebMarkupContainer("unassigned");
		
		int unassignedItems = getCalipso().loadCountUnassignedItemsForSpace(space);
		
		if (unassignedItems>0){
			WebMarkupContainer link;
			if (!isSingleSpace){
				link = new BreadCrumbLink("link", getBreadCrumbModel()){
					@Override
					protected IBreadCrumbParticipant getParticipant(String componentId) {
	                    setCurrentSpace(space);
	                    ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);
	                    itemSearch.setUnassigned();
	                    setCurrentItemSearch(itemSearch);
	                    return new SingleSpacePanel(componentId, getBreadCrumbModel(), itemSearch);
					}
					
				};
			}
			else{
				link = new IndicatingAjaxLink("link") {
	                public void onClick(AjaxRequestTarget target) {
	                    ItemSearch itemSearch = new ItemSearch(space, getPrincipal(), this);
	                    itemSearch.setUnassigned();
	                    setCurrentItemSearch(itemSearch);
	
	                    SingleSpacePanel singleSpacePanel = (SingleSpacePanel) breadCrumbModel.getActive();
	                    singleSpacePanel.refreshItemListPanel(target);
	                }
	            };
			}

			unassigned.add(link);

			link.add(new Label("unassignedCount", new Model(new Long(unassignedItems))));
		}
		else{
			unassigned.setVisible(false);
		}
		add(unassigned);
	}
}