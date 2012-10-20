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
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.wicket.yui.YuiDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 * header that appears only within relate items use case
 * containing modal window link
 */
public class ItemRelatePanel extends BasePanel {
    
    private String refId;    
    
    public ItemRelatePanel(String id, boolean isItemViewPage) {                
        super(id);
        ItemSearch itemSearch = getCurrentItemSearch();
        refId = itemSearch == null ? null : itemSearch.getRelatingItemRefId();
        if (refId != null) {
            final YuiDialog dialog = new YuiDialog("itemWindow");
            add(dialog);                                                        
            AjaxLink link = new AjaxLink("link") {
                public void onClick(AjaxRequestTarget target) {
                    Item item = getCalipso().loadItemByRefId(refId);
                    dialog.show(target, refId, new ItemView(YuiDialog.CONTENT_ID, getCalipso().getItemRenderingTemplateForUser(getPrincipal(), item.getStatus(), item.getSpace().getId()), item));
                }
            };
            link.add(new Label("refId", refId));             
            if(isItemViewPage) {
                add(new WebMarkupContainer("link").setVisible(false));
                add(new WebMarkupContainer("message").setVisible(false));
                add(new RelateForm("form").add(link));
            } else {
                add(new Label("message", localize("item_list.searchingForRelated")));
                add(link);
                add(new WebMarkupContainer("form").setVisible(false));
            }           
            add(new Link("cancel") {
                public void onClick() {
                    Item item = getCalipso().loadItemByRefId(refId);
                    setCurrentItemSearch(null);
                    setResponsePage(ItemViewPage.class, new PageParameters("0=" + item.getUniqueRefId()));
                }
            });
        } else {
            setVisible(false);
        }        
    }
    
    /**
     * wicket form
     */
    private class RelateForm extends Form {
        
        private int type;
        private String comment;                
        
        public RelateForm(String id) {
            super(id);            
            setModel(new CompoundPropertyModel(this));
            final Map<Integer, String> options = new HashMap<Integer, String>(3);
            options.put(DUPLICATE_OF, localize("item_view_form.duplicateOf"));
            options.put(DEPENDS_ON, localize("item_view_form.dependsOn"));
            options.put(RELATED, localize("item_view_form.relatedTo"));
            DropDownChoice choice = new DropDownChoice("type", new ArrayList(options.keySet()), new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    return options.get(o);
                }
                public String getIdValue(Object o, int i) {
                    return o.toString();
                }
            });
            add(choice);
            TextArea commentArea = new TextArea("comment");
            commentArea.setRequired(true);
            commentArea.add(new ErrorHighlighter());
            add(commentArea);
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
             
        @Override
        protected void onSubmit() {
            Item item = getCalipso().loadItemByRefId(refId);
            long itemId = ((ItemViewPanel)findParent(ItemViewPanel.class)).getItemId();
            Item relatedItem = getCalipso().loadItem(itemId);
            item.addRelated(relatedItem, type);
            item.setEditReason(comment);
            getCalipso().updateItem(item, getPrincipal());
            setCurrentItemSearch(null);
            setResponsePage(ItemViewPage.class, new PageParameters("0=" + item.getUniqueRefId()));
        }          
        
        
    }
    
}
