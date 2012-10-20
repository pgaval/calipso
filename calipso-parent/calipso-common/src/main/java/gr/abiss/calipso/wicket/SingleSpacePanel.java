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

import gr.abiss.calipso.domain.ItemSearch;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;

/**
 * @author erasmus
 */
public class SingleSpacePanel extends BasePanel {
	protected static final Logger logger = Logger.getLogger(SingleSpacePanel.class);

	
	private DashboardPanel dashboardPanel;
	private ItemListPanel itemListPanel;
	private ItemSearch itemSearch;
	
	public SingleSpacePanel(String id, IBreadCrumbModel breadCrumbModel, ItemSearch itemSearch) {
		super(id, breadCrumbModel);	
		this.itemSearch = itemSearch;
		setCurrentSpace(this.itemSearch.getSpace()); 
        setCurrentItemSearch(this.itemSearch);

        dashboardPanel = new DashboardPanel("dashboardPanel", breadCrumbModel, true);
        itemListPanel = new ItemListPanel("itemListPanel", breadCrumbModel);

        dashboardPanel.setOutputMarkupId(true);
        itemListPanel.setOutputMarkupId(true);

		add(dashboardPanel);	
		add(itemListPanel);
	}

    public void refreshDashboardPanel() {
    	DashboardPanel newDashboardPanel = new DashboardPanel("dashboardPanel", getBreadCrumbModel());
    	dashboardPanel.replaceWith(newDashboardPanel);
    	dashboardPanel = newDashboardPanel;
    	dashboardPanel.setOutputMarkupId(true);
	}

    public void refreshDashboardPanel(AjaxRequestTarget target) {
    	refreshDashboardPanel();
        target.addComponent(dashboardPanel);
	}

    public void refreshItemListPanel() {
    	ItemListPanel newItemListPanel = new ItemListPanel("itemListPanel", getBreadCrumbModel());
		itemListPanel.replaceWith(newItemListPanel);
		itemListPanel = newItemListPanel;
		itemListPanel.setOutputMarkupId(true);
	}
	
    public void refreshItemListPanel(AjaxRequestTarget target) {
    	refreshItemListPanel();
        target.addComponent(itemListPanel);
	}

    public String getTitle(){
    	if(itemSearch.getSpace() != null){
    		return localize(itemSearch.getSpace().getNameTranslationResourceKey());
    	}
    	return null;
    }

    
}

