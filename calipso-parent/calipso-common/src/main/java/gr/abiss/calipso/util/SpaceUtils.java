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

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.CustomAttribute;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.I18nStringResource;
import gr.abiss.calipso.domain.ItemFieldCustomAttribute;
import gr.abiss.calipso.domain.ItemRenderingTemplate;
import gr.abiss.calipso.domain.Language;
import gr.abiss.calipso.domain.RenderingTemplate;
import gr.abiss.calipso.domain.RoleSpaceStdField;
import gr.abiss.calipso.domain.RoleType;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.SpaceRole;
import gr.abiss.calipso.wicket.customattrs.CustomAttributeUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;

public class SpaceUtils {
	
	private static final Logger logger = Logger.getLogger(SpaceUtils.class);

	public static void copySpace(CalipsoService calipso, Space spaceFrom, Space space){
		// clear any old roles/tmpls
		calipso.bulkUpdateDeleteRolesAndTemplatesForSpace(space);

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
		List<ItemRenderingTemplate> templatesFrom = calipso.getItemRenderingTemplates(spaceFrom);
		logger.info("Adding templates to new space");
		Map<String, ItemRenderingTemplate> newTemplates = new HashMap<String, ItemRenderingTemplate>();
		logger.info("Adding templates to new space: "+templatesFrom);
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
				calipso.storeItemRenderingTemplate(templateTo);
			}
			logger.info("Added templates to new space: "+space.getItemRenderingTemplates());
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
					logger.info("Added templates to new role ("+roleTo.getDescription()+"): "+roleTemplatesTo);
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
			attrTo.setMappingKey(attrFrom.getMappingKey());
			// copy lookup values
			//List<CustomAttributeLookupValue> valuesFrom = calipso.findAllLookupValuesByCustomAttribute(attrFrom);
			Map<String, String> nameTranslations = CustomAttributeUtils.preloadExistingLookupValues(calipso, attrFrom);
			CustomAttributeUtils.parseOptionsIntoAttribute(nameTranslations, attrTo, space.getSupportedLanguages());
		}
		else{
			logger.warn("Could not copy attrFrom: "+attrFrom+", to: "+attrTo);
		}
		
	}
	
	public static void copy(CalipsoService calipso, Space space,
			Field field, AssetTypeCustomAttribute attrTo) {
		if(field != null && field.getCustomAttribute() != null && attrTo != null){
			ItemFieldCustomAttribute attrFrom = field.getCustomAttribute();
			Field.Name fieldName = field.getName();
			if(field.getFieldType().equals(Field.FIELD_TYPE_DROPDOWN_HIERARCHICAL)){
				attrTo.setFormType(CustomAttribute.FORM_TYPE_OPTIONS_TREE);
			}
			else if(fieldName.isOptionsType()){
				attrTo.setFormType(CustomAttribute.FORM_TYPE_SELECT);
			}
			else if(fieldName.isDecimalNumber()){
				attrTo.setFormType(CustomAttribute.DATA_TYPE_DOUBLE);
			}
			else if(fieldName.isDate()){
				attrTo.setFormType(CustomAttribute.FORM_TYPE_DATE);
			}
			else if(fieldName.isOrganization()){
				attrTo.setFormType(CustomAttribute.FORM_TYPE_ORGANIZATION);
			}
			else if(fieldName.isUser()){
				attrTo.setFormType(CustomAttribute.FORM_TYPE_USER);
			}
			else if(fieldName.isCountry()){
				attrTo.setFormType(CustomAttribute.FORM_TYPE_COUNTRY);
			}
			else if(field.getName().getText().equals(Field.FIELD_TYPE_SIMPLE_ATTACHEMENT)){
				attrTo.setFormType(CustomAttribute.FORM_TYPE_SIMPLE_ATTACHMENT);
			}
			else if(fieldName.isFile()){
				attrTo.setFormType(CustomAttribute.FORM_TYPE_FILE);
			}
			else if(field.isMultivalue()){
				attrTo.setFormType(CustomAttribute.FORM_TYPE_TABULAR);
			}
			else /* if(fieldName.isFreeText())*/{
				attrTo.setFormType(CustomAttribute.FORM_TYPE_TEXTAREA);
			}
			
			
			
			attrTo.setDataType(attrFrom.getDataType());
			// copy custom attribute name translations
			List<I18nStringResource> translations =  calipso.getNameTranslations(attrFrom);
			attrTo.setNameTranslations(translations);
			attrTo.setDefaultStringValue(attrFrom.getDefaultStringValue());
			attrTo.setValidationExpression(attrFrom.getValidationExpression());
			attrTo.setMappingKey(attrFrom.getMappingKey());
			// copy lookup values
			//List<CustomAttributeLookupValue> valuesFrom = calipso.findAllLookupValuesByCustomAttribute(attrFrom);
			Map<String, String> nameTranslations = CustomAttributeUtils.preloadExistingLookupValues(calipso, attrFrom);

			logger.warn("nameTranslations: "+nameTranslations);
			CustomAttributeUtils.parseOptionsIntoAttribute(nameTranslations, attrTo, space.getSupportedLanguages());
		}
		else{
			logger.warn("Could not copy attribute from field: "+field+", to: "+attrTo);
		}
		
	}
	
	/**
	 * Load persisted SpaceRole instances for the given Space, add or remove
	 * Regular, Guest and Anonymous roles as needed according to previous
	 * screens
	 */
	public static void initSpaceSpaceRoles(CalipsoService calipso,  Space space) {
//		logger.info("initSpaceSpaceRoles1, space.getSpaceRoles():"
//				+ space.getSpaceRoles());
		// load persisted roles for space if any
		//if (CollectionUtils.isEmpty(space.getSpaceRoles())) {
			
		//}
		Set<RoleType> roleTypes = new HashSet<RoleType>();
		// if a new space, add the regular user role
		if (CollectionUtils.isEmpty(space.getSpaceRoles())) {
			// Set up a pre-defined regular user role work flow
			SpaceRole regularUserRole = new SpaceRole(space,
					RoleType.REGULAR_USER.getDescription(),
					RoleType.REGULAR_USER);
			space.add(regularUserRole);
			space.getMetadata().initRegularUserRole(
					regularUserRole.getRoleCode());

			logger.info("This is a new Space, added a regular user role with roleCode:"+regularUserRole.getRoleCode());

		} else {
			logger.info("This is not a new Space, update guest/anonymous according to previous page");
			boolean addTypeInRoleTypes;
			Set<SpaceRole> rolesToRemove = new HashSet<SpaceRole>();
			for (SpaceRole spaceRole : space.getSpaceRoles()) {
				RoleType roleType = spaceRole.getRoleType();

				addTypeInRoleTypes = true;
				// remove if the user went back and changed "Allow guest" or
				// "Allow anonymous" to false
				logger.info("Going over role with type: "+roleType);
				if (!space.isGuestAllowed() && roleType.equals(RoleType.GUEST)) {
					logger.info("Removing GUEST Role with roleCode: "+spaceRole.getRoleCode());
					// space.remove(spaceRole);
					rolesToRemove.add(spaceRole);
					// TODO: usefull?
					space.getMetadata().removeRole(spaceRole.getRoleCode());
					addTypeInRoleTypes = false;
				}
				if (!space.isAnonymousAllowed()
						&& roleType.equals(RoleType.ANONYMOUS)) {
					logger.info("Removing ANONYMOUS Role with roleCode: "+spaceRole.getRoleCode());
					// space.remove(spaceRole);
					rolesToRemove.add(spaceRole);
					// TODO: usefull?
					space.getMetadata().removeRole(spaceRole.getRoleCode());
					addTypeInRoleTypes = false;
				}
				// otherwise let the code bellow know this role exists already
				if (addTypeInRoleTypes) {
					roleTypes.add(roleType);
					logger.info("Added in RoleTypes:"+roleType);
				}
			}
			// Remove SpaceRoles outside the for loop to avoid
			// ConcurrentModificationException
			space.removeSpaceRoles(rolesToRemove);
		}

		logger.info("roleTypes: "+roleTypes);

		// Set up guest role and work flow if needed and missing
		if (space.isGuestAllowed()) {
			if (!roleTypes.contains(RoleType.GUEST)) {
				SpaceRole guestUserRole = new SpaceRole(space,
						RoleType.GUEST.getDescription(), RoleType.GUEST);
				logger.info("Adding GUEST Role with roleCode: "+guestUserRole.getRoleCode());
				space.add(guestUserRole);
				space.getMetadata().initGuestUserRole(
						guestUserRole.getRoleCode());
			} else {
				logger.info("There is already a GUEST Role");
			}
		}
		// Set up anonymous role and work flow if needed and missing
		if (space.isAnonymousAllowed()) {
			if (!roleTypes.contains(RoleType.ANONYMOUS)) {
				SpaceRole anonymousUserRole = new SpaceRole(space,
						RoleType.ANONYMOUS.getDescription(), RoleType.ANONYMOUS);
				logger.info("Adding ANONYMOUS Role with roleCode: "+anonymousUserRole.getRoleCode());
				space.add(anonymousUserRole);
				space.getMetadata().initGuestUserRole(
						anonymousUserRole.getRoleCode());
			}
		}

		// setup admin if missing
		// /Set up "Space Administrator" role
		if (!roleTypes.contains(RoleType.SPACE_ADMINISTRATOR)) {
			SpaceRole spaceAdministrator = new SpaceRole(space,
					RoleType.SPACE_ADMINISTRATOR.getDescription(),
					RoleType.SPACE_ADMINISTRATOR);
			space.add(spaceAdministrator);
			space.getMetadata().initRegularUserRole(
					spaceAdministrator.getRoleCode());
			logger.info("Adding SPACE_ADMINISTRATOR Role with roleCode: "+spaceAdministrator.getRoleCode());
		} else {
			logger.info("Skipped adding SPACE_ADMINISTRATOR Role as it already exists");
		}

	}

}
