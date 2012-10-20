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

import gr.abiss.calipso.domain.RoleSpaceStdField;
import gr.abiss.calipso.domain.RoleType;
import gr.abiss.calipso.domain.SpaceRole;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.util.BreadCrumbUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;



/**
 * @author marcello
 */
public class SpaceRolePanel extends BasePanel {
    private boolean isEdit;
    private SpaceRole spaceRole; 
    
    public SpaceRolePanel(String id, IBreadCrumbModel breadCrumbModel, SpaceRole spaceRole) {
    	super(id, breadCrumbModel);
        this.spaceRole = spaceRole;
        
        this.isEdit = spaceRole.getId()>0;
        
        add(new Label("title", getTitle()));
    	getBackLinkPanel().makeCancel();
        deleteLink();

        add(new SpaceRoleForm("form", spaceRole));
    }

    @Override
    public String getTitle() {
    	if(isEdit){
    		return localize("space_role_form.titleEdit", spaceRole.getDescription());
    	}
    	else{
    		return localize("space_role_form.titleNew");
    	}
    }

    private void deleteLink(){
    	if(spaceRole.getRoleType().equals(RoleType.GUEST)){//if is special role type "Guest"
    		add(new WebMarkupContainer("delete").setVisible(false));
    	}
    	else{//if edit
	    	add(new Link("delete") {
				@Override
				public void onClick() {
					boolean checkDeletetion = false;
					if (isEdit){
						//TODO Re-implement this
						List<User> users = getCalipso().findUsersWithRoleForSpace(getCalipso().loadSpaceRole(spaceRole.getId()));
	                    int affectedCount = users.size();
	                    checkDeletetion = affectedCount > 0; 
					}

                    if (checkDeletetion) {
                        final String heading = localize("space_role_delete.confirm") + " : " + spaceRole.getDescription();
                        final String warning = localize("space_role_delete.line3");
                        final String line1 = localize("space_role_delete.line1", localize(spaceRole.getSpace().getNameTranslationResourceKey()));
                        final String line2 = localize("space_role_delete.line2");                        
                        
		                activate(new IBreadCrumbPanelFactory(){
		                	public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel) {
		                		ConfirmPanel confirm = new ConfirmPanel(componentId, breadCrumbModel, heading, warning, new String[] {line1, line2}) {
		                			public void onConfirm() {
		                				spaceRole.getSpace().getMetadata().removeRole(spaceRole.getRoleCode());
		                				spaceRole.getSpace().remove(spaceRole);
	
		                				activate(new IBreadCrumbPanelFactory(){
		                					public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel) {
		                						BreadCrumbUtils.moveToPanelForRelod(breadCrumbModel, SpacePermissionsPanel.class);
		                						return new SpacePermissionsPanel(componentId, breadCrumbModel, spaceRole.getSpace(), new ArrayList<RoleSpaceStdField>(spaceRole.getSpace().getRoleSpaceStdFields())/*, new ArrayList<SpaceRole>(spaceRole.getSpace().getSpaceRoles())*/);
		                					}
		                				});
		                			};
		                		};
		                		return confirm;
		                	}
		                });
                        
                    } else {
                    	spaceRole.getSpace().getMetadata().removeRole(spaceRole.getRoleCode());
                    	spaceRole.getSpace().remove(spaceRole);
                        activate(new IBreadCrumbPanelFactory(){
                        	public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel){
                        		BreadCrumbUtils.removePreviousBreadCrumbPanel(breadCrumbModel);
                        		//Set up lists
                        		List<RoleSpaceStdField> roleSpaceStdFieldsList;
                        		if (spaceRole.getSpace().getRoleSpaceStdFields()==null){
                        			roleSpaceStdFieldsList = new ArrayList<RoleSpaceStdField>();
                        		}
                        		else{
                        			roleSpaceStdFieldsList = new ArrayList<RoleSpaceStdField>(spaceRole.getSpace().getRoleSpaceStdFields());
                        		}
                        		
                        		List<SpaceRole> spaceRolesList;
                        		if (spaceRole.getSpace().getSpaceRoles()==null){
                        			spaceRolesList = new ArrayList<SpaceRole>();
                        		}
                        		else{
                        			spaceRolesList = new ArrayList<SpaceRole>(spaceRole.getSpace().getSpaceRoles());
                        		}

                        		return new SpacePermissionsPanel(componentId, breadCrumbModel, spaceRole.getSpace(), roleSpaceStdFieldsList/*, spaceRolesList*/);
            				}
                        });                	                        
                    }
				}//onclick
			});//add, new Link
    	}
    }

    private class SpaceRoleForm extends Form {                
        
        private SpaceRole spaceRole;
        
        public SpaceRoleForm(String id, final SpaceRole spaceRole) {
            
            super(id);
            add(new FeedbackPanel("feedback"));
            this.spaceRole = spaceRole;
            
            SpaceRoleModel modelObject = new SpaceRoleModel();                                
            modelObject.setRoleDescription(spaceRole.getDescription());
            final CompoundPropertyModel model = new CompoundPropertyModel(modelObject);
            setModel(model);

            // Submit ==========================================================
            Button btnSubmit = new Button("btnSubmit");
            add(btnSubmit);
            setDefaultButton(btnSubmit);
            
            // option ===========================================================
            final TextField field = new TextField("roleDescription");
            field.setRequired(true);
            field.add(new ErrorHighlighter());
            // validation: already exists?
            add(field);
            
            //form label
            field.setLabel(new ResourceModel("space_role_form.roleName"));
            add(new SimpleFormComponentLabel("roleDescriptionLabel",field));
        }
                
        @Override
        protected void onSubmit() {
            final SpaceRoleModel model = (SpaceRoleModel) getModelObject();
            spaceRole.setDescription(model.getRoleDescription());

			activate(new IBreadCrumbPanelFactory(){
				public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel){
					BreadCrumbUtils.removePreviousBreadCrumbPanel(breadCrumbModel);
					return new SpacePermissionsPanel(componentId, breadCrumbModel, spaceRole);
				}
			});
        }     
    }

    /**
     * custom form backing object that wraps role key
     * required for the create / edit use case
     */
    private class SpaceRoleModel implements Serializable {
                
        private String roleDescription;

        public String getRoleDescription() {
            return roleDescription;
        }

        public void setRoleDescription(String roleDescription) {
            this.roleDescription = roleDescription;
        }
               
    }
	
}