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

import gr.abiss.calipso.domain.i18n.AbstractI18nResourceTranslatable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

/**
 * A Calipso installation can be divided into different project
 * areas or workspaces. Each Space features it's own workflow, 
 * user roles, assets and so on.
 * The Metadata of a Space determines the workflow, roles and role 
 * rights for each form field per workflow state.
 */
public class Space extends AbstractI18nResourceTranslatable implements Serializable {
	
	private static final Logger logger = Logger.getLogger(Space.class);

	// Item visibility modes
	public static final Short ITEMS_VISIBLE_TO_REGULAR_ROLES = 1;
	public static final Short ITEMS_INVISIBLE_TO_ANONYMOUS_REPORTERS = 6;
	public static final Short ITEMS_VISIBLE_TO_LOGGEDIN_REPORTERS_NO_COMMENTS = 5;
	public static final Short ITEMS_VISIBLE_TO_LOGGEDIN_REPORTERS = 2;
	public static final Short ITEMS_VISIBLE_TO_ANY_LOGGEDIN_USER = 3;
	public static final Short ITEMS_VISIBLE_TO_ANONYMOUS_USERS = 4;
	public static List<Short> ITEM_VISIBILITY_MODES_LIST = new ArrayList<Short>(4);
	
	// Asset visibility modes. The assets are only manageable by
	// space admins of the space they where created for, although 
	// their visibility may expand to the space group or 
	// the application as a whole.
	public static final Short ASSETS_NOT_USED = 1;
	public static final Short ASSETS_VISIBLE_TO_OWNER_SPACE = 2;
	public static final Short ASSETS_VISIBLE_TO_SPACEGROUP_SPACES = 3;
	public static final Short ASSETS_VISIBLE_TO_ANY_SPACE = 4;// TODO: for future use
	public static List<Short> ASSET_VISIBILITY_MODES_LIST = new ArrayList<Short>(4);

	static{
		// init item visibility 
		ITEM_VISIBILITY_MODES_LIST.add(ITEMS_VISIBLE_TO_REGULAR_ROLES);
		ITEM_VISIBILITY_MODES_LIST.add(ITEMS_INVISIBLE_TO_ANONYMOUS_REPORTERS);
		ITEM_VISIBILITY_MODES_LIST.add(ITEMS_VISIBLE_TO_ANONYMOUS_USERS);
		ITEM_VISIBILITY_MODES_LIST.add(ITEMS_VISIBLE_TO_LOGGEDIN_REPORTERS);
		ITEM_VISIBILITY_MODES_LIST.add(ITEMS_VISIBLE_TO_LOGGEDIN_REPORTERS_NO_COMMENTS);
		ITEM_VISIBILITY_MODES_LIST.add(ITEMS_VISIBLE_TO_ANY_LOGGEDIN_USER);
		ITEM_VISIBILITY_MODES_LIST = Collections.unmodifiableList(ITEM_VISIBILITY_MODES_LIST);
		// init asset visibility 
		ASSET_VISIBILITY_MODES_LIST.add(ASSETS_NOT_USED);
		ASSET_VISIBILITY_MODES_LIST.add(ASSETS_VISIBLE_TO_OWNER_SPACE);
		ASSET_VISIBILITY_MODES_LIST.add(ASSETS_VISIBLE_TO_SPACEGROUP_SPACES);
		ASSET_VISIBILITY_MODES_LIST.add(ASSETS_VISIBLE_TO_ANY_SPACE);
		ASSET_VISIBILITY_MODES_LIST = Collections.unmodifiableList(ASSET_VISIBILITY_MODES_LIST);
	}
    
    private long id;
    private Boolean published = Boolean.FALSE;
    private int version;
    private Integer type;
    private String prefixCode;
    private String name;
    private String description;
    private Long defaultDuration;
    private Date closingDate = null;
    private Short itemVisibility = ITEMS_VISIBLE_TO_REGULAR_ROLES;
    private Short assetVisibility = ASSETS_NOT_USED;
    private Boolean isTemplate = Boolean.FALSE;
    private boolean itemSummaryEnabled = true;
    private Boolean simpleAttachmentsSupport = Boolean.TRUE;
    private Boolean itemDetailCommentEnabled = Boolean.TRUE;
    private SpaceSequence spaceSequence;
    private SpaceGroup spaceGroup;
    private Metadata metadata;
    private Set<Asset> assets;
    private Set<RoleSpaceStdField> roleSpaceStdFields;
    private Set<SpaceRole> spaceRoles;
    private List<ItemRenderingTemplate> itemRenderingTemplates = new LinkedList<ItemRenderingTemplate>();
    private List<Language> supportedLanguages = new LinkedList<Language>();
	
    public Space() {
        spaceSequence = new SpaceSequence();
        spaceSequence.setSpace(this);
        metadata = new Metadata();
    }
    
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }     
    
    public SpaceSequence getSpaceSequence() {
        return spaceSequence;
    }

    public void setSpaceSequence(SpaceSequence spaceSequence) {
        this.spaceSequence = spaceSequence;
    }    
    
    public String getPrefixCode() {
        return prefixCode;
    }

    public void setPrefixCode(String prefixCode) {
        this.prefixCode = prefixCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }    
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    

	@Override
	public String getI18nId() {
		return new Long(this.getId()).toString();
	}

    public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }        
    
    /**
	 * @return the visibility
	 */
	public Short getItemVisibility() {
		return itemVisibility;
	}

	/**
	 * @param visibility the visibility to set
	 */
	public void setItemVisibility(Short visibility) {
		this.itemVisibility = visibility;
	}

	/**
	 * @return the assetVisibility
	 */
	public Short getAssetVisibility() {
		return assetVisibility;
	}

	/**
	 * @param assetVisibility the assetVisibility to set
	 */
	public void setAssetVisibility(Short assetVisibility) {
		this.assetVisibility = assetVisibility;
	}

	public Boolean getIsTemplate() {
		return isTemplate;
	}

	public void setIsTemplate(Boolean isTemplate) {
		this.isTemplate = isTemplate;
	}

	/**
	 * Whether logged-in users have access to space items. 
	 * This will return true even if logged-in users only have access
	 * to Items reported by them.
	 * @return
	 */
	public boolean isGuestAllowed() {
        return ITEMS_VISIBLE_TO_ANY_LOGGEDIN_USER.equals(this.itemVisibility) 
            	|| ITEMS_VISIBLE_TO_LOGGEDIN_REPORTERS.equals(this.itemVisibility)
            	|| ITEMS_VISIBLE_TO_LOGGEDIN_REPORTERS_NO_COMMENTS.equals(this.itemVisibility);
    }

    /**
	 * @return the anonymousAllowed
	 */
	public boolean isAnonymousAllowed() {
        return ITEMS_VISIBLE_TO_ANONYMOUS_USERS.equals(this.itemVisibility)
        		|| ITEMS_INVISIBLE_TO_ANONYMOUS_REPORTERS.equals(this.itemVisibility);
	}
    
	public boolean isAssetEnabled() {
		return !this.assetVisibility.equals(ASSETS_NOT_USED);
	}
    
    /**
	 * @return the spaceGroup
	 */
	public SpaceGroup getSpaceGroup() {
		return spaceGroup;
	}

	/**
	 * @param spaceGroup the spaceGroup to set
	 */
	public void setSpaceGroup(SpaceGroup spaceGroup) {
		this.spaceGroup = spaceGroup;
	}

	@Override
    public String toString() {
    	return new ToStringBuilder(this)
	        .append("id", this.id)
	        .append("name", this.name)
	        .append("space Group", this.spaceGroup)
	        .append("prefixCode", this.prefixCode)
	        .append("description", this.description)
	        .append("spaceRoles", this.getSpaceRoles())
	        .toString();
    }
    
    @Override
	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o == this) { return true; }
		if (!(o instanceof Space)) {
			return false;
		}
		Space other = (Space) o;
		return new EqualsBuilder()
	        .append(this.getName(), other.getName())
	        .append(this.getPrefixCode(), other.getPrefixCode())
	        .isEquals();
	}
	
    @Override
    public int hashCode() {
        return new HashCodeBuilder(33, 77)
	        .append(this.name)
	        .append(this.prefixCode)
	        .toHashCode();
    } 

//Assets ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/**
	 * Get the assets for this space
	 * @return the Space assets
	 */
	public Set<Asset> getAssets() {
		return this.assets;
	}

	/**
	 * @param assets the assets to set
	 */
	public void setAssets(Set<Asset> assets) {
		this.assets = assets;
	}

//Roles Space Standard fields ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Set<RoleSpaceStdField> getRoleSpaceStdFields() {
		return roleSpaceStdFields;
	}
	
//-------------------------------------------------------------------------------------------------
	
	public void setRoleSpaceStdFields(Set<RoleSpaceStdField> roleSpaceStdFields) {
		this.roleSpaceStdFields = roleSpaceStdFields;
	}

//-------------------------------------------------------------------------------------------------
	
	public void add(RoleSpaceStdField roleSpaceStdField){
		if (this.roleSpaceStdFields==null){
			this.roleSpaceStdFields = new LinkedHashSet<RoleSpaceStdField>();
		}
		this.roleSpaceStdFields.add(roleSpaceStdField);
	}

//-------------------------------------------------------------------------------------------------
	
	public void remove(RoleSpaceStdField roleSpaceStdField){
		if (this.roleSpaceStdFields!=null){
			this.roleSpaceStdFields.remove(roleSpaceStdField);
		}
	}

// Space Roles ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Set<SpaceRole> getSpaceRoles(){
		return this.spaceRoles;
	}
	/**
	 * Get the Set of SpaceRoles as a List
	 * @return
	 */
	public List<SpaceRole> getSpaceRolesList(){
		List<SpaceRole> roleList;
		if(this.spaceRoles == null){
			roleList = new ArrayList<SpaceRole>(0);
		}
		else{
			roleList = new ArrayList<SpaceRole>(this.spaceRoles.size());
			roleList.addAll(this.spaceRoles);
		}
		return roleList;
	}
	/**
	 * Get the Set of SpaceRoles as a List excluding Space Admin
	 * @return
	 */
	public List<SpaceRole> getSpaceRolesListWithoutSpaceAdmin(){
		List<SpaceRole> roleList = new LinkedList<SpaceRole>();
		if(this.spaceRoles != null && this.spaceRoles.size() > 0){
			for(SpaceRole sr : this.spaceRoles){
				if(!sr.getRoleType().equals(RoleType.SPACE_ADMINISTRATOR)){
					roleList.add(sr);
				}
			}
		}
		return roleList;
	}
	
//-------------------------------------------------------------------------------------------------
	
	public void setSpaceRoles(Set<SpaceRole>  spaceRoles){
		this.spaceRoles = spaceRoles;
	}
	
//-------------------------------------------------------------------------------------------------
	
	public void add(SpaceRole spaceRole){
		if (this.spaceRoles==null){
			this.spaceRoles = new LinkedHashSet<SpaceRole>();
		}
		this.spaceRoles.add(spaceRole);
	} 
//-------------------------------------------------------------------------------------------------

	public void remove(SpaceRole spaceRole){
		if (this.spaceRoles!=null){
			this.spaceRoles.remove(spaceRole);
		}
	}
	public void removeSpaceRoles(Collection<SpaceRole> spaceRolesToRemove){
		if (this.spaceRoles!=null){
			this.spaceRoles.removeAll(spaceRolesToRemove);
		}
	}

	
	public List<ItemRenderingTemplate> getItemRenderingTemplates() {
		return itemRenderingTemplates;
	}

//-------------------------------------------------------------------------------------------------
	
	public void setItemRenderingTemplates(List<ItemRenderingTemplate> itemRenderingTemplates) {
		this.itemRenderingTemplates = itemRenderingTemplates;
	}
	
	public Boolean getSimpleAttachmentsSupport() {
		return simpleAttachmentsSupport;
	}

	public void setSimpleAttachmentsSupport(Boolean simpleAttachmentsSupport) {
		this.simpleAttachmentsSupport = simpleAttachmentsSupport;
	}

	public Boolean getItemDetailCommentEnabled() {
		return itemDetailCommentEnabled;
	}

	public void setItemDetailCommentEnabled(Boolean itemDetailCommentEnabled) {
		this.itemDetailCommentEnabled = itemDetailCommentEnabled;
	}

	/**
	 * @return the supportedLanguages
	 */
	public List<Language> getSupportedLanguages() {
		return supportedLanguages;
	}

	/**
	 * @param supportedLanguages the supportedLanguages to set
	 */
	public void setSupportedLanguages(List<Language> supportedLanguages) {
		this.supportedLanguages = supportedLanguages;
	}

	/**
	 * @return the defaultDuration
	 */
	public Long getDefaultDuration() {
		return defaultDuration;
	}

	/**
	 * @param defaultDuration the defaultDuration to set
	 */
	public void setDefaultDuration(Long defaultDuration) {
		this.defaultDuration = defaultDuration;
	}

	public Date getClosingDate() {
		return closingDate;
	}

	public void setClosingDate(Date closingDate) {
		this.closingDate = closingDate;
	}

	public boolean isItemSummaryEnabled() {
		return itemSummaryEnabled;
	}

	public void setItemSummaryEnabled(boolean itemSummaryEnabled) {
		this.itemSummaryEnabled = itemSummaryEnabled;
	}

	public boolean isNew(){
		return this.getId() == 0;
	}

//-------------------------------------------------------------------------------------------------
	
	public void add(ItemRenderingTemplate itemRenderingTemplate){
		if (this.itemRenderingTemplates==null){
			this.itemRenderingTemplates = new LinkedList<ItemRenderingTemplate>();
		}
		itemRenderingTemplate.setSpace(this);
		this.itemRenderingTemplates.add(itemRenderingTemplate);
	}
	
//-------------------------------------------------------------------------------------------------

	public void remove(ItemRenderingTemplate itemRenderingTemplate){
		if (this.itemRenderingTemplates!=null){
			boolean success = this.itemRenderingTemplates.remove(itemRenderingTemplate);
			itemRenderingTemplate.setSpace(null);
		}
	}


	public void addAll(List<ItemRenderingTemplate> tmplList) {
		if(CollectionUtils.isNotEmpty(tmplList)){
			if(this.getItemRenderingTemplates() == null){
				this.setItemRenderingTemplates(new LinkedList<ItemRenderingTemplate>());
			}
			for(ItemRenderingTemplate tmpl : tmplList){
				this.add(tmpl);
			}
		}
		
	}

}