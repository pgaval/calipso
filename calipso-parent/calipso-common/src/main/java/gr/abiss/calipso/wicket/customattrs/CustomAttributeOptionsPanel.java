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

package gr.abiss.calipso.wicket.customattrs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.springframework.util.CollectionUtils;

import gr.abiss.calipso.domain.CustomAttribute;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.domain.I18nStringIdentifier;
import gr.abiss.calipso.domain.I18nStringResource;
import gr.abiss.calipso.domain.Language;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.ErrorHighlighter;

/**
 * Renders a textarea for options entry per language. Stores values in a
 * Map<String languages, String optionsText> in the provided model using expressions 
 * like "textAreaOptions[en]".
 * 
 * @author manos
 * 
 */
public class CustomAttributeOptionsPanel extends BasePanel {

	//private Map<String, String> textAreaOptions;
	//private CompoundPropertyModel model;

	public CustomAttributeOptionsPanel(String id, CustomAttribute customAttribute,
			List<Language> languages, Map<String, String> textAreaOptions) {
		super(id);
		//logger.info("CustomAttributeOptionsPanel constructor, customAttribute: "+customAttribute);
		// use the provided language list or the global if null
		if (CollectionUtils.isEmpty(languages)) {
			languages = this.getCalipso().getSupportedLanguages();
		}
		
		// initialize model
		//this.model = new CompoundPropertyModel(textAreaOptions);

		// preload existing lookup values
		if(CollectionUtils.isEmpty(textAreaOptions)){
			CustomAttributeUtils.preloadExistingLookupValues(getCalipso(), customAttribute, textAreaOptions);
		}
		
		// good to go
		addComponents(customAttribute, languages, textAreaOptions);
	}



	@SuppressWarnings("unchecked")
	private void addComponents( CustomAttribute customAttribute, List<Language> languages, final Map<String, String> textAreaOptions) {
		// default value
		TextField<String> textField = new TextField<String>("defaultOption", new PropertyModel(customAttribute, "defaultStringValue"));
		textField.setLabel(new ResourceModel("space_field_form.defaultOption"));
		add(textField);
		add(new SimpleFormComponentLabel("defaultOptionLabel", textField));
		// TODO: switch this to tabs per language
		// List optionTranslationTabs = new ArrayList();
		// get the field's custom attribute or create one if needed

		add(new ListView("optionTranslations", languages) {
			protected void populateItem(ListItem listItem) {
				// logger.debug("Building option translations for : "+fieldInternalName);
				Language language = (Language) listItem.getModelObject();
				TextArea optionsTextArea = new TextArea(
						"optionTranslationsList", new PropertyModel(textAreaOptions, "["+language.getId()+"]"));
				//logger.info("optionsTextArea 1 model: "+optionsTextArea.getModel()+", object: "+ optionsTextArea.getModelObject());
				optionsTextArea.setType(String.class);
				// name translations are required.
				optionsTextArea.setRequired(true);
				optionsTextArea.add(new ErrorHighlighter());
				listItem.add(optionsTextArea);
				/*
				optionsTextArea = (TextArea) CustomAttributeOptionsPanel.this.model.bind(
						optionsTextArea,
						new StringBuffer("textAreaOptions[")
								.append(language.getId()).append("]")
								.toString());
				 */
				//logger.info("optionsTextArea 2 model: "+optionsTextArea.getModel()+", object: "+ optionsTextArea.getModelObject());
				// form label for name
				optionsTextArea.setLabel(new ResourceModel("language."
						+ language.getId()));
				listItem.add(new SimpleFormComponentLabel("languageLabel",
						optionsTextArea));
			}
			
		});
	}

}
