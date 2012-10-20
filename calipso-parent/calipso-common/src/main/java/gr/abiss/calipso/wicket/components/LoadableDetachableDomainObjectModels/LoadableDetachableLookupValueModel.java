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

package gr.abiss.calipso.wicket.components.LoadableDetachableDomainObjectModels;

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.CalipsoApplication;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 *
 */
public class LoadableDetachableLookupValueModel extends LoadableDetachableModel {
	protected static final Logger logger = Logger.getLogger(LoadableDetachableLookupValueModel.class);

	private static final long serialVersionUID = 1L;
	private long id;
	
	
	/**
	 * 
	 * @param lookupValue
	 */
	public LoadableDetachableLookupValueModel(CustomAttributeLookupValue lookupValue){
		this(lookupValue, lookupValue.getId());
	}
	
	
	
	/**
	 * @param lookupValue
	 * @param id
	 */
	public LoadableDetachableLookupValueModel(
			CustomAttributeLookupValue lookupValue, long id) {
		super(lookupValue);
		if(id == 0){
			throw new IllegalArgumentException("CustomAttributeLookupValue.id cannot be zero!");
		}
		this.id = id;
	}
	
	
	/** 
	 * @see org.apache.wicket.model.LoadableDetachableModel#load()
	 */
	@Override
	protected Object load() {
		return getJtrac().loadCustomAttributeLookupValue(id);
	}
	
	
	private CalipsoService getJtrac(){
		return ((CalipsoApplication)Application.get()).getCalipso();
	}



	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}



	/**
	 * @param id the id to set
	 */
	private void setId(long id) {
		this.id = id;
	}

}
