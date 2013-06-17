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

package gr.abiss.calipso;

import gr.abiss.calipso.domain.Config;
import gr.abiss.calipso.domain.Counts;
import gr.abiss.calipso.domain.CountsHolder;
import gr.abiss.calipso.domain.Field;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.ItemItem;
import gr.abiss.calipso.domain.Metadata;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.State;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.UserSpaceRole;
import gr.abiss.calipso.util.ItemUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

/**
 * JUnit test cases for the business implementation as well as the DAO
 * Tests assume that a database is available, and with HSQLDB around this is not
 * an issue.
 */
public class CalipsoTest extends CalipsoTestBase {
    
    private Space getSpace() {
        Space space = new Space();
        space.setPrefixCode("TEST");
        space.setName("Test Space");
        return space;
    }
    
    private Metadata getMetadata() {
        Metadata metadata = new Metadata();
        String xmlString = "<metadata><fields>"
                + "<field name='cusInt01' label='Test Label'/>"
                + "<field name='cusInt02' label='Test Label 2'/>"
                + "</fields></metadata>";
        metadata.setXmlString(xmlString);
        return metadata;
    }
    
    //==========================================================================
    
    public void testGeneratedPasswordIsAlwaysDifferent() {
        String p1 = calipsoService.generatePassword();
        String p2 = calipsoService.generatePassword();
        assertTrue(!p1.equals(p2));
    }
    
    public void testEncodeClearTextPassword() {// ervis
        assertEquals("21232f297a57a5a743894a0e4a801fc3", calipsoService.encodeClearText("admintest"));
    }
    
    public void testMetadataInsertAndLoad() {
        Metadata m1 = getMetadata();
        calipsoService.storeMetadata(m1);
        assertTrue(m1.getId() > 0);
        Metadata m2 = calipsoService.loadMetadata(m1.getId());
        assertTrue(m2 != null);
        Map<Field.Name, Field> fields = m2.getFields();
        assertTrue(fields.size() == 2);
    }
    
    public void testUserInsertAndLoad() {
        User user = new User();
        user.setLoginName("test");
        user.setEmail("test@calipsoService.com");
        calipsoService.storeUser(user);
        User user1 = calipsoService.loadUser("test");
        assertTrue(user1.getEmail().equals("test@calipsoService.com"));
        User user2 = dao.findUsersByEmail("test@calipsoService.com").get(0);
        assertTrue(user2.getLoginName().equals("test"));
    }
    
    public void testUserSpaceRolesInsert() {
        Space space = getSpace();
        Metadata metadata = getMetadata();
        
        space.setMetadata(metadata);
        calipsoService.storeSpace(space);
        
        User user = new User();
        user.setLoginName("test");
        
//        user.addSpaceWithRole(space, "ROLE_TEST");
        calipsoService.storeUser(user);
        
        User u1 = calipsoService.loadUser("test");
        
        GrantedAuthority[] gas = u1.getAuthorities();
        assertEquals(2, gas.length);
        assertEquals("ROLE_USER", gas[0].getAuthority());
        assertEquals("ROLE_TEST_TEST", gas[1].getAuthority());
        
        List<UserSpaceRole> userSpaceRoles = calipsoService.findUserRolesForSpace(space.getId());
        assertEquals(1, userSpaceRoles.size());
        UserSpaceRole usr = userSpaceRoles.get(0);
        assertEquals("test", usr.getUser().getLoginName());
//        assertEquals("ROLE_TEST", usr.getRoleKey());
        
        List<User> users = calipsoService.findUsersForUser(u1);
        assertEquals(1, users.size());
        
        List<User> users2 = calipsoService.findUsersForSpace(space.getId());
        assertEquals(1, users2.size());
        
    }
    
    public void testConfigStoreAndLoad() {
        Config config = new Config("testParam", "testValue");
        calipsoService.storeConfig(config);
        String value = calipsoService.loadConfig("testParam");
        assertEquals("testValue", value);
    }
    
    public void testStoreAndLoadUserWithAdminRole() {
        User user = new User();
        user.setLoginName("test");
//        user.addSpaceWithRole(null, "ROLE_ADMIN");
        calipsoService.storeUser(user);
        
        UserDetails ud = calipsoService.loadUserByUsername("test");
        
        Set<String> set = new HashSet<String>();
        for (GrantedAuthority ga : ud.getAuthorities()) {
            set.add(ga.getAuthority());
        }
        
        assertEquals(2, set.size());
        assertTrue(set.contains("ROLE_USER"));
        assertTrue(set.contains("ROLE_ADMIN"));
        
    }
    
    public void testDefaultAdminUserHasAdminRole() {
        UserDetails ud = calipsoService.loadUserByUsername("admin");
        Set<String> set = new HashSet<String>();
        for (GrantedAuthority ga : ud.getAuthorities()) {
            set.add(ga.getAuthority());
        }
        assertEquals(2, set.size());
        assertTrue(set.contains("ROLE_USER"));
        assertTrue(set.contains("ROLE_ADMIN"));
    }
    
    public void testItemInsertAndCounts() {
        Space s = getSpace();
        calipsoService.storeSpace(s);
        User u = new User();
        u.setLoginName("test");
//        u.addSpaceWithRole(s, "DEFAULT");
        calipsoService.storeUser(u);
        Item i = new Item();
        i.setSpace(s);
        i.setAssignedTo(u);
        i.setLoggedBy(u);
        i.setStatus(State.CLOSED);
        calipsoService.storeItem(i, null);
        assertEquals(1, i.getSequenceNum());
        
        CountsHolder ch = calipsoService.loadCountsForUser(u);
        assertEquals(1, ch.getTotalAssignedToMe());
        assertEquals(1, ch.getTotalLoggedByMe());
        assertEquals(1, ch.getTotalTotal());
        
        Counts c = ch.getCounts().get(s.getId());
        assertEquals(1, c.getLoggedByMe());
        assertEquals(1, c.getAssignedToMe());
        assertEquals(1, c.getTotal());
    }
    
    public void testRemoveSpaceRoleDoesNotOrphanDatabaseRecord() {
        Space space = getSpace();
        calipsoService.storeSpace(space);
        long spaceId = space.getId();
        User user = new User();
        user.setLoginName("test");
//        user.addSpaceWithRole(space, "ROLE_ADMIN");
        calipsoService.storeUser(user);
        long id = jdbcTemplate.queryForLong("select id from user_space_roles where space_id = " + spaceId);
        UserSpaceRole usr = calipsoService.loadUserSpaceRole(id);
//        assertEquals(spaceId, usr.getSpace().getId());
        calipsoService.removeUserSpaceRole(usr);
        endTransaction();
        assertEquals(0, jdbcTemplate.queryForInt("select count(0) from user_space_roles where space_id = " + spaceId));
    }
    
    public void testFindSpacesWhereGuestAllowed() {
        Space space = getSpace();
        space.setItemVisibility(Space.ITEMS_VISIBLE_TO_ANONYMOUS_USERS);
        calipsoService.storeSpace(space);
        assertEquals(1, calipsoService.findSpacesWhereGuestAllowed().size());
    }
    
    public void testRenameSpaceRole() {
        Space space = getSpace();
        calipsoService.storeSpace(space);
        User u = new User();
        u.setLoginName("test");
//        u.addSpaceWithRole(space, "DEFAULT");
        calipsoService.storeUser(u);
        assertEquals(1, jdbcTemplate.queryForInt("select count(0) from user_space_roles where role_key = 'DEFAULT'"));
        calipsoService.bulkUpdateRenameSpaceRole(space, "DEFAULT", "NEWDEFAULT");
        assertEquals(0, jdbcTemplate.queryForInt("select count(0) from user_space_roles where role_key = 'DEFAULT'"));
        assertEquals(1, jdbcTemplate.queryForInt("select count(0) from user_space_roles where role_key = 'NEWDEFAULT'"));
    }
    
    public void testGetItemAsHtmlDoesNotThrowException() {
        Config config = new Config("mail.server.host", "dummyhost");
        calipsoService.storeConfig(config);
        // now email sending is switched on
        Space s = getSpace();
        calipsoService.storeSpace(s);
        User u = new User();
        u.setLoginName("test");
        u.setName("Test User");
        u.setEmail("test");
//        u.addSpaceWithRole(s, "DEFAULT");
        calipsoService.storeUser(u);
        Item i = new Item();
        i.setSpace(s);
        i.setAssignedTo(u);
        i.setLoggedBy(u);
        i.setStatus(State.CLOSED);
        // next step will internally try to render item as Html for sending e-mail
        calipsoService.storeItem(i, null);
        System.out.println(ItemUtils.getAsXml(i).asXML());
    }
    
    public void testDeleteItemThatHasRelatedItems() {
        Space s = getSpace();
        calipsoService.storeSpace(s);
        User u = new User();
        u.setLoginName("test");
        u.setEmail("dummy");
//        u.addSpaceWithRole(s, "DEFAULT");
        calipsoService.storeUser(u);
        //========================
        Item i0 = new Item();
        i0.setSpace(s);
        i0.setAssignedTo(u);
        i0.setLoggedBy(u);
        i0.setStatus(State.CLOSED);
        calipsoService.storeItem(i0, null);
        //=======================
        Item i1 = new Item();
        i1.setSpace(s);
        i1.setAssignedTo(u);
        i1.setLoggedBy(u);
        i1.setStatus(State.CLOSED);
        i1.addRelated(i0, ItemItem.DEPENDS_ON);
        calipsoService.storeItem(i1, null);
        //========================
        Item i2 = new Item();
        i2.setSpace(s);
        i2.setAssignedTo(u);
        i2.setLoggedBy(u);
        i2.setStatus(State.CLOSED);
        i2.addRelated(i1, ItemItem.DUPLICATE_OF);
        calipsoService.storeItem(i2, null);
        assertEquals(3, calipsoService.loadCountOfHistoryInvolvingUser(u));
        // can we remove i1?
        Item temp = calipsoService.loadItem(i1.getId());
        calipsoService.removeItem(temp);
    }
    
}
