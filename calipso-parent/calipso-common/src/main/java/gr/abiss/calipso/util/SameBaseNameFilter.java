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

package gr.abiss.calipso.util;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.log4j.Logger;

/**
 *
 */
public class SameBaseNameFilter implements FilenameFilter {
	
	protected static final Logger logger = Logger.getLogger(SameBaseNameFilter.class);
	/**
	 * The base (i.e. without using the extension) name to filter upon
	 */
	private String baseName = null;
	
	/**
	 * 
	 * @param filename the filename to use for constructing the base name by removing the extention
	 */
	public SameBaseNameFilter(String filename){
		baseName = AttachmentUtils.getBaseName(filename);
		if(logger.isDebugEnabled()){
			logger.debug("Using base name: "+baseName);
		}
	}
	


	/**
	 * Accept only if the base names match the one created by the constructor. Case insensitive.
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File file, String name) {
		boolean accept = false;
		logger.debug("Comparing '"+this.baseName+"' with '"+AttachmentUtils.getBaseName(name)+"': "+this.baseName.equalsIgnoreCase(AttachmentUtils.getBaseName(name)));
		if(this.baseName.equalsIgnoreCase(AttachmentUtils.getBaseName(name))){
			accept = true;
		}
		return accept;
	}

}
