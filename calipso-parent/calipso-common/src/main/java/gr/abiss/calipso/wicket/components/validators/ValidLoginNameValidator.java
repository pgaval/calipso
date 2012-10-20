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

import gr.abiss.calipso.domain.Asset;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

/**
 *
 */
public class ValidLoginNameValidator extends AbstractValidator{

	protected static final Logger logger = Logger.getLogger(ValidLoginNameValidator.class);
	private static final long serialVersionUID = 1L;
	

	public ValidLoginNameValidator(){	
	                     
	    }
	@Override
	protected void onValidate(IValidatable v) {
        String s = (String) v.getValue();                   
        if(!ValidationUtils.isValidLoginName(s)) {
            error(v);
        }
    }  

    @Override
    protected String resourceKey() {                    
        return "user_form.loginId.error.invalid";
    } 
}
