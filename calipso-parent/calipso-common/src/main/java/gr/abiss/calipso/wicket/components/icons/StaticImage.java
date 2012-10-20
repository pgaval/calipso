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
 */

package gr.abiss.calipso.wicket.components.icons;


import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;

/**
 * A simple component for static images, internal or external to the webapp.
 * @author manos
 *
 */
public class StaticImage extends WebComponent
{
  /**
  * @param id 
  * @param model the model containing the image URL
  */
  public StaticImage(String id, IModel<String> urlModel){
    super(id, urlModel);
  }

  protected void onComponentTag(ComponentTag tag){
    super.onComponentTag(tag);
    checkComponentTag(tag, "img");
    tag.put("src", getDefaultModelObjectAsString());
  }
}
