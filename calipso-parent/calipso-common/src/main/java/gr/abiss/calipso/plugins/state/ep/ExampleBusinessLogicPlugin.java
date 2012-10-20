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

package gr.abiss.calipso.plugins.state.ep;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.plugins.state.AbstractStatePlugin;
import gr.abiss.calipso.plugins.state.CopyAssetInfoToItemPlugin;
import gr.abiss.calipso.util.XmlUtils;
import gr.abiss.calipso.wicket.CalipsoSession;

/**
 * @author manos
 *
 */
public class ExampleBusinessLogicPlugin extends CopyAssetInfoToItemPlugin {
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(ExampleBusinessLogicPlugin.class);
	

	public ExampleBusinessLogicPlugin(){
		super();
		logger.debug("ExampleBusinessLogicPlugin constructor called");
	}
	
	/**
	 * @see gr.abiss.calipso.plugins.state.AbstractStatePlugin#execute(gr.abiss.calipso.CalipsoService, gr.abiss.calipso.domain.History)
	 */
	@Override
	public Serializable executePostStateChange(CalipsoService calipsoService, History history) {
		// update item using asset info first

		logger.debug("ExampleBusinessLogicPlugin execute called");
		// now move on with checks and calculations
		Item item = history.getParent();
		List<Field> itemFields = item.getSpace().getMetadata().getFieldList();
		// if the first option is selected, i.e. "active"

		logger.debug("ExampleBusinessLogicPlugin item.getCusInt03(): "+item.getCusInt03());
		
		if(item.getCusInt03().intValue() == 1){
			// update cusTim05
			item.setCusTim05(item.getCusTim01());
			// update cusDbl11
			item.setCusDbl11(new Double(item.getCusDbl02().doubleValue() - item.getCusDbl01().doubleValue()));
			double mustBeInteger = item.getCusDbl11().doubleValue() / item.getCusDbl10().doubleValue();
			if(Math.floor(mustBeInteger) != mustBeInteger){
				String comment = "\nH περίοδος παραγωγής στην οποία αντιστοιχεί η παρούσα ΕΠ δεν είναι ίση ή πολλαπλάσια της πειοδικότητας ελέγχου (Rejected, as the production period for this GoP is not equal or multiple of the check period).";
				reject(calipsoService, history, item, comment);
			}
			
			// update cusTim03
			Date now = item.getTimeStamp();
			Calendar calendarCusTim02 = Calendar.getInstance();
			calendarCusTim02.setTime(item.getCusTim02());
			calendarCusTim02.add(Calendar.DATE, 30);
			item.setCusTim03(calendarCusTim02.getTime());
			
			// reset and go for cusTim04
			calendarCusTim02.setTime(item.getCusTim02());
			calendarCusTim02.add(Calendar.YEAR, 1);
			item.setCusTim04(calendarCusTim02.getTime());
			
			// check if the item was submitted during the allowed period
			// cusTim04 >= timeStamp >= cusTim03
			if(!(item.getCusTim04().compareTo(now) >= 0 && item.getCusTim03().compareTo(now) <= 0)){
				String comment = "\nΑπορρίφθηκε καθώς to αίτημα δεν είναι εμπρόθεσμο (Rejected, as the submission is not within the minimum/maximum valid submission date).";
				reject(calipsoService, history, item, comment);
			}
			
			// update cusTim06
			item.setCusTim06(calendarCusTim02.getTime());
				
		}
		else{
			String comment = "\nΑπορρίφθηκε καθώς ο ΜΑΜΕΠ δεν είναι ενεργός (Rejected, as MAMEP is inactive).";
			reject(calipsoService, history, item, comment);
		}
		

		calipsoService.updateHistory(history);
		calipsoService.updateItem(item, history.getLoggedBy(), false);
		
		return null;
	}

	/**
	 * @param calipsoService
	 * @param history
	 * @param item
	 * @param comment
	 */
	private void reject(CalipsoService calipsoService, History history,
			Item item, String comment) {

		logger.debug("Rejecting item, reason: "+comment);
		String htmlComment = new StringBuffer("<p>[<span class='red'>").append(comment).append("</span>]</p>").toString();

		history.setHtmlComment(history.getHtmlComment()+htmlComment);
		history.setDetail(history.getDetail()+comment);
		item.setHtmlDetail(item.getHtmlDetail()+htmlComment);
		item.setDetail(item.getDetail()+comment);
		
		Integer status = item.getSpace().getMetadata().getStateByName("Απορρίφθηκε");
		history.setStatus(status);
		item.setStatus(status);

		if(logger.isDebugEnabled()){
			logger.debug("Updated Item status to 'Rejected'");
		}
	}

}
