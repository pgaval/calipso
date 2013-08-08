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

package gr.abiss.calipso.hibernate;

import gr.abiss.calipso.CalipsoDao;
import gr.abiss.calipso.domain.AbstractItem;
import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.AssetTypeCustomAttributeSearch;
import gr.abiss.calipso.domain.AssetTypeSearch;
import gr.abiss.calipso.domain.Attachment;
import gr.abiss.calipso.domain.ColumnHeading;
import gr.abiss.calipso.domain.Config;
import gr.abiss.calipso.domain.Country;
import gr.abiss.calipso.domain.Counts;
import gr.abiss.calipso.domain.CountsHolder;
import gr.abiss.calipso.domain.CustomAttribute;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.I18nStringIdentifier;
import gr.abiss.calipso.domain.I18nStringResource;
import gr.abiss.calipso.domain.InforamaDocument;
import gr.abiss.calipso.domain.InforamaDocumentParameter;
import gr.abiss.calipso.domain.InforamaDocumentParameterSearch;
import gr.abiss.calipso.domain.InforamaDocumentSearch;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.ItemFieldCustomAttribute;
import gr.abiss.calipso.domain.ItemItem;
import gr.abiss.calipso.domain.ItemRenderingTemplate;
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.Language;
import gr.abiss.calipso.domain.Metadata;
import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.OrganizationSearch;
import gr.abiss.calipso.domain.PageDictionary;
import gr.abiss.calipso.domain.PageDictionarySearch;
import gr.abiss.calipso.domain.PageInforamaDocument;
import gr.abiss.calipso.domain.PageInforamaDocumentSearch;
import gr.abiss.calipso.domain.RoleSpaceStdField;
import gr.abiss.calipso.domain.RoleType;
import gr.abiss.calipso.domain.SavedSearch;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.SpaceGroup;
import gr.abiss.calipso.domain.SpaceRole;
import gr.abiss.calipso.domain.SpaceSequence;
import gr.abiss.calipso.domain.State;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.UserSpaceRole;
import gr.abiss.calipso.domain.ValidationExpression;
import gr.abiss.calipso.domain.i18n.I18nResourceTranslatable;
import gr.abiss.calipso.dto.AssetSearch;
import gr.abiss.calipso.wicket.components.validators.RegexpValidator;
import gr.abiss.calipso.wicket.regexp.ValidationExpressionSearch;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


/**
 * DAO Implementation using Spring Hibernate template
 * note usage of the Spring "init-method" and "destroy-method" optionsItem
 */
public class HibernateDao extends HibernateDaoSupport implements CalipsoDao {
	
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(HibernateDao.class);
	private static final ConcurrentMap<Long, Metadata> metadataCache = new ConcurrentHashMap<Long, Metadata>();
	
    private SchemaHelper schemaHelper;
    
    public void setSchemaHelper(SchemaHelper schemaHelper) {
        this.schemaHelper = schemaHelper;
    }

    /**
     * 
     */
	@Override
	public Set<SpaceGroup> getSpaceGroupsForUser(Serializable userId){
		User user = (User) getHibernateTemplate().load(User.class, userId);
        @SuppressWarnings("unchecked")
        // TODO
        // List<SpaceGroup> groupList = getHibernateTemplate().find("from SpaceGroup sg join fetch sg.admins as admin where admin.id = ?", user.getId());
        List<SpaceGroup> groupList = getHibernateTemplate().find("from SpaceGroup sg");
        Set<SpaceGroup> users = new HashSet<SpaceGroup>();
        if(groupList != null && groupList.size() > 0){
        	users.addAll(groupList);
        }
        return users;
	}

    
    @Override
	public Serializable save(Serializable o) {
        return getHibernateTemplate().save(o);
    }

    
    @Override
	public void saveOrUpdate(Serializable o) {
        getHibernateTemplate().saveOrUpdate(o);
    }

    
    @Override
	public void update(Serializable o) {
        getHibernateTemplate().update(o);
    }
    

    @Override
	public void refresh(Serializable o) {
        getHibernateTemplate().refresh(o);
    }
    
    
    @Override
	public Object get(Class entityClass, Serializable id) {
        return getHibernateTemplate().get(entityClass, id);
    }
    
    @Override
	public void saveOrUpdateAll(Collection all) {
    	if(all != null && all.size() > 0){
    		for(Object o : all){
    			getHibernateTemplate().merge(o);
    		}
    	}
    }

    
    @Override
	public void merge(Serializable o) {
        getHibernateTemplate().merge(o);
    }
    
    @Override
	public void storeItem(Item item) {
        getHibernateTemplate().merge(item);
    }
    
    @Override
	public Item loadItem(long id) {
        return (Item) getHibernateTemplate().get(Item.class, id);
    }
    
    @Override
	public void storeHistory(History history) {        
        getHibernateTemplate().merge(history);
    }    
    
    @Override
	public History loadHistory(long id) {
        return (History) getHibernateTemplate().get(History.class, id);
    }

    @Override
	public List<Item> findItems(long sequenceNum, String prefixCode) {
        Object[] params = new Object[] {sequenceNum, prefixCode};
        return getHibernateTemplate().find("from Item item where item.sequenceNum = ? and item.space.prefixCode = ?", params);
    }

	/** {@inheritDoc} */
	@Override
    public List<ItemRenderingTemplate> getItemRenderingTemplates(Space space) {
    	//getHibernateTemplate().merge(space);
    	//return space.getItemRenderingTemplates();
		List<ItemRenderingTemplate> tmpls = space.getItemRenderingTemplates();
		if(space.getId() > 0){
			tmpls = getHibernateTemplate().find("from ItemRenderingTemplate tmpl where tmpl.space.id = ?", new Object[] {space.getId()});
		}
		if(tmpls == null){
			tmpls = new LinkedList<ItemRenderingTemplate>();
		}
		return tmpls;
    }
	

	/** {@inheritDoc} */
	@Override
    public ItemRenderingTemplate getItemRenderingTemplateForUser(final User user, final Integer itemStatus, final Long spaceId){
		if(user == null){
			throw new IllegalArgumentException("User cannot be null");
		}
		if(itemStatus == null){
			throw new IllegalArgumentException("Item status cannot be null");
		}
		if(spaceId == null){
			throw new IllegalArgumentException("Space id cannot be null");
		}
        return (ItemRenderingTemplate) getHibernateTemplate().execute(new HibernateCallback() {
            @Override
			public Object doInHibernate(Session session) {
//            	TreeSet<ItemRenderingTemplate> templs = new TreeSet<ItemRenderingTemplate>();
//            	if(CollectionUtils.isNotEmpty(user.getSpaceRoles(item.getSpace()))){
//            		Query query = session.createQuery("from ");
//            	}
            	String queryString = "select distinct tmpl from SpaceRole as spaceRole " +
        				"	join spaceRole.itemRenderingTemplates as tmpl " +
        				"where spaceRole.space.id = :spaceId " + 
        				" and spaceRole in(:spaceRoles) and index(tmpl) = :itemStatus "+
        				"order by tmpl.priority asc";
        		Query query = session.createQuery(queryString);
        		//logger.info("item space: "+item.getSpace());
        		query.setLong("spaceId", spaceId);
        		query.setString("itemStatus", itemStatus.toString());
        		query.setParameterList("spaceRoles", user.getSpaceRoles(spaceId));
        		query.setMaxResults(1);
        		
        		// TODO: A hack until we implement the custom attribute subclass hierarchy.
        		ItemRenderingTemplate tmpl = null;
        		List<ItemRenderingTemplate> templates = query.list();
        		//logger.info("Got "+templates.size()+" results for quesry: "+queryString);
        		if(CollectionUtils.isNotEmpty(templates)){
        			tmpl = templates.get(0);
        		}
        		return tmpl;
            }
        });
    }
    
    @Override
	public List<Item> findItems(ItemSearch itemSearch) {
        int pageSize = itemSearch.getPageSize();
        if (pageSize == -1) {
            List<Item> list = getHibernateTemplate().findByCriteria(itemSearch.getCriteria());
            itemSearch.setResultCount(list.size());
            return list;
        } else {
            // pagination
            int firstResult = pageSize * itemSearch.getCurrentPage();
            List<Item> list = getHibernateTemplate().findByCriteria(itemSearch.getCriteria(), firstResult, pageSize);
            List<Item> finalList = null;
            DetachedCriteria criteria = itemSearch.getCriteriaForCount();
            criteria.setProjection(Projections.rowCount());
            Long count = (Long) getHibernateTemplate().findByCriteria(criteria).get(0);
            itemSearch.setResultCount(count);
            return list;
        }
    }
    
    @Override
	public Map<String, List> findItemGroupByTotals(ItemSearch itemSearch) {
		Map<String, List> results = new HashMap<String, List>();
		List<ColumnHeading> optionHeadings = itemSearch.getGroupByHeadings();
		if(CollectionUtils.isNotEmpty(optionHeadings)){
			DetachedCriteria criteria = itemSearch.getCriteria();
			for(ColumnHeading heading : optionHeadings){
				criteria.setProjection( Projections.projectionList()
			            .add( Projections.groupProperty(heading.getNameText()) )
			            .add( Projections.rowCount(), "rowCount")
			        ).addOrder(Order.desc("rowCount"));
				List queryResults = getHibernateTemplate().findByCriteria(criteria);
				results.put(heading.getNameText(), queryResults);
				
			}
		}
		return results;	
	}
    
    @Override
	public List<AbstractItem> findAllItems() {
        // return getHibernateTemplate().loadAll(AbstractItem.class);
        return (List<AbstractItem>) getHibernateTemplate().execute(new HibernateCallback() {
            @Override
			public Object doInHibernate(Session session) {
                Criteria criteria = session.createCriteria(AbstractItem.class);
                criteria.setFetchMode("space", FetchMode.JOIN);                              
                return criteria.list();
            }
        });        
    }

    /**
     * Returns an iterator for items due within 24 hours.
     */
	@Override
	public Iterator<Item> findItemsDueIn24Hours(){
		// get a calendar instance, which defaults to "now"
	    Calendar calendar = Calendar.getInstance();
	    // add one day to the date/calendar
	    calendar.add(Calendar.DAY_OF_YEAR, 1);
	    // now get "tomorrow"
	    Date thisTimeTommorow = calendar.getTime();
	    // we prefer an iterator VS a list or other collection as it is more efficient for larger datasets and batch jobs in general
    	return getHibernateTemplate().iterate("from Item item where item.sentDueToNotifications = false and (item.dueTo < ? or item.stateDueTo < ? )", new Object[]{thisTimeTommorow, thisTimeTommorow});
	}
    @Override
	public List<History> findHistoryForItem(Item item){
    	return getHibernateTemplate().find("from History history where history.parent.id = ?", item.getId());
    } 

    @Override
	public void removeItem(Item item) {
        getHibernateTemplate().delete(item);
    }    
    
    @Override
	public List<Item> findUnassignedItemsForSpace(Space space){
    	return getHibernateTemplate().find("from Item item where item.space.id=? and item.assignedTo is null", space.getId());
    }
    
    @Override
	public int loadCountUnassignedItemsForSpace(Space space){
    	Long count = (Long) getHibernateTemplate().find("select count(item) from Item item where item.space.id=? and item.assignedTo is null", space.getId()).get(0);
    	return count.intValue();
    }
    
    @Override
	public void removeItemItem(ItemItem itemItem) {
        getHibernateTemplate().delete(itemItem);
    }    
    
    @Override
	public List<Attachment> findTemporaryAttachments(){
    	return getHibernateTemplate().find("from Attachment attachment where attachment.temporary=true");
    }
    
    @Override
	public void removeAttachment(Attachment attachment) {
        getHibernateTemplate().delete(attachment);
    }
    
    @Override
	public void storeAttachment(Attachment attachment) {
        getHibernateTemplate().merge(attachment);
    }
    
//    public void storeMetadata(Metadata metadata) {
//        getHibernateTemplate().merge(metadata);
//    }
    
//    public Metadata loadMetadata(long id) {
//        return (Metadata) getHibernateTemplate().get(Metadata.class, id);
//    }
//    
    @Override
	public Space storeSpace(Space space) {
    	Map<String, Map<String, String>> translations = space.getId() > 0 ? space.getTranslations() :null;
    	logger.info("storeSpace "+space.getId()+", translations: "+translations);
    	// get the field list before persisting or the custom attributes will be lost
    	List<Field> fieldList = space.getMetadata().getFieldList();
    	if(space.getId() > 0){
	    	Metadata freshMeta = (Metadata) getHibernateTemplate().get(Metadata.class, space.getMetadata().getId());
	    	freshMeta.setXmlString(space.getMetadata().getXmlString());
	        space.setMetadata(freshMeta);
    	}
    	// explicitly merge templates first or the cascade has bug https://hibernate.onjira.com/browse/HHH-3332
    	/*if(space.getId() > 0){
    		if(CollectionUtils.isNotEmpty(space.getItemRenderingTemplates())){
        		//logger.info("mergin templates: "+space.getItemRenderingTemplates());
        		for(ItemRenderingTemplate tmpl : space.getItemRenderingTemplates()){
        			//logger.info("tmpl: "+tmpl.toString());
        			if(tmpl.getId() == null){
        				this.getHibernateTemplate().save(tmpl);
        			}
        			else{
        				ItemRenderingTemplate persisted = (ItemRenderingTemplate) this.getHibernateTemplate().load(ItemRenderingTemplate.class, tmpl.getId());
        				persisted.setDescription(tmpl.getDescription());
        				persisted.setHideHistory(tmpl.getHideHistory());
        				persisted.setHideOverview(tmpl.getHideOverview());
        				persisted.setPriority(tmpl.getPriority());
        				persisted.setSpace(space);
        				persisted.setTemplateLanguage(tmpl.getTemplateLanguage());
        				persisted.setTemplateText(tmpl.getTemplateText());
        				this.getHibernateTemplate().update(persisted);
        			}
        		}
        	}
        	space = (Space) getHibernateTemplate().merge(space);
    	}
    	else{*/
    		List<ItemRenderingTemplate> templates = space.getItemRenderingTemplates();
    		//space.setItemRenderingTemplates(null);
        	getHibernateTemplate().merge(space);
//        	if(CollectionUtils.isNotEmpty(templates)){
//        		for(ItemRenderingTemplate template : templates){
//        			template.setSpace(space);
//        			this.getHibernateTemplate().merge(template);
//        		}
//        	}
        		
    	/*}*/
        //logger.info("Saved space, updating metadataCache for space: "+space.getPrefixCode());
        // save custom attributes
        if(CollectionUtils.isNotEmpty(fieldList)){
        	ValidationExpression noValidation = this.loadValidationExpression(1);
        	for(Field field : fieldList){
        		ItemFieldCustomAttribute attr = field.getCustomAttribute();
        		if(attr != null){
        			
        			if(attr.getValidationExpression() == null){
        				attr.setValidationExpression(noValidation);
        			}
        			// keep lookup translations to save later
        			if(attr.getVersion().intValue() > attr.getPersistedVersion().intValue()){
        				List<CustomAttributeLookupValue> values = attr.getAllowedLookupValues();
            			Map<String, Map<String, Map<String, String>>> attrLookupValuesTranslations = new HashMap<String, Map<String, Map<String, String>>>();
            			if(CollectionUtils.isNotEmpty(values)){
            				for (CustomAttributeLookupValue value : values) {
            					Map<String, Map<String, String>> valueTranslations = value.getTranslations();
            					if(MapUtils.isNotEmpty(valueTranslations)){
            						attrLookupValuesTranslations.put(value.getListIndex()+"", value.getTranslations());
            					}
            				}
            			}
            			logger.info("storeSpace saved lookup attribue translations for later: "+attrLookupValuesTranslations);
            			Map<String, Map<String, String>> attrTranslations = attr.getTranslations();
            			logger.info("storeSpace saved lookup attribue translations for later: "+attrLookupValuesTranslations);
            			attr = (ItemFieldCustomAttribute) getHibernateTemplate().merge(attr);
            			logger.info("attr translations after merge: "+attr.getTranslations());
            			attr.setTranslations(attrTranslations);
            			this.saveOrUpdateTranslations(attr);
            			field.setCustomAttribute(attr);
            			// save lookup translations
            			if(MapUtils.isNotEmpty(attrLookupValuesTranslations)){
            				values = attr.getAllowedLookupValues();
            				for (CustomAttributeLookupValue value : values) {
            					Map<String, Map<String, String>> lookupTranslations = attrLookupValuesTranslations.get(value.getListIndex()+"");
            					if(MapUtils.isNotEmpty(lookupTranslations)){
            	        			logger.info("value translations after merge: "+value.getTranslations());
            						value.setTranslations(lookupTranslations);
            						this.saveOrUpdateTranslations(value);
            					}
            				}
            			}
        			}
        		}
        		else{
        			if(logger.isDebugEnabled()){
        				logger.debug("Skipped saving null custom attribute for field "+field.getName().getText());
        			}
        		
        		}
        		
        	}
        }
        this.merge(space);
		logger.info("space translations after merge: "+space.getTranslations());
        if(translations != null){
        	space.setTranslations(translations);
        	this.saveOrUpdateTranslations(space);
        }
        metadataCache.put(space.getId(), space.getMetadata());
        return space;
    }

    @Override
	public Space loadSpace(long id) {
        Space space = (Space) getHibernateTemplate().get(Space.class, id);
        loadSpaceMetadataFromCache(space);
        return space;
    }

	private void loadSpaceMetadataFromCache(Space space) {
		Metadata meta = metadataCache.get(space.getId());
        if(meta == null){
        	Hibernate.initialize(space.getMetadata());
        	metadataCache.put(space.getId(), space.getMetadata());
        }
        else{
        	space.setMetadata(meta);//this.getHibernateTemplate().merge(meta);
        }
	}
    @Override
	public Space loadSpace(final SpaceRole spaceRole) {
    	Space space = (Space) getHibernateTemplate().execute(new HibernateCallback() {
            @Override
			public Object doInHibernate(Session session) {
                Query q = session.createQuery("select space from SpaceRole sr, Space space where sr = ?");
                q.setParameter(0, spaceRole);
                q.setMaxResults(1);
                List<Space> results = q.list();
                return results.isEmpty()?null:results.get(0);
            }
        });
    	loadSpaceMetadataFromCache(space);
    	return space;
    }
    
    @Override
	public UserSpaceRole loadUserSpaceRole(long id) {
        return (UserSpaceRole) getHibernateTemplate().get(UserSpaceRole.class, id);
    }    
    
    @Override
	public SpaceSequence loadSpaceSequence(long id) {                
        return (SpaceSequence) getHibernateTemplate().get(SpaceSequence.class, id);           
    }    
    
    @Override
	public void storeSpaceSequence(SpaceSequence spaceSequence) {
        getHibernateTemplate().saveOrUpdate(spaceSequence);
        // important to prevent duplicate sequence numbers, see CalipsoServiceImpl#storeItem()
        getHibernateTemplate().flush();
    }
    
    @Override
	public List<Space> findSpacesByPrefixCode(String prefixCode) {
        return getHibernateTemplate().find("from Space space where space.prefixCode = ?", prefixCode);
    }
    
	/**
	 * Returns all other Spaces of which the Assets are visible for the given Space
	 * @see gr.abiss.calipso.CalipsoDao#getVisibleAssetsForSpace(gr.abiss.calipso.domain.Space)
	 */
	@Override
	public Collection<Asset> getVisibleAssetsForSpace(Space space) {
		return getHibernateTemplate()
			.find("from Asset asset where asset.space = ? or " +
					"((asset.space.spaceGroup = ? and asset.space.assetVisibility="+Space.ASSETS_VISIBLE_TO_SPACEGROUP_SPACES+") " +
					"or asset.space.assetVisibility = "+Space.ASSETS_VISIBLE_TO_ANY_SPACE+")", 
					new Object[] {space, space.getSpaceGroup()});
	}
    
	/**
	 * Returns all other Spaces of which the Assets are visible for the given Space
	 * @see gr.abiss.calipso.CalipsoDao#getVisibleAssetSpacesForSpace(gr.abiss.calipso.domain.Space)
	 */
	@Override
	public Collection<Space> getVisibleAssetSpacesForSpace(Space space) {
		return getHibernateTemplate()
			.find("from Space space where space !=? and " +
					"((space.spaceGroup = ? and space.assetVisibility="+Space.ASSETS_VISIBLE_TO_SPACEGROUP_SPACES+") " +
					"or space.assetVisibility = "+Space.ASSETS_VISIBLE_TO_ANY_SPACE+")", 
					new Object[] {space, space.getSpaceGroup()});
	}

    @Override
	public List<Space> findAllSpaces() {
        return getHibernateTemplate().find("from Space space order by space.prefixCode");
    }
    
    @Override
	public List<Space> findAllTemplateSpaces() {
        return getHibernateTemplate().find("from Space space where space.isTemplate = true order by space.prefixCode");
    }
    
    @Override
	public List<Space> findSpacesWhereIdIn(List<Long> ids) {
        return getHibernateTemplate().findByNamedParam("from Space space where space.id in (:ids)", "ids", ids);
    }   
    
    @Override
	public List<Space> findSpacesWhereGuestAllowed() { 
    	// left join fetch space.spaceRoles
    	@SuppressWarnings("unchecked")
    	List<Space> spaces = getHibernateTemplate().find(
    			"from Space space join fetch space.metadata where space.itemVisibility in (" + 
    					Space.ITEMS_VISIBLE_TO_ANY_LOGGEDIN_USER + 
    					", " + Space.ITEMS_VISIBLE_TO_LOGGEDIN_REPORTERS + 
    					", " +  Space.ITEMS_VISIBLE_TO_LOGGEDIN_REPORTERS_NO_COMMENTS +
    					")");  
    	if(CollectionUtils.isNotEmpty(spaces)){
    		for(Space space : spaces){
    			getHibernateTemplate().initialize(space.getSpaceRoles());
    			space.setSpaceRoles(new HashSet<SpaceRole>(findSpaceRolesForSpace(space)));
    		}
    	}
    	return spaces;
    }

    @Override
	public List<Space> findSpacesWhereAnonymousAllowed() { 
    	@SuppressWarnings("unchecked")
		List<Space> spaces =  getHibernateTemplate()
    		.find("from Space space join fetch space.metadata where space.itemVisibility in ("
    				+ Space.ITEMS_VISIBLE_TO_ANONYMOUS_USERS
    				+ ", "
    				+ Space.ITEMS_INVISIBLE_TO_ANONYMOUS_REPORTERS
    				+ ")");
    	if(CollectionUtils.isNotEmpty(spaces)){
    		for(Space space : spaces){
    			getHibernateTemplate().initialize(space.getSpaceRoles());
    			space.setSpaceRoles(new HashSet<SpaceRole>(findSpaceRolesForSpace(space)));
    		}
    	}
    	return spaces;
    }
    
    @Override
	public void removeSpace(Space space) {        
        getHibernateTemplate().delete(space);
    }    
    
    @Override
	public void storeUser(User user) {
    	Organization org = user.getOrganization();
    	// save org if not-null and new
    	if(org != null && org.getId() == 0){
        	getHibernateTemplate().persist(org);
    	}
        getHibernateTemplate().merge(user);
    }
    
    @Override
	public User loadUser(long id) {
        return (User) getHibernateTemplate().get(User.class, id);
    }
    
    @Override
	public void removeUser(User user) {
        getHibernateTemplate().delete(user);
    }

    @Override
	public List<User> findAllUsers() {
        return getHibernateTemplate().find("from User user order by user.name");
    }
    @Override
	public List<Organization> findAllOrganizations() {
        return getHibernateTemplate().find("from Organization org order by org.name");
    }
    
    @Override
	public List<User> findUsersWhereIdIn(List<Long> ids) {
        return getHibernateTemplate().findByNamedParam("from User user where user.id in (:ids)", "ids", ids);
    }    

    @Override
	@SuppressWarnings("unchecked")
	public List<User> findUsersMatching(final String searchText, final String searchOn, Space space) {
    	//logger.debug("findUsersMatching searchText: "+searchText+", searchOn: "+searchOn+", space: space");
    	if(space != null && (space.getItemVisibility().equals(Space.ITEMS_VISIBLE_TO_ANY_LOGGEDIN_USER) 
    			|| space.getItemVisibility().equals(Space.ITEMS_VISIBLE_TO_LOGGEDIN_REPORTERS)
    			|| space.getItemVisibility().equals(Space.ITEMS_VISIBLE_TO_LOGGEDIN_REPORTERS_NO_COMMENTS))){
    		// if GUESTs are allowed just search all registered based on the text-based stuff
    		return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
                @Override
				public Object doInHibernate(Session session) {
                    Criteria criteria = session.createCriteria(User.class);
                    criteria.add(Restrictions.ilike(searchOn, searchText, MatchMode.ANYWHERE));
                    criteria.addOrder(Order.asc("name"));
                    return criteria.list();
                }
            });
    	}
    	// else if space, limit search to space users
    	else if(space != null){
    		return getHibernateTemplate()
    			.find("select distinct u from User u join u.userSpaceRoles usr join usr.spaceRole sr" 
                    + " where sr.space.id = ? and u."+searchOn+" like ? order by u.name", new Object[]{space.getId(), "%"+searchText+"%"});
    	}
    	// else keep this for backwards compatibility
    	else{
    		return findUsersMatching(searchText, searchOn);
    	}
    	
    }
    
    @Override
	@SuppressWarnings("unchecked")
	public List<User> findUsersMatching(final String searchText, final String searchOn) {   
        return (List<User>) getHibernateTemplate().execute(new HibernateCallback() {
            @Override
			public Object doInHibernate(Session session) {
                Criteria criteria = session.createCriteria(User.class);
                criteria.add(Restrictions.ilike(searchOn, searchText, MatchMode.ANYWHERE));
                criteria.addOrder(Order.asc("name"));
                return criteria.list();
            }
        });
    }
    
    @Override
	public List<User> findUsersByLoginName(String loginName) {
        return getHibernateTemplate().find("from User user where user.loginName = ?", loginName);
    }
    
    @Override
	public List<User> findUsersByEmail(String email) {
        return getHibernateTemplate().find("from User user where user.email = ?", email);
    }
    
    @Override
	public List<UserSpaceRole> findUserRolesForSpace(long spaceId) {
        // join fetch for user object
//         return getHibernateTemplate().find("select usr from UserSpaceRole usr join fetch usr.user"
//                 + " where usr.space.id = ? order by usr.user.name", spaceId);

        return getHibernateTemplate().find("select usr from UserSpaceRole usr join fetch usr.user join usr.spaceRole sr"
                + " where sr.space.id = ? order by usr.user.name", spaceId);
    }
    
//    public List<User> findUsersWithRoleForSpace(long spaceId, String roleKey) {
//        return getHibernateTemplate().find("from User user"
//                + " join user.userSpaceRoles as usr where usr.space.id = ?"
//                + " and usr.roleKey = ? order by user.name", new Object[] {spaceId, roleKey});        
//    }    

    @Override
	public List<User> findUsersWithRoleForSpace(SpaceRole spaceRole) {
        return getHibernateTemplate().find("from User user"
                + " join user.userSpaceRoles as usr where usr.spaceRole.id = ?"
                + " order by user.name", spaceRole.getId());        
    }

    @Override
	public int loadCountOfHistoryInvolvingUser(User user) {
        Long count = (Long) getHibernateTemplate().find("select count(history) from History history where "
                + " history.loggedBy = ? or history.assignedTo = ?", new Object[] {user, user}).get(0);
        return count.intValue();        
    }
    
    //==========================================================================    
    
    @Override
    public List<Object[]>  selectLatestItemPerSpace(User user){
    	List<Object[]> loggedByList = getHibernateTemplate().find("select item.space.prefixCode, item.space.id, item.space.closingDate, item.sequenceNum, item.status, item.id from Item item" 
                + " where item.loggedBy.id = ? order by item.timeStamp desc", user.getId());
    	List<Object[]> latestPerSpace = new LinkedList<Object[]>();
    	Set<String> prefixes = new HashSet<String>();
    	if(CollectionUtils.isNotEmpty(loggedByList)){
    		for(Object[] o : loggedByList){
    			String spacePrefix = (String) o[0];
    			if(!prefixes.contains(spacePrefix)){
    				latestPerSpace.add(o);
    				prefixes.add(spacePrefix);
    			}
    		}
    	}
    	return latestPerSpace;
    	
    }
    
    @Override
	public CountsHolder loadCountsForUser(User user) {
        Collection<Space> spaces = user.getSpaces();
        if (spaces.size() == 0) {
            return null;
        }
        CountsHolder ch = new CountsHolder();
        HibernateTemplate ht = getHibernateTemplate();        
        List<Object[]> loggedByList = ht.find("select item.space.id, count(item) from Item item" 
                + " where item.loggedBy.id = ? group by item.space.id", user.getId());
        List<Object[]> assignedToList = ht.find("select item.space.id, count(item) from Item item" 
                + " where item.assignedTo.id = ? group by item.space.id", user.getId());
        List<Object[]> statusList = ht.findByNamedParam("select item.space.id, count(item) from Item item" 
                + " where item.space in (:spaces) group by item.space.id", "spaces", spaces);

        List<Object[]> unassignedList = ht.findByNamedParam("select item.space.id, count(item) from Item item" +
        		" where item.space in (:spaces) and item.assignedTo is null group by item.space.id", "spaces", spaces);
        
        for(Object[] oa : loggedByList) {
            ch.addLoggedByMe((Long) oa[0], (Long) oa[1]);
        }
        for(Object[] oa : assignedToList) {
            ch.addAssignedToMe((Long) oa[0], (Long) oa[1]);
        }
        for(Object[] oa : statusList) {
            ch.addTotal((Long) oa[0], (Long) oa[1]);
        }
        for(Object[] oa : unassignedList) {
            ch.addUnassigned((Long) oa[0], (Long) oa[1]);
        }
        return ch;
    }
    
    @Override
	public Counts loadCountsForUserSpace(User user, Space space) {
        HibernateTemplate ht = getHibernateTemplate();        
        List<Object[]> loggedByList = ht.find("select status, count(item) from Item item" 
                + " where item.loggedBy.id = ? and item.space.id = ? group by item.status", new Object[] {user.getId(), space.getId()});
        List<Object[]> assignedToList = ht.find("select status, count(item) from Item item" 
                + " where item.assignedTo.id = ? and item.space.id = ? group by item.status", new Object[] {user.getId(), space.getId()});
        List<Object[]> statusList = ht.find("select status, count(item) from Item item" 
                + " where item.space.id = ? group by item.status", space.getId());        
        List<Object[]> unassignedList = ht.find("select status, count(item) from Item item" +
        		" where item.space.id=? and item.assignedTo is null group by item.status", space.getId());
        
        Counts c = new Counts(true);
        for(Object[] oa : loggedByList) {
            c.addLoggedByMe((Integer) oa[0], (Long) oa[1]);
        }
        for(Object[] oa : assignedToList) {
            c.addAssignedToMe((Integer) oa[0], (Long) oa[1]);
        }
        for(Object[] oa : statusList) {
            c.addTotal((Integer) oa[0], (Long) oa[1]);
        }
        for(Object[] oa : unassignedList) {
            c.addUnassigned((Integer) oa[0], (Long) oa[1]);
        }
        
        
        return c;
    }
    
    //==========================================================================
    
    @Override
	public List<User> findUsersForSpace(long spaceId) {
        return getHibernateTemplate().find("select distinct u from User u join u.userSpaceRoles usr join usr.spaceRole sr" 
                + " where sr.space.id = ? order by u.name", spaceId);
    }
    
    @Override
	public List<User> findUsersInOrganizations(List<Organization> organizations){
    	 List<User> findByNamedParam = getHibernateTemplate()
    	 	.findByNamedParam("select user from User user join user.organization org " +
    	 			" where user.organization in (:organizations) order by user.name", 
    	 		"organizations", organizations);
		return findByNamedParam;
    }
    
    
    @Override
	public List<User> findUsersForSpaceSet(Collection<Space> spaces) {
        return getHibernateTemplate().findByNamedParam("select u from User u join u.userSpaceRoles usr join usr.spaceRole sr" 
                + " where sr.space in (:spaces) order by u.name", "spaces", spaces);
    }
    
    @Override
	public void removeUserSpaceRole(UserSpaceRole userSpaceRole) {        
        //getHibernateTemplate().delete(userSpaceRole);
    	
    	getHibernateTemplate().bulkUpdate("delete from UserSpaceRole usr where usr.id = ?", userSpaceRole.getId());
    }
    
    @Override
	public List<Config> findAllConfig() {
        return getHibernateTemplate().loadAll(Config.class);
    }
    
    @Override
	public void storeConfig(Config config) {
        getHibernateTemplate().merge(config);
    }
    
    @Override
	public Config loadConfig(String param) {
        return (Config) getHibernateTemplate().get(Config.class, param);
    }

    @Override
	public int loadCountOfRecordsHavingFieldNotNull(Space space, Field field) {
        Criteria criteria = getSession().createCriteria(Item.class);
        criteria.add(Restrictions.eq("space", space));
        criteria.add(Restrictions.isNotNull(field.getName().toString()));
        criteria.setProjection(Projections.rowCount());
        int itemCount = NumberUtils.toInt(criteria.list().get(0).toString());
        // even when no item has this field not null currently, items may have history with this field not null
        // because of the "parent" difference, cannot use AbstractItem and have to do a separate Criteria query
        criteria = getSession().createCriteria(History.class);
        criteria.createCriteria("parent").add(Restrictions.eq("space", space));
        criteria.add(Restrictions.isNotNull(field.getName().toString()));
        criteria.setProjection(Projections.rowCount());
        return itemCount + NumberUtils.toInt(criteria.list().get(0).toString());        
    }

    @Override
	public int bulkUpdateFieldToNull(Space space, Field field) {
        int itemCount = getHibernateTemplate().bulkUpdate("update Item item set item." + field.getName() + " = null" 
                + " where item.space.id = ?", space.getId());
        //logger.info("no of Item rows where " + field.getName() + " set to null = " + itemCount);
        int historyCount = getHibernateTemplate().bulkUpdate("update History history set history." + field.getName() + " = null"
                + " where history.parent in ( from Item item where item.space.id = ? )", space.getId());
        //logger.info("no of History rows where " + field.getName() + " set to null = " + historyCount);
        return itemCount;
    }

    @Override
	public int loadCountOfRecordsHavingFieldWithValue(Space space, Field field, int optionKey) {
        Criteria criteria = getSession().createCriteria(Item.class);
        criteria.add(Restrictions.eq("space", space));
        criteria.add(Restrictions.eq(field.getName().toString(), optionKey));
        criteria.setProjection(Projections.rowCount());
        int itemCount = NumberUtils.toInt(criteria.list().get(0).toString());
        // even when no item has this field value currently, items may have history with this field value
        // because of the "parent" difference, cannot use AbstractItem and have to do a separate Criteria query
        criteria = getSession().createCriteria(History.class);
        criteria.createCriteria("parent").add(Restrictions.eq("space", space));
        criteria.add(Restrictions.eq(field.getName().toString(), optionKey));
        criteria.setProjection(Projections.rowCount());
        return itemCount + NumberUtils.toInt(criteria.list().get(0).toString());        
    }

    @Override
	public int bulkUpdateFieldToNullForValue(Space space, Field field, int optionKey) {
        int itemCount = getHibernateTemplate().bulkUpdate("update Item item set item." + field.getName() + " = null" 
                + " where item.space.id = ?"
                + " and item." + field.getName() + " = ?", new Object[] {space.getId(), optionKey});
        //logger.info("no of Item rows where " + field.getName() + " value '" + optionKey + "' replaced with null = " + itemCount);
        int historyCount = getHibernateTemplate().bulkUpdate("update History history set history." + field.getName() + " = null"
                + " where history." + field.getName() + " = ?"
                + " and history.parent in ( from Item item where item.space.id = ? )", new Object[] {optionKey, space.getId()});
        //logger.info("no of History rows where " + field.getName() + " value '" + optionKey + "' replaced with null = " + historyCount);
        return itemCount;        
    }
    
    @Override
	public int loadCountOfRecordsHavingStatus(Space space, int status) {
        Criteria criteria = getSession().createCriteria(Item.class);
        criteria.add(Restrictions.eq("space", space));
        criteria.add(Restrictions.eq("status", status));
        criteria.setProjection(Projections.rowCount());
        Long itemCount = (Long) criteria.list().get(0);
        // even when no item has this status currently, items may have history with this status
        // because of the "parent" difference, cannot use AbstractItem and have to do a separate Criteria query
        criteria = getSession().createCriteria(History.class);
        criteria.createCriteria("parent").add(Restrictions.eq("space", space));
        criteria.add(Restrictions.eq("status", status));
        criteria.setProjection(Projections.rowCount());
        return itemCount.intValue() + ((Long) criteria.list().get(0)).intValue();
    }    
    
    @Override
	public int bulkUpdateStatusToOpen(Space space, int status) {
        int itemCount = getHibernateTemplate().bulkUpdate("update Item item set item.status = " + State.OPEN 
                + " where item.status = ? and item.space.id = ?", new Object[] {status, space.getId()});
        //logger.info("no of Item rows where status changed from " + status + " to " + State.OPEN + " = " + itemCount);
        int historyCount = getHibernateTemplate().bulkUpdate("update History history set history.status = " + State.OPEN 
                + " where history.status = ?"
                + " and history.parent in ( from Item item where item.space.id = ? )", new Object[] {status, space.getId()});
        //logger.info("no of History rows where status changed from " + status + " to " + State.OPEN + " = " + historyCount);
        return itemCount;
    }    
    
    @Override
	public int bulkUpdateRenameSpaceRole(Space space, String oldRoleKey, String newRoleKey) {
        return getHibernateTemplate().bulkUpdate("update UserSpaceRole usr set usr.roleKey = ?"
                + " where usr.roleKey = ? and usr.space.id = ?", new Object[] {newRoleKey, oldRoleKey, space.getId()});
    }
    
//    public int bulkUpdateDeleteSpaceRole(Space space, String roleKey) {
//        if (roleKey == null) {
//            return getHibernateTemplate().bulkUpdate("delete UserSpaceRole usr where usr.space.id = ?", space.getId());            
//        } else {
//            return getHibernateTemplate().bulkUpdate("delete UserSpaceRole usr"
//                    + " where usr.space.id = ? and usr.roleKey = ?", new Object[] {space.getId(), roleKey});
//        }
//    }

    //public  

    @Override
	public int bulkUpdateDeleteUserSpaceRolesForSpace(Space space){
    	List<SpaceRole> spaceRolesList = this.findSpaceRolesForSpace(space);
    	int records = 0;
    	if (spaceRolesList!=null){
    		for (SpaceRole spaceRole : spaceRolesList){
    			records += getHibernateTemplate().bulkUpdate("delete from UserSpaceRole usr where usr.spaceRole.id = ?", spaceRole.getId());
    		}
    	}
    	return records;
    }

    @Override
	public int bulkUpdateDeleteItemsForSpace(Space space) {
        int historyCount = getHibernateTemplate().bulkUpdate("delete History history where history.parent in"
                + " ( from Item item where item.space.id = ? )", space.getId());
        //logger.debug("deleted " + historyCount + " records from history");
        int itemItemCount = getHibernateTemplate().bulkUpdate("delete ItemItem itemItem where itemItem.item in"
                + " ( from Item item where item.space.id = ? )", space.getId());
        //logger.debug("deleted " + itemItemCount + " records from item_items");
        int itemCount = getHibernateTemplate().bulkUpdate("delete Item item where item.space.id = ?", space.getId());
        //logger.debug("deleted " + itemCount + " records from items");
        return historyCount + itemItemCount + itemCount;
    }

    // Saved Search============================
    
    @Override
	public void storeSavedSearch(SavedSearch savedSearch) {        
        getHibernateTemplate().merge(savedSearch);
    }
    
    @Override
	public SavedSearch loadSavedSearch(long id) {
        return (SavedSearch) getHibernateTemplate().get(SavedSearch.class, id);
    }
    
    @Override
	public void removeSavedSearch(SavedSearch savedSearch) {        
        getHibernateTemplate().delete(savedSearch);
    }
    
    @Override
	public List<SavedSearch> findSavedSearches(User user) {
        return getHibernateTemplate().find("from SavedSearch savedSearch where savedSearch.user = ?", user);
    }
    
    /**
     * 
     * @param user
     * @return
     */
    @Override
	@SuppressWarnings("unchecked")
	public List<SavedSearch> findVisibleSearches(User user){
    	// init query string and params
    	List<Object> params = new LinkedList<Object>();
    	StringBuffer queryString = new StringBuffer("from SavedSearch ss where ");
    	
    	// is anonymous only show public
    	if(user.getId() == 0){
    		queryString.append("ss.visibility = ? ");
        	params.add(SavedSearch.VISIBILITY_PUBLIC);
    	}
    	// else look for private and visible to loggedin
    	else{
        	queryString.append("ss.user = ? or ss.visibility <= ? ");
        	params.add(user);
        	params.add(SavedSearch.VISIBILITY_LOGGEDIN_USERS);
        	
        	// loggedin users may also be able to see searches visible 
        	// within spaces/space groups
        	Set<Space> spaces = user.getSpaces();
        	if(CollectionUtils.isNotEmpty(spaces)){
        		queryString.append(" or (ss.visibility = gr.abiss.calipso.domain.SavedSearch.VISIBILITY_WITHIN_SPACE and (");
        		for(Iterator<Space> iter = spaces.iterator();iter.hasNext();){
        			Space space = iter.next();
            		queryString.append("ss.space.id = ?");
                	params.add(new Long(space.getId()));
                	if(iter.hasNext()){
                		queryString.append(" or ");
                	}
        		}
        		queryString.append("))");
        	}
    	}
    	
    	//logger.debug("Looking for visible searches query: "+queryString.toString());
        return getHibernateTemplate().find(queryString.toString(), params.toArray());
    }
        
    @Override
	public List<SavedSearch> findSavedSearches(User user, Space space) {
    	if (space==null){
    		return getHibernateTemplate().find("from SavedSearch savedSearch where savedSearch.user = ? and savedSearch.space is null", user);
    	}
        return getHibernateTemplate().find("from SavedSearch savedSearch where savedSearch.user = ? and savedSearch.space = ?", new Object[] {user, space});
    }
    
    //////////////////////
    // Asset Management //
    //////////////////////

    /*~~~~~~~~~~~~~~~~~*\
    | Custom Attributes | ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    \*~~~~~~~~~~~~~~~~~*/
    
    /**
     * Store the given Custom Attribute
     *
     */
    @Override
	public void storeCustomAttribute(CustomAttribute assetTypeCustomAttribute){
    	getHibernateTemplate().saveOrUpdate(assetTypeCustomAttribute);
    }
    
    @Override
	public void saveOrUpdateTranslations(I18nResourceTranslatable nt){
    	Map<String,Map<String,String>> translationsMap = nt.getTranslations();
    	logger.info("Saving translations of: "+nt.getName()+", translations: "+translationsMap);
    	if(MapUtils.isNotEmpty(translationsMap)){
    		for(String propertyName : translationsMap.keySet()){
    			Map<String,String> propNameTranslations = translationsMap.get(propertyName);
    			if(MapUtils.isNotEmpty(propNameTranslations)){
        			for(String locale : propNameTranslations.keySet()){
        				if(StringUtils.isNotBlank(propNameTranslations.get(locale))){
        					I18nStringIdentifier sid = new I18nStringIdentifier(nt.getPropertyTranslationResourceKey(propertyName), locale);
        					logger.info("Saving I18nStringResource with key: "+sid.getKey()+", locale: "+sid.getLocale()+", value: "+propNameTranslations.get(locale));
        					merge(new I18nStringResource(sid, propNameTranslations.get(locale)));
        				}
        			}
    			}
    		}
    	}
    }


	//-------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Get a list of all Asset type custom attributes
     */
    @Override
	public List<AssetTypeCustomAttribute> findAllCustomAttributes(){
    	return getHibernateTemplate().find("select customAttribute from AssetTypeCustomAttribute customAttribute order by customAttribute.name");
    }
    
    //-------------------------------------------------------------------------------------------------------------------------

    /**
     * Load AssetTypeCustomAttribute by id
     */
    @Override
	public AssetTypeCustomAttribute loadAssetTypeCustomAttribute(long id){
    	return (AssetTypeCustomAttribute)getHibernateTemplate().get(AssetTypeCustomAttribute.class, id);
}
    
    /**
     * Load ItemFieldCustomAttribute by item and field name
     */
    @Override
	public ItemFieldCustomAttribute loadItemCustomAttribute(Space space, String fieldName){
    	ItemFieldCustomAttribute attr = null;
    	Object[] params = new Object[2];
    	params[0] = space.getId();
    	params[1] = fieldName;
    	List results = getHibernateTemplate().find("from ItemFieldCustomAttribute attr where attr.space.id = ? and attr.fieldName = ?", params);
    	if(results.size() > 0){
    		attr = (ItemFieldCustomAttribute) results.get(0);
    	}
    	return attr;
    }

	/**
	 * Delete ItemFieldCustomAttribute by item and field name
	 */
	@Override
	public void deleteItemCustomAttribute(Space space, String fieldName){
		ItemFieldCustomAttribute attr = loadItemCustomAttribute(space, fieldName);
		if(attr != null){
			getHibernateTemplate().delete(attr);
		}
	}
    
    //-------------------------------------------------------------------------------------------------------------------------

    @Override
	public CustomAttributeLookupValue loadCustomAttributeLookupValue(long id){
    	return (CustomAttributeLookupValue)getHibernateTemplate().get(CustomAttributeLookupValue.class, id);
    }
    
    @Override
	public CustomAttributeLookupValue loadCustomAttributeLookupValue(CustomAttribute attr, String name){
    	CustomAttributeLookupValue value = null;
    	DetachedCriteria criteria =  DetachedCriteria.forClass(CustomAttributeLookupValue.class)
			.add(Restrictions.eq("attribute", attr))
			.add(Restrictions.eq("value", name));
    	@SuppressWarnings("unchecked")
		List<CustomAttributeLookupValue> results = getHibernateTemplate().findByCriteria(criteria);
    	if(!results.isEmpty()){
    		value = results.get(0);
    	}
    	return value;
    }
    //-------------------------------------------------------------------------------------------------------------------------
    
    @Override
	public void removeLookupValue(CustomAttributeLookupValue lookupValue){
    	//logger.info("Deleting old lookupValue: "+lookupValue);
    	// delete translations
    	List<I18nStringResource> translations = this.findI18nStringResourcesFor("name", lookupValue);
    	getHibernateTemplate().deleteAll(translations);
    	
    	// remove lookup value
    	//CustomAttribute attr = lookupValue.getAttribute();
    	//lookupValue.getAttribute().remove(lookupValue);
    	//getHibernateTemplate().delete(lookupValue);
    	
    }//removeLookupValue
    
    //-------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Search Custom Attributes.
     * 
     * */
	@Override
	@SuppressWarnings("unchecked")
	public List<AssetTypeCustomAttribute> findCustomAttributesMatching(final AssetTypeCustomAttributeSearch searchCustomAttribute) {
		DetachedCriteria criteria = searchCustomAttribute.getDetachedCriteria();
		
		List<AssetTypeCustomAttribute> list = getHibernateTemplate().findByCriteria(criteria, searchCustomAttribute.getPageBegin()-1, searchCustomAttribute.getPageSize());
		criteria = searchCustomAttribute.getDetachedCriteriaForCount();
		criteria.setProjection(Projections.rowCount());
		Long count = (Long) getHibernateTemplate().findByCriteria(criteria).get(0);
		searchCustomAttribute.setResultCount(count);
		
		return list;
    }
    
	//-------------------------------------------------------------------------------------------------------------------------
	/**
	 * Store the given lookup value of an Asset Type Custom Attribute
	 * 
	 **/
	
	@Override
	public void storeLookupValue(CustomAttributeLookupValue lookupValue){
		getHibernateTemplate().merge(lookupValue);
	}//storeLookupValue

	//-------------------------------------------------------------------------------------------------------------------------
	
    @Override
	public List<AssetType> findAllAssetTypesByCustomAttribute(AssetTypeCustomAttribute attribute){
    	
    	return getHibernateTemplate().find("select at from AssetType at join at.allowedCustomAttributes atca where atca.id = ?", attribute.getId());
    	
    }//findAllAssetTypesByCustomAttribute

    //-------------------------------------------------------------------------------------------------------------------------
    
    /**
     * Counts records for a given Custom Attribute. 
     * */
    @Override
	public int loadCountAssetsForCustomAttribute(AssetTypeCustomAttribute customAttribute){
    	Long count = (Long) getHibernateTemplate().find("select count(*) from AssetTypeCustomAttribute atca join atca.assetTypes at join at.assets a where atca.id =?", customAttribute.getId()).get(0);
    	return count.intValue();
    }//loadCountAssetsForCustomAttribute
    
    //-------------------------------------------------------------------------------------------------------------------------
    
    /**
	 * Counts records for a given Asset Type and a Custom Attribute
	 * */
	@Override
	public int loadCountForAssetTypeAndCustomAttribute(AssetType assetType,	CustomAttribute customAttribute) {
		//Long count = (Long) getHibernateTemplate()
		//		.find("select count(*) from AssetCustomAttributeValue acav join acav.asset a where a.assetType.id = ? and acav.customAttribute.id = ?",
		//				new Object[] { assetType.getId(),
		//						customAttribute.getId() }).get(0);
		Long count = (Long) getHibernateTemplate().find(
				"select count(asset) from Asset asset left join asset.customAttributes as customAttribute where asset.assetType = ? and index(customAttribute) = ?", new Object[]{assetType, customAttribute}).get(0);
		return count.intValue();
	}// loadCountForAttributeValues
    //-------------------------------------------------------------------------------------------------------------------------

	/**
	 * Check how many times the given option value has been used.
	 */
    @Override
	public int loadCountForCustomAttributeLookupValue(CustomAttributeLookupValue lookupValue){
    	//Long count = (Long) getHibernateTemplate().find("select count(*) from AssetCustomAttributeValue acav where acav.attributeValue = ?", String.valueOf(lookupValue.getId())).get(0);
    	Long count = (Long) getHibernateTemplate().find("select count(asset) from Asset asset left join asset.customAttributes as customAttribute where customAttribute=?", String.valueOf(lookupValue.getId())).get(0);
    	return count.intValue();    	
    }
    
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * Deletes the given custom attribute. 
     * If custom attribute's type is "Dropdown List" then deletes its values as well.  
     * */
    @Override
	public void removeCustomAttribute(CustomAttribute customAttribute){
    	getHibernateTemplate().delete(customAttribute);
    }
    
    
    /*~~~~~~~~~~~*\
     | Asset Type | ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    \*~~~~~~~~~~~*/
    

	/**
	 * Get a list of all persisted AssetType instances. May return null or an empty List
	 * @see gr.abiss.calipso.CalipsoDao#findAllAssetTypes()
	 */
	@Override
	public List<AssetType> findAllAssetTypes() {
		return getHibernateTemplate().find("select assetType from AssetType assetType");
	}
	/**
	 * Get a list of all persisted AssetType instances that have visible assets 
	 * for the given Space instance. May return null or an empty List
	 * @see gr.abiss.calipso.CalipsoDao#findAllAssetTypes()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<AssetType> findAllAssetTypesForSpace(Space space) {
		String query = new StringBuffer("select distinct assetType from AssetType assetType join assetType.assets as asset ")
			// where assets belong to the given space
			.append("where asset.space = ? ")
			// or assets are visible to any space
			.append("or asset.space.assetVisibility = ")
			.append(Space.ASSETS_VISIBLE_TO_ANY_SPACE)
			// or assets are visible to the given space's spaceGroup
			.append("or (asset.space.assetVisibility = ")
			.append(Space.ASSETS_VISIBLE_TO_SPACEGROUP_SPACES)
			.append(" and asset.space.spaceGroup = ?)")
			.toString();
		//logger.debug("Looking gor asset types of the give space, query: "+query);
		return getHibernateTemplate().find(query, new Object[]{space, space.getSpaceGroup()});
	}
	
	
	
    @Override
	public List<AssetType> findAssetTypesWhereIdIn(List<Long> ids) {
        return getHibernateTemplate().findByNamedParam("from AssetType assetType where assetType.id in (:ids)", "ids", ids);
    }   

	/**
	 * Search Asset Types
	 * @author marcello
	 * @param assetTypeSearch contains search parameter values
	 * @return a list of Asset Types matching given criteria 
	 * 
	 **/
	@Override
	public List<AssetType> findAssetTypesMatching(AssetTypeSearch assetTypeSearch){
		
		DetachedCriteria criteria = assetTypeSearch.getDetachedCriteria();
		List<AssetType> list = getHibernateTemplate().findByCriteria(criteria, assetTypeSearch.getPageBegin()-1, assetTypeSearch.getPageSize());
		criteria = assetTypeSearch.getDetachedCriteriaForCount();
		criteria.setProjection(Projections.rowCount());
		Long count = (Long) getHibernateTemplate().findByCriteria(criteria).get(0);
		assetTypeSearch.setResultCount(count);

		return list;
	}//findAssetTypesMatching
	
	/**
	 * Store the given AssetType instance
	 */
    @Override
	public void storeAssetType(AssetType assetType) {
        getHibernateTemplate().merge(assetType);
    }
	/**
	 * Get a list of all allowed AssetTypeCustomAttributes for this AssetType. May return null or an empty List
	 * @see gr.abiss.calipso.CalipsoDao#findAllAssetTypes()
	 */
	@Override
	public List<AssetTypeCustomAttribute> findAllAssetTypeCustomAttributesByAssetType(AssetType assetType) {
		return getHibernateTemplate().find("select att from AssetTypeCustomAttribute att join att.assetTypes at where at.id = ?", assetType.getId());
	}

	/**
	 * Load AssetType by id. May return null if no match is found
	 */
	@Override
	public AssetType loadAssetType(long id){
        return (AssetType) getHibernateTemplate().get(AssetType.class, id);
    }
	@Override
	public AssetType loadAssetTypeByAssetId(Long assetId){
		AssetType assetType = null;
		@SuppressWarnings("unchecked")
		List<AssetType> list = getHibernateTemplate().find("select asset.assetType from Asset asset where asset.id = ?", assetId);
        if(!list.isEmpty()){
        	assetType = list.get(0);
        }
        return assetType;
	}
	/**
	 * Load AssetType by id. May return null if no match is found
	 */
	@Override
	public AssetType loadAssetTypeByName(String name){
        @SuppressWarnings("unchecked")
		List<AssetType> list = getHibernateTemplate().find("from AssetType assetType where assetType.name = ?", name);
        if(!list.isEmpty()){
        	return list.get(0);
        }
        else{
        	return null;
        }
    }

	/**
	 * Get a list of all level 1 (root) lookup values for a given CustomAttribute. They will contain their children. May return null or an empty List
	 */
	@Override
	public List<CustomAttributeLookupValue> findLookupValuesByCustomAttribute(CustomAttribute attr) {
		return getHibernateTemplate().find("select attVal from CustomAttributeLookupValue attVal where attVal.attribute.id = ? and attVal.level = 1 order by attVal.showOrder ASC, attVal.id ASC", attr.getId());
	}

	/**
	 * Get a list of all active level 1 (root) lookup values for a given CustomAttribute. They will contain their children. May return null or an empty List
	 */
	@Override
	public List<CustomAttributeLookupValue> findActiveLookupValuesByCustomAttribute(CustomAttribute attr) {
		return attr != null ? getHibernateTemplate()
				.find("select attVal from CustomAttributeLookupValue attVal where attVal.attribute.id = ? and attVal.level = 1 and attVal.active = true order by attVal.showOrder ASC, attVal.id ASC",
						attr.getId())
				: new LinkedList<CustomAttributeLookupValue>();
	}

	
	/**
	 * Get a list of all lookup values matching the level for a given CustomAttribute. Only applies to Tree Options. May return null or an empty List
	 */
	@Override
	public List<CustomAttributeLookupValue> findLookupValuesByCustomAttribute(CustomAttribute attr, int level) {
		return getHibernateTemplate().find("select attVal from CustomAttributeLookupValue attVal where attVal.attribute.id = ? and attVal.level = "+level+" order by attVal.id ASC", attr.getId());
	}
	/**
	 * Get a list of all lookup values for a given CustomAttribute. Only applies to Tree Options. May return null or an empty List
	 */
	@Override
	public List<CustomAttributeLookupValue> findAllLookupValuesByCustomAttribute(CustomAttribute attr) {
		return getHibernateTemplate().find("select attVal from CustomAttributeLookupValue attVal where attVal.attribute.id = ? order by attVal.id ASC", attr.getId());
	}

	////////////
	// Assets // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
	////////////
	
	/**
	 * @param asset an asset instance
	 * Stores the given Asset instance
	 **/
	@Override
	public void storeAsset(Asset asset){
		//if(asset.getCustomAttributes() != null){
		//	for(CustomAttribute attr : asset.getCustomAttributes().keySet()){
		//		logger.debug("Saving custom attribute "+attr+" with value "+ asset.getCustomAttributes().get(attr));
		//	}
		//}
		getHibernateTemplate().saveOrUpdate(asset);
	}//storeAsset
	

	//---------------------------------------------------------------------------------------------
	
	/**
	Load Asset by id. May return null if no match is found
	*/

	@Override
	public Asset loadAsset(Long id){
		Asset asset = (Asset) getHibernateTemplate().get(Asset.class, id);
		// TODO:
		/*
		if(asset.getCustomAttributes() != null && asset.getCustomAttributes().size() > 0){
			for(AssetTypeCustomAttribute attr :  asset.getCustomAttributes().keySet()){
				if(attr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_USER)){
					//attr.setUser(this.loadUser(attr.get))
				}
			}
		}
		*/
		return asset;
	} //loadAssset
	

	@Override
	@SuppressWarnings("unchecked")
	public Asset loadAssetWithAttributes(Long id){
		Asset asset =  null;
		if(id != null){
			List<Asset> results = getHibernateTemplate().find(//
					//"from Asset asset left join fetch asset.customAttributes as customAttribute index[customAttribute] as attrIndex where asset.id = ?", id);
					"select asset from Asset asset left join asset.customAttributes as customAttribute where asset.id = ?", id);
			if(!results.isEmpty()){
				asset = results.get(0);
				Hibernate.initialize(asset.getCustomAttributes());
				preloadCustomAttributeEntityValuesForAsset(asset);
			}
		}
		else{
			logger.warn("Cannot load Asset using a null value");
		}
		return asset;
		
	} //loadAssset

	/**
	 * TODO: A hack until we implement the custom attribute subclass hierarchy.
	 * @param asset
	 */
	@Override
	public void preloadCustomAttributeEntityValuesForAsset(Asset asset) {
		Map<AssetTypeCustomAttribute,String> attrs = asset.getCustomAttributes();
		for(Entry<AssetTypeCustomAttribute, String> entry  : attrs.entrySet()){
			AssetTypeCustomAttribute attr = entry.getKey();
			if(attr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_USER)){
				attr.setUserValue(loadUser(NumberUtils.toLong(entry.getValue())));
			}
			if(attr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_ORGANIZATION)){
				attr.setOrganizationValue(loadOrganization(NumberUtils.toLong(entry.getValue())));
			}
			if(attr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_ASSET)){
				attr.setAssetValue(loadAsset(NumberUtils.toLong(entry.getValue())));
			}
			if(attr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_COUNTRY)){
				attr.setCountryValue(loadCountry(entry.getValue()));
			}
			if(attr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)
				|| attr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_OPTIONS_TREE)){
				attr.setLookupValue(loadCustomAttributeLookupValue(NumberUtils.toLong(entry.getValue())));
			}
			
		}
	}
	
	
	//---------------------------------------------------------------------------------------------
	
	/**
	 * Search for assets 
	 * @param assetSearch contains search parameter values
	 * @return a list of Assets matching given criteria
	 * */
	@Override
	@SuppressWarnings("unchecked")
	public List<Asset> findAssetsMatching(AssetSearch _assetSearch, final boolean fetchCustomAttributes){
		final AssetSearch assetSearch = _assetSearch;
        return (List<Asset>) getHibernateTemplate().execute(new HibernateCallback() {
            @Override
			public Object doInHibernate(Session session) {
            	String baseQueryString = assetSearch.getQueryString();
            	if(!fetchCustomAttributes){
            		baseQueryString = baseQueryString.replaceAll(" fetch", "");
            	}
        		String queryString = "select distinct asset " + baseQueryString;
        		String countQueryString = "select count(distinct asset) " + baseQueryString.replaceAll(" fetch", "");
        		Query query = session.createQuery(queryString);
        		Query countQuery = session.createQuery(countQueryString);
        		Iterator<Serializable> params = assetSearch.getParams().iterator();
        		for(int i=0;params.hasNext();i++){
        			Serializable param = params.next();
            		if(logger.isDebugEnabled()){
            			logger.debug("Adding param: "+param);
            		}
            		query.setParameter(i, param);
            		countQuery.setParameter(i, param);
        		}
        		if(logger.isDebugEnabled()){
        			logger.debug("Executing count query: "+countQueryString);
        		}
        		Long count = ((Long) countQuery.iterate().next()).longValue();
        		assetSearch.setResultCount(count);
        		if(logger.isDebugEnabled()){
        			logger.debug("Executing main query: "+queryString);
        		}
        		// TODO: A hack until we implement the custom attribute subclass hierarchy.
        		List<Asset> assets = query.setMaxResults(assetSearch.getPageSize()).setFirstResult(assetSearch.getPageBegin()-1).list();
        		if(fetchCustomAttributes && assets != null && !assets.isEmpty()){
        			for(Asset asset : assets){
        				preloadCustomAttributeEntityValuesForAsset(asset);
        			}
        		}
        		return assets;
            }
        });
	}
	
	//---------------------------------------------------------------------------------------------

	@Override
	public List<Object> findCustomAttributeValueMatching(AssetSearch assetSearch){

		DetachedCriteria criteria = assetSearch.getDetachedCriteria();
		List<Object> list = getHibernateTemplate().findByCriteria(criteria, assetSearch.getPageBegin()-1, assetSearch.getPageSize());
		criteria = assetSearch.getDetachedCriteriaForCount();
		criteria.setProjection(Projections.rowCount());
		Long count = (Long) getHibernateTemplate().findByCriteria(criteria).get(0);
		assetSearch.setResultCount(count.longValue());
//		assetSearch.setResultCount(list.size());

		return list;
	}//findCustomAttributeValueMatching
	
	//---------------------------------------------------------------------------------------------
	
	/**
	 * Get a list of assets for the given item	
	 * @author marcello
	 * @param item the given item
	 *  */
	@Override
	public List<Asset> findAllAssetsByItem(Item item){
		return getHibernateTemplate().find("select asset from Asset asset, Item item where asset in elements(item.assets) and item.id = ?", item.getId());
	}//findAssetsByItem

	//---------------------------------------------------------------------------------------------
	
	
	@Override
	public List<Asset> findAllAssetsBySpace(Space space){
		return getHibernateTemplate().find("select asset from Asset asset where asset.space.id = ?", space.getId());
	}//findAssetsByItem


	
    ////////////
    // Fields // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ///////////

    @Override
	public List<RoleSpaceStdField> findSpaceFieldsBySpaceRole(SpaceRole spaceRole){
    	List results = getHibernateTemplate().find("select roleSpaceStdField from RoleSpaceStdField roleSpaceStdField where roleSpaceStdField.spaceRole.id = ? order by roleSpaceStdField.fieldMaskId desc", spaceRole.getId());
    	//logger.info("User role: "+spaceRole+", fields: "+results);
    	return results;
    }

    @Override
	public List<RoleSpaceStdField> findSpaceFieldsBySpaceandRoleType(SpaceRole spaceRole){
    	return getHibernateTemplate().find("select roleSpaceStdField from RoleSpaceStdField roleSpaceStdField where roleSpaceStdField.spaceRole.space.id = ? and roleSpaceStdField.spaceRole.roleTypeId = ? order by roleSpaceStdField.fieldMaskId desc", new Object[] {spaceRole.getSpace().getId(), spaceRole.getRoleTypeId()});
    }
     
    @Override
	public List<RoleSpaceStdField> findSpaceFieldsBySpace(Space space){
    	return getHibernateTemplate().find("select roleSpaceStdField from RoleSpaceStdField roleSpaceStdField where roleSpaceStdField.spaceRole.space.id = ? order by roleSpaceStdField.fieldMaskId desc", space.getId());
    }

    @Override
	public RoleSpaceStdField loadRoleSpaceField(long id){
    	return (RoleSpaceStdField)getHibernateTemplate().get(RoleSpaceStdField.class, id);
    }

    @Override
	public void storeRoleSpaceStdField(RoleSpaceStdField roleSpaceStdField){
    	getHibernateTemplate().merge(roleSpaceStdField);
    }

//    public List<RoleSpaceStdField>findSpaceFieldsForUser(User user){
//    	//return getHibernateTemplate().find("select roleSpaceStdField from RoleSpaceStdField roleSpaceStdField where roleSpaceStdField.roleKey in (select usr.roleKey from UserSpaceRole usr where usr.roleKey = roleSpaceStdField.roleKey and usr.user.id = ?) and roleSpaceStdField.space.id in (select usr.space.id from UserSpaceRole usr where usr.space.id = roleSpaceStdField.space.id and usr.user.id = ?) order by roleSpaceStdField.fieldMaskId desc", new Object[] {user.getId(), user.getId()});
//    	
////    	SELECT RSF.* 
////    	FROM ROLE_SPACE_FIELDS RSF 
////    	     INNER JOIN space_roles SR ON RSF.RSF_SRID = SR.SR_ID 
////    	     INNER JOIN user_space_roles USR ON SR.SR_ID = USR.usr_srid
//    	     
//    	return getHibernateTemplate().find("select roleSpaceStdField from RoleSpaceStdField rsf join rsf.spaceRole sr join userSpaceRoles usr where usr.user.id= ?", user.getId());
//    }
//
    @Override
	public void removeRoleSpaceStdField(RoleSpaceStdField roleSpaceStdField){
    	getHibernateTemplate().delete(roleSpaceStdField);
    }

    @Override
	public int bulkUpdateDeleteRoleSpaceStdFieldsForSpaceRole(SpaceRole spaceRole){
    	return getHibernateTemplate().bulkUpdate("delete from RoleSpaceStdField rsf where rsf.spaceRole.id = ?", spaceRole.getId());
    }
    
    ///////////////////////////
    // Countries // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ///////////////////////////

    @Override
	public void storeCountry(Country country){
    	getHibernateTemplate().merge(country);
    }

    @Override
	public Country loadCountry(String id){
    	return (Country) getHibernateTemplate().get(Country.class, id);
    }  
    
    @Override
	public List<Country> findAllCountries() {
        return getHibernateTemplate().find("from Country c order by c.id");
    }
    
    @Override
	public List<Language> getAllLanguages(){
    	 return getHibernateTemplate().find("from Language l");
    }

    /*
    public List<I18nStringResource> findI18nStringResourcesFor(I18nResourceTranslatable nt){
    	return getHibernateTemplate().find("from I18nStringResource rs where rs.id.key = ?", nt.getNameTranslationResourceKey());
    }
    */
    
    @Override
	public List<I18nStringResource> findI18nStringResourcesFor(String propertyName, I18nResourceTranslatable nt){
    	String key = nt.getPropertyTranslationResourceKey(propertyName);
    	return getHibernateTemplate().find("from I18nStringResource rs where rs.id.key = ?", key);
    }
    ///////////////////////////
    // Validation Expression // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ///////////////////////////

    @Override
	public void storeValidationExpression(ValidationExpression validationExpression){
    	getHibernateTemplate().merge(validationExpression);
    }
    
    @Override
	public ValidationExpression loadValidationExpression(long id){
    	return (ValidationExpression) getHibernateTemplate().get(ValidationExpression.class, id);
    }    
    
    @Override
	public List<ValidationExpression> findAllValidationExpressions() {
        return getHibernateTemplate().loadAll(ValidationExpression.class);
    }

	@Override
	public List<ValidationExpression> findValidationExpressionsMatching(ValidationExpressionSearch validationExpressionSearch){

		DetachedCriteria criteria = validationExpressionSearch.getDetachedCriteria();
		List<ValidationExpression> list = getHibernateTemplate().findByCriteria(criteria, validationExpressionSearch.getPageBegin()-1, validationExpressionSearch.getPageSize());
		criteria = validationExpressionSearch.getDetachedCriteriaForCount();
		criteria.setProjection(Projections.rowCount());
		Long count = (Long) getHibernateTemplate().findByCriteria(criteria).get(0);
		validationExpressionSearch.setResultCount(count);

		return list;
	}
	
	@Override
	public ValidationExpression findValidationExpressionByName(String name){
		ValidationExpression exp = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(ValidationExpression.class)
			.add(Property.forName("name").eq(name));
		List veList = getHibernateTemplate().findByCriteria(criteria);
		if(veList.size() >0){
			exp = (ValidationExpression) veList.get(0);
		}
    	return exp;
    }
	
	public boolean findIfValidationExpressionExistsByName(String name){
		return findValidationExpressionByName(name) != null;
	}
	
	
    
    //////////////////
    // Organization // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //////////////////

    @Override
	public void storeOrganization(Organization organization){
    	getHibernateTemplate().merge(organization);
    }

    //---------------------------------------------------------------------------------------------
    
    @Override
	public Organization loadOrganization(long id){
    	return (Organization) getHibernateTemplate().get(Organization.class, id);
    }
    
    

    //---------------------------------------------------------------------------------------------

	@Override
	public List<Organization> findOrganizationsMatching(OrganizationSearch organizationSearch){

		DetachedCriteria criteria = organizationSearch.getDetachedCriteria();
		List<Organization> list = getHibernateTemplate().findByCriteria(criteria, organizationSearch.getPageBegin()-1, organizationSearch.getPageSize());
		criteria = organizationSearch.getDetachedCriteriaForCount();
		criteria.setProjection(Projections.rowCount());
		Long count = (Long) getHibernateTemplate().findByCriteria(criteria).get(0);
		organizationSearch.setResultCount(count);

		return list;
	}//findAssetsMatching

    /////////////////
    // Space Roles // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /////////////////

	@Override
	public void storeSpaceRole(SpaceRole spaceRole){
		getHibernateTemplate().merge(spaceRole);
	}

	//---------------------------------------------------------------------------------------------

	@Override
	public SpaceRole loadSpaceRole(long spaceRoleId){
		return (SpaceRole)getHibernateTemplate().get(SpaceRole.class, spaceRoleId);
	}

	//---------------------------------------------------------------------------------------------

    @Override
	public List<SpaceRole> findSpaceRolesForSpace(Space space){
    	return getHibernateTemplate().find("select spaceRole from SpaceRole spaceRole where spaceRole.space.id = ?", space.getId());
    }

    //---------------------------------------------------------------------------------------------

    @Override
	public void removeSpaceRole(SpaceRole spaceRole){
    	getHibernateTemplate().delete(spaceRole);
    }

    //---------------------------------------------------------------------------------------------

    @Override
	public int bulkUpdateDeleteSpaceRolesForSpace(Space space){
    	List<SpaceRole> spaceRoleList = this.findSpaceRolesForSpace(space);
    	int records = 0;
    	if (spaceRoleList != null){
    		for (SpaceRole spaceRole : spaceRoleList){
    			records += this.bulkUpdateDeleteRoleSpaceStdFieldsForSpaceRole(spaceRole);
    			
    			this.removeSpaceRole(spaceRole);
    			records++;
    		}
    	}
    	return records;
    }

    //---------------------------------------------------------------------------------------------
    
    @Override
	public List<SpaceRole> findSpaceRolesForSpaceAndRoleType(Space space, int roleTypeId){
    	if (space==null){
    		return getHibernateTemplate().find("select spaceRole from SpaceRole spaceRole where spaceRole.roleTypeId = ?", roleTypeId);
    	}
    	
    	return getHibernateTemplate().find("select spaceRole from SpaceRole spaceRole where spaceRole.space.id = ? and spaceRole.roleTypeId = ?", new Object[]{space.getId(), roleTypeId});
    }

    ///////////////////////////////////
    // Inforama Document Integration // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ///////////////////////////////////
    
    //PageDictionary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
	public PageDictionary loadPageDictionary(String className){
    	return (PageDictionary) getHibernateTemplate().get(PageDictionary.class, className);
    }
    
    //---------------------------------------------------------------------------------------------
    
    @Override
	public PageDictionary loadPageDictionary(int id){
    	return (PageDictionary) getHibernateTemplate().get(PageDictionary.class, id);
    }

    //---------------------------------------------------------------------------------------------

    @Override
	public List<PageDictionary> findPageDictionaryMatching(PageDictionarySearch pageDictionarySearch){
    	DetachedCriteria criteria = pageDictionarySearch.getDetachedCriteria();
    	List<PageDictionary> list = getHibernateTemplate().findByCriteria(criteria, pageDictionarySearch.getPageBegin()-1, pageDictionarySearch.getPageSize());
    	criteria = pageDictionarySearch.getDetachedCriteriaForCount();
    	criteria.setProjection(Projections.rowCount());
    	Long count = (Long) getHibernateTemplate().findByCriteria(criteria).get(0);
    	pageDictionarySearch.setResultCount(count);

    	return list;
    }

    //---------------------------------------------------------------------------------------------

    @Override
	public void storePageDictionary(PageDictionary pageDictionary){
    	getHibernateTemplate().merge(pageDictionary);
    }

    //---------------------------------------------------------------------------------------------

    @Override
	public void removePageDictionary(PageDictionary pageDictionary){
    	getHibernateTemplate().delete(pageDictionary);
    } 
    
    
    //InforamaDocument ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
	public InforamaDocument loadInforamaDocument(int id){
    	return (InforamaDocument) getHibernateTemplate().get(InforamaDocument.class, id);
    }
    
    //---------------------------------------------------------------------------------------------

    @Override
	public List<InforamaDocument> findInforamaDocumentMatching(InforamaDocumentSearch inforamaDocumentSearch){
    	DetachedCriteria criteria = inforamaDocumentSearch.getDetachedCriteria();
    	List<InforamaDocument> list = getHibernateTemplate().findByCriteria(criteria, inforamaDocumentSearch.getPageBegin()-1, inforamaDocumentSearch.getPageSize());
    	criteria = inforamaDocumentSearch.getDetachedCriteriaForCount();
    	criteria.setProjection(Projections.rowCount());
    	Long count = (Long) getHibernateTemplate().findByCriteria(criteria).get(0);
    	inforamaDocumentSearch.setResultCount(count);

    	return list;
    }

    //---------------------------------------------------------------------------------------------

    @Override
	public List<InforamaDocument> findInforamaDocumentsForClassNameAndSpace(String className, Space space){
    	return getHibernateTemplate().find(
    			"select inforamaDocument from InforamaDocument inforamaDocument " +
    			"join inforamaDocument.pageDictionary pageDictionary " +
    			"join inforamaDocument.spaces spaces " +
    			"where  pageDictionary.pageClassName = ? and spaces.id = ?", 
    			new Object[]{ className, space.getId()}
    			);
    }

    //---------------------------------------------------------------------------------------------
    
    @Override
	public List<InforamaDocument> findAllInforamaDocuments(){

    	return getHibernateTemplate().find("from InforamaDocument inforamaDocument");
    }
    
    //---------------------------------------------------------------------------------------------

    @Override
	public void storeInforamaDocument(InforamaDocument inforamaDocument){
    	getHibernateTemplate().merge(inforamaDocument);
    }
    
    //PageInforamaDocument ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
	public PageInforamaDocument loadPageInforamaDocument(int id){
    	return (PageInforamaDocument) getHibernateTemplate().get(PageInforamaDocument.class, id);
    }
    
    //---------------------------------------------------------------------------------------------

    @Override
	public List<PageInforamaDocument> findPageInforamaDocumentForClassName(String className, Space space){
    	return getHibernateTemplate().find("select pageInforamaDocument from PageInforamaDocument pageInforamaDocument join pageInforamaDocument.pageDictionary pageDictionary join pageInforamaDocument.inforamaDocument inforamaDocument join inforamaDocument.spaces spaces where  pageDictionary.pageClassName = ? and spaces.id = ?", new Object[]{ className, space.getId()});
    }

    //---------------------------------------------------------------------------------------------
    
    @Override
	public void storePageInforamaDocument(PageInforamaDocument pageInforamaDocument){
    	getHibernateTemplate().merge(pageInforamaDocument);
    }

    //---------------------------------------------------------------------------------------------

    @Override
	public List<PageInforamaDocument> findPageInforamaDocumentMatching(PageInforamaDocumentSearch pageInforamaDocumentSearch){
    	DetachedCriteria criteria = pageInforamaDocumentSearch.getDetachedCriteria();
    	List<PageInforamaDocument> list = getHibernateTemplate().findByCriteria(criteria, pageInforamaDocumentSearch.getPageBegin()-1, pageInforamaDocumentSearch.getPageSize());
    	criteria = pageInforamaDocumentSearch.getDetachedCriteriaForCount();
    	criteria.setProjection(Projections.rowCount());
    	Long count = (Long) getHibernateTemplate().findByCriteria(criteria).get(0);
    	pageInforamaDocumentSearch.setResultCount(count);
    	
    	return list;
    }
    
    //---------------------------------------------------------------------------------------------
    
    @Override
	public void removeInforamaDocument(InforamaDocument inforamaDocument){
    	getHibernateTemplate().delete(inforamaDocument);
    }
    
    
    //InforamaDocumentParameter ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    @Override
	public List<InforamaDocumentParameter> findInforamaDocumentParameterMatching(InforamaDocumentParameterSearch inforamaDocumentParameterSearch){
    	DetachedCriteria criteria = inforamaDocumentParameterSearch.getDetachedCriteria();
    	List<InforamaDocumentParameter> list = getHibernateTemplate().findByCriteria(criteria, inforamaDocumentParameterSearch.getPageBegin()-1, inforamaDocumentParameterSearch.getPageSize());
    	criteria = inforamaDocumentParameterSearch.getDetachedCriteriaForCount();
    	criteria.setProjection(Projections.rowCount());
    	Long count = (Long) getHibernateTemplate().findByCriteria(criteria).get(0);
    	inforamaDocumentParameterSearch.setResultCount(count);

    	return list;
    }
    
    //---------------------------------------------------------------------------------------------

    @Override
	public void storeInforamaDocumentParameter(InforamaDocumentParameter inforamaDocumentParameter){
    	getHibernateTemplate().merge(inforamaDocumentParameter);
    }

    //---------------------------------------------------------------------------------------------

    @Override
	public InforamaDocumentParameter loadInforamaDocumentParameter(int id){
    	return (InforamaDocumentParameter) getHibernateTemplate().get(InforamaDocumentParameter.class, id);
    }
    
    //---------------------------------------------------------------------------------------------
    
    @Override
	public List<InforamaDocumentParameter> findInforamaDocumentParametersForDocument(InforamaDocument inforamaDocument){
    	return getHibernateTemplate().find("select inforamaDocumentParameter from InforamaDocumentParameter inforamaDocumentParameter where inforamaDocumentParameter.inforamaDocument.id = ?", inforamaDocument.getId());
    }
    
    //---------------------------------------------------------------------------------------------
    
    @Override
	public void removeInforamaDocumentParameter(InforamaDocumentParameter inforamaDocumentParameter){
    	getHibernateTemplate().delete(inforamaDocumentParameter);
    }
    
    //InforamaDocument and Spaces ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    @Override
	public List<InforamaDocument> findInforamaDocumentsForSpace(Space space){
    	return getHibernateTemplate().find("select inforamaDocument from InforamaDocument inforamaDocument join inforamaDocument.spaces spaces where  spaces.id = ?", space.getId());
    }
    
    //---------------------------------------------------------------------------------------------

    @Override
	public List<Space> findSpacesForInforamaDocument(InforamaDocument inforamaDocument){
    	//return getHibernateTemplate().find("select pageInforamaDocument from PageInforamaDocument pageInforamaDocument join pageInforamaDocument.pageDictionary pageDictionary join pageInforamaDocument.inforamaDocument inforamaDocument join inforamaDocument.spaces spaces where  pageDictionary.pageClassName = ? and spaces.id = ?", new Object[]{ className, space.getId()});
    	//join InforamaDocument inforamaDocument where inforamaDocument.id = ?
    	                                  //select asset from Asset asset, Item item where asset in elements(item.assets) and item.id = ?
    	return getHibernateTemplate().find("select space from Space space, InforamaDocument inforamaDocument where space in elements (inforamaDocument.spaces) and inforamaDocument.id=?", inforamaDocument.getId());
    }
    
    //---------------------------------------------------------------------------------------------
    
    @Override
	public int loadCountSpacesForInforamaDocument(InforamaDocument inforamaDocument){
    	Long count = (Long)getHibernateTemplate().find("select count(space) from Space space, InforamaDocument inforamaDocument where space in elements (inforamaDocument.spaces) and inforamaDocument.id=?", inforamaDocument.getId()).get(0);
    	
    	return count.intValue();
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * note that this is automatically configured to run on startup 
     * as a spring bean "init-method"
     */
    public void createSchema() {
    	/*
    	List<CustomAttributeLookupValue> values = 
    		getHibernateTemplate().find("from CustomAttributeLookupValue value");
    	for(CustomAttributeLookupValue value : values){
    		List<I18nStringResource> rss1 =
    			getHibernateTemplate().find("from I18nStringResource rs where rs.id.key=? and  rs.id.locale=?", 
    					new Object[]{value.getNameTranslationResourceKey(),"el"});
    		if(!rss1.isEmpty()){
        		I18nStringResource rs = (I18nStringResource) rss1.get(0);
        		if(value.getName() == null){
        			value.setName(rs.getValue());
        			this.update(value);
        		}
    		}
    		else{
    			I18nStringIdentifier id = new I18nStringIdentifier(value.getNameTranslationResourceKey(), "el");
    			I18nStringResource enRs = new I18nStringResource(id, value.getValue());
    			this.save(enRs);
    		}
    		List<I18nStringResource> rss =
    			getHibernateTemplate().find("from I18nStringResource rs where rs.id.key=? and  rs.id.locale=?", 
					new Object[]{value.getNameTranslationResourceKey(),"en"});
    		if(rss.isEmpty()){
    			I18nStringIdentifier id = new I18nStringIdentifier(value.getNameTranslationResourceKey(), "en");
    			I18nStringResource enRs = new I18nStringResource(id, value.getValue());
    			this.save(enRs);
    		}
    	}
    	*/
        try { 
        	//schemaHelper.updateSchema();
            List results = getHibernateTemplate().find("from User user where user.id = 1");
            
        } catch (Exception e) {
            logger.warn("expected database schema does not exist, will create. Error is: " + e.getMessage());
            schemaHelper.updateSchema();

            initCountries();
            
            // Setup Reserved Admin Role
            SpaceRole admininstrator = new SpaceRole(null, RoleType.ADMINISTRATOR.getDescription(), RoleType.ADMINISTRATOR);
            
            logger.info("inserting default roles into database");
            this.storeSpaceRole(admininstrator);

            Country greece = this.loadCountry("GR");
            logger.info("inserting default admin user into database");
            User admin = new User();
            admin.setLoginName("admin");
            admin.setName("Support");
            admin.setLastname("Administrator");
            admin.setAddress("19, Kalvou Street, Nea Ionia");
            admin.setZip("14231");
            admin.setPhone("2111027900");
            admin.setFax("2111027999");
            admin.setEmail("info@abiss.gr");
            admin.setLocale("en");
            admin.setCountry(greece);
            admin.setPassword("21232f297a57a5a743894a0e4a801fc3");
            admin.addSpaceRole(loadSpaceRole(1));
            admin.setDateCreated(new Date());
            admin.setDateLastUpdated(admin.getDateCreated());
            admin.setCreatedBy(admin);
            admin.setLastUpdatedBy(admin);
            //this.storeUser(admin);
            User anonymous = new User();
            anonymous.setLoginName("anonymous");
            anonymous.setName("Anonymous");
            anonymous.setLastname("User");
            anonymous.setEmail("info@abiss.gr");
            anonymous.setLocale("el");
            anonymous.setCountry(greece);
            anonymous.setPassword("21232f297a57a5a743894a0e4a801fc3");
            anonymous.setDateCreated(new Date());
            anonymous.setDateLastUpdated(admin.getDateCreated());
            anonymous.setCreatedBy(admin);
            anonymous.setLastUpdatedBy(admin);
            //this.storeUser(admin);

            // Setup Reserved Organization
            Organization org = new Organization();
            org.setName("Abiss.gr");
            org.setVatNumber("EL999438460");
            org.setAddress("19, Kalvou Street, Nea Ionia");
            org.setZip("14231");
            org.setPhone("2111027900");
            org.setFax("2111027999");
            org.setWeb("http://www.Abiss.gr");
            org.setEmail("info at abiss.gr");
            org.setCountry(greece);
            org.setDateCreated(new Date());
            org.setDateLastUpdated(org.getDateCreated());
            //admin = loadUser(1);
            org.setCreatedBy(admin);
            org.setLastUpdatedBy(admin);
            org.setLastUpdateComment("Created by Calipso database initialization.");
            org.addUser(admin);
            this.storeUser(admin);
            this.storeUser(anonymous);
            
            // Setup default regexp validations
            logger.info("inserting default validation regexps into database");
            this.storeValidationExpression(new ValidationExpression("None", "No validation", RegexpValidator.NO_VALIDATION));
            this.storeValidationExpression(new ValidationExpression("Email", "Simple email validation", "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"));
            this.storeValidationExpression(new ValidationExpression("Max 20 words", "Allow a maximum wordcount of 20", "^[^\\p{L}\\p{N}_]*(?:[\\p{L}\\p{N}_]+\\b[^\\p{L}\\p{N}_]*){1,20}$"));
            this.storeValidationExpression(new ValidationExpression("Max 50 words", "Allow a maximum wordcount of 500", "^[^\\p{L}\\p{N}_]*(?:[\\p{L}\\p{N}_]+\\b[^\\p{L}\\p{N}_]*){1,50}$"));
            this.storeValidationExpression(new ValidationExpression("Max 100 words", "Allow a maximum wordcount of 100", "^[^\\p{L}\\p{N}_]*(?:[\\p{L}\\p{N}_]+\\b[^\\p{L}\\p{N}_]*){1,100}$"));
            this.storeValidationExpression(new ValidationExpression("Max 200 words", "Allow a maximum wordcount of 200", "^[^\\p{L}\\p{N}_]*(?:[\\p{L}\\p{N}_]+\\b[^\\p{L}\\p{N}_]*){1,200}$"));
            this.storeValidationExpression(new ValidationExpression("Max 300 words", "Allow a maximum wordcount of 300", "^[^\\p{L}\\p{N}_]*(?:[\\p{L}\\p{N}_]+\\b[^\\p{L}\\p{N}_]*){1,300}$"));
            this.storeValidationExpression(new ValidationExpression("Max 500 words", "Allow a maximum wordcount of 500", "^[^\\p{L}\\p{N}_]*(?:[\\p{L}\\p{N}_]+\\b[^\\p{L}\\p{N}_]*){1,500}$"));
            // Setup initial properties
            logger.info("inserting default configuration properties into database");

            this.storeConfig(new Config("calipso.hideLoginLink", Boolean.FALSE.toString()));
            this.storeConfig(new Config("calipso.hideRegisterLink", Boolean.FALSE.toString()));
            this.storeConfig(new Config("mail.forceVerification", Boolean.TRUE.toString()));
            this.storeConfig(new Config("attachment.extentionsAllowed", 
            		new StringBuffer()
            			.append("odt ods odp odg odf ")// OOo
            			.append("doc docx xls xlsx ")// MS Office
            			.append("gif jpg png bmp ")// Images
            			.append("pdf csv txt xml html rtf ")// Misc
            			.toString()));
            this.storeConfig(new Config("classes.dashboard", "gr.abiss.calipso.wicket.DashboardPanel"));
            //initCustomAttributes();
            return;
        }
        logger.info("database schema exists, normal startup");        
    }


	private void initCustomAttributes() {
		
		String attributeName = "SEC(2010)572";

		ValidationExpression noValidation = this.loadValidationExpression(1);
		
		// 1520 categorization
		AssetTypeCustomAttribute attribute = new AssetTypeCustomAttribute(attributeName+": Sector Information", 12,
				"java.lang.String", noValidation, true, true); 
		AssetTypeCustomAttribute attribute2 = new AssetTypeCustomAttribute(attributeName+": Type of Complaint or Inquiry Information", 12,
				"java.lang.String", noValidation, true, true); 
		
		List<CustomAttributeLookupValue> lookupValues = new LinkedList<CustomAttributeLookupValue>();
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Consumer Goods",1,1));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Food — Fruit and vegetables",1,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Food — Meat",2,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Food — Bread and Cereals",3,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Food — Health food and nutrients",4,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Food — Other",5,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Non-alcoholic beverages",6,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Alcoholic beverages",7,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Tobacco",8,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Clothing (including tailor-made goods) and footwear",9,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "House maintenance and improvement goods",10,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Furnishings",11,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Large domestic household appliances",12,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Small domestic household appliances",13,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Electronic goods",14,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Information and communication technology (ICT) goods",15,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Leisure goods (sports equipment, musical instruments, etc)",16,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "New cars",17,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Second-hand cars",18,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Other personal transport",19,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Spares and accessories for vehicles and other means of personal transport",20,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Fuels and lubricants for vehicles and other means of personal transport",21,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Books, magazines, newspapers, stationery (excluding postal delivery)",22,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Pets and pet goods",23,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Electrical appliances for personal care",24,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Cosmetics and toiletries for personal care",25,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Jewellery, silverware, clocks, watches and accessories",26,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Baby and child care articles",27,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Cleaning and maintenance products, articles for cleaning and non-durablehousehold articles",28,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "General Consumer Services",2,1));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Real estate services",1,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Construction of new houses",2,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "House maintenance and improvement services",3,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "House removal and storage",4,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "House cleaning services",5,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Personal care services",6,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Cleaning, repair and hiring of clothing and footwear",7,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Support, research and intermediary services",8,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Maintenance and repair of vehicles and other transport",9,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Legal services & accountancy",10,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Funeral services",11,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Child care",12,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Pet services",13,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Financial Services",3,1));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Financial Services — Payment account and payment services",1,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Financial Services — Credit (excluding mortgage/home loans)",2,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Financial Services — Mortgages / Home loans",3,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Financial Services — Savings",4,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Financial Services — Other",5,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Investments, pensions and securities",6,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Non-life Insurance — Home and property",7,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Non-life Insurance — Transport",8,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Non-life Insurance – Travel",9,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Non-life Insurance — Health, accident and other",10,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Insurance — Life",11,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Postal services and electronic communications",4,1));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Postal services & couriers",1,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Fixed telephone services",2,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Mobile telephone services",3,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Internet services",4,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Television services",5,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Other communication services",6,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Transport services",5,1));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Tram, bus, metro and underground",1,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Railways",2,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Airlines",3,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Taxi",4,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Sea, river, other water transport",5,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Transport infrastructure services",6,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Rental services",7,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Leisure Services",6,1));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Hotels and other holiday accommodation",1,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Package travel",2,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Travel agency services",3,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Timeshare and similar",4,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Restaurants and bars",5,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Services related to sports and hobbies",6,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Cultural and entertainment services",7,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Gambling, lotteries",8,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Other leisure services",9,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Energy and Water",7,1));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Water",1,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Electricity",2,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Gas",3,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Other energy sources",4,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Health",8,1));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Prescribed medication",1,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Over-the-counter medication",2,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Medical devices and other physical aids used by patients",3,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Health services",4,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Retirement homes and home care",5,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Education",10,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Schools",1,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Language, driving instruction and other private courses",2,2));
		lookupValues.add(new CustomAttributeLookupValue(attribute, "Other",11,1));
		lookupValues.add(new CustomAttributeLookupValue(attribute, " Other (Includes both goods and services)",1,2));
		

		List<CustomAttributeLookupValue> lookupValues2 = new LinkedList<CustomAttributeLookupValue>();
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Quality of goods and services",12,1));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Defective, caused damage",1,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Not in conformity with order",2,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Not fit for particular purpose",3,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Delivery of goods/ Provision of services",13,1));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Not delivered / not provided",1,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Partially delivered / partially provided",2,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Delay",3,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Not available / No access",4,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Refusal to sell / provide a good or a service",5,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Suspension of the delivery of a good or the provision of a service without prior notice",6,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Opening hours",7,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Customer service",8,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "After-sales service/assistance",9,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, " Other issues related to the delivery of goods/provisions of services",10,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Price / Tariff",14,1));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Price / tariff change",1,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Price discrimination",2,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Tariff transparency (unclear, complex)",3,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Other issues related to price/tariff",4,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Invoicing / billing and debt collection",15,1));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Incorrect invoice / bill",1,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Unclear invoice / bill",2,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Non-issue of invoice or difficult access to invoice/monthly statement",3,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Unjustified invoicing / billing",4,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Debt collection",5,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Other issues related to invoicing/billing and debt collection",6,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Warranty / statutory guarantee and commercial guarantees",16,1));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Warranty / statutory guarantee not honoured",1,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Commercial guarantee not honoured",2,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Redress",17,1));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Difficult access to redress",1,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "No redress",2,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Part or incorrect redress",3,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Delayed redress",4,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Other issues related to redress",5,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Unfair Commercial Practices",18,1));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Misleading contractual terms and conditions",1,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Incorrect or misleading indication of prices / tariffs and labelling",2,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Misleading advertising",3,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Unsolicited advertising",4,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Unsolicited goods or services",5,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Aggressive selling practices",6,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Fraudulent practices",7,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Other unfair commercial practices",8,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Contracts and sales",19,1));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Unfair contractual terms / change of contractual terms",1,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Lack of information",2,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Order confirmation (not received/wrong)",3,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Cooling-off period / Right of withdrawal",4,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Payments (e.g. prepayments and instalments)",5,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Rescission of contract",6,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Minimum contractual period",7,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Other issues related to contracts and sales",8,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Provider change / switching",20,1));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Provider change / switching",1,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Safety - covers both goods (including food) and services",21,1));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Product safety – covers both goods (including food) and services",1,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Package, labelling and instructions - covers both goods (including food) and services",2,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Privacy and data protection",22,1));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Data protection",1,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Privacy",2,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Other issues related to privacy and data protection",3,2));
		lookupValues2.add(new CustomAttributeLookupValue(attribute2, "Other issues",23,1));
		
		// add allowed/lookup values to custom attribute but add their parent
		CustomAttributeLookupValue parent = null;
		for(CustomAttributeLookupValue value : lookupValues){
			//logger.info("Saving "+attribute.getName()+" value: "+value);
			// either update parent handle or set the existing into the value?
			if(value.getLevel() == 1){
				parent = value;
			}
			else{
				parent.addChild(value);
			}
			attribute.addAllowedLookupValue(value);
		}

		this.storeCustomAttribute(attribute);
		parent = null;
		for(CustomAttributeLookupValue value : lookupValues2){
			//logger.info("Saving "+attribute2.getName()+" value: "+value);
			// either update parent handle or set the existing into the value?
			if(value.getLevel() == 1){
				parent = value;
			}
			else{
				parent.addChild(value);
			}
			attribute2.addAllowedLookupValue(value);
		}
		this.storeCustomAttribute(attribute2);
		//logger.info("Saving "+lookupValues.size()+" and " +lookupValues2.size() + "values for lookupValues and lookupValues2 respectively");

		// Create translation resources for attribute name
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(attribute.getNameTranslationResourceKey(), "en"), attributeName+": Sector Information"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(attribute.getNameTranslationResourceKey(), "el"), attributeName+": Τομεακές Πληροφορίες"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(attribute2.getNameTranslationResourceKey(), "en"), attributeName+": Type of Complaint or Inquiry Information"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(attribute2.getNameTranslationResourceKey(), "el"), attributeName+": Είδος Καταγγελίας ή Πληροφορίες Αιτήματος"));

		// Create translation resources for attribute lookup value names
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(1-1).getNameTranslationResourceKey(), "en"), "Consumer Goods"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(2-1).getNameTranslationResourceKey(), "en"), "Food — Fruit and vegetables"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(3-1).getNameTranslationResourceKey(), "en"), "Food — Meat"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(4-1).getNameTranslationResourceKey(), "en"), "Food — Bread and Cereals"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(5-1).getNameTranslationResourceKey(), "en"), "Food — Health food and nutrients"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(6-1).getNameTranslationResourceKey(), "en"), "Food — Other"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(7-1).getNameTranslationResourceKey(), "en"), "Non-alcoholic beverages"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(8-1).getNameTranslationResourceKey(), "en"), "Alcoholic beverages"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(9-1).getNameTranslationResourceKey(), "en"), "Tobacco"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(10-1).getNameTranslationResourceKey(), "en"), "Clothing (including tailor-made goods) and footwear"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(11-1).getNameTranslationResourceKey(), "en"), "House maintenance and improvement goods"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(12-1).getNameTranslationResourceKey(), "en"), "Furnishings"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(13-1).getNameTranslationResourceKey(), "en"), "Large domestic household appliances"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(14-1).getNameTranslationResourceKey(), "en"), "Small domestic household appliances"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(15-1).getNameTranslationResourceKey(), "en"), "Electronic goods"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(16-1).getNameTranslationResourceKey(), "en"), "Information and communication technology (ICT) goods"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(17-1).getNameTranslationResourceKey(), "en"), "Leisure goods (sports equipment, musical instruments, etc)"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(18-1).getNameTranslationResourceKey(), "en"), "New cars"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(19-1).getNameTranslationResourceKey(), "en"), "Second-hand cars"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(20-1).getNameTranslationResourceKey(), "en"), "Other personal transport"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(21-1).getNameTranslationResourceKey(), "en"), "Spares and accessories for vehicles and other means of personal transport"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(22-1).getNameTranslationResourceKey(), "en"), "Fuels and lubricants for vehicles and other means of personal transport"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(23-1).getNameTranslationResourceKey(), "en"), "Books, magazines, newspapers, stationery (excluding postal delivery)"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(24-1).getNameTranslationResourceKey(), "en"), "Pets and pet goods"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(25-1).getNameTranslationResourceKey(), "en"), "Electrical appliances for personal care"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(26-1).getNameTranslationResourceKey(), "en"), "Cosmetics and toiletries for personal care"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(27-1).getNameTranslationResourceKey(), "en"), "Jewellery, silverware, clocks, watches and accessories"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(28-1).getNameTranslationResourceKey(), "en"), "Baby and child care articles"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(29-1).getNameTranslationResourceKey(), "en"), "Cleaning and maintenance products, articles for cleaning and non-durablehousehold articles"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(30-1).getNameTranslationResourceKey(), "en"), "General Consumer Services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(31-1).getNameTranslationResourceKey(), "en"), "Real estate services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(32-1).getNameTranslationResourceKey(), "en"), "Construction of new houses"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(33-1).getNameTranslationResourceKey(), "en"), "House maintenance and improvement services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(34-1).getNameTranslationResourceKey(), "en"), "House removal and storage"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(35-1).getNameTranslationResourceKey(), "en"), "House cleaning services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(36-1).getNameTranslationResourceKey(), "en"), "Personal care services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(37-1).getNameTranslationResourceKey(), "en"), "Cleaning, repair and hiring of clothing and footwear"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(38-1).getNameTranslationResourceKey(), "en"), "Support, research and intermediary services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(39-1).getNameTranslationResourceKey(), "en"), "Maintenance and repair of vehicles and other transport"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(40-1).getNameTranslationResourceKey(), "en"), "Legal services & accountancy"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(41-1).getNameTranslationResourceKey(), "en"), "Funeral services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(42-1).getNameTranslationResourceKey(), "en"), "Child care"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(43-1).getNameTranslationResourceKey(), "en"), "Pet services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(44-1).getNameTranslationResourceKey(), "en"), "Financial Services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(45-1).getNameTranslationResourceKey(), "en"), "Financial Services — Payment account and payment services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(46-1).getNameTranslationResourceKey(), "en"), "Financial Services — Credit (excluding mortgage/home loans)"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(47-1).getNameTranslationResourceKey(), "en"), "Financial Services — Mortgages / Home loans"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(48-1).getNameTranslationResourceKey(), "en"), "Financial Services — Savings"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(49-1).getNameTranslationResourceKey(), "en"), "Financial Services — Other"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(50-1).getNameTranslationResourceKey(), "en"), "Investments, pensions and securities"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(51-1).getNameTranslationResourceKey(), "en"), "Non-life Insurance — Home and property"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(52-1).getNameTranslationResourceKey(), "en"), "Non-life Insurance — Transport"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(53-1).getNameTranslationResourceKey(), "en"), "Non-life Insurance – Travel"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(54-1).getNameTranslationResourceKey(), "en"), "Non-life Insurance — Health, accident and other"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(55-1).getNameTranslationResourceKey(), "en"), "Insurance — Life"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(56-1).getNameTranslationResourceKey(), "en"), "Postal services and electronic communications"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(57-1).getNameTranslationResourceKey(), "en"), "Postal services & couriers"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(58-1).getNameTranslationResourceKey(), "en"), "Fixed telephone services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(59-1).getNameTranslationResourceKey(), "en"), "Mobile telephone services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(60-1).getNameTranslationResourceKey(), "en"), "Internet services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(61-1).getNameTranslationResourceKey(), "en"), "Television services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(62-1).getNameTranslationResourceKey(), "en"), "Other communication services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(63-1).getNameTranslationResourceKey(), "en"), "Transport services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(64-1).getNameTranslationResourceKey(), "en"), "Tram, bus, metro and underground"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(65-1).getNameTranslationResourceKey(), "en"), "Railways"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(66-1).getNameTranslationResourceKey(), "en"), "Airlines"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(67-1).getNameTranslationResourceKey(), "en"), "Taxi"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(68-1).getNameTranslationResourceKey(), "en"), "Sea, river, other water transport"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(69-1).getNameTranslationResourceKey(), "en"), "Transport infrastructure services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(70-1).getNameTranslationResourceKey(), "en"), "Rental services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(71-1).getNameTranslationResourceKey(), "en"), "Leisure Services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(72-1).getNameTranslationResourceKey(), "en"), "Hotels and other holiday accommodation"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(73-1).getNameTranslationResourceKey(), "en"), "Package travel"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(74-1).getNameTranslationResourceKey(), "en"), "Travel agency services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(75-1).getNameTranslationResourceKey(), "en"), "Timeshare and similar"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(76-1).getNameTranslationResourceKey(), "en"), "Restaurants and bars"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(77-1).getNameTranslationResourceKey(), "en"), "Services related to sports and hobbies"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(78-1).getNameTranslationResourceKey(), "en"), "Cultural and entertainment services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(79-1).getNameTranslationResourceKey(), "en"), "Gambling, lotteries"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(80-1).getNameTranslationResourceKey(), "en"), "Other leisure services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(81-1).getNameTranslationResourceKey(), "en"), "Energy and Water"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(82-1).getNameTranslationResourceKey(), "en"), "Water"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(83-1).getNameTranslationResourceKey(), "en"), "Electricity"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(84-1).getNameTranslationResourceKey(), "en"), "Gas"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(85-1).getNameTranslationResourceKey(), "en"), "Other energy sources"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(86-1).getNameTranslationResourceKey(), "en"), "Health"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(87-1).getNameTranslationResourceKey(), "en"), "Prescribed medication"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(88-1).getNameTranslationResourceKey(), "en"), "Over-the-counter medication"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(89-1).getNameTranslationResourceKey(), "en"), "Medical devices and other physical aids used by patients"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(90-1).getNameTranslationResourceKey(), "en"), "Health services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(91-1).getNameTranslationResourceKey(), "en"), "Retirement homes and home care"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(92-1).getNameTranslationResourceKey(), "en"), "Education"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(93-1).getNameTranslationResourceKey(), "en"), "Schools"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(94-1).getNameTranslationResourceKey(), "en"), "Language, driving instruction and other private courses"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(95-1).getNameTranslationResourceKey(), "en"), "Other"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(96-1).getNameTranslationResourceKey(), "en"), " Other (Includes both goods and services)"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(1-1).getNameTranslationResourceKey(), "en"), "Quality of goods and services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(2-1).getNameTranslationResourceKey(), "en"), "Defective, caused damage"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(3-1).getNameTranslationResourceKey(), "en"), "Not in conformity with order"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(4-1).getNameTranslationResourceKey(), "en"), "Not fit for particular purpose"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(5-1).getNameTranslationResourceKey(), "en"), "Delivery of goods/ Provision of services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(6-1).getNameTranslationResourceKey(), "en"), "Not delivered / not provided"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(7-1).getNameTranslationResourceKey(), "en"), "Partially delivered / partially provided"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(8-1).getNameTranslationResourceKey(), "en"), "Delay"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(9-1).getNameTranslationResourceKey(), "en"), "Not available / No access"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(10-1).getNameTranslationResourceKey(), "en"), "Refusal to sell / provide a good or a service"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(11-1).getNameTranslationResourceKey(), "en"), "Suspension of the delivery of a good or the provision of a service without prior notice"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(12-1).getNameTranslationResourceKey(), "en"), "Opening hours"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(13-1).getNameTranslationResourceKey(), "en"), "Customer service"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(14-1).getNameTranslationResourceKey(), "en"), "After-sales service/assistance"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(15-1).getNameTranslationResourceKey(), "en"), " Other issues related to the delivery of goods/provisions of services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(16-1).getNameTranslationResourceKey(), "en"), "Price / Tariff"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(17-1).getNameTranslationResourceKey(), "en"), "Price / tariff change"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(18-1).getNameTranslationResourceKey(), "en"), "Price discrimination"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(19-1).getNameTranslationResourceKey(), "en"), "Tariff transparency (unclear, complex)"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(20-1).getNameTranslationResourceKey(), "en"), "Other issues related to price/tariff"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(21-1).getNameTranslationResourceKey(), "en"), "Invoicing / billing and debt collection"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(22-1).getNameTranslationResourceKey(), "en"), "Incorrect invoice / bill"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(23-1).getNameTranslationResourceKey(), "en"), "Unclear invoice / bill"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(24-1).getNameTranslationResourceKey(), "en"), "Non-issue of invoice or difficult access to invoice/monthly statement"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(25-1).getNameTranslationResourceKey(), "en"), "Unjustified invoicing / billing"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(26-1).getNameTranslationResourceKey(), "en"), "Debt collection"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(27-1).getNameTranslationResourceKey(), "en"), "Other issues related to invoicing/billing and debt collection"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(28-1).getNameTranslationResourceKey(), "en"), "Warranty / statutory guarantee and commercial guarantees"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(29-1).getNameTranslationResourceKey(), "en"), "Warranty / statutory guarantee not honoured"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(30-1).getNameTranslationResourceKey(), "en"), "Commercial guarantee not honoured"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(31-1).getNameTranslationResourceKey(), "en"), "Redress"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(32-1).getNameTranslationResourceKey(), "en"), "Difficult access to redress"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(33-1).getNameTranslationResourceKey(), "en"), "No redress"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(34-1).getNameTranslationResourceKey(), "en"), "Part or incorrect redress"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(35-1).getNameTranslationResourceKey(), "en"), "Delayed redress"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(36-1).getNameTranslationResourceKey(), "en"), "Other issues related to redress"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(37-1).getNameTranslationResourceKey(), "en"), "Unfair Commercial Practices"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(38-1).getNameTranslationResourceKey(), "en"), "Misleading contractual terms and conditions"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(39-1).getNameTranslationResourceKey(), "en"), "Incorrect or misleading indication of prices / tariffs and labelling"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(40-1).getNameTranslationResourceKey(), "en"), "Misleading advertising"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(41-1).getNameTranslationResourceKey(), "en"), "Unsolicited advertising"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(42-1).getNameTranslationResourceKey(), "en"), "Unsolicited goods or services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(43-1).getNameTranslationResourceKey(), "en"), "Aggressive selling practices"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(44-1).getNameTranslationResourceKey(), "en"), "Fraudulent practices"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(45-1).getNameTranslationResourceKey(), "en"), "Other unfair commercial practices"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(46-1).getNameTranslationResourceKey(), "en"), "Contracts and sales"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(47-1).getNameTranslationResourceKey(), "en"), "Unfair contractual terms / change of contractual terms"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(48-1).getNameTranslationResourceKey(), "en"), "Lack of information"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(49-1).getNameTranslationResourceKey(), "en"), "Order confirmation (not received/wrong)"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(50-1).getNameTranslationResourceKey(), "en"), "Cooling-off period / Right of withdrawal"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(51-1).getNameTranslationResourceKey(), "en"), "Payments (e.g. prepayments and instalments)"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(52-1).getNameTranslationResourceKey(), "en"), "Rescission of contract"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(53-1).getNameTranslationResourceKey(), "en"), "Minimum contractual period"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(54-1).getNameTranslationResourceKey(), "en"), "Other issues related to contracts and sales"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(55-1).getNameTranslationResourceKey(), "en"), "Provider change / switching"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(56-1).getNameTranslationResourceKey(), "en"), "Provider change / switching"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(57-1).getNameTranslationResourceKey(), "en"), "Safety - covers both goods (including food) and services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(58-1).getNameTranslationResourceKey(), "en"), " Product safety – covers both goods (including food) and services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(59-1).getNameTranslationResourceKey(), "en"), " Package, labelling and instructions - covers both goods (including food) and services"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(60-1).getNameTranslationResourceKey(), "en"), "Privacy and data protection"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(61-1).getNameTranslationResourceKey(), "en"), " Data protection"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(62-1).getNameTranslationResourceKey(), "en"), " Privacy"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(63-1).getNameTranslationResourceKey(), "en"), " Other issues related to privacy and data protection"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(64-1).getNameTranslationResourceKey(), "en"), "Other issues"));
		
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(1-1).getNameTranslationResourceKey(), "el"), "Καταναλωτικά Αγαθά"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(2-1).getNameTranslationResourceKey(), "el"), "Τρόφιμα — Φρούτα και λαχανικά"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(3-1).getNameTranslationResourceKey(), "el"), "Τρόφιμα — Κρέας"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(4-1).getNameTranslationResourceKey(), "el"), "Τρόφιμα — Ψωμί και δημητριακά"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(5-1).getNameTranslationResourceKey(), "el"), "Τρόφιμα — Υγιεινά τρόφιμα και θρεπτικά συστατικά"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(6-1).getNameTranslationResourceKey(), "el"), "Τρόφιμα — Άλλα"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(7-1).getNameTranslationResourceKey(), "el"), "Μη αλκοολούχα ποτά"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(8-1).getNameTranslationResourceKey(), "el"), "Αλκοολούχα ποτά"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(9-1).getNameTranslationResourceKey(), "el"), "Καπνός"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(10-1).getNameTranslationResourceKey(), "el"), "Είδη ένδυσης και υπόδησης"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(11-1).getNameTranslationResourceKey(), "el"), "Αγαθά για συντήρηση και βελτίωση της κατοικίας"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(12-1).getNameTranslationResourceKey(), "el"), "Επίπλωση και διακόσμηση"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(13-1).getNameTranslationResourceKey(), "el"), "Μεγάλες οικιακές συσκευές"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(14-1).getNameTranslationResourceKey(), "el"), "Μικρές οικιακές συσκευές"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(15-1).getNameTranslationResourceKey(), "el"), "Ηλεκτρονικά αγαθά"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(16-1).getNameTranslationResourceKey(), "el"), "Αγαθά τεχνολογιών πληροφοριών και επικοινωνιών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(17-1).getNameTranslationResourceKey(), "el"), "Αγαθά αναψυχής"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(18-1).getNameTranslationResourceKey(), "el"), "Καινούργια αυτοκίνητα"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(19-1).getNameTranslationResourceKey(), "el"), "Μεταχειρισμένα αυτοκίνητα"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(20-1).getNameTranslationResourceKey(), "el"), "Άλλα μέσα προσωπικής μεταφοράς"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(21-1).getNameTranslationResourceKey(), "el"), "Ανταλλακτικά και εξαρτήματα για οχήματα και άλλα μέσα προσωπικής μεταφοράς"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(22-1).getNameTranslationResourceKey(), "el"), "Καύσιμα και λιπαντικά για οχήματα και άλλα μέσα προσωπικής μεταφοράς"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(23-1).getNameTranslationResourceKey(), "el"), "Βιβλία, περιοδικά, εφημερίδες και χαρτικά είδη"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(24-1).getNameTranslationResourceKey(), "el"), "Ζώα συντροφιάς και αγαθά για ζώα συντροφιάς"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(25-1).getNameTranslationResourceKey(), "el"), "Ηλεκτρικές συσκευές προσωπικής μέριμνας"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(26-1).getNameTranslationResourceKey(), "el"), "Καλλυντικά και είδη υγιεινής προσωπικής μέριμνας"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(27-1).getNameTranslationResourceKey(), "el"), "Κοσμήματα, ασημικά, ρολόγια κάθε είδους και αξεσουάρ"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(28-1).getNameTranslationResourceKey(), "el"), "Είδη μέριμνας βρεφών και παιδιών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(29-1).getNameTranslationResourceKey(), "el"), "Προϊόντα για καθαρισμό και συντήρηση, είδη για καθαρισμό και μη διαρκή οικιακά είδη"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(30-1).getNameTranslationResourceKey(), "el"), "Γενικές Υπηρεσίες προς τον Καταναλωτή"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(31-1).getNameTranslationResourceKey(), "el"), "Υπηρεσίες που συνδέονται με ακίνητα"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(32-1).getNameTranslationResourceKey(), "el"), "Κατασκευή νέων κατοικιών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(33-1).getNameTranslationResourceKey(), "el"), "Υπηρεσίες συντήρησης και βελτίωσης κατοικιών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(34-1).getNameTranslationResourceKey(), "el"), "Απομάκρυνση περιεχομένου κατοικίας και αποθήκευση"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(35-1).getNameTranslationResourceKey(), "el"), "Υπηρεσίες καθαρισμού κατοικίας"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(36-1).getNameTranslationResourceKey(), "el"), "Υπηρεσίες προσωπικής μέριμνας"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(37-1).getNameTranslationResourceKey(), "el"), "Καθάρισμα, επιδιόρθωση και ενοικίαση ενδυμάτων και υποδημάτων"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(38-1).getNameTranslationResourceKey(), "el"), "Υποστήριξη, έρευνα και υπηρεσίες διαμεσολάβησης"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(39-1).getNameTranslationResourceKey(), "el"), "Συντήρηση και επισκευή οχημάτων και άλλων μεταφορικών μέσων"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(40-1).getNameTranslationResourceKey(), "el"), "Νομικές και λογιστικές υπηρεσίες"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(41-1).getNameTranslationResourceKey(), "el"), "Υπηρεσίες γραφείων κηδειών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(42-1).getNameTranslationResourceKey(), "el"), "Παιδική μέριμνα"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(43-1).getNameTranslationResourceKey(), "el"), "Υπηρεσίες για ζώα συντροφιάς"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(44-1).getNameTranslationResourceKey(), "el"), "Χρηματοπιστωτικές υπηρεσίες"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(45-1).getNameTranslationResourceKey(), "el"), "Χρηματοπιστωτικές υπηρεσίες — Λογαριασμός πληρωμών και υπηρεσίες πληρωμών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(46-1).getNameTranslationResourceKey(), "el"), "Χρηματοοικονομικές υπηρεσίες — πιστώσεις"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(47-1).getNameTranslationResourceKey(), "el"), "Χρηματοοικονομικές υπηρεσίες — Υποθήκες/στεγαστικά δάνεια"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(48-1).getNameTranslationResourceKey(), "el"), "Χρηματοοικονομικές υπηρεσίες — Αποταμιεύσεις"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(49-1).getNameTranslationResourceKey(), "el"), "Χρηματοοικονομικές υπηρεσίες — Άλλο"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(50-1).getNameTranslationResourceKey(), "el"), "Επενδύσεις, συντάξεις και κινητές αξίες"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(51-1).getNameTranslationResourceKey(), "el"), "Ασφάλιση γενικών κλάδων — Κατοικία και ιδιοκτησία"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(52-1).getNameTranslationResourceKey(), "el"), "Ασφάλιση γενικών κλάδων — Μεταφορές"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(53-1).getNameTranslationResourceKey(), "el"), "Ασφάλιση γενικών κλάδων – Ταξίδια"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(54-1).getNameTranslationResourceKey(), "el"), "Ασφάλιση γενικών κλάδων — Υγεία, ατυχήματα και άλλα"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(55-1).getNameTranslationResourceKey(), "el"), "Ασφάλιση — Ζωή"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(56-1).getNameTranslationResourceKey(), "el"), "Ταχυδρομικές υπηρεσίες και ηλεκτρονικές επικοινωνίες"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(57-1).getNameTranslationResourceKey(), "el"), "Ταχυδρομικές υπηρεσίες και ταχυμεταφορές"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(58-1).getNameTranslationResourceKey(), "el"), "Υπηρεσίες σταθερής τηλεφωνίας"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(59-1).getNameTranslationResourceKey(), "el"), "Υπηρεσίες κινητής τηλεφωνίας"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(60-1).getNameTranslationResourceKey(), "el"), "Υπηρεσίες διαδικτύου"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(61-1).getNameTranslationResourceKey(), "el"), "Υπηρεσίες τηλεόρασης"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(62-1).getNameTranslationResourceKey(), "el"), "Άλλες υπηρεσίες επικοινωνίας"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(63-1).getNameTranslationResourceKey(), "el"), "Υπηρεσίες μεταφορών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(64-1).getNameTranslationResourceKey(), "el"), "Τραμ, λεωφορείο, μετρό και υπόγειος σιδηρόδρομος"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(65-1).getNameTranslationResourceKey(), "el"), "Σιδηροδρομικές μεταφορές"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(66-1).getNameTranslationResourceKey(), "el"), "Αερογραμμές"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(67-1).getNameTranslationResourceKey(), "el"), "Ταξί"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(68-1).getNameTranslationResourceKey(), "el"), "Θαλάσσιες, ποτάμιες και λοιπές μεταφορές μέσω πλωτών οδών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(69-1).getNameTranslationResourceKey(), "el"), "Υπηρεσίες μεταφορικών υποδομών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(70-1).getNameTranslationResourceKey(), "el"), "Υπηρεσίες μίσθωσης"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(71-1).getNameTranslationResourceKey(), "el"), "Υπηρεσίες αναψυχής"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(72-1).getNameTranslationResourceKey(), "el"), "Ξενοδοχεία και άλλα καταλύματα διακοπών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(73-1).getNameTranslationResourceKey(), "el"), "Οργανωμένα ταξίδια"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(74-1).getNameTranslationResourceKey(), "el"), "Υπηρεσίες ταξιδιωτικών πρακτορείων"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(75-1).getNameTranslationResourceKey(), "el"), "Χρονομεριστική μίσθωση και παρόμοια"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(76-1).getNameTranslationResourceKey(), "el"), "Εστιατόρια και μπαρ"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(77-1).getNameTranslationResourceKey(), "el"), "Υπηρεσίες που σχετίζονται με αθλήματα και χόμπι"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(78-1).getNameTranslationResourceKey(), "el"), "Πολιτιστικές και ψυχαγωγικές υπηρεσίες"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(79-1).getNameTranslationResourceKey(), "el"), "Τυχερά παιχνίδια, λαχειοφόρες αγορές"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(80-1).getNameTranslationResourceKey(), "el"), "Άλλες υπηρεσίες αναψυχής"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(81-1).getNameTranslationResourceKey(), "el"), "Ενέργεια και ύδρευση"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(82-1).getNameTranslationResourceKey(), "el"), "Ύδρευση"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(83-1).getNameTranslationResourceKey(), "el"), "Ηλεκτρικό ρεύμα"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(84-1).getNameTranslationResourceKey(), "el"), "Αέριο"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(85-1).getNameTranslationResourceKey(), "el"), "Άλλες πηγές ενέργειας"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(86-1).getNameTranslationResourceKey(), "el"), "Υγεία"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(87-1).getNameTranslationResourceKey(), "el"), "Συνταγογραφημένα φάρμακα"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(88-1).getNameTranslationResourceKey(), "el"), "Φάρμακα στην ελεύθερη αγορά"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(89-1).getNameTranslationResourceKey(), "el"), "Ιατροτεχνολογικά προϊόντα και άλλα φυσικά βοηθήματα που χρησιμοποιούνται από ασθενείς"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(90-1).getNameTranslationResourceKey(), "el"), "Υπηρεσίες υγείας"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(91-1).getNameTranslationResourceKey(), "el"), "Εγκαταστάσεις περίθαλψης και κατ’ οίκον περίθαλψη"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(92-1).getNameTranslationResourceKey(), "el"), "Εκπαίδευση"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(93-1).getNameTranslationResourceKey(), "el"), "Σχολεία"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(94-1).getNameTranslationResourceKey(), "el"), "Εκμάθηση γλωσσών, μαθήματα οδήγησης και άλλα ιδιαίτερα μαθήματα"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(95-1).getNameTranslationResourceKey(), "el"), "Άλλο"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues.get(96-1).getNameTranslationResourceKey(), "el"), "Άλλο (περιλαμβάνονται αγαθά και υπηρεσίες)"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(1-1).getNameTranslationResourceKey(), "el"), "Ποιότητα αγαθών και υπηρεσιών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(2-1).getNameTranslationResourceKey(), "el"), "Ελαττωματική, προκληθείσα ζημιά"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(3-1).getNameTranslationResourceKey(), "el"), "Μη συμμόρφωση με την παραγγελία"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(4-1).getNameTranslationResourceKey(), "el"), "Ακατάλληλο για συγκεκριμένη χρήση"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(5-1).getNameTranslationResourceKey(), "el"), "Παράδοση αγαθών/Παροχή υπηρεσιών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(6-1).getNameTranslationResourceKey(), "el"), "Μη παράδοση/μη παροχή"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(7-1).getNameTranslationResourceKey(), "el"), "Μερική παράδοση/μερική παροχή"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(8-1).getNameTranslationResourceKey(), "el"), "Καθυστέρηση"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(9-1).getNameTranslationResourceKey(), "el"), "Μη διαθεσιμότητα/Μη πρόσβαση"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(10-1).getNameTranslationResourceKey(), "el"), "Άρνηση πώλησης/παροχής ενός αγαθού ή μιας υπηρεσίας"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(11-1).getNameTranslationResourceKey(), "el"), "Αναστολή παράδοσης ενός αγαθού ή παροχής μιας υπηρεσίας χωρίς προηγούμενη ειδοποίηση"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(12-1).getNameTranslationResourceKey(), "el"), "Ωράριο λειτουργίας"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(13-1).getNameTranslationResourceKey(), "el"), "Εξυπηρέτηση πελατών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(14-1).getNameTranslationResourceKey(), "el"), "Εξυπηρέτηση μετά την πώληση/βοήθεια"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(15-1).getNameTranslationResourceKey(), "el"), "Άλλα θέματα που αφορούν την παροχή προϊόντων/υπηρεσιών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(16-1).getNameTranslationResourceKey(), "el"), "Τιμή/χρέωση"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(17-1).getNameTranslationResourceKey(), "el"), "Αλλαγή τιμής/χρέωσης"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(18-1).getNameTranslationResourceKey(), "el"), "Άνιση μεταχείριση όσον αφορά τις τιμές"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(19-1).getNameTranslationResourceKey(), "el"), "Διαφάνεια στη χρέωση (ασαφής, περίπλοκη)"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(20-1).getNameTranslationResourceKey(), "el"), "Άλλα ζητήματα που σχετίζονται με την τιμή/χρέωση"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(21-1).getNameTranslationResourceKey(), "el"), "Τιμολόγηση/χρέωση και είσπραξη χρεών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(22-1).getNameTranslationResourceKey(), "el"), "Εσφαλμένο τιμολόγιο/λογαριασμός"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(23-1).getNameTranslationResourceKey(), "el"), "Ασαφές τιμολόγιο/λογαριασμός"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(24-1).getNameTranslationResourceKey(), "el"), "Μη έκδοση τιμολογίου ή δύσκολη πρόσβαση σε τιμολόγιο/μηνιαία κατάσταση"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(25-1).getNameTranslationResourceKey(), "el"), "Αδικαιολόγητη τιμολόγηση/χρέωση"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(26-1).getNameTranslationResourceKey(), "el"), "Είσπραξη χρεών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(27-1).getNameTranslationResourceKey(), "el"), "Άλλα ζητήματα σχετικά με την τιμολόγηση/χρέωση και είσπραξη χρεών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(28-1).getNameTranslationResourceKey(), "el"), "Εγγύηση/νομική εγγύηση και εμπορικές εγγυήσεις"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(29-1).getNameTranslationResourceKey(), "el"), "Μη τήρηση εγγύησης/νομικής εγγύησης"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(30-1).getNameTranslationResourceKey(), "el"), "Μη τήρηση εμπορικής εγγύησης"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(31-1).getNameTranslationResourceKey(), "el"), "Έννομη προστασία"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(32-1).getNameTranslationResourceKey(), "el"), "Δύσκολη πρόσβαση στην έννομη προστασία"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(33-1).getNameTranslationResourceKey(), "el"), "Απουσία έννομης προστασίας"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(34-1).getNameTranslationResourceKey(), "el"), "Μερική ή εσφαλμένη έννομη προστασία"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(35-1).getNameTranslationResourceKey(), "el"), "Καθυστερημένη έννομη προστασία"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(36-1).getNameTranslationResourceKey(), "el"), "Άλλα ζητήματα που σχετίζονται με την έννομη προστασία"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(37-1).getNameTranslationResourceKey(), "el"), "Αθέμιτες εμπορικές πρακτικές"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(38-1).getNameTranslationResourceKey(), "el"), "Παραπλανητικοί συμβατικοί όροι και προϋποθέσεις"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(39-1).getNameTranslationResourceKey(), "el"), "Εσφαλμένη ή παραπλανητική ένδειξη τιμών/χρεώσεων και επισήμανση"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(40-1).getNameTranslationResourceKey(), "el"), "Παραπλανητική διαφήμιση"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(41-1).getNameTranslationResourceKey(), "el"), "Ανεπιθύμητες διαφημίσεις"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(42-1).getNameTranslationResourceKey(), "el"), "Ανεπιθύμητα αγαθά ή υπηρεσίες"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(43-1).getNameTranslationResourceKey(), "el"), "Επιθετικές πρακτικές πωλήσεων"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(44-1).getNameTranslationResourceKey(), "el"), "Πρακτικές εξαπάτησης"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(45-1).getNameTranslationResourceKey(), "el"), "Άλλες αθέμιτες εμπορικές πρακτικές"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(46-1).getNameTranslationResourceKey(), "el"), "Συμβάσεις και πωλήσεις"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(47-1).getNameTranslationResourceKey(), "el"), "Αθέμιτοι συμβατικοί όροι/μετατροπή συμβατικών όρων"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(48-1).getNameTranslationResourceKey(), "el"), "Έλλειψη πληροφοριών"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(49-1).getNameTranslationResourceKey(), "el"), "Επιβεβαίωση παραγγελίας (μη ληφθείσα/εσφαλμένη)"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(50-1).getNameTranslationResourceKey(), "el"), "Περίοδος υπαναχώρησης/Δικαίωμα υπαναχώρησης"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(51-1).getNameTranslationResourceKey(), "el"), "Πληρωμές (π.χ. προκαταβολές και δόσεις)"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(52-1).getNameTranslationResourceKey(), "el"), "Άρση της σύμβασης"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(53-1).getNameTranslationResourceKey(), "el"), "Ελάχιστη συμβατική περίοδος"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(54-1).getNameTranslationResourceKey(), "el"), "Άλλα θέματα που αφορούν συμβάσεις και πωλήσεις"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(55-1).getNameTranslationResourceKey(), "el"), "Αλλαγή παρόχου"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(56-1).getNameTranslationResourceKey(), "el"), "Αλλαγή παρόχου"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(57-1).getNameTranslationResourceKey(), "el"), "Ασφάλεια – καλύπτει τα αγαθά και τις υπηρεσίες"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(58-1).getNameTranslationResourceKey(), "el"), "Ασφάλεια των προϊόντων – καλύπτει τα αγαθά και τις υπηρεσίες"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(59-1).getNameTranslationResourceKey(), "el"), "Συσκευασία, επισήμανση και οδηγίες – καλύπτει τα αγαθά και τις υπηρεσίες"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(60-1).getNameTranslationResourceKey(), "el"), "Προστασία της ιδιωτικής ζωής και προστασία των δεδομένων"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(61-1).getNameTranslationResourceKey(), "el"), "Προστασία των δεδομένων"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(62-1).getNameTranslationResourceKey(), "el"), "Ιδιωτική ζωή"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(63-1).getNameTranslationResourceKey(), "el"), "Άλλα θέματα που αφορούν την προστασία της ιδιωτικής ζωής και των δεδομένων προσωπικού χαρακτήρα"));
		this.saveOrUpdate(new I18nStringResource(new I18nStringIdentifier(lookupValues2.get(64-1).getNameTranslationResourceKey(), "el"), "Άλλα θέματα"));
		
	}

	/**
	 * 
	 */
	private void initCountries() {
		// Setup countries
		logger.info("inserting countries into database");
		this.storeCountry(new Country("Andorra","ANDORRA","AD","en"));
		this.storeCountry(new Country("United Arab Emirates","UNITED ARAB EMIRATES","AE","en"));
		this.storeCountry(new Country("Afghanistan","AFGHANISTAN","AF","en"));
		this.storeCountry(new Country("Antigua and Barbuda","ANTIGUA AND BARBUDA","AG","en"));
		this.storeCountry(new Country("Anguilla","ANGUILLA","AI","en"));
		this.storeCountry(new Country("Albania","ALBANIA","AL","en"));
		this.storeCountry(new Country("Armenia","ARMENIA","AM","en"));
		this.storeCountry(new Country("Netherlands Antilles","NETHERLANDS ANTILLES","AN","en"));
		this.storeCountry(new Country("Angola","ANGOLA","AO","en"));
		this.storeCountry(new Country("Antarctica","ANTARCTICA","AQ","en"));
		this.storeCountry(new Country("Argentina","ARGENTINA","AR","en"));
		this.storeCountry(new Country("American Samoa","AMERICAN SAMOA","AS","en"));
		this.storeCountry(new Country("Austria","AUSTRIA","AT","en"));
		this.storeCountry(new Country("Australia","AUSTRALIA","AU","en"));
		this.storeCountry(new Country("Aruba","ARUBA","AW","en"));
		this.storeCountry(new Country("Åland Islands","ÅLAND ISLANDS","AX","en"));
		this.storeCountry(new Country("Azerbaijan","AZERBAIJAN","AZ","en"));
		this.storeCountry(new Country("Bosnia and Herzegovina","BOSNIA AND HERZEGOVINA","BA","en"));
		this.storeCountry(new Country("Barbados","BARBADOS","BB","en"));
		this.storeCountry(new Country("Bangladesh","BANGLADESH","BD","en"));
		this.storeCountry(new Country("Belgium","BELGIUM","BE","en"));
		this.storeCountry(new Country("Burkina Faso","BURKINA FASO","BF","en"));
		this.storeCountry(new Country("Bulgaria","BULGARIA","BG","en"));
		this.storeCountry(new Country("Bahrain","BAHRAIN","BH","en"));
		this.storeCountry(new Country("Burundi","BURUNDI","BI","en"));
		this.storeCountry(new Country("Benin","BENIN","BJ","en"));
		this.storeCountry(new Country("Saint Barthélemy","SAINT BARTHÉLEMY","BL","en"));
		this.storeCountry(new Country("Bermuda","BERMUDA","BM","en"));
		this.storeCountry(new Country("Brunei Darussalam","BRUNEI DARUSSALAM","BN","en"));
		this.storeCountry(new Country("Bolivia","BOLIVIA, PLURINATIONAL STATE OF","BO","en"));
		this.storeCountry(new Country("Brazil","BRAZIL","BR","en"));
		this.storeCountry(new Country("Bahamas","BAHAMAS","BS","en"));
		this.storeCountry(new Country("Bhutan","BHUTAN","BT","en"));
		this.storeCountry(new Country("Bouvet Island","BOUVET ISLAND","BV","en"));
		this.storeCountry(new Country("Botswana","BOTSWANA","BW","en"));
		this.storeCountry(new Country("Belarus","BELARUS","BY","en"));
		this.storeCountry(new Country("Belize","BELIZE","BZ","en"));
		this.storeCountry(new Country("Canada","CANADA","CA","en"));
		this.storeCountry(new Country("Cocos (Keeling) Islands","COCOS (KEELING) ISLANDS","CC","en"));
		this.storeCountry(new Country("Congo, the Democratic Republic of the","CONGO, THE DEMOCRATIC REPUBLIC OF THE","CD","en"));
		this.storeCountry(new Country("Central African Republic","CENTRAL AFRICAN REPUBLIC","CF","en"));
		this.storeCountry(new Country("Congo","CONGO","CG","en"));
		this.storeCountry(new Country("Switzerland","SWITZERLAND","CH","en"));
		this.storeCountry(new Country("Côte d'Ivoire","CÔTE D'IVOIRE","CI","en"));
		this.storeCountry(new Country("Cook Islands","COOK ISLANDS","CK","en"));
		this.storeCountry(new Country("Chile","CHILE","CL","en"));
		this.storeCountry(new Country("Cameroon","CAMEROON","CM","en"));
		this.storeCountry(new Country("China","CHINA","CN","en"));
		this.storeCountry(new Country("Colombia","COLOMBIA","CO","en"));
		this.storeCountry(new Country("Costa Rica","COSTA RICA","CR","en"));
		this.storeCountry(new Country("Cuba","CUBA","CU","en"));
		this.storeCountry(new Country("Cape Verde","CAPE VERDE","CV","en"));
		this.storeCountry(new Country("Christmas Island","CHRISTMAS ISLAND","CX","en"));
		this.storeCountry(new Country("Cyprus","CYPRUS","CY","en"));
		this.storeCountry(new Country("Czech Republic","CZECH REPUBLIC","CZ","en"));
		this.storeCountry(new Country("Germany","GERMANY","DE","en"));
		this.storeCountry(new Country("Djibouti","DJIBOUTI","DJ","en"));
		this.storeCountry(new Country("Denmark","DENMARK","DK","en"));
		this.storeCountry(new Country("Dominica","DOMINICA","DM","en"));
		this.storeCountry(new Country("Dominican Republic","DOMINICAN REPUBLIC","DO","en"));
		this.storeCountry(new Country("Algeria","ALGERIA","DZ","en"));
		this.storeCountry(new Country("Ecuador","ECUADOR","EC","en"));
		this.storeCountry(new Country("Estonia","ESTONIA","EE","en"));
		this.storeCountry(new Country("Egypt","EGYPT","EG","en"));
		this.storeCountry(new Country("Western Sahara","WESTERN SAHARA","EH","en"));
		this.storeCountry(new Country("Eritrea","ERITREA","ER","en"));
		this.storeCountry(new Country("Spain","SPAIN","ES","en"));
		this.storeCountry(new Country("Ethiopia","ETHIOPIA","ET","en"));
		this.storeCountry(new Country("Finland","FINLAND","FI","en"));
		this.storeCountry(new Country("Fiji","FIJI","FJ","en"));
		this.storeCountry(new Country("Falkland Islands (Malvinas)","FALKLAND ISLANDS (MALVINAS)","FK","en"));
		this.storeCountry(new Country("Micronesia, Federated States of","MICRONESIA, FEDERATED STATES OF","FM","en"));
		this.storeCountry(new Country("Faroe Islands","FAROE ISLANDS","FO","en"));
		this.storeCountry(new Country("France","FRANCE","FR","en"));
		this.storeCountry(new Country("Gabon","GABON","GA","en"));
		this.storeCountry(new Country("United Kingdom","UNITED KINGDOM","GB","en"));
		this.storeCountry(new Country("Grenada","GRENADA","GD","en"));
		this.storeCountry(new Country("Georgia","GEORGIA","GE","en"));
		this.storeCountry(new Country("French Guiana","FRENCH GUIANA","GF","en"));
		this.storeCountry(new Country("Guernsey","GUERNSEY","GG","en"));
		this.storeCountry(new Country("Ghana","GHANA","GH","en"));
		this.storeCountry(new Country("Gibraltar","GIBRALTAR","GI","en"));
		this.storeCountry(new Country("Greenland","GREENLAND","GL","en"));
		this.storeCountry(new Country("Gambia","GAMBIA","GM","en"));
		this.storeCountry(new Country("Guinea","GUINEA","GN","en"));
		this.storeCountry(new Country("Guadeloupe","GUADELOUPE","GP","en"));
		this.storeCountry(new Country("Equatorial Guinea","EQUATORIAL GUINEA","GQ","en"));
		this.storeCountry(new Country("Greece","GREECE","GR", "el", "30"));
		this.storeCountry(new Country("South Georgia and the South Sandwich Islands","SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS","GS","en"));
		this.storeCountry(new Country("Guatemala","GUATEMALA","GT","en"));
		this.storeCountry(new Country("Guam","GUAM","GU","en"));
		this.storeCountry(new Country("Guinea-Bissau","GUINEA-BISSAU","GW","en"));
		this.storeCountry(new Country("Guyana","GUYANA","GY","en"));
		this.storeCountry(new Country("Hong Kong","HONG KONG","HK","en"));
		this.storeCountry(new Country("Heard Island and McDonald Islands","HEARD ISLAND AND MCDONALD ISLANDS","HM","en"));
		this.storeCountry(new Country("Honduras","HONDURAS","HN","en"));
		this.storeCountry(new Country("Croatia","CROATIA","HR","en"));
		this.storeCountry(new Country("Haiti","HAITI","HT","en"));
		this.storeCountry(new Country("Hungary","HUNGARY","HU","en"));
		this.storeCountry(new Country("Indonesia","INDONESIA","ID","en"));
		this.storeCountry(new Country("Ireland","IRELAND","IE","en"));
		this.storeCountry(new Country("Israel","ISRAEL","IL","en"));
		this.storeCountry(new Country("Isle of Man","ISLE OF MAN","IM","en"));
		this.storeCountry(new Country("India","INDIA","IN","en"));
		this.storeCountry(new Country("British Indian Ocean Territory","BRITISH INDIAN OCEAN TERRITORY","IO","en"));
		this.storeCountry(new Country("Iraq","IRAQ","IQ","en"));
		this.storeCountry(new Country("Iran, Islamic Republic of","IRAN, ISLAMIC REPUBLIC OF","IR","en"));
		this.storeCountry(new Country("Iceland","ICELAND","IS","en"));
		this.storeCountry(new Country("Italy","ITALY","IT","en"));
		this.storeCountry(new Country("Jersey","JERSEY","JE","en"));
		this.storeCountry(new Country("Jamaica","JAMAICA","JM","en"));
		this.storeCountry(new Country("Jordan","JORDAN","JO","en"));
		this.storeCountry(new Country("Japan","JAPAN","JP","en"));
		this.storeCountry(new Country("Kenya","KENYA","KE","en"));
		this.storeCountry(new Country("Kyrgyzstan","KYRGYZSTAN","KG","en"));
		this.storeCountry(new Country("Cambodia","CAMBODIA","KH","en"));
		this.storeCountry(new Country("Kiribati","KIRIBATI","KI","en"));
		this.storeCountry(new Country("Comoros","COMOROS","KM","en"));
		this.storeCountry(new Country("Saint Kitts and Nevis","SAINT KITTS AND NEVIS","KN","en"));
		this.storeCountry(new Country("Korea, Democratic People's Republic of","KOREA, DEMOCRATIC PEOPLE'S REPUBLIC OF","KP","en"));
		this.storeCountry(new Country("Korea, Republic of","KOREA, REPUBLIC OF","KR","en"));
		this.storeCountry(new Country("Kuwait","KUWAIT","KW","en"));
		this.storeCountry(new Country("Cayman Islands","CAYMAN ISLANDS","KY","en"));
		this.storeCountry(new Country("Kazakhstan","KAZAKHSTAN","KZ","en"));
		this.storeCountry(new Country("Lao People's Democratic Republic","LAO PEOPLE'S DEMOCRATIC REPUBLIC","LA","en"));
		this.storeCountry(new Country("Lebanon","LEBANON","LB","en"));
		this.storeCountry(new Country("Saint Lucia","SAINT LUCIA","LC","en"));
		this.storeCountry(new Country("Liechtenstein","LIECHTENSTEIN","LI","en"));
		this.storeCountry(new Country("Sri Lanka","SRI LANKA","LK","en"));
		this.storeCountry(new Country("Liberia","LIBERIA","LR","en"));
		this.storeCountry(new Country("Lesotho","LESOTHO","LS","en"));
		this.storeCountry(new Country("Lithuania","LITHUANIA","LT","en"));
		this.storeCountry(new Country("Luxembourg","LUXEMBOURG","LU","en"));
		this.storeCountry(new Country("Latvia","LATVIA","LV","en"));
		this.storeCountry(new Country("Libyan Arab Jamahiriya","LIBYAN ARAB JAMAHIRIYA","LY","en"));
		this.storeCountry(new Country("Morocco","MOROCCO","MA","en"));
		this.storeCountry(new Country("Monaco","MONACO","MC","en"));
		this.storeCountry(new Country("Moldova","MOLDOVA, REPUBLIC OF","MD","en"));
		this.storeCountry(new Country("Montenegro","MONTENEGRO","ME","en"));
		this.storeCountry(new Country("Saint Martin (French part)","SAINT MARTIN","MF","en"));
		this.storeCountry(new Country("Madagascar","MADAGASCAR","MG","en"));
		this.storeCountry(new Country("Marshall Islands","MARSHALL ISLANDS","MH","en"));
		this.storeCountry(new Country("Macedonia, the former Yugoslav Republic of","MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF","MK","en"));
		this.storeCountry(new Country("Mali","MALI","ML","en"));
		this.storeCountry(new Country("Myanmar","MYANMAR","MM","en"));
		this.storeCountry(new Country("Mongolia","MONGOLIA","MN","en"));
		this.storeCountry(new Country("Macao","MACAO","MO","en"));
		this.storeCountry(new Country("Northern Mariana Islands","NORTHERN MARIANA ISLANDS","MP","en"));
		this.storeCountry(new Country("Martinique","MARTINIQUE","MQ","en"));
		this.storeCountry(new Country("Mauritania","MAURITANIA","MR","en"));
		this.storeCountry(new Country("Montserrat","MONTSERRAT","MS","en"));
		this.storeCountry(new Country("Malta","MALTA","MT","en"));
		this.storeCountry(new Country("Mauritius","MAURITIUS","MU","en"));
		this.storeCountry(new Country("Maldives","MALDIVES","MV","en"));
		this.storeCountry(new Country("Malawi","MALAWI","MW","en"));
		this.storeCountry(new Country("Mexico","MEXICO","MX","en"));
		this.storeCountry(new Country("Malaysia","MALAYSIA","MY","en"));
		this.storeCountry(new Country("Mozambique","MOZAMBIQUE","MZ","en"));
		this.storeCountry(new Country("Namibia","NAMIBIA","NA","en"));
		this.storeCountry(new Country("New Caledonia","NEW CALEDONIA","NC","en"));
		this.storeCountry(new Country("Niger","NIGER","NE","en"));
		this.storeCountry(new Country("Norfolk Island","NORFOLK ISLAND","NF","en"));
		this.storeCountry(new Country("Nigeria","NIGERIA","NG","en"));
		this.storeCountry(new Country("Nicaragua","NICARAGUA","NI","en"));
		this.storeCountry(new Country("Netherlands","NETHERLANDS","NL","en"));
		this.storeCountry(new Country("Norway","NORWAY","NO","en"));
		this.storeCountry(new Country("Nepal","NEPAL","NP","en"));
		this.storeCountry(new Country("Nauru","NAURU","NR","en"));
		this.storeCountry(new Country("Niue","NIUE","NU","en"));
		this.storeCountry(new Country("New Zealand","NEW ZEALAND","NZ","en"));
		this.storeCountry(new Country("Oman","OMAN","OM","en"));
		this.storeCountry(new Country("Panama","PANAMA","PA","en"));
		this.storeCountry(new Country("Peru","PERU","PE","en"));
		this.storeCountry(new Country("French Polynesia","FRENCH POLYNESIA","PF","en"));
		this.storeCountry(new Country("Papua New Guinea","PAPUA NEW GUINEA","PG","en"));
		this.storeCountry(new Country("Philippines","PHILIPPINES","PH","en"));
		this.storeCountry(new Country("Pakistan","PAKISTAN","PK","en"));
		this.storeCountry(new Country("Poland","POLAND","PL","en"));
		this.storeCountry(new Country("Saint Pierre and Miquelon","SAINT PIERRE AND MIQUELON","PM","en"));
		this.storeCountry(new Country("Pitcairn","PITCAIRN","PN","en"));
		this.storeCountry(new Country("Puerto Rico","PUERTO RICO","PR","en"));
		this.storeCountry(new Country("Palestinian Territory, Occupied","PALESTINIAN TERRITORY, OCCUPIED","PS","en"));
		this.storeCountry(new Country("Portugal","PORTUGAL","PT","en"));
		this.storeCountry(new Country("Palau","PALAU","PW","en"));
		this.storeCountry(new Country("Paraguay","PARAGUAY","PY","en"));
		this.storeCountry(new Country("Qatar","QATAR","QA","en"));
		this.storeCountry(new Country("Réunion","RÉUNION","RE","en"));
		this.storeCountry(new Country("Romania","ROMANIA","RO","en"));
		this.storeCountry(new Country("Serbia","SERBIA","RS","en"));
		this.storeCountry(new Country("Russian Federation","RUSSIAN FEDERATION","RU","en"));
		this.storeCountry(new Country("Rwanda","RWANDA","RW","en"));
		this.storeCountry(new Country("Saudi Arabia","SAUDI ARABIA","SA","en"));
		this.storeCountry(new Country("Solomon Islands","SOLOMON ISLANDS","SB","en"));
		this.storeCountry(new Country("Seychelles","SEYCHELLES","SC","en"));
		this.storeCountry(new Country("Sudan","SUDAN","SD","en"));
		this.storeCountry(new Country("Sweden","SWEDEN","SE","en"));
		this.storeCountry(new Country("Singapore","SINGAPORE","SG","en"));
		this.storeCountry(new Country("Saint Helena","SAINT HELENA, ASCENSION AND TRISTAN DA CUNHA","SH","en"));
		this.storeCountry(new Country("Slovenia","SLOVENIA","SI","en"));
		this.storeCountry(new Country("Svalbard and Jan Mayen","SVALBARD AND JAN MAYEN","SJ","en"));
		this.storeCountry(new Country("Slovakia","SLOVAKIA","SK","en"));
		this.storeCountry(new Country("Sierra Leone","SIERRA LEONE","SL","en"));
		this.storeCountry(new Country("San Marino","SAN MARINO","SM","en"));
		this.storeCountry(new Country("Senegal","SENEGAL","SN","en"));
		this.storeCountry(new Country("Somalia","SOMALIA","SO","en"));
		this.storeCountry(new Country("Suriname","SURINAME","SR","en"));
		this.storeCountry(new Country("Sao Tome and Principe","SAO TOME AND PRINCIPE","ST","en"));
		this.storeCountry(new Country("El Salvador","EL SALVADOR","SV","en"));
		this.storeCountry(new Country("Syrian Arab Republic","SYRIAN ARAB REPUBLIC","SY","en"));
		this.storeCountry(new Country("Swaziland","SWAZILAND","SZ","en"));
		this.storeCountry(new Country("Turks and Caicos Islands","TURKS AND CAICOS ISLANDS","TC","en"));
		this.storeCountry(new Country("Chad","CHAD","TD","en"));
		this.storeCountry(new Country("French Southern Territories","FRENCH SOUTHERN TERRITORIES","TF","en"));
		this.storeCountry(new Country("Togo","TOGO","TG","en"));
		this.storeCountry(new Country("Thailand","THAILAND","TH","en"));
		this.storeCountry(new Country("Tajikistan","TAJIKISTAN","TJ","en"));
		this.storeCountry(new Country("Tokelau","TOKELAU","TK","en"));
		this.storeCountry(new Country("Timor-Leste","TIMOR-LESTE","TL","en"));
		this.storeCountry(new Country("Turkmenistan","TURKMENISTAN","TM","en"));
		this.storeCountry(new Country("Tunisia","TUNISIA","TN","en"));
		this.storeCountry(new Country("Tonga","TONGA","TO","en"));
		this.storeCountry(new Country("Turkey","TURKEY","TR","en"));
		this.storeCountry(new Country("Trinidad and Tobago","TRINIDAD AND TOBAGO","TT","en"));
		this.storeCountry(new Country("Tuvalu","TUVALU","TV","en"));
		this.storeCountry(new Country("Taiwan, Province of China","TAIWAN, PROVINCE OF CHINA","TW","en"));
		this.storeCountry(new Country("Tanzania, United Republic of","TANZANIA, UNITED REPUBLIC OF","TZ","en"));
		this.storeCountry(new Country("Ukraine","UKRAINE","UA","en"));
		this.storeCountry(new Country("Uganda","UGANDA","UG","en"));
		this.storeCountry(new Country("United States Minor Outlying Islands","UNITED STATES MINOR OUTLYING ISLANDS","UM","en"));
		this.storeCountry(new Country("United States","UNITED STATES","US","en"));
		this.storeCountry(new Country("Uruguay","URUGUAY","UY","en"));
		this.storeCountry(new Country("Uzbekistan","UZBEKISTAN","UZ","en"));
		this.storeCountry(new Country("Holy See (Vatican City State)","HOLY SEE (VATICAN CITY STATE)","VA","en"));
		this.storeCountry(new Country("Saint Vincent and the Grenadines","SAINT VINCENT AND THE GRENADINES","VC","en"));
		this.storeCountry(new Country("Venezuela","VENEZUELA, BOLIVARIAN REPUBLIC OF","VE","en"));
		this.storeCountry(new Country("Virgin Islands, British","VIRGIN ISLANDS, BRITISH","VG","en"));
		this.storeCountry(new Country("Virgin Islands, U.S.","VIRGIN ISLANDS, U.S.","VI","en"));
		this.storeCountry(new Country("Viet Nam","VIET NAM","VN","en"));
		this.storeCountry(new Country("Vanuatu","VANUATU","VU","en"));
		this.storeCountry(new Country("Wallis and Futuna","WALLIS AND FUTUNA","WF","en"));
		this.storeCountry(new Country("Samoa","SAMOA","WS","en"));
		this.storeCountry(new Country("Yemen","YEMEN","YE","en"));
		this.storeCountry(new Country("Mayotte","MAYOTTE","YT","en"));
		this.storeCountry(new Country("South Africa","SOUTH AFRICA","ZA","en"));
		this.storeCountry(new Country("Zambia","ZAMBIA","ZM","en"));
		this.storeCountry(new Country("Zimbabwe","ZIMBABWE","ZW","en"));
	}

	/**
	 * @see gr.abiss.calipso.CalipsoDao#updateItem(gr.abiss.calipso.domain.Item, gr.abiss.calipso.domain.User, boolean)
	 */
	@Override
	public void updateItem(Item item, User user, boolean updateHistory) {
		// TODO Auto-generated method stub
		storeItem(item);
	}

	/**
	 * @see gr.abiss.calipso.CalipsoDao#loadI18nStringResource(gr.abiss.calipso.domain.I18nStringIdentifier)
	 */
	@Override
	public I18nStringResource loadI18nStringResource(I18nStringIdentifier id) {
		//logger.info("Looking for resource: "+id);
		return (I18nStringResource) getHibernateTemplate().get(I18nStringResource.class, id);
	}

	/** {@inheritDoc} */
	@Override
	public Metadata getCachedMetadataForSpace(Space space){
		//logger.info("getCachedMetadataForSpace: "+space.getPrefixCode());
		
		Metadata metadata = metadataCache.get(new Long(space.getId()));
		if(metadata == null){
			metadata = this.getMetadataForSpace(space);
        	//logger.info("getCachedMetadataForSpace: cache does not contain metadata for space: "+space.getPrefixCode());
			metadataCache.put(new Long(space.getId()), metadata);
        	//logger.info("getCachedMetadataForSpace: added metadata to cache for space: "+space.getPrefixCode());
		}
		return metadata;
	}
	

	/** {@inheritDoc} */
	@Override
	public Metadata getMetadataForSpace(Space space) {
		Metadata metadata = null;
		@SuppressWarnings("unchecked")
		List<Metadata> list = getHibernateTemplate()
			.find("select space.metadata from Space space where space.id = ?", space.getId());
        if(!list.isEmpty()){
        	metadata = list.get(0);
        }
        //logger.info("getMetadataForSpace: loaded persisted metadata for space: "+space.getPrefixCode()+", metadata ID: "+metadata.getId());
        return metadata;
	}
	
}
