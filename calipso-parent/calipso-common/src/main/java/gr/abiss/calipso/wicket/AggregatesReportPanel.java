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


package gr.abiss.calipso.wicket;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gr.abiss.calipso.domain.ColumnHeading;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.ItemSearch;

import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

public class AggregatesReportPanel extends BasePanel {

	private static final Logger logger = Logger.getLogger(AggregatesReportPanel.class);
	public AggregatesReportPanel(String id, ModalWindow modal1,
			ItemSearch itemSearch) {
		super(id);
		addComponents(itemSearch);
	}

	public void addComponents(final ItemSearch itemSearch) {
		final List<ColumnHeading> columnHeadings = itemSearch
				.getColumnHeadingsToRender();
		List<ColumnHeading> groupByHeadings = itemSearch.getGroupByHeadings();
		final Map<String, List> results = getCalipso().findItemGroupByTotals(itemSearch);
		logger.info("Results: "+results);
		@SuppressWarnings("unchecked")
		ListView headings = new ListView("headings", groupByHeadings) {
			@SuppressWarnings("unchecked")
			protected void populateItem(ListItem listItem) {
				final ColumnHeading ch = (ColumnHeading) listItem
						.getModelObject();
				
				String label = ch.isField() ? localize(ch.getLabel())
						: localize("item_list." + ch.getNameText());
				listItem.add(new Label("heading", label));
				
				final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
				listItem.add(new ListView("itemList", results.get(ch.getNameText())) {
					private static final long serialVersionUID = 1L;

					protected void populateItem(ListItem listItem) {
		            	Object[] result = (Object[]) listItem.getModelObject();
		            	String labelKey = localize("CustomAttributeLookupValue."+result[0]+".name");
		            	
		            	listItem.add(new Label("column", labelKey+": "+result[1]));
		            }
		        });
			}
		};
		add(headings);

	}

}
