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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;


/**
 * Essentially an enumeration of role types. This would be an enum if those did not suck; 
 * hashCode and equals are badly implemented and arrogantly defined as final.
 * See for example http://bugs.sun.com/view_bug.do?bug_id=6373406
 */
public class RoleType implements Serializable{
	private static final Logger logger = Logger.getLogger(RoleType.class);
	private static final long serialVersionUID = 1L;

	public static final RoleType ADMINISTRATOR = new RoleType(1, "Administator");
	public static final RoleType SPACE_ADMINISTRATOR = new RoleType(2, "Space Administrator");
	public static final RoleType REGULAR_USER = new RoleType(3, "Regular User");
	public static final RoleType GUEST = new RoleType(4, "Guest");
	public static final RoleType ANONYMOUS = new RoleType(5, "Anonymous");

	public static Map<String,RoleType> TYPESMAP = new HashMap<String,RoleType>();
	static {
		TYPESMAP.put(ADMINISTRATOR.getIdAsString(), ADMINISTRATOR);
		TYPESMAP.put(SPACE_ADMINISTRATOR.getIdAsString(), SPACE_ADMINISTRATOR);
		TYPESMAP.put(REGULAR_USER.getIdAsString(), REGULAR_USER);
		TYPESMAP.put(GUEST.getIdAsString(), GUEST);
		TYPESMAP.put(ANONYMOUS.getIdAsString(), ANONYMOUS);
		// freeze.
		TYPESMAP = Collections.unmodifiableMap(TYPESMAP);
	}

	private int id;
	private String description;
	
	private RoleType(){
	}
	
	private RoleType(int id, String description){
		this.id = id;
		this.description = description;
	}

	public int getId(){
		return this.id;
	}
	
	public String getIdAsString(){
		return String.valueOf(this.id);
	}

	public String getDescription() {
		return this.description;
	}
	
    public int hashCode() {
        return new HashCodeBuilder(33, 41)
	        .append(this.id)
	        .append(this.description)
	        .toHashCode();
    } 
	
    public String toString() {
		return new ToStringBuilder(this)
			.append("id", this.id)
			.append("description", this.description)
			.toString();
    }

	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o == this) { return true; }
		if (o.getClass() != getClass()) {
			return false;
		}
		RoleType other = (RoleType) o;
        return new EqualsBuilder()
	        .append(this.id, other.id)
	        .append(this.description, other.description)
	        .isEquals();	    
	}
	
}
