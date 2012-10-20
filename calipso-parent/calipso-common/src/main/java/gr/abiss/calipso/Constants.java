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

/**
 * CalipsoService constants
 */
public class Constants {
    
    public static final String METADATA = "metadata";
    public static final String ROLES = "roles";
    public static final String ROLE = "role";
    public static final String FIELDS = "fields";
    public static final String ORDER = "order";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PRIORITY = "priority";
    public static final String EXPRESSION = "expression";
    public static final String FIELDTYPE = "field-type";
    public static final String VALIDATIONEXPR = "validation-expression";
    public static final String LABEL = "label";
    public static final String OPTION = "option";
    public static final String GROUP_ID = "group-id";
    public static final String VALUE = "value";
    public static final String OPTIONAL = "optional";
    public static final String LINECOUNT = "linecount";
    public static final String MULTIVALUE = "multivalue";
	public static final String DEFAULT_VALUE = "default-value";
    public static final String FIELD_GROUP = "field-group";
    public static final String FIELD = "field";
    public static final String FIELD_ORDER = "field-order";
    public static final String TRUE = "true";
    public static final String TRANSITION = "transition";
    public static final String STATE = "state";
    public static final String STATES = "states";
    public static final String STATUS = "status";
    public static final String PLUGIN = "plugin";
    public static final String DATEFORMATS = "date-formats";
    public static final String DATEFORMAT = "date-format";
    public static final String ASSET_TYPE_ID = "make-asset";
	public static final String EXISTING_ASSET_TYPE_ID = "exising-asset-type";
	public static final String EXISTING_ASSET_TYPE_MULTIPLE = "exising-asset-type-multiple";
    public static final String MAX_DURATION = "max-duration";
    public static final String CLASS = "class";
    public static final String MASK = "mask";
    public static final String FIELD_GROUP_XPATH = "/" + METADATA + "/" + FIELDS + "/" + FIELD_GROUP;
    public static final String FIELD_XPATH = "/" + METADATA + "/" + FIELDS + "/" + FIELD;
    public static final String DATEFORMATS_XPATH =  "/" + METADATA +"/" + DATEFORMATS + "/" + DATEFORMAT;
    public static final String ROLE_XPATH = "/" + METADATA + "/" + ROLES + "/" + ROLE;
    public static final String STATE_XPATH = "/" + METADATA + "/" + STATES + "/" + STATE;
    public static final String FIELD_ORDER_XPATH = "/" + METADATA + "/" + FIELDS + "/" + FIELD_ORDER + "/" + FIELD;
    public static final int PAGE_SIZE = 10;

///////////////////////////////////////////////////////////////////////////////////////////////////

//    public enum ReservedRoles{
//    	ADMINISTATOR(1, "Administrator"),
//    	SPACE_ADMINISTATOR(2, "Space Administrator"),
//    	GUEST(3, "Guest"),
//    	REGULAR_USER(4, "Regular User");
//
//    	private long id;
//    	private String description;
//    	
//    	private ReservedRoles(long id, String description){
//    		this.id = id;
//    		this.description = description;
//    	}
//
//    	public void setId(long id) {
//			this.id = id;
//		}
//
//    	public long getId() {
//			return id;
//		}
//
//    	public void setDescription(String description) {
//			this.description = description;
//		}
//
//    	public String getDescription() {
//			return description;
//		}
//    	
//    	public String getIdAsString(){
//    		return String.valueOf(this.id);
//    	}
//    	
//    }//ReservedRoles
    
}