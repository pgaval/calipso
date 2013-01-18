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
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A class that groups spaces configuration-wise and allows them to share 
 * Assets and more. 
 */
public class SpaceGroup implements Serializable {

	private Long id;
	private String name;
	private String description;
	private Set<User> admins = new HashSet<User>();
	private Set<Space> spaces;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the admins
	 */
	public Set<User> getAdmins() {
		return admins;
	}

	/**
	 * @param admins the admins to set
	 */
	public void setAdmins(Set<User> admins) {
		this.admins = admins;
	}

	/**
	 * @return the spaces
	 */
	public Set<Space> getSpaces() {
		return spaces;
	}

	/**
	 * @param spaces the spaces to set
	 */
	public void setSpaces(Set<Space> spaces) {
		this.spaces = spaces;
	}

	public boolean addSpace(Space space){
		if(this.spaces == null){
			this.spaces = new HashSet<Space>();
		}
		space.setSpaceGroup(this);
		return this.spaces.add(space);
	}
	
	public boolean removeSpace(Space space){
		return this.spaces.remove(space);
	}

	public boolean addAdmin(User user){
		if(this.admins == null){
			this.admins = new HashSet<User>();
		}
		//user.setSpaceGroup(this);
		return this.admins.add(user);
	}
	

	public boolean removeSpace(User user){
		return this.admins.remove(user);
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o == this) { return true; }
		if (!(o instanceof SpaceGroup)) {
			return false;
		}
		SpaceGroup that = (SpaceGroup) o;
		return new EqualsBuilder()
        .append(this.getName(), that.getName())
        .isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(11, 53)
		.append(this.getName())
	        .toHashCode();
	}
	
	@Override
	public String toString(){
    	return new ToStringBuilder(this)
	        .append("id", this.id)
	        .append("name", this.name)
	        .append("description", this.description)
	        .toString();
	}
}
