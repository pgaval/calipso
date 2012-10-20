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

import org.apache.log4j.Logger;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;

/**
 * item list page
 */
public class ItemListPanel extends BasePanel {
	
	protected static final Logger logger = Logger.getLogger(ItemListPanel.class);
	private String searchName = null;

	//////////////////////////////////////////////////////////////////////////////////////////

	public String getTitle(){
		if(searchName == null)
			return localize("item_list.title");
		else
			return searchName+" "+localize("item_list.title");
    }
	
    //////////////////////////////////////////////////////////////////////////////////////////
	/** 
	 * Used when saving a search for future use (thus making it a report)
	 */
    public ItemListPanel(String id, IBreadCrumbModel breadCrumbModel, String searchName){
    	super(id, breadCrumbModel);

    	this.searchName = searchName;

    	addComponents(id, breadCrumbModel);
    }//ItemListPanel

    //---------------------------------------------------------------------------------------

    public ItemListPanel(String id, IBreadCrumbModel breadCrumbModel){
    	super(id, breadCrumbModel);
    	
    	refreshParentPageHeader();
    	
    	addComponents(id, breadCrumbModel);
    }//ItemListPanel

    //////////////////////////////////////////////////////////////////////////////////////////

    private void addComponents(String id, IBreadCrumbModel breadCrumbModel){
		add(new ItemRelatePanel("relate", false));
		//logger.info("Item search: "+this.getCurrentItemSearch());
		//logger.info("Item search space: "+this.getCurrentItemSearch() != null ? this.getCurrentItemSearch()  :null);
		// If:
		// 1) Is a saved query or
		// 2) The user is not logged in
		// then: Don't show the "save query" functionality
		if (this.searchName!=null || getPrincipal()==null || (getPrincipal()!=null && getPrincipal().getId()==0)){
			add(new ItemList("panel", breadCrumbModel, false));
		}//if
		else{
			add(new ItemList("panel", breadCrumbModel));
		}//else
    }//addComponents
}//ItemListPanel