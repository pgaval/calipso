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

package gr.abiss.calipso.wicket.components.validators;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

/**
 * Custom validator to check the validity of a SimpleDateFormat expression
 * 
 * @author manos
 * 
 */
public class SimpleDateFormatValidator extends AbstractValidator {
	protected static final Logger logger = Logger
			.getLogger(SimpleDateFormatValidator.class);

	/**
	 * Checks a value is a valid SimpleDateFormat Expression.
	 * 
	 * @param validatable
	 *            the <code>IValidatable</code> to check
	 */
	protected void onValidate(IValidatable validatable) {
		boolean isValid = true;
		String expression = (String) validatable.getValue();
		if(StringUtils.isNotBlank(expression)){
			try {
				new SimpleDateFormat(expression);
			} catch (PatternSyntaxException e) {
				isValid = false;
			}
		}
		else{
			isValid = false;
		}
		
		if (!isValid) {
			error(validatable);
		}
	}

}
