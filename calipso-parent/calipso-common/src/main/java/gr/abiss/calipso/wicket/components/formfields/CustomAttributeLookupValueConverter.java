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

import gr.abiss.calipso.domain.CustomAttributeLookupValue;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.util.convert.converter.AbstractConverter;
import org.apache.wicket.util.string.Strings;

/**
 * Converts from Object to CustomAttributeLookupValue.
 */
public class CustomAttributeLookupValueConverter extends AbstractConverter<CustomAttributeLookupValue>
{
	private static final long serialVersionUID = 1L;

	private Map<String ,CustomAttributeLookupValue> valuesMap;
	private Component owner;
	
	public CustomAttributeLookupValueConverter(Collection<CustomAttributeLookupValue> values, Component owner){
		this.valuesMap = new HashMap<String, CustomAttributeLookupValue>();
		this.owner = owner;
		if(values != null){
			for(CustomAttributeLookupValue value : values){
				this.valuesMap.put(owner.getLocalizer().getString(value.getNameTranslationResourceKey(), owner), value);
			}
		}
	}
	
	/**
	 * @see org.apache.wicket.util.convert.IConverter#convertToObject(java.lang.String,Locale)
	 */
	public CustomAttributeLookupValue convertToObject(final String value, final Locale locale)
	{
		if ((value == null) || Strings.isEmpty(value))
		{
			return null;
		}
		else
		{
			return valuesMap.get(value);
		}
	}

	/**
	 * @see org.apache.wicket.util.convert.IConverter#convertToString(Object, java.util.Locale)
	 */
	@Override
	public String convertToString(final CustomAttributeLookupValue value, final Locale locale)
	{
		return owner.getLocalizer().getString(value.getNameTranslationResourceKey(), owner);
	}

	/**
	 * @see org.apache.wicket.util.convert.converter.AbstractConverter#getTargetType()
	 */
	@Override
	protected Class<CustomAttributeLookupValue> getTargetType()
	{
		return CustomAttributeLookupValue.class;
	}
}