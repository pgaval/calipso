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

package gr.abiss.calipso.wicket.asset.menu;

import java.util.ArrayList;
import java.util.List;

import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.CustomAttribute;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.ConfirmPanel;
import gr.abiss.calipso.wicket.ErrorHighlighter;
import gr.abiss.calipso.wicket.CalipsoFeedbackMessageFilter;
import gr.abiss.calipso.wicket.regexp.ValidationPanel;

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
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
/**
 * 
 *
 */
public class AssetPanelManageCustomAttributes extends BasePanel {

	private static final long serialVersionUID = 1L;
	private AssetTypeCustomAttribute assetTypeCustomAttribute;
	private CustomAttributeLookupValue lookupValue;
	private boolean canBeDeleted;
	private boolean isEdit;
	private Panel lookupValuesPanel;
	private boolean isSearch;

	/**
	 * Constructor for Custom Attribute editing in case that the custom attribute in question is from type "Drop Down List".
	 * 
	 * @param previousPage Previous page. Useful for cancel.
	 * @param assetTypeCustomAttribute The custom attribute for editing
	 * @param lookupValue the lookup value for editing
	 * */
	public AssetPanelManageCustomAttributes(String id, IBreadCrumbModel breadCrumbModel, AssetTypeCustomAttribute assetTypeCustomAttribute, 
			CustomAttributeLookupValue lookupValue, boolean isSearch) {
		super(id, breadCrumbModel);
		canBeDeleted = (assetTypeCustomAttribute != null)
			&& (getCalipso().loadCountAssetsForCustomAttribute(assetTypeCustomAttribute) == 0);
		isEdit = assetTypeCustomAttribute != null;
		this.isSearch = isSearch;
		
		this.assetTypeCustomAttribute = isEdit?getCalipso().loadAssetTypeCustomAttribute(assetTypeCustomAttribute.getId())
				:new AssetTypeCustomAttribute();
		this.lookupValue = lookupValue;
		// check if is null, if not check if any asset instance use this custom attribute
		// if also not then can be deleted
		setupVisuals();
		addComponents();
	}
    
	private void setupVisuals(){
        //edit or new label
        add(new Label("label", isEdit?localize("asset.customAttributes.edit"):localize("asset.customAttributes.createNewAttribute")));
        //make cancel button
		getBackLinkPanel().makeCancel();
		//Highlight this Custom Attribute in the previous page
	}
	
	
	/**
	 * Renders User Interface components
	 * */
	private void addComponents(){	        

		deleteLink();
		
		add(new AssetCustomAttributeForm("form", this.assetTypeCustomAttribute));
	}
	/**
	 * Wicket Form
	 *  
	 */	
	private class AssetCustomAttributeForm extends Form{
		
		private static final long serialVersionUID = 1L;
		private AssetTypeCustomAttribute customAttribute;
		private CalipsoFeedbackMessageFilter filter;
		private Panel validPanel;
		private DropDownChoice type;
		private CompoundPropertyModel model;
		
		public AssetCustomAttributeForm(String id, AssetTypeCustomAttribute assetTypeCustomAttribute) {
			super(id);
            FeedbackPanel feedback = new FeedbackPanel("feedback");
            filter = new CalipsoFeedbackMessageFilter();
            feedback.setFilter(filter);
            add(feedback);

            customAttribute = assetTypeCustomAttribute;
            model = new CompoundPropertyModel(customAttribute);
			setModel(model);
			addFormComponents();
		}
		/*
		@Override
		protected void validate() {
		    filter.reset();
		    super.validate();
		}
		*/
		
		protected void onSubmit() {
			if(logger.isDebugEnabled()){
				logger.debug("Trying to store CustomAttribute : " + customAttribute.getName());
			}
			getCalipso().storeCustomAttribute((AssetTypeCustomAttribute)model.getObject());
			activate(new IBreadCrumbPanelFactory() {
				
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public BreadCrumbPanel create(String componentId,
						IBreadCrumbModel breadCrumbModel) {
					return null;// new AssetCustomAttributesPanel(componentId, breadCrumbModel);
				}
			});
				
		}
			
		
		public String getTitle() {
			if (isEdit){
				return localize("asset.customAttributes.edit");
			}//if
	
			return localize("asset.customAttributes.createNewAttribute");
		}
		
		private void addFormComponents(){
			add(new WebMarkupContainer("mandatoryPanel"));
			// name
			// ------------------------------------------------------------------------------------

			TextField description = new TextField("name");
			if (canBeDeleted) {
				description.setRequired(true);
				description.add(new ErrorHighlighter());
			}
			add(description);
			description.setModel(model);
			// form label for name
			description.setLabel(new ResourceModel(
					"asset.customAttributes.description"));
			add(new SimpleFormComponentLabel("nameLabel", description));

			// form type
			// -------------------------------------------------------------------------------			
			type = new DropDownChoice("formType", CustomAttribute.FORM_TYPES, new IChoiceRenderer() {
				
				private static final long serialVersionUID = 1L;

				public String getIdValue(Object o, int i) {
					return i + "";
				}

				public Object getDisplayValue(Object o) {

					return localize("asset.attributeType_" + o.toString());
				}
			}) {
				private static final long serialVersionUID = 1L;
				/**
				 * @see org.apache.wicket.Component#initModel()
				 */
				@Override
				protected boolean wantOnSelectionChangedNotifications() {
					return true;
				}

				@Override
				protected void onSelectionChanged(Object newSelection) {
					Integer selection = (Integer)newSelection;
					
					if (canBeDeleted) {
						AssetCustomAttributeForm.this.remove(validPanel);

						if (selection.equals(AssetTypeCustomAttribute.FORM_TYPE_TEXT)) {
							validPanel = new ValidationPanel("validPanel", model, canBeDeleted);
						}
						else{
							validPanel = new EmptyPanel("validPanel");
						}
						AssetCustomAttributeForm.this.add(validPanel);
					}

					AssetPanelManageCustomAttributes.this.remove(lookupValuesPanel);
					if(selection.equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)){
						lookupValuesPanel = new AssetsPanelManageCustomAttributeLookupValues("lookupValuesPanel", getBreadCrumbModel(), customAttribute, lookupValue);
					}else{
						lookupValuesPanel = new EmptyPanel("lookupValuesPanel");
					}
					
					AssetPanelManageCustomAttributes.this.add(lookupValuesPanel);
					setModelObject(newSelection);
				}

				/**
				 * @see
				 * org.apache.wicket.markup.html.form.AbstractSingleSelectChoice
				 * #getDefaultChoice(java.lang.Object)
				 */
				protected String getDefaultChoice(Object selected) {
					return AssetTypeCustomAttribute.FORM_TYPE_TEXT.toString();
				}
			};
			
			type.setNullValid(false);
			type.setEnabled(!isEdit);
			type.setOutputMarkupId(true);
			add(type);
			type.setModel(model);
			// form label for form type
			type.setLabel(new ResourceModel("asset.customAttributes.type"));
			add(new SimpleFormComponentLabel("formTypeLabel", type));
			if (!isEdit) {
				type.setRequired(true);
				type.add(new ErrorHighlighter());
				add(type);
			}

			// Mandatory mark. red asterisk (*)
			if (type.getModelObject() != null &&
					type.getModelObject().equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)) {
				validPanel = new ValidationPanel("validPanel", model, canBeDeleted);
			}

			else {
				validPanel = new EmptyPanel("validPanel");
			}
			add(validPanel);
			if(type.getModelObject() != null &&
					type.getModelObject().equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)){
				lookupValuesPanel = new AssetsPanelManageCustomAttributeLookupValues("lookupValuesPanel", getBreadCrumbModel(), customAttribute, lookupValue);
			}else{
				lookupValuesPanel = new EmptyPanel("lookupValuesPanel");
			}
			AssetPanelManageCustomAttributes.this.add(lookupValuesPanel);
			
			Fragment mandatoryFragment;
			Fragment activeFragment;

			if (!isSearch) {// Edit Mode

				mandatoryFragment = new Fragment("mandatoryField",
						"mandatoryEditMode", this);
				activeFragment = new Fragment("activeField", "activeEditMode", this);

				// Mandatory checkbox
				// ------------------------------------------------------------------
				CheckBox mandatory = new CheckBox("mandatory");
				mandatoryFragment.add(mandatory);
				mandatory.setModel(model);
				// form label for mandatory
				mandatory.setLabel(new ResourceModel(
						"asset.customAttributes.mandatory"));
				add(new SimpleFormComponentLabel("mandatoryLabel", mandatory));

				// Active checkbox
				// ---------------------------------------------------------------------
				CheckBox active = new CheckBox("active");
				activeFragment.add(active);
				active.setModel(model);
				// form label for active
				active.setLabel(new ResourceModel("asset.customAttributes.active"));
				add(new SimpleFormComponentLabel("activeLabel", active));
				
				

			} else {// Search Mode
				List<Boolean> searchModi = new ArrayList<Boolean>();
				searchModi.add(false);
				searchModi.add(true);
				// Mandatory
				// ---------------------------------------------------------------------------
				mandatoryFragment = new Fragment("mandatoryField",
						"mandatorySearchMode", this);
				final DropDownChoice mandatoryChoice = new DropDownChoice(
						"mandatory", searchModi, new IChoiceRenderer() {
							public Object getDisplayValue(Object object) {
								String answer = localize("asset.customAttribute.no");
								if(((Boolean)object).equals(true)){
									answer = localize("asset.customAttribute.yes");
								}
								return answer;
							}

							public String getIdValue(Object object, int index) {
								return index + "";
							}
						});

				mandatoryChoice.setNullValid(true);

				// form label for mandatory
				mandatoryChoice.setLabel(new ResourceModel(
						"asset.customAttributes.mandatory"));
				add(new SimpleFormComponentLabel("mandatoryLabel", mandatoryChoice));

				mandatoryFragment.add(mandatoryChoice);

				// Active
				// ------------------------------------------------------------------------------
				activeFragment = new Fragment("activeField", "activeSearchMode",
						this);
				final DropDownChoice activeChoice = new DropDownChoice("active",
						searchModi, new IChoiceRenderer() {
					public Object getDisplayValue(Object object) {
						String answer = localize("asset.customAttribute.yes");
						if(((Boolean)object).equals(true)){
							answer = localize("asset.customAttribute.no");
						}
						return answer;
					}

					public String getIdValue(Object object, int index) {
						return index + "";
					}
				});

				activeChoice.setNullValid(true);

				// form label for active
				activeChoice.setLabel(new ResourceModel(
						"asset.customAttributes.active"));
				add(new SimpleFormComponentLabel("activeLabel", activeChoice));

				activeFragment.add(activeChoice);
			}
			add(mandatoryFragment);
			add(activeFragment);
		}
	}
	
	private void deleteLink(){
		//hide link if: admin, user==null, try to delete self or new user
		if( canBeDeleted == false || assetTypeCustomAttribute == null || assetTypeCustomAttribute.getId() == null){
			add(new WebMarkupContainer("delete").setVisible(false));
		}
		else{//if edit
	    	add(new Link("delete") {		
				@Override
				public void onClick() {
					
					final String line1 = localize("asset.customAttributes.deleteConfirmMessage");
					final String line2 = new String("\"" + assetTypeCustomAttribute.getName() + "\"");
					final String heading = new String(localize("asset.customAttributes.deleteConfirmHeading"));
					final String warning = new String("");
	            	
	            	activate(new IBreadCrumbPanelFactory(){
	                    public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel){
	                    	ConfirmPanel confirm = new ConfirmPanel(componentId, breadCrumbModel, heading, warning, new String[] {line1}) {
	                            public void onConfirm() {
									//Delete Custom Attribute
									getCalipso().removeCustomAttribute(assetTypeCustomAttribute);
	                                
	                                BreadCrumbUtils.removePreviousBreadCrumbPanel(getBreadCrumbModel());
	                                
	                                activate(new IBreadCrumbPanelFactory(){
										public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
											return (BreadCrumbPanel) breadCrumbModel.getActive();
										}                                    	
	                                });
	                            }                        
	                        };
	                        return confirm;
	                    }
	                });
				}
			});
		}
	}
}