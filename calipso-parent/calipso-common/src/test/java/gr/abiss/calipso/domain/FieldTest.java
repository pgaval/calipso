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
 * This file incorporates work released by the JTrac project and covered 
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

package gr.abiss.calipso.domain;

import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.util.XmlUtils;
import junit.framework.TestCase;
import org.dom4j.Document;
import org.dom4j.Element;

public class FieldTest extends TestCase {
    
    public void testSetValidName() {
        Field field = new Field();
        field.setName("cusInt01");
        assertEquals(field.getName().toString(), "cusInt01");        
    }
    
    public void testSetInValidNameFails() {
        Field field = new Field();
        try {
            field.setName("foo");
            fail("How did we set an invalid name?");
        } catch (Exception e) {
            // expected
        }        
    }    
    
    public void testConstructFromXml() {
        Document d = XmlUtils.parse("<field name='cusInt01' label='Test Label'/>");
        Field field = new Field(d.getRootElement());
        assertEquals("cusInt01", field.getName().toString());
        assertEquals("Test Label", field.getLabel());
        assertEquals(field.isOptional(), false);
    }
    
    public void testConstructFromXmlWithOptionalAttribute() {
        Document d = XmlUtils.parse("<field name='cusInt01' label='Test Label' optional='true'/>");
        Field field = new Field(d.getRootElement());
        assertTrue(field.isOptional());
    }
    
    public void testGetAsXml() {
        Field field = new Field();
        field.setName("cusInt01");
        field.setLabel("Test Label");        
        Element e = field.getAsElement();
        assertEquals("cusInt01", e.attributeValue("name"));
        assertEquals("Test Label", e.attributeValue("label"));    
    }  
    
}
