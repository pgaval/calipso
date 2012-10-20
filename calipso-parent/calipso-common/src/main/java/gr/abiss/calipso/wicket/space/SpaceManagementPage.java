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

package gr.abiss.calipso.wicket.space;

import gr.abiss.calipso.domain.CalipsoBreadCrumbBar;
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.wicket.BasePage;
import gr.abiss.calipso.wicket.space.panel.SpacePanelLanguageSupport;

/**
 *
 */
public class SpaceManagementPage extends BasePage{

	private static final long serialVersionUID = 1L;

	public SpaceManagementPage(){
    	//breadcrumb navigation. stays static
    	CalipsoBreadCrumbBar breadCrumbBar = new CalipsoBreadCrumbBar("breadCrumbBar", this);
        add(breadCrumbBar);
        
        //panels that change with navigation
        SpacePanelLanguageSupport spacePanelLanguageSupport = new SpacePanelLanguageSupport("panel", breadCrumbBar);
        add(spacePanelLanguageSupport);
        breadCrumbBar.setActive(spacePanelLanguageSupport);      
	}
	
	
}
