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

import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.ItemRenderingTemplate;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.template.PackageTextTemplate;



/**
 * Renders an item using one of the space templates available or using the default
 * view. Currently only the default view is implemented using the ItemView panel.
 */
public class ItemTemplateViewPage extends WebPage {

	private static final long serialVersionUID = 4854112992037656475L;
	private static final Logger logger = Logger.getLogger(ItemTemplateViewPage.class);



	public ItemTemplateViewPage() {
		super();
		PageParameters parameters = new PageParameters();
		// parameters.set("name", name);
		add(new JsTextContainer("styleString", parameters));
		this.setVersioned(true);
	}

	public ItemTemplateViewPage(IModel<?> model) {
		super(model);
		this.setVersioned(true);
		PageParameters parameters = new PageParameters();
		// parameters.set("name", name);
		add(new JsTextContainer("styleString", parameters));
		this.setVersioned(true);
	}


	public ItemTemplateViewPage(PageParameters parameters) {
    	// get item
		final String refId = parameters.get("0").toString();
        // TODO: is this needed?
        Item item = ComponentUtils.getCalipso(this).loadItem(Item.getItemIdFromUniqueRefId(refId));
		//log.info("Got item: "+item);
		add(new Label("pageTitle", item.getUniqueRefId()));

		ItemRenderingTemplate tmpl = new ItemRenderingTemplate();
		tmpl.setHideHistory(true);
		tmpl.setHideOverview(false);
		tmpl.setShowSpaceName(true);
		add(new ItemView("panel", null, tmpl, item, true)
				.setRenderBodyOnly(true));
		add(new JsTextContainer("styleString", parameters));
		this.setVersioned(true);
    }

	static String getPackagedStyleString(PageParameters parameters) {
		PackageTextTemplate template = new PackageTextTemplate(
				ItemTemplateViewPage.class, "pdf-style.tmpl");

		// TODO:
		return template.asString(/* parameters */);
	}

	private static String getStyleString() {
		// TODO Auto-generated method stub
		return "			#normalFooter, #firstPageFooter{font-size:12px;} \r\n"
				+ "			@page {\r\n"
				+ "				padding-top:100px;\r\n"
				+ "				margin-top:30px;\r\n"
				+ "				margin-bottom:30px;\r\n"
				+ "				\r\n"
				+ "				background: url('watermark.png') no-repeat top center; \r\n"
				+ "				@bottom-center { content: element(footer); }\r\n"
				+ "			}\r\n"
				+ "			@page :first {\r\n"
				+ "				padding-top:10px;\r\n"
				+ "				background: url('watermark.png') no-repeat center 30px;\r\n"
				+ "				@bottom-center { content: element(firstPageFooter); }\r\n"
				+ "			}\r\n"
				+ "			#page:before {content: counter(page);}\r\n"
				+ "			\r\n"
				+ "			#pagecount:before {content: counter(pages);}\r\n"
				+ "			#header {\r\n"
				+ "		        	display: block; text-align: center;\r\n"
				+ "		        	position: running(header);\r\n"
				+ "		    }\r\n"
				+ "			#footer {\r\n"
				+ "		        	display: block; text-align: center;\r\n"
				+ "		        	position: running(footer);\r\n"
				+ "		    }\r\n"
				+ "			#firstPageFooter {\r\n"
				+ "		        	display: block; text-align: center;\r\n"
				+ "		        	position: running(firstPageFooter);\r\n"
				+ "		    }\r\n"
				+ "		    #page:before {content: counter(page);}\r\n"
				+ "			#pagecount:before {content: counter(pages);}\r\n"
				+ "			.heading-container1{display:none;}\r\n"
				+ "			h1{font-size:16px;margin:10px;padding:10px;background: #EADFD7;font-weight:bold;color:#666666;}\r\n"
				+ "			body {font-size:14px;margin:10px;padding:10px; font-family: \"Arial Unicode MS\"; }\r\n"
				+ "			th{text-align:left;font-weight:bold;}\r\n"
				+ "			td, th, caption{padding-left:4px;padding-right:4px;padding-top:2px;padding-bottom:2px;}\r\n"
				+ "			table.overview{ -fs-table-paginate:paginate;border-collapse:collapse;background:#FFFFFF;width:100%;margin-bottom:8px; }\r\n"
				+ "			table.overview-summary{display: none;}\r\n"
				+ "			tr.alt { background: #F2F2F2; }\r\n"
				+ "			table, tbody, thead, tfooter, tr{width:100%;}\r\n"
				+ "			table.custom-attribute-tabular {-fs-table-paginate:paginate;}\r\n"
				+ "			table.custom-attribute-tabular  tr { }\r\n"
				+ "			table.custom-attribute-tabular tr.even { background: #F7F7F7; }\r\n"
				+ "			table.custom-attribute-tabular thead {background: #f2ebe7;}\r\n"
				+ "			table.overview > thead > tr > th {background: #EADFD7;font-weight:bold;text-align:left;}\r\n"
				+ "			table.overview > thead:not(:first-child) > tr > th > span.continued:after {content: ' (Cont)';}\r\n"
				+ "			\r\n"
				+ "			td, th{vertical-align:top;}\r\n"
				+ "			.clear{clear:both;}\r\n"
				+ "      		a, a:link{\r\n"
				+ "      		    color:#4E3227; \r\n"
				+ "      		    text-decoration: none;\r\n"
				+ "      		}\r\n"
				+ "      		table.overview th.label-right {\r\n"
				+ "			    font-weight: bold;\r\n"
				+ "			    width: 25%;\r\n"
				+ "			    vertical-align: top;\r\n"
				+ "			}\r\n"
				+ "			table.overview, \r\n"
				+ "			table.overview tr, \r\n"
				+ "			table.custom-attribute-tabular, \r\n"
				+ "			table.custom-attribute-tabular tr {page-break-inside:avoid;}";
	}

}
