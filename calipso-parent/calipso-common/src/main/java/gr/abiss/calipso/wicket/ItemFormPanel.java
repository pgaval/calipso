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

import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.Attachment;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.ItemRenderingTemplate;
import gr.abiss.calipso.domain.ItemUser;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.State;
import gr.abiss.calipso.domain.StdField;
import gr.abiss.calipso.domain.StdFieldMask;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.UserSpaceRole;
import gr.abiss.calipso.dto.AssetSearch;
import gr.abiss.calipso.util.AttachmentUtils;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.util.ItemUtils;
import gr.abiss.calipso.util.UserUtils;
import gr.abiss.calipso.util.XmlUtils;
import gr.abiss.calipso.wicket.asset.ItemFormAssetSearchPanel;
import gr.abiss.calipso.wicket.components.LoadableDetachableDomainObjectModels.LoadableDetachableReadOnlyItemModel;
import gr.abiss.calipso.wicket.components.formfields.AssetTextField;
import gr.abiss.calipso.wicket.components.formfields.CheckBoxMultipleChoice;
import gr.abiss.calipso.wicket.components.renderers.UserChoiceRenderer;
import gr.abiss.calipso.wicket.yui.YuiCalendar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.validator.StringValidator;

/**
 * Create / Edit item form page
 * 
 * create, delete or edit a particular Space Item
 */
public class ItemFormPanel extends AbstractItemFormPanel {
	protected static final Logger logger = Logger.getLogger(ItemFormPanel.class);
	private boolean isEdit;

	private class AssigneeChoice extends DropDownChoice {

		public AssigneeChoice(String id, List<User> assignees) {
			super(id, assignees);

			setChoiceRenderer(new UserChoiceRenderer());
		}

		// -----------------------------------------------------------------------------------------

		@Override
		protected CharSequence getDefaultChoice(String selected) {
			if (selected.equals("-1")) {
				return "<option value=\"\"></option>";
			}
			return null;
		}

	}

	// /////////////////////////////////////////////////////////////////////////////////////////////

	public String getTitle() {
		if (isEdit) {
			return localize("dashboard.edit");
		} else {
			return localize("dashboard.new");
		}
	}

	public ItemFormPanel(String id, IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);
		this.setOutputMarkupId(true);
		this.isEdit = false;
		if (logger.isDebugEnabled()) {
			logger.debug("ItemFormPanel constructor: no Item in scope, creating new...");
		}
		Item item = new Item();
		Space space = getCurrentSpace();
		item.setSpace(space);
		item.setStatus(State.NEW);

		if(space.getSimpleAttachmentsSupport()){
			// we add a file form field specifically for simple attachments. The Item instance used as a model
			// also has an attachment field for model use only.
			this.fileUploadFields.put(SIMPLE_ATTACHEMENT_KEY, new FileUploadField("file"));
		}
		cancelLink();
		deleteLink(null);// or we could place item, it's the same
    	add(new Label("title", this.localize(item.getSpace())+": "+item.getStatusValue()));

    	ItemRenderingTemplate tmpl = getCalipso().getItemRenderingTemplateForUser(this.getPrincipal(), item.getStatus(), space.getId());
    	//logger.info("Got template fopr user: "+tmpl);

    	addVelocityTemplatePanel(tmpl, item);
		add(new ItemForm("form", item, tmpl));
	}

	public ItemFormPanel(String id, IBreadCrumbModel breadCrumbModel,
			long itemId) {
		super(id, breadCrumbModel);
		this.setOutputMarkupId(true);
		this.isEdit = true;
		if (logger.isDebugEnabled()) {
			logger.debug("ItemFormPanel constructor: no Item in scope, loading from ID...");
		}
		Item item = getCalipso().loadItem(itemId);

		if(item.getSpace().getSimpleAttachmentsSupport()){
			this.fileUploadFields.put(SIMPLE_ATTACHEMENT_KEY, new FileUploadField("file"));
		}
		cancelLink();
		deleteLink(item);

    	add(new Label("title", this.localize(item.getSpace())+": "+item.getStatusValue()));
    	Integer itemstatus = item.getStatus();
		item.setStatus(State.NEW);
    	ItemRenderingTemplate tmpl = getCalipso().getItemRenderingTemplateForUser(this.getPrincipal(), item.getStatus(), item.getSpace().getId());
		item.setStatus(itemstatus);
    	//logger.info("Got template fopr user: "+tmpl);
    	addVelocityTemplatePanel(tmpl, item);
		add(new ItemForm("form", item, tmpl));
	}

	/**
	 * wicket form
	 */
	private class ItemForm extends Form {

		private static final long serialVersionUID = 1L;
		private CalipsoFeedbackMessageFilter filter;
		private CustomFieldsFormPanel customFieldsFormPanel;
		private boolean editMode;
		private int version;
		private WebMarkupContainer reportedByContainer;
		private WebMarkupContainer assetsPanelContainer = null;
		private WebMarkupContainer assetsPanel = null;
		private AssetTextField itemAsset = null;
		//private Asset exAsset = null;
		private Asset selectedAsset = null;
		private AssetSearch assetSearch = null;
		CompoundPropertyModel model;
		boolean flagEditModeOn;
		public ItemForm(String id, final Item item, ItemRenderingTemplate tmpl) {
			super(id);
			this.setOutputMarkupId(true);
			refreshParentMenu(getBreadCrumbModel());
			model = initModel(item);
			Space space = item.getSpace();
			// multipart mode (always needed for uploads!)
			setMultiPart(true);
			FeedbackPanel feedback = new FeedbackPanel("feedback");
			filter = new CalipsoFeedbackMessageFilter();
			feedback.setFilter(filter);
			add(feedback);
			version = item.getVersion();
			if (item.getId() > 0) {
				editMode = true;
			}
			// initialize model for either create or edit
			setModel(model);
			// get masks
			Map<StdField.Field, StdFieldMask> fieldMaskMap = getPrincipalMasks();
			// ADD FIELDS
			// summary
			addSummary();
			// Detail
			addDetail(space);
			// Reported By
			addReportedBy(item);
			// Due To
			addDueTo(fieldMaskMap);
			// Planned Effort
			addPlannedEffort(fieldMaskMap);
			// Choose an Asset?
			renderAssetTextField(getCurrentSpace(), fieldMaskMap);
			

			// custom fields
			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if (editMode) {// is edit model is bound around history

				flagEditModeOn = false;
				
				this.customFieldsFormPanel = new CustomFieldsFormPanel("fields", model, space,
						flagEditModeOn, fileUploadFields);
				add(customFieldsFormPanel.setOutputMarkupId(true));

			} else {// is new model is bound around item
				boolean historyMode = false;
				this.customFieldsFormPanel = new CustomFieldsFormPanel("fields", model, item,
						getPrincipal(), historyMode, fileUploadFields);
			}
			this.customFieldsFormPanel.setRenderBodyOnly(false);
			this.customFieldsFormPanel.setOutputMarkupId(true);
			add(customFieldsFormPanel);
			// hide some components if editing item
			WebMarkupContainer hideAssignedTo = new WebMarkupContainer(
					"hideAssignedTo");
			add(hideAssignedTo);
			
			WebMarkupContainer hideNotifyList = new WebMarkupContainer(
					"hideNotifyList");
			WebMarkupContainer notificationsContainer = new WebMarkupContainer("notificationsContainer");
			notificationsContainer.add(hideNotifyList);
			add(notificationsContainer.setVisible(getCalipso().isEmailSendingConfigured() && getPrincipal().hasRegularRoleForSpace(space)));
			
			WebMarkupContainer hideEditReason = new WebMarkupContainer(
					"hideEditReason");
			add(hideEditReason);
			if (editMode) {
				hideAssignedTo.setVisible(false);
				hideNotifyList.setVisible(false);

				// add Edit reason text area
				TextArea editReason = new TextArea("editReason");
				editReason.setRequired(true);
				editReason.add(new ErrorHighlighter());
				hideEditReason.add(editReason);
				// form label for edit reason
				editReason.setLabel(new ResourceModel("item_form.editReason"));
				hideEditReason.add(new SimpleFormComponentLabel(
						"editReasonLabel", editReason));
			} else {
				hideEditReason.setVisible(false);

				// TODO: should work out an assignment visibility setting per role/state, should be easy 
				// using the metadata for it
				hideAssignedTo.setVisible((!getPrincipal().isAnonymous()) && (getPrincipal().hasRegularRoleForSpace(space)));
				// assigned to
				// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				
				List<UserSpaceRole> userSpaceRoles = getCalipso()
						.findUserRolesForSpace(space.getId());
				List<User> assignable = UserUtils
						.filterUsersAbleToTransitionFrom(userSpaceRoles, space,
								State.OPEN);
				item.setAssignedTo(UserUtils.getSpaceAdmin(assignable,
						getCurrentSpace()));
				// DropDownChoice choice = new DropDownChoice("assignedTo",
				// assignable, new IChoiceRenderer() {
				// public Object getDisplayValue(Object o) {
				// return ((User) o).getName();
				// }
				// public String getIdValue(Object o, int i) {
				// return ((User) o).getId() + "";
				// }
				// });

				AssigneeChoice choice = new AssigneeChoice("assignedTo",
						assignable);
				choice.setNullValid(true);
				// choice.setRequired(true);
				WebMarkupContainer border = new WebMarkupContainer("border");
				border.add(choice);
				border.add(new ErrorHighlighter(choice));
				hideAssignedTo.add(border);
				// form label
				choice.setLabel(new ResourceModel("item_form.assignTo"));
				hideAssignedTo.add(new SimpleFormComponentLabel(
						"assignedToLabel", choice));

				// notify list
				// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				List<ItemUser> choices = UserUtils
						.convertToItemUserList(userSpaceRoles);
				ListMultipleChoice itemUsers = new CheckBoxMultipleChoice(
						"itemUsers", choices, new UserChoiceRenderer(), true);
				hideNotifyList.add(itemUsers);
				// attachment
				// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				setMaxSize(Bytes.megabytes(getCalipso()
						.getAttachmentMaxSizeInMb()));
			}
			// send notifications
			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			CheckBox sendNotificationsCheckbox = new CheckBox(
					"sendNotifications");
			notificationsContainer.add(sendNotificationsCheckbox);
			// form label
			sendNotificationsCheckbox.setLabel(new ResourceModel(
					"item_form.sendNotifications"));
			notificationsContainer.add(new SimpleFormComponentLabel("sendNotificationsLabel",
					sendNotificationsCheckbox));
		}

		/**
		 * @param fieldMaskMap
		 */
		private void addPlannedEffort(
				Map<StdField.Field, StdFieldMask> fieldMaskMap) {
			if (fieldMaskMap.get(StdField.Field.PLANNED_EFFORT) != null
					&& (fieldMaskMap.get(StdField.Field.PLANNED_EFFORT)
							.getMask().equals(StdFieldMask.Mask.CREATE) || fieldMaskMap
							.get(StdField.Field.PLANNED_EFFORT).getMask()
							.equals(StdFieldMask.Mask.UPDATE))) {
				EffortField effortField = new EffortField("effortField",
						new PropertyModel(getModel(), "plannedEffort"), false);
				add(effortField);
				effortField.setLabel(new ResourceModel(
						"item_form.plannedEffort"));
				add(new SimpleFormComponentLabel("plannedEffortLabel",
						effortField));
			} else {
				add(new WebMarkupContainer("plannedEffortLabel")
						.setVisible(false));
				add(new WebMarkupContainer("effortField").setVisible(false));
			}
		}

		/**
		 * 
		 */
		private Map<StdField.Field, StdFieldMask> getPrincipalMasks() {
			getPrincipal().setRoleSpaceStdFieldList(
					getCalipso().findSpaceFieldsForUser(getPrincipal()));
			return getPrincipal()
					.getStdFieldsForSpace(getCurrentSpace());
		}

		/**
		 * @return
		 */
		private void addDueTo(Map<StdField.Field, StdFieldMask> fieldMaskMap) {
			if (fieldMaskMap.get(StdField.Field.DUE_TO) != null
					&& (fieldMaskMap.get(StdField.Field.DUE_TO).getMask()
							.equals(StdFieldMask.Mask.CREATE) || fieldMaskMap
							.get(StdField.Field.DUE_TO).getMask()
							.equals(StdFieldMask.Mask.UPDATE))) {
				YuiCalendar calendar = new YuiCalendar("dueToField",
						new PropertyModel(getModel(), "dueTo"), false);
				add(calendar);
				calendar.setLabel(new ResourceModel("item_form.dueToDate"));
				add(new SimpleFormComponentLabel("dueToLabel", calendar));
			} else {
				add(new WebMarkupContainer("dueToLabel").setVisible(false));
				add(new WebMarkupContainer("dueToField").setVisible(false));
			}
		}

		/**
		 * @param item
		 */
		private void addReportedBy(final Item item) {
			reportedByContainer = new WebMarkupContainer("reportedByContainer");
			// not visible for guest and anonymous
			reportedByContainer.setVisible(!getPrincipal().isGuest() && !getPrincipal().isAnonymous());
			reportedByContainer.setOutputMarkupId(true);
			reportedByContainer.setOutputMarkupPlaceholderTag(true);
			add(reportedByContainer);
			final TextField reportedByName = new TextField("reportedByName", new Model(getPrincipal().getDisplayValue()));
			// TextField reportedByName = new TextField("reportedByName", new
			// PropertyModel("space", "reportedBy.name"));
			reportedByName.setOutputMarkupId(true);
			reportedByContainer.add(reportedByName);

			reportedByName.setLabel(new ResourceModel("item_form.reportedBy"));
			reportedByContainer.add(new SimpleFormComponentLabel("reportedByLabel", reportedByName));

			final HiddenField reportedBy = new HiddenField("reportedBy.id");
			// final TextField reportedBy = new TextField("reportedBy.id");
			reportedBy.setOutputMarkupId(true);
			reportedByContainer.add(reportedBy);

			SearchUserPanel searchUserPanel = new SearchUserPanel("searchUserPanel", item.getSpace()) {
				@Override
				public void onUserSelect(User user, AjaxRequestTarget target) {
					reportedByName.setModelObject(user.getFullName());
					item.setReportedBy(user);
					target.addComponent(reportedByContainer);

				}
			};
			searchUserPanel.setOutputMarkupId(true);
			reportedByContainer.add(searchUserPanel);
		}

		/**
		 * 
		 */
		private void addDetail(Space currentSpace) {
			WebMarkupContainer commentContainer = new WebMarkupContainer("commentContainer");
			
			TextArea detail;
			detail = new TextArea("detail");
			detail.setMarkupId("detail");
			detail.setRequired(false);
			detail.add(new ErrorHighlighter());
			commentContainer.add(detail);
			// form label
			detail.setLabel(new ResourceModel("item_form.detail"));
			commentContainer.add(new SimpleFormComponentLabel("detailLabel", detail));

			add(commentContainer.setVisible(currentSpace.getItemDetailCommentEnabled()));
		}

		/**
		 * 
		 */
		private void addSummary() {
			WebMarkupContainer container = new WebMarkupContainer("summaryContainer");

			final TextField summaryField;
			summaryField = new TextField("summary");
			// TODO:add validation to check character length of the input
			summaryField.add(StringValidator
					.maximumLength(ItemUtils.MAX_SUMMARY_CHARACTERS));
			summaryField.add(new ErrorHighlighter());
			summaryField.setOutputMarkupId(true);
			container.add(summaryField);
			// form label
			summaryField.setLabel(new ResourceModel("item_form.summary"));
			container.add(new SimpleFormComponentLabel("summaryLabel", summaryField));
			//logger.info("Space has summary enabled: "+getCurrentSpace().isItemSummaryEnabled());
			if(getCurrentSpace().isItemSummaryEnabled()){
				summaryField.setRequired(true);
				add(new Behavior() {
					public void renderHead(IHeaderResponse response) {
						response.renderOnLoadJavaScript("document.getElementById('"
								+ summaryField.getMarkupId() + "').focus()");
					}
				});
			}
			else{
				container.setVisible(false);
			}
			add(container);
		}


				/**
		 * @param item
		 * @return
		 */
		private CompoundPropertyModel initModel(final Item item) {
			if (editMode) {
				// this ensures that the model object is re-loaded as part of
				// the
				// form submission workflow before form binding and avoids
				// hibernate lazy loading issues during the whole update
				// transaction
				LoadableDetachableModel itemModel = new LoadableDetachableModel() {
					protected Object load() {
						logger.debug("attaching existing item " + item.getId());
						return getCalipso().loadItem(item.getId());
					}
				};
				model = new CompoundPropertyModel(itemModel);
			} else {
				item.setReportedBy(getPrincipal());
				model = new CompoundPropertyModel(item);
			}
			return model;
		}

		/**
		 * 
		 */
		private void renderAssetTextField(Space space, Map<StdField.Field, StdFieldMask> fieldMaskMap) {
			// maybe a specific  asset type was declared on space workflow for state.NEW?
			AssetType existingAssetType = null;
			// check the workflow
			Map<Integer, Long> existingAssetTypeIdsMap = space.getMetadata().getStatesExistingSpaceAssetTypeIdsMap();
			// if map not empty mean there is a choice
			boolean filterByAssetType = MapUtils.isNotEmpty(existingAssetTypeIdsMap);
			
			// if a specific  asset type was declared on space workflow for state.NEW or 
			// the current space's assets are visible for this user 
			if (!editMode && (filterByAssetType 
					|| (space.isAssetEnabled() && fieldMaskMap.get(StdField.Field.ASSET) != null
					&& (fieldMaskMap.get(StdField.Field.ASSET).getMask().equals(StdFieldMask.Mask.CREATE) 
					|| fieldMaskMap.get(StdField.Field.ASSET).getMask().equals(StdFieldMask.Mask.UPDATE))))) {
				
				// add item asset text field and image link for asset panel popup
				addItemAsset(filterByAssetType);
				// init asset for asset search
				Asset exAsset = new Asset();
				exAsset.setSpace(space);
				if(filterByAssetType){
					// Within the current implementation there is only one existing asset type per space
					// during creation, so we get the first element of the map
					Long existingAssetTypeId =(MapUtils.isNotEmpty(existingAssetTypeIdsMap))?existingAssetTypeIdsMap.get(0):null;
					existingAssetType = getCalipso().loadAssetType(existingAssetTypeId.longValue());
					exAsset.setAssetType(existingAssetType);
				}
				assetSearch = new AssetSearch(exAsset, this);
				assetsPanel = createNewAssetPanel(assetSearch);
				assetsPanelContainer = new WebMarkupContainer("assetsPanelContainer");
				assetsPanelContainer.setOutputMarkupId(true);
				add(assetsPanelContainer);
				//assetsPanel.setVisible(true);
				assetsPanelContainer.add(assetsPanel.setVisible(false));
				add(new WebMarkupContainer("availableAssetPanel"));
			}
			else {
				hideItemAsset();
			}// else
		}

		/**
		 * 
		 */
		private void hideItemAsset() {
			assetsPanelContainer = new WebMarkupContainer("assetsPanelContainer");
			assetsPanelContainer.setOutputMarkupId(true);
			add(assetsPanelContainer);
			assetsPanelContainer.add(new WebMarkupContainer("assetsPanel"));
			
			add(new WebMarkupContainer("availableAssetPanel"));
			add(new Label("existinAssetTypeLabel", "").setVisible(false));
			add(new Label("findAsset").setVisible(false));
			add(new TextField("itemAsset").setVisible(false));
			
		}

		/**
		 * 
		 */
		private ItemFormAssetSearchPanel createNewAssetPanel(AssetSearch assetSearch) {
			ItemFormAssetSearchPanel assetPanel = new ItemFormAssetSearchPanel("assetsPanel", assetSearch){
				@Override
				public void onAssetSelect(Asset asset, AjaxRequestTarget target) {
					itemAsset.setModelObject(asset);
					selectedAsset = asset;
					target.focusComponent(itemAsset);
					target.addComponent(itemAsset);
					// hide the asset panel after the user has selected an asset
					ItemForm.this.assetsPanel.setVisible(false);
					target.addComponent(ItemForm.this.assetsPanelContainer);
					
					Item itemToUpdate = (Item) ItemForm.this.getModelObject();
					ItemUtils.initItemFields(getCalipso(), itemToUpdate, selectedAsset, true, null);
					//logger.info("Asset selected, cusInt01 value: "+itemToUpdate.getValue(Field.Name.CUS_INT_01));
					customFieldsFormPanel = new CustomFieldsFormPanel("fields", model, itemToUpdate, getPrincipal(), false, fileUploadFields);
					customFieldsFormPanel.setOutputMarkupId(true);
					ItemForm.this.replace(customFieldsFormPanel);
					target.addComponent(customFieldsFormPanel);
					target.appendJavaScript("disableFields();");
				}

				@Override
				public void closePanel(AjaxRequestTarget target) {
					ItemForm.this.assetsPanel.setVisible(false);
					target.addComponent(ItemForm.this.assetsPanelContainer);
					
				}
			};
			return assetPanel;
		}

		/**
		 * @param filterByAssetType
		 */
		private void addItemAsset(boolean filterByAssetType) {
			itemAsset =	new AssetTextField("itemAsset", new Model(), Asset.class);
			
			itemAsset.setRequired(itemAsset.getModelObject() == null);
			itemAsset.setLabel(new ResourceModel("item_form.selectAssetForInfoCopy"));
			itemAsset.add(new ErrorHighlighter());
			add(new SimpleFormComponentLabel("existinAssetTypeLabel", itemAsset));
			if(filterByAssetType){
				itemAsset.setRequired(true);
				itemAsset.add(new ErrorHighlighter());
			}
			add(itemAsset);
			// Toggle View/Hide Search Asset possibility
			AjaxLink findAsset = new AjaxLink("findAsset"){
					@Override
					public void onClick(AjaxRequestTarget target) {
					assetsPanel.setVisible(!assetsPanel.isVisible());
					target.addComponent(ItemForm.this.assetsPanelContainer);
				}
			};
			add(findAsset);
		}
		/*
		@Override
		protected void validate() {

			filter.reset();
			Item item = (Item) getModelObject();
			if (editMode && item.getVersion() != version) {
				// user must have used back button after edit
				error(localize("item_form.error.version"));
			}
			super.validate();

		}
		*/
		@Override
		protected void onSubmit() {
			final Item item = (Item) getModelObject();
			// update tree options etc
			for (Field field : item.getSpace().getMetadata().getFieldList()) {
				if (field.isDropDownType()){
					if(field.getCustomAttribute() != null && field.getCustomAttribute().getLookupValue() != null){
						item.setValue(field.getName(), NumberUtils.createInteger(field.getCustomAttribute().getLookupValue().getId()+""));
					}
					else{
						item.setValue(field.getName(), null);
					}
				}
			}
			if(selectedAsset != null){
				item.addAsset(selectedAsset);
			}
			// keep a ref to fileuploads
			// TODO: move this to service like we do for history
			Map<String, FileUpload> fileUploads = getNonNullUploads();
			for (String filename : fileUploads.keySet()) {
				FileUpload upload = fileUploads.get(filename);
				//logger.info("Adding file upload for file: "+filename);
				addAndReplaceSameNamed(item, filename, upload);
			}
			User user = getPrincipal();
			// Set current space id at item creation. So, the item space origin
			// is known.
			item.setAssignableSpaces(getCurrentSpace());
			String safeHtmlComment = XmlUtils.removeXss(XmlUtils.removeComments(item.getDetail()));
			item.setDetail(XmlUtils.stripTags(safeHtmlComment));
			item.setHtmlDetail(safeHtmlComment);
			if (editMode) {
				// Update item
				getCalipso().updateItem(item, user);

			} else {
				item.setLoggedBy(user);
				item.setStatus(State.OPEN);

				// Add selected assets to this item
				if (assetsPanel != null) {
					// TODO:
					/*
					if (itemAssetsPanel.getSelectedAssets() != null) {
						for (Asset asset : itemAssetsPanel.getSelectedAssets()) {
							item.addAsset(asset);
						}// for
					}// if
					*/
				}// if
				getCalipso().storeItem(item, fileUploads);

			}

			// on creating an item, clear any search filter (especially the
			// related item) from session
			setCurrentItemSearch(null);
			activate(new IBreadCrumbPanelFactory() {
				public BreadCrumbPanel create(String componentId,
						IBreadCrumbModel breadCrumbModel) {
					if (!isEdit) {// if not edit
						getBreadCrumbModel().allBreadCrumbParticipants()
								.clear();
						getBreadCrumbModel()
								.setActive(
										new DashboardPanel(componentId,
												breadCrumbModel));
					} else {
						BreadCrumbUtils
								.removePreviousBreadCrumbPanel(getBreadCrumbModel());
					}

					return new ItemViewPanel(componentId, getBreadCrumbModel(),
							item.getUniqueRefId());
				}
			});
		}

		private void addAndReplaceSameNamed(final Item item, String filename,
				FileUpload upload) {
			String clientFilename = upload.getClientFileName();
			//logger.info("addAndReplaceSameNamed with filename: "+filename+" and clientFilename: "+clientFilename);
			if(StringUtils.isNotBlank(filename) && !filename.equalsIgnoreCase(Field.FIELD_TYPE_SIMPLE_ATTACHEMENT)){
				String extention = upload.getClientFileName().substring(
						upload.getClientFileName().lastIndexOf('.'));
				filename = filename + extention;
			}
			else if(filename.equalsIgnoreCase(Field.FIELD_TYPE_SIMPLE_ATTACHEMENT)){
				filename = upload.getClientFileName().toLowerCase();
			}
			else{
				filename = upload.getClientFileName();
			}
			
			// String filename = upload.getClientFileName();
			Attachment attachment = new Attachment();
			// attachment file's name
			//logger.info("Setting attachment filename: "+filename);
			attachment.setItem(item);
			attachment.setFileName(filename);
			//logger.debug("making attachment with filename: "
			//		+ filename+"basepath: "+attachment.getBasePath());
			attachment.setTemporary(false);
			AttachmentUtils.buildBasePath(attachment);
			//logger.info("addAndReplaceSameNamed: attachment base path:" + attachment.getBasePath());
			// attachments to Item, replacing others with the same name if
			// needed
			AttachmentUtils.addAndReplaceSameNamed(item, attachment);
		}

	}

	private void deleteLink(final Item item) {
		// TODO: add config for item delete
		//if (item == null || item.getStatus() == State.NEW) {// if new
		if(true){
			add(new WebMarkupContainer("delete").setVisible(false));
		} 
		else {// if edit
			add(new Link("delete") {
				@Override
				public void onClick() {
					final String heading = localize("item_delete.confirm");
					final String warning = localize("item_delete.line2");
					final String line1 = localize("item_delete.line1");

					activate(new IBreadCrumbPanelFactory() {
						public BreadCrumbPanel create(final String componentId,
								IBreadCrumbModel breadCrumbModel) {
							ConfirmPanel confirm = new ConfirmPanel(
									componentId, breadCrumbModel, heading,
									warning, new String[] { line1 }) {
								public void onConfirm() {
									// avoid lazy init problem
									getCalipso().removeItem(
											getCalipso().loadItem(item.getId()));

									ArrayList<Class> panels = new ArrayList<Class>(
											2);
									panels.add(ItemListPanel.class);
									panels.add(SingleSpacePanel.class);

									final BreadCrumbPanel gotoPanel = BreadCrumbUtils
											.getPanel(getBreadCrumbModel(),
													panels);

									if (gotoPanel != null) {
										activate(new IBreadCrumbPanelFactory() {
											public BreadCrumbPanel create(
													String componentId,
													IBreadCrumbModel breadCrumbModel) {
												return gotoPanel;
											}
										});
									} else {
										setResponsePage(SpacePage.class);
									}
								}
							};
							return confirm;
						}
					});// activate
				}// onclick
			});// add, new Link
		}
	}

	private void cancelLink() {
		// add back link from BasePanel and change back to cancel i.e. it's
		// label==========================================================
		add(getBackLinkPanel().makeCancel());
	}

}