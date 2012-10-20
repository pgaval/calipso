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

import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.dto.AssetSearch;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.SearchUserPanel;
import gr.abiss.calipso.wicket.components.assets.AssetSearchDataProvider;
import gr.abiss.calipso.wicket.components.assets.AssetsDataView;
import gr.abiss.calipso.wicket.components.icons.StaticImage;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

/**
 * Renders an Ajax-enabled "form" that allows the selection of an Asset.
 */
public abstract class ItemFormAssetSearchPanel extends BasePanel {
	
	private static final long serialVersionUID = 1L;

	public abstract void onAssetSelect(Asset asset, AjaxRequestTarget target);

	public abstract void closePanel(AjaxRequestTarget target);
	//---------------------------------------------------------------------------------------------

	public ItemFormAssetSearchPanel(String id, AssetSearch as) {
		super(id);
		this.setOutputMarkupId(true);
		this.setVisible(true);
		final AssetSearch assetSearch = as;
		this.setDefaultModel(new CompoundPropertyModel(assetSearch));
		
		final WebMarkupContainer assetSearchForm = new WebMarkupContainer("assetAjaxSearchForm");
		assetSearchForm.setOutputMarkupId(true);
		add(assetSearchForm);
		assetSearchForm.add(new AjaxLink("close"){

			@Override
			public void onClick(AjaxRequestTarget target) {
				closePanel(target);
		}
			
		});
		// inventory code
		final TextField inventoryCode = new TextField("asset.inventoryCode");
		inventoryCode.setLabel(new ResourceModel("asset.inventoryCode"));
		inventoryCode.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                // Reset the inventoryCode model dropdown when the vendor changes
            	assetSearch.getAsset().setInventoryCode(inventoryCode.getDefaultModelObjectAsString());
            }
        });
		assetSearchForm.add(inventoryCode);
		assetSearchForm.add(new SimpleFormComponentLabel("assetInventoryCodeLabel", inventoryCode));
		
		AssetSearchDataProvider assetSearchDataProvider = new AssetSearchDataProvider(assetSearch);
		// if AssetSearch only addresses one AssetType, do not allow other choices
		List<AssetType> assetTypes = null;
		if(assetSearch.getAsset().getAssetType() != null){
			assetTypes = new ArrayList<AssetType>(1);
			assetTypes.add(assetSearch.getAsset().getAssetType());
		}
		else{
			assetTypes = getCalipso().findAllAssetTypes();
		}
		@SuppressWarnings("serial")
		final DropDownChoice assetTypeChoice = new DropDownChoice("asset.assetType", assetTypes, new IChoiceRenderer(){
            public Object getDisplayValue(Object o) {
                return localize(((AssetType)o).getNameTranslationResourceKey());
            }
            public String getIdValue(Object o, int i) {
                return localize(((AssetType)o).getName());
            }        
		});
		if(assetTypes.size() == 1){
			logger.debug("Only allow one Asset TypeChoice");
			assetTypeChoice.setNullValid(false).setRequired(true);
		}
		else{
			logger.debug("Only any AssetType Choice");
		}

	    // List view headers 
	    List<String> columnHeaders = assetSearch.getColumnHeaders();
	    
		ListView headings = new ListView("headings", columnHeaders) {
	       
			private static final long serialVersionUID = 1L;

			protected void populateItem(ListItem listItem) {
	            final String header = (String) listItem.getModelObject();
	            AjaxLink headingLink = new AjaxLink("heading") {
					@Override
					public void onClick(AjaxRequestTarget target) {
	                	assetSearch.doSort(header);
	                	target.addComponent(ItemFormAssetSearchPanel.this);
					}
	            };
	            
	            listItem.add(headingLink); 
	            String label = localize("asset.assetsList." + header);
	            headingLink.add(new Label("heading", label));
	            if (header.equals(assetSearch.getSortFieldName())) {
	                String order = assetSearch.isSortDescending() ? "order-down" : "order-up";
	                listItem.add(new SimpleAttributeModifier("class", order));
	            }
	        }
	    };
	    assetSearchForm.add(headings);
	    
	    //Header message 
	    Label hAction = new Label("hAction");
	    hAction.setDefaultModel(new Model(localize("edit")));	    	
	    assetSearchForm.add(hAction);
		
	    // the DataView with the results of the search
		final AssetsDataView assetDataView = new AssetsDataView("assetDataView", assetSearchDataProvider, getBreadCrumbModel(), getCalipso().getRecordsPerPage()){
			
			// when click the add button
			@Override
			public void onAddAssetClick(Asset asset, AjaxRequestTarget target) {
				// re-render
				onAssetSelect(asset, target);
			}
		};
		assetSearchForm.add(assetDataView);
		
		AjaxPagingNavigator panelNavigator = new AjaxPagingNavigator("navigator",assetDataView){
			@Override
			protected void onAjaxEvent(AjaxRequestTarget target) {
				target.addComponent(ItemFormAssetSearchPanel.this);
			}
		};
		assetSearchForm.add(panelNavigator);
		
		// back to our asset type choice....
		assetSearchForm.add(assetTypeChoice);
		assetTypeChoice.setLabel(new ResourceModel("asset.assetType"));
		// Add Ajax Behaviour...
		assetTypeChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            protected void onUpdate(AjaxRequestTarget target) {
                // Reset the phone model dropdown when the vendor changes
            	assetDataView.setCurrentPage(0);
            	assetSearch.getAsset().setAssetType((AssetType) assetTypeChoice.getModelObject());
            }
        });
		assetSearchForm.add(new SimpleFormComponentLabel("assetTypeLabel", assetTypeChoice));
		AjaxLink submitLink = new AjaxLink("submit") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				//assetSearchForm.replace(getConfiguredAssetListPanel(assetSearch));
				target.addComponent(ItemFormAssetSearchPanel.this);
			}
		};
		assetSearchForm.add(submitLink);
	}
	/**
	 * @param assetSearch
	 * @return
	 */
	
	private AssetsListPanel getConfiguredAssetListPanel(final AssetSearch assetSearch) {
		// override AssetsListPanel's getAssetActionLink to add the asset
		AssetsListPanel assetListPanel = new AssetsListPanel("assetsListPanel", getBreadCrumbModel(), assetSearch){
			public AbstractLink getAssetActionLink(String markupId, final Asset asset){
				AjaxLink link = new AjaxLink(markupId) {
					@Override
					public void onClick(AjaxRequestTarget target) {
						onAssetSelect(asset, target);
					}
				};
		        link.add(new StaticImage("actionLinkImg",  new Model("../resources/add.gif")));
		        return link;
			}
		};
		assetListPanel.setOutputMarkupId(true);
		return assetListPanel;
	}
	
	
}