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

import java.util.Map;
import java.util.Set;

import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.util.BreadCrumbUtils;

import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author erasmus
 */
public class HistoryPreviewPanel extends AbstractItemFormPanel {
	
	public String getTitle(){
        return localize("preview");
    }
	
	public HistoryPreviewPanel(String id, final IBreadCrumbModel breadCrumbModel, 
				final History history, Map<String,FileUploadField> fileUploadFields, long itemId, boolean moveToOtherSpace) {
		super(id, breadCrumbModel);
		this.fileUploadFields = fileUploadFields;
		getBackLinkPanel().makeBack(new BreadCrumbLink("link", breadCrumbModel) {
			@Override
			protected IBreadCrumbParticipant getParticipant(String componentId) {
				BreadCrumbUtils.removePreviousBreadCrumbPanel(getBreadCrumbModel());
				return new ItemViewPanel(componentId, breadCrumbModel, history.getUniqueRefId(), history);
			}		
		});
		
		add(new HistoryEntry("historyEntry", breadCrumbModel, history, null));
		
		add(new HistoryPreviewForm("form", history, itemId, moveToOtherSpace));	
	}
	
	private class HistoryPreviewForm extends Form {		
		public HistoryPreviewForm(String id, final History history, final long itemId, final boolean moveToOtherSpace){
            super(id);
            
			add(new Button("edit") {
	            @Override
	            public void onSubmit() {	            	
	            	BreadCrumbUtils.removePreviousBreadCrumbPanel(getBreadCrumbModel());
	            	
	            	activate(new IBreadCrumbPanelFactory(){
	            		public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
	            			// TODO: are atachments lost?
	            			return new ItemViewPanel(componentId, breadCrumbModel, history.getUniqueRefId(), history);
	            		}
	            	});
	            }           
	        });
	        /*
	        add(new Button("submit") {
	            @Override
	            public void onSubmit() {
					getJtrac().storeHistoryForItem(itemId, getNonNullUploads(), history);
	            	
	            	if (history.getAssignableSpaces()!=null && moveToOtherSpace == true) {
		            	//Change item space
		            	getJtrac().storeItemSpace(itemId,  getJtrac().loadSpace(history.getAssignableSpaces().getId()));
		                //ItemSearch itemSearch = new ItemSearch(item.getSpace(), getPrincipal(), ItemViewFormPanel.this);   
		                //setCurrentItemSearch(itemSearch);
		                //Redirect to the Item list because this item doesn't belong to current space
		                //TODO check if works
//		                BreadCrumbPanel activePanel = (BreadCrumbPanel)getBreadCrumbModel().getActive();
//		            	BreadCrumbUtils.removeActiveBreadCrumbPanel(getBreadCrumbModel());
//		            	
//		            	activePanel.activate(new IBreadCrumbPanelFactory(){
//							public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
//								return new ItemListPanel(componentId, breadCrumbModel);
//							}
//		            	});
		            	//setResponsePage(ItemListPage.class);
		            }//if
		            else{
		            	BreadCrumbUtils.removePreviousBreadCrumbPanel(getBreadCrumbModel());
		            	
		            	activate(new IBreadCrumbPanelFactory(){
		            		public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
		            			return new ItemViewPanel(componentId, breadCrumbModel, history.getUniqueRefId());
		            		}
		            	});
		            }//else	                
	            	
	            }           
	        });
	        */
			
		}
	}

	
}

