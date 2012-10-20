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

import gr.abiss.calipso.domain.State;

import java.io.Serializable;
import java.util.Map;

import org.apache.wicket.util.template.JavaScriptTemplate;
import org.apache.wicket.util.template.TextTemplate;

/**
 * Contains some useful javascripts.
 * */

public class JavaScripts implements Serializable{
	
	/*
	 * Call from ItemViewFormPanel
	 * */
	public TextTemplate enableAssignableSpacesDropDownChoice = new TextTemplate(){

		@Override
		public String getString() {
			StringBuffer javaScript = new StringBuffer("");
			javaScript
			.append("var assignableSpacesId;").append("\n")
			.append("function enableAssignableSpacesDropDownChoice(statusElementId){").append("\n")
//			.append("	document.getElementById(assignableSpacesId).disabled = !(document.getElementById(statusElementId).value=='" + String.valueOf(State.MOVE_TO_OTHER_SPACE) + "');").append("\n")
			.append("}//enableAssignableSpacesDropDownChoice");

			return javaScript.toString();
		}
		
		//-------------------------------------------------------------------
		
		@Override
		public TextTemplate interpolate(Map arg0) {
			return null;
		}
		
	};

	/**********************************************************************************************************************/
	

	/*
	 * Call from CustomFieldsFormPanel
	 * */
	
	public String assignableSpacesId = "";
	
	public TextTemplate populateAssignableSpacesId = new TextTemplate(){

		public String getString() {
			StringBuffer javaScript = new StringBuffer("");
			javaScript
			.append("var AssignableSpacesId = ").append("'").append(assignableSpacesId).append("'").append(";");
			return javaScript.toString();
		}//getString
		
		@Override
		public TextTemplate interpolate(Map arg0) {
			return null;
		}
	};
	
	//---------------------------------------------------------------
	
	public TextTemplate setAssignableSpacesId = new TextTemplate(){

		public String getString() {
			StringBuffer javaScript = new StringBuffer("");
			javaScript
			.append("function setAssignableSpacesId(asId){").append("\n")
			.append("	assignableSpacesId=asId;").append("\n")
			.append("}").append("\n");
			
			
			return javaScript.toString();
		}//getString
		
		@Override
		public TextTemplate interpolate(Map arg0) {
			return null;
		}
	};
}