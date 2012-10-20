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

import java.util.Locale;

import gr.abiss.calipso.domain.Effort;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.AbstractConverter;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;

public class EffortField extends FormComponentPanel{
	private static final long serialVersionUID = 1L;

	private TextField daysField, hoursField, minutesField;
	private HiddenField effortField;
	private Effort effort;

	// --------------------------------------------------------------------------------------------

	public EffortField(String id, IModel model, boolean required) {
		super(id, null);

		if (model.getObject()!=null){
			effort = new Effort();
			try{
				effort = new Effort(new Integer(model.getObject().toString()));
			}
			catch(NumberFormatException numberFormatException){
				effort = new Effort();
			}
		}
		else{
			effort = new Effort();
		}

		// Days ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		daysField = new TextField("days", new PropertyModel(effort, "days"), Integer.class){
			@Override
			public IConverter getConverter(Class clazz) {
				return new AbstractConverter(){
					public Object convertToObject(String s, Locale locale) {
						try{
							if (s == null || (s!=null && s.equals(""))){
								s = "0";
							}
							effort.setDays(new Integer(s));
							return effort.getDays();
						}
						catch(NumberFormatException numberFormatException){
							daysField.error(getLocalizer().getString("effortField.invalidDays", null));
							return null;
						}
					}

					@Override
					protected Class getTargetType() {
						return Integer.class;
					}
				};
			}
		};
		daysField.setRequired(required);
		daysField.add(new ErrorHighlighter());
		add(daysField);
		daysField.setLabel(new ResourceModel("effortField.days"));
		add(new SimpleFormComponentLabel("daysLabel", daysField));

		// Hours ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		hoursField = new TextField("hours", new PropertyModel(effort, "hours"), Integer.class){
			@Override
			public IConverter getConverter(Class clazz) {
				return new AbstractConverter(){
					public Object convertToObject(String s, Locale locale) {
						try{
							if (s == null || (s!=null && s.equals(""))){
								s = "0";
							}
							effort.setHours(new Integer(s));
							return effort.getHours();
						}
						catch(NumberFormatException numberFormatException){
							hoursField.error(getLocalizer().getString("effortField.invalidHours", null));
							return null;
						}
					}

					@Override
					protected Class getTargetType() {
						return Integer.class;
					}
				};
			}
		};
		hoursField.setRequired(required);
		hoursField.add(new ErrorHighlighter());
		add(hoursField);
		hoursField.setLabel(new ResourceModel("effortField.hours"));
		add(new SimpleFormComponentLabel("hoursLabel", hoursField));

		// Minutes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		minutesField = new TextField("minutes", new PropertyModel(effort, "minutes"), Integer.class){
			@Override
			public IConverter getConverter(Class clazz) {
				return new AbstractConverter(){
					public Object convertToObject(String s, Locale locale) {
						try{
							if (s == null || (s!=null && s.equals(""))){
								s = "0";
							}
							effort.setMinutes(new Integer(s));
							return effort.getMinutes();
						}
						catch(NumberFormatException numberFormatException){
							minutesField.error(getLocalizer().getString("effortField.invalidMinutes", null));
							return null;
						}
					}

					@Override
					protected Class getTargetType() {
						return Integer.class;
					}
				};
			}
		};
		minutesField.setRequired(required);
		minutesField.add(new ErrorHighlighter());
		add(minutesField);
		minutesField.setLabel(new ResourceModel("effortField.minutes"));
		add(new SimpleFormComponentLabel("minutesLabel", minutesField));

		//Total Effort in minutes (Hidden field) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		effortField = new HiddenField("effortField", model, Integer.class){
			public IConverter getConverter(Class clazz) {
				return new AbstractConverter(){
					public Object convertToObject(String s, Locale locale) {
						return effort.getEffortInMinutes();
					}

					@Override
					protected Class getTargetType() {
						return Integer.class;
					}
				};
			};
		};
		add(effortField);
		
	}//EffordField

	// --------------------------------------------------------------------------------------------

	public void setEffort(Effort effort){
		this.effort = effort;
	}
	
	// --------------------------------------------------------------------------------------------
	
	public Effort getEffort(){
		return this.effort;
	}

	// --------------------------------------------------------------------------------------------
	
    @Override
    public void updateModel() {
    	effortField.updateModel();
    }
}