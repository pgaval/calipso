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

import gr.abiss.calipso.wicket.DashboardRowPanel;

import java.io.Serializable;
import org.springframework.security.GrantedAuthority;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

/**
 * Class that exists purely to hold a "ternary" mapping of 
 * user <--> space <--> role and is also persisted
 * the JTrac authorization (access control) scheme works as follows:
 * if space is null, that means that this is a "global" JTrac role
 * if space is not null, this role applies for the user to that
 * space, and the getAuthority() method used by Acegi returns the 
 * role key appended with "_" + spacePrefixCode
 */
public class UserSpaceRole implements GrantedAuthority, Serializable {
	
	protected static final Logger logger = Logger.getLogger(UserSpaceRole.class);
    
    private long id;
    private User user;
    private SpaceRole spaceRole;

	public UserSpaceRole() {
        // zero arg constructor
    }

    public UserSpaceRole(long id, User user, SpaceRole spaceRole) {
		this.id = id;
		this.user = user;
		this.spaceRole = spaceRole;
	}

    public UserSpaceRole(User user, SpaceRole spaceRole) {
		this.user = user;
		this.spaceRole = spaceRole;
	}
    
    public boolean isAbleToCreateNewItem() {
    	
        if (this.spaceRole == null || this.spaceRole.getSpace() == null) {
            return false;
        }
        boolean userIsLoggedIn = (user !=null && user.getId()>0);
        return user.getPermittedTransitions(this.spaceRole.getSpace(), State.NEW).size() > 0 /*&& userIsLoggedIn*/;
    }
    
    //======== ACEGI GrantedAuthority implementation =============
    
//    public String getAuthority() {        
//        if (space != null) {
//            return roleKey + "_" + space.getPrefixCode();
//        }
//        return roleKey;
//    }

    public String getAuthority() {        
        if (this.spaceRole.getSpace() != null) {
            return this.spaceRole.getIdAsString() + "_" + this.spaceRole.getSpace().getPrefixCode();
        }
        return this.spaceRole.getIdAsString();
    }
    
    //=============================================================      
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public SpaceRole getSpaceRole() {
		return spaceRole;
	}
    
    public void setSpaceRole(SpaceRole spaceRole) {
		this.spaceRole = spaceRole;
	}
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }    
    
 //   @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (!(o instanceof UserSpaceRole)) {
//            return false;
//        }
//        final UserSpaceRole usr = (UserSpaceRole) o;
//        return (
//            (space == usr.getSpace() || space.equals(usr.getSpace()))
//            && user.equals(usr.getUser())
//            && roleKey.equals(usr.getRoleKey())
//        );
//    }
/*
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserSpaceRole)) {
            return false;
        }
        final UserSpaceRole usr = (UserSpaceRole) o;

        return (
        		(this.spaceRole == usr.getSpaceRole() || this.spaceRole.equals(usr.getSpaceRole())) &&
        		(this.user == usr.getUser() || this.user.equals(usr.getUser())
            )
        );
    }*/
    
    @Override
    public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o == this) { return true; }
		if (!(o instanceof User)) {
			return false;
		}
        UserSpaceRole other = (UserSpaceRole) o;
        return new EqualsBuilder()
	        .append(this.getUser(), other.getUser())
	        .append(this.getSpaceRole(), other.getSpaceRole())
	        .isEquals();
    }
    

    @Override
    public int hashCode() {
    	Space space = this.spaceRole!= null?this.spaceRole.getSpace():null;
        return new HashCodeBuilder(17, 37).
        append(this.user != null?this.user.getId():0).
        append(this.spaceRole!= null?this.spaceRole.getId():0).
        append(space != null?space.getId():0).
        toHashCode();
/*manos: replacing this as it throws lazy exceptions
        int hash = 7;
//        hash = hash * 31 + user.hashCode();
        hash = hash * 31 + (user == null ? 0 :  user.hashCode());
//        hash = hash * 31 + (space == null ? 0 : space.hashCode());
        hash = hash * 31 + (this.spaceRole.getSpace() == null ? 0 : this.spaceRole.getSpace().hashCode());
//        hash = hash * 31 + roleKey.hashCode();
//        hash = hash * 31 + (roleKey == null ? 0 : roleKey.hashCode());
        hash = hash * 31 + (this.spaceRole.hashCode());
        return hash;
        */
    } 
    
    @Override
    public String toString() {
        return getAuthority();
    }

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return this.toString().compareTo(arg0.toString());
	}
    
}