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

import gr.abiss.calipso.wicket.BasePanel;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;

/**
 *
 */
public class RegisterUserConfirmPasswordValidator extends AbstractFormValidator{

    
	protected static final Logger logger = Logger.getLogger(RegisterUserConfirmPasswordValidator.class);
	private static final long serialVersionUID = 1L;
	
	private FormComponent passwordField;
	private FormComponent confirmPasswordField;
	
	
	public RegisterUserConfirmPasswordValidator(FormComponent passwordField, FormComponent confirmPasswordField){
		this.passwordField = passwordField;
		this.confirmPasswordField = confirmPasswordField;
		if(logger.isDebugEnabled()){
			logger.debug("passwordField modelObject : " + passwordField.getModelObject());
			logger.debug("confirmPasswordField modelObject : " + confirmPasswordField.getModelObject());
		}
	}
	
	
	
	/**
	 * @see org.apache.wicket.markup.html.form.validation.IFormValidator#getDependentFormComponents()
	 */
	public FormComponent[] getDependentFormComponents() {
        return new FormComponent[] {passwordField, confirmPasswordField};
	}

	/**
	 * @see org.apache.wicket.markup.html.form.validation.IFormValidator#validate(org.apache.wicket.markup.html.form.Form)
	 */
	public void validate(Form form) {
        String a = (String) passwordField.getModelObject();
        String b = (String) confirmPasswordField.getModelObject();
        if((a != null && !a.equals(b)) || (b!= null && !b.equals(a))) {
            confirmPasswordField.error("System can not be found ...");
        }                    
	}

	/**
	 * @see org.apache.wicket.validation.validator.AbstractValidator#resourceKey()
	 */
	@Override
	protected String resourceKey() {
		return "user_form.passwordConfirm.error";
	}
}
