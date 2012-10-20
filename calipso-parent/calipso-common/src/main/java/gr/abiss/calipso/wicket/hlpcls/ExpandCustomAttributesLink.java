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

import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.wicket.asset.AssetTypeCustomAttributeValues;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;

public class ExpandCustomAttributesLink extends IndicatingAjaxLink implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(ExpandCustomAttributesLink.class);

	private boolean isExpanded = false; 
	private MarkupContainer componentWhenCollapsed;
	private MarkupContainer componentWhenExpanded;
	private MarkupContainer targetComponent;
	private MarkupContainer imageWhenCollapsed;
	private MarkupContainer imageWhenExpanded;
	private IBreadCrumbModel breadCrumbModel;
	private Asset asset;
	
	public ExpandCustomAttributesLink(String id, Asset asset) {
		super(id);
		this.asset = asset;
	}
	public ExpandCustomAttributesLink(String id,IBreadCrumbModel breadCrumbModel, Asset asset) {
		super(id);
		this.breadCrumbModel = breadCrumbModel;
		this.asset = asset;
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	
	public boolean isExpanded() {
		return isExpanded;
	}

	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}

	public MarkupContainer getComponentWhenCollapsed() {
		return componentWhenCollapsed;
	}

	public void setComponentWhenCollapsed(MarkupContainer componentWhenCollapsed) {
		this.componentWhenCollapsed = componentWhenCollapsed;
	}

	public MarkupContainer getComponentWhenExpanded() {
		return componentWhenExpanded;
	}

	public void setComponentWhenExpanded(MarkupContainer componentWhenExpanded) {
		this.componentWhenExpanded = componentWhenExpanded;
	}

	public MarkupContainer getTargetComponent() {
		return targetComponent;
	}

	public void setTargetComponent(MarkupContainer targetComponent) {
		this.targetComponent = targetComponent;
	}

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public MarkupContainer getImageWhenCollapsed() {
		return imageWhenCollapsed;
	}

	public void setImageWhenCollapsed(MarkupContainer imageWhenCollapsed) {
		this.imageWhenCollapsed = imageWhenCollapsed;
	}

	public MarkupContainer getImageWhenExpanded() {
		return imageWhenExpanded;
	}

	public void setImageWhenExpanded(MarkupContainer imageWhenExpanded) {
		this.imageWhenExpanded = imageWhenExpanded;
	}
	
	//////////////////////////////////////////////////////////////////////////////////

	private void setComponentWhenExpanded(){
		AssetTypeCustomAttributeValues assetTypeCustomAttributeValues = new AssetTypeCustomAttributeValues(componentWhenCollapsed.getId(),breadCrumbModel, asset.getId());
		assetTypeCustomAttributeValues.setOutputMarkupId(true);
		
		componentWhenExpanded = assetTypeCustomAttributeValues;
	}//setComponentWhenExpanded

	//////////////////////////////////////////////////////////////////////////////////
	@Override
	public void onClick(AjaxRequestTarget target) {
		if (targetComponent!=null){
			//Collapse
			if (isExpanded){
				if (componentWhenExpanded==null){
					setComponentWhenExpanded();
				}//if
				componentWhenExpanded.replaceWith(componentWhenCollapsed);
				
				ExpandCustomAttributesLink.this.remove(imageWhenExpanded);
				ExpandCustomAttributesLink.this.add(imageWhenCollapsed);
				
				componentWhenExpanded = null;
				isExpanded = false; 
			}
			else{//Expand
				if (componentWhenExpanded==null){
					setComponentWhenExpanded();
				}//if				
				componentWhenCollapsed.replaceWith(componentWhenExpanded);

				ExpandCustomAttributesLink.this.remove(imageWhenCollapsed);
				ExpandCustomAttributesLink.this.add(imageWhenExpanded);

				isExpanded = true;
			}

			target.addComponent(targetComponent);
			target.addComponent(ExpandCustomAttributesLink.this);
		}//if
	}//onClick
}