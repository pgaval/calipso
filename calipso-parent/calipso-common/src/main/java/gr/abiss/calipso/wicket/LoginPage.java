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


import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.wicket.components.validators.ValidationUtils;
import gr.abiss.calipso.wicket.hlpcls.ExpandPanelSimple;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.cookies.CookieUtils;

/**
 * login page
 */
public class LoginPage extends CalipsoBasePage {              
    
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(LoginPage.class);   
    
	protected CalipsoService getCalipso() {
    	return ((CalipsoApplication) getApplication()).getCalipso();
	}

	public LoginPage() {
    	super();
    	
        setVersioned(false);
        //add(new Label("title", getLocalizer().getString("login.title", null)));
        add(new LoginForm("form"));
        add(new ForgottenPasswordForm("forgottenPasswordForm"));
        //String jtracVersion = ComponentUtils.getCalipso(this).getReleaseVersion();
        //add(new Label("version", jtracVersion));                
    }
    
    /**
     * wicket form
     */     
    private class LoginForm extends StatelessForm {                               
        
        private String loginName;
        private String password;
        private boolean rememberMe;

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }        

        public boolean isRememberMe() {
            return rememberMe;
        }

        public void setRememberMe(boolean rememberMe) {
            this.rememberMe = rememberMe;
        }         
        
        public LoginForm(String id) {            
            super(id);
            add(new WebMarkupContainer("hide") {
                @Override
                public boolean isVisible() {
                    return !LoginForm.this.hasError();
                }
            });
            add(new FeedbackPanel("feedback"));
            info(this.getLocalizer().getString("login.msg", this));
            setModel(new CompoundPropertyModel(this));
            ////Login name
            final TextField loginNameField = new TextField("loginName");
            loginNameField.setOutputMarkupId(true);
            add(loginNameField);
            //form label
            loginNameField.setLabel(new ResourceModel("login.loginName"));
            add(new SimpleFormComponentLabel("loginNameLabel",loginNameField));
            
            ////password
            final PasswordTextField passwordField = new PasswordTextField("password");
            passwordField.setRequired(false);
            passwordField.setOutputMarkupId(true);
            add(passwordField);
            //form label
            passwordField.setLabel(new ResourceModel("login.password"));
            add(new SimpleFormComponentLabel("passwordLabel",passwordField));
            
            
            // intelligently set focus on the appropriate textbox
            add(new Behavior() {
                public void renderHead(IHeaderResponse response) {
                    String markupId;
                    if(loginNameField.getConvertedInput() == null) {
                        markupId = loginNameField.getMarkupId();
                    } else {
                        markupId = passwordField.getMarkupId();
                    }                    
                    response.renderOnLoadJavaScript("document.getElementById('" + markupId + "').focus()");
                }
            });     
            
            //remember me checkbox
            CheckBox rememberMeCheckBox = new CheckBox("rememberMe");
            add(rememberMeCheckBox);
            //form label
            rememberMeCheckBox.setLabel(new ResourceModel("login.rememberMe"));
            add(new SimpleFormComponentLabel("rememberMeLabel", rememberMeCheckBox));         
        }
                
        @Override
        protected void onSubmit() {                    
            if(loginName == null || password == null) {
            	if(logger.isDebugEnabled()){
                    logger.debug("Login failed - login name or password is null");
            	}
                error(getLocalizer().getString("login.error", null));                
                return;
            }
            if(logger.isDebugEnabled()){
            	logger.debug("Trying to obtain a user via  authenticate()");
            }
            User user = ((CalipsoApplication) getApplication()).authenticate(loginName, password);

            if(logger.isDebugEnabled()){
            	logger.debug("Obtained user via  authenticate(): "+user);
            }
            if (user == null) { // login failed                
                error(getLocalizer().getString("login.error", null));                   
            } else { // login success
                // remember me cookie
                if(rememberMe) {
                    new CookieUtils().save(CalipsoApplication.REMEMBER_ME, loginName + ":" + getCalipso().encodeClearText(password));
                }
                // setup session with principal
                // TODO: where is refreshPrincipal?
                // Establish *Logged-in* Guest access to public spaces 
				/*
				 * List<Space> spaces =
				 * getJtrac().findSpacesWhereGuestAllowed(); if (spaces.size() >
				 * 0) { // init guest roles in memory only for (Space space :
				 * spaces) { user.getUserSpaceRoles().add(new
				 * UserSpaceRole(user, new SpaceRole(space,
				 * RoleType.GUEST.getDescription(), RoleType.GUEST))); }
				 * 
				 * if(logger.isDebugEnabled()){ logger.debug(spaces.size() +
				 * "public space(s) available, initialized guest with user roles("
				 * +
				 * user.getUserSpaceRoles().size()+"): "+user.getUserSpaceRoles(
				 * )); } } else{ if(logger.isDebugEnabled()){
				 * logger.debug("No public spaces where found."); } } // TODO:
				 * //
				 * user.setRoleSpaceStdFieldList(getJtrac().findSpaceFieldsForUser
				 * (user));
				 */
                ((CalipsoSession) getSession()).setUser(user);
                // proceed to bookmarkable page or default dashboard
                if (!continueToOriginalDestination()) {
                    setResponsePage(DashboardPage.class);
                } 
            }                
        }     
                        
    }         

    
    private class ForgottenPasswordForm extends StatelessForm{
    	private String emailAddress; 
    	private final TextField emailAddressField;
    	private WebMarkupContainer forgottenPasswordPlaceHolder;
    	private WebMarkupContainer successMessage;

    	private ExpandPanelSimple forgottenPassword;

    	private User user;
    	
    	public void setEmailAddress(String emailAddress) {
			this.emailAddress = emailAddress;
		}
    	
    	public String getEmailAddress() {
			return emailAddress;
		}
    	
    	public ForgottenPasswordForm(String id) {
    		super(id);
    		
    			
    		setModel(new CompoundPropertyModel(this));

    		forgottenPasswordPlaceHolder = new WebMarkupContainer("forgottenPasswordPlaceHolder");
    		add(forgottenPasswordPlaceHolder);
    		forgottenPasswordPlaceHolder.setVisible(false);
    		ForgottenPasswordForm.this.setOutputMarkupId(true);

    		forgottenPassword = new ExpandPanelSimple("forgottenPasswordLink", ForgottenPasswordForm.this, forgottenPasswordPlaceHolder){
					private static final long serialVersionUID = 1L;

				@Override
    			public void onLinkClick() {
					
    			}
    		};
    		add(forgottenPassword);
    		forgottenPassword.setVisible(getCalipso().isEmailSendingConfigured());

    		//Email Address Field
    		emailAddressField = new TextField("emailAddress");
    		forgottenPasswordPlaceHolder.add(emailAddressField);

    		//Email Address Label
    		emailAddressField.setLabel(new ResourceModel("login.emailAddress"));
    		forgottenPasswordPlaceHolder.add(new SimpleFormComponentLabel("emailAddressLabel",emailAddressField));

    		successMessage = new WebMarkupContainer("successMessage");
    		LoginPage.this.add(successMessage);
    		successMessage.setVisible(false);

    		AbstractFormValidator emailValidator = new AbstractFormValidator(){
				@Override
				public FormComponent[] getDependentFormComponents() {
					return new FormComponent[]{emailAddressField};
				}

				@Override
				public void validate(Form form) {
					if (emailAddressField==null || (emailAddressField!=null && emailAddressField.getValue().trim().length()==0)){
						emailAddressField.error(getLocalizer().getString("login.emailAddressNotEmpty", null));
						setFocus();
						return;
					}
					if (!ValidationUtils.isValidEmail(emailAddressField.getValue().trim())){
						emailAddressField.error(getLocalizer().getString("login.invalidEmailAddress", null));
						setFocus();
						return;
					}
					final List<User> users = getCalipso().findUsersMatching(emailAddressField.getValue().trim(), "email");
					if (users==null || (users!=null && users.size()==0)){
						emailAddressField.error(getLocalizer().getString("login.emailAddressNotExists", null));
						setFocus();
					}
					else{
						user = users.get(0);
					}
				}
    		};

    		add(emailValidator);
    		
            add(new Behavior() {
                public void renderHead(IHeaderResponse response) {
                    String markupId = emailAddressField.getMarkupId();
                    response.renderOnLoadJavaScript("function setFocusOnEmail(){ document.getElementById('" + markupId + "').focus(); }");
                }
            }); 
        	    		
    	}

    	private void setFocus(){
            add(new Behavior() {
                public void renderHead(IHeaderResponse response) {
                    String markupId = emailAddressField.getMarkupId();
                    response.renderOnLoadJavaScript("document.getElementById('" + markupId + "').focus()");
                }
            });
    	}

    	@Override
    	protected void onSubmit() {
    		
    			getCalipso().sendPassword(user);
    		
    		
    		forgottenPasswordPlaceHolder.setVisible(false);
    		successMessage.setVisible(true);
    	}

    }
}