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
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.plugins.state.AbstractStatePlugin;
import gr.abiss.calipso.plugins.state.CopyAssetInfoToItemPlugin;
import gr.abiss.calipso.util.DateUtils;
import gr.abiss.calipso.util.XmlUtils;
import gr.abiss.calipso.wicket.CalipsoSession;

/**
 * @author manos
 *
 */
public class ExampleEp1BusinessLogicPlugin extends AbstractStatePlugin {
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(ExampleEp1BusinessLogicPlugin.class);

	public ExampleEp1BusinessLogicPlugin(){
		super();
		logger.debug("constructor called");
	}
	
	/**
	 * @see gr.abiss.calipso.plugins.state.AbstractStatePlugin#execute(gr.abiss.calipso.CalipsoService, gr.abiss.calipso.domain.History)
	 */
	@Override
	public Serializable executePostStateChange(CalipsoService calipsoService, History history) {

		logger.debug("execute called");
		// now move on with checks and calculations
		Item item = history.getParent();
		// if the first option is selected, i.e. "active"
		
		if(item.getCusInt03().intValue() != 1){
			String comment = "\nΗ Μερίδα ΕΠ είναι ανενεργή (The GoO Account is blocked).";
			addRejectionCause(calipsoService, history, item, comment);
		}
		
		
		// update cusTim05
		item.setCusTim05(item.getCusTim01());
		history.setCusTim05(item.getCusTim01());
		// update cusDbl11 period
		Calendar endPeriod = Calendar.getInstance();
		endPeriod.setTime(item.getCusTim02());
		endPeriod.add(Calendar.DATE, 1);
		
		int monthDiff = DateUtils.getMonthDifference(item.getCusTim01(), endPeriod.getTime());
		if(monthDiff <= 0 || item.getCusTim01().compareTo(item.getCusTim02()) > 0){
			String comment = "\nΗ ημερομηνία έναρξης περιόδου παραγωγής στην οποία αντιστοιχεί η παρούσα εγγύηση προέλευσης, πρέπει να είναι προγενέστερη της ημερομηνίας λήξης της (Production start date corresponding to this GoO must be earlier than its production end date).";
			addRejectionCause(calipsoService, history, item, comment);
		}
		Double cusDbl11 = new Double(monthDiff);
		item.setCusDbl11(cusDbl11);
		history.setCusDbl11(cusDbl11);
		
		
		// update cusTim03
		Date now = item.getTimeStamp();
		Calendar calendarCusTim02 = Calendar.getInstance();
		calendarCusTim02.setTime(item.getCusTim02());
		calendarCusTim02.add(Calendar.DATE, 30);
		item.setCusTim03(calendarCusTim02.getTime());
		history.setCusTim03(calendarCusTim02.getTime());
		
		// reset and go for cusTim04
		calendarCusTim02.setTime(item.getCusTim02());
		calendarCusTim02.add(Calendar.YEAR, 1);
		item.setCusTim04(calendarCusTim02.getTime());
		history.setCusTim04(calendarCusTim02.getTime());
		
		// check if the item was submitted during the allowed period
		// cusTim04 >= timeStamp >= cusTim03
		if(!(item.getCusTim04().compareTo(now) >= 0 && item.getCusTim03().compareTo(now) <= 0)){
			String comment = "\nΤo αίτημα δεν είναι εμπρόθεσμο (The submission date is not within the valid submission period).";
			addRejectionCause(calipsoService, history, item, comment);
		}
		
		// update cusTim06
		item.setCusTim06(calendarCusTim02.getTime());
		history.setCusTim06(calendarCusTim02.getTime());
		
		markRejectedIfErrors(history, item);
		
		calipsoService.updateHistory(history);
		calipsoService.updateItem(item, history.getLoggedBy(), false);
		return null;
	}


}
