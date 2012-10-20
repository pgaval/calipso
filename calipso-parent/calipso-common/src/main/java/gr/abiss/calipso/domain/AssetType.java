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

import gr.abiss.calipso.domain.i18n.AbstractI18nResourceTranslatable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Class to represent the type Asset instances belong to. Mainly removes redundancy 
 * for the Asset name and definition of custom attributes.
 * @author manos
 */
public class AssetType extends AbstractI18nResourceTranslatable implements Serializable {
	
	/**
	 * The AssetType ID
	 */
	private long id;
	
	/**
	 * The AssetType name
	 */
	private String name;
	
	/**
	 * The rendering template used for printing. This is usually a velocity or freemarker HTML
	 * template of which the result is forwarded to xhtmlrenderer for PDF export.
	 */
	private RenderingTemplate printingTemplate;
	
	/**
	 * The allowed set of AssetTypeCustomAttribute instances for this AssetType
	 */
	private SortedSet<AssetTypeCustomAttribute> allowedCustomAttributes = new TreeSet<AssetTypeCustomAttribute>();
	
	/**
	 * The Asset instances belonging to this AssetType
	 */
	private Set<Asset> assets;
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Default constructor
	 */
	public AssetType() {
		super();
	}

	/**
	 * @param id
	 * @param name
	 * @param allowedCustomAttributes
	 * @param assets
	 */
	public AssetType(long id, String name,
			SortedSet<AssetTypeCustomAttribute> allowedCustomAttributes,
			Set<Asset> assets) {
		super();
		this.id = id;
		this.name = name;
		this.allowedCustomAttributes = allowedCustomAttributes;
		this.assets = assets;
	}

	
	/**
	 * @param id
	 * @param name
	 * @param allowedCustomAttributes
	 */
	public AssetType(long id, String name,
			SortedSet<AssetTypeCustomAttribute> allowedCustomAttributes) {
		super();
		this.id = id;
		this.name = name;
		this.allowedCustomAttributes = allowedCustomAttributes;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the rendering template used for printing. This is usually a velocity or freemarker HTML
	 * template of which the result is forwarded to xhtmlrenderer for PDF export.
	 * @return the printing template
	 */
	public RenderingTemplate getPrintingTemplate() {
		return printingTemplate;
	}

	/**
	 * Set the rendering template used for printing. This is usually a velocity or freemarker HTML
	 * template of which the result is forwarded to xhtmlrenderer for PDF export.
	 * @param printingTemplate the printing template to set
	 */
	public void setPrintingTemplate(RenderingTemplate printingTemplate) {
		this.printingTemplate = printingTemplate;
	}

	/**
	 * @return the allowedCustomAttributes
	 */
	public Set<AssetTypeCustomAttribute> getAllowedCustomAttributes() {
		return this.allowedCustomAttributes;
	}

	/**
	 * @param allowedCustomAttributes the allowedCustomAttributes to set
	 */
	public void setAllowedCustomAttributes(
			SortedSet<AssetTypeCustomAttribute> allowedCustomAttributes) {
		this.allowedCustomAttributes = allowedCustomAttributes;
	}

	/**
	 * @return the assets
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

	//////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void add(AssetTypeCustomAttribute assetTypeCustomAttribute){
		if (this.allowedCustomAttributes==null){
			this.allowedCustomAttributes = new TreeSet<AssetTypeCustomAttribute>();
		}//if
		assetTypeCustomAttribute.add(this);
		this.allowedCustomAttributes.add(assetTypeCustomAttribute);
	}//add

	public void addAll(Collection<AssetTypeCustomAttribute> attrs){
		if(attrs != null && !attrs.isEmpty()){
			Iterator<AssetTypeCustomAttribute> iter = attrs.iterator();
			while(iter.hasNext()){
				add(iter.next());
			}
		}
	}
	//----------------------------------------------------------------------------------------------------------

	public void remove(CustomAttribute assetTypeCustomAttribute){
		if (this.allowedCustomAttributes!=null){
			this.allowedCustomAttributes.remove(assetTypeCustomAttribute);
		}//if
	}//remove

	//----------------------------------------------------------------------------------------------------------
	
	public void removeAllowedCustomAttributes(){
		if (this.allowedCustomAttributes != null){
			this.allowedCustomAttributes.clear();
		}//if
	}//removeAllowedCustomAttributes
	
	/**
	 * @see gr.abiss.calipso.domain.i18n.AbstractI18nResourceTranslatable#getNameTranslationResourceKey()
	 */
	public String getPropertyTranslationResourceKey(String propertyName){
		return new StringBuffer(getShortName(AssetType.class))
		.append('.')
		.append(this.getId())
		.append('.')
		.append(propertyName)
		.toString();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id", this.getId())
			.append("name", this.getName())
			.toString();
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o == this) { return true; }
		if (!(o instanceof AssetType)) {
			return false;
		}
		// for some reason, this only works
		// when using getters for the 'other',
		// maybe because of Hibernate proxies
		AssetType other = (AssetType) o;
		return new EqualsBuilder()
			.append(this.id, other.getId())
	        .append(this.name, other.getName())
	        .isEquals();
	}
}//AssetType