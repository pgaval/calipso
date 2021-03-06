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

package gr.abiss.calipso.wicket;

import gr.abiss.calipso.domain.CalipsoBreadCrumbBar;

import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;

/**
 * options menu page
 */
public class OptionsPage extends BasePage {
	private static final long serialVersionUID = 1L;

	private CalipsoBreadCrumbBar breadCrumbBar;
    public OptionsPanel optionsPanel;

    public OptionsPage() {                  
    	//breadcrumb navigation. stays static
    	breadCrumbBar = new CalipsoBreadCrumbBar("breadCrumbBar", this);
    	add(breadCrumbBar);

        refreshHeader();

        //panels that change with navigation
        optionsPanel = new OptionsPanel("panel", breadCrumbBar);
        add(optionsPanel);
        breadCrumbBar.setActive(optionsPanel);
        refreshMenu(breadCrumbBar);
		this.setVersioned(true);
    }

    public void activate (IBreadCrumbPanelFactory breadCrumbPanelFactory){
    	optionsPanel.activate(breadCrumbPanelFactory);
    	refreshMenu(breadCrumbBar);
    }
    
    public CalipsoBreadCrumbBar getBreadCrumbBar(){
    	return this.breadCrumbBar;
    }
}