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

import java.util.List;

import gr.abiss.calipso.domain.Country;

import org.apache.wicket.Localizer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

/**
 * Renders a list of Country domain objects
 */
public class CountriesDropDownChoice extends DropDownChoice {
	/**
	 * @param id
	 * @param choices
	 */
	public CountriesDropDownChoice(String id, List<Country> countries) {
		super(id, countries);
		this.setChoiceRenderer(new CountriesRenderer(this));
	}
	
	public CountriesDropDownChoice(String id, IModel model, List<Country> countries) {
		super(id, model, countries);
		this.setChoiceRenderer(new CountriesRenderer(this));
	}


	final class CountriesRenderer implements IChoiceRenderer{
		private CountriesDropDownChoice countriesDropDownChoice;
		private Localizer localizer;
		
		protected CountriesRenderer(CountriesDropDownChoice countriesDropDownChoice){
			this.countriesDropDownChoice = countriesDropDownChoice;
			this.localizer = countriesDropDownChoice.getLocalizer();
		}
		
		public Object getDisplayValue(Object object) {
			Country country = (Country) object;
			return country.getId()+" - "+ localizer.getString("country."+country.getId(), countriesDropDownChoice);
		}

		public String getIdValue(Object object, int index) {
			return String.valueOf(index);
		}
		
	}
	

}
