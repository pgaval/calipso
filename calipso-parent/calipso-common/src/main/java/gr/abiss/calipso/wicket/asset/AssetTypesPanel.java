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

package gr.abiss.calipso.wicket.asset;

import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;

import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.AssetTypeSearch;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.hlpcls.ExpandAssetSearchLink;

/**
 * @author marcello
 */
public class AssetTypesPanel extends BasePanel {

	private AssetType assetType;
	private AssetTypeSearch assetTypeSearch;
	private AssetTypesListPanel assetTypesListPanel;
	
	CompoundPropertyModel assetTypeModel;
	private SearchAssetTypeForm searchForm = null;
	private WebMarkupContainer searchContainer;
	private WebMarkupContainer searchPlaceHolder;
	private boolean isSearchOpen;

    public AssetTypesPanel setSelectedAssetTypeId(long selectedAssetTypeId) {
    	assetTypesListPanel.setSelectedAssetTypeId(selectedAssetTypeId);
        return this;
    }
	
	public AssetTypesPanel(String id, IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);
		this.assetType = new AssetType();
		this.assetTypeSearch = new AssetTypeSearch(this.assetType, this);
		this.isSearchOpen = false;
		
		addComponents();		
	}
	
	//------------------------------------------
	
	public AssetTypesPanel(String id, IBreadCrumbModel breadCrumbModel, AssetType assetType){
		super(id, breadCrumbModel);
		this.assetTypeSearch = new AssetTypeSearch(assetType, this);
		this.assetType = assetType;
		this.isSearchOpen = true;
		
		addComponents();
	}//AssetTypesPage
	
	//------------------------------------------
	
	public AssetTypesPanel(String id, IBreadCrumbModel breadCrumbModel, AssetTypeSearch assetTypeSearch){
		super(id, breadCrumbModel);
		this.assetTypeSearch = assetTypeSearch;
		this.assetType = assetTypeSearch.getSearchObject();
		this.isSearchOpen = false;
		
		addComponents();
	}//AssetTypesPage
	
	//////////////////////////////////////////////
	
	private void addComponents(){

		createNewAssetType();
		searchAssetType();
		assetTypesListPanel();
	}//addComponents
	
	//------------------------------------------
	private void assetTypesListPanel(){
		assetTypesListPanel = new AssetTypesListPanel("assetTypesListPanel", getBreadCrumbModel(), this.assetTypeSearch);
		add(assetTypesListPanel);		
	}
	
	//------------------------------------------
	
	private void createNewAssetType(){
		Link newAssetType = new Link("new") {
            public void onClick() {
            	activate(new IBreadCrumbPanelFactory(){
    			public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
    				return new AssetTypeFormPagePanel(getBreadCrumbModel().getActive().getComponent().getId(), getBreadCrumbModel());
    			}
    		});
            }//onClick
        };
        add(newAssetType);
	}//createNewAssetType

	//------------------------------------------
	
	private void searchAssetType(){
		assetTypeModel = new CompoundPropertyModel(this.assetType);

		// --- container -------------------------------------------
		searchContainer = new WebMarkupContainer("searchContainer");
		add(searchContainer);

		// -- place holder ----------------------------------------
		searchPlaceHolder = new WebMarkupContainer("searchPlaceHolder");
		searchContainer.add(searchPlaceHolder);
		
		// -- Page is (re)loaded, and the search panel is open ----
		if (isSearchOpen){
			searchForm = new SearchAssetTypeForm("form", assetTypeModel);
			searchContainer.add(searchPlaceHolder);
			searchPlaceHolder.add(searchForm);
		}
		// -- Page is (re)loaded, and the search panel is closed ----		
		else{
			//Set place holder to not visible
			searchPlaceHolder.setVisible(false);
		}
		
		//setOutputMarkupId, needed for ajax
		searchContainer.setOutputMarkupId(true);
		
		ExpandAssetSearchLink search = new ExpandAssetSearchLink("search",
				searchContainer, searchContainer, searchPlaceHolder, new SearchAssetTypeForm("form", assetTypeModel), isSearchOpen){
			@Override
			public void onLinkClick() {
				AssetTypesPanel.this.isSearchOpen = this.isOpen();
				
			}
		};
		
		add(search);		
	}
	

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	private class SearchAssetTypeForm extends Form{

		public SearchAssetTypeForm(String id, CompoundPropertyModel model) {
			super(id);
			setModel(model);
			
			//name
			TextField name = new TextField("name");
			add(name);
			//form label
			name.setLabel(new ResourceModel("asset.assetTypes.description"));
			add(new SimpleFormComponentLabel("nameLabel", name));

		}//SearchAssetTypeForm
		
		//------------------------------------------------------------------------------------------------
		
		protected void onSubmit() {
			activate(new IBreadCrumbPanelFactory(){
				public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {

					//Remove last breadcrumb participant
					if (breadCrumbModel.allBreadCrumbParticipants().size()>0){
						breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
					}//if

					return new AssetTypesPanel(id, breadCrumbModel, AssetTypesPanel.this.assetType);
				}
			});
			
		}//onSubmit
	}//SearchAssetTypeForm 	

	
	//---------------------------------------
	public String getTitle() {
		return localize("asset.assetTypes.title");
	}
	
}

