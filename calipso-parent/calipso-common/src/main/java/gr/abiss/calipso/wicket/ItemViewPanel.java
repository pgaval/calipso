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

import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.ItemRenderingTemplate;
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.User;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.EmptyPanel;

public class ItemViewPanel extends BasePanel {  
    
	protected static final Logger logger = Logger.getLogger(ItemViewPanel.class);            
      
	private static final long serialVersionUID = 1L;
	private Item item = null;
	
	public String getTitle(){
        return localize("item_view.title", item.getUniqueRefId());
    }
	
	// required for itemRelatePanel
    public long getItemId() {
        return item.getId(); 
    }    
    /**
     * 
     * @param id
     * @param breadCrumbModel
     * @param refId
     */
    public ItemViewPanel(String id, IBreadCrumbModel breadCrumbModel, String refId) {  
    	this(id, breadCrumbModel, refId, null);
    }
    
    /**
     * 
     * @param id
     * @param breadCrumbModel
     * @param refId
     * @param previewHistory
     */
    public ItemViewPanel(String id, IBreadCrumbModel breadCrumbModel, String refId, History previewHistory){
    	this(id, null, breadCrumbModel, refId, previewHistory, null);
    }
    
    /**
     * 
     * @param id
     * @param breadCrumbModel
     * @param refId
     * @param previewHistory
     */
    public ItemViewPanel(String id, ItemRenderingTemplate tmpl, IBreadCrumbModel breadCrumbModel, String refId, History previewHistory, User user) {  
    	super(id, breadCrumbModel);
	
    	if(this.item == null){
    		this.item = getCalipso().loadItem(Item.getItemIdFromUniqueRefId(refId));
    		if(this.item != null){
    			setCurrentSpace(item.getSpace());
    		}
    		else{
    			logger.warn("Couldn't figure out current space from item ref id: "+refId);
    		}
    	}
    	if(user == null){
    		user = getPrincipal();
    	}
    	if(item != null && tmpl == null){
    		tmpl = getCalipso().getItemRenderingTemplateForUser(user, this.item.getStatus(), this.item.getSpace().getId());
    	}
    	addComponents(tmpl, previewHistory, user);
    }
    
    /**
     * 
     * @param tmpl 
     * @param previewHistory
     */
    private void addComponents(ItemRenderingTemplate tmpl, History previewHistory, User user) {  
        final ItemSearch itemSearch = getCurrentItemSearch();
        add(new ItemRelatePanel("relate", true));        
        
        boolean isRelate = itemSearch != null && itemSearch.getRelatingItemRefId() != null;
        
        
        // ensure user has appropriate rights to view the Item
        if(item != null && !user.getSpaces().contains(item.getSpace())) {
            logger.debug("user is not allocated to space");
            logger.debug("user.getSpaces(): "+user.getSpaces());
            logger.debug("item.getSpace(): "+item.getSpace());
            throw new RestartResponseAtInterceptPageException(ErrorPage.class);
        }

    	addVelocityTemplatePanel(tmpl, item);
        // hide overview?
        if(tmpl != null && tmpl.getHideOverview().booleanValue()){
        	add(new EmptyPanel("itemView").setRenderBodyOnly(true));
        }
        else{
            add(new ItemView("itemView", getBreadCrumbModel(), tmpl, item, isRelate || user.getId() == 0).setRenderBodyOnly(true));
        	
        }
//        
//        WebMarkupContainer addNewRecordContainer = new WebMarkupContainer("addNewRecord");
//        add(addNewRecordContainer);
        // we are giving guest normal workflow roles, although we may want to 
        // restrict in post-only 
        if(/*user.isGuestForSpace(item.getSpace()) || */isRelate) {
        	add(new WebMarkupContainer("addNewRecord").setVisible(false));
        } else { // edit
        	if(previewHistory!=null){
        		add(new ItemViewFormPanel("addNewRecord", getBreadCrumbModel(), item, itemSearch, previewHistory));
        	}
        	else{ // new
        		add(new ItemViewFormPanel("addNewRecord", getBreadCrumbModel(), item, itemSearch));
        	}
        }        
        
        // back link ==========================================================
        //we add it here because we have addNewRecordContainer and the BackLinkPanel
        //from BasePanel won't work
        //addNewRecordContainer.add(getBackLinkPanel());        
    }
}
