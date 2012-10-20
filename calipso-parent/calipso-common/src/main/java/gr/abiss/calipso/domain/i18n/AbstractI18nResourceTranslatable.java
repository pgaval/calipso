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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

/**
 * @author manos
 *
 */
public abstract class AbstractI18nResourceTranslatable implements Serializable, I18nResourceTranslatable {
	private static final Logger logger = Logger.getLogger(AbstractI18nResourceTranslatable.class);
	// i18n, not persisted as a property. structure is <propertyName, <locale, value>>
	protected Map<String,Map<String,String>> translations = new HashMap<String,Map<String,String>>();
	
	/**
	 * @see gr.abiss.calipso.domain.i18n.I18nResourceTranslatable#getName()
	 */
	public abstract String getName();
	
	/**
	 * @see gr.abiss.calipso.domain.i18n.I18nResourceTranslatable#setName(java.lang.String)
	 */
	public abstract void setName(String name);

	
	/**
	 * @see gr.abiss.calipso.domain.i18n.I18nResourceTranslatable#getTranslations()
	 */
	public Map<String,Map<String,String>> getTranslations() {
		return this.translations;
	}
	
	/**
	 * @param translations the translations to set
	 */
	public void setTranslations(Map<String, Map<String, String>> translations) {
		this.translations = translations;
	}

	/**
	 * @see gr.abiss.calipso.domain.i18n.I18nResourceTranslatable#getNameTranslations()
	 */
	public Map<String, String> getNameTranslations() {
		return this.getPropertyTranslations("name");
	}
	/**
	 * @see gr.abiss.calipso.domain.i18n.I18nResourceTranslatable#setNameTranslations(java.util.Map)
	 */
	public void setNameTranslations(Map<String, String> nameTranslations) {
		this.translations.put("name", nameTranslations);
	}

	/**
	 * @see gr.abiss.calipso.domain.i18n.I18nResourceTranslatable#setNameTranslations(java.util.List)
	 */
	public void setNameTranslations(List<I18nStringResource> translations) {
		this.setPropertyTranslations("name", translations);
	}

	/**
	 * @see gr.abiss.calipso.domain.i18n.I18nResourceTranslatable#getPropertyTranslations(java.lang.String)
	 */
	public Map<String, String> getPropertyTranslations(String propertyName) {
		if(this.translations.get(propertyName) == null){
			this.translations.put(propertyName, new HashMap<String,String>());
		}
		return this.translations.get(propertyName);
	}
	
	/**
	 * @see gr.abiss.calipso.domain.i18n.I18nResourceTranslatable#setPropertyTranslations(java.lang.String, java.util.List)
	 */
	public void setPropertyTranslations(String propertyName, List<I18nStringResource> propTranslations) {
		// always init a map
		Map<String,String> translations = new HashMap<String,String>();
		if(CollectionUtils.isNotEmpty(propTranslations)){
			for(I18nStringResource rs : propTranslations){
				translations.put(rs.getLocale(), rs.getValue());
			}
		}
		this.setPropertyTranslations(propertyName, translations);
	}
	
	public void setPropertyTranslations(String propertyName, Map<String,String> translations) {
		if(this.translations == null){
			this.translations = new HashMap<String,Map<String,String>>();
		}
		this.translations.put(propertyName, translations);
	}
	
	/**
	 * @see gr.abiss.calipso.domain.i18n.I18nResourceTranslatable#getNameTranslationResourceKey()
	 */
	public String getNameTranslationResourceKey(){
		return getPropertyTranslationResourceKey("name");
	}
	
	/**
	 * Returns the Class name without the package (if any)
	 * @param c the class
	 * @return the name without the package prefix
	 */
	protected static String getShortName(Class c) {
	    String className = c.getName();
	    int firstChar = className.lastIndexOf ('.') + 1;
	    if ( firstChar > 0 ) {
	    	className = className.substring ( firstChar );
	    }
	    return className;
	 }

	/**
	 * @see gr.abiss.calipso.domain.i18n.I18nResourceTranslatable#addNameTranslation(java.lang.String, java.lang.String)
	 */
	public void addNameTranslation(String languageId, String translatedName) {
		if(this.translations.get("name") == null){
			this.translations.put("name", new HashMap<String, String>()); 
		}
		this.translations.get("name").put(languageId, translatedName);
	}
	/**
	 * @see gr.abiss.calipso.domain.i18n.I18nResourceTranslatable#getPropertyTranslationResourceKey(java.lang.String)
	 */
	public abstract String getPropertyTranslationResourceKey(String name);

}