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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import gr.abiss.calipso.config.CalipsoPropertiesEditor;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.wicket.components.icons.GravatarImage;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.BufferedDynamicImageResource;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * Renders an icon for the given user, organization or asset.
 * Gravatar icons may be rendered for users.
 * @author manos
 *
 */
public class IconPanel extends BasePanel {

	public static final String FOLDER_USERS = "users";
	public static final String FOLDER_ORGANIZATIONS = "organizations";
	public static final String FOLDER_ASSETTYPES = "assetTypes";
	

	public IconPanel(String id, User user) {
		this(id, user, false);
	}
	
	public IconPanel(String id, User user, boolean getSmall) {
		super(id);
		if(user == null){

			addIcon(id, new Model(new Long(0)), FOLDER_USERS, false);
		}
		else if(user.isGravatar()){
			//modify css classes for the span tag
			SimpleAttributeModifier sam;
			if(getSmall){
				sam = new SimpleAttributeModifier("class", "iconSmall");
			}
			else{
				sam = new SimpleAttributeModifier("class", "icon");
			}
			GravatarImage icon = new GravatarImage("icon", user.getEmailHash(), getSmall?48:100);
			icon.add(sam);
			add(icon);
		}
		else{
			addIcon(id, new PropertyModel(user, "id"), FOLDER_USERS, false);
		}
	}
	
	public IconPanel(String id, IModel idModel, String iconFolderPathPart) {
		this(id, idModel, iconFolderPathPart, false);
	}

	public IconPanel(String id, IModel idModel, String iconsFolderName, boolean getSmall) {
		super(id);
		addIcon(id, idModel, iconsFolderName, getSmall);
	}
	
	public void addIcon(String id, IModel idModel, String iconsFolderName, boolean getSmall) {
		
		BufferedImage icon = null;
		long iconId = (Long)idModel.getObject();
		
		if(iconId != 0){
			//get image folder path
	        String iconFolderPath = CalipsoPropertiesEditor.getHomeFolder(
	        		new StringBuffer(iconsFolderName).append(File.separator).append("id").append(iconId).toString());
	        File iconFile;
	        if(getSmall){
	        	iconFile = new File(new StringBuffer(iconFolderPath).append(File.separator).append("icon_small.png").toString());
	        }
	        else{
	        	iconFile = new File(new StringBuffer(iconFolderPath).append(File.separator).append("icon.png").toString());
	        }
	        
	        //read image	        
	        try {
				icon = ImageIO.read(iconFile);
			} catch (IOException e) {}
		}
		
		//modify css classes for the span tag
		SimpleAttributeModifier sam;
		if(getSmall){
			sam = new SimpleAttributeModifier("class", "iconSmall");
		}
		else{
			sam = new SimpleAttributeModifier("class", "icon");
		}
				
		//render html
		if(icon != null){
			BufferedDynamicImageResource iconResource = new BufferedDynamicImageResource();
			iconResource.setImage(icon);
			Image image = new Image("icon", iconResource);
			image.add(sam);

			add(image);
		}
		else{
			WebMarkupContainer defaultImage = new WebMarkupContainer("icon");
			if(getSmall){// if small render the small icon
				defaultImage.add(new SimpleAttributeModifier("src","../resources/default-"+iconsFolderName+"-icon-small.png"));
			}
			else{
				defaultImage.add(new SimpleAttributeModifier("src","../resources/default-"+iconsFolderName+"-icon.png"));
			}
			defaultImage.add(sam);
			add(defaultImage);
		}		
	
	}
	

}

