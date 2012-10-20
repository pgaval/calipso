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

import gr.abiss.calipso.util.BreadCrumbUtils;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author erasmus
 * 
 * Back to the previous page, if exists. 
 * Uses BreadCrumbModel.
 */
public class BackLinkPanel extends Panel {
	BreadCrumbLink link;
	Component icon;
	Component label;

	public BackLinkPanel(final String id, final IBreadCrumbModel breadCrumbModel) {
		super(id);

		//if page is the root or if breadCrumbModel==null
		if (breadCrumbModel == null || breadCrumbModel.allBreadCrumbParticipants().size() < 1) {
			link = null;
			add(new WebMarkupContainer("link").setVisible(false));
			return;
		}

		//add the back link
		link = new BreadCrumbLink("link", breadCrumbModel) {
			@Override
			protected IBreadCrumbParticipant getParticipant(String componentId) {
				// the previous page
				return (IBreadCrumbParticipant) BreadCrumbUtils
						.backBreadCrumbPanel(breadCrumbModel);
			}
		};
		
		icon = new Fragment("icon", "backIcon", this);
		link.add(icon);
		
		//the label, default is "back"
		label = new Label("label", ComponentUtils.localize(this, "back")).setRenderBodyOnly(true);
		link.add(label);		
		
		add(link);
	}

	//WARNING: link id must be "link"
	public void setLink(BreadCrumbLink link) {
		if(this.link == null || link == null){
			return;
		}
				
		remove(this.link);
		add(link);
		this.link = link;
	}
	
	public BackLinkPanel makeBack(BreadCrumbLink link) {
		//add link
		setLink(link);
		
		//if no link
		if(this.link == null){
			return this;
		}
		
		//the icon
		this.link.remove(this.icon);//remove old icon
		icon = new Fragment("icon", "backIcon", this);//create back icon
		this.link.add(icon);//add back icon
		
		//the label
		this.link.remove(this.label);//remove old label
		label = new Label("label", ComponentUtils.localize(this, "back")).setRenderBodyOnly(true);//create back label
		this.link.add(label);//add back label
		
		return this;
	}
	public BackLinkPanel makeBack() {
		makeBack(null);		
		return this;
	}
	
	public BackLinkPanel makeCancel(BreadCrumbLink link) {
		//add link
		setLink(link);
		
		//if no link
		if(this.link == null){
			return this;
		}
		
		//the icon
		this.link.remove(this.icon);//remove old icon
		icon = new Fragment("icon", "cancelIcon", this);//create cancel icon
		this.link.add(icon);//add cancel icon
		
		//the label
		this.link.remove(this.label);//remove old label
		label = new Label("label", ComponentUtils.localize(this, "cancel")).setRenderBodyOnly(true);//create cancel label
		this.link.add(label);//add cancel label
		
		return this;
	}
	public BackLinkPanel makeCancel() {
		makeCancel(null);
		return this;
	}
}

