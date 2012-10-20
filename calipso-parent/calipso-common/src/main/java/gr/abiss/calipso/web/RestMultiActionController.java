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

package gr.abiss.calipso.web;

import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.exception.InvalidRefIdException;
import gr.abiss.calipso.util.ItemUtils;
import gr.abiss.calipso.util.XmlUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.mvc.multiaction.MethodNameResolver;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

/**
 * Spring MultiActionController that handles REST requests
 * returns XML messages
 */
public class RestMultiActionController extends AbstractMultiActionController {
    
    /**
     * custom MethodNameResolver is configured that checks the value of an expected
     * paramter called "method" in the request and formats the value that may be
     * in the form of  "namespace.subnamespace.action" into "namespaceSubnamespaceAction"
     * or more like a java method name
     */
    public RestMultiActionController() {
        setMethodNameResolver(new MethodNameResolver() {
            public String getHandlerMethodName(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
                String temp = request.getParameter("method");
                if (temp == null) {
                    return null;
                }
                StringBuffer sb = new StringBuffer();
                for(int i = 0; i < temp.length(); i++) {
                    char c = temp.charAt(i);
                    if (c == '.') {
                        i++;
                        c = temp.charAt(i);
                        sb.append(Character.toUpperCase(c));
                    } else {
                        sb.append(c);
                    }
                }
                return sb.toString();
            }
        });
    }
    
    private void writeXml(Document document, HttpServletResponse response) throws Exception {
        applyCacheSeconds(response, 0, true);
        response.setContentType("text/xml");
        document.write(response.getWriter());
    }
    
    private String getContent(HttpServletRequest request) throws Exception {
        InputStream is = request.getInputStream();        
        int ch;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((ch = is.read()) != -1) {
            baos.write((byte) ch);
        }
        return new String(baos.toByteArray());
    }
        
    public void versionGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Document d = XmlUtils.getNewDocument("version");
        Element root = d.getRootElement();
        root.addAttribute("number", calipsoService.getReleaseVersion());
        root.addAttribute("timestamp", calipsoService.getReleaseTimestamp());
        writeXml(d, response);
    }
    
    public void itemGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String refId = request.getParameter("refId");
        Item item = null;
        try {
            item = calipsoService.loadItemByRefId(refId);
        } catch (InvalidRefIdException e) {
            // TODO
        }
        // TODO if item == null
        if (item == null) {
            return;
        }
        Document d = ItemUtils.getAsXml(item);
        writeXml(d, response);
    }
    
    public void itemPut(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug(getContent(request));
        Document d = XmlUtils.getNewDocument("success");
        Element root = d.getRootElement();
        root.addElement("refId").addText("FOOBAR-123");
        writeXml(d, response);      
    }
    
    public void spaceUsersGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String prefixCode = request.getParameter("prefixCode");
        Space space = calipsoService.loadSpace(prefixCode);
        Document d = XmlUtils.getNewDocument("users");
        Element root = d.getRootElement();
        root.addAttribute("prefixCode", prefixCode);
        List<User> users = calipsoService.findUsersForSpace(space.getId());
        for(User user : users) {
            root.addElement("user").addAttribute("loginName", user.getLoginName()).addText(user.getName());
        }
        writeXml(d, response);
    }
    
    
}
