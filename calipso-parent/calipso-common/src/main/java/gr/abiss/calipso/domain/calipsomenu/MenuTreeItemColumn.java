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

import javax.swing.tree.TreeNode;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation;
import org.apache.wicket.extensions.markup.html.tree.table.PropertyTreeColumn;
import org.apache.wicket.model.PropertyModel;

public class MenuTreeItemColumn extends PropertyTreeColumn{
	private static final long serialVersionUID = 1L;

	String callerPageClassName;

	//---------------------------------------------------------------------------------------------
	
	public MenuTreeItemColumn(ColumnLocation location, String header,
			String propertyExpression, 
			String callerPageClassName) {
		super(location, header, propertyExpression);
		this.callerPageClassName = callerPageClassName;
		
	}
	
	//---------------------------------------------------------------------------------------------
	
	@Override
	public Component newCell(MarkupContainer parent, String id, TreeNode node, int level) {
		final Component cell = super.newCell(parent, id, node, level);
		
		PropertyModel menuItemPropertyModel = new PropertyModel(node, "userObject.menu");
		final MenuItem menuItem = (MenuItem)menuItemPropertyModel.getObject();

		if (getTreeTable().getTreeState().isNodeSelected(node)){
			menuItem.onClick();
		}//if

		
		if (this.callerPageClassName.equals(menuItem.getPageClassName())){
			try{
				getTreeTable().getTreeState().selectNode(node, true);
			}
			catch (Exception e) {
				
			}
		}//if

		return cell;
	}

	public String getCallerPageClassName() {
		return callerPageClassName;
	}
	
	public void setCallerPageClassName(String callerPageClassName) {
		this.callerPageClassName = callerPageClassName;
	}
}
