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

public class RoleSpaceStdField implements Serializable {

	private Long id;
	private SpaceRole spaceRole;
	private Integer stdFieldId = new Integer(0);
	private Integer fieldMaskId = new Integer(0);

	public RoleSpaceStdField() {

	}

	public RoleSpaceStdField(Long id, SpaceRole spaceRole, Integer stdFieldId, Integer fieldMaskId) {
		this.id = id;
		this.spaceRole = spaceRole;
		this.stdFieldId = stdFieldId;
		this.fieldMaskId = fieldMaskId;
	}
	

	public RoleSpaceStdField(Long id, SpaceRole spaceRole, StdField stdField, StdFieldMask stdFieldMask) {
		this.id = id;
		this.spaceRole = spaceRole;

		if (stdField!=null && stdField.getField()!=null && stdField.getField().getId()!=null){
			this.stdFieldId = stdField.getField().getId();
		}
		
		if (stdFieldMask!=null && stdFieldMask.getMask() !=null && stdFieldMask.getMask().getId() !=null){
			this.fieldMaskId = stdFieldMask.getMask().getId();
		}
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setSpaceRole(SpaceRole spaceRole) {
		this.spaceRole = spaceRole;
	}

	public SpaceRole getSpaceRole() {
		return spaceRole;
	}

	public Integer getStdFieldId() {
		return stdFieldId;
	}

	public void setStdFieldId(Integer stdField) {
		this.stdFieldId = stdField;
	}

	public Integer getFieldMaskId() {
		return fieldMaskId;
	}

	public void setFieldMaskId(Integer fieldMask) {
		this.fieldMaskId = fieldMask;
	}

	public StdFieldMask getFieldMask(){
		for (int i=0; i<StdFieldMask.Mask.values().length; i++){
			if (StdFieldMask.Mask.values()[i].getId().equals(this.fieldMaskId)){
				return new StdFieldMask(StdFieldMask.Mask.values()[i]);
			}//if
		}//for
		
		return null;
		
	}
	
	public StdField getStdField(){
		for (int i=0; i<StdField.Field.values().length; i++){
			if (StdField.Field.values()[i].getId().equals(this.stdFieldId)){
				return new StdField(StdField.Field.values()[i]);
			}//if
		}//for

		return null;
	}
	
	@Override
	public String toString() {
		return "Id=" + this.id + " " +
				"Space Role=" + this.spaceRole + " " +
				"Field Id=" + this.stdFieldId + " " +
				"Mask Id=" + this.fieldMaskId;
	}
	
	@Override
	public boolean equals(Object object) {
		RoleSpaceStdField roleSpaceStdField = (RoleSpaceStdField)object;
		//Tow objects of this class are equal only if:
		//space, stdField and spaceRole of each object are identical.

		return this.getSpaceRole().equals(roleSpaceStdField.spaceRole) && 
		this.getStdFieldId().equals(roleSpaceStdField.getStdFieldId());
	}
	
}
