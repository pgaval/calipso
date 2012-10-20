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


import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

/**
 * Represents a custom property as it applies to AssetType instances and includes info 
 * on value datatyping and rendering/validation info.
 *
 */
public class AssetTypeCustomAttribute extends CustomAttribute implements Serializable, Comparable<AssetTypeCustomAttribute>{

	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(AssetTypeCustomAttribute.class);

	/**
	 * Serial Version Id
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean mandatory;
	private boolean active;

	private Set<AssetType> assetTypes;
	
	public AssetTypeCustomAttribute(Long id, String name, Integer formType,
			boolean mandatory, boolean active) {
		this.id = id;
		this.name = name;
		this.formType = formType;
		this.mandatory = mandatory;
		this.active = active;
	}
	
	
	
	public AssetTypeCustomAttribute(Long id, String name, ValidationExpression validationExpression, Integer formType,
			boolean mandatory, boolean active) {
		this.id = id;
		this.name = name;
		this.formType = formType;
		this.mandatory = mandatory;
		this.active = active;
		this.validationExpression = validationExpression;

	}

	
	
	public AssetTypeCustomAttribute(String name, Integer formType,
			String dataType, ValidationExpression validationExpression, boolean mandatory, boolean active) {
		super(name, formType, dataType, validationExpression);
		this.mandatory = mandatory;
		this.active = active;
	}



	public AssetTypeCustomAttribute() {
	}
	

	public void add(AssetType assetType){
		if (this.assetTypes==null){
			this.assetTypes = new LinkedHashSet<AssetType>();
		}
		this.assetTypes.add(assetType);
	}
	
	public void remove(AssetType assetType){
		if (this.assetTypes!=null){
			this.assetTypes.remove(assetType);
		}
	}
	
	/**
	 * @return the assetTypes
	 */
	public Set<AssetType> getAssetTypes() {
		return this.assetTypes;
	}


	/**
	 * @param assetTypes the assetType to set
	 */
	public void setAssetTypes(Set<AssetType> assetTypes) {
		this.assetTypes = assetTypes;
	}


	/**
	 * @return the mandatory
	 */
	public boolean isMandatory() {
		return this.mandatory;
	}


	/**
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id", this.getId())
			.append("name", this.getName())
			.append("active", this.isActive())
			.append("editable", this.isEditable())
			.append("mandatory", this.isMandatory())
			.append("formType", this.getFormType())
			.toString();
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
		AssetTypeCustomAttribute other = null;
		try{

			other = (AssetTypeCustomAttribute) o;
		}
		catch(ClassCastException e){
			logger.warn(e);
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
	
	/**
	 * Compares to accomplish rendering order semantics using 
	 *  attributeGroupIndex, attributeIndex and name.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(AssetTypeCustomAttribute other) {    
		final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;
	
	    //this optimization is usually worthwhile, and can
	    //always be added
	    if ( this == other ) return EQUAL;
	    // if same class, compare based on rendering order members
    	 if (this.attributeIndex.shortValue() < other.attributeIndex.shortValue()){
		    	return BEFORE;
	    }
	    else if (this.attributeIndex.shortValue() < other.attributeIndex.shortValue()){
	    	return AFTER;
	    }
	    else{
	    	if(this.name != null && other.name != null){
	    		return this.name.compareTo(other.name);
	    	}
	    	else if(this.name == null){
	    		return BEFORE;
	    	}
    	 	else{
    	 		return AFTER;
    	 	}
	    }

	}

}