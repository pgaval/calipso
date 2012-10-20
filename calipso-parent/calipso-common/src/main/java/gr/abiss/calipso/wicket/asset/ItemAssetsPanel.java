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
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.dto.AssetSearch;
import gr.abiss.calipso.util.AssetsUtils;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.CollapsedPanel;
import gr.abiss.calipso.wicket.ExpandedPanel;
import gr.abiss.calipso.wicket.hlpcls.ExpandCustomAttributesLink;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * @author marcello
 */

public class ItemAssetsPanel extends BasePanel {
    
	protected static final Logger logger = Logger.getLogger(ItemAssetsPanel.class);

	/** Reference item*/
	private Item item;

	/** For asset searching*/
	private AssetSearch assetSearch;

	/** All available asset types for filtering*/
	private List <AssetType> availableAssetTypesList = new ArrayList<AssetType>();

	/** All available item asset types for filtering*/
	private List <AssetType> allItemAssetTypeList = new ArrayList<AssetType>();
	
	/** All available assets*/
	private List <Asset> availableAssets = new ArrayList<Asset>();

	
	/** All item assets*/
	private List <Asset> allItemAssetsList = new ArrayList<Asset>();

	/** All selected assets */
	private List<Asset> selectedAssets = new ArrayList<Asset>();

	private AssetFilter assetFilter = new AssetFilter();
	
	private boolean isEditMode;
	private CompoundPropertyModel model;
	WebMarkupContainer itemAssetsListPlaceHolder;

	private WebMarkupContainer itemAssetsListContainer;

	
	
	
	
	
	
	
	
	private class AssetFilter implements Serializable{
		
		private static final long serialVersionUID = 1L;

		private String assetCode;
		
		public AssetFilter(){
			this.assetCode = "";
		}
		
		public AssetFilter(String assetCode){
			this.assetCode = assetCode;
		}
		
		public String getAssetCode() {
			return assetCode;
		}
		
		public void setAssetCode(String assetCode) {
			this.assetCode = assetCode;
		}
		
		@Override
		public String toString() {
			return this.assetCode;
		}
	}//AssetFilter
	
	/**
	 * 
	 * @param id
	 */
	public ItemAssetsPanel(String id) {
		super(id);
		
		Asset asset = new Asset();
		this.assetSearch = new AssetSearch(asset, this);
		this.assetSearch.getAsset().setSpace(getCurrentSpace());
		this.item = null;
		this.isEditMode = false;

		renderAvailableAssets(getRemainingAssets());
	}//ItemAssetsPanel

	/**
	 * 
	 * @param id
	 * @param item
	 */
	public ItemAssetsPanel(String id, Item item) {
		this(id, item, false);
	}//ItemAssetsPanel
	/**
	 * 
	 * @param id
	 * @param item
	 * @param isEditMode
	 */
	public ItemAssetsPanel(String id, Item item, boolean isEditMode) {
		super(id);
		Asset asset = new Asset();
		this.assetSearch = new AssetSearch(asset, this);
		this.assetSearch.getAsset().setSpace(getCurrentSpace());
		this.item = item;
		this.isEditMode = isEditMode;

		getAllAssetsForCurrentItem();
	}

	/**
	 * 
	 * @return
	 * 		The assets that belong to the space but don't belong to the item
	 */
	private IModel getRemainingAssets(){
		
        LoadableDetachableModel remainingAssetsForItemModel = new LoadableDetachableModel() {
            protected Object load() {
            	List<Asset> freshAvailableAssets = getCalipso().findAssetsMatching(assetSearch, false);
            	ItemAssetsPanel.this.availableAssets = new ArrayList<Asset>();

            	if (CollectionUtils.isNotEmpty(freshAvailableAssets)){
            		for (Asset asset : freshAvailableAssets){
            			if (!ItemAssetsPanel.this.allItemAssetsList.contains(asset)){
            				ItemAssetsPanel.this.availableAssets.add(asset);
                			if (!ItemAssetsPanel.this.availableAssetTypesList.contains(asset.getAssetType())){
                				ItemAssetsPanel.this.availableAssetTypesList.add(asset.getAssetType());
                			}//if
            			}//if
            		}//for
            	}//if

            	return ItemAssetsPanel.this.availableAssets;
            }//load
        };//assetsListModel

        remainingAssetsForItemModel.getObject();

        return remainingAssetsForItemModel;
	}//getAssets

	/**
	 * 
	 * @return
	 * 		All assets that belong to the item
	 */
	private IModel getAllAssetsForCurrentItem(){
        LoadableDetachableModel itemAssetsListModel = new LoadableDetachableModel() {
           
			private static final long serialVersionUID = 1L;

			protected Object load() {
            	ItemAssetsPanel.this.allItemAssetsList = getCalipso().findAllAssetsByItem(ItemAssetsPanel.this.item);

            	if (allItemAssetsList!=null){
            		for (Asset asset : ItemAssetsPanel.this.allItemAssetsList){
	        			if (!allItemAssetTypeList.contains(asset.getAssetType())){
	        				allItemAssetTypeList.add(asset.getAssetType());
	        			}            			
            		}
            	}
            	return allItemAssetsList;
            }
        };
        itemAssetsListModel.getObject();
        return itemAssetsListModel;
	}
	
	private WebMarkupContainer renderAssetCodeFilter(){
		WebMarkupContainer assetCodeContainer = new WebMarkupContainer("assetCodeContainer");
//		AssetFilter assetFilter = new AssetFilter();
		final CompoundPropertyModel assetFilterModel = new CompoundPropertyModel(new AssetFilter());

		//Label
		WebMarkupContainer assetCodeLabel = new WebMarkupContainer("assetCodeLabel");

		//Asset Code
		final TextField assetCode = new TextField("assetCode", new PropertyModel(assetFilter, "assetCode")){
			
		};
		assetCode.setOutputMarkupId(true);

		//TODO: Implement filter asset by Inventory Code
		//Button
		AjaxLink submitAssetCode = new AjaxLink("submitAssetCode"){
			@Override
			public void onClick(AjaxRequestTarget target) {
				target.addComponent(assetCode);
			}
		};
		submitAssetCode.setOutputMarkupId(true);

		assetCodeContainer.add(assetCodeLabel);
		assetCodeContainer.add(assetCode);
		assetCodeContainer.add(submitAssetCode);

		return assetCodeContainer;
	}//renderAssetCodeFilter
	
	//---------------------------------------------------------------------------------------------

	private void renderAvailableAssets(final IModel availableAssetsModel){
		//Container ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		final WebMarkupContainer assetsContainer = new WebMarkupContainer("assetsContainer");
		assetsContainer.setOutputMarkupId(true);
		
		add(assetsContainer);
		
		
		//Asset filter(s) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		WebMarkupContainer assetFIlterContainer = new WebMarkupContainer("assetFIlterContainer");
		//assetFIlterContainer.setRenderBodyOnly(true);
		assetsContainer.add(assetFIlterContainer);
		
		
		//Asset Type Filter
		//DropDownChoice assetTypeFIlter = renderAssetTypeFilter(assetsContainer, availableAssetsModel, this.availableAssetTypesList);
		
		// assettype dropDown
		
		final DropDownChoice assetTypeChoice = new DropDownChoice("assetTypeChoice",  new Model(), this.availableAssetTypesList,  new IChoiceRenderer(){
            public Object getDisplayValue(Object o) {
                return localize(((AssetType)o).getNameTranslationResourceKey());
            }
            public String getIdValue(Object o, int i) {
            	return String.valueOf(((AssetType)o).getId());
            }
		});
		assetTypeChoice.setOutputMarkupId(true);
		
		assetTypeChoice.add(new AjaxFormComponentUpdatingBehavior ("onchange") {
			
			protected void onUpdate(AjaxRequestTarget target) {
				
				if (assetTypeChoice.getModelObject() != null){
					AssetType assetType = (AssetType) assetTypeChoice.getModelObject();
					assetSearch.getAsset().setAssetType(getCalipso().loadAssetType(assetType.getId()));
				}//if
				else{
					assetSearch.getAsset().setAssetType(null);
				}//else

				try{
					// remove table with assets
					assetsContainer.remove(itemAssetsListContainer);
				}
				catch (Exception e) {
					
				}
				// create again new fragment with search queries (currentSpace, assetType)
				Fragment itemAssetsListContainerFragment = new Fragment("itemAssetsListPlaceHolder", "itemAssetsListContainerFragment", assetsContainer);
				if (isEditMode){
					itemAssetsListContainerFragment.add(renderAssetList(getAllAssetsForCurrentItem(), false));
				}
				else{
					itemAssetsListContainerFragment.add(renderAssetList(getRemainingAssets(), false));
				}
				assetsContainer.add(itemAssetsListContainerFragment);
				target.addComponent(assetsContainer);
			}
		});
		
		boolean hasAvailableAssets = CollectionUtils.isNotEmpty(this.availableAssetTypesList);
		
		List<Asset> spaceAssets = new ArrayList<Asset>(getCalipso().getVisibleAssetsForSpace(getCurrentSpace()));
		boolean spaceHasAssets = CollectionUtils.isNotEmpty(spaceAssets);

		assetTypeChoice.setNullValid(false);
		assetFIlterContainer.add(new WebMarkupContainer("assetTypeFIlterLabel").setVisible(hasAvailableAssets));
		assetFIlterContainer.add(assetTypeChoice.setVisible(hasAvailableAssets));

		//Asset Inventory Code
		WebMarkupContainer assetCodeContainer = renderAssetCodeFilter();
		assetCodeContainer.setVisible(hasAvailableAssets); 
		assetFIlterContainer.add(assetCodeContainer);
		assetCodeContainer.setVisible(false);

		
		//Asset List ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		itemAssetsListPlaceHolder = new WebMarkupContainer("itemAssetsListPlaceHolder");
		assetsContainer.add(itemAssetsListPlaceHolder);
		ItemAssetsPanel.this.itemAssetsListContainer = itemAssetsListPlaceHolder;
		ItemAssetsPanel.this.itemAssetsListContainer.setOutputMarkupId(false);
		
		// Messages ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		
		if (!hasAvailableAssets){
			assetsContainer.add(new Label("noAssetMessage", new Model(localize("asset.assetPanel.noAvailableAssets"))));
		}
		else{
			assetsContainer.add(new WebMarkupContainer("noAssetMessage").setVisible(false));
		}
	}//renderAvailableAssets2
	
	//--------------------------------------------------------------------------------------------------------
	
	private void renderItemAssets(final IModel itemAssetsModel){
		boolean itemInvolvesAssets = CollectionUtils.isNotEmpty(allItemAssetsList);
		
		//Container ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		final WebMarkupContainer assetsContainer = new WebMarkupContainer("assetsContainer");
		assetsContainer.setOutputMarkupId(true);
		add(assetsContainer);

		//Asset filter(s) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		WebMarkupContainer assetFIlterContainer = new WebMarkupContainer("assetFIlterContainer");
		assetsContainer.add(assetFIlterContainer);

		//Asset Type Filter
		assetFIlterContainer.add(new WebMarkupContainer("assetTypeChoice").setVisible(false));
		assetFIlterContainer.add(new WebMarkupContainer("assetTypeFIlterLabel").setVisible(false));

		//Asset Inventory Code
		WebMarkupContainer assetCodeContainer = new WebMarkupContainer("assetCodeContainer");
		assetCodeContainer.setVisible(false);
		assetFIlterContainer.add(assetCodeContainer);

		//Item Assets List Container ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		Fragment itemAssetsListContainerFragment = new Fragment("itemAssetsListPlaceHolder", "itemAssetsListContainerFragment", assetsContainer);
		ItemAssetsPanel.this.itemAssetsListContainer = itemAssetsListContainerFragment;
		itemAssetsListContainerFragment.add(renderAssetList(itemAssetsModel, !this.isEditMode));
		if(!itemInvolvesAssets){
			itemAssetsListContainerFragment.setVisible(false).setRenderBodyOnly(true);
		}
		else{
			itemAssetsListContainerFragment.setVisible(true).setRenderBodyOnly(false);
		}

		assetsContainer.add(itemAssetsListContainerFragment);

		//Messages ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		
		if (!itemInvolvesAssets){
			assetsContainer.add(new Label("noAssetMessage", new Model(localize("asset.assetPanel.itemInvolvesNoAssets"))));
		}
		else{
			assetsContainer.add(new WebMarkupContainer("noAssetMessage").setVisible(false));
		}

	}//renderItemAssets2
	/**
	 * 
	 * @param assetsModel
	 * @param isViewMode
	 * @return 
	 */
	private WebMarkupContainer renderAssetList(final IModel assetsModel, final boolean isViewMode){

		//Assets list container ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		final WebMarkupContainer itemAssetsListContainer = new WebMarkupContainer("itemAssetsListContainer");

		//Assets list ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");

		if (!isViewMode){
			Label hCheckbox = new Label("hCheckbox", localize("asset.assetPanel.choose"));
			itemAssetsListContainer.add(hCheckbox);			
		}
		else{
			itemAssetsListContainer.add(new WebMarkupContainer("hCheckbox").setVisible(false));
		}

		ListView assetsListView = new ListView("assetsList", assetsModel) {
			protected void populateItem(final ListItem listItem) {
				if (listItem.getIndex() % 2 !=0){
					listItem.add(sam);
				}//if

				final Asset asset = (Asset)listItem.getModelObject();

				WebMarkupContainer chooseContainer = new WebMarkupContainer("chooseContainer");
				listItem.add(chooseContainer);
				
				if (!isViewMode){
					// TODO:
					final CheckBox chooseAssetCheckBox = new CheckBox("choose", new Model(selectedAssets.contains(asset)));
					
					if (isEditMode){
						if (allItemAssetsList.contains(asset)){
							chooseAssetCheckBox.setDefaultModelObject(new String("1"));
							selectedAssets.add(asset);
						}//if
					}//if
					chooseContainer.add(chooseAssetCheckBox);
					chooseAssetCheckBox.setOutputMarkupId(true);
					
					
					
					
					
					
					
					chooseAssetCheckBox.add(new AjaxFormComponentUpdatingBehavior("onchange"){
						@Override
						protected void onUpdate(AjaxRequestTarget target) {
							if (chooseAssetCheckBox.getModelObject() != null){
								Boolean isSelected = (Boolean)chooseAssetCheckBox.getModelObject();
								if(isSelected.equals(true)){
									selectedAssets.add(asset);
								}
								else{
									selectedAssets.remove(asset);
								}
							}
						}
					});
				}
				else{
					chooseContainer.add(new WebMarkupContainer("choose").setVisible(false));
					chooseContainer.setVisible(false);
				}//else

				// --- Asset Type ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        		Label assetType = new Label("assetType", localize(asset.getAssetType().getNameTranslationResourceKey()));
        		listItem.add(assetType);

        		final WebMarkupContainer customAttributesContainer = new WebMarkupContainer("customAttributesContainer");
        		customAttributesContainer.setOutputMarkupId(true);
        		listItem.add(customAttributesContainer);

        		final WebMarkupContainer customAttributesPanelContainer = new WebMarkupContainer("customAttributesPanel");
        		customAttributesPanelContainer.setOutputMarkupId(true);
        		customAttributesContainer.add(customAttributesPanelContainer);

        		ExpandCustomAttributesLink customAttributesLink = new ExpandCustomAttributesLink("showCustomAttributesLink", asset);
        		customAttributesLink.setComponentWhenCollapsed(customAttributesPanelContainer);
        		customAttributesLink.setTargetComponent(customAttributesContainer);
        		customAttributesLink.setImageWhenCollapsed(new CollapsedPanel("imagePanel"));
        		customAttributesLink.setImageWhenExpanded(new ExpandedPanel("imagePanel"));

        		CollapsedPanel imagePanel = new CollapsedPanel("imagePanel");
        		customAttributesLink.add(imagePanel);

        		listItem.add(customAttributesLink);

        		// --- Inventory Code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				listItem.add(new Label("inventoryCode", asset.getInventoryCode()));
				//format and display dates
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

				// --- Support Start Date ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				if(asset.getSupportStartDate() != null){
					listItem.add(new Label("supportStartDate", dateFormat.format(asset.getSupportStartDate())).add(new SimpleAttributeModifier("class", "date")));
				}
				else{
					listItem.add(new Label("supportStartDate", ""));
				}
				
				// --- Support End Date ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				if(asset.getSupportEndDate() != null){
					listItem.add(new Label("supportEndDate", dateFormat.format(asset.getSupportEndDate())).add(AssetsUtils.getSupportEndDateStyle(asset.getSupportEndDate())));
				}
				else{
					listItem.add(new Label("supportEndDate", ""));
				}
					
			}
		};
		itemAssetsListContainer.add(assetsListView);
		itemAssetsListContainer.setOutputMarkupId(true);

		return itemAssetsListContainer;
	}

	//---------------------------------------------------------------------------------------------

	public List<Asset> getSelectedAssets() {
		return selectedAssets;
	}

	//---------------------------------------------------------------------------------------------

	public List <Asset> getItemAssets(){
		return this.allItemAssetsList;
	}

	//---------------------------------------------------------------------------------------------

	public void renderAvailableAssets(){
		renderAvailableAssets(getRemainingAssets());
	}//renderAvailableAssets

	//---------------------------------------------------------------------------------------------

	public void renderItemAssets(){
		renderItemAssets(getAllAssetsForCurrentItem());
	}//renderItemAssets

}//ItemAssetsPanel