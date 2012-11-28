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

import gr.abiss.calipso.domain.AbstractItem;
import gr.abiss.calipso.domain.Attachment;
import gr.abiss.calipso.domain.Country;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.FieldGroup;
import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.ItemFieldCustomAttribute;
import gr.abiss.calipso.domain.Metadata;
import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.State;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.UserSpaceRole;
import gr.abiss.calipso.domain.ValidationExpression;
import gr.abiss.calipso.util.AttachmentUtils;
import gr.abiss.calipso.util.DateUtils;
import gr.abiss.calipso.wicket.components.LoadableDetachableDomainObjectModels.LoadableDetachableReadOnlyItemModel;
import gr.abiss.calipso.wicket.components.formfields.DateField;
import gr.abiss.calipso.wicket.components.formfields.MultipleValuesTextField;
import gr.abiss.calipso.wicket.components.formfields.TreeChoice;
import gr.abiss.calipso.wicket.components.renderers.UserChoiceRenderer;
import gr.abiss.calipso.wicket.components.validators.NumberValidator;
import gr.abiss.calipso.wicket.components.validators.PositiveNumberValidator;
import gr.abiss.calipso.wicket.components.validators.ValidationExpressionValidator;
import gr.abiss.calipso.wicket.form.FieldUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.string.Strings;
//import org.apache.wicket.util.template.TextTemplateHeaderContributor;
//import org.apache.wicket.validation.validator.NumberValidator;

/**
 * panel for custom fields that can be reused in the item-create / item-view
 * forms
 */
public class CustomFieldsFormPanel extends BasePanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(CustomFieldsFormPanel.class);

	private String assignableSpaceValue = "";
	private List<User> assignableSpaceUsers;
	private String autoCompleteModel;
	private DropDownChoice statusChoice;
	private DropDownChoice assignedToChoice;
	private ItemViewFormPanel itemViewFormPanel;
	private boolean editMode;
	private boolean historyMode;
	private IModel historyModel;
	private Item item;
	Space space;
	private AutoCompleteTextField autoTextField;
	public AssignableSpacesDropDownChoice assignableSpacesDropDownChoice;


	// =================================================================================================================

	/**
	 * Dropdown choice that shows all spaces where the item can be moved. The
	 * values will populated with Ajax.
	 **/
	public class AssignableSpacesDropDownChoice extends
			IndicatingDropDownChoice {

		public AssignableSpacesDropDownChoice(String id, List data,
				IChoiceRenderer iChoiceRenderer) {
			super(id, data, iChoiceRenderer);
		}// AssignableSpacesDropDownChoice

	}// AssignableSpacesDropDownChoice

	// =================================================================================================================

	/**
	 * 
	 * 
	 * Called from ItemFormPanel to Edit Item (NOT add History comment) you want
	 * to EDIT custom fields values for the item
	 * 
	 * @param id
	 *            The markup id
	 * @param model
	 *            The model is bound to History
	 * @param space
	 *            the space that the custom fields belong
	 * @param editMode
	 *            if the edit the custom fields
	 * 
	 */

	public CustomFieldsFormPanel(String id, CompoundPropertyModel model,
			Space space, boolean editMode,
			Map<String, FileUploadField> fileUploadFields) {
		// edit
		super(id);
		this.item = null;
		this.space = space;
		this.editMode = editMode;
		// List<Field> fields = space.getMetadata().getEditableFields();
		List<Field> fields = space.getMetadata().getFieldList();
		//addSimpleAttachmentIfSupported(space, fields);
		addComponents(model, space.getMetadata(), fields, fileUploadFields);

	}
	
	/**
	 * 
	 * Called from ItemFormPanel to create NEW Item with historyMode false
	 * Called from ItemViewFormPanel to add a history comment with historyMode
	 * true fields values for the item
	 * 
	 * @param id
	 *            The markup id
	 * @param model
	 *            The model is bound to item domain object
	 * @param item
	 * @param user
	 */
	public CustomFieldsFormPanel(String id, CompoundPropertyModel model, Item item, User user, boolean historyMode,
			Map<String, FileUploadField> fileUploadFields) {
		// NEW
		super(id);
		this.item = item;
		this.editMode = false;
		this.historyMode = historyMode;
		if (historyMode) {
			this.historyModel = model;
		}
		
		// make sure the relationship is ok
		List<Field> fields = item.getEditableFieldList(user);
		//addSimpleAttachmentIfSupported(getCurrentSpace(), fields);
		addComponents(model, item.getSpace().getMetadata(), fields, fileUploadFields);
	}

	// =================================================================================================================

	/**
	 * Reads the selected space value, where the item can be moved to.
	 * 
	 * @return The space where the item can be moved to.
	 * */
	public String getAssignableSpaceValue() {
		this.assignableSpaceValue = assignableSpacesDropDownChoice
				.getModelValue();
		return this.assignableSpaceValue;
	} // getAssignableSpaceValue

	// --------------------------------------------------------------------------------------------------------------------

	/**
	 * Reads the users of the selected "Assignable space", that they be appear
	 * in the "Assign to:" Dropdown choice.
	 * 
	 * @return A list of users, that appears in the "Assign to:" Dropdown
	 *         choice.
	 **/
	public List<User> getAssignableSpaceUsers() {
		assignableSpaceUsers = new LinkedList<User>();
		assignableSpaceValue = getAssignableSpaceValue();

		if (assignableSpaceValue != null && !assignableSpaceValue.equals("")
				&& !assignableSpaceValue.equals("-1")) {
			Space space = getCalipso().loadSpace(
					Long.parseLong(assignableSpaceValue));
			List<UserSpaceRole> userSpaceRoles = getCalipso()
					.findUserRolesForSpace(space.getId());
			// assignableSpaceUsers =
			// UserUtils.filterUsersAbleToTransitionFrom(userSpaceRoles, space,
			// State.MOVE_TO_OTHER_SPACE);
		}// if

		return assignableSpaceUsers;

	}// getAssignableSpaceUsers

	// --------------------------------------------------------------------------------------------------------------------

	public void setStatusChoice(DropDownChoice statusChoice) {
		this.statusChoice = statusChoice;
	}

	// --------------------------------------------------------------------------------------------------------------------

	public void setAssignedToChoice(DropDownChoice assignedToChoice) {
		this.assignedToChoice = assignedToChoice;
	}

	// --------------------------------------------------------------------------------------------------------------------

	public AssignableSpacesDropDownChoice getAssignableSpacesDropDownChoice() {
		return assignableSpacesDropDownChoice;
	}

	// --------------------------------------------------------------------------------------------------------------------

	public void setItemViewFormPanel(ItemViewFormPanel itemViewFormPanel) {
		this.itemViewFormPanel = itemViewFormPanel;
	}

	/**
	 * @return the autoCompleteModel
	 */
	public String getAutoCompleteModel() {
		return autoCompleteModel;
	}

	/**
	 * @param autoCompleteModel
	 *            the autoCompleteModel to set
	 */
	public void setAutoCompleteModel(String autoCompleteModel) {
		this.autoCompleteModel = autoCompleteModel;
	}

	// =================================================================================================================

/*
	private void addSimpleAttachmentIfSupported(Space space, List<Field> fields) {
		if(space.getSimpleAttachmentsSupport()){
			if(logger.isDebugEnabled()) logger.debug("Adding simple atachement FileInputField");
			Field field = new Field(AbstractItemFormPanel.SIMPLE_ATTACHEMENT_KEY);
			field.setFieldType(Field.FIELD_TYPE_SIMPLE_ATTACHEMENT);
			field.setLabel(Field.FIELD_TYPE_SIMPLE_ATTACHEMENT);
			fields.add(field);
		}
		else{
			if(logger.isDebugEnabled()) logger.debug("Simple attachments not supported in space");
		}
	}
*/
	/**
	 * Helper method to load possible values for a option select or tree 
	 * @return The list of possible choices
	 * */
	private List<CustomAttributeLookupValue> loadLookupValues(Field field) {
		return getCalipso().findLookupValues(this.space, field.getName().getText());
	}

	/**
	 * Helper method to load assignable spaces instead of just IDs
	 * @return The list of assignable Spaces
	 * */
	private List<Space> loadAssignableSpaces(Field field) {

		List<Space> spaces = new ArrayList<Space>();

		if (field.getOptions() == null) {
			return spaces;

		}// if
		List<String> assignableSpacesIds = new ArrayList<String>(field.getOptions().values());
		for (int i = 0; i < assignableSpacesIds.size(); i++) {
			long assignableSpaceId = Long
					.parseLong((String) assignableSpacesIds.get(i));
			spaces.add(getCalipso().loadSpace(assignableSpaceId));
		}// for
		return spaces;

	}// loadAssignableSpaces

	// --------------------------------------------------------------------------------------------------------------------

	@SuppressWarnings("serial")
	private void addComponents(final CompoundPropertyModel model,
			final Metadata metadata, 
			final List<Field> fields,
			final Map<String, FileUploadField> fileUploadFields) {
			//final AbstractItem item = (AbstractItem) model.getObject();
		List<FieldGroup> fieldGroupsList = metadata.getFieldGroups();
		@SuppressWarnings("unchecked")
		ListView fieldGroups = new ListView("fieldGroups", fieldGroupsList){

			@Override
			protected void populateItem(ListItem listItem) {
				FieldGroup fieldGroup = (FieldGroup) listItem.getModelObject();
				listItem.add(new Label("fieldGroupLabel", fieldGroup.getName()));
				List<Field> groupFields = fieldGroup.getFields();
				List<Field> editableGroupFields = new LinkedList<Field>();
				
				if(CollectionUtils.isNotEmpty(groupFields)){
					for(Field field : groupFields){
						// is editable?
						if(fields.contains(field)){
							editableGroupFields.add(field);
						}
					}
				}
				ListView listView = new ListView("fields", editableGroupFields) {

					@SuppressWarnings("deprecation")
					protected void populateItem(ListItem listItem) {
						boolean preloadExistingValue = true;
						final Field field = (Field) listItem.getModelObject();
						// preload custom attribute
						if(field.getCustomAttribute() == null){
							field.setCustomAttribute(getCalipso().loadItemCustomAttribute(getCurrentSpace(), field.getName().getText()));
						}
						// preload value?
						if(preloadExistingValue){
							AbstractItem history = (AbstractItem) model.getObject();
							history.setValue(field.getName(), item.getValue(field.getName()));
						}
						// return the value for the field (see abstract item getCustomValue method)
						Object fieldLastCustomValue = null;

						String i18nedFieldLabelResourceKey = item.getSpace().getPropertyTranslationResourceKey(field.getName().getText());
						
						if (item != null && !editMode) {
							if(field.getName().isFile()){
								Set<Attachment> tmpAttachments = item.getAttachments();
								if (tmpAttachments != null && tmpAttachments.size() > 0) {
									for (Attachment attachment : tmpAttachments) {
										if (field.getLabel().equals(AttachmentUtils.getBaseName(attachment.getFileName()))) {
											fieldLastCustomValue = attachment.getFileName();
											break;
										}
									}
								}
								
							}else{
								fieldLastCustomValue = item.getValue(field.getName());
							}
							
						}

						// Decide whether the field is mandatory

						boolean valueRequired = false;
						//logger.info("fieldLastCustomValue for field " + field.getLabel() +": "+fieldLastCustomValue);
						if (field.getMask() == State.MASK_MANDATORY
								|| (field.getMask() == State.MASK_MANDATORY_IF_EMPTY 
									&& (fieldLastCustomValue == null || StringUtils.isBlank(fieldLastCustomValue.toString())) )) {
							valueRequired = true; // value required
						}

						
						
						// go over custom fields
						WebMarkupContainer labelContainer = new WebMarkupContainer("labelContainer");
						listItem.add(labelContainer);
						
						SimpleFormComponentLabel label = null;
						if (field.getName().isDropDownType()) {
							String autoValue;
							final Map<String, String> options = field.getOptions();
							if(field.getFieldType().equals(Field.FIELD_TYPE_DROPDOWN_HIERARCHICAL)){
								Fragment f = new Fragment("field", "treeField", CustomFieldsFormPanel.this);
								// add HTML description through velocity
								addVelocityTemplatePanel(f, "htmlDescriptionContainer", "htmlDescription", field.getCustomAttribute().getHtmlDescription(), null, true);
								ItemFieldCustomAttribute customAttribute = field.getCustomAttribute();
//								if(customAttribute == null){
//									customAttribute = getCalipso().loadCustomAttribute(getCurrentSpace(), field.getName().getText());
//									field.setCustomAttribute(customAttribute);
//								}

								// preload existing value
								if(field.getCustomAttribute() != null 
										&& customAttribute.getLookupValue() == null
										&& item.getCustomValue(field) != null){
									customAttribute.setLookupValue(getCalipso().loadCustomAttributeLookupValue(NumberUtils.createLong(item.getCustomValue(field).toString())));
								}
								List<CustomAttributeLookupValue> customAttributeLookupValues = getCalipso().findLookupValuesByCustomAttribute(customAttribute);
								TreeChoice choice = new TreeChoice("field", new PropertyModel<CustomAttributeLookupValue>(field, "customAttribute.lookupValue"), customAttributeLookupValues, customAttribute);
								
								// TODO: temp, make configurable in space field form for 1520
								int attrId = customAttribute.getId().intValue();
								choice.setVisibleMenuLinks(attrId == 3 || attrId == 4 || attrId == 16 || attrId == 17);
								//choice.setType(Long.class);
								choice.setRequired(valueRequired);
								// i18n
								choice.setLabel(new ResourceModel(i18nedFieldLabelResourceKey));
								WebMarkupContainer border = new WebMarkupContainer("border");
								f.add(border);
								//border.add(new ErrorHighlighter(choice));
								border.add(choice);
								//choice.setModel(model.bind(field.getName().getText()));
								//border.add(model.bind(choice, field.getName().getText()));
								listItem.add(f.setRenderBodyOnly(true));
								label =new SimpleFormComponentLabel("label", choice);
							}
							// get the type of component that will render the choices
							else if (field.getFieldType().equals(
									Field.FIELD_TYPE_AUTOSUGGEST)) {

								renderAutoSuggest(model, listItem, field,
										i18nedFieldLabelResourceKey, valueRequired,
										options);

							} else {
								// normal drop down
								label = renderDropDown(model, listItem, field,
									i18nedFieldLabelResourceKey, valueRequired, options);

							}

						} else if (field.getName().equals(Field.Name.ASSIGNABLE_SPACES)) {
							Fragment f = new Fragment("field", "dropDown", CustomFieldsFormPanel.this);
							
							// add HTML description through velocity
							addVelocityTemplatePanel(f, "htmlDescriptionContainer", "htmlDescription", field.getCustomAttribute().getHtmlDescription(), null, true);
							
							List assignableSpaces = loadAssignableSpaces(field);
							final Map options = new LinkedHashMap();

							// Replace value from space id to space description for
							// reasons of appearance.
							for (int i = 0; i < assignableSpaces.size(); i++) {
								Space space = (Space) assignableSpaces.get(i);
								if (!space.equals(getCurrentSpace())) {
									options.put(space.getId(), localize(space.getNameTranslationResourceKey()));
								}// if
							}// for

							final List keys = new ArrayList(options.keySet());

							AssignableSpacesDropDownChoice choice = new AssignableSpacesDropDownChoice(
									"field", keys, new IChoiceRenderer() {
										public Object getDisplayValue(Object o) {
											return o;
										};

										public String getIdValue(Object o, int i) {
											return o.toString();
										};
									});

							choice.setNullValid(true);
							// i18n
							choice.setLabel(new ResourceModel(i18nedFieldLabelResourceKey));

							choice.setRequired(valueRequired);
							choice .add(new Behavior(){
							      public void renderHead(Component component, IHeaderResponse response) {
							    	  response.renderJavaScript(new JavaScripts().setAssignableSpacesId.asString(), "setAssignableSpacesId");
							      }
							});
							choice.add(new AjaxFormComponentUpdatingBehavior("onChange") {
								protected void onUpdate(AjaxRequestTarget target) {
									if (statusChoice != null
											&& !statusChoice.getDefaultModelObjectAsString()
													.equals("")
											&& !statusChoice.getDefaultModelObjectAsString()
													.equals("-1")) {
										// if
										// (statusChoice.getValue().equals(String.valueOf(State.MOVE_TO_OTHER_SPACE))){
										// assignedToChoice.setChoices(getAssignableSpaceUsers());
										// assignedToChoice.setNullValid(false);
										// assignedToChoice.setVisible(true);
										// }//if
									}// if
									target.appendJavaScript("assignableSpacesId="
											+ "'"
											+ assignableSpacesDropDownChoice
													.getMarkupId() + "';");
									// This can happen at the creation of a new item
									// where makes no sense to be moved to other space
									if (assignedToChoice != null) {
										target.addComponent(assignedToChoice);
									}
								}// onUpdate

								@Override
								protected void onError(AjaxRequestTarget arg0,
										RuntimeException arg1) {
									// Do nothing.
									// It happens only if the choice be set to NULL by
									// the user.
									// The exception occurs because the model binds to
									// space id that is from (primitive) type
									// long and its value can not be set to NULL.
								}

							});

							choice.setOutputMarkupId(true);
							assignableSpacesDropDownChoice = choice;

							if (itemViewFormPanel != null) {
								itemViewFormPanel.getItemViewForm()
										.setAssignableSpacesDropDownChoice(
												assignableSpacesDropDownChoice);
							}// if

							WebMarkupContainer border = new WebMarkupContainer("border");
							f.add(border);
							border.add(new ErrorHighlighter(choice));
							// Set field name explicitly to avoid runtime error
							//border.add(model.bind(choice, field.getName() + ".id"));
							choice.setModel(model.bind(field.getName() + ".id"));
							border.add(choice);
							listItem.add(f.setRenderBodyOnly(true));
							border.setVisible(!CustomFieldsFormPanel.this.editMode);
							label =new SimpleFormComponentLabel("label", choice);
						} else if (field.getName().getType() == 6) {
							// date picker
							Fragment fragment = new Fragment("field", "dateFragment", CustomFieldsFormPanel.this);
							// add HTML description through velocity
							addVelocityTemplatePanel(fragment, "htmlDescriptionContainer", "htmlDescription", field.getCustomAttribute().getHtmlDescription(), null, true);
							
							
							if(item.getId() == 0 && item.getValue(field.getName()) == null  
									&& field.getDefaultValueExpression() != null 
									&&  field.getDefaultValueExpression().equalsIgnoreCase("now")){
								item.setValue(field.getName(), new Date());
							}
							DateField calendar = new DateField(
									"field",
									preloadExistingValue 
										? new PropertyModel(model.getObject(), field.getName().getText()) 
										: new PropertyModel(model, field.getName().getText())){

											@Override
											protected String getDateFormat() {
												// TODO Auto-generated method stub
												return metadata.getDateFormats().get(Metadata.DATE_FORMAT_SHORT);
											}
								
							};
							calendar.setRequired(valueRequired);
							// i8n
							calendar.setLabel(new ResourceModel(i18nedFieldLabelResourceKey));

							fragment.add(calendar);
							listItem.add(fragment.setRenderBodyOnly(true));
							label =new SimpleFormComponentLabel("label", calendar);
							
						}
						// TODO: Creating new space item - users custom field
						else if (field.getName().isOrganization()) { // is organization
							// Get users list
							Fragment f = new Fragment("field", "dropDownFragment", CustomFieldsFormPanel.this);
							// add HTML description through velocity
							addVelocityTemplatePanel(f, "htmlDescriptionContainer", "htmlDescription", field.getCustomAttribute().getHtmlDescription(), null, true);
							
							Organization organization = (Organization) item
									.getValue(field.getName());
							// logger.debug("Item organization field has value: "+organization);
							final List<Organization> allOrganizationsList = getCalipso()
									.findAllOrganizations();
							DropDownChoice organizationChoice = new DropDownChoice(
									"field", allOrganizationsList,
									new IChoiceRenderer() {
										// user's choice renderer
										// display value user's name
										public Object getDisplayValue(Object object) {
											return ((Organization) object).getName();
										}

										public String getIdValue(Object object,
												int index) {
											// id value user's id
											return index+"";
										}
									});

							organizationChoice.add(new ErrorHighlighter());

							organizationChoice.setRequired(valueRequired);
							// i18n
							organizationChoice.setLabel(new ResourceModel(i18nedFieldLabelResourceKey));
							//f.add(model.bind(organizationChoice, field.getName().getText()));
							organizationChoice.setModel(model.bind(field.getName().getText()));
							f.add(organizationChoice);
							listItem.add(f.setRenderBodyOnly(true));
							label =new SimpleFormComponentLabel("label", organizationChoice);
						}

						else if (field.getName().isFile()) {
							// File
							addFileInputField(model, fileUploadFields, listItem, field,
									i18nedFieldLabelResourceKey, valueRequired);

						}
						// TODO: Creating new space item - organizations custom field
						else if (field.getName().isUser()) { // is user

							Fragment f = new Fragment("field", "dropDownFragment", CustomFieldsFormPanel.this);
							// add HTML description through velocity
							addVelocityTemplatePanel(f, "htmlDescriptionContainer", "htmlDescription", field.getCustomAttribute().getHtmlDescription(), null, true);
							
							// find organization id from field's options

							String strOrgId = field.getOptions().get("organizationId");
							Long orgId = Long.valueOf(strOrgId);
							// load organization from database

							Organization selectedOrg = getCalipso().loadOrganization(
									orgId);
							// TODO: load all users from organization
							// add new list of selected organizations
							List<Organization> selectedOrganization = new ArrayList<Organization>();
							// add selected organization
							selectedOrganization.add(selectedOrg);

							final List<User> usersFromOrganization = getCalipso()
									.findUsersInOrganizations(selectedOrganization);

							// TODO: dropdown of all organization

							DropDownChoice usersFromOrganizationChoice = new DropDownChoice(
									"field", usersFromOrganization, new UserChoiceRenderer());
							usersFromOrganizationChoice.setNullValid(false);
							usersFromOrganizationChoice.setRequired(valueRequired);
							// i18n
							usersFromOrganizationChoice.setLabel(new ResourceModel(i18nedFieldLabelResourceKey));
							//f.add(model.bind(usersFromOrganizationChoice, field.getName().getText()));
							usersFromOrganizationChoice.setModel(model.bind(field.getName().getText()));
							f.add(usersFromOrganizationChoice);
							listItem.add(f.setRenderBodyOnly(true));
							label =new SimpleFormComponentLabel("label", usersFromOrganizationChoice);
							
						} else if (field.getName().isCountry()) {
							// organization fragment holds a dropDown of countries
							final List<Country> allCountriesList = getCalipso()
									.findAllCountries();
							Fragment f = new Fragment("field", "dropDownFragment", CustomFieldsFormPanel.this);
							// add HTML description through velocity
							addVelocityTemplatePanel(f, "htmlDescriptionContainer", "htmlDescription", field.getCustomAttribute().getHtmlDescription(), null, true);
							
							listItem.add(f.setRenderBodyOnly(true));
							DropDownChoice countryChoice = getCountriesDropDown("field", allCountriesList);
							countryChoice.add(new ErrorHighlighter());

							countryChoice.setRequired(valueRequired);
							// i18n
							countryChoice.setLabel(new ResourceModel(i18nedFieldLabelResourceKey));

							//f.add(model.bind(countryChoice, field.getName().getText()));
							countryChoice.setModel(model.bind(field.getName().getText()));
							f.add(countryChoice);
							label =new SimpleFormComponentLabel("label",countryChoice);

						}

						else {
							if(logger.isDebugEnabled()) logger.debug("model.getObject(): "+model.getObject());
							if(logger.isDebugEnabled()) logger.debug("model.getObject().getClass(): "+model.getObject().getClass().getName());
							if(logger.isDebugEnabled()) logger.debug("field.getName().getText(): "+field.getName().getText());
							//if(logger.isDebugEnabled()) logger.debug("((Item)model.getObject()).getCusStr01(): "+((Item)model.getObject()).getCusStr01());
							FormComponent textField;
							Fragment f;
							
							
							if(field.isMultivalue()){
								f = new Fragment("field", "multipleValuesTextField", CustomFieldsFormPanel.this);
								// add HTML description through velocity
								addVelocityTemplatePanel(f, "htmlDescriptionContainer", "htmlDescription", field.getCustomAttribute().getHtmlDescription(), null, true);
								
								if(logger.isDebugEnabled()) logger.debug("model.getObject(): "+model.getObject());
								if(logger.isDebugEnabled()) logger.debug("field.getName().getText(): "+field.getName().getText());
								textField = preloadExistingValue
									? new MultipleValuesTextField("field", new PropertyModel(model.getObject(), field.getName().getText()), field.getXmlConfig())
									: new MultipleValuesTextField("field", field.getXmlConfig());
							}
							else if(field.getLineCount() == 1){
								f = new Fragment("field", "textField", CustomFieldsFormPanel.this);
								// add HTML description through velocity
								addVelocityTemplatePanel(f, "htmlDescriptionContainer", "htmlDescription", field.getCustomAttribute().getHtmlDescription(), null, true);
								textField = preloadExistingValue 
									? new TextField("field", new PropertyModel(model.getObject(), field.getName().getText()))
									: new TextField("field");
							}
							else{
								f = new Fragment("field", "textareaField", CustomFieldsFormPanel.this);
								// add HTML description through velocity
								addVelocityTemplatePanel(f, "htmlDescriptionContainer", "htmlDescription", field.getCustomAttribute().getHtmlDescription(), null, true);
								textField = preloadExistingValue
									? new TextArea("field", new PropertyModel(model.getObject(), field.getName().getText())) {
										@Override
										protected void onComponentTag(ComponentTag tag) {		
											super.onComponentTag(tag);
											tag.put("rows", field.getLineCount().toString());
										}
									}
									: new TextArea("field") {
										@Override
										protected void onComponentTag(ComponentTag tag) {		
											super.onComponentTag(tag);
											tag.put("rows", field.getLineCount().toString());
										}
									};
							}
							
							// any validations for this field?
							ValidationExpression validationExpression = getCalipso().loadValidationExpression(
									field.getValidationExpressionId());
							if(validationExpression != null){
								textField.add(new ValidationExpressionValidator(validationExpression));
							}
							
							// TODO: do we need these two?
							if (field.getName().getType() == 4) {
								textField.setType(Double.class);
							}
							else if(field.getName().isDecimalNumber()){
								textField.add(new PositiveNumberValidator());
							}
						
							textField.add(new ErrorHighlighter());
							textField.setRequired(valueRequired);
							// i18n
							textField.setLabel(new ResourceModel(i18nedFieldLabelResourceKey));
							if(preloadExistingValue){
								f.add(textField);
							}
							else{
								//f.add(model.bind(textField, field.getName().getText()));
								textField.setModel(model.bind(field.getName().getText()));
								f.add(textField);
							}
							// f.add(textField);
							listItem.add(f.setRenderBodyOnly(true));
							label =new SimpleFormComponentLabel("label", textField);
							
							// styles
							FieldUtils.appendFieldStyles(field.getXmlConfig(), textField);
						}
						
						// add label
						labelContainer.add(label != null? label : new Label("label", ""));
						if(StringUtils.isBlank(field.getCustomAttribute().getHtmlDescription())){
							labelContainer.add(new SimpleAttributeModifier("class","labelContainer"));
						}
						// mandatory?

						// mark as mandatory in the UI?
						labelContainer.add(new Label("star", valueRequired ? "* " : " ")
								.setEscapeModelStrings(false));
					}

					private SimpleFormComponentLabel renderAutoSuggest(
							final IModel model, ListItem listItem,
							final Field field, String i18nedFieldLabelResourceKey,
							boolean valueRequired, final Map<String, String> options) {
						SimpleFormComponentLabel label = null;
						Fragment f = new Fragment("field", "autoCompleteFragment", CustomFieldsFormPanel.this);
						// add HTML description through velocity
						addVelocityTemplatePanel(f, "htmlDescriptionContainer", "htmlDescription", field.getCustomAttribute().getHtmlDescription(), null, true);
						
						final List<String> autoKeys = new ArrayList<String>(
								options != null ? new ArrayList<String>(
										options.keySet())
										: new ArrayList<String>());

						// the autocomplet textField
						// TODO: fix to render values instead of integer
						AbstractAutoCompleteRenderer autoRenderer = new AbstractAutoCompleteRenderer() {


							@Override
							protected String getTextValue(Object object) {
								// TODO Auto-generated method stub
								return object.toString();
							}

							@Override
							protected void renderChoice(Object object,
									Response response, String criteria) {
								response.write(object.toString());
							}

						};

						autoTextField = new AutoCompleteTextField("field", new PropertyModel(field, "customAttribute.lookupValue")) {

							// TODO: the list
							@Override
							protected Iterator<String> getChoices(String input) {

								if (Strings.isEmpty(input)) {
									List<String> emptyList = Collections
											.emptyList();
									return emptyList.iterator();
								}
								List<String> searchResults = new ArrayList<String>();

								for (String s : options.values()) {
									if (s.startsWith(input)) {
										searchResults.add(s);
									}
								}
								return searchResults.iterator();
							}

						};
						autoTextField
								.add(new AjaxFormComponentUpdatingBehavior(
										"onchange") {

									@Override
									protected void onUpdate(
											AjaxRequestTarget target) {
										// TODO Auto-generated method stub
										List<String> searchResults = new ArrayList<String>();
										for (String s : options.values()) {
											if (s.startsWith((String) autoTextField
													.getModelObject())) {
												searchResults.add(s);
											}
										}
										target.addComponent(autoTextField);
									}
								});
						// i18n
						autoTextField.setLabel(new ResourceModel(i18nedFieldLabelResourceKey));
						String componentValue = (String) ((autoTextField != null && autoTextField
								.getModelObject() != null) ? autoTextField
								.getModelObject() : null);

						autoTextField.setRequired(valueRequired);
						WebMarkupContainer border = new WebMarkupContainer(
								"border");
						f.add(border);
						border.add(new ErrorHighlighter(autoTextField));
						autoTextField.setModel(new PropertyModel(model.getObject(), field.getName().getText()));
						//border.add(model.bind(autoTextField, field.getName().getText()));
						border.add(autoTextField);
						if (logger.isDebugEnabled()
								&& autoTextField != null
								&& autoTextField.getDefaultModelObjectAsString() != null) {
							if(logger.isDebugEnabled()) logger.debug("Auto complete value is :"
									+ autoTextField.getDefaultModelObjectAsString());
						}
						listItem.add(f.setRenderBodyOnly(true));
						label =new SimpleFormComponentLabel("label",autoTextField);
						return label;

						// -------------------------
					}

					private SimpleFormComponentLabel addFileInputField(
							final CompoundPropertyModel model,
							final Map<String, FileUploadField> fileUploadFields,
							ListItem listItem, final Field field,
							String i18nedFieldLabelResourceKey, boolean valueRequired) {
						SimpleFormComponentLabel label = null;
						Fragment f = new Fragment("field", "fileField", CustomFieldsFormPanel.this);
						// add HTML description through velocity
						addVelocityTemplatePanel(f, "htmlDescriptionContainer", "htmlDescription", field.getCustomAttribute().getHtmlDescription(), null, true);
						
						FileUploadField fileField = new FileUploadField("field");
						fileUploadFields.put(field.getLabel(), fileField);
						fileField.add(new ErrorHighlighter());

						fileField.setRequired(valueRequired);
						// i18n
						fileField.setLabel(new ResourceModel(field.getName().getText().equalsIgnoreCase(Field.FIELD_TYPE_SIMPLE_ATTACHEMENT) ? Field.FIELD_TYPE_SIMPLE_ATTACHEMENT : i18nedFieldLabelResourceKey));

						//f.add(model.bind(fileField, field.getName().getText()));
						fileField.setModel(model.bind(field.getName().getText()));
						f.add(fileField);
						listItem.add(f.setRenderBodyOnly(true));
						label =new SimpleFormComponentLabel("label", fileField);
						return label;
					}

					/**
					 * @param model
					 * @param listItem
					 * @param field
					 * @param i18nedFieldLabelResourceKey
					 * @param valueRequired
					 * @param options
					 */
					private SimpleFormComponentLabel renderDropDown(final IModel model,
							ListItem listItem, final Field field,
							String i18nedFieldLabelResourceKey, boolean valueRequired,
							final Map<String, String> options) {
						SimpleFormComponentLabel label = null;
						/*
						 final List<CustomAttributeLookupValue> lookupValues = getCalipso().findLookupValuesByCustomAttribute(field.getCustomAttribute());
								TreeChoice choice = new TreeChoice("field", new PropertyModel(field, "customAttribute.lookupValue"), lookupValues);
								choice.setType(CustomAttributeLookupValue.class);
								//choice.setType(Long.class);
								choice.setRequired(valueRequired);
								// i18n
								choice.setLabel(new ResourceModel(i18nedFieldLabelResourceKey));
								WebMarkupContainer border = new WebMarkupContainer("border");
								f.add(border);
								//border.add(new ErrorHighlighter(choice));
								border.add(choice);
								
						 */
						
						// drop down list
							final Fragment fragment = new Fragment("field", "dropDown", CustomFieldsFormPanel.this);
							// add HTML description through velocity
							addVelocityTemplatePanel(fragment, "htmlDescriptionContainer", "htmlDescription", field.getCustomAttribute().getHtmlDescription(), null, true);
							if(field.getCustomAttribute() == null){
								field.setCustomAttribute(getCalipso().loadItemCustomAttribute(getCurrentSpace(), field.getName().getText()));
							}
							final List<CustomAttributeLookupValue> lookupValues = getCalipso().findLookupValuesByCustomAttribute(field.getCustomAttribute());
							// preselect previous user choice from DB if available
							Object preselected = item.getValue(field.getName());
							if(preselected != null){
								String sPreselectedId = preselected.toString();
								if(CollectionUtils.isNotEmpty(lookupValues)){
									for(CustomAttributeLookupValue value : lookupValues){
										if((value.getId()+"").equals(sPreselectedId)){
											field.getCustomAttribute().setLookupValue(value);
											break;
										}
									}
								}
							}
							// else set using the default string value instead, if any
							// TODO: move this into a LookupValueDropDownChoice class
							else{
								String defaultStringValue = field.getCustomAttribute().getDefaultStringValue();
								if(defaultStringValue != null && CollectionUtils.isNotEmpty(lookupValues)){
									for(CustomAttributeLookupValue value : lookupValues){
										if(value.getValue().equals(defaultStringValue)){
											field.getCustomAttribute().setLookupValue(value);
											break;
										}
									}
								}
							}
							DropDownChoice choice = new DropDownChoice("field", new PropertyModel(field, "customAttribute.lookupValue"), 
									lookupValues, new IChoiceRenderer<CustomAttributeLookupValue>() {
										public Object getDisplayValue(
												CustomAttributeLookupValue o) {
											return fragment.getString(o.getNameTranslationResourceKey());
										}

										public String getIdValue(
												CustomAttributeLookupValue o,
												int index) {
											return String.valueOf(index);
										}
							});
							choice.setNullValid(true);
							
							// i18n
							choice.setLabel(new ResourceModel(i18nedFieldLabelResourceKey));
							choice.setRequired(valueRequired);
							WebMarkupContainer border = new WebMarkupContainer("border");
							fragment.add(border);
							border.add(new ErrorHighlighter(choice));
							//border.add(model.bind(choice, field.getName().getText()));
							border.add(choice);
							listItem.add(fragment.setRenderBodyOnly(true));
							label =new SimpleFormComponentLabel("label", choice);
							return label;
					}
					/**
					 * @param model
					 * @param listItem
					 * @param field
					 * @param i18nedFieldLabelResourceKey
					 * @param valueRequired
					 * @param options
					 */
					private SimpleFormComponentLabel renderPeriodPartDropDown(final CompoundPropertyModel model,
							ListItem listItem, final Field field,
							String i18nedFieldLabelResourceKey, boolean valueRequired,
							final List<Date> options) {
						SimpleFormComponentLabel label = null;
						// drop down list
							Fragment f = new Fragment("field", "dropDown", CustomFieldsFormPanel.this);

							// add HTML description through velocity
							addVelocityTemplatePanel(f, "htmlDescriptionContainer", "htmlDescription", field.getCustomAttribute().getHtmlDescription(), null, true);
							
							DropDownChoice choice = new DropDownChoice("field",
									options, new IChoiceRenderer() {
										public Object getDisplayValue(Object o) {
											return DateUtils.format((Date) o);
										};

										public String getIdValue(Object o, int i) {
											return String.valueOf(i);
										};
									});
							choice.setNullValid(true);
							// i18n
							choice.setLabel(new ResourceModel(i18nedFieldLabelResourceKey));
							choice.setRequired(valueRequired);
							WebMarkupContainer border = new WebMarkupContainer("border");
							f.add(border);
							border.add(new ErrorHighlighter(choice));
							//border.add(model.bind(choice, field.getName().getText()));
							choice.setModel(model.bind(field.getName().getText()));
							border.add(choice);
							listItem.add(f.setRenderBodyOnly(true));
							label =new SimpleFormComponentLabel("label", choice);
							return label;
					}
				};
				if(editableGroupFields.isEmpty()){
					listItem.setVisible(false);
				}
				listView.setReuseItems(true);
				listItem.add(listView.setRenderBodyOnly(true));
				
			}
			
		};
		add(fieldGroups.setReuseItems(true));
		
		
		// 
	}
}
