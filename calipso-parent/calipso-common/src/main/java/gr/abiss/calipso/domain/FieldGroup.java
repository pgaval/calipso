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
 * 
 * This file incorporates work released by the JTrac project and  covered 
 * by the following copyright and permission notice:  
 * 
 *   Copyright 2002-2005 the original author or authors.
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *   
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package gr.abiss.calipso.domain;

import static gr.abiss.calipso.Constants.FIELD_GROUP;
import static gr.abiss.calipso.Constants.GROUP_ID;
import static gr.abiss.calipso.Constants.PRIORITY;
import static gr.abiss.calipso.Constants.NAME;
import gr.abiss.calipso.domain.i18n.AbstractI18nResourceTranslatable;
import gr.abiss.calipso.domain.i18n.I18nResourceTranslatable;
import gr.abiss.calipso.util.XmlUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.dom4j.Element;

/**
 * <code>FieldGroup</code> corresponds to fieldset HTML elements
 */
public class FieldGroup extends AbstractI18nResourceTranslatable implements Comparable{
    
	protected static final Logger logger = Logger.getLogger(FieldGroup.class);
	
	private List<Field> fields;

	private String id = "";
	//private String uuid;
	private String name;
	private short priority;

	public FieldGroup(Element e) {
		this();
		setId(e.attributeValue(GROUP_ID));
		setName(e.attributeValue(NAME));
		setPriority(Short.parseShort(e.attributeValue(PRIORITY)));
	}
	public FieldGroup(String id, String name) {
		this();
		this.id = id;
		this.name = name;
		this.priority = 0;
	}

	public FieldGroup() {
		//this.uuid =  UUID.randomUUID().toString();
	}
	/* append this object onto an existing XML document */
	public void addAsChildOf(Element parent) {
		Element e = parent.addElement(FIELD_GROUP);
		copyTo(e);
	}

	/* marshal this object into a fresh new XML Element */
	public Element getAsElement() {
		Element e = XmlUtils.getNewElement(FIELD_GROUP);
		copyTo(e);
		return e;
	}

	/* copy object values into an existing XML Element */
	private void copyTo(Element e) {
		// appending empty strings to create new objects for "clone" support
		e.addAttribute(GROUP_ID, this.id + "");
		e.addAttribute(NAME, this.name + "");
		e.addAttribute(PRIORITY, this.priority + "");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;		
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
/*
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	*/
	public short getPriority() {
		return priority;
	}

	public void setPriority(short priority) {
		this.priority = priority;
	}

	/**
	 * @see gr.abiss.calipso.domain.i18n.AbstractI18nResourceTranslatable#getNameTranslationResourceKey()
	 */
	public String getPropertyTranslationResourceKey(String propertyName){
		return new StringBuffer(getShortName(FieldGroup.class))
		.append('.')
		.append(this.getId())
		.append('.')
		.append(propertyName)
		.toString();
	}

	public void addField(Field field) {
		if(this.fields == null){
			this.fields = new LinkedList<Field>();
		}
		this.fields.add(field);
	}
	
	public String toString(){
		return new ToStringBuilder(this)
		.append("id", this.getId())
		.append("name", this.getName())
		.append("priority", this.getPriority())
		.toString();
	}
	
	@Override
	public boolean equals(Object o) {
			return this.id == ((FieldGroup)o).id;
	}
	@Override
	public int compareTo(Object o) {
		FieldGroup other = (FieldGroup) o;
		int comparison = 0;
		if(this.getPriority() < other.getPriority()){
			comparison = -1;
		}
		if(this.getPriority() > other.getPriority()){
			comparison = 1;
		}
		return comparison;
	}
	
	
}
