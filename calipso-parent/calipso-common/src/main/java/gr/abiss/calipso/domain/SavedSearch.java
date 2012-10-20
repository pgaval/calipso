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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SavedSearch implements Serializable {

	public static final Short VISIBILITY_PUBLIC = 10;
	public static final Short VISIBILITY_LOGGEDIN_USERS = 20;
	public static final Short VISIBILITY_WITHIN_SPACEGROUP = 30;
	public static final Short VISIBILITY_WITHIN_SPACE = 40;
	public static final Short VISIBILITY_PRIVATE = 50;
	
    private long id;
    private String queryString;
    private String name = "";
    private User user;
    private Space space;
    private Short visibility = VISIBILITY_PRIVATE;
    
    public SavedSearch() {
	}
    
	public SavedSearch(long id, String queryString, String name, User user) {
		super();
		this.id = id;
		this.queryString = queryString;
		this.name = name;
		this.user = user;
		this.space = null;
	}

	public SavedSearch(long id, String queryString, String name, User user, Space space) {
		super();
		this.id = id;
		this.queryString = queryString;
		this.name = name;
		this.user = user;
		this.space = space;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}
	
	/**
	 * @return the visibility
	 */
	public Short getVisibility() {
		return visibility;
	}

	/**
	 * @param visibility the visibility to set
	 */
	public void setVisibility(Short visibility) {
		this.visibility = visibility;
	}

	public String toString() {
		return name;
	}
	
}
