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

import java.util.List;

import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.util.AssetsUtils;
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
public class ItemAssetsViewPanel extends BasePanel {

	private Item item;

	///////////////////////////////////////////////////////////////////////////

	public ItemAssetsViewPanel(String id, final Item item) {
		super(id);
		
        LoadableDetachableModel assetsListModel = new LoadableDetachableModel() {
            protected Object load() {
            	List<Asset> assetList = getCalipso().findAllAssetsByItem(item);
            	return assetList;
            }//load
        };//assetsListModel

        assetsListModel.getObject();

        addComponents(assetsListModel);
	}//ItemAssetsViewPanel

	///////////////////////////////////////////////////////////////////////////

	private void addComponents(final IModel assets){
		final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");

		ListView listView = new ListView("assetsList", assets) {
			protected void populateItem(final ListItem listItem) {
				if (listItem.getIndex() % 2 !=0){
					listItem.add(sam);
				}//if

				final Asset asset = (Asset)listItem.getModelObject();

				listItem.add(new Label("assetType", localize(asset.getAssetType().getNameTranslationResourceKey())));
				listItem.add(new Label("inventoryCode", asset.getInventoryCode()));
				listItem.add(new Label("supportStartDate", asset.getSupportStartDate().toString()));
				listItem.add(new Label("supportEndDate", asset.getSupportEndDate().toString()).add(AssetsUtils.getSupportEndDateStyle(asset.getSupportEndDate())));
			}
		};
		add(listView);
	}
}//ItemAssetsViewPanel