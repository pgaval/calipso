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

package gr.abiss.calipso.domain;

import java.io.Serializable;

public class Effort implements Serializable{
	
	private int days;
	private int hours;
	private int minutes;
	
	public Effort() {
		this.days = 0;
		this.hours = 0;
		this.minutes = 0;
	}

	public Effort(int days, int hours, int minutes) {
		this.days = days;
		this.hours = hours;
		this.minutes = minutes;
	}

	public Effort(Integer minutes) {
		if (minutes==null){
			this.days = this.hours = this.minutes = 0;
		}
		else{
			this.days = minutes.intValue() / 1440;
			minutes = minutes.intValue() - (this.days * 1440);
			this.hours = minutes.intValue() / 60;
			this.minutes = minutes.intValue() - (this.hours * 60);
		}
	}

	public Effort(Double minutes) {
		this(new Integer(minutes.intValue()));
	}
	
	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	
	public Integer getEffortInMinutes(){
		return (this.days * 24 * 60) + (this.hours * 60) + this.minutes; 
	}

	public Double getEffortInMinutesAsDouble(){
		return new Double(this.getEffortInMinutes());
	}
	
	public String formatEffort(String daysLabel, String hoursLabel, String minutesLabel){
		StringBuffer effort = new StringBuffer();
		
		effort.append(this.days).append(" ").append(daysLabel).append(" ")
			.append(this.hours).append(" ").append(hoursLabel).append(" ")
			.append(this.minutes).append(" ").append(minutesLabel);
		
		return effort.toString();
	}
}
