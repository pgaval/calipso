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

import gr.abiss.calipso.domain.Country;
import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.OrganizationSearch;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.wicket.components.validators.PhoneNumberValidator;
import gr.abiss.calipso.wicket.components.validators.ValidationUtils;
import gr.abiss.calipso.wicket.components.viewLinks.OrganizationViewLink;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanelLink;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
/**
 * user edit form
 */
public class UserFormPanel extends BasePanel {
	protected static final Logger logger = Logger.getLogger(UserFormPanel.class);
    public String getTitle(){
        return localize("user_form.userDetails");
    }
    
    public UserFormPanel(String id, IBreadCrumbModel breadCrumbModel) {  
    	super(id, breadCrumbModel);
        User user = new User();
        user.setLocale(getCalipso().getDefaultLocale());
        add(new UserForm("form", user));
        
        //hides delete link
        deleteLink(user);
        
        //make a cancel link
        getBackLinkPanel().makeCancel();
        
        //highlight this user on previous page
        setHighlightOnPreviousPage(user.getId());
    }    
    
    public UserFormPanel(String id, IBreadCrumbModel breadCrumbModel, User user) {
    	super(id, breadCrumbModel);
        add(new UserForm("form", user));
        
        //delete link
        deleteLink(user);
        
        //make a cancel link
        getBackLinkPanel().makeCancel();
        
        //highlight this user on previous page
        setHighlightOnPreviousPage(user.getId());
    }

    public UserFormPanel(String id, IBreadCrumbModel breadCrumbModel, User user, boolean directCall) {
    	super(id, breadCrumbModel);
        add(new UserForm("form", user));
        
        //delete link
        deleteLink(user);
    }

    
    private void setHighlightOnPreviousPage(long setSelectedUserId){
		// get previous page. We use the active one as the previous because
    	//when this page is created it's not yet activated.
		BreadCrumbPanel previous = (BreadCrumbPanel) getBreadCrumbModel().getActive();

        if (previous instanceof UserListPanel) {
        	((UserListPanel) previous).setSelectedUserId(setSelectedUserId);
        }          	
    }
    
    
    /**
     * wicket form
     */    
    private class UserForm extends Form {
    	
        private User user;
        private String password;
        private String passwordConfirm;
        private boolean sendNotifications;
        private boolean isDemoUser = false;
        private DropDownChoice spaceChoice;
        private DropDownChoice roleChoice;
        private String roleKey;
        private Space space;
        private Organization organization;
        private IconFormPanel iconFormPanel;
        
        private CalipsoFeedbackMessageFilter filter;

        
        public UserForm(String id, final User user) {
            
            super(id);
            if(user!=null){
            	this.user = user;
            }else{
            	this.user = getPrincipal();
            }
            final CompoundPropertyModel model = new CompoundPropertyModel(this);
            setModel(model);
            //
            if(user!=null && user.getId() == 0) {
                sendNotifications = true;
            }
            else{
            	isDemoUser = getPrincipal().getLoginName().toLowerCase().contains("demo");
            }
            FeedbackPanel feedback = new FeedbackPanel("feedback");
            filter = new CalipsoFeedbackMessageFilter();
            feedback.setFilter(filter);
            add(feedback);             
                
            // icon form panel======================================================
            iconFormPanel = new IconFormPanel("iconFormPanel", user); 
            add(iconFormPanel);

            // login name ======================================================
            final TextField loginName = new TextField("user.loginName");
            loginName.setRequired(true).setEnabled(!isDemoUser);
            // if you already have create user you can't edit the login id
            if(user.getId()!= 0){ // user already created
            	loginName.setEnabled(false);
            }
            loginName.add(new ErrorHighlighter());
            loginName.setOutputMarkupId(true);           
            add(new Behavior(){
                public void renderHead(IHeaderResponse response) {
                    response.renderOnLoadJavaScript("document.getElementById('" + loginName.getMarkupId() + "').focus()");
                }
            });
            
            // validation no strange characters
            loginName.add(new AbstractValidator() {
                protected void onValidate(IValidatable v) {
                    String s = (String) v.getValue();                   
                    if(!ValidationUtils.isValidLoginName(s)) {
                        error(v);
                    }
                }
                @Override
                protected String resourceKey() {                    
                    return "user_form.loginId.error.invalid";
                }                
            });      
            
            // validation: does user already exist with same loginName?
            loginName.add(new AbstractValidator() {
                protected void onValidate(IValidatable v) {
                    String s = (String) v.getValue();
                    User temp = getCalipso().loadUser(s);
                    if(temp != null && temp.getId() != user.getId()) {
                        error(v);
                    }
                }
                @Override
                protected String resourceKey() {                    
                    return "user_form.loginId.error.exists";
                }                
            });   
            add(loginName);
            
            //form label
            loginName.setLabel(new ResourceModel("user_form.loginName"));
            add(new SimpleFormComponentLabel("loginNameLabel",loginName));
            
            // locked ==========================================================
            WebMarkupContainer locked = new WebMarkupContainer("locked");
            // don't show the locked checkbox when creating new user
            // only way you can edit someone else is if you are an admin
            // and of course, don't allow locking self            
            if(user.getId() != 0 && user.getId() != getPrincipal().getId()) {
            	CheckBox lockedCheckBox = new CheckBox("user.locked");
            	lockedCheckBox.setEnabled(!isDemoUser);
                locked.add(lockedCheckBox);
                
            	//form label
            	lockedCheckBox.setLabel(new ResourceModel("user_form.locked"));
            	locked.add(new SimpleFormComponentLabel("lockedLabel", lockedCheckBox));
            } else {
                locked.setVisible(false);
            }
            
            add(locked);
            
            // name ============================================================
            TextField name=new TextField("user.name");
            name.setRequired(true).add(new ErrorHighlighter()).setEnabled(!isDemoUser);
            add(name);
            
        	//form label
            name.setLabel(new ResourceModel("user_form.name"));
        	add(new SimpleFormComponentLabel("nameLabel", name));
        	
        	 // last name ============================================================
            TextField lastname=new TextField("user.lastname");
            lastname.setRequired(true).add(new ErrorHighlighter()).setEnabled(!isDemoUser);
            add(lastname);
            
        	//form label
            lastname.setLabel(new ResourceModel("user_form.lastname"));
        	add(new SimpleFormComponentLabel("lastnameLabel", lastname));
            
            // email ===========================================================
        	TextField email=new TextField("user.email");
        	// email validation
        	email.add(EmailAddressValidator.getInstance());
        	email.setRequired(true).add(new ErrorHighlighter()).setEnabled(!isDemoUser);
        	add(email);
            
        	//form label
        	email.setLabel(new ResourceModel("user_form.email"));
        	add(new SimpleFormComponentLabel("emailLabel", email));    

        	
        	//Address -----------------------------------------------------------------------------//
			//Field
			TextField address = new TextField("user.address");
			//address.setRequired(true);
			address.add(new ErrorHighlighter());
			add(address);
			
			//Label
			address.setLabel(new ResourceModel("user_form.address"));
			add(new SimpleFormComponentLabel("addressLabel", address));//
			
			//Postal Code -------------------------------------------------------------------------//
			//Field TODO: 
			TextField zip = new TextField("user.zip");
			//zip.setRequired(true);
			zip.add(new ErrorHighlighter());
			add(zip);
			
			//Label
			zip.setLabel(new ResourceModel("user_form.zip"));
			add(new SimpleFormComponentLabel("zipLabel", zip));//
			// country
			// get all countries
			final List<Country> allCountriesList  = getCalipso().findAllCountries();
			/*logger.info("User's Name : "+user.getName());
			logger.info("User's id : "+user.getId());
			logger.info("User's Country : "+user.getCountry());*/
			DropDownChoice countryChoice = getCountriesDropDown("user.country", allCountriesList);
			
			countryChoice.setNullValid(false);
			countryChoice.add(new ErrorHighlighter());
			//countryChoice.setRequired(true);
			countryChoice.setLabel(new ResourceModel("user_form.country"));
			add(countryChoice);
			// Label
			add(new SimpleFormComponentLabel("countryLabel", countryChoice));
			
			
			//Phone -------------------------------------------------------------------------------//
			//Field
			TextField phone = new TextField("user.phone");
			// phone validation for organization phone number
			phone.add(new PhoneNumberValidator());
			//phone.setRequired(true);
			phone.add(new ErrorHighlighter());
			add(phone);
			
			//Label
			phone.setLabel(new ResourceModel("user_form.phone"));
			add(new SimpleFormComponentLabel("phoneLabel", phone));

			//alternativePhone -------------------------------------------------------------------------------//
			//Field
			TextField alternativePhone = new TextField("user.alternativePhone");
			// phone validation for phone number
			alternativePhone.add(new PhoneNumberValidator());
			add(alternativePhone);
			
			//Label
			alternativePhone.setLabel(new ResourceModel("user_form.alternativePhone"));
			add(new SimpleFormComponentLabel("alternativePhoneLabel", alternativePhone));

			//fax -------------------------------------------------------------------------------//
			//Field
			TextField fax = new TextField("user.fax");
			// phone validation for fax phone number
			fax.add(new PhoneNumberValidator());
			add(fax);
			
			//Label
			fax.setLabel(new ResourceModel("user_form.fax"));
			add(new SimpleFormComponentLabel("faxLabel", fax));
        	
            
        	// allow selection of an organization if the user is new or checked by an admin
        	if(user.getId() == 0 || getPrincipal().isGlobalAdmin()){ 
        		Fragment newOrganizationFragment = new Fragment("organizationArea","newOrganizationFragment", this);
        		add(newOrganizationFragment);
        		// load All organization
        		List<Organization> organizationList = getCalipso().findAllOrganizations();
        		// create dropdownChoice that shows all organizations
        		DropDownChoice organizationChoice = new DropDownChoice("user.organization", organizationList, new IChoiceRenderer(){
        			public Object getDisplayValue(Object object) {
        				return ((Organization)object).getName();
        			}
        			// return index of the object in organizationList
        			public String getIdValue(Object object, int index) {
        				return String.valueOf(index);
        			}
        		});
        		// add to fragment 
        		newOrganizationFragment.add(organizationChoice);
        		// create label Label
        		organizationChoice.setLabel(new ResourceModel("user_form.organization"));
        		//
        		newOrganizationFragment.add(new SimpleFormComponentLabel("organizationLabel", organizationChoice));
        	}//if
        	else{ // edit user
        		
        		if(user.getOrganization()!=null){
            		// get user's organization name
            		// For all spaces admin's default organization, and organization id is null
        			Fragment editOrganizationFragment = new Fragment("organizationArea","editOrganizationFragment", this);
            		add(editOrganizationFragment);
        			Organization org = getCalipso().loadOrganization(user.getOrganization().getId());
        			editOrganizationFragment.add(new OrganizationViewLink("organization",getBreadCrumbModel(), org));
        		}
        		else{
        			Fragment noOrganizationFragment = new Fragment("organizationArea", "noOrganizationFragment", this);
        			add(noOrganizationFragment);
        			noOrganizationFragment.add(new Label("noOrganizationLabel", new ResourceModel("organization.noOrganization")));
        		}
        		
        	}
            // locale ==========================================================
            final Map<String, String> locales = getCalipso().getLocales();
            List<String> localeKeys = new ArrayList<String>(locales.keySet());
            DropDownChoice localeChoice = new DropDownChoice("user.locale", localeKeys, new IChoiceRenderer() {
                public Object getDisplayValue(Object o) {
                    return locales.get(o);
                }
                public String getIdValue(Object o, int i) {
                    return o.toString();
                }                
            });
            add(localeChoice);

            //form label
            localeChoice.setLabel(new ResourceModel("user_form.language"));
            add(new SimpleFormComponentLabel("languageLabel", localeChoice));      

            //==============================================================
            //Space-role allocation on user creation by space admin 
            //otherwise, (edit or user creation by global admin)  
            // hide user - space - role allocation
            
            WebMarkupContainer spaceRoleAllocationArea = new WebMarkupContainer("spaceRoleAllocationArea");
            
            //Add space -----------------------------------------------------
            List<Space> spaceList = new ArrayList<Space>();
            spaceList = getPrincipal().getSpacesWhereUserIsAdmin();
            spaceChoice = new DropDownChoice("space", spaceList, new IChoiceRenderer(){
            	public String getIdValue(Object object, int i) {
            		return String.valueOf(((Space)object).getId());
            	}
            	
            	public Object getDisplayValue(Object object) {
            		return localize(((Space)object).getNameTranslationResourceKey());
            	}
            });
            spaceChoice.setRequired(true);
            spaceChoice.add(new ErrorHighlighter());
            spaceChoice.setNullValid(false);

            Label spaceLabel = new Label("spaceLabel");
            spaceChoice.setLabel(new ResourceModel("user_form.space"));
            spaceRoleAllocationArea.add(new SimpleFormComponentLabel("spaceLabel", spaceChoice));
            
			spaceChoice.add(new AjaxFormComponentUpdatingBehavior("onChange") {
				protected void onUpdate(AjaxRequestTarget target) {
					Space s = (Space) getFormComponent().getConvertedInput();
					if (s == null) {
						roleChoice.setEnabled(false);
					} else {
						Space temp = getCalipso().loadSpace(s.getId());
						// populate choice, enable button etc
						initRoleChoice(temp);
					}
					target.addComponent(roleChoice);
				}
			});
            
            spaceRoleAllocationArea.add(spaceChoice);

            //Add Role ---------------------------------------------------
            roleChoice = new DropDownChoice("roleKey"/*, new Model(), new ArrayList()*/);
			roleChoice.setOutputMarkupId(true);
			roleChoice.setEnabled(false);
			roleChoice.setRequired(true);
			roleChoice.setNullValid(true);
			roleChoice.add(new ErrorHighlighter());
            
			Label roleLabel = new Label("roleLabel");
            roleChoice.setLabel(new ResourceModel("user_form.role"));
            spaceRoleAllocationArea.add(new SimpleFormComponentLabel("roleLabel", roleChoice));
//            
//            spaceRoleAllocationArea.add(roleLabel);
            spaceRoleAllocationArea.add(roleChoice);
            
            spaceRoleAllocationArea.setVisible(!getPrincipal().isGlobalAdmin() && getPrincipal().isSpaceAdmin() && user.getId()==0);
            add(spaceRoleAllocationArea);
            
            // hide e-mail message if edit =====================================
            WebMarkupContainer hide = new WebMarkupContainer("hide");
            if(user.getId() > 0) {
                hide.setVisible(false);
            }
            add(hide);
            // password ========================================================
            final PasswordTextField passwordField = new PasswordTextField("password");            
            add(passwordField);
            passwordField.setRequired(false).setEnabled(!isDemoUser);
            
            //form label
            passwordField.setLabel(new ResourceModel("user_form.password"));
            add(new SimpleFormComponentLabel("passwordLabel", passwordField));
            
            // confirm password ================================================
            final PasswordTextField confirmPasswordField = new PasswordTextField("passwordConfirm");
            confirmPasswordField.setRequired(false);
            confirmPasswordField.add(new ErrorHighlighter());
            add(confirmPasswordField.setEnabled(!isDemoUser));
            // validation, do the passwords match
            add(new AbstractFormValidator() {
                public FormComponent[] getDependentFormComponents() {
                    return new FormComponent[] {passwordField, confirmPasswordField};
                }
                public void validate(Form form) {
                    String a = (String) passwordField.getConvertedInput();
                    String b = (String) confirmPasswordField.getConvertedInput();
                    if((a != null && !a.equals(b)) || (b!= null && !b.equals(a))) {
                        confirmPasswordField.error(localize("user_form.passwordConfirm.error"));
                    }                    
                }
            });
            
            //form label
            confirmPasswordField.setLabel(new ResourceModel("user_form.confirmPassword"));
            add(new SimpleFormComponentLabel("passwordConfirmLabel", confirmPasswordField));
            
            // send notifications ==============================================
            WebMarkupContainer hideSendNotifications = new WebMarkupContainer("hideSendNotifications");
            add(hideSendNotifications);
            if(getPrincipal().getId() != user.getId()) {
            	CheckBox sendNotificationsCheckBox = new CheckBox("sendNotifications");
                hideSendNotifications.add(sendNotificationsCheckBox);
                
                //form label
                sendNotificationsCheckBox.setLabel(new ResourceModel("user_form.mailPassword"));
                hideSendNotifications.add(new SimpleFormComponentLabel("sendNotificationsLabel", sendNotificationsCheckBox));
            } else {
                hideSendNotifications.setVisible(false);
            }
            
            
        }
     /*
        @Override
        protected void validate() {
            filter.reset();
            super.validate();          
        }        
       */ 
        @Override
        protected void onSubmit() {
        	if (isDemoUser){
        		return;
        	}
      	
			// system stuff
			// add creation date etc if new
			Date now = new Date();
        	if(user.getDateCreated() == null){
        		user.setDateCreated(now);
        		user.setCreatedBy(getPrincipal());
			}
			// add update info
        	user.setDateLastUpdated(now);
        	user.setLastUpdatedBy(getPrincipal());
			
            if(password != null) {
                getCalipso().storeUser(user, password, sendNotifications);
            } else if(user.getId() == 0) {
                // new user, generate password and send mail
                getCalipso().storeUser(user, null, true);
            } else {
                getCalipso().storeUser(user);
                
            }
            if (getPrincipal().isSpaceAdmin()){
            	if (this.getSpace()!=null && this.getRoleKey()!=null){
            		//TODO Implement Later-er
//            		getJtrac().storeUserSpaceRole(user, space, roleKey);
            	}
            }
            refreshPrincipal(user);
            
            iconFormPanel.onSubmit();
            
            //if editing profile from the header username link
            if(getBreadCrumbModel().allBreadCrumbParticipants().size() == 1){
                setResponsePage(DashboardPage.class);
                return;
            }
            
            activate(new IBreadCrumbPanelFactory(){
				public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
            		// get previous page
					BreadCrumbPanel previous = (BreadCrumbPanel) BreadCrumbUtils.backBreadCrumbPanel(getBreadCrumbModel());

					if(previous instanceof SpaceAllocatePanel){
                        // TODO refactor this better, but for now this is so that the newly created user 
                        // appears in the drop down for allocation and is preselected
						
						List<IBreadCrumbParticipant> participants = getBreadCrumbModel().allBreadCrumbParticipants();
                    	
                    	long spaceId = ((SpaceAllocatePanel)previous).getSpaceId();
                    	
            			BreadCrumbPanel startPanel = (BreadCrumbPanel) participants.get(participants.size() - 3);
            			getBreadCrumbModel().setActive(startPanel);
            			
            			previous = new SpaceAllocatePanel(componentId, breadCrumbModel, spaceId, user.getId());
                    }
                    					
					return previous;
				}
            		
            });
        }  
        
        public User getUser() {
            return user;
        }
        
        public void setUser(User user) {
            this.user = user;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }        
        
        public String getPasswordConfirm() {
            return passwordConfirm;
        }
        
        public void setPasswordConfirm(String passwordConfirm) {
            this.passwordConfirm = passwordConfirm;
        }

        public boolean isSendNotifications() {
            return sendNotifications;
        }
        
        public void setSendNotifications(boolean sendNotifications) {
            this.sendNotifications = sendNotifications;
        }         
        
		public void setOrganization(Organization organization) {
			this.organization = organization;
		}

		public Organization getOrganization() {
			return organization;
		}

		public String getRoleKey() {
			return roleKey;
		}

		public void setRoleKey(String roleKey) {
			this.roleKey = roleKey;
		}

		
		public Space getSpace() {
			return space;
		}

		public void setSpace(Space space) {
			this.space = space;
		}

		private void initRoleChoice(Space s) {
			List<String> roleKeys = new ArrayList(s.getMetadata().getRolesMap().keySet());
			roleKeys.add("ROLE_SPACEADMIN");
			//TODO Implement later
//			roleKeys.removeAll(user.getRoleKeys(s));
			roleKeys.remove("ROLE_GUEST");

			if (roleKeys.size() == 1) {
				// pre select role for convenience
				roleKey = roleKeys.get(0);
			}
			roleChoice.setChoices(roleKeys);
			roleChoice.setEnabled(true);
		}
    }        

    //////////////////////////////////////////////////////////////////////////////////////
	private void deleteLink(final User user){
		//hide link if: admin, user==null, try to delete self or new user
		if( !getPrincipal().isGlobalAdmin() || user == null || user.getId() == getPrincipal().getId() || user.getId() <= 1){
			add(new WebMarkupContainer("delete").setVisible(false));
		}
		else{//if edit
	    	add(new Link("delete") {		
				@Override
				public void onClick() {
					
	                int count = getCalipso().loadCountOfHistoryInvolvingUser(user);
	                if(count > 0) {
	                	UserFormPanel.this.error(localize("user_delete.notPossible"));
	                    return;
	                }
	
	                final String heading = localize("user_delete.confirm");                    
	                final String line1 = localize("user_delete.line1");
	                final String warning = localize("user_delete.line2");
	            	
	            	activate(new IBreadCrumbPanelFactory(){
	                    public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel){
	                    	ConfirmPanel confirm = new ConfirmPanel(componentId, breadCrumbModel, heading, warning, new String[] {line1}) {
	                            public void onConfirm() {
	                                getCalipso().removeUser(user);
	                                // logged in user may have been allocated to space with this user assigned
	                                UserFormPanel.this.refreshPrincipal();
	                                
	                                BreadCrumbUtils.removePreviousBreadCrumbPanel(getBreadCrumbModel());
	                                
	                                activate(new IBreadCrumbPanelFactory(){
										public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
											return (BreadCrumbPanel) breadCrumbModel.getActive();
										}                                    	
	                                });
	                            }                        
	                        };
	                        return confirm;
	                    }
	                });
				}//onclick
			});//add, new Link
		}
	}
}