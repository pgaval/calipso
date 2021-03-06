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
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.wicket.CalipsoApplication;

import org.apache.wicket.Application;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 *
 */
public class LoadableDetachableSpaceModel extends LoadableDetachableModel{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	/**
	 * 
	 * @param space
	 */
	public LoadableDetachableSpaceModel(Space space){
		this(space, space.getId());
	}
	
	/**
	 * 
	 * @param space
	 * @param id
	 */
	public LoadableDetachableSpaceModel(Space space, long id){
		super(space);
		if(id == 0){
			throw new IllegalArgumentException();
		}
		this.id = id;
	}
	
	/**
	 * @see org.apache.wicket.model.LoadableDetachableModel#load()
	 */
	@Override
	protected Object load() {
		return getJtrac().loadSpace(id);
	}
	
	private CalipsoService getJtrac(){
		return ((CalipsoApplication)Application.get()).getCalipso();
	}

}
