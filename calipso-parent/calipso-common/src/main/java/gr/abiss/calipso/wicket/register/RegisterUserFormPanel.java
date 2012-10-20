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

package gr.abiss.calipso.wicket.register;

import gr.abiss.calipso.domain.Country;
import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.OrganizationSearch;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.ErrorHighlighter;
import gr.abiss.calipso.wicket.IconFormPanel;
import gr.abiss.calipso.wicket.CalipsoFeedbackMessageFilter;
import gr.abiss.calipso.wicket.LoginPage;
import gr.abiss.calipso.wicket.MandatoryPanel;
import gr.abiss.calipso.wicket.UserListPanel;
import gr.abiss.calipso.wicket.components.validators.DomainMatchingEmailAddressValidator;
import gr.abiss.calipso.wicket.components.validators.PhoneNumberValidator;
import gr.abiss.calipso.wicket.components.validators.ValidationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
//import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.UrlValidator;
import org.apache.log4j.Logger;

/**
 *
 */
public class RegisterUserFormPanel extends BasePanel {
	protected static final Logger logger = Logger.getLogger(RegisterUserFormPanel.class);
    
    public String getTitle(){
        return localize("user_form.userDetails");
    }
    
    public RegisterUserFormPanel(String id, IBreadCrumbModel breadCrumbModel) {  
    	super(id, breadCrumbModel);
    	
        User user = new User();
        user.setLocale(getCalipso().getDefaultLocale());
        
        add(new UserForm("form", user));
        
        //make a cancel link
        //getBackLinkPanel().makeCancel();
        
        //highlight this user on previous page
        //setHighlightOnPreviousPage(user.getId());
    }    

    
    
    /**
     * wicket form
     */    
    //TODO: Organization Form for adding new organization
    private class UserForm extends Form {
        
        private User user;
        private String password;
        private String passwordConfirm;
        private WebMarkupContainer hide;
        private boolean showPasswordFields;
        private DropDownChoice spaceChoice;
        private DropDownChoice roleChoice;
        private TextField regUserMail;
        private String roleKey;
        private Space space=null;
        private Organization organization;
        private DropDownChoice organizationChoice;        
        private IconFormPanel iconFormPanel;
        private Fragment formFragment;
        private EmptyPanel formFragmentPanel;
        private DomainMatchingEmailAddressValidator domainMatchingEmailAddressValidator;
        private final boolean forceSendPassword = BooleanUtils.toBoolean(getCalipso().loadConfig("mail.forceVerification"));
        private CalipsoFeedbackMessageFilter filter;
        
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
        /*
        public boolean isSendNotifications() {
            return sendNotifications;
        }
        
        public void setSendNotifications(boolean sendNotifications) {
            this.sendNotifications = sendNotifications;
        }         
        */
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
		
        public UserForm(String id, final User user) {
            
            super(id);
            this.user = user;
            // sendNotification depends on forceSendPassword
            showPasswordFields = !forceSendPassword;
            
            FeedbackPanel feedback = new FeedbackPanel("feedback");
            filter = new CalipsoFeedbackMessageFilter();
            feedback.setFilter(filter);
            add(feedback);             
            
            final CompoundPropertyModel model = new CompoundPropertyModel(this);
            setModel(model);    
        //	Container that holds the organization form for organization creation
        //	add an empty Panel to the page
            // TODO:
            formFragment = new Fragment("organizationContainer","emptyFragment", RegisterUserFormPanel.this);
            formFragmentPanel = new EmptyPanel("organizationContainer"); 
            formFragment.add(formFragmentPanel);
            add(formFragment);//TODO:
            
            // icon form panel======================================================
            iconFormPanel = new IconFormPanel("iconFormPanel", user); 
            add(iconFormPanel);
            
            
            // login name ======================================================
            final TextField loginName = new TextField("user.loginName");
            loginName.setRequired(true);
            loginName.add(new ErrorHighlighter());
            loginName.setOutputMarkupId(true);           
            add(new Behavior() {
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
            loginName.setLabel(new ResourceModel("user_form.loginId"));
            add(new SimpleFormComponentLabel("loginNameLabel",loginName));
            
            // name ============================================================
            TextField name=new TextField("user.name");
            name.setRequired(true).add(new ErrorHighlighter());
            add(name);
            
        	//form label
            name.setLabel(new ResourceModel("user_form.name"));
        	add(new SimpleFormComponentLabel("nameLabel", name));

            
            // lastname ============================================================
            TextField lastname=new TextField("user.lastname");
            lastname.setRequired(true).add(new ErrorHighlighter());
            add(lastname);
            
        	//form label
            lastname.setLabel(new ResourceModel("user_form.lastname"));
        	add(new SimpleFormComponentLabel("lastnameLabel", lastname));

        	
            // email ===========================================================
        	regUserMail=new TextField("user.email");
        	// TODO: validation that checks 
        	regUserMail.setRequired(true).add(new ErrorHighlighter());
        	// add email to validate if is email
        	regUserMail.add(EmailAddressValidator.getInstance());
        	add(regUserMail);
        	//form label
        	regUserMail.setLabel(new ResourceModel("user_form.email"));
        	add(new SimpleFormComponentLabel("emailLabel", regUserMail));
        	
        	
           	
        	//Address -----------------------------------------------------------------------------//
			//Field
			TextField address = new TextField("user.address");
			address.setRequired(true);
			address.add(new ErrorHighlighter());
			add(address);
			
			//Label
			address.setLabel(new ResourceModel("user_form.address"));
			add(new SimpleFormComponentLabel("addressLabel", address));//
			
			//Postal Code -------------------------------------------------------------------------//
			//Field TODO: 
			TextField zip = new TextField("user.zip");
			zip.setRequired(true);
			zip.add(new ErrorHighlighter());
			add(zip);
			
			//Label
			zip.setLabel(new ResourceModel("user_form.zip"));
			add(new SimpleFormComponentLabel("zipLabel", zip));//
			// country
			// get all countries
			final List<Country> allCountriesList  = getCalipso().findAllCountries();
			DropDownChoice countryChoice = getCountriesDropDown("user.country", allCountriesList);
			countryChoice.setRequired(true);
			countryChoice.setLabel(new ResourceModel("user_form.country"));
			add(countryChoice);
			// Label
			add(new SimpleFormComponentLabel("countryLabel", countryChoice));
			
			
			//Phone -------------------------------------------------------------------------------//
			//Field
			TextField phone = new TextField("user.phone");
			// phone validation for organization phone number
			phone.add(new PhoneNumberValidator());
			phone.setRequired(true);
			phone.add(new ErrorHighlighter());
			add(phone);
			
			//Label
			phone.setLabel(new ResourceModel("user_form.phone"));
			add(new SimpleFormComponentLabel("phoneLabel", phone));

			//alternativePhone -------------------------------------------------------------------------------//
			//Field
			TextField alternativePhone = new TextField("user.alternativePhone");
			// phone validation for organization phone number
			alternativePhone.add(new PhoneNumberValidator());
			add(alternativePhone);
			
			//Label
			alternativePhone.setLabel(new ResourceModel("user_form.alternativePhone"));
			add(new SimpleFormComponentLabel("alternativePhoneLabel", alternativePhone));

			//fax -------------------------------------------------------------------------------//
			//Field
			TextField fax = new TextField("user.fax");
			// phone validation for organization phone number
			fax.add(new PhoneNumberValidator());
			add(fax);
			
			//Label
			fax.setLabel(new ResourceModel("user_form.fax"));
			add(new SimpleFormComponentLabel("faxLabel", fax));
        	
            
        	
        	
        	// organization ====================================================
        	WebMarkupContainer organizationArea = new WebMarkupContainer("organizationArea");
        	boolean organizationIsVisible = true;
        	organizationArea.setVisible(organizationIsVisible);
        	add(organizationArea);

        	if (organizationIsVisible){
        		//Field
        		List<Organization> organizationList = getCalipso().findOrganizationsMatching(new OrganizationSearch(new Organization(), this));
        		
        		// add "other" for creating your own organization
        		Organization otherOrg = new Organization();
        		// TODO: i18n "Other"
        		//otherOrg.setName("Other");
        		organizationList.add(otherOrg);
        		
        		
        		organizationChoice = new DropDownChoice("user.organization", organizationList, new IChoiceRenderer(){
        			public Object getDisplayValue(Object object) {
        				if(((Organization)object).getId()==0){
        					
        					return localize(("organization.other"));
        				}
        				return ((Organization)object).getName();
        			}

        			public String getIdValue(Object object, int index) {
        				return String.valueOf(((Organization)object).getId());
        			}
        		}){
        			/* (non-Javadoc)
        			 * @see org.apache.wicket.markup.html.form.DropDownChoice#wantOnSelectionChangedNotifications()
        			 */
        			@Override
        			protected boolean wantOnSelectionChangedNotifications() {
        				return true;
        			}
        			/* (non-Javadoc)
        			 * @see org.apache.wicket.markup.html.form.DropDownChoice#onSelectionChanged(java.lang.Object)
        			 */
        			@Override//TODO
        			protected void onSelectionChanged(Object newSelection) {
        				Organization tmpOrg = (Organization)newSelection;
        				// by default don't send password by email
        				// if first accessed or null choice is selected
        				if(tmpOrg == null || getDefaultModelObjectAsString().equals("")){
        					logger.info("Is NULL organization");
    						// clear organization domain <-> email domain validator
       						resetDomainMatchingValidator(new ArrayList<String>(0));
       						Fragment newEmptyFragment = new Fragment("organizationContainer","emptyFragment", RegisterUserFormPanel.this);
       						newEmptyFragment.add(new EmptyPanel("organizationContainer"));
       						UserForm.this.addOrReplace(formFragment);
       						showPasswordFields = !forceSendPassword;       						
        				}
        				// if not null and selected was "Other"
        				else if(tmpOrg.getId()==0){	// create new organization so it depends on forceSendPassword value to hide-send notification
        					// Model
        					logger.info("Is new organization");
       						showPasswordFields = false;
        					CompoundPropertyModel orgModel = new CompoundPropertyModel((Organization)newSelection);
    						// Create organization fragment and organization Container
        					Fragment organizationFragment = new Fragment("organizationContainer","organizationFragment", RegisterUserFormPanel.this);
        					organizationFragment.setRenderBodyOnly(true);
    						OrganizationContainer orgContainer = new OrganizationContainer("organizationContainer", orgModel);
    						// add container to fragment
    						organizationFragment.add(orgContainer);
    						// Replace existing empty fragment and empty panel
    						UserForm.this.addOrReplace(formFragment);
    						formFragment.replaceWith(organizationFragment);
    						// clear organization domain <-> email domain validator
       						resetDomainMatchingValidator(new ArrayList<String>(0));
        				}
        				else{// if predefined organization is chosen so hide/send notification
        					showPasswordFields = false;	
        					logger.info("Is predefined organization");
        					// TODO: skip sending empty if not needed
        					// 'Other' is not selected
       						Fragment newEmptyFragment = new Fragment("organizationContainer","emptyFragment", RegisterUserFormPanel.this);
       						newEmptyFragment.add(new EmptyPanel("organizationContainer"));
       						UserForm.this.addOrReplace(formFragment);
       						formFragment.replaceWith(newEmptyFragment);
       						// add domain match email address validator only when user selects
       						// predefined organizations
       						String url = tmpOrg.getWeb();
       						logger.info("|||  WEB : "+url);
       						List<String> urls = new ArrayList<String>(1);
       						urls.add(url);
    						// reset organization domain <-> email domain validator
       						resetDomainMatchingValidator(urls);
        				}
        				UserForm.this.hide.setVisible(showPasswordFields);
        				setModelObject(newSelection);
        				
        			}
        		};
        		// TODO:
        		organizationArea.add(organizationChoice);
        		organizationChoice.setNullValid(true);
        		//Label
        		organizationChoice.setLabel(new ResourceModel("user_form.organization"));
        		organizationArea.add(new SimpleFormComponentLabel("organizationLabel", organizationChoice));
        	}//if

        	
        	
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
            
            hide = new WebMarkupContainer("hide");
            hide.setVisible(showPasswordFields);
            add(hide);
            // password ========================================================
            final PasswordTextField passwordField = new PasswordTextField("password");            
            hide.add(passwordField);
            passwordField.setRequired(false);
            
            //form label
            passwordField.setLabel(new ResourceModel("user_form.password"));
            hide.add(new SimpleFormComponentLabel("passwordLabel", passwordField));
            
            // confirm password ================================================
            final PasswordTextField confirmPasswordField = new PasswordTextField("passwordConfirm");
            confirmPasswordField.setRequired(false);
            confirmPasswordField.add(new ErrorHighlighter());
            hide.add(confirmPasswordField);
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
            hide.add(new SimpleFormComponentLabel("passwordConfirmLabel", confirmPasswordField));
            
            /*
            // send notifications by email ==============================================
            // this component should be removed
            WebMarkupContainer hideSendNotifications = new WebMarkupContainer("hideSendNotifications");
            hideSendNotifications.setVisible(false);
            add(hideSendNotifications);
            
            CheckBox sendNotificationsCheckBox = new CheckBox("sendNotifications");
            sendNotificationsCheckBox.setVisible(false);
            hideSendNotifications.add(sendNotificationsCheckBox);
            
                
            //form label
            sendNotificationsCheckBox.setLabel(new ResourceModel("user_form.mailPassword"));
            hideSendNotifications.add(new SimpleFormComponentLabel("sendNotificationsLabel", sendNotificationsCheckBox));
            */
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
        	logger.info("|||||  EMAIL MODEL : " + regUserMail.getDefaultModelObjectAsString());
        	if(password != null) {
                getCalipso().storeUser(user, password, showPasswordFields);
            } else{
                // new user, generate password and send mail
                getCalipso().storeUser(user, null, true);
            } 
            refreshPrincipal(user);
            
            iconFormPanel.onSubmit();
            logger.info("|||||  EMAIL MODEL : " + regUserMail.getDefaultModelObjectAsString());
            getSession().info(localize("login.registeredUserMessage"));
            setResponsePage(LoginPage.class);
            
            
        } 
        
        private void resetDomainMatchingValidator(Collection<String> urls){
        	if(domainMatchingEmailAddressValidator==null){
					// validator is created for the first time and added
					domainMatchingEmailAddressValidator = new DomainMatchingEmailAddressValidator(urls);
					UserForm.this.regUserMail.add(domainMatchingEmailAddressValidator);
				}
				else{
					// validator already exists, just reset the URLs he uses
					domainMatchingEmailAddressValidator.reset(urls);
				}
        }
        
        class OrganizationContainer extends WebMarkupContainer{
        	private Organization organization;
        	public OrganizationContainer(String id, CompoundPropertyModel organizationModel){
        		super(id);
        		this.organization = organization;
                setModel(organizationModel);

                //Name --------------------------------------------------------------------------------
    			//Set mandatory
    			add(new MandatoryPanel("mandatoryPanel"));

    			//Field
    			TextField name = new TextField("name");
    			name.setRequired(true);
    			name.add(new ErrorHighlighter());
    			add(name);

    			//Label
    			name.setLabel(new ResourceModel("organization.name"));
    			add(new SimpleFormComponentLabel("nameLabel", name));//


    			//vatNumber -------------------------------------------------------------------------------
    			//Field
    			TextField vatNumber = new TextField("vatNumber");
    			// vatNumber validation for organization vatNumber number
    			// TODO
    			//vatNumber.add(new vatNumberNumberValidator());
    			vatNumber.setRequired(true);
    			vatNumber.add(new ErrorHighlighter());
    			add(vatNumber);
    			
    			//Label
    			vatNumber.setLabel(new ResourceModel("organization.vatNumber"));
    			add(new SimpleFormComponentLabel("vatNumberLabel", vatNumber));//

    			
    			//Address -----------------------------------------------------------------------------//
    			//Field
    			TextField address = new TextField("address");
    			address.setRequired(true);
    			address.add(new ErrorHighlighter());
    			add(address);
    			
    			//Label
    			address.setLabel(new ResourceModel("organization.address"));
    			add(new SimpleFormComponentLabel("addressLabel", address));//
    			
    			//Postal Code -------------------------------------------------------------------------//
    			//Field TODO: 
    			TextField zip = new TextField("zip");
    			zip.setRequired(true);
    			zip.add(new ErrorHighlighter());
    			add(zip);

    			//Label
    			zip.setLabel(new ResourceModel("organization.zip"));
    			add(new SimpleFormComponentLabel("zipLabel", zip));//
    			// country
    			// get all countries
    			final List<Country> allCountriesList  = getCalipso().findAllCountries();
    			DropDownChoice countryChoice =  getCountriesDropDown("country", allCountriesList);
    			// TODO
    			countryChoice.setRequired(true);
    			countryChoice.setLabel(new ResourceModel("organization.country"));
    			add(countryChoice);
    			// Label
    			add(new SimpleFormComponentLabel("countryLabel", countryChoice));
    			
    			
    			
    			//Phone -------------------------------------------------------------------------------//
    			//Field
    			TextField phone = new TextField("phone");
    			// phone validation for organization phone number
    			phone.add(new PhoneNumberValidator());
    			phone.setRequired(true);
    			phone.add(new ErrorHighlighter());
    			add(phone);
    			
    			//Label
    			phone.setLabel(new ResourceModel("organization.phone"));
    			add(new SimpleFormComponentLabel("phoneLabel", phone));

    			//alternativePhone -------------------------------------------------------------------------------//
    			//Field
    			TextField alternativePhone = new TextField("alternativePhone");
    			// phone validation for organization phone number
    			alternativePhone.add(new PhoneNumberValidator());
    			add(alternativePhone);
    			
    			//Label
    			alternativePhone.setLabel(new ResourceModel("organization.alternativePhone"));
    			add(new SimpleFormComponentLabel("alternativePhoneLabel", alternativePhone));

    			//fax -------------------------------------------------------------------------------//
    			//Field
    			TextField fax = new TextField("fax");
    			// phone validation for organization phone number
    			fax.add(new PhoneNumberValidator());
    			add(fax);
    			
    			//Label
    			fax.setLabel(new ResourceModel("organization.fax"));
    			add(new SimpleFormComponentLabel("faxLabel", fax));

    			//Web ---------------------------------------------------------------------------------//
    			//Field
    			TextField web = new TextField("web");
    			// url validation for organization's webpage
    			web.add(new UrlValidator());
    			add(web);

    			//Label
    			web.setLabel(new ResourceModel("organization.web"));
    			add(new SimpleFormComponentLabel("webLabel", web));

    			
    			//email ---------------------------------------------------------------------------------
    			//Field
    			TextField email = new TextField("email");
    			// url validation for organization's emailpage
    			email.add(EmailAddressValidator.getInstance());
    			add(email);

    			//Label
    			email.setLabel(new ResourceModel("organization.email"));
    			email.setRequired(true);
    			email.add(new ErrorHighlighter());
    			add(new SimpleFormComponentLabel("emailLabel", email));

                
        	}
        }
    }  
}
