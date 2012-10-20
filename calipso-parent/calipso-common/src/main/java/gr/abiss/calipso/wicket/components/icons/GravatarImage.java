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
 * This file incorporates work released by the Mystic Coders, LLC and  
 * is covered by the following copyright and permission notice:  
 * 
 *   Copyright 2009 Mystic Coders, LLC (http://www.mysticcoders.com)
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

package gr.abiss.calipso.wicket.components.icons;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;


/**
 * Simple Web Component that must be attached to an HTML tag of type 'img'.
 * <p>
 * Supply a model which contains the email address and an optional size attribute
 * which is auto-scaled proportional.
 * </p>
 * <p>
 * See here for more info on setting up Gravatar URL's:
 * http://en.gravatar.com/site/implement/url
 * </p>
 * <p>
 * Java:
 * </p>
 * <pre>
 *      // when you have a hash ready there is no reason 
 *      // to build it again
 *      String emailHash = user.getEmailHash();
 *      add(new GravatarImage("image", new Model(emailHash);
 *      // otherwise just let GravatarImage do it
 *      // add(new GravatarImage("image", new Model("email@domain.com"));
 * </pre>
 * <p/>
 * HTML:
 * <pre>
 *      &lt;img wicket:id=&quot;image&quot; /&gt;
 * </pre>
 *
 * @author Andrew Lombardi (andrew@mysticcoders.com)
 */
public class GravatarImage extends WebComponent {

    private static final int DEFAULT_HSIZE = 44;

    public GravatarImage(String id, String emailHash) {
        this(id, emailHash, DEFAULT_HSIZE);
    }

    public GravatarImage(String id, String emailHash, int hsize) {
        super(id);
        setDefaultModel(new GravatarModel(emailHash, hsize));
    }
    public GravatarImage(String id, IModel<String> model) {
        this(id, model, DEFAULT_HSIZE);
    }

    public GravatarImage(String id, IModel<String> model, int hsize) {
        super(id);
        setDefaultModel(new GravatarModel(model, hsize));
    }

    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        checkComponentTag(tag, "img");
        tag.put("src", getDefaultModelObjectAsString());
    }


}