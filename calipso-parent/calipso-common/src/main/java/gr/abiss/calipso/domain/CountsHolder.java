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
 * Just wraps a Map of Counts keyed to Space ids
 * but adds logic for adding and iterative totalling
 */
public class CountsHolder implements Serializable {

    private Map<Long, Counts> counts = new HashMap<Long, Counts>();
    
    public void addLoggedByMe(long spaceId, long count) {
        add(Counts.LOGGED_BY_ME, spaceId, count);
    }
    
    public void addAssignedToMe(long spaceId, long count) {
        add(Counts.ASSIGNED_TO_ME, spaceId, count);
    }    
    
    public void addTotal(long spaceId, long count) {
        add(Counts.TOTAL, spaceId, count);
    }     
    
    public void addUnassigned(long spaceId, long count) {
        add(Counts.UNASSIGNED, spaceId, count);
    }     
    
    private void add(int type, long spaceId, long count) {
        Counts c = counts.get(spaceId);
        if (c == null) {
            c = new Counts(false);
            counts.put(spaceId, c);
        }
        c.add(type, -1, count);
    }
    
    private int getTotalForType(int type) {
        int total = 0;
        for(Counts c : counts.values()) {
            total += c.getTotalForType(type);
        }
        return total;
    }
    
    public int getTotalLoggedByMe() {
        return getTotalForType(Counts.LOGGED_BY_ME);
    }
        
    public int getTotalAssignedToMe() {
        return getTotalForType(Counts.ASSIGNED_TO_ME);
    }    
    
    public int getTotalTotal() {
        return getTotalForType(Counts.TOTAL);
    }    
    
    public int getTotalUnassigned() {
        return getTotalForType(Counts.UNASSIGNED);
    }   

    public Map<Long, Counts> getCounts() {
        return counts;
    }
    
}
