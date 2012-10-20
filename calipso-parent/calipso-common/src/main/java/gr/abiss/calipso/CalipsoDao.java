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

package gr.abiss.calipso;

import gr.abiss.calipso.domain.AbstractItem;
import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.domain.AssetTypeCustomAttributeSearch;
import gr.abiss.calipso.domain.AssetTypeSearch;
import gr.abiss.calipso.domain.Attachment;
import gr.abiss.calipso.domain.Config;
import gr.abiss.calipso.domain.Country;
import gr.abiss.calipso.domain.Counts;
import gr.abiss.calipso.domain.CountsHolder;
import gr.abiss.calipso.domain.CustomAttribute;
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
import gr.abiss.calipso.domain.SavedSearch;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.SpaceGroup;
import gr.abiss.calipso.domain.SpaceRole;
import gr.abiss.calipso.domain.SpaceSequence;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.UserSpaceRole;
import gr.abiss.calipso.domain.ValidationExpression;
import gr.abiss.calipso.domain.i18n.I18nResourceTranslatable;
import gr.abiss.calipso.dto.AssetSearch;
import gr.abiss.calipso.wicket.regexp.ValidationExpressionSearch;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import java.util.List;

/**
 * CalipsoService DAO Interface
 * all database access operations
 */
public interface CalipsoDao {
	public Object get(Class entityClass, Serializable id);
	Serializable save(Serializable o);
	void saveOrUpdate(Serializable o);
	void update(Serializable o);
	void saveOrUpdateAll(Collection all);
	void merge(Serializable o);
    void storeItem(Item item);
    void updateItem(Item item, User user, boolean updateHistory);
	Set<SpaceGroup> getSpaceGroupsForUser(Serializable userId);
    Item loadItem(long id);
    History loadHistory(long id);    
    void storeHistory(History history);
    List<Item> findItems(long sequenceNum, String prefixCode);
    List<Item> findItems(ItemSearch itemSearch);
    List<AbstractItem> findAllItems();
    public List<History> findHistoryForItem(Item item);
    void removeItem(Item item);
    public List<Item> findUnassignedItemsForSpace(Space space);
    public int loadCountUnassignedItemsForSpace(Space space);
    void removeItemItem(ItemItem itemItem);
    //===========================================
    int loadCountOfRecordsHavingFieldNotNull(Space space, Field field);
    int bulkUpdateFieldToNull(Space space, Field field);
    int loadCountOfRecordsHavingFieldWithValue(Space space, Field field, int optionKey);
    int bulkUpdateFieldToNullForValue(Space space, Field field, int optionKey);
    int loadCountOfRecordsHavingStatus(Space space, int status);
    int bulkUpdateStatusToOpen(Space space, int status);
    int bulkUpdateRenameSpaceRole(Space space, String oldRoleKey, String newRoleKey);
//    int bulkUpdateDeleteSpaceRole(Space space, String roleKey);
    int bulkUpdateDeleteItemsForSpace(Space space);    
    //========================================================   
    public List<Attachment> findTemporaryAttachments();
    public void removeAttachment(Attachment attachment);
    void storeAttachment(Attachment attachment);
    //===========================================
    //void storeMetadata(Metadata metadata);
    //Metadata loadMetadata(long id);
    //===========================================
    Space storeSpace(Space space);
    Space loadSpace(long id);
	Space loadSpace(SpaceRole spaceRole);
    List<Space> findSpacesByPrefixCode(String prefixCode);
    List<Space> findAllSpaces();
    List<Space> findSpacesWhereIdIn(List<Long> ids);
    List<Space> findSpacesWhereAnonymousAllowed();
    List<Space> findSpacesWhereGuestAllowed();
    void removeSpace(Space space);
    //=========================================== 
    SpaceSequence loadSpaceSequence(long id);
    void storeSpaceSequence(SpaceSequence spaceSequence);
    //===========================================
    void storeUser(User user);
    User loadUser(long id);
    void removeUser(User user);
    List<User> findAllUsers();
    List<Organization> findAllOrganizations();
    List<User> findUsersWhereIdIn(List<Long> ids);
    List<User> findUsersMatching(String searchText, String searchOn, Space space);
    List<User> findUsersMatching(String searchText, String searchOn);
    List<User> findUsersByLoginName(String loginName);
    List<User> findUsersByEmail(String email);
    List<User> findUsersForSpace(long spaceId);
    List<User> findUsersInOrganizations(List<Organization> orgs);
    List<UserSpaceRole> findUserRolesForSpace(long spaceId);
//    List<User> findUsersWithRoleForSpace(long spaceId, String roleKey);
    List<User> findUsersWithRoleForSpace(SpaceRole spaceRole);
    List<User> findUsersForSpaceSet(Collection<Space> spaces);
    int loadCountOfHistoryInvolvingUser(User user);
    //===========================================
    UserSpaceRole loadUserSpaceRole(long id);
    void removeUserSpaceRole(UserSpaceRole userSpaceRole);
    public int bulkUpdateDeleteUserSpaceRolesForSpace(Space space);
    //===========================================
    CountsHolder loadCountsForUser(User user);
    Counts loadCountsForUserSpace(User user, Space space);
    //===========================================
    List<Config> findAllConfig();
    void storeConfig(Config config);
    Config loadConfig(String key);
    // Saved Search============================
    public void storeSavedSearch(SavedSearch savedSearch);
    public SavedSearch loadSavedSearch(long id); 
    public void removeSavedSearch(SavedSearch savedSearch);
    public List<SavedSearch> findVisibleSearches(User user);
    public List<SavedSearch> findSavedSearches(User user);  
    public List<SavedSearch> findSavedSearches(User user, Space space);
    
    // Asset Management ============================
    public void storeCustomAttribute(CustomAttribute assetTypeCustomAttribute);
    public List<AssetTypeCustomAttribute> findAllCustomAttributes();
    public AssetTypeCustomAttribute loadAssetTypeCustomAttribute(long id);
    
    public CustomAttributeLookupValue loadCustomAttributeLookupValue(long id);
    public void preloadCustomAttributeEntityValuesForAsset(Asset asset);
	public CustomAttributeLookupValue loadCustomAttributeLookupValue(
			CustomAttribute attr, String name);
    public void removeLookupValue(CustomAttributeLookupValue lookupValue);
    public List<AssetTypeCustomAttribute> findCustomAttributesMatching(final AssetTypeCustomAttributeSearch searchCustomAttribute);
    public void storeLookupValue(CustomAttributeLookupValue lookupValue);
    public List<AssetType> findAllAssetTypesByCustomAttribute(AssetTypeCustomAttribute attribute);
    public int loadCountAssetsForCustomAttribute(AssetTypeCustomAttribute customAttribute);
    public int loadCountForAssetTypeAndCustomAttribute(AssetType assetType, CustomAttribute customAttribute);
    public int loadCountForCustomAttributeLookupValue(CustomAttributeLookupValue lookupValue);
    public void removeCustomAttribute(CustomAttribute customAttribute);

    public List<AssetType> findAllAssetTypes();
    public List<AssetType> findAllAssetTypesForSpace(Space space);
    public List<AssetType> findAssetTypesWhereIdIn(List<Long> ids);
    public List<AssetType> findAssetTypesMatching(AssetTypeSearch assetTypeSearch);
    public void storeAssetType(AssetType assetType);
    public List<AssetTypeCustomAttribute> findAllAssetTypeCustomAttributesByAssetType(AssetType assetType);
    public AssetType loadAssetType(long id);
    public AssetType loadAssetTypeByName(String name);
    public List<CustomAttributeLookupValue> findLookupValuesByCustomAttribute(CustomAttribute attr);
    public List<CustomAttributeLookupValue> findLookupValuesByCustomAttribute(CustomAttribute attr, int level);
    
    public void storeAsset(Asset asset);
    public Asset loadAsset(Long id);
    public Asset loadAssetWithAttributes(Long id);
    public List<Asset> findAssetsMatching(AssetSearch assetSearch, final boolean fetchCustomAttributes);
    public List<Object> findCustomAttributeValueMatching(AssetSearch assetSearch);
    public List<Asset> findAllAssetsByItem(Item item);
    public List<Asset> findAllAssetsBySpace(Space space);

    // Fields ================================================================
    public List<RoleSpaceStdField> findSpaceFieldsBySpaceRole(SpaceRole spaceRole);
    public List<RoleSpaceStdField> findSpaceFieldsBySpaceandRoleType(SpaceRole spaceRole);
    public List<RoleSpaceStdField> findSpaceFieldsBySpace(Space space);
    public RoleSpaceStdField loadRoleSpaceField(long id);
    public void storeRoleSpaceStdField(RoleSpaceStdField roleSpaceStdField);
//    public List<RoleSpaceStdField>findSpaceFieldsForUser(User user);
    public void removeRoleSpaceStdField(RoleSpaceStdField roleSpaceStdField);
    public int bulkUpdateDeleteRoleSpaceStdFieldsForSpaceRole(SpaceRole spaceRole);
    // Country ----------------------------------------------------------------------------------
    public void storeCountry(Country country);
    public Country loadCountry(String id);
    public List<Country> findAllCountries();
    // Validation Expression --------------------------------------------------------------------
    public void storeValidationExpression(ValidationExpression validationExpression);
    public ValidationExpression loadValidationExpression(long id);
    public List<ValidationExpression> findAllValidationExpressions();
    public List<ValidationExpression> findValidationExpressionsMatching(ValidationExpressionSearch validationExpression);
    public ValidationExpression findValidationExpressionByName(String name);
    
    // Organization ------------------------------------------------------------------------------
    public void storeOrganization(Organization organization);
    public Organization loadOrganization(long id);
    public List<Organization> findOrganizationsMatching(OrganizationSearch organizationSearch);
    
    // Space Roles --------------------------------------------------------------------------------
    public void storeSpaceRole(SpaceRole spaceRole);
    public SpaceRole loadSpaceRole(long spaceRoleId);
    public List<SpaceRole> findSpaceRolesForSpace(Space space);
    public void removeSpaceRole(SpaceRole spaceRole);
    public int bulkUpdateDeleteSpaceRolesForSpace(Space space);
    public List<SpaceRole> findSpaceRolesForSpaceAndRoleType(Space space, int roleTypeId);
    
    // Inforama Integration -----------------------------------------------------------------------
    //PageDictionary
    public PageDictionary loadPageDictionary(int id);
    public PageDictionary loadPageDictionary(String className);
    public List<PageDictionary> findPageDictionaryMatching(PageDictionarySearch pageDictionarySearch);
    public void storePageDictionary(PageDictionary pageDictionary);
    public void removePageDictionary(PageDictionary pageDictionary);
    
    //InforamaDocument
    public InforamaDocument loadInforamaDocument(int id);
    public void storeInforamaDocument(InforamaDocument inforamaDocument);
    public List<InforamaDocument> findInforamaDocumentMatching(InforamaDocumentSearch inforamaDocumentSearch);
    public List<InforamaDocument> findInforamaDocumentsForClassNameAndSpace(String className, Space space);
    public void removeInforamaDocument(InforamaDocument inforamaDocument);
    public List<InforamaDocument> findAllInforamaDocuments();
    
    //InforamaDocumentParameter
    public InforamaDocumentParameter loadInforamaDocumentParameter(int id);
    public void storeInforamaDocumentParameter(InforamaDocumentParameter inforamaDocumentParameter);
    public List<InforamaDocumentParameter> findInforamaDocumentParametersForDocument(InforamaDocument inforamaDocument);
    public List<InforamaDocumentParameter> findInforamaDocumentParameterMatching(InforamaDocumentParameterSearch inforamaDocumentParameterSearch);
    public void removeInforamaDocumentParameter(InforamaDocumentParameter inforamaDocumentParameter);
    
    //PageInforamaDocument
    public PageInforamaDocument loadPageInforamaDocument(int id);
    public List<PageInforamaDocument> findPageInforamaDocumentForClassName(String className, Space space);
    public List<PageInforamaDocument> findPageInforamaDocumentMatching(PageInforamaDocumentSearch pageInforamaDocumentSearch);
    public void storePageInforamaDocument(PageInforamaDocument pageInforamaDocument);

    //InforamaDocument and Space
    public List<InforamaDocument> findInforamaDocumentsForSpace(Space space);
    public List<Space> findSpacesForInforamaDocument(InforamaDocument inforamaDocument);
    public int loadCountSpacesForInforamaDocument(InforamaDocument inforamaDocument);
	/**
	 * Returns all other Spaces of which the Assets are visible for the given Space
	 * @param space
	 * @return
	 */
	Collection<Space> getVisibleAssetSpacesForSpace(Space space);
	
	/**
	 * Returns all Assets that are visible for the given Space
	 * @param space the given space
	 * @return
	 */
	public Collection<Asset> getVisibleAssetsForSpace(Space space);
	/**
	 * 
	 */
	public Iterator<Item> findItemsDueIn24Hours();
	/**
	 * @param id
	 * @return
	 */
	I18nStringResource loadI18nStringResource(I18nStringIdentifier id);
	/**
	 * @return
	 */
	public List<Language> getAllLanguages();
	/**
	 * @param nt
	 * @return
	 */
	//public List<I18nStringResource> findI18nStringResourcesFor(I18nResourceTranslatable nt);
	public List<I18nStringResource> findI18nStringResourcesFor(String propertyName, I18nResourceTranslatable nt);
	public void saveOrUpdateTranslations(I18nResourceTranslatable nt);
	public void refresh(Serializable entity);
	public AssetType loadAssetTypeByAssetId(Long assetId);
	public ItemFieldCustomAttribute loadItemCustomAttribute(Space space,
			String fieldName);
	public List<CustomAttributeLookupValue> findAllLookupValuesByCustomAttribute(CustomAttribute attribute);
	Map<String, List> findItemGroupByTotals(ItemSearch itemSearch);
	List<ItemRenderingTemplate> getItemRenderingTemplates(Space space);
	
	/**
	 * Return the top-priority template for the combination of the user's roles and the current item status (state) 
	 * @param user
	 * @param itemStatus
	 * @param spaceId
	 * @return the top-priority template to use or null if no match is found.
	 */
	ItemRenderingTemplate getItemRenderingTemplateForUser(final User user, Integer itemStatus, Long spaceId);
	
	

	/**
	 * Loads the Metadata instance corresponding 
	 * to the given space from the database
	 * @param space
	 * @return the persisted Metadata instance
	 */
	public Metadata getMetadataForSpace(Space space);
	
	/**
	 * Returns a cached version of the Metadata instance corresponding 
	 * to the given space, adding it in the cache if necessary.
	 * @param space
	 * @return the cached Metadata instance
	 */
	public Metadata getCachedMetadataForSpace(Space space);
	List<Object[]> selectLatestItemPerSpace(User user);
	/**
	 * 
	 * Delete ItemFieldCustomAttribute by item and field name
	 * @param space
	 * @param fieldName
	 */
	void deleteItemCustomAttribute(Space space, String fieldName);
}
