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
import gr.abiss.calipso.domain.AssetTypeCustomAttributeSearch;
import gr.abiss.calipso.domain.CustomAttribute;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.CalipsoFeedbackMessageFilter;
import gr.abiss.calipso.wicket.hlpcls.ExpandAssetSearchLink;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

/**
 * @author marcello
 * 
 * Searches and lists custom attributes
 * 
 * It is constituted of:
 * > A search panel which gives the ability to search for custom attributes 
 *   after all its properties as search criteria.   
 * > A list of custom attributes with pagination ability.
 *   If page is loaded for first time, the list contains all custom attributes 
 *   stored in the repository.
 *   Otherwise contains custom attributes as a search result.  
 * 
 * Includes: 
 * > "AssetCustomAttributeFormPanel.java": Renders all fields of a given Custom attribute.
 * > "PaginationPanel.java": Abstract repeater that renders a list of objects and paginate it as well.  
 * 
 */

public class AssetCustomAttributesPanel extends BasePanel {

	private final CustomAttribute assetTypeCustomAttribute;
	private AssetType referenceAssetType;
	private final AssetTypeCustomAttributeSearch assetTypeCustomAttributeSearch;
	private final String id;
	private final IBreadCrumbModel breadCrumbModel;
	private Long selectedAttributeId;
	
	private AssetTypeCustomAttributeForm searchForm = null;
	private WebMarkupContainer searchContainer;
	private WebMarkupContainer searchPlaceHolder;
	private boolean isSearchOpen;

	//--------------------------------------------------------------------------------------------------------------

	/**
	 * For reload page for pagination
	 * */
	public AssetCustomAttributesPanel(String id, IBreadCrumbModel breadCrumbModel, AssetType referenceAssetType, AssetTypeCustomAttributeSearch assetTypeCustomAttributeSearch, boolean isSearchOpen) {
		super(id, breadCrumbModel);
		this.id = id;
		this.breadCrumbModel = breadCrumbModel;
		this.referenceAssetType = referenceAssetType;		
		this.assetTypeCustomAttributeSearch = assetTypeCustomAttributeSearch;
		this.assetTypeCustomAttribute = this.assetTypeCustomAttributeSearch.getSearchObject();
		this.isSearchOpen = isSearchOpen;
		
		addComponents();
	}//AssetCustomAttributesPage

	//--------------------------------------------------------------------------------------------------------------

	/**
	 * Usually for default call.
	 * No search parameters, lists all attributes. 
	 * */
	public AssetCustomAttributesPanel(String id, IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);
		this.id = id;
		this.breadCrumbModel = breadCrumbModel;		
		this.assetTypeCustomAttribute = new AssetTypeCustomAttribute();
		this.assetTypeCustomAttributeSearch = new AssetTypeCustomAttributeSearch(/*this.assetTypeCustomAttribute*/this);
		this.isSearchOpen = false;

		addComponents();

	}//AssetCustomAttributesPage

	//--------------------------------------------------------------------------------------------------------------

	/**
	 * For connecting to an asset type.
	 * */
	public AssetCustomAttributesPanel(String id, IBreadCrumbModel breadCrumbModel, AssetType referenceAssetType){
		super(id, breadCrumbModel);
		this.id = id;
		this.breadCrumbModel = breadCrumbModel;		
		this.referenceAssetType = referenceAssetType;
		this.assetTypeCustomAttribute = new AssetTypeCustomAttribute();
		this.assetTypeCustomAttributeSearch = new AssetTypeCustomAttributeSearch(/*this.assetTypeCustomAttribute*/this);
		this.isSearchOpen = false;

		addComponents();

	}//AssetCustomAttributesPage

	//--------------------------------------------------------------------------------------------------------------

	/**
	 * For search custom attributes.
	 * */
	public AssetCustomAttributesPanel(String id, IBreadCrumbModel breadCrumbModel, AssetTypeCustomAttributeSearch assetTypeCustomAttributeSearch, AssetType referenceAssetType){
		super(id, breadCrumbModel);
		this.id = id;
		this.breadCrumbModel = breadCrumbModel;		
		this.referenceAssetType = referenceAssetType;
		this.assetTypeCustomAttributeSearch = assetTypeCustomAttributeSearch;
		this.assetTypeCustomAttribute = assetTypeCustomAttributeSearch.getSearchObject();
		this.isSearchOpen = true;

		addComponents();
		
	}//AssetCustomAttributesPage
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Creates UI components
	 * */
	private void addComponents(){
		createNewCustomAttributes();
		searchAssetCustomAttributes();
		listAttributes();
	}
	
	//--------------------------------------------------------------------------------------------------------------

	/**
	 * Renders link that drives to page for new attribute creation.
	 * */
	private void createNewCustomAttributes(){
		Link newCustomAttribute = new Link("new") {
            @Override
			public void onClick() {
    			activate(new IBreadCrumbPanelFactory(){
    				@Override
					public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
    					AssetCustomAttributeFormPagePanel assetCustomAttributeFormPagePanel = new AssetCustomAttributeFormPagePanel(id, breadCrumbModel);
    					if (referenceAssetType!=null){
    						assetCustomAttributeFormPagePanel.setReferenceAssetType(referenceAssetType);
    					}
    					return assetCustomAttributeFormPagePanel;
    				}
    			});
            	
            }//onClick
        };
        
        add(newCustomAttribute);
		
	}//createNewCustomAttributes
	
	//--------------------------------------------------------------------------------------------------------------

	/**
	 * Renders search panel
	 * */
	private void searchAssetCustomAttributes(){
		// --- container -------------------------------------------
		searchContainer = new WebMarkupContainer("searchContainer");
		add(searchContainer);

		// -- place holder ----------------------------------------
		searchPlaceHolder = new WebMarkupContainer("searchPlaceHolder");
		searchContainer.add(searchPlaceHolder);
		
		// -- Page is (re)loaded, and the search panel is open ----
		if (isSearchOpen){
			searchForm = new AssetTypeCustomAttributeForm("form", assetTypeCustomAttributeSearch);
			searchContainer.add(searchPlaceHolder);
			searchPlaceHolder.add(searchForm);
		}
		// -- Page is (re)loaded, and the search panel is closed ----		
		else{
			//Set place holder to not visible
			searchPlaceHolder.setVisible(false);
		}
		
		//setOutputMarkupId, needed for ajax
		searchContainer.setOutputMarkupId(true);
		
		// -- open / close search parameters handling via ajax
		ExpandAssetSearchLink search = new ExpandAssetSearchLink("search",
				searchContainer, searchContainer, searchPlaceHolder, new AssetTypeCustomAttributeForm("form", assetTypeCustomAttributeSearch),
						isSearchOpen){
							@Override
							public void onLinkClick() {
								AssetCustomAttributesPanel.this.isSearchOpen = this.isOpen();
							}
		};
		add(search);		
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	/**
	 * Wicket Form for search 
	 * */
	private class AssetTypeCustomAttributeForm extends Form {
		private final CalipsoFeedbackMessageFilter filter;
		private final AssetTypeCustomAttributeSearch assetTypeCustomAttributeSearch;
		private final Map<String,String> textAreaOptions = new HashMap<String,String>();

		public AssetTypeCustomAttributeForm(String id, AssetTypeCustomAttributeSearch assetTypeCustomAttributeSearch) {
			super(id);
            FeedbackPanel feedback = new FeedbackPanel("feedback");
            filter = new CalipsoFeedbackMessageFilter();
            feedback.setFilter(filter);
            add(feedback);
            
            this.assetTypeCustomAttributeSearch = assetTypeCustomAttributeSearch;
            
            CompoundPropertyModel model = new CompoundPropertyModel(assetTypeCustomAttributeSearch);
			setModel(model);
			//isMandatory := false => make attribute description and type optional
			//assetTypeCanBeModified := true => make asset type drop down list choose enable  
			add(new AssetCustomAttributeFormPanel("customAttributeFormPanel",
					model, false, true, textAreaOptions));
		}//AssetTypeCustomAttributeForm
		/*
		@Override
		protected void validate() {
		    filter.reset();
		    
		    super.validate();
		}//validate
		*/
		@Override
		protected void onSubmit() {
			activate(new IBreadCrumbPanelFactory(){
				@Override
				public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {

					//Remove last breadcrumb participant
					if (breadCrumbModel.allBreadCrumbParticipants().size()>0){
						breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
					}//if

					return new AssetCustomAttributesPanel(AssetCustomAttributesPanel.this.id, AssetCustomAttributesPanel.this.breadCrumbModel, AssetTypeCustomAttributeForm.this.assetTypeCustomAttributeSearch, AssetCustomAttributesPanel.this.referenceAssetType);
				}
			});
		}//onSubmit
	}//AssetTypeCustomAttributeForm
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Renders custom attribute list
	 * */
	private void listAttributes(){
        LoadableDetachableModel attributesListModel = new LoadableDetachableModel() {
            @Override
			protected Object load() {
            	return getCalipso().findCustomAttributesMatching(AssetCustomAttributesPanel.this.assetTypeCustomAttributeSearch);
            }//load
        };
        
        attributesListModel.getObject();
        
        
        ////////////////
        // Pagination //
        ////////////////

        PaginationPanel paginationPanel = new PaginationPanel("paginationPanel", getBreadCrumbModel(),  this.assetTypeCustomAttributeSearch){
        	
        	IBreadCrumbPanelFactory breadCrumbPanelFactory = new IBreadCrumbPanelFactory(){
				@Override
				public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
					//Remove last breadcrumb participant
					if (breadCrumbModel.allBreadCrumbParticipants().size()>0){
						breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
					}//if
					
					return new AssetCustomAttributesPanel(breadCrumbModel.getActive().getComponent().getId(), breadCrumbModel, AssetCustomAttributesPanel.this.referenceAssetType, AssetCustomAttributesPanel.this.assetTypeCustomAttributeSearch, AssetCustomAttributesPanel.this.isSearchOpen);
				}
			};

        	@Override
			public void onNextPageClick() {
        		activate(breadCrumbPanelFactory);
        	}
        	
        	@Override
			public void onPreviousPageClick() {
        		activate(breadCrumbPanelFactory);
        	}
        	
        	@Override
			public void onPageNumberClick() {
        		activate(breadCrumbPanelFactory);
        	}
        };

        add(paginationPanel);


        /////////////////
        // List header //
        /////////////////

        List<String> columnHeaders = AssetCustomAttributesPanel.this.assetTypeCustomAttributeSearch.getColumnHeaders();

        ListView headings = new ListView("headings", columnHeaders) {
            @Override
			protected void populateItem(ListItem listItem) {
                final String header = (String) listItem.getModelObject();
                
                Link headingLink = new Link("heading") {
                    @Override
					public void onClick() {
                    	AssetCustomAttributesPanel.this.assetTypeCustomAttributeSearch.doSort(header);
                    }
                };
                listItem.add(headingLink); 
                String label = localize("asset.customAttributes." + header);
                headingLink.add(new Label("heading", label));
                if (header.equals(AssetCustomAttributesPanel.this.assetTypeCustomAttributeSearch.getSortFieldName())) {
                    String order = AssetCustomAttributesPanel.this.assetTypeCustomAttributeSearch.isSortDescending() ? "order-down" : "order-up";
                    listItem.add(new SimpleAttributeModifier("class", order));
                }
            }
        };
        
        add(headings);
        
        /////////////////////
        // Attributes List //
        /////////////////////
        
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
        
        if (this.referenceAssetType!=null){
        	add(new Label("add", localize("asset.assetTypes.addCustomAttributeToType", localize(this.referenceAssetType.getNameTranslationResourceKey()))));
        }//if
        else{
        	add(new WebMarkupContainer("add").setVisible(false));
        }//else
        
        ListView listView = new ListView("attributesList", attributesListModel){
        	AssetType referenceAssetType = AssetCustomAttributesPanel.this.referenceAssetType;
        	
        	@Override
        	protected void populateItem(ListItem listItem) {
        		final AssetTypeCustomAttribute attribute = (AssetTypeCustomAttribute)listItem.getModelObject();
        		
        		if (attribute.getId().equals(selectedAttributeId)) {
                    listItem.add(new SimpleAttributeModifier("class", "selected"));
                } else if (listItem.getIndex() % 2 !=0){
        			listItem.add(sam);
        		}//if

        		// listItem.add(new Label("name", new PropertyModel(attribute, "name")));
        		listItem.add(new Label("name", localize(attribute.getNameTranslationResourceKey())));
        		listItem.add(new Label("formType", new Model(localize("asset.attributeType_" + attribute.getFormType()))));
        		listItem.add(new Label("mandatory", new Model(attribute.isMandatory()?localize("yes"):localize("no"))));
        		listItem.add(new Label("active", new Model(attribute.isActive()?localize("yes"):localize("no"))));
        		
                Link edit = new Link("edit") {
                    @Override
					public void onClick() {

            			activate(new IBreadCrumbPanelFactory(){
            				@Override
							public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
            					AssetCustomAttributeFormPagePanel assetCustomAttributeFormPagePanel = new AssetCustomAttributeFormPagePanel(id, breadCrumbModel, attribute);
            					if (referenceAssetType!=null){
            						assetCustomAttributeFormPagePanel.setReferenceAssetType(referenceAssetType);
            					}//if
            					return assetCustomAttributeFormPagePanel;
            				}
            			});
                    	
                    }//onClick
                };
                
                listItem.add(edit);
                
                
                if(referenceAssetType == null){
                	listItem.add(new WebMarkupContainer("add").setVisible(false));
                }//if
                else{
                	WebMarkupContainer add;
                	         	
                	if(referenceAssetType.getAllowedCustomAttributes().contains(attribute)){//if this customAttribute is used  
                		
                		add = new Fragment("add", "removeLink", this);
                		if(logger.isDebugEnabled()){
            				logger.debug("Allowed custom attributes : " + referenceAssetType.getAllowedCustomAttributes());
            			}
                		add.add(new Link("link"){
	                		//remove a custom attribute to the Asset Type in question
	                		@Override
							public void onClick() {
	                			if(logger.isDebugEnabled()){
	                				logger.debug("Allowed custom attributes : " + referenceAssetType.getAllowedCustomAttributes());
	                				logger.debug("Removing attribute : "  + attribute.getName());
	                			}
	                			AssetCustomAttributesPanel.this.referenceAssetType.getAllowedCustomAttributes().remove(attribute);
	                			if(logger.isDebugEnabled()){
	                				logger.debug("new Allowed custom attributes : " + referenceAssetType.getAllowedCustomAttributes());
	                			}
	        					//Remove last 2 breadcrumb participants
	        					if (breadCrumbModel.allBreadCrumbParticipants().size()>2){
	        						breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
	        						breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
	        					}//if
	                			
	            				activate(new IBreadCrumbPanelFactory(){
	            					@Override
									public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
	            						return new AssetTypeFormPagePanel(getBreadCrumbModel().getActive().getComponent().getId(), getBreadCrumbModel(), AssetCustomAttributesPanel.this.referenceAssetType);
	            					}
	            				});
	                			
	                		}//onClick
	                	});
                	}
                	else{//if this customAttribute is not used, can add it
	                	add = new Fragment("add", "addLink", this);
	                	
	                	add.add(new Link("link"){
	                		//Adds a custom attribute to the Asset Type in question
	                		@Override
							public void onClick() {
	                			if(logger.isDebugEnabled()){
	                				logger.debug("Allowed custom attributes : " + referenceAssetType.getAllowedCustomAttributes());
	                				logger.debug("Added custom attribute : " + attribute.getName());
	                			}
	                			AssetCustomAttributesPanel.this.referenceAssetType.getAllowedCustomAttributes().add(attribute);
	                			if(logger.isDebugEnabled()){
	                				logger.debug("New Allowed custom attributes : " + referenceAssetType.getAllowedCustomAttributes());
	                			}
	        					//Remove last 2 breadcrumb participants
	        					if (breadCrumbModel.allBreadCrumbParticipants().size()>2){
	        						breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
	        						breadCrumbModel.allBreadCrumbParticipants().remove(breadCrumbModel.allBreadCrumbParticipants().size()-1);
	        					}//if
	                			
	            				activate(new IBreadCrumbPanelFactory(){
	            					@Override
									public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
	            						return new AssetTypeFormPagePanel(getBreadCrumbModel().getActive().getComponent().getId(), getBreadCrumbModel(), AssetCustomAttributesPanel.this.referenceAssetType);
	            					}
	            				});
	                			
	                		}//onClick
	                	});
	                }

                	listItem.add(add);
                }//else
        	}
        };

        add(listView);
        add(new WebMarkupContainer("noData").setVisible(this.assetTypeCustomAttributeSearch.getResultCount()==0));
        
	}//listAttributes
	
	//--------------------------------------------------------------------------------------------------------------

	@Override
	public String getTitle() {
		return localize("asset.customAttributes.title");
	}
	
	//--------------------------------------------------------------------------------------------------------------

    public void setSelectedAttributeId(Long selectedAttributeId) {
        this.selectedAttributeId = selectedAttributeId;
    }
    
    //--------------------------------------------------------------------------------------------------------------
    
    public void setReferenceAssetType(AssetType assetType){
    	this.referenceAssetType = assetType;
    }
    
    public AssetType getReferenceAssetType(){
    	return this.referenceAssetType;
    }
}