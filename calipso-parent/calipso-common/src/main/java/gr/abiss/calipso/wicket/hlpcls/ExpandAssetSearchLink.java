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

package gr.abiss.calipso.wicket.hlpcls;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;

public abstract class ExpandAssetSearchLink extends AjaxLink{
	
	private MarkupContainer targetComponent;
	private MarkupContainer searchContainer;
	private MarkupContainer searchPlaceHolder;
	private MarkupContainer searchForm;

	private boolean isOpen = false;

	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	public ExpandAssetSearchLink(String id) {
		super(id);
	}

	//---------------------------------------------------------------------------------------------
	
	public ExpandAssetSearchLink(String id, MarkupContainer targetComponent,
			MarkupContainer searchContainer, MarkupContainer searchPlaceHolder,
			MarkupContainer searchForm) {
		super(id);
		this.targetComponent = targetComponent;
		this.searchContainer = searchContainer;
		this.searchPlaceHolder = searchPlaceHolder;
		this.searchForm = searchForm;
	}

	//---------------------------------------------------------------------------------------------

	public ExpandAssetSearchLink(String id, MarkupContainer targetComponent,
			MarkupContainer searchContainer, MarkupContainer searchPlaceHolder,
			MarkupContainer searchForm, boolean isOpen) {
		super(id);
		this.targetComponent = targetComponent;
		this.searchContainer = searchContainer;
		this.searchPlaceHolder = searchPlaceHolder;
		this.searchForm = searchForm;
		this.isOpen = isOpen;
	}


	//---------------------------------------------------------------------------------------------
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	public MarkupContainer getTargetComponent() {
		return targetComponent;
	}

	public void setTargetComponent(MarkupContainer targetComponent) {
		this.targetComponent = targetComponent;
	}

	public MarkupContainer getSearchContainer() {
		return searchContainer;
	}

	public void setSearchContainer(MarkupContainer searchContainer) {
		this.searchContainer = searchContainer;
	}

	public MarkupContainer getSearchPlaceHolder() {
		return searchPlaceHolder;
	}

	public void setSearchPlaceHolder(MarkupContainer searchPlaceHolder) {
		this.searchPlaceHolder = searchPlaceHolder;
	}

	public MarkupContainer getSearchForm() {
		return searchForm;
	}

	public void setSearchForm(MarkupContainer searchForm) {
		this.searchForm = searchForm;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}	

	//////////////////////////////////////////////////////////////////////////////////////////////////

	public abstract void onLinkClick(); 
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onClick(AjaxRequestTarget target) {

		if (isOpen){
			//Remove search form panel from place holder
			searchPlaceHolder.remove(searchForm);
			
			//Remove place holder from container
			searchContainer.remove(searchPlaceHolder);
			
			//Add place holder to container
			searchContainer.add(searchPlaceHolder);
			
			//Set place holder to not visible
			searchPlaceHolder.setVisible(false);
			
			//Set flag to false
			isOpen = false;
		}
		else{
			//If place holder exists remove it from container
			if (searchPlaceHolder!=null){
				searchContainer.remove(searchPlaceHolder);
			}

			//Create place holder
			searchPlaceHolder = new WebMarkupContainer(searchPlaceHolder.getId());
			
			//Add place holder to container 
			searchContainer.add(searchPlaceHolder);
			
			//Add Search Form to place holder 
			searchPlaceHolder.add(searchForm);
			
			//add Place Holder to container
			searchContainer.add(searchPlaceHolder);

			isOpen = true;
		}

		onLinkClick();
		target.addComponent(targetComponent);
	}//onClick

}//ExpandAssetSearchLink
