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

import static gr.abiss.calipso.Constants.FIELD;
import static gr.abiss.calipso.Constants.PRIORITY;
import static gr.abiss.calipso.Constants.FIELDTYPE;
import static gr.abiss.calipso.Constants.GROUP_ID;
import static gr.abiss.calipso.Constants.LABEL;
import static gr.abiss.calipso.Constants.NAME;
import static gr.abiss.calipso.Constants.OPTION;
import static gr.abiss.calipso.Constants.OPTIONAL;
import static gr.abiss.calipso.Constants.DEFAULT_VALUE;
import static gr.abiss.calipso.Constants.TRUE;
import static gr.abiss.calipso.Constants.VALIDATIONEXPR;
import static gr.abiss.calipso.Constants.VALUE;
import static gr.abiss.calipso.Constants.LINECOUNT;
import static gr.abiss.calipso.Constants.MULTIVALUE;
import gr.abiss.calipso.domain.StdFieldMask.Mask;
import gr.abiss.calipso.util.XmlUtils;
import gr.abiss.calipso.wicket.components.formfields.FieldConfig;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.dom4j.Element;

/**
 * <code>Metadata</code> is composited of Field elements that represent each of
 * the custom fields that may be used within an item
 */
public class Field implements Serializable {
    
	protected static final Logger logger = Logger.getLogger(Field.class);
	public static final String FIELD_TYPE_AUTOSUGGEST = "Auto-suggest text";
	public static final String FIELD_TYPE_DROPDOWN = "Drop down menu";
	public static final String FIELD_TYPE_DROPDOWN_HIERARCHICAL = "Hierarchical drop down";
	public static final String FIELD_TYPE_SIMPLE_ATTACHEMENT = "attachment";
	public static final Long NO_VALIDATION_ID = new Long(1);
	private static final Map<String, Name> NAMES_MAP;

	// set up a static Map to resolve a String to our Field.Name enum value
	static {
		NAMES_MAP = new HashMap<String, Name>();
		for (Name n : Name.values()) {
			NAMES_MAP.put(n.text, n);
		}
		//logger.info("NAMES_MAP("+NAMES_MAP.size()+"): "+NAMES_MAP);
	}

	private Name name;
	private FieldConfig xmlConfig;
	private int priority = 0;
	private String label;
	private String groupId;
	private FieldGroup group;
	String organizationType;
	private Integer mask = Mask.OPTIONAL.getId();
	private String fieldType = new String(FIELD_TYPE_DROPDOWN); // by default
																// has this

	private Long validationExpressionId = NO_VALIDATION_ID;
	private ValidationExpression validationExpression;
	private boolean optional;
	private Short lineCount = 1;
	private Boolean multivalue = false;

	private Map<String, String> options;
	private ItemFieldCustomAttribute customAttribute;
	private String defaultValueExpression;

	public Field() {
		// zero arg constructor
	}									// value

	public Field(String fieldName) {
		this.setName(fieldName);
	}

	public Field(Name n) {
		this.setName(n);
	}

	public Field(Element e) {
		setName(e.attributeValue(NAME));
		label = e.attributeValue(LABEL);
		this.groupId = e.attributeValue(GROUP_ID);
		//logger.info("loaded field "+this.getName().getText()+" with group id: "+this.groupId);
		// TODO: we can use the same way to add a PK for database records or
		// drop-down options
		fieldType = e.attributeValue(FIELDTYPE);
		validationExpressionId = NumberUtils.toLong(e
				.attributeValue(VALIDATIONEXPR));
		if (e.attribute(OPTIONAL) != null) {
			optional = BooleanUtils.toBoolean(e.attributeValue(OPTIONAL));
		}
		if (e.attribute(PRIORITY) != null) {
			priority = NumberUtils.toInt(e.attributeValue(PRIORITY));
		}
		if(this.getName().isFreeText()){
			if (e.attribute(MULTIVALUE) != null) {
				this.multivalue = BooleanUtils.toBoolean(e.attributeValue(MULTIVALUE));
			}
			if (e.attribute(LINECOUNT) != null) {
				this.lineCount = Short.parseShort(e.attributeValue(LINECOUNT));
			}
		}
		if(e.attribute(DEFAULT_VALUE) != null){
			this.defaultValueExpression = e.attributeValue(DEFAULT_VALUE);
		}
		if(e.element("field-config") != null){
			this.xmlConfig = FieldConfig.fromXML(e.element("field-config").asXML());
		}
		// TODO: remove this, we should only load options from database instead of tree
		for (Object o : e.elements(OPTION)) {
			addOption((Element) o);
		}
	}

	/**
	 * Resolve a String to a valid enum value for Field.Name
	 */
	public static Name convertToName(String text) {
		Name n = NAMES_MAP.get(text);
		if (n == null) {
			throw new RuntimeException("Bad name " + text);
		}
		return n;
	}

	/**
	 * test if a given string is a valid field name
	 */
	public static boolean isValidName(String text) {
		return NAMES_MAP.containsKey(text);
	}

	/**
	 * @return the fieldType
	 */
	public String getFieldType() {
		return fieldType;
	}

	/**
	 * @param fieldType
	 *            the fieldType to set
	 */
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * the names that are used for the custom fields in the outside world - e.g.
	 * the XML representation of the metadata that is persisted to the database
	 */
	public enum Name {
		// name (type, text)
		CUS_INT_01(3, "cusInt01"), 
		CUS_INT_02(3, "cusInt02"), 
		CUS_INT_03(3, "cusInt03"), 
		CUS_INT_04(3, "cusInt04"), 
		CUS_INT_05(3, "cusInt05"), 
		CUS_INT_06(3, "cusInt06"), 
		CUS_INT_07(3, "cusInt07"), 
		CUS_INT_08(3, "cusInt08"), 
		CUS_INT_09(3, "cusInt09"), 
		CUS_INT_10(3, "cusInt10"), 
		CUS_INT_11(3, "cusInt11"), 
		CUS_INT_12(3, "cusInt12"), 
		CUS_INT_13(3, "cusInt13"), 
		CUS_INT_14(3, "cusInt14"), 
		CUS_INT_15(3, "cusInt15"), 
		CUS_INT_16(3, "cusInt16"), 
		CUS_INT_17(3, "cusInt17"), 
		CUS_INT_18(3, "cusInt18"), 
		CUS_INT_19(3, "cusInt19"), 
		CUS_INT_20(3, "cusInt20"), 
		CUS_INT_21(3, "cusInt21"), 
		CUS_INT_22(3, "cusInt22"), 
		CUS_INT_23(3, "cusInt23"), 
		CUS_INT_24(3, "cusInt24"), 
		CUS_INT_25(3, "cusInt25"), 
		CUS_INT_26(3, "cusInt26"), 
		CUS_INT_27(3, "cusInt27"), 
		CUS_INT_28(3, "cusInt28"), 
		CUS_INT_29(3, "cusInt29"), 
		CUS_INT_30(3, "cusInt30"), 
		CUS_INT_31(3, "cusInt31"), 
		CUS_INT_32(3, "cusInt32"), 
		CUS_INT_33(3, "cusInt33"), 
		CUS_INT_34(3, "cusInt34"), 
		CUS_INT_35(3, "cusInt35"), 
		CUS_INT_36(3, "cusInt36"), 
		CUS_INT_37(3, "cusInt37"), 
		CUS_INT_38(3, "cusInt38"), 
		CUS_INT_39(3, "cusInt39"), 
		CUS_INT_40(3, "cusInt40"), 
		
		CUS_DBL_01(4, "cusDbl01"), 
		CUS_DBL_02(4, "cusDbl02"), 
		CUS_DBL_03(4, "cusDbl03"), 
		CUS_DBL_04(4, "cusDbl04"), 
		CUS_DBL_05(4, "cusDbl05"), 
		CUS_DBL_06(4, "cusDbl06"), 
		CUS_DBL_07(4, "cusDbl07"), 
		CUS_DBL_08(4, "cusDbl08"), 
		CUS_DBL_09(4, "cusDbl09"), 
		CUS_DBL_10(4, "cusDbl10"), 
		CUS_DBL_11(4, "cusDbl11"), 
		CUS_DBL_12(4, "cusDbl12"), 
		CUS_DBL_13(4, "cusDbl13"), 
		CUS_DBL_14(4, "cusDbl14"), 
		CUS_DBL_15(4, "cusDbl15"), 
		CUS_DBL_16(4, "cusDbl16"), 
		CUS_DBL_17(4, "cusDbl17"), 
		CUS_DBL_18(4, "cusDbl18"), 
		CUS_DBL_19(4, "cusDbl19"), 
		CUS_DBL_20(4, "cusDbl20"), 
		CUS_DBL_21(4, "cusDbl21"), 
		CUS_DBL_22(4, "cusDbl22"), 
		CUS_DBL_23(4, "cusDbl23"), 
		CUS_DBL_24(4, "cusDbl24"), 
		CUS_DBL_25(4, "cusDbl25"), 
		CUS_DBL_26(4, "cusDbl26"), 
		CUS_DBL_27(4, "cusDbl27"), 
		CUS_DBL_28(4, "cusDbl28"), 
		CUS_DBL_29(4, "cusDbl29"), 
		CUS_DBL_30(4, "cusDbl30"), 
		CUS_DBL_31(4, "cusDbl31"), 
		CUS_DBL_32(4, "cusDbl32"), 
		CUS_DBL_33(4, "cusDbl33"), 
		CUS_DBL_34(4, "cusDbl34"), 
		CUS_DBL_35(4, "cusDbl35"), 
		CUS_DBL_36(4, "cusDbl36"), 
		CUS_DBL_37(4, "cusDbl37"), 
		CUS_DBL_38(4, "cusDbl38"), 
		CUS_DBL_39(4, "cusDbl39"), 
		CUS_DBL_40(4, "cusDbl40"), 
		CUS_DBL_41(4, "cusDbl41"), 
		CUS_DBL_42(4, "cusDbl42"), 
		CUS_DBL_43(4, "cusDbl43"), 
		CUS_DBL_44(4, "cusDbl44"), 
		CUS_DBL_45(4, "cusDbl45"), 
		CUS_DBL_46(4, "cusDbl46"), 
		CUS_DBL_47(4, "cusDbl47"), 
		CUS_DBL_48(4, "cusDbl48"), 
		CUS_DBL_49(4, "cusDbl49"), 
		CUS_DBL_50(4, "cusDbl50"),
		
		CUS_STR_01(5, "cusStr01"), 
		CUS_STR_02(5, "cusStr02"), 
		CUS_STR_03(5, "cusStr03"), 
		CUS_STR_04(5, "cusStr04"), 
		CUS_STR_05(5, "cusStr05"), 
		CUS_STR_06(5, "cusStr06"), 
		CUS_STR_07(5, "cusStr07"), 
		CUS_STR_08(5, "cusStr08"), 
		CUS_STR_09(5, "cusStr09"), 
		CUS_STR_10(5, "cusStr10"), 
		CUS_STR_11(5, "cusStr11"), 
		CUS_STR_12(5, "cusStr12"), 
		CUS_STR_13(5, "cusStr13"), 
		CUS_STR_14(5, "cusStr14"), 
		CUS_STR_15(5, "cusStr15"), 
		CUS_STR_16(5, "cusStr16"), 
		CUS_STR_17(5, "cusStr17"), 
		CUS_STR_18(5, "cusStr18"), 
		CUS_STR_19(5, "cusStr19"), 
		CUS_STR_20(5, "cusStr20"), 
		CUS_STR_21(5, "cusStr21"), 
		CUS_STR_22(5, "cusStr22"), 
		CUS_STR_23(5, "cusStr23"), 
		CUS_STR_24(5, "cusStr24"), 
		CUS_STR_25(5, "cusStr25"), 
		CUS_STR_26(5, "cusStr26"), 
		CUS_STR_27(5, "cusStr27"), 
		CUS_STR_28(5, "cusStr28"), 
		CUS_STR_29(5, "cusStr29"), 
		CUS_STR_30(5, "cusStr30"), 
		CUS_STR_31(5, "cusStr31"), 
		CUS_STR_32(5, "cusStr32"), 
		CUS_STR_33(5, "cusStr33"), 
		CUS_STR_34(5, "cusStr34"), 
		CUS_STR_35(5, "cusStr35"), 
		CUS_STR_36(5, "cusStr36"), 
		CUS_STR_37(5, "cusStr37"), 
		CUS_STR_38(5, "cusStr38"), 
		CUS_STR_39(5, "cusStr39"), 
		CUS_STR_40(5, "cusStr40"), 
		CUS_STR_41(5, "cusStr41"), 
		CUS_STR_42(5, "cusStr42"), 
		CUS_STR_43(5, "cusStr43"), 
		CUS_STR_44(5, "cusStr44"), 
		CUS_STR_45(5, "cusStr45"), 
		CUS_STR_46(5, "cusStr46"), 
		CUS_STR_47(5, "cusStr47"), 
		CUS_STR_48(5, "cusStr48"), 
		CUS_STR_49(5, "cusStr49"), 
		CUS_STR_50(5, "cusStr50"), 
		CUS_STR_51(5, "cusStr51"), 
		CUS_STR_52(5, "cusStr52"), 
		CUS_STR_53(5, "cusStr53"), 
		CUS_STR_54(5, "cusStr54"), 
		CUS_STR_55(5, "cusStr55"), 
		CUS_STR_56(5, "cusStr56"), 
		CUS_STR_57(5, "cusStr57"), 
		CUS_STR_58(5, "cusStr58"), 
		CUS_STR_59(5, "cusStr59"), 
		CUS_STR_60(5, "cusStr60"), 
		CUS_STR_61(5, "cusStr61"), 
		CUS_STR_62(5, "cusStr62"), 
		CUS_STR_63(5, "cusStr63"), 
		CUS_STR_64(5, "cusStr64"), 
		CUS_STR_65(5, "cusStr65"), 
		CUS_STR_66(5, "cusStr66"), 
		CUS_STR_67(5, "cusStr67"), 
		CUS_STR_68(5, "cusStr68"), 
		CUS_STR_69(5, "cusStr69"), 
		CUS_STR_70(5, "cusStr70"), 
		CUS_STR_71(5, "cusStr71"), 
		CUS_STR_72(5, "cusStr72"), 
		CUS_STR_73(5, "cusStr73"), 
		CUS_STR_74(5, "cusStr74"), 
		CUS_STR_75(5, "cusStr75"), 
		CUS_STR_76(5, "cusStr76"), 
		CUS_STR_77(5, "cusStr77"), 
		CUS_STR_78(5, "cusStr78"), 
		CUS_STR_79(5, "cusStr79"), 
		CUS_STR_80(5, "cusStr80"), 
		CUS_COUNTRY_01(25, "cusCountry1"), 
		CUS_COUNTRY_02(25, "cusCountry2"), 
		CUS_TIM_01(6, "cusTim01"), 
		CUS_TIM_02(6, "cusTim02"), 
		CUS_TIM_03(6, "cusTim03"), 
		CUS_TIM_04(6, "cusTim04"), 
		CUS_TIM_05(6, "cusTim05"), 
		CUS_TIM_06(6, "cusTim06"), 
		CUS_TIM_07(6, "cusTim07"), 
		CUS_TIM_08(6, "cusTim08"), 
		CUS_TIM_09(6, "cusTim09"), 
		CUS_TIM_10(6, "cusTim10"), 
		CUS_TIM_11(6, "cusTim11"), 
		CUS_TIM_12(6, "cusTim12"), 
		CUS_TIM_13(6, "cusTim13"), 
		CUS_TIM_14(6, "cusTim14"), 
		CUS_TIM_15(6, "cusTim15"), 
		CUS_TIM_16(6, "cusTim16"), 
		CUS_TIM_17(6, "cusTim17"), 
		CUS_TIM_18(6, "cusTim18"), 
		CUS_TIM_19(6, "cusTim19"), 
		CUS_TIM_20(6, "cusTim20"), 
		ORGANIZATION(10, "organization"), 
		FILES1(11, "file1"), 
		FILES2(11, "file2"), 
		FILES3(11, "file3"), 
		FILES4(11, "file4"), 
		FILES5(11, "file5"), 
		USER(20, "user"), 
		ASSIGNABLE_SPACES(100, "assignableSpaces"),
		SIMPLE_ATTACHEMENT(200, FIELD_TYPE_SIMPLE_ATTACHEMENT);

		private final int type;
		private final String text;

		Name(int type, String text) {
			this.type = type;
			this.text = text;
		}

		public int getType() {
			return type;
		}

		public String getText() {
			return text;
		}

		public boolean isOptionsType() {
			return type < 4 || type == 200;
		}
		public boolean isDropDownType() {
			return type < 4;
		}

		public boolean isDecimalNumber() {
			return type == 4;
		}

		public boolean isFreeText() {
			return type == 5;
		}

		public boolean isDate() {
			return type == 6;
		}

		public boolean isEmail() {
			return type == 7;
		}

		public boolean isOrganization() {
			return type == 10;
		}

		public boolean isFile() {
			return type == 11 || type == 200;
		}

		public boolean isUser() {
			return type == 20;
		}

		public boolean isCountry() {
			return type == 25;
		}

		public boolean isAssignableSpaces() {
			return type == 100;
		}

		public boolean isSimpleAttachment() {
			return type == 100;
		}

		public String getDescription() {
			switch (type) {
			case 1:
			case 2:
			case 3:
				return "Drop Down List";
			case 33:
				return "Hierarchical Drop Down List";
			case 30:
				return "Auto-suggest";
			case 4:
				return "Decimal Number";
			case 5:
				return "Free Text Field";
			case 25:
				return "Country";
			case 6:
				return "Date Field";
			case 7:
				return "Email Field";
			case 10:
				return "Organization";
			case 11:
				return "File";
			case 20:
				return "User";
			case 100:
				return "Assignable Spaces";
			case 200:
				return "Attachment";
			default:
				throw new RuntimeException("Unknown type " + type);
			}
		}

		@Override
		public String toString() {
			return text;
		}
	}

	// ===================================================================


	public String getOrganizationType() {
		return this.organizationType;
	}

	public void setOrganizationType(String organizationType) {
		this.organizationType = organizationType;
	}

	/* append this object onto an existing XML document */
	public void addAsChildOf(Element parent) {
		Element e = parent.addElement(FIELD);
		copyTo(e);
	}

	/* marshal this object into a fresh new XML Element */
	public Element getAsElement() {
		Element e = XmlUtils.getNewElement(FIELD);
		copyTo(e);
		return e;
	}

	/* copy object values into an existing XML Element */
	private void copyTo(Element e) {
		// appending empty strings to create new objects for "clone" support
		e.addAttribute(NAME, name + "");
		e.addAttribute(PRIORITY, priority+"");
		
		if (this.groupId != null) {
			e.addAttribute(GROUP_ID, this.groupId);
		}
		if (fieldType != null) {
			e.addAttribute(FIELDTYPE, fieldType);
		}
		if (validationExpressionId != null
				&& validationExpressionId.longValue() != 0) {
			e.addAttribute(VALIDATIONEXPR, validationExpressionId + "");
		}
		if (this.defaultValueExpression != null) {
			e.addAttribute(DEFAULT_VALUE, this.defaultValueExpression);
		}
		if(this.getName().isFreeText()){
			e.addAttribute(LINECOUNT, this.lineCount.toString());
			e.addAttribute(MULTIVALUE, this.multivalue.toString());
		}
		e.addAttribute(LABEL, label + "");
		Element configElem =FieldConfig.asDom4j(this.xmlConfig);
		if(configElem != null){
			e.add(configElem);
		}
		if (optional) {
			e.addAttribute(OPTIONAL, TRUE);
		}
		if (options == null) {
			return;
		}
		for (Map.Entry<String, String> entry : options.entrySet()) {
			Element option = e.addElement(OPTION);
			option.addAttribute(VALUE, entry.getKey() + "");
			option.addText((String) entry.getValue() + "");
		}
	}

	public void addOption(String value) {
		if (options == null ||  options.size() == 0) {
			addOption("1", value);
			return;
		}
		addOption(options.size() + 1 + "", value);
	}

	public void addOption(String key, String value) {
		if (options == null) {
			options = new LinkedHashMap<String, String>();
		}
		options.put(key, value);
	}

	public void addOption(Element e) {
		String value = e.attributeValue(VALUE);
		if (value == null) {
			return;
		}
		String text = e.getTextTrim();
		if (text == null || text.equals("")) {
			return;
		}
		addOption(value, text);
	}

	public String getCustomValue(String key) {
		if (options == null || key == null) {
			return "";
		}
		String value = options.get(key);
		if (value == null) {
			return "";
		}
		return value;
	}

	public boolean hasOption(String value) {
		if (options == null) {
			return false;
		}
		return options.containsValue(value);
	}

	public Field getClone() {
		return new Field(getAsElement());
	}

	public void initOptions() {}

	// ===================================================================

	public Map<String, String> getOptions() {
		return options;
	}

	public void setOptions(Map<String, String> options) {
		this.options = options;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public FieldConfig getXmlConfig() {
		return xmlConfig;
	}

	public void setXmlConfig(FieldConfig xmlConfig) {
		this.xmlConfig = xmlConfig;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	/* custom accessor */
	public void setName(String nameAsString) {
		setName(convertToName(nameAsString));
	}

	public boolean isDropDownType() {
		return name.isDropDownType();
	}
	
	public boolean isDateType() {
		return name.isDate();
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	/**
	 * @return the validationExpressionId
	 */
	public Long getValidationExpressionId() {
		return validationExpressionId;
	}

	/**
	 * @param validationExpressionId
	 *            the validationExpressionId to set
	 */
	public void setValidationExpressionId(Long validationExpressionId) {
		this.validationExpressionId = validationExpressionId;
	}

	/**
	 * @param validationExpression
	 *            the validationExpression to set
	 */
	public void setValidationExpression(
			ValidationExpression validationExpression) {
		this.validationExpression = validationExpression;
		setValidationExpressionId(this.validationExpression.getId());
	}

	/**
	 * @return the validationExpression
	 */
	public ValidationExpression getValidationExpression() {
		return validationExpression;
	}

	/**
	 * @return the mask of the field
	 */
	public Integer getMask() {
		return mask;
	}

	/**
	 * @param mask
	 *            the mask of the field
	 */
	public void setMask(Integer mask) {
		this.mask = mask;
	}

	public int hashCode() {
		return new HashCodeBuilder(11, 41).append(this.name).append(this.label)
				.append(this.fieldType).append(this.organizationType)
				.append(this.validationExpressionId)
				.append(this.validationExpression).append(this.optional)
				.append(this.options).toHashCode();
	}

	public String toString() {
		return new ToStringBuilder(this).append("name", name)
				.append("label", this.label)
				.append("fieldType", this.fieldType)
				.append("organizationType", this.organizationType)
				.append("validationExpressionId", this.validationExpressionId)
				.append("validationExpression", this.validationExpression)
				.append("optional", this.optional)
				.append("options", this.options).toString();
	}

	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o == this) { return true; }
		if (o.getClass() != getClass()) {
			return false;
		}
		Field other = (Field) o;
		return new EqualsBuilder()
				.append(this.name, other.name)
				/*
				.append(this.label, other.label)
				.append(this.fieldType, other.fieldType)
				.append(this.organizationType, other.organizationType)
				.append(this.validationExpressionId,
						other.validationExpressionId)
				.append(this.validationExpression, other.validationExpression)
				.append(this.optional, other.optional)
				.append(this.options, other.options)
				 */
				.isEquals();
	}

	public void setCustomAttribute(ItemFieldCustomAttribute customAttribute) {
		this.customAttribute = customAttribute;
	}

	public ItemFieldCustomAttribute getCustomAttribute() {
		// TODO Auto-generated method stub
		return this.customAttribute;
	}

	public String getDefaultValueExpression() {
		return defaultValueExpression;
	}

	public void setDefaultValueExpression(String defaultValueExpression) {
		this.defaultValueExpression = defaultValueExpression;
	}

	public Short getLineCount() {
		return lineCount;
	}

	public void setLineCount(Short lineCount) {
		this.lineCount = lineCount;
	}

	public Boolean isMultivalue() {
		return multivalue;
	}

	public void setMultivalue(Boolean multivalue) {
		this.multivalue = multivalue;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public FieldGroup getGroup() {
		return group;
	}

	/**
	 * Used by SpacefieldFormPanel
	 * @param group
	 */
	public void setGroup(FieldGroup group) {
		this.group = group;
		if(group != null){
			this.groupId = group.getId();
		}
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}
