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

import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.dto.AssetSearch;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.SpaceListPanel;
import gr.abiss.calipso.wicket.hlpcls.ExpandAssetSearchLink;
import gr.abiss.calipso.wicket.yui.YuiCalendar;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

/**
 * @author marcello
 */
public class AssetSpacePanel extends BasePanel {
	
	private static final long serialVersionUID = 1L;
	
	private Asset asset;
	private AssetSearch assetSearch;
	private Map<AssetTypeCustomAttribute, String> attributeValues;
	private AssetsListPanel assetsListPanel;
	
	private AssetSearchForm searchForm = null;
	private WebMarkupContainer searchContainer;
	private WebMarkupContainer searchPlaceHolder;
	private boolean isSearchOpen;
	private boolean simple = false;

	////////////////////////////////////////////////////////////////
	
	public AssetSpacePanel setSelectedAssetId(long selectedAssetId) {
    	assetsListPanel.setSelectedAssetId(selectedAssetId);
        return this;
    }	
	
	//---------------------------------------------------------------------------------------------
	
	public AssetSpacePanel(String id, IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);
		this.asset = new Asset();
		this.asset.setSpace(getCurrentSpace());
		this.assetSearch = new AssetSearch(this.asset, this);
		this.isSearchOpen = false;
		
		setupVisuals();

		addComponents();
	}
	
	@Deprecated
	public AssetSpacePanel(String id, IBreadCrumbModel breadCrumbModel, AssetSearch assetSearch) {
		super(id, breadCrumbModel);
		this.assetSearch = assetSearch;
		this.asset = assetSearch.getSearchObject();
		if(logger.isDebugEnabled()){
			logger.debug("Loading current space : [SUCCESSFUL]" + (getCurrentSpace() != null));
		}
		this.asset.setSpace(getCurrentSpace());
		this.isSearchOpen = true;
		
		setupVisuals();

		addComponents();
	}
	
	public AssetSpacePanel(String id, IBreadCrumbModel breadCrumbModel, AssetSearch assetSearch, Long spaceId) {
		this(id, breadCrumbModel, assetSearch, spaceId, false);
	}
	public AssetSpacePanel(String id, IBreadCrumbModel breadCrumbModel, AssetSearch assetSearch, Long spaceId, boolean simple) {
		super(id, breadCrumbModel);
		// used for popups etc
		this.simple = simple;
		this.assetSearch = assetSearch;
		this.asset = assetSearch.getSearchObject();
		if(logger.isDebugEnabled()){
			logger.debug("Loading current space : [SUCCESSFUL]" + (getCurrentSpace() != null));
		}
		Space space = getCalipso().loadSpace(spaceId);
		setCurrentSpace(space);
		this.asset.setSpace(space);
		this.isSearchOpen = true;
		setupVisuals();
		addComponents();
	}
	
	//--------------------------------------------------------------

	public AssetSpacePanel(String id, IBreadCrumbModel breadCrumbModel, AssetSearch assetSearch, Map<AssetTypeCustomAttribute, String> attributeValues) {
		super(id, breadCrumbModel);
		this.assetSearch = assetSearch;
		this.asset = assetSearch.getSearchObject();
		//this.asset.setSpace(getCurrentSpace());
		this.isSearchOpen = true;
		this.attributeValues = attributeValues;
		
		setupVisuals();

		addComponents();
	}

	
	////////////////////////////////////////////////////////////////
	
	private void addComponents(){
		createAsset();
		searchAsset();
		
		assetsListPanel = new AssetsListPanel("assetsListPanel", getBreadCrumbModel(), this.assetSearch);
		add(assetsListPanel);
	}

	
	//--------------------------------------------------------------
	
	private void setupVisuals() {
		//refresh header
		refreshParentPageHeader();
		
	    //highlight this asset's spaceId on previous (the active one) page =======================
		//when this object is created it is not in breadcrumb, so the previous page is the active one
		BreadCrumbPanel previous = (BreadCrumbPanel) getBreadCrumbModel().getActive();
		
		if (previous instanceof SpaceListPanel && asset != null && asset.getSpace() != null) {
			((SpaceListPanel) previous).setSelectedSpaceId(asset.getSpace().getId());
		}
	}
	
	/**
	 * Link to Asset Creation
	 * */
	private void createAsset(){
		Link newAsset = new Link("new") {
           
			private static final long serialVersionUID = 1L;

			public void onClick() {
    			activate(new IBreadCrumbPanelFactory(){
    				
					private static final long serialVersionUID = 1L;

					public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
    					return new AssetFormPagePanel(id, breadCrumbModel);
    				}
    			});
            	
            }//onClick
        };

        add(newAsset);

	}//createAsset
	
	//---------------------------------------------------------------------------------------------
	
	private void searchAsset(){
		
		//Container ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		searchContainer = new WebMarkupContainer("searchContainer");
		add(searchContainer);

		//Place holder ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		searchPlaceHolder = new WebMarkupContainer("searchPlaceHolder");
		searchContainer.add(searchPlaceHolder);
		
		//Page is (re)loaded, and the search panel is open ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		if (isSearchOpen){
			searchForm = new AssetSearchForm("form", assetSearch);
			searchContainer.add(searchPlaceHolder);
			searchPlaceHolder.add(searchForm);
		}
		//Page is (re)loaded, and the search panel is closed ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		else{
			//Set place holder to not visible
			searchPlaceHolder.setVisible(false);
		}
		
		//setOutputMarkupId, needed for ajax ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		searchContainer.setOutputMarkupId(true);
		
		//open / close search parameters handling via ajax ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		ExpandAssetSearchLink search = new ExpandAssetSearchLink("search",
				searchContainer, searchContainer, searchPlaceHolder, new AssetSearchForm("form", assetSearch),
						isSearchOpen){
							@Override
							public void onLinkClick() {
								AssetSpacePanel.this.isSearchOpen = this.isOpen();
							}
		};
		add(search);
		
	}//searchAsset
	
	@Override
	public String getTitle() {
		
		return localize("asset.title");
	}
	///////////////////////////////////////////////////////////////////////////
	
	/////////////////
	// Search Form //
	/////////////////
	
    /**
     * wicket form for asset search
     */    
    private class AssetSearchForm extends Form {
    	AssetSearch searchAsset;
    	AssetFormCustomAttributePanel customAttributesPanel;
    	WebMarkupContainer panelContainer;
    	
    	AssetSearchForm(String id, AssetSearch searchAsset){
			super(id);
			this.searchAsset=searchAsset;
			refreshParentMenu(getBreadCrumbModel());
			CompoundPropertyModel model = new CompoundPropertyModel(searchAsset);
			setModel(model);
			
			//input for inventory code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			TextField inventoryCode = new TextField("asset.inventoryCode");
			add(inventoryCode);
			//form label
			inventoryCode.setLabel(new ResourceModel("asset.inventoryCode"));
			add(new SimpleFormComponentLabel("assetInventoryCodeLabel", inventoryCode));

			//dates from-to StartDate ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			YuiCalendar fromStartDate = new YuiCalendar("startDateFrom", new PropertyModel(model, "startDateFrom"), false);
			add(fromStartDate);
			YuiCalendar toStartDate = new YuiCalendar("startDateTo", new PropertyModel(model, "startDateTo"), false);
			add(toStartDate);
			//form labels
			fromStartDate.setLabel(new ResourceModel("asset.fromStartDate"));
			add(new SimpleFormComponentLabel("startDateFromLabel", fromStartDate));
			
			toStartDate.setLabel(new ResourceModel("asset.toStartDate"));
			add(new SimpleFormComponentLabel("startDateToLabel", toStartDate));
			
			//dates from-to EndDate ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			YuiCalendar fromEndDate = new YuiCalendar("endDateFrom", new PropertyModel(model, "endDateFrom"), false);
			add(fromEndDate);
			YuiCalendar toEndDate = new YuiCalendar("endDateTo", new PropertyModel(model, "endDateTo"), false);
			add(toEndDate);
			//form labels
			fromEndDate.setLabel(new ResourceModel("asset.fromEndDate"));
			add(new SimpleFormComponentLabel("endDateFromLabel", fromEndDate));

			toEndDate.setLabel(new ResourceModel("asset.toEndDate"));
			add(new SimpleFormComponentLabel("endDateToLabel", toEndDate));
			
			List<Space> spaceChoices = new LinkedList<Space>();
			final Space currentSpace = getCurrentSpace();
			// add "local space"
			if(currentSpace != null){
				spaceChoices.add(currentSpace);
			}
			// TODO: add <in space group> 
			//spaceChoices.add(Space.getAllSpacesDummy(localize("item_search_form.allSpaces")));
			spaceChoices.addAll(getCalipso().getVisibleAssetSpacesForSpace(currentSpace));
			assetSearch.setSearchableSpaces(spaceChoices);
			@SuppressWarnings("serial")
			DropDownChoice spaceChoice = new DropDownChoice("asset.space", spaceChoices, new IChoiceRenderer(){
				public Object getDisplayValue(Object object) {
					Space currentSpaceChoice = (Space) object;
					String spaceNameResourceKey = localize(currentSpaceChoice.getNameTranslationResourceKey());
					if(currentSpaceChoice.equals(currentSpace)){
						return spaceNameResourceKey+" "+localize("item_search_form.local");
					}
					else if(currentSpaceChoice.getSpaceGroup() != null 
							&& currentSpace.getSpaceGroup() != null 
							&& currentSpaceChoice.equals(currentSpace.getSpaceGroup())){
						return spaceNameResourceKey+" "+localize("item_search_form.sameSpaceGroup");
					}
					else{
						return spaceNameResourceKey;
					}
				}

				public String getIdValue(Object object, int index) {
					return index+"";
				}
			}){
				/**
				 * @see org.apache.wicket.markup.html.form.DropDownChoice#wantOnSelectionChangedNotifications()
				 */
				@Override
				protected boolean wantOnSelectionChangedNotifications() {
					// TODO Auto-generated method stub
					return true;
				}
				/**
				 * @see org.apache.wicket.markup.html.form.DropDownChoice#onSelectionChanged(java.lang.Object)
				 */
				@Override
				protected void onSelectionChanged(Object newSelection) {
					// TODO Auto-generated method stub
					setModelObject(newSelection);
				}
			};
			spaceChoice.setNullValid(true);
			add(spaceChoice);
			//form label
			spaceChoice.setLabel(new ResourceModel("asset.space"));
			add(new SimpleFormComponentLabel("assetSpaceLabel", spaceChoice));
			
			//asset type drop-down menu ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			List<AssetType> assetTypes = getCalipso().findAllAssetTypes();
			
			final DropDownChoice assetTypeChoice = new DropDownChoice("asset.assetType", assetTypes, new IChoiceRenderer(){
                public Object getDisplayValue(Object o) {
                    return localize(((AssetType)o).getNameTranslationResourceKey());
                }
                public String getIdValue(Object o, int i) {
                    return ((AssetType)o).getName();
                }        
			}
			);
			
			assetTypeChoice.add(new AjaxFormComponentUpdatingBehavior ("onchange") {
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					AssetType assetType = (AssetType)assetTypeChoice.getModelObject();
					attributeValues = null;

					if (assetType!=null){
						if (panelContainer!=null){
							AssetSearchForm.this.remove(panelContainer);
						}//if
						else if (customAttributesPanel!=null){
							AssetSearchForm.this.remove(customAttributesPanel);
						}//if
						final AssetFormCustomAttributePanel customAttributesPanel = new AssetFormCustomAttributePanel("customAttributesPanel", getBreadCrumbModel(), assetType, attributeValues);
						AssetSearchForm.this.add(customAttributesPanel);
						AssetSearchForm.this.customAttributesPanel = customAttributesPanel;
					}//else
					else{
						if (customAttributesPanel!=null){
							AssetSearchForm.this.remove(customAttributesPanel);
							customAttributesPanel = null;
						}//if
						if (panelContainer==null){
							panelContainer = new WebMarkupContainer("customAttributesPanel");
						}//if
						AssetSearchForm.this.add(panelContainer);
						AssetSpacePanel.this.assetSearch.getAsset().setAssetType(null);
					}//else

					target.addComponent(AssetSearchForm.this);
				}
			});

			assetTypeChoice.setNullValid(true);
			add(assetTypeChoice);
			//form labels
			assetTypeChoice.setLabel(new ResourceModel("asset.assetType"));
			add(new SimpleFormComponentLabel("assetTypeLabel", assetTypeChoice));
			

			if (searchAsset.getAsset().getAssetType()==null){
				panelContainer = new WebMarkupContainer("customAttributesPanel");
				add (panelContainer);
			}//if
			else{
				customAttributesPanel = new AssetFormCustomAttributePanel("customAttributesPanel", getBreadCrumbModel(), searchAsset.getAsset().getAssetType(), attributeValues);
				add(customAttributesPanel);
			}//else
		}

    	//-----------------------------------------------------------------------------------------

    	@Override
        protected void onSubmit() {
    		
    		attributeValues = new LinkedHashMap<AssetTypeCustomAttribute, String>();

    		if (customAttributesPanel!=null){
	    		List<AttributeValue> attributeValueList = customAttributesPanel.getAttributeValueList();
	    		
	    		//Load Custom Attributes from selected Asset Type ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	    		// made allowed custom attributes non-lazy for asset type
	    		//SortedSet<AssetTypeCustomAttribute> allowedCustomAttributes = new TreeSet<AssetTypeCustomAttribute>(getJtrac().findAllAssetTypeCustomAttributesByAssetType(AssetSpacePanel.this.assetSearch.getAsset().getAssetType()));
	    		//AssetSpacePanel.this.assetSearch.getAsset().getAssetType().setAllowedCustomAttributes(allowedCustomAttributes);

	    		//Set Attribute values ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	    		if (attributeValueList!=null){
		    		for (int i=0; i<attributeValueList.size(); i++){
		    			AttributeValue attributeValue = attributeValueList.get(i);
		    			if (attributeValue.getAssetTypeCustomAttribute().getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_DATE)){
		    				YuiCalendar calendar = (YuiCalendar)attributeValue.getFormComponent();
		    				attributeValues.put(attributeValue.getAssetTypeCustomAttribute(), calendar.getDateValueAsString());
		    			}//if
		    			else{
		    				attributeValues.put(attributeValue.getAssetTypeCustomAttribute(), attributeValue.getFormComponent().getValue());
		    			}//else
		    		}//for
		    		AssetSpacePanel.this.assetSearch.setAttributeValues(attributeValues);
	    		}//if
    		}//if

        	activate(new IBreadCrumbPanelFactory(){
    			public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
    				//Remove last breadcrumb participant ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    				if (breadCrumbModel.allBreadCrumbParticipants().size()>0){
    					breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
    				}//if

    				return new AssetSpacePanel(getBreadCrumbModel().getActive().getComponent().getId(), getBreadCrumbModel(), AssetSpacePanel.this.assetSearch, AssetSpacePanel.this.attributeValues);
    			}
    		});
        }
    }        
}