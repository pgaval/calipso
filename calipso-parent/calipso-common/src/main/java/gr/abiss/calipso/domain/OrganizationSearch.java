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

public class OrganizationSearch extends AbstractSearch implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Organization organization;

	public OrganizationSearch(Organization organization, Component c) {
		super(c);
		this.organization = organization;
		this.sortFieldName = "name";
	}

	@Override
	public List<String> getColumnHeaders() {
		List<String> columnHeadings = new LinkedList<String>();
		
		columnHeadings.add("name");
		columnHeadings.add("address");
		columnHeadings.add("zip");
		columnHeadings.add("country");
		columnHeadings.add("phone");
		columnHeadings.add("web");

		return columnHeadings;
	}

	private DetachedCriteria getCriteriaBase() {
		DetachedCriteria criteria = DetachedCriteria.forClass(Organization.class);

		if (this.organization.getName()!=null){
			criteria.add(Restrictions.ilike("name", this.organization.getName(), MatchMode.START));
		}

		if (this.organization.getAddress()!=null){
			criteria.add(Restrictions.ilike("address", this.organization.getAddress(), MatchMode.START));
		}

		if (this.organization.getZip()!=null){
			criteria.add(Restrictions.ilike("zip", this.organization.getZip(), MatchMode.START));
		}
		if (this.organization.getPhone()!=null){
			criteria.add(Restrictions.ilike("phone", this.organization.getPhone(), MatchMode.START));
		}

		if (this.organization.getWeb()!=null){
			criteria.add(Restrictions.ilike("web", this.organization.getWeb(), MatchMode.START));
		}
		return criteria;
	}
	
	@Override
	public DetachedCriteria getDetachedCriteria() {
		DetachedCriteria criteria = this.getCriteriaBase();
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
		DetachedCriteria criteria = this.getCriteriaBase();
		return criteria;
	}

	@Override
	public Organization getSearchObject() {
		return this.organization;
	}
	
	
	

	
}
