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

package gr.abiss.calipso.wicket.asset;

import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.domain.Language;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.ConfirmPanel;
import gr.abiss.calipso.wicket.ErrorHighlighter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

/**
 * Renders the lookup values of a given Custom Attribute.
 * Gives also the ability for Create / Edit / Delete a lookup value.
 * @deprecated
 */

public class AssetCustomAttributeLookupValuesFormPanel extends BasePanel {

	protected static final Logger logger = Logger.getLogger(AssetCustomAttributeLookupValuesFormPanel.class);
	private  AssetTypeCustomAttribute assetTypeCustomAttribute;
	private CustomAttributeLookupValue lookupValue;
	List<CustomAttributeLookupValue> lookupValues;
	private boolean isNewEntry;
	private boolean canBeDeleted;
	private AssetType referenceAssetType = null;

	/**
	 *  @param id Component markup id
	 *  @param assetTypeCustomAttribute The given Custom Attribute
	 *  @param parentPreviousPage The previous page of the parent page 
	 * */

	public AssetCustomAttributeLookupValuesFormPanel(String id, IBreadCrumbModel breadCrumbModel, AssetTypeCustomAttribute assetTypeCustomAttribute) {
		super(id, breadCrumbModel);
		// logger.debug("Constructor with no lookup value");
		this.assetTypeCustomAttribute = assetTypeCustomAttribute;
		this.canBeDeleted = false;
	
		addComponents();
	}//AssetCustomAttributeLookupValuesFormPanel
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @param id Component markup id
	 * @param assetTypeCustomAttribute The given Custom Attribute
	 * @param lookupValue The given lookup value
	 * @param parentPreviousPage The previous page of the parent page
	 */
	public AssetCustomAttributeLookupValuesFormPanel(String id, IBreadCrumbModel breadCrumbModel, AssetTypeCustomAttribute assetTypeCustomAttribute, CustomAttributeLookupValue lookupValue) {
		super(id, breadCrumbModel);
		// logger.debug("Constructor with lookup value"+lookupValue);
		this.assetTypeCustomAttribute = assetTypeCustomAttribute;
		this.lookupValue = lookupValue;
		this.canBeDeleted = getCalipso().loadCountForCustomAttributeLookupValue(lookupValue)==0;
		
		addComponents();
	}//AssetCustomAttributeLookupValuesFormPanel
	
	//-------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Renders User Interface components
	 * */
	private void addComponents(){
		

		if (this.assetTypeCustomAttribute.getAllowedLookupValues()==null){
			lookupValues = new ArrayList<CustomAttributeLookupValue>();
		}//if
		else{
			lookupValues = new ArrayList<CustomAttributeLookupValue>(this.assetTypeCustomAttribute.getAllowedLookupValues());
		}//else
		
		add(new ValuesForm("valuesForm", lookupValues));
		WebMarkupContainer editArea = new WebMarkupContainer("editArea");
		
		if (this.lookupValue==null){
			editArea.add(new EditForm("editForm", new CustomAttributeLookupValue()));
			editArea.setVisible(false);
		}//if
		else{
			isNewEntry = (lookupValue.getAttribute()==null);
			editArea.add(new Label("editLabel", localize("asset.customAttributes." + String.valueOf(isNewEntry?"add":"edit") + "Value")));
			editArea.add(new EditForm("editForm", this.lookupValue));
		}//else
		
		add(editArea);
		

		Link add = new Link("add"){
			public void onClick() {

				activate(new IBreadCrumbPanelFactory(){
					public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {

						//Remove last breadcrumb participant
						if (breadCrumbModel.allBreadCrumbParticipants().size()>0){
							breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
						}//if

						return new AssetCustomAttributeFormPagePanel(breadCrumbModel.getActive().getComponent().getId(), breadCrumbModel, AssetCustomAttributeLookupValuesFormPanel.this.assetTypeCustomAttribute, new CustomAttributeLookupValue()).setReferenceAssetType(referenceAssetType);
					}
				});
				
			}//onClick
		};
		add(add);

	}//addComponents
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 *Form for editing lookup values show order  
	 **/
	
	private class ValuesForm extends Form{
		
		public ValuesForm(String id, final List<CustomAttributeLookupValue> lookupValues) {
			super(id);
		
		///////////////
		// List View //
		///////////////

			//List of lookup values
			ListView listView = new ListView("values", lookupValues) {
				protected void populateItem(final ListItem listItem) {
					final CustomAttributeLookupValue lookupValue = (CustomAttributeLookupValue)listItem.getModelObject();
					listItem.add(new Label("showOrder", String.valueOf(lookupValue.getShowOrder())));
					listItem.add(new Label("value", localize(lookupValue.getNameTranslationResourceKey())));

					//Up
					//Move value up
					listItem.add(new Link("up") {
						public void onClick() {
							//Look if is the value first in list
							boolean isFirst = (listItem.getIndex()==0);
							if (!isFirst){
								//Store above value
								String aboveValue = lookupValues.get(listItem.getIndex()-1).getValue();
								//Store current Value
								String currentValue = lookupValue.getValue();
								
								//Swap values
								//Set above value as current value 
								lookupValues.get(listItem.getIndex()).setValue(aboveValue);
								//Set current value as above value 
								lookupValues.get(listItem.getIndex()-1).setValue(currentValue);

								activate(new IBreadCrumbPanelFactory(){
									public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {

										//Remove last breadcrumb participant
										if (breadCrumbModel.allBreadCrumbParticipants().size()>0){
											breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
										}//if

										return new AssetCustomAttributeFormPagePanel(breadCrumbModel.getActive().getComponent().getId(), breadCrumbModel, 
																						AssetCustomAttributeLookupValuesFormPanel.this.assetTypeCustomAttribute/*, true*/).setReferenceAssetType(referenceAssetType);
									}
								});
							}//if
						}
					});
					
					//Down
					//Move value down
					listItem.add(new Link("down") {
						public void onClick() {
							int displayOrder = lookupValue.getShowOrder();
							boolean isLast = (listItem.getIndex()==lookupValues.size()-1);
							if (!isLast){
								//Store below Value
								String belowValue = lookupValues.get(listItem.getIndex()+1).getValue();
								//Store current Value
								String currentValue = lookupValue.getValue();
								
								//Swap values
								//Set current value as below value 
								lookupValues.get(listItem.getIndex()+1).setValue(currentValue);								
								//Set below value as current value 
								lookupValues.get(listItem.getIndex()).setValue(belowValue);

								activate(new IBreadCrumbPanelFactory(){
									public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {

										//Remove last breadcrumb participant
										if (breadCrumbModel.allBreadCrumbParticipants().size()>0){
											breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
										}//if

										return new AssetCustomAttributeFormPagePanel(breadCrumbModel.getActive().getComponent().getId(), breadCrumbModel, 
																						AssetCustomAttributeLookupValuesFormPanel.this.assetTypeCustomAttribute/*, true*/).setReferenceAssetType(referenceAssetType);
									}
								});
								
							}//if
						}//onClick
					});

					//Edit
					Link edit = new Link("edit"){
						@Override
						public void onClick() {

							activate(new IBreadCrumbPanelFactory(){
								public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
									//Remove last breadcrumb participant
									if (breadCrumbModel.allBreadCrumbParticipants().size()>0){
										breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
									}//if

									return new AssetCustomAttributeFormPagePanel(breadCrumbModel.getActive().getComponent().getId(), getBreadCrumbModel(), 
																				AssetCustomAttributeLookupValuesFormPanel.this.assetTypeCustomAttribute, lookupValue).setReferenceAssetType(referenceAssetType);
								}
							});

						}//onClick
					};//edit
					listItem.add(edit);
					
					//Delete Value
					Link delete = new Link("delete"){
						@Override
						public void onClick() {

							activate(new IBreadCrumbPanelFactory(){
								public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
									
									//Delete Custom Attribute Lookup Value
									assetTypeCustomAttribute.remove(lookupValue);

									BreadCrumbUtils.removeActiveBreadCrumbPanel(getBreadCrumbModel());
									return new AssetCustomAttributeFormPagePanel(getBreadCrumbModel().getActive().getComponent().getId(), getBreadCrumbModel(), assetTypeCustomAttribute/*, true*/).setReferenceAssetType(referenceAssetType);
								}
							});

						}//onClick
					};//edit
					
					boolean canBeDeleted = getCalipso().loadCountForCustomAttributeLookupValue(lookupValue)==0;
					boolean isNewEntry = (lookupValue.getAttribute()==null);
					
					listItem.add(delete.setVisible(canBeDeleted && !isNewEntry));

				}//populateItem
			};//listView

			add(listView);
		}//ValuesForm

		//------------------------------------------------------------------------------------------------------------
		
		@Override
		protected void onSubmit() {
			
		}//onSubmit
	}//ValuesForm
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Form for editing lookup values
	 * */
	
	private class EditForm extends Form{
		private CustomAttributeLookupValue lookupValue;
		public CustomAttributeLookupValue getLookupValue() {
			return lookupValue;
		}
		public void setLookupValue(CustomAttributeLookupValue lookupValue) {
			this.lookupValue = lookupValue;
		}


		public EditForm(String id, final CustomAttributeLookupValue lookupValue) {
			super(id);
			this.lookupValue = lookupValue;
			final CompoundPropertyModel model = new CompoundPropertyModel(this);
			setModel(model);
			//final TextField newValue = new TextField("value", new PropertyModel(lookupValue,"value"));
			//newValue.setRequired(true);
			//newValue.add(new UniqueInputValidator(lookupValues));
			//add(newValue);

			// logger.debug("Translations for lookup value "+lookupValue.getName()+": "+lookupValue.getTranslations());
			if(MapUtils.isEmpty(lookupValue.getNameTranslations())){
				// logger.debug("Loaded translations for lookup value "+lookupValue.getName()+" using key: "+lookupValue.getNameTranslationResourceKey());
				lookupValue.setNameTranslations(getCalipso().getNameTranslations(lookupValue));
				// logger.debug("Loaded translations for lookup value "+lookupValue.getName()+": "+lookupValue.getTranslations());
			}
			add(new ListView("nameTranslations", getCalipso().getSupportedLanguages()){
				protected void populateItem(ListItem listItem) {
					Language language = (Language) listItem.getModelObject();
					TextField description = new TextField("name");
					// name translations are required.
					description.setRequired(true);
					description.add(new ErrorHighlighter());
					listItem.add(description);
					description.setModel(new PropertyModel(model.getObject(), "lookupValue.nameTranslations["+language.getId()+"]"));
					//model.bind(description, "lookupValue.nameTranslations["+language.getId()+"]");
					// form label for name
					description.setLabel(new ResourceModel("language."+language.getId()));
					listItem.add(new SimpleFormComponentLabel("languageLabel", description));
				}
			});
			
			Button btnSubmit = new Button("btnSubmit"){
				public void onSubmit() {
					lookupValue.setName(lookupValue.getNameTranslations().get(getCalipso().getDefaultLocale()));
					lookupValue.setAttribute(AssetCustomAttributeLookupValuesFormPanel.this.assetTypeCustomAttribute);

					//New value
					if (isNewEntry){
						//First value
						if (AssetCustomAttributeLookupValuesFormPanel.this.assetTypeCustomAttribute.getAllowedLookupValues()==null || AssetCustomAttributeLookupValuesFormPanel.this.assetTypeCustomAttribute.getAllowedLookupValues().size()==0){
							lookupValue.setShowOrder(1);
						}//if
						else{
							if (lookupValue.getShowOrder()==0){
								lookupValue.setShowOrder(AssetCustomAttributeLookupValuesFormPanel.this.assetTypeCustomAttribute.getAllowedLookupValues().size()+1);
							}//if
						}//else
						//if (AssetCustomAttributeLookupValuesFormPanel.this.assetTypeCustomAttribute.getAllowedLookupValues()==null){
						//	AssetCustomAttributeLookupValuesFormPanel.this.assetTypeCustomAttribute.setAllowedLookupValues(new LinkedHashSet<CustomAttributeLookupValue>(getJtrac().findLookupValuesByAssetAttribute(AssetCustomAttributeLookupValuesFormPanel.this.assetTypeCustomAttribute)));
						//}
						AssetCustomAttributeLookupValuesFormPanel.this.assetTypeCustomAttribute.addAllowedLookupValue(lookupValue);
						
						activate(new IBreadCrumbPanelFactory(){
							public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {

								//Remove last breadcrumb participant
								if (breadCrumbModel.allBreadCrumbParticipants().size()>0){
									breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
								}//if

								return new AssetCustomAttributeFormPagePanel(breadCrumbModel.getActive().getComponent().getId(), breadCrumbModel, 
																			AssetCustomAttributeLookupValuesFormPanel.this.assetTypeCustomAttribute/*, true*/).setReferenceAssetType(referenceAssetType);
							}
						});
						
					}//if
					else{
						activate(new IBreadCrumbPanelFactory(){
							public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {

								//Remove last breadcrumb participant
								if (breadCrumbModel.allBreadCrumbParticipants().size()>0){
									breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
								}//if

								return new AssetCustomAttributeFormPagePanel(
										breadCrumbModel.getActive().getComponent().getId(), 
										breadCrumbModel, 
										(AssetTypeCustomAttribute) lookupValue.getAttribute()/*, true*/).setReferenceAssetType(referenceAssetType);
							}
						});
						
					}//else
				}
			};
			add(btnSubmit);

			//Delete value
			SubmitLink btnDelete = new SubmitLink("btnDelete"){
				public void onSubmit() {
					activate(new IBreadCrumbPanelFactory(){
						final String line1 = new String("");
						final String line2 = new String("\"" + lookupValue.getValue() + "\"");
						final String heading = new String(localize("asset.customAttributes.deleteValueConfirm"));
						final String warning = new String(localize("asset.customAttributes.deleteValue"));
	
	                	public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
	                		ConfirmPanel confirm = new ConfirmPanel(getBreadCrumbModel().getActive().getComponent().getId(), getBreadCrumbModel(), heading, warning, new String[] {line1, line2}) {
								public void onConfirm() {
									//Delete Custom Attribute Lookup Value
									assetTypeCustomAttribute.remove(lookupValue);

									BreadCrumbUtils.popPanels(2, getBreadCrumbModel());
									//Go to previous page
									activate(new IBreadCrumbPanelFactory(){
										public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
											return new AssetCustomAttributeFormPagePanel(getBreadCrumbModel().getActive().getComponent().getId(), getBreadCrumbModel(), assetTypeCustomAttribute/*, true*/).setReferenceAssetType(referenceAssetType);
										}
									});
								}
	                		};
	                		return confirm;
	                	}
					});
				}
			};
			btnDelete.setDefaultFormProcessing(false);
			add(btnDelete.setVisible(canBeDeleted && !isNewEntry));

			Button btnCancel = new Button("btnCancel"){
				public void onSubmit() {
					activate(new IBreadCrumbPanelFactory(){
						public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {

							//Remove last breadcrumb participant
							if (breadCrumbModel.allBreadCrumbParticipants().size()>0){
								breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
							}//if

							return new AssetCustomAttributeFormPagePanel(breadCrumbModel.getActive().getComponent().getId(), breadCrumbModel, AssetCustomAttributeLookupValuesFormPanel.this.assetTypeCustomAttribute).setReferenceAssetType(referenceAssetType);
						}
					});
					
				}//onSubmit
			};
			add(btnCancel);
			
		}//EditForm
	} //EditForm

	public AssetCustomAttributeLookupValuesFormPanel setReferenceAssetType(AssetType referenceAssetType) {
		this.referenceAssetType = referenceAssetType;
		return this;
	}

	
	
}//AssetCustomAttributeLookupValuesFormPanel