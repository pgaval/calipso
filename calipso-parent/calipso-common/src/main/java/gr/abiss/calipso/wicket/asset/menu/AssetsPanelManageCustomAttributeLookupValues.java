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

import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.ConfirmPanel;
import gr.abiss.calipso.wicket.components.dataprovider.DataProviderAssetTypeLookupValue;
import gr.abiss.calipso.wicket.components.dataview.DataViewAssetTypeLookupValue;
import gr.abiss.calipso.wicket.components.validators.UniqueInputValidator;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

/**
 * 
 * Renders the lookup values of a given Custom Attribute.
 * Gives also the ability for Create / Edit / Delete a lookup value.
 */

public class AssetsPanelManageCustomAttributeLookupValues extends BasePanel {
	
	private static final long serialVersionUID = 1L;
	private  AssetTypeCustomAttribute assetTypeCustomAttribute;
	private CustomAttributeLookupValue lookupValue;
	Set<CustomAttributeLookupValue> lookupValues;
	private boolean isNewEntry;
	private boolean canBeDeleted;
	private WebMarkupContainer editArea;
	private EditLookupValueForm editForm;
	private ValuesForm valuesForm;
	private DataProviderAssetTypeLookupValue dataProvider;
	private DataViewAssetTypeLookupValue dataView;


	/**
	 * @param id Component markup id
	 * @param assetTypeCustomAttribute The given Custom Attribute
	 * @param lookupValue The given lookup value
	 * @param parentPreviousPage The previous page of the parent page
	 */
	public AssetsPanelManageCustomAttributeLookupValues(String id, IBreadCrumbModel breadCrumbModel, AssetTypeCustomAttribute assetTypeCustomAttribute, CustomAttributeLookupValue lookupValue) {
		super(id, breadCrumbModel);
		
		this.assetTypeCustomAttribute = assetTypeCustomAttribute;
		this.lookupValue = lookupValue;
		this.canBeDeleted = ( lookupValue != null && getCalipso().loadCountForCustomAttributeLookupValue(lookupValue)==0);	
		addComponents();
	}//AssetCustomAttributeLookupValuesFormPanel
	
	//-------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Renders User Interface components
	 * */
	private void addComponents(){
		

		if (!CollectionUtils.isEmpty(assetTypeCustomAttribute.getAllowedLookupValues())){
			lookupValues = new LinkedHashSet<CustomAttributeLookupValue>(assetTypeCustomAttribute.getAllowedLookupValues());
		}
		valuesForm = new ValuesForm("valuesForm");
		add(valuesForm);
		editArea = new WebMarkupContainer("editArea");
		
		if (this.lookupValue == null){
			lookupValue = new CustomAttributeLookupValue();
			editForm  = new EditLookupValueForm("editForm", lookupValue);
		}//if
		else{
			isNewEntry = (lookupValue.getAttribute()==null);

			Fragment editAreaFragment = new Fragment("editArea", "editAreaFragment",
					AssetsPanelManageCustomAttributeLookupValues.this);
			AssetsPanelManageCustomAttributeLookupValues.this.add(editAreaFragment);
			editForm = new EditLookupValueForm("editForm",lookupValue );
			editAreaFragment.add(editForm);
			editAreaFragment.add(new Label("editLabel", "EDIT LABEL"));
		}//else
		add(editArea);
		editArea.add(editForm);
		
		

		Link add = new Link("add"){
			public void onClick() {
				AssetsPanelManageCustomAttributeLookupValues.this.remove(editArea);
				Fragment editAreaFragment = new Fragment("editArea", "editAreaFragment",
						AssetsPanelManageCustomAttributeLookupValues.this);
				AssetsPanelManageCustomAttributeLookupValues.this.add(editAreaFragment);
				editForm = new EditLookupValueForm("editForm",lookupValue );
				editAreaFragment.add(editForm);
				editAreaFragment.add(new Label("editLabel", "EDIT LABEL"));
			}//onClick
		};
		add(add);

	}
	
	
	/**
	 *Form for editing lookup values show order  
	 **/
	
	private class ValuesForm extends Form{
		private static final long serialVersionUID = 1L;
		public ValuesForm(String id) {
			super(id);
			dataProvider = new DataProviderAssetTypeLookupValue(assetTypeCustomAttribute);
			dataView =new DataViewAssetTypeLookupValue("dataView", dataProvider);
			add(dataView);
		}
		@Override
		protected void onSubmit() {
			
		}
	}
	
	
	/**
	 * Form for editing lookup values
	 * */
	
	private class EditLookupValueForm extends Form{
		
		private static final long serialVersionUID = 1L;
		private CustomAttributeLookupValue lookupValue;
		
		
		public EditLookupValueForm(String id, final CustomAttributeLookupValue lookUpValue) {
			super(id);
			this.lookupValue = lookUpValue;
			final TextField newValue = new TextField("value", new PropertyModel(this.lookupValue, "value"));
			// 
			newValue.setRequired(true);
			newValue.add(new UniqueInputValidator(lookupValues));
			add(newValue);
			
			Button btnSubmit = new Button("btnSubmit"){
				private static final long serialVersionUID = 1L;

				public void onSubmit() {
					if (isNewEntry){
						lookupValue.setAttribute(assetTypeCustomAttribute);
						lookupValue.setShowOrder(lookupValues.size()+1);
						lookupValue.setValue((String)newValue.getModelObject());
					}
					else{
						lookupValue.setValue((String)newValue.getModelObject());
					}
					dataProvider.getAssetTypeCustomAttribute().getAllowedLookupValues().add(lookupValue);
					// it's
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
											return new AssetPanelManageCustomAttributes(getBreadCrumbModel().getActive().getComponent().getId(), getBreadCrumbModel(), assetTypeCustomAttribute, null, false);
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

							return new AssetPanelManageCustomAttributes(breadCrumbModel.getActive().getComponent().getId(), breadCrumbModel, AssetsPanelManageCustomAttributeLookupValues.this.assetTypeCustomAttribute, lookupValue, false);
						}
					});
					
				}//onSubmit
			};
			add(btnCancel);
			
		}//EditForm
	} //EditForm

}//AssetCustomAttributeLookupValuesFormPanel