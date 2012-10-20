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

package gr.abiss.calipso.plugins.state;

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.AbstractItem;
import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.Attachment;
import gr.abiss.calipso.domain.Country;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.util.AttachmentUtils;
import gr.abiss.calipso.util.DateUtils;
import gr.abiss.calipso.util.XmlUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;

/**
 * State plugins are called just before Item/History persistence
 * if the state they have been mapped on is to be applied.
 * 
 * @author manos
 *
 */
public abstract class AbstractStatePlugin implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(AbstractStatePlugin.class);

	public static final int PRE_STATE_CHANGE = 10;
	public static final int PRE_HISTORY_SAVE = 15;
	public static final int POST_STATE_CHANGE = 20;
	
	protected List<String> errors = null;
/*
	public Serializable execute(CalipsoService calipsoService, AbstractItem item, int state){
		switch(state){
			case PRE_STATE_CHANGE: return executePreStateChange(calipsoService, item);
			case POST_STATE_CHANGE: return executePostStateChange(calipsoService, item);
			default: throw new UnsupportedOperationException("Could not determine plugin method from state");
		}
	}*/ 
	 

	public Serializable executePostStateChange(CalipsoService calipsoService, History item) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Serializable executePreHistoryChange(CalipsoService calipsoService, History item) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Serializable executePreStateChange(CalipsoService calipsoService, Item item) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @param calipsoService
	 * @param history
	 * @param item
	 * @param comment
	 */
	protected void addRejectionCause(CalipsoService calipsoService, History history,
			Item item, String comment) {
		if(this.errors == null){
			errors = new LinkedList<String>();
		}
		if(logger.isDebugEnabled()){
			logger.debug("Registering rejection reason: "+comment);
		}
		errors.add(comment);
	}
	

	/**
	 * @param history
	 * @param item
	 */
	protected void markRejectedIfErrors(History history, Item item) {
		if(CollectionUtils.isNotEmpty(this.errors)){
			StringBuffer htmlCommentBuf = new StringBuffer("\n<p><span class='red'>").append("Απορρίφθηκε αυτόματα καθώς (Automatically rejected due to):").append("</span></p>\n<ul>");
			for(String reason : this.errors){
				htmlCommentBuf.append("<li>").append(reason).append("</li>\n");
			}
			htmlCommentBuf.append("</ul>\n");
			String htmlComment =  htmlCommentBuf.toString();
			String comment = XmlUtils.stripTags(htmlComment);
			
			history.setHtmlComment(history.getHtmlComment()+htmlComment);
			history.setDetail(history.getDetail()+comment);
			item.setHtmlDetail(item.getHtmlDetail()+htmlComment);
			item.setDetail(item.getDetail()+comment);
			
			Integer status = item.getSpace().getMetadata().getStateByName("Απορρίφθηκε");
			history.setStatus(status);
			item.setStatus(status);
		}
	}
	
	protected List<Asset> getItemAssetsOfType(Item item, AssetType type) {
		return getItemAssetsOfType(item, type.getName());
	}
	
	protected List<Asset> getItemAssetsOfType(Item item, String typeName) {
		List<Asset> assets = new LinkedList<Asset>();
		Set<Asset> itemAssets = item.getAssets();
		if(CollectionUtils.isNotEmpty(itemAssets)){
			for(Asset asset : itemAssets){
				if(asset.getAssetType().getName().equals(typeName)){
					assets.add(asset);
				}
			}
		}
		return assets;
	}
	

	protected AssetType getItemAssetTypeByName(Item item, String typeName) {
		AssetType assetType = null;
		Set<Asset> itemAssets = item.getAssets();
		if(CollectionUtils.isNotEmpty(itemAssets)){
			for(Asset asset : itemAssets){
				if(asset.getAssetType().getName().equals(typeName)){
					assetType = asset.getAssetType();
					break;
				}
			}
		}
		return assetType;
	}
	

}
