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

import gr.abiss.calipso.util.WebUtils;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author erasmus
 */
public class DashboardNumbers extends Panel {
	
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(DashboardNumbers.class);

	private boolean isNegative;
	Long number; 
	Long total;

	//---------------------------------------------------------------------------------------------
	
	public DashboardNumbers(String id, Long number, Long total) {
		this(id, number, total, false);
	}

	//---------------------------------------------------------------------------------------------
	
	public DashboardNumbers(String id, Long number, Long total, boolean isNegative) {
		super(id);
		if(number != null){
			this.number = number;
			this.total = total;
		}
		else{
			this.number = this.total = 0L;
		}

        this.isNegative = isNegative;	
        addComponents();
	}

	//---------------------------------------------------------------------------------------------
	
	private void addComponents(){
		WebMarkupContainer dashboardNumbers = new WebMarkupContainer("DashboardNumbers");
		
		if(number != null){
			//calculate percent, if total == 0 use 0% for percent to avoid ugly NaN
			double dPercent = (total==null || total==0) ? 0.0 : (double)number / total;
			String percent = WebUtils.formatPercentage(dPercent); 

	        //percent graph, use style="width:XXXpx;" where xxx=0...100
	        WebMarkupContainer loggedByMePercentImage = new WebMarkupContainer("percentImage");
	        loggedByMePercentImage.add(new SimpleAttributeModifier("style", "width:"+(dPercent*100)+"px;"));
	        
	        //add the graph
	        dashboardNumbers.add(loggedByMePercentImage);
			//add the plain number loggedByMe label
	        dashboardNumbers.add(new Label("Number", String.valueOf(number)).setRenderBodyOnly(true));
	        //add the percent of loggedByMe
	        dashboardNumbers.add(new Label("PercentNumber", new StringBuffer(" (").append(percent).append(")").toString()).setRenderBodyOnly(true));	
		}
		else{
			dashboardNumbers.setVisible(false);			
		}
		
		add(dashboardNumbers.setRenderBodyOnly(true));		
	}
}