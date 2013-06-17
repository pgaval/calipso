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
 * 
 * This file incorporates work released by the JTrac project and  covered 
 * by the following copyright and permission notice:  
 * 
 *   Copyright 2002-2005 the original author or authors.
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *   
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package gr.abiss.calipso.domain;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;
import org.apache.commons.lang.builder.EqualsBuilder;

import bsh.This;

/**
 * Class to represent a country. Some information 
 * comes from ISO 3166. The persisted list states the country names 
 * (official short names in English) in alphabetical order as given in ISO 3166-1 
 * and the corresponding ISO 3166-1-alpha-2 code elements which are used as 
 * the persisted ID/Primary Key. 
 */
public class Country implements Serializable {

	private static final long serialVersionUID = 1L;
	

	/**
	 * The ID according to ISO 3166-1-alpha-2
	 */
	private String id;
	private String name;
	private String isoName;
	private String preferredLocale;
	private String callingCode;
	
	
	
	public Country() {
		super();
	}

	public Country(String name, String isoName, String id) {
		this();
		this.name = name;
		this.isoName = isoName;
		this.id = id;
	}
	
	public Country(String name, String isoName, String id, String preferredLocale) {
		this(name, isoName, id);
		this.preferredLocale = preferredLocale;
	}
	
	public Country(String name, String isoName, String id, String preferredLocale, String callingCode) {
		this(name, isoName, id, preferredLocale);
		this.callingCode = callingCode;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the isoName
	 */
	public String getIsoName() {
		return isoName;
	}
	/**
	 * @param isoName the isoName to set
	 */
	public void setIsoName(String isoName) {
		this.isoName = isoName;
	}
	/**
	 * Get the ID according to ISO 3166-1-alpha-2
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the preferredLocale
	 */
	public String getPreferredLocale() {
		return preferredLocale;
	}
	/**
	 * @param preferredLocale the preferredLocale to set
	 */
	public void setPreferredLocale(String preferredLocale) {
		this.preferredLocale = preferredLocale;
	}
	/**
	 * @return the callingCode
	 */
	public String getCallingCode() {
		return callingCode;
	}
	/**
	 * @param callingCode the callingCode to set
	 */
	public void setCallingCode(String callingCode) {
		this.callingCode = callingCode;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o == this) { return true; }
		if (!(o instanceof Country)) {
			return false;
		}
		// for some reason, this only works
		// when using getters, probably
		// because of Hibernate proxies
		Country other = (Country) o;
		return new EqualsBuilder()
			.append(this.getId(), other.getId())
	        .isEquals();
	}
	
}