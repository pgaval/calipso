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

import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * @author marcello
 */
public class CalipsoErrorPage extends BasePage {
	
	private static final long serialVersionUID = 1L;

	public CalipsoErrorPage(){
		add(new WebMarkupContainer("errorMsg").setVisible(false));
		this.setVersioned(true);
	}
	
	public CalipsoErrorPage(RuntimeException e) {
		add(new WebMarkupContainer("errorMsg").setVisible(false));
//		add(new Label("errorMsg", e.getMessage() + " --- " + 
//				e.getCause().getMessage() + " --- " +
//				e.getCause() + " --- " + 
//				e.getLocalizedMessage()));
	}
}

