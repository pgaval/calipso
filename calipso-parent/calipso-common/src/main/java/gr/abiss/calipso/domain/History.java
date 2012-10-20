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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import gr.abiss.calipso.util.ItemUtils;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

/**
 * Any updates to an Item (even a new insert) causes a snapshot of
 * the item to be stored in the History table.
 * In this way for each Item, a History view is available which
 * shows the diffs, who made changes and when, etc.
 */
public class History extends AbstractItem  implements IAttachmentOwner {
	/**
	 * Please make proper use of logging, see
	 * http://www.owasp.org/index.php/Category
	 * :Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(History.class);
    private Integer type;
    private String comment;
    private String htmlComment;
    private Integer actualEffort;
    private Set<Attachment> attachments;

    public History() {
        // zero arg constructor
    }
    
    /**
     * this is used a) when creating snapshot of item when inserting history
     * and b) to create snapshot of item when editing item in which case
     * the status, loggedBy and assignedTo fields are additionally tweaked
     */
    public History(Item item) {
        setStatus(item.getStatus());
        setSummary(item.getSummary());
        setDetail(item.getDetail());
        setHtmlDetail(item.getHtmlDetail());
        setLoggedBy(item.getLoggedBy());
        setAssignedTo(item.getAssignedTo());
        // setTimeStamp(item.getTimeStamp());
        setPlannedEffort(item.getPlannedEffort());
        //==========================
        for(Field.Name fieldName : Field.Name.values()) {
        	//logger.info("setting history "+fieldName+": "+item.getValue(fieldName));
            setValue(fieldName, item.getValue(fieldName));
        }
    }
    
    /**
     * Lucene DocumentCreator implementation
     */
    public Document createDocument() {
        Document d = new Document();
        d.add(new org.apache.lucene.document.Field("id", getId() + "", Store.YES, Index.NO));
        d.add(new org.apache.lucene.document.Field("itemId", getParent().getId() + "", Store.YES, Index.NO));
        d.add(new org.apache.lucene.document.Field("type", "history", Store.YES, Index.NO));
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
        if (comment != null) {
            if (sb.length() > 0) {
                sb.append(" | ");
            }
            sb.append(comment);
        }        
        d.add(new org.apache.lucene.document.Field("text", sb.toString(), Store.NO, Index.TOKENIZED));
        return d;
    }
    
    @Override
    public String getRefId() {
        return getParent().getRefId();
    }      
    
    @Override
    public String getUniqueRefId() {
    	return getParent().getUniqueRefId();
    }
    
    @Override
    public Space getSpace() {
        return getParent().getSpace();
    }                
    
    public int getIndex() {
        int index = 0;
        for(History h : getParent().getHistory()) {
            if (getId() == h.getId()) {
                return index;
            }
            index++;
        }
        return -1;
    }
    
    //==========================================================================
    
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }    
    
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getHtmlComment() {
        return this.htmlComment;
    }

    public void setHtmlComment(String htmlComment) {
        this.htmlComment = htmlComment;
    }

    public Set<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
    }

    /**
	 * @see gr.abiss.calipso.domain.IAttachmentOwner#addAttachment(gr.abiss.calipso.domain.Attachment)
	 */
    public boolean addAttachment(Attachment attachment){
    	if(this.attachments == null){
    		this.attachments = new HashSet<Attachment>();
    	}
    	attachment.setHistory(this);
    	return this.attachments.add(attachment);
    }
    
    /**
	 * @see gr.abiss.calipso.domain.IAttachmentOwner#addAttachments(java.util.Collection)
	 */
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
    public void removeAttachment(Attachment attachment){
    	attachment.setHistory(null);
    	this.attachments.remove(attachment);
    }
    

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
    
    public Integer getActualEffort() {
        return actualEffort;
    }

    public void setActualEffort(Integer actualEffort) {
        this.actualEffort = actualEffort;
    }
    
	public Double getTotalIdleTime() {
		this.setTotalIdleTime(ItemUtils.calcTotalIdleTime());
		return totalIdleTime;
	}

	public void setTotalIdleTime(Double totalIdleTime) {
		this.totalIdleTime = totalIdleTime;
	}

	
	public Double getTotalResponseTime() {
		return totalResponseTime;
	}

	public void setTotalResponseTime(Double totalResponseTime) {
		this.totalResponseTime = totalResponseTime;
	}

	public Double getTotalOpenTime() {
		return totalOpenTime;
	}

	public void setTotalOpenTime(Double totalOpenTime) {
		this.totalOpenTime = totalOpenTime;
	}

    // TODO: 
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        sb.append("; comment [").append(comment);
        sb.append("]; actualEffort [").append(actualEffort);
        sb.append("]; attachments [").append(attachments);
        sb.append("]");
        return sb.toString();
    }
    
}
