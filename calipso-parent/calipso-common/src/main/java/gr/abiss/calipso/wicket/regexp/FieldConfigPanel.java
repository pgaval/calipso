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

import java.util.Locale;

import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.components.formfields.FieldConfig;

import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.convert.IConverter;

public class FieldConfigPanel extends BasePanel {
	
	private static final long serialVersionUID = 1L;
	
	public FieldConfigPanel(String id, CompoundPropertyModel model, boolean isMandatory) {
		super(id);
		setOutputMarkupId(true);
		setDefaultModel(model);
		
		TextField defaultValueExpression = new TextField("defaultValueExpression"); 
    	defaultValueExpression.setOutputMarkupId(true);
    	defaultValueExpression.setLabel(new ResourceModel("field.defaultValueExpression"));
    	add(defaultValueExpression);
    	add(new SimpleFormComponentLabel("defaultValueExpressionLabel", defaultValueExpression));
    	
    	TextArea xmlField = new TextArea("xmlConfig"); 
    	xmlField.setType(FieldConfig.class);
    	xmlField.setOutputMarkupId(true);
    	xmlField.setLabel(new ResourceModel("field.xmlConfig"));
    	add(xmlField);
    	add(new SimpleFormComponentLabel("xmlLabel", xmlField));
    	
    }
}