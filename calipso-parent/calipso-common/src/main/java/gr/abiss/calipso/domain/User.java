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


import gr.abiss.calipso.domain.RoleType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.userdetails.UserDetails;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

/**
 * Standard User entity with attributes such as name, password etc.
 * The parent relationship is used for easy grouping of users and
 * flexible inheritance of permission schemes TODO.  The user type
 * determines if this is a normal user or a user group.  Only
 * user groups can have child references.
 *
 * We also tie in to the Acegi security framework and implement
 * the Acegi UserDetails interface so that Acegi can take care
 * of Authentication and Authorization
 */
public class User implements UserDetails, Serializable, IUser {
	
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(User.class);
	
    public static final int SEARCH_NAME = 0;
    public static final int SEARCH_LOGIN_NAME = 1;
    public static final int SEARCH_EMAIL = 2;
    
    private long id;
    private Integer type;
    private User parent;
    private String loginName;
    private String name;
    private String password;
    
    private String lastname;
    private String address;
    private String zip;
	private Country country;
	private String phone;
	private String alternativePhone;
    private String fax;
    // system properties
    private Date dateCreated;
    private Date dateLastUpdated;
    private User createdBy;
    private User lastUpdatedBy;

    private String email;
    private String emailHash;
    private boolean gravatar;
    //private Metadata metadata;
    private String locale;
    private boolean locked;
    
    private Set<UserSpaceRole> userSpaceRoles = new HashSet<UserSpaceRole>();   
    Map<String, UserSpaceRole> userSpaceRoleMap = new TreeMap<String, UserSpaceRole>();
//    private Map<StdField, StdFieldMask> stdFieldMap;
    private List<RoleSpaceStdField> roleSpaceStdFieldList = new ArrayList<RoleSpaceStdField>();
    private Organization organization;
    
	public User() {
		
	}
    //=============================================================
   
//    public void addSpaceWithRole(Space space, String roleKey) {
//        userSpaceRoles.add(new UserSpaceRole(this, space, roleKey));        
//    }
//    
//    public void removeSpaceWithRole(Space space, String roleKey) {
//        userSpaceRoles.remove(new UserSpaceRole(this, space, roleKey));        
//    }

    public void addSpaceRole(SpaceRole spaceRole) {
        userSpaceRoles.add(new UserSpaceRole(this, spaceRole));        
    }

    public void removeSpaceRole(SpaceRole spaceRole) {
        userSpaceRoles.remove(new UserSpaceRole(this, spaceRole));        
    }

//    /**
//    * when the passed space is null this has a special significance
//     * it will return roles that are 'global'
//     */
//    public List<String> getRoleKeys(Space space) {
//        List<String> roleKeys = new ArrayList<String>();
//        for(UserSpaceRole usr : userSpaceRoles) {
//            Space s = usr.getSpace();
//            if (s == space || (s != null && s.equals(space))) {
//                roleKeys.add(usr.getRoleKey());
//            }
//        }
//        return roleKeys;
//    }


    /**
     * Get a list of roles the user has for this space    
     * @param spaceId the space id of which the roles must belong to
     * @return
     */
    public List<SpaceRole>getSpaceRoles(Long spaceId){
    	long spId = spaceId.longValue();
    	List<SpaceRole> spaceRolesList = new ArrayList<SpaceRole>();
    	for (UserSpaceRole userSpaceRole : userSpaceRoles){
    		SpaceRole spaceRole = userSpaceRole.getSpaceRole();
    		if (spaceRole.getSpace()!=null && spaceRole.getSpace().getId() == spId){
    			spaceRolesList.add(spaceRole);
    		}
    	}
    	return spaceRolesList;
    }
    /**
     * Get a list of roles the user has for this space    
     * @param space the space in which the roles must belong to
     * @return
     */
    public List<SpaceRole>getSpaceRoles(Space space){
    	return getSpaceRoles(space.getId());
    }

    /**
     * See if the user has any regular roles (including space admin) for this space
     * @return whether the user has any regular roles for this space
     */
    public boolean hasRegularRoleForSpace(Space space){
    	boolean hasRegularRole = false;
    	for (UserSpaceRole userSpaceRole : userSpaceRoles){
    		SpaceRole spaceRole = userSpaceRole.getSpaceRole();
    		if (spaceRole.getSpace()!=null 
    				&& spaceRole.getSpace().equals(space)
    				&& (spaceRole.getRoleType().equals(RoleType.REGULAR_USER)
    						|| spaceRole.getRoleType().equals(RoleType.SPACE_ADMINISTRATOR))){
    			hasRegularRole = true;
    			break;
    		}
    	}
    	return hasRegularRole;
    }
    
    /**
     * Get a list of regular roles (i.e. not anonymous or guest) 
     * the user has for this space    
     * @param space the space in which the roles must belong to
     * @return
     */
    public List<SpaceRole>getRegularSpaceRoles(Space space){
    	List<SpaceRole> spaceRolesList = getSpaceRoles(space);
    	List<SpaceRole> spaceRolesToRemove = new LinkedList<SpaceRole>();
    	if(CollectionUtils.isNotEmpty(spaceRolesList)){
    		for (SpaceRole spaceRole : spaceRolesList){
        		if (spaceRole.getRoleType().equals(RoleType.ANONYMOUS)
        				|| spaceRole.getRoleType().equals(RoleType.GUEST)){
        			spaceRolesToRemove.add(spaceRole);
        		}
        	}
    	}
    	return spaceRolesList;
    }
    
    public boolean isAllowedToCreateNewItem(Space space){
    	boolean allowed = false;
   		if(this.getPermittedTransitions(space, State.NEW).size() > 0){
   			allowed = true;
    	}
    	return allowed;
    }
    
    public List<SpaceRole>getSpaceRoles(){
    	List<SpaceRole>spaceRolesList = new ArrayList<SpaceRole>();
    	
    	for (UserSpaceRole userSpaceRole : userSpaceRoles){
    		spaceRolesList.add(userSpaceRole.getSpaceRole());
    	}//for
    	
    	return spaceRolesList;
    }
    /**
     * 
     * @param space
     * @return
     */
    private List<String> getRoleKeys(Space space) {
        List<String> roleKeys = new ArrayList<String>();
        for(UserSpaceRole usr : userSpaceRoles) {
//            Space s = usr.getSpaceRole().getSpace();
//            if (s == space || (s != null && s.equals(space))) {
//                roleKeys.add(usr.getSpaceRole().getRoleCode());
//            }
        	if (usr.getSpaceRole().getSpace()!=null && (usr.getSpaceRole().getSpace()==space || usr.getSpaceRole().getSpace().equals(space))){
	        	if (!usr.getSpaceRole().getRoleType().equals(RoleType.ADMINISTRATOR)){
	        		roleKeys.add(usr.getSpaceRole().getRoleCode());
	        	}
        	}
        }

        return roleKeys;
    }

    
    public Map<Integer, String> getPermittedTransitions(Space space, int status) {
    	List<String> roleKeys = getRoleKeys(space);
    	Metadata meta = space.getMetadata();
        Map<Integer, String> permittedTransisions = meta.getPermittedTransitions(roleKeys, status);
        return permittedTransisions;
    }
    /**
     * 
     * @param space
     * @param status
     * 		The status of the item(i.e new, open, close)
     * @return
     */
    public List<Field> getEditableFieldList(Space space, int status) {
        return space.getMetadata().getEditableFields(getRoleKeys(space), status);
    }
    

    public List<Field> getViewableFieldList(Space space, int status) {
        return space.getMetadata().getViewableFields(getRoleKeys(space), status);
    }
    public Map<Field.Name,Field> getViewableFieldMap(Space space, int status) {
        return space.getMetadata().getViewableFieldsMap(getRoleKeys(space), status);
    }

    public Set<Space> getSpaces() {
        Set<Space> spaces = new HashSet<Space>(userSpaceRoles.size());
        for (UserSpaceRole usr : userSpaceRoles) {
            if (usr.getSpaceRole().getSpace() != null) {
                spaces.add(usr.getSpaceRole().getSpace());
            }
        }
        return spaces;
    }    

    public boolean isAllocatedToSpace(long spaceId) {
        for (UserSpaceRole userSpaceRole : userSpaceRoles) {
            if (userSpaceRole.getSpaceRole().getSpace() != null && userSpaceRole.getSpaceRole().getSpace().getId() == spaceId) {
                return true;
            }
        }
        return false;
    }
    
    public int getSpaceCount() {
        return getSpaces().size();
    }


    public boolean isGlobalAdmin() {
    	for (SpaceRole spaceRole : getSpaceRoles()){
    		if (spaceRole.getRoleType().equals(RoleType.ADMINISTRATOR)){
    	    	return true;
    		}
    	}
    	return false;
    }
    public boolean isGuest() {
    	for (SpaceRole spaceRole : getSpaceRoles()){
    		if (spaceRole.getRoleType().equals(RoleType.GUEST)){
    	    	return true;
    		}
    	}
    	return false;
    }
    public boolean isAnonymous() {
    	if(this.getId() == 0){
    		return true;
    	}
    	return false;
    }
    
    public boolean isSpaceAdmin(Space space){
    	for (SpaceRole spaceRole : getSpaceRoles(space)){
    		if (spaceRole.getRoleType().equals(RoleType.SPACE_ADMINISTRATOR)){
    			return true;
    		}
    	}
    	return false;

    } 

    
    public boolean isSpaceAdminForAllOfItsSpaces(){
    	for (Space space : this.getSpaces()){
    		if (!isSpaceAdmin(space)){
    			return false;
    		}
    	}
    	return true;
    }
    
    public boolean isSpaceAdmin(){
    	return isSpaceAdminForAtLeastOneSpace();
    }
    

    private boolean isSpaceAdminForAtLeastOneSpace(){
    	for (Space space : this.getSpaces()){
    		if (isSpaceAdmin(space)){
    			return true;
    		}
    	}
    	return false;
    	
    }

    public List<Space> getSpacesWhereUserIsAdmin(){
    	List<Space> adminSpaces = new ArrayList<Space>();

		for (Space space : this.getSpaces()){
			if (this.isSpaceAdmin(space)){
				adminSpaces.add(space);
			}//if
		}//for

    	return adminSpaces;
    }
    public List<Space> getTemplateSpacesForUser(){
    	List<Space> adminSpaces = new ArrayList<Space>();
		for (Space space : this.getSpaces()){
			if (this.isSpaceAdmin(space) && space.getIsTemplate()){
				adminSpaces.add(space);
			}//if
		}//for

    	return adminSpaces;
    }

    /** 
     * This returns 'valid' (a twisted version of valid actually) spaceRoles, meaning it excludes:
     * <ul>
     * <li>Global roles (i.e. with a null space), like Administrator</li>
     * <li>More than one roles for any space</li>
     * The roles are sorted by Space name to help the dashboard. Only one role per space is returned.
     * TODO: This is beyond stupid. First of all, 
     * the roles are added to userSpaceRoleMap every time. Second, 
     * if this is for the dashboard then there must be a better way to
     * achieve whatever it tries to...
     */
    public Collection<UserSpaceRole> getSpaceRolesNoGlobal(){
        for(UserSpaceRole usr : userSpaceRoles) {
            if(usr.getSpaceRole().getSpace() != null) {
            	SpaceRole sr = usr.getSpaceRole();
            	Space space = sr.getSpace();
            	if(StringUtils.isNotBlank(space.getName())){
                    userSpaceRoleMap.put(space.getName(), usr);
            	}
            }
        }
        return userSpaceRoleMap.values();
    }        

    public boolean isGuestForSpace(Space space) {
        if (id == 0) {
            return true;
        }
        for(UserSpaceRole usr : getUserSpaceRolesBySpaceId(space.getId())) {
            if(usr.getSpaceRole().getRoleType().equals(RoleType.GUEST)) {
                return true;
            }
        }
        return false;
    }
    
    public List<UserSpaceRole> getRegularUserSpaceRoles(){
    	List<UserSpaceRole> regularUserSpaceRoles = new ArrayList<UserSpaceRole>();
    	
    	if (this.userSpaceRoles!=null){
    		for (UserSpaceRole userSpaceRole : this.userSpaceRoles){
    			if (!userSpaceRole.getSpaceRole().getRoleType().equals(RoleType.ADMINISTRATOR)){
    				regularUserSpaceRoles.add(userSpaceRole);
    			}//if
    		}//for
    	}//if
    	
    	return regularUserSpaceRoles;
    }
    
    public Map<StdField.Field, StdFieldMask> getStdFieldsForSpace(Space space){

    	Map<StdField.Field, StdFieldMask>  stdFieldForSpaceMap = new LinkedHashMap<StdField.Field, StdFieldMask>();

		for (RoleSpaceStdField roleSpaceStdField : this.roleSpaceStdFieldList){
			if (roleSpaceStdField.getSpaceRole().getSpace().equals(space)){
				if (stdFieldForSpaceMap.containsKey(roleSpaceStdField.getStdField().getField())){
					//Get the "best" case
					if (roleSpaceStdField!=null && 
							roleSpaceStdField.getStdField()!=null && 
							roleSpaceStdField.getStdField().getField()!=null && 
							stdFieldForSpaceMap.get(roleSpaceStdField.getStdField().getField())!=null &&
							stdFieldForSpaceMap.get(roleSpaceStdField.getStdField().getField()).getMask()!=null && 
							roleSpaceStdField.getFieldMask()!=null && 
							roleSpaceStdField.getFieldMask().getMask()!=null){
						if (stdFieldForSpaceMap.get(roleSpaceStdField.getStdField().getField()).getMask().ordinal() > roleSpaceStdField.getFieldMask().getMask().ordinal()){
							stdFieldForSpaceMap.put(roleSpaceStdField.getStdField().getField(), roleSpaceStdField.getFieldMask());
						}//if
					}//if
				}//if
				else{
					stdFieldForSpaceMap.put(roleSpaceStdField.getStdField().getField(), roleSpaceStdField.getFieldMask());
				}//else
			}//if
		}//for

    	return stdFieldForSpaceMap;
    }

    public List<RoleSpaceStdField> getStdFields(){    	
    	return roleSpaceStdFieldList;
    }
    

    private Collection<UserSpaceRole> getUserSpaceRolesBySpaceId(long spaceId) {
        List<UserSpaceRole> list = new ArrayList<UserSpaceRole>();
        for (UserSpaceRole usr : userSpaceRoles) {
            if (usr.getSpaceRole().getSpace() != null && usr.getSpaceRole().getSpace().getId() == spaceId) {
                list.add(usr);
            }
        }
        return list;
    }    
    
    //============ ACEGI UserDetails implementation ===============
    
    public boolean isAccountNonExpired() {
        return true;
    }
    
    public boolean isAccountNonLocked() {
        return !isLocked();
    }
    
//    public GrantedAuthority[] getAuthorities() {
//        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//        // grant full access only if not a Guest
//        if (id > 0) {
//            authorities.add(new UserSpaceRole(this, null, "ROLE_USER"));
//        }
//        for (UserSpaceRole usr : userSpaceRoles) {            
//            authorities.add(usr);
//        }
//        return authorities.toArray(new GrantedAuthority[authorities.size()]);
//    }

    public GrantedAuthority[] getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        // grant full access only if not a Guest
        if (id > 0) {
            //authorities.add(new UserSpaceRole(this, new SpaceRole(Constants.ReservedRoles.REGULAR_USER.getId(), null, Constants.ReservedRoles.GUEST.getDescription())));
        	new SpaceRole(null, "", RoleType.REGULAR_USER);
        }
        for (UserSpaceRole usr : userSpaceRoles) {            
            authorities.add(usr);
        }
        return authorities.toArray(new GrantedAuthority[authorities.size()]);
    }
    
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    public String getUsername() {
        return getLoginName();
    }
    
    public String getPassword() {
        return password;
    } 
    
    //=============================================================    

    public Set<UserSpaceRole> getUserSpaceRoles() {
        return userSpaceRoles;
    }

    public void setUserSpaceRoles(Set<UserSpaceRole> userSpaceRoles) {
        this.userSpaceRoles = userSpaceRoles;
    }    
    
    public User getParent() {
        return parent;
    }
    
    public void setParent(User parent) {
        this.parent = parent;
    }

    /* (non-Javadoc)
	 * @see gr.abiss.calipso.domain.IUser#getName()
	 */
    public String getName() {
        return name;
    }

    public String getFullName() {
        return name + " "+lastname;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    /* (non-Javadoc)
	 * @see gr.abiss.calipso.domain.IUser#getEmail()
	 */
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
	 * @return the emailHash
	 */
	public String getEmailHash() {
		return emailHash;
	}

	/**
	 * @param emailHash the emailHash to set
	 */
	public void setEmailHash(String emailHash) {
		this.emailHash = emailHash;
	}

	/**
	 * @return the gravatar
	 */
	public boolean isGravatar() {
		return gravatar;
	}

	/**
	 * @param gravatar the gravatar to set
	 */
	public void setGravatar(boolean gravatar) {
		this.gravatar = gravatar;
	}

	/* (non-Javadoc)
	 * @see gr.abiss.calipso.domain.IUser#getLocale()
	 */
    public String getLocale() {
        return locale;
    }
    
    public void setLocale(String locale) {
        this.locale = locale;
    }    
    
    public boolean isLocked() {
        return locked;
    }
    
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
//    public Metadata getMetadata() {
//        return metadata;
//    }
//    
//    public void setMetadata(Metadata metadata) {
//        this.metadata = metadata;
//    }
//    
    /* (non-Javadoc)
	 * @see gr.abiss.calipso.domain.IUser#getId()
	 */
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public Integer getType() {
        return type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    /* (non-Javadoc)
	 * @see gr.abiss.calipso.domain.IUser#getLoginName()
	 */
    public String getLoginName() {
        return loginName;
    }
    
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
    
    @Override
    public String toString() {
		return new ToStringBuilder(this)
			.append("id", this.getId())
			.append("name", this.getName())
			.append("lastname", this.getLastname())
			.append("loginName", this.getLoginName())
			.append("organization", this.getOrganization())
			.append("dateCreated", this.getDateCreated())
			.toString();
    }
    
    @Override
    public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o == this) { return true; }
		if (!(o instanceof User)) {
			return false;
		}
        User other = (User) o;
        return new EqualsBuilder()
	        .append(this.getLoginName(), other.getLoginName())
	        .append(this.getDateCreated(), other.getDateCreated())
	        .isEquals();
    }
    
    @Override
    public int hashCode() {
        if(loginName == null) {
            return 0;
        }
        return loginName.hashCode();
    }

	public List<RoleSpaceStdField> getRoleSpaceStdFieldList() {
		return roleSpaceStdFieldList;
	}

	public void setRoleSpaceStdFieldList(
			List<RoleSpaceStdField> roleSpaceStdFieldList) {
		this.roleSpaceStdFieldList = roleSpaceStdFieldList;
	}
 
	/* (non-Javadoc)
	 * @see gr.abiss.calipso.domain.IUser#getOrganization()
	 */
	public Organization getOrganization() {
		return organization;
	}
	
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	/* (non-Javadoc)
	 * @see gr.abiss.calipso.domain.IUser#getLastname()
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * @return the Postal Code
	 */
	public String getZip() {
		return zip;
	}

	/**
	 * @param postalCode the Postal Code to set
	 */
	public void setZip(String postalCode) {
		this.zip = postalCode;
	}

	
	
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the alternativePhone
	 */
	public String getAlternativePhone() {
		return alternativePhone;
	}

	/**
	 * @param alternativePhone the alternativePhone to set
	 */
	public void setAlternativePhone(String alternativePhone) {
		this.alternativePhone = alternativePhone;
	}

	/* (non-Javadoc)
	 * @see gr.abiss.calipso.domain.IUser#getFax()
	 */
	public String getFax() {
		return fax;
	}

	/**
	 * @param fax the fax to set
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}

	/**
	 * @return the dateCreated
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * @return the dateLastUpdated
	 */
	public Date getDateLastUpdated() {
		return dateLastUpdated;
	}

	/**
	 * @param dateLastUpdated the dateLastUpdated to set
	 */
	public void setDateLastUpdated(Date dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
	}

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the lastUpdatedBy
	 */
	public User getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	/**
	 * @param lastUpdatedBy the lastUpdatedBy to set
	 */
	public void setLastUpdatedBy(User lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	/* (non-Javadoc)
	 * @see gr.abiss.calipso.domain.IUser#getCountry()
	 */
	public Country getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(Country country) {
		this.country = country;
	}
	public String getDetails(){
		return new StringBuffer()
		.append("Name =")
		.append(getName())
		.append(" Last Name = ")
		.append(getLastname())
		.append(" Login Name = ")
		.append(getLoginName())
		.append(" Fax = ")
		.append(getFax())
		.toString();
	}
	public String getDisplayValue(){
		
    	StringBuffer s = new StringBuffer()
			.append(this.getName())
			.append(" ")
			.append(this.getLastname())
			.append(" (")
			.append(this.getLoginName())
			.append(")");
    	if(this.getOrganization() != null){
    		s.append(", ")
    			.append(this.getOrganization().getName());
    	}
        return s.toString();
    }
}