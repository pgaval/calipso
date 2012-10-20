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

package gr.abiss.calipso.wicket;

import java.util.HashSet;
import java.util.Set;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.ValidationErrorFeedback;
import org.apache.log4j.Logger;

/**
 * custom feedback message filter, removes duplicates and works in conjunction
 * with the ErrorHighlighter form component behavior
 */
public class CalipsoFeedbackMessageFilter implements IFeedbackMessageFilter {
	
    private Set<String> previous = new HashSet<String>();
    
    public void reset() {
    	this.previous.clear();
    }
    
    public boolean accept(FeedbackMessage fm) {
        String message = null;
        // wicket bit too flexible, wicket internally created errors are not just Strings
        // but if you added an error using the error(String) signature - will be just String
        Object fmMessage = fm.getMessage();
        if(fmMessage instanceof String) {
            message = (String) fmMessage;
        } else {
            ValidationErrorFeedback error = (ValidationErrorFeedback) fm.getMessage();
            message = error.getMessage();
        }        
        if(!this.previous.contains(message)) {
            this.previous.add(message);
            return true;
        }
        return false;
    }
    
}
