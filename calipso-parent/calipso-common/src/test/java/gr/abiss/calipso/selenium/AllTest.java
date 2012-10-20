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

package gr.abiss.calipso.selenium;

public class AllTest extends SeleniumTestCase {
        
    static {
        clazz = AllTest.class;
    }
    
    public AllTest(String name) {
        super(name);
    }
    
    public void testGetLoginPage() {
        selenium.open("http://localhost:8080/calipso/app/login");  
        assertEquals("Calipso Login", selenium.getTitle());
    }   
    
    public void testSuccessfulLogin() {   
        selenium.type("loginName", "admin");
        selenium.type("password", "admin");
        selenium.clickAndWait("//input[@value='Submit']");          
        assertTextPresent("DASHBOARD");
    }    
        
    public void testCreateNewSpaceAndAllocateAdmin() throws Exception {        
        selenium.clickAndWait("link=OPTIONS");        
        assertTextPresent("Options Menu");                
        selenium.clickAndWait("link=Manage Spaces");        
        assertTextPresent("Space List");                        
        selenium.clickAndWait("link=Create New Space");        
        assertTextPresent("Space Details");                        
        selenium.type("space.name", "Test Space");
        selenium.type("space.prefixCode", "TEST");
        selenium.clickAndWait("//input[@value='Next']");        
        assertTextPresent("Custom Fields for Space:");        
        selenium.clickAndWait("//input[@value='Next']");        
        assertTextPresent("Space Roles");        
        selenium.clickAndWait("//input[@value='Save']");        
        assertTextPresent("Users Allocated To Space");                
        selenium.clickAndWait("//input[@value='Allocate']");        
        assertTextPresent("Admin");             
    }  
    
    public void testCreateNewItem() throws Exception {        
        selenium.clickAndWait("link=DASHBOARD");
        assertTextPresent("Test Space");        
        selenium.clickAndWait("link=NEW");
        assertTextPresent("Summary");        
        selenium.type("summary", "Test Summary");
        selenium.type("detail", "Test Detail");
        selenium.select("hideAssignedTo:border:assignedTo", "Admin");        
        selenium.clickAndWait("//input[@value='Submit']");
        assertTextPresent("TEST-1");
    }

    public void testSearchAllContainsItem() throws Exception {        
        selenium.clickAndWait("link=SEARCH");
        assertTextPresent("Show History");        
        selenium.clickAndWait("//input[@value='Search']");
        assertTextPresent("1 Record Found");        
        selenium.clickAndWait("link=TEST-1");
        assertTextPresent("History");
    }
     
    public void testUpdateHistoryForItem() throws Exception {        
        selenium.select("status", "Closed");
        selenium.type("comment", "Test Comment");
        selenium.clickAndWait("//input[@value='Submit']");
        assertTextPresent("Test Comment");
        
    }

    public void testCreateNewUser() throws Exception {        
        selenium.clickAndWait("link=OPTIONS");                
        selenium.clickAndWait("link=Manage Users");
        assertTextPresent("Users and allocated Spaces");        
        selenium.clickAndWait("link=Create New User");
        assertTextPresent("User Details");        
        selenium.type("user.loginName", "testuser");
        selenium.type("user.name", "Test User");
        selenium.type("user.email", "foo@bar.com");
        selenium.clickAndWait("//input[@value='Submit']");      
        selenium.clickAndWait("//input[@value='Search']");
        assertTextPresent("Test User");    
    }
        
    public void testLogout() throws Exception {        
        selenium.clickAndWait("link=LOGOUT");
        assertTextPresent("Logout Successful");
        stopSelenium();        
    } 
    
}
