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

package gr.abiss.calipso.wicket;

import gr.abiss.calipso.domain.SLASearch;
import gr.abiss.calipso.domain.Sla;
import gr.abiss.calipso.wicket.components.validators.NumberValidator;
import gr.abiss.calipso.wicket.yui.YuiCalendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

public class SLAsPage extends BasePage{

	private SLASearch searchSLA;
	
	public SLAsPage() {
		searchSLA = new SLASearch();
		createSLA();
		searchSLA();
		listSLAs();
		
	}

	public SLAsPage(SLASearch searchSLA) {
		this.searchSLA = searchSLA;
		createSLA();
		searchSLA();
		listSLAs();		
	}
	/**
	 * Link to SLA Creation
	 * */
	private void createSLA(){
		Link newSla = new Link("new") {
            public void onClick() {
            	setResponsePage(new SLAFormPage(SLAsPage.this));
            }//onClick
        };
        
        add(newSla);

	}//createSLA

	
	/***
	 *Search for SLA 
	 */
	
	private void searchSLA(){
		add(new SearchSLAForm("form", searchSLA));
	}//searchSLA
	
	/*
//	private class BiggerValidator extends AbstractValidator{
//		private int lessValue = 0;
//		private int moreValue = 0;
//		public BiggerValidator() {
//			this.lessValue = this.moreValue = 0;
//		}
//		
//		public BiggerValidator(int lessValue, int moreValue) {
//			this.lessValue = lessValue;
//			this.moreValue = moreValue;
//		}
//
//		protected void onValidate(IValidatable v) {
//			if (v!=null){
//				if (this.lessValue>this.moreValue){
//					error(v);
//			}
//		}
//		
//		@Override
//		protected String resourceKey() {
//			return "sla.fromToResponseTimeValid";
//		}
//	}
	
	*/
	
	private class SearchSLAForm extends Form {
		
		private SLASearch searchSLA;
		private CalipsoFeedbackMessageFilter filter;
				
		public SearchSLAForm(String id, SLASearch searchSLA) {
			super(id);
            FeedbackPanel feedback = new FeedbackPanel("feedback");
            filter = new CalipsoFeedbackMessageFilter();
            feedback.setFilter(filter);
            add(feedback);   
			
			this.searchSLA = searchSLA;
			CompoundPropertyModel model = new CompoundPropertyModel(searchSLA); 
			setModel(model);
			
			TextField slaCode = new TextField("slaCode");
			add(slaCode);
			
			YuiCalendar fromStartDate = new YuiCalendar("fromStartDate", new PropertyModel(model, "fromStartDate"), false);
			add(fromStartDate);
			
			YuiCalendar toStartDate = new YuiCalendar("toStartDate", new PropertyModel(model, "toStartDate"), false);
			add(toStartDate);
			
			YuiCalendar fromEndDate = new YuiCalendar("fromEndDate", new PropertyModel(model, "fromEndDate"), false);			
			add(fromEndDate);
			
			YuiCalendar toEndDate = new YuiCalendar("toEndDate", new PropertyModel(model, "toEndDate"), false);
			add(toEndDate);
			
			TextField notes = new TextField("notes");
			add(notes);
			
			final TextField fromResponseTime = new TextField("fromResponseTime");
			fromResponseTime.add(new NumberValidator("sla.notValidNumber"));
			add(fromResponseTime);

			final TextField toResponseTime = new TextField("toResponseTime");
			toResponseTime.add(new NumberValidator("sla.notValidNumber"));
			add(toResponseTime);
			
		}//SearchSLAForm
		
		@Override
		protected void onSubmit() {
			setResponsePage(new SLAsPage(searchSLA));
		}
		
	} //SearchSLAForm
	
	
	/**
	 * SLA list
	 * */
	private void listSLAs(){
        LoadableDetachableModel slaListModel = new LoadableDetachableModel() {
            protected Object load() {
            	List<Sla> slaList = new LinkedList<Sla>();
            	slaList.add(new Sla(1, "SLA-QQ-123", new Date("10/01/2007"), new Date("10/01/2009"), "Notes 1-2-3", 13.0));
            	slaList.add(new Sla(2, "SLA-QQ-789", new Date("10/01/2006"), new Date("10/01/2011"), "Notes 78 GHGH gggg", 24.0));
                return slaList; 
            }
        };
        
        ////////////////
        // Pagination //
        ////////////////
        long resultCount = 2;
        int pageCount = 3;
        final int currentPage = 1;
        
        String resultCountMessage = resultCount == 1 ? "sla.recordFound" : "sla.recordsFound";
        add(new Label("recordsFound", localize(resultCountMessage, String.valueOf(resultCount))));
        
        WebMarkupContainer pagination = new WebMarkupContainer("pagination");
        if(pageCount > 1) {
            Link prevOn = new Link("prevOn") {
                public void onClick() {
                    setResponsePage(SLAsPage.class);                    
                }
            };
            prevOn.add(new Label("prevOn", "<<"));
            Label prevOff = new Label("prevOff", "<<");
            if(currentPage == 0) {
                prevOn.setVisible(false);
            } else {
                prevOff.setVisible(false);
            }
            pagination.add(prevOn);
            pagination.add(prevOff);
            
            List<Integer> pageNumbers = new ArrayList<Integer>(pageCount);
            for(int i = 0; i < pageCount; i++) {
                pageNumbers.add(new Integer(i));
            }
            
            ListView pages = new ListView("pages", pageNumbers) {
                protected void populateItem(ListItem listItem) {
                    final Integer i = (Integer) listItem.getModelObject();
                    String pageNumber = i + 1 + "";
                    Link pageOn = new Link("pageOn") {
                        public void onClick() {
                            // TODO avoid next line, refresh pagination only
                            setResponsePage(SLAsPage.class);
                        }
                    };
                    pageOn.add(new Label("pageOn", pageNumber));
                    Label pageOff = new Label("pageOff", pageNumber);
                    if(i == currentPage) {
                        pageOn.setVisible(false);
                    } else {
                        pageOff.setVisible(false);
                    }
                    listItem.add(pageOn);
                    listItem.add(pageOff);
                }
            };
            pagination.add(pages);
            
            Link nextOn = new Link("nextOn") {
                public void onClick() {
                    setResponsePage(SLAsPage.class);                    
                }
            };
            nextOn.add(new Label("nextOn", ">>"));
            Label nextOff = new Label("nextOff", ">>");
            if(currentPage == pageCount - 1) {
                nextOn.setVisible(false);
            } else {
                nextOff.setVisible(false);
            }
            pagination.add(nextOn);
            pagination.add(nextOff);
        } else { // if pageCount == 1
            pagination.setVisible(false);
        }
        
        add(pagination);        
        
        
        //////////////
        // SLA List //
        //////////////
        
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
        
        ListView listView = new ListView("slaList", slaListModel) {
        	@Override
        	protected void populateItem(ListItem listItem) {
        		if (listItem.getIndex() % 2 !=0){
        			listItem.add(sam);
        		}//if
        		
        		final Sla sla = (Sla) listItem.getModelObject();
        		listItem.add(new Label("slaCode", new PropertyModel(sla, "slaCode")));
        		listItem.add(new Label("startDate", new PropertyModel(sla, "startDate")));
        		listItem.add(new Label("endDate", new PropertyModel(sla, "endDate")));
        		listItem.add(new Label("notes", new PropertyModel(sla, "notes")));
        		listItem.add(new Label("responseTime", new PropertyModel(sla, "responseTime")));
                Link edit = new Link("edit") {
                    public void onClick() {
//                    	setResponsePage(new SLAFormPage(SLAsPage.this, String.valueOf(sla.getSlaId())));
                    	setResponsePage(new SLAFormPage(SLAsPage.this, sla));
                    }//onClick
                };
                listItem.add(edit);
        	}//populateItem
        };
        
        add(listView);
        
	}//listSLAs
}