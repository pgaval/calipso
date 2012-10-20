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
 * 
 * This file incorporates work released by the JTrac project and  covered 
 * by the following copyright and permission notice:  
 * 
 *   Copyright 2002-2005 the original author or authors.
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *   
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package gr.abiss.calipso.wicket;

import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.Field.Name;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;

/**
 * Common functionality for ItemFormPanel (create Item) and ItemViewPanel (create History for Item)
 */
public abstract class AbstractItemFormPanel extends BasePanel {
	private static final Logger logger = Logger.getLogger(AbstractItemFormPanel.class);

	public static final String SIMPLE_ATTACHEMENT_KEY = Name.SIMPLE_ATTACHEMENT.getText();
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param id
	 */
	public AbstractItemFormPanel(String id) {
		super(id);
	}
	public AbstractItemFormPanel(String id, IBreadCrumbModel breadCrumbModel){
		super(id, breadCrumbModel);
	}

	// used while copnstructing/rendering fields for the user 
	protected Map<String,FileUploadField> fileUploadFields = new HashMap<String,FileUploadField>();
	//private FileUploadField fileUploadField = new FileUploadField("file"); 
	
	protected Map<String, FileUpload> getNonNullUploads(){
		// only keeps the non-null file uploads
		// TODO: add number suffix to simple attachment keys, enable multiple file uploads
		Map<String,FileUpload> fileUploads = new HashMap<String,FileUpload>();
		for(String fileUploadKey : fileUploadFields.keySet()){
			FileUploadField fileUploadField = fileUploadFields.get(fileUploadKey);
			FileUpload fileUpload = fileUploadField.getFileUpload();
			if(fileUploadKey.equalsIgnoreCase(Field.FIELD_TYPE_SIMPLE_ATTACHEMENT)){
				fileUploadKey = Field.FIELD_TYPE_SIMPLE_ATTACHEMENT;
			}
			logger.debug("Upload Field "+fileUploadKey+ " FileUpload: "+fileUpload);
			if(fileUpload != null && fileUpload.getSize() > 0){
				fileUploads.put(fileUploadKey, fileUpload);
			}
		}
		logger.debug("Returning fileUploads: "+fileUploads);
		return fileUploads;
	}
		

}