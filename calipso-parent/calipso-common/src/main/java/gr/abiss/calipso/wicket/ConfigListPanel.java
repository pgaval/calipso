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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

/**
 * config list
 */
public class ConfigListPanel extends BasePanel {
	
	private static final long serialVersionUID = 1L;
	private String selectedParam;
	
    public String getTitle(){
        return localize("config_list.configurationSettings");
    }
      
	public void setSelectedParam(String selectedParam) {
		this.selectedParam = selectedParam;
	}
    
	public ConfigListPanel(String id, final IBreadCrumbModel breadCrumbModel) {
		this(id, breadCrumbModel, null);		
	}
	
    public ConfigListPanel(String id, final IBreadCrumbModel breadCrumbModel, final String selectedParam) {                           
    	super(id, breadCrumbModel);
    	
    	this.selectedParam = selectedParam;
    	
        final Map<String, String> configMap = getCalipso().loadAllConfig();
        
        List<String> params = new ArrayList(Config.getParams());
        
        final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
        
        add(new ListView("configs", params) {
            protected void populateItem(ListItem listItem) {
                final String param = (String) listItem.getModelObject();
                final String value = configMap.get(param);
                if (param.equals(ConfigListPanel.this.selectedParam)) {
                    listItem.add(new SimpleAttributeModifier("class", "selected"));
                } else if(listItem.getIndex() % 2 == 1) {
                    listItem.add(sam);
                }                
                listItem.add(new Label("param", param));
                listItem.add(new Label("value", value));
                listItem.add(new BreadCrumbLink("link", breadCrumbModel){
                	protected IBreadCrumbParticipant getParticipant(String componentId){
                		return new ConfigFormPanel(componentId, breadCrumbModel, param, value);
                	}
                });
                listItem.add(new Label("description", localize("config." + param)));
            }
        });
        
        
        final CalipsoPropertiesEditor cpr = new CalipsoPropertiesEditor();
        
        add(new ListView("configs_", new ArrayList(cpr.getParams())) {
            protected void populateItem(ListItem listItem) {
                final String param = (String) listItem.getModelObject();
                final String value = cpr.getValue(param);
                if (param.equals(ConfigListPanel.this.selectedParam)) {
                    listItem.add(new SimpleAttributeModifier("class", "selected"));
                } else if(listItem.getIndex() % 2 == 1) {
                    listItem.add(sam);
                }                
                listItem.add(new Label("param_", param));
                listItem.add(new Label("value_", value));
                listItem.add(new BreadCrumbLink("link_", breadCrumbModel){
                	
					private static final long serialVersionUID = 1L;

					protected IBreadCrumbParticipant getParticipant(String componentId){
                		return new ConfigFormPanel(componentId, breadCrumbModel, param, value, cpr);
                	}
                });
                listItem.add(new Label("description_", localize("config." + param)));
            }
        });
        
    }
    
}
