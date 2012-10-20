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

package gr.abiss.calipso.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Used mainly as an alternative to map entries
 */
public class KeyValuePair  implements Comparable<KeyValuePair>, Serializable{

	private static final long serialVersionUID = 1L;
	private Serializable key;
	private Serializable value;
	
	public static List<KeyValuePair> fromMap(Map map){
		ArrayList<KeyValuePair> list = null;
		if(map != null){
			list = new ArrayList<KeyValuePair>(map.size());
			if(map != null){
				for(Iterator iter = map.entrySet().iterator(); iter.hasNext();){
					Entry entry = (Entry) iter.next();
					list.add(new KeyValuePair((Serializable)entry.getKey(), (Serializable)entry.getValue()));
				}
			}
		}
		return list;
	}

	public KeyValuePair(Serializable key, Serializable value) {
		this.key = key;
		this.value = value;
	}

	public Serializable getKey() {
		return key;
	}

	public void setKey(Serializable key) {
		this.key = key;
	}

	public Serializable getValue() {
		return this.value;
	}

	public void setValue(Serializable value) {
		this.value = value;
	}

	public int compareTo(KeyValuePair other) {
		return this.key.toString().compareTo(other.getKey().toString());
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o == this) { return true; }
		if (!(o instanceof KeyValuePair)) {
			return false;
		}
		KeyValuePair other = (KeyValuePair) o;
		return new EqualsBuilder()
			.append(this.getKey(), other.getKey())
	        .isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(27, 63)
		.append(this.getKey())
	        .toHashCode();
	}
	
		
}
