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

package gr.abiss.calipso.wicket.regexp;

import gr.abiss.calipso.domain.ValidationExpression;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.components.validators.RegexpValidator;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class ValidationPanel extends BasePanel {
	
	private static final long serialVersionUID = 1L;
	
	DropDownChoice<ValidationExpression> valExprChoice;
	public ValidationPanel(String id, IModel model, boolean isMandatory) {
		super(id);
		setOutputMarkupId(true);
		//setDefaultModel(model);

		final List<ValidationExpression> validationExprList = getCalipso().findAllValidationExpressions();
		
		
    	 valExprChoice = new DropDownChoice<ValidationExpression>("validationExpression", model, validationExprList, new IChoiceRenderer<ValidationExpression>(){
    		
			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(ValidationExpression object) {
				return ((ValidationExpression)object).getName();
			}

			public String getIdValue(ValidationExpression object, int index) {
				return index +"";
			}
    	}){
			private static final long serialVersionUID = 1L;
	    	@Override
	    	protected CharSequence getDefaultChoice(String selected) {
	    		return super.getDefaultChoice(RegexpValidator.NO_VALIDATION);
	    	}
	    };
	    valExprChoice.setOutputMarkupId(true);
	    valExprChoice.setNullValid(false);
    	valExprChoice.setLabel(new ResourceModel("validation_Expression.list"));
    	add(new SimpleFormComponentLabel("label", valExprChoice));
    	add(valExprChoice);
    }
}