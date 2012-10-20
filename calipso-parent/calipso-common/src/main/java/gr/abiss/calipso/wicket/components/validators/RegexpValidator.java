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

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

/**
 * Custom validator to check the validity of a string as a Java Regular
 * Expression
 * 
 * @author manos
 * 
 */
public class RegexpValidator extends AbstractValidator {
	protected static final Logger logger = Logger
			.getLogger(RegexpValidator.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -3012928866754692699L;

	/**
	 * used to skip validation
	 */
	public static final String NO_VALIDATION = "gr.abiss.calipso.util.RegexpValidator#NO_VALIDATION";

	/**
	 * Checks a value is a valid Java Regular Expression. An empty String is
	 * valid, null is not.
	 * 
	 * @param validatable
	 *            the <code>IValidatable</code> to check
	 */
	protected void onValidate(IValidatable validatable) {
		boolean regexpValid = true;
		String regexp = (String) validatable.getValue();
		if (!regexp.equals(NO_VALIDATION) && regexp.length() > 0) {
			try {
				Pattern.compile(regexp);
			} catch (PatternSyntaxException e) {
				regexpValid = false;
			}
		}
		if (!regexpValid || regexp == null) {
			error(validatable);
		}
	}

}
