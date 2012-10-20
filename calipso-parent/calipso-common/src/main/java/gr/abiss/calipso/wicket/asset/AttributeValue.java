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

package gr.abiss.calipso.wicket.asset;

import java.io.Serializable;

import gr.abiss.calipso.domain.AssetTypeCustomAttribute;

import org.apache.wicket.markup.html.form.FormComponent;

public class AttributeValue implements Serializable {

	private FormComponent formComponent;
	private AssetTypeCustomAttribute assetTypeCustomAttribute;
	
	public AttributeValue() {
	}
	/**
	 * 
	 * @param formComponent
	 * 			Wicket form component
	 * @param assetTypeCustomAttribute
	 * 				Custom Attribute 
	 */
	public AttributeValue(FormComponent formComponent, AssetTypeCustomAttribute assetTypeCustomAttribute) {
		this.formComponent = formComponent;
		this.assetTypeCustomAttribute = assetTypeCustomAttribute;
	}

	public AssetTypeCustomAttribute getAssetTypeCustomAttribute() {
		return assetTypeCustomAttribute;
	}

	public void setAssetTypeCustomAttribute(
			AssetTypeCustomAttribute assetTypeCustomAttribute) {
		this.assetTypeCustomAttribute = assetTypeCustomAttribute;
	}

	public FormComponent getFormComponent() {
		return formComponent;
	}

	public void setFormComponent(FormComponent formComponent) {
		this.formComponent = formComponent;
	}
}
