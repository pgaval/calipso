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


package gr.abiss.calipso.wicket.regexp;

import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.OrganizationSearch;
import gr.abiss.calipso.domain.ValidationExpression;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.OrganizationFormPanel;
import gr.abiss.calipso.wicket.OrganizationListPanel;
import gr.abiss.calipso.wicket.OrganizationPanel;
import gr.abiss.calipso.wicket.hlpcls.ExpandAssetSearchLink;

import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;

/**
 * 
 *
 */
public class ValidationExpressionPanel extends BasePanel {

	private static final long serialVersionUID = 1L;

	private ValidationExpressionListPanel validationExpressionListPanel;
	
	private ValidationExpressionSearch validationExpressionSearch;
	private ValidationExpression validationExpression;

	private CompoundPropertyModel validationExpressionModel;
	private SearchValidationExpressionForm searchForm = null;
	private WebMarkupContainer searchContainer;
	private WebMarkupContainer searchPlaceHolder;
	private boolean isSearchOpen;
	
    public ValidationExpressionPanel setSelectedValidationExpressionId(long validationExpressionId) {
    	validationExpressionListPanel.setSelectedValidationExpressionId(validationExpressionId);
        return this;
    }
    
	public ValidationExpressionPanel(String id, IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);
		
		validationExpression = new ValidationExpression();
		validationExpressionSearch = new ValidationExpressionSearch(validationExpression, this);
		
		addComponents();
	}
	
	public ValidationExpressionPanel(String id, IBreadCrumbModel breadCrumbModel, ValidationExpressionSearch validationExpressionSearch) {
		super(id, breadCrumbModel);

		this.validationExpressionSearch = validationExpressionSearch;
		this.validationExpression = (ValidationExpression) validationExpressionSearch.getSearchObject(); 
		addComponents();
	}
	
	public ValidationExpressionPanel(String id, IBreadCrumbModel breadCrumbModel, ValidationExpression validationExpression){
		super(id, breadCrumbModel);
		this.validationExpression = validationExpression;
		this.validationExpressionSearch = new ValidationExpressionSearch(this.validationExpression, this);

		this.isSearchOpen = true;

		addComponents();
	}
	
	@Override
	public String getTitle() {
		return localize("validation_Expression.header");
	}
	
	private void addComponents(){
		createValidationExpression();
		searchValidationExpression();
		createValidationExpressionListPanel();
	}
	
	private void createValidationExpression(){
		add(new Link ("new"){
			@Override
			public void onClick() {
				activate(new IBreadCrumbPanelFactory(){
					public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
						return new ValidationExpressionFormPanel(getBreadCrumbModel().getActive().getComponent().getId(), getBreadCrumbModel());
					}
				});
			}
		});
	}
	
	private void searchValidationExpression(){
		//TODO Implement search organization action
//		add(new WebMarkupContainer("search"));
		
		validationExpressionModel = new CompoundPropertyModel(this.validationExpression);

		// --- container -------------------------------------------
		searchContainer = new WebMarkupContainer("searchContainer");
		add(searchContainer);

		// -- place holder ----------------------------------------
		searchPlaceHolder = new WebMarkupContainer("searchPlaceHolder");
		searchContainer.add(searchPlaceHolder);
		
		// -- Page is (re)loaded, and the search panel is open ----
		if (isSearchOpen){
			searchForm = new SearchValidationExpressionForm("form", validationExpressionModel);
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
				searchContainer, searchContainer, searchPlaceHolder, new SearchValidationExpressionForm("form", validationExpressionModel), isSearchOpen){
			@Override
			public void onLinkClick() {
				ValidationExpressionPanel.this.isSearchOpen = this.isOpen();
			}
		};
		
		add(search);	
	}
	private void createValidationExpressionListPanel(){
		validationExpressionListPanel = new ValidationExpressionListPanel("validationExpressionListPanel", getBreadCrumbModel(), this.validationExpressionSearch);
		add(validationExpressionListPanel);
	}
		
	private class SearchValidationExpressionForm extends Form{

		public SearchValidationExpressionForm(String id, CompoundPropertyModel model) {
			super(id);
			setModel(model);
			// search text Fields
				
			//Name
			TextField name = new TextField("name");
			add(name);

			//Label
			name.setLabel(new ResourceModel("validation_Expression.name"));
			add(new SimpleFormComponentLabel("nameLabel", name));

			//description 
			TextField description = new TextField("description");
			add(description);
			//Label
			description.setLabel(new ResourceModel("validation_Expression.description"));
			add(new SimpleFormComponentLabel("descriptionLabel", description));
		}
		
		protected void onSubmit() {
			activate(new IBreadCrumbPanelFactory(){
				public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {

					//Remove last breadcrumb participant
					if (breadCrumbModel.allBreadCrumbParticipants().size()>0){
						breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
					}//if

					return new ValidationExpressionPanel(id, breadCrumbModel, ValidationExpressionPanel.this.validationExpression);
				}
			});
			
		}		
		
	}
	
}

