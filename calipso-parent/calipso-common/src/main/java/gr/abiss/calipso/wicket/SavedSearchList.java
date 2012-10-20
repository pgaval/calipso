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

import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.SavedSearch;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.exception.CalipsoSecurityException;
import gr.abiss.calipso.util.ItemUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.value.ValueMap;

/**
 * @author erasmus
 * @author marcello
 *
 */
public class SavedSearchList extends BasePanel {
    
	protected static final Logger logger = Logger.getLogger(SavedSearchList.class);

	private static final long serialVersionUID = 1L;
	
	private WebMarkupContainer editContainer = null;
	private WebMarkupContainer listContainer = null;
	private Item lastEditListItem = null;
	private SaveSearchFormPanel saveSearchFormPanel = null;
	private List <SavedSearch> savedSearchList = null;
	private SavedSearchDataList savedSearchDataList = null;

	///////////////////////////////////////////////////////////////////////////////////////

	private class SaveSearchFormPanel extends AbstractSaveSearchFormPanel{

		public SaveSearchFormPanel(String id, SavedSearch savedSearch) {
			super(id, savedSearch);
		}//SaveSearchFormPanel

		//---------------------------------------------------------------------

		public SaveSearchFormPanel(String id, String message) {
			super(id, message);
		}//SaveSearchFormPanel

		//---------------------------------------------------------------------

		@Override
		public void cancel() {
			getCancelTargetComponent().remove(saveSearchFormPanel);
			saveSearchFormPanel = null;

            editContainer = new WebMarkupContainer("saveSearchFormPanel");
            getCancelTargetComponent().add(editContainer);
            lastEditListItem = null;
            editContainer.setVisible(false);
		}//cancel

		//---------------------------------------------------------------------

		@Override
		public void save(String name) {
			cancel();
		}//save

		//---------------------------------------------------------------------

		@Override
		public void confirm() {
			if (lastEditListItem.getModelObject()!=null){
				getCalipso().removeSavedSearch((SavedSearch) lastEditListItem.getModelObject());
				lastEditListItem.getParent().remove(lastEditListItem);
				savedSearchDataList.remove(lastEditListItem);
			}//if
			
			cancel();
		}//confirm
	}//SaveSearchFormPanel

	///////////////////////////////////////////////////////////////////////////////////////

	private class SavedSearchDataList extends RefreshingView{
		final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
		
		public SavedSearchDataList(String id) {
			super(id);
		}//SavedSearchDataList

		public SavedSearchDataList(String id, IModel model){
			super(id, model);
		}//SavedSearchDataList
		
		@Override
		protected Iterator<Model> getItemModels() {
    		List<Model> models = new ArrayList<Model>(); 
    		for(int i=0;i<((List<Model>)getDefaultModelObject()).size();i++){
    			models.add(new Model((SavedSearch)((List<SavedSearch>)getDefaultModelObject()).get(i))); 
    		}//for
    		return models.iterator();        		
		}//Iterator

		@Override
		protected void populateItem(final Item listItem) {
            if(listItem.getIndex() % 2 == 1) {
                listItem.add(sam);
            }      

         	final SavedSearch savedSearch = (SavedSearch) listItem.getModelObject();

         	//----------- Name ----------------------------------------------
        	BreadCrumbLink link = new BreadCrumbLink("name", getBreadCrumbModel()) {
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

    				return new ItemListPanel(id, getBreadCrumbModel(), savedSearch.getName());
    			}
    		};
    		
    		link.add(new Label("name", savedSearch.getName()));
    		listItem.add(link);           

            //--------- Edit Container ----------------------------------------------------
    		listItem.setOutputMarkupId(true);

            editContainer = new WebMarkupContainer("saveSearchFormPanel");
            listItem.add(editContainer);
            editContainer.setVisible(false);

    		//----------- Edit  ----------------------------------------------

    		AjaxLink editLink = new AjaxLink("edit"){
    			@Override
    			public void onClick(AjaxRequestTarget target) {

    				if (lastEditListItem!=null){
    					return;
    				}//if

    				if (editContainer!=null){
    					editContainer.setVisible(true);
    					listItem.remove(editContainer);
    					editContainer = null;
    					saveSearchFormPanel = new SaveSearchFormPanel("saveSearchFormPanel", savedSearch);
    					saveSearchFormPanel.setCancelTargetComponent(listItem);
    					listItem.add(saveSearchFormPanel);
    					lastEditListItem = listItem;
    				}//if
    				else{
    					listItem.remove(saveSearchFormPanel);
    					saveSearchFormPanel = null;

    	                editContainer = new WebMarkupContainer("saveSearchFormPanel");
    	                listItem.add(editContainer);
    	                editContainer.setVisible(false);
    	                lastEditListItem = null;
    				}//else

    				target.addComponent(listItem);
    			}//onClick
    		};
    		editLink.setOutputMarkupId(true);
			listItem.add(editLink);

			//----------- Delete ----------------------------------------------
			AjaxLink deleteLink = new AjaxLink("delete"){
				@Override
				public void onClick(AjaxRequestTarget target) {
    				if (lastEditListItem!=null){
    					return;
    				}//if
    				
    				if (editContainer!=null){
    					editContainer.setVisible(true);
    					listItem.remove(editContainer);
    					editContainer = null;
    					saveSearchFormPanel = new SaveSearchFormPanel("saveSearchFormPanel", localize("saved_search_list.confirmDelete", savedSearch.getName()));
    					saveSearchFormPanel.setCancelTargetComponent(listItem);
    					saveSearchFormPanel.setConfirmTargetComponent(listContainer);
    					listItem.add(saveSearchFormPanel);
    					lastEditListItem = listItem;
    				}//if
    				else{
    					listItem.remove(saveSearchFormPanel);
    					saveSearchFormPanel = null;

    	                editContainer = new WebMarkupContainer("saveSearchFormPanel");
    	                listItem.add(editContainer);
    	                editContainer.setVisible(false);
    	                lastEditListItem = null;
    				}//else

    				target.addComponent(listItem);        				
				}
			};
			deleteLink.setOutputMarkupId(true);
			listItem.add(deleteLink);			
		}//populateItem

	}//SavedSearchDataList

	///////////////////////////////////////////////////////////////////////////////////////

	public SavedSearchList(String id, IBreadCrumbModel breadCrumbModel, final User user) {
		super(id, breadCrumbModel);

		//needed for ajax
		setOutputMarkupId(true);

        LoadableDetachableModel savedSearchListModel = new LoadableDetachableModel() {
           
			private static final long serialVersionUID = 1L;

			protected Object load() {
                logger.debug("loading saved searches from database");
                savedSearchList = getCalipso().findSavedSearches(user, getCurrentSpace());
                return savedSearchList;
            }
        };

        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
        
        listContainer = new WebMarkupContainer("listContainer");
        listContainer.setOutputMarkupId(true);
        add(listContainer);
        
        savedSearchDataList = new SavedSearchDataList("nameList", savedSearchListModel);
        savedSearchDataList.setOutputMarkupId(true);
        savedSearchDataList.setItemReuseStrategy(new ReuseIfModelsEqualStrategy());
        listContainer.add(savedSearchDataList);
	}//SavedSearchList
}