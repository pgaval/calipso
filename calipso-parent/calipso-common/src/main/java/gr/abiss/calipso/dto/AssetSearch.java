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

import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.Space;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;

/**
 * This class is essentially an Asset search command implemented in HQL. It uses HQL and inherits the AbstractQuerySearch
 * simply because querying collections (in this case the AssetTypeCustomAttributes) is unsupported by Criteria API queries
 */
public class AssetSearch extends AbstractQuerySearch implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AssetSearch.class);
	// HQL params stuff
	private Asset asset;
	private Date startDateFrom;
	private Date startDateTo;
	private Date endDateFrom;
	private Date endDateTo;
	private Map<AssetTypeCustomAttribute, String> customAttributes = null;
	public boolean searchForCustomAttributes = false;
	private List<Space> searchableSpaces;

	public AssetSearch(Asset asset, String sortField){
		super(asset, sortField);
		init(asset);
	}
	
	public AssetSearch(Asset asset, Component c){
		super(c);
		init(asset);
	}
	

	private void init(Asset asset) {
		this.asset = asset;
		if(this.sortFieldName == null){
			this.sortFieldName = "assetType";
		}
		this.searchForCustomAttributes = (this.asset.getAssetType()!=null && hasCustomAttributesCriteria());
	}
	
	@Override
	public Asset getSearchObject() {
		return this.asset;
	}
	
	@Override
	public List<String> getColumnHeaders() {
		List<String> columnHeadings = new LinkedList<String>();
		columnHeadings.add("assetType");
		columnHeadings.add("inventoryCode");
		columnHeadings.add("supportStartDate");
		columnHeadings.add("supportEndDate");
		columnHeadings.add("space");				
		return columnHeadings;
	}
	@Override
	public String getQueryString() {
		StringBuffer query = new StringBuffer();
		params = new LinkedList<Serializable>();
		query.append("from Asset asset ");
		// join and fetch custom attributes if the search has conditions for those
		this.searchForCustomAttributes = (this.asset.getAssetType()!=null && hasCustomAttributesCriteria());
		//if(this.searchForCustomAttributes){
			query.append("left join fetch asset.customAttributes as customAttribute ");//join fetch customAttribute.index as attrIndex
		//}
		
		// space condition
		List<Space> spaceList = new LinkedList<Space>();
		if (this.asset.getSpace() == null){
			if(this.searchableSpaces != null && this.searchableSpaces.size() > 0){
				spaceList.addAll(searchableSpaces);
			}
		}
		else{
			spaceList.add(this.asset.getSpace());
		}
		// HQL "asset.space.id in (?)" wont work with query.setParameter(index, list)
		//  as hibernate tries to cast the list to long
		if(spaceList != null && spaceList.size() > 0){
			query.append(params.isEmpty()?"where (":"and (");
			Iterator<Space> spaceIter = spaceList.iterator();
			while(spaceIter.hasNext()){
				Space searchableSpace = spaceIter.next();
				
				// add space in search
				query.append("asset.space.id = ? or asset.space.assetVisibility = ")
				.append(Space.ASSETS_VISIBLE_TO_ANY_SPACE)
				.append(" ");
				params.add(new Long(searchableSpace.getId()));
				
				// add space group
				if(searchableSpace.getSpaceGroup() != null 
						&& searchableSpace.getAssetVisibility()
							.equals(Space.ASSETS_VISIBLE_TO_SPACEGROUP_SPACES)){
					query.append(" or (asset.space.spaceGroup.id = ? and asset.space.assetVisibility = ")
						.append(Space.ASSETS_VISIBLE_TO_SPACEGROUP_SPACES)
						.append(") ");
					params.add(searchableSpace.getSpaceGroup().getId());
				}
				if(spaceIter.hasNext()){
					query.append(" or ");
				}
			}
			query.append(") ");
		}
		
		
		// inventory code condition
		if (this.asset.getInventoryCode()!=null){
			query.append(params.isEmpty()?"where ":"and ").append("upper(asset.inventoryCode) like ? ");
			params.add("%" + this.asset.getInventoryCode().toUpperCase()+"%");
		}

		// support start date condition
		if (this.startDateFrom!=null){
			query.append(params.isEmpty()?"where ":"and ").append("asset.supportStartDate >= ? ");
			params.add(this.startDateFrom);
			//criteria.add(Restrictions.ge("supportStartDate", this.startDateFrom));
		}
		if (this.startDateTo!=null){
			query.append(params.isEmpty()?"where ":"and ").append("asset.supportStartDate <= ? ");
			params.add(this.startDateTo);
			//criteria.add(Restrictions.le("supportStartDate", this.startDateTo));
		}

		// support end date condition
		if (this.endDateFrom!=null){
			query.append(params.isEmpty()?"where ":"and ").append("asset.supportEndDate >= ? ");
			params.add(this.endDateFrom);
			//criteria.add(Restrictions.ge("supportEndDate", this.endDateFrom));
		}
		if (this.endDateTo!=null){
			query.append(params.isEmpty()?"where ":"and ").append("asset.supportEndDate <= ? ");
			params.add(this.endDateTo);
			// wrong/typo -> criteria.add(Restrictions.le("supportStartDate", this.endDateTo));
		}
		
		// created by
		if (this.asset.getCreatedBy()!=null){
			query.append(params.isEmpty()?"where ":"and ").append("asset.createdBy = ? ");
			params.add(this.asset.getCreatedBy());
		}

		// asset type and custom attributes conditions
		if (this.asset.getAssetType()!=null){
			query.append(params.isEmpty()?"where ":"and ").append("asset.assetType = ? ");
			params.add(this.asset.getAssetType());
			//criteria.add(Restrictions.eq("assetType", this.asset.getAssetType()));

			if (hasCustomAttributesCriteria()){
				StringBuffer customAttributeConditions = new StringBuffer();
				customAttributeConditions.append("and (");
				//DetachedCriteria innerCriteria = criteria.createAlias("customAttributes", "customAttribute");
				Iterator<AssetTypeCustomAttribute> attributesIterator = customAttributes.keySet().iterator();
				//Disjunction disjunction = Restrictions.disjunction();
				int customAttrsIndex = 0;
				while(attributesIterator.hasNext()){
					AssetTypeCustomAttribute customAttribute = attributesIterator.next();
					String value = customAttributes.get(customAttribute);
					boolean hasValue = value != null && value.length() > 0 && !value.equals("-1");
					if (customAttribute.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)){
						hasValue = hasValue && !value.equals("-1");
					}
					if (hasValue){
						customAttributeConditions.append(customAttrsIndex > 0 ?"and ":"")
								.append("(index(customAttribute) = ? and customAttribute like ?) ");
						params.add(customAttribute);
						params.add(value);
						customAttrsIndex++;
						//Criterion criterion = Restrictions.conjunction()
						//.add(Restrictions.eq("index(customAttribute)", customAttribute))
						//.add(Restrictions.eq("customAttribute", value));
						//disjunction.add(criterion);
						//criteria.add(criterion);
					}//if
				}//while
				//innerCriteria.add(disjunction);
				if(customAttrsIndex > 0){
					query.append(customAttributeConditions).append(") ");
				}
			}//if
		}//if
		query.append(" order by asset.")
			.append(this.sortFieldName)
			.append(" ")
			.append(isSortDescending()?"desc ":"asc ");
		/*
		if (isSortDescending()){
			criteria.addOrder(Order.desc(this.sortFieldName));
		}//if
		else{
			criteria.addOrder(Order.asc(this.sortFieldName));
		}//else

		return criteria;
		*/
		String queryString = query.toString();
		if(logger.isInfoEnabled()){
			logger.info("Asset search query: "+queryString);
			logger.info("Asset search params: "+params);
		}
		return queryString;
	}//getDetachedCriteria
	
	
	private boolean hasCustomAttributesCriteria(){
		if (customAttributes!=null){
			Iterator<AssetTypeCustomAttribute> attributeValuesIterator = customAttributes.keySet().iterator();
			while(attributeValuesIterator.hasNext()){
				AssetTypeCustomAttribute customAttribute = attributeValuesIterator.next();
				String value = customAttributes.get(customAttribute);
				boolean hasValue = value!=null && value.length()>0;
				if (customAttribute.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)){
					hasValue = hasValue && !value.equals("-1");
				}//if
				if (hasValue){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param spaceChoices
	 */
	public void setSearchableSpaces(List<Space> spaces) {
		this.searchableSpaces = spaces;
	}

	

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public Date getStartDateFrom() {
		return startDateFrom;
	}

	public void setStartDateFrom(Date startDateFrom) {
		this.startDateFrom = startDateFrom;
	}

	public Date getStartDateTo() {
		return startDateTo;
	}

	public void setStartDateTo(Date startDateTo) {
		this.startDateTo = startDateTo;
	}

	public Date getEndDateFrom() {
		return endDateFrom;
	}

	public void setEndDateFrom(Date endDateFrom) {
		this.endDateFrom = endDateFrom;
	}

	public Date getEndDateTo() {
		return endDateTo;
	}

	public void setEndDateTo(Date endDateTo) {
		this.endDateTo = endDateTo;
	}	
	

	public Map<AssetTypeCustomAttribute, String> getAttributeValues() {
		return customAttributes;
	}

	public void setAttributeValues(
			Map<AssetTypeCustomAttribute, String> attributeValues) {
		this.customAttributes = attributeValues;
	}	
}
