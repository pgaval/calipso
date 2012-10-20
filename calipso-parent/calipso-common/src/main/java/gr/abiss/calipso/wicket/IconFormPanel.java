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

import gr.abiss.calipso.config.CalipsoPropertiesEditor;
import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.util.Thumbnail;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

public class IconFormPanel extends BasePanel {
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(IconFormPanel.class);
	private  FileUploadField iconUploadField;
	private boolean deleteIcon = false;
    
	private String iconsFolderName;
	private String iconFolderPath = null;
	private String iconIdFolderPath = null;
	private String iconFilePath = null;
	private String iconFileSmallPath = null;
		
	public IconFormPanel(String id, User user) {
		this(id, new PropertyModel(user, "id"), IconPanel.FOLDER_USERS);
		logger.debug("constructor called with user");
		CheckBox gravatarCheckBox = new CheckBox("gravatar", new PropertyModel(user, "gravatar"));
        add(gravatarCheckBox);
        gravatarCheckBox.setLabel(new ResourceModel("user_form.useGravatar"));
        add(new SimpleFormComponentLabel("useGravatar", gravatarCheckBox));
	    add(new IconPanel("userImage", user));
		addComponents(id);
	}
	
	public IconFormPanel(String id, Organization organization) {
		this(id, new PropertyModel(organization, "id"), IconPanel.FOLDER_ORGANIZATIONS);
		logger.debug("constructor called with org");
		hideGravatar();
	    add(new IconPanel("userImage", this.getDefaultModel(), this.iconsFolderName));
		addComponents(id);
	}
	

	public IconFormPanel(String id, AssetType assetType) {
		this(id, new PropertyModel(assetType, "id"), IconPanel.FOLDER_ASSETTYPES);
		logger.debug("constructor called with assettype");
		hideGravatar();
	    add(new IconPanel("userImage", this.getDefaultModel(), this.iconsFolderName));
		addComponents(id);
	}
	
	public IconFormPanel(String id, IModel model, String iconsFolderName) {
		super(id);
		setDefaultModel(model);
		this.iconsFolderName = iconsFolderName;
	}
	

	public boolean isDeleteIcon() {
		return deleteIcon;
	}

	public void setDeleteIcon(boolean deleteIcon) {
		this.deleteIcon = deleteIcon;
	}

	private void hideGravatar() {
		CheckBox gravatarCheckBox = new CheckBox("gravatar");
        add(gravatarCheckBox.setVisible(false));
        add(new Label("useGravatar", "").setVisible(false));
	}
	
	private void addComponents(String id) {
        //upload icon
        iconUploadField = new FileUploadField("imageFileUpload", new Model());
        add(iconUploadField);
        		
        Object iconId = getDefaultModel() != null ? getDefaultModel().getObject():null;      
		if(iconId != null && iconId instanceof Long){
			createIconsPathStrings(((Long)iconId).longValue());
	        
	        //if icon exists render checkbox
	        if(new File(iconFilePath).exists()){
	        	// delete icon checkbox =======================================
	            CheckBox deleteIconCheckBox = new CheckBox("deleteIcon", new PropertyModel(this, "deleteIcon"));
	            add(deleteIconCheckBox);
	            //form label
	            deleteIconCheckBox.setLabel(new ResourceModel("user_form.deleteIcon"));
	            add(new SimpleFormComponentLabel("deleteIconLabel", deleteIconCheckBox));
	            
	            return;
	        }
		}
		
        //if icons does not exist, don't render checkbox
        add(new WebMarkupContainer("deleteIconLabel").setVisible(false));
        add(new WebMarkupContainer("deleteIcon").setVisible(false));
	}
	
	public void onSubmit(){
		Object model = getDefaultModelObject(); 
		logger.debug("Model object: "+model);
		long iconId = (Long)getDefaultModelObject(); 
		if(iconId != 0){
			if(iconFolderPath == null){
				createIconsPathStrings(iconId);
			}
			
	        //User Icon ===================================================
	        final FileUpload iconUpload = iconUploadField.getFileUpload();
	        
	        //If user uploaded an image
	        if(deleteIcon == false && iconUpload != null){
	        	
	            //if user folder does not exists create it
	            File iconFolder = new File(iconFolderPath);
	            if(!iconFolder.exists()){
	            	iconFolder.mkdir();
	            }
	        	
	            //if user folder does not exists create it
	            File iconIdFolder = new File(iconIdFolderPath);
	            if(!iconIdFolder.exists()){
	            	iconIdFolder.mkdir();
	            }
	            	            
	            // get an image with a maximum with of 100 pixels	            
				try {
	            	File icon = new File(iconFilePath);
	            	File iconSmall = new File(iconFileSmallPath);
	            	
	            	if(!icon.exists()){icon.createNewFile();}
	            	if(!iconSmall.exists()){iconSmall.createNewFile();}
	            	
	            	
					//get image from upload
					BufferedImage bufferedImage = ImageIO.read(iconUpload.getInputStream());
					
					//generate and save image
					Thumbnail tn = new Thumbnail(bufferedImage);
					//create a big icon
					tn.getThumbnail(100);					
					tn.saveThumbnail(icon, Thumbnail.IMAGE_PNG);
					//create small icon
					tn.getThumbnail(50);					
					tn.saveThumbnail(iconSmall, Thumbnail.IMAGE_PNG);
				} catch (Exception e) {
					logger.error("Error saving icon.");
				}
	        }
	        else if(deleteIcon){
	        	//if user folder exists delete the icons
	            if(new File(iconIdFolderPath).exists()){
	            	File icon = new File(iconFilePath);
	            	File iconSmall = new File(iconFileSmallPath);
	            	
	            	if(icon.exists()){
	            		icon.delete();
	            	}
	            	
	            	if(iconSmall.exists()){
	            		iconSmall.delete();
	            	}
	            }
	        }
		}
	}
	
	private void createIconsPathStrings(long iconId) {
		this.iconFolderPath = CalipsoPropertiesEditor.getHomeFolder(iconsFolderName);
		this.iconIdFolderPath = iconFolderPath + File.separator+"id"+iconId;
		this.iconFilePath = iconIdFolderPath + File.separator + "icon.png";
		this.iconFileSmallPath = iconIdFolderPath + File.separator + "icon_small.png";
	}
}

