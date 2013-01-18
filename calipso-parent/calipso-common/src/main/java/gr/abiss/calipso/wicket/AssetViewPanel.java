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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.AssetCustomAttributeValue;
import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.Country;
import gr.abiss.calipso.dto.KeyValuePair;
import gr.abiss.calipso.util.ExcelUtils;
import gr.abiss.calipso.util.PdfUtils;
import gr.abiss.calipso.wicket.components.PdfRequestTarget;
import gr.abiss.calipso.wicket.components.formfields.MultipleValuesTextField;
import gr.abiss.calipso.wicket.components.viewLinks.AssetViewLink;
import gr.abiss.calipso.wicket.components.viewLinks.OrganizationViewLink;
import gr.abiss.calipso.wicket.components.viewLinks.UserViewLink;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;

/**
 * Asset edit form
 */
public class AssetViewPanel extends BasePanel {
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(AssetViewPanel.class);
	

	public AssetViewPanel(String id, IBreadCrumbModel breadCrumbModel, Long assetId) {
		super(id, breadCrumbModel);
		init(breadCrumbModel, getCalipso().loadAssetWithAttributes(assetId));
	}
	
	public AssetViewPanel(String id, IBreadCrumbModel breadCrumbModel, Asset asset) {
		super(id, breadCrumbModel);
		init(breadCrumbModel, asset);
	}
	

	public String getTitle() {
		return localize("asset.form.legend");
	}

	public void init(IBreadCrumbModel breadCrumbModel, final Asset asset){
		
		//logger.debug("Loaded asset: "+asset);
		
		add(new IconPanel("assetIcon", new PropertyModel(asset.getAssetType(), "id"), "assetTypes"));
		add(new Label("assetType", localize(asset.getAssetType().getNameTranslationResourceKey())));
		add(new Label("inventoryCode", asset.getInventoryCode()));
		add(new Link("printToPdf") {
            public void onClick() {
            	// TODO: pickup template from TB if it exists for this asset type
            	getRequestCycle().scheduleRequestHandlerAfterCurrent(
            			new PdfRequestTarget(
            					PdfUtils.getPdf(getCalipso(), asset, AssetViewPanel.this), asset.getInventoryCode()));
            }
        });
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		if(asset.getSupportStartDate() != null){
			add(new Label("supportStartDate", dateFormat.format(asset.getSupportStartDate())));
		}
		else{
			add(new Label("supportStartDate", ""));
		}
		if(asset.getSupportEndDate() != null){
			add(new Label("supportEndDate", dateFormat.format(asset.getSupportStartDate())));
		}
		else{
			add(new Label("supportEndDate", ""));
		}

		// TODO: this is insane but otherwise we cannot retrieve values from
		// the map using the keys obtained from it! equals/hashCode looks ok, need to investigate
		// see also AssetFormPagePanel#updateCustomAttributeValue
		@SuppressWarnings("serial")
		ListView listView = new ListView("attributeValuesList",	KeyValuePair.fromMap(asset.getCustomAttributes())) {
			final SimpleAttributeModifier sam = new SimpleAttributeModifier("class", "alt");
			@SuppressWarnings("unchecked")
			@Override
			protected void populateItem(ListItem listItem) {
				if(listItem.getIndex() % 2 == 1) {
                    listItem.add(sam);
                }      
				KeyValuePair entry = (KeyValuePair) listItem.getModelObject();
				
				AssetTypeCustomAttribute customAttr = (AssetTypeCustomAttribute) entry.getKey();
				String sValue = (String) entry.getValue();
				Label customAttributeLabel = new Label("customAttribute",localize(customAttr.getNameTranslationResourceKey()));
				listItem.add(customAttributeLabel);

				String value = new String(localize("asset.customAttributeNoValue"));
				if (sValue != null && !sValue.isEmpty()) {
					value = sValue;
				}
				// this works for all componentViewLinks

				if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)
						|| customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_OPTIONS_TREE)) {
					Label customAttributeValueLabel;
					if(customAttr.getLookupValue() != null){
						customAttributeValueLabel = (Label) new Label("customAttributeValue", customAttr.getLookupValue().getValue());
					}
					else{

						customAttributeValueLabel = new Label("customAttributeValue");
					}
					listItem.add(customAttributeValueLabel);
				}
				else if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_USER)) {
					UserViewLink userViewLink = new UserViewLink("customAttributeValue", getBreadCrumbModel(), customAttr.getUserValue());
					listItem.add(userViewLink);
				} else if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_ORGANIZATION)) {
					// this works for all componentViewLinks
					OrganizationViewLink organizationViewLink = new OrganizationViewLink("customAttributeValue", getBreadCrumbModel(), customAttr.getOrganizationValue());
					listItem.add(organizationViewLink);
				} else if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_ASSET)) {
					// this works for all componentViewLinks
					AssetViewLink assetViewLink = new AssetViewLink("customAttributeValue", getBreadCrumbModel(), customAttr.getAssetValue());
					listItem.add(assetViewLink);
				} else if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_COUNTRY)) {
					Country country = customAttr.getCountryValue();
					Label customAttributeValueLabel = (Label) new Label("customAttributeValue", country!=null?localize(country):"").setEscapeModelStrings(false);
					listItem.add(customAttributeValueLabel);
					
				} else if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_TABULAR)) {
					Label customAttributeValueLabel = (Label) new Label("customAttributeValue", value).setEscapeModelStrings(false);
					listItem.add(customAttributeValueLabel);
					
				} else {
					Label customAttributeValueLabel = (Label) new Label("customAttributeValue", MultipleValuesTextField.toHtmlSafeTable(value)).setEscapeModelStrings(false);
					listItem.add(customAttributeValueLabel);
				}// 

			}// populateItem
		};

		add(listView);
	}
	
}