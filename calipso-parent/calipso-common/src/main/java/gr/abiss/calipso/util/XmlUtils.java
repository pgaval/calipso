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
 * 
 * This file incorporates work released by the JTrac project and  covered 
 * by the following copyright and permission notice:  
 * 
 *   Copyright 2002-2005 the original author or authors.
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *   
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package gr.abiss.calipso.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;


import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;
import org.ccil.cowan.tagsoup.Schema;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.xerox.adoc.dexss.DeXSS;


/**
 * Utilities to parse strings into XML DOM Documents and vice versa
 */
public final class XmlUtils {
    private static final Schema HTML_SCHEMA = new HTMLSchema();

    private Parser createParser() {
        Parser parser = new Parser();
        try {
        	// see https://issues.apache.org/jira/browse/TIKA-599 
			parser.setProperty(Parser.schemaProperty, HTML_SCHEMA);
	        parser.setFeature(Parser.ignoreBogonsFeature, true);
		} catch (SAXNotRecognizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return parser;
    } 
	
	//------------------------------------------------------------------------
	/**
	 * Escape, but do not replace HTML.
	 * The default behaviour is to escape ampersands.
	 */
	public static String escapeHTML(String s) {
	    return escapeHTML(s, true);
	}
	
	public static String removeComments(String s) {
		return s != null ? s.replaceAll("(?s)<!--.*?-->", "") : s;
	}
	//------------------------------------------------------------------------
	/**
	 * Escape, but do not replace HTML.
	 * @param escapeAmpersand Optionally escape
	 * ampersands (&amp;).
	 */
	public static String escapeHTML(String s, boolean escapeAmpersand) {
	    // got to do amp's first so we don't double escape
	    if (escapeAmpersand) {
	        s = StringUtils.replace(s, "&", "&amp;");
	    }
	    s = StringUtils.replace(s, "&nbsp;", " ");
	    s = StringUtils.replace(s, "\"", "&quot;");
	    s = StringUtils.replace(s, "<", "&lt;");
	    s = StringUtils.replace(s, ">", "&gt;");
	    return s;
	}
 

    
    /**
     * uses Dom4j to neatly format a given XML string
     * by adding indents, newlines etc.
     */
    public static String getAsPrettyXml(String xmlString) {
        return xmlString != null ? getAsPrettyXml(parse(xmlString)) : null;
    }
    
    /**
     * Override that accepts an XML DOM Document
     * @param document XML as DOM Document
     */
    public static String getAsPrettyXml(Document document) {
        OutputFormat format = new OutputFormat(" ", true);
        format.setSuppressDeclaration(true);
        StringWriter out = new StringWriter();
        XMLWriter writer = new XMLWriter(out, format);
        try {
            try {
                writer.write(document);
            } finally {
                writer.close();
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return out.toString().trim();
    }
    
    /**
     * Converts a String into XML by parsing into a DOM Document
     * uses Dom4j
     */
    public static Document parse(String xmlString) {
        try {
            return DocumentHelper.parseText(xmlString);
        } catch (DocumentException de) {
            throw new RuntimeException(de);
        }
    }
    
    public static Element getNewElement(String name) {
        return DocumentHelper.createElement(name);
    }
    
    public static Document getNewDocument(String rootElementName) {
        Document d = DocumentHelper.createDocument();
        d.addElement(rootElementName);
        return d;
    }
    
    public static Document transform(Document source, Document stylesheet) {
        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            Transformer transformer = factory.newTransformer(new DocumentSource(stylesheet));
            DocumentResult result = new DocumentResult();
            transformer.transform(new DocumentSource(source), result);
            return result.getDocument();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static String replace(String string, Pattern pattern, String replacement) {
        Matcher m = pattern.matcher(string);
        return m.replaceAll(replacement);
    }

    public static String stripTags(String html){
    	return html != null ? html.replaceAll("\\<.*?>","") : null;
    }
    
    /**
     * Removes XSS threats from input HTML
     *
     * @param s   the String to transform
     * @return    the transformed String
     */
    public static String removeXss(String s) {
        if (s == null) {
            return null;
        }
        Writer sw = new StringWriter(); // ignore output
		try {
			DeXSS xss = DeXSS.createInstance(null, sw);
	        xss.process(s);
	        sw.close();
	        s = sw.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

    	//System.out.println("Output HTML: \n"+s);
        return s;
    }

}
