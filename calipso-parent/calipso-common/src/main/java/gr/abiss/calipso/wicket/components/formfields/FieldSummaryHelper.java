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

import static gr.abiss.calipso.wicket.components.formfields.FieldConfig.SUMMARY_AVERAGE;
import static gr.abiss.calipso.wicket.components.formfields.FieldConfig.SUMMARY_TOTAL;
import static gr.abiss.calipso.wicket.components.formfields.FieldConfig.TYPE_DECIMAL;
import static gr.abiss.calipso.wicket.components.formfields.FieldConfig.TYPE_INTEGER;
import static gr.abiss.calipso.wicket.components.formfields.FieldConfig.TYPE_DATE;

import java.lang.reflect.InvocationTargetException;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.MaximumValidator;
import org.apache.wicket.validation.validator.MinimumValidator;


public class FieldSummaryHelper{
	private static final Logger logger = Logger.getLogger(FieldSummaryHelper.class);
	private static final Double ZERO = new Double(0);
	private static final Map<String, String> TYPE_FORMATS = new HashMap<String, String>();
	static{
		TYPE_FORMATS.put(TYPE_DECIMAL, "java.text.DecimalFormat");
		TYPE_FORMATS.put(TYPE_INTEGER, "java.text.DecimalFormat");
		TYPE_FORMATS.put(TYPE_DATE, "java.text.SimpleDateFormat");
	}

	private HashMap<String, FieldSummaryHelper> helpers;
	private List<FieldSummaryHelper> helpersList;

	private String type = "string";
	private String label = null;
	private String summary = null;
	private String min = null;
	private String max = null;
	private Object summaryObject = null;
	private int summaryEntriesCount = 0;
	private Format format = null;
	private List<IValidator> validators = new LinkedList<IValidator>();
	
	private FieldSummaryHelper(){
		
	}
	
	public FieldSummaryHelper(FieldConfig fieldConfig){
		this.type = fieldConfig.getType();
		this.label = fieldConfig.getLabelKey();
		this.summary = fieldConfig.getSummary();
		this.min = fieldConfig.getMin();
		this.max = fieldConfig.getMax();
		if(StringUtils.isNotBlank(type)){
			// confiogure min/max
			if(StringUtils.isNotBlank(min)){
				if(TYPE_DECIMAL.equalsIgnoreCase(type)){
					validators.add(new MinimumValidator<Double>(Double.parseDouble(min)));
				}
				if(TYPE_INTEGER.equalsIgnoreCase(type)){
					validators.add(new MinimumValidator<Integer>(Integer.parseInt(min)));
				}
			}
			if(StringUtils.isNotBlank(max)){
				if(TYPE_DECIMAL.equalsIgnoreCase(type)){
					validators.add(new MaximumValidator<Double>(Double.parseDouble(min)));
				}
				if(TYPE_INTEGER.equalsIgnoreCase(type)){
					validators.add(new MaximumValidator<Integer>(Integer.parseInt(min)));
				}
			}
			
			
			// configure formatting
			if(StringUtils.isNotBlank(fieldConfig.getFormat())){
				
				try {
					Class formatClass = Class.forName(TYPE_FORMATS.get(type));
					this.format = (Format) formatClass.getConstructor(String.class).newInstance(fieldConfig.getFormat());
				} catch (Exception e) {
					//throw new RuntimeException(e);
					logger.error("Failed configuring format for field config: "+fieldConfig.getLabelKey());
				}
			}
		}
		
		
		
		// configure summary
		if(StringUtils.isNotBlank(this.summary)){
			if(TYPE_DECIMAL.equalsIgnoreCase(this.type)
					|| TYPE_INTEGER.equalsIgnoreCase(this.type)){
				this.summaryObject = ZERO;	
			}
		}
		if(fieldConfig != null && CollectionUtils.isNotEmpty(fieldConfig.getSubFieldConfigs())){
			List<FieldConfig> subConfigs = fieldConfig.getSubFieldConfigs();
			helpers = new HashMap<String, FieldSummaryHelper>();
			helpersList = new ArrayList<FieldSummaryHelper>(subConfigs.size());
			for(FieldConfig subConfig : subConfigs){
				FieldSummaryHelper helper = new FieldSummaryHelper(subConfig);
				helpers.put(subConfig.getLabelKey(), helper);
				helpersList.add(helper);
			}
		}
	}

	public void updateSummary(FieldConfig subFieldConfig, String value) {
		FieldSummaryHelper helper = helpers.get(subFieldConfig.getLabelKey());
		helper.updateSummary(value);
	}
	public void updateSummary(int subFieldIndex, String value) {
		FieldSummaryHelper helper = helpersList.get(subFieldIndex);
		helper.updateSummary(value);
	}

	public void updateSummary(String value) {
		//logger.info("updateSummary, label: "+label+", sumary: "+summary+", type: "+type+", value: "+value);
		if(StringUtils.isNotBlank(summary) && StringUtils.isNotBlank(value)){
			if(TYPE_DECIMAL.equalsIgnoreCase(type)
					|| TYPE_INTEGER.equalsIgnoreCase(type)){
				Double doubleValue = Double.parseDouble(value);
				summaryObject = new Double(((Double) summaryObject) + doubleValue);
			}
//			else{
//				logger.info("updateSummary for "+label+"skipped as helper.type is invalid");
//			}
		}
//		else{
//			logger.info("updateSummary for "+label+"skipped calculating sumary as helper.summary or value is empty");
//		}
		summaryEntriesCount++;
	}

	public String getSummary(FieldConfig subFieldConfig){
		FieldSummaryHelper helper = helpers.get(subFieldConfig.getLabelKey());
		return helper.summary;
	}
	public String getCalculatedSummary(FieldConfig subFieldConfig){
		FieldSummaryHelper helper = helpers.get(subFieldConfig.getLabelKey());
		return helper.getCalculatedSummary();
	}
	public String getCalculatedSummary(int subFieldIndex){
		FieldSummaryHelper helper = helpersList.get(subFieldIndex);
		return helper.getCalculatedSummary();
	}

	public Object parse(String value, Locale locale){
		Object o = null;
		if(StringUtils.isNotEmpty(type)){
			try {
				if(type.equalsIgnoreCase(TYPE_INTEGER) 
						|| type.equalsIgnoreCase(TYPE_DECIMAL)){
					
						o = NumberFormat.getNumberInstance(locale).parseObject(value);
					
				}
				if(type.equalsIgnoreCase(TYPE_DATE)){
						o = this.format.parseObject(value);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(o == null){
			o = value;
		}
		logger.info("parse "+value +" returns " +o +" ("+o.getClass()+") with locale "+locale+" for type: "+type+" and label: "+this.label);
		return o;
	}

	public String parseFormat(FieldConfig subFieldConfig, String value, Locale locale){
		FieldSummaryHelper helper = helpers.get(subFieldConfig.getLabelKey());
		logger.info("parseFormat subFieldConfig, helper: "+helper);
		return helper.parseFormat(value, locale);
	}
	public String parseFormat(int subFieldIndex, String value, Locale locale){
		FieldSummaryHelper helper = helpersList.get(subFieldIndex);
		logger.info("parseFormat subFieldIndex:, helper: "+helper);
		return helper.parseFormat(value, locale);
	}

	public List<IValidator> getValidators(int subFieldIndex){
		FieldSummaryHelper helper = helpersList.get(subFieldIndex);
		return helper.getValidators();
	}
	public List<IValidator> getValidators(FieldConfig subFieldConfig){
		FieldSummaryHelper helper = helpers.get(subFieldConfig.getLabelKey());
		return helper.getValidators();
	}
	
	private String parseFormat(String value, Locale locale){
		return format(parse(value, locale));
	}
	
	private String format(Object value){
		String val;
		if(this.format == null || value instanceof String){
			logger.info("format skipped "+value+" for label"+this.label);
			val = value.toString();
		}
		else{
			val = this.format.format(value);
			logger.info("format formatted '"+value+"' to '"+val+"' using a "+this.format+" for label"+this.label);
		}
		logger.info("format "+value +" returns " +val+" for label"+this.label);
		return val;
	}
	
	private String getCalculatedSummary() {
		//logger.info("getCalculatedSummary, label: "+label+", sumary: "+summary+", type: "+type);
		String calculatedSummary = "";
		if(StringUtils.isNotBlank(summary)){
			if(TYPE_DECIMAL.equalsIgnoreCase(type)
					|| TYPE_INTEGER.equalsIgnoreCase(type)){
				Double total = (Double) summaryObject;
				if(ZERO.equals(total)){
					calculatedSummary = this.format(ZERO);
				}
				else if(SUMMARY_TOTAL.equalsIgnoreCase(summary)){
					calculatedSummary = this.format(total);
				}
				else if(SUMMARY_AVERAGE.equalsIgnoreCase(summary)){
					calculatedSummary = this.format(new Double(total.doubleValue()/summaryEntriesCount));
				}
			}
//			else{
//				logger.info("getCalculatedSummary for "+label+"skipped calculating sumary as helper.type is invalid");
//			}
		}
//		else{
//			logger.info("getCalculatedSummary for "+label+"skipped calculating sumary as helper.summary is empty");
//		}
		return calculatedSummary;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public int getSummaryEntriesCount() {
		return summaryEntriesCount;
	}

	public void setSummaryEntriesCount(int summaryEntriesCount) {
		this.summaryEntriesCount = summaryEntriesCount;
	}

	private List<IValidator> getValidators() {
		return validators;
	}

	private void setValidators(List<IValidator> validators) {
		this.validators = validators;
	}

	public String toString() {
	     return new ToStringBuilder(this).
	       append("label", label).
	       append("type", type).
	       append("format", format).
	       toString();
	   }
}