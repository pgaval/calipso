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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
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
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

/**
 * user allocate page
 */
public class UserAllocatePanel extends BasePanel {

	private long userId;
	private long selectedSpaceId;
	private User user;

    public String getTitle(){
        return localize("user_allocate_space.spacesAllocated", user.getName() + " (" + user.getUsername() + ")");
    }
    
	public void setSelectedSpaceId(long selectedSpaceId) {
		this.selectedSpaceId = selectedSpaceId;
	}

	public UserAllocatePanel(String id, IBreadCrumbModel breadCrumbModel, long userId) {
		super(id, breadCrumbModel);
		this.userId = userId;
		user = getCalipso().loadUser(userId);
		highlightPreviousPageUser();
		add(new UserAllocateForm("form"));
	}

	public UserAllocatePanel(String id, IBreadCrumbModel breadCrumbModel, long userId, long selectedSpaceId) {
		super(id, breadCrumbModel);
		
		this.userId = userId;
		user = getCalipso().loadUser(userId);
		this.selectedSpaceId = selectedSpaceId;
		highlightPreviousPageUser();
		add(new UserAllocateForm("form"));
	}
	
    //highlight this userId on previous (the active one) page
	private void highlightPreviousPageUser() {
		//when this object is created it is not in breadcrumb, so the previous page is the active one
		BreadCrumbPanel previous = (BreadCrumbPanel) getBreadCrumbModel().getActive();
		
		if (previous instanceof UserListPanel) {
			((UserListPanel) previous).setSelectedUserId(userId);
		}
		else if (previous instanceof SpaceAllocatePanel) {
			((SpaceAllocatePanel) previous).setSelectedUserId(userId);
		}
	}
	
	//renew this page
	public void renewPage(final long userId) {
		renewPage(userId, null);
	}
	
	//renew this page, using spaceId if not null		
	public void renewPage(final long userId, final Long selectedSpaceId){
		//remove this bread crumb and set a new one
		BreadCrumbUtils.removeActiveBreadCrumbPanel(getBreadCrumbModel());
		
	    activate(new IBreadCrumbPanelFactory()
	    {
	        public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel)
	        {
	        	if(selectedSpaceId == null)
	        		return new UserAllocatePanel(componentId, breadCrumbModel, userId);
	        	else
	        		return new UserAllocatePanel(componentId, breadCrumbModel, userId, selectedSpaceId);
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
		if (previous instanceof SpaceAllocatePanel) { 
			BreadCrumbPanel thisPanel = (BreadCrumbPanel) participants.get(participants.size() - 1);
			BreadCrumbPanel startPanel = (BreadCrumbPanel) participants.get(participants.size() - 3);
			getBreadCrumbModel().setActive(startPanel);
			getBreadCrumbModel().setActive(thisPanel);
		}
	}

	/**
	 * wicket form
	 */
	private class UserAllocateForm extends Form {
		private static final long serialVersionUID = 1L;
		
		private Space space;
		private SpaceRole spaceRole;
		private CalipsoFeedbackMessageFilter filter;
		private FeedbackPanel feedback;

		public Space getSpace() {
			return space;
		}

		public void setSpace(Space space) {
			this.space = space;
		}

		public SpaceRole getSpaceRole() {
			return this.spaceRole;
		}
		
		public void setSpaceRole(SpaceRole spaceRole) {
			this.spaceRole = spaceRole;
		}

		private DropDownChoice roleKeyChoice;
		private Button allocateButton;

		/**
		 * function that attempts to pre-select roleKey for convenience used on
		 * form init and also on Ajax onChange event for Space choice
		 */
		private void initRoleChoice(Space s) {
			List<SpaceRole> spaceRoles = getCalipso().findAvailableSpaceRolesForUser(s, user);
			
        	if (spaceRoles.size()==1){
        		spaceRole = spaceRoles.get(0);
//        		allocateButton.setEnabled(true);
        	}

        	roleKeyChoice.setChoices(spaceRoles);
        	roleKeyChoice.setEnabled(true);

        	allocateButton.setEnabled(true);
		}

		
//		@Override
//		protected void validate() {
//			filter.reset();
//			super.validate();
//		}
		
		public UserAllocateForm(String id) {

			super(id);
			final CompoundPropertyModel model = new CompoundPropertyModel(this);
			setModel(model);

            feedback = new FeedbackPanel("feedback");
            filter = new CalipsoFeedbackMessageFilter();
            feedback.setFilter(filter);
            add(feedback);   
			
//			add(new FeedbackPanel("feedback"));

			add(new Label("label", localize("user_allocate_space.adminprivs", user.getName() + " (" + user.getLoginName() + ")")));

			List<UserSpaceRole> usrs = new ArrayList<UserSpaceRole>(user.getUserSpaceRoles());

			final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
		
			add(new WebMarkupContainer("nodata").setVisible(usrs.size()==0));
			
			add(new ListView("usrs", usrs) {
				protected void populateItem(ListItem listItem) {
					final UserSpaceRole usr = (UserSpaceRole)listItem.getModelObject();

					if (usr.getSpaceRole().getSpace() != null
							&& usr.getSpaceRole().getSpace().getId() == selectedSpaceId) {
						listItem.add(new SimpleAttributeModifier("class",
								"selected"));
					} else if (listItem.getIndex() % 2 == 1) {
						listItem.add(sam);
					}
					WebMarkupContainer spaceSpan = new WebMarkupContainer("space");
					listItem.add(spaceSpan);
					if (usr.getSpaceRole().getSpace() == null) {
						spaceSpan.setVisible(false);
					} else {
						spaceSpan.add(new Label("name", localize(usr.getSpaceRole().getSpace().getNameTranslationResourceKey())));
						
						// add a BreadCrumb link to SpaceAllocateUser
						spaceSpan.add(new BreadCrumbLink("prefixCode", getBreadCrumbModel()) {
							@Override
							protected IBreadCrumbParticipant getParticipant(String componentId) {
								//if a condition is met, removes the previous crumb from the list
								preventRecursiveStackBuildup();
								return new SpaceAllocatePanel(componentId, getBreadCrumbModel(), usr.getSpaceRole().getSpace().getId(), userId);
							}
							
						}.add(new Label("prefixCode", usr.getSpaceRole().getSpace().getPrefixCode())));
						
					}
					listItem.add(new Label("spaceRole", new PropertyModel(usr, "spaceRole.description")));
					Button deallocate = new Button("deallocate") {
						@Override
						public void onSubmit() {
							getCalipso().removeUserSpaceRole(usr);
							refreshPrincipal(usr.getUser());
							renewPage(userId);
						}
					};
					// make it impossible to remove the first user ensuring
					// there is always an admin
					if (usr.getUser().getId() == 1 && usr.getSpaceRole().getRoleType().equals(RoleType.ADMINISTRATOR)) {
						deallocate.setVisible(false);
					}
					listItem.add(deallocate);
				}
			});

			List<Space> spaces = new ArrayList<Space>();
			User u = getPrincipal();
			if (!u.isGlobalAdmin()){
				List<Space> spaceList = getCalipso().findUnallocatedSpacesForUser(user.getId());
        		for (Space s: spaceList){
        			if (u.getSpacesWhereUserIsAdmin().contains(s)){
        				spaces.add(s);
        			}//if
        		}//for
			}//if
			else{
				spaces = getCalipso().findUnallocatedSpacesForUser(u.getId());
			}//else

			// space drop down menu ---------------------------------------------------------------
			final DropDownChoice spaceChoice = new DropDownChoice("space", spaces,
					new IChoiceRenderer() {
						public Object getDisplayValue(Object o) {
							return localize(((Space) o).getNameTranslationResourceKey());
						}

						public String getIdValue(Object o, int i) {
							return ((Space) o).getId() + "";
						}
					});
			spaceChoice.setNullValid(true);

			add(spaceChoice);

			// form label for space
			spaceChoice.setLabel(new ResourceModel("user_allocate_space.space"));
			add(new SimpleFormComponentLabel("spaceLabel", spaceChoice));

			spaceChoice.add(new AjaxFormComponentUpdatingBehavior("onChange") {
				protected void onUpdate(AjaxRequestTarget target) {
					Space s = (Space) getFormComponent().getConvertedInput();
					if (s == null) {
						roleKeyChoice.setEnabled(false);
						allocateButton.setEnabled(false);
					} else {
						Space temp = getCalipso().loadSpace(s.getId());
						// populate choice, enable button etc
						initRoleChoice(temp);
					}
					target.addComponent(roleKeyChoice);
					target.addComponent(allocateButton);
				}
			});

			// role drop down menu ----------------------------------------------------------------
			roleKeyChoice  = new DropDownChoice("spaceRole", new ArrayList<SpaceRole>(), new IChoiceRenderer(){
            	public Object getDisplayValue(Object object) {
            		return ((SpaceRole)object).getDescription();
            	}
            	
            	public String getIdValue(Object object, int id) {
            		return String.valueOf(((SpaceRole)object).getId());
            	}
            });
			
			roleKeyChoice.add(new AjaxFormComponentUpdatingBehavior("onChange"){
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					allocateButton.setEnabled(roleKeyChoice.getValue()!=null && !roleKeyChoice.getValue().equals("-1"));
					target.addComponent(allocateButton);
				}
			});
			
			roleKeyChoice.setOutputMarkupId(true);
			roleKeyChoice.setEnabled(false);
			roleKeyChoice.setRequired(true);
			roleKeyChoice.setNullValid(true);
			roleKeyChoice.add(new ErrorHighlighter());
			add(roleKeyChoice);

			//form label for Role drop down manu
			roleKeyChoice.setLabel(new ResourceModel("user_allocate_space.role"));
			add(new SimpleFormComponentLabel("spaceRoleLabel", roleKeyChoice));

			allocateButton = new Button("allocate") {
				@Override
				public void onSubmit() {
					if (space == null || spaceRole == null) {
						return;
					}
					getCalipso().storeUserSpaceRole(user, spaceRole);
					refreshPrincipal(user);

					renewPage(userId, space.getId());
				}
			};
			allocateButton.setOutputMarkupId(true);
			allocateButton.setEnabled(false);
			allocateButton.setDefaultFormProcessing(false);
			add(allocateButton);

			if (spaces.size() == 1) {
				// pre select space for convenience
				space = spaces.get(0);
				// see if the role can be pre selected also at least populate
				// choice, enable button etc
				initRoleChoice(space);
			}

			// make admin ======================================================

			WebMarkupContainer makeAdmin = new WebMarkupContainer("makeAdmin");
			if (user.isGlobalAdmin()) {
				makeAdmin.setVisible(false);
			} else {
				makeAdmin.add(new Button("makeAdmin") {
					@Override
					public void onSubmit() {
						getCalipso().storeUserSpaceRole(user, getCalipso().loadAdministrator());
						refreshPrincipal(user);
						renewPage(userId);
					}
				});
			}
			add(makeAdmin);

			
			// make space admin ======================================================

			WebMarkupContainer makeSpaceAdmin = new WebMarkupContainer("makeSpaceAdmin");
				makeSpaceAdmin.add(new Button("makeSpaceAdmin") {
					@Override
					public void onSubmit() {
						if (user.getSpaces()!=null && user.getSpaces().size()>0){
							for (Space space : user.getSpaces()){
								SpaceRole spaceAdministrator = getCalipso().loadSpaceAdministrator(space);
								
								if (!user.getSpaceRoles(space).contains(spaceAdministrator)){
									getCalipso().storeUserSpaceRole(user, spaceAdministrator);
								}//if
							}//for
						}//if
						refreshPrincipal(user);
						renewPage(userId);
					}
				});
			add(makeSpaceAdmin);
			
            // done ============================================================
            add(new Button("done"){
            	@Override
                public void onSubmit() {
            		activate(new IBreadCrumbPanelFactory() {
						public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
							//go to prev panel
							return (BreadCrumbPanel) BreadCrumbUtils.backBreadCrumbPanel(getBreadCrumbModel());
						}
					});         
            	}        	
            });
            
            // validation ===============================================================
            AbstractFormValidator emptyValuesValidator = new AbstractFormValidator(){
				public FormComponent[] getDependentFormComponents() {
					return new FormComponent[] {spaceChoice, roleKeyChoice};
				}//getDependentFormComponents

				public void validate(Form form) {
					if (spaceChoice.getValue()==null || roleKeyChoice.getValue()==null){
						if (spaceChoice.getValue()==null){
							spaceChoice.error(localize("Required"));
							return;
						}
						if (roleKeyChoice.getValue()==null){
							roleKeyChoice.error(localize("Required"));
						}
					}
				}//validate
            	
            };

            add(emptyValuesValidator);
		}

	}

}
