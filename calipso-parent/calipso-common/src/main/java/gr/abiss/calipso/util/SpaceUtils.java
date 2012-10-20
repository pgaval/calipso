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

package gr.abiss.calipso.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.jfree.util.Log;

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.I18nStringResource;
import gr.abiss.calipso.domain.ItemFieldCustomAttribute;
import gr.abiss.calipso.domain.ItemRenderingTemplate;
import gr.abiss.calipso.domain.Language;
import gr.abiss.calipso.domain.RenderingTemplate;
import gr.abiss.calipso.domain.RoleSpaceStdField;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.SpaceRole;
import gr.abiss.calipso.wicket.customattrs.CustomAttributeUtils;

public class SpaceUtils {
	
	private static final Logger logger = Logger.getLogger(SpaceUtils.class);

	public static void copySpace(CalipsoService calipso, Space spaceFrom, Space space){
		// start with metadata
		String xml = spaceFrom.getMetadata().getXmlString();
		// roles
		List<SpaceRole> rolesFrom = calipso.findSpaceRolesForSpace(spaceFrom);
		Map<String,SpaceRole> rolesTo = new HashMap<String,SpaceRole>();
		if(CollectionUtils.isNotEmpty(rolesFrom)){
			for(SpaceRole roleFrom : rolesFrom){
				String roleDescription = roleFrom.getDescription();
				SpaceRole roleTo = new SpaceRole(space, roleDescription, roleFrom.getRoleType());
				rolesTo.put(roleDescription, roleTo);
				xml = xml.replaceAll(Pattern.quote(roleFrom.getRoleCode()), roleTo.getRoleCode());
				// useful?
				for (RoleSpaceStdField roleSpaceStdField : calipso.findSpaceFieldsBySpaceRole(roleFrom)) {
					roleTo.add(new RoleSpaceStdField(null, roleTo, roleSpaceStdField.getStdField(), roleSpaceStdField.getFieldMask()));
				}
				space.add(roleTo);
			}
		}
		
		// add fields and translations for custom fields
		List<Field> fieldsToCopy = spaceFrom.getMetadata().getFieldList();
		//List<ItemFieldCustomAttribute> newFieldCustomAttributes = new LinkedList<ItemFieldCustomAttribute>();

		space.getMetadata().setXmlString(xml);
		if(CollectionUtils.isNotEmpty(fieldsToCopy)){
			for(Field fieldFrom : fieldsToCopy){
				
				// copy custom attribute
				ItemFieldCustomAttribute attrFrom = calipso.loadItemCustomAttribute(spaceFrom, fieldFrom.getName().getText());
				if(attrFrom != null){
					ItemFieldCustomAttribute attrTo = new ItemFieldCustomAttribute();
					copy(calipso, space, attrFrom, attrTo);
					Field fieldTo = space.getMetadata().getField(fieldFrom.getName().getText());
					fieldTo.setCustomAttribute(attrTo);
					
					String fieldKey = fieldFrom.getName().getText();
					List<I18nStringResource> resources = calipso.getPropertyTranslations(fieldKey, spaceFrom);
					spaceFrom.setPropertyTranslations(fieldKey, resources);
					Map<String,String> labelsToCopy = spaceFrom.getPropertyTranslations(fieldKey);
					if(MapUtils.isNotEmpty(labelsToCopy)){
						Map<String,String> newlabels = new HashMap<String,String>(); 
						for(String locale : labelsToCopy.keySet()){
							if(locale.equals(calipso.getDefaultLocale()) 
									|| space.getSupportedLanguages().contains(new Language(locale))){
								newlabels.put(locale, labelsToCopy.get(locale));
							}
						}
						space.setPropertyTranslations(fieldKey, newlabels);
					}
					//spaceFrom.setTranslations(null);
				}
			}
			// Map<Name, Field> fieldsToTranslate = space.getMetadata().getFields();
		}
		
		// copy templates
		List<ItemRenderingTemplate> templatesFrom = spaceFrom.getItemRenderingTemplates();
		Map<String, ItemRenderingTemplate> newTemplates = new HashMap<String, ItemRenderingTemplate>();
		if(CollectionUtils.isNotEmpty(templatesFrom)){
			int templateNameSuffix = 0;
			for(ItemRenderingTemplate templateFrom : templatesFrom){
				ItemRenderingTemplate templateTo = new ItemRenderingTemplate(templateFrom);
				// keep a reference to add to space roles
				newTemplates.put(templateTo.getDescription(), templateTo);
				space.add(templateTo);
				// change the unsaved template name 
				templateNameSuffix = templateNameSuffix+1;
				templateTo.setDescription(space.getPrefixCode()+"-template-"+templateNameSuffix);
			}
			//logger.info("Added templates to new space: "+space.getItemRenderingTemplates());
			// iterate roles from the original space to add the item rendering templates
			// to the new space roles
			for(SpaceRole roleFrom : rolesFrom){
				SpaceRole roleTo = rolesTo.get(roleFrom.getDescription());
				Map<String, RenderingTemplate> roleTemplatesFrom = roleFrom.getItemRenderingTemplates();
				if(MapUtils.isNotEmpty(roleTemplatesFrom)){
					Map<String, RenderingTemplate> roleTemplatesTo = new HashMap<String, RenderingTemplate>();
					for(String key : roleTemplatesFrom.keySet()){
						String templateDescription = ((ItemRenderingTemplate) roleTemplatesFrom.get(key)).getDescription();
						RenderingTemplate templateTo = newTemplates.get(templateDescription);
						roleTemplatesTo.put(key, templateTo);
					}
					roleTo.setItemRenderingTemplates(roleTemplatesTo);
				}
			
			}
			
		}

	}

	private static void copy(CalipsoService calipso, Space space,
			ItemFieldCustomAttribute attrFrom, ItemFieldCustomAttribute attrTo) {
		if(attrFrom != null && attrTo != null){
			attrTo.setFieldName(attrFrom.getFieldName());
			attrTo.setFormType(attrFrom.getFormType());
			attrTo.setSpace(space);
			// copy custom attribute name translations
			List<I18nStringResource> translations =  calipso.getNameTranslations(attrFrom);
			attrTo.setNameTranslations(translations);
			attrTo.setHtmlDescription(attrFrom.getHtmlDescription());
			attrTo.setDefaultStringValue(attrFrom.getDefaultStringValue());
			attrTo.setShowInSearchResults(attrFrom.isShowInSearchResults());
			attrTo.setValidationExpression(attrFrom.getValidationExpression());
			// copy lookup values
			//List<CustomAttributeLookupValue> valuesFrom = calipso.findAllLookupValuesByCustomAttribute(attrFrom);
			Map<String, String> nameTranslations = CustomAttributeUtils.preloadExistingLookupValues(calipso, attrFrom);
			CustomAttributeUtils.parseOptionsIntoAttribute(nameTranslations, attrTo, space.getSupportedLanguages());
		}
		else{
			logger.warn("Could not copy attrFrom: "+attrFrom+", to: "+attrTo);
		}
		
	}

}
