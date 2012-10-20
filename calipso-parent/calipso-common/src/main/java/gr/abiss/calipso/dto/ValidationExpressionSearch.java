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

package gr.abiss.calipso.dto;

import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.ValidationExpression;
import gr.abiss.calipso.dto.AbstractSearch;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.Component;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * 
 *
 */
public class ValidationExpressionSearch extends AbstractSearch implements Serializable{
	private ValidationExpression validationExpression;
	public ValidationExpressionSearch(ValidationExpression validationExpression, Component c){
		super(c);
		this.validationExpression = validationExpression;
		this.sortFieldName = "name";
	}
	@Override
	public List<String> getColumnHeaders() {
		List<String> columnHeadings = new LinkedList<String>();
		columnHeadings.add("name");
		columnHeadings.add("description");
		columnHeadings.add("expression");
		
		return columnHeadings;
	}

	@Override
	public DetachedCriteria getDetachedCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ValidationExpression.class);

		if (this.validationExpression.getName()!=null){
			criteria.add(Restrictions.ilike("name", this.validationExpression.getName(), MatchMode.START));
		}
		if (this.validationExpression.getDescription()!=null){
			criteria.add(Restrictions.ilike("description", this.validationExpression.getDescription(), MatchMode.START));
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
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ValidationExpression.class);
		
		criteria = this.getDetachedCriteria();

		return criteria;
	}

	@Override
	public Object getSearchObject() {
		return this.validationExpression;
	}

}
