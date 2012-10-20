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

import gr.abiss.calipso.domain.SpaceGroup;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

import org.apache.log4j.Logger;

/**
 * Custom validator to check the uniqueness of SpaceGroup name for the given user
 * 
 */
public class UniqueSpaceGroupNameForEachAdminValidator extends AbstractValidator {

	private static final long serialVersionUID = 1L;
	
	protected static final Logger logger = Logger.getLogger(UniqueSpaceGroupNameForEachAdminValidator.class);

	private Collection<SpaceGroup> spaceGroups = null;

	/**
	 * Constructor to pass a collection of SpaceGroups the user is already an administrator for
	 * @param extentionsString
	 */
	public UniqueSpaceGroupNameForEachAdminValidator(Collection<SpaceGroup> spaceGroups){
		this.spaceGroups = spaceGroups;
	}
	
	/**
	 * Ensure the name of the given SpaceGroup is not the same as any of the SpaceGroup names the 
	 * user is an admin for.
	 */
	protected void onValidate(IValidatable validatable) {
		boolean isValid = true;
		if(validatable != null && validatable.getValue() != null && this.spaceGroups != null && this.spaceGroups.size() > 0){
			for(SpaceGroup spaceGroup: this.spaceGroups){
				if(validatable.getValue().toString().equals(spaceGroup.getName())){
					isValid = false;
					break;
				}
			}
		}
		if(!isValid){
			error(validatable);
		}
		
	}
}
