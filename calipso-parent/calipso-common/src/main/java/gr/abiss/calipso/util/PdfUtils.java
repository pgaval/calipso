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

package gr.abiss.calipso.util;

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.Asset;
import gr.abiss.calipso.domain.AssetTypeCustomAttribute;
import gr.abiss.calipso.domain.Country;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.Organization;
import gr.abiss.calipso.domain.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.InputSource;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;

/**
 * Excel Sheet generation helper
 */
public class PdfUtils {
	
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(PdfUtils.class);

	public static byte[] getPdf(CalipsoService calipso, Asset asset, Component callerComponent ){
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		String html = null;
		try {
			Localizer localizer = callerComponent.getLocalizer();
			String template = calipso.getPrintTemplateTextForAsset(asset.getId());// asset.getAssetType().getPrintingTemplate().getTemplateText();
			if(template != null){
				// template exists, use it
				Reader templateReader = new StringReader(template);
				// create a Velocity context object and add the asset
				final VelocityContext context = new VelocityContext();
				context.put("rootAsset", asset);
				context.put("calipso", calipso);
				// create a writer for capturing the Velocity output
				StringWriter writer = new StringWriter();
				// execute the velocity script and capture the output in writer
				Velocity.evaluate(context, writer, asset.getInventoryCode(), templateReader);
				// get the output as a string
				html = writer.getBuffer().toString();
				logger.debug("Velocity result: \n"+html);
			}
			else{
				StringBuffer htmlBuffer = getDefaultHeader();
				htmlBuffer.append("<h1>").append(localizer.getString(asset.getAssetType().getNameTranslationResourceKey(), callerComponent)).append("</h1>");
				htmlBuffer.append("<table cellspacing='0'>");
				htmlBuffer.append("<tr><th>")
					.append(localizer.getString("asset.form.inventoryCode", callerComponent))
					.append("</th><td>")
					.append(asset.getInventoryCode());
				htmlBuffer.append("</td></tr>");
				if(MapUtils.isNotEmpty(asset.getCustomAttributes())){
					Map<AssetTypeCustomAttribute,String> attrs = asset.getCustomAttributes();
					for(Entry<AssetTypeCustomAttribute, String> entry : attrs.entrySet()){
						AssetTypeCustomAttribute customAttr = entry.getKey();
						htmlBuffer.append("<tr><th>")
							.append(localizer.getString(customAttr.getNameTranslationResourceKey(), callerComponent))
							.append("</th><td>");
						
						if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)
								|| customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_OPTIONS_TREE)) {
							htmlBuffer.append(XmlUtils.escapeHTML(localizer.getString(customAttr.getLookupValue().getNameTranslationResourceKey(), null)));
						}
						else if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_USER)) {
							User user = customAttr.getUserValue();
							htmlBuffer.append(user!=null?XmlUtils.escapeHTML(user.getFullName()):"");
						} else if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_ORGANIZATION)) {
							Organization org = customAttr.getOrganizationValue();
							htmlBuffer.append(org!=null?XmlUtils.escapeHTML(org.getName()):"");
						} else if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_ASSET)) {
							Asset innerAsset = customAttr.getAssetValue();
							htmlBuffer.append(innerAsset!=null?XmlUtils.escapeHTML(innerAsset.getInventoryCode()):"");
						} else if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_COUNTRY)) {
							Country country = customAttr.getCountryValue();
							htmlBuffer.append(country!=null?localizer.getString("country."+country.getId(), callerComponent):"");
						} else {
							htmlBuffer.append(XmlUtils.escapeHTML(entry.getValue()));
						}
						htmlBuffer.append("</td></tr>");
					}
				}
				htmlBuffer.append("</table>");
				
				htmlBuffer.append("</body></html>");
				html = htmlBuffer.toString();
			}
			// convert HTML string to PDF and store it in the buffer output stream 
	        writePdf(calipso, os, html);
		} catch (Exception e) {
			logger.error("Failed to creare PDF for asset, html: \n"+html, e);
		}
		return os.toByteArray();
	}

	public static byte[] getPdf(CalipsoService calipso, Item item, String template, Component callerComponent ){
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		String html = null;
		try {
			Localizer localizer = callerComponent.getLocalizer();
			if(template != null){
				// template exists, use it
				Reader templateReader = new StringReader(template);
				// create a Velocity context object and add the asset
				final VelocityContext context = new VelocityContext();
				context.put("item", item);
				context.put("calipso", calipso);
				// create a writer for capturing the Velocity output
				StringWriter writer = new StringWriter();
				// execute the velocity script and capture the output in writer
				Velocity.evaluate(context, writer, item.getRefId(), templateReader);
				// get the output as a string
				html = writer.getBuffer().toString();
			}
			else{
				// no template exists, just output manual HTML to feed xhtmlrenderer
				StringBuffer htmlBuffer = getDefaultHeader();
				htmlBuffer.append("<h1>").append(localizer.getString(item.getSpace().getNameTranslationResourceKey(), callerComponent)).append(": ").append(item.getRefId()).append("</h1>");
//				htmlBuffer.append("<table cellspacing='0'>");
//				htmlBuffer.append("<tr><th>")
//					.append(localizer.getString("asset.form.inventoryCode", callerComponent))
//					.append("</th><td>")
//					.append(asset.getInventoryCode());
//				htmlBuffer.append("</td></tr>");
//				if(MapUtils.isNotEmpty(asset.getCustomAttributes())){
//					Map<AssetTypeCustomAttribute,String> attrs = asset.getCustomAttributes();
//					for(Entry<AssetTypeCustomAttribute, String> entry : attrs.entrySet()){
//						AssetTypeCustomAttribute customAttr = entry.getKey();
//						htmlBuffer.append("<tr><th>")
//							.append(localizer.getString(customAttr.getNameTranslationResourceKey(), callerComponent))
//							.append("</th><td>");
//						
//						if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_SELECT)
//								|| customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_OPTIONS_TREE)) {
//							htmlBuffer.append(XmlUtils.escapeHTML(localizer.getString(customAttr.getLookupValue().getNameTranslationResourceKey(), null)));
//						}
//						else if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_USER)) {
//							User user = customAttr.getUserValue();
//							htmlBuffer.append(user!=null?XmlUtils.escapeHTML(user.getFullName()):"");
//						} else if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_ORGANIZATION)) {
//							Organization org = customAttr.getOrganizationValue();
//							htmlBuffer.append(org!=null?XmlUtils.escapeHTML(org.getName()):"");
//						} else if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_ASSET)) {
//							Asset innerAsset = customAttr.getAssetValue();
//							htmlBuffer.append(innerAsset!=null?XmlUtils.escapeHTML(innerAsset.getInventoryCode()):"");
//						} else if (customAttr.getFormType().equals(AssetTypeCustomAttribute.FORM_TYPE_COUNTRY)) {
//							Country country = customAttr.getCountryValue();
//							htmlBuffer.append(country!=null?localizer.getString("country."+country.getId(), callerComponent):"");
//						} else {
//							htmlBuffer.append(XmlUtils.escapeHTML(entry.getValue()));
//						}
//						htmlBuffer.append("</td></tr>");
//					}
//				}
//				htmlBuffer.append("</table>");
				htmlBuffer.append("</body></html>");

				html = htmlBuffer.toString();
			}
			// convert HTML string to PDF and store it in the buffer output stream 
	        writePdf(calipso, os, html);
		} catch (Exception e) {
			logger.error("Failed to creare PDF for item, html: \n"+html, e);
		}
		return os.toByteArray();
	}

	public static StringBuffer getDefaultHeader() {
		StringBuffer htmlBuffer = new StringBuffer("<?xml version='1.0' encoding='UTF-8'?><html><head>" +
				"<style type='text/css'>" +
				"#normalFooter, #firstPageFooter{font-size:12px;} " +
				"@page {" +
				"	padding-top:100px;" +
				"	background: url('watermark.png') no-repeat top center; " +
				"	@top-center { content: element(header) }" +
				"	@bottom-right {content: element(footer);}" +
				"}" +
				"@page :first {" +
				"	padding-top:10px;" +
				"	background: url('watermark.png') no-repeat center 30px; " +
				"	@top-center { content: element(header) }" +
				"	@bottom-right {content: element(firstPageFooter);}" +
				"}" +
				"#page:before {content: counter(page);}" +
				"#pagecount:before {content: counter(pages);} " +
				"#footer{position: running(footer);}" +
				"#firstPageFooter{position: running(firstPageFooter);}" +
		        "#header {\n" +
		        "	display: block; text-align: center;\n" + 
		        "	position: running(header);}\n" +
				"body { font-family: \"Arial Unicode MS\";margin:10px;padding:10px; }th{width:50%;background:#EEEEEE;}td, th{border:1px solid #EEEEEE;padding-left:4px;padding-right:4px;vertical-align:top;}table{border-collapse:collapse;}</style></head><body>" +
				"<div id='header'>Header</div>")
			.append("<div id='footer' style=''><div class='footerContent'>Page <span id='page'/> of <span id='pagecount'/></div></div>")
			.append("<div id='firstPageFooter' style=''><div class='footerContent'>Page <span id='page'/> of <span id='pagecount'/></div></div>");
//		"<html><head><style>\n" +
//        "div.footer {\n" +
//        "display: block; text-align: center;\n" + 
//        "position: running(footer);}\n" +
//        "div.content {page-break-after: always;}" +
//        "@page { }\n " +
//        "@page { @bottom-center { content: element(footer) }}\n" +
//        "</style></head>\n" +
//        "<body><div class='header'>Header</div><div class='footer'>Footer</div><div class='content'>Page1</div><div>Page2</div></body></html>";
		
		return htmlBuffer;
	}

//	public static void appendDefaultFooter(StringBuffer htmlBuffer) {
//		
//	}


	/**
	 * @param fontDir
	 * @param os
	 * @param html
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static void writePdf(CalipsoService calipso, OutputStream os,
			String html) throws DocumentException, IOException {
	    logger.info("writePdf html: "+html);
		writePdf(calipso.getFontsDirPath(), calipso.getResourcesDirPath(), os, html);
	}
	
	public static void writePdf(String fontDir, String resourcesBasePath, OutputStream os,
			String html) throws DocumentException, IOException {
		ITextRenderer renderer = new ITextRenderer();
		renderer.getFontResolver()
			.addFont(fontDir+File.separator+"ARIALUNI.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		if(StringUtils.isNotBlank(resourcesBasePath)){
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			    DocumentBuilder builder = factory.newDocumentBuilder();
			    InputSource is = new InputSource( new StringReader(html));
			    Document doc = builder.parse(is);
			    String rsPath = new File(resourcesBasePath+File.separator).toURI().toURL().toString();
			    renderer.setDocument(doc, rsPath);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		else{
			renderer.setDocumentFromString(html);
		}
		renderer.layout();
		renderer.createPDF(os);
	}

	/**
	 * @param fontDir
	 * @param os
	 * @param html
	 * @throws DocumentException
	 * @throws IOException
	 */
	@Deprecated
	public static void writePdf(String fontDir, OutputStream os,
			String html) throws DocumentException, IOException {
		writePdf(fontDir, null, os, html);
	}
    
}
