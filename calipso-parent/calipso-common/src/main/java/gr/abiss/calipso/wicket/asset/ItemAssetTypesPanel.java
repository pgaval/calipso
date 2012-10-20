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

import java.util.ArrayList;
import java.util.List;

import gr.abiss.calipso.domain.AbstractItem;
import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.wicket.BasePanel;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;


/**
 * @author marcello
 */
public class ItemAssetTypesPanel extends BasePanel {
	
	private Item item;
	private List<AssetType> assetTypes;
	
	public ItemAssetTypesPanel(String id, Item item) {
		super(id);
		
		this.item = item;
		
		renderItemAssetTypes(loadItemAssetTypes());
		
	}//ItemAssetTypesPanel

	// -------------------------------------------------------------

	public ItemAssetTypesPanel(String id, AbstractItem abstractItem) {
		super(id);
		this.item = new Item();
		this.item.setId(abstractItem.getId());

		renderItemAssetTypes(loadItemAssetTypes());

	}//ItemAssetTypesPanel
	
	//////////////////////////////////////////////////////////////////

	private IModel loadItemAssetTypes(){

        LoadableDetachableModel itemAssetsListModel = new LoadableDetachableModel() {
            protected Object load() {
            	List <Asset> itemAssets = getCalipso().findAllAssetsByItem(ItemAssetTypesPanel.this.item);
            	ItemAssetTypesPanel.this.assetTypes = new ArrayList<AssetType>();
            	if (itemAssets != null){
            		for (Asset asset: itemAssets) {
            			if (!ItemAssetTypesPanel.this.assetTypes.contains(asset.getAssetType())){
            				ItemAssetTypesPanel.this.assetTypes.add(asset.getAssetType());
            			}//if
            		}//for
            	}//if

            	
            	
            	return ItemAssetTypesPanel.this.assetTypes;
            }//load
        };//assetsListModel

        itemAssetsListModel.getObject();

        return itemAssetsListModel;

	}//loadItemAssets

	// -------------------------------------------------------------
	
	
	private void renderItemAssetTypes(final IModel itemAssetTypesModel){
		
		final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "altRow");
		
		ListView listView = new ListView("assetTypesList", itemAssetTypesModel) {
			protected void populateItem(final ListItem listItem) {
				if (listItem.getIndex() % 2 !=0){
					listItem.add(sam);
				}//if

				final AssetType assetType = (AssetType)listItem.getModelObject();
				listItem.add(new Label("assetType", localize(assetType.getNameTranslationResourceKey())));
			}
		};
		
		add(listView);
	}//renderItemAssetTypes
	
	////////////////////////////////////////////////////////////////////
	
	@Override
	public String toString() {
		
		String returnValue = new String("");
		
		if (this.assetTypes!=null){
			for (int i=0; i<this.assetTypes.size(); i++){
				returnValue += this.assetTypes.get(i).getName();
				if (i<this.assetTypes.size()-1){
					returnValue += ", ";
				}//if
			}//for
			return returnValue;
		}//if
			
		return returnValue;
	}//toString
	
	
}//ItemAssetTypesPanel

