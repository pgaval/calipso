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
import gr.abiss.calipso.domain.Attachment;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.ItemUser;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.State;
import gr.abiss.calipso.domain.StdField;
import gr.abiss.calipso.domain.StdFieldMask;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.UserSpaceRole;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.util.UserUtils;
import gr.abiss.calipso.util.XmlUtils;
import gr.abiss.calipso.wicket.asset.ItemAssetsPanel;
import gr.abiss.calipso.wicket.components.formfields.CheckBoxMultipleChoice;
import gr.abiss.calipso.wicket.components.renderers.UserChoiceRenderer;
import gr.abiss.calipso.wicket.yui.YuiCalendar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Form to update history for item
 */
public class ItemViewFormPanel extends AbstractItemFormPanel implements IHeaderContributor {
	private static final Logger logger = Logger.getLogger(ItemViewFormPanel.class);
	private static final long serialVersionUID = 1L;

	private final CalipsoFeedbackMessageFilter filter;
	private final ItemSearch itemSearch;
	private final ItemViewForm itemViewForm;
	// TODO: move to AbstractItemFormPanel?
	private ItemAssetsPanel availableAssetsPanel;

	/**
	 * 
	 * @param id
	 * @param breadCrumbModel
	 * @param item
	 * @param itemSearch
	 */
	public ItemViewFormPanel(String id, IBreadCrumbModel breadCrumbModel,
			Item item, ItemSearch itemSearch) {
		this(id, breadCrumbModel, item, itemSearch, null);
	}

	/**
	 * 
	 * @param id
	 * @param breadCrumbModel
	 * @param item
	 * @param itemSearch
	 * @param previewHistory
	 */
	public ItemViewFormPanel(String id, IBreadCrumbModel breadCrumbModel,
			Item item, ItemSearch itemSearch, History previewHistory) {
		super(id, breadCrumbModel);
		this.itemSearch = itemSearch;
		FeedbackPanel feedback = new FeedbackPanel("feedback");
		filter = new CalipsoFeedbackMessageFilter();
		feedback.setFilter(filter);

    	User currentUser = getPrincipal();
    	Space currentSpace = getCurrentSpace();
    	boolean userCanSeeComments = currentUser.hasRegularRoleForSpace(currentSpace) || (currentSpace.getItemVisibility().equals(Space.ITEMS_VISIBLE_TO_LOGGEDIN_REPORTERS) && currentUser.isGuestForSpace(currentSpace));
	        
		
		itemViewForm = new ItemViewForm("form", item, previewHistory);
		itemViewForm.add(feedback);
		WebMarkupContainer formContainer = new WebMarkupContainer("formContainer");
		formContainer.add(itemViewForm);
		add(formContainer.setVisible(userCanSeeComments));
		// TODO: add expired clean up for files uploaded by UploadPanel as well
		// getJtrac().removeExpiredTemporaryAttachments();
	}

	public ItemViewForm getItemViewForm() {
		return this.itemViewForm;
	}

	/**
	 * wicket form
	 */
	public class ItemViewForm extends Form {
		private static final long serialVersionUID = 1L;
		private String filename;
		private String hiddenfield;
		private FileUploadField fileUploadField;
		private long itemId;
		private DropDownChoice assignedToChoice;
		private DropDownChoice statusChoice;
		private CustomFieldsFormPanel customFieldsFormPanel;
		CustomFieldsFormPanel.AssignableSpacesDropDownChoice assignableSpacesDropDownChoice;
		private Item item;
		ListMultipleChoice itemUsers;
		//public String q;
		private boolean preview = false;
		private SubmitUtilDao submitUtils;

		public ItemViewForm(String id, final Item item) {
			this(id, item, null);
		}


		@SuppressWarnings("serial")
		public ItemViewForm(String id, final Item item, History previewHistory) {
			super(id);
			setMultiPart(true);
			this.itemId = item.getId();
			final History history;
			if (previewHistory != null) {
				history = previewHistory;
			} else {
				history = new History();
			}
			item.add(history);
			history.setItemUsers(item.getItemUsers());
			final CompoundPropertyModel model = new CompoundPropertyModel(
					history);
			setModel(model);
			this.item = item;
			User user = getPrincipal();
			HiddenField hiddenfield = new HiddenField("hiddenfield", new PropertyModel(this, "hiddenfield"));
			if (!user.isGlobalAdmin() && !user.isSpaceAdmin()) {
				hiddenfield.add(new SpaceClosingDateValidator(item.getSpace()
					.getClosingDate()));
			}
			add(hiddenfield);
			// custom fields
			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			boolean historyMode = true;
			customFieldsFormPanel = new CustomFieldsFormPanel("fields", model,
					item, user, historyMode, fileUploadFields);
			customFieldsFormPanel.setRenderBodyOnly(true);
			customFieldsFormPanel.setAssignedToChoice(assignedToChoice);
			add(customFieldsFormPanel);
			customFieldsFormPanel.setItemViewFormPanel(ItemViewFormPanel.this);

			// Available Assets panel
			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			getPrincipal().setRoleSpaceStdFieldList(
					getCalipso().findSpaceFieldsForUser(getPrincipal()));
			Map<StdField.Field, StdFieldMask> fieldMaskMap = getPrincipal()
					.getStdFieldsForSpace(getCurrentSpace());
			add(new WebMarkupContainer("availableAssetsPanel"));

			// Due To
			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if (fieldMaskMap.get(StdField.Field.DUE_TO) != null
					&& fieldMaskMap.get(StdField.Field.DUE_TO).getMask()
							.equals(StdFieldMask.Mask.UPDATE)) {
				YuiCalendar calendar = new YuiCalendar("dueToField",
						new PropertyModel(getModel(), "dueTo"), false);
				add(calendar);
				calendar.setLabel(new ResourceModel("item_form.dueToDate"));
				add(new SimpleFormComponentLabel("dueToLabel", calendar));
			} else {
				add(new WebMarkupContainer("dueToLabel").setVisible(false));
				add(new WebMarkupContainer("dueToField").setVisible(false));
			}

			// Planned Effort
			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if (fieldMaskMap.get(StdField.Field.PLANNED_EFFORT) != null
					&& fieldMaskMap.get(StdField.Field.PLANNED_EFFORT)
							.getMask().equals(StdFieldMask.Mask.UPDATE)) {
				EffortField plannedEffortField = new EffortField(
						"plannedEffortField", new PropertyModel(getModel(),
								"plannedEffort"), false);
				add(plannedEffortField);
				plannedEffortField.setLabel(new ResourceModel(
						"item_form.plannedEffort"));
				add(new SimpleFormComponentLabel("plannedEffortLabel",
						plannedEffortField));
			} else {
				add(new WebMarkupContainer("plannedEffortLabel")
						.setVisible(false));
				add(new WebMarkupContainer("plannedEffortField")
						.setVisible(false));
			}

			// Actual Effort
			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if (fieldMaskMap.get(StdField.Field.ACTUAL_EFFORT) != null
					&& (fieldMaskMap.get(StdField.Field.ACTUAL_EFFORT)
							.getMask().equals(StdFieldMask.Mask.CREATE) || fieldMaskMap
							.get(StdField.Field.ACTUAL_EFFORT).getMask()
							.equals(StdFieldMask.Mask.UPDATE))) {
				EffortField actualEffortField = new EffortField(
						"actualEffortField", new PropertyModel(getModel(),
								"actualEffort"), false);
				add(actualEffortField);
				actualEffortField.setLabel(new ResourceModel(
						"item_form.actualEffort"));
				add(new SimpleFormComponentLabel("actualEffortLabel",
						actualEffortField));
			} else {
				add(new WebMarkupContainer("actualEffortLabel")
						.setVisible(false));
				add(new WebMarkupContainer("actualEffortField")
						.setVisible(false));
			}

			final Space space = item.getSpace();
			final List<UserSpaceRole> userSpaceRoles = getCalipso().findUserRolesForSpace(space.getId());
			// assigned to, status =====================================================
			WebMarkupContainer statusAndAssignContainer = new WebMarkupContainer("statusAndAssignContainer");
			final WebMarkupContainer border = new WebMarkupContainer("border");
			border.setOutputMarkupId(true);
			final WebMarkupContainer hide = new WebMarkupContainer("hide");
			border.add(hide);
			final List<User> emptyList = new ArrayList<User>(0); // will be
																	// populated
																	// over Ajax

			if (history.getAssignedTo() != null) {
				emptyList.add(history.getAssignedTo());
			}

			assignedToChoice = new DropDownChoice("assignedTo", emptyList,
					new UserChoiceRenderer());

			if (history.getAssignedTo() != null) {
				assignedToChoice.setVisible(true);
				hide.setVisible(false);
			} else
				assignedToChoice.setVisible(false);

			assignedToChoice.setOutputMarkupId(true);
			assignedToChoice.setNullValid(true);
			border.add(new ErrorHighlighter(assignedToChoice));
			border.add(assignedToChoice);
			statusAndAssignContainer.add(border);
			customFieldsFormPanel.setAssignedToChoice(assignedToChoice);
			// form label for Assigned To
			assignedToChoice.setLabel(new ResourceModel(
					"item_view_form.assignTo"));
			statusAndAssignContainer.add(new SimpleFormComponentLabel("assignedToLabel",
					assignedToChoice));

			// status ==========================================================
			final Map<Integer, String> statesMap = item
					.getPermittedTransitions(user);
			
			// The states list is ordered from low to high,   
			// we can use it to check for the "naturally " 
			// previous and next state to add buttons as appropriate 
			this.submitUtils = new SubmitUtilDao(statesMap, item.getStatus().intValue());

			Button cancelButton = new Button("cancelButton"){
				@Override
				public void onSubmit() {
					throw new RestartResponseException(DashboardPage.class, new PageParameters()); 
				}
			};

			cancelButton.add(new AttributeModifier("value", new StringResourceModel(CollectionUtils.isNotEmpty(submitUtils.getStates())?"cancel":"back", ItemViewFormPanel.this, null)));
			// no reason to validate or process the submit
			cancelButton.setDefaultFormProcessing(false);
			add(cancelButton.setVisible(this.submitUtils.isClosed()));
			
			Button previousButton = new Button("previousButton"){
				@Override
				public void onSubmit() {
					statusChoice.setModelObject(submitUtils.getPreviousState());
					//logger.info("previousButton.onSubmit, set status to: "+submitUtils.getPreviousState());
				}
				
			};
			previousButton.add(new AttributeModifier("title", new Model(statesMap.get(submitUtils.getPreviousState()))));
			previousButton.add(new AttributeModifier("value",
					new StringResourceModel(submitUtils
							.getPreviousStateMessage(), ItemViewFormPanel.this,
							null)));

			add(previousButton);

			Button nextButton = new Button("nextButton"){
				@Override
				public void onSubmit() {
					statusChoice.setModelObject(submitUtils.getNextState());
					//logger.info("nextButton.onSubmit, set status to: "+submitUtils.getNextState());
				}
				
			};

			nextButton.add(new AttributeModifier("title", new Model(statesMap.get(submitUtils.getNextState()))));
			nextButton.add(new AttributeModifier("value",
					new StringResourceModel(submitUtils.getNextStateMessage(),
							ItemViewFormPanel.this, null)));
			if(submitUtils.isClosedAllowedOnly()){
				nextButton.add(new AttributeModifier("class", new Model("submit-to-close")));
			}
			add(nextButton.setVisible(submitUtils.getNextState() != null));

			boolean statusChoiceVisible = true;// submitUtils.isStateChangeAllowed() && submitUtils.getSingleStateChangeAllowed() == null;
			Button submitButton = new Button("submitButton");
			add(submitButton.setVisible(/* statusChoiceVisible */!submitUtils
					.isClosedAllowedOnly()));
			
			statusChoice = new IndicatingDropDownChoice("status",
					new ArrayList(statesMap.keySet()),
					new IChoiceRenderer<Integer>() {

						@Override
						public Object getDisplayValue(Integer o) {
							return statesMap.get(o);
						}

						@Override
						public String getIdValue(Integer o, int index) {
							// TODO Auto-generated method stub
							return o.toString();
						}
					});
			statusChoice.setNullValid(true);
			statusChoice.add(new ErrorHighlighter());

			statusChoice.add(new AjaxFormComponentUpdatingBehavior("onChange") {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					Integer selectedStatus = (Integer) getFormComponent()
							.getConvertedInput();
					// logger.info("Selected status : " + selectedStatus);
					if (selectedStatus == null) {
						assignedToChoice.setVisible(true);
						hide.setVisible(false);// changed to true
					} else {
						// if
						// (selectedStatus==State.MOVE_TO_OTHER_SPACE){//Assign
						// To Other Space
						// if (customFieldsFormPanel.getAssignableSpaceValue()!=
						// null &&
						// !customFieldsFormPanel.getAssignableSpaceValue().equals("")){
						// assignedToChoice.setChoices(customFieldsFormPanel.getAssignableSpaceUsers());
						// assignedToChoice.setNullValid(false);
						// }//if
						// assignedToChoice.setVisible(true);
						// hide.setVisible(false);
						// }
						// else{

						List<User> assignable = UserUtils
								.filterUsersAbleToTransitionFrom(
										userSpaceRoles, space, selectedStatus);
						assignedToChoice.setChoices(assignable);
						User asignee = item.getAssignedTo();
						if(asignee != null){
							assignedToChoice.setDefaultModelObject(asignee);
						}
						assignedToChoice.setVisible(true);
						hide.setVisible(false);
						// }
					}

					target.appendJavaScript("enableAssignableSpacesDropDownChoice('"
							+ statusChoice.getMarkupId() + "'" + ")");
					target.addComponent(border);
				}
			});
			statusChoice .add(new Behavior(){
			      @Override
				public void renderHead(Component component, IHeaderResponse response) {
			    	  response.renderJavaScript(new JavaScripts().enableAssignableSpacesDropDownChoice.asString(), "enableAssignableSpacesDropDownChoice");
			      }
			});
			customFieldsFormPanel.setStatusChoice(statusChoice);
			// disable status if:
			// A: only the current status is available
			// B: a single other state is the only option
			statusChoice.setVisible(statusChoiceVisible);
			add(statusChoice);
			// form label for statusChoice
			statusChoice.setLabel(new ResourceModel("item_view_form.newStatus"));
			//statusAndAssignContainer.add(new SimpleFormComponentLabel("statusLabel", statusChoice).setVisible(statusChoiceVisible));
			add(statusAndAssignContainer.setVisible(statusChoiceVisible));//
			// comment ====================================================
			WebMarkupContainer commentContainer = new WebMarkupContainer("commentContainer");
			TextArea comment = new TextArea("comment");
			comment.setRequired(false);
			comment.add(new ErrorHighlighter());
			commentContainer.add(comment);
			// form label for comment
			comment.setLabel(new ResourceModel("item_view_form.comment"));
			commentContainer.add(new SimpleFormComponentLabel("commentLabel", comment));
			add(commentContainer.setVisible(space.getItemDetailCommentEnabled()));
			// notify list =====================================================
			
			WebMarkupContainer notificationsContainer = new WebMarkupContainer("notificationsContainer");
			CheckBox sendNotificationsCheckBox = new CheckBox(
					"sendNotifications");
			notificationsContainer.add(sendNotificationsCheckBox);
			// form label for Send Notifications
			sendNotificationsCheckBox.setLabel(new ResourceModel(
					"item_view_form.sendNotifications"));
			notificationsContainer.add(new SimpleFormComponentLabel("sendNotificationsLabel",
					sendNotificationsCheckBox));
			List<ItemUser> choices = UserUtils
					.convertToItemUserList(userSpaceRoles);
			ListMultipleChoice itemUsers = new CheckBoxMultipleChoice(
					"itemUsers", choices, new UserChoiceRenderer(), true);
			notificationsContainer.add(itemUsers);
			add(notificationsContainer.setVisible(getCalipso().isEmailSendingConfigured() && getPrincipal().hasRegularRoleForSpace(space)));
			// attachment ======================================================
			/*
			 * fileUploadField = new FileUploadField("fileField");
			 * setMaxSize(Bytes
			 * .megabytes(getJtrac().getAttachmentMaxSizeInMb()));
			 * 
			 * 
			 * final WebMarkupContainer fileUploadFieldContainer = new
			 * WebMarkupContainer("fileUploadFieldContainer");
			 * fileUploadFieldContainer.setOutputMarkupId(true);
			 * add(fileUploadFieldContainer);
			 * 
			 * final WebMarkupContainer fileUploadedContainer = new
			 * WebMarkupContainer("fileUploadedContainer");
			 * fileUploadedContainer.setOutputMarkupId(true);
			 * add(fileUploadedContainer);
			 * 
			 * setOutputMarkupId(true);
			 * 
			 * //TODO: move these to CustomFieldsFormPanel ~500 // we only need
			 * delete while making a comment, probably not here
			 * if(history.getAttachment() != null){
			 * fileUploadedContainer.add(new Label("fileName",
			 * history.getAttachment().getFileName()));
			 * fileUploadedContainer.add(new IndicatingAjaxLink("delete") {
			 * public void onClick(AjaxRequestTarget target) {
			 * history.setAttachment(null);
			 * 
			 * fileUploadedContainer.setVisible(false);
			 * target.addComponent(fileUploadedContainer);
			 * 
			 * fileUploadFieldContainer.remove("fileField");
			 * 
			 * fileUploadFieldContainer.add(fileUploadField);
			 * target.addComponent(fileUploadFieldContainer); } });
			 * 
			 * 
			 * fileUploadFieldContainer.add(new
			 * WebMarkupContainer("fileField").setVisible(false)); } else{
			 * fileUploadFieldContainer.add(fileUploadField);
			 * fileUploadedContainer.setVisible(false); }
			 */

			//sendNotificationsCheckBox.setDefaultModelObject(Boolean.FALSE)

			// validation that assignedTo is not null if status is not null and
			// not CLOSED
			// have to use FormValidator because this is conditional validation
			// across two FormComponents
//			add(new AbstractFormValidator() {
//				public FormComponent[] getDependentFormComponents() {
//					// actually we depend on assignedToChoice also, but wicket
//					// logs a warning when the
//					// component is not visible but we are doing ajax. anyway we
//					// use assignedToChoice.getInput()
//					// not assignedToChoice.convertedInput() so no danger there
//					return new FormComponent[] { statusChoice };
//				}
//
//				public void validate(Form unused) {
//					if (assignedToChoice.getInput() == null
//							|| assignedToChoice.getInput().trim().length() == 0) {
//						Integer i = (Integer) statusChoice.getConvertedInput();
//						if (i != null && i != State.CLOSED) {
//							// user may have customized the name of the CLOSED
//							// State e.g. for i18n
//							// so when reporting the error, use the display name
//							String closedDisplayName = space.getMetadata()
//									.getStatusValue(State.CLOSED);
//							assignedToChoice.error(localize(
//									"item_view_form.assignedTo.error",
//									closedDisplayName));
//						}
//					}
//				}
//			});
			// TODO: temporary comment out for demo
			/*
			add(new Button("preview") {
				@Override
				public void onSubmit() {
					preview = true;
				}
			});
			*/

		}

		public void setAssignableSpacesDropDownChoice(
				CustomFieldsFormPanel.AssignableSpacesDropDownChoice assignableSpacesDropDownChoice) {
			this.assignableSpacesDropDownChoice = assignableSpacesDropDownChoice;
		}
		/*
		@Override
		protected void validate() {
			filter.reset();
			super.validate();
		}
		*/

		@Override
		protected void onSubmit() {

			// TODO change
			//logger.debug("CORRECT SUBMIT");
			//logger.info("SUBMITTED STATUS: "+this.statusChoice.getModelObject());
			final History history = (History) getModelObject();
			String safeHtml = XmlUtils.removeXss(XmlUtils.removeComments(history.getComment()));
			history.setComment(XmlUtils.stripTags(safeHtml));
			history.setHtmlComment(safeHtml);
			User user = ((CalipsoSession) getSession()).getUser();
			history.setLoggedBy(user);

			// Add selected assets to this item
			if (availableAssetsPanel != null) {
				if (availableAssetsPanel.getSelectedAssets() != null) {
					item = getCalipso().loadItem(itemId);
					for (Asset asset : availableAssetsPanel.getSelectedAssets()) {
						item.addAsset(getCalipso().loadAsset(asset.getId()));
					}// for
				}// if
			}// if

			// update tree options etc
			for (Field field : item.getSpace().getMetadata().getFieldList()) {
				if (field.isDropDownType()){
					if(field.getCustomAttribute() != null && field.getCustomAttribute().getLookupValue() != null){
						//logger.info("Processing "+field.getName().getText()+", LOOKUP value object: "+ field.getCustomAttribute().getLookupValue());
						item.setValue(field.getName(), NumberUtils.createInteger(field.getCustomAttribute().getLookupValue().getId()+""));
						history.setValue(field.getName(), NumberUtils.createInteger(field.getCustomAttribute().getLookupValue().getId()+""));
					}
				}
			}
			// Space contains special State: MOVE_TO_OTHER_SPACE
			// if
			// (item.getSpace().getMetadata().getStatesMap().get(State.MOVE_TO_OTHER_SPACE)!=null){
			// //Assignable Spaces dropdown list contains some value but the
			// status is not equal to "MOVE_TO_OTHER_SPACE"
			// if (history.getAssignableSpaces()!=null &&
			// !statusChoice.getValue().equals(String.valueOf(State.MOVE_TO_OTHER_SPACE))){
			// history.setAssignableSpaces(null); //Set Assignable Spaces Value
			// to NULL
			// }//if
			// }//if

			if (false) {
			//if (preview) {
				preview = false;

				// TODO: remove this for FileUpload as we have probably already
				// saved TEMP attachments
				history.setParent(item);
				Set<Attachment> attachments = history.getAttachments();
				if (attachments != null && attachments.size() > 0) {
					for (Attachment attachment : attachments) {
						// TODO store it temporary
						// history.addAttachment((getJtrac().storeTemporaryAttachment(fileUploadField.getFileUpload())));
						history.addAttachment(attachment);
					}

				}

				BreadCrumbPanel activePanel = (BreadCrumbPanel) getBreadCrumbModel()
						.getActive();
				// BreadCrumbUtils.removeActiveBreadCrumbPanel(getBreadCrumbModel());

				activePanel.activate(new IBreadCrumbPanelFactory() {
					@Override
					public BreadCrumbPanel create(String id,
							IBreadCrumbModel breadCrumbModel) {
						return new HistoryPreviewPanel(id, breadCrumbModel,
								history, fileUploadFields, itemId, false /*
														 * statusChoice.getValue(
														 * )
														 * .equals(String.valueOf
														 * (
														 * State.MOVE_TO_OTHER_SPACE
														 * ))
														 */);
						// return new ItemViewFormPanel(id, breadCrumbModel,
						// item, itemSearch, history,
						// fileUploadField.getFileUpload());
					}
				});
			}
			else { 
				getCalipso().storeHistoryForItem(itemId, getNonNullUploads(), history);

				// Assignable Spaces dropdown list contains some value and the
				// status is equal to "MOVE_TO_OTHER_SPACE"
				// if (history.getAssignableSpaces()!=null &&
				// statusChoice.getValue().equals(String.valueOf(State.MOVE_TO_OTHER_SPACE)))
				// {
				// //Change item space
				// getJtrac().storeItemSpace(itemId,
				// getJtrac().loadSpace(history.getAssignableSpaces().getId()));
				// ItemSearch itemSearch = new ItemSearch(item.getSpace(),
				// getPrincipal(), ItemViewFormPanel.this);
				// setCurrentItemSearch(itemSearch);
				// //Redirect to the Item list because this item doesn't belong
				// to current space
				// //TODO check if works
				// BreadCrumbPanel activePanel =
				// (BreadCrumbPanel)getBreadCrumbModel().getActive();
				// BreadCrumbUtils.removeActiveBreadCrumbPanel(getBreadCrumbModel());
				//
				// activePanel.activate(new IBreadCrumbPanelFactory(){
				// public BreadCrumbPanel create(String componentId,
				// IBreadCrumbModel breadCrumbModel) {
				// return new ItemListPanel(componentId, breadCrumbModel);
				// }
				// });
				// //setResponsePage(ItemListPage.class);
				// }//if
				// else{
				BreadCrumbPanel activePanel = (BreadCrumbPanel) getBreadCrumbModel()
						.getActive();
				BreadCrumbUtils
						.removeActiveBreadCrumbPanel(getBreadCrumbModel());

				activePanel.activate(new IBreadCrumbPanelFactory() {
					@Override
					public BreadCrumbPanel create(String componentId,
							IBreadCrumbModel breadCrumbModel) {
						//logger.info("Before rerendering the itemViewPanel");
						//logger.info("history.getUniqueRefId() is null ? :"
						//		+ (history.getUniqueRefId() == null));
						return new ItemViewPanel(componentId, breadCrumbModel,
								history.getUniqueRefId());
					}
				});
				// }//else
			}

		}
	}

	// ---------------------------------------------------------------------------------------------

	@Override
	public void renderHead(IHeaderResponse headerResponse) {

		if (this.itemViewForm.hasError()) {
			StringBuffer javaScript = new StringBuffer("");

			javaScript
					.append("function init(){")
					.append("\n")
					.append("document.getElementById('history-section').scrollIntoView(true);")
					.append("\n").append("}").append("\n")
					.append("window.onload = init;").append("\n");

			headerResponse.renderJavaScript(javaScript.toString(), null);
		}
	}
	
	private class SubmitUtilDao implements Serializable{

		private Integer previousState = null;
		private Integer currentState = null;
		private Integer nextState = null;

		private String previousStateMessage = "previous";

		public String getPreviousStateMessage() {
			return this.previousStateMessage;
		}

		public boolean isClosed() {

			return this.currentState != null
					&& this.currentState.intValue() == State.CLOSED;
		}

		public void setPreviousStateMessage(String previousStateMessage) {
			this.previousStateMessage = previousStateMessage;
		}

		public String getNextStateMessage() {
			return nextStateMessage;
		}

		public void setNextStateMessage(String nextStateMessage) {
			this.nextStateMessage = nextStateMessage;
		}

		private String nextStateMessage = "next";
		private Boolean stateChangeAllowed = false;
		private Integer singleStateChangeAllowed = null;
		private List<Integer> states = null;
		
		public SubmitUtilDao(Map<Integer, String> statesMap, int currentState){
			List<Integer> states = new ArrayList(statesMap.keySet());

			this.currentState = currentState;
			this.states = states;
			if(CollectionUtils.isNotEmpty(states)){
				String currentStateName = statesMap.get(currentState);
				boolean isCurrentStateWithdrawn = StringUtils.isNotBlank(currentStateName) && "Withdrawn".equalsIgnoreCase(currentStateName.trim());
				Iterator<Integer> statesIter = states.iterator();
				Integer state = null;
				while(statesIter.hasNext()){
					state = statesIter.next();
					String stateName = statesMap.get(state);
					logger.debug("SubmitUtilDao state: " + state
							+ ", state name: " + stateName + ", Withdrawn: "
							+ ("Withdrawn".equalsIgnoreCase(stateName)));
					// is a previous state accessible?
					if(state.intValue() < currentState && state.intValue() > State.NEW){
						previousState = state;
						if ("Withdrawn".equalsIgnoreCase(stateName)) {
							setPreviousStateMessage("withdraw");
						}
						else if(isCurrentStateWithdrawn){
							setPreviousStateMessage("mask.update");
						}
						else{
							setPreviousStateMessage("previous");
						}
					}

					// is a next state available?
					if(state.intValue() > currentState){
						nextState = state;
						if (nextState.intValue() == 99) {
							setNextStateMessage("submit");
						}
						else if ("Withdrawn".equalsIgnoreCase(stateName)) {
							setNextStateMessage("withdraw");
						}
						else{
							setNextStateMessage("next");
						}
						break;
					}
				}
				logger.debug("SubmitUtilDao getPreviousStateMessage: "
						+ getPreviousStateMessage() + ", getNextStateMessage: "
						+ getNextStateMessage());
				// can state be changed at all?
				this.stateChangeAllowed = states.size() > 1 
						|| (states.size() == 1 && states.get(0).intValue() != currentState);
				this.singleStateChangeAllowed = states.size() == 1 ? states.get(0) : null;
				
				logger.debug("SubmitUtilDao initialized with previous: "
						+ previousState + ", next: " + nextState
						+ ", stateChangeAllowed: " + stateChangeAllowed
						+ ", lastStateBeforeClose: " + singleStateChangeAllowed
						+ ", previousStateMessage" + previousStateMessage
						+ ", nextStateMessage: " + nextStateMessage);
			}
		}


		public Integer getPreviousState() {
			return previousState;
		}

		public Integer getCurrentState() {
			return currentState;
		}

		public Integer getNextState() {
			return nextState;
		}
		
		public Boolean isStateChangeAllowed() {
			return stateChangeAllowed;
		}

		public Boolean isClosedAllowedOnly() {
			return this.singleStateChangeAllowed != null &&  this.singleStateChangeAllowed.intValue() == State.CLOSED;
		}

		public Integer getSingleStateChangeAllowed() {
			return singleStateChangeAllowed;
		}

		public List<Integer> getStates() {
			return states;
		}

		
	}
}