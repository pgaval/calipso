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

import static gr.abiss.calipso.Constants.*;

import gr.abiss.calipso.util.XmlUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.dom4j.Element;

/**
 * In addition to definition of custom fields, the Metadata
 * for a Space may contain a bunch of Role defintions as well.
 * Roles do the following
 * - define the State Transitions possible (i.e. from status --> to status)
 * - for each State (from status) define the access permissions that this Role has per Field
 */
public class Role implements Serializable {
    
    private String name;
//    private String description;
    private Map<Integer, State> states = new HashMap<Integer, State>();
    
    public Role(String name) {
        this.name = name;
    }
    
    public Role(Element e) {
        name = e.attributeValue(NAME);
        for (Object o : e.elements(STATE)) {
            State state = new State((Element) o);
            states.put(state.getStatus(), state);
        }
    }
    
    /* append this object onto an existing XML document */
    public void addAsChildOf(Element parent) {
        Element e = parent.addElement(ROLE);
        copyTo(e);
    }
    
    /* marshal this object into a fresh new XML Element */
    public Element getAsElement() {
        Element e = XmlUtils.getNewElement(ROLE);
        copyTo(e);
        return e;
    }
    
    /* copy object values into an existing XML Element */
    private void copyTo(Element e) {
        // appending empty strings to create new objects for "clone" support
        e.addAttribute(NAME, name + "");
        for (State state : states.values()) {
            state.addAsChildOf(e);
        }
    }

    //=======================================================================

    public void add(State state) {
        states.put(state.getStatus(), state);
    }

    public void removeState(int stateId) {
        states.remove(stateId);
        for(State s : states.values()) {
            s.removeTransition(stateId);
        }
    }

    public boolean hasTransitionsFromState(int stateKey) {        
        return states.get(stateKey).getTransitions().size() > 0;
    }
    
    //=======================================================================
    
    public Map<Integer, State> getStates() {
        return states;
    }
    
    public void setStates(Map<Integer, State> states) {
        this.states = states;
    }
    
//    public String getDescription() {
//        return description;
//    }
//    
//    public void setDescription(String description) {
//        this.description = description;
//    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("name", name)
				.append("states", states)
				.toString();
	}    
}
