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

package gr.abiss.calipso.wicket.regexp;

import gr.abiss.calipso.domain.ValidationExpression;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.OrganizationFormPanel;
import gr.abiss.calipso.wicket.asset.PaginationPanel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
/**
 * 
 *
 */
public class ValidationExpressionListPanel extends BasePanel {
	
	private ValidationExpressionSearch validationExpressionSearch;
	private long selectedValidationExpressionId;
	
	public ValidationExpressionListPanel(String id, IBreadCrumbModel breadCrumbModel,
								final ValidationExpressionSearch validationExpressionSearch){
		super(id,breadCrumbModel);
		this.validationExpressionSearch = validationExpressionSearch;
		
		//TODO: loadable detachable model - validation Expression list
		LoadableDetachableModel validationExpressionModel = new LoadableDetachableModel() {
			
			@Override
			protected Object load() {
				// TODO: get the list of validation expression
				List<ValidationExpression> validationExpressionList = new ArrayList<ValidationExpression>();
				validationExpressionList = getCalipso().findValidationExpressionsMatching(validationExpressionSearch);
				return validationExpressionList;
			}
		};
		validationExpressionModel.getObject();

        addComponents(validationExpressionModel);	
	}
	
    public ValidationExpressionListPanel setSelectedValidationExpressionId(long validationExpressionId) {
        this.selectedValidationExpressionId = validationExpressionId;
        return this;
    }
    
	private void addComponents(IModel validationExpressions){
        ////////////////
        // Pagination //
        ////////////////
        
        PaginationPanel paginationPanel = new PaginationPanel("paginationPanel", getBreadCrumbModel(), this.validationExpressionSearch){
        	IBreadCrumbPanelFactory breadCrumbPanelFactory = new IBreadCrumbPanelFactory(){
    			public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
    				//Remove last breadcrumb participant
    				if (breadCrumbModel.allBreadCrumbParticipants().size()>0){
    					breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
    				}//if

    				return new ValidationExpressionListPanel(getBreadCrumbModel().getActive().getComponent().getId(), getBreadCrumbModel(), ValidationExpressionListPanel.this.validationExpressionSearch);
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
        
        List<String> columnHeaders = this.validationExpressionSearch.getColumnHeaders();
        
        ListView headings = new ListView("headings", columnHeaders) {
            protected void populateItem(ListItem listItem) {
                final String header = (String) listItem.getModelObject();
                
                Link headingLink = new Link("heading") {
                    public void onClick() {
                    	ValidationExpressionListPanel.this.validationExpressionSearch.doSort(header);
                    }
                };
                listItem.add(headingLink); 
                String label = localize("validation_Expression." + header);
                headingLink.add(new Label("heading", label));
                if (header.equals(ValidationExpressionListPanel.this.validationExpressionSearch.getSortFieldName())) {
                    String order = ValidationExpressionListPanel.this.validationExpressionSearch.isSortDescending() ? "order-down" : "order-up";
                    listItem.add(new SimpleAttributeModifier("class", order));
                }
            }
        };

        add(headings);
        
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
        
		ListView listView = new ListView("validationExpressionList", validationExpressions) {
			protected void populateItem(ListItem listItem) {
				final ValidationExpression validationExpression = (ValidationExpression)listItem.getModelObject();
				                
                if (selectedValidationExpressionId == validationExpression.getId()) {
                    listItem.add(new SimpleAttributeModifier("class", "selected"));
                } else if (listItem.getIndex() % 2 !=0){
					listItem.add(sam);
				}//if
				
			
                listItem.add(new Label("name", validationExpression.getName()));
				listItem.add(new Label("descriptionLabel", validationExpression.getDescription()));
				listItem.add(new Label("expressionLabel", validationExpression.getExpression()));

				listItem.add(new BreadCrumbLink("edit", getBreadCrumbModel()){
					@Override
					protected IBreadCrumbParticipant getParticipant(String id) {
						return new ValidationExpressionFormPanel(id, getBreadCrumbModel(), getCalipso().loadValidationExpression(validationExpression.getId()));
					}
					
				});

			}//populateItem
		};//ListView		
		add(listView);	
	}
}
