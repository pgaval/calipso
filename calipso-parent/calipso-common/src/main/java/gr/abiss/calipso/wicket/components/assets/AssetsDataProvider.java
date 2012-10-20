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

package gr.abiss.calipso.wicket.components.assets;

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.wicket.CalipsoApplication;
import gr.abiss.calipso.wicket.components.LoadableDetachableDomainObjectModels.LoadableDetachableAssetModel;

import java.util.Iterator;

import org.apache.wicket.Application;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

/**
 *
 */
public class AssetsDataProvider implements IDataProvider {

	private static final long serialVersionUID = 1L;

	private Asset asset;
	private Space space;
	
	/**
	 * 
	 * @param space The current space
	 * @param assetType The asset type
	 */
	public AssetsDataProvider(Space space, Asset asset){
		this.asset = asset;
		
	}
	
	// by default bring all assets available for current space
	public AssetsDataProvider(Space space){
		this.space = space;
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
	public Iterator iterator(int first, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
	 */
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see org.apache.wicket.markup.repeater.data.IDataProvider#model(java.lang.Object)
	 */
	public IModel model(Object object) {
		return new LoadableDetachableAssetModel((Asset)object);
	}
	
	private CalipsoService getJtrac(){
		return ((CalipsoApplication)Application.get()).getCalipso();
	}

	/**
	 * @return the asset
	 */
	public Asset getAsset() {
		return asset;
	}

	/**
	 * @param asset the asset to set
	 */
	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	/**
	 * @return the space
	 */
	public Space getSpace() {
		return space;
	}

	/**
	 * @param space the space to set
	 */
	public void setSpace(Space space) {
		this.space = space;
	}

}
