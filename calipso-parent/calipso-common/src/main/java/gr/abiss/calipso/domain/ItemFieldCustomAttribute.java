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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

/**
 * Represents an Item field custom property, mainly to link to CustomAttributeLookupValue instances.
 *
 */
public class ItemFieldCustomAttribute extends CustomAttribute implements Serializable, Comparable<ItemFieldCustomAttribute>{

	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(ItemFieldCustomAttribute.class);
	protected static final String NAME = "name";

	private String fieldName = null;
	private boolean showInSearchResults = false;
	private String htmlDescription = null;
	private Space space = null;
	
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public boolean isShowInSearchResults() {
		return showInSearchResults;
	}

	public void setShowInSearchResults(boolean showInSearchResults) {
		this.showInSearchResults = showInSearchResults;
	}

	public String getHtmlDescription() {
		return htmlDescription;
	}

	public void setHtmlDescription(String htmlDescription) {
		this.htmlDescription = htmlDescription;
	}

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id", this.getId())
			.append("name", this.getName())
			.append("editable", this.isEditable())
			.append("formType", this.getFormType())
			.append("fieldName", this.getFieldName())
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
		ItemFieldCustomAttribute other = null;
		try{

			other = (ItemFieldCustomAttribute) o;
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
	public int compareTo(ItemFieldCustomAttribute other) {    
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
	
	/**
	 * @see gr.abiss.calipso.domain.i18n.AbstractI18nResourceTranslatable#getPropertyTranslationResourceKey(String)
	 */
	public String getPropertyTranslationResourceKey(String name) {
		String key = null;
		if(NAME.equals(name)){
			key = new StringBuffer("Space")
			.append('.')
			.append(this.getSpace().getId())
			.append('.')
			.append(this.getFieldName())
			.toString();
		}
		else{
			key = super.getPropertyTranslationResourceKey(name);
		}
		return key;
	}
	
	public void removeAll(Collection<CustomAttributeLookupValue> toRemove) {
		if(toRemove != null){
			List<CustomAttributeLookupValue> removables = new LinkedList<CustomAttributeLookupValue>();
			removables.addAll(toRemove);
			for(CustomAttributeLookupValue old : removables){
				this.remove(old);
			}
		}
	}

}