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

package gr.abiss.calipso.domain.i18n;

import gr.abiss.calipso.domain.I18nStringResource;

import java.util.List;
import java.util.Map;

/**
 * @author manos
 *
 */
public interface I18nResourceTranslatable {

	/**
	 * @return the name
	 */
	public String getName();

	/**
	 * @return the name
	 */
	public void setName(String name);

	/**
	 * @return the translations for property "name". The returned map 
	 * uses locale IDs for keys and translation texts for values, 
	 * in other words each entry must be in the form {"locale", "Name text"} 
	 * e.g. {"en", "Descriptive name"}
	 */
	public Map<String, String> getNameTranslations();

	/**
	 * Set the translations for property "name". The given map 
	 * must use locale IDs for keys and translation texts for values, 
	 * in other words each entry must be in the form {"locale", "Name text"} 
	 * e.g. {"en", "Descriptive name"}
	 * @param nameTranslations
	 */
	public void setNameTranslations(Map<String, String> nameTranslations);

	/**
	 * Set the translations for property "name".
	 */
	public void setNameTranslations(List<I18nStringResource> translations);

	/**
	 * Set the translations for the given property name.
	 */
	public void setPropertyTranslations(String propertyName,
			List<I18nStringResource> nameTranslations);
	
	/**
	 * Set the translations for the given property name.
	 */
	public void setPropertyTranslations(String propertyName,
			Map<String,String> nameTranslations);

	/**
	 * Get the translations for the given property name. The returned map 
	 * uses locale IDs for keys and translation texts for values, 
	 * in other words each entry must be in the form {"locale", "Name text"} 
	 * e.g. {"en", "Descriptive name"}
	 * @return the translations for the given property name (empty Map at worst, never null). 
	 */
	public Map<String, String> getPropertyTranslations(String propertyName);

	/**
	 * Get the translations of all property names.
	 */
	public Map<String, Map<String, String>> getTranslations();
	
	/**
	 * Get the translations of all property names.
	 */
	public void setTranslations( Map<String, Map<String, String>> translations);

	/**
	 *  Get the key of the "name" translation.
	 */
	public String getNameTranslationResourceKey();

	/**
	 *  Get the key of the translation matching the given property name.
	 */
	public String getPropertyTranslationResourceKey(String name);

}