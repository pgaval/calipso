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

package gr.abiss.calipso.wicket.fileUpload;

import gr.abiss.calipso.wicket.BasePanel;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.wicket.util.file.Files;

/**
 *
 */
public abstract class FileUtils {
	protected static final Logger logger = Logger.getLogger(FileUtils.class);
	
	
	/**
	 * Deletes all the files in the given directory.
	 * @param dir The directory that you want to delete.
	 * @exception IOException
	 */
	public static void deleteAllFilesInTempDir(File dir) throws IOException {
		if (dir.isDirectory()) {
			for (File child : dir.listFiles()) {
				try {
					Files.remove(child);
				} catch (Exception e) {
					throw new IOException(
							"Error trying to delete files in directory "
									+ dir.getName() + ".Error " + e);
				}
			}
			logger.info("Success deleting temporary directory " + dir.getName());
		}
	}
}
