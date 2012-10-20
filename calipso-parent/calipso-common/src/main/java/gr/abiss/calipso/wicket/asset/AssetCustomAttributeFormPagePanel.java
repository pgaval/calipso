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

import java.util.HashMap;
import java.util.Map;

import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.domain.Language;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.CalipsoApplication;
import gr.abiss.calipso.wicket.CalipsoFeedbackMessageFilter;
import gr.abiss.calipso.wicket.ConfirmPanel;
import gr.abiss.calipso.wicket.customattrs.CustomAttributeUtils;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 * @author marcello
 * 
 * For Custom Attribute Creation / Editing / Deletion
 * 
 * 
 */
public class AssetCustomAttributeFormPagePanel extends BasePanel {

	private static final Logger logger = Logger.getLogger(AssetCustomAttributeFormPagePanel.class);
	private AssetTypeCustomAttribute assetTypeCustomAttribute;
	private CustomAttributeLookupValue lookupValue;
	//private AssetCustomAttributeLookupValuesFormPanel assetCustomAttributeLookupValuesFormPanel = null;
	private boolean canBeDeleted;
	private boolean isEdit;
	private AssetType referenceAssetType = null;

	private Map<String, String> textAreaOptions;
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Constructors //
	/////////////////

	/**
	 * Constructor for Custom Attribute creation.AssetType
	 * 
	 * */
	public AssetCustomAttributeFormPagePanel(String id, IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);
		//logger.info("AssetCustomAttributeFormPagePanel(String id, IBreadCrumbModel breadCrumbModel)");
		this.assetTypeCustomAttribute = new AssetTypeCustomAttribute();
		this.assetTypeCustomAttribute.setActive(true);
		this.assetTypeCustomAttribute.setMandatory(true);
		this.referenceAssetType = null;

		//Oxymoron but useful for creation. Otherwise, the mandatory Asset Type will be not enabled.
		this.canBeDeleted = true;
		this.isEdit = false;

		setupVisuals();
		addComponents();
	}//AssetCustomAttributeFormPage
	
	//-------------------------------------------------------------------------------------------------------------
	
	/**
	 * Constructor for Custom Attribute editing.
	 * The given custom attribute will be reloaded from database. 
	 * 
	 * @param previousPage Previous page. Useful for cancel.
	 * @param assetTypeCustomAttribute The custom attribute for editing
	 * 
	 * */
	public AssetCustomAttributeFormPagePanel(String id, IBreadCrumbModel breadCrumbModel, AssetTypeCustomAttribute attr) {
		super(id, breadCrumbModel);
		//logger.info("AssetCustomAttributeFormPagePanel(String id, IBreadCrumbModel breadCrumbModel, AssetTypeCustomAttribute attr)");
		//logger.info("attr: "+attr);
		//logger.info("this.assetTypeCustomAttribute: "+this.assetTypeCustomAttribute);
		
		if(this.assetTypeCustomAttribute == null){
			if(attr.getId() == null){
				this.assetTypeCustomAttribute = attr;
			}
			else{
				this.assetTypeCustomAttribute = getCalipso().loadAssetTypeCustomAttribute(attr.getId());
			}
		}

		//logger.info("this.assetTypeCustomAttribute: "+this.assetTypeCustomAttribute);
		this.referenceAssetType = null;
		this.canBeDeleted = getCalipso().loadCountAssetsForCustomAttribute(this.assetTypeCustomAttribute)==0;		
		this.isEdit = true;

		setupVisuals();
		addComponents();
	}//AssetCustomAttributeFormPage
	
	//-------------------------------------------------------------------------------------------------------------

	/**
	 * Constructor for Custom Attribute editing.
	 * The given custom attribute will not be reloaded from database.
	 * 
	 * @param previousPage Previous page. Useful for cancel.
	 * @param assetTypeCustomAttribute The custom attribute for editing
	 * @param isReload If true the given custom attribute will be not reloaded from database.  
	 * 
	public AssetCustomAttributeFormPagePanel(String id, IBreadCrumbModel breadCrumbModel, AssetTypeCustomAttribute assetTypeCustomAttribute, boolean isReload) {
		super(id, breadCrumbModel);
		this.assetTypeCustomAttribute = assetTypeCustomAttribute;
		this.canBeDeleted = false;
		this.referenceAssetType = null;

		if (!isReload){
			this.assetTypeCustomAttribute = getCalipso().loadAssetTypeCustomAttribute(this.assetTypeCustomAttribute.getId());
			this.canBeDeleted = getCalipso().loadCountAssetsForCustomAttribute(this.assetTypeCustomAttribute)==0;
		}//if

		this.isEdit = true;

		setupVisuals();
		addComponents();
	}//AssetCustomAttributeFormPage
	
	 * */
	//-------------------------------------------------------------------------------------------------------------
	
	/**
	 * Constructor for Custom Attribute editing in case that the custom attribute in question is from type "Drop Down List".
	 * 
	 * @param previousPage Previous page. Useful for cancel.
	 * @param assetTypeCustomAttribute The custom attribute for editing
	 * @param lookupValue the lookup value for editing
	 * */
	public AssetCustomAttributeFormPagePanel(String id, IBreadCrumbModel breadCrumbModel, AssetTypeCustomAttribute attr, CustomAttributeLookupValue lookupValue) {
		super(id, breadCrumbModel);
		//logger.info("AssetCustomAttributeFormPagePanel(String id, IBreadCrumbModel breadCrumbModel, AssetTypeCustomAttribute attr, CustomAttributeLookupValue lookupValue)");
		if(this.assetTypeCustomAttribute == null){
			if(attr.getId() == null){
				this.assetTypeCustomAttribute = attr;
			}
			else{
				this.assetTypeCustomAttribute = getCalipso().loadAssetTypeCustomAttribute(attr.getId());
			}
		}
		//logger.info("Constructor this.assetTypeCustomAttribute: "+this.assetTypeCustomAttribute);
		//logger.info("Constructor lookupVelue: "+lookupValue);
		this.lookupValue = lookupValue;
		this.referenceAssetType = null;
		isEdit = true;
		
		setupVisuals();
		addComponents();
	}//AssetCustomAttributeFormPage

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
	private void setupVisuals(){
        //edit or new label
        add(new Label("label", isEdit?localize("asset.customAttributes.edit"):localize("asset.customAttributes.createNewAttribute")));
        //make cancel button
		getBackLinkPanel().makeCancel();
		//Highlight this Custom Attribute in the previous page
		setHighlightOnPreviousPage(assetTypeCustomAttribute.getId());		
	}
	
    private void setHighlightOnPreviousPage(Long selectedAttributeId){
		// get previous page. We use the active one as the previous because
    	//when this page is created it's not yet activated.
		BreadCrumbPanel previous = (BreadCrumbPanel) getBreadCrumbModel().getActive();

        if (previous instanceof AssetCustomAttributesPanel) {
        	((AssetCustomAttributesPanel) previous).setSelectedAttributeId(selectedAttributeId);
        }          	
    }
	
	/**
	 * Renders User Interface components
	 * */
	private void addComponents(){	        

		//logger.info("AssetCustomAttributeFormPagePanel.addComponents, this.assetTypeCustomAttribute: "+this.assetTypeCustomAttribute);
		deleteLink();
		
		add(new AssetCustomAttributeForm("form", this.assetTypeCustomAttribute));
		
		/*
		//Select List
		if (this.assetTypeCustomAttribute!=null && this.assetTypeCustomAttribute.getFormType()!=null && this.assetTypeCustomAttribute.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)){			
			if (this.lookupValue==null){
				assetCustomAttributeLookupValuesFormPanel = new AssetCustomAttributeLookupValuesFormPanel("lookupValuesPanel", getBreadCrumbModel(),  this.assetTypeCustomAttribute);
			}//if
			else{
				assetCustomAttributeLookupValuesFormPanel = new AssetCustomAttributeLookupValuesFormPanel("lookupValuesPanel", getBreadCrumbModel(), this.assetTypeCustomAttribute, this.lookupValue);
			}//else
			
			assetCustomAttributeLookupValuesFormPanel.setReferenceAssetType(referenceAssetType);
			add(assetCustomAttributeLookupValuesFormPanel);
			
		}//if
		else{
			add(new WebMarkupContainer("lookupValuesPanel").setVisible(false));
		}//else
		*/
	}//addComponents
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Wicket Form
	 *  
	 */	
	private class AssetCustomAttributeForm extends Form{
		private AssetTypeCustomAttribute customAttribute;
		private CalipsoFeedbackMessageFilter filter;

		private Map<String, String> textAreaOptions = new HashMap<String,String>();
		
		public AssetCustomAttributeForm(String id, AssetTypeCustomAttribute assetTypeCustomAttribute){
			super(id);
			if(assetTypeCustomAttribute == null){
				throw new RuntimeException("AssetTypeCustomAttribute canot be null");
			}
            this.customAttribute = assetTypeCustomAttribute;
			// preload options
			CustomAttributeUtils.preloadExistingLookupValues(getCalipso(), customAttribute, textAreaOptions);
            FeedbackPanel feedback = new FeedbackPanel("feedback");
            filter = new CalipsoFeedbackMessageFilter();
            feedback.setFilter(filter);
            add(feedback);

			CompoundPropertyModel model = new CompoundPropertyModel(customAttribute);
			setModel(model);
			
			add(new AssetCustomAttributeFormPanel("customAttributeFormPanel", model, true, canBeDeleted, textAreaOptions));
		}//AssetCustomAttributeForm

		//----------------------------------------------------------------------------------------------------------
		/*
		@Override
		protected void validate() {
		    filter.reset();
		    super.validate();
		}//validate	
		*/
		//----------------------------------------------------------------------------------------------------------

		protected void onSubmit() {
			
			CustomAttributeUtils.parseOptionsIntoAttribute(textAreaOptions, assetTypeCustomAttribute, getCalipso().getSupportedLanguages());
			//if is created and it is type FORM_TYPE_SELECT, don't go to the previous page but edit this asset
			/*if(isEdit == false && customAttribute.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)){
				activate(new IBreadCrumbPanelFactory(){
					public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
						//Remove last breadcrumb participant
						BreadCrumbUtils.removeActiveBreadCrumbPanel(getBreadCrumbModel());
						AssetCustomAttributeFormPagePanel assetCustomAttributeFormPagePanel = new AssetCustomAttributeFormPagePanel(id, breadCrumbModel, customAttribute, true);
						if (referenceAssetType!=null){
							assetCustomAttributeFormPagePanel.setReferenceAssetType(referenceAssetType);
						}
						
						return assetCustomAttributeFormPagePanel;
					}
				});
				
			}
			else{*/
				getCalipso().store(customAttribute);
				/*
				 * ervis
				 *  We want a connection between Form
				 */
				setHighlightOnPreviousPage(customAttribute.getId());				
				//else go to previous page
				activate(new IBreadCrumbPanelFactory(){
					public BreadCrumbPanel create(final String id, final IBreadCrumbModel breadCrumbModel) {
						BreadCrumbUtils.popPanels(2, breadCrumbModel);
						AssetCustomAttributesPanel assetCustomAttributesPanel;
						
						if (referenceAssetType!=null){
							assetCustomAttributesPanel = new AssetCustomAttributesPanel(id, breadCrumbModel, referenceAssetType);
						}//if
						else{
							assetCustomAttributesPanel = new AssetCustomAttributesPanel(id, breadCrumbModel);
						}//else
						assetCustomAttributesPanel.setSelectedAttributeId(customAttribute.getId());

						return assetCustomAttributesPanel;
					}
				});
			//}
		}//onSubmit

	}//AssetCustomAttributeForm
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	public String getTitle() {
		if (isEdit){
			return localize("asset.customAttributes.edit");
		}//if

		return localize("asset.customAttributes.createNewAttribute");
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////

	public AssetCustomAttributeFormPagePanel setReferenceAssetType(AssetType referenceAssetType) {
		this.referenceAssetType = referenceAssetType;
		/*
		if(assetCustomAttributeLookupValuesFormPanel != null){
			assetCustomAttributeLookupValuesFormPanel.setReferenceAssetType(referenceAssetType);
		}*/
		
		return this;
	}

	//---------------------------------------------------------------------------------------------

	public AssetType getReferenceAssetType(){
		return this.referenceAssetType;
	}
	
//////////////////////////////////////////////////////////////////////////////////////
	private void deleteLink(){
		//hide link if: admin, user==null, try to delete self or new user
		if( canBeDeleted == false || assetTypeCustomAttribute == null || assetTypeCustomAttribute.getId() == null
				|| !Boolean.parseBoolean(((CalipsoApplication)Application.get()). getCalipsoPropertyValue("allow.delete.assetTypeCustomAttribute"))){
			add(new WebMarkupContainer("delete").setVisible(false));
		}
		else{//if edit
	    	add(new Link("delete") {		
				@Override
				public void onClick() {
					
					final String line1 = localize("asset.customAttributes.deleteConfirmMessage");
					final String line2 = new String("\"" + localize(assetTypeCustomAttribute.getNameTranslationResourceKey()) + "\"");
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
				}//onclick
			});//add, new Link
		}
	}
}