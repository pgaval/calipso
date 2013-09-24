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

import gr.abiss.calipso.util.HumanTime;
import gr.abiss.calipso.util.ItemUtils;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

/**
 * This object represents a generic item which can be an issue, defect, task etc.
 * some logic for field accessors and conversion of keys to display values 
 * is contained in the AbstractItem class
 */
public class Item extends AbstractItem implements IAttachmentOwner {

	private static final long serialVersionUID = 1L;

	private Integer type;
    private Space space;
    private long sequenceNum;
    private boolean sentDueToNotifications = false;

	private Date stateDueTo;
	private Set<History> history;
    private Set<Item> children;
    private Set<Asset> assets;
    private Set<Attachment> attachments = new HashSet<Attachment>();
    
    public Item(){
    	
    }
    
    /**
	 * @return the stateDueTo
	 */
	public Date getStateDueTo() {
		return stateDueTo;
	}

	/**
	 * @param stateDueTo the stateDueTo to set
	 */
	public void setStateDueTo(Date stateDueTo) {
		this.stateDueTo = stateDueTo;
	}


    // should be ideally in form backing object but for convenience
    private String editReason;

    @Override
    public String getRefId() {
        return getSpace().getPrefixCode() + "-" + sequenceNum;
    }    
    
    @Override
	public String getUniqueRefId(){
    	return getSpace().getPrefixCode() + "-" + getId() + "-" + sequenceNum;
    }
    
    public Map<Integer, String> getPermittedTransitions(User user) {
        return user.getPermittedTransitions(space, getStatus());        
    }
    
    public List<Field> getEditableFieldList(User user) {
        return user.getEditableFieldList(space, getStatus());
    }
    
    public List<Field> getViewableFieldList(User user) {
        return user.getViewableFieldList(space, getStatus());
    }
    
    public Map<Field.Name, Field> getViewableFieldMap(User user) {
        return user.getViewableFieldMap(space, getStatus());
    }
    
    public void add(History h) {
        if (this.history == null) {
            this.history = new LinkedHashSet<History>();
        }
        h.setParent(this);
        this.history.add(h);
//        this.setTotalIdleTime(ItemUtils.calcTotalIdleTime());
//        this.setTotalOpenTime(ItemUtils.calcTotalOpenTime());
//        this.setTotalResponseTime(ItemUtils.calcTotalResponseTime());
    }
    
    /**
	 * @see gr.abiss.calipso.domain.IAttachmentOwner#addAttachment(gr.abiss.calipso.domain.Attachment)
	 */
    @Override
	public boolean addAttachment(Attachment attachment){
    	if(this.attachments == null){
    		this.attachments = new HashSet<Attachment>();
    	}
    	attachment.setItem(this);
    	return this.attachments.add(attachment);
    }
    
    /**
	 * @see gr.abiss.calipso.domain.IAttachmentOwner#addAttachments(java.util.Collection)
	 */
    @Override
	public boolean addAttachments(Collection<Attachment> attachments){
    	boolean ok = true;
    	if(attachments != null){
    		for(Attachment attachment : attachments){
    			ok = ok && this.addAttachment(attachment);
    		}
    	}
    	return ok;
    }

    /**
	 * @see gr.abiss.calipso.domain.IAttachmentOwner#removeAttachments(java.util.Collection)
	 */
    @Override
	public void removeAttachments(Collection<Attachment> attachments){
    	if(attachments != null && attachments.size() > 0){
			for(Attachment attachmentToRemove: attachments){
				this.removeAttachment(attachmentToRemove);
			}
		}
    }
    /**
	 * @see gr.abiss.calipso.domain.IAttachmentOwner#removeAttachment(gr.abiss.calipso.domain.Attachment)
	 */
    @Override
	public void removeAttachment(Attachment attachment){
    	attachment.setItem(null);
    	this.attachments.remove(attachment);
    }
    
    @Override
	public void removeAttachmentsByFileName(String fileName){
    	if(fileName != null && this.attachments != null){
    		Set<Attachment> attachmentsToRemove = new HashSet<Attachment>();
    		for(Attachment attachment : this.attachments){
    			if(fileName.equals(fileName)){
    				attachmentsToRemove.add(attachment);
    			}
    		}
    		// remove outside the loop to avoid
    		// ConcurrentModificationException
			this.removeAttachments(attachmentsToRemove);
    	}
    }
    
    
    public void addRelated(Item relatedItem, int relationType) {
        if (getRelatedItems() == null) {
            setRelatedItems(new LinkedHashSet<ItemItem>());
        }
        ItemItem itemItem = new ItemItem(this, relatedItem, relationType);        
        getRelatedItems().add(itemItem);
    }    
    
    /**
     * Lucene DocumentCreator implementation
     */
    @Override
	public Document createDocument() {
        Document d = new Document();        
        d.add(new org.apache.lucene.document.Field("id", getId() + "", Store.YES, Index.NO));            
        d.add(new org.apache.lucene.document.Field("type", "space", Store.YES, Index.NO));        
        StringBuffer sb = new StringBuffer();
        if (getSummary() != null) {
            sb.append(getSummary());
        }        
        if (getDetail() != null) {
            if (sb.length() > 0) {
                sb.append(" | ");
            }
            sb.append(getDetail());
        }
        d.add(new org.apache.lucene.document.Field("text", sb.toString(), Store.NO, Index.TOKENIZED));
        return d;
    }    
    
    public History getLatestHistory() {
        if (history == null) {
            return null;
        }
        History out = null;
        for(History h : history) {
            out = h;
        }
        return out;
    }       
    
    //===========================================================
    
    @Override
    public Space getSpace() {
        return space;
    }    
    
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setSpace(Space space) {
        this.space = space;
    }    
    
    public long getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(long sequenceNum) {
        this.sequenceNum = sequenceNum;
    }     

    public Set<History> getHistory() {
        return history;
    }

    public void setHistory(Set<History> history) {
        this.history = history;
    }

    public Set<Item> getChildren() {
        return children;
    }

    public void setChildren(Set<Item> children) {
        this.children = children;
    }

    @Override
	public Set<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
    }      

    public String getEditReason() {
        return editReason;
    }

    public void setEditReason(String editReason) {
        this.editReason = editReason;
    }   
    
    /**
	 * @return the sentDueToNotifications
	 */
	public boolean isSentDueToNotifications() {
		return sentDueToNotifications;
	}

	/**
	 * @param sentDueToNotifications the sentDueToNotifications to set
	 */
	public void setSentDueToNotifications(boolean sentNotifications) {
		this.sentDueToNotifications = sentNotifications;
	}
    
    @Override
	public String getDueToUserFriendly(Date now) {
		return new StringBuffer()
			.append(HumanTime.approximately(now, this.stateDueTo))
			.append(" / ")
			.append(HumanTime.approximately(now, this.getDueTo()))
			.toString();
	}
    

	@Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        sb.append("; type [").append(type);
        sb.append("]; space [").append(space);
        sb.append("]; sequenceNum [").append(sequenceNum);
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Parses a unique reference id and returns the item id.
     * Default value is 0.
     * @param uniqueRefId
     * @return itemId
     */
    public static long getItemIdFromUniqueRefId(String uniqueRefId){
		long itemId = 0;
		
		try{
			itemId = Long.parseLong(uniqueRefId); //Reference id is just a number
		}//try
		catch(Exception e){
			try{
				itemId = Long.parseLong(uniqueRefId.substring(uniqueRefId.indexOf("-")+1, uniqueRefId.lastIndexOf("-")));// Item id is between "-" 
			}//try
			catch(Exception e2){
				
			}//catch
		}//catch
		
		return itemId;
	}//getItemIdFromUniqueRefId

	/**
	 * @return the assets
	 */
	@Override
	public Set<Asset> getAssets() {
		return this.assets;
	}

	/**
	 * @param assets the assets to set
	 */
	@Override
	public void setAssets(Set<Asset> assets) {
		this.assets = assets;
	}
	
	/**
	 * @param assets the asset to set
	 */
	public void addAsset(Asset asset) {
		if(this.assets == null){
			this.assets = new HashSet<Asset>();
		}
		this.assets.add(asset);
	}

	/**
	 * @param asset The asset to remove
	 * */
	public void removeAsset(Asset asset){
		if(this.assets != null){
			this.assets.remove(asset);
		}//if
	}//removeAsset

	/**
	 *Removes all related assets of this item 
	 */
	
	public void removeAllAssets(){
		if (this.assets!=null){
			this.assets.clear();
		}//if
	}//removeAllAssets
	
	
	////////////////
	// Statistics //
	////////////////
	
	@Override
	public Double getTotalResponseTime() {
		if (this.history!=null){
			this.setTotalResponseTime(ItemUtils.calcTotalResponseTime(this));
		}

		return totalResponseTime;
	}


	@Override
	public Long getTimeFromCreationToFirstReply() {
		if (this.history!=null){
			this.setTimeFromCreationToFirstReply(ItemUtils.calcTimeFromCreationToFirstReply(this));
		}//if
		return timeFromCreationToFirstReply;
	}
	
	@Override
	public Long getTimeFromCreationToClose() {
		if (this.history!=null){
			this.setTimeFromCreationToClose(ItemUtils.calcTimeFromCreationToClose(this));
		}
		return timeFromCreationToClose;
	}
	
}
