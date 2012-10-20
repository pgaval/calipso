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

import java.util.Date;
import java.util.List;
import gr.abiss.calipso.domain.Country;
import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.wicket.components.validators.PhoneNumberValidator;

import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;

import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.UrlValidator;


/**
 * @author marcello
 */
public class OrganizationFormPanel extends BasePanel {
	private static final long serialVersionUID = 1L;

	private Organization organization;
	private boolean isEdit;

	// --------------------------------------------------------------------------------------------

	@Override
	public String getTitle() {
		if (this.isEdit){
			return localize("organization.edit");
		}

		return localize("organization.create");
	}

	// --------------------------------------------------------------------------------------------
	
	public OrganizationFormPanel(String id, IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);
		this.isEdit = false;

		organization = new Organization();
		add(new Label("label", localize("organization.create")));
		add(new OrganizationForm("form", organization));
	}

	// --------------------------------------------------------------------------------------------
	
	public OrganizationFormPanel(String id, IBreadCrumbModel breadCrumbModel, Organization organization) {
		super(id, breadCrumbModel);
		this.isEdit = true;

		this.organization = organization;
		add(new Label("label", localize("organization.edit")));
		add(new OrganizationForm("form", organization));
	}

	// --------------------------------------------------------------------------------------------

	private class OrganizationForm extends Form{
		private static final long serialVersionUID = 1L;

		private Organization organization;
		private CalipsoFeedbackMessageFilter filter;
		
		private IconFormPanel iconFormPanel;
		
		public OrganizationForm(String id, Organization organization) {
			super(id);
			this.organization = organization;
			CompoundPropertyModel model = new CompoundPropertyModel(this.organization);
			setModel(model);
			/*
	private Country country;
			 */
			// Feedback ---------------------------------------------------------------------------
            FeedbackPanel feedback = new FeedbackPanel("feedback");

            filter = new CalipsoFeedbackMessageFilter();
            feedback.setFilter(filter);
            add(feedback);
            
            // icon form panel======================================================
            iconFormPanel = new IconFormPanel("iconFormPanel", organization); 
            add(iconFormPanel);
			
			//Name --------------------------------------------------------------------------------
			//Set mandatory
			add(new MandatoryPanel("mandatoryPanelForName"));
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
			vatNumber.add(new ErrorHighlighter());
			add(vatNumber);
			
			//Label
			vatNumber.setLabel(new ResourceModel("organization.vatNumber"));
			add(new SimpleFormComponentLabel("vatNumberLabel", vatNumber));//

			
			//Address -----------------------------------------------------------------------------//
			//Field
			TextField address = new TextField("address");
			address.add(new ErrorHighlighter());
			add(address);
			
			//Label
			address.setLabel(new ResourceModel("organization.address"));
			add(new SimpleFormComponentLabel("addressLabel", address));//
			
			//Postal Code -------------------------------------------------------------------------//
			//Field TODO: 
			TextField zip = new TextField("zip");
			zip.add(new ErrorHighlighter());
			add(zip);
			
			//Label
			zip.setLabel(new ResourceModel("organization.zip"));
			add(new SimpleFormComponentLabel("zipLabel", zip));//
			// country
			// get all countries
			final List<Country> allCountriesList  = getCalipso().findAllCountries();
			DropDownChoice countryChoice =  getCountriesDropDown("country", allCountriesList);

			add(new MandatoryPanel("mandatoryPanelForCountry"));
			countryChoice.setRequired(true);
			countryChoice.add(new ErrorHighlighter());
			countryChoice.setLabel(new ResourceModel("organization.country"));
			add(countryChoice);
			// Label
			add(new SimpleFormComponentLabel("countryLabel", countryChoice));
			
			
			//Phone -------------------------------------------------------------------------------//
			//Field
			TextField phone = new TextField("phone");
			// phone validation for organization phone number
			phone.add(new PhoneNumberValidator());
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
			email.add(new ErrorHighlighter());
			add(new SimpleFormComponentLabel("emailLabel", email));

			
			//lastUpdateComment ---------------------------------------------------------------------------------
			//Field
			TextArea lastUpdateComment = new TextArea("lastUpdateComment");
			lastUpdateComment.add(new ErrorHighlighter());
			if(!isEdit){
				lastUpdateComment.setVisible(false);
			}
			else{
				lastUpdateComment.setRequired(true);
			}
			add(lastUpdateComment);

			//Label
			lastUpdateComment.setLabel(new ResourceModel("organization.lastUpdateComment"));
			add(new SimpleFormComponentLabel("lastUpdateCommentLabel", lastUpdateComment));
		}

		// ----------------------------------------------------------------------------------------

		@Override
		protected void onSubmit() {
			final String defaultValue = new String("");

			if (this.organization.getAddress()==null){
				this.organization.setAddress(defaultValue);
			}

			if (this.organization.getZip()==null){
				this.organization.setZip(defaultValue);
			}

			if (this.organization.getPhone()==null){
				this.organization.setPhone(defaultValue);
			}

			if (this.organization.getWeb()==null){
				this.organization.setWeb(defaultValue);
			}

			// system stuff
			// add creation date etc if new
			Date now = new Date();
			if(organization.getDateCreated() == null){
				organization.setDateCreated(now);
				organization.setCreatedBy(getPrincipal());
			}
			// add update info
			organization.setDateLastUpdated(now);
			organization.setLastUpdatedBy(getPrincipal());
			
			getCalipso().storeOrganization(this.organization);
			
			iconFormPanel.onSubmit();

			if (isEdit){
				activate(new IBreadCrumbPanelFactory(){
					public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
						BreadCrumbUtils.popPanels(2, breadCrumbModel);
						OrganizationPanel organizationPanel = new OrganizationPanel(id, breadCrumbModel);
						organizationPanel.setSelectedOrganizationId(organization.getId());
						return organizationPanel;
					}
				});
			}
			else{
				BreadCrumbUtils.removeActiveBreadCrumbPanel(getBreadCrumbModel());
				activate(new IBreadCrumbPanelFactory(){
					public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
						return new OrganizationPanel(id, breadCrumbModel);
					}
				});
			}
		}
	}
}