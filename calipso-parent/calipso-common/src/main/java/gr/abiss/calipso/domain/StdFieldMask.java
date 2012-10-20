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

public class StdFieldMask implements Serializable {

	private Mask mask;
	
	public enum Mask{
		MANDATORY(new Integer(2), "mandatory"), //Create/Update mandatory
		OPTIONAL(new Integer(3), "optional"), //Create/Update optional
		UPDATE(new Integer(5), "update"), //"Best" Case
		CREATE(new Integer(6), "create"),
		READ(new Integer(1), "read"),
		HIDDEN(new Integer(4), "hidden"); //"Worst" case

		private Integer id;
		private String name;

		private Mask(Integer id, String name) {
			this.id = id;
			this.name = name;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	
	}


	public StdFieldMask(Mask mask) {
		this.mask = mask;
	}


	public Mask getMask() {
		return mask;
	}


	public void setMask(Mask mask) {
		this.mask = mask;
	}
	
	@Override
	public boolean equals(Object object) {
		return ((StdFieldMask)object).getMask().getId().equals(this.getMask().getId());
	}
}
