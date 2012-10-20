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

import gr.abiss.calipso.wicket.BasePanel;

import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;

/**
 * @author marcello
 * 
 * Assets Main menu
 * 
 */
public class AssetsPanel extends BasePanel {
	
	public AssetsPanel(String id, final IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);

		//Custom Attributes
	   	add(new BreadCrumbLink("customAttributes", breadCrumbModel){
	   		protected IBreadCrumbParticipant getParticipant(String componentId){
	   			return new AssetCustomAttributesPanel(componentId, breadCrumbModel);
	   		}
		});

	   	//Asset Types
	   	add(new BreadCrumbLink("assetTypes", breadCrumbModel){
	        protected IBreadCrumbParticipant getParticipant(String componentId){
	        	return new AssetTypesPanel(componentId, breadCrumbModel);
	        }
	   	});
	}

	/**
	 * Breadcrump path title
	 * */
	public String getTitle() {
		return localize("asset.assetsMenu");
	}
}

