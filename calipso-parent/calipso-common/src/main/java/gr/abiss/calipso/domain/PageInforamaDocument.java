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

import java.io.Serializable;

public class PageInforamaDocument implements Serializable{
	private static final long serialVersionUID = 1L;

	private int id;
	private PageDictionary pageDictionary;
	private InforamaDocument inforamaDocument;
	private String commandDescription;
	
	public PageInforamaDocument() {
	}

	public PageInforamaDocument(int id, PageDictionary pageDictionary,
			InforamaDocument inforamaDocument, String commandDescription) {
		super();
		this.id = id;
		this.pageDictionary = pageDictionary;
		this.inforamaDocument = inforamaDocument;
		this.commandDescription = commandDescription;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PageDictionary getPageDictionary() {
		return pageDictionary;
	}

	public void setPageDictionary(PageDictionary pageDictionary) {
		this.pageDictionary = pageDictionary;
	}

	public InforamaDocument getInforamaDocument() {
		return inforamaDocument;
	}

	public void setInforamaDocument(InforamaDocument inforamaDocument) {
		this.inforamaDocument = inforamaDocument;
	}

	public String getCommandDescription() {
		return commandDescription;
	}

	public void setCommandDescription(String commandDescription) {
		this.commandDescription = commandDescription;
	}
}
