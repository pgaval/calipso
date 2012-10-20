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

package gr.abiss.calipso.domain;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AssetCustomAttributeDateValue implements Serializable {

	private AssetCustomAttributeValue assetCustomAttributeValue;
	private Date value;
	private DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	public AssetCustomAttributeDateValue(AssetCustomAttributeValue assetCustomAttributeValue) {
		this.assetCustomAttributeValue = assetCustomAttributeValue;
		/*
		if (this.assetCustomAttributeValue.getAttributeValue()!=null){
			try{
				this.value = df.parse(this.assetCustomAttributeValue.getAttributeValue());
			}
			catch(ParseException e){
				this.value = null;
			}
		}
		else{
			this.value = null;
		}
		*/
	}

	public Date getValue() {
		return this.value;
	}

	public void setValue(Date value) {
		this.value = value;
	}
	
	
}
