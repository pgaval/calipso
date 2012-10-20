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

import static gr.abiss.calipso.Constants.*;

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.Field.Name;
import gr.abiss.calipso.util.XmlUtils;
import gr.abiss.calipso.wicket.AbstractItemFormPanel;
import gr.abiss.calipso.wicket.BasePanel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Document;
import org.dom4j.Element;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

/**
 * XML metadata is one of the interesting design decisions of JTrac. Metadata is
 * defined for each space and so Items that belong to a space are customized by
 * the space metadata. This class can marshall and unmarshall itself to XML and
 * this XML is stored in the database in a single column. Because of this
 * approach, Metadata can be made more and more complicated in the future
 * without impact to the database schema.
 * 
 * Things that the Metadata configures for a Space:
 * 
 * 1) custom Fields for an Item (within a Space) - Label - whether mandatory or
 * not [ DEPRECATED ] - the option values (drop down list options) - the option
 * "key" values are stored in the database (WITHOUT any relationships) - the
 * values corresponding to "key"s are resolved in memory from the Metadata and
 * not through a database join.
 * 
 * 2) the Roles available within a space - for each (from) State the (to) State
 * transitions allowed for this role - and within each (from) State the fields
 * that this Role can view / edit
 * 
 * 3) the State labels corresponding to each state - internally States are
 * integers, but for display we need a label - labels can be customized -
 * special State values: 0 = New, 1 = Open, 99 = Closed
 * 
 * 4) the order in which the fields are displayed on the data entry screens and
 * the query result screens etc.
 * 
 * There is one downside to this approach and that is there is a limit to the
 * nunmbers of custom fields available. The existing limits are - Drop Down: 10
 * - Free Text: 5 - Numeric: 3 - Date/Time: 3
 * 
 * Metadata can be inherited, and this allows for "reuse" TODO
 */
public class Metadata implements Serializable {

	private static final Logger logger = Logger.getLogger(Metadata.class);
	
	// date formats
	public static final String DATE_FORMAT_LONG = "dateFormatLong";
	public static final String DATE_FORMAT_SHORT = "dateFormatShort";

	public static final String DATETIME_FORMAT_LONG = "dateTimeFormatLong";
	public static final String DATETIME_FORMAT_SHORT = "dateTimeFormatShort";
	public static final List<String> DATE_FORMAT_KEYS;
	static{
		String[] keys = {DATETIME_FORMAT_LONG, DATETIME_FORMAT_SHORT, DATE_FORMAT_LONG, DATE_FORMAT_SHORT};
		DATE_FORMAT_KEYS = Arrays.asList(keys);
	}
	private final ConcurrentHashMap<String,String> dateFormats = new ConcurrentHashMap<String,String>();
	private final ConcurrentHashMap<String,SimpleDateFormat> simpleDateFormats = new ConcurrentHashMap<String,SimpleDateFormat>();

	private long id;
	private int version;
	private Integer type;
	private String name;
	private String description;
	private Metadata parent;
	
	
	private List<FieldGroup> fieldGroups = new LinkedList<FieldGroup>();
	private Map<String,FieldGroup> fieldGroupsById = new HashMap<String,FieldGroup>();
	private Map<Field.Name, Field> fields;
	private Map<String, Field> fieldsByLabel;
	private Map<String, Role> roles;
	private Map<Integer, String> states;
	private Map<String, Integer> statesByName;
	private Map<Integer, String> statesPlugins;
	private Map<Integer, Long> maxDurations;
	// holds all asset types ids for a space
	private Map<Integer, Long> assetTypeIdMap;
	// a map that holds existing assetTypes ids for a space
	// this serves as flag that an asset of the given type must be sesected
	//  by the user upon entering the given State
	private Map<Integer, Long> existingAssetTypeIdsMap;
	// whether multiple assets of the above type can be selected
	private Map<Integer, Boolean> existingAssetTypeMultipleMap;
	private List<Field.Name> fieldOrder;

	public Metadata() {
		init();
		addDefaultFieldGroup();
	}

	public SimpleDateFormat getDateFormat(String formatKey){
		SimpleDateFormat sdf = simpleDateFormats.get(formatKey);
		if(sdf == null){
			String expression = this.dateFormats.get(formatKey);
			if(expression == null){
				throw new RuntimeException("Invalid date format key: "+formatKey);
			}
			sdf = new SimpleDateFormat(expression);
			simpleDateFormats.put(formatKey, sdf);
		}
		return sdf;
	}
	
	public FieldGroup getDefaultFieldGroup(){
		return this.fieldGroups.get(0);
	}
	
	private void init() {
		fields = new EnumMap<Field.Name, Field>(Field.Name.class);
		fieldsByLabel = new HashMap<String,Field>();
		if (logger.isDebugEnabled()) {
			for (Field f : fields.values()) {
				logger.debug("Field " + " name " + f.getName().getText()
						+ " , description" + f.getName().getDescription() + " ");
			}
		}
		roles = new HashMap<String, Role>();
		states = new TreeMap<Integer, String>();
		statesByName = new TreeMap<String,Integer>();
		statesPlugins = new TreeMap<Integer, String>();
		maxDurations = new TreeMap<Integer, Long>();
		fieldOrder = new LinkedList<Field.Name>();
		assetTypeIdMap = new TreeMap<Integer, Long>();
		existingAssetTypeIdsMap = new TreeMap<Integer, Long>();
		existingAssetTypeMultipleMap = new TreeMap<Integer, Boolean>();
		
		// date formats
		this.dateFormats.put(DATETIME_FORMAT_LONG, "yyyy/MM/dd HH:mm:ss");
		this.dateFormats.put(DATETIME_FORMAT_SHORT, "yyyy/MM/dd HH:mm");
		this.dateFormats.put(DATE_FORMAT_LONG, "yyyy/MM/dd");
		this.dateFormats.put(DATE_FORMAT_SHORT, "yyyy/MM/dd");
	}
	
	public void loadOptions(CalipsoService calipso, Space space){
		for (Field field : fields.values()) {
			field.setCustomAttribute(calipso.loadItemCustomAttribute(space, field.getName().getText()));
		}
	}

	/* accessor, will be used by Hibernate */
	@SuppressWarnings("unchecked")
	public void setXmlString(String xmlString) {
		//init();
		//logger.info("setXmlString: "+xmlString);
		if (xmlString == null) {
			return;
		}
		Document document = XmlUtils.parse(xmlString);
		
		// date formats
		
		for (Element e : (List<Element>) document.selectNodes(DATEFORMATS_XPATH)) {
			String dfKey = e.attribute(NAME).getValue();
			String dfExpression = e.attribute(EXPRESSION).getValue();
			this.dateFormats.put(dfKey, dfExpression);
		}
		
		// field groups
		fieldGroups.clear();
		for (Element e : (List<Element>) document.selectNodes(FIELD_GROUP_XPATH)) {
			FieldGroup fieldGroup = new FieldGroup(e);
			fieldGroups.add(fieldGroup);
			fieldGroupsById.put(fieldGroup.getId(), fieldGroup);
		}
		if(fieldGroups.isEmpty()){
			addDefaultFieldGroup();
		}

		// sort by priority
		TreeSet<FieldGroup> fieldGroupSet = new TreeSet<FieldGroup>();
		fieldGroupSet.addAll(fieldGroups);
		fieldGroups.clear();
		fieldGroups.addAll(fieldGroupSet);
		
		if(logger.isDebugEnabled()) logger.debug("Loaded fieldGroups:"+fieldGroups);
		for (Element e : (List<Element>) document.selectNodes(FIELD_XPATH)){

			Field field = new Field(e);
			fields.put(field.getName(), field);
			fieldsByLabel.put(field.getLabel(), field);
			// link to full field group object or 
			// of default if none is set

			//logger.info("field name: "+field.getName().getText()+", group id: "+field.getGroupId()+", group: "+fieldGroupsById.get(field.getGroupId()).getName());
			if(field.getGroupId() != null){
				FieldGroup fieldGroup = fieldGroupsById.get(field.getGroupId());
				if(fieldGroup == null){
					logger.warn("Field belongs to undefined field-group element with id: "+field.getGroupId()+", adding to default group");
					fieldGroup = fieldGroupsById.get("default");
				}
				else{
					fieldGroup.addField(field);
				}
			}
			else{
				// add field to default group if it does not
				// belong to any
				FieldGroup defaultFieldGroup =fieldGroups.get(0); 
				field.setGroup(defaultFieldGroup);
				defaultFieldGroup.addField(field);
			}
		}
		for (Element e : (List<Element>) document.selectNodes(ROLE_XPATH)) {
			Role role = new Role(e);
			roles.put(role.getName(), role);
		}
		for (Element e : (List<Element>) document.selectNodes(STATE_XPATH)) {
			String key = e.attributeValue(STATUS);
			String value = e.attributeValue(LABEL);
			states.put(Integer.parseInt(key), value);
			statesByName.put(value, Integer.parseInt(key));
			statesPlugins.put(Integer.parseInt(key), e.attributeValue(PLUGIN));
			String sDurations = e.attributeValue(MAX_DURATION);
			if(StringUtils.isNotBlank(sDurations)){
				maxDurations.put(Integer.parseInt(key), NumberUtils.createLong(sDurations));
			}
			String asTypeId = e.attributeValue(ASSET_TYPE_ID);
			if(StringUtils.isNotBlank(asTypeId)){
				assetTypeIdMap.put(Integer.parseInt(key), NumberUtils.createLong(asTypeId));
			}
			String existingAssetTypeId = e.attributeValue(EXISTING_ASSET_TYPE_ID);
			if(StringUtils.isNotBlank(existingAssetTypeId)){
				existingAssetTypeIdsMap.put(Integer.parseInt(key), NumberUtils.createLong(existingAssetTypeId));
			}

			String existingAssetTypeMultiple = e.attributeValue(EXISTING_ASSET_TYPE_MULTIPLE);
			if(StringUtils.isNotBlank(existingAssetTypeMultiple)){
				existingAssetTypeMultipleMap.put(Integer.parseInt(key), BooleanUtils.toBoolean(existingAssetTypeMultiple));
			}
		}
		fieldOrder.clear();
		for (Element e : (List<Element>) document.selectNodes(FIELD_ORDER_XPATH)) {
			String fieldName = e.attributeValue(NAME);
			fieldOrder.add(Field.convertToName(fieldName));
		}
	}

	protected void addDefaultFieldGroup() {
		if(logger.isDebugEnabled()) logger.debug("adding default group to fieldGroups:"+fieldGroups);
		FieldGroup fieldGroup = new FieldGroup();
		fieldGroup.setId("default");
		fieldGroup.setName("default");
		fieldGroup.setPriority(Short.parseShort("1"));
		fieldGroups.add(fieldGroup);
		fieldGroupsById.put(fieldGroup.getId(), fieldGroup);
	}

	/* accessor, will be used by Hibernate */
	public String getXmlString() {
		Document d = XmlUtils.getNewDocument(METADATA);
		Element root = d.getRootElement();
		Element dateFormats = root.addElement(DATEFORMATS);
		for (String dfKey : this.dateFormats.keySet()) {
			Element df = dateFormats.addElement(DATEFORMAT);
			df.addAttribute(NAME, dfKey);
			df.addAttribute(EXPRESSION, this.dateFormats.get(dfKey));
		}
		
		Element fs = root.addElement(FIELDS);
		for (FieldGroup fieldGroup : this.fieldGroups) {
			fieldGroup.addAsChildOf(fs);
		}
		for (Field field : fields.values()) {
			field.addAsChildOf(fs);
		}
		Element rs = root.addElement(ROLES);
		for (Role role : roles.values()) {
			role.addAsChildOf(rs);
		}
		Element ss = root.addElement(STATES);
		for (Map.Entry<Integer, String> entry : states.entrySet()) {
			Element e = ss.addElement(STATE);
			e.addAttribute(STATUS, entry.getKey() + "");
			e.addAttribute(LABEL, entry.getValue());
			e.addAttribute(PLUGIN, statesPlugins.get(entry.getKey()));
			Long lMaxDuration = maxDurations.get(entry.getKey());
			Long assetTypeId = assetTypeIdMap.get(entry.getKey());
			Long existingAssetTypeId = existingAssetTypeIdsMap.get(entry.getKey());
			Boolean existingAssetTypeMuliple = existingAssetTypeMultipleMap.get(entry.getKey());
			
			if(lMaxDuration != null){
				e.addAttribute(MAX_DURATION, lMaxDuration.toString());
			}
			if(assetTypeId != null){
				e.addAttribute(ASSET_TYPE_ID, assetTypeId.toString());
			}
			if(existingAssetTypeId != null){
				e.addAttribute(EXISTING_ASSET_TYPE_ID, existingAssetTypeId.toString());
			}
			if(existingAssetTypeMuliple != null){
				e.addAttribute(EXISTING_ASSET_TYPE_MULTIPLE, BooleanUtils.toStringTrueFalse(existingAssetTypeMuliple));
			}
		}
		Element fo = fs.addElement(FIELD_ORDER);
		for (Field.Name f : fieldOrder) {
			Element e = fo.addElement(FIELD);
			e.addAttribute(NAME, f.toString());
		}
		String xml = XmlUtils.getAsPrettyXml(d.asXML());
		//logger.info("getXmlString: "+xml);
		return xml;
	}


	// ====================================================================

	public void initRoles() {
		// set up default simple workflow
		states.put(State.NEW, "New");
		states.put(State.OPEN, "Open");
		// states.put(State.MOVE_TO_OTHER_SPACE, "Move-To-Other-Space");
		states.put(State.CLOSED, "Closed");
		statesByName.put("New", State.NEW);
		statesByName.put("Open", State.OPEN);
		statesByName.put("Closed", State.CLOSED);
		addRole(RoleType.REGULAR_USER.getIdAsString());
		toggleTransition(RoleType.REGULAR_USER.getIdAsString(), State.NEW,
				State.OPEN);
		toggleTransition(RoleType.REGULAR_USER.getIdAsString(), State.OPEN,
				State.OPEN);
		toggleTransition(RoleType.REGULAR_USER.getIdAsString(), State.OPEN,
				State.CLOSED);
		toggleTransition(RoleType.REGULAR_USER.getIdAsString(), State.CLOSED,
				State.OPEN);

		addRole(RoleType.GUEST.getIdAsString());
	}

	public void initRegularUserRole(String roleCode) {
		states.put(State.NEW, "New");
		states.put(State.OPEN, "Open");
		// states.put(State.MOVE_TO_OTHER_SPACE, "Move-To-Other-Space");
		states.put(State.CLOSED, "Closed");

		statesByName.put("New", State.NEW);
		statesByName.put("Open", State.OPEN);
		statesByName.put("Closed", State.CLOSED);

		addRole(roleCode);

		toggleTransition(roleCode, State.NEW, State.OPEN);
		toggleTransition(roleCode, State.OPEN, State.OPEN);
		toggleTransition(roleCode, State.OPEN, State.CLOSED);
		toggleTransition(roleCode, State.CLOSED, State.OPEN);
	}

	public void initGuestUserRole(String roleCode) {
		addRole(roleCode);
	}

	public Field getField(Name fieldName) {
		return fields.get(fieldName);
	}
	
	public Field getFieldByLabel(String label) {
		return fieldsByLabel.get(label);
	}

	public Field getField(String fieldName) {
		return fields.get(Field.convertToName(fieldName));
	}

	public void add(Field field) {
		logger.info("Metadata.add field: " + field);
		logger.info("field line count: "+field.getLineCount());
		logger.info("field default value expression: "+field.getDefaultValueExpression());
		logger.info("field customAttribute: "+field.getCustomAttribute());
		logger.info("field validationExpression: "+field.getValidationExpression());
		fields.put(field.getName(), field); // will overwrite if exists
		if (!fieldOrder.contains(field.getName())) { // but for List, need to check
			fieldOrder.add(field.getName());
		}
		for (Role role : roles.values()) {
			for (State state : role.getStates().values()) {
				state.add(field.getName());
			}
		}
	}

	public void removeField(String fieldName) {
		logger.info("Removing field, Name: "+fieldName);
		Field.Name tempName = Field.convertToName(fieldName);
		logger.info("Removing field, tempName: "+tempName);
		Field field2remove = fields.remove(tempName);

		logger.info("Removing field, field: "+field2remove);
		fieldOrder.remove(tempName);
		String label = null;
		if(CollectionUtils.isNotEmpty(this.fieldsByLabel.keySet())){
			for(String key : this.fieldsByLabel.keySet()){
				Field field = this.fieldsByLabel.get(key);
				if(field.getName().equals(tempName)){
					label = key;
					break;
				}
			}
		}
		if(label != null){
			Field removed = this.fieldsByLabel.remove(label);
			logger.info("removed field: "+removed);
			if(removed != null){
				field2remove = removed;
			}
		}
		
		if(field2remove != null){
			if(CollectionUtils.isNotEmpty(this.fieldGroups)){
				for(FieldGroup group : this.fieldGroups){
					if(CollectionUtils.isNotEmpty(group.getFields())){
						boolean groupRemoved = group.getFields().remove(field2remove);
						logger.info("removed fild from group? "+groupRemoved);
					}
				}
			}
		}
		
		
		for (Role role : roles.values()) {
			for (State state : role.getStates().values()) {
				state.remove(tempName);
			}
		}
	}

	/**
	 * Adds a "special" state like Move-To-Other-Space. Can be used for future
	 * use where another state should be added with an explicit state order
	 * number.
	 * 
	 * */
	public void addSpcecialState(int status, String stateName) {
		states.put(status, stateName);
		statesByName.put(stateName, status);
		for (Role role : roles.values()) {
			State state = new State(status, null, null, null);
			state.add(fields.keySet());
			role.add(state);
		}
	}
	
	/**
	 * 
	 * @param state
	 * 		The space state
	 * @return the state Integer ID
	 */
	public Integer getStateByName(String stateName){
		return this.statesByName.get(stateName);
	}

	
	// TODO: proper asset type save
	public void addState(String stateName, String statePlugin,
			Long duration, Long assetTypeId, Long existingAssetTypeId){
		addState(stateName, statePlugin, duration, assetTypeId, existingAssetTypeId, Boolean.FALSE);
	}
		
	// TODO: proper asset type save
	public void addState(String stateName, String statePlugin,
				Long duration, Long assetTypeId, Long existingAssetTypeId, Boolean existingAssetTypeMultiple){
		
		// always the application will have by default at least to states
		// so we can't get outOfBoundException
		// so the new state we'll be added before the State.CLOSED
		int newStatus = states.keySet().size()-1;
		
		states.put(newStatus, stateName);
		statesByName.put(stateName, newStatus);
		// by default each role will have permissions for this state, for all
		// fields
		
		for (Role role : roles.values()) {
			// crate state instance
			State state = new State(newStatus, statePlugin, duration, assetTypeId);
			state.setExistingAssetTypeId(existingAssetTypeId);
			state.add(fields.keySet());
			role.add(state);
		}
	}

	public void removeState(int stateId) {
		String stateName = states.remove(stateId);
		if(StringUtils.isNotBlank(stateName)){
			statesByName.remove(stateName);
		}
		for (Role role : roles.values()) {
			role.removeState(stateId);
		}

	}

	public void addRole(String roleName) {
		Role role = new Role(roleName);
		for (Map.Entry<Integer, String> entry : states.entrySet()) {
			State state = new State(entry.getKey(), null, null, null);
			state.add(fields.keySet());
			role.add(state);
		}
		roles.put(role.getName(), role);
	}

	public void renameRole(String oldRole, String newRole) {
		// important! this has to be combined with a database update
		Role role = roles.get(oldRole);
		if (role == null) {
			return; // TODO improve CalipsoTest and assert not null here
		}
		role.setName(newRole);
		roles.remove(oldRole);
		roles.put(newRole, role);
	}

	public void removeRole(String roleName) {
		// important! this has to be combined with a database update
		roles.remove(roleName);
	}

	public Set<Field.Name> getUnusedFieldNames() {
		EnumSet<Field.Name> allFieldNames = EnumSet.allOf(Field.Name.class);
		for (Field f : getFields().values()) {
			allFieldNames.remove(f.getName());
		}
		return allFieldNames;
	}

	public Map<String, String> getAvailableFieldTypes() {
		Map<String, String> fieldTypes = new LinkedHashMap<String, String>();
		for (Field.Name fieldName : getUnusedFieldNames()) {
			if (!fieldName.isAssignableSpaces()) {
				String fieldType = fieldTypes.get(fieldName.getType() + "");
				if (fieldType == null) {
					fieldTypes.put(fieldName.getType() + "", "1");
				} else {
					int count = Integer.parseInt(fieldType);
					count++;
					fieldTypes.put(fieldName.getType() + "", count + "");
				}
			}// if
		}// for
		return fieldTypes;
	}

	public Field getNextAvailableField(int fieldType) {
		Field newField = null;
		for (Field.Name fieldName : getUnusedFieldNames()) {
			if (fieldName.getType() == fieldType) {
				newField = new Field(fieldName + "");

				logger.info("New field name: '"+fieldName+"' type: "+fieldType);
				break;
			}
		}
		if(fieldType == 200){
			logger.info("New field is a simple attachement");
			newField.setFieldType(Field.FIELD_TYPE_SIMPLE_ATTACHEMENT);
			newField.setLabel(Field.FIELD_TYPE_SIMPLE_ATTACHEMENT);
		}
		
		
		
		
		// throw error if null. should not happen but helps during development.
		if(newField == null){
			throw new RuntimeException("No field available of type " + fieldType);
		}
		return newField;
	}

	// customized accessor
	public Map<Field.Name, Field> getFields() {
		Map<Field.Name, Field> map = fields;
		if (parent != null) {
			map.putAll(parent.getFields());
		}
		return fields;
	}

	// to make JSTL easier
	public Collection<Role> getRoleList() {
		return roles.values();
	}

	public List<Field> getFieldList() {
		Map<Field.Name, Field> map = getFields();
		List<Field> list = new ArrayList<Field>(fields.size());
		list.addAll(map.values());
		return list;
	}

	public String getCustomValue(Field.Name fieldName, Integer key) {
		return getCustomValue(fieldName, key + "");
	}

	public String getCustomValue(Field.Name fieldName, String key) {
		Field field = fields.get(fieldName);
		if (field != null) {
			return field.getCustomValue(key);
		}
		if (parent != null) {
			return parent.getCustomValue(fieldName, key);
		}
		return "";
	}

	public String getStatusValue(Integer key) {
		if (key == null) {
			return "";
		}
		String s = states.get(key);
		if (s == null) {
			return "";
		}
		return s;
	}

	public int getRoleCount() {
		return roles.size();
	}

	public int getFieldCount() {
		return getFields().size();
	}

	public int getStateCount() {
		return states.size();
	}

	/**
	 * logic for resolving the next possible transitions for a given role and
	 * state - lookup Role by roleKey - for this Role, lookup state by key
	 * (integer) - for the State, iterate over transitions, get the label for
	 * each and add to map The map returned is used to render the drop down list
	 * on screen, [ key = value ]
	 */
	public Map<Integer, String> getPermittedTransitions(List<String> roleKeys, int status) {
		Map<String, Role> rolesMap = getRolesMap();
		for (String key : rolesMap.keySet()) {
			Role role = rolesMap.get(key);
		}
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		for (String roleKey : roleKeys) {
			Role role = roles.get(roleKey);
			if (role != null) {
				State state = role.getStates().get(status);
				if (state != null) {
					for (int transition : state.getTransitions()) {
						map.put(transition, this.states.get(transition));
					}
				}
			}
		}
		return map;
	}

	// returning map ideal for JSTL
	public Map<String, Boolean> getRolesAbleToTransition(int fromStatus,
			int toStatus) {
		Map<String, Boolean> map = new HashMap<String, Boolean>(roles.size());
		for (Role role : roles.values()) {
			State s = role.getStates().get(fromStatus);
			if (s.getTransitions().contains(toStatus)) {
				map.put(role.getName(), true);
			}
		}
		return map;
	}

	public Set<String> getRolesAbleToTransitionFrom(int state) {
		Set<String> set = new HashSet<String>(roles.size());
		for (Role role : roles.values()) {
			State s = role.getStates().get(state);
			if (s.getTransitions().size() > 0) {
				set.add(role.getName());
			}
		}
		return set;
	}

	private State getRoleState(String roleKey, int stateKey) {
		Role role = roles.get(roleKey);
		return role.getStates().get(stateKey);
	}

	public void toggleTransition(String roleKey, int fromState, int toState) {
		State state = getRoleState(roleKey, fromState);
		if (state.getTransitions().contains(toState)) {
			state.getTransitions().remove(toState);
		} else {
			state.getTransitions().add(toState);
		}
	}

	/**
	 * Saves the next available mask
	 * 
	 * @param stateKey
	 *            Current space state
	 * @param roleKey
	 *            Current space role
	 * @param fieldName
	 *            The custom field's name
	 */
	public void switchMask(int stateKey, String roleKey, String fieldName) {
		State state = getRoleState(roleKey, stateKey);
		Field.Name tempName = Field.convertToName(fieldName);
		Integer mask = state.getFields().get(tempName);
		switch (mask) {
		// case State.MASK_HIDDEN: state.getFields().put(name,
		// State.MASK_READONLY); return; HIDDEN support in future
		case State.MASK_HIDDEN:
			state.getFields().put(tempName, State.MASK_READONLY);
			return;
		case State.MASK_READONLY:
			state.getFields().put(tempName, State.MASK_OPTIONAL);
			return;
		case State.MASK_OPTIONAL:
			state.getFields().put(tempName, State.MASK_MANDATORY);
			return;
		case State.MASK_MANDATORY:
			state.getFields().put(tempName, State.MASK_MANDATORY_IF_EMPTY);
			return;
		case State.MASK_MANDATORY_IF_EMPTY:
			state.getFields().put(tempName, State.MASK_HIDDEN);
			return;
		default: // should never happen
		}
	}	
	
	/**
	 * Saves the next available mask
	 * 
	 * @param stateKey
	 *            Current space state
	 * @param mask
	 *            The chosen mask to set
	 * @param roleKey
	 *            Current space role
	 * @param fieldName
	 *            The custom field's name
	 */
	public void setMask(int stateKey, Integer mask, String roleKey, String fieldName) {
		State state = getRoleState(roleKey, stateKey);
		Field.Name tempName = Field.convertToName(fieldName);
		state.getFields().put(tempName, mask);
	}

	public List<Field> getEditableFields(String roleKey, int status) {
		return getEditableFields(Collections.singletonList(roleKey), status);
	}

	public List<Field> getReadableFields(List<SpaceRole> roles, int status) {
		Set<String> roleKeys = new HashSet<String>();
		if(CollectionUtils.isNotEmpty(roles)){
			for(SpaceRole role: roles){
				roleKeys.add(role.getRoleCode());
			}
		}
		return getReadableFields(roleKeys, status);
	}
	

	public List<Field> getReadableFields(Collection<String> roleKeys, int status) {
		Map<Field.Name, Field> fs = new HashMap<Field.Name, Field>(getFieldCount());
		if(CollectionUtils.isNotEmpty(roleKeys)){
			for (String roleKey : roleKeys) {
				if (status > -1) {
					State state = getRoleState(roleKey, status);
					fs.putAll(getReadableFields(state));
				} else { // we are trying to find all editable fields
					Role role = roles.get(roleKey);
					for (State state : role.getStates().values()) {
						if (state.getStatus() == State.NEW) {
							continue;
						}
						fs.putAll(getReadableFields(state));
					}
				}
			}
		}
		// just to fix the order of the fields
		List<Field> result = new ArrayList<Field>(getFieldCount());
		for (Field.Name fieldName : fieldOrder) {
			Field f = fs.get(fieldName);
			// and not all fields may be editable
			if (f != null) {
				result.add(f);
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param roleKeys
	 * 
	 * @param status
	 *            The item status can be (i.e new, open, close )
	 * 
	 * @return The custom fields that an item can edit
	 */
	public List<Field> getEditableFields(Collection<String> roleKeys, int status) {
		Map<Field.Name, Field> fs = new HashMap<Field.Name, Field>(
				getFieldCount());
		for (String roleKey : roleKeys) {
			if (status > -1) {
				State state = getRoleState(roleKey, status);
				fs.putAll(getEditableFields(state));
			} else { // we are trying to find all editable fields
				Role role = roles.get(roleKey);
				for (State state : role.getStates().values()) {
					if (state.getStatus() == State.NEW) {
						continue;
					}
					fs.putAll(getEditableFields(state));
				}
			}
		}
		// just to fix the order of the fields
		List<Field> result = new ArrayList<Field>(getFieldCount());
		for (Field.Name fieldName : fieldOrder) {
			Field f = fs.get(fieldName);
			// and not all fields may be editable
			if (f != null) {
				result.add(f);
			}
		}
		return result;
	}

	public List<Field> getEditableFields() {
		return getEditableFields(roles.keySet(), -1);
	}
	
	private Map<Field.Name, Field> getEditableFields(State state) {
		Map<Field.Name, Field> fs = new HashMap<Field.Name, Field>(
				getFieldCount());
		for (Map.Entry<Field.Name, Integer> entry : state.getFields()
				.entrySet()) {
			int entryState = entry.getValue();
			if (entryState != State.MASK_HIDDEN
					&& entryState != State.MASK_READONLY) {
				Field f = fields.get(entry.getKey());
				// set if optional or not, this changes depending on the user /
				// role and status
				// TODO
				// set field mask from the entry's value
				f.setMask(entry.getValue());
				fs.put(f.getName(), f);
			}
		}
		return fs;
	}	
	private Map<Field.Name, Field> getReadableFields(State state) {
		Map<Field.Name, Field> fs = new HashMap<Field.Name, Field>(
				getFieldCount());
		for (Map.Entry<Field.Name, Integer> entry : state.getFields()
				.entrySet()) {
			int entryState = entry.getValue();
			if (entryState != State.MASK_HIDDEN) {
				Field f = fields.get(entry.getKey());
				// set if optional or not, this changes depending on the user /
				// role and status
				// TODO
				// set field mask from the entry's value
				f.setMask(entry.getValue());
				fs.put(f.getName(), f);
			}
		}
		return fs;
	}

	public List<Field> getViewableFields(List<SpaceRole> roles, int status) {
		Set<String> roleKeys = new HashSet<String>();
		if(CollectionUtils.isNotEmpty(roles)){
			for(SpaceRole role : roles){
				roleKeys.add(role.getRoleCode());
			}
		}
		return getViewableFields(roleKeys, status);
	}
	
	/**
	 * Used to retreive the custom fields a user can actually see in the
	 * overview, i.e. not MASK_HIDDEN
	 * 
	 * @param roleKeys
	 * @param status
	 * @return
	 */
	public List<Field> getViewableFields(Collection<String> roleKeys, int status) {
		Map<Field.Name, Field> fs = new HashMap<Field.Name, Field>(
				getFieldCount());
		for (String roleKey : roleKeys) {
			if (status > -1) {
				State state = getRoleState(roleKey, status);
				fs.putAll(getViewableFields(state));
			} else { // we are trying to find all editable fields
				Role role = roles.get(roleKey);
				for (State state : role.getStates().values()) {
					if (state.getStatus() == State.NEW) {
						continue;
					}
					fs.putAll(getViewableFields(state));
				}
			}
		}
		// just to fix the order of the fields
		List<Field> result = new ArrayList<Field>(getFieldCount());
		for (Field.Name fieldName : fieldOrder) {
			Field f = fs.get(fieldName);
			// and not all fields may be editable
			if (f != null) {
				result.add(f);
			}
		}
		return result;
	}

	/**
	 * Used to retrieve the custom fields a user can actually see in the
	 * overview, i.e. not MASK_HIDDEN
	 * 
	 * @param roleKeys
	 * @param status
	 * @return
	 */
	public Map<Field.Name, Field> getViewableFieldsMap(
			Collection<String> roleKeys, int status) {
		Map<Field.Name, Field> fs = new HashMap<Field.Name, Field>(
				getFieldCount());
		for (String roleKey : roleKeys) {
			if (status > -1) {
				State state = getRoleState(roleKey, status);
				fs.putAll(getViewableFields(state));
			} else { // we are trying to find all editable fields
				Role role = roles.get(roleKey);
				if (logger.isDebugEnabled()) {
					logger.debug("trying to find all editable fields");
				}
				for (State state : role.getStates().values()) {
					if (state.getStatus() == State.NEW) {
						continue;
					}
					if (logger.isDebugEnabled()) {
						logger.debug("geting viewable fields");
					}
					fs.putAll(getViewableFields(state));
				}
			}
		}
		return fs;
	}

	/**
	 * Used to retrieve the custom fields a user can actually see in the
	 * overview, i.e. not MASK_HIDDEN
	 * 
	 * @param state
	 * @return
	 */
	private Map<Field.Name, Field> getViewableFields(State state) {
		Map<Field.Name, Field> fs = new HashMap<Field.Name, Field>(
				getFieldCount());
		for (Map.Entry<Field.Name, Integer> entry : state.getFields()
				.entrySet()) {
			int entryState = entry.getValue();
			if (entryState != State.MASK_HIDDEN) {
				Field f = fields.get(entry.getKey());
				// set field mask from the entry's value
				f.setMask(entry.getValue());
				fs.put(f.getName(), f);
			}
		}
		return fs;
	}

	// ==================================================================

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Metadata getParent() {
		return parent;
	}

	public void setParent(Metadata parent) {
		this.parent = parent;
	}

	// =======================================
	// no setters required

	public Map<String, Role> getRolesMap() {
		return roles;
	}

	public Map<Integer, String> getStatesMap() {
		return states;
	}
	public Map<Integer, String> getStatesPluginMap() {
		return this.statesPlugins;
	}
	public Map<Integer, Long> getStatesDurationMap() {
		return this.maxDurations;
	}
	public Map<Integer, Long> getStatesAssetTypeIdMap() {
		if(logger.isDebugEnabled()){
			logger.debug("Returning asset type map :  " + assetTypeIdMap);
		}
		return this.assetTypeIdMap;
	}

	public List<Field.Name> getFieldOrder() {
		return fieldOrder;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id [").append(id);
		sb.append("]; parent [").append(parent);
		sb.append("]; fields [").append(fields);
		sb.append("]; roles [").append(roles);
		sb.append("]; states [").append(states);
		sb.append("]; fieldOrder [").append(fieldOrder);
		sb.append("]");
		return sb.toString();
	}

	/**
	 * @return 
	 */
	public Map<Integer, Long> getStatesExistingSpaceAssetTypeIdsMap() {
		// TODO Auto-generated method stub
		return this.existingAssetTypeIdsMap;
	}

	public Map<Integer, Boolean> getExistingAssetTypeMultipleMap() {
		// TODO Auto-generated method stub
		return this.existingAssetTypeMultipleMap;
	}

	public Map<String, FieldGroup> getFieldGroupsById() {
		return fieldGroupsById;
	}

	public void setFieldGroupsById(Map<String, FieldGroup> fieldGroupsById) {
		this.fieldGroupsById = fieldGroupsById;
	}

	public List<FieldGroup> getFieldGroups() {
		return fieldGroups;
	}

	public void setFieldGroups(List<FieldGroup> fieldGroups) {
		this.fieldGroups = fieldGroups;
	}

	public Map<String, String> getDateFormats() {
		return dateFormats;
	}
}
