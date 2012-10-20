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

package gr.abiss.calipso.util;

import java.io.Serializable;

public class Page implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private int _totalLines;
	private int _linesPP;
	private int _pages;
	private int _currentPage;
	private int _pageBegin;
	private int _pageEnd;
	
	private Page _linkList;
	
	//======================================================================
	
	public Page(){
		
		_totalLines = 1;
		_linesPP = 1;
		_currentPage = 1;
		_pages = calcPages();
		_pageBegin = getPageBegin();
		_pageEnd = getPageEnd();
		
	}//Page
	
	//----------------------------------------------------------------------
	
	public Page(int totalLines){
		
		_totalLines = totalLines;
		_linesPP = 1;
		_currentPage = 1;
		_pages = calcPages();
		_pageBegin = getPageBegin();
		_pageEnd = getPageEnd();
		
	}//Page
	
	//----------------------------------------------------------------------

	public Page(int totalLines, int linesPerPage){
		
		_totalLines = totalLines;
		_linesPP = linesPerPage;
		_currentPage = 1;
		_pages = calcPages();
		_pageBegin = getPageBegin();
		_pageEnd = getPageEnd();
		
	}//Page
		
	//======================================================================
	
	private void createLinkList(){
		_linkList = new Page();
		_linkList.setTotalLines(this.getTotalLines());
	}//createLinkList
	
	//======================================================================
	
	public int calcPages(){		
		
		if (_totalLines==0){
			return  1;
		}//if
		
		if (_totalLines % _linesPP==0){
				return _totalLines / _linesPP;
			}//if
	
			return (_totalLines / _linesPP) + 1;
		
	}//calcPages
	
	//----------------------------------------------------------------------
	
	public int getPageBegin(){
		return ((_currentPage-1) * _linesPP) + 1;
	}//getPageBegin
	
	//----------------------------------------------------------------------

	public int getPageEnd(){
		if (_currentPage * _linesPP >= _totalLines){
				return _totalLines;
			}//if
	
			return _currentPage * _linesPP;			
	}//getPageEnd
	
	//----------------------------------------------------------------------
	
	public int getPages(){
		return _pages;
	}//getPages
	
	//----------------------------------------------------------------------
	
	public void setTotalLines(int totalLines){
		
		_totalLines = totalLines;
		_pages  = calcPages();
			
	}//setTotalLines
	
	//----------------------------------------------------------------------
	
	public int getTotalLines(){
		
		return _totalLines;
		
	}//getTotalLines
	
	//----------------------------------------------------------------------
	
	public void setLinesPP(int lpp){
	
		_linesPP = lpp;
		_pages  = calcPages();
		
	}//setLinesPP
	
	//----------------------------------------------------------------------

	public int getLinesPP(){
		
		return _linesPP;
		
	}//getLinesPP
	
	//----------------------------------------------------------------------
	
	public void setCurrentPage(int cp){
		_currentPage = cp;
	}//setCurrentPage
	
	//----------------------------------------------------------------------

	public int getCurrentPage(){
		
		return _currentPage;
		
	}//getCurrentPage
	
	//----------------------------------------------------------------------
	
	public int getRestLines(){
		if (_totalLines==getPageEnd()){
			return Math.abs((_pages*_linesPP)-_totalLines);			
		}//if
		
		return 0;
	}//getRestLines
	
	//----------------------------------------------------------------------
	
	public void setLinklistPages(int pgLnkPages){
		createLinkList();
		_linkList.setLinesPP(pgLnkPages);
		
	}//setPageLinklistPages
	
	//----------------------------------------------------------------------
	
	public int getLinkListBegin(){
		int d = this.getCurrentPage()/_linkList.getLinesPP();
		int r = this.getCurrentPage()%_linkList.getLinesPP();
		if (r==0){
			_linkList.setCurrentPage(d);
		}//if
		else{
			_linkList.setCurrentPage(d+1);
		}//else

		return _linkList.getPageBegin(); 
	}//getLinkListBegin
	
	//----------------------------------------------------------------------
	
	public int getLinkListEnd(){
		int d = this.getCurrentPage()/_linkList.getLinesPP();
		int r = this.getCurrentPage()%_linkList.getLinesPP();
		if (r==0){
			_linkList.setCurrentPage(d);
		}//if
		else{
			_linkList.setCurrentPage(d+1);
		}//else

		return _linkList.getPageEnd();
	}//getLinkListEnd
	
}//Page
