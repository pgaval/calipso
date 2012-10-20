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

package gr.abiss.calipso.domain;

import java.io.Serializable;

import org.apache.wicket.Component;

public class PageDictionary implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String pageDescription;
	private String pageClassName;
	private String localizedKey;
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	public PageDictionary() {
		this.id = 0;
		this.pageDescription = null;
		this.pageClassName = null;
		this.localizedKey = null;
	}

	//---------------------------------------------------------------------------------------------
	
	public PageDictionary(int id, String pageDescription, String pageClassName) {
		this.id = id;
		this.pageDescription = pageDescription;
		this.pageClassName = pageClassName;
		this.localizedKey = "";
	}

	//---------------------------------------------------------------------------------------------

	public PageDictionary(int id, String pageDescription, String pageClassName, String localizedKey) {
		this.id = id;
		this.pageDescription = pageDescription;
		this.pageClassName = pageClassName;
		this.localizedKey = localizedKey;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPageDescription() {
		return pageDescription;
	}

	public void setPageDescription(String pageDescription) {
		this.pageDescription = pageDescription;
	}

	public String getPageClassName() {
		return pageClassName;
	}

	public void setPageClassName(String pageClassName) {
		this.pageClassName = pageClassName;
	}

	public String getLocalizedKey() {
		return localizedKey;
	}

	public void setLocalizedKey(String localizedKey) {
		this.localizedKey = localizedKey;
	}
	
	public String getDescription(Component component){
		if (this.localizedKey!=null){
			return component.getLocalizer().getString(this.localizedKey, null);
		}//if

		return this.pageDescription;
	}
	
	@Override
	public String toString() {
		return this.pageDescription + " (" + this.pageClassName + ")";
	}
}