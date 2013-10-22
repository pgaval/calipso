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

package gr.abiss.calipso.wicket.components.formfields;

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public abstract class DateField extends
		org.apache.wicket.extensions.yui.calendar.DateField {

	protected static final Logger logger = Logger.getLogger(DateField.class);
			
	public DateField(String id) {
		super(id);
	}
	

	public DateField(String id, IModel<Date> model) {
		super(id, model);
	}

	@Override
    protected DateTextField newDateTextField(String id, PropertyModel model){
		DateTextField dateTextField =  DateTextField.withConverter(id, model,
            new PatternDateConverter(this.getDateFormat(), true));
		dateTextField.add(new AttributeModifier("size", true, new Model<Integer>() {
            @Override
            public Integer getObject() {               
                return new Integer(DateField.this.getDateFormat().length());
            }
        }));
		return dateTextField;
    }

    @Override
    protected DatePicker newDatePicker(){
    	DatePicker dp = new DatePicker(){
            @Override
            protected String getDatePattern(){
                return DateField.this.getDateFormat();
            }
        };
        dp.setShowOnFieldClick(true);
        return dp;
    }
    
    
	protected abstract String getDateFormat();

}
