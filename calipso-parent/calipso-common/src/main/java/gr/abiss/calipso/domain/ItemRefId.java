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

import gr.abiss.calipso.exception.InvalidRefIdException;

import java.io.Serializable;

/**
 * Class that exists purely to parse a String into a valid item ref id of the form ABC-123-456
 * 
 */
public class ItemRefId implements Serializable {
    
    private String prefixCode;
    private long itemId;
    private long sequenceNum;
    
    public ItemRefId(String refId) {
    	int firstHyphenPos = refId.indexOf('-');
    	int secondHyphenPos = refId.lastIndexOf('-');
    	
    	if (firstHyphenPos==-1 || secondHyphenPos==-1){
    		throw new InvalidRefIdException("invalid ref id");
    	}//if
    	
    	try{
    		prefixCode = refId.substring(0, firstHyphenPos); 
    		itemId = Long.parseLong(refId.substring(firstHyphenPos+1, secondHyphenPos));
    		sequenceNum = Long.parseLong(refId.substring(secondHyphenPos+1, refId.length()));
    	}//try
    	catch(Exception e){
    		throw new InvalidRefIdException("invalid ref id");
    	}//catch
    }

    public String getPrefixCode() {
        return prefixCode;
    }

    public long getSequenceNum() {
        return sequenceNum;
    }

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}    
    
}
