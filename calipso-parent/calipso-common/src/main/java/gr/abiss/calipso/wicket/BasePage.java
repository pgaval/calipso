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
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.wicket.helpMenu.HelpMenuPanel;

import org.apache.log4j.Logger;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * base class for all wicket pages, this provides a way to access the spring
 * managed service layer also takes care of the standard template for all pages
 * which is using wicket markup inheritance
 */
public abstract class BasePage extends WebPage {
	private HeaderPanel headerPanel;
	private final InfoPanel infoPanel;
	private Panel helpMenuPanel;

	protected static final Logger logger = Logger.getLogger(BasePage.class);

	public BasePage() {

		headerPanel = new HeaderPanel();
		headerPanel.setRenderBodyOnly(true);
		add(headerPanel);
		infoPanel = new InfoPanel();
		infoPanel.setRenderBodyOnly(false);
		add(infoPanel);
		this.setVersioned(true);
		String calipsoVersion = ComponentUtils.getCalipso(this).getReleaseVersion();
		add(new Label("version", calipsoVersion));
		setTitle("Calipso");
		if (getCurrentSpace() != null) {
			helpMenuPanel = new HelpMenuPanel("menu");
		} else {
			helpMenuPanel = (Panel) new EmptyPanel("menu")
					.setVisible(false);
		}
		add(helpMenuPanel);

	}

	protected void setTitle(String title) {
		add(new Label("title", title));
	}

	public String getInfo() {
		return "";
	}

	protected void refreshHeader() {
		HeaderPanel newHeaderPanel = new HeaderPanel();
		headerPanel.replaceWith(newHeaderPanel);
		headerPanel = newHeaderPanel;
	}

	protected void refreshMenu() {
		Panel newMenuPanel;
		if (getCurrentSpace() != null) {
			newMenuPanel = new HelpMenuPanel("menu");
		} else {
			newMenuPanel = (Panel) new EmptyPanel("menu")
					.setRenderBodyOnly(true);
		}

		helpMenuPanel.replaceWith(newMenuPanel);
		helpMenuPanel = newMenuPanel;
	}

	protected void refreshMenu(IBreadCrumbModel breadCrumbModel) {
		Panel newMenuPanel;
		if (getCurrentSpace() != null) {
			newMenuPanel = new HelpMenuPanel("menu", breadCrumbModel);
		} else {
			newMenuPanel = (Panel) new EmptyPanel("menu")
					.setRenderBodyOnly(true);
		}

		helpMenuPanel.replaceWith(newMenuPanel);
		helpMenuPanel = newMenuPanel;
	}

	protected CalipsoService getCalipso() {
		return ComponentUtils.getCalipso(this);
	}

	protected User getPrincipal() {
		return ComponentUtils.getPrincipal(this);
	}

	protected void setCurrentSpace(Space space) {
		ComponentUtils.setCurrentSpace(this, space);
	}

	protected Space getCurrentSpace() {
		return ComponentUtils.getCurrentSpace(this);
	}

	protected void setCurrentItemSearch(ItemSearch itemSearch) {
		ComponentUtils.setCurrentItemSearch(this, itemSearch);
	}

	protected ItemSearch getCurrentItemSearch() {
		return ComponentUtils.getCurrentItemSearch(this);
	}

	protected String localize(String key) {
		return ComponentUtils.localize(this, key);
	}

	protected String localize(String key, Object... params) {
		return ComponentUtils.localize(this, key, params);
	}

	protected void refreshPrincipal(User user) {
		ComponentUtils.refreshPrincipal(this, user);
	}

	protected void refreshPrincipal() {
		ComponentUtils.refreshPrincipal(this);
	}

	protected InfoPanel getInfoPanel() {
		return this.infoPanel;
	}

	protected void setInfo(String info) {
		this.infoPanel.setInfo(info);
	}
}