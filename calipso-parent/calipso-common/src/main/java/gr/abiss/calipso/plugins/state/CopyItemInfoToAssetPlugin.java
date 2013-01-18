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

package gr.abiss.calipso.plugins.state;

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.domain.Attachment;
import gr.abiss.calipso.domain.Country;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.plugins.state.AbstractStatePlugin;
import gr.abiss.calipso.util.AttachmentUtils;
import gr.abiss.calipso.util.DateUtils;
import gr.abiss.calipso.util.ItemUtils;
import gr.abiss.calipso.util.XmlUtils;


import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.NumberUtils;
import org.apache.log4j.Logger;


/**
 * Creates an Asset using data from Item attributes. 
 * 
 * To subclass this just add a default constructor and initialize 
 * assetTypeName in it.
 */
public class CopyItemInfoToAssetPlugin extends AbstractStatePlugin{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(CopyItemInfoToAssetPlugin.class);
	
	private Asset asset = null;
	private AssetType assetType;

	protected CopyItemInfoToAssetPlugin(){
		super();
	}
	
	/**
	 * Called to create a new Asset
	 * @param assetType
	 */
	public CopyItemInfoToAssetPlugin(AssetType assetType){
		this.assetType = assetType;
	}

	/**
	 * Called to update an existing Asset
	 * @param assetType
	 */
	public CopyItemInfoToAssetPlugin(Asset asset){
		this.asset = asset;
		this.assetType = asset.getAssetType();
	}

	public Serializable executePostStateChange(CalipsoService calipsoService, History history){
		// if new
		if(asset == null){
			asset = new Asset();
			asset.setAssetType(assetType);
			asset.setCreatedBy(history.getLoggedBy());
			// date added in the inventory
			asset.setSupportStartDate(new Date());
		}
		
		// get the item the history belongs to
		Item item = history.getParent();
		// copy information from item
		initFromHistoryItem(calipsoService, item, asset, assetType);
		// add to asset and history
		item.addAsset(asset);
		// save the asset 
		calipsoService.storeAsset(asset);
		// update html and plain text comment
		String htmlSuffix = ItemUtils.fmt("item_view.automatically.created.asset", new Object[]{assetType.getName(), asset.getInventoryCode()}, calipsoService.getMessageSource(), new Locale(history.getLoggedBy().getLocale()));
		history.setHtmlComment(history.getHtmlComment()+htmlSuffix);
		history.setComment(history.getComment()+XmlUtils.stripTags(htmlSuffix));
		calipsoService.updateItem(item, history.getLoggedBy(), false);
		//calipsoService.updateHistory(history);
		return asset;
	}


	/**
	 * Initialize the given Asset's custom properties, space and inventoryCode 
	 * (using the item id and only if not already set) from the History's parent (Item). 
	 * @param history
	 * @param asset
	 * @param assetType
	 */
	protected void initFromHistoryItem(CalipsoService calipsoService, Item item, Asset asset, AssetType assetType) {
		// add to space
		if(asset.getSpace() == null){
			asset.setSpace(item.getSpace());
		}
		if(asset.getInventoryCode() == null){
			// use the item creating the asset as an inventory code
			asset.setInventoryCode(UUID.randomUUID().toString());
		}
		List<Field> itemFields = item.getSpace().getMetadata().getFieldList();
		if(CollectionUtils.isNotEmpty(assetType.getAllowedCustomAttributes())){
			for(AssetTypeCustomAttribute assetTypeAttr : assetType.getAllowedCustomAttributes()){
				// if attribute holds an asset
				if(assetTypeAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_ASSET)){
					initAssetAttributeUsingItemAssets(item, asset, assetTypeAttr);
				}
				else if(assetTypeAttr.getName().equals("Created by")){
					asset.addOrReplaceCustomAttribute(assetTypeAttr, item.getLoggedBy().getId()+"");
				}
				else{
					initAssetAttributeUsingItemFields(calipsoService, item, asset, itemFields,	assetTypeAttr);
				}
			}
		}
	}

	/**
	 * @param item
	 * @param asset
	 * @param assetTypeAttr
	 */
	private void initAssetAttributeUsingItemAssets(Item item, Asset asset,
			AssetTypeCustomAttribute assetTypeAttr) {
		Set<Asset> assets = item.getAssets();
		if(CollectionUtils.isNotEmpty(assets)){
			// use the first asset who's type name
			// equals the custom attribute name
			for(Asset innerAsset : assets){
				if(innerAsset.getAssetType().getName().trim().equals(assetTypeAttr.getName().trim())){
					asset.addOrReplaceCustomAttribute(assetTypeAttr, innerAsset.getId().toString());
					break;
				}
			}
		}
	}

	/**
	 * @param calipsoService 
	 * @param item
	 * @param asset
	 * @param itemFields
	 * @param assetTypeAttr
	 */
	private void initAssetAttributeUsingItemFields(CalipsoService calipsoService, Item item, Asset asset,
			List<Field> itemFields, AssetTypeCustomAttribute assetTypeAttr) {
		for(Field field : itemFields){
			// got a match?
			if(field.getLabel().trim().equals(assetTypeAttr.getName().trim())){
				// DropDown
				if(field.getName().isDropDownType()){
					// Item field and Asset attribute options need to be in the same order
					//calipsoService.loadCustomAttributeLookupValue
					List<CustomAttributeLookupValue> assetLookupValues = assetTypeAttr.getAllowedLookupValues();
					
					// we save the option index for Item (the option label is in the XML) 
					// so we have to match the lookup value with the same index for the custom attribute
					// this works under the assumption the available options ordering is the same 
					// for both the Item field and Asset attribute
					Object lookupValueId = item.getValue(field.getName());
					//logger.info("Item lookupValueId: "+lookupValueId + " for field: "+field.getName().getText());
					CustomAttributeLookupValue itemLookupValue = lookupValueId != null ? calipsoService.loadCustomAttributeLookupValue(NumberUtils.createLong(lookupValueId.toString()).longValue()) : null;

					//logger.info("Item lookup value: "+itemLookupValue);
					//		calipsoService.loadCustomAttribute(item.getSpace(), field.getName().getText()), 
						//	field.getName().getText());
					
					if(itemLookupValue != null){
						// logger.debug("Looking for a match for item field with index "+fieldSelectedIndex+" and name "+field.getName().getText());
						for(CustomAttributeLookupValue v : assetLookupValues){
							//logger.debug("Checking for a match with id: "+v.getId()+", showOrder "+v.getShowOrder()+", value: "+v.getValue()+" and name: "+v.getName()+", match: "+(v.getShowOrder() == itemLookupValue.getShowOrder()));
							if(v.getShowOrder() == itemLookupValue.getShowOrder()){
								//logger.info("initAssetAttributeUsingItemFields: matched asset lookup value: "+v);
								asset.addOrReplaceCustomAttribute(assetTypeAttr, v.getId()+"");
								break;
							}
						}
					}
				}
				// Decimal
				else if(field.getName().isDecimalNumber()){
					asset.addOrReplaceCustomAttribute(assetTypeAttr, ((Double) item.getValue(field.getName()))+"");
				}
				// String
				else if(field.getName().isFreeText()){
					asset.addOrReplaceCustomAttribute(assetTypeAttr, ((String) item.getValue(field.getName())));
				}
				// date
				else if(field.getName().isDate()){
					asset.addOrReplaceCustomAttribute(assetTypeAttr, DateUtils.format((Date) item.getValue(field.getName())));
				}
				// Organization
				else if(field.getName().isOrganization()){
					asset.addOrReplaceCustomAttribute(assetTypeAttr, ((Organization) item.getValue(field.getName())).getId()+"");
				}
				// File
				else if(field.getName().isFile()){
					for(Attachment attachment : item.getAttachments()){
						if(AttachmentUtils.getBaseName(attachment.getFileName()).equals(field.getName())){
							// found our attachment
							asset.addOrReplaceCustomAttribute(assetTypeAttr, attachment.getId()+"");
						}
					}
				}
				// User
				else if(field.getName().isUser()){
					asset.addOrReplaceCustomAttribute(assetTypeAttr, ((User) item.getValue(field.getName())).getId()+"");
				}
				// Country
				else if(field.getName().isCountry()){
					asset.addOrReplaceCustomAttribute(assetTypeAttr, ((Country) item.getValue(field.getName())).getId());
				}
			}
		}
	}
}
