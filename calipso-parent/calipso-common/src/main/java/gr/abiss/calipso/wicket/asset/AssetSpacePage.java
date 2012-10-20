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

import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.CalipsoBreadCrumbBar;
import gr.abiss.calipso.dto.AssetSearch;
import gr.abiss.calipso.wicket.BasePage;
import gr.abiss.calipso.wicket.helpMenu.HelpMenuPanel;

import org.apache.log4j.Logger;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbBar;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;

/**
 * @author marcello
 */
public class AssetSpacePage extends BasePage {
	protected static final Logger logger = Logger.getLogger(AssetSpacePage.class);
	
	 AssetSpacePanel assetSpacePanel;
	 AssetType assetType;
	public AssetSpacePage() {
		init(null);
	}
	
	public AssetSpacePage(AssetType assetType) {
		this.assetType = assetType;
		init(assetType);
		// 
	}
	private void init(AssetType assetType){
		//breadcrumb navigation. stays static
		CalipsoBreadCrumbBar breadCrumbBar = new CalipsoBreadCrumbBar("breadCrumbBar", this);
	    add(breadCrumbBar);
	    
	    if(assetType == null){
	    	//panels that change with navigation
		    assetSpacePanel = new AssetSpacePanel("panel", breadCrumbBar);
	    }
	    else{
	    	// TODO: 
	    	Asset asset = new Asset();
	    	asset.setAssetType(assetType);
	    	assetSpacePanel = new AssetSpacePanel("panel", breadCrumbBar, new AssetSearch(asset, this));
	    }
	    
	    add(assetSpacePanel);
	    breadCrumbBar.setActive(assetSpacePanel); 
	
	}
}

