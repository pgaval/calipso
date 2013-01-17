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

import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.FieldGroup;
import gr.abiss.calipso.domain.FieldGroupPriorityComparator;
import gr.abiss.calipso.domain.I18nStringIdentifier;
import gr.abiss.calipso.domain.I18nStringResource;
import gr.abiss.calipso.domain.ItemFieldCustomAttribute;
import gr.abiss.calipso.domain.ItemRenderingTemplate;
import gr.abiss.calipso.domain.ItemRenderingTemplateName;
import gr.abiss.calipso.domain.RoleSpaceStdField;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.SpaceRole;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.wicket.form.AbstractSpaceform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.UrlUtils;
import org.hibernate.Hibernate;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.CheckBoxColumn;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.column.editable.EditablePropertyColumn;
import com.inmethod.grid.datagrid.DataGrid;
import com.inmethod.grid.datagrid.DefaultDataGrid;

/**
 * 
 * space fields add / re-order page
 */
public class SpaceFieldListPanel extends BasePanel {

	protected static final Logger logger = Logger.getLogger(SpaceFieldListPanel.class);
	
    //private Space space;
        	
    public String getTitle(){
        return localize("space_fields.customFields");
    }
    
    public SpaceFieldListPanel(String id, IBreadCrumbModel breadCrumbModel, final Space space, String selectedFieldName) {
    	super(id, breadCrumbModel);
        
        setupVisuals(space);
        
        // FIELD GROUPS
	    List<IGridColumn> cols = (List) Arrays.asList(
	    		  new CheckBoxColumn("selected"),  
		          new PropertyColumn(new Model("Id"), "id"),
		          new PropertyColumn(new Model("Name"), "name"),
		          new PropertyColumn(new Model("Priority"), "priority"));
        final ListDataProvider listDataProvider = new ListDataProvider(space.getMetadata().getFieldGroups());
	    final WebMarkupContainer gridContainer = new WebMarkupContainer("gridContainer");
	    gridContainer.setOutputMarkupId(true);
	    final DataGrid grid = new DefaultDataGrid("fieldGroupGrid", new DataProviderAdapter(listDataProvider), cols);
	    grid.setSelectToEdit(false);
	      grid.setClickRowToSelect(true);
	      grid.setAllowSelectMultiple(false);
	      gridContainer.add(grid);
	      add(gridContainer);
	      // add "new" link
	      final ModalWindow fieldGroupModal = new ModalWindow("fieldGroupModal");
	      gridContainer.add(fieldGroupModal);
	      gridContainer.add(grid);
	      add(gridContainer);
	      add(new AjaxLink("newFieldGroupLink"){
	            public void onClick(AjaxRequestTarget target){
	            	// TODO: add row to grid?
	            	final FieldGroup tpl;
	            	if(CollectionUtils.isNotEmpty(grid.getSelectedItems())){
	            		tpl = (FieldGroup) ((IModel)grid.getSelectedItems().iterator().next()).getObject();
	            	}
	            	else{
	            		tpl = new FieldGroup("", "");
	            	}
	            	
	            	fieldGroupModal.setContent(new EditFieldGroupPanel("content", fieldGroupModal, tpl){
						@Override
						protected void persist(AjaxRequestTarget target,
								Form form) {
							if(!space.getMetadata().getFieldGroups().contains(tpl)){
								space.getMetadata().getFieldGroups().add(tpl);
            	            	//logger.info("added new fieldgroup to space");
        	            	}
							SortedSet<FieldGroup> fieldGroups = new TreeSet<FieldGroup>();
							fieldGroups.addAll(space.getMetadata().getFieldGroups());
							space.getMetadata().getFieldGroups().clear();
							space.getMetadata().getFieldGroups().addAll(fieldGroups);
        	            	//update grid
        	                if (target != null) {
        	                	target.addComponent(gridContainer);
        	                }
							
						}
	            	});
	            	fieldGroupModal.setTitle(this.getLocalizer().getString("fieldGroups", this));
	            	fieldGroupModal.show(target);
	            }
	        });
        
	    // FIELDS
        SpaceFieldsForm form = new SpaceFieldsForm("form", space, selectedFieldName);
        add(form);
    }
    
    private void setupVisuals(final Space space) {
        add(new Label("name", new PropertyModel(space, "name")));
        add(new Label("prefixCode", new PropertyModel(space, "prefixCode")));
        
        getBackLinkPanel().makeCancel(new BreadCrumbLink("link", getBreadCrumbModel()) {
			@Override
			protected IBreadCrumbParticipant getParticipant(String arg0) {
				return BreadCrumbUtils.moveToPanel(getBreadCrumbModel(), SpaceListPanel.class.getName());
			}
		});
		
	}
    /**
     * wicket form
     */     
    private class SpaceFieldsForm extends AbstractSpaceform {        

        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }          
        
        public SpaceFieldsForm(String id, final Space space, final String selectedFieldName) {
            super(id, space);
           
            final CompoundPropertyModel model = new CompoundPropertyModel(this);
            setModel(model);
            
            final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
            
            
            // Holds the added space Fields
            WebMarkupContainer spaceFields = new WebMarkupContainer("spaceFields");
            
            /* 
             *  When you add a space field, here is where magic happens
             *  and you can add fields to table of selected fields.
             *   
             *  Every table row has standard six table data two buttons up/down 
             *  three labels that describe the internal name, type of field and
             *  the field name given to the label  by the user, a list of options
             *  and an edit button that you can edit either the user-given label
             *  or the if option list (if DropDown).
             *  The ListView listView is added to a WebMarkContainer spaceFields
             *  which holds the selected space fields.
             *  The List fieldOrder holds the insertion order of space fields.
             */
            @SuppressWarnings("unchecked")
			ListView listView = new ListView("fields", space.getMetadata().getFieldList()) {
                protected void populateItem(ListItem listItem) {
                    final Field field = (Field) listItem.getModelObject();
                    
                    if (field.getName().getText().equals(selectedFieldName)) {
                        listItem.add(new SimpleAttributeModifier("class", "selected"));
                    } else if(listItem.getIndex() % 2 == 1) {
                        listItem.add(sam);
                    }
                    RequestCycle rc = RequestCycle.get();
//        			final AttributeModifier yes = );
                    Button upButton = new Button("up") {
                        @Override
                        public void onSubmit() {    
                            List<Field.Name> fieldOrder = space.getMetadata().getFieldOrder();
                            //logger.info("Field order before UP: "+fieldOrder);
                            int index = fieldOrder.indexOf(field.getName());
                            int swapIndex = index - 1;
                            if (swapIndex < 0) {
                                if (fieldOrder.size() > 1) {        
                                    swapIndex = fieldOrder.size() - 1;
                                } else {
                                    swapIndex = 0;
                                }
                            }
                            if (index != swapIndex) {
                                Collections.swap(fieldOrder, index, swapIndex);
                                
                                activate(new IBreadCrumbPanelFactory(){
                                	public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel){
                                		BreadCrumbUtils.removeActiveBreadCrumbPanel(breadCrumbModel);
                                		
										return new SpaceFieldListPanel(componentId, breadCrumbModel, space, field.getName().getText());
									}
                                	
                                });
                            }

                            //logger.info("Field order after UP: "+fieldOrder);
                        }
                    };
                    upButton.add(new AttributeModifier(
        					"src", UrlUtils.rewriteToContextRelative("resources/up.gif", rc)));
                    listItem.add(upButton);

                    Button downButton = new Button("down") {
                        @Override
                        public void onSubmit() {  
                            List<Field.Name> fieldOrder = space.getMetadata().getFieldOrder();
                            int index = fieldOrder.indexOf(field.getName());
                            int swapIndex = index + 1;
                            if (swapIndex == fieldOrder.size()) {
                                swapIndex = 0;
                            }
                            if (index != swapIndex) {
                                Collections.swap(fieldOrder, index, swapIndex);
                                
                                activate(new IBreadCrumbPanelFactory(){
                                	public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel){
                                		BreadCrumbUtils.removeActiveBreadCrumbPanel(breadCrumbModel);
                                		
										return new SpaceFieldListPanel(componentId, breadCrumbModel, space, field.getName().getText());
									}
                                	
                                });
                            }                            
                        }                        
                    };
                    downButton.add(new AttributeModifier(
        					"src", UrlUtils.rewriteToContextRelative("resources/down.gif", rc)));
                    listItem.add(downButton);
                    
                    listItem.add(new Label("name", "("+field.getName().getText()+") "+localize(space, field.getName().getText())));
                    listItem.add(new Label("type", new PropertyModel(field, "name.description")));                    
                    listItem.add(new Label("label", new PropertyModel(field, "label")));
                    List<String> optionsList;
                    //logger.info("field "+field.getName().getText()+" is drop down type: "+field.getName().isDropDownType());
                    if(field.isDropDownType()) {
                    	
                    	// TODO:options
                    	if (field.getName().equals(Field.Name.ASSIGNABLE_SPACES)){
                    		List<String> optionValues = new ArrayList<String>(field.getOptions().values());
                    		optionsList = new ArrayList<String>();
                    		for (int i=0; i<optionValues.size(); i++){
                    			Space sp = (Space) getCalipso().loadSpace(Long.parseLong((String)optionValues.get(i)));
                    			optionsList.add(sp.getName());
                    		}//for
                    	}//if
                    	else{
                    		// change
                    		optionsList = new LinkedList<String>();
    						ItemFieldCustomAttribute attr = field.getCustomAttribute();
    						if(attr == null){
    							attr = getCalipso().loadItemCustomAttribute(space, field.getName().getText());
    						}
    								
    						if(attr != null){
								List<CustomAttributeLookupValue> existingLookupValues = new LinkedList<CustomAttributeLookupValue>();
								List<CustomAttributeLookupValue> valuesList = attr.getAllowedLookupValues();
								if(CollectionUtils.isNotEmpty(valuesList)){
									existingLookupValues.addAll(valuesList);
								}
								else{
									existingLookupValues = getCalipso().findLookupValuesByCustomAttribute(attr);
								}
								
								// build string representation of items for simple rendering
								buildOptionsListFromRoots(optionsList, existingLookupValues);
    						}
    						else{
    							logger.warn("No ItemFieldCustomAttribute found for field: "+ field.getName().getText()+", in space: "+space.getPrefixCode());
    						}
                    	}//else
                    } else {
                        optionsList = new ArrayList(0);
                    }
                    WebMarkupContainer optionsContainer = new WebMarkupContainer("options-container");
                    listItem.add(optionsContainer);
                    ListView options = new ListView("options", optionsList) {
                        protected void populateItem(ListItem item) {
                            item.add(new Label("option", item.getModelObject() + "").setRenderBodyOnly(true));
                        }                        
                    };
                    optionsContainer.add(options);
                    if(optionsList.size() > 12){
                    	optionsContainer.add(new SimpleAttributeModifier("class", "scroll"));
                    }
                    if(optionsList.size() == 0){
                    	optionsContainer.setVisible(false);
                    }
                    Button editButton = new Button("edit") {
                        @Override
                        public void onSubmit() {
                            final Field fieldClone = field;
                            
                            //Dropdown list of Spaces that an item from "this" space can be moved to? 
                            if (field.getName().equals(Field.Name.ASSIGNABLE_SPACES)){
                    			activate(new IBreadCrumbPanelFactory(){
                    				public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
                    					return new AssignableSpacesPanel(id, breadCrumbModel, space, fieldClone);
                    				}
                    			});
                            	
                            }//if
                            else{                            
                    			activate(new IBreadCrumbPanelFactory(){
                    				public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
                    					return new SpaceFieldFormPanel(id, breadCrumbModel, space, fieldClone);
                    				}
                    			});
                            }//else
                        }
                    };
                    editButton.add(new AttributeModifier(
        					"src", UrlUtils.rewriteToContextRelative("resources/edit.gif", rc)));
                    
                    listItem.add(editButton);
                }

				private void buildOptionsList(List<String> optionsList,
						CustomAttributeLookupValue lookupValue) {
					
					//logger.info("buildOptionsList called with value: "+lookupValue);
					try{
						// TODO:load in single query
						String lang = getSession().getLocale().getLanguage();
						//logger.info("Looking for lookupValue '"+lookupValue+"' and locale: "+lang);
						I18nStringResource res = getCalipso().loadI18nStringResource(new I18nStringIdentifier(lookupValue.getNameTranslationResourceKey(), lang));
						String s;
						if(res != null){
							//logger.info("Found lookupValue with key '"+lookupValue.getNameTranslationResourceKey()+"' and value: "+res.getValue());
							s = res.getValue();
						}
						else if(lookupValue.getTranslations() != null 
								&& lookupValue.getTranslations().get("name") != null){
							s = lookupValue.getTranslations().get("name").get(lang);
							//logger.info("Could not find a lookupValue with key '"+lookupValue.getNameTranslationResourceKey()+"' and lang: "+lang);
						}
						else{
							s = lookupValue.getNameTranslationResourceKey();
							//logger.warn("Could not find a lookupValue or in-memory object translation with key '"+lookupValue.getNameTranslationResourceKey()+"' and lang: "+lang);
						}
						if(!optionsList.contains(s)){
							optionsList.add(s);
							if(res != null && lookupValue.getChildren() != null){
								for(CustomAttributeLookupValue child : lookupValue.getChildren()){
									//logger.info("buildOptionsList calling self with value: "+child);
									buildOptionsList(optionsList, child);
								}
								
							}
						}
					
					}
					catch(RuntimeException e){
						logger.error(e);
					}
				}
				
				/**
				 * Process only the roots
				 * @param optionsList
				 * @param lookupValues
				 */
				private void buildOptionsListFromRoots(List<String> optionsList,
						Collection<CustomAttributeLookupValue> lookupValues) {
					if(lookupValues != null && !lookupValues.isEmpty()){
						for(CustomAttributeLookupValue root : lookupValues){
							if(root.getLevel() == 1 && root.isActive()){
								//logger.info("buildOptionsListFromRoots calling buildOptionsList with root: " + root);
								buildOptionsList(optionsList, root);
							}
						}
					}
				}

				               
            };   
            spaceFields.add(listView);
            add(spaceFields);
            add(new WebMarkupContainer("noData").setVisible(space.getMetadata().getFieldList().size()==0));
            spaceFields.setVisible(space.getMetadata().getFieldList().size()>0);

            final Map<String, String> types = space.getMetadata().getAvailableFieldTypes();
            List<String> typesList = new ArrayList(types.keySet());    
            // pre-select the drop down for convenience
            if(typesList.size() > 0) {
                type = typesList.get(0);
            }             
            
            @SuppressWarnings("unchecked")
			DropDownChoice choice = new DropDownChoice("type", typesList, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                	String key = "space_fields.type_" + o;
                	String typeMsg = localize(key);
                	//logger.info("DropDownChoice for FIELDTYPES, Key: "+key+", msg: "+typeMsg);
                    return typeMsg + " - " + localize("space_fields.typeRemaining", types.get(o));
                }
                public String getIdValue(Object o, int i) {
                    return o.toString();
                }
            });         
            
            add(choice);     
            //form label for choice
            choice.setLabel(new ResourceModel("space_fields.chooseType"));
            add(new SimpleFormComponentLabel("typeLabel", choice));
            
            
            
            /*
             *  Add the selected space field to the database (check Metadata.java)
             *  
             *  
             */
            add(new Button("add") {
                @Override
                public void onSubmit() {                    
                    if(type == null) {
                        return;
                    }
                    // Get's the selected field (of the type --> type) from Metadata
                    final Field field = space.getMetadata().getNextAvailableField(Integer.parseInt(type));
                    field.initOptions();
                    if (field.getName().equals(Field.Name.ASSIGNABLE_SPACES)){
            			activate(new IBreadCrumbPanelFactory(){
            				public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
            					return new AssignableSpacesPanel(id, breadCrumbModel, space, field);
            				}
            			});                    	
                    	
                    }//if
                    else{
            			activate(new IBreadCrumbPanelFactory(){
            				public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
            					return new SpaceFieldFormPanel(id, breadCrumbModel, space, field);
            				}
            			});                    	
                    }//else
                }
            });                      

            add(new Button("back") {
                @Override
                public void onSubmit() {
        			activate(new IBreadCrumbPanelFactory(){
        				public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
        					BreadCrumbUtils.removePreviousBreadCrumbPanel(breadCrumbModel);
        					return new SpaceFormPanel(id, breadCrumbModel, space);
        				}
        			});                    	
                }           
            });
            add(new Button("apply") {
                @Override
                public void onSubmit() {
					activate(new IBreadCrumbPanelFactory() {
						public BreadCrumbPanel create(String componentId,
								IBreadCrumbModel breadCrumbModel) {
							BreadCrumbUtils
									.removePreviousBreadCrumbPanel(breadCrumbModel);
							return new SpaceFieldListPanel(componentId,
									breadCrumbModel, persistChanges(), null);
						}
					});
				}
            }.setVisible(false));
            
            add(new Button("next") {
                @Override
                public void onSubmit() {
        			activate(new IBreadCrumbPanelFactory(){
        				public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
        					return new SpacePermissionsPanel(id, breadCrumbModel, space);
        				}
        			});                    	
                }
            });
            
		      
        }
        
		private Space persistChanges() {
			Space space = this.getSpace();
			space = getCalipso().storeSpace(space);
			// current user may be allocated to this space, and e.g.
			// name could have changed
			refreshPrincipal();
			return space;
		}
    }
}
