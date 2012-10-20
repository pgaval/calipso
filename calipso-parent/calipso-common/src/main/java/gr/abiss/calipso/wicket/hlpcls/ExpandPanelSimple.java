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

package gr.abiss.calipso.wicket.hlpcls;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;

public abstract class ExpandPanelSimple extends AjaxLink{

	private boolean isOpen = false;
	private MarkupContainer targetComponent;
	private MarkupContainer referencePanel;
	private MarkupContainer panelContainer;
	private boolean justHide = false;
	
	////////////////////////////////////////////////////////////////
	
	public ExpandPanelSimple(String id, IModel model) {
		super(id, model);
	}

	//---------------------------------------------------------------------------------------------
	
	public ExpandPanelSimple(String id) {
		super(id);
	}

	//---------------------------------------------------------------------------------------------
	
	public ExpandPanelSimple(String id, boolean isOpen,
			MarkupContainer targetComponent, MarkupContainer referencePanel) {
		
		super(id);
		this.isOpen = isOpen;
		this.targetComponent = targetComponent;
		this.referencePanel = referencePanel;
	}

	//---------------------------------------------------------------------------------------------
	
	public ExpandPanelSimple(String id, MarkupContainer targetComponent,
			MarkupContainer referencePanel, MarkupContainer panelContainer) {
		super(id);
		this.targetComponent = targetComponent;
		this.referencePanel = referencePanel;
		this.panelContainer = panelContainer;
	}	
	
	//---------------------------------------------------------------------------------------------
	
	public ExpandPanelSimple(String id, MarkupContainer targetComponent, MarkupContainer referencePanel){
		super(id);

		this.targetComponent = targetComponent;
		this.referencePanel = referencePanel;

		justHide = true;
	}
	
	////////////////////////////////////////////////////////////////

	public abstract void onLinkClick();

	////////////////////////////////////////////////////////////////

	@Override
	public void onClick(AjaxRequestTarget target) {
		if (!justHide){
			//Close Panel
			if (isOpen){
				targetComponent.remove(referencePanel);
				targetComponent.add(panelContainer);
				isOpen = false;
			} 
			else{//Open panel
				targetComponent.remove(panelContainer);
				targetComponent.add(referencePanel);
				isOpen = true;
			}
		}
		else{
			isOpen = referencePanel.isVisible();
			referencePanel.setVisible(!isOpen);
		}
		onLinkClick();
		target.addComponent(targetComponent);
	}
	
}
