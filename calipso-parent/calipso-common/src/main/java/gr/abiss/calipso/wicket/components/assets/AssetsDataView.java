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

package gr.abiss.calipso.wicket.components.assets;

import gr.abiss.calipso.domain.Asset;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.Model;

/**
 *
 */
public abstract class AssetsDataView extends DataView {
	private IBreadCrumbModel breadCrumbModel;
	private static final long serialVersionUID = 1L;

	private static final int PAGE_SIZE = 10;
	
	final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
	
	/**
	 * @param id
	 * @param dataProvider
	 * @param itemsPerPage
	 */
	public AssetsDataView(String id, IDataProvider dataProvider, int itemsPerPage) {
		this(id, dataProvider, null, itemsPerPage);
	}

	/**
	 * @param id
	 * @param dataProvider
	 */
	public AssetsDataView(String id, IDataProvider dataProvider) {
		this(id, dataProvider, null, PAGE_SIZE);
		
	}
	
	/**
	 * When constructor is called adds 
	 * a default item per page ITEMS_PER_PAGE
	 * @param id
	 * @param dataProvider
	 * @param breadCrumbModel
	 */
	public AssetsDataView(String id, IDataProvider dataProvider, IBreadCrumbModel breadCrumbModel, int itemsPerPage) {
		super(id, dataProvider);
		this.breadCrumbModel = breadCrumbModel;
		setItemsPerPage(itemsPerPage);
		
		
	}
	
	/**
	 * @see org.apache.wicket.markup.repeater.RefreshingView#populateItem(org.apache.wicket.markup.repeater.Item)
	 */
	@Override
	protected void populateItem(Item item) {
		final Asset asset = (Asset)item.getModelObject();
		
		if (item.getIndex() % 2 != 0){
			item.add(sam);
		}
		
		item.add(new Label("typeLabel", this.getLocalizer().getString(asset.getAssetType().getNameTranslationResourceKey(), this)));
		
		// expanded link
		// to be sure that will work for all cases
		// even if no breadCrumbModel exists
		/*if(breadCrumbModel != null){
			ExpandCustomAttributesLink expandCustomAttributesLink = 
				new ExpandCustomAttributesLink("expandCustomAttributesLink", breadCrumbModel, asset);
			item.add(expandCustomAttributesLink);
		}else{
			item.add(new WebMarkupContainer("expandCustomAttributesLink").setVisible(false).setRenderBodyOnly(true));
		}*/
		
		item.add(new Label("inventoryCodeLabel", new Model(asset.getInventoryCode())));
		// SimpleDateFormat to convert Date object to String
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		Date supportStartDate  = asset.getSupportStartDate();
		String startDate = (asset.getSupportStartDate() != null)?df.format(supportStartDate):"";
		item.add(new Label("supportStartDateLabel", new Model(startDate)));
		
		Date supportEndDate  = asset.getSupportEndDate();
		String endDate = (supportEndDate != null)?df.format(supportEndDate):"";
		item.add(new Label("supportEndDateLabel", new Model(endDate)));
		
		// space
		item.add(new Label("spacenameLabel", this.getLocalizer().getString(asset.getSpace().getNameTranslationResourceKey(), this)));
		
		item.add(new AjaxLink("add"){

			@Override
			public void onClick(AjaxRequestTarget target) {
				onAddAssetClick(asset, target);
			}
			
		});
	}
	
	public abstract void onAddAssetClick(Asset asset, AjaxRequestTarget target);
}
