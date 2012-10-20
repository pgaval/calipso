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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Class to represent a possible value for a given AssetTypeCustomAttribute.
 * @author manos
 */
public class CustomAttributeLookupValue extends AbstractI18nResourceTranslatable implements Serializable, Comparable<CustomAttributeLookupValue>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4542118543248754203L;
	/**
	 * 
	 */
	private long id;
	private CustomAttribute attribute;
	private CustomAttributeLookupValue parent;
	// this should be renamed to description, 
	// essentially the ID is the value 
	// and the UI only needs the translation resource key to render 
	// something meaningfull to the user.
	private String value;
	private int showOrder;
	private int level = 1;
	private Set<CustomAttributeLookupValue> children;
	
	public CustomAttributeLookupValue() {
	}



	public CustomAttributeLookupValue(long id, AssetTypeCustomAttribute attribute, String value, int showOrder) {
		this.id = id;
		this.attribute = attribute;
		this.value = value;
		this.showOrder = showOrder;
		this.level = 1;
	}

	public CustomAttributeLookupValue(AssetTypeCustomAttribute attribute, String value, int showOrder, int level) {
		this.attribute = attribute;
		this.value = value;
		this.level = level;
		this.showOrder = showOrder;
	}


	/**
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}


	/**
	 * @return the attribute
	 */
	public CustomAttribute getAttribute() {
		return this.attribute;
	}


	/**
	 * @param attribute the attribute to set
	 */
	public void setAttribute(CustomAttribute attribute) {
		this.attribute = attribute;
	}


	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the show order of the value
	 * */
	public int getShowOrder() {
		return showOrder;
	}



	/**
	 * @param showOrder Show Order to set
	 * */
	public void setShowOrder(int showOrder) {
		this.showOrder = showOrder;
	}
	

	/**
	 * @see gr.abiss.calipso.domain.i18n.AbstractI18nResourceTranslatable#getNameTranslationResourceKey()
	 */
	public String getPropertyTranslationResourceKey(String name){
		return new StringBuffer(getShortName(CustomAttributeLookupValue.class))
		.append('.')
		.append(this.getId())
		.append('.')
		.append(name)
		.toString();
	}

	
	@Override
	public boolean equals(Object object) {
		if (object==null){
			return false;
		}//if

		try{
			return this.getId()==((CustomAttributeLookupValue)object).getId();
		}
		catch(Exception e){
			return false;
		}
	}



	/**
	 * @see gr.abiss.calipso.domain.i18n.AbstractI18nResourceTranslatable#getName()
	 */
	@Override
	public String getName() {
		return this.value;
	}



	/**
	 * @see gr.abiss.calipso.domain.i18n.AbstractI18nResourceTranslatable#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.value = name;
	}
	
	public CustomAttributeLookupValue getParent() {
		return parent;
	}



	public void setParent(CustomAttributeLookupValue parent) {
		this.parent = parent;
	}



	public int getLevel() {
		return level;
	}



	public void setLevel(int level) {
		this.level = level;
	}



	public Set<CustomAttributeLookupValue> getChildren() {
		return children;
	}



	public void setChildren(Set<CustomAttributeLookupValue> children) {
		this.children = children;
	}
	
	public void addChild(CustomAttributeLookupValue child) {
		if(this.children == null){
			this.children = new TreeSet<CustomAttributeLookupValue>();
		}
		this.children.add(child);
		//child.setParent(this);
	}



	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id", this.getId())
			.append("showOrder", this.getShowOrder())
			.append("level", this.getLevel())
			.append("value", this.getValue())
			.toString();
	}



	@Override
	public int compareTo(CustomAttributeLookupValue other) {
		int firstIs = this.getLevel() - other.getLevel();
		if(firstIs == 0){
			firstIs = this.getShowOrder() - other.getShowOrder();
		}
		return firstIs;
	}
}
