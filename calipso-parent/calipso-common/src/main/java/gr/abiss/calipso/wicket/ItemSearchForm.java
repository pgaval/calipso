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

import static gr.abiss.calipso.domain.ColumnHeading.Name.ID;
import static gr.abiss.calipso.domain.ColumnHeading.Name.SUMMARY;
import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.ColumnHeading;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.ItemRefId;
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.FilterCriteria.Expression;
import gr.abiss.calipso.exception.InvalidRefIdException;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

/**
 * item search form panel
 */
public class ItemSearchForm extends BasePanel {


	private static final Logger logger = Logger.getLogger(ItemSearchForm.class);
    private ItemSearch itemSearch;
    private boolean expandAll;
    
    public ItemSearchForm(String id, IBreadCrumbModel breadCrumbModel, User user) {
        super(id, breadCrumbModel);
        this.itemSearch = new ItemSearch(user, this);
        addComponents();
    }
    
    public ItemSearchForm(String id, IBreadCrumbModel breadCrumbModel) {
        super(id, breadCrumbModel);
        Space s = getCurrentSpace();
        refreshParentMenu(breadCrumbModel);
        if(s != null) {
            this.itemSearch = new ItemSearch(s, getPrincipal(), this, this.getCalipso());
        } else {
            this.itemSearch = new ItemSearch(getPrincipal(), this);
        }

        addComponents();
        
    }
    
    public ItemSearchForm(String id, IBreadCrumbModel breadCrumbModel, ItemSearch itemSearch) {
        super(id, breadCrumbModel);
        this.itemSearch = itemSearch;
        addComponents();
    }
    
    private void addComponents() {
    	final Form form = new Form("form");
    	Space space = getCurrentSpace();
    	final boolean hideSummary = space != null && !space.isItemSummaryEnabled();
        add(form);
        form.add(new FeedbackPanel("feedback"));
        form.setModel(new CompoundPropertyModel(itemSearch));
        List<Integer> sizes = Arrays.asList(new Integer[] {5, 10, 15, 25, 50, 100});        
        DropDownChoice pageSizeChoice = new DropDownChoice("pageSize", sizes, new IChoiceRenderer() {
            public Object getDisplayValue(Object o) {
                return ((Integer) o) == -1 ? localize("item_search_form.noLimit") : o.toString();
            }
            public String getIdValue(Object o, int i) {
                return o.toString();
            }
        });
        form.add(pageSizeChoice); 
        //form label for page size
        pageSizeChoice.setLabel(new ResourceModel("item_search_form.resultsPerPage"));
        form.add(new SimpleFormComponentLabel("pageSizeLabel", pageSizeChoice));
        
        //showHistoryLabel
        CheckBox showHistoryCheckBox = new CheckBox("showHistory");        
        form.add(showHistoryCheckBox);
        //form label for showHistoryLabel
        showHistoryCheckBox.setLabel(new ResourceModel("item_search_form.showHistory"));
        form.add(new SimpleFormComponentLabel("showHistoryLabel", showHistoryCheckBox));
        
        form.add(new Button("search") {
            @Override
            public void onSubmit() {
                String refId = itemSearch.getRefId();                
                if(refId != null) {
                    // user can save typing by entering the refId number without the space prefixCode
                	// and the space sequence number.
                	// User also search by item number without to select a space. 
                    try {
                        long id = Long.parseLong(refId);
                        //Load item in order to get first and last part from item  
                        Item item = getCalipso().loadItem(id);
                        
                        //If item doesn't exists
                        if (item==null){
                        	//Set a dummy value, in order to raise "item not found" instead of "Invalid reference id" 
                        	refId = "0-0-0";
                        }//if
                        else{
                        	refId = item.getUniqueRefId();
                        }//else
                    } catch(Exception e) {
                        // oops that didn't work, continue
                    }
                	
                    try {
                        new ItemRefId(refId);
                    } catch(InvalidRefIdException e) {
                        form.error(localize("item_search_form.error.refId.invalid"));
                        return;
                    }
                    final Item item = getCalipso().loadItem(Item.getItemIdFromUniqueRefId(refId));
                    if(item == null) {
                        form.error(localize("item_search_form.error.refId.notFound")); 
                        return;
                    }
                    setCurrentItemSearch(itemSearch);
                    activate(new IBreadCrumbPanelFactory(){
						public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
							return new ItemViewPanel(componentId, breadCrumbModel, item.getUniqueRefId());
						}
                    	
                    });
                    return;
                }
                String searchText = itemSearch.getSearchText();
                if(searchText != null) {
                    if(!getCalipso().validateTextSearchQuery(searchText)) {
                        form.error(localize("item_search_form.error.summary.invalid"));
                        return;
                    }
                }
                setCurrentItemSearch(itemSearch);
                activate(new IBreadCrumbPanelFactory(){
					public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
						return new ItemListPanel(componentId, breadCrumbModel);
					}                	
                });
            }
        });
        form.add(new Link("expandAll") {
            public void onClick() {
                expandAll = true;                
            }
            @Override
            public boolean isVisible() {
                return !expandAll;
            }
        });

        form.add(new ListView("columns", itemSearch.getColumnHeadings()) {
            protected void populateItem(final ListItem listItem) {
            	final ColumnHeading ch = (ColumnHeading) listItem.getModelObject();
            	boolean enabled = true;
            	if(ch.getName() != null && ch.getName().equals(SUMMARY) && hideSummary){
            		enabled = false;
            	}
                
            	String label = ch.isField() ? localize(ch.getLabel()) : localize("item_list." + ch.getNameText());

                CheckBox visibleCheckBox = new CheckBox("visible", new PropertyModel(ch, "visible"));
                visibleCheckBox.setEnabled(enabled);
                
                listItem.add(visibleCheckBox);

                //form Label
                visibleCheckBox.setLabel(new ResourceModel("", label));
                listItem.add(new SimpleFormComponentLabel("columnLabel", visibleCheckBox));

                List<Expression> validExpressions = ch.getValidFilterExpressions();
                DropDownChoice expressionChoice = new IndicatingDropDownChoice("expression", validExpressions, new IChoiceRenderer() {
                    public Object getDisplayValue(Object o) {
                        String key = ((Expression) o).getKey();
                        return localize("item_filter." + key);
                    }
                    public String getIdValue(Object o, int i) {
                        return ((Expression) o).getKey();
                    }
                });
                expressionChoice.setEnabled(enabled);
                if(ch.getName() == ID) {
                    ch.getFilterCriteria().setExpression(Expression.EQ);   
                }
                Component fragParent = null;

                
                if(expandAll) {
                    ch.getFilterCriteria().setExpression(validExpressions.get(0));
                    fragParent = ch.getFilterUiFragment(form, getPrincipal(), getCurrentSpace(), getCalipso());
                } else {
                    fragParent = getFilterUiFragment(form, ch);
                }
                
                
                fragParent.setOutputMarkupId(true);
                listItem.add(fragParent);
                expressionChoice.setModel(new PropertyModel(ch.getFilterCriteria(), "expression"));
                expressionChoice.setNullValid(true);
                listItem.add(expressionChoice);                
                expressionChoice.add(new AjaxFormComponentUpdatingBehavior("onChange") {
                    protected void onUpdate(AjaxRequestTarget target) {
                        if(!ch.getFilterCriteria().requiresUiFragmentUpdate()) {
                            return;
                        }

                        Component fragment = getFilterUiFragment(form, ch);
                        fragment.setOutputMarkupId(true);
                        listItem.replace(fragment);
                        target.addComponent(fragment);
                        target.appendJavaScript("document.getElementById('" + fragment.getMarkupId() + "').focus()");
                    }
                });
            }
        });
    }

    private Component getFilterUiFragment(MarkupContainer container, ColumnHeading ch) {
        if(ch.getFilterCriteria().getExpression() == null) {
            return new WebMarkupContainer("fragParent");
        }    

        return ch.getFilterUiFragment(container, getPrincipal(), getCurrentSpace(), getCalipso());
    }

}