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
 * Essentially just an ID for ISO 639-1 Language Codes  	
 * @author manos
 *
 */
public class Language implements Serializable{
	
	private String id;


	public Language(){
	}
	
	public Language(String id){
		this.id = id;
	}
	
	/**
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
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(15, 153)
			.append(this.getId())
	        .toHashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o == this) { return true; }
		if (!(o instanceof Language)) {
			return false;
		}
		// for some reason, this only works
		// when using getters for the 'other',
		// maybe because of Hibernate proxies
		Language other = (Language) o;
		return new EqualsBuilder()
			.append(this.getId(), other.getId())
	        .isEquals();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id", this.getId())
			.toString();
	}
}
