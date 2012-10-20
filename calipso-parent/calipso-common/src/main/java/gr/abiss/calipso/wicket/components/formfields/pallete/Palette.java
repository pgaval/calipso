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

package gr.abiss.calipso.wicket.components.formfields.pallete;

import org.apache.wicket.extensions.markup.html.form.palette.component.Recorder;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * @author manos
 * 
 */
public class Palette extends
		org.apache.wicket.extensions.markup.html.form.palette.Palette {

	private static final long serialVersionUID = 1L;
	private Recorder recorder;

	/**
	 * @param id
	 *            Component id
	 * @param choicesModel
	 *            Model representing collection of all available choices
	 * @param choiceRenderer
	 *            Render used to render choices. This must use unique IDs for
	 *            the objects, not the index.
	 * @param rows
	 *            Number of choices to be visible on the screen with out
	 *            scrolling
	 * @param allowOrder
	 *            Allow user to move selections up and down
	 */
	public Palette(String id, IModel choicesModel,
			IChoiceRenderer choiceRenderer, int rows, boolean allowOrder) {
		super(id, choicesModel, choiceRenderer, rows, allowOrder);
		this.recorder = new Recorder(id + "_recorder", this);
	}

	/**
	 * @param id
	 *            Component id
	 * @param selectedChoicesModel
	 *            Model representing collection of user's selections
	 * @param availableChoicesModel
	 *            Model representing collection of all available choices
	 * @param choiceRenderer
	 *            Render used to render choices. This must use unique IDs for
	 *            the objects, not the index.
	 * @param rows
	 *            Number of choices to be visible on the screen with out
	 *            scrolling
	 * @param allowOrder
	 *            Allow user to move selections up and down
	 */
	public Palette(String id, IModel selectedChoicesModel,
			IModel availableChoicesModel, IChoiceRenderer choiceRenderer,
			int rows, boolean allowOrder) {
		super(id, selectedChoicesModel, availableChoicesModel, choiceRenderer,
				rows, allowOrder);
	}

	/**
	 * Returns the resource reference of the default stylesheet. You may return
	 * null to avoid using any stylesheet.
	 * 
	 * @return A resource reference
	 */
	@Override
	protected ResourceReference getCSS() {
		return null;
	}
}
