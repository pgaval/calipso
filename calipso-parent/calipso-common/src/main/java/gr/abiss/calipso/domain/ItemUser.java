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

/**
 * Class that exists purely to hold a single user associated with an item
 * along with a integer "type" indicating the nature of the relationship
 * between Item --> User (one directional relationship)
 *
 * This is used in the following cases
 * - users "watching" an Item and need to be notified on Status changes
 *
 * and can be used for other kinds of relationships in the future
 */
public class ItemUser implements Serializable, IUser {
    
    private long id;
    private User user;
    private int type;
    
    public ItemUser() {
        // zero arg constructor
    }
    
    public ItemUser(User user) {
        this.user = user;
    }
    
    public ItemUser(User user, int type) {
        this.user = user;
        this.type = type;
    }

    //=================================================
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }    
    
    /**
	 * @see gr.abiss.calipso.domain.IUser#getName()
	 */
	public String getName() {
		return this.user.getName();
	}

	/**
	 * @see gr.abiss.calipso.domain.IUser#getEmail()
	 */
	public String getEmail() {
		return this.user.getEmail();
	}

	/**
	 * @see gr.abiss.calipso.domain.IUser#getLoginName()
	 */
	public String getLoginName() {
		return this.user.getLoginName();
	}

	/**
	 * @see gr.abiss.calipso.domain.IUser#getOrganization()
	 */
	public Organization getOrganization() {
		return this.user.getOrganization();
	}

	/**
	 * @see gr.abiss.calipso.domain.IUser#getLastname()
	 */
	public String getLastname() {
		return this.user.getLastname();
	}

	/**
	 * @see gr.abiss.calipso.domain.IUser#getFax()
	 */
	public String getFax() {
		return this.user.getFax();
	}

	@Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("id [").append(id);
        sb.append("]; user [").append(user);
        sb.append("]; type [").append(type);
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemUser)) {
            return false;
        }
        final ItemUser iu = (ItemUser) o;
        return (user.equals(iu.getUser()) && type == iu.getType());
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = hash * 31 + user.hashCode();
        hash = hash * 31 + type;
        return hash;
    }
    
}
