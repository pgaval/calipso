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

import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.SavedSearch;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.exception.CalipsoSecurityException;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.util.ItemUtils;
import gr.abiss.calipso.wicket.register.RegisterAnonymousUserFormPage;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * header navigation
 */
public class HeaderPanel extends BasePanel {
	protected static final Logger logger = Logger.getLogger(HeaderPanel.class);

	// this constructor draws only the title. Used in login and logout
	public HeaderPanel(boolean simple) {
		super("header");
		try {
			add(new WebMarkupContainer("user").setVisible(false));
			add(new WebMarkupContainer("logout").setVisible(false));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		boolean hideLogin = BooleanUtils.toBoolean(getCalipso().loadConfig(
				"calipso.hideLoginLink"));
		boolean hideRegister = BooleanUtils.toBoolean(getCalipso().loadConfig(
				"calipso.hideRegisterLink"));
		final User user = getPrincipal();
		if ((user == null || user.getId() == 0)) {
			add(new Link("login") {
				public void onClick() {
					setResponsePage(LoginPage.class);
				}
			}.setVisible(!hideLogin));
			add(new Link("register") {
				public void onClick() {
					setResponsePage(RegisterAnonymousUserFormPage.class);
				}
			}.setVisible(!hideRegister));// TODO: move to config
		} else {
			add(new WebMarkupContainer("login").setVisible(false));
			add(new WebMarkupContainer("register").setVisible(false));
		}
		add(new WebMarkupContainer("dashboard").setVisible(false));
		add(new WebMarkupContainer("search").setVisible(false));
		add(new WebMarkupContainer("options").setVisible(false));
		// add(new WebMarkupContainer("space").setVisible(false));
		// add(new WebMarkupContainer("new").setVisible(false));
	}

	public HeaderPanel() {
		super("header");

		final User user = getPrincipal();
		final List<Space> spaces = user != null ? new ArrayList<Space>(
				user.getSpaces()) : new ArrayList<Space>();

		boolean hideLogin = BooleanUtils.toBoolean(getCalipso().loadConfig(
				"calipso.hideLoginLink"));
		boolean hideRegister = BooleanUtils.toBoolean(getCalipso().loadConfig(
				"calipso.hideRegisterLink"));
		// manage single space
		if (spaces.size() == 1) {
			setCurrentSpace(spaces.get(0));
		}
		final Space space = getCurrentSpace();
		Component link = null;
		if (getPrincipal().isAnonymous()) {
			ExternalLink externalLink = new ExternalLink("dashboard", "/");
			externalLink.setContextRelative(true);
			link = externalLink;
		} else {
			link = new Link("dashboard") {
				public void onClick() {
					setCurrentSpace(null);
					setResponsePage(DashboardPage.class);
				}
			};
		}
		add(link);

		if (space == null) {

			// add(new Label("space", "").setVisible(false));// 1
			// add(new Label("new", "").setVisible(false));// 2
			add(new Link("search") {// 3
				public void onClick() {
					setResponsePage(ItemSearchFormPage.class);
				}
			}.setVisible(user != null && user.getSpaceCount() > 0
					&& !user.isAnonymous()));
		} else {
			/*
			 * add(new Link("space") {
			 * 
			 * @Override public void onClick() {
			 * setResponsePage(SpacePage.class); } }.add(new Label("space",
			 * space.getName())));
			 */
			// add(new WebMarkupContainer("space").add(new Label("space",
			// space.getName())));

			// In case that User opens an Item direct from e-mail notification
			// link
			// and has no access to this Item
			/*
			 * try { if (user.getPermittedTransitions(space, State.NEW).size() >
			 * 0) { add(new Link("new") { public void onClick() {
			 * setResponsePage(ItemFormPage.class); } }); } else { add(new
			 * WebMarkupContainer("new").setVisible(false)); } } catch
			 * (Exception e) { logger.error("user.getPermittedTransitions :: " +
			 * e.getMessage()); add(new
			 * WebMarkupContainer("new").setVisible(false)); }
			 */
			add(new Link("search") {
				public void onClick() {
					// if search then we user global search
					setCurrentSpace(null);
					setResponsePage(ItemSearchFormPage.class);
				}
			}.setVisible(user.getSpaceCount() > 0 && !user.isAnonymous()));
		}

		if (user == null || user.getId() == 0) {
			add(new WebMarkupContainer("options").setVisible(false));
			add(new WebMarkupContainer("logout").setVisible(false));
			add(new Link("login") {
				public void onClick() {
					setResponsePage(LoginPage.class);
				}
			}.setVisible(!hideLogin));
			add(new Link("register") {
				public void onClick() {
					setResponsePage(RegisterAnonymousUserFormPage.class);
				}
			}.setVisible(!hideRegister));
			add(new WebMarkupContainer("user").setVisible(false));
		} else {
			add(new Link("options") {
				public void onClick() {
					// when options clicked then we go to menu that space
					// doesn't have meaning
					setCurrentSpace(null);
					setResponsePage(OptionsPage.class);
				}
			});
			add(new Link("logout") {
				public void onClick() {
					Cookie cookie = new Cookie("calipsoService", "");
					String path = ((WebRequest) getRequest()).getContextPath();
					cookie.setPath(path);
					((WebResponse) getResponse()).clearCookie(cookie);
					getSession().invalidate();
					logger.debug("invalidated session and cleared cookie");
					// is acegi - cas being used ?
					String logoutUrl = ((CalipsoApplication) getApplication())
							.getCasLogoutUrl();
					if (logoutUrl != null) {
						logger.debug("cas authentication being used, clearing security context and redirecting to cas logout page");
						SecurityContextHolder.clearContext();
						// have to use stateless page reference because session
						// is killed
						setResponsePage(CasLogoutPage.class);
					} else {
						setResponsePage(LogoutPage.class, new PageParameters(
								"locale=" + user.getLocale()));
					}
				}
			});
			add(new WebMarkupContainer("login").setVisible(false));
			// issue
			add(new WebMarkupContainer("register").setVisible(false));

			add(new Link("user") {
				public void onClick() {
					setResponsePage(new UserViewPage(user));
				}
			}.add(new Label("user", user.getDisplayValue())
					.setRenderBodyOnly(true)));
		}
	}

}