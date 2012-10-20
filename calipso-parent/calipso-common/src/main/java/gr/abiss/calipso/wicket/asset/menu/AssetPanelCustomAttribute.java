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

package gr.abiss.calipso.wicket.asset.menu;

import gr.abiss.calipso.domain.CustomAttribute;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.ValuePair;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.ErrorHighlighter;
import gr.abiss.calipso.wicket.MandatoryPanel;
import gr.abiss.calipso.wicket.components.renderers.UserChoiceRenderer;
import gr.abiss.calipso.wicket.regexp.ValidationPanel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

/**
 * @author marcello
 * 
 *         Renders all fields of a given Custom attribute.
 * 
 */
public class AssetPanelCustomAttribute extends BasePanel {

	private static final long serialVersionUID = 1L;
	private boolean assetTypeCanBeModified;
	private ValidationPanel validPanel;
	DropDownChoice type;

	/**
	 * @param isMandatory
	 *            make attribute description and type mandatory
	 */

	public AssetPanelCustomAttribute(String id,
			final CompoundPropertyModel model, boolean isMandatory,
			boolean assetTypeCanBeModified) {
		super(id);
		this.assetTypeCanBeModified = assetTypeCanBeModified;
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
	private void addComponents(final CompoundPropertyModel model,
			final boolean isMandatory) {

		// Mandatory mark. red asterisk (*)
		if (isMandatory) {
			add(new MandatoryPanel("mandatoryPanel")); // Create new Custom
												// Asset (Creation Mode)
			validPanel = new ValidationPanel("validPanel", model, isMandatory);
		}

		else {
			add(new WebMarkupContainer("mandatoryPanel"));// for search, fields
															// are optional
															// (Search Mode)
			add(new EmptyPanel("validPanel"));
		}
		// name
		// ------------------------------------------------------------------------------------

		TextField description = new TextField("name");
		if (isMandatory) {
			description.setRequired(true);
			description.add(new ErrorHighlighter());
		}
		add(description);
		description.setModel(model);
		// form label for name
		description.setLabel(new ResourceModel(
				"asset.customAttributes.description"));
		add(new SimpleFormComponentLabel("nameLabel", description));

		// form type
		// -------------------------------------------------------------------------------
		// Ervis
		// attributeTypeList is a an object that contains a list of
		// attributeTypes
		// and a Map of pairs (AttributeTypes,AttributeTypes)
		//final AttributeTypes attributeTypesList = new AttributeTypes();
		
		type = new DropDownChoice("formType", new ArrayList<Integer>(CustomAttribute.FORM_TYPES), new IChoiceRenderer() {
			
			public String getIdValue(Object o, int i) {
				return i + "";
			}

			public Object getDisplayValue(Object o) {

				return localize("asset.attributeType_" + o.toString());
			}
		}) {
			private static final long serialVersionUID = 1L;
			/**
			 * @see org.apache.wicket.Component#initModel()
			 */
			@Override
			protected boolean wantOnSelectionChangedNotifications() {
				return true;
			}

			@Override
			protected void onSelectionChanged(Object newSelection) {
				if (isMandatory) {
					AssetPanelCustomAttribute.this.remove(validPanel);
					if (newSelection.equals(CustomAttribute.FORM_TYPE_TEXT)) {
						validPanel = new ValidationPanel("validPanel", model, isMandatory);
						AssetPanelCustomAttribute.this.add(validPanel);
					}
					else{
						AssetPanelCustomAttribute.this.add(new EmptyPanel("validPanel"));
					}
				}
				setModelObject(newSelection);
			}

			/**
			 * @see
			 * org.apache.wicket.markup.html.form.AbstractSingleSelectChoice
			 * #getDefaultChoice(java.lang.Object)
			 */
			@Override
			protected CharSequence getDefaultChoice(String selected) {
				return super.getDefaultChoice(CustomAttribute.FORM_TYPE_TEXT.toString());
			}
		};
		
		type.setNullValid(false);
		type.setEnabled(this.assetTypeCanBeModified);
		type.setOutputMarkupId(true);
		add(type);
		type.setModel(model);
		// form label for form type
		type.setLabel(new ResourceModel("asset.customAttributes.type"));
		add(new SimpleFormComponentLabel("formTypeLabel", type));
		if (isMandatory) {
			type.setRequired(true);
			type.add(new ErrorHighlighter());
			add(validPanel);
		}
		
		Fragment mandatoryFragment;
		Fragment activeFragment;

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

}// AssetCustomAttributeFormPanel