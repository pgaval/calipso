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

package gr.abiss.calipso.util;

import java.util.List;

import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;

public class BreadCrumbUtils {

	/**
	 * @return The corresponding panel activating the back command based on given BreadCrumbModel . If there is no back returns null.
	 * @param breadCrumbModel The given Breadcrumb Model
	 * */
	public static Object backBreadCrumbPanel(IBreadCrumbModel breadCrumbModel){

		int participantsSize = breadCrumbModel.allBreadCrumbParticipants().size();

		//There must be at least 2 participants in order to return the back page participant
		//Current page = participantsSize-1
		//Back page = participantsSize-2
		if (participantsSize>=2){
			return breadCrumbModel.allBreadCrumbParticipants().get(participantsSize-2);
		}//if

		return null;
	}//backBreadCrumbPanel

	/**
	 * @param breadCrumbModel The given BreadCrumb Model
	 * */
	public static void removeActiveBreadCrumbPanel(IBreadCrumbModel breadCrumbModel){
		IBreadCrumbParticipant previous = (IBreadCrumbParticipant) backBreadCrumbPanel(breadCrumbModel);

		if(previous != null){
			breadCrumbModel.setActive(previous);
		}
		
	}//removeActiveBreadCrumbPanel

	/**
	 * @param breadCrumbModel The given BreadCrumb Model
	 * */
	public static void removePreviousBreadCrumbPanel(IBreadCrumbModel breadCrumbModel){
		int participantsSize = breadCrumbModel.allBreadCrumbParticipants().size();

		//There must be at least 3 participants in order to remove the back page participant
		if (participantsSize>=3){
			breadCrumbModel.setActive((IBreadCrumbParticipant) breadCrumbModel.allBreadCrumbParticipants().get(participantsSize - 3));
		}//if
		else{
			breadCrumbModel.allBreadCrumbParticipants().clear();
		}
	}//removePreviousBreadCrumbPanel
	
	/**
	 * Returns the first panel of path to active panel
	 * @param breadCrumbModel The given BreadCrumb Model
	 * */
	public static BreadCrumbPanel moveToRootPanel(IBreadCrumbModel breadCrumbModel){
		int participantsSize = breadCrumbModel.allBreadCrumbParticipants().size();

		if (participantsSize>0){
			breadCrumbModel.setActive((IBreadCrumbParticipant) breadCrumbModel.allBreadCrumbParticipants().get(0));
		}
		
		return (BreadCrumbPanel)breadCrumbModel.getActive().getComponent();
	}

	
	/**
	 * Returns a BreadCrumbPanel of a given breadCrumbModel according to the panelIndex
	 * @param breadCrumbModel  The BreadCrumbModel
	 * @param panelIndex Panel index
	 * @return 
	 * */
	public static BreadCrumbPanel moveToPanel(IBreadCrumbModel breadCrumbModel, int panelIndex){
		int participantsSize = breadCrumbModel.allBreadCrumbParticipants().size();
		
		if (panelIndex<participantsSize && panelIndex>0){
			breadCrumbModel.setActive((IBreadCrumbParticipant) breadCrumbModel.allBreadCrumbParticipants().get(panelIndex));
			return (BreadCrumbPanel)breadCrumbModel.getActive().getComponent();
		}
		
		return (BreadCrumbPanel)breadCrumbModel.getActive().getComponent();
	}
	
	
	/**
	 * @param breadCrumbModel
	 * @param panelClassName
	 * @return 
	 * 
	 * */
	public static BreadCrumbPanel moveToPanel(IBreadCrumbModel breadCrumbModel, String panelClassName){
		int participantsSize = breadCrumbModel.allBreadCrumbParticipants().size();

		for (int i=0; i<participantsSize; i++){
			IBreadCrumbParticipant breadCrumbParticipant = (IBreadCrumbParticipant) breadCrumbModel.allBreadCrumbParticipants().get(i);
			if (breadCrumbParticipant.getClass().getName().equals(panelClassName)){
				breadCrumbModel.setActive((IBreadCrumbParticipant) breadCrumbModel.allBreadCrumbParticipants().get(i));
				return (BreadCrumbPanel)breadCrumbModel.getActive().getComponent();
			}//if

		}//for
		
		return (BreadCrumbPanel)breadCrumbModel.getActive().getComponent();
	}

	/**
	 * @param breadCrumbModel
	 * @param 
	 * @return 
	 * 
	 * */
	public static BreadCrumbPanel moveToPanel(IBreadCrumbModel breadCrumbModel, Class panelClass){
		int participantsSize = breadCrumbModel.allBreadCrumbParticipants().size();

		for (int i=0; i<participantsSize; i++){
			IBreadCrumbParticipant breadCrumbParticipant = (IBreadCrumbParticipant) breadCrumbModel.allBreadCrumbParticipants().get(i);
			if (breadCrumbParticipant.getClass().equals(panelClass)){
				breadCrumbModel.setActive((IBreadCrumbParticipant) breadCrumbModel.allBreadCrumbParticipants().get(i));
				return (BreadCrumbPanel)breadCrumbModel.getActive().getComponent();
			}//if

		}//for
		
		return (BreadCrumbPanel)breadCrumbModel.getActive().getComponent();
	}

	public static BreadCrumbPanel moveToPanelForRelod(IBreadCrumbModel breadCrumbModel, Class panelClass){
		int participantsSize = breadCrumbModel.allBreadCrumbParticipants().size();

		for (int i=0; i<participantsSize; i++){
			IBreadCrumbParticipant breadCrumbParticipant = (IBreadCrumbParticipant) breadCrumbModel.allBreadCrumbParticipants().get(i);
			if (breadCrumbParticipant.getClass().equals(panelClass)){
				if (i>=1){
					breadCrumbModel.setActive((IBreadCrumbParticipant) breadCrumbModel.allBreadCrumbParticipants().get(i-1));
				}
				return (BreadCrumbPanel)breadCrumbModel.getActive().getComponent();
			}//if

		}//for
		
		return (BreadCrumbPanel)breadCrumbModel.getActive().getComponent();
	}
	
	/**
	 * @param breadCrumbModel
	 * @param 
	 * @return BreadCrumbPanel of the panelClass class, null if not found
	 * 
	 * */
	public static BreadCrumbPanel getPanel(IBreadCrumbModel breadCrumbModel, Class panelClass){
		int participantsSize = breadCrumbModel.allBreadCrumbParticipants().size();

		for (int i=0; i<participantsSize; i++){
			IBreadCrumbParticipant breadCrumbParticipant = (IBreadCrumbParticipant) breadCrumbModel.allBreadCrumbParticipants().get(i);
			if (breadCrumbParticipant.getClass().equals(panelClass)){
				return (BreadCrumbPanel) breadCrumbParticipant;
			}//if

		}//for
		
		return null;
	}
	
	public static BreadCrumbPanel getPanel(IBreadCrumbModel breadCrumbModel, List<Class> panelsClasses){
		int participantsSize = breadCrumbModel.allBreadCrumbParticipants().size();
		
		for (int i=0; i<participantsSize; i++){
			IBreadCrumbParticipant breadCrumbParticipant = (IBreadCrumbParticipant) breadCrumbModel.allBreadCrumbParticipants().get(i);
			
			if(panelsClasses.contains(breadCrumbParticipant.getClass())){
				return (BreadCrumbPanel) breadCrumbParticipant;
			}//if

		}//for
		
		return null;
	}
	
	public static void popPanels(int panelsCount, IBreadCrumbModel breadCrumbModel){
		for (int i=0; i<panelsCount; i++){
			removeActiveBreadCrumbPanel(breadCrumbModel);
		}
	}//popPanels
	
}