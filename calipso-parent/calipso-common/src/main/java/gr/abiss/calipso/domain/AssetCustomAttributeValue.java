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

import org.apache.commons.lang.builder.EqualsBuilder;

public class AssetCustomAttributeValue implements Serializable {
/*
	private static final long serialVersionUID = 1L;

	private Long id;
	private Asset asset;
	private AssetTypeCustomAttribute customAttribute;
	private String attributeValue;
	
	///////////////////////////////////////////////////////////////
	
	public AssetCustomAttributeValue() {
		
	}
	
	//----------------------------------------------------------------------------------------------------------------------------
	
	public AssetCustomAttributeValue(Long id, Asset asset,
			AssetTypeCustomAttribute customAttribute, String attributeValue) {
		this.id = id;
		this.asset = asset;
		this.customAttribute = customAttribute;
		this.attributeValue = attributeValue;
	}

	
	//----------------------------------------------------------------------------------------------------------------------------
	
	public AssetCustomAttributeValue(Asset asset, AssetTypeCustomAttribute customAttribute, String value) {
		this.id = new Long(0);
		this.asset = asset;
		this.customAttribute = customAttribute;
		this.attributeValue = value;
	}

	//----------------------------------------------------------------------------------------------------------------------------
	
	public AssetCustomAttributeValue(AssetTypeCustomAttribute customAttribute, String value) {
		this.id = new Long(0);
		this.asset = null;
		this.customAttribute = customAttribute;
		this.attributeValue = value;
	}
	
	///////////////////////////////////////////////////////////////
	
	public Long getId() {
		return id;
	}

	//--------------------------------------------------------------

	public void setId(Long id) {
		this.id = id;
	}
	
	//--------------------------------------------------------------

	public Asset getAsset() {
		return asset;
	}

	//--------------------------------------------------------------
	
	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	//--------------------------------------------------------------
	
	public AssetTypeCustomAttribute getCustomAttribute() {
		return customAttribute;
	}

	//--------------------------------------------------------------
	
	public void setCustomAttribute(AssetTypeCustomAttribute customAttribute) {
		this.customAttribute = customAttribute;
	}

	//--------------------------------------------------------------
	
	public String getAttributeValue() {
		return attributeValue;
	}

	//--------------------------------------------------------------
	
	public void setAttributeValue(String value) {
		this.attributeValue = value;
	}

	//--------------------------------------------------------------
	
	@Override
	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o == this) { return true; }
		if (!(o instanceof AssetCustomAttributeValue)) {
			return false;
		}
		// for some reason, this only works
		// when using getters for the 'other',
		// maybe because of Hibernate proxies
		AssetCustomAttributeValue other = (AssetCustomAttributeValue) o;
		return new EqualsBuilder()
			.append(this.id, other.getId())
	        .append(this.customAttribute, other.getCustomAttribute())
	        .append(this.asset, other.getAsset())
	        .isEquals();
	}
	
	//--------------------------------------------------------------
	

	public boolean hasValue(){
		boolean hasValue = this.getAttributeValue()!=null && !this.getAttributeValue().trim().equals("");
		
		if (this.getCustomAttribute().getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)){
			hasValue = hasValue && !this.getAttributeValue().equals("-1");
		}//if
		
		return hasValue;
	} 
	
	//--------------------------------------------------------------
	
	public static boolean hasValue(AssetTypeCustomAttribute assetTypeCustomAttribute, String value){
		boolean hasValue = value!=null && !value.trim().equals("");
		
		if (assetTypeCustomAttribute.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)){
			hasValue = hasValue && !value.equals("-1");
		}//if
		
		return hasValue;
	}
	*/
}//AssetCustomAttributeValue
