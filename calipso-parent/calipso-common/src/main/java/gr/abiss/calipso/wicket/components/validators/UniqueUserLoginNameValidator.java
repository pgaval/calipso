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

import java.util.Collection;

import gr.abiss.calipso.domain.User;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

/**
 *
 */
public class UniqueUserLoginNameValidator extends AbstractValidator{
	Collection<User> usersInDb;
	public UniqueUserLoginNameValidator(Collection<User> usersAlreadyRegister){
		usersInDb = usersAlreadyRegister;
	}

	/**
	 * @see org.apache.wicket.validation.validator.AbstractValidator#onValidate(org.apache.wicket.validation.IValidatable)
	 */
	@Override
	protected void onValidate(IValidatable validatable) {
		boolean isValid = true;
		String givenLoginName = (String) validatable.getValue();
		for(User user : usersInDb){
			if(user.getLoginName().equals(givenLoginName)){
				isValid = false;
			}
		}
		if(!isValid){
			error(validatable);
		}
	}

    @Override
    protected String resourceKey() {                    
        return "user_form.loginId.error.exists";
    }                


}
