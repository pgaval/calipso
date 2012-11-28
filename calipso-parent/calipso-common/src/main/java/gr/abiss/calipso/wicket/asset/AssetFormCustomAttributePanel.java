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
import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.domain.Country;
import gr.abiss.calipso.domain.CustomAttribute;
import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.dto.KeyValuePair;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.ErrorHighlighter;
import gr.abiss.calipso.wicket.MandatoryPanel;
import gr.abiss.calipso.wicket.components.formfields.TreeChoice;
import gr.abiss.calipso.wicket.components.renderers.UserChoiceRenderer;
import gr.abiss.calipso.wicket.components.validators.RegexpValidator;
import gr.abiss.calipso.wicket.yui.YuiCalendar;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 * Used to render CustomAttribute form fields for creation, editing and search
 */
public class AssetFormCustomAttributePanel extends BasePanel {
	
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(AssetFormCustomAttributePanel.class);

	public Asset asset;
	private ListView customAttributesList;
	private List<AttributeValue> attributeValueList;
	private AssetType assetType;
	private boolean isSearchMode = false;
	
	// For drop-down choices
	List<Organization> allVisibleOrganizations = null;
	List<Country> allVisibleCountries = null;
	List<User> allVisibleUsers = null;
	List<Asset> allVisibleAssets = null;
	/**
	 * Called when creating/editing new Asset to render and set values
	 * to custom Attributes
	 * @param id
	 * 		Markup id of the panel
	 * @param breadCrumbModel
	 * 		BreadCrumb Model of the panel
	 * @param asset
	 * 		The asset 
	 * @return
	 * 		WebMarkupContainer with custom Attributes components
	 */
	public AssetFormCustomAttributePanel(String id, IBreadCrumbModel breadCrumbModel, Asset asset) {
		super(id, breadCrumbModel);
		isSearchMode = false;
		this.asset = asset;
		this.assetType = asset.getAssetType();
		// init custom attributes for new Asset using all the allowed attributes by AssetType
		Map<AssetTypeCustomAttribute, String> attributesMap = new TreeMap<AssetTypeCustomAttribute,String>();
		// just a test to investigate attribute behavior as map keys
		logger.debug("Got custom attributes("+asset.getCustomAttributes().size()+"): "+asset.getCustomAttributes());
		if(MapUtils.isNotEmpty(this.asset.getCustomAttributes())){
			for(CustomAttribute assetTypeAttr : this.asset.getCustomAttributes().keySet()){
				logger.debug("Got attribute from map: "+assetTypeAttr);
				logger.debug("Attribute contained in map: "+attributesMap.containsKey(assetTypeAttr));
			}
			
		}
		
		
		for(AssetTypeCustomAttribute assetTypeAttr : this.assetType.getAllowedCustomAttributes()){
			
			// use the existing value if available, unfortunately AssetTypeCustomAttribute
			// instances do not behave well as Map keys...
			boolean exists = false;
			if(MapUtils.isNotEmpty(asset.getCustomAttributes())){
				for(Entry<AssetTypeCustomAttribute, String> existingEntry : asset.getCustomAttributes().entrySet()){
					if(existingEntry.getKey().equals(assetTypeAttr)){
						attributesMap.put(assetTypeAttr, existingEntry.getValue());
						exists = true;
						break;
					}
				}
			}
			// else just initialize to null
			if(!exists){
				attributesMap.put(assetTypeAttr, null);
			}
		}
		renderAttributes(KeyValuePair.fromMap(attributesMap));
	}
	
	/**
	 * Called for searching assets by custom attribute.
	 * @param id
	 * @param breadCrumbModel
	 * @param assetType
	 * @param attributesMap
	 */
	public AssetFormCustomAttributePanel(String id, IBreadCrumbModel breadCrumbModel, AssetType assetType,
		Map<AssetTypeCustomAttribute, String> attributesMap) {
		super(id, breadCrumbModel);
		isSearchMode = true;
		this.assetType = assetType;
		this.asset = new Asset();
		this.asset.setAssetType(assetType);
		logger.debug("Constructor 2 with custom attributes: "+attributesMap);
		renderAttributes(KeyValuePair.fromMap(attributesMap));
	}

	
	public List<AttributeValue> getAttributeValueList() {
		return attributeValueList;
	}
	
	private void renderAttributes(List<KeyValuePair> attributeEntryList) {
		// remove the old listView
		if (customAttributesList != null) {
			remove(customAttributesList);
		}
		
		customAttributesList = new ListView("fields", attributeEntryList) {
			@Override
			protected void populateItem(ListItem listItem) {
				KeyValuePair entry = (KeyValuePair) listItem.getModelObject();
				final AssetTypeCustomAttribute attribute = (AssetTypeCustomAttribute) entry.getKey();
				if (attribute.isActive()) {
					Fragment labelFragment = new Fragment("fieldLabel", "mandatory", this);
					//Label label = new Label("label", attribute.getName());
					Label label = new Label("label", new ResourceModel(attribute.getNameTranslationResourceKey()));
					labelFragment.add(label);

					if (attribute.isMandatory() && !isSearchMode) {
						labelFragment.add(new MandatoryPanel("mandatoryPanel"));
					}
					else {
						labelFragment.add(new WebMarkupContainer(
								"mandatoryPanel"));
					}
					listItem.add(labelFragment);

					//String value = null;
					//if (AssetFormCustomAttributePanel.this.attributesMap != null
					//		&& AssetFormCustomAttributePanel.this.attributesMap.size() > 0) {
						
					//	value = AssetFormCustomAttributePanel.this.attributesMap.get(attribute);
					//}// if

					/*AssetCustomAttributeValue attributeValue = new AssetCustomAttributeValue(
							AssetFormCustomAttributePanel.this.asset,
							attribute, value);*/
					listItem.add(renderAttribute(entry));
				}// if
				else {
					listItem.add(new WebMarkupContainer("fieldLabel")
							.setVisible(false));
					listItem.add(new WebMarkupContainer("customAttributes")
							.setVisible(false));
				}
			}// populateItem
		};

		add(customAttributesList);
	}// renderAttributes

	// ----------------------------------------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	private Component renderAttribute(KeyValuePair attributeEntry) {
		FormComponent formComponent = null;
		Component fragment = null;
		
		if (attributeValueList == null) {
			attributeValueList = new LinkedList<AttributeValue>();
		}
		
		AssetTypeCustomAttribute attribute = (AssetTypeCustomAttribute) attributeEntry.getKey();
		String sValue = (String) attributeEntry.getValue();
		// Drop-Down Field
		if (attribute.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)) {

			//logger.info("PROCESSING simple select attribute: "+attribute.getName());
			fragment = new Fragment("customAttributes", "dropDownField", this);
			final List<CustomAttributeLookupValue> lookupValues = getCalipso().findAllLookupValuesByCustomAttribute(attribute);
			// TODO: move this into a LookupValueDropDownChoice class
			if(attribute.getLookupValue() == null){
				String defaultStringValue = attribute.getDefaultStringValue();
				if(defaultStringValue != null && CollectionUtils.isNotEmpty(lookupValues)){
					for(CustomAttributeLookupValue value : lookupValues){
						if(value.getValue().equals(defaultStringValue)){
							attribute.setLookupValue(value);
							break;
						}
					}
				}
				
			}
			//logger.info("LOADED simple select attribute options: "+lookupValues);
			DropDownChoice fieldValueChoice = new DropDownChoice(
					"customAttribute.allowedLookupValues.id", new PropertyModel(attribute, "lookupValue"),
					lookupValues, new IChoiceRenderer() {
						public Object getDisplayValue(Object o) {
							return ((CustomAttributeLookupValue) o).getValue();
						}
						public String getIdValue(Object o, int i) {
							return i+"";
						}
					});
			fieldValueChoice.setNullValid(!attribute.isMandatory() || isSearchMode);
			attributeValueList.add(new AttributeValue(fieldValueChoice,	attribute));
			// for all-cases-logic to apply later on, i.e. set mandatory, labels etc.
			formComponent = fieldValueChoice;
		}
		// hierarchical select
		else if (attribute.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_OPTIONS_TREE)) {
			fragment = new Fragment("customAttributes", "treeField", this);
			logger.info("PROCESSING tree select attribute: "+attribute.getName());
			List<CustomAttributeLookupValue> customAttributeLookupValues = getCalipso().findLookupValuesByCustomAttribute(attribute);
			TreeChoice treeChoice = new TreeChoice(
					"customAttribute.allowedLookupValues.id", 
					new PropertyModel(attribute, "lookupValue"), customAttributeLookupValues, attribute);
			treeChoice.setType(CustomAttributeLookupValue.class);
			attributeValueList.add(new AttributeValue(treeChoice, attribute));
			// for all-cases-logic to apply later on, i.e. set mandatory, labels etc.
			formComponent = treeChoice;
		}
		// User
		else if (attribute.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_USER)) {
			fragment = new Fragment("customAttributes", "dropDownFragment", this);
			if(allVisibleUsers == null){
				allVisibleUsers = getCalipso().findAllUsers();
			}
			DropDownChoice userChoice = new DropDownChoice("attributeValue",
					new PropertyModel(attribute, "userValue"), allVisibleUsers, new UserChoiceRenderer());
			userChoice.setEscapeModelStrings(false);
			attributeValueList.add(new AttributeValue(userChoice, attribute));
			// for all-cases-logic to apply later on, i.e. set mandatory, labels etc.
			formComponent = userChoice;
		}
		// Organization
		else if (attribute.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_ORGANIZATION)) {
			fragment = new Fragment("customAttributes", "dropDownFragment", this);
			if(allVisibleOrganizations == null){
				allVisibleOrganizations = getCalipso().findAllOrganizations();
			}
			DropDownChoice organizationChoice = new DropDownChoice("attributeValue", 
					new PropertyModel(attribute, "organizationValue"), allVisibleOrganizations, new IChoiceRenderer() {
						public Object getDisplayValue(Object object) {
							Organization organization = (Organization) object;
							return organization.getName();
						}
						public String getIdValue(Object object, int index) {
							// TODO Auto-generated method stub
							return index + "";
						}
					});
			attributeValueList.add(new AttributeValue(organizationChoice, attribute));
			// for all-cases-logic to apply later on, i.e. set mandatory, labels etc.
			formComponent = organizationChoice;
		}		
		// Country
		else if (attribute.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_COUNTRY)) {
			fragment = new Fragment("customAttributes", "dropDownFragment", this);
			if(allVisibleCountries == null){
				allVisibleCountries = getCalipso().findAllCountries();
			}
			DropDownChoice countryChoice = getCountriesDropDown("attributeValue", allVisibleCountries);
			countryChoice.setModel(new PropertyModel(attribute, "countryValue"));
			attributeValueList.add(new AttributeValue(countryChoice, attribute));
			// for all-cases-logic to apply later on, i.e. set mandatory, labels etc.
			formComponent = countryChoice;
		}
		// Asset (as custom attribute value)
		else if (attribute.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_ASSET)) {
			fragment = new Fragment("customAttributes", "dropDownFragment", this);
			// TODO: Check allVisibleAssets to get proper assets
			if(allVisibleAssets == null){
				allVisibleAssets = (List<Asset>) getCalipso().getVisibleAssetsForSpace(getCurrentSpace());
			}
			DropDownChoice assetChoice = new DropDownChoice("attributeValue",
					new PropertyModel(attribute, "assetValue"), allVisibleAssets, new IChoiceRenderer() {
						public Object getDisplayValue(Object object) {
							Asset asset = (Asset) object;
							return new StringBuffer(asset.getInventoryCode())
									.append("(").append(asset.getAssetType().getName())
									.append(", ").append(asset.getSpace().getName()).append(")");
						}
						public String getIdValue(Object object, int index) {
							return index + "";
						}

					});
			attributeValueList.add(new AttributeValue(assetChoice, attribute));
			// for all-cases-logic to apply later on, i.e. set mandatory, labels etc.
			formComponent = assetChoice;
		}

		// Text Field or Number Field
		else if (attribute.getFormType().equals(
				AssetTypeCustomAttribute.FORM_TYPE_TEXT)
				|| attribute.getFormType().equals(
						AssetTypeCustomAttribute.FORM_TYPE_NUMBER)) {
			fragment = new Fragment("customAttributes", "textField", this);
			TextField textField = new TextField("attributeValue", new Model(sValue));
			if (attribute.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_NUMBER)) {
				textField.setType(Double.class);
			} else {// is type text
				String regexp = attribute.getValidationExpression().getExpression();
				if (!regexp.equals(RegexpValidator.NO_VALIDATION)) {
					textField.add(new PatternValidator(regexp));
				}
			}
			attributeValueList.add(new AttributeValue(textField, attribute));
			// for all-cases-logic to apply later on, i.e. set mandatory, labels etc.
			formComponent = textField;
		}// if

		// Date field
		else if (attribute.getFormType().equals(
				AssetTypeCustomAttribute.FORM_TYPE_DATE)) {
			//AssetCustomAttributeDateValue dateValue = new AssetCustomAttributeDateValue(
			//		attributeValue);
			YuiCalendar calendar = new YuiCalendar("customAttributes",
					//new PropertyModel(new Model(dateValue), "value"),
					new Model(),
					attribute.isMandatory() && !isSearchMode);
			calendar.add(new ErrorHighlighter());
			attributeValueList.add(new AttributeValue(calendar, attribute));
			fragment = calendar;
		}
		// else nothing
		else{
			fragment = new WebMarkupContainer("customAttributes");
		}
		// configure common validation/label etc. if applicable
		logger.info("Fragment: "+fragment+", formcomponent: "+formComponent+", attribute: "+attribute.getName());
		if(formComponent != null){
			if(fragment instanceof Fragment){
				//logger.info("fragment.add attribute: "+attribute.getName()+", form component: "+formComponent);
				((Fragment) fragment).add(formComponent);
			}
			formComponent.setRequired(attribute.isMandatory() && !isSearchMode);
			formComponent.add(new ErrorHighlighter());
			formComponent.setLabel(new ResourceModel(attribute.getNameTranslationResourceKey()));
		}
		
		return fragment;
	}// renderAttribute
}