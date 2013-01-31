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

import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.AssetTypeCustomAttributeSearch;
import gr.abiss.calipso.domain.CustomAttribute;
import gr.abiss.calipso.domain.Language;
import gr.abiss.calipso.domain.ValuePair;
import gr.abiss.calipso.domain.i18n.AbstractI18nResourceTranslatable;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.ErrorHighlighter;
import gr.abiss.calipso.wicket.MandatoryPanel;
import gr.abiss.calipso.wicket.customattrs.CustomAttributeOptionsPanel;
import gr.abiss.calipso.wicket.regexp.ValidationPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.PropertyModel;;

/**
 * Renders all fields of a given Custom attribute for editing.
 */
public class AssetCustomAttributeFormPanel extends BasePanel {
	protected static final Logger logger = Logger.getLogger(AssetCustomAttributeFormPanel.class);
	private static final long serialVersionUID = 1L;
	private boolean assetTypeCanBeModified;
	
	
	private WebMarkupContainer optionsPanelContainer;
	private WebMarkupContainer validPanelContainer;
	private Panel validPanel;
	private Panel optionsPanel;

	private Map<String, String> textAreaOptions;
	DropDownChoice<Integer> type;
	/**
	 * @param isMandatory
	 *            make attribute description and type mandatory
	 */

	public AssetCustomAttributeFormPanel(String id,
			final CompoundPropertyModel model, boolean isMandatory,
			boolean assetTypeCanBeModified, 
			Map<String, String> textAreaOptions) {
		super(id);
		this.setOutputMarkupId(true);
		this.assetTypeCanBeModified = assetTypeCanBeModified;
		this.textAreaOptions = textAreaOptions;
		addComponents(model, isMandatory);
	}// AssetCustomAttributeFormPanel

	// //////////////////////////////////////////////////////////////////////////////////////

	
	/**
	 * Renders User Interface Components
	 * 
	 * @param isMandatory
	 *            make attribute description and type mandatory
	 * @param model
	 *            The AssetTypeCustomAttribute model that the methods' components are bind to
	 */
	@SuppressWarnings("serial")
	private void addComponents(final CompoundPropertyModel model,
			final boolean isMandatory) {
		// Mandatory mark. red asterisk (*)
		if (isMandatory) {
			add(new MandatoryPanel("mandatoryPanel")); // Create new Custom
												// Asset (Creation Mode)
			//validPanel = new ValidationPanel("validPanel", model, isMandatory);
		}

		else {
			add(new WebMarkupContainer("mandatoryPanel"));// for search, fields
															// are optional
															// (Search Mode)
			//add(new EmptyPanel("validPanel"));
		}
		
		// name
		if(model.getObject() instanceof AssetTypeCustomAttribute){
			List<Language> languages = getCalipso().getSupportedLanguages();
			CustomAttribute attr = (CustomAttribute) model.getObject();
			if(MapUtils.isEmpty(attr.getNameTranslations())){
				attr.setNameTranslations(getCalipso().getNameTranslations(attr));
				logger.info("Loaded '"+attr.getName()+"' name translations from the DB: "+attr.getNameTranslations());
			}
			else{

				logger.info("Loaded '"+attr.getName()+"' name translations from memory: "+attr.getNameTranslations());
			}
			// TODO: change this to only use the space-supported languages after
			// moving asset type creation to space admin
			//nameTranslations
			add(new ListView("nameTranslations", languages){
				protected void populateItem(ListItem listItem) {
					Language language = (Language) listItem.getModelObject();
					TextField description = new TextField("name");
					if (isMandatory) {
						description.setRequired(true);
						description.add(new ErrorHighlighter());
					}
					listItem.add(description);
					description.setModel(new PropertyModel(model.getObject(), "nameTranslations["+language.getId()+"]"));
					//model.bind(description, "nameTranslations["+language.getId()+"]");
					// form label for name
					description.setLabel(new ResourceModel("language."+language.getId()));
					listItem.add(new SimpleFormComponentLabel("languageLabel", description));
				}
			});
		}/*
		else{
			WebMarkupContainer container = new WebMarkupContainer("nameTranslations");
			TextField description = new TextField("name");
			description.setRequired(false);
			model.bind(description, "name");
			container.add(description);
			container.add(new Label("languageLabel", "").setVisible(false));
			add(container);
		}*/


		// form type
		// -------------------------------------------------------------------------------
		// Ervis
		// attributeTypeList is a an object that contains a list of
		// attributeTypes
		// and a Map of pairs (AttributeTypes,AttributeTypes)
		//final AttributeTypes attributeTypesList = new AttributeTypes();
		
		type = new DropDownChoice<Integer>("formType", new ArrayList<Integer>(CustomAttribute.FORM_TYPES), new IChoiceRenderer<Integer>() {
			public Object getDisplayValue(Integer o) {
				return localize("asset.attributeType_" + o.toString());
				}

			public String getIdValue(Integer object, int index) {
				return index + "";
				}}) {
			private static final long serialVersionUID = 1L;
			/**
			 * @see org.apache.wicket.Component#initModel()
			 */
			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}
			
//			@Override
//			protected void onSelectionChanged(Integer newSelection) {
//				if (isMandatory) {
//					AssetCustomAttributeFormPanel.this.remove(validPanel);
//					if (newSelection.equals(AssetTypeCustomAttribute.FORM_TYPE_MULTISELECT)
//							|| newSelection.equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)
//							|| newSelection.equals(AssetTypeCustomAttribute.FORM_TYPE_OPTIONS_TREE)) {
//						optionsPanel = new CustomAttributeOptionsPanel("optionTranslationsPanel", (AssetTypeCustomAttribute) model.getObject(), getCalipso().getSupportedLanguages(), textAreaOptions);
//						AssetCustomAttributeFormPanel.this.add(validPanel);
//					}
//					else if (newSelection.equals(AssetTypeCustomAttribute.FORM_TYPE_TEXT)) {
//							validPanel = new ValidationPanel("validPanel", model, isMandatory);
//							AssetCustomAttributeFormPanel.this.add(validPanel);
//						}
//						else{
//						AssetCustomAttributeFormPanel.this.add(new EmptyPanel("validPanel"));
//					}
//				}
//				setModelObject(newSelection);
//			}

	

//			/**
//			 * @see
//			 * org.apache.wicket.markup.html.form.AbstractSingleSelectChoice
//			 * #getDefaultChoice(java.lang.Object)
//			 */
//			@Override
//			protected CharSequence getDefaultChoice(Object selected) {
//				// TODO Auto-generated method stub
//				return super
//						.getDefaultChoice(AssetTypeCustomAttribute.FORM_TYPE_TEXT);
//			}
		};
		type.setOutputMarkupId(true);
		type.add(new AjaxFormComponentUpdatingBehavior("onchange") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				logger.info("onUpdate");
				//AssetCustomAttributeFormPanel.this.remove(validPanel);
				Integer selected = type.getModelObject();
				
				if (selected.equals(AssetTypeCustomAttribute.FORM_TYPE_MULTISELECT)
						|| selected.equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)
						|| selected.equals(AssetTypeCustomAttribute.FORM_TYPE_OPTIONS_TREE)) {
					optionsPanel.setVisible(true);
					validPanel.setVisible(false);
				}
				else if (selected.equals(AssetTypeCustomAttribute.FORM_TYPE_TEXT)) {
					optionsPanel.setVisible(false);
					validPanel.setVisible(true);
				}
				else{
					optionsPanel.setVisible(false);
					validPanel.setVisible(false);
				}
				target.add(optionsPanelContainer);
				target.add(validPanelContainer);				
			}
		});
		
		type.setNullValid(false);
		type.setEnabled(this.assetTypeCanBeModified);
		type.setOutputMarkupId(true);
		add(type);
		type.setModel(new PropertyModel(model.getObject(), "formType"));
		// form label for form type
		type.setLabel(new ResourceModel("asset.customAttributes.type"));
		
		add(new SimpleFormComponentLabel("formTypeLabel", type));
		if (isMandatory) {
			type.setRequired(true);
			type.add(new ErrorHighlighter());
		}
		
		Integer selected = type.getModelObject() != null ? type.getModelObject()  : AssetTypeCustomAttribute.FORM_TYPE_SELECT;
		//if(selected == null)
		logger.debug("selected: "+selected);
		addValidationPanel(model, isMandatory, selected);
		addOptionsPanel(model, selected);
		
		Fragment mandatoryFragment;
		Fragment activeFragment;
		TextField mappingKey = new TextField("mappingKey");//, new PropertyModel(model.getObject(), "mappingKey"));
		add(mappingKey);

		if (isMandatory) {// Edit Mode

			mandatoryFragment = new Fragment("mandatoryField",
					"mandatoryEditMode", this);
			activeFragment = new Fragment("activeField", "activeEditMode", this);

			// Mandatory checkbox
			// ------------------------------------------------------------------
			CheckBox mandatory = new CheckBox("mandatory");
			mandatoryFragment.add(mandatory);
			mandatory.setModel(model);
			// form label for mandatory
			mandatory.setLabel(new ResourceModel(
					"asset.customAttributes.mandatory"));
			add(new SimpleFormComponentLabel("mandatoryLabel", mandatory));

			// Active checkbox
			// ---------------------------------------------------------------------
			CheckBox active = new CheckBox("active");
			activeFragment.add(active);
			active.setModel(model);
			// form label for active
			active.setLabel(new ResourceModel("asset.customAttributes.active"));
			add(new SimpleFormComponentLabel("activeLabel", active));
			

		} else {// Search Mode
			List<ValuePair> searchModi = new ArrayList<ValuePair>();
			searchModi.add(new ValuePair(localize("asset.customAttribute.yes"), "1"));
			searchModi.add(new ValuePair(localize("asset.customAttribute.no"), "0"));

			// Mandatory
			// ---------------------------------------------------------------------------
			mandatoryFragment = new Fragment("mandatoryField",
					"mandatorySearchMode", this);
			final DropDownChoice mandatoryChoice = new DropDownChoice(
					"mandatory", searchModi, new IChoiceRenderer() {
						public Object getDisplayValue(Object object) {
							return ((ValuePair) object).getName();
						}

						public String getIdValue(Object object, int index) {
							return String.valueOf(((ValuePair) object)
									.getValue());
						}
					});

			mandatoryChoice.setNullValid(true);

			// form label for mandatory
			mandatoryChoice.setLabel(new ResourceModel(
					"asset.customAttributes.mandatory"));
			add(new SimpleFormComponentLabel("mandatoryLabel", mandatoryChoice));

			mandatoryFragment.add(mandatoryChoice);

			// Active
			// ------------------------------------------------------------------------------
			activeFragment = new Fragment("activeField", "activeSearchMode",
					this);
			final DropDownChoice activeChoice = new DropDownChoice("active",
					searchModi, new IChoiceRenderer() {
						public Object getDisplayValue(Object object) {
							return ((ValuePair) object).getName();
						}

						public String getIdValue(Object object, int index) {
							return index+"";
						}
					});

			activeChoice.setNullValid(true);

			// form label for active
			activeChoice.setLabel(new ResourceModel(
					"asset.customAttributes.active"));
			add(new SimpleFormComponentLabel("activeLabel", activeChoice));

			activeFragment.add(activeChoice);
		}
		add(mandatoryFragment);
		add(activeFragment);

	}// addComponents

	private void addValidationPanel(final CompoundPropertyModel model,
			final boolean isMandatory, Integer selected) {
		validPanel = new ValidationPanel("validPanel", model, isMandatory);
		validPanel.setOutputMarkupId(true);
		if(AssetTypeCustomAttribute.FORM_TYPE_TEXT.equals(selected)) {
			logger.debug("hiding validation panel");
			validPanel.setVisible(true);
		}
		else{
			logger.debug("showing validation panel");
			validPanel.setVisible(false);
		}
		validPanelContainer = new WebMarkupContainer("validPanelContainer");
		validPanelContainer.setOutputMarkupPlaceholderTag(true);
		validPanelContainer.add(validPanel);
		add(validPanelContainer);
	}

	private void addOptionsPanel(final CompoundPropertyModel model,
			Integer selected) {
		logger.info("selected: "+selected);
		Object modelObject = model.getObject();
		if(modelObject instanceof AssetTypeCustomAttributeSearch){
			modelObject = ((AssetTypeCustomAttributeSearch) modelObject).getSearchObject();
		}
		optionsPanel = new CustomAttributeOptionsPanel("optionTranslationsPanel", (AssetTypeCustomAttribute) modelObject, getCalipso().getSupportedLanguages(), textAreaOptions);
		optionsPanel.setOutputMarkupId(true);
		//optionsPanel.setOutputMarkupId(true);
		if(selected.equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)
			|| selected.equals(AssetTypeCustomAttribute.FORM_TYPE_MULTISELECT)
			|| selected.equals(AssetTypeCustomAttribute.FORM_TYPE_OPTIONS_TREE)) {
			optionsPanel.setVisible(true);
		}
		else{
			optionsPanel.setVisible(false);
		}
		optionsPanelContainer = new WebMarkupContainer("optionsPanelContainer");
		optionsPanelContainer.setOutputMarkupPlaceholderTag(true);
		optionsPanelContainer.add(optionsPanel);
		add(optionsPanelContainer);
	}

}// AssetCustomAttributeFormPanel