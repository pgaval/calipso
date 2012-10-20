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

package gr.abiss.calipso.wicket;

import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;

import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.util.BreadCrumbUtils;

import java.io.Serializable;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

/**
 * @author marcello
 */
public class SpaceFieldOptionPanel extends BasePanel {

//	public SpaceFieldOptionPanel(String id, IBreadCrumbModel breadCrumbModel) {
//		super(id, breadCrumbModel);
//	}

    private Space space;
    
    private void addComponents(Field field, String key) {                
        add(new SpaceFieldOptionForm("form", field, key));
    }     

    @Override
    public String getTitle() {
    	return localize("space_field_option_edit.title");
    }
    
    public SpaceFieldOptionPanel(String id, IBreadCrumbModel breadCrumbModel, Space space, Field field, String key) {
    	super(id, breadCrumbModel);
        this.space = space;
        addComponents(field, key);
    }
    
    /**
     * wicket form
     */     
    private class SpaceFieldOptionForm extends Form {               
        
        private Field field;
        private String key;
        
        public SpaceFieldOptionForm(String id, final Field field, final String key) {
            
            super(id);          
            add(new FeedbackPanel("feedback"));
            
            this.field = field;
            this.key = key;
            
            SpaceFieldOptionModel modelObject = new SpaceFieldOptionModel();
            modelObject.setOption(field.getCustomValue(key));
            final CompoundPropertyModel model = new CompoundPropertyModel(modelObject);
            setModel(model);
            
            // delete ==========================================================
            Button delete = new Button("delete") {
                @Override
                public void onSubmit() {
                    int affectedCount = getCalipso().loadCountOfRecordsHavingFieldWithValue(space, field, Integer.parseInt(key));
                    if (affectedCount > 0) {
                        final String heading = localize("space_field_option_delete.confirm") + " : " + field.getCustomValue(key) 
                            + " [" + field.getLabel() + "]";
                        final String warning = localize("space_field_option_delete.line3");
                        final String line1 = localize("space_field_option_delete.line1");
                        final String line2 = localize("space_field_option_delete.line2", affectedCount + "");                        

		                activate(new IBreadCrumbPanelFactory(){
		                	public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel) {
		                		ConfirmPanel confirm = new ConfirmPanel(componentId, breadCrumbModel, heading, warning, new String[] {line1, line2}) {
		                			public void onConfirm() {
		                                field.getOptions().remove(key);        
		                                getCalipso().bulkUpdateFieldToNullForValue(space, field, Integer.parseInt(key));
		                                // database has been updated, if we don't do this
		                                // user may leave without committing metadata change       
		                                getCalipso().storeSpace(space);
		                                // synchronize metadata else when we save again we get Stale Object Exception
		                                //space.setMetadata(getCalipso().loadMetadata(space.getMetadata().getId()));
		                				
		                				activate(new IBreadCrumbPanelFactory(){
		                					public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel) {
		                						BreadCrumbUtils.moveToPanelForRelod(breadCrumbModel, SpaceFieldListPanel.class);
		                						return new SpaceFieldFormPanel(componentId, breadCrumbModel, space, field);
		                					}
		                				});
		                			};
		                		};
		                		return confirm;
		                	}
		                });
                        
                    } else {
                        // this is an unsaved space / field or there are no impacted items
                        field.getOptions().remove(key);
                        activate(new IBreadCrumbPanelFactory(){
                        	public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel){
                        		BreadCrumbUtils.removePreviousBreadCrumbPanel(breadCrumbModel);
    							return new SpaceFieldFormPanel(componentId, breadCrumbModel, space, field);
    						}
                        });
                        

//                        setResponsePage(new SpaceFieldFormPage(space, field, previous));
                    }
                }                
            };
            delete.setDefaultFormProcessing(false);
            add(delete);
            // heading label ===================================================
            add(new Label("label", new PropertyModel(field, "label")));
            // option ===========================================================
            final TextField option = new TextField("option");
            option.setRequired(true);
            option.add(new ErrorHighlighter());
            option.add(new AbstractValidator() {
                protected void onValidate(IValidatable v) {
                    String s = (String) v.getValue();
                    if(field.hasOption(s)) {
                        error(v);
                    }
                }
                @Override
                protected String resourceKey() {                    
                    return "space_field_option_edit.error.exists";
                }                
            });
            add(option);
            // cancel ==========================================================
            add(new Link("cancel") {
                public void onClick() {
//                    setResponsePage(new SpaceFieldFormPage(space, field, previous));
                    activate(new IBreadCrumbPanelFactory(){
                    	public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel){
                    		BreadCrumbUtils.removePreviousBreadCrumbPanel(breadCrumbModel);
							return new SpaceFieldFormPanel(componentId, breadCrumbModel, space, field);
						}
                    });
                }                
            });            
        }
                
        @Override
        protected void onSubmit() {                    
            SpaceFieldOptionModel model = (SpaceFieldOptionModel) getModelObject();
            field.addOption(key, model.getOption());
//            setResponsePage(new SpaceFieldFormPage(space, field, previous));
            activate(new IBreadCrumbPanelFactory(){
            	public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel){
            		BreadCrumbUtils.removePreviousBreadCrumbPanel(breadCrumbModel);
					return new SpaceFieldFormPanel(componentId, breadCrumbModel, space, field);
				}
            });
        }     
    }        
        
    /**
     * custom form backing object that wraps Field
     * required for the create / edit use case
     */
    private class SpaceFieldOptionModel implements Serializable {
                
        private String option;

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }
               
    }
	
	
}

