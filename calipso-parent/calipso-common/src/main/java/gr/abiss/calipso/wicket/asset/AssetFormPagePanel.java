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
import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.ErrorHighlighter;
import gr.abiss.calipso.wicket.CalipsoFeedbackMessageFilter;
import gr.abiss.calipso.wicket.MandatoryPanel;
import gr.abiss.calipso.wicket.components.formfields.TreeChoice;
import gr.abiss.calipso.wicket.components.renderers.AssetTypeRenderer;
import gr.abiss.calipso.wicket.components.validators.UniqueAssetInventoryCodeValidator;
import gr.abiss.calipso.wicket.yui.YuiCalendar;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

/**
 *  Used to create an Asset instance
 */
public class AssetFormPagePanel extends BasePanel {
	
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(AssetFormPagePanel.class);
	private Asset asset;
	private AssetFormCustomAttributePanel customAttributesPanel;
	private List<AttributeValue> attributeValueList;
	private boolean isEdit;
	
	/**
	 *  Creating new Asset
	 */
	public AssetFormPagePanel(String id, IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);

		this.asset = new Asset();
		asset.setSpace(getCurrentSpace());
		this.isEdit = false;
		addComponents();
	}// AssetFormPage

	// -------------------------------------------------------------------------
	/**
	 * Edit an asset
	 */
	public AssetFormPagePanel(String id, IBreadCrumbModel breadCrumbModel,
			Asset asset) {
		super(id, breadCrumbModel);
		this.isEdit = true;
		this.asset = getCalipso().loadAssetWithAttributes(asset.getId());
		addComponents();
	}// AssetFormPage

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// try set selected Asset on previous AssetSpacePanel class
	// can add more classes
	public void setHighlightOnPreviousPage(long selectedAssetId) {
		// try for AssetSpacePanel
		BreadCrumbPanel previous = BreadCrumbUtils.getPanel(
				getBreadCrumbModel(), AssetSpacePanel.class);
		if (previous != null) {
			((AssetSpacePanel) previous).setSelectedAssetId(selectedAssetId);
		}
	}

	private void addComponents() {
		getBackLinkPanel().makeCancel();// make back link to show cancel
		add(new Label("title", localize("asset.form.assetData")));
		add(new AssetForm("form", this.asset));
	}// addComponents

	// -------------------------------------------------------------------------

	@Override
	public String getTitle() {
		if (this.isEdit) {
			return localize("asset.edit.title", localize(this.asset.getSpace().getNameTranslationResourceKey()));
		}

		return localize("asset.create.title", localize(this.asset.getSpace().getNameTranslationResourceKey()));
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * wicket form
	 */
	private class AssetForm extends Form {
		private DropDownChoice assetTypeChoice;
		private CalipsoFeedbackMessageFilter filter;

		AssetForm(String id, Asset asset) {
			super(id);
			// Feedback staff
			// add feedbackPanel
			FeedbackPanel feedback = new FeedbackPanel("feedback");
			// Set new filter to use on the feedback messages model
			filter = new CalipsoFeedbackMessageFilter();
			feedback.setFilter(filter);
			add(feedback);

			add(new MandatoryPanel("mandatoryPanel"));

			CompoundPropertyModel model = new CompoundPropertyModel(asset);
			setModel(model);
			
			// Fragment for rendering user's 
			// TODO: make more general to cover all 
			Fragment dropDownFragment = new Fragment("extraAssetComponents","dropDownFragment", this);
			add(dropDownFragment);
			
			// Inventory Code ====================================
			TextField inventoryCode = new TextField("inventoryCode", new PropertyModel(model, "inventoryCode"));
			// TODO: make this validator or whatever check the input code via ajax before allowing submit. 
			// this is too much
			final List<Asset> allVisibleAssets = getCalipso().findAllAssetsBySpace(getCurrentSpace());
			inventoryCode.add(new UniqueAssetInventoryCodeValidator(allVisibleAssets, asset.getInventoryCode()));
			if(inventoryCode.getModelObject() != null){
				// after the asset is created you can't edit the inventory
				inventoryCode.setEnabled(false);
			}
			inventoryCode.setRequired(true);
			inventoryCode.add(new ErrorHighlighter());
			add(inventoryCode);

			// form label
			inventoryCode
					.setLabel(new ResourceModel("asset.form.inventoryCode"));
			add(new SimpleFormComponentLabel("inventoryCodeLabel",
					inventoryCode));

			// -------------------------------------------------------------------------------------

			// Support Start Date ====================================
			final YuiCalendar supportStartDate = new YuiCalendar(
					"supportStartDate", new PropertyModel(model,
							"supportStartDate"), false);
			//supportStartDate.add(new ErrorHighlighter());
			add(supportStartDate);
			// form label
			supportStartDate.setLabel(new ResourceModel(
					"asset.form.supportStartDate"));
			add(new SimpleFormComponentLabel("supportStartDateLabel",
					supportStartDate));

			// Support End Date ====================================
			final YuiCalendar supportEndDate = new YuiCalendar(
					"supportEndDate",
					new PropertyModel(model, "supportEndDate"), false);
			add(supportEndDate);
			// form label
			supportEndDate.setLabel(new ResourceModel(
					"asset.form.supportEndDate"));
			add(new SimpleFormComponentLabel("supportEndDateLabel",
					supportEndDate));

			// display AssetTypes ====================================
			List<AssetType> assetTypes = getCalipso().findAllAssetTypes();

			assetTypeChoice = new DropDownChoice("assetType",
					assetTypes, new IChoiceRenderer(){
				public Object getDisplayValue(Object o) {
					return localize(((AssetType) o).getNameTranslationResourceKey());
				}

				public String getIdValue(Object o, int i) {
					return i+"";
				}
			});

			// Display with Ajax the corresponding attributes according to Asset
			// type
			assetTypeChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					// inform the asset of it's new type
					AssetFormPagePanel.this.asset.setAssetType((AssetType) assetTypeChoice.getModelObject());
					// render form fields for custom attributes allowed for the specific type
					final AssetFormCustomAttributePanel customAttributesPanel = new AssetFormCustomAttributePanel(
							"customAttributesPanel", getBreadCrumbModel(), AssetFormPagePanel.this.asset);
					if(customAttributesPanel != null) {
						AssetForm.this.remove(customAttributesPanel);
					}
					AssetForm.this.add(customAttributesPanel);
					target.addComponent(AssetForm.this);
					AssetFormPagePanel.this.customAttributesPanel = customAttributesPanel;
				}
			});

			assetTypeChoice.setRequired(true).add(new ErrorHighlighter());
			assetTypeChoice.setEnabled(AssetFormPagePanel.this.asset.getId() == null);
			add(assetTypeChoice);
			// form label
			assetTypeChoice.setLabel(new ResourceModel("asset.form.assetType"));
			add(new SimpleFormComponentLabel("assetTypeLabel", assetTypeChoice));

			// New Asset
			if (AssetFormPagePanel.this.asset.getId() == null) {
				add(new WebMarkupContainer("customAttributesPanel"));
			}// if
			else {
				customAttributesPanel = new AssetFormCustomAttributePanel(
						"customAttributesPanel", getBreadCrumbModel(), AssetFormPagePanel.this.asset);
				add(customAttributesPanel);
			}// else

			// Validation. Support Start date must be prior to Support End date
			AbstractFormValidator dateValidator = new AbstractFormValidator() {
				public FormComponent[] getDependentFormComponents() {
					return new FormComponent[] { supportStartDate,
							supportEndDate };
				}// getDependentFormComponents

				public void validate(Form form) {
					Date startDate = supportStartDate.getDateValue();
					Date endDate = supportEndDate.getDateValue();

					if (startDate != null && endDate != null) {
						if (startDate.after(endDate)) {
							supportStartDate
									.error(localize("asset.form.invalidSupportStartDate"));
						}// if
					}// if
				}// validate
			};

			add(dateValidator);

		}// AssetForm

		/*
		@Override
		protected void validate() {
			filter.reset();

			super.validate();
		}// validate
		*/
		// --------------------------------------------------------------------------------------------------

		@Override
		protected void onSubmit() {
			attributeValueList = customAttributesPanel.getAttributeValueList();

			if (CollectionUtils.isNotEmpty(attributeValueList)) {
				SortedMap<AssetTypeCustomAttribute, String> customAttributes = new TreeMap<AssetTypeCustomAttribute, String>();
				for (int i = 0; i < attributeValueList.size(); i++) {
					AttributeValue attributeValue = attributeValueList.get(i);

					if (attributeValue.getAssetTypeCustomAttribute().getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_DATE)) {
						YuiCalendar calendar = (YuiCalendar) attributeValue.getFormComponent();
						customAttributes.put(attributeValue.getAssetTypeCustomAttribute(), (calendar != null)?calendar.getDateValueAsString():null);
					}
					else if(attributeValue.getAssetTypeCustomAttribute().getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)){
						DropDownChoice ddc = (DropDownChoice) attributeValue.getFormComponent();
						CustomAttributeLookupValue assetTypeCustomAttributeLookupValue = (CustomAttributeLookupValue) ddc.getModelObject();
						customAttributes.put(attributeValue.getAssetTypeCustomAttribute(), (assetTypeCustomAttributeLookupValue != null)?
								assetTypeCustomAttributeLookupValue.getId()+"":null);
					}
					else if(attributeValue.getAssetTypeCustomAttribute().getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_OPTIONS_TREE)){
						TreeChoice ddc = (TreeChoice) attributeValue.getFormComponent();
						CustomAttributeLookupValue assetTypeCustomAttributeLookupValue = (CustomAttributeLookupValue) ddc.getModelObject();
						customAttributes.put(attributeValue.getAssetTypeCustomAttribute(), (assetTypeCustomAttributeLookupValue != null)?
								assetTypeCustomAttributeLookupValue.getId()+"":null);
					}
					else if(attributeValue.getAssetTypeCustomAttribute().getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_USER)){ // is User
						DropDownChoice ddc = (DropDownChoice) attributeValue
						.getFormComponent();
						User user = (User) ddc.getModelObject();
						customAttributes.put(attributeValue.getAssetTypeCustomAttribute(), (user != null)?user.getId()+"":null);
					}
					else if(attributeValue.getAssetTypeCustomAttribute().getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_COUNTRY)){ // is User
						DropDownChoice ddc = (DropDownChoice) attributeValue
						.getFormComponent();
						Country country = (Country) ddc.getModelObject();
						customAttributes.put(attributeValue.getAssetTypeCustomAttribute(), (country != null)?country.getId():null);
					}
					else if(attributeValue.getAssetTypeCustomAttribute()
							.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_ORGANIZATION)){ // is User
						DropDownChoice ddc = (DropDownChoice) attributeValue
						.getFormComponent();
						Organization organization = (Organization) ddc.getModelObject();
						customAttributes.put(attributeValue.getAssetTypeCustomAttribute(), (organization != null)?organization.getId()+"":null);
						
					}
					else if(attributeValue.getAssetTypeCustomAttribute().getFormType().equals(
									AssetTypeCustomAttribute.FORM_TYPE_ASSET)){ // is organization
						DropDownChoice ddc = (DropDownChoice) attributeValue
						.getFormComponent();
						Asset tmpAsset = (Asset) ddc.getModelObject();
						customAttributes.put(attributeValue.getAssetTypeCustomAttribute(), (tmpAsset != null)?tmpAsset.getId()+"":null) ;
					}
					else {
						FormComponent field = attributeValue.getFormComponent();
						AssetTypeCustomAttribute att = attributeValue.getAssetTypeCustomAttribute();
						String sValue =  field.getModelObject() != null? field.getModelObject().toString():null;
						if(sValue != null && sValue.length() > 0){
							customAttributes.put(att, sValue);
						}
					}
				}
				
				// clear old attributes
				asset.getCustomAttributes().clear();
				// set new attributes
				asset.setCustomAttributes(customAttributes);
				
			}
			getCalipso().storeAsset(asset);
			
			// highlight this asset on previous page
			setHighlightOnPreviousPage(asset.getId());

			activate(new IBreadCrumbPanelFactory() {
				public BreadCrumbPanel create(String id,
						IBreadCrumbModel breadCrumbModel) {
					BreadCrumbUtils.popPanels(2, breadCrumbModel);

					AssetSpacePanel assetSpacePanel = new AssetSpacePanel(id,
							breadCrumbModel);
					assetSpacePanel.setSelectedAssetId(asset.getId());
					return assetSpacePanel;
					// //the previous one
					// return (BreadCrumbPanel)
					// BreadCrumbUtils.backBreadCrumbPanel(breadCrumbModel);
				}
			});
		}// onSubmit
	}// AssetForm
/*
	// TODO: remove this iteration thing when the map keys behave. See also AssetViewPanel
	private void updateCustomAttributeValue(SortedMap<AssetTypeCustomAttribute,String> customAttributesMap, AssetTypeCustomAttribute customAttribute,
			String value) {
		if(customAttributesMap != null && !customAttributesMap.isEmpty()){
			for(Entry<AssetTypeCustomAttribute, String> entry : customAttributesMap.entrySet()){
				if(customAttribute.)
			}
		}
	}*/
}