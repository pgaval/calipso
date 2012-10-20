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

package gr.abiss.calipso.wicket.components.validators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

import org.apache.log4j.Logger;

/**
 * Custom validator to check the validity of uploaded files according to
 * Calipso configuration.
 * 
 */
public class ValidExtensionFileUploadValidator extends AbstractValidator {

	private static final long serialVersionUID = 1L;
	
	protected static final Logger logger = Logger.getLogger(ValidExtensionFileUploadValidator.class);

	private List<String> extensions = null;

	/**
	 * Whitespace-separated list of file extension, 
	 * e.g. <code>getJtrac().loadConfig("attachment.extentionsAllowed")</code>
	 * @param extentionsString
	 */
	public ValidExtensionFileUploadValidator(String extentionsString){
		if(extentionsString != null && extentionsString.length() > 0){
			String[] extents = extentionsString.toUpperCase().replaceAll("\\.", "").replaceAll("\\*", "").split(" ");
			this.extensions = new ArrayList<String>(extents.length);
		}
		if(this.extensions == null || this.extensions.size() == 0){
			logger.error("ValidExtensionFileUploadValidator will not validate file extentions as it was initialized to an empty whiteList using ["+extentionsString+"]");
		}
	}

	/**
	 * List of file extension strings without the dot.
	 * @param extentions
	 */
	public ValidExtensionFileUploadValidator(List<String> extentions){
		if(extentions != null && extentions.size() > 0){
			this.extensions = new ArrayList<String>(extentions.size());
			Iterator<String> iter = extentions.iterator();
			while(iter.hasNext()){
				this.extensions.add(iter.next().toUpperCase());
			}
		}
		else{
			logger.error("ValidExtensionFileUploadValidator will not validate file extentions as it was initialized with an empty whiteList");
		}
	}
	
	
	/**
	 * Always returns true, otherwise validation wont work
	 */
	public boolean validateOnNullValue() {
		return true;
	}

	/**
	 * 
	 */
	protected void onValidate(IValidatable validatable) {
		boolean isValid = false;
		String fileName = null;
		try{
			FileUpload fileUpload = (FileUpload) validatable.getValue();
			fileName = fileUpload.getClientFileName();
			String fileExtention = fileName.substring(fileName.lastIndexOf('.'));
			isValid = this.extensions.contains(fileExtention.toUpperCase());
		}
		catch(Throwable e){
			logger.error("Could not validate extention for filename: "+fileName);
			
		}

		if(!isValid){
			error(validatable);
		}
		
	}


}
