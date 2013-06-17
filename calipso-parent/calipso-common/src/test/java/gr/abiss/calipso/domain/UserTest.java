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


import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.springframework.security.GrantedAuthority;

public class UserTest extends TestCase {    
    
    public void testGetAuthoritiesFromUserSpaceRoles() {      
        
        Space s1 = new Space();
        s1.setPrefixCode("SPACE-ONE");                             

        User u = new User();
        u.setLoginName("test");        
        
//        u.addSpaceWithRole(s1, "ROLE_ONE-ONE");
//        u.addSpaceWithRole(s1, "ROLE_ONE-TWO");
//        u.addSpaceWithRole(null, "ROLE_ADMIN");
        u.setId(1);
        
        GrantedAuthority[] gas = u.getAuthorities();
        
        Set<String> set = new HashSet<String>();
        for(GrantedAuthority ga : gas) {
            set.add(ga.getAuthority());
        }        
                
        assertEquals(4, gas.length);
        
        assertTrue(set.contains("ROLE_USER"));
        assertTrue(set.contains("ROLE_ONE-ONE_SPACE-ONE"));
        assertTrue(set.contains("ROLE_ONE-TWO_SPACE-ONE"));
        assertTrue(set.contains("ROLE_ADMIN"));
     
    }
    
    public void testCheckIfAdminForAllSpaces() {
        User u = new User();
        u.setLoginName("test");
//        u.addSpaceWithRole(null, "ROLE_ADMIN");
        assertTrue(u.isGlobalAdmin());
    }
    
}
