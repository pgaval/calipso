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

public class PageDictionarySearch extends AbstractSearch implements Serializable{

	private PageDictionary pageDictionary; 
	
	public PageDictionarySearch(PageDictionary pageDictionary, Component component){
		super(component);
		this.pageDictionary = pageDictionary;
		this.sortFieldName = "pageDescription";
	}
	
	@Override
	public List<String> getColumnHeaders() {
		List<String> columnHeadings = new LinkedList<String>();

		columnHeadings.add("pageDescription");
		columnHeadings.add("pageClassName");
		columnHeadings.add("localizedKey");

		return columnHeadings;
	}

	@Override
	public DetachedCriteria getDetachedCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(PageDictionary.class);

		if (this.pageDictionary.getPageDescription()!=null){
			criteria.add(Restrictions.ilike("pageDescription", this.pageDictionary.getPageDescription(), MatchMode.START));
		}

		if (this.pageDictionary.getPageClassName()!=null){
			criteria.add(Restrictions.ilike("pageClassName", this.pageDictionary.getPageClassName(), MatchMode.START));
		}

		if (this.pageDictionary.getPageClassName()!=null){
			criteria.add(Restrictions.ilike("localizedKey", this.pageDictionary.getLocalizedKey(), MatchMode.START));
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
		return this.pageDictionary;
	}

}
