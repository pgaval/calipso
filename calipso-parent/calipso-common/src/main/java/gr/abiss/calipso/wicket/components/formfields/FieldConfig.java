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

package gr.abiss.calipso.wicket.components.formfields;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.dom4j.io.DOMReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;

public class FieldConfig implements Serializable{

	public static final String TYPE_STRING = "string";
	public static final String TYPE_DECIMAL = "decimal";
	public static final String TYPE_INTEGER = "integer";
	public static final String TYPE_DATE = "date";
	public static final String SUMMARY_TOTAL = "total";
	public static final String SUMMARY_AVERAGE = "average";
	
	private static final XStream xstream = new XStream();
	static{
		xstream.alias("field-config", FieldConfig.class);
		xstream.useAttributeFor(FieldConfig.class, "classname");
		xstream.useAttributeFor(FieldConfig.class, "style");
		xstream.useAttributeFor(FieldConfig.class, "type");
		xstream.useAttributeFor(FieldConfig.class, "format");
		xstream.useAttributeFor(FieldConfig.class, "summary");
		xstream.useAttributeFor(FieldConfig.class, "min");
		xstream.useAttributeFor(FieldConfig.class, "max");
		xstream.useAttributeFor(FieldConfig.class, "optional");
		xstream.useAttributeFor(FieldConfig.class, "showHelpInPdf");
		xstream.aliasAttribute(FieldConfig.class, "showHelpInPdf", "show-help-in-pdf");
	}

	public static FieldConfig fromXML(String xml){
		FieldConfig fc = null;
		if(StringUtils.isNotBlank(xml) && xml.startsWith("<")){
			return xml != null ? (FieldConfig) xstream.fromXML(xml) : null;
		}
		return fc;
	}
	
	public static String toXML(FieldConfig fieldConfig){
		return fieldConfig != null? xstream.toXML(fieldConfig) : null;
	}
	
	public static org.dom4j.Element asDom4j(FieldConfig fieldConfig){
		org.dom4j.Element elem = null;
		try {
			if(fieldConfig != null){
				Document doc = DocumentBuilderFactory
				    .newInstance()
				    .newDocumentBuilder()
				    .parse(new ByteArrayInputStream(toXML(fieldConfig).getBytes("UTF-8")));

				DOMReader reader = new DOMReader();
			
				org.dom4j.Document document = reader.read(doc);
				elem = document.getRootElement();
			}
			
		} catch (Exception e) {
			new RuntimeException(e);
		}
		return elem;
	}
	public static FieldConfig getFallBackFieldConfig(){
		return new FieldConfig(null, null);
	}
	
	
	private List<FieldConfig> subFieldConfigs = new LinkedList<FieldConfig>();
	private String labelKey;
	private String helpKey;
	private Integer size;
	private Integer maxLength;
	private String validationExpression;
	private String style;
	private String totalsLineFunction = null;
	private String classname;
	private String type = "string";
	private String summary = null;
	private String format = null;
	private String min = "0";
	private String max = null;
	boolean optional = false;
	
	private boolean showHelpInPdf = false;

	public FieldConfig(String labelKey, Integer size,
			String validationExpression, String helpKey) {
		this(labelKey, size);
		this.validationExpression = validationExpression;
		this.helpKey = helpKey;
	}

	public FieldConfig(String labelKey, Integer size) {
		this(labelKey);
		this.size = size;
	}

	public FieldConfig(String labelKey) {
		this.labelKey = labelKey;
	}

	public List<FieldConfig> getSubFieldConfigs() {
		return subFieldConfigs;
	}

	public void setSubFieldConfigs(List<FieldConfig> subFieldConfigs) {
		this.subFieldConfigs = subFieldConfigs;
	}

	public String getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}

	public String getHelpKey() {
		return helpKey;
	}

	public void setHelpKey(String helpKey) {
		this.helpKey = helpKey;
	}

	public String getValidationExpression() {
		return validationExpression;
	}

	public void setValidationExpression(String validationExpression) {
		this.validationExpression = validationExpression;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getTotalsLineFunction() {
		return totalsLineFunction;
	}

	public void setTotalsLineFunction(String addline) {
		this.totalsLineFunction = addline;
	}

	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public boolean isShowHelpInPdf() {
		return showHelpInPdf;
	}
	public void setShowHelpInPdf(boolean showHelpInPdf) {
		this.showHelpInPdf = showHelpInPdf;
	}
	
	public boolean isNumberType(){
		boolean isNumber = false;
		if(StringUtils.isNotBlank(this.type) 
				&& (this.type.equalsIgnoreCase(TYPE_DECIMAL) 
						|| this.type.equalsIgnoreCase(TYPE_INTEGER))){
			isNumber = true;
		}
		return isNumber;
	}
	public void addSubFieldConfig(FieldConfig config) {
		if (this.subFieldConfigs == null) {
			this.subFieldConfigs = new LinkedList<FieldConfig>();
		}
		this.subFieldConfigs.add(config);
	}

}