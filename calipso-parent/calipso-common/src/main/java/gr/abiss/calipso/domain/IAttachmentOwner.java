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

import java.util.Collection;
import java.util.Set;

/**
 *
 */
public interface IAttachmentOwner {

	public abstract Set<Attachment> getAttachments();
	
	public abstract boolean addAttachment(Attachment attachment);

	public abstract boolean addAttachments(Collection<Attachment> attachments);

	public abstract void removeAttachments(Collection<Attachment> attachments);

	public abstract void removeAttachment(Attachment attachment);
	
	public abstract void removeAttachmentsByFileName(String fileName);

}