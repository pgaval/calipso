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


import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.hibernate.criterion.DetachedCriteria;

/**
 * Inherits stuff from AbstractSearch but uses HQL instead of criteria, 
 * Unfortunately criteria does not support querying into collections.
 */
public abstract class AbstractQuerySearch extends AbstractSearch implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(AbstractQuerySearch.class);
	
	public AbstractQuerySearch(Component c) {
		super(c);
	}
	
	public AbstractQuerySearch(Object assetObject, String sortFieldName) {
		super(assetObject, sortFieldName);
	}

	/**
	 * Holds the HQL positional parameters (?)
	 */
	protected List<Serializable> params;

	/**
	 * Get the HQL query string
	 * @return the HQL query string
	 */
	public abstract String getQueryString();

	/**
	 * Get the HQL positional parameters (? replacements)
	 * @return the HQL parameters
	 */
	public List<Serializable> getParams(){
		return params;
	}
	
	/**
	 * Leftovers from AbstractSearch
	 */
	public DetachedCriteria getDetachedCriteria(){
		throw new NotImplementedException("Instances of "+getClass()+" use HQL instead of Criteria");
	}
	
	/**
	 * Leftovers from AbstractSearch
	 */
	public DetachedCriteria getDetachedCriteriaForCount(){
		throw new NotImplementedException("Instances of "+getClass()+" use HQL instead of Criteria");
	}
}
