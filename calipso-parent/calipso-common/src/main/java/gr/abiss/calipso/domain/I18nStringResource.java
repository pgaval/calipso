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
 * POJO to store internationalization string resources
 */
public class I18nStringResource implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private I18nStringIdentifier id;

	private String key;
	private String locale;
	private String value;

	/**
	 * @param sid
	 * @param string
	 */
	public I18nStringResource() {
	}
	/**
	 * @param sid
	 * @param string
	 */
	public I18nStringResource(I18nStringIdentifier id, String value) {
		this.id = id;
		this.key = id.getKey();
		this.locale = id.getLocale();
		this.value = value;
	}

	/**
	 * @return the id
	 */
	public I18nStringIdentifier getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(I18nStringIdentifier id) {
		this.id = id;
		this.key = id.getKey();
		this.locale = id.getLocale();
	}
	

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
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
	 * @param locale the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append(this.getId())
			.append(this.getValue())
			.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(19, 55)
			.append(this.getId())
			.append(this.getValue())
			.toHashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o == this) { return true; }
		if (!(o instanceof I18nStringResource)) {
			return false;
		}
		// for some reason, this only works
		// when using getters for the 'other',
		// maybe because of Hibernate proxies
		I18nStringResource other = (I18nStringResource) o;
		return new EqualsBuilder()
			.append(this.getId(), other.getId())
	        .append(this.getValue(), other.getValue())
	        .isEquals();
	}

}
