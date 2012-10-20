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
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.User;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbBar;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;

/**
 * dashboard page
 */
public class ItemViewPage extends BasePage {
	protected static final Logger logger = Logger.getLogger(ItemViewPage.class);             
        
    private long itemId;

    public long getItemId() {
        return itemId;
    }
    public ItemViewPage(){
    	
    }
    
    public ItemViewPage(PageParameters params) {        
    	logger.info("item id parsed from url params.get(0) = '" + params.get(0) + "', null: "+(params.get(0) ==null));
    	logger.info("item id parsed from url params.get(\"0\")= '" + params.get("0")+ "', null: "+(params.get("0") ==null));
    	logger.info("item id parsed from url params.get(\"itemId\")= '" + params.get("itemId") + "', null: "+(params.get("itemId") ==null));
    	org.apache.wicket.util.string.StringValue paramId = params.get("0");
    	logger.info("paramId 1: "+paramId);
    	if(StringUtils.isBlank(paramId+"") || "null".equals(paramId+"")){
    		paramId = params.get(0);
        	logger.info("paramId 2: "+paramId);
    	}
    	if(StringUtils.isBlank(paramId+"") || "null".equals(paramId+"")){
    		paramId = params.get("itemId");
        	logger.info("paramId 3: "+paramId);
    	}
    	if(StringUtils.isBlank(paramId+"") || "null".equals(paramId+"")){
    		throw new AbortWithHttpErrorCodeException(404);
    	}
        String refId = paramId.toString();
        logger.info("refId: "+refId);
        logger.info("uniqueRefId: "+Item.getItemIdFromUniqueRefId(refId));

        setCurrentItemSearch(null);
        
        // TODO: is this needed?
        Item item = getCalipso().loadItem(Item.getItemIdFromUniqueRefId(refId));   
        logger.info("item: "+item);     
        if(item != null){
        	itemId = item.getId(); // required for itemRelatePanel
        }
        else{
        	throw new AbortWithHttpErrorCodeException(404);
        }
    	//breadcrumb navigation. stays static
        CalipsoBreadCrumbBar breadCrumbBar = new CalipsoBreadCrumbBar("breadCrumbBar", this);
        add(breadCrumbBar);
        
        //panels that change with navigation
        ItemViewPanel itemViewPanel = new ItemViewPanel("panel", breadCrumbBar, refId);
        add(itemViewPanel);
        breadCrumbBar.setActive(itemViewPanel);      
    }   
}
