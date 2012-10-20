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

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

public class NumberValidator extends AbstractValidator{
	private String resourceKey;

	
	public NumberValidator(String resourceKey) {
		this.resourceKey = resourceKey;
	}

	
	public String getResourceKey() {
		return resourceKey;
	}

	public void setResourceKey(String resourceKey) {
		this.resourceKey = resourceKey;
	}


	protected void onValidate(IValidatable v) {
		try{
			Integer i= new Integer(v.getValue().toString());
		}
		catch (NumberFormatException e) {
			error(v);
//			Map vars = new HashMap<String, String>();
//			vars.put("0", v);
//			error(v, "sla.notValidNumber", vars);
		}
		catch (Exception e) {
			error(v);
		}
	}//onValidate

	protected String resourceKey() {
		return this.resourceKey;
	}
}//NumberValidator
