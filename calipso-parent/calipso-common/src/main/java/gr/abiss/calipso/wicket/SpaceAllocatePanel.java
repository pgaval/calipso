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

import gr.abiss.calipso.domain.RoleType;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.SpaceRole;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.UserSpaceRole;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.wicket.components.renderers.UserChoiceRenderer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanelLink;
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
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

/**
 * space allocate page
 */
public class SpaceAllocatePanel extends BasePanel {

    private long spaceId;
    private long selectedUserId;
    private Space space;
    
    public String getTitle(){
        return localize("space_allocate.usersAllocatedToSpace", localize(space.getNameTranslationResourceKey()) + " (" + space.getPrefixCode() + ")");
    }

    public void setSelectedUserId(long selectedUserId) {
        this.selectedUserId = selectedUserId;
    }
    
    public long getSpaceId() {
        return spaceId;
    }

    
    public SpaceAllocatePanel(String id, IBreadCrumbModel breadCrumbModel, long spaceId) { 
    	this(id, breadCrumbModel, spaceId, 0);
    }

    public SpaceAllocatePanel(String id, IBreadCrumbModel breadCrumbModel, long spaceId, long selectedUserId) {
    	super(id, breadCrumbModel);
    	
        this.spaceId = spaceId;
        space = getCalipso().loadSpace(spaceId);
        this.selectedUserId = selectedUserId;
        highlightPreviousPageSpace();
        add(new SpaceAllocateForm("form"));
    }    
    
    //highlight this spaceId on previous (the active one) page
	private void highlightPreviousPageSpace() {
		//when this object is created it is not in breadcrumb, so the previous page is the active one
		BreadCrumbPanel previous = (BreadCrumbPanel) getBreadCrumbModel().getActive();
		
		if (previous instanceof SpaceListPanel) {
			((SpaceListPanel) previous).setSelectedSpaceId(spaceId);
		} else if (previous instanceof UserAllocatePanel) {
			((UserAllocatePanel) previous).setSelectedSpaceId(spaceId);
		}
	}
	
	//renew this page
	public void renewPage(final long spaceId) {
		renewPage(spaceId, null);
	}

	//renew this page, using spaceId if not null		
	public void renewPage(final long spaceId, final Long selectedUserId){
		//remove this bread crumb and set a new one
		BreadCrumbUtils.removeActiveBreadCrumbPanel(getBreadCrumbModel());
		
	    activate(new IBreadCrumbPanelFactory()
	    {
	        public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel)
	        {
	        	if(selectedUserId == null)
	        		return new SpaceAllocatePanel(componentId, breadCrumbModel, spaceId);
	        	else
	        		return new SpaceAllocatePanel(componentId, breadCrumbModel, spaceId, selectedUserId);
	        }
	    });
	}

	public void preventRecursiveStackBuildup(){
		//get allBreadCrumbParticipants list from BreadCrumbBar
		List<IBreadCrumbParticipant> participants = getBreadCrumbModel().allBreadCrumbParticipants(); 
		
		if(participants.size() < 3)//must be at least 3 crumbs
			return;
		
		//get previous panel
		BreadCrumbPanel previous = (BreadCrumbPanel) participants.get(participants.size() - 2);

		// prevent recursive stack buildup, code below removes the previous breadcrumb
		if (previous instanceof UserAllocatePanel) { 
			BreadCrumbPanel thisPanel = (BreadCrumbPanel) participants.get(participants.size() - 1);
			BreadCrumbPanel startPanel = (BreadCrumbPanel) participants.get(participants.size() - 3);
			getBreadCrumbModel().setActive(startPanel);
			getBreadCrumbModel().setActive(thisPanel);
		}
	}
    
    /**
     * wicket form
     */     
    private class SpaceAllocateForm extends Form {

        private User user;
        //private String roleKey;
        private SpaceRole spaceRole;

        private DropDownChoice roleKeyChoice;        
        private Button allocateButton;  

        /**
         * function that attempts to pre-select roleKey for convenience
         * used on form init and also on Ajax onChange event for User choice
         */
//        private void initRoleChoice(User u) {            
//            List<String> roleKeys = new ArrayList(space.getMetadata().getRolesMap().keySet());
//            roleKeys.add("ROLE_SPACEADMIN");
//            for(String s : u.getRoleKeys(space)) {                
//                roleKeys.remove(s);
//            }
//
//            roleKeys.remove("ROLE_GUEST");
//            
//            if(roleKeys.size() == 1) {
//                // pre select role for convenience
//                roleKey = roleKeys.get(0);
//                allocateButton.setEnabled(true);
//            }
//            roleKeyChoice.setChoices(roleKeys);                    
//            roleKeyChoice.setEnabled(true);
//            
//            allocateButton.setEnabled(roleKeys!=null && roleKeys.size()>0 && roleKeyChoice.getValue()!=null && !roleKeyChoice.getValue().equals("-1"));
//        }        


        private void initRoleChoice(User u) {
//        	List<SpaceRole> spaceRoles = getJtrac().findSpaceRolesForSpace(space);
//        	List<UserSpaceRole> userSpaceRolesList;
//        	if (u.getUserSpaceRoles()!=null){
//        		userSpaceRolesList = new ArrayList<UserSpaceRole>(u.getUserSpaceRoles());
//        	}
//        	else{
//        		userSpaceRolesList = new ArrayList<UserSpaceRole>();
//        	}
//        	
//        	//Remove already assigned space roles 
//        	for (UserSpaceRole userSpaceRole : userSpaceRolesList){
//        		if (spaceRoles.contains(userSpaceRole.getSpaceRole())){
//        			spaceRoles.remove(userSpaceRole.getSpaceRole());
//        		}//if
//        	}//for
//
//        	//Remove guest space Role
//        	//for (SpaceRo)

        	List<SpaceRole> spaceRoles = getCalipso().findAvailableSpaceRolesForUser(space, u);
        	// only an admin can assign admin roles
        	if(!getPrincipal().isGlobalAdmin()){
        		if(spaceRoles != null && spaceRoles.size() > 0){
        			List<SpaceRole> unauthorizedRoles = new LinkedList<SpaceRole>();
        			for(SpaceRole role : spaceRoles){
        				if(role.getRoleType().equals(RoleType.ADMINISTRATOR)){
        					unauthorizedRoles.add(role);
        				}
        			}
        			spaceRoles.removeAll(unauthorizedRoles);
        		}
        	}
        	if (spaceRoles.size()==1){
        		spaceRole = spaceRoles.get(0);
        		allocateButton.setEnabled(true);
        	}

        	roleKeyChoice.setChoices(spaceRoles);
        	roleKeyChoice.setEnabled(true);
        	roleKeyChoice.setNullValid(false);
        	allocateButton.setEnabled(spaceRoles!=null && spaceRoles.size()>0 && roleKeyChoice.getValue()!=null && !roleKeyChoice.getValue().equals("-1"));
        }

        //-----------------------------------------------------------------------------------------

        public SpaceAllocateForm(String id) {
            super(id);
                                    
            if(selectedUserId > 0) {
                // pre-select newly created user for convenience
                user = getCalipso().loadUser(selectedUserId);
            }
            
            final CompoundPropertyModel model = new CompoundPropertyModel(this);
            setModel(model);
            
            final WebMarkupContainer legend = new WebMarkupContainer("legend");
            legend.add(new Label("label", localize("space_allocate.usersAllocatedToSpace", localize(space.getNameTranslationResourceKey()) + " (" + space.getPrefixCode() + ")"))); 
            add(legend);
            
            
            final WebMarkupContainer allocatedTable = new WebMarkupContainer("allocatedTable");
            add(allocatedTable);
            
            
            LoadableDetachableModel usrsModel = new LoadableDetachableModel() {
                protected Object load() {
                    logger.debug("loading user space roles list from database");
                    return getCalipso().findUserRolesForSpace(spaceId);
                }
            };                     
            
            if(((List<UserSpaceRole>)usrsModel.getObject()).size()==0){
            	legend.setVisible(false);
            	allocatedTable.setVisible(false);
            }
            	
            
            
            final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
            
            allocatedTable.add(new ListView("usrs", usrsModel) {
                protected void populateItem(ListItem listItem) {
                    final UserSpaceRole usr = (UserSpaceRole) listItem.getModelObject();
                    
                    if(selectedUserId == usr.getUser().getId()) {
                        listItem.add(new SimpleAttributeModifier("class", "selected"));
                    } else if(listItem.getIndex() % 2 == 1) {
                        listItem.add(sam);
                    }
                    
                    listItem.add(new BreadCrumbLink("loginName", getBreadCrumbModel()){
						@Override
						protected IBreadCrumbParticipant getParticipant(String componentId) {
							//if a condition is met, removes the previous crumb from the list
							preventRecursiveStackBuildup();
							return new UserAllocatePanel(componentId, getBreadCrumbModel(), usr.getUser().getId(), spaceId);
						}
                    }.add(new Label("loginName", new PropertyModel(usr, "user.loginName"))));
                    
                    listItem.add(new Label("name", new PropertyModel(usr, "user.name")));
                    listItem.add(new Label("lastName", new PropertyModel(usr, "user.lastname")));
                    listItem.add(new Label("organization", new PropertyModel(usr, "user.organization.name")));
                    listItem.add(new Label("spaceRole", new PropertyModel(usr, "spaceRole.description")));
                    listItem.add(new Button("deallocate") {
                        @Override
                        public void onSubmit() {
                            // avoid lazy loading problem
                            UserSpaceRole temp = getCalipso().loadUserSpaceRole(usr.getId());
                            getCalipso().removeUserSpaceRole(temp);
                            refreshPrincipal(temp.getUser());
                            
                            renewPage(spaceId);
                        }
                    });
                }
            });
            
            add(new BreadCrumbPanelLink("createNewUser", SpaceAllocatePanel.this, UserFormPanel.class));
//            List<User> users = getJtrac().findUnallocatedUsersForSpace(spaceId);
            List<User> users;

            if (getPrincipal().isSpaceAdmin() && !getPrincipal().isGlobalAdmin()){
            	List<User> allUsers = getCalipso().findUnallocatedUsersForSpace(spaceId);
            	users = new ArrayList<User>();
            	
//            	 for (Space space : getPrincipal().getSpaces()){
//            		 for (User userFromAllUsers : allUsers){
//            		 }//for
//            	 }
            	
	       		 for (User userFromAllUsers : allUsers){
	       			 if (userFromAllUsers.getSpaces().contains(space)){
	       				 users.add(userFromAllUsers);
	       			 }//if
	       		 }//for
	
	       		 if (!users.contains(getPrincipal())){
	       			 users.add(getPrincipal());
	       		 }//if
            	
            }
            else{
            	users = getCalipso().findUnallocatedUsersForSpace(spaceId);
            }

            DropDownChoice userChoice = new DropDownChoice("user", users, new UserChoiceRenderer());
            userChoice.setNullValid(true);

            add(userChoice);
            //form label for user
            userChoice.setLabel(new ResourceModel("space_allocate.user"));
            add(new SimpleFormComponentLabel("userLabel", userChoice));
            
            userChoice.add(new AjaxFormComponentUpdatingBehavior("onChange") {
                protected void onUpdate(AjaxRequestTarget target) {
                    User u = (User) getFormComponent().getConvertedInput();
                    if (u == null) {
                        roleKeyChoice.setEnabled(false);
                        allocateButton.setEnabled(false);
                    } else {
                        User temp = getCalipso().loadUser(u.getId());
                        // populate choice, enable button etc
                        initRoleChoice(temp);
                    }
                    
                    target.addComponent(roleKeyChoice);
                    target.addComponent(allocateButton);
                }
            });             
            
            roleKeyChoice = new DropDownChoice("spaceRole", new ArrayList<SpaceRole>(), new IChoiceRenderer(){
            	public Object getDisplayValue(Object object) {
            		return ((SpaceRole)object).getDescription();
            	}
            	
            	public String getIdValue(Object object, int id) {
            		return String.valueOf(((SpaceRole)object).getId());
            	}
            });
            
            roleKeyChoice.setOutputMarkupId(true);
            roleKeyChoice.setEnabled(false);            
            //roleKeyChoice.setNullValid(true);
            roleKeyChoice.setNullValid(false);
            
            roleKeyChoice.add(new AjaxFormComponentUpdatingBehavior("onChange") {
            	protected void onUpdate(AjaxRequestTarget target) {
            		allocateButton.setEnabled(roleKeyChoice.getValue()!=null && !roleKeyChoice.getValue().equals("-1"));
            		target.addComponent(allocateButton);
            	}
            });
            
            add(roleKeyChoice);
            
            //form label for Role drop down manu
			roleKeyChoice.setLabel(new ResourceModel("space_allocate.role"));
			add(new SimpleFormComponentLabel("spaceRoleLabel", roleKeyChoice));
            
            allocateButton = new Button("allocate") {
                @Override
                public void onSubmit() {                    
                    if(user == null || spaceRole == null) {
                        return;
                    }
                    // avoid lazy init problem
                    User temp = getCalipso().loadUser(user.getId());
                    getCalipso().storeUserSpaceRole(temp, spaceRole);

                    refreshPrincipal(temp);
                    renewPage(spaceId, user.getId());
                }
            };
            
            allocateButton.setOutputMarkupId(true);
            allocateButton.setEnabled(false);
            add(allocateButton);    
            
            if(users.size() == 1) {
                // pre select the user for convenience
                user = users.get(0);                
            }              
            
            if(user != null) {
                initRoleChoice(user);
            } 
            
            
            
            // done ============================================================
            add(new Button("done"){
            	@Override
                public void onSubmit() {            		
            		//check if this panel is used in space creation i.e SpaceListPanel exists in BreadCrumb
            		if(BreadCrumbUtils.getPanel(getBreadCrumbModel(), SpaceFormPanel.class) != null){
            			
//            			//check if this user can view the created space
//            	        List<UserSpaceRole> spaceRoles = new ArrayList(getPrincipal().getUserSpaceRoles());            	        
//            		    for(UserSpaceRole u: spaceRoles){
//            		    	if(u.getSpaceRole().getSpace()!=null && u.getSpaceRole().getSpace().equals(space)){
//            		    		setCurrentSpace(space);
//            					setResponsePage(SpacePage.class); //go to single space
//            					return;
//            		        }
//            		    }
            		    
            		    //if the user cannot view the above space, take him to SpaceListPanel        		    
                		final BreadCrumbPanel spaceListPanel = BreadCrumbUtils.getPanel(getBreadCrumbModel(), SpaceListPanel.class);
                		if(spaceListPanel != null){
    						((SpaceListPanel)spaceListPanel).setSelectedSpaceId(space.getId());

    	            		activate(new IBreadCrumbPanelFactory() {
    	    					public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
    	    						return spaceListPanel;
    	    					}
    	    				});    						
                			return;
                		}
            		    
            		}

            		//else go to prev panel
            		activate(new IBreadCrumbPanelFactory() {
    					public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
    						return (BreadCrumbPanel) BreadCrumbUtils.backBreadCrumbPanel(getBreadCrumbModel());
    					}
    				});
            	}        	
            });
            
            
            // save ============================================================
//            add(new Button("save") {
//                @Override
//                public void onSubmit() {
//                	// boolean isNewSpace = space.getId() == 0;
//                	//TODO refactor for roleSpaceFields 
//                    
//                	for (RoleSpaceStdField roleSpaceStdField : roleSpaceFields){
//                    	space.add(roleSpaceStdField);
//                    }
//                    
//                    getJtrac().storeSpace(space);
//
//                    // current user may be allocated to this space, and e.g. name could have changed
//                    refreshPrincipal();
//                    if (isNewSpace){
//                    	isNewSpace = false; //after the user allocation, we edit the space
//                        activate(new IBreadCrumbPanelFactory(){
//                        	public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel){
//            					return new SpaceAllocatePanel(componentId, breadCrumbModel, space.getId());
//            				}
//                        });
//                    }
//                    else{
//                        activate(new IBreadCrumbPanelFactory(){
//                        	public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel){
//                        		SpaceListPanel spaceListPanel = new SpaceListPanel(componentId, breadCrumbModel);
//                        		spaceListPanel.setSelectedSpaceId(space.getId());
//                        		BreadCrumbUtils.moveToPanelForRelod(breadCrumbModel, SpaceListPanel.class);
//                        		return spaceListPanel;
//            				}
//                        });
//                    }
//                }
//            });
            
            
            
            
        }
    }
}
