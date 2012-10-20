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

package gr.abiss.calipso.util;

import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.AssetTypeCustomAttribute;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.behavior.SimpleAttributeModifier;

public class AssetsUtils {

	public static SimpleAttributeModifier getSupportEndDateStyle(Date supportEndDate){
		SimpleAttributeModifier supportEndDateStyle;
		DateTime today = new DateTime(Calendar.getInstance().getTime()); 
		DateTime supportEndDateDateTime = new DateTime(supportEndDate);

		supportEndDateStyle = new SimpleAttributeModifier("class", "date-ongoing");

		if (today.inSeconds() > supportEndDateDateTime.inSeconds()){
			supportEndDateStyle = new SimpleAttributeModifier("class", "date-reached");
		}//if

		return supportEndDateStyle;
	}//getSupportEndDateStyle

	public static AssetTypeCustomAttribute getAssetTypeAttribute(AssetType assetType, String attrName) {
		AssetTypeCustomAttribute attribute = null;
		Set<AssetTypeCustomAttribute> attrs = assetType.getAllowedCustomAttributes();
		if(CollectionUtils.isNotEmpty(attrs)){
			for(AssetTypeCustomAttribute attr : attrs){
				if(attr.getName().equals(attrName)){
					attribute = attr;
					break;
				}
			}
		}
		return attribute;
	}
}//AssetsUtils