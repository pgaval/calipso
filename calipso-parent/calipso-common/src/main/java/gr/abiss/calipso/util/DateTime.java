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

import java.util.Calendar;
import java.util.Date;

public class DateTime implements Cloneable{
	private Long _year;
	private Long _month;
	private Long _day;
	
	private Long _hours;
	private Long _minutes;
	private Long _seconds;

	///////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Created DateTime: 0/0/0000 00:00:00
	 * */
	
	public DateTime(){
		_day = new Long (0);
		_month = new Long(0);
		_year = new Long (0);
		
		_hours = new Long(0);
		_minutes = new Long(0);
		_seconds = new Long(0);		
	}//DateTime
	//	----------------------------------------------------------------------------------
	
	public DateTime(Long day, Long month, Long year, Long hours, Long minutes, Long seconds){
		_day = day;
		_month = month;
		_year = year;
		
		_hours = hours;
		_minutes = minutes;
		_seconds = seconds;
	}//DateTime
	
	//----------------------------------------------------------------------------------
	
	public DateTime (Date dateTime){
		Calendar date = Calendar.getInstance();
		date.setTime(dateTime);
		
		_day = new Long (date.get(Calendar.DAY_OF_MONTH));
		_month = new Long (date.get(Calendar.MONTH)+1);
		_year = new Long (date.get(Calendar.YEAR));
		
		_hours = new Long (date.get(Calendar.HOUR_OF_DAY));
		_minutes = new Long (date.get(Calendar.MINUTE));
		_seconds = new Long (date.get(Calendar.SECOND));
		
	}//DateTime
	
	//	----------------------------------------------------------------------------------
	
	/**
	 * DateTime Format: dd/mm/yyyy hh:mm:ss
	 * */
	public DateTime(String dateTime){
		
		String parts[] = dateTime.split(" ");
		String[] dateParts = null;
		String[] timeParts = null;
		
		if (parts.length==2){
			dateParts = parts[0].split("/");
			timeParts = parts[1].split(":");
			if (dateParts.length==3 && timeParts.length==3){
				_day = Long.parseLong(dateParts[0]);
				_month = Long.parseLong(dateParts[1]);
				_year = Long.parseLong(dateParts[2]);
				
				_hours = Long.parseLong(timeParts[0]);
				_minutes = Long.parseLong(timeParts[1]);
				_seconds = Long.parseLong(timeParts[2]);
			}//if
		}//if
		
	}//DateTime
	
	///////////////////////////////////////////////////////////////////////////////////////
	
	
	public Object clone() {
		DateTime dateTime =  null;
		try{
			dateTime = (DateTime)super.clone();
		}//try
		catch (CloneNotSupportedException e){
			throw new InternalError(e.getMessage());			
		}//try
		
		return dateTime; 

	}//clone
	
	/////////////////////////////////////////////////////////////////////////////////////////
	
	public Long get_day() {
		return _day;
	}//get_day
	
	//	----------------------------------------------------------------------------------
	
	public Long get_hours() {
		return _hours;
	}//get_hours
	//	----------------------------------------------------------------------------------
	
	public Long get_minutes() {
		return _minutes;
	}//get_minutes
	
	//	----------------------------------------------------------------------------------
	
	public Long get_month() {
		return _month;
	}//get_month
	
	//	----------------------------------------------------------------------------------

	public Long get_seconds() {
		return _seconds;
	}//get_seconds
	
	//	----------------------------------------------------------------------------------
	
	public Long get_year() {
		return _year;
	}//get_year
	
	//	----------------------------------------------------------------------------------
	
	public void set_day(Long day) {
		_day = day;
	}//set_day
	
	//	----------------------------------------------------------------------------------
	
	public void set_hours(Long hours) {
		_hours = hours;
	}//set_hours
	
	//	----------------------------------------------------------------------------------

	public void set_minutes(Long minutes) {
		_minutes = minutes;
	}//set_minutes
	
	//	----------------------------------------------------------------------------------
	
	public void set_month(Long month) {
		_month = month;
	}//set_month
	
	//	----------------------------------------------------------------------------------
	
	public void set_seconds(Long seconds) {
		_seconds = seconds;
	}//set_seconds
	
	//	----------------------------------------------------------------------------------
	
	public void set_year(Long year) {
		_year = year;
	}//set_year
	
	//	----------------------------------------------------------------------------------
	
	public Integer getDaysOfMonth(){
		
		int daysOfMonth = 0;
		int month = Integer.parseInt(_month.toString());
		
		switch(month){
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				daysOfMonth = 31;
			break;
			case 4:
			case 6:
			case 9:
			case 11:
				daysOfMonth = 30;
			break;
			case 2:
				if ((_year%4==0) || (_year%1000==0)){
					daysOfMonth = 29;
				}//if
				daysOfMonth = 28;
		}//switch
		
		return daysOfMonth;
		
	}//getDaysOfMonth
	
	//	----------------------------------------------------------------------------------
	
	public long inSeconds(){
		long seconds = 0;

		seconds = (_year * 12 * 30 * 86400) + 
							(_month * 30 * 86400) + 
							(_day * 86400) +
							(_hours * 3600) +
							(_minutes * 60) +
							_seconds;
		return seconds;

	}//inSeconds

	//	----------------------------------------------------------------------------------

	public String toString() {
		StringBuffer s = new StringBuffer("");

		s.append(_year).append(" years ")
			.append(_month).append(" months ")
			.append(_day).append(" days ")
			.append(_hours).append(" hours ")
			.append(_minutes).append(" minutes ")
			.append(_seconds).append(" seconds");

		return s.toString();

	}//toString

	///////////////////////////////////////////////////////////////////////////////////////

	public static DateTime diff(DateTime dt1, DateTime dt2){
		DateTime rv = new DateTime();

		DateTime dtTemp = (DateTime)dt2.clone();

		//Justify Seconds
		if (dtTemp.get_seconds()<dt1.get_seconds()){
			dtTemp.set_seconds(60 + dtTemp.get_seconds());
			dtTemp.set_minutes(dtTemp.get_minutes()-1);
		}//if

		//Justify Minutes
		if(dtTemp.get_minutes()<dt1.get_minutes()){
			dtTemp.set_minutes(60 + dtTemp.get_minutes());
			dtTemp.set_hours(dtTemp.get_hours()-1);
		}//if
		
		//Justify Hour
		if (dtTemp.get_hours()<dt1.get_hours()){
			dtTemp.set_hours(24 + dtTemp.get_hours());
			dtTemp.set_day(dtTemp.get_day()-1);
		}//if

		//Justify Day
		if (dtTemp.get_day()<dt1.get_day()){
			dtTemp.set_day(dtTemp.getDaysOfMonth()+dtTemp.get_day());
			dtTemp.set_month(dtTemp.get_month()-1);
		}//if

		//Justify Month
		if (dtTemp.get_month()<dt1.get_month()){
			dtTemp.set_month(12 + dtTemp.get_month());
			dtTemp.set_year(dtTemp.get_year()-1);
		}//if
		
		
		rv.set_seconds(dtTemp.get_seconds() - dt1.get_seconds());
		rv.set_minutes(dtTemp.get_minutes() - dt1.get_minutes());
		rv.set_hours(dtTemp.get_hours() - dt1.get_hours());
		
		rv.set_day(dtTemp.get_day() - dt1.get_day());
		rv.set_month(dtTemp.get_month() - dt1.get_month());
		rv.set_year(dtTemp.get_year() - dt1.get_year());
		if (rv.get_year()<0){
			rv.set_year(new Long(0));
		}//if
		
		return rv;
		
	}//diff
	
	// ----------------------------------------------------------------------------------
	
	public static DateTime max(DateTime[] dateTimeList){
		DateTime maxDateTime = new DateTime("0/0/0000 00:00:00");
		long maxTime = 0;
		int ind = 0;
		
		if (dateTimeList.length>0){
			for (int i=0; i<dateTimeList.length; i++){
				if (dateTimeList[i].inSeconds()>maxTime){
					maxTime = dateTimeList[i].inSeconds();
					ind = i;
				}//if
			}//for
		}//if
		
		maxDateTime = dateTimeList[ind];
		return maxDateTime;
	}//max

	// ----------------------------------------------------------------------------------
	
	public static DateTime min(DateTime[] dateTimeList){
		DateTime minDateTime = new DateTime("0/0/0000 00:00:00");
		long minTime = 0;
		int ind = 0;
		
		if (dateTimeList.length>0){
			minTime = dateTimeList[0].inSeconds();
			for (int i=1; i<dateTimeList.length; i++){
				if (dateTimeList[i].inSeconds()<minTime){
					minTime = dateTimeList[i].inSeconds();
					ind = i;
				}//if
			}//for
		}//if
		
		minDateTime = dateTimeList[ind];
		
		return minDateTime;
		
	}//min

//	----------------------------------------------------------------------------------

	public static DateTime avg(DateTime[] dateTimeList){
		DateTime avgDateTime = new DateTime("0/0/0000 00:00:00");
		
		if (dateTimeList.length==0){
			return avgDateTime;
		}//if
		
		long secs = 0;
		long avg = 0;
		
		for (int i=0; i<dateTimeList.length; i++){
			secs += dateTimeList[i].inSeconds();
		}//for
		
		avg = (secs / dateTimeList.length);
		
		long mo=0; long d=0; long h=0; long m=0; long s=0;
		
		m = (avg / 60);
		s = avg % 60;
		
		if (m>60){
			h = (m / 60);
			m = m % 60;
		}//if
		
		if (h>24){
			d = (h / 24);
			h = h % 24;
		}//if
		
		if(d>30){
			mo = d / 30;
			d = d % 30;
		}//if

		avgDateTime.set_seconds((Long)s);
		avgDateTime.set_minutes((Long)m);
		avgDateTime.set_hours((Long)h);
		
		avgDateTime.set_day((Long)d);
		avgDateTime.set_month((Long)mo);
		
		return avgDateTime;
	}//avg
	
	//----------------------------------------------------------------------------------
	
	public static DateTime fromSeconds(Long seconds){
		
		
		long days = seconds / 86400;
		seconds = seconds-(days*86400);
		
		long hour = seconds/3600;
		seconds = seconds-(hour*3600);
		
		long min = seconds/60;
		seconds = seconds-(min*60);
		
		long secs = seconds;

		DateTime dateTime = new DateTime("0/0/0000 00:00:00");
		
		dateTime.set_day(days);
		dateTime.set_hours(hour);
		dateTime.set_minutes(min);
		dateTime.set_seconds(secs);
		
		return dateTime;
		
	}//fromSeconds
	
	//----------------------------------------------------------------------------------
	
	public static DateTime fromSeconds(Double seconds){
		
		//return DateTime.fromSeconds(Long.parseLong(seconds.toString()));
		return DateTime.fromSeconds(seconds.longValue());
	}
}//DateTime