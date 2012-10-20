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

package gr.abiss.calipso.wicket;

import gr.abiss.calipso.domain.Space;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * @author marcello
 */
public class SearchOnAnotherSpacePanel extends BasePanel {

	public SearchOnAnotherSpacePanel(String id, IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);
		addComponents();
	}

	public SearchOnAnotherSpacePanel(String id) {
		super(id);
		addComponents();
	}

	/////////////////////////////////////////////////////////////////////////////

	private void addComponents(){
		final List<Space> userSpaces = new ArrayList<Space>(getPrincipal().getSpaces());

		if (getCurrentSpace()!=null){
			//Use, for all space search
			Space emptySpace = new Space();
			emptySpace.setId(0);
			emptySpace.setName(localize("item_search_form.allSpaces"));
			emptySpace.setPrefixCode("");
			
			userSpaces.add(0, emptySpace);
			userSpaces.remove(getCurrentSpace());
		}

		// -- Spaces Drop Down List ------------------------------------------- 
		final DropDownChoice allSpaces = new DropDownChoice("allSpaces", new Model(), userSpaces,  new IChoiceRenderer(){
				public String getIdValue(Object object, int index) {
					return String.valueOf(((Space)object).getId());
				}
			
				public Object getDisplayValue(Object object) {
					return localize(((Space)object).getNameTranslationResourceKey());
				}
			});
		allSpaces.setNullValid(false);
		allSpaces.setOutputMarkupId(true);

		allSpaces.add(new AjaxFormComponentUpdatingBehavior ("onchange") {
			protected void onUpdate(AjaxRequestTarget target) {
				//Do nothing. Needed for get its value via ajax.
			}//onUpdate
		});

		add(allSpaces);

		// -- Search Button -------------------------------------------
		final AjaxLink go = new AjaxLink("go"){
			@Override
			public void onClick(AjaxRequestTarget target) {
				target.addComponent(allSpaces);
				if (allSpaces.getValue()!=null && !allSpaces.getValue().equals("") && !allSpaces.getValue().equals("-1")){
					if (allSpaces.getValue().equals("0")){//All Spaces
						((CalipsoSession) getSession()).setCurrentSpace(null);
						setResponsePage(ItemSearchFormPage.class);
					}//if
					else{
						Space selectedSpace = getCalipso().loadSpace(Long.parseLong(allSpaces.getValue()));
						for (Space space : userSpaces){
							if (space.equals(selectedSpace)){
								setCurrentSpace(space);
								setResponsePage(ItemSearchFormPage.class);
							}//if
						}//for
					}//else
				}
			}
		};
		go.setOutputMarkupId(true);
		add(go);
	}
}