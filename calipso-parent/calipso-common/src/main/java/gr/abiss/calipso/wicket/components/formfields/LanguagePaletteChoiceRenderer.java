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

package gr.abiss.calipso.wicket.components.formfields;

import gr.abiss.calipso.domain.Language;

import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 * @author manos
 * 
 */
public class LanguagePaletteChoiceRenderer implements IChoiceRenderer {

	private static final long serialVersionUID = 1L;

	private Component formComponent;
	private Localizer localizer;

	public LanguagePaletteChoiceRenderer(Component formComponent){
		this.formComponent = formComponent;
		this.localizer = formComponent.getLocalizer();
	}
	
	private LanguagePaletteChoiceRenderer(){
		
	}
	
	public Object getDisplayValue(Object object) {
		Language lang = (Language) object;
		return lang.getId()+" - "+ localizer.getString("language."+lang.getId(), formComponent);
	}

	public String getIdValue(Object object, int index) {
		Language lang = (Language) object;
		return lang.getId();
	}

}
