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

package gr.abiss.calipso.util;

import gr.abiss.calipso.util.XmlUtils;
import junit.framework.TestCase;
import org.dom4j.Document;

public class XmlUtilsTest extends TestCase {
    
    public void testXmlStringParse() {
        String s = "<test/>";
        Document d = XmlUtils.parse(s);
        assertTrue(d.getRootElement().getName().equals("test"));
    }
    
    public void testBadXmlParseFails() {
        String s = "foo";
        try {
            Document d = XmlUtils.parse(s);
            fail("How did we parse invalid XML?");
        } catch (Exception e) {
            // expected
        }        
    }
    
    public void testGetAsPrettyXml() {
        String s = "<root><node1><node2>data</node2></node1></root>";
        String result = XmlUtils.getAsPrettyXml(s);
        assertTrue(result.equals("<root>\n <node1>\n  <node2>data</node2>\n </node1>\n</root>"));        
    }
    
}
