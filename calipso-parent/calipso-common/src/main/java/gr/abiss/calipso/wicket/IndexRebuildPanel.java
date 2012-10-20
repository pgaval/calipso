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

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.AbstractItem;
import gr.abiss.calipso.util.BreadCrumbUtils;

import java.util.List;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.time.Duration;

/**
 * rebuild indexes admin option
 */
public class IndexRebuildPanel extends BasePanel {      
	
	@Override
	public String getTitle() {
		return localize("index_rebuild.heading");
	}
	
	public IndexRebuildPanel(String id, IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);
    	
        add(new Label("heading", localize("index_rebuild.heading")));
        add(new RebuildIndexesForm("form"));
    }
	    
    /**
     * wicket form
     */    
    private class RebuildIndexesForm extends Form {
        
        private int current;
        private int total;
        private boolean finished;        
        
        public RebuildIndexesForm(String id) {
            
            super(id);
            
            final Label progress = new Label("progress");
            progress.setOutputMarkupId(true);               
            
            add(new Button("start") {
                @Override
                public void onSubmit() {
                    // hide the button
                    this.setVisible(false);
                    // long running process, use thread
                    new Thread() {
                        // don't serialize this!
                        private transient CalipsoService calipsoService = getCalipso();
                        public void run() {
                            calipsoService.clearIndexes();
                            List<AbstractItem> items = calipsoService.findAllItems();                            
                            total = items.size();
                            for(current = 0; current < total; current++) {
                                calipsoService.index(items.get(current));
                            }
                            finished = true;
                        }                    
                    }.start();
                    
                    // poll and update the progress every 5 seconds
                    final AjaxSelfUpdatingTimerBehavior ajax = new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1));
                    progress.add(ajax);
                    IModel model = new AbstractReadOnlyModel() {
                        public Object getObject() {
                            if(finished) {
                                ajax.stop();
                            	return localize("index_rebuild_success.message");
                            }
                            int percent = total == 0 ? 0 : 100 * current / total;
                            return percent + "% [" + current + " / " + total + "]";
                        };
                    };
                    progress.setDefaultModel(model);             
                }
            });

            add(progress);            
        }
        
    }
    
}
