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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import gr.abiss.calipso.domain.StdFieldMask;
import gr.abiss.calipso.domain.StdFieldType;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * @author marcello
 */
public abstract class MaskPanel extends BasePanel {
	private StdFieldType stdFieldType;
	//private String selected = "";
	private final MaskChoice dropDownChoice;
	private StdFieldMask selectedMask;

	private class MaskChoice extends DropDownChoice{
		public MaskChoice(String id) {
			super(id);
		}
		

		public MaskChoice(String id, IModel model, List list, IChoiceRenderer choiceRenderer) {
			super(id, model, list, choiceRenderer);
		}

		@Override
		protected boolean wantOnSelectionChangedNotifications() {
			return true;
			
		}

		protected void onSelectionChanged(Object object) {
			onMaskChanged((StdFieldMask) object);
		}
		
	}
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////		
	public MaskPanel(String id, IBreadCrumbModel breadCrumbModel, StdFieldType stdFieldType, StdFieldMask selectedMask){
		super(id, breadCrumbModel);
		
		this.stdFieldType = stdFieldType;

		WebMarkupContainer maskForm = new WebMarkupContainer("maskForm");
		add(maskForm);
		
		List<StdFieldMask> masksList = new ArrayList<StdFieldMask>(this.stdFieldType.getType().getAvailableMasks());
		if (selectedMask == null){
			//default is hidden
			this.selectedMask = new StdFieldMask(StdFieldMask.Mask.HIDDEN);
		}
		else{
			this.selectedMask = selectedMask;
		}
		
		dropDownChoice = new MaskChoice("mask", new PropertyModel(this, "selectedMask") , masksList, new IChoiceRenderer(){
			public Object getDisplayValue(Object object) {
				return ((StdFieldMask)object).getMask().getName();
			}

			public String getIdValue(Object object, int i) {
				if (i>=0){
					return ((StdFieldMask)object).getMask().getId().toString();
				}
				return object.toString();
			}
		});

		dropDownChoice.setOutputMarkupId(true);		
		dropDownChoice.setNullValid(false);	
		
		maskForm.add(dropDownChoice);
		
		
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	public abstract void onMaskChanged(StdFieldMask selectedStdFieldMask);
}
