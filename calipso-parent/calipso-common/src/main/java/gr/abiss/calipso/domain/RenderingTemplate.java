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

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * @author manos
 */
public class RenderingTemplate implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final Short LANGUAGE_VELOCITY = 1;
	public static final Short LANGUAGE_FREEMARKER = 2;

	public static final Short DOMAIN_ASSET = 1;
	public static final Short DOMAIN_DASHBOARD = 2;
	public static final Short DOMAIN_ITEM_STATE = 3;
	
	private Long id;
	private Short templateLanguage = LANGUAGE_VELOCITY;
	//private Short templateDomain;
	
	private String templateText;
	
	public RenderingTemplate() {
		// default constructor
	}

	
	public RenderingTemplate(RenderingTemplate templateFrom) {
		this.templateLanguage = templateFrom.getTemplateLanguage();
		this.templateText = templateFrom.getTemplateText();
	}
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the templateType
	 */
	public Short getTemplateLanguage() {
		return templateLanguage;
	}
	/**
	 * @param templateType the templateType to set
	 */
	public void setTemplateLanguage(Short templateType) {
		this.templateLanguage = templateType;
	}
	/**
	 * @return the templateText
	 */
	public String getTemplateText() {
		return templateText;
	}
	/**
	 * @param templateText the templateText to set
	 */
	public void setTemplateText(String templateText) {
		this.templateText = templateText;
	}
	
	public String toString() {
		
	     return new ToStringBuilder(this)
		     .append("id", this.getId())
		     .toString();
	   }

	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
	        return true;
	    if (obj == null)
	        return false;
	    if (!(obj instanceof RenderingTemplate))
	        return false;
	    final RenderingTemplate other = (RenderingTemplate) obj;
	    if (getId() == null) {
	        if (other.getId() != null)
	            return false;
	    } else if (!getId().equals(other.getId()))
	        return false;
	    if (getTemplateText() == null) {
	        if (other.getTemplateText() != null)
	            return false;
	    } else if (!getTemplateText().equals(other.getTemplateText()))
	        return false;
	    return true;
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
	    result = prime * result + ((getTemplateText() == null) ? 0 : getTemplateText().hashCode());
	    return result;
	}
	
	
}
