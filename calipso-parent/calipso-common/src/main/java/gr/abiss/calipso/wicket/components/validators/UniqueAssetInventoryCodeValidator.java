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
import java.util.List;

import gr.abiss.calipso.domain.Asset;


import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
/**
 *
 */
public class UniqueAssetInventoryCodeValidator extends AbstractValidator{


	protected static final Logger logger = Logger.getLogger(UniqueAssetInventoryCodeValidator.class);
	private static final long serialVersionUID = 1L;
	
	Collection<Asset> col;
	String lastValue;
	/**
	 * 
	 * @param col
	 * @param lastValue
	 */
	public UniqueAssetInventoryCodeValidator(Collection<Asset> col, String lastValue){		
		this.col = col;
		this.lastValue = lastValue;
	}
	@Override
	protected void onValidate(IValidatable v) {
		
		boolean isValid = true;
		String s = (String) v.getValue();
		if(logger.isDebugEnabled()){
			logger.debug("Validating user input : " + s);
		}
		for(Asset as : col){
			logger.debug("Inventory Code in asset list : " + as.getInventoryCode());
			if((!s.equals(lastValue) )&& s.equals(as.getInventoryCode())){
				isValid = false;
				break;
			}
		}
		if (!isValid) {
			error(v);
		}
	}
	/**
	 * @see org.apache.wicket.validation.validator.AbstractValidator#resourceKey()
	 */
	@Override
	protected String resourceKey() {
		return "asset.inventorycodeAlreadyExist";
	}
}
