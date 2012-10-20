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

package gr.abiss.calipso.wicket.asset;

import java.util.List;
import java.util.ArrayList; 

import gr.abiss.calipso.dto.AbstractSearch;
import gr.abiss.calipso.wicket.BasePanel;

import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

/**
 * @author marcello
 */
public abstract class PaginationPanel extends BasePanel {
	private AbstractSearch abstractSearch;
	
	public abstract void onPreviousPageClick();
	public abstract void onNextPageClick();
	public abstract void onPageNumberClick();
	
	
	public PaginationPanel(String id, IBreadCrumbModel breadCrumbModel, AbstractSearch abstractSearch) {
		super(id, breadCrumbModel);
		this.abstractSearch = abstractSearch;
        
        long resultCount = this.abstractSearch.getResultCount();
        final int currentPage = this.abstractSearch.getCurrentPage();
        String resultCountMessage = resultCount == 1 ? "asset.customAttributes.recordFound" : "asset.customAttributes.recordsFound";
        Label recordsFound = new Label("recordsFound", localize(resultCountMessage, String.valueOf(resultCount)));
        add(recordsFound);
        recordsFound.setVisible(resultCount>0);
        
        WebMarkupContainer pagination = new WebMarkupContainer("pagination");
        
        if (this.abstractSearch.getPageCount()>1){
        	//Previous Page
            Link prevOn = new Link("prevOn") {
                public void onClick() {
                	PaginationPanel.this.abstractSearch.setCurrentPage(currentPage-1);
                	onPreviousPageClick();
                }
            };
            prevOn.add(new Label("prevOn", "<<"));
            pagination.add(prevOn);
            
            Label prevOff = new Label("prevOff", "<<");
            if(this.abstractSearch.getCurrentPage() == 1) {
                prevOn.setVisible(false);
            } else {
                prevOff.setVisible(false);
            }
            pagination.add(prevOff);
            
            //Discrete Page Numbers
            List<Integer> pageNumbers = new ArrayList<Integer>((int)this.abstractSearch.getPageCount());
            for(int i = 0; i < (int)this.abstractSearch.getPageCount(); i++) {
                pageNumbers.add(new Integer(i));
            }
            
            ListView pages = new ListView("pages", pageNumbers) {
                protected void populateItem(ListItem listItem) {
                    final Integer i = (Integer) listItem.getModelObject();
                    String pageNumber = i + 1 + "";
                    Link pageOn = new Link("pageOn") {
                        public void onClick() {
                        	PaginationPanel.this.abstractSearch.setCurrentPage(i+1);
                        	onPageNumberClick();
                        }
                    };
                    pageOn.add(new Label("pageOn", pageNumber));
                    Label pageOff = new Label("pageOff", pageNumber);
                    if(i == PaginationPanel.this.abstractSearch.getCurrentPage()-1) {
                        pageOn.setVisible(false);
                    } else {
                        pageOff.setVisible(false);
                    }
                    listItem.add(pageOn);
                    listItem.add(pageOff);
                }
            };
            pagination.add(pages);
            
            //Next page
            Link nextOn = new Link("nextOn") {
                public void onClick() {
                	PaginationPanel.this.abstractSearch.setCurrentPage(currentPage + 1);
                	onNextPageClick();
                }
            };
            nextOn.add(new Label("nextOn", ">>"));
            Label nextOff = new Label("nextOff", ">>");
            if(PaginationPanel.this.abstractSearch.getCurrentPage() == PaginationPanel.this.abstractSearch.getPageCount()) {
                nextOn.setVisible(false);
            } else {
                nextOff.setVisible(false);
            }
            pagination.add(nextOn);
            pagination.add(nextOff);
        }//if
        else{
        	pagination.setVisible(false);
        }//else
        add(pagination);
	}
}//PaginationPanel
