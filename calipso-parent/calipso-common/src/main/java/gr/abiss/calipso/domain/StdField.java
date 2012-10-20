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

public class StdField implements Serializable{

	private static final long serialVersionUID = 1L;

	private Field field;


	public enum Field{
		
		TIME_FROM_CREATION_TO_FIRST_REPLY(new Integer(1), new StdFieldType(StdFieldType.Type.STATISTIC), "timeFromCreationToFirstReply"),
		TIME_FROM_CREATION_TO_CLOSE(new Integer(2), new StdFieldType(StdFieldType.Type.STATISTIC), "timeFromCreationToClose"),
		ASSET(new Integer(3), new StdFieldType(StdFieldType.Type.ASSET), "asset"),
		ASSET_TYPE(new Integer(4), new StdFieldType(StdFieldType.Type.ASSETTYPE), "assets"),
		DUE_TO(new Integer(5), new StdFieldType(StdFieldType.Type.INFO), "dueTo"),
		PLANNED_EFFORT(new Integer(6), new StdFieldType(StdFieldType.Type.INFO), "plannedEffort"),
		ACTUAL_EFFORT(new Integer(7), new StdFieldType(StdFieldType.Type.INFO), "actualEffort");

		
		private final Integer id;
		private final StdFieldType fieldType;
		private final String name;
		
		private Field(final Integer id, final StdFieldType fieldType, final String name) {
			this.id = id;
			this.fieldType = fieldType;
			this.name = name;
		}

		public StdFieldType getFieldType() {
			return fieldType;
		}

		public Integer getId() {
			return id;
		}

		public String getName() {
			return name;
		} 
		
	}
	
	public StdField() {

	}

	public StdField(Field field) {
		this.field = field;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	@Override
	public boolean equals(Object obj) {
		StdField stdField = (StdField)obj;
		
		return this.getField().getId().equals(stdField.getField().getId());
	}
}