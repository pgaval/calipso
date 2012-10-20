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

import gr.abiss.calipso.domain.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

/**
 * 
 */

public class NonDuplicateInputValidator extends AbstractValidator {

	protected static final Logger logger = Logger
			.getLogger(RegexpValidator.class);
	private Field field;

	/**
	 * 
	 * @param field
	 *            The field that the validator should check.
	 */
	public NonDuplicateInputValidator(Field field) {
		this.field = field;

	}

	private static final long serialVersionUID = 1L;

	protected void onValidate(IValidatable validatable) {

		String s = (String) validatable.getValue();
		// the original list of the text area
		// split by win/unix new lines just in case and remove empty new lines.
		List<String> _originalOptions = Arrays.asList(s.split("[\\r\\n]+"));
		// the above cannot be modified...
		List<String> originalOptions = new ArrayList<String>(_originalOptions);
		Set<String> uniqueOptions = new HashSet<String>(originalOptions);
		Map<String, String> errorVars = new HashMap<String, String>();
		boolean valid = true;
		// check for entering dupes within the textarea

		if (originalOptions.size() != uniqueOptions.size()) {
			valid = false;
			// remove the rest to add a message for the duplicates
			originalOptions.removeAll(uniqueOptions);

			errorVars.put("duplicateEntry", (String) CollectionUtils
					.disjunction(originalOptions, uniqueOptions).iterator()
					.next());
		} else {
			// check for entering an existing option
			for (String originalOption : originalOptions) {
				if (field.hasOption(originalOption)) {
					valid = false;
					errorVars.put("duplicateEntry", originalOption);
				}
			}
		}
		if (!valid) {
			error(validatable, errorVars);
		}
	}

	@Override
	protected String resourceKey() {
		return "AbstractValidator.optionExists";
	}
}