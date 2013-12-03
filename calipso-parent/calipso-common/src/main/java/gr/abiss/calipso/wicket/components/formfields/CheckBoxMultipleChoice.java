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

package gr.abiss.calipso.wicket.components.formfields;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.util.string.Strings;

/**
 * custom multo select list / check box control that
 * is scrollable and selected entries "float" to the top
 */
public class CheckBoxMultipleChoice extends ListMultipleChoice {

	protected static final Logger logger = Logger.getLogger(CheckBoxMultipleChoice.class);
    private boolean isForSet;
    
    public CheckBoxMultipleChoice(String id, List choices, IChoiceRenderer renderer) {
        super(id, choices, renderer);
    }
    
    public CheckBoxMultipleChoice(String id, List choices, IChoiceRenderer renderer, boolean isForSet) {
        this(id, choices, renderer);
        this.isForSet = isForSet;
    }
    
    @Override
    protected Collection convertValue(String[] ids) {
        List list = (List) super.convertValue(ids);
        if(isForSet) {
            return new HashSet(list);
        } else {
            return list;
        }
    }
    
    /**
     * code adapted from onComponentTagBody implementation of wicket's built-in
     * CheckBoxMultipleChoice component
     */
    @Override
    public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
        
        final List choices = getChoices();
        boolean scrollable = choices.size() > 6;
        
        final StringBuilder buffer = new StringBuilder();
        final StringBuilder selectedBuffer = new StringBuilder();
        
        if(scrollable) {
            selectedBuffer.append("<div class=\"multiselect scrollable\">");
        } else {
            selectedBuffer.append("<div class=\"multiselect\">");
        }
        
        final String selected = getValue();
        
        boolean hasSelected = false;
        
        Locale locale = getLocale();
        
        for (int index = 0; index < choices.size(); index++) {
            
            final Object choice = choices.get(index);
            IChoiceRenderer choiceRenderer = getChoiceRenderer();
			// logger.info("choiceRenderer: "+choiceRenderer);
			// logger.info("choice: "+choice);
			// logger.info(".getDisplayValue(choice): "+choiceRenderer.getDisplayValue(choice));
			//
            final String label = getConverter(String.class).convertToString(
            		choiceRenderer.getDisplayValue(choice).toString(), 
            		locale);
            
            if (label != null) {
                
                String id = getChoiceRenderer().getIdValue(choice, index);
                final String idAttr = getInputName() + "_" + id;
                
                String display = label;
                if (localizeDisplayValues()) {
                    display = getLocalizer().getString(label, this, label);
                }
                CharSequence escaped = Strings.escapeMarkup(display, false, true);
                boolean isSelected = false;
                StringBuilder whichBuffer = buffer;                
                if(isSelected(choice, index, selected)) {
                    isSelected = true;                    
                    if(scrollable) {
                        hasSelected = true;
                        whichBuffer = selectedBuffer;
                    }
                }
                whichBuffer.append("<input name=\"").append(getInputName()).append("\"").append(" type=\"checkbox\"")
                            .append(isSelected ? " checked=\"checked\"" : "")
                            .append((isEnabled() ? "" : " disabled=\"disabled\"")).append(" value=\"")
                            .append(id).append("\" id=\"").append(idAttr).append("\"/>").append("<label for=\"")
                            .append(idAttr).append("\">").append(escaped).append("</label>").append("<br />");
            }
        }
        
        if(hasSelected) {
            selectedBuffer.append("<hr />");
        }
        
        selectedBuffer.append(buffer).append("</div>");
        
        replaceComponentTagBody(markupStream, openTag, selectedBuffer);
        
    }
    
    
}
