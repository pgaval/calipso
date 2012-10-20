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

public class AssetTypeSearch extends AbstractSearch implements Serializable {
	private static final long serialVersionUID = 1L;

	private AssetType assetType;

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public AssetTypeSearch(AssetType assetType, Component c) {
		super(c);
		this.assetType = assetType;
		this.sortFieldName = "name";
	}// AssetTypeSearch

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public AssetType getSearchObject() {
		return this.assetType;
	}

	// -----------------------------------------------------------------------------------------------------------------------------

	public List<String> getColumnHeaders() {
		List<String> columnHeadings = new LinkedList<String>();

		columnHeadings.add("name");

		return columnHeadings;
	}// getColumnHeaders

	// -----------------------------------------------------------------------------------------------------------------------------

	public DetachedCriteria getDetachedCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AssetType.class);

		if (this.assetType.getName() != null) {
			criteria.add(Restrictions.ilike("name", this.assetType.getName(), MatchMode.START));
		}// if

		if (isSortDescending()) {
			criteria.addOrder(Order.desc(this.sortFieldName));
		}// if
		else {
			criteria.addOrder(Order.asc(this.sortFieldName));
		}// else

		return criteria;
	}// getDetachedCriteria

	// -----------------------------------------------------------------------------------------------------------------------------

	public DetachedCriteria getDetachedCriteriaForCount() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AssetType.class);

		criteria = this.getDetachedCriteria();

		return criteria;
	}// getDetachedCriteriaForCount

}
