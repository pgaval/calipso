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
public class ExampleEp2BusinessLogicPlugin extends AbstractStatePlugin {
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(ExampleEp2BusinessLogicPlugin.class);
	

	public ExampleEp2BusinessLogicPlugin(){
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
		
		// update cusDbl07 = cusDbl05 – cusDbl06
		Double cusDbl07 = item.getCusDbl05().doubleValue() - item.getCusDbl06().doubleValue();
		item.setCusDbl07(cusDbl07);
		history.setCusDbl07(cusDbl07);
		
		// cusDbl08 = cusDbl07 / 1MWh (i.e. floor to integer)
		Double cusDbl08 = new Double((int) cusDbl07.doubleValue());
		if(cusDbl08.doubleValue() < 1){
			String comment = "\n H Παραχθείσα Ενέργεια πρέπει να είναι τουλάχιστον 1MWh (The Produced Power must be at least 1MWh).";
			addRejectionCause(calipsoService, history, item, comment);
		}
		item.setCusDbl08(cusDbl08);
		history.setCusDbl08(cusDbl08);
		
		// cusDbl09 = cusDbl08 – cusDbl01
		Double cusDbl09 = new Double(item.getCusDbl08().doubleValue() - item.getCusDbl01().doubleValue());
		item.setCusDbl09(cusDbl09);
		history.setCusDbl09(cusDbl09);
		
		// cusDbl08 >= cusDbl01
		if(cusDbl08 < item.getCusDbl01().doubleValue()){
			String comment = "\n Ο αριθμός των αιτούμενων προς έκδοση ΕΠ πρέπει να είναι μικρότερος ή ίσος με αυτόν των διαθέσιμων βάση παραγωγής και προηγούμενων εκδώσεων (The requested number of GoO must be lower than or equal to the number of avalable GoOs, which is based on the production and the GoOs already issued).";
			addRejectionCause(calipsoService, history, item, comment);
		}

		markRejectedIfErrors(history, item);
		
		calipsoService.updateHistory(history);
		calipsoService.updateItem(item, history.getLoggedBy(), false);
		
		return null;
	}


}
