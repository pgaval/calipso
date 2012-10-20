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

package gr.abiss.calipso.wicket.helpMenu;

import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.SavedSearch;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.State;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.exception.CalipsoSecurityException;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.util.ItemUtils;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.ItemFormPage;
import gr.abiss.calipso.wicket.ItemFormPanel;
import gr.abiss.calipso.wicket.ItemListPanel;
import gr.abiss.calipso.wicket.SingleSpacePanel;
import gr.abiss.calipso.wicket.SpacePage;
import gr.abiss.calipso.wicket.asset.AssetSpacePage;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.value.ValueMap;
import org.hibernate.Hibernate;
/**
 * Help menu is a menu, that has the most
 * standard user actions for every page.
 *  
 *
 */
public class HelpMenuPanel extends BasePanel {

	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(HelpMenuPanel.class);
	
	public HelpMenuPanel(String id) {
		super(id);
		init();
	}

	public HelpMenuPanel(String id, IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);
		init();
	}

	private void init() {
		final Space space = getCurrentSpace();
		final boolean hasBreadCrumbModel = getBreadCrumbModel() != null;
		if(space == null){
			WebMarkupContainer empty = new WebMarkupContainer("availableAssetTypesListMenu");
			add(new EmptyPanel("assetTypesHeadingLabel").setRenderBodyOnly(true).setVisible(false));
			empty.setVisible(false);
			add(empty.setRenderBodyOnly(true));
			add(new Label("spaceCreateLink").setVisible(false));
			WebMarkupContainer spaceLink = new WebMarkupContainer("spaceLink");
			spaceLink.setVisible(true);
			add(spaceLink);
			
		}
		else{
			if(getCalipso().isAllowedToCreateNewItem(getPrincipal(), space)){
				// link to create current space
				if(hasBreadCrumbModel){
					BreadCrumbLink spaceCreateLink = new BreadCrumbLink("spaceCreateLink", getBreadCrumbModel()) {
						private static final long serialVersionUID = 1L;
						@Override
		    			protected IBreadCrumbParticipant getParticipant(String id) {
							List<IBreadCrumbParticipant> breadCrumbList = getBreadCrumbModel().allBreadCrumbParticipants();
							int siz = breadCrumbList.size();
							if(breadCrumbList.get(siz -1).equals(getBreadCrumbModel().getActive())){
								breadCrumbList.remove(siz-1);
							}
							return new ItemFormPanel(id,getBreadCrumbModel());
						}
					};
					add(spaceCreateLink);
				}
				else{
					Link spaceCreateLink = new Link("spaceCreateLink"){
						private static final long serialVersionUID = 1L;
						@Override
						public void onClick() {
							setResponsePage(ItemFormPage.class);
						}
					};
					add(spaceCreateLink);
				}
				
			}
			else{
				add(new Label("spaceCreateLink").setVisible(false));
			}
			if(hasBreadCrumbModel){
				
				BreadCrumbLink spaceLink = new BreadCrumbLink("spaceLink", getBreadCrumbModel()) {
					private static final long serialVersionUID = 1L;
					@Override
	    			protected IBreadCrumbParticipant getParticipant(String id) {
						List<IBreadCrumbParticipant> breadCrumbList = getBreadCrumbModel().allBreadCrumbParticipants();
						int siz = breadCrumbList.size();
						if(breadCrumbList.get(siz -1).equals(getBreadCrumbModel().getActive())){
							breadCrumbList.remove(siz-1);
						}
				        ItemSearch itemSearch = new ItemSearch(getCurrentSpace(), getPrincipal(), this);
				        SingleSpacePanel singleSpacePanel = new SingleSpacePanel(id, getBreadCrumbModel(), itemSearch);
				        return singleSpacePanel;
					}
				};
				add(spaceLink.setRenderBodyOnly(getPrincipal().isAnonymous()));
				spaceLink.add(new Label("spaceNameLabel", localize(space.getNameTranslationResourceKey())));
			}
			else{
				Link spaceLink = new Link("spaceLink"){
					private static final long serialVersionUID = 1L;
					@Override
					public void onClick() {
						setResponsePage(SpacePage.class);
					}
				};
				add(spaceLink.setRenderBodyOnly(getPrincipal().isAnonymous()));
				spaceLink.add(new Label("spaceNameLabel", localize(space.getNameTranslationResourceKey())));
			}
			
			List<AssetType> visibleAssetTypes = getCalipso().findAllAssetTypesForSpace(space);
			boolean showAssets = CollectionUtils.isNotEmpty(visibleAssetTypes) && !getPrincipal().isAnonymous();
			RenderedClazzListView availableAssetTypesListMenu = new RenderedClazzListView("availableAssetTypesListMenu", visibleAssetTypes);
			availableAssetTypesListMenu.setRenderBodyOnly(false);
			add(new Label("assetTypesHeadingLabel", localize("asset.assetTypes")).setVisible(showAssets));
			add(availableAssetTypesListMenu.setVisible(showAssets));
			// render message if no asset types exist
//			if(CollectionUtils.isEmpty(visibleAssetTypes) || getPrincipal().isAnonymous()){
//				availableAssetTypesListMenu.setVisible(false);
//				add(new Label("noAssetTypes", localize("asset.no.assetTypes.exist")).setVisible(!getPrincipal().isAnonymous()));
//			}
//			else{
//				add(new Label("noAssetTypes", "").setVisible(false));
//			}
		}
		// searches don't depend on spaces
		List<SavedSearch> visibleReports = new ArrayList<SavedSearch>(getCalipso().findVisibleSearches(getPrincipal()));
		add(new Label("reports", localize("reports")));
		if(CollectionUtils.isNotEmpty(visibleReports)){
			add(new ListView("visibleSearchListView", visibleReports){
				private static final long serialVersionUID = 1L;

				@Override
				protected void populateItem(ListItem listItem) {
					
					final SavedSearch savedSearch =(SavedSearch)listItem.getModelObject();
					if(getBreadCrumbModel() != null && space != null){
						BreadCrumbLink savedSearchBreadCrumbLink = new BreadCrumbLink("visbleSearchItem", getBreadCrumbModel()) {
			    			
							private static final long serialVersionUID = 1L;
		
							@Override
			    			protected IBreadCrumbParticipant getParticipant(String id) {
			    				//create PageParameters and ItemSearch classes
			    				PageParameters params = new PageParameters(savedSearch.getQueryString(), ",");
			    		        ItemSearch itemSearch = null;
			    				try {
			    					itemSearch = ItemUtils.getItemSearch(getPrincipal(), params, this);
			    				} catch (CalipsoSecurityException e) {
			    					e.printStackTrace();
			    				}
			    		        setCurrentItemSearch(itemSearch);
			    		        BreadCrumbUtils.removePreviousBreadCrumbPanel(getBreadCrumbModel());
			    				return new ItemListPanel(id, getBreadCrumbModel(), savedSearch.getName());
			    			}
						};

			    		listItem.add(savedSearchBreadCrumbLink);
			    		savedSearchBreadCrumbLink.add(new Label("visbleSearchItemLabel", savedSearch.getName()));
					}else{
						listItem.add(new EmptyPanel("visbleSearchItem").setVisible(false));
					}
				}
				
			});

			add(new Label("noReports", "").setVisible(false));
		}
		// if there are no visible reports
		else{
			add(new EmptyPanel("visibleSearchListView").setVisible(false));
			add(new Label("noReports", localize("reports.none.available")));
		}
		
	}

	private class RenderedClazzListView extends ListView {
		
		private static final long serialVersionUID = 1L;
		
		public RenderedClazzListView(String id, List<AssetType> list) {
			super(id, list);
		}
		@Override
		protected void populateItem(ListItem item) {
			final AssetType assetType =(AssetType) item.getModelObject();
			Link link = new Link("space"){
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					setResponsePage(new AssetSpacePage(assetType));
				}
			};
			link.add(new Label("itemLabel", HelpMenuPanel.this.localize(assetType.getNameTranslationResourceKey())));
			item.add(link);
		}
	}
}
