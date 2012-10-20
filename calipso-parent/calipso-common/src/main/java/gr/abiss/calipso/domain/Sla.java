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


public class Sla implements Serializable{

	private int slaId;
	private String slaCode;
	private Date startDate;
	private Date endDate;
	private String notes;
	private Double responseTime;
	
	public Sla() {
	}

	public Sla(int slaId, String slaCode, Date startDate, Date endDate, String notes, Double responseTime) {
		this.slaId = slaId;
		this.slaCode = slaCode;
		this.startDate = startDate;
		this.endDate = endDate;
		this.notes = notes;
		this.responseTime = responseTime;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Double getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(Double responseTime) {
		this.responseTime = responseTime;
	}

	public String getSlaCode() {
		return slaCode;
	}

	public void setSlaCode(String slaCode) {
		this.slaCode = slaCode;
	}

	public int getSlaId() {
		return slaId;
	}

	public void setSlaId(int slaId) {
		this.slaId = slaId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
}
