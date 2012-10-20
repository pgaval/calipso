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

package gr.abiss.calipso.wicket.components.renderers;

import gr.abiss.calipso.domain.IUser;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 * Should be used to render user dropdown (i.e. HTML select) elements
 * throughout the application.
 *
 */
public class UserChoiceRenderer implements IChoiceRenderer {
	
	private static final long serialVersionUID = 1L;
	
	public Object getDisplayValue(Object o) {
		IUser u = (IUser) o;
    	StringBuffer s = new StringBuffer()
			.append(u.getName())
			.append(" ")
			.append(u.getLastname())
			.append(" (")
			.append(u.getLoginName())
			.append(")");
    	if(u.getOrganization() != null){
    		s.append(", ")
    			.append(u.getOrganization().getName());
    	}
        return s.toString();
    }
    
    public String getIdValue(Object o, int i) {
        return String.valueOf(i);
    }
}