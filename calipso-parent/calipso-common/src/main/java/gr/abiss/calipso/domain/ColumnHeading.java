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

import static gr.abiss.calipso.domain.ColumnHeading.Name.ASSET_TYPE;
import static gr.abiss.calipso.domain.ColumnHeading.Name.ASSIGNED_TO;
import static gr.abiss.calipso.domain.ColumnHeading.Name.DETAIL;
import static gr.abiss.calipso.domain.ColumnHeading.Name.DUE_TO;
import static gr.abiss.calipso.domain.ColumnHeading.Name.ID;
import static gr.abiss.calipso.domain.ColumnHeading.Name.LOGGED_BY;
import static gr.abiss.calipso.domain.ColumnHeading.Name.PLANNED_EFFORT;
import static gr.abiss.calipso.domain.ColumnHeading.Name.REPORTED_BY;
import static gr.abiss.calipso.domain.ColumnHeading.Name.SPACE;
import static gr.abiss.calipso.domain.ColumnHeading.Name.STATUS;
import static gr.abiss.calipso.domain.ColumnHeading.Name.SUMMARY;
import static gr.abiss.calipso.domain.ColumnHeading.Name.TIME_FROM_CREATION_TO_CLOSE;
import static gr.abiss.calipso.domain.ColumnHeading.Name.TIME_FROM_CREATION_TO_FIRST_REPLY;
import static gr.abiss.calipso.domain.ColumnHeading.Name.TIME_STAMP;
import static gr.abiss.calipso.domain.ColumnHeading.Name.TOTAL_RESPONSE_TIME;
import static gr.abiss.calipso.domain.FilterCriteria.Expression.BETWEEN;
import static gr.abiss.calipso.domain.FilterCriteria.Expression.CONTAINS;
import static gr.abiss.calipso.domain.FilterCriteria.Expression.EQ;
import static gr.abiss.calipso.domain.FilterCriteria.Expression.GT;
import static gr.abiss.calipso.domain.FilterCriteria.Expression.IN;
import static gr.abiss.calipso.domain.FilterCriteria.Expression.IS_NULL;
import static gr.abiss.calipso.domain.FilterCriteria.Expression.LT;
import static gr.abiss.calipso.domain.FilterCriteria.Expression.NOT_EQ;
import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.FilterCriteria.Expression;
import gr.abiss.calipso.util.DateUtils;
import gr.abiss.calipso.util.StdFieldsUtils;
import gr.abiss.calipso.wicket.EffortField;
import gr.abiss.calipso.wicket.components.formfields.CheckBoxMultipleChoice;

import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DateField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.PropertyModel;
import org.bouncycastle.asn1.cmp.OOBCertHash;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 * used to render columns in the search results table and also in the search
 * filter screen
 */
public class ColumnHeading implements Serializable {
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(ColumnHeading.class);
	private static final Map<String, Name> NAMES_MAP;

	// set up a static Map to resolve a String to our ColumnHeading.Name enum
	// value
	static {
		NAMES_MAP = new HashMap<String, Name>();
		for (Name n : Name.values()) {
			NAMES_MAP.put(n.text, n);
		}
	}

	/**
	 * Resolve a String to a valid enum value for ColumnHeading.Name
	 */
	private static Name convertToName(String text) {
		Name n = NAMES_MAP.get(text);
		if (n == null) {
			throw new RuntimeException("Bad name " + text);
		}
		return n;
	}

	/**
	 * test if a given string is a valid column heading name
	 */
	public static boolean isValidName(String text) {
		return NAMES_MAP.containsKey(text);
	}

	public static boolean isValidFieldOrColumnName(String text) {
		return isValidName(text) || Field.isValidName(text);
	}

	public enum Name {
		// name/text
		ID("id"), 
		SUMMARY("summary"), 
		DETAIL("detail"), 
		LOGGED_BY("loggedBy"), 
		REPORTED_BY("reportedBy"), 
		STATUS("status"), 
		ASSIGNED_TO("assignedTo"),  
		DUE_TO("dueTo"), 
		TIME_STAMP("timeStamp"), 
		SPACE("space"), 
		ASSET_TYPE("assets"), 
		TOTAL_RESPONSE_TIME("totalResponseTime"), 
		TIME_FROM_CREATION_TO_FIRST_REPLY("timeFromCreationToFirstReply"), 
		TIME_FROM_CREATION_TO_CLOSE("timeFromCreationToClose"), 
		PLANNED_EFFORT("plannedEffort"), 
		ORGANIZATION("organization"), 
		USER("user");

		private String text;

		Name(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		@Override
		public String toString() {
			return text;
		}

	}

	private Field field;
	private Name name;
	private String label;
	private boolean visible = true;
	private boolean isDbField = true;
	private boolean visibleCriterium = true;
	private Processor processor;

	private FilterCriteria filterCriteria = new FilterCriteria();

	// ---------------------------------------------------------------------------------------------

	public ColumnHeading(Name name) {
		this.name = name;
		if (name == DETAIL || name == SPACE) {
			visible = false;
		}

		if (name == TOTAL_RESPONSE_TIME
				|| name == TIME_FROM_CREATION_TO_FIRST_REPLY
				|| name == TIME_FROM_CREATION_TO_CLOSE) {
			isDbField = false;
			// visible = false;
		}

		if (name == TOTAL_RESPONSE_TIME) {
			visible = false;
			visibleCriterium = false;
		}

		processor = getProcessor();
	}

	// ---------------------------------------------------------------------------------------------
	/**
	 * For standard fields, does not require DB-based localization
	 */
	public ColumnHeading(Name name, Component c) {
		this(name);

		this.label = localize(name.getText(), c);
	}

	// ---------------------------------------------------------------------------------------------

	/**
	 * For standard fields, does not require DB-based localization
	 */
	public ColumnHeading(String name, Component c) {
		this(convertToName(name));

		this.label = localize(name, c);
	}

	// ---------------------------------------------------------------------------------------------

	public ColumnHeading(Field field, Space space) {
		this.field = field;

		this.label = space.getPropertyTranslationResourceKey(field.getName().getText());//field.getLabel();
		if (this.label == null) {
			this.label = field.getName().getText();
		}
		processor = getProcessor();
	}

	// ---------------------------------------------------------------------------------------------

	public boolean isField() {
		return field != null;
	}

	public boolean isDropDownType() {
		if (isField()) {
			return field.isDropDownType();
		} else {
			return name == LOGGED_BY || name == ASSIGNED_TO
					|| name == REPORTED_BY || name == STATUS;
		}
	}

	private String localize(String key, Component c) {
		return c.getLocalizer().getString("item_list." + key, c);
	}


	public static List<ColumnHeading> getColumnHeadings(Space s, User u, Component c){
		return getColumnHeadings(s, u, c, null);
	}
	
	public static List<ColumnHeading> getColumnHeadings(Space s, User u, Component c, CalipsoService calipso){
		Map<StdField.Field, StdFieldMask> fieldMaskMap = u.getStdFieldsForSpace(s);
		
		List<ColumnHeading> list = new ArrayList<ColumnHeading>();
		list.add(new ColumnHeading(ID, c));
		if(s == null || s.isItemSummaryEnabled()){
			list.add(new ColumnHeading(SUMMARY, c));
		}
		list.add(new ColumnHeading(DETAIL, c));
		list.add(new ColumnHeading(STATUS, c));
		list.add(new ColumnHeading(ASSIGNED_TO, c));
		list.add(new ColumnHeading(LOGGED_BY, c));
		list.add(new ColumnHeading(REPORTED_BY, c).setVisible(false));
		//logger.info("fieldMaskMap.get(StdField.Field.DUE_TO"+fieldMaskMap.get(StdField.Field.DUE_TO).getMask().getName());
		if (fieldMaskMap.get(StdField.Field.DUE_TO) != null
				&& !fieldMaskMap.get(StdField.Field.DUE_TO).getMask()
						.equals(StdFieldMask.Mask.HIDDEN)) {
			list.add(new ColumnHeading(StdField.Field.DUE_TO.getName(), c));
		}// if
		if (fieldMaskMap.get(StdField.Field.ASSET_TYPE) != null
				&& !fieldMaskMap.get(StdField.Field.ASSET_TYPE).getMask()
						.equals(StdFieldMask.Mask.HIDDEN)) {
			list.add(new ColumnHeading(StdField.Field.ASSET_TYPE.getName(), c));
		}// if

		list.add(new ColumnHeading(TOTAL_RESPONSE_TIME, c));

		if (fieldMaskMap.get(StdField.Field.TIME_FROM_CREATION_TO_FIRST_REPLY) != null
				&& !fieldMaskMap
						.get(StdField.Field.TIME_FROM_CREATION_TO_FIRST_REPLY)
						.getMask().equals(StdFieldMask.Mask.HIDDEN)) {
			list.add(new ColumnHeading(
					StdField.Field.TIME_FROM_CREATION_TO_FIRST_REPLY.getName(),
					c));
		}// if

		if (fieldMaskMap.get(StdField.Field.TIME_FROM_CREATION_TO_CLOSE) != null
				&& !fieldMaskMap
						.get(StdField.Field.TIME_FROM_CREATION_TO_CLOSE)
						.getMask().equals(StdFieldMask.Mask.HIDDEN)) {
			list.add(new ColumnHeading(
					StdField.Field.TIME_FROM_CREATION_TO_CLOSE.getName(), c));
		}// if

		if (fieldMaskMap.get(StdField.Field.PLANNED_EFFORT) != null
				&& !fieldMaskMap.get(StdField.Field.PLANNED_EFFORT).getMask()
						.equals(StdFieldMask.Mask.HIDDEN)) {
			list.add(new ColumnHeading(StdField.Field.PLANNED_EFFORT.getName(),
					c));
		}// if

		for (Field f : s.getMetadata().getFieldList()) {
			if(!f.getName().isFile()){
				ColumnHeading ch = new ColumnHeading(f, s);
				if(f.getCustomAttribute() == null && calipso != null){
					f.setCustomAttribute(calipso.loadItemCustomAttribute(s, f.getName().getText()));
				}
				if(f.getCustomAttribute() != null){
					boolean show = f.getCustomAttribute().isShowInSearchResults();
					ch.setVisible(show);
					//logger.info("show: "+ch.isVisible());
				}
//				else{
//					logger.info("show: custom attr was null");
//				}
				list.add(ch);
			}
		}
		list.add(new ColumnHeading(TIME_STAMP, c));

		return list;
	}

	public static List<ColumnHeading> getColumnHeadings(User u, Component c) {

		List<ColumnHeading> list = new ArrayList<ColumnHeading>();
		list.add(new ColumnHeading(ID, c));
		list.add(new ColumnHeading(SPACE, c));
		list.add(new ColumnHeading(SUMMARY, c));
		list.add(new ColumnHeading(DETAIL, c));
		list.add(new ColumnHeading(STATUS));
		list.add(new ColumnHeading(LOGGED_BY, c));
		list.add(new ColumnHeading(ASSIGNED_TO, c));
		list.add(new ColumnHeading(REPORTED_BY, c));

		if (!StdFieldsUtils
				.getFieldMask(u, new StdField(StdField.Field.DUE_TO)).getMask()
				.equals(StdFieldMask.Mask.HIDDEN)) {
			list.add(new ColumnHeading(StdField.Field.DUE_TO.getName(), c));
		}

		list.add(new ColumnHeading(TIME_STAMP, c));
		list.add(new ColumnHeading(TOTAL_RESPONSE_TIME, c));

		if (!StdFieldsUtils
				.getFieldMask(
						u,
						new StdField(
								StdField.Field.TIME_FROM_CREATION_TO_FIRST_REPLY))
				.getMask().equals(StdFieldMask.Mask.HIDDEN)) {
			list.add(new ColumnHeading(
					StdField.Field.TIME_FROM_CREATION_TO_FIRST_REPLY.getName(),
					c));
		}

		if (!StdFieldsUtils
				.getFieldMask(
						u,
						new StdField(StdField.Field.TIME_FROM_CREATION_TO_CLOSE))
				.getMask().equals(StdFieldMask.Mask.HIDDEN)) {
			list.add(new ColumnHeading(
					StdField.Field.TIME_FROM_CREATION_TO_CLOSE.getName(), c));
		}

		if (!StdFieldsUtils
				.getFieldMask(u, new StdField(StdField.Field.ASSET_TYPE))
				.getMask().equals(StdFieldMask.Mask.HIDDEN)) {
			list.add(new ColumnHeading(StdField.Field.ASSET_TYPE.getName(), c));
		}

		if (!StdFieldsUtils
				.getFieldMask(u, new StdField(StdField.Field.PLANNED_EFFORT))
				.getMask().equals(StdFieldMask.Mask.HIDDEN)) {
			list.add(new ColumnHeading(StdField.Field.PLANNED_EFFORT.getName(),
					c));
		}

		return list;
	}

	public static List<ColumnHeading> getColumnHeadings(Space s) {
		List<ColumnHeading> list = new ArrayList<ColumnHeading>();
		list.add(new ColumnHeading(ID));
		if(s == null || s.isItemSummaryEnabled()){
			list.add(new ColumnHeading(SUMMARY));
		}
		list.add(new ColumnHeading(DETAIL));
		list.add(new ColumnHeading(STATUS));
		list.add(new ColumnHeading(LOGGED_BY));
		list.add(new ColumnHeading(ASSIGNED_TO));
		list.add(new ColumnHeading(REPORTED_BY));
		list.add(new ColumnHeading(DUE_TO));
		list.add(new ColumnHeading(ASSET_TYPE));
		list.add(new ColumnHeading(TOTAL_RESPONSE_TIME));
		list.add(new ColumnHeading(TIME_FROM_CREATION_TO_FIRST_REPLY));
		list.add(new ColumnHeading(TIME_FROM_CREATION_TO_CLOSE));
		list.add(new ColumnHeading(PLANNED_EFFORT));

		for (Field f : s.getMetadata().getFieldList()) {

			//if(!f.getName().isFile()){
				list.add(new ColumnHeading(f, s));
			//}
		}
		list.add(new ColumnHeading(TIME_STAMP));

		return list;
	}

	public static List<ColumnHeading> getColumnHeadings(User u) {

		List<ColumnHeading> list = new ArrayList<ColumnHeading>();
		list.add(new ColumnHeading(ID));
		list.add(new ColumnHeading(SPACE));
		list.add(new ColumnHeading(SUMMARY));
		list.add(new ColumnHeading(DETAIL));
		list.add(new ColumnHeading(LOGGED_BY));
		list.add(new ColumnHeading(ASSIGNED_TO));
		list.add(new ColumnHeading(REPORTED_BY));
		list.add(new ColumnHeading(ASSET_TYPE));
		list.add(new ColumnHeading(DUE_TO));
		list.add(new ColumnHeading(TIME_STAMP));
		list.add(new ColumnHeading(TOTAL_RESPONSE_TIME));
		list.add(new ColumnHeading(TIME_FROM_CREATION_TO_FIRST_REPLY));
		list.add(new ColumnHeading(TIME_FROM_CREATION_TO_CLOSE));
		list.add(new ColumnHeading(PLANNED_EFFORT));

		return list;
	}

	public List<Expression> getValidFilterExpressions() {
		return processor.getValidFilterExpressions();
	}

	public Fragment getFilterUiFragment(MarkupContainer container, User user,
			Space space, CalipsoService calipsoService) {
		return processor.getFilterUiFragment(container, user, space, calipsoService);
	}

	public void addRestrictions(CustomCriteria customCriteria) {
		if (processor instanceof ProcessorCustom) {
			ProcessorCustom processorCustom = (ProcessorCustom) processor;
			processorCustom.addRestrictions(customCriteria);
		}
	}

	public void addRestrictions(DetachedCriteria criteria) {
		processor.addRestrictions(criteria);
	}

	public String getAsQueryString() {
		return processor.getAsQueryString();
	}

	public void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
		processor.loadFromQueryString(s, user, calipsoService);
	}

	/**
	 * also see description below for the private getProcessor() method
	 */
	private abstract class Processor implements Serializable {

		/*
		 * return the possible expressions (equals, greater-than etc) to show on
		 * filter UI for selection
		 */
		abstract List<Expression> getValidFilterExpressions();

		/*
		 * return the wicket ui fragment that will be shown over ajax (based on
		 * selected expression)
		 */
		abstract Fragment getFilterUiFragment(MarkupContainer container,
				User user, Space space, CalipsoService calipsoService);

		/*
		 * get as hibernate restriction and append to passed in criteria that
		 * will be used to query the database
		 */
		abstract void addRestrictions(DetachedCriteria criteria);

		/*
		 * return a querystring representation of the filter criteria to create
		 * a bookmarkable url
		 */
		abstract String getAsQueryString();

		/*
		 * load a querystring representation and initialize filter critera when
		 * acting on a bookmarkable url
		 */
		abstract void loadFromQueryString(String s, User user, CalipsoService calipsoService);

	}

	private abstract class ProcessorCustom extends Processor {
		abstract void addRestrictions(CustomCriteria customCriteria);
	}

	/**
	 * this routine is a massive if-then construct that acts as a factory for
	 * the right implementation of the responsibilities defined in the
	 * "Processor" class (above) based on the type of ColumnHeading - the right
	 * implementation will be returned. having everything in one place below,
	 * makes it easy to maintain, as the logic of each of the methods are
	 * closely interdependent for a given column type for e.g. the kind of
	 * hibernate criteria needed depends on what is made available on the UI
	 */
	private Processor getProcessor() {
		if (isField()) {
			switch (field.getName().getType()) {
			// ==============================================================

			case 1:
			case 2:
			case 3:
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(IN);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						final Fragment fragment = new Fragment("fragParent",
								"multiSelect", container);
						//final Map<String, String> options = field.getOptions();
						List<CustomAttributeLookupValue> lookupValues = calipsoService.findLookupValues(space, field.getName().getText());
						// TODO: create a leaf-node only finder method in calipso service
						List<CustomAttributeLookupValue> leafLookupValues = new LinkedList<CustomAttributeLookupValue>();
						
						// leaf selection only for now
						for(CustomAttributeLookupValue val : lookupValues){
							if(CollectionUtils.isEmpty(val.getChildren())){
								leafLookupValues.add(val);
							}
						}
						lookupValues = null;
						
						final CheckBoxMultipleChoice choice = new CheckBoxMultipleChoice(
								"values", leafLookupValues,
								new IChoiceRenderer<CustomAttributeLookupValue>() {

									@Override
									public Object getDisplayValue(
											CustomAttributeLookupValue object) {
										return fragment.getString(object.getNameTranslationResourceKey());
									}

									@Override
									public String getIdValue(
											CustomAttributeLookupValue object,
											int index) {
										return object.getId()+"";
									}
								});
						fragment.add(choice);
						choice.setModel(new PropertyModel(filterCriteria,
								"values"));
						return fragment;
					}

					void addRestrictions(DetachedCriteria criteria) {
						if (filterHasValueList()) {
							List values = filterCriteria.getValues();
							List<Integer> keys = new ArrayList<Integer>(
									values.size());
							for (Object o : values) {
								if(o instanceof CustomAttributeLookupValue){
									CustomAttributeLookupValue val = (CustomAttributeLookupValue) o;
									keys.add(NumberUtils.createInteger(val.getId()+""));
								}
								else{
									keys.add(NumberUtils.createInteger(o+""));
								}
							}
							criteria.add(Restrictions.in(getNameText(), keys));
						}
					}

					String getAsQueryString() {
						return getQueryStringFromValueList();
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setValueListFromQueryString(s);
					}
				};
				// ==============================================================
			case 4: // decimal number
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(EQ, NOT_EQ, GT, LT, BETWEEN);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						Fragment fragment = new Fragment("fragParent",
								"textField", container);
						TextField textField = new TextField("value",
								Double.class);
						textField.setModel(new PropertyModel(filterCriteria,
								"value"));
						fragment.add(textField);
						if (filterCriteria.getExpression() == BETWEEN) {
							TextField textField2 = new TextField("value2",
									Double.class);
							textField2.setModel(new PropertyModel(
									filterCriteria, "value2"));
							fragment.add(textField2);
						} else {
							fragment.add(new WebMarkupContainer("value2")
									.setVisible(false));
						}
						return fragment;
					}

					void addRestrictions(DetachedCriteria criteria) {
						if (filterHasValue()) {
							Object value = filterCriteria.getValue();
							switch (filterCriteria.getExpression()) {
							case EQ:
								criteria.add(Restrictions.eq(getNameText(),
										value));
								break;
							case NOT_EQ:
								criteria.add(Restrictions.not(Restrictions.eq(
										name.text, value)));
								break;
							case GT:
								criteria.add(Restrictions.gt(getNameText(),
										value));
								break;
							case LT:
								criteria.add(Restrictions.lt(getNameText(),
										value));
								break;
							case BETWEEN:
								criteria.add(Restrictions.gt(getNameText(),
										value));
								criteria.add(Restrictions.lt(getNameText(),
										filterCriteria.getValue2()));
								break;
							default:
							}
						}
					}

					String getAsQueryString() {
						return getQueryStringFromValue(Double.class);
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setValueFromQueryString(s, Double.class);
					}

				};
				// ==============================================================
			case 6: // date
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(EQ, NOT_EQ, GT, LT, BETWEEN);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						Fragment fragment = new Fragment("fragParent",
								"dateField", container);
						DateField calendar = new DateField("value",
								new PropertyModel(filterCriteria, "value")/*,
								false*/);
						fragment.add(calendar);
						if (filterCriteria.getExpression() == BETWEEN) {
							DateField calendar2 = new DateField(
									"value2",
									new PropertyModel(filterCriteria, "value2")/*,
									false*/);
							fragment.add(calendar2);
						} else {
							fragment.add(new WebMarkupContainer("value2")
									.setVisible(false));
						}
						return fragment;
					}

					void addRestrictions(DetachedCriteria criteria) {
						if (filterHasValue()) {
							Object value = filterCriteria.getValue();
							switch (filterCriteria.getExpression()) {
							case EQ:
								criteria.add(Restrictions.eq(getNameText(),
										value));
								break;
							case NOT_EQ:
								criteria.add(Restrictions.not(Restrictions.eq(
										getNameText(), value)));
								break;
							case GT:
								criteria.add(Restrictions.gt(getNameText(),
										value));
								break;
							case LT:
								criteria.add(Restrictions.lt(getNameText(),
										value));
								break;
							case BETWEEN:
								criteria.add(Restrictions.gt(getNameText(),
										value));
								criteria.add(Restrictions.lt(getNameText(),
										filterCriteria.getValue2()));
								break;
							default:
							}
						}
					}

					String getAsQueryString() {
						return getQueryStringFromValue(Date.class);
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setValueFromQueryString(s, Date.class);
					}
				};
				// ==============================================================
			case 5: // free text
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(CONTAINS);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						return getTextFieldFragment(container);
					}

					void addRestrictions(DetachedCriteria criteria) {
						if (filterHasValue()) {
							criteria.add(Restrictions.ilike(getNameText(),
									(String) filterCriteria.getValue(),
									MatchMode.ANYWHERE));
						}
					}

					String getAsQueryString() {
						return getQueryStringFromValue(String.class);
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setValueFromQueryString(s, String.class);
					}
				};
				// TODO:
			case 10:// organization
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(CONTAINS);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						return getTextFieldFragment(container);
					}

					void addRestrictions(DetachedCriteria criteria) {
						if (filterHasValue()) {
							criteria.add(Restrictions.ilike(getNameText(),
									(String) filterCriteria.getValue(),
									MatchMode.ANYWHERE));
						}
					}

					String getAsQueryString() {
						return getQueryStringFromValue(String.class);
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setValueFromQueryString(s, String.class);
					}
				};
				// TODO:
			case 11:// file
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(CONTAINS);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						return getTextFieldFragment(container);
					}

					void addRestrictions(DetachedCriteria criteria) {
						if (filterHasValue()) {
							criteria.add(Restrictions.ilike(getNameText(),
									(String) filterCriteria.getValue(),
									MatchMode.ANYWHERE));
						}
					}

					String getAsQueryString() {
						return getQueryStringFromValue(String.class);
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setValueFromQueryString(s, String.class);
					}
				};
			case 200:// attachment
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(CONTAINS);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						return getTextFieldFragment(container);
					}

					void addRestrictions(DetachedCriteria criteria) {
						if (filterHasValue()) {
							criteria.add(Restrictions.ilike(getNameText(),
									(String) filterCriteria.getValue(),
									MatchMode.ANYWHERE));
						}
					}

					String getAsQueryString() {
						return getQueryStringFromValue(String.class);
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setValueFromQueryString(s, String.class);
					}
				};
			case 20:// is user
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(CONTAINS);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						return getTextFieldFragment(container);
					}

					void addRestrictions(DetachedCriteria criteria) {
						if (filterHasValue()) {
							criteria.add(Restrictions.ilike(getNameText(),
									(String) filterCriteria.getValue(),
									MatchMode.ANYWHERE));
						}
					}

					String getAsQueryString() {
						return getQueryStringFromValue(String.class);
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setValueFromQueryString(s, String.class);
					}
				};

			case 25:// is user
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(CONTAINS);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						return getTextFieldFragment(container);
					}

					void addRestrictions(DetachedCriteria criteria) {
						if (filterHasValue()) {
							criteria.add(Restrictions.ilike(getNameText(),
									(String) filterCriteria.getValue(),
									MatchMode.ANYWHERE));
						}
					}

					String getAsQueryString() {
						return getQueryStringFromValue(String.class);
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setValueFromQueryString(s, String.class);
					}
				};
			case 100: // Assignable spaces (move to space)
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(IN);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						Fragment fragment = new Fragment("fragParent",
								"multiSelect", container);
						final Map<String, String> options = field.getOptions();
						List<String> keys = null;
						if (options != null) {
							keys = new ArrayList(options.keySet()); // bound
																	// value
						} else {
							keys = new ArrayList();
						}

						List<Space> spaces = new LinkedList<Space>();
						for (String key : keys) {
							// spaces.add(ComponentUtils.getJtrac(c).loadSpace(Long.parseLong(key)));
							spaces.add(calipsoService.loadSpace(Long.parseLong(key)));
						}// for
						CheckBoxMultipleChoice choice = new CheckBoxMultipleChoice(
								"values", spaces, new IChoiceRenderer<Space>() {
									private static final long serialVersionUID = 1L;

									@Override
									public Object getDisplayValue(Space o) {
										logger.info("Option for space: "+o);
										String name = o.getName();
										return name != null ? name : o.getId();
									}

									@Override
									public String getIdValue(Space o,
											int index) {
										return o.getId()+"";
									}
								});

						fragment.add(choice);
						choice.setModel(new PropertyModel(filterCriteria,
								"values"));
						return fragment;
					}

					void addRestrictions(DetachedCriteria criteria) {
						if (filterHasValueList()) {
							List values = filterCriteria.getValues();
							criteria.add(Restrictions.in(getNameText(), values));
						}
					}

					String getAsQueryString() {
						return getQueryStringFromValueList();
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setValueListFromQueryString(s);
					}
				};
				// ==============================================================
			default:
				throw new RuntimeException("Unknown Column Heading " + name);
			}
		} else { // this is not a custom field but one of the "built-in" columns
			switch (name) {
			// ==============================================================
			case ID:
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(EQ);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						return getTextFieldFragment(container);
					}

					void addRestrictions(DetachedCriteria criteria) {
						if (filterHasValue()) {
							// should never come here for criteria: see
							// ItemSearch#getRefId()
							throw new RuntimeException(
									"should not come here for 'id'");
						}
					}

					String getAsQueryString() {
						return getQueryStringFromValue(String.class);
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setValueFromQueryString(s, String.class);
					}
				};
				// ==============================================================
			case SUMMARY:
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(CONTAINS);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						return getTextFieldFragment(container);
					}

					void addRestrictions(DetachedCriteria criteria) {
						if (filterHasValue()) {
							criteria.add(Restrictions.ilike(getNameText(),
									(String) filterCriteria.getValue(),
									MatchMode.ANYWHERE));
						}
					}

					String getAsQueryString() {
						return getQueryStringFromValue(String.class);
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setValueFromQueryString(s, String.class);
					}
				};
				// ==============================================================
			case DETAIL:
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(CONTAINS);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						return getTextFieldFragment(container);
					}

					void addRestrictions(DetachedCriteria criteria) {
						// do nothing, 'detail' already processed, see:
						// ItemSearch#getSearchText()
					}

					String getAsQueryString() {
						return getQueryStringFromValue(String.class);
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setValueFromQueryString(s, String.class);
					}
				};

				// ==============================================================
			case STATUS:
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(IN);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						Fragment fragment = new Fragment("fragParent",
								"multiSelect", container);
						// status selectable only when context space is not null
						// final Map<Integer, String> options =
						// space.getMetadata().getStatesMap();
						final Map<Integer, String> options;
						if (space == null) {
							options = State.getSpecialStates();
						} else {
							options = space.getMetadata().getStatesMap();
						}
						options.remove(State.NEW);
						CheckBoxMultipleChoice choice = new CheckBoxMultipleChoice(
								"values", new ArrayList(options.keySet()),
								new IChoiceRenderer() {
									public Object getDisplayValue(Object o) {
										return options.get(o);
									}

									public String getIdValue(Object o, int i) {
										return o.toString();
									}
								});
						fragment.add(choice);
						choice.setModel(new PropertyModel(filterCriteria,
								"values"));
						return fragment;
					}

					void addRestrictions(DetachedCriteria criteria) {
						if (filterHasValueList()) {
							criteria.add(Restrictions.in(getNameText(),
									filterCriteria.getValues()));
						}
					}

					String getAsQueryString() {
						return getQueryStringFromValueList();
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						//logger.info("loadFromQueryString: "+s);
						setStatusListFromQueryString(s);
					}
				};
				// ==============================================================
			case ASSIGNED_TO:
			case LOGGED_BY:
			case REPORTED_BY:
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(IN, IS_NULL);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						Fragment fragment = new Fragment("fragParent",
								"multiSelect", container);
						List<User> users = null;
						if (space == null) {
							users = calipsoService.findUsersForUser(user);
						} else {
							users = calipsoService.findUsersForSpace(space.getId());
						}
						CheckBoxMultipleChoice choice = new CheckBoxMultipleChoice(
								"values", users, new IChoiceRenderer() {
									public Object getDisplayValue(Object o) {
										return ((User) o).getFullName();
									}

									public String getIdValue(Object o, int i) {
										return ((User) o).getId() + "";
									}
								});
						fragment.add(choice);
						choice.setModel(new PropertyModel(filterCriteria,
								"values"));
						return fragment;
					}

					void addRestrictions(DetachedCriteria criteria) {
						if (filterHasValueList()) {
							criteria.add(Restrictions.in(getNameText(),
									filterCriteria.getValues()));
						} else if (filterIsNullExpression()) {
							criteria.add(Restrictions.isNull(getNameText()));
						}
					}

					String getAsQueryString() {
						return getQueryStringFromUserList();
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setUserListFromQueryString(s, calipsoService);
					}
				};
				// ==============================================================

			case ASSET_TYPE:
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(IN);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						Fragment fragment = new Fragment("fragParent",
								"multiSelect", container);

						List<AssetType> assetTypes = calipsoService.findAllAssetTypes();
						CheckBoxMultipleChoice choice = new CheckBoxMultipleChoice(
								"values", assetTypes, new IChoiceRenderer() {
									public Object getDisplayValue(Object o) {
										return ((AssetType) o).getName();
									}

									public String getIdValue(Object o, int i) {
										return String.valueOf(((AssetType) o)
												.getId());
									}
								});
						fragment.add(choice);
						choice.setModel(new PropertyModel(filterCriteria,
								"values"));

						return fragment;
					}

					void addRestrictions(DetachedCriteria criteria) {
						if (filterHasValueList()) {
							criteria.createAlias("assets", "assets");
							criteria.add(Restrictions.in("assets.assetType",
									filterCriteria.getValues()));
						}
					}

					String getAsQueryString() {
						return getQueryStringFromAssetTypeList();
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setAssetTypeListFromQueryString(s, user, calipsoService);
					}
				};
				// ==============================================================
			case TIME_STAMP:
			case DUE_TO:
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(BETWEEN, GT, LT, EQ, NOT_EQ);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						Fragment fragment = new Fragment("fragParent",
								"dateField", container);
						DateField calendar = new DateField("value",
								new PropertyModel(filterCriteria, "value")/*,
								false*/);
						fragment.add(calendar);
						if (filterCriteria.getExpression() == BETWEEN) {
							DateField calendar2 = new DateField(
									"value2",
									new PropertyModel(filterCriteria, "value2")/*,
									false*/);
							fragment.add(calendar2);
						} else {
							fragment.add(new WebMarkupContainer("value2")
									.setVisible(false));
						}
						return fragment;
					}

					void addRestrictions(DetachedCriteria criteria) {
						if (filterHasValue()) {
							Object value = filterCriteria.getValue();
							switch (filterCriteria.getExpression()) {
							case EQ:
								criteria.add(Restrictions.eq(getNameText(),
										value));
								break;
							case NOT_EQ:
								criteria.add(Restrictions.not(Restrictions.eq(
										getNameText(), value)));
								break;
							case GT:
								criteria.add(Restrictions.gt(getNameText(),
										value));
								break;
							case LT:
								criteria.add(Restrictions.lt(getNameText(),
										value));
								break;
							case BETWEEN:
								criteria.add(Restrictions.gt(getNameText(),
										value));
								criteria.add(Restrictions.lt(getNameText(),
										filterCriteria.getValue2()));
								break;
							default:
							}
						}
					}

					String getAsQueryString() {
						return getQueryStringFromValue(Date.class);
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setValueFromQueryString(s, Date.class);
					}
				};
				// ==============================================================
			case SPACE:
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(IN);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						Fragment fragment = new Fragment("fragParent",
								"multiSelect", container);
						List<Space> spaces = new ArrayList(user.getSpaces());
						CheckBoxMultipleChoice choice = new CheckBoxMultipleChoice(
								"values", spaces, new IChoiceRenderer() {
									public Object getDisplayValue(Object o) {
										return ((Space) o).getName();
									}

									public String getIdValue(Object o, int i) {
										return ((Space) o).getId() + "";
									}
								});
						fragment.add(choice);
						choice.setModel(new PropertyModel(filterCriteria,
								"values"));
						return fragment;
					}

					void addRestrictions(DetachedCriteria criteria) {
						// already handled space as special case, see
						// ItemSearch#getSelectedSpaces()
					}

					String getAsQueryString() {
						return getQueryStringFromSpaceList();
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setSpaceListFromQueryString(s, user, calipsoService);
					}
				};

				// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

			case TOTAL_RESPONSE_TIME:
			case TIME_FROM_CREATION_TO_FIRST_REPLY:
			case TIME_FROM_CREATION_TO_CLOSE:
				return new ProcessorCustom() {
					private Class validationClass = null;

					List<Expression> getValidFilterExpressions() {
						return getAsList(BETWEEN, GT, LT);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						Fragment fragment = new Fragment("fragParent",
								"effortField", container);
						WebMarkupContainer days = new WebMarkupContainer("days");

						if (name.equals(TOTAL_RESPONSE_TIME)) {
							validationClass = Double.class;
						} else {
							validationClass = Long.class;
						}

						TextField textField = new TextField("value",
								validationClass);
						days.add(textField);
						// textField.setModel(new PropertyModel(this,
						// "filterCriteria.value"));
						textField.setModel(new PropertyModel(filterCriteria,
								"value"));
						fragment.add(days);

						WebMarkupContainer days2 = new WebMarkupContainer(
								"days2");
						if (filterCriteria.getExpression() == BETWEEN) {
							TextField textField2 = new TextField("value2",
									validationClass);
							days2.add(textField2);
							// textField2.setModel(new PropertyModel(this,
							// "filterCriteria.value2"));
							textField2.setModel(new PropertyModel(
									filterCriteria, "value2"));
						}// if
						else {
							fragment.add(new WebMarkupContainer("value2")
									.setVisible(false));
							days2.setVisible(false);
						}// else
						fragment.add(days2);
						// fragment.setVisible(isVisible());
						return fragment;
					}

					void addRestrictions(DetachedCriteria criteria) {
					}

					void addRestrictions(CustomCriteria customCriteria) {
						if (filterHasValue()) {
							String criteriaObjectClass = null;
							if (validationClass != null) {
								criteriaObjectClass = validationClass
										.getCanonicalName();
							}// if

							Object value = filterCriteria.getValue();
							Object value2 = filterCriteria.getValue2();

							Double v1 = new Double(value.toString());
							value = v1 * 86400;// In Seconds
							switch (filterCriteria.getExpression()) {
							case GT:
								customCriteria.add(name.getText(),
										CustomCriteria.GT, value.toString(),
										criteriaObjectClass);
								break;
							case LT:
								customCriteria.add(name.getText(),
										CustomCriteria.LT, value.toString(),
										criteriaObjectClass);
								break;
							case BETWEEN:
								Double v2 = new Double(value2.toString());
								value2 = v2 * 86400; // In Seconds
								customCriteria.add(name.getText(),
										CustomCriteria.GET, value.toString(),
										criteriaObjectClass);
								customCriteria.add(name.getText(),
										CustomCriteria.LET, value2.toString(),
										criteriaObjectClass);
								break;
							default:
							}
						}
					}

					String getAsQueryString() {
						return getQueryStringFromValue(Long.class);
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setValueFromQueryString(s, Long.class);
					}
				};

				// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

			case PLANNED_EFFORT:
				return new Processor() {
					List<Expression> getValidFilterExpressions() {
						return getAsList(EQ, NOT_EQ, GT, LT, BETWEEN);
					}

					Fragment getFilterUiFragment(MarkupContainer container,
							User user, Space space, CalipsoService calipsoService) {
						Fragment fragment = new Fragment("fragParent",
								"effortField2", container);
						EffortField effortField = new EffortField("value",
								new PropertyModel(filterCriteria, "value"),
								false);
						fragment.add(effortField);
						if (filterCriteria.getExpression() == BETWEEN) {
							EffortField effortField2 = new EffortField(
									"value2", new PropertyModel(filterCriteria,
											"value2"), false);
							fragment.add(new WebMarkupContainer("and"));
							fragment.add(effortField2);
						} else {
							fragment.add(new WebMarkupContainer("and")
									.setVisible(false));
							fragment.add(new WebMarkupContainer("value2")
									.setVisible(false));
						}
						return fragment;
					}

					void addRestrictions(DetachedCriteria criteria) {
						if (filterHasValue()) {
							Object value = filterCriteria.getValue();
							switch (filterCriteria.getExpression()) {
							case EQ:
								criteria.add(Restrictions.eq(getNameText(),
										value));
								break;
							case NOT_EQ:
								criteria.add(Restrictions.not(Restrictions.eq(
										getNameText(), value)));
								break;
							case GT:
								criteria.add(Restrictions.gt(getNameText(),
										value));
								break;
							case LT:
								criteria.add(Restrictions.lt(getNameText(),
										value));
								break;
							case BETWEEN:
								criteria.add(Restrictions.gt(getNameText(),
										value));
								criteria.add(Restrictions.lt(getNameText(),
										filterCriteria.getValue2()));
								break;
							default:
							}
						}
					}

					String getAsQueryString() {
						return getQueryStringFromValue(Integer.class);
					}

					void loadFromQueryString(String s, User user, CalipsoService calipsoService) {
						setValueFromQueryString(s, Integer.class);
					}
				};

				// ==============================================================
			default:
				throw new RuntimeException("Unknown Column Heading " + name);
			}
		}
	}

	private List<Expression> getAsList(Expression... expressions) {
		List<Expression> list = new ArrayList<Expression>();
		for (Expression e : expressions) {
			list.add(e);
		}
		return list;
	}

	private Fragment getTextFieldFragment(MarkupContainer container) {
		Fragment fragment = new Fragment("fragParent", "textField", container);
		TextField textField = new TextField("value", String.class);
		textField.setModel(new PropertyModel(filterCriteria, "value"));
		fragment.add(textField);
		fragment.add(new WebMarkupContainer("value2").setVisible(false));
		return fragment;
	}

	private boolean filterHasValueList() {
		if (filterCriteria.getExpression() != null
				&& filterCriteria.getValues() != null
				&& filterCriteria.getValues().size() > 0) {
			return true;
		}
		return false;
	}

	private boolean filterHasValue() {
		Object value = filterCriteria.getValue();
		if (filterCriteria.getExpression() != null && value != null
				&& value.toString().trim().length() > 0) {
			return true;
		}
		return false;
	}

	private boolean filterIsNullExpression() {
		return (filterCriteria.getExpression() != null)
				&& (filterCriteria.getExpression()
						.equals(FilterCriteria.Expression.IS_NULL));
	}

	private String prependExpression(String s) {
		if (s.equals("")) {
			return filterCriteria.getExpression().getKey();
		} else {
			return filterCriteria.getExpression().getKey() + "_" + s;
		}
	}

	private String getQueryStringFromValueList() {
		if (!filterHasValueList()) {
			return null;
		}
		String temp = "";
		for (Object o : filterCriteria.getValues()) {
			if (temp.length() > 0) {
				temp = temp + "_";
			}
			if(o instanceof CustomAttributeLookupValue){
				temp = temp + ((CustomAttributeLookupValue)o).getId();
			}
			else{
				temp = temp + o;
			}
			
		}
		return prependExpression(temp);
	}

	private String getQueryStringFromValue(Class clazz) {
		if (!filterHasValue()) {
			return null;
		}
		String temp = "";
		if (clazz.equals(Date.class)) {
			temp = DateUtils.format((Date) filterCriteria.getValue());
			if (filterCriteria.getValue2() != null) {
				temp = temp + "_"
						+ DateUtils.format((Date) filterCriteria.getValue2());
			}
		} else {
			temp = filterCriteria.getValue() + "";
			if (filterCriteria.getValue2() != null) {
				temp = temp + "_" + filterCriteria.getValue2();
			}
		}
		return prependExpression(temp);
	}

	// TODO refactor code duplication
	private String getQueryStringFromUserList() {
		boolean hasValueList = filterHasValueList();
		Expression expression = filterCriteria.getExpression();

		if (!hasValueList && expression == null) {
			return null;
		}
		String temp = "";
		if (hasValueList) {
			for (User u : (List<User>) filterCriteria.getValues()) {
				if (temp.length() > 0) {
					temp = temp + "_";
				}
				temp = temp + u.getId();
			}
		}
		return prependExpression(temp);
	}

	// TODO refactor code duplication
	private String getQueryStringFromSpaceList() {
		if (!filterHasValueList()) {
			return null;
		}
		String temp = "";
		for (Space s : (List<Space>) filterCriteria.getValues()) {
			if (temp.length() > 0) {
				temp = temp + "_";
			}
			temp = temp + s.getId();
		}
		return prependExpression(temp);
	}

	// TODO refactor code duplication
	private String getQueryStringFromAssetTypeList() {
		if (!filterHasValueList()) {
			return null;
		}
		String temp = "";
		for (AssetType a : (List<AssetType>) filterCriteria.getValues()) {
			if (temp.length() > 0) {
				temp = temp + "_";
			}
			temp = temp + a.getId();
		}
		return prependExpression(temp);
	}

	private List<String> setExpressionAndGetRemainingTokens(String s) {
		String[] tokens = s.split("_");
		filterCriteria.setExpression(FilterCriteria
				.convertToExpression(tokens[0]));
		List<String> remainingTokens = new ArrayList<String>();
		// ignore first token, this has been parsed as Expression above
		for (int i = 1; i < tokens.length; i++) {
			remainingTokens.add(tokens[i]);
		}
		return remainingTokens;
	}

	private void setValueListFromQueryString(String raw) {
		filterCriteria.setValues(setExpressionAndGetRemainingTokens(raw));
	}

	// TODO refactor with more methods in filtercriteria
	private void setValueFromQueryString(String raw, Class clazz) {
		List<String> tokens = setExpressionAndGetRemainingTokens(raw);
		String v1 = tokens.get(0);
		String v2 = tokens.size() > 1 ? tokens.get(1) : null;
		if (clazz.equals(Double.class)) {
			filterCriteria.setValue(new Double(v1));
			if (v2 != null) {
				filterCriteria.setValue2(new Double(v2));
			}
		} else if (clazz.equals(Long.class)) {
			filterCriteria.setValue(new Long(v1));
			if (v2 != null) {
				filterCriteria.setValue2(new Long(v2));
			}
		} else if (clazz.equals(Integer.class)) {
			filterCriteria.setValue(new Integer(v1));
			if (v2 != null) {
				filterCriteria.setValue2(new Integer(v2));
			}
		} else if (clazz.equals(Date.class)) {
			filterCriteria.setValue(DateUtils.convert(v1));
			if (v2 != null) {
				filterCriteria.setValue2(DateUtils.convert(v2));
			}
		} else { // String
			filterCriteria.setValue(v1);
			if (v2 != null) {
				filterCriteria.setValue2(v2);
			}
		}

	}

	private void setUserListFromQueryString(String raw, CalipsoService calipsoService) {
		List<String> tokens = setExpressionAndGetRemainingTokens(raw);
		if (tokens.size() != 0) {
			List<User> users = calipsoService
					.findUsersWhereIdIn(getAsListOfLong(tokens));
			filterCriteria.setValues(users);
		}
	}

	private void setSpaceListFromQueryString(String raw, User user, CalipsoService calipsoService) {
		List<String> tokens = setExpressionAndGetRemainingTokens(raw);
		List<Space> temp = calipsoService.findSpacesWhereIdIn(getAsListOfLong(tokens));
		// for security, prevent URL spoofing to show spaces not allocated to
		// user
		List<Space> spaces = new ArrayList<Space>();
		for (Space s : temp) {
			if (user.isAllocatedToSpace(s.getId())) {
				spaces.add(s);
			}
		}
		filterCriteria.setValues(spaces);
	}

	private void setAssetTypeListFromQueryString(String raw, User user,
			CalipsoService calipsoService) {
		List<String> tokens = setExpressionAndGetRemainingTokens(raw);

		// AssetTypeSearch assetTypeSearch = new AssetTypeSearch()
		List<AssetType> temp = calipsoService
				.findAssetTypesWhereIdIn(getAsListOfLong(tokens));
		// calipsoService.findSpacesWhereIdIn(getAsListOfLong(tokens));
		// for security, prevent URL spoofing to show spaces not allocated to
		// user
		List<AssetType> assetTypes = new ArrayList<AssetType>();
		for (AssetType a : temp) {
			if (user.isAllocatedToSpace(a.getId())) {
				assetTypes.add(a);
			}
		}
		filterCriteria.setValues(assetTypes);
	}

	private void setStatusListFromQueryString(String raw) {
		List<String> tokens = setExpressionAndGetRemainingTokens(raw);
		List<Integer> statuses = new ArrayList<Integer>();
		for (String s : tokens) {
			statuses.add(new Integer(s));
		}
		filterCriteria.setValues(statuses);
	}

	private List<Long> getAsListOfLong(List<String> tokens) {
		List<Long> ids = new ArrayList<Long>();
		for (String s : tokens) {
			ids.add(new Long(s));
		}
		return ids;
	}

	/* custom accessor */
	public void setName(String nameAsString) {
		name = convertToName(nameAsString);
	}

	public String getNameText() {
		if (isField()) {
			return field.getName().getText();
		}
		return name.text;
	}

	// ==========================================================================

	public Name getName() {
		return name;
	}

	public Field getField() {
		return field;
	}

	public String getLabel() {
		return label;
	}

	public FilterCriteria getFilterCriteria() {
		return filterCriteria;
	}

	public void setFilterCriteria(FilterCriteria filterCriteria) {
		this.filterCriteria = filterCriteria;
	}

	public boolean isVisible() {
		return visible;
	}

	public ColumnHeading setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public boolean isDbField() {
		return isDbField;
	}

	public void setDbField(boolean isDbField) {
		this.isDbField = isDbField;
	}

	public boolean isVisibleCriterium() {
		return visibleCriterium;
	}

	public void setVisibleCriterium(boolean visibleCriterium) {
		this.visibleCriterium = visibleCriterium;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ColumnHeading)) {
			return false;
		}
		final ColumnHeading ch = (ColumnHeading) o;
		return ch.getName().equals(name);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name [").append(name);
		sb.append("]; filterCriteria [").append(filterCriteria);
		sb.append("]");
		return sb.toString();
	}
}