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

import java.util.LinkedList;
import java.util.List;

import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.OrganizationSearch;
import gr.abiss.calipso.wicket.asset.PaginationPanel;
import gr.abiss.calipso.wicket.components.viewLinks.OrganizationViewLink;

import org.apache.commons.lang.StringUtils;
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
public class OrganizationListPanel extends BasePanel {

	private OrganizationSearch organizationSearch;
	private long selectedOrganizationId;
	
	public OrganizationListPanel(String id, IBreadCrumbModel breadCrumbModel, final OrganizationSearch organizationSearch) {
		super(id, breadCrumbModel);
		
		this.organizationSearch = organizationSearch;
		
        LoadableDetachableModel assetTypesListModel = new LoadableDetachableModel() {
            protected Object load() {
            	List<Organization> organizationsList = new LinkedList<Organization>();
            	organizationsList = getCalipso().findOrganizationsMatching(organizationSearch);

                return organizationsList;
            }
        };

        assetTypesListModel.getObject();

        addComponents(assetTypesListModel);		
		
	}

	//---------------------------------------------------------------------------------------------
	
    public OrganizationListPanel setSelectedOrganizationId(long organizationId) {
        this.selectedOrganizationId = organizationId;
        return this;
    }

    //---------------------------------------------------------------------------------------------
    
	private void addComponents(IModel assetTypes){
        ////////////////
        // Pagination //
        ////////////////
        
        PaginationPanel paginationPanel = new PaginationPanel("paginationPanel", getBreadCrumbModel(), this.organizationSearch){
        	IBreadCrumbPanelFactory breadCrumbPanelFactory = new IBreadCrumbPanelFactory(){
    			public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
    				//Remove last breadcrumb participant
    				if (breadCrumbModel.allBreadCrumbParticipants().size()>0){
    					breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
    				}//if

    				return new OrganizationListPanel(getBreadCrumbModel().getActive().getComponent().getId(), getBreadCrumbModel(), OrganizationListPanel.this.organizationSearch);
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

        List<String> columnHeaders = this.organizationSearch.getColumnHeaders();
        
        ListView headings = new ListView("headings", columnHeaders) {
            protected void populateItem(ListItem listItem) {
                final String header = (String) listItem.getModelObject();
                
                Link headingLink = new Link("heading") {
                    public void onClick() {
                    	OrganizationListPanel.this.organizationSearch.doSort(header);
                    }
                };
                listItem.add(headingLink); 
                String label = localize("organization." + header);
                headingLink.add(new Label("heading", label));
                if (header.equals(OrganizationListPanel.this.organizationSearch.getSortFieldName())) {
                    String order = OrganizationListPanel.this.organizationSearch.isSortDescending() ? "order-down" : "order-up";
                    listItem.add(new SimpleAttributeModifier("class", order));
                }
            }
        };

        add(headings);

        /////////////////////
        // Asset Type List //
        /////////////////////
        
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
        
		ListView listView = new ListView("organizationList", assetTypes) {
			protected void populateItem(ListItem listItem) {
				final Organization organization = (Organization)listItem.getModelObject();
				                
                if (selectedOrganizationId == organization.getId()) {
                    listItem.add(new SimpleAttributeModifier("class", "selected"));
                } else if (listItem.getIndex() % 2 !=0){
					listItem.add(sam);
				}//if
				
				//listItem.add(new Label("description", organization.getName()));
                listItem.add(new OrganizationViewLink("description", getBreadCrumbModel(), organization));
				listItem.add(new Label("address", organization.getAddress()));
				listItem.add(new Label("zip", organization.getZip()));
				listItem.add(new Label("country", localize(organization.getCountry())));
				listItem.add(new Label("phone", organization.getPhone()));
		    	String url = organization.getWeb();
		    	if(StringUtils.isNotEmpty(url)){
			    	Label webLink = new Label("web", url);
			    	webLink.add(new SimpleAttributeModifier("href", url));
			    	listItem.add(webLink);
		    	}
		    	else{
		    		listItem.add(new Label("web", "").setVisible(false));
		    	}

				listItem.add(new BreadCrumbLink("edit", getBreadCrumbModel()){
					@Override
					protected IBreadCrumbParticipant getParticipant(String id) {
						return new OrganizationFormPanel(id, getBreadCrumbModel(), getCalipso().loadOrganization(organization.getId()));
					}
					
				});

			}//populateItem
		};//ListView		
		add(listView);		
	}
}