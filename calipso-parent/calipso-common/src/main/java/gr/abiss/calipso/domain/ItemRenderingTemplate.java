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

package gr.abiss.calipso.domain;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Instances of ItemRenderingTemplate are used to persist view rendering 
 * templates for Items. They are connected to a single space 
 * and multiple SpaceRoles. 
 * 
 * A SpaceRole has a collection of ItemRenderingTemplate instances 
 * stored in a map, using Item States as the map key, allowing to reuse 
 * a template in multiple role/state combinations.
 */
public class ItemRenderingTemplate extends RenderingTemplate {

	private Short priority = 0;
	private Boolean showSpaceName = Boolean.FALSE;
	private Boolean hideOverview = Boolean.FALSE;
	private Boolean hideHistory = Boolean.FALSE;

	private Space space;
	private String description;


	public ItemRenderingTemplate() {
		super();
	}

	public ItemRenderingTemplate(ItemRenderingTemplate templateFrom) {
		super(templateFrom);
		this.priority = templateFrom.getPriority();
		this.hideOverview = templateFrom.getHideOverview();
		this.hideHistory = templateFrom.getHideHistory();
		this.description = templateFrom.getDescription();
		
	}

	public Short getPriority() {
		return priority;
	}

	public void setPriority(Short priority) {
		this.priority = priority;
	}
	

	public Boolean getShowSpaceName() {
		return showSpaceName;
	}

	public void setShowSpaceName(Boolean showSpaceName) {
		this.showSpaceName = showSpaceName;
	}

	public Boolean getHideOverview() {
		return hideOverview;
	}

	public void setHideOverview(Boolean hideOverview) {
		this.hideOverview = hideOverview;
	}

	public Boolean getHideHistory() {
		return hideHistory;
	}

	public void setHideHistory(Boolean hideHistory) {
		this.hideHistory = hideHistory;
	}
	

	public Space getSpace() {
		return space;
	}
	
	public void setSpace(Space space) {
		this.space = space;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString(){
	 return new ToStringBuilder(this)
	  	.appendSuper(super.toString())
	     .append("description", this.getDescription())
	     .append("priority", this.getPriority())
	     .append("hideOverview", this.getHideOverview())
				.append("hideHistory", this.getHideHistory())
				.append("templateText (abbreviated)",
						StringUtils.abbreviate(this.getTemplateText(), 20))
	     .toString();
	}
	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
	        return true;
	    if (obj == null)
	        return false;
	    if (!(obj instanceof ItemRenderingTemplate))
	        return false;
	    final ItemRenderingTemplate other = (ItemRenderingTemplate) obj;
	    if (getId() == null) {
	        if (other.getId() != null)
	            return false;
	    } else if (!getId().equals(other.getId()))
	        return false;
	    if (getDescription() == null) {
	        if (other.getDescription() != null)
	            return false;
	    } else if (!getDescription().equals(other.getDescription()))
	        return false;
	    return true;
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
	    result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
	    return result;
	}

}
