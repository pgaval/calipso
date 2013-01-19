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

import static gr.abiss.calipso.domain.ItemItem.DEPENDS_ON;
import static gr.abiss.calipso.domain.ItemItem.DUPLICATE_OF;
import static gr.abiss.calipso.domain.ItemItem.RELATED;
import gr.abiss.calipso.domain.Attachment;
import gr.abiss.calipso.domain.Effort;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.FieldGroup;
import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.ItemItem;
import gr.abiss.calipso.domain.ItemRenderingTemplate;
import gr.abiss.calipso.domain.Metadata;
import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.RoleSpaceStdField;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.State;
import gr.abiss.calipso.domain.StdField;
import gr.abiss.calipso.domain.StdFieldMask;
import gr.abiss.calipso.domain.StdFieldType;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.util.DateTime;
import gr.abiss.calipso.util.DateUtils;
import gr.abiss.calipso.util.ItemUtils;
import gr.abiss.calipso.util.PdfUtils;
import gr.abiss.calipso.util.ReflectionUtils;
import gr.abiss.calipso.util.StdFieldsUtils;
import gr.abiss.calipso.wicket.asset.ItemAssetsPanel;
import gr.abiss.calipso.wicket.components.PdfRequestTarget;
import gr.abiss.calipso.wicket.components.formfields.FieldConfig;
import gr.abiss.calipso.wicket.components.formfields.MultipleValuesTextField;
import gr.abiss.calipso.wicket.components.viewLinks.UserViewLink;
import gr.abiss.calipso.wicket.fileUpload.AttachmentDownLoadableLinkPanel;
import gr.abiss.calipso.wicket.hlpcls.RelateLink;
import gr.abiss.calipso.wicket.hlpcls.SpaceAssetAdminLink;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;



/**
 * 
 * panel for showing the item read-only view
 * 
 * 
 * 
 */
public class ItemView extends BasePanel {    
	
	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(ItemView.class);
	private Item item;
    private boolean hideLinks;
    private long itemId;
    //private RelateLink relateLink1, relateLink2;

    //constructor used in ItemRelatePanel and ItemRelateRemovePage
    public ItemView(String id, ItemRenderingTemplate tmpl, final Item item) {                
        this(id, null, tmpl, item, true);
        add(new WebMarkupContainer("backLink"));
        this.item = item;
    }

    public ItemView(String id, IBreadCrumbModel breadCrumbModel, ItemRenderingTemplate tmpl, final Item item, boolean hideLinks) {                
        super(id, breadCrumbModel);
        addOrReplace(new WebMarkupContainer("backLink"));
        this.hideLinks = hideLinks;
        if(item != null){
        	this.itemId = item.getId();
        	this.item = getCalipso().loadItem(itemId);
        }
        if(this.item == null){
        	logger.warn("Cant figure out current item, this.item: "+this.item+", provided id: "+this.itemId);
        }
        addComponents(tmpl, item);
    }


	public long getItemId(){
    	return this.itemId;
    }

    public String getUserName(){
    	return getPrincipal().getName();
    }

    public String getCustomFieldValue(String fieldName){
    	String customValue = "";
    	
    	try{
    		Field field = new Field(fieldName);
    		customValue = getCalipso().loadItem(itemId).getCustomValue(field).toString();
    	}
    	catch (Exception e) {
			
		}

    	return customValue;
    }

    public String getCurrentDateTime(){
    	return new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(Calendar.getInstance().getTime());
    }
    private void addFieldValueDisplay(final Item item,
			final SimpleAttributeModifier sam,
			final SimpleDateFormat dateFormat, ListItem listItem) {
		if(listItem.getIndex() % 2 == 0) {
            listItem.add(sam);
        }
        Field field = (Field) listItem.getModelObject();
        if(field.getCustomAttribute() == null){
			field.setCustomAttribute(getCalipso().loadItemCustomAttribute(getCurrentSpace(), field.getName().getText()));
		}
        Field.Name fieldName = field.getName();
        FieldConfig fieldConfig = field.getXmlConfig();
        
        boolean showHelpText = fieldConfig != null && fieldConfig.isShowHelpInPdf();
        //logger.info("field: "+field.getLabel());
        
        String i18nedFieldLabelResourceKey = item.getSpace().getPropertyTranslationResourceKey(fieldName.getText());
        //logger.info("i18nedFieldLabelResourceKey: "+i18nedFieldLabelResourceKey+", field.getName().getText(): "+fieldName.getText());
        listItem.add(new Label("fieldLabel", localize(i18nedFieldLabelResourceKey)));
        // add help text?
        addVelocityTemplatePanel(listItem, "htmlDescriptionContainer", "htmlDescription", showHelpText?field.getCustomAttribute().getHtmlDescription():null, null, true);
		
    	Serializable value =  item.getCustomValue(field);
    	if(field.getName().isCountry() && value != null){// country
    		//logger.debug("Country field, name: '"+field.getLabel()+"', value: '"+value+"'");
    		listItem.add(new Label("fieldValue", localize("country."+value)));
    	}
    	else if(field.getName().isUser() && value != null){// user
    		listItem.add(new Label("fieldValue", ((User) value).getFullName()));
    	}
    	else if(field.getName().isOrganization() && value != null){// organization
    		listItem.add(new Label("fieldValue", ((Organization) value).getName()));
    	}
    	else if(field.isDropDownType()){
    		Object o = item.getValue(fieldName);
    		listItem.add(new Label("fieldValue", o!=null?new ResourceModel("CustomAttributeLookupValue."+o.toString()+".name"):null));
    	}
    	else if(field.getName().isDate()){
    		Date date = (Date) item.getValue(fieldName);
    		listItem.add(new Label("fieldValue", date != null ? dateFormat.format(date) : ""));
    	}
    	else{
    		Label label;
    		if(field.isMultivalue()){
    			label = new Label("fieldValue", MultipleValuesTextField.toHtmlSafeTable(((String)value), field.getXmlConfig(), this.getLocalizer(), this));
    			label.setEscapeModelStrings(false);
    			label.add(new AttributeAppender("class", new Model("content"), " "));
    		}
    		else{
    			label = new Label("fieldValue", (String)value);
    		}
    		listItem.add(label);
    	}
	}
    private void addComponents(ItemRenderingTemplate tmpl, final Item item) {
    	//Edit item is only possible for "global" administrator and current space administrator
    	User currentUser = getPrincipal();
    	Space currentSpace = getCurrentSpace();
    	//add(new Label("title", this.localize(currentSpace)+": "+item.getStatusValue()));
    	
    	
    	WebMarkupContainer editContainer = new WebMarkupContainer("editContainer");
    	add(editContainer.setRenderBodyOnly(true));
    	editContainer.add(new Link("edit") {
			@Override
			public void onClick() {
				//breadcrum must be activated in the active panel, that is ItemViewPanel
				((BreadCrumbPanel)getBreadCrumbModel().getActive()).activate(new IBreadCrumbPanelFactory() {
					public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
						return new ItemFormPanel(componentId, breadCrumbModel, item.getId());
					}				
				});
			}		
		});
    	editContainer.setVisible(currentUser.isGlobalAdmin() || currentUser.isSpaceAdmin(currentSpace));
    	if(hideLinks){
    		editContainer.setVisible(false);
    	}
    	
    	add(new Link("printToPdf") {
            public void onClick() {
            	// TODO: pickup print template from DB if appropriate
    			// TODO: is this needed?
    	        //panels that change with navigation
    			//ItemViewPage itemViewPage = new ItemViewPage(refId, null);
    	        String markup = "error generating report";
    	        CharSequence resultCharSequence = renderPageHtmlInNewRequestCycle(ItemTemplateViewPage.class, new PageParameters("0=" + item.getUniqueRefId()));
    	        
    	        markup = resultCharSequence.toString();
    	        //logger.info("printToPdf: "+markup);
            	getRequestCycle().scheduleRequestHandlerAfterCurrent(

            			new PdfRequestTarget(
            					PdfUtils.getPdf(getCalipso(), item, markup, ItemView.this), item.getRefId()));
            }
        });
    	
    	String refId = item.getUniqueRefId();

    	Link refIdLink = new BookmarkablePageLink("uniqueRefId", ItemViewPage.class, new PageParameters("0=" + refId));
    	add(refIdLink.add(new Label("uniqueRefId", refId)));

    	

    	//Relate Link ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    	
    	WebMarkupContainer relatedContainer = new WebMarkupContainer("relatedContainer");
    	add(relatedContainer);
    	RelateLink relateLink1 = new RelateLink("relate1", item, getBreadCrumbModel(), currentUser, ItemView.this);
        //relateLink1.setVisible(!hideLinks);
        relatedContainer.add(relateLink1);
        
        if(currentUser.hasRegularRoleForSpace(item.getSpace())){
        	relatedContainer.setVisible(true);
    	}
        else{
        	relatedContainer.setVisible(false);
        }
        if(item.getRelatedItems() != null) {        
            add(new ListView("relatedItems", new ArrayList(item.getRelatedItems())) {            
                protected void populateItem(ListItem listItem) {
                    final ItemItem itemItem = (ItemItem) listItem.getModelObject();
                    String message = null;
                    if(itemItem.getType() == DUPLICATE_OF) {
                        message = localize("item_view.duplicateOf");
                    } else if (itemItem.getType() == DEPENDS_ON) {
                        message = localize("item_view.dependsOn");
                    } else if (itemItem.getType() == RELATED){
                        message = localize("item_view.relatedTo");                  
                    }
                    final String refId = itemItem.getRelatedItem().getUniqueRefId();
                    if(hideLinks) {
                        message = message + " " + refId;
                    }
                    listItem.add(new Label("message", message));
                    Link link = new Link("link") {
                        public void onClick() {
                            setResponsePage(ItemViewPage.class, new PageParameters("0=" + refId));
                        }
                    };
                    link.add(new Label("refId", refId));
                    link.setVisible(!hideLinks);
                    listItem.add(link);
                    listItem.add(new Link("remove") {
                        public void onClick() {
                            setResponsePage(new ItemRelateRemovePage(item.getId(), itemItem));
                        }
                    }.setVisible(!hideLinks));
                }
            });
        } else {
            add(new WebMarkupContainer("relatedItems").setVisible(false));
        }
        
        if(item.getRelatingItems() != null) {
            add(new ListView("relatingItems", new ArrayList(item.getRelatingItems())) {            
                protected void populateItem(ListItem listItem) {
                    final ItemItem itemItem = (ItemItem) listItem.getModelObject();
                    // this looks very similar to related items block above
                    // but the display strings could be different and in future handling of the 
                    // inverse of the bidirectional link could be different as well                    
                    String message = null;
                    if(itemItem.getType() == DUPLICATE_OF) {
                        message = localize("item_view.duplicateOfThis");
                    } else if (itemItem.getType() == DEPENDS_ON) {
                        message = localize("item_view.dependsOnThis");
                    } else if (itemItem.getType() == RELATED){
                        message = localize("item_view.relatedToThis");                  
                    }
//                    final String refId = itemItem.getItem().getRefId();
                    final String refId = itemItem.getItem().getUniqueRefId();
                    if(hideLinks) {
                        message = refId + " " + message;
                    }                    
                    listItem.add(new Label("message", message));
                    Link link = new Link("link") {
                        public void onClick() {
                            setResponsePage(ItemViewPage.class, new PageParameters("0=" + refId));
                        }
                    };
                    link.add(new Label("refId", refId));
                    link.setVisible(!hideLinks);
                    listItem.add(link);
                    listItem.add(new Link("remove") {
                        public void onClick() {
                            setResponsePage(new ItemRelateRemovePage(item.getId(), itemItem));
                        }
                    }.setVisible(!hideLinks)); 
                }
            });
        } else {
            add(new WebMarkupContainer("relatingItems").setVisible(false));
        }
        
        add(new Label("status", new PropertyModel(item, "statusValue")));
        
        //user profile view link
        add(new UserViewLink("loggedBy", getBreadCrumbModel(), item.getLoggedBy()).setVisible(!getPrincipal().isAnonymous()));
        
        if(item.getAssignedTo()!=null){
        	add(new UserViewLink("assignedTo", getBreadCrumbModel(), item.getAssignedTo()).setVisible(!getPrincipal().isAnonymous()));
        }
        else{
        	Label assignedToLabel = new Label("assignedTo", localize("item.unassigned"));
        	if(item.getStatus() != State.CLOSED){
        		assignedToLabel.add(new SimpleAttributeModifier("class", "unassigned"));
        	}
        	add(assignedToLabel);
        }
        
        add(new UserViewLink("reportedBy", getBreadCrumbModel(), item.getReportedBy()));
        
        WebMarkupContainer summaryContainer = new WebMarkupContainer("summaryContainer");
        summaryContainer.add(new Label("summary", new PropertyModel(item, "summary")));
        add(summaryContainer.setVisible(item.getSpace().isItemSummaryEnabled()));

        //detail commented out
        //add(new Label("detail", new PropertyModel(item, "detail")).setEscapeModelStrings(false));
        
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
       	// All custom fields of the fields that belong to the item and are viewable for 
        // the session user
        final Map<Field.Name, Field> viewableFieldsMap = item.getViewableFieldMap(currentUser);
        

        
       
        
        // pick up the non-file fields from the list above and put them here for a list view
        final List<Field> noFileViewableFieldNamesList = new ArrayList<Field>();
        // Separate list view for files
        List<Field> fileViewableFieldNamesList = new ArrayList<Field>();
        List<Field> simpleAtachmentViewableFieldNamesList = new ArrayList<Field>();
        for(Field field : viewableFieldsMap.values()){
        	if(!field.getName().isFile()){ // is not file and not hidden (since we got it from getViewbleFieldList)
        		noFileViewableFieldNamesList.add(field);
        	}
        	else{ // if file and not simple attachment, keep for later simpleAtachmentViewableFieldNamesList
        		if(field.getName().getText().equals(Field.FIELD_TYPE_SIMPLE_ATTACHEMENT)){
        			simpleAtachmentViewableFieldNamesList.add(field);
        		}
        		else{
        			fileViewableFieldNamesList.add(field);
        		}
        	}
        }
        Metadata metadata = getCalipso().getCachedMetadataForSpace(item.getSpace());
        final SimpleDateFormat dateFormat = metadata.getDateFormat(Metadata.DATE_FORMAT_LONG);
		List<FieldGroup> fieldGroupsList = metadata.getFieldGroups();
		@SuppressWarnings("unchecked")
		ListView fieldGroups = new ListView("fieldGroups", fieldGroupsList){

			@Override
			protected void populateItem(ListItem listItem) {
				// get group
				FieldGroup fieldGroup = (FieldGroup) listItem.getModelObject();
				listItem.add(new Label("fieldGroupLabel", fieldGroup.getName()));
				List<Field> groupFields = fieldGroup.getFields();
				List<Field> viewbleFieldGroups = new LinkedList<Field>();
				// get group fields
				if(CollectionUtils.isNotEmpty(groupFields)){
					for(Field field : groupFields){
						// is viewalble?
						if(noFileViewableFieldNamesList.contains(field)){
							viewbleFieldGroups.add(field);
						}
					}
				}
				ListView listView = new ListView("fields", viewbleFieldGroups) {
					@SuppressWarnings("deprecation")
					protected void populateItem(ListItem listItem) {
						addFieldValueDisplay(item, sam, dateFormat, listItem);
					}
				};
				if(viewbleFieldGroups.isEmpty()){
					listItem.setVisible(false);
				}
				listView.setReuseItems(true);
				listItem.add(listView.setRenderBodyOnly(true));
				
			}
			
		};
		add(fieldGroups);
		

		// Iterates custom fields than are not type file
//        add(new ListView("CustomFieldsListView", noFileViewableFieldNamesList) {
//            protected void populateItem(ListItem listItem) {
//                addFieldValueDisplay(item, sam, dateFormat, listItem);
//            }
//
//			
//        });
        
        if(fileViewableFieldNamesList.size() > 0){
        	// add custom field type file label
        	add(new Label("fileFieldLabel", localize("files")));
	        //add values for custom fields type file
	        // check for if doesn't exist custom field type file
        	// TODO: user should upload a file for every customField type file
        	// get Attachments
        	ArrayList<Attachment> itemAttachments = item.getAttachments() != null?new ArrayList<Attachment>(item.getAttachments()):new ArrayList();
        	//logger.info("Files to render: "+itemAttachments.size());
	        ListView fileCustomFieldsListView = new ListView("fileCustomFieldsListView", itemAttachments){
				@Override
				protected void populateItem(ListItem listItem) {
					Attachment tmpAttachment = (Attachment)listItem.getModelObject();
					listItem.add( new AttachmentDownLoadableLinkPanel("fileLink", tmpAttachment));
				}
	        };
	        add(fileCustomFieldsListView);
        }else{
        	// add empty label
        	add(new EmptyPanel("fileCustomFieldsListView").setVisible(false));
        	// add custom field type file label
            add(new Label("fileFieldLabel", localize("files")));
        }
        
        // TODO: Dont think this actually checks for user roles rights within the State scope, 
        // plus the *readable* fields are needed instead to feed the historyEntry ("history" ListView) bellow
        //final List<Field> editable = item.getSpace().getMetadata().getEditableFields();
        final List<Field> readable = item.getSpace().getMetadata().getReadableFields(currentUser.getSpaceRoles(item.getSpace()), item.getStatus());
        add(new ListView("labels", readable) {
            protected void populateItem(ListItem listItem) {
                Field field = (Field) listItem.getModelObject();
                listItem.add(new Label("label", field.getLabel()));
            }            
        });

        currentUser.setRoleSpaceStdFieldList(getCalipso().findSpaceFieldsForUser(currentUser));
        Map<StdField.Field, StdFieldMask> fieldMaskMap = currentUser.getStdFieldsForSpace(currentSpace);

        //Get user fields
        List<RoleSpaceStdField> stdFields = currentUser.getStdFields();

        //Standard fields ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        //List of standard Fields
        List<RoleSpaceStdField> standardFields = StdFieldsUtils.filterFieldsBySpace(stdFields, fieldMaskMap, currentSpace);

        //Render Standard field
        add(new ListView("stdFields", standardFields) {
        	protected void populateItem(ListItem listItem) {
        		RoleSpaceStdField stdField = (RoleSpaceStdField) listItem.getModelObject();
        		boolean invisible = stdField.getStdField().getField().getFieldType().getType().equals(StdFieldType.Type.ASSET) || 
            						stdField.getStdField().getField().getFieldType().getType().equals(StdFieldType.Type.ASSETTYPE) ||
            						stdField.getStdField().getField().equals(StdField.Field.ACTUAL_EFFORT); 

    			listItem.add(new Label("label", localize("field." + stdField.getStdField().getField().getName())).setVisible(!invisible));
        		IModel value = new Model("");
	
        		if (stdField.getStdField().getField().getFieldType().getType().equals(StdFieldType.Type.STATISTIC)){
	        		Method method = null;
	        		ReflectionUtils.buildFromProperty("get", stdField.getStdField().getField().getName());
	
	        		try{
	        			method = item.getClass().getMethod(ReflectionUtils.buildFromProperty("get", stdField.getStdField().getField().getName()));
	        			value = new Model(ItemUtils.formatEffort(method.invoke(item), localize("item_list.days"), localize("item_list.hours"), localize("item_list.minutes")));
	        		}
	        		catch(NoSuchMethodException noSuchMethodException){
	        			logger.error(noSuchMethodException);
	        		}
	        		catch(InvocationTargetException invocationTargetException){
	        			logger.error(invocationTargetException);
	        		}
	        		catch(IllegalAccessException illegalAccessException){
	        			logger.error(illegalAccessException);
	        		}
        		}
        		else if (stdField.getStdField().getField().getFieldType().getType().equals(StdFieldType.Type.INFO)){
        			if (stdField.getStdField().getField().equals(StdField.Field.DUE_TO)){
        				value = new Model(new StringBuffer().append(DateUtils.format(item.getStateDueTo())).append(" / ").append(DateUtils.format(item.getDueTo())).toString());
        				if(item.getStatus() != State.CLOSED){
	        				if (item.getDueTo()!=null && item.getDueTo().before(Calendar.getInstance().getTime())){
	        					listItem.add(new SimpleAttributeModifier("class", "dueToDate-alarm"));
	        				}//if
	        				else if (item.getDueTo()!=null && item.getPlannedEffort()!=null && item.getDueTo().after(Calendar.getInstance().getTime())){
	        					DateTime dueToDateTime = new DateTime(item.getDueTo());
	        					DateTime nowDateTime = new DateTime(Calendar.getInstance().getTime());
	        					long restTimeToDueTo = DateTime.diff(nowDateTime, dueToDateTime).inSeconds()/60;
	        					if (restTimeToDueTo < item.getPlannedEffort().longValue()){
	        						listItem.add(new SimpleAttributeModifier("class", "dueToDate-warning"));
	        					}//if
	        				}
        				}
        			}//if
        			else if (stdField.getStdField().getField().equals(StdField.Field.PLANNED_EFFORT)){
        				if (item.getPlannedEffort()!=null){
        					value = new Model(new Effort(item.getPlannedEffort()).formatEffort(localize("item_list.days"), localize("item_list.hours"), localize("item_list.minutes")));
        				}
        			}
        		}
        		
        		listItem.add(new Label("value", value).setVisible(!invisible));
        	}
        });

        //Assets ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        //Space is integrated with asset or space is not but was perhaps in the past integrated and at least one asset was bind with this item
        boolean userCanViewAssets = fieldMaskMap.get(StdField.Field.ASSET)!=null && !fieldMaskMap.get(StdField.Field.ASSET).getMask().equals(StdFieldMask.Mask.HIDDEN);
        boolean spaceIsUIntegratedWithAsset = item.getSpace().isAssetEnabled();
        boolean userCanUpdateAssets = fieldMaskMap.get(StdField.Field.ASSET)!=null && fieldMaskMap.get(StdField.Field.ASSET).getMask().equals(StdFieldMask.Mask.UPDATE);
        boolean userCanAdministrateAssetsForSpace = currentUser!=null && currentUser.getId()!=0 && (currentUser.isGlobalAdmin() || currentUser.isSpaceAdmin(currentSpace));
        boolean userCanSeeComments = currentUser.hasRegularRoleForSpace(currentSpace) || (currentSpace.getItemVisibility().equals(Space.ITEMS_VISIBLE_TO_LOGGEDIN_REPORTERS) && currentUser.isGuestForSpace(currentSpace));
        if(userCanSeeComments){
        	// show history?
        	userCanSeeComments = !(tmpl != null && tmpl.getHideHistory().booleanValue());
        }
        WebMarkupContainer assetsContainer = new WebMarkupContainer("assetsContainer");
        add(assetsContainer.setRenderBodyOnly(true));
        ItemAssetsPanel itemAssetsPanel = new ItemAssetsPanel("itemAssetsViewPanel" , item);

    	WebMarkupContainer editAssetContainer = new WebMarkupContainer("editAssetContainer");
    	add(editContainer.setRenderBodyOnly(true));

    	editAssetContainer.add(new Link("editAsset") {
			@Override
			public void onClick() {
				//breadCrumb must be activated in the active panel, that is ItemViewPanel
				((BreadCrumbPanel)getBreadCrumbModel().getActive()).activate(new IBreadCrumbPanelFactory() {
					public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
						return new ItemAssetFormPanel(componentId, breadCrumbModel, item.getId());
					}
				});
			}		
		});
    	editAssetContainer.setVisible(userCanUpdateAssets);
    	assetsContainer.add(editAssetContainer);
    	
    	if(hideLinks){
    		editAssetContainer.setVisible(false);
    	}
    	// --- Link to Asset administration for assets 
    	SpaceAssetAdminLink spaceAssetAdminLink = new SpaceAssetAdminLink("asset", getBreadCrumbModel()){
			@Override
			public void onLinkActivate() {
				//do nothing
			}
    	};
    	assetsContainer.add(spaceAssetAdminLink);
    	spaceAssetAdminLink.setVisible(userCanAdministrateAssetsForSpace);
    	if(hideLinks){
    		spaceAssetAdminLink.setVisible(false);
    	}
        //Case 1:
        //Current space is integrated with assets and user can view these assets
        //Case 2:
        //Current space is NOT integrated with assets BUT was integrated with assets in the past and user can view these assets
        //============
        //Pseudo code:
        //============
        // if (Case 1 OR Case 2) then 
        //   showAssets();
        // fi
        // else
        //   doNotShowAssets();
        // esle
    	

    	boolean itemInvolvesAssets = itemAssetsPanel.getItemAssets() != null && itemAssetsPanel.getItemAssets().size()>0;
        if ((spaceIsUIntegratedWithAsset && userCanViewAssets) || (!spaceIsUIntegratedWithAsset && itemInvolvesAssets && userCanViewAssets)){
        	itemAssetsPanel.renderItemAssets();
        	assetsContainer.add(itemAssetsPanel);
        }//if
        else{
        	assetsContainer.setVisible(false);
        }//else

        
    	WebMarkupContainer historyContainer = new WebMarkupContainer("historyContainer");

    	historyContainer.add(new WebMarkupContainer("historyComment").setVisible(userCanSeeComments));
        if (item.getHistory() != null && userCanSeeComments) {
        	List<History> history = new ArrayList(item.getHistory());
            historyContainer.add(new ListView("history", history) {
                protected void populateItem(ListItem listItem) {
                    final History h = (History) listItem.getModelObject();
                    
                    //First history entry is empty => Add item detail to first history item for view harmonization.  
                    if (listItem.getIndex()==0){
                    	h.setComment(item.getDetail());
                    	h.setHtmlComment(item.getHtmlDetail());
                    }
                    HistoryEntry historyEntry = new HistoryEntry("historyEntry", getBreadCrumbModel(), h, readable);
                    if(listItem.getIndex() % 2 == 0) {
                    	historyEntry.add(sam);
                    }                    
                    listItem.add(historyEntry);
                }                
            }.setRenderBodyOnly(true));            
        }
        else{
        	historyContainer.add(new WebMarkupContainer("history").add(new WebMarkupContainer("historyEntry")));
        	historyContainer.setVisible(false);
        }
        add(historyContainer);
       
    }
}
