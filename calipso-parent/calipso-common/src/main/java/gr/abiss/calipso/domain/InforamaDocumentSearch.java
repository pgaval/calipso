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

import gr.abiss.calipso.dto.AbstractSearch;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class InforamaDocumentSearch extends AbstractSearch implements Serializable{

	private InforamaDocument inforamaDocument;
	
	public InforamaDocumentSearch(InforamaDocument inforamaDocument, Component component){
		super(component);
		this.inforamaDocument = inforamaDocument;
		this.sortFieldName = "webServiceURL";
	}
	
	@Override
	public List<String> getColumnHeaders() {
		List<String> columnHeadings = new LinkedList<String>();
		
		columnHeadings.add("webServiceURL");
		columnHeadings.add("commandDescription");
		columnHeadings.add("projectName");
		columnHeadings.add("documentName");
		columnHeadings.add("pageDictionary");

		return columnHeadings;
	}

	@Override
	public DetachedCriteria getDetachedCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(InforamaDocument.class);

		if (this.inforamaDocument.getWebServiceURL()!=null){
			criteria.add(Restrictions.ilike("webServiceURL", this.inforamaDocument.getWebServiceURL(), MatchMode.START));
		}
		
		if (this.inforamaDocument.getProjectName()!=null){
			criteria.add(Restrictions.ilike("projectName", this.inforamaDocument.getProjectName(), MatchMode.START));
		}
		
		if (this.inforamaDocument.getDocumentName()!=null){
			criteria.add(Restrictions.ilike("documentName", this.inforamaDocument.getDocumentName(), MatchMode.START));
		}
		
		if (isSortDescending()) {
			criteria.addOrder(Order.desc(this.sortFieldName));
		}// if
		else {
			criteria.addOrder(Order.asc(this.sortFieldName));
		}// else
		
		return criteria;
	}

	@Override
	public DetachedCriteria getDetachedCriteriaForCount() {
		return this.getDetachedCriteria();
	}

	@Override
	public Object getSearchObject() {
		return this.inforamaDocument;
	}

	
}
