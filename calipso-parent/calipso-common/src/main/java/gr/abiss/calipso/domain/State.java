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

import gr.abiss.calipso.util.XmlUtils;
import gr.abiss.calipso.wicket.BasePage;

import java.util.HashSet;

import static gr.abiss.calipso.Constants.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.dom4j.Element;

/**
 * State as in "State Transition" holds a set of possible future states to
 * transition to also holds a map of [ field name = integer "mask" ] to
 * represent permissions (view or edit) that the role owning this state has for
 * each field for an item which is in this particular state
 * 
 * For example, consider a state FOO and a role BAR. When a user with role BAR
 * views an item that is having the status FOO: ie. when item.status ==
 * FOO.status, the fields that can be viewed on screen will be the entries in
 * FOO.fields where the value == MASK_READONLY (or 1)
 */
public class State implements Serializable {
    protected static final Logger logger = Logger.getLogger(State.class); 

	private static final long serialVersionUID = 1L;
	private int status;
	private String plugin = null;
	private Long maxDuration = null;
	private Long assetTypeId = null;
	private Long existingAssetTypeId = null;
	private Boolean existingAssetTypeMultiple = false;
	private Set<Integer> transitions = new HashSet<Integer>();
	private Element element;
	private Map<Field.Name, Integer> fields = new HashMap<Field.Name, Integer>();

	public static final int NEW = 0;
	public static final int OPEN = 1;
	public static final int CLOSED = 99;
	// State should be predefined with a constant order number which is common
	// for all spaces.
	// public static final int MOVE_TO_OTHER_SPACE = 98;

	public static final int MASK_HIDDEN = 0;
	public static final int MASK_READONLY = 1;
	public static final int MASK_OPTIONAL = 2;
	public static final int MASK_MANDATORY = 3;
	public static final int MASK_MANDATORY_IF_EMPTY = 4;
	
	public static final List<Integer> MASK_KEYS;
	static{
		List<Integer> maskKeys = new ArrayList<Integer>(5);
		maskKeys.add(MASK_HIDDEN);
		maskKeys.add(MASK_READONLY);
		maskKeys.add(MASK_OPTIONAL);
		maskKeys.add(MASK_MANDATORY);
		maskKeys.add(MASK_MANDATORY_IF_EMPTY);
		MASK_KEYS = Collections.unmodifiableList(maskKeys);
	}

	public State() {
		// zero arg constructor
	}

	public State(int s, String plugin, Long maxDuration, Long assetTypeId) {
		this.status = s;
		this.plugin = plugin;
		this.maxDuration = maxDuration;
		this.assetTypeId = assetTypeId;
	}
	
	public State(Element e) {
		this.element = e;
		this.status = Integer.parseInt(e.attributeValue(STATUS));
		String xmlPlugin = e.attributeValue(PLUGIN);
		// plugin
		if(StringUtils.isNotBlank(xmlPlugin)){
			this.plugin = xmlPlugin;
		}
		String xmlAssetTypeId = e.attributeValue(ASSET_TYPE_ID);
		// asset type id
		if(StringUtils.isNotBlank(xmlAssetTypeId)){
			assetTypeId = NumberUtils.toLong(xmlAssetTypeId);
		}
		String xmlExistingAssetTypeId = e.attributeValue(EXISTING_ASSET_TYPE_ID);
		// asset type id
		if(StringUtils.isNotBlank(xmlExistingAssetTypeId)){
			existingAssetTypeId = NumberUtils.toLong(xmlExistingAssetTypeId);
		}
		String xmlExistingAssetTypeMultiple = e.attributeValue(EXISTING_ASSET_TYPE_MULTIPLE);
		if(StringUtils.isNotBlank(xmlExistingAssetTypeMultiple)){
			this.existingAssetTypeMultiple = BooleanUtils.toBoolean(existingAssetTypeMultiple);
		}
		
		// max duration
		String sMaxDuration = e.attributeValue(MAX_DURATION);
		if(StringUtils.isNotBlank(sMaxDuration)){
			this.maxDuration = NumberUtils.createLong(e.attributeValue(MAX_DURATION));
		}
		// transition
		for (Object o : e.elements(TRANSITION)) {
			Element t = (Element) o;
			transitions.add(new Integer(t.attributeValue(STATUS)));
		}
		// field
		for (Object o : e.elements(FIELD)) {
			Element f = (Element) o;
			String mask = f.attributeValue(MASK);
			String fieldName = f.attributeValue(NAME);
			fields.put(Field.convertToName(fieldName),
					NumberUtils.toInt(mask, 1));
		}
	}

	/* append this object onto an existing XML document */
	public void addAsChildOf(Element parent) {
		Element e = parent.addElement(STATE);
		copyTo(e);
	}

	/* marshal this object into a fresh new XML Element */
	public Element getAsElement() {
		Element e = XmlUtils.getNewElement(STATE);
		copyTo(e);
		return e;
	}

	/* copy object values into an existing XML Element */
	private void copyTo(Element e) {
		// appending empty strings to create new objects for "clone" support
		e.addAttribute(STATUS, status + "");
		if(this.plugin != null && this.plugin.length() > 0){
			e.addAttribute(PLUGIN, this.plugin);
		}
		// asset type id
		if(assetTypeId != null){
			e.addAttribute(ASSET_TYPE_ID, assetTypeId.toString());
		}

		// asset type id
		if(existingAssetTypeId != null){
			e.addAttribute(EXISTING_ASSET_TYPE_ID, existingAssetTypeId.toString());
		}
		// asset type id
		if(existingAssetTypeMultiple != null){
			e.addAttribute(EXISTING_ASSET_TYPE_MULTIPLE, BooleanUtils.toStringTrueFalse(existingAssetTypeMultiple));
		}
		// max duration
		if(this.maxDuration != null){
			e.addAttribute(MAX_DURATION, this.maxDuration.toString());
		}
		
		for (Integer toStatus : transitions) {
			Element t = e.addElement(TRANSITION);
			t.addAttribute(STATUS, toStatus + "");
		}
		
		for (Map.Entry<Field.Name, Integer> entry : fields.entrySet()) {
			Element f = e.addElement(FIELD);
			f.addAttribute(NAME, entry.getKey() + "");
			f.addAttribute(MASK, entry.getValue() + "");
		}
	}

	// =======================================================================

	public void add(Collection<Field.Name> fieldNames) {
		for (Field.Name fieldName : fieldNames) {
			add(fieldName);
		}
	}

	public void add(Field.Name fieldName) {
		int mask = MASK_READONLY;
		// for NEW states, normally all Fields on the Item are editable
		if (status == NEW) {
			mask = MASK_MANDATORY;
		}
		fields.put(fieldName, mask);
	}

	public void remove(Field.Name fieldName) {
		fields.remove(fieldName);
	}

	public void addTransition(int toStatus) {
		transitions.add(toStatus);
	}

	public void removeTransition(int toStatus) {
		transitions.remove(toStatus);
	}

	// =======================================================================

	public Map<Field.Name, Integer> getFields() {
		return fields;
	}

	public void setFields(Map<Field.Name, Integer> fields) {
		this.fields = fields;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the plugin
	 */
	public String getPlugin() {
		return plugin;
	}

	/**
	 * @param plugin the plugin to set
	 */
	public void setPlugin(String plugin) {
		this.plugin = plugin;
	}

	public Set<Integer> getTransitions() {
		return transitions;
	}

	public void setTransitions(Set<Integer> transitions) {
		this.transitions = transitions;
	}

	/**
	 * @return the maxDuration
	 */
	public Long getMaxDuration() {
		return maxDuration;
	}

	/**
	 * @return the assetTypeName
	 */
	public Long getAssetTypeName() {
		return assetTypeId;
	}

	/**
	 * @param maxDuration the maxDuration to set
	 */
	public void setMaxDuration(Long maxDuration) {
		this.maxDuration = maxDuration;
	}

	/**
	 * @param assetTypeName the assetTypeName to set
	 */
	public void setAssetTypeName(Long assetTypeId) {
		this.assetTypeId = assetTypeId;
	}
	/**
	 * @return the existingAssetTypeId
	 */
	public Long getExistingAssetTypeId() {
		return existingAssetTypeId;
	}

	/**
	 * @param existingAssetTypeId the existingAssetTypeId to set
	 */
	public void setExistingAssetTypeId(Long existingAssetTypeId) {
		this.existingAssetTypeId = existingAssetTypeId;
	}

	public static Map<Integer, String> getSpecialStates() {
		Map<Integer, String> specialStates = new HashedMap();
		specialStates.put(new Integer(OPEN), "Open");
		specialStates.put(new Integer(CLOSED), "Closed");

		return specialStates;

	}

	public Boolean getExistingAssetTypeMultiple() {
		return existingAssetTypeMultiple;
	}

	public void setExistingAssetTypeMultiple(Boolean existingAssetTypeMultiple) {
		this.existingAssetTypeMultiple = existingAssetTypeMultiple;
	}

	public int hashCode() {
		return new HashCodeBuilder(1, 31).append(this.status)
				.append(this.transitions).append(this.fields).toHashCode();
	}

	public String toString() {
		return new ToStringBuilder(this).append("status", status)
				.append("transitions", this.transitions)
				.append("fields", this.fields).toString();
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (o.getClass() != getClass()) {
			return false;
		}
		State other = (State) o;
		return new EqualsBuilder().append(this.status, other.status)
				.append(this.transitions, other.transitions)
				.append(this.fields, other.fields).isEquals();
	}
}
