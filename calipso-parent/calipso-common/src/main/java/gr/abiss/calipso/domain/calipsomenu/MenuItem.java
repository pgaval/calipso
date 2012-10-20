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

package gr.abiss.calipso.domain.calipsomenu;

import gr.abiss.calipso.domain.CalipsoBreadCrumbBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbBar;

public class MenuItem implements Serializable{
	private static final long serialVersionUID = 1L;
	private String description;
	private String pageClassName;
	private boolean isMenuSummary;
	private List<MenuItem> submenuList;
	private ResourceReference image;
	private Page page;
	private CalipsoBreadCrumbBar breadCrumbBar;

	///////////////////////////////////////////////////////////////////////////////////////////////
	
	public MenuItem() {
		this.description = "";
		this.pageClassName = "";
		this.isMenuSummary = false;
	}

	//---------------------------------------------------------------------------------------------

	public MenuItem(String description, String pageClassName) {
		this.description = description;
		this.pageClassName = pageClassName;
		this.isMenuSummary = false;
	}

	//---------------------------------------------------------------------------------------------

	public MenuItem(String description, String pageClassName, boolean isMenuSummary) {
		this.description = description;
		this.pageClassName = pageClassName;
		this.isMenuSummary = isMenuSummary;
		if (this.isMenuSummary){
			this.submenuList = new ArrayList<MenuItem>();
		}
	}

	//---------------------------------------------------------------------------------------------

	public MenuItem(String description, String pageClassName, ResourceReference image) {
		this(description, pageClassName, false, image);
	}

	//---------------------------------------------------------------------------------------------

	public MenuItem(String description, String pageClassName, ResourceReference image, CalipsoBreadCrumbBar breadCrumbBar) {
		this(description, pageClassName, false, image);
		this.breadCrumbBar = this.breadCrumbBar;
	}
	
	//---------------------------------------------------------------------------------------------

	public MenuItem(String description, String pageClassName, boolean isMenuSummary, ResourceReference image) {
		this(description, pageClassName, isMenuSummary);
		this.image = image;
	}
	
	//---------------------------------------------------------------------------------------------

	public MenuItem(String description, String pageClassName, boolean isMenuSummary, ResourceReference image, CalipsoBreadCrumbBar breadCrumbBar) {
		this(description, pageClassName, isMenuSummary);
		this.image = image;
		this.breadCrumbBar = breadCrumbBar;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////

	public void onClick(){
		//	Dummy event. Override if necessary. 
	}

	public void setBreadCrumbBar(){
		//Dummy. Override if necessary.
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPageClassName() {
		return this.pageClassName;
	}

	public void setPageClassName(String pageClassName) {
		this.pageClassName = pageClassName;
	}
	
	public boolean isMenuSummary() {
		return this.isMenuSummary;
	}

	public void setMenuSummary(boolean isMenuSummary) {
		this.isMenuSummary = isMenuSummary;
	}
	
	public List<MenuItem> getSubmenuList(){
		return this.submenuList;
	}
	
	public void setImage(ResourceReference image) {
		this.image = image;
	}
	
	public ResourceReference getImage() {
		return image;
	}

	public void setPage(Page page) {
		this.page = page;
	}
	
	public Page getPage() {
		return page;
	}

	public void setBreadCrumbBar(CalipsoBreadCrumbBar breadCrumbBar) {
		this.breadCrumbBar = breadCrumbBar;
	}

	public CalipsoBreadCrumbBar getBreadCrumbBar() {
		return breadCrumbBar;
	}

	public void add(MenuItem menuItem) throws NotSummaryMenuException{
		if (this.isMenuSummary){
			if (this.submenuList==null){
				this.submenuList = new ArrayList<MenuItem>();
			}//if
			this.submenuList.add(menuItem);
		}//if
		else{
			throw new NotSummaryMenuException();
		}//else
	}
}