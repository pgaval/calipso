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

package gr.abiss.calipso.wicket.components.formfields;

import gr.abiss.calipso.domain.Asset;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.util.convert.converter.AbstractConverter;

/**
 * Converts Assets to something presentable to the user
 * @see gr.abiss.calipso.util.HumanTime
 */
public class AssetConverter extends AbstractConverter{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(AssetConverter.class);

	private Map<String,Asset> assets;
	
	public AssetConverter(){
		super();
	}

	
	/**
	 * @see org.apache.wicket.util.convert.IConverter#convertToString(Object, java.util.Locale)
	 */
	public String convertToString(final Object value, Locale locale){
		String stringValue = null;
		if(value != null){
			Asset asset = (Asset) value;
			stringValue = asset.getInventoryCode();
			if(this.assets == null){
				this.assets = new HashMap<String,Asset>();
			}
		}
		return stringValue;
	}

	/**
	 * @see org.apache.wicket.util.convert.converters.AbstractConverter#getTargetType()
	 */
	protected Class getTargetType(){
		return Asset.class;
	}

	/**
	 * @see org.apache.wicket.util.convert.IConverter#convertToObject(java.lang.String, java.util.Locale)
	 */
	public Object convertToObject(String value, Locale locale) {
		if(!this.assets.containsKey(value)){
			//throw new RuntimeException("Failed to match an Asset with the given inventory code");
			logger.error("Failed to match an Asset with the given inventory code");
		}
		return this.assets.get(value);
	}
}