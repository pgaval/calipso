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

package gr.abiss.calipso.wicket.components.user;

import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.wicket.SearchUserPanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 *
 */
public abstract class UserDataView extends DataView{
	
	
	private static final long serialVersionUID = 1L;
	
	private final SimpleAttributeModifier sam = new SimpleAttributeModifier(
			"class", "alt");
	private final SimpleAttributeModifier selected = new SimpleAttributeModifier(
			"class", "selected");
	
	public abstract void onAddingUser(User user, AjaxRequestTarget target);

	/**
	 * @param id
	 * @param dataProvider
	 * @param itemsPerPage
	 */
	public UserDataView(String id, IDataProvider dataProvider, int itemsPerPage) {
		super(id, dataProvider, itemsPerPage);
		
	}
	
	public UserDataView(String id, IDataProvider dataProvider) {
		super(id, dataProvider);
		
	}

	/**
	 * @see org.apache.wicket.markup.repeater.RefreshingView#populateItem(org.apache.wicket.markup.repeater.Item)
	 */
	@Override
	protected void populateItem(final Item item) {
		final User user = (User) item.getModelObject();
		setDefaultModel(new CompoundPropertyModel(user));
		
		if (item.getIndex() % 2 != 0) {
			item.add(sam);
		}

		item.add(new Label("name", user.getName()));

		item.add(new Label("lastname", user.getLastname()));

		item.add(new Label("loginName", user.getLoginName()));

		String strOrganization;
		if (user.getOrganization() != null) {
			strOrganization = user.getOrganization().getName();
		} else {
			
			strOrganization = "";
		}

		item.add(new Label("organization", strOrganization));

		item.add(new Label("email", user.getEmail()));

		item.add(new Label("address", user.getAddress()));

		item.add(new Label("country", this.getLocalizer().getString("country."+user.getCountry().getId(), this)));
		
		item.add(new Label("phone", user.getPhone()));
		
		item.add(new AjaxLink("select") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				onAddingUser(user, target);
				item.add(selected);
			}
		});
	}

}
