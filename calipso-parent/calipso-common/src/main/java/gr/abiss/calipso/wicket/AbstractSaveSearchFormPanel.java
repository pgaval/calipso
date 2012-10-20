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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.SavedSearch;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.Model;

/**
 * @author marcello
 */
public abstract class AbstractSaveSearchFormPanel extends BasePanel {

	private final static String EDITFORM_ID = "editForm";
	private final static String FORMCONTAINER_ID = "formContainer";
	private final static String EDITFORMFRAGMENT_ID = "editFormFragment";
	private final static String CONFIRMFORMFRAGMENT_ID = "confirmFormFragment";
	private final static String CONFIRMFORM_ID = "confirmForm";

	private MarkupContainer cancelTargetComponent;
	private MarkupContainer confirmTargetComponent;
	private ItemSearch itemSearch;

	public AbstractSaveSearchFormPanel(String id) {
		super(id);
		Fragment editFormFragment = new Fragment(FORMCONTAINER_ID, EDITFORMFRAGMENT_ID, this);
		editFormFragment.add(new EditForm(EDITFORM_ID, new SavedSearch()));
		add(editFormFragment);
	}//SaveSearchFormPanel

	// -------------------------------------------------------------------------------

	public AbstractSaveSearchFormPanel(String id, SavedSearch savedSearch) {
		super(id);
		Fragment editFormFragment = new Fragment(FORMCONTAINER_ID, EDITFORMFRAGMENT_ID, this);
		editFormFragment.add(new EditForm(EDITFORM_ID, savedSearch));
		add(editFormFragment);
		
	}//SaveSearchFormPanel
	
	// -------------------------------------------------------------------------------
	
	public AbstractSaveSearchFormPanel(String id, IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);
		Fragment editFormFragment = new Fragment(FORMCONTAINER_ID, EDITFORMFRAGMENT_ID, this);
		editFormFragment.add(new EditForm(EDITFORM_ID, new SavedSearch()));
		add(editFormFragment);

	}//SaveSearchFormPanel

	// -------------------------------------------------------------------------------
	
	public AbstractSaveSearchFormPanel(String id, IBreadCrumbModel breadCrumbModel, SavedSearch savedSearch) {
		super(id, breadCrumbModel);
		Fragment editFormFragment = new Fragment(FORMCONTAINER_ID, EDITFORMFRAGMENT_ID, this);
		editFormFragment.add(new EditForm(EDITFORM_ID, savedSearch));
		add(editFormFragment);

	}//SaveSearchFormPanel

	// -------------------------------------------------------------------------------

	public AbstractSaveSearchFormPanel(String id, String message) {
		super(id);
		Fragment confirmFormFragment = new Fragment(FORMCONTAINER_ID, CONFIRMFORMFRAGMENT_ID, this);
		confirmFormFragment.add(new ConfirmForm(CONFIRMFORM_ID, message));
		add(confirmFormFragment);
	}//AbstractSaveSearchFormPanel

	////////////////////////////////////////////////////////////////////////////////////

	public abstract void cancel();
	public abstract void save(String name);
	public abstract void confirm();

	////////////////////////////////////////////////////////////////////////////////////
	
	public MarkupContainer getCancelTargetComponent() {
		return cancelTargetComponent;
	}
	//------------------------------------------------------------------------
	public void setCancelTargetComponent(MarkupContainer cancelTargetComponent) {
		this.cancelTargetComponent = cancelTargetComponent;
	}
	//------------------------------------------------------------------------
	public ItemSearch getItemSearch() {
		return itemSearch;
	}
	//------------------------------------------------------------------------	
	public void setItemSearch(ItemSearch itemSearch) {
		this.itemSearch = itemSearch;
	}
	//------------------------------------------------------------------------
	public void setConfirmTargetComponent(MarkupContainer confirmTargetComponent) {
		this.confirmTargetComponent = confirmTargetComponent;
	}
	//------------------------------------------------------------------------
	public MarkupContainer getConfirmTargetComponent() {
		return confirmTargetComponent;
	}
	////////////////////////////////////////////////////////////////////////////////////

	private class EditForm extends Form{
		private CalipsoFeedbackMessageFilter filter;

		public EditForm(String id, final SavedSearch savedSearch) {
			super(id);

			// -- Feedback to user in case of error, etc. 
            FeedbackPanel feedback = new FeedbackPanel("feedback");
            filter = new CalipsoFeedbackMessageFilter();
            feedback.setFilter(filter);
            add(feedback);

			//--- Name --------
			final TextField name = new TextField("name", new Model(savedSearch));
			add(name);
			// render the appropriate saved search visibility options
			// depending on whether we are in a specific space context
			// and the admin rights of the user in session
			final List<Short> savedSearchVisibilityModes = new LinkedList<Short>();
			savedSearchVisibilityModes.add(SavedSearch.VISIBILITY_PRIVATE);
			// if current space admin
			if(getCurrentSpace() != null && getPrincipal().isSpaceAdmin(getCurrentSpace())){
				savedSearchVisibilityModes.add(SavedSearch.VISIBILITY_WITHIN_SPACE);
				savedSearchVisibilityModes.add(SavedSearch.VISIBILITY_WITHIN_SPACEGROUP);
			}
			// if global admin
			if(getPrincipal().isGlobalAdmin()){
				savedSearchVisibilityModes.add(SavedSearch.VISIBILITY_PUBLIC);
				savedSearchVisibilityModes.add(SavedSearch.VISIBILITY_LOGGEDIN_USERS);	
			}
			
			final DropDownChoice visibility = new DropDownChoice("visibility", new Model(savedSearch), savedSearchVisibilityModes, new IChoiceRenderer(){
				public Object getDisplayValue(Object object) {
					return localize(new StringBuffer("item_saved_search.visibility.").append(((Short)object).shortValue()).toString());
				}

				public String getIdValue(Object object, int index) {
					return index+"";
				}
			});
			visibility.setNullValid(false);
			add(visibility);
			
			//--- Save --------
			Button btnSubmit = new Button("btnSave"){
				public void onSubmit() {
					String savedSearchName = (String) name.getModelObject();
					Short savedVisibility = (Short) visibility.getModelObject();
					String savedSearchQueryString = null;
					
					if (getCurrentItemSearch()!=null){//Actually in case of edit if no search has been done
						PageParameters params = getCurrentItemSearch().getAsQueryString();
	
				        for(Object o : params.getNamedKeys()) {
				            if(savedSearchQueryString == null){
				            	savedSearchQueryString = o.toString();
				            }else{
				            	savedSearchQueryString += ","+o.toString();
				            }//else
				        }//for
					}//if
			        if(savedSearchQueryString == null){
		            	savedSearchQueryString = "";
			        }
			        
			        savedSearch.setQueryString(savedSearchQueryString);
			        savedSearch.setSpace(getCurrentSpace());
			        savedSearch.setUser(getPrincipal());
			        savedSearch.setName(savedSearchName);
			        savedSearch.setVisibility(savedVisibility);

			        getCalipso().storeSavedSearch(savedSearch);
					save(savedSearchName);
				}
			};
			add(btnSubmit);
			
			//--- Cancel --------
			AjaxLink btnCancel = new AjaxLink("btnCancel"){
				@Override
				public void onClick(AjaxRequestTarget target) {
					cancel();
					if (cancelTargetComponent!=null){
						target.addComponent(cancelTargetComponent);
					}//if
				}//onClick
			};
			add(btnCancel);
			
			//Validation. Saved search description should not be null or empty
			AbstractFormValidator dateValidator = new AbstractFormValidator(){
				public FormComponent[] getDependentFormComponents() {
					return new FormComponent[] {name};
				}//getDependentFormComponents

				public void validate(Form form) {
					String savedSearchName = name.getValue(); 
					
					if (savedSearchName==null || (savedSearchName!=null && savedSearchName.trim().equals(""))){
						name.error(localize("saved_search_form.nameIsNull"));
					}//if
				}//validate
			};
			
			add(dateValidator);
			
		}//EditForm

	} //EditForm

	////////////////////////////////////////////////////////////////////////////////////

	private class ConfirmForm extends Form{
		public ConfirmForm(String id, String message){
			super(id);

			// --- Message ------
			Label messageLabel = new Label("message", new Model(message));
			add(messageLabel);

			// --- Yes ----------
			AjaxLink btnYes = new AjaxLink("btnYes"){
				@Override
				public void onClick(AjaxRequestTarget target) {
					
					confirm();
					if (confirmTargetComponent!=null){
						target.addComponent(confirmTargetComponent);
					}//if
				}//onClick
			};
			add(btnYes);

			// --- No -----------
			AjaxLink btnNo = new AjaxLink("btnNo"){
				@Override
				public void onClick(AjaxRequestTarget target) {
					cancel();
					if (cancelTargetComponent!=null){
						target.addComponent(cancelTargetComponent);
					}//if
				}//onClick
			};
			add(btnNo);
		}//ConfirmForm
	}

}//AbstractSaveSearchFormPanel