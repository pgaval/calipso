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

package gr.abiss.calipso.util;


import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.domain.Attachment;
import gr.abiss.calipso.domain.Country;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.History;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.ItemItem;
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.Metadata;
import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.State;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.exception.CalipsoSecurityException;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.ComponentUtils;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.types.CommandlineJava.SysProperties;
import org.apache.wicket.Component;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.HtmlUtils;

/**
 * Utilities to convert an Item into HTML etc.
 * The getAsHtml() routine is used to diplay an item - within a tag lib for JSP
 * And we are able to re-use this to send HTML e-mail etc.
 */
public final class ItemUtils {    
	private static final Logger logger =  Logger.getLogger(ItemUtils.class);
	// maximum character for summary (when creating editing an item)
	public static final int MAX_SUMMARY_CHARACTERS = 128;
	
    /** 
     * does not do HTML escaping. converts tabs to spaces and converts leading 
     * spaces (for each multi-line) to as many '&nbsp;' sequences as required
     */
    public static String fixWhiteSpace(String text) {
        if(text == null) {
            return "";
        }
        //String temp = HtmlUtils.htmlEscape(text);
        //String temp = XmlUtils.transformToHTMLSubset(text);
	String temp = text;
        BufferedReader reader = new BufferedReader(new StringReader(temp));
        StringBuilder sb = new StringBuilder();
        String s;
        boolean first = true;
        try {
            while((s = reader.readLine()) != null) {                          
                if(first) {
                    first = false;
                } else {
                    sb.append("<br/>");
                }
                if(s.startsWith(" ")) {
                    int i;
                    for(i = 0; i < s.length(); i++) {                    
                        if(s.charAt(i) == ' ') {
                            sb.append("&nbsp;");
                        } else {
                            break;
                        }                        
                    }
                    s = s.substring(i);
                }                
                sb.append(s);
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString().replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    }    
    
    public static String fmt(String key, MessageSource messageSource, Locale locale) {
        return fmt(key, null, messageSource, locale);
    }

    public static String fmt(String key, Object[] args, MessageSource messageSource, Locale locale) {
        try {
            return messageSource.getMessage(key, args, locale);
        } catch (Exception e) {
            return "???item_view." + key + "???";
        }
    }
    
    public static String getAsHtml(Item item, MessageSource messageSource, Locale locale) {
        return getAsHtml(item, null, null, messageSource, locale);
    }
    
    public static String getAsHtml(Item item, HttpServletRequest request, HttpServletResponse response) {
        Locale locale = RequestContextUtils.getLocale(request);
        MessageSource messageSource = RequestContextUtils.getWebApplicationContext(request);        
        return getAsHtml(item, request, response, messageSource, locale);
    }    
    
    private static String getAsHtml(Item item, HttpServletRequest request, HttpServletResponse response, 
            MessageSource ms, Locale loc) {        
        
        boolean isWeb = request != null && response != null;             
        
        String tableStyle = " class='calipsoService'";
        String tdStyle = "";
        String thStyle = "";
        String altStyle = " class='alt'";
        String labelStyle = " class='label'";
        
        if (!isWeb) {
            // inline CSS so that HTML mail works across most mail-reader clients
            String tdCommonStyle = "border: 1px solid black";
            tableStyle = " class='calipsoService' style='border-collapse: collapse; font-family: Arial; font-size: 75%'";
            tdStyle = " style='" + tdCommonStyle + "'";
            thStyle = " style='" + tdCommonStyle + "; background: #ededed'";
            altStyle = " style='background: #fffcdd'";
            labelStyle = " style='" + tdCommonStyle + "; background: #ededed; font-weight: bold; text-align: right'";
        }

        StringBuffer sb = new StringBuffer();
        sb.append("<table width='100%'" + tableStyle + ">");
        sb.append("<tr" + altStyle + ">");
        sb.append("  <td" + labelStyle + ">" + fmt("item_view.id", ms, loc) + "</td>");
        sb.append("  <td" + tdStyle + ">" + item.getUniqueRefId() + "</td>");
        sb.append("  <td" + labelStyle + ">" + fmt("item_view.relatedItems", ms, loc) + "</td>");
        sb.append("  <td colspan='3'" + tdStyle + ">");
        if (item.getRelatedItems() != null || item.getRelatingItems() != null) {
            String flowUrlParam = null;
            String flowUrl = null;
            if (isWeb) {
                flowUrlParam = "_flowExecutionKey=" + request.getAttribute("flowExecutionKey");
                flowUrl = "/flow?" + flowUrlParam;
            }
            if (item.getRelatedItems() != null) {
                // ItemViewForm itemViewForm = null;
                if (isWeb) {
                    // itemViewForm = (ItemViewForm) request.getAttribute("itemViewForm");
                    sb.append("<input type='hidden' name='_removeRelated'/>");
                }
                for(ItemItem itemItem : item.getRelatedItems()) {                    
                    String refId = itemItem.getRelatedItem().getUniqueRefId();
                    if (isWeb) {
                        String checked = "";
                        Set<Long> set = null; // itemViewForm.getRemoveRelated();
                        if (set != null && set.contains(itemItem.getId())) {
                            checked = " checked='true'";
                        }
                        String url = flowUrl + "&_eventId=viewRelated&itemId=" + itemItem.getRelatedItem().getId();
                        refId = "<a href='" + response.encodeURL(request.getContextPath() + url) + "'>" + refId + "</a>"
                                + "<input type='checkbox' name='removeRelated' value='" 
                                + itemItem.getId() + "' title='" + fmt("item_view.remove", ms, loc) + "'" + checked + "/>";
                    }
                    sb.append(fmt(itemItem.getRelationText(), ms, loc) + " " + refId + " ");
                }
            }
            if (item.getRelatingItems() != null) {
                for(ItemItem itemItem : item.getRelatingItems()) {
                    String refId = itemItem.getItem().getUniqueRefId();
                    if (isWeb) {
                        String url = flowUrl + "&_eventId=viewRelated&itemId=" + itemItem.getItem().getId();
                        refId = "<a href='" + response.encodeURL(request.getContextPath() + url) + "'>" + refId + "</a>";
                    }
                    sb.append(refId + " " + fmt(itemItem.getRelationText() + "This", ms, loc) + ". ");
                }
            }
        }
        sb.append("  </td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("  <td width='15%'" + labelStyle + ">" + fmt("item_view.status", ms, loc) + "</td>");
        sb.append("  <td" + tdStyle + ">" + item.getStatusValue() + "</td>");
        sb.append("  <td" + labelStyle + ">" + fmt("item_view.loggedBy", ms, loc) + "</td>");
        sb.append("  <td" + tdStyle + ">" + item.getLoggedBy().getName() + "</td>");
        sb.append("  <td" + labelStyle + ">" + fmt("item_view.assignedTo", ms, loc) + "</td>");
        sb.append("  <td width='15%'" + tdStyle + ">" + (item.getAssignedTo() == null ? "" : item.getAssignedTo().getName()) + "</td>");
        sb.append("</tr>");
        sb.append("<tr" + altStyle + ">");
        sb.append("  <td" + labelStyle + ">" + fmt("item_view.summary", ms, loc) + "</td>");
        sb.append("  <td colspan='5'" + tdStyle + ">" + HtmlUtils.htmlEscape(item.getSummary()) + "</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("  <td valign='top'" + labelStyle + ">" + fmt("item_view.detail", ms, loc) + "</td>");
        sb.append("  <td colspan='5'" + tdStyle + ">" + fixWhiteSpace(item.getDetail()) + "</td>");
        sb.append("</tr>");
        
        int row = 0;
        Map<Field.Name, Field> fields = item.getSpace().getMetadata().getFields();
        for(Field.Name fieldName : item.getSpace().getMetadata().getFieldOrder()) {
            Field field = fields.get(fieldName);
            sb.append("<tr" + (row % 2 == 0 ? altStyle : "") + ">");
            sb.append("  <td" + labelStyle + ">" + field.getLabel() + "</td>");
            sb.append("  <td colspan='5'" + tdStyle + ">" + item.getCustomValue(field) + "</td>");
            sb.append("</tr>");
            row++;
        }
        sb.append("</table>");
        
        //=========================== ASSETS ===================================

        if (item.getSpace().isAssetEnabled()){
        	sb.append("<br/>&nbsp;<b" + tableStyle + ">" + fmt("item_view.assets", ms, loc) + "</b>");
        	sb.append("<table width='100%'" + tableStyle + ">");

        	//Header
        	sb.append("<tr>");
            sb.append("  <th" + thStyle + ">" + fmt("item_view.assetType", ms, loc) + "</th><th" + thStyle + ">" 
            			+ fmt("item_view.inventoryCode", ms, loc) + "</th>" + "<th" + thStyle + ">" 
            			+ fmt("item_view.supportStartDate", ms, loc) + "</th><th" + thStyle + ">" 
            			+ fmt("item_view.supportEndDate", ms, loc) + "</th><th" + thStyle + ">" + fmt("item_view.Attributes", ms, loc) 
            			+ "</th>");
        	sb.append("</tr>");
        	
        	//Detail
        	if (item.getAssets() != null){
        		row = 1;
        		for (Asset asset : item.getAssets()){
        			 sb.append("<tr valign='top'" + (row % 2 == 0 ? altStyle : "") + ">");
        			 	sb.append("  <td" + tdStyle + ">" + fmt(asset.getAssetType().getNameTranslationResourceKey(), ms, loc) + "</td>"); //Asset Type
        			 	sb.append("  <td" + tdStyle + ">" + asset.getInventoryCode() + "</td>"); // Inventory Code
        			 	sb.append("  <td" + tdStyle + ">" + asset.getSupportStartDate() + "</td>"); // Support Start Date
        			 	sb.append("  <td" + tdStyle + ">" + asset.getSupportEndDate() + "</td>"); // Support End Date
        			 	sb.append("  <td" + tdStyle + ">" + "" + "</td>"); // Custom Attributes
                     sb.append("</tr>");
                     row++;
        		}//for
        	}//if
        		
        	sb.append("</table>");
        }//if
        
        //=========================== HISTORY ==================================

        sb.append("<br/>&nbsp;<b" + tableStyle + ">" + fmt("item_view.history", ms, loc) + "</b>");
        sb.append("<table width='100%'" + tableStyle + ">");
        sb.append("<tr>");
        sb.append("  <th" + thStyle + ">" + fmt("item_view.loggedBy", ms, loc) + "</th><th" + thStyle + ">" + fmt("item_view.status", ms, loc) + "</th>"
                + "<th" + thStyle + ">" + fmt("item_view.assignedTo", ms, loc) + "</th><th" + thStyle + ">" + fmt("item_view.comment", ms, loc) + "</th><th" + thStyle + ">" + fmt("item_view.timeStamp", ms, loc) + "</th>");
        List<Field> editable = item.getSpace().getMetadata().getEditableFields();
        for(Field field : editable) {
            sb.append("<th" + thStyle + ">" + field.getLabel() + "</th>");
        }
        sb.append("</tr>");
        
        if (item.getHistory() != null) {
            row = 1;
            for(History history : item.getHistory()) {
                sb.append("<tr valign='top'" + (row % 2 == 0 ? altStyle : "") + ">");
                sb.append("  <td" + tdStyle + ">" + history.getLoggedBy().getName() + "</td>");
                sb.append("  <td" + tdStyle + ">" + history.getStatusValue() +"</td>");
                sb.append("  <td" + tdStyle + ">" + (history.getAssignedTo() == null ? "" : history.getAssignedTo().getName()) + "</td>");
                sb.append("  <td" + tdStyle + ">");
                Set<Attachment> attachments = history.getAttachments();
                if(attachments != null && attachments.size() > 0){
                	for(Attachment attachment : attachments) {
                        if (request != null && response != null) {
                            String href = response.encodeURL(request.getContextPath() + "/app/attachments/" + attachment.getFileName() +"?filePrefix=" + attachment.getFilePrefix());
                            sb.append("<a target='_blank' href='" + href + "'>" + attachment.getFileName() + "</a>&nbsp;");
                        } else {
                            sb.append("(attachment:&nbsp;" + attachment.getFileName() + ")&nbsp;<br />");
                        }
                    }
                }
                sb.append(fixWhiteSpace(history.getComment()));
                sb.append("  </td>");
                sb.append("  <td" + tdStyle + ">" + history.getTimeStamp() + "</td>");
                for(Field field : editable) {
                    sb.append("<td" + tdStyle + ">" + history.getCustomValue(field) + "</td>");
                }
                sb.append("</tr>");
                row++;
            }
        }
        sb.append("</table>");
        return sb.toString();
    }
    
    public static Document getAsXml(Item item) {
        Document d = XmlUtils.getNewDocument("space");
        Element root = d.getRootElement();
//        root.addAttribute("refId", item.getRefId());
        root.addAttribute("refId", item.getUniqueRefId());
        if (item.getRelatedItems() != null && item.getRelatedItems().size() > 0) {
            Element relatedItems = root.addElement("relatedItems");
            for(ItemItem itemItem : item.getRelatedItems()) {
                Element relatedItem = relatedItems.addElement("relatedItem");
//                relatedItem.addAttribute("refId", itemItem.getItem().getRefId());
                relatedItem.addAttribute("refId", itemItem.getItem().getUniqueRefId());
            }           
        }
        if (item.getRelatingItems() != null && item.getRelatingItems().size() > 0) {
            Element relatingItems = root.addElement("relatingItems");
            for(ItemItem itemItem : item.getRelatingItems()) {
                Element relatingItem = relatingItems.addElement("relatingItem");
//                relatingItem.addAttribute("refId", itemItem.getItem().getRefId());
                relatingItem.addAttribute("refId", itemItem.getItem().getUniqueRefId());
            }
        }
        if (item.getSummary() != null) {
            root.addElement("summary").addText(item.getSummary());
        }
        if (item.getDetail() != null) {
            root.addElement("detail").addText(item.getDetail());
        }
        Element loggedBy = root.addElement("loggedBy");
        loggedBy.addAttribute("userId", item.getLoggedBy().getId() + "");
        loggedBy.addText(item.getLoggedBy().getName());
        if (item.getAssignedTo() != null) {
            Element assignedTo = root.addElement("assignedTo");
            assignedTo.addAttribute("userId", item.getAssignedTo().getId() + "");
            assignedTo.addText(item.getAssignedTo().getName());
        }
        return d;
    }

    //---------------------------------------------------------------

    public static Double calcTotalIdleTime(){

    	return null;
    }//calcTotalIdleTime

    //---------------------------------------------------------------

    /**
     * Calculates the total response time for item (ticket) in question.
     * The calculation is based on item history.
     * 
     * @param item The item (ticket) in question
     * 
     * @return If the the Status is "closed", the time between creation and closing
     * 			otherwise, the time between creation and last entry 
     *  
     * */
    public static Double calcTotalResponseTime(Item item){

    	Double totalResponseTime = new Double(0.0);
    	History lastHistoryEntry = null;

    	if (item.getHistory()!=null){
//			//Get first history entry
//			for (History historyEntry : item.getHistory()){
//				firstHistoryEntry = historyEntry;
//				break;
//			}//for

			//Status is closed
    		if (item.getStatus()==State.CLOSED){
    			// Get history entry with status "CLOSED" and exit
    			// history can contains more entries in case of
    			// adding of comments after item closing
    			for (History historyEntry : item.getHistory()){
    				if (historyEntry.getStatus()!=null && historyEntry.getStatus()==State.CLOSED){
    					lastHistoryEntry = historyEntry;
    					break;
    				}//if
    			}//for

    		}//if
    		else{
    			lastHistoryEntry = item.getLatestHistory();
    		}//else
    	}//if

    	if (lastHistoryEntry!=null){
    		DateTime firstEntry = new DateTime(DateUtils.formatTimeStamp(item.getTimeStamp()));
    		DateTime lastEntry = new DateTime(DateUtils.formatTimeStamp(lastHistoryEntry.getTimeStamp()));
    		
    		totalResponseTime = Double.valueOf(Long.toString(DateTime.diff(firstEntry, lastEntry).inSeconds()));
    	}
    	
    	return totalResponseTime;
    	
    }//calcTotalResponseTime

    //---------------------------------------------------------------

    /**
     * Calculates the total open time for item (ticket) in question.
     * The calculation is based on item history.
     * 
     * @param item  The item (ticket) in question
     * 
     * @return If the the Status is "closed", the time between creation and closing
     * 			otherwise, the time between creation and now.
     *    
     * */
    public static Double calcTotalOpenTime(Item item){
    	

    	return null;
    }//calcTotalOpenTime

    //---------------------------------------------------------------

    public static Long calcTimeFromCreationToFirstReply(Item item){
    	Long timeFromCreationToFirstReply = new Long(0);
    	
    	DateTime firstEntry = new DateTime(DateUtils.formatTimeStamp(item.getTimeStamp()));
    	DateTime lastEntry = new DateTime(Calendar.getInstance().getTime());
    	
    	if (item.getHistory()!=null && item.getHistory().size()>1){
    		for (History historyEntry : item.getHistory()){
    			if (!item.getLoggedBy().equals(historyEntry.getLoggedBy())){
    				lastEntry = new DateTime(historyEntry.getTimeStamp());
    				break;
    			}//if
    		}//for
    	}//if
    	
    	timeFromCreationToFirstReply = DateTime.diff(firstEntry, lastEntry).inSeconds();
    	
    	return timeFromCreationToFirstReply;
    	
    }//calcTimeFromCreationToFirstReply

    //---------------------------------------------------------------

    public static Long calcTimeFromCreationToClose(Item item){
    	Long timeFromCreationToClose = new Long(0);

    	DateTime firstEntry = new DateTime(DateUtils.formatTimeStamp(item.getTimeStamp()));
    	DateTime lastEntry = new DateTime(Calendar.getInstance().getTime());

    	if (item.getHistory()!=null && item.getHistory().size()>1){

    		for (History historyEntry : item.getHistory()){
    			if (historyEntry.getStatus()!=null && historyEntry.getStatus() == State.CLOSED){
    				lastEntry = new DateTime(DateUtils.formatTimeStamp(historyEntry.getTimeStamp()));
    			}//if
    		}//for

    	}//if

    	timeFromCreationToClose = DateTime.diff(firstEntry, lastEntry).inSeconds();

    	return timeFromCreationToClose;
    }//calcTimeFromCreationToClose

    //---------------------------------------------------------------

    public static String formatEffort(Object effortInSeconds, String daysLabel, String hoursLabel, String minutesLabel){
    	StringBuilder effort = new StringBuilder("");

		try{
			if (new Double(effortInSeconds.toString()) >=60){
				DateTime dtEffort = DateTime.fromSeconds(new Double(effortInSeconds.toString()));
				
				effort.append(dtEffort.get_day()).append(" ").append(daysLabel)
				.append(" ").append(dtEffort.get_hours()).append(" ").append(hoursLabel)
				.append(" ").append(dtEffort.get_minutes()).append(" ").append(minutesLabel);
			}//if
		}//try
		catch (Exception e){
			throw new RuntimeException(e);
		}//catch
		
		return effort.toString();
	}//formatEffort

    public static ItemSearch getItemSearch(User user, PageParameters params, Component comp, CalipsoService calipso) throws CalipsoSecurityException {
        long spaceId = params.get("s").toLong(-1);        
        CalipsoService calipsoService = ComponentUtils.getCalipso(comp);
        
        ItemSearch itemSearch = null;
        if(spaceId > 0) {            
            Space space = calipsoService.loadSpace(spaceId);
            if(!user.isAllocatedToSpace(space.getId())) {
                throw new CalipsoSecurityException("User not allocated to space: " + spaceId + " in URL: " + params);
            }
            itemSearch = new ItemSearch(space, user, comp, calipso);            
        } else {
            itemSearch = new ItemSearch(user, comp);
        }
        itemSearch.initFromPageParameters(params, user, calipsoService);
        return itemSearch;        
    }
    

	public static void initItemFields(CalipsoService calipsoService, Item item, Asset asset) {
		initItemFields(calipsoService, item, asset, false, null);
	}

	/**
	 * 
	 * @param calipsoService
	 * @param item
	 * @param asset
	 * @param recursive whether to recursively process nested assets to populate item fields
	 * @param processedAssets the assets that have already been processed
	 */
	public static void initItemFields(CalipsoService calipsoService, Item item, Asset asset, boolean recursive, Set<Asset> processedAssets) {
		if(recursive){
			if(processedAssets == null){
				processedAssets = new HashSet<Asset>();
			}
			processedAssets.add(asset);
		}
		asset = calipsoService.loadAssetWithAttributes(asset.getId());
		Metadata metadata = item.getSpace().getMetadata();
		if(MapUtils.isNotEmpty(asset.getCustomAttributes())){
			for(Entry<AssetTypeCustomAttribute,String> entry : asset.getCustomAttributes().entrySet()){
				AssetTypeCustomAttribute assetTypeAttr = entry.getKey();
				String value = entry.getValue();
				//logger.debug("Attempting to copy Asset attribute "+assetTypeAttr.getName()+" to item, value: "+value);
				// if attribute holds an asset
				if(assetTypeAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_ASSET)){
					Asset assetValue = assetTypeAttr.getAssetValue();
					if(recursive && !processedAssets.contains(assetValue)){
						//logger.debug("Attribute '"+assetTypeAttr.getName()+"' is asset, recursing...");
						initItemFields(calipsoService, item, assetValue, recursive, processedAssets);
						//logger.debug("Attribute '"+assetTypeAttr.getName()+"' applied");
					}
					else{
						// TODO: add assets to item or what?
						//initAssetAttributeUsingItemAssets(item, asset, assetTypeAttr);
					}
				}
				else{
					Field field = metadata.getFieldByLabel(assetTypeAttr.getName());
					// TODO: hack, we are leaking toString somewhere.
					if(StringUtils.isBlank(value) || value.equals("null")){
						value = null;
					}
					if(field != null/* && StringUtils.isNotBlank(value)*/){
						// logger.debug("Found Item field matching the description: "+field.getLabel());
						initItemField(calipsoService, item, asset, field, assetTypeAttr, value);
					}
					else{
						// logger.debug("Could not find a matching Item field for asset attribute: "+assetTypeAttr.getName());
					}
				}
			}
		}
	}
	
	private static void initItemField(CalipsoService calipsoService,Item item, Asset asset, Field field, AssetTypeCustomAttribute assetTypeAttr, String value){
		String itemFieldName = field.getName().getText();
		try {
			//logger.debug("initItemField, field name: "+itemFieldName);
			String firstLetter = itemFieldName.substring(0, 1);
			String remainingPart = itemFieldName.substring(1, itemFieldName.length());
			Method itemFieldGetter = Item.class.getMethod("get" + firstLetter.toUpperCase() + remainingPart);
			Class itemFieldType = itemFieldGetter.getReturnType();
			Method itemFieldSetter = Item.class.getMethod("set" + firstLetter.toUpperCase() + remainingPart, itemFieldType);
			
			// Integer/DropDown
			if(itemFieldType.equals(Integer.class)/*field.getFieldType().equals(Field.FIELD_TYPE_AUTOSUGGEST)
					|| field.getFieldType().equals(Field.FIELD_TYPE_DROPDOWN)
					|| field.getFieldType().equals(Field.FIELD_TYPE_DROPDOWN_HIERARCHICAL)*/){
				if(value == null){
					itemFieldSetter.invoke(item, (Integer) null);
				}
				else{
					boolean found = false;
					//logger.debug("Item field '"+field.getName().getText()+"' is of type 'options', iterating Asset attribute lookup values (options) for a deeper match");
					CustomAttributeLookupValue assetLookupValue = calipsoService.loadCustomAttributeLookupValue(NumberUtils.createLong(value).longValue());

					//logger.info("assetLookupValue: '"+assetLookupValue);
					//logger.debug("Item field '"+field.getName().getText()+"' is of type 'options', iterating Asset attribute lookup values (options) for a deeper match on name: "+assetLookupValue.getName()+", showOrder: "+assetLookupValue.getShowOrder());
					// we need to find a matching showOrder in this one
					List<CustomAttributeLookupValue> itemLookupValues =  calipsoService.findLookupValues(item.getSpace(), field.getName().getText());
					if(assetLookupValue != null && CollectionUtils.isNotEmpty(itemLookupValues)){
						for(CustomAttributeLookupValue lookupValue : itemLookupValues){
							//logger.debug("Checking lookup value with value:'"+lookupValue);
							if(lookupValue.getShowOrder()  == assetLookupValue.getShowOrder()){
								itemFieldSetter.invoke(item, NumberUtils.createInteger(lookupValue.getId()+""));
								//logger.info("Found a match, result ID: "+itemFieldGetter.invoke(item));
								found = true;
								break;
							}
						}
					}else{
						logger.warn("No lookup values found for attribute "+assetTypeAttr.getName());
					}
					// this should work for simple numbers
					if(!found){
						// logger.debug("No option matched, adding a simple number instead");
						itemFieldSetter.invoke(item, new Integer(value));
					}
				}
			}
			// Long
			else if(itemFieldType.equals(Long.class)){
				if(value == null){
					itemFieldSetter.invoke(item, (Long) null);
				}
				else{
					itemFieldSetter.invoke(item, new Long(value));
				}
			}
			// Decimal
			else if(itemFieldType.equals(Double.class)){
				if(value == null){
					itemFieldSetter.invoke(item, (Double) null);
				}
				else{
					itemFieldSetter.invoke(item, new Double(value));
				}
			}
			// String
			else if(itemFieldType.equals(String.class)){
				itemFieldSetter.invoke(item, value);
			}
			// Date
			else if(itemFieldType.equals(Date.class)){
				if(value == null){
					itemFieldSetter.invoke(item, (Date) null);
				}
				else{
					itemFieldSetter.invoke(item, DateUtils.convert(value));
				}
			}
			// Organization
			else if(itemFieldType.equals(Organization.class)){
				if(value == null){
					itemFieldSetter.invoke(item, (Organization) null);
				}
				else{
					itemFieldSetter.invoke(item, calipsoService.loadOrganization(new Long(value)));
				}
			}
			// User
			else if(itemFieldType.equals(User.class)){
				if(value == null){
					itemFieldSetter.invoke(item, (User) null);
				}
				else{
					itemFieldSetter.invoke(item, calipsoService.loadUser(new Long(value)));
				}
			}
			// Country
			else if(itemFieldType.equals(Country.class)){
				if(value == null){
					itemFieldSetter.invoke(item, (Country) null);
				}
				else{
					itemFieldSetter.invoke(item, calipsoService.loadCountry(value));
				}
			}
			// Asset
			else if(itemFieldType.equals(Asset.class) && NumberUtils.isDigits(value)){
				if(value == null){
					itemFieldSetter.invoke(item, (Asset) null);
				}
				else{
					item.addAsset(calipsoService.loadAsset(new Long(value)));
				}
			}
			else{
				logger.warn("Could not convert asset attribute '"+assetTypeAttr.getName()+"' value ("+value+") to item field: "+itemFieldName);
			}
			//logger.info("Returning with Item "+assetTypeAttr.getName()+": "+itemFieldGetter.invoke(item));
		} catch (Exception e) {
			throw new RuntimeException("Could not convert asset attribute '"+assetTypeAttr.getName()+"' value ("+value+") to item field: "+itemFieldName, e);
		}
	}
}