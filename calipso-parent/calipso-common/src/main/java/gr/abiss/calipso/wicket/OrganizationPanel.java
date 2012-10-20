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

import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.OrganizationSearch;
import gr.abiss.calipso.wicket.hlpcls.ExpandAssetSearchLink;

import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * @author marcello
 */
public class OrganizationPanel extends BasePanel {

	private OrganizationListPanel organizationListPanel;
	
	private OrganizationSearch organizationSearch;
	private Organization organization;

	private CompoundPropertyModel organizationModel;
	private SearchOrganizationForm searchForm = null;
	private WebMarkupContainer searchContainer;
	private WebMarkupContainer searchPlaceHolder;
	private boolean isSearchOpen;
	
	// --------------------------------------------------------------------------------------------
	
    public OrganizationPanel setSelectedOrganizationId(long organizationId) {
    	organizationListPanel.setSelectedOrganizationId(organizationId);
        return this;
    }
	
	// --------------------------------------------------------------------------------------------
	
	public OrganizationPanel(String id, IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);
		
		organization = new Organization();
		organizationSearch = new OrganizationSearch(organization, this);
		
		addComponents();
	}

	// --------------------------------------------------------------------------------------------

	public OrganizationPanel(String id, IBreadCrumbModel breadCrumbModel, OrganizationSearch organizationSearch) {
		super(id, breadCrumbModel);

		this.organizationSearch = organizationSearch;
		this.organization = organizationSearch.getSearchObject(); 
		addComponents();
	}

	// --------------------------------------------------------------------------------------------
	
	public OrganizationPanel(String id, IBreadCrumbModel breadCrumbModel, Organization organization){
		super(id, breadCrumbModel);
		this.organization = organization;
		this.organizationSearch = new OrganizationSearch(this.organization, this);

		this.isSearchOpen = true;

		addComponents();
	}//OrganizationPanel
	
	// --------------------------------------------------------------------------------------------

	@Override
	public String getTitle() {
		return localize("organization.header");
	}

	// --------------------------------------------------------------------------------------------

	private void addComponents(){
		createOrganization();
		searchOrganization();
		organizationListPanel();
	}

	// --------------------------------------------------------------------------------------------

	private void createOrganization(){
		add(new Link ("new"){
			@Override
			public void onClick() {
				activate(new IBreadCrumbPanelFactory(){
					public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
						return new OrganizationFormPanel(getBreadCrumbModel().getActive().getComponent().getId(), getBreadCrumbModel());
					}
				});
			}
		});
	}
	
	// --------------------------------------------------------------------------------------------
	
	private void searchOrganization(){
		//TODO Implement search organization action
//		add(new WebMarkupContainer("search"));
		
		organizationModel = new CompoundPropertyModel(this.organization);

		// --- container -------------------------------------------
		searchContainer = new WebMarkupContainer("searchContainer");
		add(searchContainer);

		// -- place holder ----------------------------------------
		searchPlaceHolder = new WebMarkupContainer("searchPlaceHolder");
		searchContainer.add(searchPlaceHolder);
		
		// -- Page is (re)loaded, and the search panel is open ----
		if (isSearchOpen){
			searchForm = new SearchOrganizationForm("form", organizationModel);
			searchContainer.add(searchPlaceHolder);
			searchPlaceHolder.add(searchForm);
		}
		// -- Page is (re)loaded, and the search panel is closed ----		
		else{
			//Set place holder to not visible
			searchPlaceHolder.setVisible(false);
		}
		
		//setOutputMarkupId, needed for ajax
		searchContainer.setOutputMarkupId(true);
		
		ExpandAssetSearchLink search = new ExpandAssetSearchLink("search",
				searchContainer, searchContainer, searchPlaceHolder, new SearchOrganizationForm("form", organizationModel), isSearchOpen){
			@Override
			public void onLinkClick() {
				OrganizationPanel.this.isSearchOpen = this.isOpen();
			}
		};
		
		add(search);		
		
	}
	
	// --------------------------------------------------------------------------------------------
	
	private void organizationListPanel(){
		organizationListPanel = new OrganizationListPanel("organizationListPanel", getBreadCrumbModel(), this.organizationSearch);
		add(organizationListPanel);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////

	private class SearchOrganizationForm extends Form{

		public SearchOrganizationForm(String id, CompoundPropertyModel model) {
			super(id);
			setModel(model);
			
			//Name --------------------------------------------------------------------------------
			//Field
			TextField name = new TextField("name");
			add(name);

			//Label
			name.setLabel(new ResourceModel("organization.name"));
			add(new SimpleFormComponentLabel("nameLabel", name));

			//Address -----------------------------------------------------------------------------
			//Field
			TextField address = new TextField("address");
			add(address);
			
			//Label
			address.setLabel(new ResourceModel("organization.address"));
			add(new SimpleFormComponentLabel("addressLabel", address));
			
			//Postal Code -------------------------------------------------------------------------
			//Field
			TextField zip = new TextField("zip");
			add(zip);

			//Label
			zip.setLabel(new ResourceModel("organization.zip"));
			add(new SimpleFormComponentLabel("zipLabel", zip));
			
			//Phone -------------------------------------------------------------------------------
			//Field
			TextField phone = new TextField("phone");
			add(phone);
			
			//Label
			phone.setLabel(new ResourceModel("organization.phone"));
			add(new SimpleFormComponentLabel("phoneLabel", phone));
			
			//Web ---------------------------------------------------------------------------------
			//Field
			TextField web = new TextField("web");
			add(web);

			//Label
			web.setLabel(new ResourceModel("organization.web"));
			add(new SimpleFormComponentLabel("webLabel", web));

		}//SearchAssetTypeForm
		
		//------------------------------------------------------------------------------------------------
		
		protected void onSubmit() {
			activate(new IBreadCrumbPanelFactory(){
				public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {

					//Remove last breadcrumb participant
					if (breadCrumbModel.allBreadCrumbParticipants().size()>0){
						breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
					}//if

					return new OrganizationPanel(id, breadCrumbModel, OrganizationPanel.this.organization);
				}
			});
			
		}//onSubmit
	}//SearchAssetTypeForm 	
	
}

