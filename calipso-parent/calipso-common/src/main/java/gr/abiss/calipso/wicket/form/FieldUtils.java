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


package gr.abiss.calipso.wicket.form;

import gr.abiss.calipso.wicket.components.formfields.FieldConfig;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.Model;

public class FieldUtils {
	private static final Logger log = Logger.getLogger(FieldUtils.class);
	public static  void appendFieldStyles(FieldConfig fieldConfig,
			final FormComponent newValueField) {
		if(fieldConfig != null){
			if(StringUtils.isNotBlank(fieldConfig.getClassname())){
				newValueField.add(
						new AttributeAppender("class", new Model<String>(fieldConfig.getClassname()), " "));
			}
			if(StringUtils.isNotBlank(fieldConfig.getStyle())){
				newValueField.add(
						new AttributeAppender("style", new Model<String>(fieldConfig.getStyle()), ";"));
			}
		}
	}
}
