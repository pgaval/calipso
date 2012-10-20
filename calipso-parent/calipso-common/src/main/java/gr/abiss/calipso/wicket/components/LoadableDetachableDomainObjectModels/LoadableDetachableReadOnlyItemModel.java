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

import gr.abiss.calipso.domain.AbstractItem;
import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.wicket.CalipsoApplication;

import org.apache.wicket.Application;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 *
 */
public class LoadableDetachableReadOnlyItemModel extends LoadableDetachableModel {
	
	private static final long serialVersionUID = 1L;

	private long id;
	private Class clazz;
	/**
	 * 
	 * @param asset Asset domain object
	 */
	public LoadableDetachableReadOnlyItemModel(AbstractItem item){
		this(item,item.getId());
	}
	/**
	 * 
	 * @param asset Item domain object
	 * @param id Item domain object id. (Cannot be zero)
	 */
	public LoadableDetachableReadOnlyItemModel(AbstractItem item, long id){
		// use super constructor so that the object
		// can be detached at the end of the request
		super(item);
//		if(id == 0){
//			throw new IllegalArgumentException();
//		}
		this.id = id;
		this.clazz = item.getClass();
		if(!this.clazz.equals(Item.class) && !this.clazz.equals(History.class)){
			throw new IllegalArgumentException("This model cannot handle instances of "+this.clazz);
		}
	}
	
	@Override
	protected Object load() {
		Object o = null;
		if(this.clazz.equals(Item.class)){
			o = ((CalipsoApplication)Application.get()).getCalipso().loadItem(this.id);
		}
		else if(this.clazz.equals(History.class)){
			o = ((CalipsoApplication)Application.get()).getCalipso().loadHistory(this.id);
		}
		return o;
	}
	
}
