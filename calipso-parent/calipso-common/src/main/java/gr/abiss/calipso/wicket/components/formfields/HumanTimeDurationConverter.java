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

import gr.abiss.calipso.util.HumanTime;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.markup.html.form.AbstractTextComponent;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.convert.converter.AbstractConverter;

/**
 * Converts HumanTime strings like "2d 5h" to milliseconds (Long) and back
 * @see gr.abiss.calipso.util.HumanTime
 */
public class HumanTimeDurationConverter extends AbstractConverter{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(HumanTimeDurationConverter.class);
	

	/**
	 * @param value
	 * @return
	 */
	public static String denormalize(String stringValue, Localizer localizer, Component component) {
		if(StringUtils.isNotBlank(stringValue)){ 
			// TODO: optimize using a single regexp to replace?
			stringValue = stringValue
				.replaceAll(" ms", localizer.getString("milliseconds", component))
				.replaceAll(" s", localizer.getString("seconds", component))
				.replaceAll(" m", localizer.getString("minutes", component))
				.replaceAll(" h", localizer.getString("hours", component))
				.replaceAll(" d", localizer.getString("days", component))
				.replaceAll(" y", localizer.getString("years", component));
		}
		return stringValue;
	}
	

	AbstractTextComponent field = null;

	Pattern whitespace = Pattern.compile("\\s+");

	private HumanTimeDurationConverter(){
		super();
	}
	
	/**
	 * @param localizer
	 */
	public HumanTimeDurationConverter(AbstractTextComponent field){
		this();
		this.field = field;
	}
	
	
	
	/**
	 * @see org.apache.wicket.util.convert.IConverter#convertToObject(java.lang.String,Locale)
	 */
	public Object convertToObject(String value, Locale locale){
		if (StringUtils.isBlank(value)){
			return null;
		}
		else{
			//logger.debug("Got string to convert:"+value);
			value = normalize(value);
			//logger.debug("Normilized string:"+value);
			Long longValue = new Long(HumanTime.eval(value).getDelta());
			//logger.debug("Converted to Long:"+longValue);
			return longValue;
		}
	}


	/**
	 * @see org.apache.wicket.util.convert.IConverter#convertToString(Object, java.util.Locale)
	 */
	public String convertToString(final Object value, Locale locale){
		String stringValue = null;
		if(value != null){
			stringValue = HumanTime.exactly(((Long)value).longValue());
			//logger.debug("Got object, string to convert:"+stringValue);
			stringValue = denormalize(stringValue);
			//logger.debug("Converted object string:"+stringValue);
		}
		return stringValue;
	}

	/**
	 * @param value
	 * @return
	 */
	private String denormalize(String stringValue) {
		if(StringUtils.isNotBlank(stringValue)){ 
			// TODO: optimize using a single regexp to replace?
			Localizer localizer = this.field.getLocalizer();
			stringValue = stringValue
				.replaceAll(" ms", localizer.getString("milliseconds", this.field))
				.replaceAll(" s", localizer.getString("seconds", this.field))
				.replaceAll(" m", localizer.getString("minutes", this.field))
				.replaceAll(" h", localizer.getString("hours", this.field))
				.replaceAll(" d", localizer.getString("days", this.field))
				.replaceAll(" y", localizer.getString("years", this.field));
		}
		return stringValue;
	}

	/**
	 * @param value
	 * @return
	 */
	private String normalize(String value) {
		if(value != null){
			// replace any commas with a space since that will be used as a separator
			// remove extra spaces
			value = removeSuperfluousWhitespace(value.toLowerCase().replaceAll(",", " "));
			
			Localizer localizer = this.field.getLocalizer();
			// TODO: optimize using a single regexp to replace?
			String[][] replacementKeys = new String[][]{
					{"ms","replacements.milliseconds"}, 
					{"s", "replacements.seconds"}, 
					{"m", "replacements.minutes"},  
					{"h", "replacements.hours"}, 
					{"d", "replacements.days"}, 
					{"y", "replacements.years"}};
			for(int i=0;i<replacementKeys.length;i++){
				String target =  replacementKeys[i][0];
				String replacementsText = localizer.getString(replacementKeys[i][1], this.field).trim();
				//logger.debug("normalize target: "+target+", replacements text: "+replacementsText);
				// remove any 
			    replacementsText = removeSuperfluousWhitespace(replacementsText);
				String[] replacements = replacementsText.split(" ");
				for(int j=0;j<replacements.length;j++){
					//logger.debug("going to use replacement: "+replacements[j]+", value: "+value);
					value = value.replaceAll(replacements[j], target);
					//logger.debug("used replacement: "+replacements[j]+", value: "+value);
				}
			}
		}
		return value;
	}

	/**
	 * @param replacementsText
	 * @return
	 */
	private String removeSuperfluousWhitespace(String s) {
		Matcher matcher = whitespace.matcher(s);
		if(matcher.find()){
			s = matcher.replaceAll(" ");
		}
		return s;
	}
	/**
	 * @see org.apache.wicket.util.convert.converters.AbstractConverter#getTargetType()
	 */
	protected Class getTargetType(){
		return Long.class;
	}
}