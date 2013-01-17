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

public class AssetTypeCustomAttributeSearch extends AbstractSearch implements Serializable{
	private static final long serialVersionUID = 1L;
	private AssetTypeCustomAttribute assetTypeCustomAttribute;

	private String name;
	private String mappingKey;
	private ValuePair active;
	private ValuePair mandatory;
	private Integer formType;
	

	//---------------------------------------------------------------------------------------------

	public AssetTypeCustomAttributeSearch(Component c) {
		super(c);
		this.sortFieldName = "name";
		this.assetTypeCustomAttribute = new AssetTypeCustomAttribute();
	}
	public AssetTypeCustomAttributeSearch(int pageSize) {
		super(pageSize);
		this.sortFieldName = "name";
		this.assetTypeCustomAttribute = new AssetTypeCustomAttribute();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public DetachedCriteria getDetachedCriteria(){
		DetachedCriteria criteria = DetachedCriteria.forClass(AssetTypeCustomAttribute.class);

		if (this.name!=null){
			this.assetTypeCustomAttribute.setName(name);
			criteria.add(Restrictions.ilike("name", this.assetTypeCustomAttribute.getName(), MatchMode.START));
		}
		if (this.mappingKey!=null){
			this.assetTypeCustomAttribute.setMappingKey(mappingKey);
			criteria.add(Restrictions.eq("mappingKey", this.assetTypeCustomAttribute.getMappingKey()));
		}
		
		if (this.formType!=null){
			this.assetTypeCustomAttribute.setFormType(formType);
			criteria.add(Restrictions.eq("formType", this.assetTypeCustomAttribute.getFormType()));
		}

		if (this.mandatory!=null){
			this.assetTypeCustomAttribute.setMandatory(mandatory.getValue().equals("1"));
			criteria.add(Restrictions.eq("mandatory", this.assetTypeCustomAttribute.isMandatory()));
		}
		
		if (this.active!=null){
			this.assetTypeCustomAttribute.setActive(active.getValue().equals("1"));
			criteria.add(Restrictions.eq("active", this.assetTypeCustomAttribute.isActive()));
		}
		
		if (isSortDescending()){
			criteria.addOrder(Order.desc(this.sortFieldName));
		}//if
		else{
			criteria.addOrder(Order.asc(this.sortFieldName));
		}//else

		return criteria;
	}//getDetachedCriteria
	
	//--------------------------------------------------------------------------------------------------
	
	public DetachedCriteria getDetachedCriteriaForCount(){
		DetachedCriteria criteria = DetachedCriteria.forClass(AssetTypeCustomAttribute.class);
		criteria = this.getDetachedCriteria();
		
		return criteria;
		
	}//getDetachedCriteriaForCount

	//--------------------------------------------------------------------------------------------------

	public List<String> getColumnHeaders(){
		List<String> columnHeadings = new LinkedList<String>();
		
		columnHeadings.add("name");
		columnHeadings.add("formType");
		columnHeadings.add("mandatory");
		columnHeadings.add("active");

		return columnHeadings;
	}//getColumnHeaders
	
	//---------------------------------------------------------------------------------------------
	
	public CustomAttribute getSearchObject() {
		return this.assetTypeCustomAttribute;
	}//getAssetObject


	//---------------------------------------------------------------------------------------------
	public ValuePair getActive() {
		return active;
	}

	//---------------------------------------------------------------------------------------------
	
	public void setActive(ValuePair active) {
		this.active = active;
	}

	//---------------------------------------------------------------------------------------------
	
	public ValuePair getMandatory() {
		return mandatory;
	}

	//---------------------------------------------------------------------------------------------
	
	public void setMandatory(ValuePair mandatory) {
		this.mandatory = mandatory;
	}

	//---------------------------------------------------------------------------------------------
	
	public String getName() {
		return name;
	}

	//---------------------------------------------------------------------------------------------
	
	public void setName(String name) {
		this.name = name;
	}
	
	//---------------------------------------------------------------------------------------------

	public String getMappingKey() {
		return mappingKey;
	}
	public void setMappingKey(String mappingKey) {
		this.mappingKey = mappingKey;
	}
	public Integer getFormType() {
		return formType;
	}

	//---------------------------------------------------------------------------------------------
	
	public void setFormType(Integer formType) {
		this.formType = formType;
	}
	
}//AssetTypeCustomAttributeSearch
