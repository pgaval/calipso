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
 * 
 * This file incorporates work released by the JTrac project and  covered 
 * by the following copyright and permission notice:  
 * 
 *   Copyright 2002-2005 the original author or authors.
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *   
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package gr.abiss.calipso.wicket.yui;

import gr.abiss.calipso.wicket.ErrorHighlighter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.AbstractConverter;

/**
 * yui date picker panel
 * @deprecated use org.apache.wicket.extensions.yui.calendar.DateField 
 */
public class YuiCalendar extends FormComponentPanel implements IHeaderContributor{
    private static final Logger logger = Logger.getLogger(YuiCalendar.class);
    private TextField dateField;
    private WebMarkupContainer container;        
    private String value;
    private Date dateValue;
    
    public YuiCalendar(String id, IModel model, boolean required) {
        
        super(id, null);  

        add(new Behavior(){
		      public void renderHead(Component component, IHeaderResponse response) {
		    	  response.renderJavaScriptReference("resources/yui/yahoo/yahoo-min.js", "yui-yahoo");
		    	  response.renderJavaScriptReference("resources/yui/event/event-min.js", "yui-event");
		    	  response.renderJavaScriptReference("resources/yui/dom/dom-min.js", "yui-dom");
		    	  response.renderJavaScriptReference("resources/yui/calendar/calendar-min.js", "yui-calendar");
		    	  response.renderJavaScriptReference("resources/yui/calendar/calendar-utils.js", "yui-calendar-utils");
		    	  response.renderCSSReference("resources/yui/container/assets/calendar.css");
		      }
		});
        
        dateField = new TextField("field", model, Date.class) {
            @Override
            public IConverter getConverter(Class clazz) {
                return new AbstractConverter() {
//                    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                	private DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    public Object convertToObject(String s, Locale locale) {
                        if(s == null || s.trim().length() == 0) {
                        	value = null;
                        	dateValue = null;
                            return null;
                        }
                        try {
                        	value = s;
                        	dateValue = df.parse(s); 
                            return dateValue;
                        } catch (Exception e) {
                        	value = null;
                            throw new ConversionException(e);
                        }                                                
                    }

                    protected Class getTargetType() {
                        return Date.class;
                    }
                    
                    @Override
                    public String convertToString(Object o, Locale locale) {
	                        Date d = (Date) o;
	                        return df.format(d);
                    }                    
                };
            }
            @Override
            public IModel getLabel() {
                return YuiCalendar.this.getLabel();
            }
        };
        dateField.setOutputMarkupId(true);
        dateField.setRequired(required);
        dateField.add(new ErrorHighlighter());
        add(dateField);

        final WebMarkupContainer button = new WebMarkupContainer("button");
        button.setOutputMarkupId(true);
        button.add(new AttributeModifier("onclick", true, new AbstractReadOnlyModel() {
            public Object getObject() {
            	String js = "showCalendar(" + getCalendarId() + ", '" + getInputId() + "');";
            	logger.info("YuiCalendar button onclick js: "+js);
            	
                return js;
            }
        }));
        add(button);
        
        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);
    }        
    
    @Override
    public void updateModel() {
        dateField.updateModel();
    }
    
    private String getCalendarId() {
        return getMarkupId();
    }
    
    private String getInputId() {
        return dateField.getMarkupId();
    }
    
    private String getContainerId() {
        return container.getMarkupId();
    }

    public String getDateValueAsString(){
    	return value;
    }
    
    public Date getDateValue(){
    	return dateValue;
    }
        
    public void renderHead(IHeaderResponse response) {       
        String calendarId = getCalendarId();
        response.renderOnDomReadyJavaScript("init" + calendarId + "()");
        response.renderJavaScript(
                  "function init" + calendarId + "() { "
                + calendarId + " = new YAHOO.widget.Calendar('" + calendarId + "', '" + getContainerId() + "'); "
                + calendarId + ".selectEvent.subscribe(handleSelect, [ " + calendarId + ", '" + getInputId() + "' ], true); }", null);        
    }        
}
