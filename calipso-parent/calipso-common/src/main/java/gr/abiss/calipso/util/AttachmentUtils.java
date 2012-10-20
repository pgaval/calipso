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

package gr.abiss.calipso.util;

import gr.abiss.calipso.domain.Attachment;
import gr.abiss.calipso.domain.Config;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.IAttachmentOwner;
import gr.abiss.calipso.domain.Item;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.tools.ant.util.FileUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Utils that deal with Attachments, upload / download and file name String
 * manipulation
 */
public class AttachmentUtils {

	protected static final Logger logger = Logger
			.getLogger(AttachmentUtils.class);

	public static String cleanFileName(String path) {
		// the client browser could be on Unix or Windows, we don't know
		int index = path.lastIndexOf('/');
		if (index == -1) {
			index = path.lastIndexOf('\\');
		}
		return (index != -1 ? path.substring(index + 1) : path);
	}

	/*
	 * public static File getFile(Attachment attachment, String home) {
	 * StringBuffer buff = new StringBuffer(); buff.append(home);
	 * buff.append(File.separator); buff.append("attachments");
	 * buff.append(File.separator);
	 * buff.append(attachment.getItem().getSpace().getId());
	 * buff.append(File.separator); buff.append(attachment.getItem().getId());
	 * // buff.append(File.separator); //
	 * buff.append(attachment.getHistory().getId());
	 * buff.append(File.separator); buff.append(attachment.getFileName());
	 * return new File(buff.toString()); }
	 */

	/**
	 * Get the File for the given attachment
	 * 
	 * @param attachment
	 * @param home
	 * @return
	 */
	public static File getSavedAttachmentFile(Attachment attachment, String home) {
		File newFile = new File(home + File.separator
				+ attachment.getBasePath() + File.separator
				+ attachment.getFileName());
		return newFile;
	}

	public static void makePermanentAttachmentFiles(
			Set<Attachment> attachments,
			Map<String, FileUpload> fileUploadsMap, String home) {
		if (attachments != null) {
			for (Attachment attachment : attachments) {
				if (logger.isDebugEnabled()) {
					logger.debug("Trying to save file for attachment basepath: "
							+ attachment.getBasePath()+", filename:  "+attachment.getFileName());
				}

				// the uploads map keys have no file extentions
				String uploadKey = attachment.getFileName().substring(0,
						attachment.getFileName().lastIndexOf('.'));
				FileUpload upload = fileUploadsMap.get(uploadKey);
				

				if (upload == null) {
					upload = fileUploadsMap.get(Field.FIELD_TYPE_SIMPLE_ATTACHEMENT);
				}
				if (upload != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("makePermanentAttachmentFile, attachment: "
								+ attachment + ", upload: " + upload);
					}

					makePermanentAttachmentFile(attachment, upload, home);
				}
				else{
					attachment.getHistory().removeAttachment(attachment);
					logger.warn("No uploaded file was found for attachment '"
							+ attachment.getFileName() + "' using key '"
							+ uploadKey + "', keys:");
					for(String s : fileUploadsMap.keySet()){
						logger.warn(s);
					}
				}
			}
		} else {
			logger.warn("Attachment set is null, skipping file persistence.");
		}
	}

	/**
	 * 
	 * @param attachment
	 * @param jtracHome
	 * @return
	 */
	private static File makePermanentAttachmentFile(Attachment attachment,
			FileUpload fileUpload, String home) {
		if (attachment.getItem().getSpace().getId() == 0
				|| attachment.getItem().getId() == 0
				|| attachment.getHistory().getId() == 0) {
			throw new RuntimeException(
					"Only call this AFTER persisting Item > History > Attachment and associating "
							+ "them with the current session as their IDs are required for proper save.");
		}

		buildBasePath(attachment);
		//logger.info("Created basepath: "+attachment.getBasePath());
		// make file
		File newFile = new File(home + attachment.getBasePath()
				+ attachment.getFileName());
		try {
			ensureFileExists(newFile);
			// write file
			fileUpload.writeTo(newFile);

			if (attachment.getFileName().endsWith(".png")
					|| attachment.getFileName().endsWith(".gif")
					|| attachment.getFileName().endsWith(".bmp")
					|| attachment.getFileName().endsWith(".jpeg")
					|| attachment.getFileName().endsWith(".jpg")) {

				File imageFileSmall = new File(home + File.separator
						+ attachment.getBasePath() + File.separator + "small_"
						+ attachment.getFileName());
				if (!imageFileSmall.exists()) {
					imageFileSmall.createNewFile();
				}
				File imageFileThumb = new File(home + File.separator
						+ attachment.getBasePath() + File.separator + "thumb_"
						+ attachment.getFileName());
				if (!imageFileThumb.exists()) {
					imageFileThumb.createNewFile();
				}

				// get image from upload
				BufferedImage bufferedImage = ImageIO.read(newFile);

				// generate and save small version for preview
				Thumbnail tn = new Thumbnail(bufferedImage);
				// create a big icon
				tn.getThumbnail(600);
				tn.saveThumbnail(imageFileSmall, Thumbnail.IMAGE_JPEG);
				tn = null;

				// create thumbnail from small image.
				bufferedImage = ImageIO.read(imageFileSmall);
				tn = new Thumbnail(bufferedImage);

				// create small icon
				tn.getThumbnail(100);
				tn.saveThumbnail(imageFileThumb, Thumbnail.IMAGE_JPEG);

			}
		} catch (Exception e) {
			throw new RuntimeException("Error saving icon.", e);
		}
		return newFile;
	}

	public static void buildBasePath(Attachment attachment) {
		// create file path
		StringBuffer attachmentBasePath = new StringBuffer();
		// buffer.append(home);
		attachmentBasePath.append(File.separator);
		attachmentBasePath.append("attachments");
		attachmentBasePath.append(File.separator);
		if(attachment.getSpace() != null){
			attachmentBasePath.append(attachment.getSpace().getId());
			attachmentBasePath.append(File.separator);
		}
		else if(attachment.getItem() != null && attachment.getItem().getSpace() != null){
			attachmentBasePath.append(attachment.getItem().getSpace().getId());
			attachmentBasePath.append(File.separator);
		}
		else if(attachment.getHistory() != null && attachment.getHistory().getSpace() != null){
			attachmentBasePath.append(attachment.getHistory().getSpace().getId());
			attachmentBasePath.append(File.separator);
		}
		else{
			throw new RuntimeException("Could not determine space.");
		}
		attachmentBasePath.append(attachment.getItem().getId());
		attachmentBasePath.append(File.separator);
		if(attachment.getHistory() != null && attachment.getHistory().getId() != 0){
			attachmentBasePath.append(attachment.getHistory().getId());
			attachmentBasePath.append(File.separator);
		}
		// set it to attachment
		attachment.setBasePath(attachmentBasePath.toString());
	}

	/**
	 * Move temp physical file to physical storage. Uses a path composed as
	 * <p>
	 * [home]/attachments/TEMP/[session user id]
	 * </p>
	 * TODO: remove filePrefix property from Attachment, we only use it to pass
	 * UserId; pass it explicitly Get a temporary file to save the attachment.
	 * 
	 * @param attachment
	 * @param jtracHome
	 * @return
	 */
	public static File saveTemporaryAttachmentFile(Attachment attachment,
			FileUpload upload, String home) {
		if (!attachment.isTemporary()) {
			throw new RuntimeException(
					"The given attachment instance is not temporary.");
		}
		File file = new File(attachment.getBasePath()
				+ attachment.getFileName());
		File dir = file.getParentFile();
		logger.debug("Checking for same named file in: "
				+ dir.getAbsolutePath() + ", dir exists: " + dir.exists());
		if (dir.exists()) {
			// remove file with the same name (without taking the extension or
			// case into account)
			File[] sameNamed = dir.listFiles(new SameBaseNameFilter(attachment
					.getFileName()));
			logger.debug("Found same named files: " + sameNamed.length);
			if (sameNamed != null && sameNamed.length > 0) {
				logger.debug("Same named files (" + sameNamed.length + "): "
						+ sameNamed);
				for (int i = 0; i < sameNamed.length; i++) {
					FileUtils.delete(sameNamed[i]);
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Saving temp Attachment file: "
					+ file.getAbsolutePath());
		}
		writeToFile(attachment, upload, file, home, false);
		return file;
	}

	private static void writeToFile(Attachment attachment,
			FileUpload fileUpload, File file, String home, boolean makeThumbs) {
		if (fileUpload == null) {
			return;
		}
		try {
			// create directory hierarchy if non-existent
			ensureFileExists(file);
			fileUpload.writeTo(file);
			file.setLastModified(new Date().getTime());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * @param file
	 * @throws IOException
	 */
	private static void ensureFileExists(File file) throws IOException {
		new File(file.getParent()).mkdirs();
		file.createNewFile();
	}

	private static void copyfile(File fromFile, File toFile) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Trying to copy from file: "
						+ fromFile.getAbsolutePath() + " to file: "
						+ toFile.getAbsolutePath());
			}
			if (!(fromFile.exists() || fromFile.isDirectory())) {
				throw new RuntimeException(
						"The file to copy from does not exist or is a directory");
			}
			ensureFileExists(toFile);
			InputStream in = new FileInputStream(fromFile);
			OutputStream out = new FileOutputStream(toFile);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	/**
	 * <ul>
	 * <li>Replace any Attachment of the Item when it has a name collision with
	 * (i.e. must be replaced by) one of the new ones.</li>
	 * <li>Set the attachmets to non-temporary</li>
	 * </ul>
	 * 
	 * @param removeFrom
	 * @param newAttachments
	 */
	public static void replaceAttachments(Item item,
			Set<Attachment> newAttachments) {
		// the set to update
		Set<Attachment> removeFrom = item.getAttachments();
		// the entries to remove
		Set<Attachment> toRemove = new HashSet<Attachment>();
		if (removeFrom != null && newAttachments != null) {
			for (Attachment newAttachment : newAttachments) {
				// remove attachment with the same fileName from Item
				// it will stay around by being connected to it's
				// history entry only
				for (Attachment existing : removeFrom) {
					if (haveSameBaseFileName(existing, newAttachment)) {
						logger.debug("Replacing " + existing.getFileName()
								+ " with " + newAttachment.getFileName());
						toRemove.add(existing);
					}
				}
				newAttachment.setTemporary(false);
			}
			// remove here to avoid ConcurrentModificationException
			item.removeAttachments(toRemove);
			// now add new attachments to Item
			item.addAttachments(newAttachments);
		}
	}

	/**
	 * Get the filename back without the extention
	 * 
	 * @param filename
	 * @return
	 */
	public static String getBaseName(String filename) {
		int dotIndex = filename.lastIndexOf(".");
		return dotIndex != -1 ? filename.substring(0, dotIndex) : filename;
	}

	/**
	 * Will return true if not null and with the same file name, without taking
	 * the extension or upper/low case into account.
	 * 
	 * @param other
	 * @return
	 */
	public static boolean haveSameBaseFileName(Attachment att1, Attachment att2) {
		boolean samename = false;
		if (att1.getFileName() != null && att2.getFileName() != null) {
			// compare the file names without the extension
			samename = getBaseName(att1.getFileName()).equalsIgnoreCase(
					getBaseName(att2.getFileName()));
		}
		return samename;
	}

	/**
	 * Remove any Attachment from the given set it has a name collision with new
	 * attachment
	 * 
	 * @param removeFrom
	 * @param removeBy
	 */
	public static void addAndReplaceSameNamed(IAttachmentOwner owner,
			Attachment attachment) {
		if(StringUtils.isBlank(attachment.getBasePath())){
			AttachmentUtils.buildBasePath(attachment);
		}
		// the set to update
		Set<Attachment> removeFrom = owner.getAttachments();
		// the entries to remove - this could be a single Attachment
		// instead of a Collection...
		Set<Attachment> toRemove = new HashSet<Attachment>();
		if (removeFrom != null) {
			for (Attachment existing : removeFrom) {
				if (haveSameBaseFileName(existing, attachment)) {
					toRemove.add(existing);
				}
			}
		}
		// remove here to avoid ConcurrentModificationException
		owner.removeAttachments(toRemove);
		// now add new attachment to Item
		owner.addAttachment(attachment);
	}

}
