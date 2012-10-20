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
 * 
 * This file incorporates work released by the JTrac project and  covered 
 * by the following copyright and permission notice:  
 * 
 *   Copyright 2002-2005 the original author or authors.
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *   
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package gr.abiss.calipso.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

/**
 * Date Formatting helper, currently date formats are hard-coded for the entire app
 * hence the use of static SimpleDateFormat instances, although they are known not to be synchronized
 */
public class DateUtils {
	protected static final Logger logger = Logger.getLogger(DateUtils.class);

//    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//    private static Format dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static SimpleDateFormat dateTimeFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    public static String formatForFileName(Date date) {
        return date == null ? "" : fileNameDateFormat.format(date);
    }
    public static String formatForFileName() {
        return formatForFileName(new Date());
    }
    public static String format(Date date) {
        return date == null ? "" : dateFormat.format(date);
    }

    public static String formatTimeStamp(Date date) {
        return date == null ? "" : dateTimeFormat.format(date);
    }
    public static String formatTimeStampYearFirst(Date date) {
        return date == null ? "" : dateTimeFormat2.format(date);
    }

    public static Date convert(String s) {
    	if (s==null || (s!=null && s.trim()=="")){
    		return null;
    	}

        try {
        	if(s.contains("-")){
        		s = s.replaceAll("-", "/");
        	}
        	// we are leaking string conversions somewhere, 
        	// meaning java.text.ParseException: Unparseable date: "2011-12-14 00:00:00.0"
        	if(s.contains(".")){
        		s = s.substring(0, s.indexOf("."));
            	//logger.info("Date string to convert: "+s);
        		return dateTimeFormat2.parse(s);
        	}
            return dateFormat.parse(s);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Convert the time to the midnight of the given date or 
     * now if null. 
     * @return the date representing midnight for the given date or 
     * today if null. 
     */
    public static Date toMidnight(Date date) {
    	Calendar cal = toMidnightCalendar(date);
        return cal.getTime();
    }   
    
    /**
     * Convert the time to the midnight of the given date or 
     * now if null. 
     *
     * @return the calendar representing midnight for the given date or 
     * today if null. 
     */
    public static Calendar toMidnightCalendar(Date date) {
    	Calendar cal = Calendar.getInstance();
    	if(date != null){
    		cal.setTime(date);
    	}
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND,0);
        return cal;
    }
    
    public static int getYearDifference(GregorianCalendar fromCalendar, GregorianCalendar toCalendar) {
    	int count = 0;
    	for(fromCalendar.add(Calendar.YEAR, 1); fromCalendar.compareTo(toCalendar) <= 0; fromCalendar.add(
    	Calendar.YEAR, 1)) {
    	count++;
    	}
    	return count;
    }

	public static int getMonthDifference(Date from, Date to) {
		Calendar fromCalendar = toMidnightCalendar(from);
		Calendar toCalendar = toMidnightCalendar(to);
		return getMonthDifference(fromCalendar, toCalendar);
	}
	
	public static int getMonthDifference(Calendar fromCalendar, Calendar toCalendar) {
		int count = 0;
		for(fromCalendar.add(Calendar.MONTH, 1); fromCalendar.compareTo(toCalendar) <= 0; fromCalendar.add(
			Calendar.MONTH, 1)) {
			count++;
		}
		return count;
	}

}