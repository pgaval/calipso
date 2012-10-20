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

import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.wicket.asset.ItemFormAssetSearchPanel;
import gr.abiss.calipso.wicket.components.user.UserDataProvider;
import gr.abiss.calipso.wicket.components.user.UserDataView;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxNavigationToolbar;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.PropertyModel;

/**
 * @author manos
 *
 */
public abstract class SearchUserPanel extends BasePanel {
	protected static final Logger logger = Logger.getLogger(SearchUserPanel.class);
	
	private static final long serialVersionUID = 1L;
	
	private boolean searchIsVisible = false;
	private String searchOn = "loginName";
	private String searchText = "";
	private WebMarkupContainer findUserPlaceHolder;
	private WebMarkupContainer usersDataViewContainer;
	private DataView userDataView;
	private Fragment findUserFragment;
	private Space space = null;
	private DropDownChoice searchOnChoice;
	private TextField searchTextField;
	private AjaxPagingNavigator navigator;

	public abstract void onUserSelect(User user, AjaxRequestTarget target);

	
	public SearchUserPanel(String id) {
		super(id);
		addComponents();
	}
	
	public SearchUserPanel(String id, Space space) {
		this(id);
		this.space = space;
	}

	private void addComponents() {
		
		setOutputMarkupId(true);

		// Toggle View/Hide Search User possibility
		AjaxLink findUser = new AjaxLink("findUser") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				if (searchIsVisible) {
					SearchUserPanel.this.remove(findUserFragment);

					findUserPlaceHolder = new WebMarkupContainer("findUserPlaceHolder");
					findUserPlaceHolder.setOutputMarkupId(true);
					SearchUserPanel.this.add(findUserPlaceHolder);
				}// if
				else {
					SearchUserPanel.this.remove(findUserPlaceHolder);
					findUserFragment = 
						new Fragment("findUserPlaceHolder", "findUserFragment",SearchUserPanel.this);
					findUserFragment.add(new AjaxLink("close"){

						@Override
						public void onClick(AjaxRequestTarget target) {
							SearchUserPanel.this.remove(SearchUserPanel.this.findUserFragment);
		
							findUserPlaceHolder = new WebMarkupContainer("findUserPlaceHolder");
							findUserPlaceHolder.setOutputMarkupId(true);
							SearchUserPanel.this.add(SearchUserPanel.this.findUserPlaceHolder);
							searchIsVisible = !searchIsVisible;
							target.addComponent(SearchUserPanel.this);
					}
						
					});
					findUserFragment.add(renderSearchCriteria());
					findUserFragment.setOutputMarkupId(true);
					SearchUserPanel.this.add(findUserFragment);
				}// else

				SearchUserPanel.this.findUserPlaceHolder.setOutputMarkupId(true);
				SearchUserPanel.this.searchIsVisible = !SearchUserPanel.this.searchIsVisible;

				target.addComponent(SearchUserPanel.this);
			}
		};
		add(findUser);

		this.findUserPlaceHolder = new WebMarkupContainer("findUserPlaceHolder");
		this.findUserPlaceHolder.setOutputMarkupId(true);
		add(this.findUserPlaceHolder);
	}

	
	private WebMarkupContainer renderSearchCriteria() {
		final WebMarkupContainer searchFormContainer = new WebMarkupContainer("searchFormContainer");
		searchFormContainer.setOutputMarkupId(true);

		List<String> searchOnOptions = Arrays.asList(
				new String[] {"loginName", "name", "lastname", "email", "address", "phone" });
		
		searchOnChoice = new DropDownChoice("searchOn",
				new PropertyModel(SearchUserPanel.this, "searchOn"), searchOnOptions,
				new IChoiceRenderer() {
					public Object getDisplayValue(Object o) {
						String s = (String) o;
						return localize("user_list." + s);
					}
					public String getIdValue(Object o, int i) {
						return o.toString();
					}
				});
		searchOnChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				searchOn =  searchOnChoice.getDefaultModelObjectAsString();
				target.addComponent(searchFormContainer);
				
			}
		});
		searchFormContainer.add(searchOnChoice);

		
		searchTextField = new TextField("searchText",
				new PropertyModel(SearchUserPanel.this, "searchText"));
		searchTextField.setOutputMarkupId(true);
		searchTextField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
					
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				searchText = searchTextField.getDefaultModelObjectAsString();
				target.addComponent(searchFormContainer);
				
			}
		});
		searchFormContainer.add(searchTextField);
		
		searchFormContainer.add(new Behavior() {
			public void renderHead(IHeaderResponse response) {
				response.renderOnLoadJavaScript("document.getElementById('"
						+ searchTextField.getMarkupId() + "').focus()");
			}
		});
		
		AjaxLink submit = new AjaxLink("submit") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				
				if (usersDataViewContainer != null) {
					searchFormContainer.remove(usersDataViewContainer);
				}
				searchFormContainer.add(renderUserDataView());
				target.addComponent(searchFormContainer);
			}
		};
		searchFormContainer.add(submit);
		searchFormContainer.add(renderUsersDataViewContainer(false));
		return searchFormContainer;
	}


	private WebMarkupContainer renderUserDataView() {
		if (this.userDataView != null) {
			this.usersDataViewContainer.remove(this.userDataView);
			this.usersDataViewContainer.remove(this.navigator);
		}
		usersDataViewContainer.setOutputMarkupId(true);
		usersDataViewContainer.setOutputMarkupPlaceholderTag(true);
		// provides user data
		IDataProvider userDataProvider = new UserDataProvider(searchText , searchOn);
		
		this.userDataView = new UserDataView("userDataView",userDataProvider, getCalipso().getRecordsPerPage()) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void onAddingUser(User user, AjaxRequestTarget target) {
				onUserSelect(user, target);
				searchIsVisible = !searchIsVisible;
				usersDataViewContainer.getParent().getParent().setVisible(false);
				target.addComponent(usersDataViewContainer);
				// visibility changes
				
			}
		};
		this.usersDataViewContainer.add(this.userDataView);
		navigator = new AjaxPagingNavigator("navigator", userDataView){
			@Override
			protected void onAjaxEvent(AjaxRequestTarget target) {
				target.addComponent(usersDataViewContainer);
			}
		};
		this.usersDataViewContainer.add(navigator);
		return renderUsersDataViewContainer(true);
	}

	private WebMarkupContainer renderUsersDataViewContainer(boolean visible) {
		if (this.usersDataViewContainer == null) {
			this.usersDataViewContainer = new WebMarkupContainer(
					"usersDataViewContainer");
		}// if
		this.usersDataViewContainer.setVisible(visible);

		return this.usersDataViewContainer;
	}
	

	public SearchUserPanel(String id, IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);
	}


	/**
	 * @return the searchOn
	 */
	public String getSearchOn() {
		return searchOn;
	}


	/**
	 * @param searchOn the searchOn to set
	 */
	public void setSearchOn(String searchOn) {
		this.searchOn = searchOn;
	}


	/**
	 * @return the searchText
	 */
	public String getSearchText() {
		return searchText;
	}


	/**
	 * @param searchText the searchText to set
	 */
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

}