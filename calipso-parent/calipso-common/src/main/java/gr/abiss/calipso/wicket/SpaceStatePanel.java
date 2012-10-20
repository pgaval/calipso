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

import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.State;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.wicket.components.formfields.HumanTimeDurationTextField;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.springframework.jca.cci.object.EisOperation;

/**
 * @author marcello
 */
public class SpaceStatePanel extends BasePanel {
	protected static final Logger logger = Logger.getLogger(SpaceStatePanel.class);

	private static final long serialVersionUID = 1L;
	private static final int IS_NEW_SPACE = -1;

	private Space space;
	private int stateKey;
	private String stateName;

	public SpaceStatePanel(String id, IBreadCrumbModel breadCrumbModel, Space space, int stateKey) {
		super(id, breadCrumbModel);
		this.space = space;
		this.stateKey = stateKey;
		this.stateName = space.getMetadata().getStatesMap().get(stateKey);
		add(new Label("title", getTitle()));
		getBackLinkPanel().makeCancel();
		deleteLink();

		add(new SpaceStateForm("form", stateKey));
	}

	public String getTitle() {
		if (stateKey == State.OPEN || stateKey == IS_NEW_SPACE) {// if new state
			return localize("space_state_form.titleNew");
		} else {
			return localize("space_state_form.titleEdit", stateName);
		}
	}

	private void deleteLink() {
		// if new state, or state name is new or closed
		if (stateKey == State.OPEN || stateKey == IS_NEW_SPACE 
								   || stateKey == State.NEW || stateKey == State.CLOSED) {
			add(new WebMarkupContainer("delete").setVisible(false));
		} else {// if edit
			add(new Link("delete") {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					int affectedCount = 0;
					if (space.getId() != 0)
						affectedCount = getCalipso().loadCountOfRecordsHavingStatus(space, stateKey);

					if (affectedCount > 0) {
						final String heading = localize("space_state_delete.confirm")
								+ " : " + stateName;
						final String warning = localize("space_state_delete.line3");
						final String line1 = localize("space_state_delete.line1");
						final String line2 = localize(
								"space_state_delete.line2", affectedCount + "");

						activate(new IBreadCrumbPanelFactory() {
							
							private static final long serialVersionUID = 1L;

							public BreadCrumbPanel create(String componentId,
									IBreadCrumbModel breadCrumbModel) {
								ConfirmPanel confirm = new ConfirmPanel(componentId, breadCrumbModel, heading,
										warning, new String[] { line1, line2 }) {
									
											private static final long serialVersionUID = 1L;

									public void onConfirm() {

										getCalipso().bulkUpdateStatusToOpen(space, stateKey);
										space.getMetadata().removeState(stateKey);
										getCalipso().storeSpace(space);
										// synchronize metadata else when we
										// save again we get Stale Object
										// Exception
										//space.setMetadata(getCalipso().loadMetadata(space.getMetadata().getId()));
										activate(new IBreadCrumbPanelFactory() {
											
											private static final long serialVersionUID = 1L;

											public BreadCrumbPanel create(
													String componentId,
													IBreadCrumbModel breadCrumbModel) {
												BreadCrumbUtils.moveToPanelForRelod(breadCrumbModel,SpacePermissionsPanel.class);
												return new SpacePermissionsPanel(componentId,breadCrumbModel, space);
											}
										});
									};
								};
								return confirm;
							}
						});

					} else {
						space.getMetadata().removeState(stateKey);
						activate(new IBreadCrumbPanelFactory() {
							private static final long serialVersionUID = 1L;
							public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
								BreadCrumbUtils.moveToPanelForRelod(breadCrumbModel, SpacePermissionsPanel.class);
								return new SpacePermissionsPanel(componentId, breadCrumbModel, space);
							}
						});
					}
				}// on-click
			});// add, new Link
		}
	}

	/**
	 * wicket form
	 */
	private class SpaceStateForm extends Form {

		private static final long serialVersionUID = 1L;

		private int stateKey;
		private DropDownChoice  makeAssetTypeChoice;
		private DropDownChoice existingAssetTypeChoice;
		private TextArea stateTemplateTextArea;
		
		private CheckBox allowMultipleCheckbox;
		public SpaceStateForm(String id, final int stateKey) {

			super(id);
			add(new FeedbackPanel("feedback"));
			this.stateKey = stateKey;
			
			SpaceStateModel modelObject = new SpaceStateModel();
			// stateKey is CREATE_NEW_STATE if add new state
			final String stateName = space.getMetadata().getStatesMap().get(stateKey);
			modelObject.setStateName(stateName);

			//existing Asset Type Multiple
			if(logger.isDebugEnabled()){
				logger.debug("multi existing asset type value for state is  : " + space.getMetadata().getExistingAssetTypeMultipleMap().get(stateKey));
			}
			modelObject.setExistingAssetTypeMultiple(space.getMetadata().getExistingAssetTypeMultipleMap().get(stateKey));
			
			
			final Long assetTypeId = space.getMetadata().getStatesAssetTypeIdMap().get(stateKey);
			if(assetTypeId != null){
				AssetType asType = getCalipso().loadAssetType(assetTypeId.longValue());
				modelObject.setMakeAsset(asType);
			}
			
			final Long existingAssetTypeId = space.getMetadata().getStatesExistingSpaceAssetTypeIdsMap().get(stateKey);
			if(existingAssetTypeId != null){
				AssetType exAssetType = getCalipso().loadAssetType(existingAssetTypeId.longValue());
				modelObject.setExistingAssetType(exAssetType);
			}
			
			modelObject.setStatePlugin(space.getMetadata().getStatesPluginMap().get(stateKey));
			
			modelObject.setStateDuration(space.getMetadata().getStatesDurationMap().get(stateKey));
			 
			// mdoelObject.setStateAssetTypeName = 
			final CompoundPropertyModel model = new CompoundPropertyModel(modelObject);
			setModel(model);

			// option
			// ===========================================================
			final TextField field = new TextField("stateName");
			field.setRequired(true);
			field.add(new ErrorHighlighter());
		
			field.add(new AbstractValidator() {
			
				private static final long serialVersionUID = 1L;
				protected void onValidate(IValidatable v) {
					String s = (String) v.getValue();
					if (space.getMetadata().getStatesMap().containsValue(s) && !s.equals(stateName)) {
						error(v);
					}
				}
				@Override
				protected String resourceKey() {
					return "space_state_form.error.state.exists";
				}
			});
			add(field);

			// form label
			field.setLabel(new ResourceModel("space_state_form.stateName"));
			boolean fieldEnabled = (stateKey != State.NEW);
			field.setEnabled(fieldEnabled);
			add(new SimpleFormComponentLabel("stateNameLabel", field));

			TextArea statePlugin = new TextArea("statePlugin");
			add(statePlugin);

			// form label
			statePlugin.setLabel(new ResourceModel("space_state_form.statePlugin"));
			add(new SimpleFormComponentLabel("statePluginLabel", statePlugin));
			
			
			// space duration
			HumanTimeDurationTextField stateDuration = new HumanTimeDurationTextField("stateDuration");
			stateDuration.setEnabled(stateKey != State.NEW);
			add(stateDuration);
			// form label
			stateDuration.setLabel(new ResourceModel("space_state_form.stateDuration"));
			add(new SimpleFormComponentLabel("stateDurationLabel", stateDuration));

			
			// Select an asset type from list of all asset types
			final List<AssetType> allAssetTypesList = getCalipso().findAllAssetTypes();
			
			makeAssetTypeChoice = new DropDownChoice("makeAsset", allAssetTypesList, new IChoiceRenderer(){
			
				private static final long serialVersionUID = 1L;
			
				public Object getDisplayValue(Object object) {
					AssetType asType = (AssetType)object;
					return asType.getName();
				}
				public String getIdValue(Object object, int index) {
					return index + "";
				}
				
			});
			makeAssetTypeChoice.setNullValid(true);
			makeAssetTypeChoice.setLabel(new ResourceModel("space_state_form.makeAsset"));
			add(makeAssetTypeChoice);
			add(new SimpleFormComponentLabel("makeAssetLabel", makeAssetTypeChoice));
			
			
			
			// select existing asset type to edit space item fields later
			existingAssetTypeChoice = new DropDownChoice("existingAssetType", allAssetTypesList, new IChoiceRenderer(){
			
				private static final long serialVersionUID = 1L;
			
				public Object getDisplayValue(Object object) {
					AssetType asType = (AssetType)object;
					return asType.getName();
				}
				public String getIdValue(Object object, int index) {
					return index + "";
				}
				
			});
			
			existingAssetTypeChoice.setLabel(new ResourceModel("space_state_form.existingAssetType"));
			
			boolean isEnabled = (stateKey == State.NEW);
			
			
			existingAssetTypeChoice.setEnabled(isEnabled);
			existingAssetTypeChoice.setNullValid(true);
			add(existingAssetTypeChoice);
			add(new SimpleFormComponentLabel("existingAssetTypeLabel", existingAssetTypeChoice));
			// TODO: only needed for field component
			field.setOutputMarkupId(true);
			
			// allow selecting multiple assets of the above type?
			allowMultipleCheckbox = new CheckBox("existingAssetTypeMultiple");
			allowMultipleCheckbox.setEnabled(isEnabled);
			allowMultipleCheckbox.setLabel(new ResourceModel("space_state_form.existingAssetTypeMultiple"));
			add(allowMultipleCheckbox);
			add(new SimpleFormComponentLabel("existingAssetTypeMultipleLabel", allowMultipleCheckbox));
			/*
			statePlugin.setOutputMarkupId(true);
			stateDuration.setOutputMarkupId(true);
			makeAssetTypeChoice.setOutputMarkupId(true);
			existingAssetTypeChoice.setOutputMarkupId(true);
			*/

			WebMarkupContainer moveToSpace = new WebMarkupContainer(
					"movetospace");
			add(moveToSpace);
			moveToSpace.setOutputMarkupId(true);
			final CheckBox addMoveToSpace = new CheckBox("addMoveToSpace",
					new Model(false));
			addMoveToSpace.setOutputMarkupId(true);

			addMoveToSpace
					.add(new AjaxFormComponentUpdatingBehavior("onclick") {
						
						private static final long serialVersionUID = 1L;

						@Override
						protected void onUpdate(AjaxRequestTarget target) {

							if (addMoveToSpace.getValue().equals("true")) {
								// SpaceStateForm.this.stateKey =
								// State.MOVE_TO_OTHER_SPACE
								field.setModelValue(new String[] { localize("space_state_form.moveToSpaceDescription") });
							}// if
							else {
								SpaceStateForm.this.stateKey = IS_NEW_SPACE;
								field.setModelValue(new String[] { null });
							}// else

							target.addComponent(field);
						}

					});

			moveToSpace.add(addMoveToSpace);

			Label moveToSpacedescription = new Label("movetospacedescription",
					localize("space_state_form.moveToSpaceDescription"));
			moveToSpace.add(moveToSpacedescription);

			// moveToSpace.setVisible(stateKey == -1 &&
			// space.getMetadata().getStatesMap().get(State.MOVE_TO_OTHER_SPACE)==null);
			moveToSpace.setVisible(false);
			
			stateTemplateTextArea = new TextArea("stateTemplateTextArea", new Model());
			add(stateTemplateTextArea);

			// form label
			stateTemplateTextArea.setLabel(new ResourceModel("space_state_form.stateTemplate"));
			add(new SimpleFormComponentLabel("stateTemplateLabel", stateDuration));
			
		}

		@Override
		protected void onSubmit() {
			
			SpaceStateModel model = (SpaceStateModel) getModelObject();
			// add state to metadata
			// get id of asset type
			
			AssetType tmpAssetType = (AssetType)makeAssetTypeChoice.getModelObject();
			Long asTypeId = null;
			if(makeAssetTypeChoice.getModelObject() != null){
				asTypeId = tmpAssetType.getId();
			}
			
			// TODO: not sure why we 'do' both metadata AND state. Setting the state in the meta 
			// should be enough - there's too much duplicate code here.
			AssetType tmpExistingAssetType = (AssetType)existingAssetTypeChoice.getModelObject();
			Long existingAssetTypeId = null;
			//Boolean existingAssetTypeMultiple = Boolean.FALSE;
			if(existingAssetTypeChoice.getModelObject() != null){
				existingAssetTypeId = tmpExistingAssetType.getId();
				//existingAssetTypeMultiple = model.getExistingAssetTypeMultiple();
			}
			if (stateKey == IS_NEW_SPACE) {
				space.getMetadata().addState(
						model.getStateName(),model.getStatePlugin(),
						model.getStateDuration(), asTypeId, existingAssetTypeId);
			}
			else {
				space.getMetadata().getStatesMap().put(stateKey, model.getStateName());
			}
			// does not depend on space status if new or already created
			if(stateKey == State.NEW){
				space.getMetadata().getStatesExistingSpaceAssetTypeIdsMap().put(stateKey, existingAssetTypeId);
				
				space.getMetadata().getExistingAssetTypeMultipleMap().put(stateKey, (Boolean)allowMultipleCheckbox.getModelObject());
			}
			// END TODO
			
			// save state plugin to metadata
			space.getMetadata().getStatesPluginMap().put(stateKey, model.getStatePlugin());
			// save state duration to metadata
			space.getMetadata().getStatesDurationMap().put(stateKey, model.getStateDuration());

			space.getMetadata().getStatesAssetTypeIdMap().put(stateKey, asTypeId);
			
			activate(new IBreadCrumbPanelFactory() {
				
				private static final long serialVersionUID = 1L;

				public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel) {
					BreadCrumbUtils.removePreviousBreadCrumbPanel(breadCrumbModel);
					return new SpacePermissionsPanel(componentId, breadCrumbModel, space);
				}
			});
		}

	}

	/**
	 * custom form backing object that wraps state key required for the create /
	 * edit use case
	 */
	private class SpaceStateModel implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private String stateName;
		private String statePlugin;
		private Long stateDuration;
		private AssetType makeAsset;
		private AssetType existingAssetType;
		private Boolean existingAssetTypeMultiple = Boolean.FALSE;

		/**
		 * @return the stateDuration
		 */
		public Long getStateDuration() {
			return stateDuration;
		}

		/**
		 * @param stateDuration the stateDuration to set
		 */
		public void setStateDuration(Long stateDuration) {
			this.stateDuration = stateDuration;
		}

		/**
		 * @return the statePlugin
		 */
		public String getStatePlugin() {
			return statePlugin;
		}

		/**
		 * @param statePlugin
		 *            the statePlugin to set
		 */
		public void setStatePlugin(String statePlugin) {
			this.statePlugin = statePlugin;
		}

		public String getStateName() {
			return stateName;
		}

		public void setStateName(String stateName) {
			this.stateName = stateName;
		}

		/**
		 * @return the assetType
		 */
		public AssetType getMakeAsset() {
			return makeAsset;
		}

		/**
		 * @param assetType the assetType to set
		 */
		public void setMakeAsset(AssetType makeAsset) {
			this.makeAsset = makeAsset;
		}

		/**
		 * @return the existingAssetType
		 */
		public AssetType getExistingAssetType() {
			return existingAssetType;
		}

		/**
		 * @param existingAssetType the existingAssetType to set
		 */
		public void setExistingAssetType(AssetType existingAssetType) {
			this.existingAssetType = existingAssetType;
		}

		/**
		 * @return the existingAssetTypeMultiple
		 */
		public Boolean getExistingAssetTypeMultiple() {
			return existingAssetTypeMultiple;
		}

		/**
		 * @param existingAssetTypeMultiple the existingAssetTypeMultiple to set
		 */
		public void setExistingAssetTypeMultiple(Boolean existingAssetTypeMultiple) {
			this.existingAssetTypeMultiple = existingAssetTypeMultiple;
		}
	}

}
