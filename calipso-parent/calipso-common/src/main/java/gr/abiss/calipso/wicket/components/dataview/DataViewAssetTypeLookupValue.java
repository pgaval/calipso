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

package gr.abiss.calipso.wicket.components.dataview;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.wicket.CalipsoApplication;
import gr.abiss.calipso.wicket.components.dataprovider.DataProviderAssetTypeLookupValue;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

/**
 */
public class DataViewAssetTypeLookupValue extends DataView {
	protected static final Logger logger = Logger
			.getLogger(DataViewAssetTypeLookupValue.class);

	private static final long serialVersionUID = 1L;
	private DataProviderAssetTypeLookupValue dataProvider;
	private List<CustomAttributeLookupValue> dataList;

	/**
	 * @param id
	 * @param dataProvider
	 * @param itemsPerPage
	 */
	@SuppressWarnings("unchecked")
	public DataViewAssetTypeLookupValue(String id,
			DataProviderAssetTypeLookupValue dataProvider, int itemsPerPage) {
		super(id, dataProvider, itemsPerPage);
		this.dataProvider = dataProvider;
		this.dataList = this.dataProvider
				.getAssetTypeCustomAttribute().getAllowedLookupValues();
		
	}

	/**
	 * 
	 * @param id
	 * @param dataProvider
	 */
	@SuppressWarnings("unchecked")
	public DataViewAssetTypeLookupValue(String id,
			DataProviderAssetTypeLookupValue dataProvider) {
		super(id, dataProvider);
		this.dataProvider = dataProvider;
		this.dataList = this.dataProvider
				.getAssetTypeCustomAttribute().getAllowedLookupValues();
	}

	/**
	 * @see org.apache.wicket.markup.repeater.RefreshingView#populateItem(org.apache.wicket.markup.repeater.Item)
	 */
	@Override
	protected void populateItem(Item item) {
		final CustomAttributeLookupValue lookupValue = (CustomAttributeLookupValue) item
				.getModelObject();

		if (logger.isDebugEnabled()) {
			logger.debug("item : " + lookupValue.getValue() + " with order :"
					+ lookupValue.getShowOrder());
		}

		item.add(new Label("showOrder", String.valueOf(lookupValue
				.getShowOrder())));
		item.add(new Label("value", lookupValue.getValue()));

		item.add(new Link("up") {
			public void onClick() {
				moveUp(lookupValue);
			}
		});

		item.add(new Link("down") {
			public void onClick() {
				moveDown(lookupValue);
			}
		});

		Link edit = new Link("edit") {
			@Override
			public void onClick() {

			}
		};
		item.add(edit);

		Link delete = new Link("delete") {
			@Override
			public void onClick() {
				((DataProviderAssetTypeLookupValue) dataProvider)
						.getAssetTypeCustomAttribute().remove(lookupValue);
			}// onClick
		};// edit

		boolean canBeDeleted = getJtrac()
				.loadCountForCustomAttributeLookupValue(lookupValue) == 0;
		boolean isNewEntry = (lookupValue.getAttribute() == null);

		item.add(delete.setVisible(canBeDeleted && !isNewEntry));

	}

	private CalipsoService getJtrac() {
		return ((CalipsoApplication) Application.get()).getCalipso();
	}

	private void moveUp(CustomAttributeLookupValue lookupValue) {
		int siz = dataList.size();
		int currentPos = dataList.indexOf(lookupValue);
		if (currentPos - 1 > 0) {
			Collections.swap(dataList, currentPos, currentPos - 1);
		}
	}

	private void moveDown(CustomAttributeLookupValue lookupValue) {
		int siz = dataList.size();
		int currentPos = dataList.indexOf(lookupValue);
		if (currentPos + 1 < siz) {
			Collections.swap(dataList, currentPos, currentPos + 1);
		}
	}
}
