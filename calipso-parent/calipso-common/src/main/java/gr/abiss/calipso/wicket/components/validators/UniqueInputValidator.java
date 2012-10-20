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

import gr.abiss.calipso.domain.CustomAttributeLookupValue;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

/**
 * Validation that check if user's given option already exists in a list of
 * options
 * 
 * 
 */
public class UniqueInputValidator extends AbstractValidator {
	protected static final Logger logger = Logger.getLogger(UniqueInputValidator.class);
	private static final long serialVersionUID = 1L;
	
	Collection<CustomAttributeLookupValue> listOfOptions;

	public UniqueInputValidator(Collection<CustomAttributeLookupValue> listOfOptions) {
		this.listOfOptions = listOfOptions;
	}

	/**
	 * @see org.apache.wicket.validation.validator.AbstractValidator#onValidate(org.apache.wicket.validation.IValidatable)
	 */
	@Override
	protected void onValidate(IValidatable validatable) {
		// true if the user's input is a valid input
		boolean validValue = true;
		// the input value as string
		String value = ((String) validatable.getValue()).trim();
		

		if (logger.isDebugEnabled()) {
			logger.debug("Asset options value : " + value);
		}
		// if user's input is empty, input is invalid
		// and there is no need to iterate the list
		if (value.isEmpty()) {
			validValue = false;
		} else {
			// check if the list exist and has at least one item to compare
			if (listOfOptions != null && listOfOptions.size() > 0) {
				for (CustomAttributeLookupValue assetTypeCustomAttributeLookupValue : listOfOptions) {
					// check if the given option is already in the list.
					if (value.equals(assetTypeCustomAttributeLookupValue
							.getValue())) {
						validValue = false;
					}
				}
			}
		}

		if (!validValue) {
			error(validatable);
		}
	}
}
