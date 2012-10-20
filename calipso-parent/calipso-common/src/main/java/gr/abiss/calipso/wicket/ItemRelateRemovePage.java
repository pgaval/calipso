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

import static gr.abiss.calipso.domain.ItemItem.*;

import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.ItemItem;
import gr.abiss.calipso.wicket.yui.YuiDialog;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 * small form only to confirm and capture comment when removing relationship
 * between items
 */
public class ItemRelateRemovePage extends BasePage {
        
    private long itemId;
    private ItemItem itemItem;
    
    public ItemRelateRemovePage(long itemId, final ItemItem itemItem) {
        this.itemId = itemId;
        this.itemItem = itemItem;
        add(new ConfirmForm("form"));
        final String relatingRefId = itemItem.getItem().getUniqueRefId();
        final String relatedRefId = itemItem.getRelatedItem().getUniqueRefId();
        final YuiDialog relatingDialog = new YuiDialog("relatingDialog");
        final YuiDialog relatedDialog = new YuiDialog("relatedDialog");
        add(relatingDialog);
        add(relatedDialog);
        AjaxLink relating = new AjaxLink("relating") {
            public void onClick(AjaxRequestTarget target) {
                Item relating = getCalipso().loadItem(itemItem.getItem().getId());
                relatingDialog.show(target, relatingRefId, new ItemView(YuiDialog.CONTENT_ID, getCalipso().getItemRenderingTemplateForUser(getPrincipal(), relating.getStatus(), relating.getSpace().getId()), relating));                
            }
        };
        relating.add(new Label("refId", relatingRefId));
        add(relating);
        
        // TODO refactor, duplicate code in ItemView
        String message = null;
        if(itemItem.getType() == DUPLICATE_OF) {
            message = localize("item_view.duplicateOf");
        } else if (itemItem.getType() == DEPENDS_ON) {
            message = localize("item_view.dependsOn");
        } else if (itemItem.getType() == RELATED){
            message = localize("item_view.relatedTo");                  
        }
        add(new Label("message", message));
        
        AjaxLink related = new AjaxLink("related") {
            public void onClick(AjaxRequestTarget target) {
                Item related = getCalipso().loadItem(itemItem.getRelatedItem().getId());
                relatedDialog.show(target, relatedRefId, new ItemView(YuiDialog.CONTENT_ID, getCalipso().getItemRenderingTemplateForUser(getPrincipal(), related.getStatus(), related.getSpace().getId()), related));
            }
        };
        related.add(new Label("refId", itemItem.getRelatedItem().getUniqueRefId()));
        add(related);        
        
    }
    
    /**
     * wicket form
     */    
    private class ConfirmForm extends Form {
                
        private String comment;                
        
        public ConfirmForm(String id) {
            super(id);            
            setModel(new CompoundPropertyModel(this));
            TextArea commentArea = new TextArea("comment");
            commentArea.setRequired(true);
            commentArea.add(new ErrorHighlighter());
            add(commentArea);
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
             
        @Override
        protected void onSubmit() {
            getCalipso().removeItemItem(itemItem);
            Item item = getCalipso().loadItem(itemId);                                    
            item.setEditReason(comment);
            getCalipso().updateItem(item, getPrincipal());
            setResponsePage(ItemViewPage.class, new PageParameters("0=" + item.getUniqueRefId()));
        }          
        
        
    }
    
}
