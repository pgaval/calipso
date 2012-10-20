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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

public class SpaceRole implements Serializable{
	private static final Logger logger = Logger.getLogger(SpaceRole.class);
	private long id;
	private Space space;
	private String description;
	private int roleTypeId;
	private String roleCode;
	private Set<UserSpaceRole> userSpaceRoles;
	private Set<RoleSpaceStdField> roleSpaceStdFields;
	private Map<String, RenderingTemplate> itemRenderingTemplates = new HashMap<String, RenderingTemplate>();

	public SpaceRole() {
		this.userSpaceRoles = new LinkedHashSet<UserSpaceRole>();
		this.roleSpaceStdFields = new LinkedHashSet<RoleSpaceStdField>();
	}

	public SpaceRole(long id, Space space, String description, RoleType roleType) {
		this.id = id;
		this.space = space;
		this.description = description;
		this.roleTypeId = roleType.getId();
		this.roleCode = createRoleCode();
		this.userSpaceRoles = new LinkedHashSet<UserSpaceRole>();
		this.roleSpaceStdFields = new LinkedHashSet<RoleSpaceStdField>();
	}

	public SpaceRole(Space space, String description, RoleType roleType){
		this.space = space;
		this.description = description;
		this.roleTypeId = roleType.getId();
		this.roleCode = createRoleCode();
		this.userSpaceRoles = new LinkedHashSet<UserSpaceRole>();
		this.roleSpaceStdFields = new LinkedHashSet<RoleSpaceStdField>();
	} 
	
	public SpaceRole(long id, Space space, RoleType roleType){
		this.id = id;
		this.space = space;
		this.roleTypeId = roleType.getId();
		this.roleCode = createRoleCode();
		this.userSpaceRoles = new LinkedHashSet<UserSpaceRole>();
		this.roleSpaceStdFields = new LinkedHashSet<RoleSpaceStdField>();
	} 
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}

	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public int getRoleTypeId() {
		return roleTypeId;
	}

	public void setRoleTypeId(int roleTypeId) {
		this.roleTypeId = roleTypeId;
	}
	
	public String getIdAsString(){
		try{
			return String.valueOf(this.id);
		}
		catch (Exception e) {
			return "0";
		}
	}
	
	public RoleType getRoleType(){
		return RoleType.TYPESMAP.get(roleTypeId+"");
	}
	
	public String getRoleCode() {		
		return this.roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public void setUserSpaceRoles(Set<UserSpaceRole> userSpaceRoles) {
		this.userSpaceRoles = userSpaceRoles;
	}

	public Set<UserSpaceRole> getUserSpaceRoles() {
		return this.userSpaceRoles;
	}

	public void add(UserSpaceRole userSpaceRole){
		this.userSpaceRoles.add(userSpaceRole);
	} 

	public boolean remove(UserSpaceRole userSpaceRole){
		return this.userSpaceRoles.remove(userSpaceRole);
	} 

	public void setRoleSpaceStdFields(Set<RoleSpaceStdField> roleSpaceStdFields) {
		this.roleSpaceStdFields = roleSpaceStdFields;
	}

	public Set<RoleSpaceStdField> getRoleSpaceStdFields() {
		return this.roleSpaceStdFields;
	}

	public void add(RoleSpaceStdField roleSpaceStdField){
		this.roleSpaceStdFields.add(roleSpaceStdField);
	}

	public boolean remove(RoleSpaceStdField roleSpaceStdField){
		return this.roleSpaceStdFields.remove(roleSpaceStdField);
	}
	

	public Map<String, RenderingTemplate>  getItemRenderingTemplates() {
		return itemRenderingTemplates;
	}
	
	public void setItemRenderingTemplates(Map<String, RenderingTemplate> itemTemplates) {
		this.itemRenderingTemplates = itemTemplates;
	}
	
	public void addItemTemplate(String stage, RenderingTemplate itemTemplate) {
		this.itemRenderingTemplates.put(stage, itemTemplate);
	}

	private String createRoleCode(){
		if (this.space!=null){
			return this.space.getPrefixCode() + "_" + String.valueOf((this.description!= null?this.description:"").hashCode()) + "_" + String.valueOf(this.hashCode());
		}
		else{
			return String.valueOf(this.description.hashCode()) + "_" + String.valueOf(this.hashCode());
		}		
	}

	@Override
	public boolean equals(Object object) {
		//Not yet created => Compare codes 
		if (this.id==0){
			return (((SpaceRole)object).getRoleCode().equals(this.getRoleCode()));
		}

		return (((SpaceRole)object).getId()==this.id);
	}
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id", id)
			.append("roleTypeId", roleTypeId)
			.append("roleCode", roleCode)
			.append("description", description)
			.toString();
	}
	
}