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


package gr.abiss.calipso.domain;

import gr.abiss.calipso.domain.i18n.AbstractI18nResourceTranslatable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.jfree.util.Log;

public abstract class CustomAttribute extends AbstractI18nResourceTranslatable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3682208731629039506L;

	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(CustomAttribute.class);
	
	public static final Integer DATA_TYPE_STRING = new Integer(1);
	public static final Integer DATA_TYPE_DATE = new Integer(2);
	public static final Integer DATA_TYPE_INTEGER = new Integer(3);
	public static final Integer DATA_TYPE_LONG = new Integer(4);
	public static final Integer DATA_TYPE_FLOAT = new Integer(5);
	public static final Integer DATA_TYPE_DOUBLE = new Integer(6);
	public static final Integer FORM_TYPE_TEXT = new Integer(1);
	public static final Integer FORM_TYPE_NUMBER = new Integer(2);
	public static final Integer FORM_TYPE_TEXTAREA = new Integer(3);
	public static final Integer FORM_TYPE_SELECT = new Integer(4);
	public static final Integer FORM_TYPE_MULTISELECT = new Integer(5);
	public static final Integer FORM_TYPE_DATE = new Integer(6);
	public static final Integer FORM_TYPE_USER = new Integer(7);
	public static final Integer FORM_TYPE_ORGANIZATION = new Integer(8);
	public static final Integer FORM_TYPE_ASSET = new Integer(10);
	public static final Integer FORM_TYPE_COUNTRY = new Integer(11);
	public static final Integer FORM_TYPE_OPTIONS_TREE = new Integer(12);
	
	public static final List<Integer> FORM_TYPES;
	static {
		ArrayList<Integer> tmp = new ArrayList<Integer>();	
		// adds 1,2,3,5
		tmp.add(CustomAttribute.FORM_TYPE_TEXT); 
		tmp.add(CustomAttribute.FORM_TYPE_NUMBER);
//		tmp.add(CustomAttribute.FORM_TYPE_TEXTAREA);
		tmp.add(CustomAttribute.FORM_TYPE_DATE);
		tmp.add(CustomAttribute.FORM_TYPE_SELECT);
		tmp.add(CustomAttribute.FORM_TYPE_OPTIONS_TREE);
		tmp.add(CustomAttribute.FORM_TYPE_USER);
		tmp.add(CustomAttribute.FORM_TYPE_ORGANIZATION);
		tmp.add(CustomAttribute.FORM_TYPE_COUNTRY);
		tmp.add(CustomAttribute.FORM_TYPE_ASSET);
		FORM_TYPES = Collections.unmodifiableList(tmp);
	}
	protected static final String STRING = "java.lang.String";
	protected static final String DATE = "java.lang.Date";
	protected static final String INTEGER = "java.lang.Integer";
	protected static final String LONG = "java.lang.Long";
	protected static final String FLOAT = "java.lang.Float";
	protected static final String DOUBLE = "java.lang.Double";
	
	
	/* persisted in table */
	protected Long id = null;
	protected String name;
	protected Integer formType;
	private String dataType;
	protected ValidationExpression validationExpression;
	// will be replaced by FK when we add an entity for grouping 
	// attributes
	//protected Short attributeGroupIndex = 0;
	protected Short attributeIndex = 0;
	private String defaultStringValue;
	
	//private String attributeGroupName = "assetType.customAttributes";
	private boolean editable;
	private User userValue;
	private Organization organizationValue;
	private Country countryValue;
	private Asset assetValue;
	private CustomAttributeLookupValue lookupValue;
	protected Set<CustomAttributeLookupValue> allowedLookupValues = new HashSet<CustomAttributeLookupValue>();

	public CustomAttribute() {
		super();
	}

	public CustomAttribute(String name, Integer formType, String dataType,
			ValidationExpression validationExpression) {
		super();
		this.name = name;
		this.formType = formType;
		this.dataType = dataType;
		this.validationExpression = validationExpression;
	}



	/**
	 * @return the lookup value, used for rendering
	 * the selected option
	 */
	public CustomAttributeLookupValue getLookupValue() {
		return this.lookupValue;
	}

	/**
	 * set the selected option
	 */
	public void setLookupValue(CustomAttributeLookupValue lookupValue) {
		this.lookupValue = lookupValue;
	}

	/**
	 * @return the user
	 */
	public User getUserValue() {
		return userValue;
	}

	/**
	 * @param user the user to set
	 */
	public void setUserValue(User user) {
		this.userValue = user;
	}

	/**
	 * @return the user
	 */
	public Country getCountryValue() {
		return countryValue;
	}

	/**
	 * @param user the user to set
	 */
	public void setCountryValue(Country countryValue) {
		this.countryValue = countryValue;
	}

	/**
	 * @return the organization
	 */
	public Organization getOrganizationValue() {
		return organizationValue;
	}

	/**
	 * @param organization the organization to set
	 */
	public void setOrganizationValue(Organization organization) {
		this.organizationValue = organization;
	}

	/**
	 * @return the asset
	 */
	public Asset getAssetValue() {
		return assetValue;
	}

	/**
	 * @param asset the asset to set
	 */
	public void setAssetValue(Asset asset) {
		this.assetValue = asset;
	}

	public void add(CustomAttributeLookupValue lookupValue) {
		if (this.getAllowedLookupValues() ==null){
			this.allowedLookupValues = new LinkedHashSet<CustomAttributeLookupValue>();
		}
		lookupValue.getAttribute().setId(this.id);
		this.getAllowedLookupValues().add(lookupValue);
	
	}

	public void remove(CustomAttributeLookupValue lookupValue) {
		if (this.getAllowedLookupValues() != null){
			this.getAllowedLookupValues().remove(lookupValue);
		}
	}
	
	public void removeAllLookupValues() {
		this.removeAll(this.getAllowedLookupValues());
	}
	
	public void removeAll(Collection<CustomAttributeLookupValue> removables) {
		if (removables != null){
			// avoid concurrent modifications
			List<CustomAttributeLookupValue> list = new LinkedList<CustomAttributeLookupValue>();
			list.addAll(removables);
			for(CustomAttributeLookupValue removable: list){
				this.remove(removable);
			}
		}
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the formType
	 */
	public Integer getFormType() {
		return this.formType;
	}

	/**
	 * @param formType the formType to set
	 */
	public void setFormType(Integer formType) {
		this.formType = formType;
	}

	/**
	 * @return the dataType
	 */
	public String getDataType() {
			
		//		if (this.dataType==null){
	//			if (this.formType.equals(FORM_TYPE_DATE)){
	//				this.dataType = DATE;
	//			}
	//			else if (this.formType.equals(FORM_TYPE_NUMBER)){
	//				this.dataType = FLOAT;
	//			}
	//			else if (this.formType.equals(FORM_TYPE_TEXT) || 
	//					this.formType.equals(FORM_TYPE_MULTISELECT) || 
	//					this.formType.equals(FORM_TYPE_SELECT) || 
	//					this.formType.equals(FORM_TYPE_TEXTAREA)){
	//				this.dataType = STRING;
	//			}
	//		}//if
	
			this.dataType = STRING;
			
			return this.dataType;
		}

	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return this.editable;
	}

	/**
	 * @param editable the editable to set
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getDefaultStringValue() {
		return defaultStringValue;
	}

	public void setDefaultStringValue(String defaultLookupValue) {
		this.defaultStringValue = defaultLookupValue;
	}

	public Short getAttributeIndex() {
		return attributeIndex;
	}

	public void setAttributeIndex(Short attributeIndex) {
		this.attributeIndex = attributeIndex;
	}

	/**
	 * @return the attributeGroupName
	 *
	public String getAttributeGroupName() {
		return attributeGroupName;
	}*/

	/**
	 * @param attributeGroupName the attributeGroupName to set
	 *
	public void setAttributeGroupName(String attributeGroupName) {
		this.attributeGroupName = attributeGroupName;
	}*/
//
//	/**
//	 * @return the attributeGroupIndex
//	 */
//	public Short getAttributeGroupIndex() {
//		return attributeGroupIndex;
//	}
//
//	/**
//	 * @param attributeGroupIndex the attributeGroupIndex to set
//	 */
//	public void setAttributeGroupIndex(Short attributeGroupIndex) {
//		this.attributeGroupIndex = attributeGroupIndex;
//	}
//
//	/**
//	 * @return the attributeIndex
//	 */
//	public Short getAttributeIndex() {
//		return attributeIndex;
//	}
//
//	/**
//	 * @param attributeIndex the attributeIndex to set
//	 */
//	public void setAttributeIndex(Short attributeIndex) {
//		this.attributeIndex = attributeIndex;
//	}

	/**
	 * 
	 * @return the Regexp string used to validate values for this custom attribute
	 */
	public ValidationExpression getValidationExpression() {
		return validationExpression;
	}

	/**
	 * @param validationExpression the Regexp string to use for validating values for this custom attribute
	 */
	public void setValidationExpression(ValidationExpression validationExpression) {
		this.validationExpression = validationExpression;
	}

	public boolean addAllowedLookupValue(CustomAttributeLookupValue value) {
		if(this.getAllowedLookupValues() == null){
			this.allowedLookupValues = new HashSet<CustomAttributeLookupValue>();
		}
		value.setAttribute(this);
		return this.getAllowedLookupValues().add(value);
	}

	/**
	 * @param allowedLookupValues the allowedValues to set
	 */
	public void setAllowedLookupValues(Set<CustomAttributeLookupValue> allowedValues) {
		this.allowedLookupValues = allowedValues;
	}

	/**
	 * @see gr.abiss.calipso.domain.i18n.AbstractI18nResourceTranslatable#getNameTranslationResourceKey()
	 */
	public String getPropertyTranslationResourceKey(String name) {
		return new StringBuffer(getShortName(CustomAttribute.class))
		.append('.')
		.append(this.getId())
		.append('.')
		.append(name)
		.toString();
	}
	

	/**
	 * @return the allowedLookupValues
	 */
	public Set<CustomAttributeLookupValue> getAllowedLookupValues() {
		return this.allowedLookupValues;
	}

	

	@Override
	public int hashCode() {
		int hashCode = new HashCodeBuilder(43, 5)
			.append(this.getName())
	        .toHashCode();

		//logger.info("Hashcode for "+this.getName() +": "+ hashCode);
		return hashCode;
	}
		
	@Override
	public boolean equals(Object o) {
		if (o == null) { 

			//logger.info(this.toString() +" NOT (1) equals "+ o.toString());
			return false;
		}
		if (o == this) { 
			//logger.info(this.toString() +" equals (2) "+ o.toString());
			return true; 
		}
		
		CustomAttribute other = null;
		try{

			other = (CustomAttribute) o;
		}
		catch(ClassCastException e){
			Log.warn(e);
		}
		finally{
			if(other == null){
				return false;
			}
		}
		boolean equals = new EqualsBuilder()
	        .append(this.getName(), other.getName())
	        .isEquals();

		//logger.info(this.toString() +" equals "+ o.toString()+": "+equals);
		return equals;
	}

}