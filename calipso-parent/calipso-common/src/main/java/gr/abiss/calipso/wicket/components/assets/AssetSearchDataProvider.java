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
import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.dto.AssetSearch;
import gr.abiss.calipso.wicket.CalipsoApplication;
import gr.abiss.calipso.wicket.components.LoadableDetachableDomainObjectModels.LoadableDetachableAssetModel;
import gr.abiss.calipso.wicket.components.LoadableDetachableDomainObjectModels.LoadableDetachableAssetTypeModel;

import java.util.Iterator;

import org.apache.wicket.Application;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

/**
 *
 */
public class AssetSearchDataProvider implements IDataProvider{

	private static final long serialVersionUID = 1L;

	private AssetSearch assetSearch;
	
	/**
	 * 
	 * @param space The current space
	 * @param assetType The asset type
	 */
	public AssetSearchDataProvider(AssetSearch assetSearch){
		this.assetSearch = assetSearch;
		
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
	public Iterator<Asset> iterator(int first, int count) {
		return getJtrac().findAssetsMatchingSubList(assetSearch, false, first, count).iterator();
	}

	/**
	 * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
	 */
	public int size() {
		return getJtrac().findAssetsMatchingCount(assetSearch, false);
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
	 * @return the assetSearch
	 */
	public AssetSearch getAssetSearch() {
		return assetSearch;
	}

	/**
	 * @param assetSearch the assetSearch to set
	 */
	public void setAssetSearch(AssetSearch assetSearch) {
		this.assetSearch = assetSearch;
	}
	

}
