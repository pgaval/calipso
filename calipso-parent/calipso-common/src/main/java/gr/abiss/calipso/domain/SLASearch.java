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
import java.util.Date;

public class SLASearch implements Serializable{
	private static final long serialVersionUID = 1L;
	private String slaCode;
	private Date fromStartDate; 
	private Date toStartDate;
	private Date fromEndDate;
	private Date toEndDate;
	private String notes;
	private String fromResponseTime;
	private String toResponseTime;
	
	public SLASearch() {
	}

	public Date getFromEndDate() {
		return fromEndDate;
	}

	public void setFromEndDate(Date fromEndDate) {
		this.fromEndDate = fromEndDate;
	}

	public String getFromResponseTime() {
		return fromResponseTime;
	}

	public void setFromResponseTime(String fromResponseTime) {
		this.fromResponseTime = fromResponseTime;
	}

	public Date getFromStartDate() {
		return fromStartDate;
	}

	public void setFromStartDate(Date fromStartDate) {
		this.fromStartDate = fromStartDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getSlaCode() {
		return slaCode;
	}

	public void setSlaCode(String slaCode) {
		this.slaCode = slaCode;
	}

	public Date getToEndDate() {
		return toEndDate;
	}

	public void setToEndDate(Date toEndDate) {
		this.toEndDate = toEndDate;
	}

	public String getToResponseTime() {
		return toResponseTime;
	}

	public void setToResponseTime(String toResponseTime) {
		this.toResponseTime = toResponseTime;
	}

	public Date getToStartDate() {
		return toStartDate;
	}

	public void setToStartDate(Date toStartDate) {
		this.toStartDate = toStartDate;
	}
	
}//SearchSLA