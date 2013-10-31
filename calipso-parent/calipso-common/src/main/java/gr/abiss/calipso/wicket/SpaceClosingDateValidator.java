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

package gr.abiss.calipso.wicket;

import java.util.Date;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.jfree.util.Log;

final class SpaceClosingDateValidator extends AbstractValidator {
	private Date closingDate = null;

	SpaceClosingDateValidator(Date closingDate) {
		this.closingDate = closingDate;
	}

	@Override
	protected void onValidate(IValidatable v) {
		try {

			if (closingDate != null && closingDate.after(new Date())) {
				error(v);
			}
		} catch (Exception e) {
			Log.error(e);
		}
	}// onValidate
}