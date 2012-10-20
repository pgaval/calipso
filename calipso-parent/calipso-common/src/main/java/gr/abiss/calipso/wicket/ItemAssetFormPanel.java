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

import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.wicket.asset.ItemAssetsPanel;

import java.util.HashSet;
import java.util.LinkedHashSet;

import org.apache.log4j.Logger;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import bsh.This;

/**
 * @author marcello
 */
public class ItemAssetFormPanel extends BasePanel {
	protected static final Logger logger = Logger.getLogger(ItemAssetFormPanel.class);

	private static final long serialVersionUID = 1L;


	private Item item;
	
	private ItemAssetsPanel itemAssetsPanel;
	private ItemAssetsPanel availableAssetsPanel;
	
	public ItemAssetFormPanel(String id, IBreadCrumbModel breadCrumbModel, long itemId) {
		super(id, breadCrumbModel);
		item = getCalipso().loadItem(itemId);
		add(new Label("title", localize("dashboard.editAssets", item.getUniqueRefId())));
		
		add(new ItemAssetForm("form", item));
	}

	public String getTitle(){
		return localize("dashboard.editAssets", item.getUniqueRefId());
	}
	
	
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Wicket Form
     * */
    private class ItemAssetForm extends Form {
    	
		private static final long serialVersionUID = 1L;
		private CalipsoFeedbackMessageFilter filter;
    	
    	///////////////////////////////////////////////////////////////////////////////////////
    	
    	public ItemAssetForm(String id, final Item item) {
			super(id);
			CompoundPropertyModel model = new CompoundPropertyModel(item);
			setModel(model);
            FeedbackPanel feedback = new FeedbackPanel("feedback");
            filter = new CalipsoFeedbackMessageFilter();
            feedback.setFilter(filter);
            add(feedback);   
			
    		itemAssetsPanel = new ItemAssetsPanel("assetPanel", item, true);
    		itemAssetsPanel.renderItemAssets();
    		
    		add(itemAssetsPanel);
    		availableAssetsPanel = new ItemAssetsPanel("availableAssetPanel", item);
    		availableAssetsPanel.renderAvailableAssets();
    		add(availableAssetsPanel);

		}
    	
    	protected void onSubmit() {
        	if(itemAssetsPanel!=null){
        		if(itemAssetsPanel.getSelectedAssets()!=null){
        			getCalipso().loadItem(item.getId()).setAssets(((new HashSet<Asset>(itemAssetsPanel.getSelectedAssets()))));
        			if(logger.isDebugEnabled()){
        				for(Asset asset : itemAssetsPanel.getSelectedAssets()){
        						logger.debug("Asset in itemAssetsPanel : " + asset.getDisplayedValue());
        				}
        			}
        		}
        	}
        	
        	if(availableAssetsPanel!=null){
        		if(availableAssetsPanel.getSelectedAssets()!=null){
        			getCalipso().loadItem(item.getId()).getAssets().addAll((new HashSet<Asset>(availableAssetsPanel.getSelectedAssets())));
        			if(logger.isDebugEnabled()){
        				for(Asset asset : availableAssetsPanel.getSelectedAssets()){
        						logger.debug("Asset in availableAssetsPanel : " + asset.getDisplayedValue());
        				}
        			}
        		}
        	}
        	getCalipso().updateItem(getCalipso().loadItem(item.getId()), getPrincipal(), false);
        	
            //Go back to item view
            activate(new IBreadCrumbPanelFactory(){
            
				private static final long serialVersionUID = 1L;

				public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
            		
            		return new ItemViewPanel(componentId, breadCrumbModel, item.getUniqueRefId());
            	}
            });
            
    	}
    }
}