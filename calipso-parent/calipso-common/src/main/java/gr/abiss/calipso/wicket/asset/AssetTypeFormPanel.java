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
import gr.abiss.calipso.domain.Language;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.ErrorHighlighter;
import gr.abiss.calipso.wicket.MandatoryPanel;
import gr.abiss.calipso.wicket.components.validators.NonDuplicateInputValidator;
import gr.abiss.calipso.wicket.components.validators.UniqueInputValidator;

import java.util.ArrayList;
import java.util.List;


import org.apache.commons.collections.MapUtils;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

/**
 * @author marcello
 * 
 * Renders properties and connected attributes list of an Asset Type
 * 
 */

public class AssetTypeFormPanel extends BasePanel {
	private boolean isEdit;
	private AssetType assetType;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Edit Asset Type
	 * 
	 * @param id Component Id
	 * @param breadCrumbModel Breadcrumb Model Reference
	 * @param assetType Asset Type instance for editing
	 * @param model model object
	 * */

	public AssetTypeFormPanel(String id, IBreadCrumbModel breadCrumbModel, AssetType assetType, CompoundPropertyModel model) {
		super(id, breadCrumbModel);
		
		List<AssetTypeCustomAttribute> customAttributes;
		
		if (assetType.getAllowedCustomAttributes() !=null){
			customAttributes = new ArrayList<AssetTypeCustomAttribute>(assetType.getAllowedCustomAttributes()); 
		}//if
		else{
			customAttributes = new ArrayList<AssetTypeCustomAttribute>();
		}//else

		isEdit = true;
		this.assetType = assetType;
		addComponents(customAttributes, model);

	}//AssetTypeFormPanel
	
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * Create Asset Type 
	 * 
	 * @param id Component Id
	 * @param breadCrumbModel Breadcrump Model Reference
	 * @param model model object
	 * 
	 * */
	public AssetTypeFormPanel(String id, IBreadCrumbModel breadCrumbModel, CompoundPropertyModel model){
		super(id, breadCrumbModel);
		isEdit = false;
		this.assetType = new AssetType();
		addComponents(null, model);
	}//AssetTypeFormPanel
	
	//------------------------------------------------------------------------------------------------------------------
	
	/**
	 * Renders UI Components
	 * */
	private void addComponents(final List<AssetTypeCustomAttribute> assetTypeCustomAttributes, final CompoundPropertyModel model){
		//Mandatory mark. red asterisk (*)
		add(new MandatoryPanel("mandatoryPanel"));

		//name
		/*
		TextField name = new TextField("name");
		name.setRequired(true);
		name.add(new ErrorHighlighter());
		add(name);
		model.bind(name);
		//form label for name
		name.setLabel(new ResourceModel("asset.assetTypes.description"));
		add(new SimpleFormComponentLabel("textLabel", name));
		 */
		if(MapUtils.isEmpty(this.assetType.getNameTranslations())){
			this.assetType.setNameTranslations(getCalipso().getNameTranslations(this.assetType));	
		}
		// TODO: switch to space or spacegroup languages when we move asset type creation context there
		add(new ListView("nameTranslations", getCalipso().getSupportedLanguages()){
			protected void populateItem(ListItem listItem) {
				Language language = (Language) listItem.getModelObject();
				TextField description = new TextField("name");
				// name translations are required.
				description.setRequired(true);
				description.add(new ErrorHighlighter());
				listItem.add(description);
				description.setModel(new PropertyModel(assetType, "nameTranslations["+language.getId()+"]"));
				//model.bind(description, "nameTranslations["+language.getId()+"]");
				// form label for name
				description.setLabel(new ResourceModel("language."+language.getId()));
				listItem.add(new SimpleFormComponentLabel("languageLabel", description));
			}
		});
		
		WebMarkupContainer customAttributes = new WebMarkupContainer("customAttributes");

		//Add new attribute
		Link addNewAttribute = new Link("add"){
			public void onClick() {
				activate(new IBreadCrumbPanelFactory(){
					public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
						return new AssetCustomAttributesPanel(getBreadCrumbModel().getActive().getComponent().getId(), getBreadCrumbModel(), AssetTypeFormPanel.this.assetType);
					}
				});
				
			}//onClick
		};

		customAttributes.add(addNewAttribute);

		final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
		
		//customAttributes
		ListView listView = new ListView("customAttributesList", assetTypeCustomAttributes) {
			protected void populateItem(ListItem listItem) {
        		if (listItem.getIndex() % 2 !=0){
        			listItem.add(sam);
        		}//if

				final AssetTypeCustomAttribute assetTypeCustomAttribute = (AssetTypeCustomAttribute)listItem.getModelObject();
				
				boolean canDeleteCustomAttribute = false;//getJtrac().loadCountForAssetTypeAndCustomAttribute(assetType, assetTypeCustomAttribute);

				//Name
				Label name = new Label("name", localize(assetTypeCustomAttribute.getNameTranslationResourceKey()));
				listItem.add(name);
				
				//Type
				Label type = new Label("type", localize("asset.attributeType_" + assetTypeCustomAttribute.getFormType()));
				listItem.add(type);
				
				//Active
				Label active = new Label("active", assetTypeCustomAttribute.isActive()?localize("yes"):localize("no"));
				listItem.add(active);
				
				//Mandatory
				Label mandatory = new Label("mandatory",  assetTypeCustomAttribute.isMandatory()?localize("yes"):localize("no"));
				listItem.add(mandatory);
				
				//Remove
				Link remove = new Link("remove"){
					public void onClick() {
						assetTypeCustomAttributes.remove(assetTypeCustomAttribute);
						assetType.remove(assetTypeCustomAttribute);
						
						BreadCrumbUtils.removeActiveBreadCrumbPanel(getBreadCrumbModel());

						activate(new IBreadCrumbPanelFactory(){
							public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
								return new AssetTypeFormPagePanel(getBreadCrumbModel().getActive().getComponent().getId(), getBreadCrumbModel(), assetType);
							}//create
						});
					}//onClick
				};
				listItem.add(remove);
				remove.setVisible(canDeleteCustomAttribute);
			}//populateItem
		};//ListView

		add(customAttributes.add(listView).setVisible(isEdit));
		
	}//addComponents

	//-----------------------------------------------------------------------------------------------------
	
	public AssetType getAssetType() {
		return this.assetType;
	}
}//AssetTypeFormPanel