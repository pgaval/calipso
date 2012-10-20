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
 * 
 * This file incorporates work released by the JTrac project and  covered 
 * by the following copyright and permission notice:  
 * 
 *   Copyright 2002-2005 the original author or authors.
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *   
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package gr.abiss.calipso.wicket;

import static gr.abiss.calipso.domain.ColumnHeading.Name.ASSET_TYPE;
import static gr.abiss.calipso.domain.ColumnHeading.Name.ASSIGNED_TO;
import static gr.abiss.calipso.domain.ColumnHeading.Name.DETAIL;
import static gr.abiss.calipso.domain.ColumnHeading.Name.DUE_TO;
import static gr.abiss.calipso.domain.ColumnHeading.Name.ID;
import static gr.abiss.calipso.domain.ColumnHeading.Name.LOGGED_BY;
import static gr.abiss.calipso.domain.ColumnHeading.Name.PLANNED_EFFORT;
import static gr.abiss.calipso.domain.ColumnHeading.Name.REPORTED_BY;
import static gr.abiss.calipso.domain.ColumnHeading.Name.SPACE;
import static gr.abiss.calipso.domain.ColumnHeading.Name.STATUS;
import static gr.abiss.calipso.domain.ColumnHeading.Name.SUMMARY;
import static gr.abiss.calipso.domain.ColumnHeading.Name.TIME_FROM_CREATION_TO_CLOSE;
import static gr.abiss.calipso.domain.ColumnHeading.Name.TIME_FROM_CREATION_TO_FIRST_REPLY;
import static gr.abiss.calipso.domain.ColumnHeading.Name.TIME_STAMP;
import static gr.abiss.calipso.domain.ColumnHeading.Name.TOTAL_RESPONSE_TIME;

import gr.abiss.calipso.domain.AbstractItem;
import gr.abiss.calipso.domain.ColumnHeading;
import gr.abiss.calipso.domain.Effort;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.State;
import gr.abiss.calipso.domain.StdField;
import gr.abiss.calipso.domain.StdFieldMask;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.ColumnHeading.Name;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.util.DateTime;
import gr.abiss.calipso.util.DateUtils;
import gr.abiss.calipso.util.ExcelUtils;
import gr.abiss.calipso.util.HumanTime;
import gr.abiss.calipso.util.ItemUtils;
import gr.abiss.calipso.util.StdFieldsUtils;
import gr.abiss.calipso.wicket.asset.ItemAssetTypesPanel;
import gr.abiss.calipso.wicket.components.formfields.HumanTimeDurationConverter;
import gr.abiss.calipso.wicket.components.viewLinks.UserViewLink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.Page;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
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
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.http.WebResponse;

/**
 * item list panel
 */
public class ItemList extends BasePanel {

	private static final Logger logger = Logger.getLogger(ItemList.class);
    private ItemSearch itemSearch;
    
    //For save search
    private SaveSearchFormPanel saveSearchFormPanel = null;
	private WebMarkupContainer saveSearchContainer = null;
	boolean isSaveEditFormOpen = false;
	WebMarkupContainer saveSearchPanelContainer;

	private LoadableDetachableModel itemListModel;
	private long resultCount;
	private int pageCount;
	private final int pageSize;
	private final int currentPage;
	
	///////////////////////////////////////////////////////////////////////////////////////
	
    private void refreshPage(final String name){
		BreadCrumbUtils.removeActiveBreadCrumbPanel(getBreadCrumbModel());
		activate(new IBreadCrumbPanelFactory() {		
			public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
				return new ItemListPanel(getId(), getBreadCrumbModel(), name);
			}		
		});
    }//refreshPage

    /////////////////////////////////////////////////////////////////////////////////////////

	private class SaveSearchFormPanel extends AbstractSaveSearchFormPanel{

		public SaveSearchFormPanel(String id) {
			super(id);
		}
		
		//--------------------------------------------------------------------------------

		@Override
		public void cancel() {
			saveSearchContainer.remove(saveSearchFormPanel);
			saveSearchFormPanel = null;

			//Add container
			saveSearchPanelContainer = new WebMarkupContainer("saveSearchFormPanel");
			saveSearchContainer.add(saveSearchPanelContainer);
		}//cancel

		//--------------------------------------------------------------------------------

		@Override
		public void save(String name) {
			refreshPage(name);
		}//save

		@Override
		public void confirm() {
		}
	}//SaveSearchFormPanel
	
	/////////////////////////////////////////////////////////////////////////////////////////
	
    public ItemList(final String id) {
    	this(id, null);
    }
    
    //--------------------------------------------------------------------------------------------------------
    
    public ItemList(final String id, final IBreadCrumbModel breadCrumbModel, boolean showSaveSearchLink) {
    	this(id, breadCrumbModel);
    	saveSearchContainer.setVisible(showSaveSearchLink);
    }
    
    public ItemList(final String id, final IBreadCrumbModel breadCrumbModel) {
        super(id, breadCrumbModel);
        setOutputMarkupId(true);
        
        this.itemSearch = getCurrentItemSearch();
        itemSearch.setUser(getPrincipal());
        itemListModel = new LoadableDetachableModel() {
            protected Object load() {
                List <Item> itemList = getCalipso().findItems(itemSearch);
                
                return itemList;
            }
        };
        
        // hack - ensure that wicket model "attach" happens NOW before pagination logic sp that
        // itemSearch is properly initialized in the LoadableDetachableModel#load() above
        itemListModel.getObject();
        
        
        
        pageCount = 1;
        pageSize = itemSearch.getPageSize();
        resultCount = itemSearch.getResultCount();
        if (pageSize != -1) {
            pageCount = (int) Math.ceil((double) resultCount / pageSize);
        }
        currentPage = itemSearch.getCurrentPage();
        
        String helpMessage;
        if(resultCount == 0){
        	helpMessage = localize("item_list.noResultsFound");
        }
        else{
        	helpMessage = localize("ItemList.help");
        }
        add(new Label("helpMessage", helpMessage));
        
        addRecordsCount();
        addPagination();
        addExcelExport();
        addAggregates(itemSearch);
        addSaveSearch();
        addItemsTable();
        
    }
    
    //--------------------------------------------------------------------------------------------------------
    
    
  
    private void addAggregates(final ItemSearch itemSearch) {
    	boolean showAgregates = !getPrincipal().isAnonymous() && itemSearch.getSpace() != null;
    	//if(!getPrincipal().isAnonymous() && itemSearch.getSpace() != null){
	        final ModalWindow agregatesReportModal = new ModalWindow("agregatesReportModal");
	        add(agregatesReportModal);
	        //modal2.setCookieName("modal-2");
	        WebMarkupContainer agregatesContainer = new WebMarkupContainer("agregatesContainer");
	        AjaxLink agregatesReportLink = new AjaxLink("agregatesReportLink"){
	            public void onClick(AjaxRequestTarget target){

	            	agregatesReportModal.setContent(new AggregatesReportPanel("content", agregatesReportModal, itemSearch));
	                agregatesReportModal.setTitle("");
	                agregatesReportModal.show(target);
	            }
	        };
	        agregatesContainer.add(agregatesReportLink);
	        add(agregatesContainer.setVisible(showAgregates));
//    	}
//    	else{
//    		add(new EmptyPanel("agregatesReportLink"));
//    	}
		
	}

	//======================== RECORDS COUNT ==================================
    private void addRecordsCount(){        
        BreadCrumbLink link = new BreadCrumbLink("count", getBreadCrumbModel()) {
			@Override
			protected IBreadCrumbParticipant getParticipant(String componentId) {
				// return to item search form
				itemSearch.setCurrentPage(0);
				return new ItemSearchFormPanel(componentId, getBreadCrumbModel(), itemSearch);
			}		
		};
        link.add(new Label("count", resultCount + ""));
        String resultCountMessage = resultCount == 1 ? "item_list.recordFound" : "item_list.recordsFound";
        link.add(new Label("recordsFound", localize(resultCountMessage)));        
        add(link);  
    }
    
    private class StaticLink extends WebMarkupContainer
    {
        public StaticLink(String id, IModel<?> model)
        {
            super(id, model);
            add(new AttributeModifier("href", true, model));
        }
    }
    //======================== PAGINATION ==================================
    private void addPagination(){

        
        WebMarkupContainer pagination = new WebMarkupContainer("pagination");
        
        if(pageCount > 1) {
        	IndicatingAjaxLink prevOn = new IndicatingAjaxLink("prevOn") {
                public void onClick(AjaxRequestTarget target) {
                    itemSearch.setCurrentPage(currentPage - 1);
                    setCurrentItemSearch(itemSearch);
                    // TODO avoid next line, refresh pagination only
                	
                    ItemList itemList = new ItemList(ItemList.this.getId(), getBreadCrumbModel());
                    ItemList.this.replaceWith(itemList);
                    target.addComponent(itemList);
                }
            };    
        	prevOn.add(new Label("prevOn", "<<"));
        	
            Label prevOff = new Label("prevOff", "<<");
            if(currentPage == 0) {
                prevOn.setVisible(false);
            } else {
                prevOff.setVisible(false);
            }
            pagination.add(prevOn);
            pagination.add(prevOff);
            
            List<Integer> pageNumbers = new ArrayList<Integer>(pageCount);
            for(int i = 0; i < pageCount; i++) {
                pageNumbers.add(new Integer(i));
            }
            
            ListView pages = new ListView("pages", pageNumbers) {
                protected void populateItem(ListItem listItem) {
                    final Integer i = (Integer) listItem.getModelObject();
                    String pageNumber = i + 1 + "";
                    
                	IndicatingAjaxLink pageOn = new IndicatingAjaxLink("pageOn") {
                        public void onClick(AjaxRequestTarget target) {
                            itemSearch.setCurrentPage(i);
                            setCurrentItemSearch(itemSearch);
                            // TODO avoid next line, refresh pagination only
                        	
                            ItemList itemList = new ItemList(ItemList.this.getId(), getBreadCrumbModel());
                            ItemList.this.replaceWith(itemList);
                            target.addComponent(itemList);
                        }
                    };    
                    pageOn.add(new Label("pageOn", pageNumber));
                    Label pageOff = new Label("pageOff", pageNumber);
                    if(i == currentPage) {
                        pageOn.setVisible(false);
                    } else {
                        pageOff.setVisible(false);
                    }
                    listItem.add(pageOn);
                    listItem.add(pageOff);
                }
            };
            pagination.add(pages);
            
            
        	IndicatingAjaxLink nextOn = new IndicatingAjaxLink("nextOn") {
                public void onClick(AjaxRequestTarget target) {
                    itemSearch.setCurrentPage(currentPage + 1);
                    setCurrentItemSearch(itemSearch);
                    // TODO avoid next line, refresh pagination only
                	
                    ItemList itemList = new ItemList(ItemList.this.getId(), getBreadCrumbModel());
                    ItemList.this.replaceWith(itemList);
                    target.addComponent(itemList);
                }
            };    
        	nextOn.add(new Label("nextOn", ">>"));
            Label nextOff = new Label("nextOff", ">>");
            if(currentPage == pageCount - 1) {
                nextOn.setVisible(false);
            } else {
                nextOff.setVisible(false);
            }
            pagination.add(nextOn);
            pagination.add(nextOff);
        } else { // if pageCount == 1
            pagination.setVisible(false);
        }
        
        add(pagination);
    }
    
    //========================== EXCEL EXPORT ==============================
    private void addExcelExport(){
        add(new Link("export") {
            public void onClick() {
                // temporarily switch off paging of results
                itemSearch.setPageSize(-1);
                final ExcelUtils eu = new ExcelUtils(getCalipso().findItems(itemSearch), itemSearch, this);
                // restore page size
                itemSearch.setPageSize(pageSize);
                getRequestCycle().scheduleRequestHandlerAfterCurrent(new IRequestHandler() {
					public void respond(IRequestCycle requestCycle) {
                        WebResponse r = (WebResponse) requestCycle.getResponse();
                        r.setAttachmentHeader("calipso-export-"+DateUtils.formatForFileName()+".xls");
                        try {                            
                            eu.exportToExcel().write(r.getOutputStream());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
					public void detach(IRequestCycle requestCycle) {
					}
                });
            }
        });
    }
    
    //========================== SAVE SEARCH ===============================
    private void addSaveSearch(){
        // --- Search Container ------------------------------------
        saveSearchContainer = new WebMarkupContainer("saveSearchContainer");
        saveSearchContainer.setOutputMarkupId(true);
        add(saveSearchContainer);
        
        // ---- Save Link -----------------------------------------

        AjaxLink saveSearchLink = new AjaxLink("saveSearchLink"){
			@Override
			public void onClick(AjaxRequestTarget target) {
				//Form panel is not exists
				if (saveSearchPanelContainer!=null){
					//Remove container
					saveSearchContainer.remove(saveSearchPanelContainer);
					saveSearchPanelContainer = null;

					//Add form panel
					saveSearchFormPanel = new SaveSearchFormPanel("saveSearchFormPanel");
					saveSearchFormPanel.setCancelTargetComponent(saveSearchContainer);
					saveSearchContainer.add(saveSearchFormPanel);
				}//if
				else{ //from panel exists
					//Remove form panel
					saveSearchContainer.remove(saveSearchFormPanel);
					saveSearchFormPanel = null;
					
					//Add container
					saveSearchPanelContainer = new WebMarkupContainer("saveSearchFormPanel");
					saveSearchContainer.add(saveSearchPanelContainer);
				}//else
				
				target.addComponent(saveSearchContainer.setVisible(!getPrincipal().isAnonymous()));
			}//onClick
        };
        saveSearchContainer.add(saveSearchLink);

        // ---- Save Search Panel ---------------------------------
        saveSearchPanelContainer = new WebMarkupContainer("saveSearchFormPanel");
        //add(saveSearchPanelContainer);
        saveSearchContainer.add(saveSearchPanelContainer);
    }
    
    
    private void addItemsTable(){
    	//if no results, don't render the empty table
//    	if(resultCount == 0){
//    		add(new WebMarkupContainer("headings").setVisible(false));
//    		add(new WebMarkupContainer("itemList").setVisible(false));    		
//    		return;
//    	}
    	
    	//====================== HEADER ========================================
        final List<ColumnHeading> columnHeadings = itemSearch.getColumnHeadingsToRender();
        
        ListView headings = new ListView("headings", columnHeadings) {
            protected void populateItem(ListItem listItem) {
                final ColumnHeading ch = (ColumnHeading) listItem.getModelObject();
                Link headingLink = new Link("heading") {
					public void onClick() {
						if (ch.isDbField()) {
							doSort(ch.getNameText());
						}// if
					}
				};
				
                listItem.add(headingLink);
                String label = ch.isField() ? localize(ch.getLabel()) : localize("item_list." + ch.getNameText());
                headingLink.add(new Label("heading", label));
                if (ch.getNameText().equals(itemSearch.getSortFieldName())) {
                    String order = itemSearch.isSortDescending() ? "order-down" : "order-up";
                    listItem.add(new SimpleAttributeModifier("class", order));
                }
            }
        };
        add(headings);
        
        //======================== ITEMS =======================================
        
        final long selectedItemId = itemSearch.getSelectedItemId();
        
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
        
        ListView itemList = new ListView("itemList", itemListModel) {
        	final private Date NOW = new Date();
            protected void populateItem(ListItem listItem) {
                // cast to AbstactItem - show history may be == true
                final AbstractItem item = (AbstractItem) listItem.getModelObject();
                
                if (selectedItemId == item.getId()) {
                    listItem.add(new SimpleAttributeModifier("class", "selected"));
                } else if(listItem.getIndex() % 2 == 1) {
                    listItem.add(sam);
                }                

                final boolean showHistory = itemSearch.isShowHistory();

                ListView fieldValues = new ListView("columns", columnHeadings) {
                    protected void populateItem(ListItem listItem) {
                        ColumnHeading ch = (ColumnHeading) listItem.getModelObject();
                        IModel value = null;
                
                    	Map<StdField.Field, StdFieldMask> fieldMaskMap = null;
                    	if (getPrincipal()!=null){
                    		Space space = getCalipso().loadSpace(item.getSpace().getId());
                    		fieldMaskMap = StdFieldsUtils.getStdFieldsForSpace(getPrincipal().getStdFields(), space, this);
                    	}
                        
                        if(ch.isField() && item.getCustomValue(ch.getField()) == null){
                        	logger.warn("Found field '"+ch.getNameText()+"' but it has no value. Perhaps it was added after initial space creation");
                        }
                        else if(ch.isField()) {
                        	if (ch.getField().getName().isOptionsType()){
                        		value = new ResourceModel("CustomAttributeLookupValue."+item.getCustomValue(ch.getField()).toString()+".name");
//                        		value = new Model(item.getCustomValue(ch.getField().getName()));
                        	}
                        	else if (ch.getField().getName().isDate()){
                        		value = new Model(DateUtils.format(DateUtils.convert(item.getCustomValue(ch.getField()).toString())));
//                        		value = new Model(item.getCustomValue(ch.getField().getName()));
                        	}
                        	else if (ch.getField().getName().isCountry()){
                        		value = new Model(localize("country." + item.getCustomValue(ch.getField())));
//                        		value = new Model(item.getCustomValue(ch.getField().getName()));
                        	}
                        	else if (ch.getField().getName().isUser()){                        		
                        		value = new Model(((User)item.getCustomValue(ch.getField())).getDisplayValue());
//                        		value = new Model(item.getCustomValue(ch.getField().getName()));
                        	}
                        	else if (ch.getField().getName().isOrganization()){
                        		value = new Model(((Organization)item.getCustomValue(ch.getField())).getName());
//                        		value = new Model(item.getCustomValue(ch.getField().getName()));
                        	}
                        	else if (ch.getField().getName().isFile()){
                        		value = new ResourceModel(item.getCustomValue(ch.getField()) != null ? "asset.customAttribute.yes" : "asset.customAttribute.no");
                        	}
                        	else{
                        		value = new Model(item.getCustomValue(ch.getField()).toString());
                        	}
                        } 
                        else {
                            // TODO optimize if-then for performance
                            Name name = ch.getName();
                            if(name == ID) {
                            	final String UniqueRefId = item.getUniqueRefId();
                                Fragment refIdFrag = new Fragment("column", "refId", this);
                                listItem.add(refIdFrag);

                                Link refIdLink = new BreadCrumbLink("refId", getBreadCrumbModel()){
                            		protected IBreadCrumbParticipant getParticipant(String componentId){                                       
                        				return new ItemViewPanel(componentId, getBreadCrumbModel(), UniqueRefId);
                            	    }
                            	};
                                
                                refIdFrag.add(refIdLink);
                                refIdLink.add(new Label("refId", UniqueRefId));
                                if (showHistory) {                                                                                                            
                                    int index = ((History) item).getIndex();
                                    if (index > 0) {
                                        refIdFrag.add(new Label("index", " (" + index + ")"));
                                    } else {
                                        refIdFrag.add(new WebMarkupContainer("index").setVisible(false));
                                    }
                                } else {                                                                           
                                    refIdFrag.add(new WebMarkupContainer("index").setVisible(false));
                                }                                                                
                                return;
                            } else if(name == SUMMARY) {
                                value = new PropertyModel(item, "summary");
                            } else if(name == DETAIL) {                                
                                if(showHistory) {
                                    Fragment detailFrag = new Fragment("column", "detail", this);
                                    final History history = (History) item;
                                    // TODO
                                    //detailFrag.add(new AttachmentLinkPanel("attachment", history.getAttachment()));
                                    if (history.getIndex() > 0) {
                                        detailFrag.add(new Label("detail", new PropertyModel(history, "comment")));
                                    } else {
                                        detailFrag.add(new Label("detail", new PropertyModel(history, "detail")));
                                    }
                                    listItem.add(detailFrag);
                                    return;
                                } else {                                    
                                    value = new PropertyModel(item, "detail");                                    
                                }                               
                            } else if(name == LOGGED_BY) {
                            	//original plain name
                                //value = new PropertyModel(item, "loggedBy.name");
                            	
                            	listItem.add(new UserViewLink("column", getBreadCrumbModel(), item.getLoggedBy()));
                            	
                            	return;
                            	
                            }  else if(name == REPORTED_BY) {
                            	listItem.add(new UserViewLink("column", getBreadCrumbModel(), item.getReportedBy()));
                            	return;
                            }else if(name == STATUS) {
                                value = new PropertyModel(item, "statusValue");
                            } else if(name == ASSIGNED_TO) {
                            	if(item.getAssignedTo() == null 
                            			&& item.getStatus() != null
                            			&& item.getStatus().intValue() != State.CLOSED){
                            		listItem.add(new Label("column", localize("item.unassigned")).add(new SimpleAttributeModifier("class", "unassigned")));
                            		return;
                            	}
                            	else{
                            		listItem.add(new UserViewLink("column", getBreadCrumbModel(), item.getAssignedTo()));
                            		return;
                            	}
                                // value = new PropertyModel(item, "assignedTo.name");
                            } 
                            else if(name == TOTAL_RESPONSE_TIME) {
                            	value = new PropertyModel(item, "totalResponseTime");
                            }
                            else if(name == TIME_FROM_CREATION_TO_FIRST_REPLY) {
                            	if ((fieldMaskMap!=null && fieldMaskMap.get(StdField.Field.TIME_FROM_CREATION_TO_FIRST_REPLY)!=null && !fieldMaskMap.get(StdField.Field.TIME_FROM_CREATION_TO_FIRST_REPLY).getMask().equals(StdFieldMask.Mask.HIDDEN))){
                            		value = new Model(ItemUtils.formatEffort(item.getTimeFromCreationToFirstReply(), localize("item_list.days"), localize("item_list.hours"), localize("item_list.minutes")));
                            	}//if
                            	else{
                            		value = new Model(localize("fieldAccess.noReadAccess"));
                            	}
                            }
                            else if(name == TIME_FROM_CREATION_TO_CLOSE) {
                            	if ((fieldMaskMap!=null && fieldMaskMap.get(StdField.Field.TIME_FROM_CREATION_TO_CLOSE)!=null && !fieldMaskMap.get(StdField.Field.TIME_FROM_CREATION_TO_CLOSE).getMask().equals(StdFieldMask.Mask.HIDDEN))){
                            		value = new Model(ItemUtils.formatEffort(item.getTimeFromCreationToClose(), localize("item_list.days"), localize("item_list.hours"), localize("item_list.minutes")));
                            	}//if
                            	else{
                            		value = new Model(localize("fieldAccess.noReadAccess"));
                            	}
                            }
                            else if(name == PLANNED_EFFORT) {
                            	if ((fieldMaskMap!=null && fieldMaskMap.get(StdField.Field.PLANNED_EFFORT)!=null && !fieldMaskMap.get(StdField.Field.PLANNED_EFFORT).getMask().equals(StdFieldMask.Mask.HIDDEN))){
                            		value = new Model("");
                            		if (item.getPlannedEffort()!=null){
                            			//value = new Model(ItemUtils.formatEffort(item.getPlannedEffort() * 60 , localize("item_list.days"), localize("item_list.hours"), localize("item_list.minutes")));
                            			value = new Model(new Effort(item.getPlannedEffort()).formatEffort(localize("item_list.days"), localize("item_list.hours"), localize("item_list.minutes")));
                            		}//if
                            	}//if
                            	else{
                            		value = new Model(localize("fieldAccess.noReadAccess"));
                            	}
                            }
                            else if(name == DUE_TO) {
                            	//logger.info("fieldMaskMap.get(StdField.Field.DUE_TO): "+fieldMaskMap.get(StdField.Field.DUE_TO));
                            	if ((fieldMaskMap!=null && fieldMaskMap.get(StdField.Field.DUE_TO)!=null 
                            			&& !fieldMaskMap.get(StdField.Field.DUE_TO).getMask().equals(StdFieldMask.Mask.HIDDEN))){
                            		//logger.info("DUE_TO 1");
                            		// value = new Model(DateUtils.format(item.getDueTo()));
                            		// if not closed yet
                            		if(item.getStatus() != State.CLOSED){
                            			//logger.info("DUE_TO 2");
                            			value = new Model(HumanTimeDurationConverter.denormalize(item.getDueToUserFriendly(NOW), this.getLocalizer(), this));
                            			// add style if needed
                            			if (item.getDueTo()!=null && item.getDueTo().before(Calendar.getInstance().getTime())){
                            				//logger.info("DUE_TO 3");
                        					listItem.add(new SimpleAttributeModifier("class", "dueToDate-alarm"));
                        				}//if
                        				else if (item.getDueTo()!=null && item.getPlannedEffort()!=null && item.getDueTo().after(NOW)){
                        					//logger.info("DUE_TO 4");
                        					DateTime dueToDateTime = new DateTime(item.getDueTo());
                        					DateTime nowDateTime = new DateTime(NOW);
                        					long restTimeToDueTo = DateTime.diff(nowDateTime, dueToDateTime).inSeconds()/60;
                        					if (restTimeToDueTo < item.getPlannedEffort().longValue()){
                        						//logger.info("DUE_TO 5");
                        						listItem.add(new SimpleAttributeModifier("class", "dueToDate-warning"));
                        					}//if
                        				} 
                            		}
                            		else{
                            			//logger.info("DUE_TO 6");
                            			value = new Model("-");
                            		}
                            	}//if
                            	else{
                            		value = new Model(localize("fieldAccess.noReadAccess"));
                            	}//else
                				                           	
                            }                            
                            else if(name == ASSET_TYPE){
                            	ItemAssetTypesPanel itemAssetTypesPanel = new ItemAssetTypesPanel("assetTypesPanel", item);
                            	Fragment assetTypesFragment = new Fragment("column", "assetTypesFragment", this);
                            	assetTypesFragment.add(itemAssetTypesPanel);
                            	listItem.add(assetTypesFragment);
                            	return;
                            } else if(name == TIME_STAMP) {
                                value = new Model(DateUtils.formatTimeStamp(item.getTimeStamp()));
                            } else if(name == SPACE) {
                                if(showHistory) {
                                    value = new PropertyModel(item, "parent.space.name");
                                } else {
                                    value = new PropertyModel(item, "space.name");
                                }
                            } else {
                                throw new RuntimeException("Unexpected name: '" + name + "'");
                            }
                        }
                        Label columnValue = new Label("column", value);
                        if (ch.getName() == TIME_STAMP || (ch.isField() && ch.getField().getName().isDate())){
                        	columnValue.add(new SimpleAttributeModifier("class", "date"));
                        }
                        listItem.add(columnValue);
                    }
                };
                listItem.add(fieldValues);
            }
        };
        add(itemList);
        
        //The no "data found message" should have as "colspan" value, the number of the columns of the results table 
        if (resultCount==0){
        	WebMarkupContainer noData = new WebMarkupContainer("noData");
        	add(noData);
        	WebMarkupContainer noDataCell = new WebMarkupContainer("noDataCell");
        	noDataCell.add(new SimpleAttributeModifier("colspan", String.valueOf(columnHeadings.size())));
        	noData.add(noDataCell);
        }
        else{
        	add(new WebMarkupContainer("noData").setVisible(false));
        }
    }

	///////////////////////////////////////////////////////////////////////////////////////

    private void doSort(String sortFieldName) {
        itemSearch.setCurrentPage(0);
        if (itemSearch.getSortFieldName().equals(sortFieldName)) {
            itemSearch.toggleSortDirection();
        } else {
            itemSearch.setSortFieldName(sortFieldName);
            itemSearch.setSortDescending(false);
        }
    }//doSort
}