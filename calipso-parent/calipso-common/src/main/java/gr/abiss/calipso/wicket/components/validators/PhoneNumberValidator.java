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

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

/**
 * Custom validator to check the validity of phone numbers (10 digits)
 * @author manos
 *
 */
public class PhoneNumberValidator extends AbstractValidator {

	
	private static final long serialVersionUID = 1L;
	
	private static Pattern pattern = Pattern.compile("\\d{10}");

	
	/**
	 * Checks a value is a valid phone number (10 digits)
	 * @param validatable
	 *            the <code>IValidatable</code> to check
	 */
	protected void onValidate(IValidatable validatable)
	{
		// Check value against pattern
		if (!pattern.matcher((String)validatable.getValue()).matches()){
			//error(validatable);
		}
	}

}