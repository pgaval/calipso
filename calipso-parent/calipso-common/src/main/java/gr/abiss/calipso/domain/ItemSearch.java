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
 * 
 * This file incorporates work released by the JTrac project and  covered 
 * by the following copyright and permission notice:  
 * 
 *   Copyright 2002-2005 the original author or authors.
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *   
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package gr.abiss.calipso.domain;

import gr.abiss.calipso.Constants;
import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.ColumnHeading.Name;
import gr.abiss.calipso.wicket.ComponentUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import static gr.abiss.calipso.domain.ColumnHeading.Name.ASSIGNED_TO;
import static gr.abiss.calipso.domain.ColumnHeading.Name.DETAIL;
import static gr.abiss.calipso.domain.ColumnHeading.Name.SUMMARY;
import static gr.abiss.calipso.domain.ColumnHeading.Name.ID;
import static gr.abiss.calipso.domain.ColumnHeading.Name.LOGGED_BY;
import static gr.abiss.calipso.domain.ColumnHeading.Name.SPACE;
import static gr.abiss.calipso.domain.ColumnHeading.Name.STATUS;


/**
 * Object that holds filter criteria when searching for Items
 * and also creates a Hibernate Criteria query to pass to the DAO
 */
public class ItemSearch implements Serializable {
	
	private static final Logger logger = Logger.getLogger(ItemSearch.class);
        
    private Space space; // if null, means aggregate across all spaces
    private User user; // this will be set in the case space is null
    
    private int pageSize;
    private int currentPage;
    private long resultCount;
    private String sortFieldName = "id";
    private boolean sortDescending = true;
    private boolean showHistory;
    private boolean showDetail;
    
    private long selectedItemId;	
    private String relatingItemRefId;    
    private Collection<Long> itemIds;
    
    private List<ColumnHeading> columnHeadings;
    private Map<String, FilterCriteria> filterCriteriaMap = new LinkedHashMap<String, FilterCriteria>();

    private String defaultVisibleFlags;
    
    public ItemSearch(User user, Component c) {
        this.user = user;
        // finds custom fields for users and store them to user's roleSpaceStdFieldList
        user.setRoleSpaceStdFieldList(ComponentUtils.getCalipso(c).findSpaceFieldsForUser(user));
        
        this.columnHeadings = ColumnHeading.getColumnHeadings(user, c);
        this.defaultVisibleFlags = getVisibleFlags();
        pageSize = ComponentUtils.getCalipso(c).getRecordsPerPage();
    }
    
    public ItemSearch(Space space, User user, Component c, CalipsoService calipso) {        
        this.space = space;
        //logger.info("Item search created with space: "+space+", user: "+user);
        if (user.getId()==0){//Non-loggedin Guest (Anonymous)
//        	u.setRoleSpaceStdFieldList(ComponentUtils.getJtrac(c).findSpaceFieldsBySpaceAndRole(space, "ROLE_GUEST"));
        	SpaceRole anonymous = new SpaceRole(0, space,  RoleType.ANONYMOUS);
        	user.setRoleSpaceStdFieldList(ComponentUtils.getCalipso(c).findSpaceFieldsBySpaceandRoleType(anonymous));
        }
        else{
        	user.setRoleSpaceStdFieldList(ComponentUtils.getCalipso(c).findSpaceFieldsForUser(user));
        }
        this.columnHeadings = ColumnHeading.getColumnHeadings(space, user, c, calipso);
        this.defaultVisibleFlags = getVisibleFlags();
        //ComponentUtils.getJtrac(c).findSpaceFieldsForUser(user)
        pageSize= ComponentUtils.getCalipso(c).getRecordsPerPage();
    }      
    
    public void initFromPageParameters(PageParameters params, User user, CalipsoService calipsoService) {       
        showHistory = params.get("showHistory").toBoolean(false);
        showDetail = params.get("showDetail").toBoolean(false);
        if(showDetail) {
            getColumnHeading(DETAIL).setVisible(true);
        }
        if(this.space != null && (!space.isItemSummaryEnabled())){
        	ColumnHeading summaryHeading = getColumnHeading(SUMMARY);
        	if(summaryHeading != null){
        		summaryHeading.setVisible(false);
        	}
        }
        pageSize = params.get("pageSize").toInt(calipsoService.getRecordsPerPage());
        sortDescending = !params.get("sortAscending").toBoolean();
        sortFieldName = params.get("sortFieldName").toString("id");        
        for(Object o : params.getNamedKeys()) {
        	logger.info("processing parameter: "+0);
            String name = o.toString();
            if(ColumnHeading.isValidFieldOrColumnName(name)) {
                ColumnHeading ch = getColumnHeading(name);
                ch.loadFromQueryString(params.get(name).toString(), user, calipsoService);
            }
        }
        relatingItemRefId = params.get("relatingItemRefId").toString(null);
    }
    
    public PageParameters getAsQueryString() {
    	PageParameters params = new PageParameters();
    	//Map<String, String> map = new HashMap<String, String>();
        if(space != null) {
            params.add("s", space.getId() + "");
        }  
        for(ColumnHeading ch : columnHeadings) {
            String s = ch.getAsQueryString();
            if(s != null) {
                params.add(ch.getNameText(), s);
            }           
        } 
        String visibleFlags = getVisibleFlags();
        if(!visibleFlags.equals(defaultVisibleFlags)) {
            params.add("cols", visibleFlags.toString());
        }        
        if(showHistory) {
            params.add("showHistory", "true");
        }
        if(pageSize != 25) {
            params.add("pageSize", pageSize + "");
        }
        if(!sortDescending) {
            params.add("sortAscending", "true");
        }
        if(!sortFieldName.equals("id")) {
            params.add("sortFieldName", sortFieldName);
        }
        if(relatingItemRefId != null) {
            params.add("relatingItemRefId", relatingItemRefId);
        }
        
        
        return params;
    }
    
    private String getVisibleFlags() {
        StringBuilder visibleFlags = new StringBuilder();
        for(ColumnHeading ch : columnHeadings) {
            if(ch.isVisible()) {
                visibleFlags.append("1");                
            } else  {
                visibleFlags.append("0");
            }            
        } 
        return visibleFlags.toString();
    }
    
    private DetachedCriteria parent; // temp working variable h
    
    public List<ColumnHeading> getGroupByHeadings(){
		List<ColumnHeading> optionHeadings = new LinkedList<ColumnHeading>();
		for(ColumnHeading heading : this.columnHeadings){
			Field field = heading.getField();
        	//logger.info("heading: "+heading);
        	//logger.info("heading field: "+heading.getField());
			if(field != null && field.getName() != null &&  field.getName().isOptionsType() 
					/*|| heading.getField().getName().isUser() || heading.getField().getName().isCountry() || heading.getField().getName().isOrganization()*/){
				optionHeadings.add(heading);
			}
		}
		return optionHeadings;
    }
    
    public List<DetachedCriteria> getGroupByCriteria(){
		List<DetachedCriteria> criteriaList = new LinkedList();
		List<ColumnHeading> optionHeadings = getGroupByHeadings();
		if(CollectionUtils.isNotEmpty(optionHeadings)){
			for(ColumnHeading heading : optionHeadings){
				DetachedCriteria criteria = this.getCriteria();
				criteria.setProjection( Projections.projectionList()
			            .add( Projections.groupProperty(heading.getNameText()) )
			            .add( Projections.rowCount(), "rowCount")
			        ).addOrder(Order.desc("rowCount"));
				criteriaList.add(criteria);
				
			}
		}
		return criteriaList;
    }
    
    
    
    
    /**
     * Get the item search criteria. This re-uses the count criteria by just adding 
     * the order-by clauses.
     */
    public DetachedCriteria getCriteria() {
        DetachedCriteria criteria = getCriteriaForCount();
        if (sortFieldName == null) { // can happen only for multi-space search
            sortFieldName = "id"; // effectively is a sort on created date
        }
        if(sortFieldName.equals("id") || sortFieldName.equals("space")) {
            if(showHistory) {
                // if showHistory: sort by item.id and then history.id

                if(space == null) {
                    DetachedCriteria parentSpace = parent.createCriteria("space");
                    parentSpace.addOrder(Order.desc("name"));                        
                }                    
                criteria.addOrder(sortDescending?Order.desc("parent.id"):Order.asc("parent.id"));
                criteria.addOrder(sortDescending?Order.desc("id"):Order.asc("id"));

            } else {

                if(space == null) {
                    DetachedCriteria parentSpace = criteria.createCriteria("space");
                    parentSpace.addOrder(Order.asc("name"));
                }                    
                criteria.addOrder(sortDescending?Order.desc("id"):Order.asc("id"));
                
            }
        } else {        
            if (sortDescending) {
                criteria.addOrder(Order.desc(sortFieldName));
            } else {
                criteria.addOrder(Order.asc(sortFieldName));
            } 
        }
        return criteria;
    }
    
    public DetachedCriteria getCriteriaForCount() {               
        DetachedCriteria criteria = null; 
        // what spaces is this search about?
        Collection<Space> spaces;
        if(space != null){
        	spaces = new ArrayList<Space>();
        	spaces.add(space);
        }
        else{
        	spaces = getSelectedSpaces();
        }
        
        if (showHistory) {
            criteria = DetachedCriteria.forClass(History.class);           
            // apply restrictions to parent, this is an inner join =============
            parent = criteria.createCriteria("parent");
            // visibility criteria for item
        	parent.add(getVisibilityCriteriaForItem(spaces));
        	// look for specific IDs?
            if (itemIds != null) {
                parent.add(Restrictions.in("id", itemIds));
            }             
        } else {
            criteria = DetachedCriteria.forClass(Item.class);
            // visibility criteria for item
        	criteria.add(getVisibilityCriteriaForItem(spaces));
        	// look for specific IDs?
            if (itemIds != null) {
                criteria.add(Restrictions.in("id", itemIds));
            }             
        }
        
        for(ColumnHeading ch : columnHeadings) {
        	if (ch.isDbField()){
        		ch.addRestrictions(criteria);
        	}
        }
        return criteria;
    }

	/**
	 * Get item visibility criteria for the spaces included in the search
	 * @param spaces
	 * @return
	 */
	private Disjunction getVisibilityCriteriaForItem(Collection<Space> spaces) {
		Disjunction itemRestrictions = Restrictions.disjunction();
		List<Space> includedSpaces = new LinkedList<Space>();
		for(Space space : spaces){
			Short visibility = space.getItemVisibility();
			boolean isLoggedIn =  user.getId() != 0;
			boolean hasRegularRoleForSpace = user.hasRegularRoleForSpace(space);
			// for spaces allowing item view for anonymous users
			if(visibility.equals(Space.ITEMS_VISIBLE_TO_ANONYMOUS_USERS)){
				itemRestrictions.add(Restrictions.eq("space", space));
				includedSpaces.add(space);
				//logger.debug("Included space '" + space.getName() + "' since it allows anonymous");
				
			}
			// for spaces allowing item view for loggedin users
			else if(isLoggedIn && visibility.equals(Space.ITEMS_VISIBLE_TO_ANY_LOGGEDIN_USER) ){
				itemRestrictions.add(Restrictions.eq("space", space));
				//logger.debug("Included space '" + space.getName() + "' since the user is logged in");
			}
			// for spaces allowing item view for item reporters
			else if(isLoggedIn 
					&& (visibility.equals(Space.ITEMS_VISIBLE_TO_LOGGEDIN_REPORTERS)
							|| visibility.equals(Space.ITEMS_VISIBLE_TO_LOGGEDIN_REPORTERS_NO_COMMENTS))){
				if(hasRegularRoleForSpace){
					itemRestrictions.add(Restrictions.eq("space", space));
					//logger.debug("Included space '" + space.getName() + "' since the user is a regular user");
				}
				else{
					itemRestrictions.add(
							Restrictions.conjunction()
								.add(Restrictions.eq("space", space))
								.add(Restrictions.eq("reportedBy", user)));
				}
				//logger.debug("Included space '" + space.getName() + "' but only for items reported by user");
			}
			// for spaces allowing item view users with explicit roles only
			else if(isLoggedIn /* && visibility.equals(Space.ITEMS_VISIBLE_TO_REGULAR_ROLES) */
					&& hasRegularRoleForSpace){
				itemRestrictions.add(Restrictions.eq("space", space));
				//logger.debug("Included space '" + space.getName() + "' since the user is a regular user");
			}
			else{
				//itemRestrictions.add(Restrictions.ne("space", space));
				//logger.debug("Did NOT include space '" + space.getName()+" user has regular roles for space: "+hasRegularRoleForSpace+", space visibility: "+space.getItemVisibility());
			}
		}
		return itemRestrictions;
	}

    
    public CustomCriteria getCustomCriteria(){
    	CustomCriteria criteria = new CustomCriteria(CustomCriteria.AND);

        for(ColumnHeading ch : columnHeadings) {
        	if (!ch.isDbField()){
        		ch.addRestrictions(criteria);
        	}//if
        }//for
    	
    	return criteria;
    }//getCustomCriteria

    
    public List<Field> getFields() {
        return space != null? space.getMetadata().getFieldList() : new LinkedList<Field>();
    }    
    
    private ColumnHeading getColumnHeading(Name name) {
        for(ColumnHeading ch : columnHeadings) {
            if(ch.getName() == name) {
                return ch;                
            }
        }
        return null;                
    }
    
    private ColumnHeading getColumnHeading(String name) {
        for(ColumnHeading ch : columnHeadings) {
            if(ch.getNameText().equals(name)) {
                return ch;                
            }
        }
        return null;                
    }
    
    private String getStringValue(ColumnHeading ch) {
        String s = (String) ch.getFilterCriteria().getValue();
        if(s == null || s.trim().length() == 0) {            
            ch.getFilterCriteria().setExpression(null);
            return null;
        }       
        return s;        
    }
    
    
    
    public String getRefId() {
        ColumnHeading ch = getColumnHeading(ID);
        return getStringValue(ch);
    }
    
    public String getSearchText() {
        ColumnHeading ch = getColumnHeading(DETAIL);
        return getStringValue(ch);
    }
    
    public Collection<Space> getSelectedSpaces() {
        ColumnHeading ch = getColumnHeading(SPACE);
        List values = ch.getFilterCriteria().getValues();
        if(values == null || values.size() == 0) {
            ch.getFilterCriteria().setExpression(null);
            return user.getSpaces();
        }
        return values;
    }
           
    public void toggleSortDirection() {
        sortDescending = !sortDescending;
    }      
    
    private List getSingletonList(Object o) {
        List list = new ArrayList(1);
        list.add(o);
        return list;
    }
    
    public void setLoggedBy(User loggedBy) {
        ColumnHeading ch = getColumnHeading(LOGGED_BY);
        ch.getFilterCriteria().setExpression(FilterCriteria.Expression.IN);
        ch.getFilterCriteria().setValues(getSingletonList(loggedBy));
    }
    
    public void setAssignedTo(User assignedTo) {
        ColumnHeading ch = getColumnHeading(ASSIGNED_TO);
        ch.getFilterCriteria().setExpression(FilterCriteria.Expression.IN);
        ch.getFilterCriteria().setValues(getSingletonList(assignedTo));
    }
    
    public void setUnassigned(){
    	ColumnHeading ch = getColumnHeading(ASSIGNED_TO);
    	ch.getFilterCriteria().setExpression(FilterCriteria.Expression.IS_NULL);
    	ch.getFilterCriteria().setValue(null);
    } 
    
    public void setStatus(int i) {
        ColumnHeading ch = getColumnHeading(STATUS);
        ch.getFilterCriteria().setExpression(FilterCriteria.Expression.IN);
        ch.getFilterCriteria().setValues(getSingletonList(i));
    }
    
    public List<ColumnHeading> getColumnHeadingsToRender() {
        List<ColumnHeading> list = new ArrayList<ColumnHeading>(columnHeadings.size());
        for(ColumnHeading ch : columnHeadings) {
            if(ch.isVisible()) {
                list.add(ch);
            }
        }
        return list;
    }
    
    //==========================================================================

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getResultCount() {
        return resultCount;
    }

    public void setResultCount(long resultCount) {
        this.resultCount = resultCount;
    }

    public String getSortFieldName() {
        return sortFieldName;
    }

    public void setSortFieldName(String sortFieldName) {
        this.sortFieldName = sortFieldName;
    }

    public boolean isSortDescending() {
        return sortDescending;
    }

    public void setSortDescending(boolean sortDescending) {
        this.sortDescending = sortDescending;
    }

    public boolean isShowHistory() {
        return showHistory;
    }

    public void setShowHistory(boolean showHistory) {
        this.showHistory = showHistory;
    }

    public boolean isShowDetail() {
        return showDetail;
    }

    public void setShowDetail(boolean showDetail) {
        this.showDetail = showDetail;
    }

    public long getSelectedItemId() {
        return selectedItemId;
    }

    public void setSelectedItemId(long selectedItemId) {
        this.selectedItemId = selectedItemId;
    }

    public String getRelatingItemRefId() {
        return relatingItemRefId;
    }

    public void setRelatingItemRefId(String relatingItemRefId) {
        this.relatingItemRefId = relatingItemRefId;
    }

    public Collection<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(Collection<Long> itemIds) {
        this.itemIds = itemIds;
    }

    public List<ColumnHeading> getColumnHeadings() {
        List<ColumnHeading> list = new ArrayList<ColumnHeading>(columnHeadings.size());
        for(ColumnHeading ch : columnHeadings) {
            //if(ch.isVisibleCriterium()) {
                list.add(ch);
            //}//if
        }//for
        logger.info("Returning columns: "+list.size());
        return list;
    	
//        return columnHeadings;
    }

    public void setColumnHeadings(List<ColumnHeading> columnHeadings) {
        this.columnHeadings = columnHeadings;
    }

    public Map<String, FilterCriteria> getFilterCriteriaMap() {
        return filterCriteriaMap;
    }

    public void setFilterCriteriaMap(Map<String, FilterCriteria> filterCriteriaMap) {
        this.filterCriteriaMap = filterCriteriaMap;
    }
    
}