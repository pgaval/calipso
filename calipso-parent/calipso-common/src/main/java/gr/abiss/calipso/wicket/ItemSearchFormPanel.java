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

import java.util.List;

import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.SavedSearch;
import gr.abiss.calipso.wicket.hlpcls.ExpandAssetSearchLink;
import gr.abiss.calipso.wicket.hlpcls.ExpandPanelSimple;

import org.apache.log4j.Logger;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 * item search form page
 */
public class ItemSearchFormPanel extends BasePanel {   
    
	protected static final Logger logger = Logger.getLogger(ItemSearchFormPanel.class);  
    private SavedSearchList savedSearchList = null;
	private WebMarkupContainer savedSearchContainer;
	private WebMarkupContainer savedSearchPlaceHolder;
	
	private boolean isSavedSearchListOpen = false;
	
	public String getTitle(){
        return localize("item_search_form.title");
    }
	
    public ItemSearchFormPanel(String id, IBreadCrumbModel breadCrumbModel) {
    	super(id, breadCrumbModel);
    	
    	refreshParentPageHeader();
    	addComponents();
    	
        add(new ItemSearchForm("panel", breadCrumbModel));
        add(new WebMarkupContainer("relate").setVisible(false));
    }       
    
    public ItemSearchFormPanel(String id, IBreadCrumbModel breadCrumbModel, ItemSearch itemSearch) {
    	super(id, breadCrumbModel);
    	
    	refreshParentPageHeader();
    	addComponents();

    	setCurrentItemSearch(itemSearch);
        add(new ItemSearchForm("panel", breadCrumbModel, itemSearch));
        add(new ItemRelatePanel("relate", false));
    }    
    
    public ItemSearchFormPanel(String id, IBreadCrumbModel breadCrumbModel, ItemSearch itemSearch, boolean isSavedSearchListOpen) {
    	this(id, breadCrumbModel, itemSearch);
    	this.isSavedSearchListOpen = isSavedSearchListOpen;
    	
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
	private void addComponents(){
		if (getCurrentSpace()!=null){
			setInfo(localize("item_search_form.currentSpace", localize(getCurrentSpace().getNameTranslationResourceKey())));
		}//if
		else{
			setInfo(localize("item_search_form.noCurrentSpace"));
		}//else

		searchOnAnotherSpace();
		savedSearchList();
	}


	// ----------------------------------------------------------------------------------------------------
	/**
	 * Renders search on another space functionality
	 * */
	private void searchOnAnotherSpace(){
		if (getPrincipal()!=null && getPrincipal().getSpaces()!=null){
			WebMarkupContainer searchOnAnotherSpacePanel = new WebMarkupContainer("searchOnAnotherSpacePanel");
			add(searchOnAnotherSpacePanel);
			searchOnAnotherSpacePanel.setOutputMarkupId(true);
			
			setOutputMarkupId(true);
			
			ExpandPanelSimple searchOnAnotherSpaceLink = new ExpandPanelSimple("searchOnAnotherSpaceLink", ItemSearchFormPanel.this,
					new SearchOnAnotherSpacePanel("searchOnAnotherSpacePanel"), searchOnAnotherSpacePanel){
				@Override
				public void onLinkClick() {
					
					
				}
			};
			
			add(searchOnAnotherSpaceLink);
		}//if
		else{
			add(new WebMarkupContainer("searchOnAnotherSpaceLink").setVisible(false));
			add(new WebMarkupContainer("searchOnAnotherSpacePanel").setVisible(false));
		}//else		
	}//searchOnAnotherSpace
	
	// ----------------------------------------------------------------------------------------------------
	
	/**
	 * Renders search panel
	 * */
	private void savedSearchList(){	
		// --- container -------------------------------------------
		savedSearchContainer = new WebMarkupContainer("savedSearchContainer");
		add(savedSearchContainer);

		// -- place holder ----------------------------------------
		savedSearchPlaceHolder = new WebMarkupContainer("savedSearchPlaceHolder");
		savedSearchContainer.add(savedSearchPlaceHolder);
		
		// -- Page is (re)loaded, and the search panel is open ----
		if (isSavedSearchListOpen){
			savedSearchList = new SavedSearchList(getId(), getBreadCrumbModel(), getPrincipal());
			savedSearchContainer.add(savedSearchPlaceHolder);
			savedSearchPlaceHolder.add(savedSearchList);
		}
		// -- Page is (re)loaded, and the search panel is closed ----		
		else{
			//Set place holder to not visible
			savedSearchPlaceHolder.setVisible(false);
		}

		List<SavedSearch> savedSearchesList = null;
		if (getPrincipal()!=null && getPrincipal().getId()!=0){
			savedSearchesList = getCalipso().findSavedSearches(getPrincipal(), getCurrentSpace());
		}//if
		
		//Show saved Searches Area, only if user is logged in and user has at list one Saved Search. 
		if (getPrincipal()!=null && getPrincipal().getId()!=0 && savedSearchesList!=null && savedSearchesList.size()>0){
			//setOutputMarkupId, needed for ajax
			savedSearchContainer.setOutputMarkupId(true);
	
			ExpandAssetSearchLink savedSearchesLink = new ExpandAssetSearchLink("savedSearchesLink",
					savedSearchContainer, savedSearchContainer, savedSearchPlaceHolder, new SavedSearchList("savedList", getBreadCrumbModel(), getPrincipal()), isSavedSearchListOpen){
				@Override
				public void onLinkClick() {
					ItemSearchFormPanel.this.isSavedSearchListOpen = this.isOpen();
				}
			};
	
			add(savedSearchesLink);
			savedSearchesLink.add(new Label("savedSearchLabel", new Model(localize("item_saved_search.showListOfSavedSearches"))));
			add(savedSearchContainer);
		}
		else{
			add(new WebMarkupContainer("savedSearchesLink").setVisible(false));
			add(savedSearchContainer.setVisible(false));
		}
	}//searchAssetCustomAttributes
}