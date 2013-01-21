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

import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.FieldGroup;
import gr.abiss.calipso.domain.ItemRenderingTemplate;
import gr.abiss.calipso.domain.Role;
import gr.abiss.calipso.domain.RoleSpaceStdField;
import gr.abiss.calipso.domain.RoleType;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.SpaceRole;
import gr.abiss.calipso.domain.State;
import gr.abiss.calipso.domain.StdField;
import gr.abiss.calipso.domain.StdFieldMask;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.util.SpaceUtils;
import gr.abiss.calipso.wicket.components.renderers.WorkflowRenderer;
import gr.abiss.calipso.wicket.components.viewLinks.EditLinkPanel;
import gr.abiss.calipso.wicket.form.AbstractSpaceform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.UrlUtils;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.CheckBoxColumn;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.common.AbstractGrid;
import com.inmethod.grid.datagrid.DataGrid;
import com.inmethod.grid.datagrid.DefaultDataGrid;

public class SpacePermissionsPanel extends BasePanel {

	private static final Logger logger = Logger
			.getLogger(SpacePermissionsPanel.class);

	private static final long serialVersionUID = 1L;

	private Space space;
	// private boolean isNewSpace;
	private final List<RoleSpaceStdField> roleSpaceFields;
	private Map<String, SpaceRole> spaceRoleMap = new HashMap<String, SpaceRole>();

	private Map<String, Role> rolesMap;

	/**
	 * Called when creating or editing a new Space.
	 * 
	 * @param id
	 * @param breadCrumbModel
	 * @param space
	 */
	public SpacePermissionsPanel(String id, IBreadCrumbModel breadCrumbModel,
			Space space) {
		super(id, breadCrumbModel);
		// List<Field> fieldList = space.getMetadata().getFieldList();

		this.space = space;
		// this.isNewSpace = isNewSpace();

		// load any roles already persisted for the space
		// add or remove guest/anonymous roles
		// according to previous screens,
		// also add a Regular User role if needed
		SpaceUtils.initSpaceSpaceRoles(getCalipso(), space);

		rolesMap = space.getMetadata().getRolesMap();
		if (CollectionUtils.isNotEmpty(space.getRoleSpaceStdFields())) {
			roleSpaceFields = new ArrayList<RoleSpaceStdField>(
					space.getRoleSpaceStdFields());
		} else {
			roleSpaceFields = getCalipso().findSpaceFieldsBySpace(space);
		}
		initSpaceRolesMap();
		setupVisuals();

		add(new SpacePermissionsForm("form", this.space));
	}

	// ---------------------------------------------------------------------------------------------

	public SpacePermissionsPanel(String id, IBreadCrumbModel breadCrumbModel,
			Space space, List<RoleSpaceStdField> roleSpaceFields) {
		super(id, breadCrumbModel);
		List<Field> fieldList = space.getMetadata().getFieldList();

		this.space = space;
		// this.isNewSpace = isNewSpace();

		// check any guest/anonymous permissions that were
		// added/removed after initial initRoles call
		SpaceUtils.initSpaceSpaceRoles(getCalipso(), space);

		rolesMap = space.getMetadata().getRolesMap();
		this.roleSpaceFields = roleSpaceFields;
		initSpaceRolesMap();

		setupVisuals();

		add(new SpacePermissionsForm("form", this.space));
	}

	// ---------------------------------------------------------------------------------------------

//	public SpacePermissionsPanel(String id, IBreadCrumbModel breadCrumbModel,
//			List<SpaceRole> spaceRolesList, Space space) {
//		super(id, breadCrumbModel);
//		this.space = space;
//		logger.info("Constructor 4, space roles: " + space.getSpaceRoles());
//		// this.isNewSpace = this.space.getId() == 0;
//
//		// check any guest/anonymous permissions that were
//		// added/removed after initial initRoles call
//		initSpaceSpaceRoles();
//
//		rolesMap = space.getMetadata().getRolesMap();
//
//		if (space.getRoleSpaceStdFields() != null) {
//			roleSpaceFields = new ArrayList<RoleSpaceStdField>(
//					space.getRoleSpaceStdFields());
//		} else {
//			roleSpaceFields = getCalipso().findSpaceFieldsBySpace(space);
//		}
//
//		// this.spaceRolesList = spaceRolesList;
//
//		initSpaceRolesMap();
//
//		setupVisuals();
//
//		add(new SpacePermissionsForm("form", this.space));
//	}

	// ---------------------------------------------------------------------------------------------
	/**
	 * Called when adding a new SpaceRole or after editing an existing one.
	 */
	public SpacePermissionsPanel(String id, IBreadCrumbModel breadCrumbModel,
			SpaceRole spaceRole) {
		super(id, breadCrumbModel);
		logger.info("Constructor 5");

		this.space = spaceRole.getSpace();
		// this.isNewSpace = isNewSpace();

		rolesMap = this.space.getMetadata().getRolesMap();

		if (CollectionUtils.isNotEmpty(space.getRoleSpaceStdFields())) {
			roleSpaceFields = new ArrayList<RoleSpaceStdField>(
					this.space.getRoleSpaceStdFields());
		} else {
			roleSpaceFields = getCalipso().findSpaceFieldsBySpace(this.space);
		}
		initSpaceRolesMap();

		if (this.space.getSpaceRoles() != null
				&& this.space.getSpaceRoles().contains(spaceRole)) {
			// do nothing, name is updated by reference
			// this.space.getSpaceRoles().add(spaceRole);
		} else {
			this.space.getMetadata().addRole(spaceRole.getRoleCode());
			this.space.add(spaceRole);
		}

		setupVisuals();

		add(new SpacePermissionsForm("form", this.space));
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	

	// ---------------------------------------------------------------------------------------------

	private void initSpaceRolesMap() {
		// reset map
		// TODO: remove this map as well
		// logger.debug("Clearing spaceRoleMap, size:"+spaceRoleMap.size());
		spaceRoleMap.clear();
		for (SpaceRole spaceRole : this.space.getSpaceRoles()) {
			// if(!spaceRole.getRoleType().equals(RoleType.SPACE_ADMINISTRATOR)){
			spaceRoleMap.put(spaceRole.getRoleCode(), spaceRole);
			// }
		}
		// logger.debug("Initialized spaceRoleMap, size:"+spaceRoleMap.size()+", spaceRoleMap: "+spaceRoleMap);
	}


	// ---------------------------------------------------------------------------------------------

	@Override
	public String getTitle() {
		return localize("space_roles.title");
	}

	private void setupVisuals() {
		// label / heading =================================================
		add(new Label("label", localize(space) + " (" + space.getPrefixCode()
				+ ")"));

		// cancel ==========================================================
		getBackLinkPanel().makeCancel(
				new BreadCrumbLink("link", getBreadCrumbModel()) {
					@Override
					protected IBreadCrumbParticipant getParticipant(String id) {
						return BreadCrumbUtils.moveToPanel(
								getBreadCrumbModel(),
								SpaceListPanel.class.getName());
					}
				});
	}

	/**
	 * wicket form
	 */
	private class SpacePermissionsForm extends AbstractSpaceform {

		private final class EditLinkColumn extends AbstractColumn {
			private final ModalWindow editTemplateModal;
			private final Space space;
			private final WebMarkupContainer templatesGridContainer;

			private EditLinkColumn(String columnId, IModel headerModel,
					ModalWindow editTemplateModal, Space space,
					WebMarkupContainer templatesGridContainer) {
				super(columnId, headerModel);
				setResizable(false);
				setInitialSize(30);
				this.editTemplateModal = editTemplateModal;
				this.space = space;
				this.templatesGridContainer = templatesGridContainer;
			}

			@Override
			public Component newCell(WebMarkupContainer parent,
					String componentId, IModel rowModel) {

				EditLinkPanel editContainer = new EditLinkPanel("edit");
				AjaxLink newTemplateLink = getEditTemplateModalLink(
						(ItemRenderingTemplate) rowModel.getObject(), space,
						templatesGridContainer, this.getGrid(),
						editTemplateModal);
				editContainer.add(newTemplateLink);
				return editContainer;
			}
		}

		private CalipsoFeedbackMessageFilter filter;

		@SuppressWarnings({ "unchecked", "serial" })
		public SpacePermissionsForm(String id, final Space space) {

			super(id, space);
			RequestCycle rc = RequestCycle.get();
			final List<Role> roles = new ArrayList<Role>(space.getMetadata()
					.getRoleList());
			final AttributeModifier editImgSrcModifier = new AttributeModifier(
					"src", UrlUtils.rewriteToContextRelative(
							"../resources/edit.gif", rc));
			final AttributeModifier addImgSrcModifier = new AttributeModifier(
					"src", UrlUtils.rewriteToContextRelative(
							"../resources/add.gif", rc));
			final AttributeModifier rowspan = new AttributeModifier("rowspan",
					roles.size() + "");
			final AttributeModifier yes = new AttributeModifier("src",
					UrlUtils.rewriteToContextRelative(
							"../resources/status-green.gif", rc));
			final AttributeModifier no = new AttributeModifier("src",
					UrlUtils.rewriteToContextRelative(
							"../resources/status-grey.gif", rc));
			// Mask-states
			final AttributeModifier readonly = new AttributeModifier("src",
					UrlUtils.rewriteToContextRelative(
							"../resources/field-readonly.gif", rc));
			final AttributeModifier mandatory = new AttributeModifier("src",
					UrlUtils.rewriteToContextRelative(
							"../resources/field-mandatory.gif", rc));
			final AttributeModifier mandatoryIfEmpty = new AttributeModifier(
					"src", UrlUtils.rewriteToContextRelative(
							"../resources/field-mandatory-if.gif", rc));
			final AttributeModifier optional = new AttributeModifier("src",
					UrlUtils.rewriteToContextRelative(
							"../resources/field-optional.gif", rc));
			final AttributeModifier hidden = new AttributeModifier("src",
					UrlUtils.rewriteToContextRelative(
							"../resources/field-hidden.gif", rc));
			final AttributeModifier altClass = new AttributeModifier("class",
					"alt");
			// view selection form
			// "fields-group-"+field.getGroupId()
			add(new ListView("field-group-selection", space.getMetadata()
					.getFieldGroups()) {
				protected void populateItem(ListItem listItem) {
					FieldGroup fieldGroup = (FieldGroup) listItem
							.getModelObject();

					listItem.add(new Label("field-group-selection-label",
							fieldGroup.getName()).setRenderBodyOnly(true));
					listItem.add(new AttributeModifier("value",
							"show-fields-group-" + fieldGroup.getId()));
				}
			});

			// states colspan
			// ---------------------------------------------------------------------
			final Map<Integer, String> statesMap = space.getMetadata()
					.getStatesMap();
			AttributeModifier statesColspan = new AttributeModifier("colspan",
					(statesMap.size() - 1) + "");
			add(new WebMarkupContainer("statesColspan").add(statesColspan));
			// fields colspan
			// ---------------------------------------------------------------------
			// list of custom field that user selected
			final List<Field> fields = space.getMetadata().getFieldList();
			AttributeModifier fieldsColspan = new AttributeModifier("colspan",
					fields.size() + "");
			add(new WebMarkupContainer("fieldsColspan").add(fieldsColspan));
			// add state
			// --------------------------------------------------------------------------
			add(new Button("addState") {
				@Override
				public void onSubmit() {
					activate(new IBreadCrumbPanelFactory() {
						public BreadCrumbPanel create(String componentId,
								IBreadCrumbModel breadCrumbModel) {
							return new SpaceStatePanel(componentId,
									breadCrumbModel, space, -1);
						}
					});
				}
			}.add(addImgSrcModifier));

			// add role
			// ---------------------------------------------------------------------------
			add(new Button("addRole") {
				@Override
				public void onSubmit() {
					activate(new IBreadCrumbPanelFactory() {
						public BreadCrumbPanel create(String componentId,
								IBreadCrumbModel breadCrumbModel) {
							return new SpaceRolePanel(componentId,
									breadCrumbModel, new SpaceRole(space, "",
											RoleType.REGULAR_USER));
						}
					});
				}
			}.add(addImgSrcModifier));
			// states col headings
			// ----------------------------------------------------------------
			final List<Integer> stateKeysNoNew = new ArrayList(
					statesMap.keySet());
			stateKeysNoNew.remove(State.NEW);
			add(new ListView("stateHeads", stateKeysNoNew) {
				protected void populateItem(ListItem listItem) {
					Integer stateKey = (Integer) listItem.getModelObject();
					listItem.add(new Label("state", statesMap.get(stateKey)));
				}
			});
			// fields col headings
			// ----------------------------------------------------------------
			add(new ListView<Field>("fieldHeads", fields) {
				protected void populateItem(ListItem<Field> listItem) {
					Field f = (Field) listItem.getModelObject();
					listItem.add(new Label("field", localize(space, f.getName()
							.getText())));
					listItem.add(new AttributeModifier("name", "fields-group-"
							+ f.getGroupId()));//
				}
			});

			// rows init
			// --------------------------------------------------------------------------

			// final Map<String, Role> rolesMap =
			// space.getMetadata().getRolesMap();

			// -------------------------------------------------------------------------------------
			List<Integer> stateKeys = new ArrayList(statesMap.keySet());

			add(new ListView("states", stateKeys) {
				protected void populateItem(ListItem listItem) {
					final boolean firstState = listItem.getIndex() == 0;
					final String stateClass = listItem.getIndex() % 2 == 1 ? "bdr-bottom alt"
							: "bdr-bottom";
					final Integer stateKeyRow = (Integer) listItem
							.getModelObject();
					listItem.add(new ListView("roles", space
							.getSpaceRolesList/* WithoutSpaceAdmin */()) {
						protected void populateItem(ListItem listItem) {
							final SpaceRole spaceRole = (SpaceRole) listItem
									.getModelObject();
							spaceRole.setItemRenderingTemplates(SpacePermissionsPanel.this.getCalipso().loadSpaceRoleTemplates(spaceRole.getId()));
							String roleClass = listItem.getIndex() % 2 == 1 ? " alt"
									: "";
							String lastRole = listItem.getIndex() == roles
									.size() - 1 ? " bdr-bottom" : "";
							listItem.add(new AttributeModifier("class",
									"center" + roleClass + lastRole));
							// System.out.println("\n****" +
							// spaceRole.getDescription());
							if (listItem.getIndex() == 0) {
								AttributeModifier rowClass = new AttributeModifier(
										"class", stateClass);
								listItem.add(new Label("state", statesMap
										.get(stateKeyRow)).add(rowspan).add(
										rowClass));
								WebMarkupContainer editState = new WebMarkupContainer(
										"editState");
								editState.add(rowspan).add(rowClass);
								Button editStateButton = new Button("editState") {
									@Override
									public void onSubmit() {
										IBreadCrumbPanelFactory factory = new IBreadCrumbPanelFactory() {
											public BreadCrumbPanel create(
													String componentId,
													IBreadCrumbModel breadCrumbModel) {
												SpaceStatePanel panel = new SpaceStatePanel(
														componentId,
														breadCrumbModel, space,
														stateKeyRow);
												return panel;
											}
										};
										activate(factory);
									}
								};
								editStateButton.add(editImgSrcModifier);
								editState.add(editStateButton);
								/*
								 * if (stateKeyRow == State.NEW) { // user can
								 * // customize // state names, // even for //
								 * Closed editStateButton.setVisible(false); }
								 */
								listItem.add(editState);
							} else {
								listItem.add(new WebMarkupContainer("state")
										.setVisible(false));
								listItem.add(new WebMarkupContainer("editState")
										.setVisible(false));
							}
							listItem.add(new Label("role", spaceRole
									.getDescription()));
							Button editRoleButton = new Button("editRole") {
								@Override
								public void onSubmit() {
									activate(new IBreadCrumbPanelFactory() {
										public BreadCrumbPanel create(
												String componentId,
												IBreadCrumbModel breadCrumbModel) {
											spaceRole.setSpace(space);
											return new SpaceRolePanel(
													componentId,
													breadCrumbModel, spaceRole);
										}
									});
								}
							};
							editRoleButton.add(editImgSrcModifier);
							listItem.add(editRoleButton);
							//logger.info("space tempoates refreshed to "+space.getItemRenderingTemplates());
							// -------------------------------------------------
							// add template selection for state/spacerole combo
							// -------------------------------------------------
							DropDownChoice<ItemRenderingTemplate> roleStateTemplateChoice = new DropDownChoice<ItemRenderingTemplate>(
									"roleStateTemplate",
									new PropertyModel<ItemRenderingTemplate>(
											spaceRole,
											"itemRenderingTemplates["
													+ stateKeyRow.shortValue()
													+ "]"),
									space.getItemRenderingTemplates(),
									new IChoiceRenderer<ItemRenderingTemplate>() {
										public Object getDisplayValue(
												ItemRenderingTemplate tmpl) {
											return tmpl.getDescription();
										}

										public String getIdValue(
												ItemRenderingTemplate tmpl,
												int i) {
											return i + "";
										}
									});
							roleStateTemplateChoice.setNullValid(true);
							roleStateTemplateChoice.setRequired(false);
							// CompoundPropertyModel model = new
							// CompoundPropertyModel(spaceRole);
							// model.bind(roleStateTemplateChoice, );
							listItem.add(roleStateTemplateChoice);

							// Do not allow name editing of SpaceAdmin or Guest
							// roles for clarity
							// TODO: allow name editing after adding some style
							// or image to signify their
							// true semantics
							if (!firstState
									|| !spaceRole.getRoleType().equals(
											RoleType.REGULAR_USER)) {
								editRoleButton.setVisible(false);
							}
							Role role = rolesMap.get(spaceRole.getRoleCode());
							final State state = role != null ? role.getStates()
									.get(stateKeyRow) : null;

							listItem.add(new ListView("stateHeads",
									stateKeysNoNew) {
								protected void populateItem(ListItem listItem) {
									final Integer stateKeyCol = (Integer) listItem
											.getModelObject();
									Button stateButton = new Button("state") {
										@Override
										public void onSubmit() {
											space.getMetadata()
													.toggleTransition(
															spaceRole
																	.getRoleCode(),
															stateKeyRow,
															stateKeyCol);
											activate(new IBreadCrumbPanelFactory() {
												public BreadCrumbPanel create(
														String componentId,
														IBreadCrumbModel breadCrumbModel) {
													BreadCrumbUtils
															.removeActiveBreadCrumbPanel(breadCrumbModel);
													return new SpacePermissionsPanel(
															componentId,
															breadCrumbModel,
															space,
															roleSpaceFields);
												}
											});

										}
									};

									stateButton.add(editImgSrcModifier);
									if (stateKeyRow == State.NEW
											&& stateKeyCol != State.OPEN) {
										stateButton.setVisible(false);
									}
									// logger.debug("spaceRole: "+spaceRole);
									// logger.debug("spaceRole.getRoleCode(): "+spaceRole.getRoleCode());
									// logger.debug("rolesMap.get(spaceRole.getRoleCode()): "+rolesMap.get(spaceRole.getRoleCode()));
									// logger.debug("rolesMap.get(spaceRole.getRoleCode()).getStates(): "+rolesMap.get(spaceRole.getRoleCode()).getStates());

									if (state != null
											&& state.getTransitions().contains(
													stateKeyCol)) {
										stateButton.add(yes);
									} else {
										stateButton.add(no);
									}
									listItem.add(stateButton);
								}
							});
							listItem.add(new ListView("fieldHeads", fields) {
								protected void populateItem(ListItem listItem) {
									final Field field = (Field) listItem
											.getModelObject();
									listItem.add(new AttributeModifier("name",
											"fields-group-"
													+ field.getGroupId()));
									if (roles.size() == 1
											&& listItem.getIndex() % 2 == 0) {
										listItem.add(altClass);
									}
									final DropDownChoice maskChoice = new DropDownChoice(
											"field",
											new Model() {
												@Override
												public Serializable getObject() {
													return state.getFields()
															.get(field
																	.getName());
												}

												@Override
												public void setObject(
														Serializable object) {
													state.getFields().put(
															field.getName(),
															(Integer) object);
												}
											}, State.MASK_KEYS,
											new IChoiceRenderer() {
												public Object getDisplayValue(
														Object object) {
													return localize("State."
															+ object.toString()
															+ ".name");

												}

												public String getIdValue(
														Object id, int index) {
													return id.toString();
												}
											});
									// maskChoice.setType(Integer.class);
									maskChoice
											.add(new AjaxFormComponentUpdatingBehavior(
													"onchange") {
												protected void onUpdate(
														AjaxRequestTarget target) {
													// logger.info("model object: "+maskChoice.getModelObject());
													target.addComponent(SpacePermissionsForm.this);
													// logger.info("model object after addComponent: "+maskChoice.getModelObject());
												}
											});
									listItem.add(maskChoice);
								}
							});
						}
					});
				}
			});

			final WebMarkupContainer templatesGridContainer = new WebMarkupContainer(
					"templatesGridContainer");
			templatesGridContainer.setOutputMarkupId(true);
			space.setItemRenderingTemplates(SpacePermissionsPanel.this.getCalipso().getItemRenderingTemplates(space));
			
			final ListDataProvider listDataProvider = new ListDataProvider(
					space.getItemRenderingTemplates());

			final ModalWindow editTemplateModal = new ModalWindow(
					"templateModal");
			List<IGridColumn> cols = (List) Arrays
					.asList(new PropertyColumn(new Model("Name"), "description"),
							new PropertyColumn(new Model("Priority"),
									"priority"), new PropertyColumn(new Model(
									"Hide overview"), "hideOverview"),
							new PropertyColumn(new Model("Hide history"),
									"hideHistory"), new EditLinkColumn("edit",
									null, editTemplateModal, space,
									templatesGridContainer));

			final DataGrid grid = new DefaultDataGrid("templatesGrid",
					new DataProviderAdapter(listDataProvider), cols);
			grid.setSelectToEdit(false);
			grid.setClickRowToSelect(true);
			grid.setAllowSelectMultiple(false);
			templatesGridContainer.add(grid);
			add(templatesGridContainer);
			// add "new" link
			templatesGridContainer.add(editTemplateModal);
			AjaxLink newTemplateLink = getEditTemplateModalLink(space,
					templatesGridContainer, grid, editTemplateModal);
			add(newTemplateLink);
			// Standard (common for all spaces) fields and roles
			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			final List<StdField> stdFieldsList = getCalipso()
					.loadAllStdFields();

			// Setup head (list of fields)
			add(new ListView("stdFieldHeads", stdFieldsList) {
				@Override
				protected void populateItem(ListItem listItem) {
					StdField stdField = (StdField) listItem.getModelObject();
					Label stdFieldLabel = new Label("stdField",
							localize("field." + stdField.getField().getName()));
					listItem.add(stdFieldLabel);
				}
			});

			// add(new
			// WebMarkupContainer("rolesPermissions").setVisible(false));

			// List of Roles
			add(new ListView("rolesPermissions",
					space.getSpaceRolesList/* WithoutSpaceAdmin */()) {
				@Override
				protected void populateItem(ListItem rolePermissionListItem) {
					// final Role role =
					// (Role)rolePermissionListItem.getModelObject();
					final SpaceRole spaceRole = (SpaceRole) rolePermissionListItem
							.getModelObject();
					rolePermissionListItem.add(new Label("role", spaceRole
							.getDescription()));

					// Permissions on Role Field
					// Permission(Role, Field)
					rolePermissionListItem.add(new ListView("permissions",
							stdFieldsList) {
						@Override
						protected void populateItem(ListItem listItem) {
							final StdField stdField = (StdField) listItem
									.getModelObject();

							final RoleSpaceStdField roleSpaceStdField;
							// RoleSpaceStdField tempRoleSpaceStdField = new
							// RoleSpaceStdField(null, spaceRole.getRoleCode(),
							// space, stdField, null);
							RoleSpaceStdField tempRoleSpaceStdField = new RoleSpaceStdField(
									null, spaceRole, stdField, null);
							StdFieldMask selectedMask = null;

							int indexOfRoleSpaceStdField = roleSpaceFields
									.lastIndexOf(tempRoleSpaceStdField);
							if (indexOfRoleSpaceStdField == -1) {// if don't
																	// exists
								tempRoleSpaceStdField
										.setFieldMaskId(StdFieldMask.Mask.HIDDEN
												.getId());
								roleSpaceFields.add(tempRoleSpaceStdField);
								roleSpaceStdField = tempRoleSpaceStdField;
								roleSpaceStdField
										.setFieldMaskId(StdFieldMask.Mask.HIDDEN
												.getId());
							} else {// the roleSpaceStdField exists in
									// roleSpaceFields list
								roleSpaceStdField = roleSpaceFields
										.get(indexOfRoleSpaceStdField);
								if (roleSpaceStdField.getFieldMask() != null) {
									selectedMask = roleSpaceStdField
											.getFieldMask();
								}
							}

							MaskPanel maskPanel = new MaskPanel("permission",
									getBreadCrumbModel(), stdField.getField()
											.getFieldType(), selectedMask) {
								@Override
								public void onMaskChanged(
										StdFieldMask selectedStdFieldMask) {
									roleSpaceStdField
											.setFieldMaskId(selectedStdFieldMask
													.getMask().getId());
								}
							};
							listItem.add(maskPanel);
						}
					});
				}
			});

			// back
			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			add(new Button("back") {
				@Override
				public void onSubmit() {
					// Save permissions state
					for (RoleSpaceStdField roleSpaceStdField : roleSpaceFields) {
						space.add(roleSpaceStdField);
					}

					activate(new IBreadCrumbPanelFactory() {
						public BreadCrumbPanel create(String componentId,
								IBreadCrumbModel breadCrumbModel) {
							BreadCrumbUtils
									.removePreviousBreadCrumbPanel(breadCrumbModel);
							return new SpaceFieldListPanel(componentId,
									breadCrumbModel, space, null);
						}
					});
				}
			});

			add(new Button("apply") {
				@Override
				public void onSubmit() {
					activate(new IBreadCrumbPanelFactory() {

						public BreadCrumbPanel create(String componentId,
								IBreadCrumbModel breadCrumbModel) {
							BreadCrumbUtils
									.removePreviousBreadCrumbPanel(breadCrumbModel);
							return new SpacePermissionsPanel(componentId,
									breadCrumbModel, persistChanges());
						}
					});
				}
			});

			add(new Button("finish") {
				@Override
				public void onSubmit() {
					boolean isNewSpace = !space.getPublished();
					persistChanges();
					if (isNewSpace) {
						isNewSpace = false; // after the user allocation, we
											// edit the space
						activate(new IBreadCrumbPanelFactory() {
							public BreadCrumbPanel create(String componentId,
									IBreadCrumbModel breadCrumbModel) {
								// TODO Reactivate following code after test
								// completion
								return new SpaceAllocatePanel(componentId,
										breadCrumbModel, space.getId());

								// TODO Delete following code after test
								// completion
								// SpaceListPanel spaceListPanel = new
								// SpaceListPanel(componentId, breadCrumbModel);
								// spaceListPanel.setSelectedSpaceId(space.getId());
								// BreadCrumbUtils.moveToPanelForRelod(breadCrumbModel,
								// SpaceListPanel.class);
								// return spaceListPanel;
							}
						});
					} else {
						activate(new IBreadCrumbPanelFactory() {
							public BreadCrumbPanel create(String componentId,
									IBreadCrumbModel breadCrumbModel) {
								SpaceListPanel spaceListPanel = new SpaceListPanel(
										componentId, breadCrumbModel);
								spaceListPanel
										.setSelectedSpaceId(space.getId());
								BreadCrumbUtils.moveToPanelForRelod(
										breadCrumbModel, SpaceListPanel.class);
								return spaceListPanel;
							}
						});
					}
				}
			});

			// TODO: fix issue when creating a new space that extends an
			// existing

			WorkflowRenderer workflow = new WorkflowRenderer(space
					.getMetadata().getRolesMap(), space.getMetadata()
					.getStatesMap(), spaceRoleMap);

			add(new Label("workflow", workflow.getAsHtml())
					.setEscapeModelStrings(false));
		}

		private AjaxLink getEditTemplateModalLink(final Space space,
				final WebMarkupContainer templatesGridContainer,
				final DataGrid grid, final ModalWindow editTemplateModal) {
			AjaxLink newTemplateLink = new AjaxLink("newTemplateLink",
					new ResourceModel("edit")) {
				public void onClick(AjaxRequestTarget target) {
					// TODO: add row to grid?
					final ItemRenderingTemplate tpl;
					if (CollectionUtils.isNotEmpty(grid.getSelectedItems())) {
						tpl = (ItemRenderingTemplate) ((IModel) grid
								.getSelectedItems().iterator().next())
								.getObject();
					} else {
						tpl = new ItemRenderingTemplate();
						tpl.setDescription("new");
						tpl.setSpace(space);
					}

					editTemplateModal
							.setContent(new EditItemRenderingTemplatePanel(
									"content", editTemplateModal, tpl) {
								@Override
								protected void persist(
										AjaxRequestTarget target, Form form) {
									if (CollectionUtils.isEmpty(space
											.getItemRenderingTemplates())
											|| !space
													.getItemRenderingTemplates()
													.contains(tpl)) {
										space.add(tpl);
										logger.info("added new template to space");
									}

									// update grid
									if (target != null) {
										target.addComponent(templatesGridContainer);
									}

								}
							});
					editTemplateModal.setTitle(this.getLocalizer().getString(
							"presentation.templates", this));
					editTemplateModal.show(target);
					// target.appendJavaScript("tinyMCE.execCommand('mceAddControl', false, 'templateText');");
				}
			};
			return newTemplateLink;
		}

		private AjaxLink getEditTemplateModalLink(
				final ItemRenderingTemplate tpl, final Space space,
				final WebMarkupContainer templatesGridContainer,
				final AbstractGrid grid, final ModalWindow editTemplateModal) {
			AjaxLink newTemplateLink = new AjaxLink("link", new ResourceModel(
					"edit")) {
				public void onClick(AjaxRequestTarget target) {

					editTemplateModal
							.setContent(new EditItemRenderingTemplatePanel(
									"content", editTemplateModal, tpl) {
								@Override
								protected void persist(
										AjaxRequestTarget target, Form form) {
									if (CollectionUtils.isEmpty(space
											.getItemRenderingTemplates())
											|| !space
													.getItemRenderingTemplates()
													.contains(tpl)) {
										space.add(tpl);
										logger.info("added new template to space");
									}

									// update grid
									if (target != null) {
										target.addComponent(templatesGridContainer);
									}

								}
							});
					editTemplateModal.setTitle(this.getLocalizer().getString(
							"presentation.templates", this));
					editTemplateModal.show(target);
					// target.appendJavaScript("tinyMCE.execCommand('mceAddControl', false, 'templateText');");
				}
			};
			return newTemplateLink;
		}

		private Space persistChanges() {
			Space space = this.getSpace();
			boolean isNewSpace = !space.getPublished();
			
			for (SpaceRole spaceRole : space.getSpaceRolesList()) {
				Set<RoleSpaceStdField> spaceRoleStdFieldsSet = new HashSet<RoleSpaceStdField>();
				spaceRoleStdFieldsSet.addAll(roleSpaceFields);
				spaceRole.setRoleSpaceStdFields(spaceRoleStdFieldsSet);
//				for (RoleSpaceStdField roleSpaceStdField : roleSpaceFields) {
//					spaceRole.add(roleSpaceStdField);
//				}
				space.add(spaceRole);
				spaceRole.setSpace(space);
			}

			// add creator as space group admin if group is new
//			if (isNewSpace || space.getSpaceGroup().getId() == null) {
////				if(space.getSpaceGroup().getId() != null){
////					space.getSpaceGroup().setAdmins(getCalipso().loadSpaceGroupAdmins(space.getSpaceGroup().getId()));
////				}
//				space.getSpaceGroup().addAdmin(getPrincipal());
//			}
			//space.setPublished(true);
			space = getCalipso().storeSpace(space);
			// add creator as space admin if space is new
//			try {
				if (isNewSpace) {

					Space persistedSpace = getCalipso().loadSpace(
							space.getPrefixCode());
					SpaceRole spaceAdminRole = null;
					for (SpaceRole spaceRole : persistedSpace
							.getSpaceRolesList()) {
						if (spaceRole.getRoleType().equals(
								RoleType.SPACE_ADMINISTRATOR)) {
							spaceAdminRole = spaceRole;
							break;
						}
					}
					if(spaceAdminRole != null){
						User u = getCalipso().loadUser(getPrincipal().getId());
						getCalipso().storeUserSpaceRole(u, spaceAdminRole);
						refreshPrincipal(u);
					}

					// current user may be allocated to this space, and e.g.
					// name could have changed
					refreshPrincipal();
				}

//			} catch (Exception e) {
//				logger.error(e);e.printStackTrace();
//			}

			return space;
		}
	}
}