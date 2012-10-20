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

import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.IconFormPanel;
import gr.abiss.calipso.wicket.CalipsoFeedbackMessageFilter;


/**
 * @author marcello
 * 
 * Creation and Editing for Asset Types.
 * It is constituted of:
 *  > a property set of Asset Type (description etc.)
 *  > a list of the connected (to this asset type) custom attributes
 *  
 *  Gives user the ability to add and remove connected custom attributes to this Asset Type as well.
 *   
 *  Includes:
 *   > "AssetTypeFormPanel": Renders properties and connected attributes list
 */

public class AssetTypeFormPagePanel extends BasePanel {
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(AssetTypeFormPagePanel.class);
	private AssetType assetType;
	private boolean isEdit;
	
    /**
     * For Asset Type editing
     * 
     *  @param id Component Id
     *  @param breadCrumbModel Breadcrump Model Reference
     *  @param assetType Asset Type instance for editing 
     * */
	public AssetTypeFormPagePanel(String id, IBreadCrumbModel breadCrumbModel, AssetType assetType) {
		super(id, breadCrumbModel);
		this.assetType = assetType;
		this.isEdit = true;
		
		setupVisuals();
		addComponents();
	}

	//----------------------------------------------------------
	
	/**
	 * For Asset Type Creation
	 * 
	 * @param id Component Id
	 * @param breadCrumbModel Breadcrump Model Reference
	 * 
	 * */
	
	public AssetTypeFormPagePanel(String id, IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);
		this.assetType = new AssetType();
		this.isEdit = false;
		
		setupVisuals();
		addComponents();
	}
	
	//----------------------------------------------------------

	/**
	 * Adds UI Components
	 * */
	private void addComponents(){
		
        add(new Label("label", isEdit?localize("asset.assetTypes.edit"):localize("asset.assetTypes.new")));
        
		add(new AssetTypeForm("form", this.assetType, this.isEdit));
	}//addComponents
	
	//----------------------------------------------------------

	/**
	 * Breadcrump path title
	 *  
	 * */
	public String getTitle() {
		if (this.isEdit){
			return localize("asset.assetTypes.edit");
		}
		return localize("asset.assetTypes.new");
	}
	
	private void setupVisuals(){
		getBackLinkPanel().makeCancel();
		setHighlightOnPreviousPage(assetType.getId());		
	}
	    
	//try set selected Asset Type Id on previous AssetTypesPanel class
	//can add more classes
    public void setHighlightOnPreviousPage(long selectedAssetTypeId){
    	// get previous page. We use the active one as the previous because
    	//when this page is created it's not yet activated.
		BreadCrumbPanel previous = (BreadCrumbPanel) getBreadCrumbModel().getActive();

		//try for AssetTypesPanel
        if (previous instanceof AssetTypesPanel) {
        	((AssetTypesPanel) previous).setSelectedAssetTypeId(selectedAssetTypeId);
        }          	
    }

	//////////////////////////////////////////////////////////////////
	
	/**
	 * Form
	 * */
	
	private class AssetTypeForm extends Form {
		private CalipsoFeedbackMessageFilter filter;
		private AssetType assetType;
		private boolean isEdit;
		private AssetTypeFormPanel assetTypeFormPanel;
		private IconFormPanel iconFormPanel;
		
		public AssetTypeForm(String id, AssetType assetType, boolean isEdit) {
			super(id);
			
            FeedbackPanel feedback = new FeedbackPanel("feedback");
            Label legendLabel;

            filter = new CalipsoFeedbackMessageFilter();
            feedback.setFilter(filter);
            add(feedback);
            
            iconFormPanel = new IconFormPanel("iconForm", assetType);
            add(iconFormPanel);
			
			this.assetType = assetType;
			this.isEdit = isEdit;
			
			CompoundPropertyModel model = new CompoundPropertyModel(this.assetType);
			setModel(model);

			if (isEdit){
				assetTypeFormPanel = new AssetTypeFormPanel("AssetTypeFormPanel", getBreadCrumbModel(), this.assetType, model); 
				add(assetTypeFormPanel);
			}//if
			else{
				assetTypeFormPanel = new AssetTypeFormPanel("AssetTypeFormPanel", getBreadCrumbModel(), model); 
				add(assetTypeFormPanel);
			}//else
		}//AssetTypeForm
		
		//----------------------------------------------------------
/*
		@Override
		protected void validate() {
		    filter.reset();
		    super.validate();
		}//validate
*/
		@Override
		protected void onSubmit() {
			// need this to properly inform relationships
			// TODO: we need to set all similar name assignments to 
			// the installation default locale
			this.assetType.setName(this.assetType.getNameTranslations().get("el"));
			logger.debug("Saving asset type with custom attributes: "+assetType.getAllowedCustomAttributes());
			getCalipso().storeAssetType(this.assetType);
			
			iconFormPanel.onSubmit();
			
			if(isEdit) {//if edited, return to previous page
				activate(new IBreadCrumbPanelFactory(){
					public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
						BreadCrumbUtils.popPanels(2, breadCrumbModel);
						AssetTypesPanel assetTypesPanel = new AssetTypesPanel(id, breadCrumbModel);
						assetTypesPanel.setSelectedAssetTypeId(assetType.getId());
						return assetTypesPanel;
					}
				});
			}
			else{//if is created, don't go to the previous page but edit this assetType (for custom attributes)
				//remove this page that is for creation and use the edit one instead
				BreadCrumbUtils.removeActiveBreadCrumbPanel(getBreadCrumbModel());
				activate(new IBreadCrumbPanelFactory(){
					public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
						return new AssetTypeFormPagePanel(id, breadCrumbModel, AssetTypeFormPagePanel.this.assetType);
					}
				});
			}
		}
	}
}
