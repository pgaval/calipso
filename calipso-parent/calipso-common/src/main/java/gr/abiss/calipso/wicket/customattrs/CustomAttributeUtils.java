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
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.domain.I18nStringIdentifier;
import gr.abiss.calipso.domain.I18nStringResource;
import gr.abiss.calipso.domain.CustomAttribute;
import gr.abiss.calipso.domain.Language;

public class CustomAttributeUtils {
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(CustomAttributeUtils.class);

	public static void preloadExistingLookupValues(
			CalipsoService calipso, 
			final CustomAttribute customAttribute,
			final Map<String, String> textAreaOptions) {
		//logger.info("preloadExistingLookupValues, customAttribute: "+customAttribute);
		for(Language language : calipso.getSupportedLanguages()){
			preloadExistingLookupValues(calipso, customAttribute, textAreaOptions,
					language);
		}
	}
	
	public static Map<String, String> preloadExistingLookupValues(
			CalipsoService calipso, 
			CustomAttribute customAttribute){

		Map<String, String> textAreaOptions = new HashMap<String, String>();
		
		//logger.info("preloadExistingLookupValues, customAttribute: "+customAttribute);
		for(Language language : calipso.getSupportedLanguages()){
			preloadExistingLookupValues(calipso, customAttribute, textAreaOptions,
					language);
		}
		return textAreaOptions;
	}
	
	private static void preloadExistingLookupValues(
			CalipsoService calipso, 
			final CustomAttribute customAttribute,
			final Map<String, String> textAreaOptions, Language language) {
		StringBuffer languageOptions = new StringBuffer();
		
		List<CustomAttributeLookupValue> existingLookupValues = calipso.findLookupValuesByCustomAttribute(customAttribute);
		if (existingLookupValues != null
				&& !existingLookupValues.isEmpty()) {
			//logger.info("field.getCustomAttribute().getAllowedLookupValues("+existingLookupValues.size()+"): "+customAttribute.getAllowedLookupValues());
			for (CustomAttributeLookupValue value : existingLookupValues) {
				if(value.isActive()){

					CustomAttributeUtils.buildStringOptionTranslations(calipso, language,
							languageOptions, value);
				
				}
			}
			textAreaOptions.put(language.getId(),
					languageOptions.toString());
			//logger.info("Adding "+language.getId()+" options to textAreaOptions, size: "+textAreaOptions.get(language.getId()).length());
		}
	}
	
	public static void buildStringOptionTranslations(
			CalipsoService calipso,
			Language language, StringBuffer languageOptions,
			CustomAttributeLookupValue value) {
		// TODO:load in single query
		//logger.info("buildStringOptionTranslations lookup value: "+ value.getId()+", language: "+language);
		I18nStringResource res = calipso.loadI18nStringResource(new I18nStringIdentifier(value.getNameTranslationResourceKey(), language.getId()));
		if(res != null && value.isActive()){
			// new line if not first
			if(languageOptions.length() > 0){
				languageOptions.append("\n");
			}
			// indent to resemble tree level
			for(int level = 1; level < value.getLevel();level++){
				languageOptions.append("\t");
			}
			languageOptions.append(res.getValue());
			if(value.getChildren() != null){
				for(CustomAttributeLookupValue child : value.getChildren()){
					buildStringOptionTranslations(calipso, language, languageOptions, child);
				}
			}
		}
	}
	
	/**
	 * Parses the given user intput to create the custom attribute lookup values (options) and their translations, 
	 * then add them to the given CustomAttribute.
	 * @param optionsTextIput
	 * @param attribute
	 * @param languages
	 */
	public static void parseOptionsIntoAttribute(Map<String,String> optionsTextIput, CustomAttribute attribute, List<Language> languages) {
		//logger.info("parseOptionsIntoAttribute, attribute: "+attribute);
		// add options
		attribute.setPersistedVersion(attribute.getVersion());
		attribute.setVersion(attribute.getVersion()+1);
		attribute.removeAllLookupValues();
		List<CustomAttributeLookupValue> optionsList = new LinkedList<CustomAttributeLookupValue>();
		String languageId = null;
		for(Language language : languages){
			if(languageId == null){
				languageId = language.getId();
			}
			String input = optionsTextIput.get(language.getId());
			//logger.info("textAreaOptions.get(language.getId(): "+input);
			if(StringUtils.isNotBlank(input)){
				Stack<CustomAttributeLookupValue> parents = new Stack<CustomAttributeLookupValue>();
				String[] lines = input.split("\\r?\\n");
				int listIndex = -1;
				for (int j=0; j < lines.length; j++) {

					listIndex++;
					// count whitespace characters to determine level
					String line = lines[j];
					//logger.info("Reading option line: "+line);
					int countLevel = 1;
					int limit = line.length();
					for(int i = 0; i < limit; ++i){
					    if(Character.isWhitespace(line.charAt(i))){
					         ++countLevel;
					    }
					    else{
					    	break;
					    }
					}
					String translatedName = line.substring(countLevel-1).trim();

					//logger.info("translatedName: "+translatedName);
					// build CustomAttributeLookupValue if it doesn't already exist
					CustomAttributeLookupValue lookupValue;
					if(language.getId().equalsIgnoreCase(languageId)){
						//logger.info("creating new lookupValue and adding to list index " + listIndex + " for " + translatedName);
						lookupValue = new CustomAttributeLookupValue();
						optionsList.add(lookupValue);
					}
					else{
						//logger.info("trying to get lookupValue from list index " + listIndex + "for "+translatedName);
						lookupValue = optionsList.get(listIndex);
					}
					lookupValue.setShowOrder(listIndex);
					if(language.getId().equalsIgnoreCase("en")){
						lookupValue.setName(translatedName);
						lookupValue.setValue(translatedName);
					}
					lookupValue.setLevel(countLevel);
					
					
					// fix parent/child
					while((!parents.isEmpty()) 
							&& parents.peek().getLevel() >= lookupValue.getLevel()){
						parents.pop();
					}
					// pile it
					if(lookupValue.getLevel() > 1){
						//logger.info("Adding child "+lookupValue.getName() + "to parent " +parents.peek());
						parents.peek().addChild(lookupValue);
					}
					parents.push(lookupValue);
					// add the translation
					//logger.info("Adding lookup value "+language.getId()+" translation: "+translatedName);
					lookupValue.addNameTranslation(language.getId(), translatedName);
					//logger.info("translations afre now: "+lookupValue.getNameTranslations());
						
				}
			}
			//Set<CustomAttributeLookupValue> lookupValueSet = new HashSet<CustomAttributeLookupValue>();
			//lookupValueSet.addAll(optionsList);

		}
		// update attribute lookup values
		attribute.removeAllLookupValues();
		/*
		List<CustomAttributeLookupValue> toRemove = new LinkedList<CustomAttributeLookupValue>();
		for(CustomAttributeLookupValue value : attribute.getAllowedLookupValues()){
			toRemove.add(value);
		}
		attribute.removeAll(toRemove);*/
		for(CustomAttributeLookupValue value : optionsList){
			//logger.info("Adding lookupValue  with translations: "+value.getNameTranslations());
			attribute.addAllowedLookupValue(value);
		}
		//logger.info("Added lookup values: "+attribute.getAllowedLookupValues());
	}
}
