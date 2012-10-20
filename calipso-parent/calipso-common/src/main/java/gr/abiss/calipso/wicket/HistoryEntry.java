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

import gr.abiss.calipso.domain.Attachment;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.Metadata;
import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.StdField;
import gr.abiss.calipso.domain.StdFieldMask;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.util.DateUtils;
import gr.abiss.calipso.util.ItemUtils;
import gr.abiss.calipso.wicket.components.viewLinks.OrganizationViewLink;
import gr.abiss.calipso.wicket.components.viewLinks.UserViewLink;
import gr.abiss.calipso.wicket.fileUpload.AttachmentDownLoadableLinkPanel;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 * @author erasmus
 */
public class HistoryEntry extends BasePanel {
	
	protected static final Logger logger = Logger.getLogger(HistoryEntry.class);
	
	private Attachment displayed;

	@SuppressWarnings("unchecked")
	public HistoryEntry(String id, IBreadCrumbModel breadCrumbModel,
			final History history, final List<Field> editable) {
		super(id, breadCrumbModel);

		// logged By
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		User loggedBy = history.getLoggedBy();
		add(loggedBy != null ? new UserIconPanel("loggedByIcon", getBreadCrumbModel(), loggedBy, true) : new EmptyPanel("loggedByIcon"));
		add(loggedBy != null ? new UserViewLink("loggedBy", getBreadCrumbModel(), loggedBy) : new EmptyPanel("loggedBy"));

		// date formats
		Metadata metadata = getCalipso().getCachedMetadataForSpace(getCurrentSpace());
		final SimpleDateFormat longDateTimeFormat = metadata.getDateFormat(Metadata.DATETIME_FORMAT_LONG);
		final SimpleDateFormat shortDateFormat = metadata.getDateFormat(Metadata.DATE_FORMAT_SHORT);
		// Changed status
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		add(new Label("status", localize("item_view.changedStatus",
				history.getStatusValue())).setVisible(!history.getStatusValue()
				.equals("")));

		// AssignedTo
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		WebMarkupContainer assignedToContainer = new WebMarkupContainer("assignedToContainer");
		add(assignedToContainer);

		if (history.getAssignedTo() != null) {
			assignedToContainer.add(new UserViewLink("assignedToLink", getBreadCrumbModel(), history.getAssignedTo()));
		} else {
			assignedToContainer.setVisible(false);
		}

		// Comment
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		WebMarkupContainer comment = new WebMarkupContainer("comment");
		add(comment);
		// TODO: fix the issue of Colections that history returns and the HTML
		if (history.getAttachments() != null
				&& history.getAttachments().size() > 0) {
			this.renderSighslideDirScript();
			
			comment.add(new ListView("attachmentThumbs", new ArrayList<Attachment>(history.getAttachments())) {
				@Override protected void populateItem(ListItem attachmentItem) {
					Attachment tmp = (Attachment) attachmentItem .getModelObject();
					attachmentItem.add(new AttachmentLinkPanel("attachment", tmp,false)); 
				} 
			});
			
		} else {
			comment.add(new EmptyPanel("attachmentThumbs").setVisible(false));
		}
		// do not escape HTML
		// comment.add(new Label("comment",
		// ItemUtils.fixWhiteSpace(h.getComment())).setEscapeModelStrings(false));
		comment.add(new Label("commentLabel", history.getHtmlComment())
				.setEscapeModelStrings(false));

		add(new Label("timeStamp", longDateTimeFormat.format(history.getTimeStamp())));

		if (editable != null) {
			add(new ListView("fields", editable) {
				protected void populateItem(ListItem listItem) {
					Field field = (Field) listItem.getModelObject();

			    	//logger.info("field: "+field);
			    	//logger.info("field.fieldName: "+field.getName());
			    	//logger.info("field.isDropDownType: "+field.isDropDownType());
			    	//logger.info("field.getFieldType: "+field.getFieldType());
			    	//logger.info("field.getName.getType: "+field.getName().getType());
					Serializable fieldValue = (Serializable) (field.isDropDownType() ? history.getValue(field.getName()) : history.getCustomValue(field));
					// TODO: i118n

					// check if attachment was uploaded and get it from the history
					if (field.getName().isFile()) {
						fieldValue = "";
						Set<Attachment> attachments = history.getAttachments();
						if (attachments != null && attachments.size() > 0) {
							for (Attachment attachment : attachments) {
								if (attachment.getFileName().startsWith(
										field.getLabel())) {
									displayed = attachment;
									fieldValue = field.getLabel();
									break;
								}
							}
						}
					}

					// if empty, dont render any change info
					
					if (fieldValue == null || fieldValue.equals("")) {
						listItem.add(new WebMarkupContainer("field").setVisible(false));
						listItem.add(new EmptyPanel("fileLink")).setVisible(false);
					} else if (field.isDropDownType()) {
						listItem.add(new Label("field", localize("item_view.changedCustomAttribute", field.getLabel(), localize("CustomAttributeLookupValue."+fieldValue+".name"))));
						listItem.add(new EmptyPanel("fileLink").setVisible(false));
					} 
					// if file, render file specific text
					else if (field.getName().isFile()) {
						AttachmentDownLoadableLinkPanel fileLink = 
							new AttachmentDownLoadableLinkPanel("fileLink", displayed);
						listItem.add(new Label("field", 
								localize("item_view.changedCustomFile",field.getLabel())));
						listItem.add(fileLink);
					} 
					// if file, render country specific text
					else if (field.getName().isCountry()) {
						listItem.add(new Label("field", localize("item_view.changedCustomAttribute", field.getLabel(), localize("country."+fieldValue))));
						listItem.add(new EmptyPanel("fileLink").setVisible(false));
					} 
					// if file, render user specific text
					else if (field.getName().isUser()) {
						//if is user render link
						if(getBreadCrumbModel() != null){
							listItem.add(new UserViewLink("field",getBreadCrumbModel(), (User)fieldValue));
						}else{
							listItem.add(new Label("field", localize("item_view.changedCustomAttribute", field.getLabel(), ((User) fieldValue).getDisplayValue())));	
						}
						listItem.add(new EmptyPanel("fileLink").setVisible(false));
					} 
					// if file, render user specific text
					else if (field.getName().isOrganization()) {
						if(getBreadCrumbModel() != null){
							listItem.add(new OrganizationViewLink("field",getBreadCrumbModel(), (Organization)fieldValue));
						}else{
							listItem.add(new Label("field", localize("item_view.changedCustomAttribute", field.getLabel(), ((Organization) fieldValue).getName())));
						}
							listItem.add(new EmptyPanel("fileLink").setVisible(false));
					}
					// if date
					else if (field.isDateType()) {
						listItem.add(new Label("field", localize("item_view.changedCustomAttribute", field.getLabel(), shortDateFormat.format((Date) history.getValue(field.getName())))));
						listItem.add(new EmptyPanel("fileLink").setVisible(false));
					} 
					// else render generic change info text
					else {
						listItem.add(new Label("field", localize("item_view.changedCustomAttribute", field.getLabel(), fieldValue.toString())));
						listItem.add(new EmptyPanel("fileLink").setVisible(false));
					}
				}
			});
		} else {
			add(new WebMarkupContainer("fields").setVisible(false));
		}

		// Common Fields Access
		getPrincipal().setRoleSpaceStdFieldList(
				getCalipso().findSpaceFieldsForUser(getPrincipal()));
		Map<StdField.Field, StdFieldMask> fieldMaskMap = getPrincipal()
				.getStdFieldsForSpace(getCurrentSpace());

		// Due To
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		if (fieldMaskMap.get(StdField.Field.DUE_TO) != null
				&& !fieldMaskMap.get(StdField.Field.DUE_TO).getMask()
						.equals(StdFieldMask.Mask.HIDDEN)
				&& history.getDueTo() != null) {
			IModel value = new Model(DateUtils.format(history.getDueTo()));
			add(new Label("dueTo", localize("item_view.changedCustomAttribute",
					localize("field.dueTo"), value)));
		}// if
		else {
			add(new WebMarkupContainer("dueTo").setVisible(false));
		}// else

		// Actual Effort
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		if (fieldMaskMap.get(StdField.Field.ACTUAL_EFFORT) != null
				&& !fieldMaskMap.get(StdField.Field.ACTUAL_EFFORT).getMask()
						.equals(StdFieldMask.Mask.HIDDEN)
				&& history.getActualEffort() != null) {
			IModel value = new Model(ItemUtils.formatEffort(
					history.getActualEffort() * 60, localize("item_list.days"),
					localize("item_list.hours"), localize("item_list.minutes")));
			add(new Label("actualEffort", localize(
					"item_view.changedCustomAttribute",
					localize("field.actualEffort"), value)));
		}// if
		else {
			add(new WebMarkupContainer("actualEffort").setVisible(false));
		}// else
	}
}