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

package gr.abiss.calipso.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Object that holds statistics for items within a single space
 * a map of these would serve as the model for the dashboard view
 * contains logic for totalling etc.
 */
public class Counts implements Serializable {

    public static final int ASSIGNED_TO_ME = 1;
    public static final int LOGGED_BY_ME = 2;
    public static final int TOTAL = 3;
    public static final int UNASSIGNED = 4;
    
    private Map<Integer, Map<Integer, Long>> typeCounts = new HashMap<Integer, Map<Integer, Long>>();     
    
    private boolean detailed;  
    
    public boolean isDetailed() {
        return detailed;
    }
    
    public Counts(boolean detailed) {        
        this.detailed = detailed;
        
        typeCounts.put(ASSIGNED_TO_ME, new HashMap<Integer, Long>());
        typeCounts.put(LOGGED_BY_ME, new HashMap<Integer, Long>());
        typeCounts.put(TOTAL, new HashMap<Integer, Long>());
        typeCounts.put(UNASSIGNED, new HashMap<Integer, Long>());
    }
    
    public void addLoggedByMe(int state, long count) {
        add(LOGGED_BY_ME, state, count);
    }
    
    public void addAssignedToMe(int state, long count) {
        add(ASSIGNED_TO_ME, state, count);
    }    
    
    public void addTotal(int state, long count) {
        add(TOTAL, state, count);
    }
    
    public void addUnassigned(int state, long count) {
        add(UNASSIGNED, state, count);
    }
    
    protected void add(int type, int state, long count) {
        Map<Integer, Long> stateCounts = typeCounts.get(type);
        Long i = stateCounts.get(state);
        if (i == null) {            
            stateCounts.put(state, count);
        } else {
            stateCounts.put(state, i + count);
        }
    }  
    
    protected int getTotalForType(int type) {
        Map<Integer, Long> stateCounts = typeCounts.get(type);
        if (stateCounts == null) {
            return 0;
        }
        int total = 0;
        for(Map.Entry<Integer, Long> entry : stateCounts.entrySet()) {
            total += entry.getValue();
        }
        return total;
    }
    
    public int getLoggedByMe() {
        return getTotalForType(LOGGED_BY_ME);
    }     
    
    public int getAssignedToMe() {
        return getTotalForType(ASSIGNED_TO_ME);
    }    
    
    public int getTotal() {
        return getTotalForType(TOTAL);
    }
    
    public int getUnassigned() {
        return getTotalForType(UNASSIGNED);
    }    
    
    public Map<Integer, Long> getLoggedByMeMap() {
        return typeCounts.get(LOGGED_BY_ME);
    }
    
    public Map<Integer, Long> getAssignedToMeMap() {
        return typeCounts.get(ASSIGNED_TO_ME);
    } 
    
    public Map<Integer, Long> getTotalMap() {
        return typeCounts.get(TOTAL);
    }
    
    public Map<Integer, Long> getUnassignedMap() {
        return typeCounts.get(UNASSIGNED);
    }
    
    // return string for easier rendering on dashboard screen    
    public Long getLoggedByMeForState(int stateKey) {
        Long i = typeCounts.get(LOGGED_BY_ME).get(stateKey);
        return i;
    }
    
    public Long getAssignedToMeForState(int stateKey) {
        Long i = typeCounts.get(ASSIGNED_TO_ME).get(stateKey);
        return i;
    } 
    
    public Long getTotalForState(int stateKey) {
        Long i = typeCounts.get(TOTAL).get(stateKey);
        return i;
    }   
    
    public Long getUnassignedForState(int stateKey) {
        Long i = typeCounts.get(UNASSIGNED).get(stateKey);
        return i;
    } 
    
    @Deprecated
    public String getLoggedByMeForState_(int stateKey) {
        Long i = typeCounts.get(LOGGED_BY_ME).get(stateKey);
        return i == null ? "" : i.toString();
    }
    @Deprecated
    public String getAssignedToMeForState_(int stateKey) {
        Long i = typeCounts.get(ASSIGNED_TO_ME).get(stateKey);
        return i == null ? "" : i.toString();
    } 
    @Deprecated
    public String getTotalForState_(int stateKey) {
        Long i = typeCounts.get(TOTAL).get(stateKey);
        return i == null ? "" : i.toString();
    }    
    
}
