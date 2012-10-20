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

package gr.abiss.calipso.wicket.assetNew;

import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.ItemViewPanel;
import gr.abiss.calipso.wicket.CalipsoApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 *
 */
public class SelectAssetsForItemPanel extends BasePanel {
    
	private static final long serialVersionUID = 1L;
	
	protected static final Logger logger = Logger.getLogger(SelectAssetsForItemPanel.class);
	private LoadableDetachableModel remainingAssets;
	private LoadableDetachableModel selectedAssets;
	private boolean redirect;
	private IModel model;
	//private LoadableDetachableModel itemModel;
	LoadableDetachableModel itemModel;
	@Deprecated
	public SelectAssetsForItemPanel(String id, IBreadCrumbModel breadCrumbModel, final IModel model){
		this(id, breadCrumbModel, model,true);
		
	}
	public SelectAssetsForItemPanel(String id, IBreadCrumbModel breadCrumbModel, final IModel model, boolean redirect) {
		super(id, breadCrumbModel);
		this.redirect = redirect;
		
		// item model
		itemModel = new LoadableDetachableModel() {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected Object load() {
				Item tmpItem = (Item)model.getObject();
				if(tmpItem == null || tmpItem.getId() == 0){
					return (Item)model.getObject();
				}
				
				return getCalipso().loadItem(tmpItem.getId());
			}
		};
		
		// needed for ajax to access object
		this.setOutputMarkupId(true);
		this.setOutputMarkupPlaceholderTag(true);
		init();
		
	}
	private void init(){
		add(new AssetSelectionForm("form"));
		// needed for ajax to access object
		
	}
	private class AssetSelectionForm extends Form{
		
		private Item item;
		
		private static final long serialVersionUID = 1L;
		public AssetSelectionForm(String id){
			super(id);
			this.item = (Item)itemModel.getObject();
			initObjectModels(this.item);
			// check if asset is new
			if(logger.isDebugEnabled()){
				 logger.debug("Rendering form, item null? : "+ (this.item == null));
			 }
			// Assets that NOT belong to the item
			setOutputMarkupId(true);
			setOutputMarkupPlaceholderTag(true);
			initFragment();
		
			
		}
			
		@SuppressWarnings("unchecked")
		private void initFragment() {
			// TODO: deprecated use new constructor wich also want parent container
			// TODO: make more generic to use only one fragment also in html
			if(CollectionUtils.isNotEmpty((List<Asset>) selectedAssets.getObject())){
				Fragment selectedAssetsFragment = new Fragment("selectedAssetsPlace","selectedAssetsFragment", this);
				add(selectedAssetsFragment);
				// remove asset -redirect
				selectedAssetsFragment.add(new AssetsListView("assetsListView", selectedAssets, this.item, false, redirect));
			}
			else{
				add(new EmptyPanel("selectedAssetsPlace"));
			}
			
			if(CollectionUtils.isNotEmpty((List<Asset>) remainingAssets.getObject())){
				Fragment remainingAssetsFragment = new Fragment("remainingAssetsPlace","remainingAssetsFragment", this);
				add(remainingAssetsFragment);
				// add asset - redirect
				remainingAssetsFragment.add(new AssetsListView("assetsListView",remainingAssets, this.item, true, redirect));
			}
			else{
				add(new EmptyPanel("remainingAssetsPlace"));
			}
			
			
		}

		private void initObjectModels(final Item item) {
			// TODO: refactor
			// Assets that belong to the item
			selectedAssets = new LoadableDetachableModel() {
				private static final long serialVersionUID = 1L;
				@Override
				protected Object load() {
					if(AssetSelectionForm.this.item == null || AssetSelectionForm.this.item.getId() == 0){
						return new ArrayList<Asset>();
					}
					return getCalipso().findAllAssetsByItem(item);
				}
			};
			
			remainingAssets = new LoadableDetachableModel(){
				private static final long serialVersionUID = 1L;
				
				@Override
				protected Object load() {
					
					List<Asset> assetInCurrentSpace = getCalipso().findAllAssetsBySpace(getCurrentSpace());
					
					if(AssetSelectionForm.this.item == null || AssetSelectionForm.this.item.getId() == 0){
						return assetInCurrentSpace;
					}
					// TODO: refactor
					List<Asset> remainingAssetsList = new ArrayList<Asset>();
					List<Asset> allAssetsInItem = new ArrayList<Asset>((List<Asset>)selectedAssets.getObject());
					
					// TODO: change this bad implementation. get a subset of non shared assets
					for(Asset asset : assetInCurrentSpace){
						if(!allAssetsInItem.contains(asset)){
							remainingAssetsList.add(asset);
							if(logger.isDebugEnabled()){
								logger.debug("Asset NOT in item : " + asset.getDisplayedValue());
							}else{
								if(logger.isDebugEnabled()){
									logger.debug("Asset in item: " + asset.getDisplayedValue());
								}
							}
						}
					}
					return remainingAssetsList;
				}
			};
		}
	}
	private class AssetsListView extends ListView{

		private static final long serialVersionUID = 1L;
		
		private Item item;
		private boolean addToList;
		private boolean redirect;
		private Form parentForm;
		/**
		 * 
		 * @param id
		 * @param assetsModel
		 * 		Model with List of the available assets
		 * @param item
		 * 		The item
		 * 
		 * @param addToList
		 * 		Two choices if addToList is true then add else remove from list
		 */
		// TODO: implement later 
		// instead of redirect boolean give Object class and redirect
		// to the class page-panel, if class null stay and use AJAX to update the page
		public AssetsListView(String id, IModel assetsModel, Item item, boolean addToList, boolean redirect) {
			super(id, assetsModel);
			this.item = item;
			this.addToList = addToList;
			this.redirect = redirect;
			//this.parentForm = parentForm;
		}

		/**
		 * @see org.apache.wicket.markup.html.list.ListView#populateItem(org.apache.wicket.markup.html.list.ListItem)
		 */
		@Override
		protected void populateItem(ListItem listItem) {
			final Asset asset = (Asset)listItem.getModelObject();
			// labels
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			listItem.add(new Label("assetType", localize(asset.getAssetType().getNameTranslationResourceKey())));
			listItem.add(new Label("inventoryCode", asset.getInventoryCode()));
			listItem.add(new Label("supportStartDate", asset.getSupportStartDate()!= null?df.format(asset.getSupportStartDate()):""));
			listItem.add(new Label("supportEndDate", asset.getSupportEndDate() != null?df.format(asset.getSupportEndDate()):""));
			// create proper buttons 
			if(redirect){
				Button assetAction = new Button("assetAction"){
					private static final long serialVersionUID = 1L;
					
					@Override
					public void onSubmit() {
						makeSaveUpdate(asset);
					}
				};
				listItem.add(assetAction);
			}else{
				AjaxButton assetAction = new AjaxButton("assetAction", parentForm){


					@Override
					protected void onSubmit(AjaxRequestTarget target,
							Form<?> form) {
						//makeSaveUpdate(asset);
						target.add(form);
					}

					@Override
					protected void onError(AjaxRequestTarget target,
							Form<?> form) {
						// TODO Auto-generated method stub
						
					}
					
				};
				listItem.add(assetAction);
			}
		}
		// TODO: make this class abstract to get
		// the button pic to proper display-explain button actions
		//	private void buttonPicturePath(String path){
			
		//}
		private void makeSaveUpdate(Asset asset){

			// TODO: re-render parent and remove this asset from listView
			if(logger.isDebugEnabled()){
				 logger.debug("Adding asset : " + asset.getDisplayedValue() + " List");
			 }
			if(addToList){
				getCalipso().loadItem(item.getId()).getAssets().add(asset);
			}else{
				Set<Asset>  allAssetsInItemHashSet = getCalipso().loadItem(item.getId()).getAssets();
				for(Asset assetInItem : allAssetsInItemHashSet){
					if(assetInItem.getInventoryCode().equals(asset.getInventoryCode())
							&&	assetInItem.getAssetType().equals(asset.getAssetType())){
						allAssetsInItemHashSet.remove(assetInItem);
						break;
					}
				}
			}
			// store item
			getCalipso().updateItem(getCalipso().loadItem(item.getId()), getPrincipal(), false);
			// TODO: redirect if class instance is not null (
			redirectOrUpdateViaAjax();
		
		}
		// TODO: remove not needed
		private void redirectOrUpdateViaAjax() {
			if(redirect){
				//breadcrumb must be activated in the active panel, that is ItemViewPanel
				   activate(new IBreadCrumbPanelFactory(){
						private static final long serialVersionUID = 1L;
						public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
		            		return new ItemViewPanel(componentId, getBreadCrumbModel(), getCalipso().loadItem(item.getId()).getUniqueRefId());
		            	}//create
		            });
			}else{// TODO: implement in later versions
				
			}
		}
	}
	
}
