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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

/**
 * custom wicketized yahoo ui dialog widget
 */
public class YuiDialog extends Panel {                
        
    public static final String CONTENT_ID = "content";        
    private WebMarkupContainer dialog;
    private String heading;    

    public String getHeading() {
        return heading;
    }
    
    public void setHeading(String heading) {
        this.heading = heading;
    }
    
    public YuiDialog(String id) {
        super(id);
        add(new Behavior(){
		      public void renderHead(Component component, IHeaderResponse response) {
		    	  response.renderJavaScriptReference("resources/yui/yahoo/yahoo-min.js", "yui-yahoo");
		    	  response.renderJavaScriptReference("resources/yui/event/event-min.js", "yui-event");
		    	  response.renderJavaScriptReference("resources/yui/dom/dom-min.js", "yui-dom");
		    	  response.renderJavaScriptReference("resources/yui/dragdrop/dragdrop-min.js", "yui-dragdrop");
		    	  response.renderJavaScriptReference("resources/yui/container/container-min.js", "yui-container");
		    	  response.renderCSSReference("resources/yui/container/assets/container.css");
		      }
		});
        
        
        setOutputMarkupId(true);  // for Wicket Ajax
        dialog = new WebMarkupContainer("dialog"); 
        dialog.setOutputMarkupId(true); // for Yahoo Dialog 
        dialog.setVisible(false);
        add(dialog);                        
        dialog.add(new Label("heading", new PropertyModel(this, "heading")));        
        dialog.add(new WebMarkupContainer(CONTENT_ID));      
    }         
    
    public void show(AjaxRequestTarget target, String h, Component content) {
        this.heading = h;
        target.addComponent(this); 
        dialog.setVisible(true);        
        dialog.replace(content);        
        final String markupId = dialog.getMarkupId();
        // using the contributor and the onDomReady Wicket helper handles the rare case that
        // the dialog is visible and the user refreshes the backing page (possible as dialog is not modal!)
        // so in that special case, this javascript is called at page load
        // but in the usual Ajax request case, this behaves just like AjaxRequestTarget.appendJavaScript()
        add(new Behavior() {
            public void renderHead(IHeaderResponse response) {
                response.renderOnDomReadyJavaScript("var " + markupId + " = new YAHOO.widget.ResizeDialog('" + markupId + "', " 
                + " { constraintoviewport : true }); " 
                + markupId + ".render(); " + markupId + ".show();");
            }
        });
    }    
    
}
