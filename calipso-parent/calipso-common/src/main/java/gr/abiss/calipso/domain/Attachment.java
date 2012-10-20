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

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a file attachment.  Files will be stored on the local
 * file system, not within the database.
 * When an Attachment is first uploaded and stored, it is prefixed
 * with the value of the id generated on the database insert.
 * This filePrefix property is stored separately to smoothly
 * handle database migrations.  So even if a database export-import 
 * changes the id column values, the files within the attachments 
 * folder can be used as is, without resorting to mass renaming.
 */
public class Attachment implements Serializable {
    
    private long id;
    private Attachment previous;
    private Item item;
    // not persisted
    private Space space;
    private History history;
    private long filePrefix;
    private String fileName;
    private String originalFileName;
    private boolean simple = false;
    private String basePath;
    private boolean temporary;
    
    
    
    public Attachment() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
	 * @return the basePath
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * @param basePath the basePath to set
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Attachment getPrevious() {
        return previous;
    }

    public void setPrevious(Attachment previous) {
        this.previous = previous;
    }

    public long getFilePrefix() {
        return filePrefix;
    }

    public void setFilePrefix(long filePrefix) {
        this.filePrefix = filePrefix;
    }

	public boolean isTemporary() {
		return temporary;
	}

	public void setTemporary(boolean temporary) {
		this.temporary = temporary;
	}

	/**
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(Item item) {
		this.item = item;
		if(this.space == null && item != null && item.getSpace() != null){
			this.space = item.getSpace();
		}
	}

	/**
	 * @return the history
	 */
	public History getHistory() {
		return history;
	}

	/**
	 * @param history the history to set
	 */
	public void setHistory(History history) {
		this.history = history;
		if(this.space == null && history != null && history.getSpace() != null){
			this.space = history.getSpace();
		}
		if(this.item == null && history != null && history.getParent() != null){
			this.item = history.getParent();
		}
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public boolean isSimple() {
		return simple;
	}

	public void setSimple(boolean simple) {
		this.simple = simple;
	}

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}
}
