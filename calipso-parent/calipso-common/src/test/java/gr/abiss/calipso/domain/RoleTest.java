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
import gr.abiss.calipso.domain.Role;
import gr.abiss.calipso.domain.State;
import gr.abiss.calipso.util.XmlUtils;

import javax.naming.Name;
import junit.framework.TestCase;
import org.dom4j.Document;

public class RoleTest extends TestCase {
    
    public void testConstructFromXml() {
        Document d = XmlUtils.parse("<role name='TESTER'>" +
            "<state status='1'>" +
                "<transition status='2'/>" +
                "<transition status='3'/>" +
                "<field name='cusInt01' mask='1'/>" + 
                "<field name='cusInt02' mask='2'/>" +
            "</state>" +                
            "<state status='2'>" + 
                "<transition status='3'/>" +
                "<field name='cusInt03' mask='1'/>" + 
                "<field name='cusInt04' mask='2'/>" +
            "</state></role>");
        Role role = new Role(d.getRootElement());
        assertEquals("TESTER", role.getName());
        assertEquals(2, role.getStates().size());
        State s1 = role.getStates().get(1);
        assertEquals(2, s1.getTransitions().size());
        assertTrue(s1.getTransitions().contains(2));
        assertTrue(s1.getTransitions().contains(3));
        assertEquals(2 , s1.getFields().size());
        assertEquals(new Integer(1), s1.getFields().get(Field.Name.CUS_INT_01));
        assertEquals(new Integer(2), s1.getFields().get(Field.Name.CUS_INT_02));
    }    
}
