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

import org.apache.commons.lang.ObjectUtils;

/**
 * Domain class to persist reusable regular expressions to the DB.
 * Used for validating Space Custom Fields and Asset Custom Attributes
 *
 */
public class ValidationExpression extends AbstractI18nResourceTranslatable implements Serializable{

	
	public static final String DESCRIPTION = "description";
	/**
	 * The validation expression ID
	 */
	private Long id;
	
	/**
	 * The  validation expression name
	 */
	private String name;

	/**
	 * The  validation expression description
	 */
	private String description;
	
	/**
	 * The  validation expression string
	 */
	private String expression;
	
	
	public ValidationExpression() {
		super();
		this.translations.put("description", new HashMap<String,String>());
	}
	
	public ValidationExpression(String name, String description,
			String expression) {
		this();
		this.name = name;
		this.description = description;
		this.expression = expression;
	}

	public ValidationExpression(long id, String description, String expression) {
		this();
		this.id = id;
		this.description = description;
		this.expression = expression;
	}
	
	@Override
	public String toString() {
		return new StringBuffer("ValidationExpression [description=")
			.append(description)
			.append(", expression=")
			.append(expression)
			.append(", id=")
			.append(id)
			.append(", name=")
			.append(name)
			.append("]")
			.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
		+ ((name == null) ? 0 : name.hashCode());
		result = prime * result
		+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ValidationExpression)){
			return false;
		}
		
		ValidationExpression other = (ValidationExpression) obj;
		if(ObjectUtils.equals(getId(), other.getId())){
			return true;
		}
		if(ObjectUtils.equals(getName(), other.getName())){
			return true;
		}
		return false;
	}
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	

	@Override
	public String getI18nId() {
		return this.getId().toString();
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
}
