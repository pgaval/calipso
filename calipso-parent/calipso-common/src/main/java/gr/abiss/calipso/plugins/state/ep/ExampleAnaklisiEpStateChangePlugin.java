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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.plugins.state.AbstractStatePlugin;
import gr.abiss.calipso.plugins.state.CopyAssetInfoToItemPlugin;
import gr.abiss.calipso.util.AssetsUtils;
import gr.abiss.calipso.util.DateUtils;
import gr.abiss.calipso.util.XmlUtils;
import gr.abiss.calipso.wicket.CalipsoSession;

/**
 * Demonstrates an approach to apply state changes as an asset attribute value.
 */
public class ExampleAnaklisiEpStateChangePlugin extends AbstractStatePlugin {
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(ExampleAnaklisiEpStateChangePlugin.class);

	private History history = null;
	private Item item = null;
	
	public ExampleAnaklisiEpStateChangePlugin(){
		super();
		logger.debug("constructor called");
	}
	
	/**
	 * @see gr.abiss.calipso.plugins.state.AbstractStatePlugin#execute(gr.abiss.calipso.CalipsoService, gr.abiss.calipso.domain.History)
	 */
	@Override
	public Serializable executePostStateChange(CalipsoService calipsoService, History history) {
		/*
Στάδιο Μετάβασης          Κατάσταση ΕΠ
================          ============
New                       Προς Ανάκληση
Σήμανση ΕΠ & Κοινοποίηση  Προς Ανάκληση
Αποσήμανση ΕΠ             Ενεργή
Ανάκληση ΕΠ               Ανακληθείσα 
		*/
		// gather item state info
		// gather item state info
		this.item = history.getParent();
		this.history = history;
		AssetType assetType = this.getItemAssetTypeByName(item, "ΜΑΕΠ");

		logger.warn("Item status: '"+item.getStatusValue()+"'");
		if(assetType == null){
			logger.warn("Item does not seem to address any assets of type 'ΜΑΕΠ'");
		}
		else{
			// get all item assets of the desired type
			List<Asset> assets = this.getItemAssetsOfType(item, assetType);
			
			// get a handle on asset attribute
			AssetTypeCustomAttribute katastasiAttribute = AssetsUtils.getAssetTypeAttribute(assetType, "Κατάσταση ΕΠ");
			CustomAttributeLookupValue desiredValue = null;
			if(!assets.isEmpty()){
				String itemState = item.getStatusValue();
				if(itemState.equals("New") || itemState.equals("Σήμανση ΕΠ & Κοινοποίηση")){
					desiredValue = calipsoService.loadCustomAttributeLookupValue(katastasiAttribute, "Προς Ανάκληση");
				}
				else if(itemState.equals("Αποσήμανση ΕΠ")){
					desiredValue = calipsoService.loadCustomAttributeLookupValue(katastasiAttribute, "Ενεργή");
				}
				else if(itemState.equals("Ανάκληση ΕΠ")){
					desiredValue = calipsoService.loadCustomAttributeLookupValue(katastasiAttribute, "Ανακληθείσα");
				}
				
				if(desiredValue != null){
					// update assets
					for(Asset asset : assets){
						calipsoService.loadAssetAttributes(asset);
						//logger.debug("asset's previous katastasiAttribute: "+asset.getCustomAttributes().get(katastasiAttribute)+", channging to "+desiredValue.getId());
						asset.addOrReplaceCustomAttribute(katastasiAttribute, String.valueOf(desiredValue.getId()));
						//logger.debug("asset's changed katastasiAttribute: "+asset.getCustomAttributes().get(katastasiAttribute));
					}
					calipsoService.updateAssets(assets);
				}
			}
		}
		return null;
	}


}
