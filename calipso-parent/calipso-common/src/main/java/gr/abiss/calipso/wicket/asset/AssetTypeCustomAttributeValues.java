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
import gr.abiss.calipso.domain.CustomAttribute;
import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.Country;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.dto.KeyValuePair;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.components.viewLinks.AssetViewLink;
import gr.abiss.calipso.wicket.components.viewLinks.OrganizationViewLink;
import gr.abiss.calipso.wicket.components.viewLinks.UserViewLink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;


/**
 * @author marcello
 */
public class AssetTypeCustomAttributeValues extends BasePanel {
	
	private static final long serialVersionUID = 1L;
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(AssetTypeCustomAttributeValues.class);

	public AssetTypeCustomAttributeValues(String id, Long assetId) {
		super(id);
		Asset asset = getCalipso().loadAssetWithAttributes(assetId);
		List<KeyValuePair> customAttributes = KeyValuePair.fromMap(asset.getCustomAttributes());
		//if(logger.isDebugEnabled()){
		//	logger.debug("Custom attributes: "+customAttributes);
		//}
		addComponents(customAttributes, asset);
		
	}
	public AssetTypeCustomAttributeValues(String id,IBreadCrumbModel breadCrumbModel , Long assetId) {
		super(id,breadCrumbModel);
		Asset asset = getCalipso().loadAssetWithAttributes(assetId);
		List<KeyValuePair> customAttributes = KeyValuePair.fromMap(asset.getCustomAttributes());
		//if(logger.isDebugEnabled()){
		//	logger.debug("Custom attributes: "+customAttributes);
		//}
		addComponents(customAttributes, asset);
		
	}

	private void addComponents(List<KeyValuePair> customAttributes, Asset asset){
		
		final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
		
		@SuppressWarnings("serial")
		ListView listView = new ListView("attributeValuesList", customAttributes) {
			@Override
			protected void populateItem(ListItem listItem) {
				KeyValuePair entry = (KeyValuePair) listItem.getModelObject();
				AssetTypeCustomAttribute customAttr = (AssetTypeCustomAttribute) entry.getKey();
				String sValue = CustomAttribute.FORM_TYPE_TABULAR.equals(customAttr.getFormType())?null:(String) entry.getValue();
				
				if (listItem.getIndex()%2==0){
					listItem.add(sam);
				}//if
				/*
				
				if(customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_ASSET)){
					value = customAttr.getAssetValue().getInventoryCode();
				}
				else if(customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_USER)){
					value =customAttr.getUserValue().getDisplayValue();
				}
				else if(customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_ORGANIZATION)){
					value =customAttr.getOrganizationValue().getName();
				}
				else{
					value = customAttributesMap.get(customAttr);
				}
				
				Label customAttributeValueLabel = new Label("customAttributeValue", value);
				listItem.add(customAttributeValueLabel);
				*/
				// Label, attribute name
				Label customAttributeLabel = new Label("customAttribute", localize(customAttr.getNameTranslationResourceKey()));
				listItem.add(customAttributeLabel);
				
				String value = new String(localize("asset.customAttributeNoValue"));
				if (sValue != null && !sValue.isEmpty()) {
					value = sValue;
				}
				
				
				if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT) || customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_OPTIONS_TREE)) {
					CustomAttributeLookupValue lookupValue = customAttr.getLookupValue();
					logger.info("lookupValue: "+lookupValue);
					
					Label customAttributeValueLabel;
					if(lookupValue != null){
						customAttributeValueLabel = (Label) new Label("customAttributeValue", localize(lookupValue.getNameTranslationResourceKey())).setEscapeModelStrings(false);
					}
					else{
						customAttributeValueLabel = new Label("");
					}
					listItem.add(customAttributeValueLabel);
				}
				// this works for all componentViewLinks
				else if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_USER)) {
					// used this to proper render value and not throw exception if breadcrumb not exists
					if(getBreadCrumbModel() != null){
						UserViewLink userViewLink = new UserViewLink("customAttributeValue", getBreadCrumbModel(), customAttr.getUserValue());
						listItem.add(userViewLink);
					}else{
						Label customAttributeValueLabel = (Label) new Label("customAttributeValue", customAttr.getUserValue().getDisplayValue()).setEscapeModelStrings(false);
						listItem.add(customAttributeValueLabel);
					}
				} else if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_ORGANIZATION)) {
					
					if(getBreadCrumbModel() != null){// this works for all componentViewLinks
						OrganizationViewLink organizationViewLink = new OrganizationViewLink("customAttributeValue", getBreadCrumbModel(),customAttr.getOrganizationValue());
						listItem.add(organizationViewLink);
					}else{
						Label customAttributeValueLabel = (Label) new Label("customAttributeValue", customAttr.getOrganizationValue().getName()).setEscapeModelStrings(false);
						listItem.add(customAttributeValueLabel);
					}
				}
				else if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_COUNTRY)) {
					Country country = customAttr.getCountryValue();
					Label customAttributeValueLabel = (Label) new Label("customAttributeValue", country!=null?localize(country):"").setEscapeModelStrings(false);
					listItem.add(customAttributeValueLabel);
				}
				else if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_ASSET)) {
					if(getBreadCrumbModel() != null){// this works for all componentViewLinks
						// this works for all componentViewLinks
						AssetViewLink assetViewLink = new AssetViewLink("customAttributeValue", getBreadCrumbModel(),customAttr.getAssetValue());
						listItem.add(assetViewLink);
					}else{
						Label customAttributeValueLabel = (Label) new Label("customAttributeValue", customAttr.getAssetValue().getDisplayedValue());
						listItem.add(customAttributeValueLabel);
					}
				} else {
					Label customAttributeValueLabel = (Label) new Label("customAttributeValue", value).setEscapeModelStrings(false);
					listItem.add(customAttributeValueLabel);
				}
					
			}//populateItem
		};

//		 WebMarkupContainer noCustomAttributes = new WebMarkupContainer("noCustomAttributes");
		 Label noCustomAttributes = new Label("noCustomAttributes", new Model(localize("asset.noCustomAttributes", localize(asset.getAssetType().getNameTranslationResourceKey()))));
		 //asset.noCustomAttributes
		 noCustomAttributes.setVisible(customAttributes == null || (customAttributes !=null && customAttributes.size()==0));
		 add(noCustomAttributes);

		add(listView);
	}//addComponents
}