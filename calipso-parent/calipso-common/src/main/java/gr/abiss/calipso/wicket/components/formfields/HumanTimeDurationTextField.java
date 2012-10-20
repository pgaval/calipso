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

package gr.abiss.calipso.wicket.components.formfields;


import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

/** 
 * Handles HumanTime input, i.e. a string like "2d 5h"
 */
public class HumanTimeDurationTextField extends TextField {

	private static final long serialVersionUID = 1L;

	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(HumanTimeDurationTextField.class);
			
	/**
	 * The converter for the HumanTimeDurationTextField
	 */
	private IConverter  converter = null;
	
	/**
	 * Creates a new HumanTimeDurationTextField
	 * @param id
	 *            The id of the text field
	 * 
	 */
	public HumanTimeDurationTextField(String id){
		this(id, null);
	}
	
	/**
	 * Creates a new HumanTimeDurationTextField
	 * 
	 * @param id
	 *            The id of the text field
	 * @param model
	 *            The model
	 * 
	 * @see org.apache.wicket.markup.html.form.TextField
	 */
	public HumanTimeDurationTextField(String id, IModel model){
		super(id, model);
		// the converter needs the localizer to perform string replacements
		this.converter = new HumanTimeDurationConverter(this);
	}
	

	/**
	 * Returns the default converter
	 * @param type
	 *            The type for which the convertor should work
	 * 
	 * @return A HumanTimeDurationConverter converter
	 */
	public IConverter getConverter(Class type){
		return this.converter;
	}
	
}