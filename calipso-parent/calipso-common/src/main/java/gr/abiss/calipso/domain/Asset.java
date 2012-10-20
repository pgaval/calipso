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
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

public class Asset implements Serializable {
	
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(Asset.class);
	private Long id = null;
	private String validationExpression;
	private String inventoryCode;
	private Date dateCreated;
	private Date dateUpdated;
	private Date supportStartDate;
	private Date supportEndDate;
	private AssetType assetType;
	private Space space;
	private User createdBy;
	private SortedMap<AssetTypeCustomAttribute, String> customAttributes = new TreeMap<AssetTypeCustomAttribute, String>();

	public Asset() {
		
	}
	public Asset(AssetType assetType) {
		this.assetType = assetType;
	}
	
	public Asset(Long id, String inventoryCode, Date supportStartDate, Date supportEndDate, AssetType type, Space space, SortedMap<AssetTypeCustomAttribute, String> customAttributes) {
		this.id = id;
		this.inventoryCode=inventoryCode;
		this.supportStartDate=supportStartDate;
		this.supportEndDate=supportEndDate;
		this.assetType = type;
		this.space=space;
		this.customAttributes=customAttributes;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}


	/**
	 * @return the inventoryCode
	 */
	public String getInventoryCode() {
		return this.inventoryCode;
	}

	/**
	 * @param inventoryCode the inventoryCode to set
	 */
	public void setInventoryCode(String inventoryCode) {
		this.inventoryCode = inventoryCode;
	}

	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date created) {
		this.dateCreated = created;
	}
	public Date getDateUpdated() {
		return dateUpdated;
	}
	public void setDateUpdated(Date updated) {
		this.dateUpdated = updated;
	}
	/**
	 * @return the supportStartDate
	 */
	public Date getSupportStartDate() {
		return this.supportStartDate;
	}

	/**
	 * @param supportStartDate the supportStartDate to set
	 */
	public void setSupportStartDate(Date supportStartDate) {
		this.supportStartDate = supportStartDate;
	}

	/**
	 * @return the supportEndDate
	 */
	public Date getSupportEndDate() {
		return this.supportEndDate;
	}

	/**
	 * @param supportEndDate the supportEndDate to set
	 */
	public void setSupportEndDate(Date supportEndDate) {
		this.supportEndDate = supportEndDate;
	}

	/**
	 * @return the assetType
	 */
	public AssetType getAssetType() {
		return this.assetType;
	}

	/**
	 * @param assetType the type to set
	 */
	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}

	/**
	 * @return the space
	 */
	public Space getSpace() {
		return this.space;
	}

	/**
	 * @param space the space to set
	 */
	public void setSpace(Space space) {
		this.space = space;
	}
	
	public User getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
	public String getDisplayedValue(){
		return getInventoryCode() + "("+ getAssetType().getName() + ")";
	}

	/**
	 * @return the customAttributes
	 */
	public Map<AssetTypeCustomAttribute, String> getCustomAttributes() {
		return this.customAttributes;
	}

	/**
	 * @param customAttributes the customAttributes to set
	 */
	public void setCustomAttributes(SortedMap<AssetTypeCustomAttribute, String> customAttributes) {
		this.customAttributes = customAttributes;
	}

	public String addOrReplaceCustomAttribute(AssetTypeCustomAttribute attr, String value){
		if(this.getCustomAttributes() == null){
			this.setCustomAttributes(new TreeMap<AssetTypeCustomAttribute,String>());
		}
		return this.getCustomAttributes().put(attr, value);
	}
	
	public void addOrReplaceCustomAttributes(Map<AssetTypeCustomAttribute,String> attrsMap){
		if(attrsMap != null && attrsMap.size() > 0){
			for(AssetTypeCustomAttribute attr : attrsMap.keySet()){
				addOrReplaceCustomAttribute(attr, attrsMap.get(attr));
			}
		}
	}
	

	public String getValidationExpression() {
		return validationExpression;
	}

	public void setValidationExpression(String validationExpression) {
		this.validationExpression = validationExpression;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id", this.getId())
			.append("inventoryCode", this.getInventoryCode())
			.append("customAttributes", this.getCustomAttributes())
			.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o == this) { return true; }
		if (!(o instanceof Asset)) {
			return false;
		}
		// for some reason, this only works
		// when using getters for the 'other',
		// maybe because of Hibernate proxies
		Asset other = (Asset) o;
		return new EqualsBuilder()
			.append(this.getInventoryCode(), other.getInventoryCode())
	        .append(this.getAssetType(), other.getAssetType())
	        .isEquals();
	}
	

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 53)
		.append(this.getInventoryCode())
		.append(this.getAssetType())
	        .toHashCode();
	}
	
}