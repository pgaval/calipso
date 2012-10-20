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

import gr.abiss.calipso.Constants;
import gr.abiss.calipso.util.Page;

import java.io.Serializable;
import java.util.List;

public class Pagination implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private List list;
	private int pageSize = Constants.PAGE_SIZE;
	private int resultCount;
    private Page page;

    
	/////////////////////////////////////////////////////////////////////////
    
	public Pagination(List list){
		this.list = list;
		this.resultCount = list.size();
		this.page = new Page(resultCount, pageSize);
		
	}//Pagination
	
	//---------------------------------------------------------------------
	
	public Pagination(){
		this.page = new Page();
	}
	
	/////////////////////////////////////////////////////////////////////////

    public long getPageCount(){
    	return this.page.getPages();
    }
    
    //---------------------------------------------------------------------
    
    public int getCurrentPage(){
    	return this.page.getCurrentPage();
    }
    
    //---------------------------------------------------------------------
    
    public void setPageSize(int pageSize){
    	this.pageSize = pageSize;
    	this.page.setLinesPP(this.pageSize);
    }//setPageSize

    //---------------------------------------------------------------------
    
	public int getResultCount() {
		return this.resultCount;
	}

	//---------------------------------------------------------------------
	
	public void setResultCount(Long resultCount) {
		int currentPage = this.getCurrentPage();
		boolean keepCurrentPage = (this.resultCount == resultCount);
		
		this.resultCount = resultCount.intValue();
		this.page = new Page(this.resultCount, this.pageSize);
		if (keepCurrentPage){
			this.page.setCurrentPage(currentPage);
		}//if
	}

	//---------------------------------------------------------------------
	
	public int getPageSize() {
		return pageSize;
	}

	//---------------------------------------------------------------------
	
	public int getPageBegin(){
		return this.page.getPageBegin();
	}
    
	//---------------------------------------------------------------------
	
	public int getPageEnd(){
		return this.page.getPageEnd();
	}
	
	//---------------------------------------------------------------------

	public void setCurrentPage(int currentPage){
		this.page.setCurrentPage(currentPage);
	}//setCurrentPage
	
}//Pagination