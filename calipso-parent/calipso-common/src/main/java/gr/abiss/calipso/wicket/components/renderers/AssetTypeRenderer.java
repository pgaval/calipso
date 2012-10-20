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

package gr.abiss.calipso.wicket.components.renderers;

import gr.abiss.calipso.domain.AssetType;

import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 * Should be used to render AssetType instances in dropdown 
 * (i.e. HTML select) elements throughout the application.
 *
 */
public class AssetTypeRenderer implements IChoiceRenderer {
	
	private static final long serialVersionUID = 1L;
	
	private Component localizerBearer;
	private Localizer localizer;
	
	public AssetTypeRenderer(Component localizerBearer){
		this.localizerBearer = localizerBearer;
		this.localizer = localizerBearer.getLocalizer();
	}

	public Object getDisplayValue(Object o) {
		return this.localizer.getString(((AssetType) o).getNameTranslationResourceKey(), localizerBearer);
	}

	public String getIdValue(Object o, int i) {
		return i+"";
	}
}