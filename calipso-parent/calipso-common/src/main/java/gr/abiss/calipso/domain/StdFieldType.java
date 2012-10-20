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
import java.util.HashSet;
import java.util.Set;

public class StdFieldType implements Serializable{

	private Type type;
		

	public enum Type{

		STATISTIC(new Integer(1), "statistic"),
		ASSET(new Integer(2), "asset"),
		ASSETTYPE(new Integer(3), "assetType"),
		INFO(new Integer(4), "info");
		

		private Integer id;
		private String internalName;
		private Set<StdFieldMask> availableMasks;
		
		private Type(Integer id, String internalName) {
			this.id = id;
			this.internalName = internalName;

			switch (this.id) {
			case 1://Statistic
				availableMasks = new HashSet<StdFieldMask>();
				availableMasks.add(new StdFieldMask(StdFieldMask.Mask.HIDDEN));
				availableMasks.add(new StdFieldMask(StdFieldMask.Mask.READ));
			break;

			case 2://Asset
				availableMasks = new HashSet<StdFieldMask>();
				availableMasks.add(new StdFieldMask(StdFieldMask.Mask.HIDDEN));
				availableMasks.add(new StdFieldMask(StdFieldMask.Mask.READ));
				availableMasks.add(new StdFieldMask(StdFieldMask.Mask.CREATE));
				availableMasks.add(new StdFieldMask(StdFieldMask.Mask.UPDATE));
			break;
			
			case 3://Asset Type
				availableMasks = new HashSet<StdFieldMask>();
				availableMasks.add(new StdFieldMask(StdFieldMask.Mask.HIDDEN));
				availableMasks.add(new StdFieldMask(StdFieldMask.Mask.READ));
			break;
			
			case 4://Info
				availableMasks = new HashSet<StdFieldMask>();
				availableMasks.add(new StdFieldMask(StdFieldMask.Mask.HIDDEN));
				availableMasks.add(new StdFieldMask(StdFieldMask.Mask.READ));
				availableMasks.add(new StdFieldMask(StdFieldMask.Mask.CREATE));
				availableMasks.add(new StdFieldMask(StdFieldMask.Mask.UPDATE));
			default:
				break;
			}
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getInternalName() {
			return internalName;
		}

		public void setInternalName(String internalName) {
			this.internalName = internalName;
		}

		public Set<StdFieldMask> getAvailableMasks() {
			return availableMasks;
		}

		public void setAvailableMasks(Set<StdFieldMask> availableMasks) {
			this.availableMasks = availableMasks;
		}
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}//Type

	public StdFieldType(Type type) {
		this.type = type;
	}

	public StdFieldType() {
	}

	@Override
	public boolean equals(Object object) {
		return ((StdFieldType)object).getType().getId().equals(this.getType().getId());		
	}
}
