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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * POJO to serve a composite ID for internationalization string 
 * resources (i.e. instances of I18nStringResource)
 */
public class I18nStringIdentifier implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String key;
	private String locale;

	public I18nStringIdentifier(){
	}
	
	public I18nStringIdentifier(String key, String locale){
		this.key = key;
		this.locale = locale;
	}
	
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * @param locale
	 *            the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append(this.getKey())
			.append(this.getLocale())
			.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(19, 55)
			.append(this.getKey())
			.append(this.getLocale())
			.toHashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o == this) { return true; }
		if (!(o instanceof I18nStringIdentifier)) {
			return false;
		}
		// for some reason, this only works
		// when using getters for the 'other',
		// maybe because of Hibernate proxies
		I18nStringIdentifier other = (I18nStringIdentifier) o;
		return new EqualsBuilder()
			.append(this.getKey(), other.getKey())
	        .append(this.getLocale(), other.getLocale())
	        .isEquals();
	}

}
