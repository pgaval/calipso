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

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.AssetType;
import gr.abiss.calipso.domain.ItemRenderingTemplate;
import gr.abiss.calipso.domain.ItemUser;
import gr.abiss.calipso.domain.RoleSpaceStdField;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.StdField;
import gr.abiss.calipso.domain.StdFieldMask;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.UserSpaceRole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;

/**
 * routines to filter User, UserSpaceRoles collections etc
 */
public class UserUtils {
	
	private static final Logger logger = Logger.getLogger(UserUtils.class);
    /**
     * This is a rather 'deep' concept, first of course you need to restrict the next possible
     * states that an item can be switched to based on the current state and the workflow defined.
     * But what about who all it can be assigned to?  This will be the set of users who fall into roles
     * that have permissions to transition FROM the state being switched to. Ouch.
     * This is why the item_view / history update screen has to be Ajaxed so that the drop
     * down list of users has to dynamically change based on the TO state
     */
    public static List<User> filterUsersAbleToTransitionFrom(List<UserSpaceRole> userSpaceRoles, Space space, int state) {
        return filterUsersAbleToTransitionFrom(userSpaceRoles, space, state, true);
    }

    public static List<User> filterUsersAbleToTransitionFrom(List<UserSpaceRole> userSpaceRoles, Space space, int state, boolean uniqueUsers) {
        Set<String> set = space.getMetadata().getRolesAbleToTransitionFrom(state);
        List<User> list = new ArrayList<User>(userSpaceRoles.size());
        for (UserSpaceRole userSpaceRole : userSpaceRoles) {
        	if (set.contains(userSpaceRole.getSpaceRole().getRoleCode())) {
        		if(!list.contains(userSpaceRole.getUser())){//must use unique users so check if a user is in the list
        			list.add(userSpaceRole.getUser());
        		}        	
            }
        }
        return list;
    }
    
    /**
     * used to init backing form object in wicket corresponding to ItemUser / notifyList
     */
    public static List<ItemUser> convertToItemUserList(List<UserSpaceRole> userSpaceRoles) {
        List<ItemUser> itemUsers = new ArrayList<ItemUser>(userSpaceRoles.size());
        Set<User> users = new HashSet<User>(itemUsers.size());
        for (UserSpaceRole usr : userSpaceRoles) {
            User user = usr.getUser();
            // we need to do this check as now JTrac supports same user mapped
            // more than once to a space with different roles
            if (!users.contains(user)) {
                users.add(user);
                itemUsers.add(new ItemUser(user));
            }
        }
        return itemUsers;
    }

    /**
     * used to prepare drop down lists for the search screen in the ui
     */
    public static Map<Long, String> getSpaceNamesMap(User user) {
        Map<Long, String> map = new HashMap<Long, String>(user.getUserSpaceRoles().size());
        for (UserSpaceRole usr : user.getUserSpaceRoles()) {
//            if (usr.getSpace() != null) {
//                map.put(usr.getSpace().getId(), usr.getSpace().getName());
//            }
            if (usr.getSpaceRole().getSpace() != null) {
                map.put(usr.getSpaceRole().getSpace().getId(), usr.getSpaceRole().getSpace().getName());
            }
        	
        }
        return map;
    }

    
	public static User getSpaceAdmin(List<User> users, Space space){
		
		for (User user : users){
			if (user.isSpaceAdmin(space)){
				return user;
			}
		}
		
		return null;
	}
	
	public static boolean canViewItems(User user, Space space){
		if(user.isAnonymous()){
			return space.getItemVisibility().equals(Space.ITEMS_VISIBLE_TO_ANONYMOUS_USERS);
		}
		else{
			return user.hasRegularRoleForSpace(space) 
					|| space.getItemVisibility().equals(Space.ITEMS_VISIBLE_TO_LOGGEDIN_REPORTERS)
					|| space.getItemVisibility().equals(Space.ITEMS_VISIBLE_TO_LOGGEDIN_REPORTERS_NO_COMMENTS)
					|| (space.getItemVisibility().equals(Space.ITEMS_VISIBLE_TO_ANY_LOGGEDIN_USER));	
		}
		
	}
	
    public static boolean canViewSpaceAssets(User currentUser, Space currentSpace, CalipsoService calipso){
    	boolean canView = false;
    	logger.info("currentSpace.isAssetEnabled(): "+currentSpace.isAssetEnabled());
    	logger.info("currentSpace.isAssetEnabled(): "+currentSpace.isAssetEnabled());
    	if(currentSpace.isAssetEnabled()){
			List<AssetType> visibleAssetTypes = calipso.findAllAssetTypesForSpace(currentSpace);
	    	logger.info("visibleAssetTypes: "+visibleAssetTypes);
	    	logger.info("currentUser.hasRegularRoleForSpace(currentSpace): "+currentUser.hasRegularRoleForSpace(currentSpace));
			canView = CollectionUtils.isNotEmpty(visibleAssetTypes) && currentUser.hasRegularRoleForSpace(currentSpace);
    	}
    	return canView;
    }
}
