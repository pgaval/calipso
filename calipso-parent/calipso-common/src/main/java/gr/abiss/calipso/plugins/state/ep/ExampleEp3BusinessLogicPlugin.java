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

package gr.abiss.calipso.plugins.state.ep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.plugins.state.AbstractStatePlugin;
import gr.abiss.calipso.plugins.state.CopyAssetInfoToItemPlugin;
import gr.abiss.calipso.plugins.state.CopyItemInfoToAssetPlugin;
import gr.abiss.calipso.util.ItemUtils;
import gr.abiss.calipso.util.XmlUtils;
import gr.abiss.calipso.wicket.CalipsoSession;

/**
 * @author manos
 *
 */
public class ExampleEp3BusinessLogicPlugin extends CopyItemInfoToAssetPlugin {
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(ExampleEp3BusinessLogicPlugin.class);
	

	public ExampleEp3BusinessLogicPlugin(){
		super();
		logger.debug("ExampleBusinessLogicPlugin constructor called");
	}
	
	/**
	 * @see gr.abiss.calipso.plugins.state.AbstractStatePlugin#execute(gr.abiss.calipso.CalipsoService, gr.abiss.calipso.domain.History)
	 */
	@Override
	public Serializable executePostStateChange(CalipsoService calipsoService, History history) {
		// get the item the history belongs to
		Item item = history.getParent();
		AssetType assetType = calipsoService.loadAssetType(5);
		int numberOfEps = item.getCusDbl01().intValue();
		List<String> comments = new ArrayList<String>(numberOfEps);
		Date now = history.getTimeStamp();
		Locale userLocale = new Locale(history.getLoggedBy().getLocale());
		for(int i=0;i < numberOfEps; i++){
			Asset asset = new Asset();
			asset.setInventoryCode(item.getUniqueRefId()+i);
			asset.setAssetType(assetType);
			asset.setSupportStartDate(now);
			// copy information from item
			initFromHistoryItem(calipsoService, item, asset, assetType);
			// add to asset and history
			item.addAsset(asset);
			// save the asset 
			calipsoService.storeAsset(asset);
			comments.add(ItemUtils.fmt(
					"item_view.automatically.created.asset", 
					new Object[]{assetType.getName(), asset.getInventoryCode()}, 
					calipsoService.getMessageSource(), 
					userLocale));
		}
		
		StringBuffer commentsBuf = new StringBuffer("<ul>");
		if(CollectionUtils.isNotEmpty(comments)){
			for(String comment : comments){
				commentsBuf.append("<li>")
					.append(XmlUtils.stripTags(comment))
					.append("</li>");
			}
		}
		commentsBuf.append("</ul>");
		// update html and plain text comment
		String htmlSuffix = commentsBuf.toString();
		history.setHtmlComment(htmlSuffix);
		history.setComment(history.getComment()+XmlUtils.stripTags(htmlSuffix));
		calipsoService.updateItem(item, history.getLoggedBy(), false);
		//calipsoService.updateHistory(history);
		return null;
	
	}


}
