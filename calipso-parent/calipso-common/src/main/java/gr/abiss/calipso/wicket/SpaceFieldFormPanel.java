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

import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.FieldGroup;
import gr.abiss.calipso.domain.ItemFieldCustomAttribute;
import gr.abiss.calipso.domain.Language;
import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.wicket.customattrs.CustomAttributeOptionsPanel;
import gr.abiss.calipso.wicket.customattrs.CustomAttributeUtils;
import gr.abiss.calipso.wicket.regexp.FieldConfigPanel;
import gr.abiss.calipso.wicket.regexp.ValidationPanel;
import gr.abiss.calipso.wicket.space.panel.TextFieldExtraPropertiesPanel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

/**
 * @author marcello
 */
public class SpaceFieldFormPanel extends BasePanel {
	
	private static final Logger logger = Logger.getLogger(SpaceFieldFormPanel.class);

	private Space space;
	private Field field;

	// the available components type for the custom fields that have options
	// list
	public static final List<String> optionsComponentList = Arrays.asList(
			Field.FIELD_TYPE_DROPDOWN, Field.FIELD_TYPE_AUTOSUGGEST, Field.FIELD_TYPE_DROPDOWN_HIERARCHICAL);

	public SpaceFieldFormPanel(String id, IBreadCrumbModel breadCrumbModel,
			Space space, Field field) {
		super(id, breadCrumbModel);
		this.space = space;
		this.field = field;	
		//logger.info("creating new lookupValue and adding to list");
///		lookupValue = new CustomAttributeLookupValue();
		
		setupVisuals();
		deleteLink();
		addComponents();
	}

	private void addComponents() {
		add(new SpaceFieldForm("form", field));
	}

	public String getTitle() {
		return localize("space_field_form.title");
	}

	private void setupVisuals() {
		// cancel ==========================================================
		getBackLinkPanel().makeCancel(
				new BreadCrumbLink("link", getBreadCrumbModel()) {
					@Override
					protected IBreadCrumbParticipant getParticipant(
							String componentId) {
						BreadCrumbUtils
								.removePreviousBreadCrumbPanel(getBreadCrumbModel());
						return new SpaceFieldListPanel(componentId,
								getBreadCrumbModel(), space, field.getName()
										.getText());
					}

				});
	}

	private void deleteLink() {
		boolean canBeDeleted = 
			Boolean.parseBoolean(((CalipsoApplication)Application.get()).
					getCalipsoPropertyValue("allow.delete.customField")) && space.getMetadata().getFields().containsKey(field.getName());
		if (!canBeDeleted) {
			add(new WebMarkupContainer("delete").setVisible(false));
			return;
		}

		// delete button only if edit ======================================
		add(new Link("delete") {
			@Override
			public void onClick() {
				int affectedCount = 0;
				if (!space.isNew()){
					affectedCount = getCalipso()
							.loadCountOfRecordsHavingFieldNotNull(space, field);
				}
				
				logger.info("space is New: "+space.isNew()+", affectedCount: "+affectedCount);
				if (affectedCount > 0) {
					final String heading = localize("space_field_delete.confirm")
							+ " : "
							+ field.getLabel()
							+ " ["
							+ field.getName().getDescription()
							+ " - "
							+ field.getName().getText() + "]";
					final String warning = localize("space_field_delete.line3");
					final String line1 = localize("space_field_delete.line1");
					final String line2 = localize("space_field_delete.line2",
							affectedCount + "");
					activate(new IBreadCrumbPanelFactory() {
						public BreadCrumbPanel create(String componentId,
								IBreadCrumbModel breadCrumbModel) {
							ConfirmPanel confirm = new ConfirmPanel(
									componentId, breadCrumbModel, heading,
									warning, new String[] { line1, line2 }) {
								public void onConfirm() {
									// database will be updated, if we don't do
									// this
									// user may leave without committing
									// metadata change
									logger.info("bulkUpdateFieldToNull...");
									getCalipso().bulkUpdateFieldToNull(space,
											field);

									logger.info("remove field from metadata...");
									space.getMetadata().removeField(
											field.getName().getText());

									logger.info("delete custom attribute...");
									getCalipso().removeItemCustomAttribute(space, field.getName().getText());

									logger.info("save space...");
									getCalipso().storeSpace(space);
									// synchronize metadata version or else if
									// we save again we get Stale Object
									// Exception
//									space.setMetadata(getCalipso().loadMetadata(
//											space.getMetadata().getId()));

									activate(new IBreadCrumbPanelFactory() {
										public BreadCrumbPanel create(
												String componentId,
												IBreadCrumbModel breadCrumbModel) {
											BreadCrumbUtils
													.moveToPanelForRelod(
															breadCrumbModel,
															SpaceFieldListPanel.class);
											return new SpaceFieldListPanel(
													componentId,
													breadCrumbModel, space,
													null);
										}
									});
								};
							};
							return confirm;
						}
					});

				}
				else{

					logger.info("space is new, removing attr from metadata only...");
					// this is an unsaved space or there are no impacted items
					space.getMetadata().removeField(field.getName().getText());

					logger.info("delete custom attribute...");
					getCalipso().removeItemCustomAttribute(space, field.getName().getText());

					// setResponsePage(new SpaceFieldListPage(space, null,
					// previous));
					activate(new IBreadCrumbPanelFactory() {
						public BreadCrumbPanel create(String id,
								IBreadCrumbModel breadCrumbModel) {
							BreadCrumbUtils.moveToPanelForRelod(
									breadCrumbModel, SpaceFieldListPanel.class);
							return new SpaceFieldListPanel(id, breadCrumbModel,
									space, null);
						}
					});
				}

			}
		});
	}

	/**
	 * wicket form
	 */
	private class SpaceFieldForm extends Form {

		// private TextField optionField;
		DropDownChoice organizationChoice;
		DropDownChoice optionsComponentChoice;

		private Field field;
		private Space formSpace;
		// private String option;
		private Map<String, String> textAreaOptions = new HashMap<String, String>();

		public Field getField() {
			return field;
		}

		public void setField(Field field) {
			this.field = field;
		}
		public Space getFormSpace() {
			return this.formSpace;
		}

		public void setFormSpace(Space space) {
			this.formSpace = space;
		}

		@SuppressWarnings("unchecked")
		public SpaceFieldForm(String id, final Field _field) {

			super(id);
			this.field = _field;
			this.formSpace = space;
			// TODO
			FeedbackPanel feedback = SpaceFieldFormPanel.this.getFeedbackPanel("feedback");
			add(feedback);

			//final CompoundPropertyModel model = new CompoundPropertyModel(this);
			//setModel(model);

			// internal name ===================================================
			add(new Label("name", new PropertyModel(field, "name.text")));
			// label ===========================================================
			ItemFieldCustomAttribute attribute = this.field.getCustomAttribute();
			if(attribute == null){
				logger.info("field has no custom attribute, loading from DB");
				attribute = getCalipso().loadItemCustomAttribute(space, field.getName().getText());
				if(attribute == null){
					logger.info("DB has no custom attribute for field, creating new with name: "+field.getName().getText());
					attribute = new ItemFieldCustomAttribute();
					attribute.setSpace(space);
					attribute.setFieldName(field.getName().getText());
					attribute.setName(field.getName().getText());
					attribute.setEditable(true);
				}
				this.field.setCustomAttribute(attribute);

				logger.info("added custom attribute to field: "+attribute);
				logger.info("added custom attribute to field: "+field.getCustomAttribute());
			}
			final String fieldInternalName = field.getName().getText();
			if(MapUtils.isEmpty(space.getPropertyTranslations(fieldInternalName))){
				space.setPropertyTranslations(fieldInternalName, getCalipso().getPropertyTranslations(fieldInternalName, space));	
			}
			add(new ListView("nameTranslations", space.getSupportedLanguages()){
				protected void populateItem(ListItem listItem) {
					//logger.debug("Building translation fields for space field: "+fieldInternalName);
					Language language = (Language) listItem.getModelObject();
					TextField description = new TextField("name");
					description.setType(String.class);
					// name translations are required.
					description.setRequired(true);
					description.add(new ErrorHighlighter());
					listItem.add(description);
					
					String exp = new StringBuffer("formSpace.translations[")
								.append(fieldInternalName)
								.append("][")
								.append(language.getId())
								.append("]")
								.toString();
					description.setModel(new PropertyModel(SpaceFieldForm.this, exp));
					// form label for name
					description.setLabel(new ResourceModel("language."+language.getId()));
					listItem.add(new SimpleFormComponentLabel("languageLabel", description));
				}
			}.setReuseItems(true));
			
			List fieldGroups = space.getMetadata().getFieldGroups();
			// only the groupId loaded form the field XML
//			logger.info("field group Id loaded from XML: "+field.getGroupId());
//			logger.info("field group set by metadata: "+field.getGroup());
			if(field.getGroupId() == null){
				FieldGroup fg = space.getMetadata().getDefaultFieldGroup();
				field.setGroup(fg);
				fg.addField(_field);
				
			}

//			logger.info("field group Id loaded from XML: "+field.getGroupId());
//			logger.info("field group set by local code: "+field.getGroup());
			field.setGroup(space.getMetadata().getFieldGroupsById().get(field.getGroupId()));
			DropDownChoice fieldGroupsChoice = new DropDownChoice("group", new PropertyModel(field, "group"), fieldGroups)/*{

				@Override
				protected void onSelectionChanged(Object newSelection) {
					// TODO Auto-generated method stub
					super.onSelectionChanged(newSelection);
					logger.info("newSelection: "+newSelection.getClass());
				}

				@Override
				protected boolean wantOnSelectionChangedNotifications() {
					// TODO Auto-generated method stub
					return true;
				}
				
			}*/;
			fieldGroupsChoice.setChoiceRenderer(new IChoiceRenderer<FieldGroup>(){

				public Object getDisplayValue(FieldGroup fieldGroup) {
					return fieldGroup.getName();
					}

				public String getIdValue(FieldGroup object, int index) {
					return index+"";
				}
			});
			fieldGroupsChoice.setNullValid(false);
			fieldGroupsChoice.setRequired(true);
			fieldGroupsChoice.setLabel(new ResourceModel("fieldGroup"));
			add(fieldGroupsChoice);
			add(new SimpleFormComponentLabel("groupLabel", fieldGroupsChoice));

			
			CheckBox showInSearchResultsCheckbox = new CheckBox("showInSearchResultsCheckbox", new PropertyModel(attribute, "showInSearchResults"));
			showInSearchResultsCheckbox.setLabel(new ResourceModel("showInSearchResultsLabel"));
			add(showInSearchResultsCheckbox);
			add(new SimpleFormComponentLabel("showInSearchResultsLabel", showInSearchResultsCheckbox));

			
			TextArea htmlDescriptionTextArea = new TextArea("htmlDescriptionTextArea", new PropertyModel(attribute, "htmlDescription"));
			htmlDescriptionTextArea.setLabel(new ResourceModel("htmlDescriptionLabel"));
			add(htmlDescriptionTextArea);
			add(new SimpleFormComponentLabel("htmlDescriptionLabel", htmlDescriptionTextArea));
			
			
			// form label
			//label.setLabel(new ResourceModel("space_field_form.label"));
			//add(new SimpleFormComponentLabel("labelLabel", label));
			if(field.getName().isFreeText()){
				add(new TextFieldExtraPropertiesPanel("textFieldExtraPropertiesPanel", new CompoundPropertyModel(field)));
			}
			else{
				add(new EmptyPanel("textFieldExtraPropertiesPanel"));
			}
			/*
			 * Validation expression Panel adds a textField and a dropDownChoice
			 * to the form
			 */
			//if(field.getName().isFreeText() || field.getName().isDate()){
				add(new FieldConfigPanel("xmlConfigPanel",
						new CompoundPropertyModel(field), false));
			//}
//			else{
//				add(new EmptyPanel("xmlConfigPanel"));
//			}
			if (field.getName().isFreeText()) {
				logger.info("Validation expression before edit: "+field.getValidationExpression());
				IModel validationModel = new PropertyModel(field, "validationExpression");

				logger.info("Validation expression  model object before edit: "+validationModel.getObject());
				add(new ValidationPanel("validPanel",validationModel, false));

				// TODO: check fragment
			} else if (field.getName().isFile()) {
				add(new EmptyPanel("validPanel"));

			} else {
				add(new EmptyPanel("validPanel"));

			}
			addOrganizations(field.getName().getType(), new CompoundPropertyModel(this));

			// options =========================================================
			WebMarkupContainer hide = new WebMarkupContainer("hide");
			// TODO:
			if (field.getName().isDropDownType()) { // drop down type
				// TODO: drop down for selecting the type of wicket component
				// that will show the options
				optionsComponentChoice = new DropDownChoice(
						"optionsComponentChoice", new PropertyModel(this,
								"field.fieldType"), optionsComponentList);
				optionsComponentChoice.setRequired(true);
				optionsComponentChoice.setOutputMarkupId(true);
				optionsComponentChoice.add(new ErrorHighlighter());
				optionsComponentChoice.setLabel(new ResourceModel(
						"space_field_form.SelectOptionsComponent"));
				hide.add(new SimpleFormComponentLabel(
						"optionsComponentChoiceLabel", optionsComponentChoice));
				hide.add(optionsComponentChoice);

				final Map<String, String> optionsMap;
				if (field.getOptions() == null) {
					optionsMap = new HashMap<String, String>();
				} else {
					optionsMap = field.getOptions();
				}
				final List<String> options = new ArrayList(optionsMap.keySet());

				hide.add(new Label("optionTextAreaLabel", new ResourceModel("space_field_form.addOption")));
				hide.add(new CustomAttributeOptionsPanel("optionTranslationsPanel", field.getCustomAttribute(), space.getSupportedLanguages(), textAreaOptions));
				

			} else {
				hide.setVisible(false);
			}
			add(hide);
			// done ============================================================
			add(new Button("done") {
				@Override
				public void onSubmit() {

					logger.info("field line count: "+field.getLineCount());
					logger.info("field default value expression: "+field.getDefaultValueExpression());
					//logger.debug("submitting, space translations: "+space.getTranslations());
					// update name based on translations
					field.setLabel(space.getPropertyTranslations(field.getName().getText()).get(getCalipso().getDefaultLocale()));
					
					// TODO:
					if (optionsComponentChoice != null
							&& optionsComponentChoice.getModelObject() != null) {
						field.setFieldType((String) optionsComponentChoice
								.getModelObject());
					}
					if (organizationChoice != null
							&& organizationChoice.getModelObject() != null) {
						// get Id of organizations which user has selected
						String orgId = String
								.valueOf(((Organization) organizationChoice
										.getModelObject()).getId());
						String orgName = String
								.valueOf(((Organization) organizationChoice
										.getModelObject()).getName());
						field.addOption("organizationId", orgId);
						field.addOption("organizationName", orgName);
					}
					
					CustomAttributeUtils.parseOptionsIntoAttribute(textAreaOptions, field.getCustomAttribute(), space.getSupportedLanguages());
					
					logger.info("edited, validationExpression: "+field.getValidationExpression());
					// may be clone, overwrite anyway
					space.getMetadata().add(field);
					
					activate(new IBreadCrumbPanelFactory() {
						public BreadCrumbPanel create(String componentId,
								IBreadCrumbModel breadCrumbModel) {
							BreadCrumbUtils
									.removePreviousBreadCrumbPanel(breadCrumbModel);
							return new SpaceFieldListPanel(componentId,
									breadCrumbModel, space, field.getName()
											.getText());
						}
					});
				}
			});
		}
		
		
		/*
		@Override
		protected void validate() {
			filter.reset();
			super.validate();
		}
		*/
		private void addOrganizations(int fieldType,
				CompoundPropertyModel formModel) {
			if (fieldType == 20) { // is type organization
				Fragment organizationFragment = new Fragment("extraFields",
						"dropDownFragment", this);
				// TODO: list of all organizations
				final List<Organization> allOrganizationsList = getCalipso()
						.findAllOrganizations();
				// TODO: dropdown of all organization
				// Map: organization Name - organization Id
				Organization selectedOrganization = new Organization();
				organizationChoice = new DropDownChoice("dropDownChoice",
						new Model(selectedOrganization), allOrganizationsList,
						new IChoiceRenderer() {
							// choice renderer
							public Object getDisplayValue(Object object) {
								// show by organization name
								return ((Organization) object).getName();

							}

							public String getIdValue(Object object, int index) {
								// value by organization id
								return String.valueOf(index);
							}
						});
				organizationChoice.setNullValid(true);
				organizationChoice.setRequired(true);
				//formModel.bind(organizationChoice, "field");
				// TODO: model fix error

				organizationFragment.add(organizationChoice);
				// label
				organizationChoice.setLabel(new ResourceModel(
						"organization.select"));
				organizationFragment.add(new SimpleFormComponentLabel(
						"dropDownLabel", organizationChoice));
				add(organizationFragment);

				// users in item-spase from this organization

			} 
			else {
				Fragment emptyFragment = new Fragment("extraFields",
						"emptyFragment", this);
				emptyFragment.add(new EmptyPanel("empty").setVisible(false));
				add(emptyFragment.setVisible(false));
				// TODO:
			}

		}

		/**
		 * @return the textAreaOptions
		 */
		public Map<String, String> getTextAreaOptions() {
			return textAreaOptions;
		}

		/**
		 * @param textAreaOptions
		 *            the textAreaOptions to set
		 */
		public void setTextAreaOptions(Map<String, String> textAreaOptions) {
			this.textAreaOptions = textAreaOptions;
		}

	}
}