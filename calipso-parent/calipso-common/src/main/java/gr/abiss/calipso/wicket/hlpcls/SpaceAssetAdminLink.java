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

package gr.abiss.calipso.wicket.hlpcls;

import gr.abiss.calipso.wicket.asset.AssetSpacePanel;

import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;

public abstract class SpaceAssetAdminLink extends BreadCrumbLink{

	private IBreadCrumbModel breadCrumbModel;

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public SpaceAssetAdminLink(String id, IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);

		this.breadCrumbModel = breadCrumbModel;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public IBreadCrumbModel getBreadCrumbModel() {
		return breadCrumbModel;
	}

	public void setBreadCrumbModel(IBreadCrumbModel breadCrumbModel) {
		this.breadCrumbModel = breadCrumbModel;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public abstract void onLinkActivate();

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected IBreadCrumbParticipant getParticipant(String componentId) {
		onLinkActivate();
		return new AssetSpacePanel(componentId, getBreadCrumbModel());
	}//getParticipant
}