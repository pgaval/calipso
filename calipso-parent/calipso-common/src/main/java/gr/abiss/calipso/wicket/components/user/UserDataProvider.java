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

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.wicket.CalipsoApplication;
import gr.abiss.calipso.wicket.components.LoadableDetachableDomainObjectModels.LoadableDetachableUserModel;

import java.util.Iterator;

import org.apache.wicket.Application;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

/**
 *
 */
public class UserDataProvider implements IDataProvider{
	
	
	private static final long serialVersionUID = 1L;
	
	private String searchText;
	private String searchOn;
	public UserDataProvider(String searchText, String searchOn){
		this.searchText = searchText;
		this.searchOn = searchOn;
	}
	
	/**
	 * @see org.apache.wicket.model.IDetachable#detach()
	 */
	public void detach() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(int, int)
	 */
	public Iterator<User> iterator(int first, int count) {
		return getJtrac().findUsersMatching(searchText, searchOn, first, count).iterator();
	}

	/**
	 * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
	 */
	public int size() {
		return getJtrac().findUsersCountMatching(searchText, searchOn);
	}

	/**
	 * @see org.apache.wicket.markup.repeater.data.IDataProvider#model(java.lang.Object)
	 */
	public IModel model(Object object) {
		// TODO Auto-generated method stub
		return new LoadableDetachableUserModel((User)object);
	}
	
	private CalipsoService getJtrac(){
		return ((CalipsoApplication)Application.get()).getCalipso();
	}

	/**
	 * @return the searchText
	 */
	public String getSearchText() {
		return searchText;
	}

	/**
	 * @return the searchOn
	 */
	public String getSearchOn() {
		return searchOn;
	}

	/**
	 * @param searchText the searchText to set
	 */
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	/**
	 * @param searchOn the searchOn to set
	 */
	public void setSearchOn(String searchOn) {
		this.searchOn = searchOn;
	}
	

}
