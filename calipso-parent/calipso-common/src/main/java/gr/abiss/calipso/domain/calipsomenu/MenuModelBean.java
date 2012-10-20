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

package gr.abiss.calipso.domain.calipsomenu;

import java.io.Serializable;

public class MenuModelBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private MenuItem menuItem;

	public MenuModelBean() {
		this.menuItem = new MenuItem("", "");
		
	}
	
	public MenuModelBean(MenuItem menuItem){
        this.menuItem = menuItem;
    }

	public MenuModelBean(String s){
		this.menuItem = new MenuItem(s, "");
	}
	
	public MenuItem getMenu(){
		return menuItem;
	}
	
	public void setMenu(MenuItem menuItem){
        this.menuItem = menuItem;
    }
	
	@Override
	public String toString(){
        return menuItem.getDescription();
    }
}
