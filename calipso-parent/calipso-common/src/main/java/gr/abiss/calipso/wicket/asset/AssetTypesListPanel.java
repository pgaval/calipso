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

package gr.abiss.calipso.wicket.asset;

import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.AssetTypeSearch;
import gr.abiss.calipso.wicket.BasePanel;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 * @author marcello
 */
public class AssetTypesListPanel extends BasePanel {
	private AssetTypeSearch assetTypeSearch;
	
    private Long selectedAssetTypeId;

	public AssetTypesListPanel(String id, IBreadCrumbModel breadCrumbModel, final AssetTypeSearch assetTypeSearch) {
		super(id, breadCrumbModel);
				
		this.assetTypeSearch = assetTypeSearch;
	
        LoadableDetachableModel assetTypesListModel = new LoadableDetachableModel() {
            protected Object load() {
            	List<AssetType> assetTypeList = new LinkedList<AssetType>();
            	assetTypeList = getCalipso().findAssetTypesMatching(assetTypeSearch);

                return assetTypeList;
            }
        };
		
        assetTypesListModel.getObject();
        
        addComponents(assetTypesListModel);		
	}
	
	//----------------------------------------------------------------------------------
	
    public AssetTypesListPanel setSelectedAssetTypeId(Long selectedAssetTypeId) {
        this.selectedAssetTypeId = selectedAssetTypeId;
        return this;
    }
	
    //----------------------------------------------------------------------------------
    
	private void addComponents(IModel assetTypes){
		
        ////////////////
        // Pagination //
        ////////////////
        
        PaginationPanel paginationPanel = new PaginationPanel("paginationPanel", getBreadCrumbModel(), this.assetTypeSearch){
        	IBreadCrumbPanelFactory breadCrumbPanelFactory = new IBreadCrumbPanelFactory(){
    			public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
    				//Remove last breadcrumb participant
    				if (breadCrumbModel.allBreadCrumbParticipants().size()>0){
    					breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
    				}//if

    				return new AssetTypesPanel(getBreadCrumbModel().getActive().getComponent().getId(), getBreadCrumbModel(), AssetTypesListPanel.this.assetTypeSearch);
    			}
    		};
        	
        	public void onNextPageClick() {
        		activate(breadCrumbPanelFactory);
        	}
        	
        	public void onPreviousPageClick() {
        		activate(breadCrumbPanelFactory);
        	}
        	
        	public void onPageNumberClick() {
        		activate(breadCrumbPanelFactory);
        	}
        };
        
        add(paginationPanel);

        
        /////////////////
        // List header //
        /////////////////

        List<String> columnHeaders = this.assetTypeSearch.getColumnHeaders();
        
        ListView headings = new ListView("headings", columnHeaders) {
            protected void populateItem(ListItem listItem) {
                final String header = (String) listItem.getModelObject();
                
                Link headingLink = new Link("heading") {
                    public void onClick() {
                    	AssetTypesListPanel.this.assetTypeSearch.doSort(header);
                    }
                };
                listItem.add(headingLink); 
                String label = localize("asset.assetTypes." + header);
                headingLink.add(new Label("heading", label));
                if (header.equals(AssetTypesListPanel.this.assetTypeSearch.getSortFieldName())) {
                    String order = AssetTypesListPanel.this.assetTypeSearch.isSortDescending() ? "order-down" : "order-up";
                    listItem.add(new SimpleAttributeModifier("class", order));
                }
            }
        };

        add(headings);

        /////////////////////
        // Asset Type List //
        /////////////////////
        
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
        
		ListView listView = new ListView("assetTypeList", assetTypes) {
			protected void populateItem(ListItem listItem) {
				final AssetType assetType = (AssetType)listItem.getModelObject();
				                
                if (selectedAssetTypeId != null && assetType.getId()== selectedAssetTypeId.longValue()) {
                    listItem.add(new SimpleAttributeModifier("class", "selected"));
                } else if (listItem.getIndex() % 2 !=0){
					listItem.add(sam);
				}//if
				
				// TODO: remove the getName part after ele finishes
				listItem.add(new Label("description", localize(assetType.getNameTranslationResourceKey())));
				
				listItem.add(new BreadCrumbLink("edit", getBreadCrumbModel()){
					@Override
					protected IBreadCrumbParticipant getParticipant(String id) {
						return new AssetTypeFormPagePanel(id, getBreadCrumbModel(), getCalipso().loadAssetType(assetType.getId()));
					}
					
				});

			}//populateItem
		};//ListView		
		add(listView);
		add(new WebMarkupContainer("noData").setVisible(this.assetTypeSearch.getResultCount()==0));
	}//addComponents
	

}//AssetTypesListPanel