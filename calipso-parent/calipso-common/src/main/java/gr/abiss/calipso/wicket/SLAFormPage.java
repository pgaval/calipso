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

import java.util.Date;

import gr.abiss.calipso.domain.Sla;
import gr.abiss.calipso.wicket.components.validators.NumberValidator;
import gr.abiss.calipso.wicket.yui.YuiCalendar;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

public class SLAFormPage extends BasePage{

	private WebPage previousPage;
	
	public SLAFormPage(WebPage previousPage){
		this.previousPage = previousPage;
		
		Sla sla = new Sla();
		add (new SLAForm("form", sla));
	}
	
	//----------------------------------------------
	
	public SLAFormPage(WebPage previousPage, String slaId) {
		this.previousPage = previousPage;
		
		Sla sla = null;
//		String slaId = params.getString("0");
		if (slaId!=null && !slaId.equals("")){
			if (slaId.equals("1")){
				sla = new Sla(1, "SLA-QQ-123", new Date("10/01/2007"), new Date("10/01/2009"), "Notes 1-2-3", 13.0);
			}
			if (slaId.equals("2")){
				sla = new Sla(2, "SLA-QQ-789", new Date("10/01/2006"), new Date("10/01/2011"), "Notes 78 GHGH gggg", 24.0);
			}
		}//if
		else{
			sla = new Sla();
		}//else
		
		add (new SLAForm("form", sla));  
	}//SLAFormPage
	
	//----------------------------------------------
	
	public SLAFormPage(WebPage previousPage, Sla sla) {
		add (new SLAForm("form", sla));
	}
	/**
	 ** Wicket Form
	 */
	
	private class SLAForm extends Form{
		private CalipsoFeedbackMessageFilter filter;
		private Sla sla;
		
		public SLAForm(String id, final Sla sla) {
			super(id);
			this.sla = sla;
            FeedbackPanel feedback = new FeedbackPanel("feedback");
            filter = new CalipsoFeedbackMessageFilter();
            feedback.setFilter(filter);
            add(feedback);   
			
			CompoundPropertyModel model = new CompoundPropertyModel(sla);
			setModel(model);

			TextField slaCode = new TextField("slaCode");
			add(slaCode);

			YuiCalendar startDate = new YuiCalendar("startDate", new PropertyModel(model, "startDate"), false);
			add(startDate);
			
//			TextField startDate = new TextField("startDate");
//			add(startDate);
			
//			TextField endDate = new TextField("endDate");
//			add(endDate);
			
			YuiCalendar endDate = new YuiCalendar("endDate", new PropertyModel(model, "endDate"), false);
			add(endDate);
			
			TextArea notes = new TextArea("notes");
			add(notes);
			
			final TextField responseTime = new TextField("responseTime");
			responseTime.setType(Double.class);
//			responseTime.add(new NumberValidator("sla.notValidNumber")); 
			add(responseTime);
			
			Link cancel = new Link("cancel"){
				@Override
				public void onClick() {
					if (previousPage==null){
						setResponsePage(SLAsPage.class);
					}//if
					else{
						setResponsePage(previousPage);
					}//else
				}
			};
			
			add(cancel);
		}
		
		@Override
		protected void onSubmit() {
			setResponsePage(new SLAFormPage(previousPage, sla));
		}
	} 
}
