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
import gr.abiss.calipso.domain.Field.Name;
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
import gr.abiss.calipso.domain.RenderingTemplate;
import gr.abiss.calipso.domain.RoleSpaceStdField;
import gr.abiss.calipso.domain.RoleType;
import gr.abiss.calipso.domain.SavedSearch;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.SpaceGroup;
import gr.abiss.calipso.domain.SpaceRole;
import gr.abiss.calipso.domain.StdField;
import gr.abiss.calipso.domain.StdFieldType;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.UserSpaceRole;
import gr.abiss.calipso.domain.ValidationExpression;
import gr.abiss.calipso.domain.i18n.I18nResourceTranslatable;
import gr.abiss.calipso.dto.AssetSearch;
import gr.abiss.calipso.wicket.regexp.ValidationExpressionSearch;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.activation.DataSource;
import javax.mail.internet.MimeBodyPart;

import org.acegisecurity.userdetails.UserDetailsService;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.springframework.context.MessageSource;

/**
 * CalipsoService main business interface (Service Layer)
 */
public interface CalipsoService extends UserDetailsService {
	Set<SpaceGroup> getSpaceGroupsForUser(Serializable userId);
	void storeItemSpace(long itemId, Space space);
    // TODO remove Wicket dep with FileUpload
    void storeItem(Item item, Map<String, FileUpload> fileUploads2sav);
    void updateItem(Item item, User user);
    void updateItem(Item item, User user, boolean updateHistory);
    public List<Attachment> findTemporaryAttachments();
    public MessageSource getMessageSource();
    public void removeExpiredTemporaryAttachments();
    void storeHistoryForItem(long itemId, Map<String,FileUpload> fileUploads, History history);
    public void updateHistory(History history);
    Item loadItem(long id);
    Item loadItemByRefId(String refId);
    History loadHistory(long id);
    List<Item> findItems(ItemSearch itemSearch);
    List<AbstractItem> findAllItems();
    public List<History> findHistoryForItem(Item item);
    void removeItem(Item item);
    public List<Item> findUnassignedItemsForSpace(Space space);
    public int loadCountUnassignedItemsForSpace(Space space);
    void removeItemItem(ItemItem itemItem);
    //========================================================
    I18nStringResource loadI18nStringResource(I18nStringIdentifier id);
    //========================================================
    int loadCountOfRecordsHavingFieldNotNull(Space space, Field field);
    int bulkUpdateFieldToNull(Space space, Field field);
    int loadCountOfRecordsHavingFieldWithValue(Space space, Field field, int optionKey);
    int bulkUpdateFieldToNullForValue(Space space, Field field, int optionKey);
    int loadCountOfRecordsHavingStatus(Space space, int status);
    int bulkUpdateStatusToOpen(Space space, int status);
    int bulkUpdateRenameSpaceRole(Space space, String oldRoleKey, String newRoleKey);
//    int bulkUpdateDeleteSpaceRole(Space space, String roleKey);
    //========================================================
    void storeUser(User user);
    void storeUser(User user, String password, boolean sendNotifications);
    void removeUser(User user);    
    User loadUser(long id);

    /**
     * Initialize implicit (i.e. not stored in DB) roles for a session user
     * @param user the user to initialize implicit roles for
     * @param spaces the spaces to scan (a role for space will be added only if the user doesnt already have any) 
     * @param roleType the role type to use (e.g. RoleType.GUEST, RoleType.ANONYMOUS)
     */
	public void initImplicitRoles(User user, List<Space> spaces, RoleType roleType);
    User loadUser(String loginName);
    List<User> findAllUsers();
    List<Organization> findAllOrganizations();
    List<User> findUsersWhereIdIn(List<Long> ids);
    List<User> findUsersMatching(String searchText, String searchOn, Space space);
    List<User> findUsersMatching(String searchText, String searchOn);
    List<User> findUsersMatching(String searchText, String searchOn, int start, int count);
    int findUsersCountMatching(String searchText, String searchOn);
    List<User> findUsersForSpace(long spaceId);
    List<User> findUsersInOrganizations(List<Organization> orgs);
    List<UserSpaceRole> findUserRolesForSpace(long spaceId);
//    List<User> findUsersWithRoleForSpace(long spaceId, String roleKey);
    List<User> findUsersWithRoleForSpace(SpaceRole spaceRole);
    List<User> findUsersForUser(User user);
    List<User> findUnallocatedUsersForSpace(long spaceId);
    int loadCountOfHistoryInvolvingUser(User user);
    //========================================================
    CountsHolder loadCountsForUser(User user);
    Counts loadCountsForUserSpace(User user, Space space);
    //========================================================
    void storeUnpublishedSpace(Space space);
    Space storeSpace(Space space);
    Space loadSpace(long id);
    Space loadSpace(String prefixCode);
	Space loadSpace(SpaceRole spaceRole);
    List<Space> findAllSpaces();
    List<Space> findSpacesWhereIdIn(List<Long> ids);
    List<Space> findSpacesWhereGuestAllowed();
    List<Space> findSpacesWhereAnonymousAllowed();
    List<Space> findUnallocatedSpacesForUser(long userId);
    void removeSpace(Space space);
    //========================================================
    public void storeUserSpaceRole(User user, SpaceRole spaceRole);
    UserSpaceRole loadUserSpaceRole(long id);
    void removeUserSpaceRole(UserSpaceRole userSpaceRole);
    public int bulkUpdateDeleteUserSpaceRolesForSpace(Space space);
    //========================================================
//    void storeMetadata(Metadata metadata);
//    Metadata loadMetadata(long id);
    //========================================================
    String generatePassword();
    String encodeClearText(String clearText);
    Map<String, String> getLocales();
    String getDefaultLocale();
	String getSupportedOrDefaultLocaleCode(Locale returnIfSupportedLocale);
    String getCalipsoHome();
    int getAttachmentMaxSizeInMb();
    int getSessionTimeoutInMinutes();
    //========================================================
    Map<String, String> loadAllConfig();
    void storeConfig(Config config);
    String loadConfig(String param);
    //========================================================
    void rebuildIndexes();
    void index(AbstractItem item);
    void clearIndexes();
    boolean validateTextSearchQuery(String text);
    //========================================================
    void executeHourlyTask();
    void executePollingTask();
    //========================================================
    String getReleaseVersion();
    String getReleaseTimestamp();
    // Saved Search============================
    public void storeSavedSearch(SavedSearch savedSearch);
    public SavedSearch loadSavedSearch(long id); 
    public void removeSavedSearch(SavedSearch savedSearch);
    public List<SavedSearch> findSavedSearches(User user);
    public List<SavedSearch> findSavedSearches(User user, Space space);
    public List<SavedSearch> findVisibleSearches(User user);
    // Asset Management ============================
    public void storeCustomAttribute(AssetTypeCustomAttribute assetTypeCustomAttribute);
    public void store(CustomAttribute attribute);
    public List<AssetTypeCustomAttribute> findAllCustomAttributes();
    public AssetTypeCustomAttribute loadAssetTypeCustomAttribute(Long id);
    public CustomAttributeLookupValue loadCustomAttributeLookupValue(long id);
    public CustomAttributeLookupValue loadCustomAttributeLookupValue(CustomAttribute attr, String name);
    public void removeLookupValue(CustomAttributeLookupValue lookupValue);
    public void storeLookupValue(CustomAttributeLookupValue lookupValue);
    public void storeCustomAttributeLookupValues(List<CustomAttributeLookupValue> lookupValues);
    public List<AssetTypeCustomAttribute> findCustomAttributesMatching(final AssetTypeCustomAttributeSearch searchCustomAttribute);
    public List<AssetType> findAllAssetTypesByCustomAttribute(AssetTypeCustomAttribute attribute);
    public int loadCountAssetsForCustomAttribute(AssetTypeCustomAttribute customAttribute);
    public int loadCountForAssetTypeAndCustomAttribute(AssetType assetType, CustomAttribute customAttribute);
    public int loadCountForCustomAttributeLookupValue(CustomAttributeLookupValue lookupValue);
    public void updateAssets(Collection<Asset> assets);
    public void removeCustomAttribute(CustomAttribute customAttribute);

    public int findAssetsMatchingCount(AssetSearch assetSearch, final boolean fetchCustomAttributes);
    public List<Asset> findAssetsMatchingSubList(AssetSearch assetSearch, final boolean fetchCustomAttributes, int start, int count);
    
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
    public List<CustomAttributeLookupValue> findAllLookupValuesByCustomAttribute(CustomAttribute attr);
    public int findLookupValuesCountByCustomAttribute(CustomAttribute attr);
    public List<CustomAttributeLookupValue> findLookupValuesByCustomAttribute(CustomAttribute attr, int start, int count);

    public void storeAsset(Asset asset);
    public Asset loadAsset(Long id);
    public Asset loadAssetWithAttributes(Long id);
    public List<Asset> findAssetsMatching(AssetSearch assetSearch, final boolean fetchCustomAttributes);
    public List<Object> findCustomAttributeValueMatching(AssetSearch assetSearch);
    public List<Asset> findAllAssetsByItem(Item item);
    public List<Asset> findAllAssetsBySpace(Space space);

    //  Fields ================================================================
    public List<RoleSpaceStdField> findSpaceFieldsBySpaceRole(SpaceRole spaceRole);
    public List<RoleSpaceStdField> findSpaceFieldsBySpaceandRoleType(SpaceRole spaceRole);
    public List<RoleSpaceStdField> findSpaceFieldsBySpace(Space space);
    public RoleSpaceStdField loadRoleSpaceField(long id);
    public void storeRoleSpaceStdField(RoleSpaceStdField roleSpaceStdField);
    public List<StdField> loadAllStdFields();
    public List<StdField> findStdFieldsByType(StdFieldType fieldType);
    public List<RoleSpaceStdField>findSpaceFieldsForUser(User user);
    public void removeRoleSpaceStdField(RoleSpaceStdField roleSpaceStdField);
    public int bulkUpdateDeleteRoleSpaceStdFieldsForSpaceRole(SpaceRole spaceRole);

    // Organization -------------------------------------------------------------------------------
    public void storeOrganization(Organization organization);
    public Organization loadOrganization(long id);
    public List<Organization> findOrganizationsMatching(OrganizationSearch organizationSearch);
    // Country ----------------------------------------------------------------------------------
    public void storeCountry(Country country);
    public Country loadCountry(String id);
    public List<Country> findAllCountries();
    public List<Language> getSupportedLanguages();
    public List<I18nStringResource> getNameTranslations(I18nResourceTranslatable nt);
    public List<I18nStringResource> getPropertyTranslations(String propertyName, I18nResourceTranslatable nt);
    // Validation Expression -------------------------------------------------------------------------------
    public void storeValidationExpression(ValidationExpression validationExpression);
    public ValidationExpression loadValidationExpression(long id);
    public List<ValidationExpression> findAllValidationExpressions();
    public List<ValidationExpression> findValidationExpressionsMatching(ValidationExpressionSearch validationExpression);
    public ValidationExpression findValidationExpressionByName(String name);
    
    // Miscellaneous ------------------------------------------------------------------------------
    public void sendPassword(String emailAddress);
    public void sendPassword(User user);
    abstract void executeReadMailForNewItems();
    public int getRecordsPerPage();
 
    //Space Roles ---------------------------------------------------------------------------------
    public void storeSpaceRole(SpaceRole spaceRole);
    public SpaceRole loadSpaceRole(long spaceRoleId);
    public List<SpaceRole> findSpaceRolesForSpace(Space space);
    public void removeSpaceRole(SpaceRole spaceRole);
    public int bulkUpdateDeleteSpaceRolesForSpace(Space space);
    public List<SpaceRole> findAvailableSpaceRolesForUser(Space space, User user);
    public List<SpaceRole> findSpaceRolesForSpaceAndRoleType(Space space, int roleTypeId);
    public SpaceRole loadAdministrator();
    public SpaceRole loadSpaceAdministrator(Space space);
    
    // Inforama Integration -----------------------------------------------------------------------
    public PageDictionary loadPageDictionary(int id);
    public PageDictionary loadPageDictionary(String className);
    public InforamaDocument loadInforamaDocument(int id);
    public void storeInforamaDocument(InforamaDocument inforamaDocument);
    public void storeInforamaDocumentParameters(InforamaDocument inforamaDocument);
    public List<InforamaDocument> findInforamaDocumentsForClassNameAndSpace(String className, Space space);
    public void removeInforamaDocument(InforamaDocument inforamaDocument);
    public List<InforamaDocument> findAllInforamaDocuments();
    
    public InforamaDocumentParameter loadInforamaDocumentParameter(int id);
    public void storeInforamaDocumentParameter(InforamaDocumentParameter inforamaDocumentParameter);
    public List<InforamaDocumentParameter> findInforamaDocumentParametersForDocument(InforamaDocument inforamaDocument);
    public PageInforamaDocument loadPageInforamaDocument(int id);
    public List<PageInforamaDocument> findPageInforamaDocumentForClassName(String className, Space space);
    public void removeInforamaDocumentParameter(InforamaDocumentParameter inforamaDocumentParameter);
    
    public List<PageDictionary> findPageDictionaryMatching(PageDictionarySearch pageDictionarySearch);
    public void storePageDictionary(PageDictionary pageDictionary);
    public void removePageDictionary(PageDictionary pageDictionary);
    public List<InforamaDocument> findInforamaDocumentMatching(InforamaDocumentSearch inforamaDocumentSearch);
    public List<InforamaDocumentParameter> findInforamaDocumentParameterMatching(InforamaDocumentParameterSearch inforamaDocumentParameterSearch);
    public List<PageInforamaDocument> findPageInforamaDocumentMatching(PageInforamaDocumentSearch pageInforamaDocumentSearch);
    public void storePageInforamaDocument(PageInforamaDocument pageInforamaDocument);
    
    public List<InforamaDocument> findInforamaDocumentsForSpace(Space space);
    public List<Space> findSpacesForInforamaDocument(InforamaDocument inforamaDocument);
    public int loadCountSpacesForInforamaDocument(InforamaDocument inforamaDocument);
	/**
	 * @param currentSpace
	 * @return
	 */
	Collection<Space> getVisibleAssetSpacesForSpace(Space currentSpace);
	
	/**
	 * Returns all Assets that are visible for the given Space
	 * @param space
	 * @return
	 */
	public Collection<Asset> getVisibleAssetsForSpace(Space space);
	public Asset loadAssetAttributes(Asset asset);
	public String getPrintTemplateTextForAsset(Long assetId);
	public List<CustomAttributeLookupValue> findLookupValues(Space space, String fieldName);
	public ItemFieldCustomAttribute loadItemCustomAttribute(Space space, String text);
	public List<Field> getEditableFieldList(Item item);
	public List<Field> getEditableFieldList(Item item, User user);
	Map<String, List> findItemGroupByTotals(ItemSearch itemSearch);
	boolean isAllowedToCreateNewItem(User user, Space space);
	public String getFontsDirPath();
	public String getResourcesDirPath();
	boolean isEmailSendingConfigured();
	public List<ItemRenderingTemplate> getItemRenderingTemplates(Space space);
	
	/**
	 * Return the top-priority template for the combination of the user's roles and the current item status (state) 
	 * @param user
	 * @param item
	 * @return the top-priority template to use or null if no match is found.
	 */
	ItemRenderingTemplate getItemRenderingTemplateForUser(User user, Integer itemStatus, Long spaceId);
	Constructor getDashBoardPanelConstructor();
	List<Object[]> selectLatestItemPerSpace(User user);
	void send(User recipient, String subject, String messageBody);
	void send(String email, String subject, String messageBody);
	void send(String email, String subject, String body, Map<String, DataSource> attachments);
	/**
	 * 
	 * Delete ItemFieldCustomAttribute by item and field name
	 * @param space
	 * @param fieldName
	 */
	void removeItemCustomAttribute(Space space, String fieldName);
	Space loadSpaceForEditing(long id);
	void send(String email, String subject, String messageBody, boolean html);
	void send(String email, String subject, String messageBody,
			Map<String, DataSource> attachments, boolean html);
	

	/**
	 * Returns a cached version of the Metadata instance corresponding 
	 * to the given space, adding it in the cache if necessary.
	 * @param space
	 * @return the cached Metadata instance
	 */
	Metadata getCachedMetadataForSpace(Space space);
	String getBaseUrl();
	Set<User> loadSpaceGroupAdmins(Long id);
	Map<String, RenderingTemplate> loadSpaceRoleTemplates(Long id);
	List<CustomAttributeLookupValue> findActiveLookupValuesByCustomAttribute(
			CustomAttribute attr);
	

}