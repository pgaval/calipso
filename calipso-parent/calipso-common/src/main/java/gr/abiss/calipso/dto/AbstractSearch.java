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

package gr.abiss.calipso.dto;

import gr.abiss.calipso.domain.Pagination;
import gr.abiss.calipso.wicket.ComponentUtils;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.Component;
import org.hibernate.criterion.DetachedCriteria;

public abstract class AbstractSearch extends Pagination implements Serializable{

	private static final long serialVersionUID = 1L;
	protected String sortFieldName = null;
	protected boolean sortDescending = false;
	protected Object assetObject;

	public AbstractSearch(Component c){
		this.setPageSize(ComponentUtils.getCalipso(c).getRecordsPerPage());
	}//AbstractSearch
	
	//----------------------------------------------------------------------
	
	public AbstractSearch(Object assetObject, String sortFieldName){
		this.assetObject = assetObject;
		this.sortFieldName = sortFieldName;
	}//AbstractSearch
	
	///////////////////////////////////////////////////////////////////////
	
	
	public abstract DetachedCriteria getDetachedCriteria();
	
	//---------------------------------------------------------------------
	
	public abstract DetachedCriteria getDetachedCriteriaForCount();
	
	//---------------------------------------------------------------------

	public abstract List<String> getColumnHeaders();
	
	//---------------------------------------------------------------------
	
	public abstract Object getSearchObject();  
	
	/////////////////////////////////////////////////////////////////////////
	
	public void doSort(String sortFieldName) {
    	this.setCurrentPage(1);
        if (this.getSortFieldName().equals(sortFieldName)) {
        	this.toggleSortDirection();
        } else {
        	this.setSortFieldName(sortFieldName);
        	this.setSortDescending(false);
        }//else
    }//doSort
	
	/////////////////////////////////////////////////////////////////////////
	
    public void toggleSortDirection() {
        sortDescending = !sortDescending;
    }//toggleSortDirection

	public String getSortFieldName() {
		return sortFieldName;
	}

	//--------------------------------------------------------------------------------------------------
	
	public void setSortFieldName(String sortFieldName) {
		this.sortFieldName = sortFieldName;
	}

	//--------------------------------------------------------------------------------------------------

	public boolean isSortDescending() {
		return sortDescending;
	}

	//--------------------------------------------------------------------------------------------------

	public void setSortDescending(boolean sortDescending) {
		this.sortDescending = sortDescending;
	}
	
    
}//AbstractSearch
