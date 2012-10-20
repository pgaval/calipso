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

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.util.BreadCrumbUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class AssignableSpacesPanel extends BasePanel{
	private static final Logger logger =  Logger.getLogger(AssignableSpacesPanel.class);
	private static final long serialVersionUID = 1L;
	private static final String VALUE_SEPARATOR = org.apache.wicket.markup.html.form.FormComponent.VALUE_SEPARATOR;
	
	private Space referenceSpace;
	private Field field;
	private List availableSpaces;
	private List assignableSpaces;
	private ListMultipleChoice availableSpacesChoice, assignableSpacesChoice;
	
    //////////////////
    // Constructors //
    //////////////////
    
	public AssignableSpacesPanel(String id, IBreadCrumbModel breadCrumbModel, Space referenceSpace, Field field) {
		super(id, breadCrumbModel);
		
		this.referenceSpace = referenceSpace;
		this.field = field;
		
		showTitle();
		
		add(new AssignableSpacesForm("form"));

	}//AssignableSpaces

	//-------------------------------------------------------------------------------------
	
	public  AssignableSpacesPanel(String id, IBreadCrumbModel breadCrumbModel, Space referenceSpace, Field field, List availableSpaces, List assignableSpaces){
		super(id, breadCrumbModel);
		
		this.referenceSpace = referenceSpace;
		this.field = field;
		this.availableSpaces = availableSpaces;
		this.assignableSpaces = assignableSpaces;	
		
		showTitle();

		add(new AssignableSpacesForm("form"));		
		
	}//AssignableSpaces


	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	private void showTitle(){
		Label title = new Label("referenceSpace", localize("assignable_spaces.pageTitle", localize(this.referenceSpace.getNameTranslationResourceKey())));
		add(title);
	}//showTitle

	//---------------------------------------------------------------------------------
	
    public String getTitle(){
        return localize("assignable_spaces.title");
    }
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	public class AssignableSpacesForm extends Form {

		TextField label = null;
		
		//---------------------------------------------------------------------------------
		
		public AssignableSpacesForm(String id) {
			super(id);
			addComponents();

		}//AssignableSpacesForm
		
		//=================================================================================
		
		private void addComponents(){
			
			addInternalName();
			addLabel();
			addDelete();
			addCancel();
			addDone();
			addSelected();
			addAll();
			removeSelected();
			removeAllSpaces();
			
			renderAvailableSpaces();
			renderAssignableSpaces();
		}//addComponents
		
		//=================================================================================
		
		//////////////////////
		// Buttons & Events //
		//////////////////////

		private void addDelete(){
		
	        Button btnDelete = new Button("delete") {
	            @Override
	            public void onSubmit() {
                    int affectedCount = 0;
                	if(referenceSpace.getId()!=0)
                		//
                		affectedCount = getCalipso().loadCountOfRecordsHavingFieldNotNull(referenceSpace, field);
                	
	            	if (affectedCount > 0) {
		                final String heading = localize("space_field_delete.confirm") + " : " + field.getLabel() 
		                    + " [" + field.getName().getDescription() + " - " + field.getName().getText() + "]";
		                final String warning = localize("space_field_delete.line3");
		                final String line1 = localize("space_field_delete.line1");
		                final String line2 = localize("space_field_delete.line2", affectedCount + "");

		                activate(new IBreadCrumbPanelFactory(){
		                	public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel) {
		                		ConfirmPanel confirm = new ConfirmPanel(componentId, breadCrumbModel, heading, warning, new String[] {line1, line2}) {
		                			public void onConfirm() {
		                				// database will be updated, if we don't do this
		                				// user may leave without committing metadata change
		                				getCalipso().bulkUpdateFieldToNull(referenceSpace, field);
		                				referenceSpace.getMetadata().removeField(field.getName().getText());
		                				
		                				// synchronize metadata version or else if we save again we get Stale Object Exception
		                				//referenceSpace.setMetadata(getCalipso().loadMetadata(referenceSpace.getMetadata().getId()));
		                				
		                				activate(new IBreadCrumbPanelFactory(){
		                					public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel) {
		                						BreadCrumbUtils.moveToPanelForRelod(breadCrumbModel, SpaceFieldListPanel.class);
		                						return new SpaceFieldListPanel(componentId, breadCrumbModel, referenceSpace, null);
		                					}
		                				});
		                			};
		                		};
		                		return confirm;
		                	}
		                });
	            	}
	            	else{
	                    // this is an unsaved space or there are no impacted items
	                	referenceSpace.getMetadata().removeField(field.getName().getText());

        				activate(new IBreadCrumbPanelFactory(){
        					public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel) {
        						BreadCrumbUtils.moveToPanelForRelod(breadCrumbModel, SpaceFieldListPanel.class);
        						return new SpaceFieldListPanel(componentId, breadCrumbModel, referenceSpace, null);
        					}
        				});	                	
	            	}
	            }
	        };


	        btnDelete.setDefaultFormProcessing(false);
	        if(!referenceSpace.getMetadata().getFields().containsKey(field.getName())) {
	            btnDelete.setVisible(false);
	        } 
	        add(btnDelete);
			
		}//addDelete
		
		//-----------------------------------------------------------
		private void addCancel(){
			Link cancel = new Link("cancel"){

				@Override
				public void onClick() {
//			        if(previousPage == null) {
//			            setResponsePage(SpaceListPage.class);
//			            return;
//			        }//if
//			        setResponsePage(previousPage);

                    activate(new IBreadCrumbPanelFactory(){
                    	public BreadCrumbPanel create(String componentId,IBreadCrumbModel breadCrumbModel){
                    		return BreadCrumbUtils.moveToPanel(breadCrumbModel, SpaceListPanel.class.getName());
						}
                    });
					
					
			    }//onClick
			};
			
			add(cancel);
			
		}//addCancel

	    //-----------------------------------------------------------

		private void addDone(){
			
			Button btnDone = new Button("done", new Model("Done")){
				@Override
				public void onSubmit() {
					if (field.getOptions()!=null){
						field.getOptions().clear();
					}//if
					if (assignableSpaces!=null){
						for (int i=0; i<assignableSpaces.size(); i++){
							Space space = (Space)assignableSpaces.get(i);
							String spaceId = String.valueOf(space.getId());
							field.addOption(spaceId, spaceId);
						}//for
					}//if
					field.setLabel((String)label.getModelObject());
					referenceSpace.getMetadata().add(field);
//					setResponsePage(new SpaceFieldListPage(referenceSpace, field.getName().getText(), mainPreviousPage));
                    activate(new IBreadCrumbPanelFactory(){
                    	public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel){
                    		BreadCrumbUtils.removePreviousBreadCrumbPanel(breadCrumbModel);
							return new SpaceFieldListPanel(componentId, breadCrumbModel, referenceSpace, field.getName().getText());
						}
                    });
					
				}//onSubmit
			};
			
			add(btnDone);
			
		}//addDone
		
		
		//-----------------------------------------------------------

		
		private void addSelected(){
			
			Button btnAddSelected = new Button("btnAddSelected"){
				@Override
				public void onSubmit() {
					
					List choices = getChoiceValues(availableSpacesChoice.getModelValue());
					for (int i=0; i<choices.size(); i++){
						Space space = getCalipso().loadSpace(Long.parseLong((String)choices.get(i)));
						assignableSpaces.add(space);
						availableSpaces.remove(space);
					}//for
					
					field.setLabel((String)label.getModelObject());
					
                    activate(new IBreadCrumbPanelFactory(){
                    	public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel){
                    		BreadCrumbUtils.removeActiveBreadCrumbPanel(breadCrumbModel);
							return new AssignableSpacesPanel(componentId, breadCrumbModel, referenceSpace, field, availableSpaces, assignableSpaces);
						}
                    });
					
//					setResponsePage(new AssignableSpacesPanel(referenceSpace, field, previousPage, availableSpaces, assignableSpaces));					
					
				}//onSubmit
			};
			
			add(btnAddSelected);
			
		}//addSelected
		

	    //-----------------------------------------------------------

		private void addAll(){
			
			Button btnAddAll = new Button("btnAddAll"){
				@Override
				public void onSubmit() {
					List choices = availableSpacesChoice.getChoices();
					for(int i=0; i<choices.size(); i++){
						try{
							long spaceId = (Long)choices.get(i);
							Space space = getCalipso().loadSpace(spaceId);
							assignableSpaces.add(space);
							availableSpaces.remove(space);
						}//try
						catch (Exception e){
							logger.error("Error :" + choices.get(i));
						}//catch
					}//for
					field.setLabel((String)label.getModelObject());
//					setResponsePage(new AssignableSpacesPanel(referenceSpace, field, previousPage, availableSpaces, assignableSpaces));

                    activate(new IBreadCrumbPanelFactory(){
                    	public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel){
                    		BreadCrumbUtils.removeActiveBreadCrumbPanel(breadCrumbModel);
							return new AssignableSpacesPanel(componentId, breadCrumbModel, referenceSpace, field, availableSpaces, assignableSpaces);
						}
                    });
					
				}//onSubmit
			};
			
			add(btnAddAll);
			
		}//addAll

		//-----------------------------------------------------------

		
		private void removeSelected(){
			
			Button btnRemoveSelected = new Button("btnRemoveSelected"){
				
				public void onSubmit() {
					List choices = getChoiceValues(assignableSpacesChoice.getModelValue());
					for (int i=0; i<choices.size(); i++){
						Space space = getCalipso().loadSpace(Long.parseLong((String)choices.get(i)));
						availableSpaces.add(space);
						assignableSpaces.remove(space);
					}//for
					field.setLabel((String)label.getModelObject());
//					setResponsePage(new AssignableSpacesPanel(referenceSpace, field, previousPage, availableSpaces, assignableSpaces));					

                    activate(new IBreadCrumbPanelFactory(){
                    	public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel){
                    		BreadCrumbUtils.removeActiveBreadCrumbPanel(breadCrumbModel);
							return new AssignableSpacesPanel(componentId, breadCrumbModel, referenceSpace, field, availableSpaces, assignableSpaces);
						}
                    });
					
				}//onSubmit
			};
			
			add(btnRemoveSelected);
			
		}//removeSelected

		//-----------------------------------------------------------

		
		private void removeAllSpaces(){
			
			Button btnRemoveAll = new Button("btnRemoveAll"){
				@Override
				public void onSubmit() {
					List choices = assignableSpacesChoice.getChoices();
					for(int i=0; i<choices.size(); i++){
						try{
							long spaceId = (Long)choices.get(i);
							Space space = getCalipso().loadSpace(spaceId);
							availableSpaces.add(space);
							assignableSpaces.remove(space);
						}//try
						catch (Exception e){
							logger.error("Error :" + choices.get(i));
						}//catch
					}//for
					field.setLabel((String)label.getModelObject());
//					setResponsePage(new AssignableSpacesPanel(referenceSpace, field, previousPage, availableSpaces, assignableSpaces));

                    activate(new IBreadCrumbPanelFactory(){
                    	public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel){
                    		BreadCrumbUtils.removeActiveBreadCrumbPanel(breadCrumbModel);
							return new AssignableSpacesPanel(componentId, breadCrumbModel, referenceSpace, field, availableSpaces, assignableSpaces);
						}
                    });
					
				}//onSubmit
			};
			
			add(btnRemoveAll);
			
		}//removeAllSpaces
				
		//=================================================================================

		///////////////
		// Rendering //
		///////////////
		
		private void addInternalName(){
			add(new Label("name", new PropertyModel(field, "name.text")));
		}//addInternalName
		
		//-------------------------------------------------------------------------------------------
		
		private void addLabel(){
            label = new TextField("field.label", new Model(field.getLabel()));
            label.setRequired(true);
            label.setOutputMarkupId(true);
            label.add(new ErrorHighlighter());
            add(label);
            
            add(new Behavior() {
                public void renderHead(IHeaderResponse response) {
                    if(field.getLabel() == null) {
                        response.renderOnLoadJavaScript("document.getElementById('" + label.getMarkupId() + "').focus()");
                    }                                        
                }
            });                    
			
		}//addLabel
		
		//-------------------------------------------------------------------------------------------
		
		private void renderAvailableSpaces(){
			availableSpaces = loadAvailableSpaces();
			final Map options = new LinkedHashMap();
			long defaultSpace = 0;
			
			for (int i=0; i<availableSpaces.size(); i++){
				Space space = (Space)availableSpaces.get(i);
				options.put(space.getId(), localize(space.getNameTranslationResourceKey()));
			}//for
			
			
			final List keys = new ArrayList(options.keySet());
			
			if (keys.size()>0){
				defaultSpace = (Long)keys.get(0);
			}//if
			
			ArrayList defaultValues = new ArrayList();
			defaultValues.add(defaultSpace);
			
			availableSpacesChoice = new ListMultipleChoice("availableSpaces", new PropertyModel(new SelectedValues(defaultValues), "selected"), keys, new IChoiceRenderer() {
	            public Object getDisplayValue(Object o) {
	                return options.get(o);
	            };
	            public String getIdValue(Object o, int i) {
	                return o.toString();
	            };
	        });

			add(availableSpacesChoice);

		}//renderAvailableSpaces
		
		//-------------------------------------------------------------------------------------------
		
		
		private void renderAssignableSpaces(){
			List assignableSpaces = loadAssignableSpaces();
			final Map options = new LinkedHashMap();
			long defaultSpace = 0;

			for (int i=0; i<assignableSpaces.size(); i++){
				Space space = (Space)assignableSpaces.get(i);
				options.put(space.getId(), localize(space.getNameTranslationResourceKey()));
			}//for
			

			final List keys = new ArrayList(options.keySet());
			
			if (keys.size()>0){
				defaultSpace = (Long)keys.get(0);
			}//if

			ArrayList defaultValues = new ArrayList();
			defaultValues.add(defaultSpace);
			

			assignableSpacesChoice = new ListMultipleChoice("assignableSpaces", new PropertyModel(new SelectedValues(defaultValues), "selected"), keys, new IChoiceRenderer() {
	            public Object getDisplayValue(Object o) {
	                return options.get(o);
	            };
	            public String getIdValue(Object o, int i) {
	                return o.toString();
	            };
	        });
			
			add(assignableSpacesChoice);
			
		}//renderAssignableSpaces
		
		//=================================================================================
		
		////////////////////
		// Helper methods //
		////////////////////

		
	    private List<Space> loadAvailableSpaces(){

	    	if (availableSpaces==null){
		    	List allSpaces = getCalipso().findAllSpaces();
		    	assignableSpaces = loadAssignableSpaces();
		    	availableSpaces = new ArrayList();
		    	
		    	
		    	for (int i=0; i<allSpaces.size(); i++){
		    		Space space = (Space)allSpaces.get(i);
		    		if (space.getId()!= referenceSpace.getId() && !isAssignableSpace(space)){
		    			availableSpaces.add(space);
		    		}//if
		    	}//for
	    	}//if
	    	
	    	return availableSpaces;
	    }//loadAvailableSpaces
	    
	    //------------------------------------------------------------------------------
	    
	    private boolean isAssignableSpace(Space space){
	    	
	    	if  (assignableSpaces!=null){
	    		for (int i=0; i<assignableSpaces.size(); i++){
	    			Space assignableSpace = (Space)assignableSpaces.get(i); 
	    			if (assignableSpace.getId()==space.getId()){
	    				return true;
	    			}//if
	    		}//for
	    	}//if

	    	return false;
	    }//isAssignableSpace
	    
	    //------------------------------------------------------------------------------
	    
	    private List<Space> loadAssignableSpaces(){	    	
	    	if (assignableSpaces==null){
		    	List<Space> spaces = new ArrayList();
		    	if (field.getOptions()==null){
		    		return spaces;
		    	}//if
		    	List assignableSpacesIds = new ArrayList(field.getOptions().values());
		    	
		    	for (int i=0; i<assignableSpacesIds.size(); i++){
		    		long assignableSpaceId = Long.parseLong((String)assignableSpacesIds.get(i));
		    		spaces.add(getCalipso().loadSpace(assignableSpaceId));
		    	}//for
		    	
		    	return spaces;
	    	}//if
	    	
	    	
	    	return assignableSpaces;
	    }//loadAssignableSpaces
		
	    //------------------------------------------------------------------------------
	    
	    private List getChoiceValues(String selected){
	    	List choiceValues = new ArrayList();
	    	
	    	if (selected != null){
	            for (final StringTokenizer tokenizer = new StringTokenizer(selected, VALUE_SEPARATOR); tokenizer.hasMoreTokens();) {
	            	choiceValues.add(tokenizer.nextToken());
	            }//for	    	
	    	}//if
	    	
	    	return choiceValues;
	    }//getChoiceValues

	}//AssignableSpacesForm
	
}//AssignableSpaces
