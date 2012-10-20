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

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.extensions.markup.html.tree.table.IColumn;
import org.apache.wicket.extensions.markup.html.tree.table.TreeTable;

public class MenuTreeTable extends TreeTable implements Serializable{
	private static final long serialVersionUID = 1L;

	public MenuTreeTable(String id, TreeModel model, IColumn[] columns) {
		super(id, model, columns);
	}

	@Override
	protected ResourceReference getNodeIcon(TreeNode node) {
		DefaultMutableTreeNode mutableTreeNode = (DefaultMutableTreeNode)node;
		MenuModelBean treeModelBean = (MenuModelBean)mutableTreeNode.getUserObject();
		
		if (treeModelBean.getMenu().getImage()!=null){
			return treeModelBean.getMenu().getImage();
		}

		return super.getNodeIcon(node);
	}
	
	@Override
	protected ResourceReference getCSS() {
		//return new ResourceReference(this.getClass(), "resources/MyTreeStyle.css");
		return null;
	}
}