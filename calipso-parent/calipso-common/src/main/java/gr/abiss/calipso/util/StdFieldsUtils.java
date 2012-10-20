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

package gr.abiss.calipso.util;

import gr.abiss.calipso.domain.RoleSpaceStdField;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.StdField;
import gr.abiss.calipso.domain.StdFieldMask;
import gr.abiss.calipso.domain.StdFieldType;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.wicket.ComponentUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.wicket.Component;

public class StdFieldsUtils {
	
	public static StdFieldMask getFieldMask(User user, StdField stdField){
		List<RoleSpaceStdField> roleSpaceStdFieldlist = user.getStdFields();
		StdFieldMask stdFieldMask = null;
		
		for (RoleSpaceStdField roleSpaceStdField : roleSpaceStdFieldlist){
			if (roleSpaceStdField.getStdField().getField().equals(stdField.getField())){
				if (stdFieldMask==null){
					if (roleSpaceStdField.getFieldMask()!=null){
						stdFieldMask = new StdFieldMask(roleSpaceStdField.getFieldMask().getMask());
					}//if
				}//if
				else{
					//if a mask exists for an other space the "best" mask 
					//will be taken for the intersection of the user's field set.
					if (stdFieldMask.getMask()!=null && roleSpaceStdField!=null && roleSpaceStdField.getFieldMask()!=null && roleSpaceStdField.getFieldMask().getMask()!=null){
						if (/*stdFieldMask.getMask()!=null && */stdFieldMask.getMask().ordinal() > roleSpaceStdField.getFieldMask().getMask().ordinal()){
							stdFieldMask.setMask(roleSpaceStdField.getFieldMask().getMask());
						}//if
					}//if
				}//else
			}//if
		}//for

		//No mask found. Initialize mask with the "worst" case
		if (stdFieldMask==null){
			stdFieldMask = new StdFieldMask(StdFieldMask.Mask.values()[StdFieldMask.Mask.values().length-1]);
		} 
		return stdFieldMask;
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------------------
	
	public static Map<StdField.Field, StdFieldMask> getStdFieldsForSpace(List<RoleSpaceStdField> roleSpaceStdFieldList, Space space, Component c){
		
		Map<StdField.Field, StdFieldMask>  stdFieldForSpaceMap = new LinkedHashMap<StdField.Field, StdFieldMask>();
		for (RoleSpaceStdField roleSpaceStdField : roleSpaceStdFieldList){
			Space space2 = ComponentUtils.getCalipso(c).loadSpace(roleSpaceStdField.getSpaceRole());
			if (space2.equals(space)){
				stdFieldForSpaceMap.put(roleSpaceStdField.getStdField().getField(), roleSpaceStdField.getFieldMask());
			}//if
		}//for
		
		return stdFieldForSpaceMap;
	}
	
	//---------------------------------------------------------------------------------------------
	
	public static List<RoleSpaceStdField> filterFieldsByType(List<RoleSpaceStdField> stdFields, Map<StdField.Field, StdFieldMask> fieldMaskMap, Space space, StdFieldType.Type stdFieldType){
		List<RoleSpaceStdField> fieldList = new ArrayList<RoleSpaceStdField>();
		
        for (RoleSpaceStdField stdField : stdFields){
        	if (stdField.getStdField().getField().getFieldType().getType().equals(stdFieldType)){
        		if (fieldMaskMap.get(stdField.getStdField().getField())!=null){//Mask has been initialized for field
	        		if (!fieldMaskMap.get(stdField.getStdField().getField()).getMask().equals(StdFieldMask.Mask.HIDDEN)){
	        			if (!listContainsField(fieldList, stdField) && stdField.getSpaceRole().getSpace().equals(space)){
	        				fieldList.add(stdField);
	        			}//if
	        		}//if
        		}//if
        	}//if
        }//for
		
		return fieldList;
	}//filterFieldsByType

	//---------------------------------------------------------------------------------------------

	public static List<RoleSpaceStdField> filterFieldsBySpace(List<RoleSpaceStdField> stdFields, Map<StdField.Field, StdFieldMask> fieldMaskMap, Space space){
		List<RoleSpaceStdField> fieldList = new ArrayList<RoleSpaceStdField>();

        for (RoleSpaceStdField stdField : stdFields){
    		if (fieldMaskMap.get(stdField.getStdField().getField())!=null){//Mask has been initialized for field
        		if (!fieldMaskMap.get(stdField.getStdField().getField()).getMask().equals(StdFieldMask.Mask.HIDDEN)){
        			if (!listContainsField(fieldList, stdField) && stdField.getSpaceRole().getSpace().equals(space)){
        				fieldList.add(stdField);
        			}//if
        		}//if
    		}//if
        }//for
		
		return fieldList;
	}
	
	//---------------------------------------------------------------------------------------------

	public static StdFieldMask getFieldBestMask(Map<StdField.Field, StdFieldMask> fieldMaskMap, StdField.Field stdField){
		StdFieldMask fieldBestMask = null;
		
		Iterator<Entry<StdField.Field, StdFieldMask>> iterator =  fieldMaskMap.entrySet().iterator();
		
		
		while (iterator.hasNext()){
			Entry<StdField.Field, StdFieldMask> entry = iterator.next();
			
			if (entry.getKey().equals(stdField)){
				//Initial value
				if (fieldBestMask==null){
					fieldBestMask = entry.getValue();
				}//if
				else{//There is already a return value candidate
					//if  mask of return value candidate is "worst" than current mask 
					if (fieldBestMask.getMask().ordinal()>entry.getValue().getMask().ordinal()){
						fieldBestMask = entry.getValue();
					}//if
				}//else
			}//if
		}//while

		//Return value is null
		if (fieldBestMask==null){
			//Initialize value with the "worst" case.
			fieldBestMask = new StdFieldMask(StdFieldMask.Mask.values()[StdFieldMask.Mask.values().length-1]);
		}//if

		
		return fieldBestMask;
	}//getFieldBestMask

	//------------------------------------------------------------------------------------------------------------------------

	private static boolean listContainsField(List<RoleSpaceStdField> fieldList, RoleSpaceStdField stdField){
		
		for (RoleSpaceStdField roleSpaceStdField : fieldList){
			if(roleSpaceStdField.getStdFieldId().equals(stdField.getStdFieldId())){
				return true;
			}
		}
		
		return false;
	}
}
