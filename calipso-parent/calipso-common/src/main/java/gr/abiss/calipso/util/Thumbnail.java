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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.lucene.store.Lock.With;

/**
 * Simple utility class to create thumbnails. Example:
 * <pre>
 * // get an image with a maximum with of 100 pixels
 * Thumbnail tn = new Thumbnail(largeImage);
 * Image image = tn.getThumbnail(100, Thumbnail.HORIZONTAL);
 * // save image
 * tn.saveThumbnail(toFile, Thumbnail.IMAGE_JPG);
 * </pre>
 * <strong>Note</strong>: this class is not thread safe.
 * @author manos
 * 
 */
public class Thumbnail {
	public static final int VERTICAL = 0;

	public static final int HORIZONTAL = 1;

	public static final String IMAGE_JPEG = "jpeg";

	public static final String IMAGE_JPG = "jpg";

	public static final String IMAGE_PNG = "png";
	
	private ImageIcon image;

	private ImageIcon thumb;

	public Thumbnail(Image image) {
		this.image = new ImageIcon(image);
	}

	public Thumbnail(String fileName) {
		image = new ImageIcon(fileName);
	}

	

	/**
	 * Creates a thumbnail
	 * @param size maximum pixel size
	 */
	public void getThumbnail(int size) {
		getThumbnail(size, -1);
	}
	
	
	/**
	 * Creates a thumbnail
	 * @param size maximum pixel size
	 * @param dir whether the maximum pixel size applies to <code>Thumbnail.VERTICAL</code> or <code>Thumbnail.HORIZONTAL</code>
	 */
	public void getThumbnail(int size, int dir) {
		int width, height;
		
		if (dir == HORIZONTAL) {
			width = size;
			height = -1;
		} else if(dir == VERTICAL){
			width = -1;
			height = size;
		} else {
			if(image.getIconHeight() > image.getIconWidth()){
				width = -1;
				height = size;
			}
			else{
				width = size;
				height = -1;
				
			}
		}
		
		thumb = new ImageIcon(image.getImage().getScaledInstance(width, height, Image.SCALE_FAST));
		
	}

	/**
	 * Creates a thumbnail
	 * @param size maximum pixel size
	 * @param dir whether the maximum pixel size applies to <code>Thumbnail.VERTICAL</code> or <code>Thumbnail.HORIZONTAL</code>
	 * @param scale the algorithm to use, see the AWT Image class
	 */
	public void getThumbnail(int size, int dir, int scale) {
		if (dir == HORIZONTAL) {
			thumb = new ImageIcon(image.getImage().getScaledInstance(size, -1, scale));
		} else {
			thumb = new ImageIcon(image.getImage().getScaledInstance(-1, size, scale));
		}
	}

	/**
	 * Save the already scaled image to the specified file and with the given file format.
	 * @param file
	 * @param imageType
	 */
	public void saveThumbnail(File file, String imageType) {
		if (thumb != null) {
			try {
				if(!file.exists()){
					file.mkdirs();
					file.createNewFile();
				}
				BufferedImage bi = new BufferedImage(thumb.getIconWidth(), thumb
						.getIconHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics g = bi.getGraphics();
				g.drawImage(thumb.getImage(), 0, 0, null);
					ImageIO.write(bi, imageType, file);
			} catch (IOException ioe) {
				throw new RuntimeException("Error occured saving thumbnail", ioe);
			}
		} else {
			throw new RuntimeException("Thumbnail could not be created");
		}
	}

}