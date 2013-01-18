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

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.Language;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.SpaceGroup;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.util.SpaceUtils;
import gr.abiss.calipso.wicket.components.formfields.HumanTimeDurationTextField;
import gr.abiss.calipso.wicket.components.validators.UniqueSpaceGroupNameForEachAdminValidator;
import gr.abiss.calipso.wicket.components.validators.ValidationUtils;
import gr.abiss.calipso.wicket.form.AbstractSpaceform;
import gr.abiss.calipso.wicket.space.panel.SpacePanelLanguageSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

/**
 * space edit form Space form when creating new space
 */
public class SpaceFormPanel extends BasePanel {
 
	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(SpaceFormPanel.class);
	private String spaceName = null;
	private boolean isEdit;

	public String getTitle() {
		if (isEdit)
			return localize("space_form.titleEdit", spaceName);
		else
			return localize("space_form.titleCreate");
	}

	public SpaceFormPanel(String id, final IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);

		isEdit = false;

		Space space = new Space();

		getBackLinkPanel().makeCancel();
		setHighlightOnPreviousPage(space.getId());
		deleteLink(null);// new Space, hides delete link

		add(new SpaceForm("form", space));
	}

	public SpaceFormPanel(String id, final IBreadCrumbModel breadCrumbModel,
			Space space) {
		super(id, breadCrumbModel);

		spaceName = space.getName();
		isEdit = spaceName != null;
		getBackLinkPanel().makeCancel();
		setHighlightOnPreviousPage(space.getId());
		deleteLink(space);

		add(new SpaceForm("form", space));
	}

	private void setHighlightOnPreviousPage(long selectedSpaceId) {
		// get previous page. We use the active one as the previous because
		// when this page is created it's not yet activated.
		BreadCrumbPanel previous = (BreadCrumbPanel) getBreadCrumbModel()
				.getActive();

		if (previous instanceof SpaceListPanel) {
			((SpaceListPanel) previous).setSelectedSpaceId(selectedSpaceId);
		}
	}

	private void deleteLink(final Space space) {
		 boolean canBeDeleted = 
			 Boolean.parseBoolean(((CalipsoApplication)Application.get()).
					 getCalipsoPropertyValue("allow.delete.item"))  && isEdit;
		if (!canBeDeleted) {
			add(new WebMarkupContainer("delete").setVisible(false));
		} else {
			add(new Link("delete") {
				@Override
				public void onClick() {
					final String heading = localize("space_delete.confirm");
					final String warning = localize("space_delete.line3");
					final String line1 = localize("space_delete.line1");
					final String line2 = localize("space_delete.line2");

					activate(new IBreadCrumbPanelFactory() {
						public BreadCrumbPanel create(String componentId,
								final IBreadCrumbModel breadCrumbModel) {
							ConfirmPanel confirm = new ConfirmPanel(
									componentId, breadCrumbModel, heading,
									warning, new String[] { line1, line2 }) {
								public void onConfirm() {
									// TODO: Should remove spaceGroup reference?
									getCalipso().removeSpace(space);
									
									// logged in user may have been allocated to
									// this space
									SpaceFormPanel.this.refreshPrincipal();

									BreadCrumbUtils
											.removePreviousBreadCrumbPanel(breadCrumbModel);

									activate(new IBreadCrumbPanelFactory() {
										public BreadCrumbPanel create(
												String componentId,
												IBreadCrumbModel breadCrumbModel) {
											return (BreadCrumbPanel) breadCrumbModel
													.getActive();
										}
									});
								}
							};

							return confirm;
						}
					});// activate
				}// onclick
			});// add, new Link
		}
	}

	/**
	 * wicket form
	 */
	private class SpaceForm extends AbstractSpaceform {

		private Space copyFrom;
		private  DropDownChoice spaceGroupChoice;
		WebMarkupContainer spaceGroupDetailsContainer;
		WebMarkupContainer spaceGroupDetails;

		private CalipsoFeedbackMessageFilter filter;
		public Space getCopyFrom() {
			return copyFrom;
		}

		public void setCopyFrom(Space copyFrom) {
			this.copyFrom = copyFrom;
		}

		@SuppressWarnings("serial")
		public SpaceForm(String id, final Space space) {

			super(id, space);

			FeedbackPanel feedback = new FeedbackPanel("feedback");
			filter = new CalipsoFeedbackMessageFilter();
			feedback.setFilter(filter);
			add(feedback);

			final CompoundPropertyModel model = new CompoundPropertyModel(this);
			setModel(model);

			// display name ====================================================
			/*final TextField name = new TextField("space.name");
			name.setRequired(true);
			name.add(new ErrorHighlighter());
			name.setOutputMarkupId(true);
			add(name);
			add(new HeaderContributor(new IHeaderContributor() {
				public void renderHead(IHeaderResponse response) {
					response.renderOnLoadJavaScript("document.getElementById('"
							+ name.getMarkupId() + "').focus()");
				}
			}));
			// form label
			name.setLabel(new ResourceModel("space_form.displayName"));
			add(new SimpleFormComponentLabel("nameLabel", name));
			*/
			if(MapUtils.isEmpty(space.getNameTranslations())){
				space.setNameTranslations(getCalipso().getNameTranslations(space));	
			}
			add(new ListView<Language>("nameTranslations", space.getSupportedLanguages()){

				protected void populateItem(ListItem<Language> listItem) {
					Language language = (Language) listItem.getModelObject();
					TextField description = new TextField("name");
					// name translations are required.
					description.setRequired(true);
					description.add(new ErrorHighlighter());
					listItem.add(description);
					description.setModel(model.bind("space.nameTranslations["+language.getId()+"]"));
					// form label for name
					description.setLabel(new ResourceModel("language."+language.getId()));
					listItem.add(new SimpleFormComponentLabel("languageLabel", description));
				}
			}.setReuseItems(true));

			// prefix Code =====================================================
			TextField prefixCode = new TextField("space.prefixCode");
			prefixCode.setRequired(true);
			prefixCode.add(new ErrorHighlighter());
			// validation: greater than 3 chars?
			prefixCode.add(new AbstractValidator() {
				protected void onValidate(IValidatable v) {
					String s = (String) v.getValue();
					if (s.length() < 3) {
						error(v);
					}
				}

				@Override
				protected String resourceKey() {
					return "space_form.error.prefixCode.tooShort";
				}
			});
			prefixCode.add(new AbstractValidator() {
				protected void onValidate(IValidatable v) {
					String s = (String) v.getValue();
					if (s.length() > 10) {
						error(v);
					}
				}

				@Override
				protected String resourceKey() {
					return "space_form.error.prefixCode.tooLong";
				}
			});
			// validation: format ok?
			prefixCode.add(new AbstractValidator() {
				protected void onValidate(IValidatable v) {
					String s = (String) v.getValue();
					if (!ValidationUtils.isAllUpperCase(s)) {
						error(v);
					}
				}

				@Override
				protected String resourceKey() {
					return "space_form.error.prefixCode.invalid";
				}
			});
			// validation: does space already exist with same prefixCode ?
			prefixCode.add(new AbstractValidator() {
				protected void onValidate(IValidatable v) {
					String s = (String) v.getValue();
					Space temp = getCalipso().loadSpace(s);
					if (temp != null && temp.getId() != space.getId()) {
						error(v);
					}
				}

				@Override
				protected String resourceKey() {
					return "space_form.error.prefixCode.exists";
				}
			});
			add(prefixCode);
			
			
			
			// form label
			prefixCode.setLabel(new ResourceModel("space_form.spaceKey"));
			add(new SimpleFormComponentLabel("prefixCodeLabel", prefixCode));

			// description =====================================================
			TextArea description = new TextArea("space.description");
			add(description.setRequired(true));
			// form label
			description.setLabel(new ResourceModel("space_form.description"));
			add(new SimpleFormComponentLabel("descriptionLabel", description));

			// enable item titles
			CheckBox itemSummaryEnabledCheckbox = new CheckBox("space.itemSummaryEnabled");
			add(itemSummaryEnabledCheckbox);
			itemSummaryEnabledCheckbox.setLabel(new ResourceModel("space_form.itemSummaryEnabled"));
			add(new SimpleFormComponentLabel("itemSummaryEnabledLabel", itemSummaryEnabledCheckbox));
			
			// is  template?
			CheckBox isTemplateCheckbox = new CheckBox("space.isTemplate");
			add(isTemplateCheckbox);
			isTemplateCheckbox.setLabel(new ResourceModel("space_form.isTemplate"));
			add(new SimpleFormComponentLabel("isTemplateLabel", isTemplateCheckbox));

			// space item duration
			HumanTimeDurationTextField defaultDuration = new HumanTimeDurationTextField("space.defaultDuration");
			add(defaultDuration);
			// space item duration label
			defaultDuration.setLabel(new ResourceModel("space_state_form.defaultDuration"));
			add(new SimpleFormComponentLabel("defaultDurationLabel", defaultDuration));
			
			// space closing date
			DateField closingDate = new DateField("space.closingDate");
			add(closingDate);
			// space closing date label
			closingDate.setLabel(new ResourceModel("closingDate"));
			add(new SimpleFormComponentLabel("closingDateLabel", closingDate));
			

			// Item Visibility allowed ===================================================
			DropDownChoice itemVisibilityChoice = 
				new DropDownChoice("space.itemVisibility", new PropertyModel(SpaceForm.this,"space.itemVisibility"), Space.ITEM_VISIBILITY_MODES_LIST, new IChoiceRenderer(){

					public Object getDisplayValue(Object object) {
						return localize("space_form.itemVisibility."+object.toString());
					}

					public String getIdValue(Object object, int index) {
						return index+"";
					}
					
				});
			itemVisibilityChoice.setNullValid(false);
			itemVisibilityChoice.setRequired(true);
			// set label for the drop down
			itemVisibilityChoice.setLabel(new ResourceModel("space_form.itemVisibility"));
			add(itemVisibilityChoice);
			// add space group choice label
			add(new SimpleFormComponentLabel("spaceItemVisibilityChoiceLabel",itemVisibilityChoice));
			// integration with assets =========================================
			DropDownChoice assetVisibilityChoice = 
				new DropDownChoice("space.assetVisibility", new PropertyModel(SpaceForm.this,"space.assetVisibility"), Space.ASSET_VISIBILITY_MODES_LIST, new IChoiceRenderer(){

					public Object getDisplayValue(Object object) {
						return localize("space_form.assetVisibility."+object.toString());
					}

					public String getIdValue(Object object, int index) {
						return index+"";
					}
					
				});
			assetVisibilityChoice.setNullValid(false);
			assetVisibilityChoice.setRequired(true);
			// set label for the drop down
			assetVisibilityChoice.setLabel(new ResourceModel("space_form.assetVisibility"));
			add(assetVisibilityChoice);
			// add space group choice label
			add(new SimpleFormComponentLabel("spaceAssetVisibilityChoiceLabel",assetVisibilityChoice));
			
			// SpaceGroup =========================================
			// get all space groups that the user has created
			// TODO: add Other
			final Set<SpaceGroup> spaceGroups = getCalipso().getSpaceGroupsForUser(getPrincipal().getId());
			
			spaceGroupDetailsContainer = new WebMarkupContainer("spaceGroupDetailsContainer");
			add(spaceGroupDetailsContainer.setOutputMarkupId(true));
			spaceGroups.add(new SpaceGroup());

			spaceGroupChoice = getSpaceGroupChoice(spaceGroups);
			spaceGroupDetailsContainer.add(spaceGroupChoice);
			// add space group choice label
			spaceGroupDetailsContainer.add(new SimpleFormComponentLabel("spaceGroupChoiceLabel",spaceGroupChoice));
			// TODO: reomve items that are already added
			spaceGroupDetails = getSpaceGroupDetailsContainer(false);
			spaceGroupDetailsContainer.add(spaceGroupDetails);
			
			add(new Button("back") {
				@Override
				public void onSubmit() {
					
					activate(new IBreadCrumbPanelFactory() {
						
						private static final long serialVersionUID = 1L;

						public BreadCrumbPanel create(String componentId,
								IBreadCrumbModel breadCrumbModel) {
							BreadCrumbUtils.removePreviousBreadCrumbPanel(breadCrumbModel);
							return new SpacePanelLanguageSupport(componentId, breadCrumbModel, space);
						}
					});
				}
			}.setDefaultFormProcessing(false));

            add(new Button("apply") {
                @Override
                public void onSubmit() {
					final Space mergedSpace = persistChanges();
					activate(new IBreadCrumbPanelFactory() {
        				public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
        					BreadCrumbUtils.removePreviousBreadCrumbPanel(breadCrumbModel);
        					return new SpaceFormPanel(id, breadCrumbModel, mergedSpace);
        				}
        			});
				}
            }.setVisible(false));
            
			add(new Button("next"){
				/* (non-Javadoc)
				 * @see org.apache.wicket.markup.html.form.Button#onSubmit()
				 */
				@Override
				public void onSubmit() {
					// update name based on translations
					// TODO: asset <-> item plugins still depend on this
					// but this should be covered by CalipsoServiceImpl now
					// space.setName(space.getNameTranslations().get(space.getSupportedLanguages().get(0).getId()));
					//logger.debug("next clicked, space roles: "+space.getSpaceRoles());
					CalipsoService calipso = getCalipso();
					if (copyFrom != null) {
						Space spaceFrom = calipso.loadSpace(copyFrom.getId());
						SpaceUtils.copySpace(calipso, spaceFrom, SpaceForm.this.getSpace());
						logger.debug("Space roles: "+space.getSpaceRoles());
					}
					if(space.getSpaceGroup() != null && space.getSpaceGroup().getId() == null){
						space.getSpaceGroup().getAdmins().add(getPrincipal());
					}
					activate(new IBreadCrumbPanelFactory() {
						public BreadCrumbPanel create(String componentId,
								IBreadCrumbModel breadCrumbModel) {
							return new SpaceFieldListPanel(componentId, breadCrumbModel, space, null);
						}
					});
				}
			});

			// TODO: choice Renderer

			// hide copy from option if edit ===================================
			
			WebMarkupContainer hide = new WebMarkupContainer("hide");
			if(space.getPublished()) { 
				hide.setVisible(false); 
				}
			else { 
				User user = getPrincipal();
				List<Space> spaces;
				// if space admin, only allow 
				if (user.isGlobalAdmin()){ 
					spaces = getCalipso().findAllSpaces();
				} 
				else{ 
					spaces = user.getTemplateSpacesForUser();
				}
			
				DropDownChoice choice = new DropDownChoice("copyFrom",	spaces, new IChoiceRenderer() { 
					public Object getDisplayValue(Object o) { 
						Space space = (Space) o;
						return new StringBuffer(space.getSpaceGroup().getName())
							.append(": ")
							.append(localize(space.getNameTranslationResourceKey())); 
					}
					public String getIdValue(Object o, int i) { 
						return ((Space) o).getId()+"";
					} 
				});
				choice.setNullValid(true);
				hide.add(choice);
				//form label 
				choice.setLabel(new ResourceModel("space_form.copyExisting")); 
				hide.add(new SimpleFormComponentLabel("copyFromLabel", choice)); } 
				add(hide);
		}

		private DropDownChoice getSpaceGroupChoice(Set<SpaceGroup> spaceGroups) {
			// add other to the list, REFACTOR
			IChoiceRenderer spaceGroupRenderer = new IChoiceRenderer() {

				public Object getDisplayValue(Object object) {

					SpaceGroup sg = (SpaceGroup) object;
					if (sg.getId() == null) {
						return localize("space_form.createNewSpaceGroup");
					}
					return sg.getName();
				}

				public String getIdValue(Object object, int index) {
					return String.valueOf(index);
				}

			};
			final DropDownChoice spaceGroupChoice = new DropDownChoice("space.spaceGroup", new PropertyModel(this.getSpace(),"spaceGroup"), 
					new ArrayList(spaceGroups), spaceGroupRenderer);/* {

				@Override
				public boolean wantOnSelectionChangedNotifications() {
					return true;
				}

				public void onSelectionChanged(Object newSelection) {
					SpaceGroup tmpSpaceGroup = (SpaceGroup) newSelection;
					setModelObject(newSelection);

					if (newSelection != null && tmpSpaceGroup.getId() == null) {
						spaceGroupDetails.setVisible(true);
					} else {
						spaceGroupDetails.setVisible(false);
					}
				}
			};*/
			spaceGroupChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
	            protected void onUpdate(AjaxRequestTarget target) {
	            	//SpaceGroup tmpSpaceGroup = (SpaceGroup) newSelection;
					//setModelObject(newSelection);
	            	SpaceGroup sg = (SpaceGroup) spaceGroupChoice.getModelObject();
	            	//logger.debug("Selected space group: "+sg);
					if (sg != null && sg.getId() == null) {
						spaceGroupDetails = SpaceForm.this.getSpaceGroupDetailsContainer(true);
						SpaceForm.this.spaceGroupDetailsContainer.replace(spaceGroupDetails);
		            	//logger.debug("spaceGroupDetails is visible..."+spaceGroupDetails.isVisible());
					} else {
						spaceGroupDetails.setVisible(false);
					}
					target.addComponent(SpaceForm.this.spaceGroupDetailsContainer);
	            }
	        });

			spaceGroupChoice.setEnabled(this.getSpace().getSpaceGroup() == null);
			spaceGroupChoice.setNullValid(false);
			spaceGroupChoice.setRequired(true);
			
			// set label for the drop down
			spaceGroupChoice.setLabel(new ResourceModel("space_form.spaceGroupChoice"));
			return spaceGroupChoice;
		}

		/**
		 * @param listOfAllSpaceGroups
		 */
		private WebMarkupContainer getSpaceGroupDetailsContainer(boolean visible) {
			WebMarkupContainer spaceGroupDetails = new WebMarkupContainer("spaceGroupDetails");
			spaceGroupDetails.setOutputMarkupId(true);
			spaceGroupDetails.setRenderBodyOnly(false);
			spaceGroupDetails.setVisible(visible);
			
			

			boolean isRequiredValue = (spaceGroupChoice != null
					&& spaceGroupChoice.getModelObject() != null
					&& ((SpaceGroup) spaceGroupChoice.getModelObject()).getId() == null);
			
			// space group name component
			TextField spaceGroupName = new TextField("space.spaceGroup.name", new PropertyModel(SpaceForm.this,"space.spaceGroup.name"));
			spaceGroupName.setRequired(isRequiredValue);
			// set space group name label
			spaceGroupName.setLabel(new ResourceModel("space_form.spaceGroupName"));
			// add validation to check unique space group name
			if(visible){
				spaceGroupName.add(new UniqueSpaceGroupNameForEachAdminValidator(getCalipso().getSpaceGroupsForUser(getPrincipal().getId())));
			}
			// space group description component
			TextArea spaceGroupDescription = new TextArea("space.spaceGroup.description", new PropertyModel(SpaceForm.this,"space.spaceGroup.description"));
			spaceGroupDescription.setLabel(new ResourceModel("space_form.spaceGroupDescription"));
			
			spaceGroupDescription.setRequired(isRequiredValue);
			// add space group name component
			spaceGroupDetails.add(spaceGroupName);
			// add space group name label
			spaceGroupDetails.add(new SimpleFormComponentLabel("spaceGroupNameLabel",spaceGroupName));
			// add space group description component			
			spaceGroupDetails.add(spaceGroupDescription);
			// add space group description label
			spaceGroupDetails.add(new SimpleFormComponentLabel("spaceGroupDescriptionLabel",spaceGroupDescription));
			return spaceGroupDetails;
		}
/*
		@Override
		protected void validate() {
			filter.reset();
			super.validate();
		}
*/
		@Override
		protected void onSubmit() {}
		 
		private Space persistChanges() {
			Space space = this.getSpace();
			space = getCalipso().storeSpace(space);
			// current user may be allocated to this space, and e.g.
			// name could have changed
			refreshPrincipal();
			return space;
		}
	}
}