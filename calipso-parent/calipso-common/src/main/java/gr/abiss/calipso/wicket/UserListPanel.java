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

import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.wicket.components.validators.RegisterUserConfirmPasswordValidator;
import gr.abiss.calipso.wicket.components.viewLinks.OrganizationViewLink;
import gr.abiss.calipso.wicket.components.viewLinks.UserViewLink;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

/**
 * user management page
 */
public class UserListPanel extends BasePanel {
    
	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(UserListPanel.class);

	private long selectedUserId;

	private String searchText = null;
	private String searchOn = "loginName";
	private User user;
	private WebMarkupContainer noDataContainer;

	public String getTitle() {
		return localize("user_list.usersAndSpaces");
	}

	public void setSelectedUserId(long selectedUserId) {
		this.selectedUserId = selectedUserId;
	}

	public UserListPanel(String id, final IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);

		boolean isNodataVisible;

		this.user = getPrincipal();

		// BreadCrumb link to create new user
		add(new BreadCrumbLink("create", breadCrumbModel) {
			
			private static final long serialVersionUID = 1L;

			protected IBreadCrumbParticipant getParticipant(String componentId) {
				return new UserFormPanel(componentId, breadCrumbModel);
			}
		});

		// add search form
		add(new SearchForm("form"));

		LoadableDetachableModel userListModel = new LoadableDetachableModel() {

			private static final long serialVersionUID = 1L;

			protected Object load() {
				if (searchText == null) { // find all uses
					// load all users
					List<User> userList = getCalipso().findAllUsers();
					// if admin for all spaces
					if (user.isGlobalAdmin()) {
						if (noDataContainer != null) {
							noDataContainer.setVisible(userList.size() == 0);
						}
						return userList;
					}

					List<User> returnList = new ArrayList<User>();
					for (User u : userList) {
						if (!u.isGlobalAdmin()) {
							List<Space> userSpaces = new ArrayList<Space>(
									u.getSpaces());
							// check in which spaces user is admin and add to
							// the list
							for (Space us : userSpaces) {
								if (user.getSpacesWhereUserIsAdmin().contains(
										us)) {
									returnList.add(u);
									break;
								}
							}
						}
					}
					if (noDataContainer != null) {
						noDataContainer.setVisible(returnList.size() == 0);
					}
					return returnList;
				} else {
					// user list of search results
					List<User> userList = getCalipso().findUsersMatching(
							searchText, searchOn);
					if (user.isGlobalAdmin()) {
						if (noDataContainer != null) {
							noDataContainer.setVisible(userList.size() == 0);
						}
						return userList;
					}

					List<User> returnList = new ArrayList<User>();
					for (User u : userList) {
						List<Space> userSpaces = new ArrayList<Space>(
								u.getSpaces());
						for (Space us : userSpaces) {
							if (!u.isGlobalAdmin()) {
								if (user.getSpacesWhereUserIsAdmin().contains(
										us)) {
									returnList.add(u);
									break;
								}
							}
						}
					}
					if (noDataContainer != null) {
						noDataContainer.setVisible(returnList.size() == 0);
					}
					return returnList;
				}
			}
		};

		final SimpleAttributeModifier sam = new SimpleAttributeModifier(
				"class", "alt");
		ListView listView = new ListView("users", userListModel) {
			protected void populateItem(ListItem listItem) {
				final User user = (User) listItem.getModelObject();
				if (selectedUserId == user.getId()) {
					listItem.add(new SimpleAttributeModifier("class",
							"selected"));
				} else if (listItem.getIndex() % 2 == 1) {
					listItem.add(sam);
				}
				listItem.add(new UserViewLink("loginName", breadCrumbModel,
						user, false, false, true, false));
				listItem.add(new Label("name", new PropertyModel(user, "name")));
				listItem.add(new Label("lastname", new PropertyModel(user,
						"lastname")));
				listItem.add(new Label("email",
						new PropertyModel(user, "email")));
				// organization name
				listItem.add(new OrganizationViewLink("organization", breadCrumbModel, user.getOrganization()));
				// country name
				listItem.add(new Label("country", new PropertyModel(user,
						"country.name")));
				listItem.add(new Label("locale", new PropertyModel(user,
						"locale")));
				listItem.add(new WebMarkupContainer("locked").setVisible(user
						.isLocked()));

				// BreadCrumb link to edit user
				listItem.add(new BreadCrumbLink("edit", breadCrumbModel) {
					protected IBreadCrumbParticipant getParticipant(
							String componentId) {
						return new UserFormPanel(componentId, breadCrumbModel,
								getCalipso().loadUser(user.getId()));
					}
				});

				// BreadCrumb link to UserAllocatePage
				listItem.add(new BreadCrumbLink("allocate", breadCrumbModel) {
					protected IBreadCrumbParticipant getParticipant(
							String componentId) {
						return new UserAllocatePanel(componentId, breadCrumbModel, user.getId());
					}
				});
			}
		};
		add(listView);

		if (userListModel.getObject() != null) {
			try {
				isNodataVisible = ((List) userListModel.getObject()).size() == 0;
			}// try
			catch (Exception e) {// For casting
				isNodataVisible = true;
			}// catch
		}// if
		else {
			isNodataVisible = true;
		}// else

		noDataContainer = new WebMarkupContainer("noData");
		noDataContainer.setVisible(isNodataVisible);
		add(noDataContainer);
	}

	/**
	 * wicket form
	 */
	private class SearchForm extends Form {

		public String getSearchText() {
			return searchText;
		}

		public void setSearchText(String searchText) {
			UserListPanel.this.searchText = searchText;
		}

		public String getSearchOn() {
			return searchOn;
		}

		public void setSearchOn(String searchOn) {
			UserListPanel.this.searchOn = searchOn;
		}

		public SearchForm(String id) {
			super(id);
			setModel(new CompoundPropertyModel(this));
			List<String> searchOnOptions = Arrays.asList(new String[] {
					"loginName", "name", "lastname", "email", "address", "phone" });
			DropDownChoice searchOnChoice = new DropDownChoice("searchOn",
					searchOnOptions, new IChoiceRenderer() {
						public Object getDisplayValue(Object o) {
							String s = (String) o;
							return localize("user_list." + s);
						}

						public String getIdValue(Object o, int i) {
							return o.toString();
						}
					});
			add(searchOnChoice);
			final TextField searchTextField = new TextField("searchText");
			searchTextField.setOutputMarkupId(true);
			add(searchTextField);
			// set focus on the search text field when page is rendered
			add(new Behavior() {
				public void renderHead(IHeaderResponse response) {
					response.renderOnLoadJavaScript("document.getElementById('"
							+ searchTextField.getMarkupId() + "').focus()");
				}
			});
		}

		// @Override
		// protected void onSubmit() {
		// setResponsePage(UserListPage.this);
		// }

	}

}
