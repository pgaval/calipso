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

import java.util.Date;

public class MailedItem {

	private String summary;
	private String detail;
	private String space;
	private String loggedByAccount;
	private Date timeStamp;

	// --------------------------------------------------------------------------------------------

	public MailedItem() {
		
	}

	// --------------------------------------------------------------------------------------------

	public MailedItem(String summary, String detail, String space,
			String spaceDescription, String loggedByAccount, Date timeStamp) {
		this.summary = summary;
		this.detail = detail;
		this.space = space;
		this.loggedByAccount = loggedByAccount;
		this.timeStamp = timeStamp;
	}

	// --------------------------------------------------------------------------------------------

	public String getSummary() {
		return summary;
	}

	// --------------------------------------------------------------------------------------------
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	// --------------------------------------------------------------------------------------------

	public String getDetail() {
		return detail.replace("\n", "<br>");
	}

	// --------------------------------------------------------------------------------------------
	
	public void setDetail(String detail) {
		this.detail = detail;
	}

	// --------------------------------------------------------------------------------------------
	
	public String getSpace() {
		return space;
	}

	// --------------------------------------------------------------------------------------------

	public void setSpace(String space) {
		this.space = space;
	}

	// --------------------------------------------------------------------------------------------
	
	public String getLoggedByAccount() {
		return loggedByAccount;
	}

	// --------------------------------------------------------------------------------------------
	
	public void setLoggedByAccount(String loggedByAccount) {
		this.loggedByAccount = loggedByAccount;
	}
	
	// --------------------------------------------------------------------------------------------
	
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	// --------------------------------------------------------------------------------------------
	
	public Date getTimeStamp() {
		return timeStamp;
	}
}
