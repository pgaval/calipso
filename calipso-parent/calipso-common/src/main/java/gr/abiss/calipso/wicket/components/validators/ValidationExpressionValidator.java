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

import gr.abiss.calipso.domain.ValidationExpression;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.util.parse.metapattern.MetaPattern;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 * This class extends wicket's PatternValidator mostly to provide the UI 
 * with custom i18n messages during validation error reporting and
 * skip validation in case the given validation expression is equal 
 * to the NO_VALIDATION constant
 */
public class ValidationExpressionValidator extends PatternValidator {

	/**
	 * Used to skip validation. legacy naming
	 */
	public static final String NO_VALIDATION = "gr.abiss.calipso.util.RegexpValidator#NO_VALIDATION";
	
	private boolean skipValidation = false;
	private String errorMessageResourceKey = null;
	
	
	/**
	 * Superclass constructor made private
	 * @param pattern
	 */
	private ValidationExpressionValidator(String pattern) {
		super(pattern);
	}

	/**
	 * Superclass constructor made private
	 * @param pattern
	 */
	private ValidationExpressionValidator(Pattern pattern) {
		super(pattern);
	}

	/**
	 * Superclass constructor made private
	 * @param pattern
	 */
	private ValidationExpressionValidator(MetaPattern pattern) {
		super(pattern);
	}

	/**
	 * @param validationExpression
	 */
	public ValidationExpressionValidator(ValidationExpression validationExpression) {
		super(validationExpression.getExpression());
		init(validationExpression);
	}
	
	/**
	 * Unused for the time being
	 * @param validationExpression
	 * @param flags pattern flags
	 */
	public ValidationExpressionValidator(ValidationExpression validationExpression, int flags) {
		super(validationExpression.getExpression(), flags);
		init(validationExpression);
	}

	private void init(ValidationExpression validationExpression) {
		if(NO_VALIDATION.equals(validationExpression.getExpression())){
			this.skipValidation = true;
		}
		this.errorMessageResourceKey = validationExpression.getPropertyTranslationResourceKey(ValidationExpression.DESCRIPTION);
	}
	
	@Override
	protected void onValidate(IValidatable<String> validatable) {
		if(!this.skipValidation){
			super.onValidate(validatable);
		}
	}

	@Override
	protected String resourceKey() {
		if(StringUtils.isNotBlank(this.errorMessageResourceKey)){
			return this.errorMessageResourceKey;
		}
		else{
			return super.resourceKey();
		}
	}
	
	

}
