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

import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.dto.AssetSearch;
import gr.abiss.calipso.util.AssetsUtils;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.CollapsedPanel;
import gr.abiss.calipso.wicket.ExpandedPanel;
import gr.abiss.calipso.wicket.components.icons.StaticImage;
import gr.abiss.calipso.wicket.components.viewLinks.AssetViewLink;
import gr.abiss.calipso.wicket.hlpcls.ExpandCustomAttributesLink;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

public class AssetsListPanel extends BasePanel{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(AssetsListPanel.class);
	private AssetSearch assetSearch;
	private Long selectedAssetId;

	public void setSelectedAssetId(Long selectedAssetId) {
		this.selectedAssetId = selectedAssetId;
		
	}

	//--------------------------------------------------------------------------------------------------------------

	public AssetsListPanel(String id, IBreadCrumbModel breadCrumbModel, final AssetSearch assetSearch) {
		super(id, breadCrumbModel);
		this.assetSearch = assetSearch;
		List<Asset> assets = getCalipso().findAssetsMatching(this.assetSearch, false);
		
        addComponents(assets);

	}
	
	private void addComponents(List<Asset> assets){
        // Pagination
		PaginationPanel paginationPanel = new PaginationPanel("paginationPanel", getBreadCrumbModel(), this.assetSearch){
        	IBreadCrumbPanelFactory breadCrumbPanelFactory = new IBreadCrumbPanelFactory(){
    			public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
    				//Remove last breadcrumb participant
    				if (breadCrumbModel != null && breadCrumbModel.allBreadCrumbParticipants().size()>0){
    					breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
    				}//if
   					return new AssetSpacePanel(getBreadCrumbModel().getActive().getComponent().getId(), getBreadCrumbModel(), AssetsListPanel.this.assetSearch);
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

	    // List view headers 
	    List<String> columnHeaders = this.assetSearch.getColumnHeaders();
	    
	    ListView headings = new ListView("headings", columnHeaders) {
	        protected void populateItem(ListItem listItem) {
	            final String header = (String) listItem.getModelObject();
	            		
	            Link headingLink = new Link("heading") {
	                public void onClick() {
	                	AssetsListPanel.this.assetSearch.doSort(header);
	                }
	            };
	            listItem.add(headingLink); 
	            String label = localize("asset.assetsList." + header);
	            headingLink.add(new Label("heading", label));
	            if (header.equals(AssetsListPanel.this.assetSearch.getSortFieldName())) {
	                String order = AssetsListPanel.this.assetSearch.isSortDescending() ? "order-down" : "order-up";
	                listItem.add(new SimpleAttributeModifier("class", order));
	            }
	        }
	    };
	    add(headings);

	    //Header message 
	    Label hAction = new Label("hAction");
	    hAction.setDefaultModel(new Model(localize("edit")));	    	
	    add(hAction);

        /////////////////
        // Asset  List //
        /////////////////
        
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
        
		ListView listView = new ListView("assetsList", assets) {
			protected void populateItem(ListItem listItem) {
				/*
				final Asset asset;
				if (listItem.getModelObject().getClass().equals(AssetCustomAttributeValue.class)){
					AssetCustomAttributeValue assetCustomAttributeValue = (AssetCustomAttributeValue)listItem.getModelObject();
					asset = assetCustomAttributeValue.getAsset();
				}
				else{
					asset = (Asset)listItem.getModelObject();
				}*/
				final Asset asset = (Asset)listItem.getModelObject();
        		if (selectedAssetId != null && selectedAssetId.equals(asset.getId())) {
                    listItem.add(new SimpleAttributeModifier("class", "selected"));
                } else if (listItem.getIndex() % 2 !=0){
					listItem.add(sam);
				}//if
				
        		Label assetType = new Label("assetType", localize(asset.getAssetType().getNameTranslationResourceKey()));
        		listItem.add(assetType);
				
        		final WebMarkupContainer customAttributesContainer = new WebMarkupContainer("customAttributesContainer");
        		customAttributesContainer.setOutputMarkupId(true);
        		listItem.add(customAttributesContainer);
        		
        		final WebMarkupContainer customAttributesPanelContainer = new WebMarkupContainer("customAttributesPanel");
        		customAttributesPanelContainer.setOutputMarkupId(true);
        		customAttributesContainer.add(customAttributesPanelContainer);

        		ExpandCustomAttributesLink customAttributesLink = new ExpandCustomAttributesLink("showCustomAttributesLink",getBreadCrumbModel(), asset);
        		customAttributesLink.setComponentWhenCollapsed(customAttributesPanelContainer);
        		customAttributesLink.setTargetComponent(customAttributesContainer);

        		customAttributesLink.setImageWhenCollapsed(new CollapsedPanel("imagePanel"));
        		customAttributesLink.setImageWhenExpanded(new ExpandedPanel("imagePanel"));
        		
        		CollapsedPanel imagePanel = new CollapsedPanel("imagePanel");
        		customAttributesLink.add(imagePanel);

        		listItem.add(customAttributesLink);

				//listItem.add(new Label("inventoryCode", asset.getInventoryCode()));
        		listItem.add(new AssetViewLink("inventoryCode", getBreadCrumbModel(), asset));

        		// display space 
        		listItem.add(new Label("space", localize(asset.getSpace().getNameTranslationResourceKey())));
				//format and display dates
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				// start date
				if(asset.getSupportStartDate() != null){
					listItem.add(new Label("supportStartDate", dateFormat.format( asset.getSupportStartDate() )));
				}
				else{
					listItem.add(new Label("supportStartDate", ""));
				}
				// end date
				if(asset.getSupportEndDate() != null){
					listItem.add(new Label("supportEndDate", dateFormat.format(asset.getSupportEndDate())).add(AssetsUtils.getSupportEndDateStyle(asset.getSupportEndDate())));
				}
				else{
					listItem.add(new Label("supportEndDate", ""));
				}
				

				//edit or other action button/link
				AbstractLink link = getAssetActionLink("actionLink", asset);
                listItem.add(link);

                //For future use
                WebMarkupContainer add = new WebMarkupContainer("add");
                add.setVisible(false);
                listItem.add(add);
			}//populateItem
		};//ListView
		add(listView);
		add(new WebMarkupContainer("noData").setVisible(this.assetSearch.getResultCount()==0));


	}//addComponents
	
	/**
	 * Others may want to override this to provide other functionality. The 
	 * markup ID should be "actionLink"
	 * @param asset
	 * @return
	 */
	public AbstractLink getAssetActionLink(String markupId, final Asset asset){
		Link link = new Link(markupId) {
            public void onClick() {
    			activate(new IBreadCrumbPanelFactory(){
    				public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
    					return new AssetFormPagePanel(getBreadCrumbModel().getActive().getComponent().getId(), breadCrumbModel, asset);
    				}
    			});
            }
        };
        link.add(new StaticImage("actionLinkImg", new Model("../resources/edit.gif")));
        return link;
	}
}//AssetsListPanel