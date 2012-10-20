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

import gr.abiss.calipso.domain.FieldGroup;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

public abstract class EditFieldGroupPanel  extends BasePanel {

	private static Logger log = Logger.getLogger(EditFieldGroupPanel.class);
	
    protected static final String BUTTON_SAVE = "save";
    protected static final String BUTTON_CANCEL = "cancel";

	public EditFieldGroupPanel(String id, final ModalWindow modalWindow, final FieldGroup fieldGroup) {
        super(id);
        modalWindow.setInitialHeight(470);
        modalWindow.setInitialWidth(600);
 
        Form<FieldGroup> fieldGroupForm = new Form<FieldGroup>("fieldGroupForm");
        fieldGroupForm.setModel(new CompoundPropertyModel<FieldGroup>(fieldGroup));
        add(fieldGroupForm);
        FeedbackPanel fieldGroupFormFeedback =  getFeedbackPanel("fieldGroupFormFeedback");
        fieldGroupForm.add(fieldGroupFormFeedback);

        setUpAndAdd(new RequiredTextField<String>("id"), fieldGroupForm);
        setUpAndAdd(new RequiredTextField<String>("name"), fieldGroupForm);
        setUpAndAdd(new RequiredTextField<String>("priority"), fieldGroupForm);
        
        fieldGroupForm.add(getSaveButton(modalWindow, fieldGroupForm, fieldGroupFormFeedback));
        fieldGroupForm.add(getCancelButton(modalWindow, fieldGroupForm));
 
    }
	/**
	 * Implement to persist data on submit
	 * @param target
	 * @param form
	 */
	protected abstract void persist (AjaxRequestTarget target, Form form);

	/**
	 * just closes the window, override to actually persist
	 * @param modalWindow
	 * @param fieldGroupForm
	 * @return
	 */
	protected AjaxButton getSaveButton(final ModalWindow modalWindow,
			Form<FieldGroup> fieldGroupForm,
			final FeedbackPanel feedbackPanel) {
		AjaxButton save = new IndicatingAjaxButton(BUTTON_SAVE, fieldGroupForm) {

			
			
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
            	log.warn("method getSaveButton was given as an example but not overriden.");
            	persist(target, form);
                if (target != null) {
                    modalWindow.close(target);
                }
            }
            @Override
            protected void onError(AjaxRequestTarget target, Form form) {
            	log.warn("method getSaveButton was given as an example but not overriden.");
                if (target != null) {
                	target.addComponent(feedbackPanel);
                }
            }
        };
		return save;
	}

	protected AjaxButton getCancelButton(final ModalWindow modalWindow,
			Form<FieldGroup> fieldGroupForm) {
		AjaxButton cancel = new AjaxButton(BUTTON_CANCEL, fieldGroupForm) {
 

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (target != null) {
                    modalWindow.close(target);
                }
            }

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// TODO Auto-generated method stub
				
			}
        };
		return cancel;
	}
 
}