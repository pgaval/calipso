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
import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.wicket.CalipsoApplication;

import org.apache.wicket.Application;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 * A detachable model for AssetType domain object
 * Use this to not overload the session.
 *
 */
public class LoadableDetachableAssetTypeModel extends LoadableDetachableModel{
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	/**
	 * 
	 * @param assetType AssetType domain object
	 */
	public LoadableDetachableAssetTypeModel(AssetType assetType){
		this(assetType,assetType.getId());
	}
	/**
	 * 
	 * @param assetType AssetType domain object
	 * @param id AssetType domain object id. (Can NOT be zero)
	 */
	public LoadableDetachableAssetTypeModel(AssetType assetType, long id){
		// use super constructor so that the object
		// can be detached at the end of the request
		super(assetType);
		if(id == 0){
			throw new IllegalArgumentException();
			
		}
		this.id = id;
	}
	
	@Override
	protected Object load() {
		return getJtrac().loadAssetType(id);
	}
	
	private CalipsoService getJtrac(){
		return ((CalipsoApplication)Application.get()).getCalipso();
	}
}
