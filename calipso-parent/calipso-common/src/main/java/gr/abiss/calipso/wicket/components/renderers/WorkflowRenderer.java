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

package gr.abiss.calipso.wicket.components.renderers;

import gr.abiss.calipso.domain.Role;
import gr.abiss.calipso.domain.SpaceRole;
import gr.abiss.calipso.domain.State;
import gr.abiss.calipso.util.XmlUtils;
import gr.abiss.calipso.wicket.BasePanel;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * Class that is used to render a workflow to the GUI
 * currently designed to render static HTML that is a crude representation of the workflow
 * TODO move this logic into the Role classes having better OO
 */
public class WorkflowRenderer implements Serializable {
	private static final Logger logger = Logger.getLogger(WorkflowRenderer.class);
    
    private Map<String, Role> rolesMap;
    private Map<String, Set<String>> transitionRoles;    
    private Map<Integer, Set<Integer>> stateTransitions;
    private Map<Integer, String> stateNames;
    private Document document;
    private Map<String, SpaceRole> spaceRoleMap;
    
    public WorkflowRenderer(Map<String, Role> rolesMap, Map<Integer, String> stateNames, Map<String, SpaceRole> spaceRoleMap) {
        this.rolesMap = rolesMap;
        this.stateNames = stateNames;
        this.spaceRoleMap = spaceRoleMap;
        init();
    }
    
    private void init() {
        // transitions <--> roleNames map
        // the key is a string concatenation <fromstate>_<tostate> for convenience
        transitionRoles = new TreeMap<String, Set<String>>();
        // for each state <--> a union of transitions across all roles
        stateTransitions = new TreeMap<Integer, Set<Integer>>();
        for(Role r : rolesMap.values()) {
            for(State s: r.getStates().values()) {
                Set<Integer> transitions = stateTransitions.get(s.getStatus());
                if (transitions == null) {
                    transitions = new HashSet<Integer>();
                    stateTransitions.put(s.getStatus(), transitions);                    
                }
                transitions.addAll(s.getTransitions());
                for (int i : s.getTransitions()) {
                    String transitionKey = s.getStatus() + "_" + i;
                    Set<String> roleNames = transitionRoles.get(transitionKey);
                    if (roleNames == null) {
                        roleNames = new HashSet<String>();
                        transitionRoles.put(transitionKey, roleNames);
                    }
                    roleNames.add(r.getName());
                }
            }
        }
        document = XmlUtils.getNewDocument("state");
        Element e = document.getRootElement();
        e.addAttribute("name", stateNames.get(State.NEW));
        e.addAttribute("key", State.NEW + "");
        addTransitions(e, State.NEW);
    }
    
    /* has the state already been added to the tree? */
    private boolean stateExists(int key) {
        return document.selectNodes("//state[@key='" + key + "']").size() > 0;
    }
    
    /* main recursive function */
    private void addTransitions(Element parent, int state) {
        Set<Integer> transitions = stateTransitions.get(state);
        if (transitions == null) {
            return;
        }
        for(int i : transitions) {
            boolean exists = stateExists(i);
            Element child = parent.addElement("state");
            child.addAttribute("name", stateNames.get(i));
            child.addAttribute("key", i + "");
            if (exists) {
                child.addAttribute("mirror", "true");
            } else {
                addTransitions(child, i);
            }            
        }        
    }

    public Document getDocument() {
        return document;
    }
    
    public String getAsString() {
        return XmlUtils.getAsPrettyXml(document);
    }
    
    private String getAsHtml(Element e) {
        StringBuffer sb = new StringBuffer();        
        List<Element> childElements = (List<Element>) e.elements();
        String stateClass = e.attributeValue("mirror") != null ? "mirror" : "state";
        sb.append("<!-- begin:WorkflowRenderer --><table class=\"workflow\" cellspacing=\"0\" cellpadding=\"0\"><tr><td rowspan=\"" + childElements.size() + "\" class=\"" + stateClass + "\">");        
        sb.append(e.attributeValue("name"));        
        sb.append("</td>");
        boolean first = true;
        String fromState = e.attributeValue("key");
        for(Element child : childElements) {
            if (!first) {
                sb.append("<tr>");
            }
            String toState = child.attributeValue("key");
            sb.append("<td class=\"transition\">");
            for(String roleKey : transitionRoles.get(fromState + "_" + toState)) {
                // tough validation: look forward, can this role actually transition from the "toState"?
                // if not indicate as error (red font)
            	logger.debug("toState: "+toState+", roleKey: "+roleKey);
                Role role = rolesMap.get(roleKey);
                int toStateKey = Integer.parseInt(toState);
                SpaceRole sr = spaceRoleMap.get(roleKey);
                logger.debug("toStateKey: "+toStateKey+", sr: "+sr);
                String description = sr!=null?sr.getDescription():null;
                if(toStateKey != State.CLOSED && !role.hasTransitionsFromState(toStateKey)) {
                    sb.append("<span class=\"error\">").append(description).append("</span>");
                } else {
                    sb.append(spaceRoleMap.get(roleKey)!=null?description:"null for "+roleKey);
                }
                sb.append("<br />");
            }
            sb.append("</td><td>");
            sb.append(getAsHtml(child));
            sb.append("</td></tr>");
            if (first) {
              first = false;  
            } 
        }        
        sb.append("</table><!-- end:WorkflowRenderer -->");        
        return sb.toString();
    }
    
    public String getAsHtml() {
        return getAsHtml(document.getRootElement());
    }
}
