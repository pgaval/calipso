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
import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.util.ItemUtils;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * Populates an Item's fields using it's Asset's attributes. This plugin is extremely generic and rough, 
 * it will iterate through the Item Asset's (and the Assets' Assets!) to find and 
 * populate matching properties based on the field/attribute name.
 */
public class CopyAssetInfoToItemPlugin extends AbstractStatePlugin{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(CopyAssetInfoToItemPlugin.class);
	

	public CopyAssetInfoToItemPlugin(){
		super();
	}
	
	public Serializable executePostStateChange(CalipsoService calipsoService, History history){
		logger.debug("CopyAssetInfoToItemPlugin#execute called with history: "+history);
		// get the item the history belongs to
		Item item = history.getParent();
		Set<Asset> assets = item.getAssets();
		// scan item assets to populate relevant fields
		if(CollectionUtils.isNotEmpty(assets)){
			for(Asset asset : assets){
				ItemUtils.initItemFields(calipsoService, item, asset, true, null);
			}
		}
		
		// update html and plain text comment
		//String htmlSuffix = ItemUtils.fmt("automatically.created.asset", new Object[]{assetType.getName(), asset.getInventoryCode()}, calipsoService.getMessageSource(), new Locale(history.getLoggedBy().getLocale()));
		//history.setHtmlComment(history.getHtmlComment()+htmlSuffix);
		//history.setComment(history.getComment()+XmlUtils.stripTags(htmlSuffix));
		calipsoService.updateItem(item, history.getLoggedBy(), false);
		//calipsoService.updateHistory(history);
		return item;
	}




}
