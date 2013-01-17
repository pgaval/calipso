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
import gr.abiss.calipso.domain.CustomCriteria;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.I18nStringIdentifier;
import gr.abiss.calipso.domain.I18nStringResource;
import gr.abiss.calipso.domain.InforamaDocument;
import gr.abiss.calipso.domain.InforamaDocumentParameter;
import gr.abiss.calipso.domain.InforamaDocumentParameterSearch;
import gr.abiss.calipso.domain.InforamaDocumentSearch;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.ItemItem;
import gr.abiss.calipso.domain.ItemRefId;
import gr.abiss.calipso.domain.ItemRenderingTemplate;
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.ItemUser;
import gr.abiss.calipso.domain.ItemFieldCustomAttribute;
import gr.abiss.calipso.domain.Language;
import gr.abiss.calipso.domain.MailedItem;
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
import gr.abiss.calipso.domain.SpaceSequence;
import gr.abiss.calipso.domain.State;
import gr.abiss.calipso.domain.StdField;
import gr.abiss.calipso.domain.StdFieldType;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.UserSpaceRole;
import gr.abiss.calipso.domain.ValidationExpression;
import gr.abiss.calipso.domain.i18n.I18nResourceTranslatable;
import gr.abiss.calipso.dto.AssetSearch;
import gr.abiss.calipso.lucene.IndexSearcher;
import gr.abiss.calipso.lucene.Indexer;
import gr.abiss.calipso.mail.MailReader;
import gr.abiss.calipso.mail.MailSender;
import gr.abiss.calipso.plugins.state.AbstractStatePlugin;
import gr.abiss.calipso.plugins.state.CopyItemInfoToAssetPlugin;
import gr.abiss.calipso.util.AttachmentUtils;
import gr.abiss.calipso.util.SpaceUtils;
import gr.abiss.calipso.util.UserUtils;
import gr.abiss.calipso.util.XmlUtils;
import gr.abiss.calipso.wicket.regexp.ValidationExpressionSearch;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.activation.DataSource;
import javax.mail.internet.MimeBodyPart;

import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.jfree.util.Log;
import org.springframework.context.MessageSource;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * CalipsoService Service Layer implementation This is where all the business
 * logic is For data persistence this delegates to CalipsoDao
 */
public class CalipsoServiceImpl implements CalipsoService {

	/**
	 * Please make proper use of logging, see
	 * http://www.owasp.org/index.php/Category
	 * :Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger
			.getLogger(CalipsoServiceImpl.class);
	private CalipsoDao dao;
	private PasswordEncoder passwordEncoder;
	private MailSender mailSender;
	private Indexer indexer;
	private IndexSearcher indexSearcher;
	private MessageSource messageSource;
	private Map<String, String> locales;
	private String defaultLocale = "en";
	private String releaseVersion;
	private String releaseTimestamp;
	private String calipsoHome;
	private int attachmentMaxSizeInMb = 5;
	private int sessionTimeoutInMinutes = 30;

	private int pageSize;
	private Constructor dashBoardPanelConstructor;
	private Map<String, String> config;

	public void setLocaleList(String[] array) {
		locales = new LinkedHashMap<String, String>();
		for (String localeString : array) {
			Locale locale = org.springframework.util.StringUtils.parseLocaleString(localeString);
			locales.put(localeString,
					localeString + " - " + locale.getDisplayName());
			if (dao.get(Language.class, localeString) == null) {
				dao.save(new Language(localeString));
			}
		}
		logger.info("available locales configured " + locales);
	}

	public List<Language> getSupportedLanguages() {
		return dao.getAllLanguages();
	}

	public List<I18nStringResource> getNameTranslations(
			I18nResourceTranslatable nt) {
		return this.getPropertyTranslations("name", nt);
	}

	public List<I18nStringResource> getPropertyTranslations(
			String propertyName, I18nResourceTranslatable nt) {
		return this.dao.findI18nStringResourcesFor(propertyName, nt);
	}
	
	public void updateAssets(Collection<Asset> assets){
		if(CollectionUtils.isNotEmpty(assets)){
			Date now = new Date();
			for(Asset asset : assets){
				updateDates(asset, now);
				this.dao.update(asset);
			}
		}
	}
	
	private void updateDates(Asset asset, Date date){
		if(asset.getDateCreated() == null){
			asset.setDateCreated(date);
		}
		asset.setDateUpdated(date);
	}
	
	/**
	 * @return the messageSource
	 */
	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setDao(CalipsoDao dao) {
		this.dao = dao;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setIndexSearcher(IndexSearcher indexSearcher) {
		this.indexSearcher = indexSearcher;
	}

	public void setIndexer(Indexer indexer) {
		this.indexer = indexer;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setReleaseTimestamp(String releaseTimestamp) {
		this.releaseTimestamp = releaseTimestamp;
	}

	public void setReleaseVersion(String releaseVersion) {
		this.releaseVersion = releaseVersion;
	}

	public void setCalipsoHome(String calipsoHome) {
		this.calipsoHome = calipsoHome;
	}

	public String getCalipsoHome() {
		return calipsoHome;
	}

	public int getAttachmentMaxSizeInMb() {
		return attachmentMaxSizeInMb;
	}

	public int getSessionTimeoutInMinutes() {
		return sessionTimeoutInMinutes;
	}

	@Override
	public Constructor getDashBoardPanelConstructor() {
		return dashBoardPanelConstructor;
	}

	/**
	 * this has not been factored into the util package or a helper class
	 * because it depends on the PasswordEncoder configured
	 */
	public String generatePassword() {
		byte[] ab = new byte[1];
		Random r = new Random();
		r.nextBytes(ab);
		return passwordEncoder.encodePassword(new String(ab), null).substring(
				24);
	}

	/**
	 * this has not been factored into the util package or a helper class
	 * because it depends on the PasswordEncoder configured
	 */
	public String encodeClearText(String clearText) {
		return passwordEncoder.encodePassword(clearText, null);
	}

	public String decodeToClearText(String encodedPassword) {
		return "";
		// passwordEncoder.
	}

	public Map<String, String> getLocales() {
		return locales;
	}

	public String getDefaultLocale() {
		return defaultLocale;
	}

	public String getSupportedOrDefaultLocaleCode(Locale returnIfSupportedLocale) {
		String localeCode = returnIfSupportedLocale.getLanguage();
		if(localeCode.length() > 2){
			localeCode = localeCode.substring(0, 2);
		}
		List<Language> languages = this.getSupportedLanguages();
		if(CollectionUtils.isNotEmpty(languages)){
			for(Language lang : languages){
				if(lang.getId().equals(localeCode)){
					return localeCode;
				}
			}
		}
		return defaultLocale;
	}

	/**
	 * this is automatically called by spring init-method hook on startup, also
	 * called whenever config is edited to refresh TODO move config into a
	 * settings class to reduce service clutter
	 */
	public void init() {
		config = loadAllConfig();
		initMailSender(config);
		initDefaultLocale(config.get("locale.default"));
		initAttachmentMaxSize(config.get("attachment.maxsize"));
		initDashBoardPanelConstructor(config.get("classes.dashboard"));
		initSessionTimeout(config.get("session.timeout"));
		initPageSize(config.get("calipso.pageSize"));
	}

	@Override
	public String getBaseUrl(){
		String url = config.get("calipso.url.base");
		if(StringUtils.isBlank(url)){
			url = "http://localhost/calipso/";
		}
		if (!url.endsWith("/")) {
            url = url + "/";
        }   
		return url;
	}
	
	private void initMailSender(Map<String, String> config) {
		this.mailSender = new MailSender(config, messageSource, defaultLocale);
	}

	private void initDefaultLocale(String localeString) {
		if (localeString == null || !locales.containsKey(localeString)) {
			logger.warn("invalid default locale configured = '" + localeString
					+ "', using " + this.defaultLocale);
		} else {
			this.defaultLocale = localeString;
		}
		logger.info("default locale set to '" + this.defaultLocale + "'");
	}

	private void initAttachmentMaxSize(String s) {
		try {
			this.attachmentMaxSizeInMb = Integer.parseInt(s);
		} catch (Exception e) {
			logger.warn("invalid attachment max size '" + s + "', using "
					+ attachmentMaxSizeInMb);
		}
		logger.info("attachment max size set to " + this.attachmentMaxSizeInMb
				+ " MB");
	}

	private void initDashBoardPanelConstructor(String className) {
		try {
			if(StringUtils.isEmpty(className)){
				logger.info("Parameter 'classes.dashboard' is empty, using default DashboardPanel");
				className = "gr.abiss.calipso.wicket.DashboardPanel";
			}
			else{
				logger.info("Initializing with DashboardPanel class: "+className);
			}
			Class aClass = Class.forName(className);
        	Constructor constructor =
        			aClass.getConstructor(new Class[]{String.class, IBreadCrumbModel.class});
			this.dashBoardPanelConstructor = constructor;
		} catch (Exception e) {
			logger.error("Invalid classname or implementation found trying to set the DashBoardPanel class to '"+className+"'. The default implementation will be used.", e);
			this.initDashBoardPanelConstructor("gr.abiss.calipso.wicket.DashboardPanel");
		}
	}

	private void initSessionTimeout(String s) {
		try {
			this.sessionTimeoutInMinutes = Integer.parseInt(s);
		} catch (Exception e) {
			logger.warn("invalid session timeout '" + s + "', using "
					+ this.sessionTimeoutInMinutes);
		}
		logger.info("session timeout set to " + this.sessionTimeoutInMinutes
				+ " minutes");
	}

	private void initPageSize(String pgSize) {
		if (pgSize == null || (pgSize != null && pgSize.trim().equals(""))) {
			this.pageSize = Constants.PAGE_SIZE;
		} else {
			try {
				this.pageSize = Integer.parseInt(pgSize);
			} catch (NumberFormatException numberFormatException) {
				this.pageSize = Constants.PAGE_SIZE;
			}
		}
	}

	// ==========================================================================

	/**
	 * @param fileUpload
	 *            the uploaded file
	 * @param relativePath
	 *            , e.g. spaceId/itemId/historyId
	 * @param fileName
	 *            e.g. foo.txt private Attachment getAttachment(FileUpload
	 *            fileUpload, String relativePath, String fileName) {
	 *            if(fileUpload == null) { return null; }
	 *            logger.debug("fileUpload not null"); Attachment attachment =
	 *            new Attachment(); attachment.setFileName(fileName);
	 *            attachment.setFilePrefix(relativePath);
	 * 
	 *            dao.storeAttachment(attachment);
	 * 
	 *            return attachment; }
	 */


	/**
	 * Returns a cached version of the Metadata instance corresponding 
	 * to the given space, adding it in the cache if necessary.
	 * @param space
	 * @return the cached Metadata instance
	 */
	@Override
	public Metadata getCachedMetadataForSpace(Space space){
		return this.dao.getCachedMetadataForSpace(space);
	}
	
	/**
	 * Save new Items. This method is not intended for updates.
	 */
	public synchronized void storeItem(Item item,
			Map<String, FileUpload> fileUploadsMap) {
		try{


	    	if(item.getLoggedBy() != null && item.getLoggedBy().isAnonymous()){
	    		item.setLoggedBy(null);
	    	}
	    	if(item.getReportedBy() != null && item.getReportedBy().isAnonymous()){
	    		item.setReportedBy(null);
	    	}

			// run plugins
			runPreStateChangePlugins(item);
			History history = new History(item);
			// copy attachments
			copyAttachmentsToHistory(item, history);

			Date now = new Date();
			item.setTimeStamp(now);
			history.setTimeStamp(now);
			item.add(history);
			SpaceSequence spaceSequence = dao.loadSpaceSequence(item.getSpace()
					.getSpaceSequence().getId());
			item.setSequenceNum(spaceSequence.next());
			// the synchronize for this storeItem method and the hibernate flush()
			// call in the dao implementation
			// are important to prevent duplicate sequence numbers
			dao.storeSpaceSequence(spaceSequence);
			// just call attachment.setPermanent false in case we had temp
			// attachments for some reason
			makeAttachmentsPermanent(history);

			// due to, state due to
			Map<Integer, Long> stateDurations = dao.getCachedMetadataForSpace(item.getSpace())
					.getStatesDurationMap();
			if (MapUtils.isNotEmpty(stateDurations)) {
				Long stateDuration = stateDurations.get(item.getStatus());
				if (stateDuration != null) {
					Date stateDeadline = new Date(now.getTime()
							+ stateDuration.intValue());
					item.setStateDueTo(stateDeadline);
				}
			}

			// set default duration if the space offers one and the item has none
			// set
			if (item.getDueTo() == null
					&& item.getSpace().getDefaultDuration() != null) {
				item.setDueTo(new Date(now.getTime()
						+ item.getSpace().getDefaultDuration().longValue()));
			}
			
			// this will at the moment execute unnecessary updates (bug in Hibernate
			// handling of "version" property)
			// see
			// http://opensource.atlassian.com/projects/hibernate/browse/HHH-1401
			// TODO confirm if above does not happen anymore
			dao.storeItem(item);
			// maybe no file uploads were given,
			// e.g. when just updating Assets
			if (fileUploadsMap != null) {
				// store the physical files *after* database persistence to
				// produce proper ID-based file paths
	//
//				logger.debug("makePermanentAttachmentFiles, attachments: "
//						+ history.getAttachments() + ", uploadsMap: "
//						+ fileUploadsMap);
				AttachmentUtils.makePermanentAttachmentFiles(
						history.getAttachments(), fileUploadsMap, calipsoHome);
			} else if (history.getAttachments() != null) {
				// we got attachments but no file uploads?
				throw new RuntimeException("Attachments contain no physical files");
			} else {
				logger.debug("No attachment files were given");
			}
			// update attachment info
			this.dao.saveOrUpdateAll(history.getAttachments());
	//
//			logger.debug("storeItem: Before calling runStatePlugins, itemStatus: "
//					+ item.getStatusValue() + ", historyStatus: "
//					+ history.getStatusValue());
			// run any plugins as neccessary
			runPostStateChangePlugins(history, item);

			// update indexes
			indexer.index(item);
			indexer.index(history);
			if (item.isSendNotifications()) {
				mailSender.send(item);
			}
		
		}
		catch(RuntimeException e){
			logger.error("Failed to persist item", e);
		}
		
	}

	private void runPreStateChangePlugins(Item item) {
		runStateChangePlugins(null, item, AbstractStatePlugin.PRE_STATE_CHANGE);
	}
	private void runPreHistorySavePlugins(History history) {
		runStateChangePlugins(history, null, AbstractStatePlugin.PRE_HISTORY_SAVE);
	}

	private void runPostStateChangePlugins(History history, Item item) {
		Map<Integer, Long> assetsToCreateMap = item.getSpace().getMetadata()
				.getStatesAssetTypeIdMap();
		if (history.getStatus() != null
				&& MapUtils.isNotEmpty(assetsToCreateMap)) {
			Long assetId = assetsToCreateMap.get(history.getStatus());
			if (assetId != null && assetId.longValue() != 0) {
				AssetType assetType = this.loadAssetType(assetId.longValue());
				CopyItemInfoToAssetPlugin plugin = null;
				// update or create asset?
				boolean update = false;
				Set<Asset> itemAssets = item.getAssets();
				if (CollectionUtils.isNotEmpty(itemAssets)) {
					for (Asset asset : itemAssets) {
						if (asset.getAssetType().equals(assetType)) {
							update = true;
							plugin = new CopyItemInfoToAssetPlugin(asset);
							break;
						}
					}
				}
				if (!update) {
					plugin = new CopyItemInfoToAssetPlugin(assetType);
				}
				plugin.executePostStateChange(this, history);
			}
		}
		runStateChangePlugins(history, item, AbstractStatePlugin.POST_STATE_CHANGE);
		
	}

	/**
	 * When saving/updating an Item and after creating a new history object to
	 * reflect the changes, we need to move attachments from TEMP storage to
	 * normal.
	 * 
	 * @param history
	 */
	private void makeAttachmentsPermanent(History history) {
		if (history != null && history.getAttachments() != null) {
			for (Attachment attachment : history.getAttachments()) {
				attachment.setTemporary(false);
			}
		}
	}

	/**
	 * Copy attachments from the Item to a History object when creating the Item
	 * for the first time.
	 * 
	 * @param item
	 * @param history
	 */
	private void copyAttachmentsToHistory(Item item, History history) {
		Set<Attachment> attachments = item.getAttachments();
		if (attachments != null && attachments.size() > 0) {
			for (Attachment attachment : attachments) {
				attachment.setItem(item);
				history.addAttachment(attachment);
			}
		}
	}

	public void updateItem(Item item, User user) {
		//logger.debug("update item called");
		History history = new History(item);
		history.setAssignedTo(null);
		history.setStatus(null);
		history.setLoggedBy(user);
		history.setComment(item.getEditReason());
		history.setTimeStamp(new Date());
		history.setDueTo(item.getDueTo());
		copyAttachmentsToHistory(item, history);
		item.add(history);
		dao.storeItem(item); // merge edits + history
		makeAttachmentsPermanent(history);
		// TODO index?
		if (item.isSendNotifications()) {
			mailSender.send(item);
		}
	}

	public void updateItem(Item item, User user, boolean updateHistory) {
		if (updateHistory) {
			//logger.debug("update item called");
			History history = new History(item);
			history.setAssignedTo(null);
			history.setStatus(null);
			history.setLoggedBy(user);
			history.setComment(item.getEditReason());
			history.setTimeStamp(new Date());
			history.setDueTo(item.getDueTo());
			copyAttachmentsToHistory(item, history);
			makeAttachmentsPermanent(history);
			item.add(history);
		}
		dao.storeItem(item); // merge edits + history
		// TODO index?
		if (item.isSendNotifications()) {
			mailSender.send(item);
		}
	}

	/**
	 * Moves an item from its space to another.
	 **/
	public synchronized void storeItemSpace(long itemId, Space newSpace) {
		Item item = dao.loadItem(itemId);
		// Get a sequence for the new space
		SpaceSequence spaceSequence = dao.loadSpaceSequence(newSpace
				.getSpaceSequence().getId());

		// Set new space sequence
		item.setSequenceNum(spaceSequence.next());
		item.setSpace(newSpace);
		dao.storeItem(item);
	}// storeItemSpace

	public List<Attachment> findTemporaryAttachments() {
		return dao.findTemporaryAttachments();
	}

	// TODO:
	public synchronized void deleteAttachment(Attachment attachment) {
		if (attachment == null)
			return;

		File attachmentFile = AttachmentUtils.getSavedAttachmentFile(
				attachment, calipsoHome);
		String fileName = attachment.getFileName();
		attachmentFile.delete();
		dao.removeAttachment(attachment);

		// TODO:
		if (fileName.endsWith(".png") || fileName.endsWith(".gif")
				|| fileName.endsWith(".bmp") || fileName.endsWith(".jpeg")
				|| fileName.endsWith(".jpg")) {
			Attachment attachmentImage = new Attachment();
			attachmentImage.setFilePrefix(attachment.getFilePrefix());

			// try to delete small image
			attachmentImage.setFileName("smallImage");
			File attachmentImageFileSmall = AttachmentUtils
					.getSavedAttachmentFile(attachmentImage, calipsoHome);

			if (attachmentImageFileSmall.exists())
				attachmentImageFileSmall.delete();

			// try to delete thumb image
			attachmentImage.setFileName("thumbImage");
			File attachmentImageFileThumb = AttachmentUtils
					.getSavedAttachmentFile(attachmentImage, calipsoHome);

			if (attachmentImageFileThumb.exists())
				attachmentImageFileThumb.delete();
		}

	}

	public synchronized void removeExpiredTemporaryAttachments() {
		/*
		 * for (Attachment attachment : findTemporaryAttachments()) { File
		 * attachmentFile = AttachmentUtils.getFile(attachment, calipsoHome);
		 * 
		 * long timePassed = new Date().getTime() -
		 * attachmentFile.lastModified();
		 * 
		 * if(timePassed > 86400000){//if 86400000ms = 24hours passed
		 * deleteAttachment(attachment); } }
		 */
	}

	/*
	 * public synchronized Attachment storeTemporaryAttachment(Attachment
	 * attachment, FileUpload fileUpload) { if(fileUpload == null) { return
	 * null; } if(attachment != null) { attachment.setTemporary(true);
	 * 
	 * 
	 * writeToFile(fileUpload, attachment);
	 * 
	 * dao.storeAttachment(attachment); }
	 * 
	 * 
	 * 
	 * return attachment; }
	 */
	public void updateHistory(History history) {
		dao.update(history);
	}

	public synchronized void storeHistoryForItem(long itemId,
			Map<String, FileUpload> fileUploads, History history) {
		Date now = new Date();

		Item item = dao.loadItem(itemId);
		this.runPreStateChangePlugins(item);
		// keep a ref to file uploads
		for (String filename : fileUploads.keySet()) {
			FileUpload upload = fileUploads.get(filename);
			String extention = upload.getClientFileName().substring(
					upload.getClientFileName().lastIndexOf('.'));
			// String filename = upload.getClientFileName();
			Attachment attachment = new Attachment();
			attachment.setSpace(item.getSpace());
			attachment.setItem(item);
			attachment.setHistory(history);

			attachment.setFileName(filename + extention);
			attachment.setOriginalFileName(upload.getClientFileName());
			attachment.setSimple(filename.equalsIgnoreCase(Field.FIELD_TYPE_SIMPLE_ATTACHEMENT));
			//logger.debug("storeHistoryForItem: making attachment with filename: "
			//		+ attachment.getFileName());
			attachment.setTemporary(false);
			// attachments to Item, replacing others with the same name if
			// needed
			AttachmentUtils.addAndReplaceSameNamed(history, attachment);
		}

		// first apply edits onto item record before we change the item status
		// the item.getEditableFieldList routine depends on the current State of
		// the item
		for (Field field : item.getEditableFieldList(history.getLoggedBy())) {
			Object value = history.getValue(field.getName());
			//logger.info("Before setting item's '"+field.getName()+"' value from:" + item.getValue(field.getName())+", to:"+value);
			if (value != null) {
				item.setValue(field.getName(), value);
			}
		}
		if (history.getStatus() != null) {
			// in case of state change, update the state due to
			if (!history.getStatus().equals(item.getStatus())) {
				// reset notifications
				item.setSentDueToNotifications(false);

				// get the new (i.e. the history) state duration
				Map<Integer, Long> stateDurations = item.getSpace()
						.getMetadata().getStatesDurationMap();
				if (MapUtils.isNotEmpty(stateDurations)) {
					Long stateDuration = stateDurations
							.get(history.getStatus());
					if (stateDuration != null) {
						Date stateDeadline = new Date(now.getTime()
								+ stateDuration.intValue());
						item.setStateDueTo(stateDeadline);
					}
				}
			}
			item.setStatus(history.getStatus());
			item.setAssignedTo(history.getAssignedTo()); // this may be null,
															// when closing
		}
		if (history.getDueTo() != null) {
			item.setDueTo(history.getDueTo());
		}
		if (history.getActualEffort() != null
				&& history.getActualEffort().intValue() == 0) {
			history.setActualEffort(null);
		}
		if (history.getPlannedEffort() != null
				&& history.getPlannedEffort().intValue() > 0) {
			item.setPlannedEffort(history.getPlannedEffort());
		}

		item.setItemUsers(history.getItemUsers());
		history.setTimeStamp(new Date());
		// replace and move to permanent storage
		item.add(history);

		this.runPreHistorySavePlugins(history);
		dao.save(history);
		// update item with the latest, replacing name conflicts
		AttachmentUtils.replaceAttachments(item, history.getAttachments());
		dao.update(item);
		// maybe no file uploads were given,
		// e.g. when just updating Assets
		if (fileUploads != null) {
			// store the physical files *after* database persistence to
			// produce proper ID-based file paths
			if (logger.isDebugEnabled()) {
				logger.debug("makePermanentAttachmentFiles, attachments: "
						+ history.getAttachments() + ", uploadsMap: "
						+ fileUploads);
			}
			AttachmentUtils.makePermanentAttachmentFiles(
					history.getAttachments(), fileUploads, calipsoHome);
		}

		runPostStateChangePlugins(history, item);

		indexer.index(history);
		indexer.index(item);
		if (history.isSendNotifications()) {
			mailSender.send(item);
		}
	}

	/**
	 * @param history
	 * @param item
	 */
	public void runStateChangePlugins(History history, Item item, Integer state) {
		// assets to create based on Item info?
		AbstractItem abstractItem = history != null? history : item;
		//logger.info("RUNNING PLUGINS ("+state+"), item state: "+abstractItem.getSpace().getMetadata().getStatusValue(abstractItem.getStatus()));
		
		// run plugins

		Map<Integer, String> pluginsMap = abstractItem.getSpace().getMetadata()
				.getStatesPluginMap();

		if (abstractItem.getStatus() != null && MapUtils.isNotEmpty(pluginsMap)) {
			String pluginClassNames = pluginsMap.get(abstractItem.getStatus());
			//logger.info("pluginClassNames:"+pluginClassNames + ", status: " + (abstractItem != null?abstractItem.getStatus():null) );
			//logger.info("Running plugins for status: "+abstractItem.getStatus()+", plugins: "+pluginsMap.get(abstractItem.getStatus()));
			if (pluginClassNames != null && pluginClassNames.length() > 0) {

				String[] pluginNames = pluginClassNames.split(" ");
				for(int i=0; i < pluginNames.length;i++){
					String pluginClassName = pluginNames[i];
					//logger.debug("Loading plugin class: "+pluginClassName);
					// "clazz" is the class name to load
					Class clazz = null;
					try {
						clazz = Class.forName(pluginClassName);
						AbstractStatePlugin plugin = (AbstractStatePlugin) clazz
								.newInstance();
						if(state.equals(AbstractStatePlugin.PRE_STATE_CHANGE)){
							plugin.executePreStateChange(this, item);	
						}
						else if(state.equals(AbstractStatePlugin.PRE_HISTORY_SAVE)){
							plugin.executePreHistoryChange(this, history);
						}
						else if(state.equals(AbstractStatePlugin.POST_STATE_CHANGE)){
							plugin.executePostStateChange(this, history);	
						}
						
					} catch (ClassNotFoundException e) {
						logger.error("Cannot load State Plugin class: " + pluginClassName, e);
						e.printStackTrace();
					} catch (InstantiationException ie) {
						logger.error("Cannot load State Plugin class: " + pluginClassName,
								ie);
						ie.printStackTrace();
					} catch (IllegalAccessException iae) {
						logger.error("Cannot load State Plugin class: " + pluginClassName,
								iae);
						iae.printStackTrace();
					}
				}
				

			}
		}
	}

	public Item loadItem(long id) {
		return dao.loadItem(id);
	}

	public Item loadItemByRefId(String refId) {
		ItemRefId itemRefId = new ItemRefId(refId); // throws runtime exception
													// if invalid id
		List<Item> items = dao.findItems(itemRefId.getSequenceNum(),
				itemRefId.getPrefixCode());
		if (items.size() == 0) {
			return null;
		}
		return items.get(0);
	}

	public History loadHistory(long id) {
		return dao.loadHistory(id);
	}

	@Override
	public Map<String, List> findItemGroupByTotals(ItemSearch itemSearch) {
		return dao.findItemGroupByTotals(itemSearch);	
	}
	

	public List<Item> findItems(ItemSearch itemSearch) {
		//logger.info("itemSearch space: "+itemSearch.getSpace());
		String searchText = itemSearch.getSearchText();
		if (searchText != null) {
			List<Long> hits = indexSearcher
					.findItemIdsContainingText(searchText);
			if (hits.size() == 0) {
				itemSearch.setResultCount(0);
				return Collections.<Item> emptyList();
			}
			itemSearch.setItemIds(hits);
		}

		List<Item> itemsList = dao.findItems(itemSearch);
		List<Item> finalItemsList = new ArrayList<Item>();
		String expr = "";
		CustomCriteria customCriteria = itemSearch.getCustomCriteria();
		if (customCriteria != null && !customCriteria.getCriteria().equals("")) {
			for (int i = 0; i < itemsList.size(); i++) {
				// expr =
				// "gr.abiss.calipso.domain.Item item = new gr.abiss.calipso.domain.Item(); item.setTotalResponseTime(new Double("
				// + itemsList.get(i).getTotalResponseTime() + ")); ";
				expr = customCriteria.getValuesExpression(itemsList.get(i));
				expr += "contains = "
						+ customCriteria.getCriteriaExpression("space");

				try {
					Interpreter interpreter = new Interpreter();
					interpreter.eval(expr);
					Boolean contains = (Boolean) interpreter.get("contains");
					if (contains) {
						finalItemsList.add(itemsList.get(i));
					}// if
				}// try
				catch (EvalError evalError) {
					evalError.printStackTrace();
				}// catch
			}// for

			itemSearch.setResultCount(finalItemsList.size());
		} else {
			finalItemsList = itemsList;
		}
		return finalItemsList;
	}

	public List<History> findHistoryForItem(Item item) {
		return dao.findHistoryForItem(item);
	}

	public void removeItem(Item item) {
		if (item.getRelatingItems() != null) {
			for (ItemItem itemItem : item.getRelatingItems()) {
				removeItemItem(itemItem);
			}
		}
		if (item.getRelatedItems() != null) {
			for (ItemItem itemItem : item.getRelatedItems()) {
				removeItemItem(itemItem);
			}
		}
		dao.removeItem(item);
	}

	public List<Item> findUnassignedItemsForSpace(Space space) {
		return dao.findUnassignedItemsForSpace(space);
	}

	public int loadCountUnassignedItemsForSpace(Space space) {
		return dao.loadCountUnassignedItemsForSpace(space);
	}

	public void removeItemItem(ItemItem itemItem) {
		dao.removeItemItem(itemItem);
	}

	public int loadCountOfRecordsHavingFieldNotNull(Space space, Field field) {
		return dao.loadCountOfRecordsHavingFieldNotNull(space, field);
	}

	public int bulkUpdateFieldToNull(Space space, Field field) {
		return dao.bulkUpdateFieldToNull(space, field);
	}

	public int loadCountOfRecordsHavingFieldWithValue(Space space, Field field,
			int optionKey) {
		return dao.loadCountOfRecordsHavingFieldWithValue(space, field,
				optionKey);
	}

	public int bulkUpdateFieldToNullForValue(Space space, Field field,
			int optionKey) {
		return dao.bulkUpdateFieldToNullForValue(space, field, optionKey);
	}

	public int loadCountOfRecordsHavingStatus(Space space, int status) {
		return dao.loadCountOfRecordsHavingStatus(space, status);
	}

	public int bulkUpdateStatusToOpen(Space space, int status) {
		return dao.bulkUpdateStatusToOpen(space, status);
	}

	public int bulkUpdateRenameSpaceRole(Space space, String oldRoleKey,
			String newRoleKey) {
		return dao.bulkUpdateRenameSpaceRole(space, oldRoleKey, newRoleKey);
	}

	// public int bulkUpdateDeleteSpaceRole(Space space, String roleKey) {
	// return dao.bulkUpdateDeleteSpaceRole(space, roleKey);
	// }

	// ========= Acegi UserDetailsService implementation ==========
	public UserDetails loadUserByUsername(String loginName) {
		List<User> users = null;
		if (loginName.indexOf("@") != -1) {
			users = dao.findUsersByEmail(loginName);
		} else {
			users = dao.findUsersByLoginName(loginName);
		}
		if (users.size() == 0) {
			throw new UsernameNotFoundException("User not found for '"
					+ loginName + "'");
		}
		// logger.debug("loadUserByUserName success for '" + loginName + "'");
		User user = users.get(0);
		// allocate implicit GUEST roles for spaces with guest access
		// if the user does not already have roles for those
		initImplicitRoles(user, findSpacesWhereGuestAllowed(), RoleType.GUEST);
		// allocate implicit ANONYMOUS roles for spaces with guest access
		// if the user does not already have roles for those
		initImplicitRoles(user, findSpacesWhereAnonymousAllowed(),
				RoleType.ANONYMOUS);

		for (UserSpaceRole usr : user.getUserSpaceRoles()) {
			// logger.debug("UserSpaceRole for logged in user: " +
			// usr.getSpaceRole().getDescription()+" for space "+usr.getSpaceRole().getSpace());
			// this is a hack, the effect of the next line would be to
			// override hibernate lazy loading and get the space and associated
			// metadata.
			// since this only happens only once on authentication and
			// simplifies a lot of
			// code later because the security principal is "fully prepared",
			// this is hopefully pardonable. The downside is that there may be
			// as many extra db hits
			// as there are spaces allocated for the user. Hibernate caching
			// should alleviate this
			usr.isAbleToCreateNewItem();
		}
		return user;
	}

	/**
	 * Initialize implicit (i.e. not stored in DB) roles for a session user
	 * 
	 * @param user
	 *            the user to initialize implicit roles for
	 * @param spaces
	 *            the spaces to scan (a role for space will be added only if the
	 *            user doesnt already have any)
	 * @param roleType
	 *            the role type to use (e.g. RoleType.GUEST, RoleType.ANONYMOUS)
	 */
	public void initImplicitRoles(User user, List<Space> spaces,
			RoleType roleType) {
		if (CollectionUtils.isNotEmpty(spaces)) {
			Set<Space> userSpaces = user.getSpaces();
			int added = 0;
			for (Space space : spaces) {
				//logger.debug("Initializing user " + roleType.getDescription()
				//		+ " roles for space: " + space.getPrefixCode());
				//logger.debug("Roles of space " + space.getPrefixCode() + ": "
				//		+ space.getSpaceRoles());
				if ((!userSpaces.contains(space))
						&& CollectionUtils.isNotEmpty(space.getSpaceRoles())) {
					//logger.debug("Iterating space roles: "
					//		+ space.getSpaceRoles());
					for (SpaceRole spaceRole : space.getSpaceRoles()) {
						if (spaceRole.getRoleType().equals(roleType)) {
							UserSpaceRole userSpaceRole = new UserSpaceRole(
									user, spaceRole);
						//	logger.debug("Found no Roles for the user in this space but "
						//			+ roleType.getDescription()
						//			+ " is allowed, added role: "
						//			+ userSpaceRole);
							user.getUserSpaceRoles().add(userSpaceRole);
							added++;
							break;
						}
					}

				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Added " + added + " " + roleType.getDescription()
						+ " roles for a total of "
						+ user.getUserSpaceRoles().size() + " roles");
			}
		}

	}

	public User loadUser(long id) {
		return dao.loadUser(id);
	}

	public User loadUser(String loginName) {
		List<User> users = dao.findUsersByLoginName(loginName);
		if (users.size() == 0) {
			return null;
		}
		return users.get(0);
	}

	public void storeUser(User user) {
		// make sure email hash is up to date
		//logger.debug("Updating email hash");
		user.setEmailHash(DigestUtils.md5Hex(user.getEmail().trim().toLowerCase()));
		dao.storeUser(user);
	}

	public void storeUser(User user, String password, boolean sendNotifications) {
		if (password == null) {
			password = generatePassword();
		}
		user.setPassword(encodeClearText(password));
		this.storeUser(user);
		if (sendNotifications) {
			mailSender.sendUserPassword(user, password);
		}
	}

	public void removeUser(User user) {
		dao.removeUser(user);
	}

	public List<User> findAllUsers() {
		return dao.findAllUsers();
	}

	public List<Organization> findAllOrganizations() {
		return dao.findAllOrganizations();
	}

	public List<User> findUsersWhereIdIn(List<Long> ids) {
		return dao.findUsersWhereIdIn(ids);
	}

	public List<User> findUsersMatching(String searchText, String searchOn) {
		return dao.findUsersMatching(searchText, searchOn);
	}

	public int findUsersCountMatching(String searchText, String searchOn) {
		return dao.findUsersMatching(searchText, searchOn).size();
	}

	public List<User> findUsersMatching(String searchText, String searchOn,
			Space space) {
		return dao.findUsersMatching(searchText, searchOn, space);
	}

	public List<User> findUsersMatching(String searchText, String searchOn,
			int start, int count) {
		List<User> usersMatching = dao.findUsersMatching(searchText, searchOn);
		
		return usersMatching.subList(start, start + count);
	}

	public List<User> findUsersForSpace(long spaceId) {
		return dao.findUsersForSpace(spaceId);
	}

	public List<User> findUsersInOrganizations(List<Organization> orgs) {
		return dao.findUsersInOrganizations(orgs);
	}

	public List<UserSpaceRole> findUserRolesForSpace(long spaceId) {
		return dao.findUserRolesForSpace(spaceId);
	}

	// public List<User> findUsersWithRoleForSpace(long spaceId, String roleKey)
	// {
	// return dao.findUsersWithRoleForSpace(spaceId, roleKey);
	// }

	public List<User> findUsersWithRoleForSpace(SpaceRole spaceRole) {
		return dao.findUsersWithRoleForSpace(spaceRole);
	}

	public List<User> findUsersForUser(User user) {
		Set<Space> spaces = user.getSpaces();
		if (spaces.size() == 0) {
			// this will happen when a user has no spaces allocated
			return Collections.emptyList();
		}
		// must be a better way to make this unique?
		List<User> users = dao.findUsersForSpaceSet(spaces);
		Set<User> userSet = new LinkedHashSet<User>(users);
		return new ArrayList<User>(userSet);
	}

	public List<User> findUnallocatedUsersForSpace(long spaceId) {
		List<User> users = findAllUsers();
		Space space = loadSpace(spaceId);
		// Set<String> roleKeys = space.getMetadata().getRolesMap().keySet();
		List<SpaceRole> spaceRolesSet = findSpaceRolesForSpace(space);
		List<UserSpaceRole> userSpaceRoles = findUserRolesForSpace(spaceId);
		List<User> unallocated = new ArrayList<User>();

		// spaces have multiple roles, find users that have not been
		// allocated all roles for the given space
		// for(User user : users) {
		// for(String roleKey : roleKeys) {
		// // UserSpaceRole usr = new UserSpaceRole(user, space, roleKey);
		// long spaceRoleId = Long.parseLong(roleKey);
		// UserSpaceRole usr = new UserSpaceRole(user,
		// loadSpaceRole(spaceRoleId));
		// if(!userSpaceRoles.contains(usr)) {
		// unallocated.add(user);
		// break;
		// }
		// }
		// }

		for (User user : users) {
			for (SpaceRole spaceRole : spaceRolesSet) {
				UserSpaceRole usr = new UserSpaceRole(user, spaceRole);
				if (!userSpaceRoles.contains(usr)) {
					unallocated.add(user);
					break;
				}// if
			}// for
		}// for

		return unallocated;
	}

	// --------------------------------------------------------------------------------------------

	public int loadCountOfHistoryInvolvingUser(User user) {
		return dao.loadCountOfHistoryInvolvingUser(user);
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public CountsHolder loadCountsForUser(User user) {
		return dao.loadCountsForUser(user);
	}

	// --------------------------------------------------------------------------------------------

	public Counts loadCountsForUserSpace(User user, Space space) {
		return dao.loadCountsForUserSpace(user, space);
	}

	@Override
    public List<Object[]>  selectLatestItemPerSpace(User user){
    	return dao.selectLatestItemPerSpace(user);
    }
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// public void storeUserSpaceRole(User user, Space space, String roleKey) {
	// //user.addSpaceWithRole(space, roleKey);
	// long id = Long.parseLong(roleKey);
	// //TODO Implement later
	// // user.addSpaceWithRole(new SpaceRole(id, space));
	// dao.storeUser(user);
	// }

	public void storeUserSpaceRole(User user, SpaceRole spaceRole) {
		try {
			user.addSpaceRole(spaceRole);
		} catch (LazyInitializationException lazyInitializationException) {
			user.addSpaceRole(loadSpaceRole(spaceRole.getId()));
		}
		dao.storeUser(user);
	}

	// --------------------------------------------------------------------------------------------

	public void removeUserSpaceRole(UserSpaceRole userSpaceRole) {
		User user = userSpaceRole.getUser();
		user.removeSpaceRole(userSpaceRole.getSpaceRole());
		dao.removeUserSpaceRole(userSpaceRole);
		dao.storeUser(user);
	}

	// --------------------------------------------------------------------------------------------

	public UserSpaceRole loadUserSpaceRole(long id) {
		return dao.loadUserSpaceRole(id);
	}

	// --------------------------------------------------------------------------------------------

	public int bulkUpdateDeleteUserSpaceRolesForSpace(Space space) {
		return dao.bulkUpdateDeleteUserSpaceRolesForSpace(space);
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Space loadSpace(long id) {
		return dao.loadSpace(id);
	}
	@Override
	public Space loadSpaceForEditing(long id) {
		Space space = dao.loadSpace(id);
		Hibernate.initialize(space.getItemRenderingTemplates());
		return space;
	}

	public Space loadSpace(SpaceRole spaceRole) {
		return dao.loadSpace(spaceRole);
	}

	// ---------------------------------------------------------------------------------------------

	public Space loadSpace(String prefixCode) {
		List<Space> spaces = dao.findSpacesByPrefixCode(prefixCode);
		if (spaces.size() == 0) {
			return null;
		}
		return spaces.get(0);
	}

	// ---------------------------------------------------------------------------------------------

	public void storeUnpublishedSpace(Space space) {
		this.dao.save(space);
	}
	
	public Space storeSpace(Space space) {
		
		try{

			// i18n step 1
//			logger.info("space: "+space);
			space.setPublished(true);
			if(MapUtils.isNotEmpty(space.getNameTranslations()) 
					&& StringUtils.isNotBlank(space.getNameTranslations().get(this.getDefaultLocale()))){
				space.setName(space.getNameTranslations().get(this.getDefaultLocale()));
			}
//			if (space.getId() > 0 && space.getPublished()) { // Edit Space
				// Load space before save
				/*Space oldSpace = dao.loadSpace(space.getId());

				// Space Roles
				// -------------------------------------------------------------------------
				// Load Space Roles before save
				List<SpaceRole> oldSpaceRolesList = dao
						.findSpaceRolesForSpace(oldSpace);

				// Assign Current Space Roles Set (if any) to a List
				List<SpaceRole> spaceRolesList = new ArrayList<SpaceRole>();
				if (space.getSpaceRoles() != null) {
					spaceRolesList = new ArrayList<SpaceRole>(space.getSpaceRoles());
				}*/

				// Role Space Role Std Fields
				// ----------------------------------------------------------
				// Load Role Space Std Fields before save
//				List<RoleSpaceStdField> oldRoleSpaceStdFieldsList = dao
//						.findSpaceFieldsBySpace(oldSpace);

				// Assign Current Role Space Std Fields Set (if any) to a List
//				List<RoleSpaceStdField> roleSpaceStdFieldsSet = new ArrayList<RoleSpaceStdField>();
//				if (space.getRoleSpaceStdFields() != null) {
//					roleSpaceStdFieldsSet = new ArrayList<RoleSpaceStdField>(
//							space.getRoleSpaceStdFields());
//				}

				// Store new Space Roles
				// ---------------------------------------------------------------
				/*List<RoleSpaceStdField> roleSpaceStdFieldsList = new ArrayList<RoleSpaceStdField>();
				if (space.getSpaceRoles() != null) {
					for (SpaceRole spaceRole : space.getSpaceRoles()) {
						if (spaceRole.getRoleSpaceStdFields() != null) {
							for (RoleSpaceStdField roleSpaceStdField : spaceRole
									.getRoleSpaceStdFields()) {
								roleSpaceStdFieldsList.add(roleSpaceStdField);
							}
						}
						dao.storeSpaceRole(spaceRole);
					}
				}

				// Store Role Space Std Fields
				// ---------------------------------------------------------
				for (RoleSpaceStdField roleSpaceStdField : roleSpaceStdFieldsList) {
					dao.storeRoleSpaceStdField(roleSpaceStdField);
				}
				*/
				// Remove unused Role Space Std Fields
				// -------------------------------------------------
				// if (oldRoleSpaceStdFieldsList!=null){
				// for (RoleSpaceStdField roleSpaceStdField :
				// oldRoleSpaceStdFieldsList){
				// if (!roleSpaceStdFieldsSet.contains(roleSpaceStdField)){
				// dao.removeRoleSpaceStdField(roleSpaceStdField);
				// }//if
				// }//for
				// }//if

				// Remove unused Space Roles
				// -----------------------------------------------------------
//				if (oldSpaceRolesList != null) {
//					for (SpaceRole spaceRole : oldSpaceRolesList) {
//						if (!spaceRolesList.contains(spaceRole)
//								&& !spaceRole.getRoleType().equals(
//										RoleType.SPACE_ADMINISTRATOR)) {
//							removeSpaceRole(spaceRole);
//						}
//					}// for
//				}// if
				dao.saveOrUpdateTranslations(space);
				space = dao.storeSpace(space);
				space.getSpaceGroup();
//			} else {
//				
//				space  = dao.storeSpace(space);
	//
//				// Store Space Roles
//				// -------------------------------------------------------------------
//				logger.info("storing roles for new space: "+space.getSpaceRoles());
//				List<RoleSpaceStdField> roleSpaceStdFieldsList = new ArrayList<RoleSpaceStdField>();
//				if (space.getSpaceRoles() != null) {
//					for (SpaceRole spaceRole : space.getSpaceRoles()) {
//						if (spaceRole.getRoleSpaceStdFields() != null) {
//							for (RoleSpaceStdField roleSpaceStdField : spaceRole
//									.getRoleSpaceStdFields()) {
//								roleSpaceStdFieldsList.add(roleSpaceStdField);
//							}
//						}
//						dao.storeSpaceRole(spaceRole);
//					}
//				}
	//
//				// Set up and save "Space Administrator" space role
//				// SpaceRole spaceAdministrator = new SpaceRole(space,
//				// RoleType.SPACE_ADMINISTRATOR.getDescription(),
//				// RoleType.SPACE_ADMINISTRATOR);
//				// dao.storeSpaceRole(spaceAdministrator);
	//
//				// Store Role Space Std Fields
//				// ---------------------------------------------------------
//				for (RoleSpaceStdField roleSpaceStdField : roleSpaceStdFieldsList) {
//					dao.storeRoleSpaceStdField(roleSpaceStdField);
//				}
	//
//			}

			// i18n step 2
			// TODO: make sure both space translations and customattribute translations use the same resource key 

			//logger.info("Persisted space "+space.getId()+", updating translations: "+space.getTranslations());

			//logger.info("Persisted space, updated translations... ");
			List<Field> fields = space.getMetadata().getFieldList();
			//logger.info("Persisted space, iterating for attribute mappings... ");
			if(fields != null && fields.size() > 0){
				for(Field field : fields){
					if(field.getCustomAttribute() == null){
						field.setCustomAttribute(loadItemCustomAttribute(space, field.getName().getText()));
					}
					ItemFieldCustomAttribute attribute = field.getCustomAttribute();

					//logger.info("Persisted space, iterating field: "+field.getLabel()+", attribute: "+attribute);
					if(attribute != null){
						
						saveOrUpdate(field, attribute);
						if(StringUtils.isNotBlank(attribute.getMappingKey())){
							//logger.info("Field has mapping key: "+attribute.getMappingKey());
							// clone for assets
							AssetTypeCustomAttributeSearch attrSearch = new AssetTypeCustomAttributeSearch(1);
							attrSearch.setMappingKey(attribute.getMappingKey());
							//attrSearch.setFormType(attribute.getFormType());
							List<AssetTypeCustomAttribute> results = dao.findCustomAttributesMatching(attrSearch);
							if(CollectionUtils.isEmpty(results)){
								AssetTypeCustomAttribute assetTypeAttr = new AssetTypeCustomAttribute();
								assetTypeAttr.setName(attribute.getName());
								SpaceUtils.copy(this, space, field, assetTypeAttr);
								//logger.info("Creating asset attribute '"+field.getLabel()+"' for missing key: "+attribute.getMappingKey());
								storeCustomAttribute(assetTypeAttr);
							}
							else{
								//logger.info("Asset attribute exists for mapping key: "+attribute.getMappingKey());
							
							}
						}
					}
					else{
						//logger.info("No attribute for field: "+field.getLabel());
						
					}
				}
			}
			return space;
		
		}
		catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}


	public void store(CustomAttribute attribute) {
		saveOrUpdate(null, attribute);
	}
	
	private void saveOrUpdate(Field field, CustomAttribute attribute) {
/*
		attribute.setName(attribute
				.getNameTranslations().get(this.getDefaultLocale()));
			
		// clear options if needed
		if (!attribute.getFormType().equals(
				AssetTypeCustomAttribute.FORM_TYPE_SELECT)
				&& attribute.getAllowedLookupValues() != null) {
			// TODO: this throws LIE, implement a new calipsoService method
			// instead
			// assetTypeCustomAttribute.getAllowedLookupValues().clear();

		}
		dao.storeCustomAttribute(attribute);
		dao.saveOrUpdateTranslations(attribute);
		// update option translations
		if (attribute.getFormType().equals(
				AssetTypeCustomAttribute.FORM_TYPE_SELECT)
				&& CollectionUtils.isNotEmpty(attribute
						.getAllowedLookupValues())) {
			for (CustomAttributeLookupValue value : attribute
					.getAllowedLookupValues()) {
				this.storeLookupValue(value);
			}
		}
	*/
		if(attribute != null){
			if(MapUtils.isNotEmpty(attribute.getNameTranslations())){
				String name = attribute.getNameTranslations().get(this.getDefaultLocale());
				if(org.apache.commons.lang.StringUtils.isNotBlank(name)){
					attribute.setName(name);
				}
				//this.loadI18nStringResource(new I18nStringIdentifier(attribute.getNameTranslationResourceKey(), this.getDefaultLocale()));	
			}
			
		}
		else{
			if(logger.isDebugEnabled()){
				logger.debug("Cannot save a null attribute for field: "+field.getName().getText());
			}
		}
		
	}

	// ---------------------------------------------------------------------------------------------

	public Set<SpaceGroup> getSpaceGroupsForUser(Serializable userId) {
		return dao.getSpaceGroupsForUser(userId);
	}

	public List<Space> findAllSpaces() {
		return dao.findAllSpaces();
	}

	public List<Space> findSpacesWhereIdIn(List<Long> ids) {
		return dao.findSpacesWhereIdIn(ids);
	}

	public List<Space> findSpacesWhereGuestAllowed() {
		return dao.findSpacesWhereGuestAllowed();
	}

	public List<Space> findSpacesWhereAnonymousAllowed() {
		return dao.findSpacesWhereAnonymousAllowed();
	}

	public List<Space> findUnallocatedSpacesForUser(long userId) {
		List<Space> spaces = findAllSpaces();
		User user = loadUser(userId);
		Set<UserSpaceRole> usrs = user.getUserSpaceRoles();
		List<Space> unallocated = spaces;// new ArrayList<Space>();

		// spaces have multiple roles, find spaces that have roles
		// not yet assigned to the user
		// for(Space space : spaces) {
		// for(String roleKey : space.getMetadata().getRolesMap().keySet()) {
		// //UserSpaceRole usr = new UserSpaceRole(user, space, roleKey);
		// long spaceRoleId = Long.parseLong(roleKey);
		// SpaceRole spaceRole = loadSpaceRole(spaceRoleId);
		// UserSpaceRole usr = new UserSpaceRole(user, spaceRole);
		// if(!usrs.contains(usr)) {
		// unallocated.add(space);
		// break;
		// }
		// }
		// }

		List<UserSpaceRole> userSpaceRolesList;
		if (user.getUserSpaceRoles() != null) {
			userSpaceRolesList = new ArrayList<UserSpaceRole>(
					user.getUserSpaceRoles());
		} else {
			userSpaceRolesList = new ArrayList<UserSpaceRole>();
		}

		// for(Space space : spaces) {
		// for (UserSpaceRole userSpaceRole : userSpaceRolesList){
		// if (userSpaceRole.getSpaceRole().getSpace().equals(space)){
		// unallocated.remove(space);
		// }
		// }
		// }

		return unallocated;
	}

	public void removeSpace(Space space) {

		// Delete related User Space Roles
		dao.bulkUpdateDeleteUserSpaceRolesForSpace(space);

		// Delete related Space Roles
		dao.bulkUpdateDeleteSpaceRolesForSpace(space);

		// Delete related Items
		dao.bulkUpdateDeleteItemsForSpace(space);

		// Remove space
		dao.removeSpace(space);
	}

	// ==========================================================================

//	public void storeMetadata(Metadata metadata) {
//		dao.storeMetadata(metadata);
//	}
//
//	public Metadata loadMetadata(long id) {
//		return dao.loadMetadata(id);
//	}

	// ==========================================================================

	public Map<String, String> loadAllConfig() {
		List<Config> list = dao.findAllConfig();
		Map<String, String> allConfig = new HashMap<String, String>(list.size());
		for (Config c : list) {
			allConfig.put(c.getParam(), c.getValue());
		}
		return allConfig;
	}

	// TODO must be some nice generic way to do this
	// TODO remove all this clutter and replace with multilayered commons config
	public void storeConfig(Config config) {
		dao.storeConfig(config);
		if (config.isMailConfig()) {
			initMailSender(loadAllConfig());
		} else if (config.isLocaleConfig()) {
			initDefaultLocale(config.getValue());
		} else if (config.isAttachmentConfig()) {
			initAttachmentMaxSize(config.getValue());
		} else if (config.isSessionTimeoutConfig()) {
			initSessionTimeout(config.getValue());
		} else if (config.isPageSizeConfig()) {
			initPageSize(config.getValue());
		}
	}

	public String loadConfig(String param) {
		Config config = dao.loadConfig(param);
		if (config == null) {
			return null;
		}
		String value = config.getValue();
		if (value == null || value.trim().equals("")) {
			return null;
		}
		return value;
	}

	// ========================================================

	public void rebuildIndexes() {
		clearIndexes();
		List<AbstractItem> items = dao.findAllItems();
		for (AbstractItem item : items) {
			indexer.index(item);
		}
	}

	public List<AbstractItem> findAllItems() {
		// this returns all Item and all History records for indexing
		return dao.findAllItems();
	}

	public void clearIndexes() {
		File file = new File(calipsoHome + "/indexes");
		for (File f : file.listFiles()) {
			f.delete();
		}
	}

	public void index(AbstractItem item) {
		indexer.index(item);
	}

	public boolean validateTextSearchQuery(String text) {
		return indexSearcher.validateQuery(text);
	}

	// ==========================================================================

	public void executeHourlyTask() {
		executeNotificationsForItemsDueIn24Hours();
	}

	/* configured to be called every five minutes */
	public void executePollingTask() {
		// check for incoming email
		executeReadMailForNewItems();
	}

	// ==========================================================================

	public String getReleaseVersion() {
		return releaseVersion;
	}

	public String getReleaseTimestamp() {
		return releaseTimestamp;
	}

	// Saved Search============================
	public void storeSavedSearch(SavedSearch savedSearch) {
		dao.storeSavedSearch(savedSearch);
	}

	public SavedSearch loadSavedSearch(long id) {
		return dao.loadSavedSearch(id);
	}

	public void removeSavedSearch(SavedSearch savedSearch) {
		dao.removeSavedSearch(savedSearch);
	}

	public List<SavedSearch> findSavedSearches(User user) {
		return dao.findSavedSearches(user);
	}

	public List<SavedSearch> findVisibleSearches(User user) {
		return dao.findVisibleSearches(user);
	}

	public List<SavedSearch> findSavedSearches(User user, Space space) {
		return dao.findSavedSearches(user, space);
	}

	// Asset Management ========================================================

	// /////////////////////
	// Custom Attributes //
	// /////////////////////
	/**
	 * @deprecated use store(CustomAttribute) instead
	 */
	public void storeCustomAttribute(
			AssetTypeCustomAttribute assetTypeCustomAttribute) {
		assetTypeCustomAttribute.setName(assetTypeCustomAttribute
				.getNameTranslations().get(this.getDefaultLocale()));
		// setup default validation expression if needed
		if (assetTypeCustomAttribute.getValidationExpression() == null) {
			// set it to RexexpValidator.No_VALIDATION by default
			assetTypeCustomAttribute.setValidationExpression(this
					.loadValidationExpression(1));
		}
		// clear options if needed
//		if (!assetTypeCustomAttribute.getFormType().equals(
//				AssetTypeCustomAttribute.FORM_TYPE_SELECT)
//				&& assetTypeCustomAttribute.getAllowedLookupValues() != null) {
			// TODO: this throws LIE, implement a new calipsoService method
			// instead
			// assetTypeCustomAttribute.getAllowedLookupValues().clear();

//		}
		dao.storeCustomAttribute(assetTypeCustomAttribute);
		logger.info("Saving custom attribute translations: "+assetTypeCustomAttribute.getTranslations());
		dao.saveOrUpdateTranslations(assetTypeCustomAttribute);
		// update option translations
		if (CollectionUtils.isNotEmpty(assetTypeCustomAttribute
						.getAllowedLookupValues())) {
			for (CustomAttributeLookupValue value : assetTypeCustomAttribute
					.getAllowedLookupValues()) {
				this.storeLookupValue(value);
			}
		}
	}

	public List<AssetTypeCustomAttribute> findAllCustomAttributes() {
		return dao.findAllCustomAttributes();
	}

	public AssetTypeCustomAttribute loadAssetTypeCustomAttribute(Long id) {
		return dao.loadAssetTypeCustomAttribute(id);
	}
	
	public String getPrintTemplateTextForAsset(Long assetId){
		String templateText = null;
		AssetType assetType = dao.loadAssetTypeByAssetId(assetId);
		if(assetType != null){
			RenderingTemplate tmpl = assetType.getPrintingTemplate();
			if(tmpl != null){
				templateText = tmpl.getTemplateText();
			}
		}
		return templateText;
	}

	public CustomAttributeLookupValue loadCustomAttributeLookupValue(
			long id) {
		return dao.loadCustomAttributeLookupValue(id);
	}

	public CustomAttributeLookupValue loadCustomAttributeLookupValue(CustomAttribute attr, String name){
		return dao.loadCustomAttributeLookupValue(attr, name);
	}

	public void removeLookupValue(
			CustomAttributeLookupValue lookupValue) {
		dao.removeLookupValue(lookupValue);
	}

	public void storeLookupValue(CustomAttributeLookupValue lookupValue) {
		lookupValue.setName(lookupValue.getNameTranslations().get(
				this.getDefaultLocale()));
		dao.storeLookupValue(lookupValue);
		dao.saveOrUpdateTranslations(lookupValue);
	}

	public void storeCustomAttributeLookupValues(
			List<CustomAttributeLookupValue> lookupValues) {
		if (lookupValues != null) {
			for (CustomAttributeLookupValue lookupValue : lookupValues) {
				this.storeLookupValue(lookupValue);
			}
		}
	}

	public List<AssetTypeCustomAttribute> findCustomAttributesMatching(
			final AssetTypeCustomAttributeSearch searchCustomAttribute) {
		return dao.findCustomAttributesMatching(searchCustomAttribute);
	}

	public List<AssetType> findAllAssetTypesByCustomAttribute(
			AssetTypeCustomAttribute attribute) {
		return dao.findAllAssetTypesByCustomAttribute(attribute);
	}

	public int loadCountAssetsForCustomAttribute(
			AssetTypeCustomAttribute customAttribute) {
		return dao.loadCountAssetsForCustomAttribute(customAttribute);
	}

	public int loadCountForAssetTypeAndCustomAttribute(AssetType assetType,
			CustomAttribute customAttribute) {
		return dao.loadCountForAssetTypeAndCustomAttribute(assetType,
				customAttribute);
	}

	public int loadCountForCustomAttributeLookupValue(
			CustomAttributeLookupValue lookupValue) {
		return dao.loadCountForCustomAttributeLookupValue(lookupValue);
	}

	public void removeCustomAttribute(CustomAttribute customAttribute) {
		dao.removeCustomAttribute(customAttribute);
	}

	// ///////////////
	// Asset types //
	// ///////////////

	public List<AssetType> findAssetTypesMatching(
			AssetTypeSearch assetTypeSearch) {
		return dao.findAssetTypesMatching(assetTypeSearch);
	}

	public List<AssetType> findAssetTypesWhereIdIn(List<Long> ids) {
		return dao.findAssetTypesWhereIdIn(ids);
	}

	public List<AssetType> findAllAssetTypes() {
		return dao.findAllAssetTypes();
	}

	public List<AssetType> findAllAssetTypesForSpace(Space space) {
		return dao.findAllAssetTypesForSpace(space);
	}

	public void storeAssetType(AssetType assetType) {
		assetType.setName(assetType.getNameTranslations().get(
				this.getDefaultLocale()));
		dao.merge(assetType);
		dao.saveOrUpdateTranslations(assetType);
	}// storeAssetType

	public List<AssetTypeCustomAttribute> findAllAssetTypeCustomAttributesByAssetType(
			AssetType assetType) {
		return dao.findAllAssetTypeCustomAttributesByAssetType(assetType);
	}

	public AssetType loadAssetType(long id) {
		return dao.loadAssetType(id);
	}
	
	public ItemFieldCustomAttribute loadItemCustomAttribute(Space space, String fieldName){
		return dao.loadItemCustomAttribute(space, fieldName);
	}


	/**
	 * Delete ItemFieldCustomAttribute by item and field name
	 */
	@Override
	public void removeItemCustomAttribute(Space space, String fieldName){
		dao.deleteItemCustomAttribute(space, fieldName);
	}
	public AssetType loadAssetTypeByName(String name) {
		return dao.loadAssetTypeByName(name);
	}

	public List<CustomAttributeLookupValue> findLookupValuesByCustomAttribute(
			CustomAttribute attr) {
		//logger.info("findLookupValuesByCustomAttribute, attr: "+attr);
		return dao.findLookupValuesByCustomAttribute(attr);
	}
	@Override
	public List<CustomAttributeLookupValue> findActiveLookupValuesByCustomAttribute(
			CustomAttribute attr) {
		//logger.info("findLookupValuesByCustomAttribute, attr: "+attr);
		return dao.findActiveLookupValuesByCustomAttribute(attr);
	}
	
	
	public List<CustomAttributeLookupValue> findAllLookupValuesByCustomAttribute(
			CustomAttribute attr) {
		//logger.info("findLookupValuesByCustomAttribute, attr: "+attr);
		return dao.findAllLookupValuesByCustomAttribute(attr);
	}
	/**
	 * 
	 * Get a list of all lookup values matching the level for a given CustomAttribute. Only applies to Tree Options. May return null or an empty List
	 */
	public List<CustomAttributeLookupValue> findLookupValuesByCustomAttribute(
			CustomAttribute attr, int level) {
		return dao.findLookupValuesByCustomAttribute(attr, level);
	}

	public int findLookupValuesCountByCustomAttribute(
			CustomAttribute attr) {
		return dao.findLookupValuesByCustomAttribute(attr).size();
	}

	public List<CustomAttributeLookupValue> findLookupValuesByCustomAttribute(
			CustomAttribute attr, int start, int count) {
		List<CustomAttributeLookupValue> l = dao
				.findLookupValuesByCustomAttribute(attr).subList(start,
						start + count);
		return l;
	}

	public List<CustomAttributeLookupValue> findLookupValues(Space space, String fieldName){
		ItemFieldCustomAttribute attr = this.dao.loadItemCustomAttribute(space, fieldName);
		List<CustomAttributeLookupValue> lookupValues = attr.getAllowedLookupValues();
		
		return lookupValues;
	}
	// //////////
	// Assets //
	// //////////

	public void storeAsset(Asset asset) {
		updateDates(asset, new Date());
		dao.saveOrUpdate(asset);
	}// storeAsset

	public Asset loadAsset(Long id) {
		return dao.loadAsset(id);
	}// loadAssset
	public Asset loadAssetAttributes(Asset asset){
		dao.refresh(asset);
		dao.preloadCustomAttributeEntityValuesForAsset(asset);
		return asset;
	}
	public Asset loadAssetWithAttributes(Long id) {
		return dao.loadAssetWithAttributes(id);
	}// loadAssset

	public List<Asset> findAssetsMatching(AssetSearch assetSearch,
			final boolean fetchCustomAttributes) {
		return dao.findAssetsMatching(assetSearch, fetchCustomAttributes);
	}// findAssetsMatching

	public int findAssetsMatchingCount(AssetSearch assetSearch,
			final boolean fetchCustomAttributes) {
		List<Asset> list = dao.findAssetsMatching(assetSearch,
				fetchCustomAttributes);
		if (list == null) {
			return 0;
		}
		return list.size();
	}

	public List<Asset> findAssetsMatchingSubList(AssetSearch assetSearch,
			final boolean fetchCustomAttributes, int start, int count) {
		List<Asset> list = dao.findAssetsMatching(assetSearch,
				fetchCustomAttributes);
		if (list == null) {
			return null;
		}
		return list.subList(start, start + count);
	}

	public List<Object> findCustomAttributeValueMatching(AssetSearch assetSearch) {
		return dao.findCustomAttributeValueMatching(assetSearch);
	}

	public List<Asset> findAllAssetsByItem(Item item) {
		return dao.findAllAssetsByItem(item);
	}

	public List<Asset> findAllAssetsBySpace(Space space) {
		return dao.findAllAssetsBySpace(space);
	}

	// //////////
	// Fields //
	// /////////

	public List<RoleSpaceStdField> findSpaceFieldsBySpaceRole(
			SpaceRole spaceRole) {
		return dao.findSpaceFieldsBySpaceRole(spaceRole);
	}

	public List<RoleSpaceStdField> findSpaceFieldsBySpaceandRoleType(
			SpaceRole spaceRole) {
		return dao.findSpaceFieldsBySpaceandRoleType(spaceRole);
	}

	public List<RoleSpaceStdField> findSpaceFieldsBySpace(Space space) {
		return dao.findSpaceFieldsBySpace(space);
	}

	public RoleSpaceStdField loadRoleSpaceField(long id) {
		return dao.loadRoleSpaceField(id);
	}

	public void storeRoleSpaceStdField(RoleSpaceStdField roleSpaceStdField) {
		dao.storeRoleSpaceStdField(roleSpaceStdField);
	}

	public List<StdField> loadAllStdFields() {
		List<StdField> list = new ArrayList<StdField>();

		for (StdField.Field field : StdField.Field.values()) {
			list.add(new StdField(field));
		}

		return list;
	}

	public List<StdField> findStdFieldsByType(StdFieldType fieldType) {
		List<StdField> list = new ArrayList<StdField>();

		for (StdField.Field field : StdField.Field.values()) {
			if (field.getFieldType().equals(fieldType)) {
				list.add(new StdField(field));
			}// if
		}// for

		return list;
	}// findStdFieldsByType

	public List<RoleSpaceStdField> findSpaceFieldsForUser(User user) {
		List<UserSpaceRole> userSpaceRoles = null;
		List<RoleSpaceStdField> roleSpaceStdFieldlist = new ArrayList<RoleSpaceStdField>();

		try {
			userSpaceRoles = user.getRegularUserSpaceRoles();
		} catch (LazyInitializationException lazyInitializationException) {
			userSpaceRoles = loadUser(user.getId()).getRegularUserSpaceRoles();
		}
		if (userSpaceRoles != null) {
			for (UserSpaceRole userSpaceRole : userSpaceRoles) {
				roleSpaceStdFieldlist
						.addAll(findSpaceFieldsBySpaceRole(userSpaceRole
								.getSpaceRole()));
			}
		}
		return roleSpaceStdFieldlist;
	}

	public void removeRoleSpaceStdField(RoleSpaceStdField roleSpaceStdField) {
		dao.removeRoleSpaceStdField(roleSpaceStdField);
	}

	public int bulkUpdateDeleteRoleSpaceStdFieldsForSpaceRole(
			SpaceRole spaceRole) {
		return dao.bulkUpdateDeleteRoleSpaceStdFieldsForSpaceRole(spaceRole);
	}

	// Organization
	// -------------------------------------------------------------------------------
	public void storeOrganization(Organization organization) {
		dao.storeOrganization(organization);
	}

	public Organization loadOrganization(long id) {
		return dao.loadOrganization(id);
	}

	public List<Organization> findOrganizationsMatching(
			OrganizationSearch organizationSearch) {
		return dao.findOrganizationsMatching(organizationSearch);
	}

	// Country
	// ----------------------------------------------------------------------------------
	public void storeCountry(Country country) {
		dao.storeCountry(country);
	}

	public Country loadCountry(String id) {
		return dao.loadCountry(id);
	}

	public List<Country> findAllCountries() {
		return dao.findAllCountries();
	}

	// Validation Expression
	// -------------------------------------------------------------------------------
	public void storeValidationExpression(
			ValidationExpression validationExpression) {
		dao.storeValidationExpression(validationExpression);
		dao.saveOrUpdateTranslations(validationExpression);
	}

	public ValidationExpression loadValidationExpression(long id) {
		return dao.loadValidationExpression(id);
	}

	public List<ValidationExpression> findAllValidationExpressions() {
		return dao.findAllValidationExpressions();
	}

	public List<ValidationExpression> findValidationExpressionsMatching(
			ValidationExpressionSearch validationExpressionSearch) {
		return dao
				.findValidationExpressionsMatching(validationExpressionSearch);
	}

	public ValidationExpression findValidationExpressionByName(String name) {
		return dao.findValidationExpressionByName(name);
	}

	// Miscellaneous
	// ------------------------------------------------------------------------------

	public void sendPassword(String emailAddress) {
		List<User> users = this.findUsersMatching(emailAddress, "email");

		if (users != null && users.size() == 1) {
			storeUser(users.get(0), generatePassword(), true);
		}
	}

	@Override
	public void send(User recipient, String subject, String messageBody) {
		send(recipient.getEmail(), subject, messageBody);
	}

	@Override
	public void send(String email, String subject, String messageBody) {
		mailSender.send(email, subject, messageBody, null, true);
	}
	@Override
	public void send(String email, String subject, String messageBody, boolean html) {
		mailSender.send(email, subject, messageBody, null, html);
	}
	@Override
	public void send(String email, String subject, String messageBody, Map<String, DataSource> attachments, boolean html) {
		mailSender.send(email, subject, messageBody, attachments, html);
	}
	@Override
	public void send(String email, String subject, String body, Map<String, DataSource> attachments) {
		mailSender.send(email, subject, body, attachments);
	}
	// ---------------------------------------------------------------------------------------------

	public void sendPassword(User user) {
		storeUser(user, generatePassword(), true);
	}

	// ---------------------------------------------------------------------------------------------

	/**
	 * Send notifications for Items due within 24 hours
	 */
	private void executeNotificationsForItemsDueIn24Hours() {
		Iterator<Item> items = dao.findItemsDueIn24Hours();
		while (items.hasNext()) {
			Item item = items.next();
			mailSender.sendDueIn24HoursNotifications(item);
			item.setSentDueToNotifications(true);
			dao.update(item);
		}
	}
	
	@Override
    public boolean isAllowedToCreateNewItem(User user, Space space){
    	boolean allowed = false;
    	if(user.getId() == 0){
    		allowed = space.getItemVisibility().equals(Space.ITEMS_INVISIBLE_TO_ANONYMOUS_REPORTERS) 
    				|| space.getItemVisibility().equals(Space.ITEMS_VISIBLE_TO_ANONYMOUS_USERS);  
    	}
    	else if(this.loadUser(user.getId()).getPermittedTransitions(loadSpace(space.getId()), State.NEW).size() > 0){
   			allowed = true;
    	}
    	return allowed;
    }

	public void executeReadMailForNewItems() {
		Map<String, String> config = loadAllConfig();
		String mailedTicketSignificant = config
				.get("mailedItem.mailSubjectPrefix");
		String mailServer = config.get("mailedItem.mailServer");
		String userName = config.get("mailedItem.mailUserNameAccount");
		String password = config.get("mailedItem.mailUserNamePassword");
		if (mailServer != null && userName != null && password != null) {

			MailReader mailReader = new MailReader(mailServer, userName,
					password, mailedTicketSignificant);
			List<MailedItem> mailedItemsList = mailReader.getMailedItemsList();
			if (mailedItemsList != null && mailedItemsList.size() > 0) {
				for (MailedItem mailedItem : mailedItemsList) {
					Item item = new Item();
					Space space = null;
					boolean itemIsAbleToBeCreated = true;

					// Set Space
					if (mailedItem.getSpace() != null
							&& mailedItem.getSpace() != "") {
						space = loadSpace(mailedItem.getSpace());
						if (space != null) {
							item.setSpace(space);
						}// if
						else {
							itemIsAbleToBeCreated = false;
						}// else
					}// if
					else {
						// TODO Handle No Space given
						logger.error("No Space given for emailed Item");
						itemIsAbleToBeCreated = false;
					}// else

					// Set logged by account
					if (mailedItem.getLoggedByAccount() != null
							&& mailedItem.getLoggedByAccount().trim() != "") {
						List<User> userList = findUsersMatching(
								mailedItem.getLoggedByAccount(), "email");
						if (userList != null && userList.size() > 0) {
							item.setLoggedBy(userList.get(0));
						}// if
						else {
							// TODO Handle no user found matching this email
							// address
							logger.error("no user found matching this email address : "
									+ mailedItem.getLoggedByAccount());
							itemIsAbleToBeCreated = false;
						}
					}// if
					else {
						// TODO Handle no LoggedBy given
						logger.error("no LoggedBy given");
						itemIsAbleToBeCreated = false;
					}// else

					// Set Time Stamp
					if (mailedItem.getTimeStamp() != null) {
						item.setTimeStamp(mailedItem.getTimeStamp());
					}// if
					else {
						// TODO Handle no Time Stamp given
						logger.error("no Time Stamp given");
						itemIsAbleToBeCreated = false;
					}// else

					// Set Summary
					if (mailedItem.getSummary() != null
							&& mailedItem.getSummary() != null) {
						item.setSummary(mailedItem.getSummary());
					}// if
					else {
						// TODO Handle no Summary given
						logger.error("no Summary given");
						itemIsAbleToBeCreated = false;
					}// else

					// Set Details
					if (mailedItem.getDetail() != null
							&& mailedItem.getDetail() != null) {
						item.setDetail(mailedItem.getDetail());
						item.setDetail(XmlUtils.removeXss(XmlUtils.escapeHTML(
								item.getDetail(), true)));
					}// if
					else {
						// TODO Handle no Detail given
						logger.error("no Detail given");
						itemIsAbleToBeCreated = false;
					}// else

					// Set State = OPEN
					item.setStatus(State.OPEN);

					// Set assignable space = space
					item.setAssignableSpaces(space);

					// Set notification list.
					if (space != null) {
						List<UserSpaceRole> userSpaceRoles = findUserRolesForSpace(space
								.getId());
						List<ItemUser> itemUsersList = UserUtils
								.convertToItemUserList(userSpaceRoles);

						item.setItemUsers(new LinkedHashSet<ItemUser>(
								itemUsersList));
					}// if

					if (itemIsAbleToBeCreated) {
						// TODO: integrate email attachments
						storeItem(item, null);
						mailSender.send(item);
					}// if
				}// for
			}
		} else {
			logger.warn("MailReader was called to scan for emailed Items but it has not yet been configured. Required config properties are mailedItem.mailServer, mailedItem.mailUserNameAccount and mailedItem.mailUserNamePassword");
		}

	}// executeReadMailForNewItems

	// ---------------------------------------------------------------------------------------------

	public int getRecordsPerPage() {
		return this.pageSize;
	}

	// Space Roles
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public void storeSpaceRole(SpaceRole spaceRole) {
		dao.storeSpaceRole(spaceRole);
	}

	// ---------------------------------------------------------------------------------------------

	public SpaceRole loadSpaceRole(long spaceRoleId) {
		return dao.loadSpaceRole(spaceRoleId);
	}

	// ---------------------------------------------------------------------------------------------

	public List<SpaceRole> findSpaceRolesForSpace(Space space) {
		return dao.findSpaceRolesForSpace(space);
	}

	// ---------------------------------------------------------------------------------------------

	public void removeSpaceRole(SpaceRole spaceRole) {
		dao.bulkUpdateDeleteRoleSpaceStdFieldsForSpaceRole(spaceRole);
		dao.removeSpaceRole(spaceRole);
	}

	// ---------------------------------------------------------------------------------------------

	public int bulkUpdateDeleteSpaceRolesForSpace(Space space) {
		return dao.bulkUpdateDeleteSpaceRolesForSpace(space);
	}

	// ---------------------------------------------------------------------------------------------

	public List<SpaceRole> findAvailableSpaceRolesForUser(Space space, User user) {
		List<SpaceRole> spaceRoles = dao.findSpaceRolesForSpace(space);

		List<UserSpaceRole> userSpaceRolesList;
		if (user.getUserSpaceRoles() != null) {
			userSpaceRolesList = new ArrayList<UserSpaceRole>(
					user.getUserSpaceRoles());
		} else {
			userSpaceRolesList = new ArrayList<UserSpaceRole>();
		}
		/*
		 * List<SpaceRole> globalAdministrators =
		 * findSpaceRolesForSpaceAndRoleType(null,
		 * RoleType.ADMINISTRATOR.getId()); SpaceRole admin =
		 * loadAdministrator(); if (admin!=null){ spaceRoles.add(admin); }
		 */
		// Remove already assigned space roles
		for (UserSpaceRole userSpaceRole : userSpaceRolesList) {
			if (spaceRoles.contains(userSpaceRole.getSpaceRole())) {
				spaceRoles.remove(userSpaceRole.getSpaceRole());
			}// if
		}// for

		// Remove guest space Role
		for (SpaceRole spaceRole : spaceRoles) {
			if (spaceRole.getRoleType().equals(RoleType.GUEST)
					|| spaceRole.getRoleType().equals(RoleType.ANONYMOUS)
					|| spaceRole.getRoleType().equals(RoleType.ADMINISTRATOR)) {
				spaceRoles.remove(spaceRole);
				break;
			}
		}

		return spaceRoles;

	}

	// ---------------------------------------------------------------------------------------------

	public List<SpaceRole> findSpaceRolesForSpaceAndRoleType(Space space,
			int roleTypeId) {
		return dao.findSpaceRolesForSpaceAndRoleType(space, roleTypeId);
	}

	// ---------------------------------------------------------------------------------------------

	public SpaceRole loadAdministrator() {
		// It must be one just one with id = 1 (the first record on table)
		// But it can happens that some one has truncate refill the table
		List<SpaceRole> globalAdministrators = findSpaceRolesForSpaceAndRoleType(
				null, RoleType.ADMINISTRATOR.getId());
		if (globalAdministrators != null && globalAdministrators.size() > 0) {
			return globalAdministrators.get(0);
		}// if

		return null;
	}

	// ---------------------------------------------------------------------------------------------

	public SpaceRole loadSpaceAdministrator(Space space) {
		List<SpaceRole> spaceAdministrators = findSpaceRolesForSpaceAndRoleType(
				space, RoleType.SPACE_ADMINISTRATOR.getId());
		if (spaceAdministrators != null && spaceAdministrators.size() > 0) {
			return spaceAdministrators.get(0);
		}

		return null;
	}

	// ---------------------------------------------------------------------------------------------

	public PageDictionary loadPageDictionary(int id) {
		return dao.loadPageDictionary(id);
	}

	// ---------------------------------------------------------------------------------------------

	public PageDictionary loadPageDictionary(String className) {
		return dao.loadPageDictionary(className);
	}

	// ---------------------------------------------------------------------------------------------

	public InforamaDocument loadInforamaDocument(int id) {
		return dao.loadInforamaDocument(id);
	}

	// ---------------------------------------------------------------------------------------------

	public void storeInforamaDocument(InforamaDocument inforamaDocument) {
		dao.storeInforamaDocument(inforamaDocument);
	}

	// ---------------------------------------------------------------------------------------------

	public void removeInforamaDocument(InforamaDocument inforamaDocument) {
		List<InforamaDocumentParameter> documentParameters = findInforamaDocumentParametersForDocument(inforamaDocument);
		if (documentParameters != null) {
			for (InforamaDocumentParameter inforamaDocumentParameter : documentParameters) {
				removeInforamaDocumentParameter(inforamaDocumentParameter);
			}// for
		}// if
		dao.removeInforamaDocument(inforamaDocument);
	}

	// ---------------------------------------------------------------------------------------------

	public List<InforamaDocument> findAllInforamaDocuments() {
		return dao.findAllInforamaDocuments();
	}

	// ---------------------------------------------------------------------------------------------

	public void storeInforamaDocumentParameters(
			InforamaDocument inforamaDocument) {
		if (inforamaDocument.getParameters() != null) {
			for (InforamaDocumentParameter inforamaDocumentParameter : inforamaDocument
					.getParameters()) {
				storeInforamaDocumentParameter(inforamaDocumentParameter);
			}// for
		}// if
	}

	// ---------------------------------------------------------------------------------------------

	public List<InforamaDocument> findInforamaDocumentsForClassNameAndSpace(
			String className, Space space) {
		return dao.findInforamaDocumentsForClassNameAndSpace(className, space);
	}

	// ---------------------------------------------------------------------------------------------

	public InforamaDocumentParameter loadInforamaDocumentParameter(int id) {
		return dao.loadInforamaDocumentParameter(id);
	}

	// ---------------------------------------------------------------------------------------------

	public void storeInforamaDocumentParameter(
			InforamaDocumentParameter inforamaDocumentParameter) {
		dao.storeInforamaDocumentParameter(inforamaDocumentParameter);
	}

	// ---------------------------------------------------------------------------------------------

	public List<InforamaDocumentParameter> findInforamaDocumentParametersForDocument(
			InforamaDocument inforamaDocument) {
		return dao.findInforamaDocumentParametersForDocument(inforamaDocument);
	}

	// ---------------------------------------------------------------------------------------------

	public void removeInforamaDocumentParameter(
			InforamaDocumentParameter inforamaDocumentParameter) {
		dao.removeInforamaDocumentParameter(inforamaDocumentParameter);
	}

	// ---------------------------------------------------------------------------------------------

	public PageInforamaDocument loadPageInforamaDocument(int id) {
		return dao.loadPageInforamaDocument(id);
	}

	// ---------------------------------------------------------------------------------------------

	public List<PageInforamaDocument> findPageInforamaDocumentForClassName(
			String className, Space space) {
		return dao.findPageInforamaDocumentForClassName(className, space);
	}

	// ---------------------------------------------------------------------------------------------

	public List<PageDictionary> findPageDictionaryMatching(
			PageDictionarySearch pageDictionarySearch) {
		return dao.findPageDictionaryMatching(pageDictionarySearch);
	}

	// ---------------------------------------------------------------------------------------------

	public void storePageDictionary(PageDictionary pageDictionary) {
		dao.storePageDictionary(pageDictionary);
	}

	// ---------------------------------------------------------------------------------------------

	public void removePageDictionary(PageDictionary pageDictionary) {
		dao.removePageDictionary(pageDictionary);
	}

	// ---------------------------------------------------------------------------------------------

	public List<InforamaDocument> findInforamaDocumentMatching(
			InforamaDocumentSearch inforamaDocumentSearch) {
		return dao.findInforamaDocumentMatching(inforamaDocumentSearch);
	}

	// ---------------------------------------------------------------------------------------------

	public List<InforamaDocumentParameter> findInforamaDocumentParameterMatching(
			InforamaDocumentParameterSearch inforamaDocumentParameterSearch) {
		return dao
				.findInforamaDocumentParameterMatching(inforamaDocumentParameterSearch);
	}

	// ---------------------------------------------------------------------------------------------

	public List<PageInforamaDocument> findPageInforamaDocumentMatching(
			PageInforamaDocumentSearch pageInforamaDocumentSearch) {
		return dao.findPageInforamaDocumentMatching(pageInforamaDocumentSearch);
	}

	// ---------------------------------------------------------------------------------------------

	public void storePageInforamaDocument(
			PageInforamaDocument pageInforamaDocument) {
		dao.storePageInforamaDocument(pageInforamaDocument);
	}

	// ---------------------------------------------------------------------------------------------

	public List<InforamaDocument> findInforamaDocumentsForSpace(Space space) {
		return dao.findInforamaDocumentsForSpace(space);
	}

	// ---------------------------------------------------------------------------------------------

	public List<Space> findSpacesForInforamaDocument(
			InforamaDocument inforamaDocument) {
		return dao.findSpacesForInforamaDocument(inforamaDocument);
	}

	// ---------------------------------------------------------------------------------------------

	public int loadCountSpacesForInforamaDocument(
			InforamaDocument inforamaDocument) {
		return dao.loadCountSpacesForInforamaDocument(inforamaDocument);
	}

	/**
	 * Returns all other Spaces of which the Assets are visible for the given
	 * Space
	 * 
	 * @see gr.abiss.calipso.CalipsoService#getVisibleAssetSpacesForSpace(gr.abiss.calipso.domain.Space)
	 */
	public Collection<Space> getVisibleAssetSpacesForSpace(Space space) {
		// TODO Auto-generated method stub
		return dao.getVisibleAssetSpacesForSpace(space);
	}

	/**
	 * Returns all other Spaces of which the Assets are visible for the given
	 * Space
	 * 
	 * @see gr.abiss.calipso.CalipsoService#getVisibleAssetSpacesForSpace(gr.abiss.calipso.domain.Space)
	 */

	public Collection<Asset> getVisibleAssetsForSpace(Space space) {
		// TODO Auto-generated method stub
		return dao.getVisibleAssetsForSpace(space);
	}

	/**
	 * @see gr.abiss.calipso.CalipsoService#loadI18nStringResource(gr.abiss.calipso.domain.I18nStringIdentifier)
	 */
	public I18nStringResource loadI18nStringResource(I18nStringIdentifier id) {
		return dao.loadI18nStringResource(id);
	}

	@Override
	public List<Field> getEditableFieldList(Item item, User user) {
		Item persistedItem = this.loadItem(item.getId());
		return persistedItem.getEditableFieldList(this.loadUser(user.getId()));
	}
	/**
	 * this method must be removed.
	 * @deprecated
	 */
	@Override
	public List<Field> getEditableFieldList(Item item) {
		User user = item.getLoggedBy();
		if(user == null){
			user = item.getAssignedTo();
		}
		return getEditableFieldList(item, user);
	}

	@Override
	public String getFontsDirPath() {
		return getCalipsoHome()+File.separator+"fonts";
	}
	@Override
	public String getResourcesDirPath() {
		return getCalipsoHome()+File.separator+"resources";
	}

	@Override
	public boolean isEmailSendingConfigured() {
		return loadConfig("mail.server.host")!=null /*&& loadConfig("mail.server.username")!=null*/;
	}

	@Override
	public List<ItemRenderingTemplate> getItemRenderingTemplates(Space space) {
		return dao.getItemRenderingTemplates(space);
	}


	@Override
	public ItemRenderingTemplate getItemRenderingTemplateForUser(User user, Integer itemStatus, Long spaceId){
		return dao.getItemRenderingTemplateForUser(user, itemStatus, spaceId);
	}

	@Override
	public Map<String, RenderingTemplate> loadSpaceRoleTemplates(Long id) {
		return id!=null && id.longValue() > 0?((SpaceRole)dao.get(SpaceRole.class, id)).getItemRenderingTemplates():new HashMap<String, RenderingTemplate>();
	}
	@Override
	public Set<User> loadSpaceGroupAdmins(Long id) {
		return ((SpaceGroup)dao.get(SpaceGroup.class, id)).getAdmins();
	}
}