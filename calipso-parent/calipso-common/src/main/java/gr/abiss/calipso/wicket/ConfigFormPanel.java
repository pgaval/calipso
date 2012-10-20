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

package gr.abiss.calipso.wicket;

import gr.abiss.calipso.config.CalipsoPropertiesEditor;
import gr.abiss.calipso.domain.Config;
import gr.abiss.calipso.util.BreadCrumbUtils;

import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 * config value edit form
 */
public class ConfigFormPanel extends BasePanel {       
	String param;
	CalipsoPropertiesEditor calipsoPropertiesEditor;
    
    public String getTitle(){
        return param;
    }
	
    public ConfigFormPanel(String id, final IBreadCrumbModel breadCrumbModel, String param, String value) { 
    	super(id, breadCrumbModel);
    	
    	this.param = param;
    	
    	setupVisuals();
    	
        add(new ConfigForm("form", param, value));
    }
    
    public ConfigFormPanel(String id, final IBreadCrumbModel breadCrumbModel, String param, String value, CalipsoPropertiesEditor cpr) { 
    	super(id, breadCrumbModel);
    	
    	this.calipsoPropertiesEditor = cpr;
    	this.param = param;
    	
    	setupVisuals();
    	
        add(new ConfigForm("form", param, value));
    }

    //call it after "param" property has taken a value
	private void setupVisuals() {
		//Title label
		add(new Label("headingTitle", param));
		//make back link a Cancel
    	getBackLinkPanel().makeCancel();
		
	    //highlight this asset's spaceId on previous (the active one) page ================================
		//when this object is created it is not in breadcrumb, so the previous page is the active one
		BreadCrumbPanel previous = (BreadCrumbPanel) getBreadCrumbModel().getActive();
		
		if (previous instanceof ConfigListPanel) {
			((ConfigListPanel) previous).setSelectedParam(param);
		}
	}
	
    /**
     * wicket form
     */
    private class ConfigForm extends Form {                
        
        private String param;
        
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }         
        
        public ConfigForm(String id, final String param, final String value) {
            
            super(id);                             
            
            this.param = param;
            this.value = value;
            
            final CompoundPropertyModel model = new CompoundPropertyModel(this);
            setModel(model);
            
            add(new Label("heading", localize("config." + param)));
            add(new Label("param", param).setRenderBodyOnly(true));

            add(new TextField("value"));
        }
                
        @Override
        protected void onSubmit() {       
        	if(calipsoPropertiesEditor != null){//if editing calipso.properties file
        		calipsoPropertiesEditor.setValue(param, value);
        		calipsoPropertiesEditor.save();        		
        	}
        	else{//if editing properties from database
	            getCalipso().storeConfig(new Config(param, value));
        	}
        	
            //remove previous and create a new one to reload values
            BreadCrumbUtils.removePreviousBreadCrumbPanel(getBreadCrumbModel());
            
            activate(new IBreadCrumbPanelFactory(){
				public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
					return new ConfigListPanel(componentId, breadCrumbModel, param);
				}	
            });
        	
        }     
                        
    }        
    
}
