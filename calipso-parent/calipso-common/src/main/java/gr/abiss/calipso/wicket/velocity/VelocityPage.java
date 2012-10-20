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

package gr.abiss.calipso.wicket.velocity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.wicket.BasePage;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.IStringResourceStream;
import org.apache.wicket.util.resource.PackageResourceStream;
import org.apache.wicket.velocity.markup.html.VelocityPanel;

/**
 */
public class VelocityPage extends BasePage {

	/*
	 * Constructor
	 * 
	 * @param parameters Page parameters
	 */
	@SuppressWarnings({ "serial", "serial" })
	public VelocityPage(final PageParameters parameters) {
		final IResourceStream template = new PackageResourceStream(
				VelocityPage.class, "fields.vm");

		// Map<String, List<Field>> map = new HashMap<String, List<Field>>();
		// List<Field> fields = VelocityTemplateApplication.getFields();
		// map.put("fields", fields);

		VelocityPanel panel;
		add(panel = new VelocityPanel("templatePanel", new Model(new HashMap())) {
			@Override
			protected IResourceStream getTemplateResource() {
				return template;
			}

			@Override
			protected boolean parseGeneratedMarkup() {
				return true;
			}
		});
		// for (Field field : fields)
		// {
		// panel.add(new TextField(field.getFieldName()));
		// }
	}

}
