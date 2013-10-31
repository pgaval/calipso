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
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.UserSpaceRole;
import gr.abiss.calipso.util.DateUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;


public class ApplicationsDashboardPanel extends BasePanel {
	
	private static final Logger logger = Logger.getLogger(ApplicationsDashboardPanel.class);
	ArrayList<Long> expandedRowsList = new ArrayList<Long>(); 
	
	/**
	 * 
	 * @param id
	 * @param breadCrumbModel
	 * @param isSingleSpace
	 */
	@SuppressWarnings({ "unchecked", "serial" })
	public ApplicationsDashboardPanel(String id, IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);
		
		final User user = getPrincipal();
		// current space???
		List<UserSpaceRole> nonGlobalSpaceRoles = new ArrayList<UserSpaceRole>(user.getSpaceRolesNoGlobal());
		logger.info("nonGlobalSpaceRoles: " + nonGlobalSpaceRoles);
        WebMarkupContainer message = new WebMarkupContainer("message");
    	setCurrentSpace(null);
        TreeSet<UserSpaceRole> sortedBySpaceCode = new TreeSet<UserSpaceRole>(new UserSpaceRoleComparator());
        sortedBySpaceCode.addAll(nonGlobalSpaceRoles);
        List<UserSpaceRole> sortedBySpaceCodeList = new ArrayList<UserSpaceRole>(sortedBySpaceCode.size());
        sortedBySpaceCodeList.addAll(sortedBySpaceCode);
        
        // first add the existing user items
        List<Object[]> userItems = getCalipso().selectLatestItemPerSpace(this.getPrincipal());
        final Set<String> usedSpacePrefixes = new HashSet<String>();
        // add the spaces not used by the user
        final WebMarkupContainer noItems = new WebMarkupContainer("noItems");
        add(new ListView<Object[]>("items", userItems) {
            @Override
			protected void populateItem(final ListItem listItem) {
            	Object[] o = (Object[]) listItem.getModelObject();
            	Date closingDate = (Date) o[2];
                listItem.add(new Label("spaceName", localize(new StringBuffer("Space.").append(o[1]).append(".name").toString())));
                listItem.add(new Label("closingDate", DateUtils.format(closingDate)));
                Integer closed = new Integer(99);
                listItem.add(new Label("status", o[4].toString().equals("99")?"complete":"incomplete"));
                //getSpace().getPrefixCode() + "-" + getId() + "-" + [3];
                String uniqueRefId = new StringBuffer(o[0].toString()).append("-").append(o[5]).append("-").append(o[3]).toString();
                Link refIdLink = new BookmarkablePageLink("uniqueRefId", ItemViewPage.class, new PageParameters("0=" + uniqueRefId));
                refIdLink.add(new Label("uniqueRefId",  o[4].toString().equals("99")?"view":"edit"));
                
                listItem.add(refIdLink);
				if (closingDate != null && closingDate.after(new Date())) {
					refIdLink.setVisible(false);
				}

                usedSpacePrefixes.add(o[0].toString());
                noItems.setVisible(false);
            }
        });
        add(noItems);
        
        
        // add the spaces not used by the user
        final WebMarkupContainer noSpaces = new WebMarkupContainer("noSpaces");
        noSpaces.setVisible(true);
        add(new ListView<UserSpaceRole>("spaces", sortedBySpaceCodeList) {
            @Override
			protected void populateItem(final ListItem listItem) {
                UserSpaceRole userSpaceRole = (UserSpaceRole) listItem.getModelObject();
                Space space = userSpaceRole.getSpaceRole().getSpace();
                listItem.add(new Label("spaceTitle", localize(space.getNameTranslationResourceKey())));
                listItem.add(new Label("spaceDescription", space.getDescription()));
                String closingDate = "";
                if(space.getClosingDate() != null){
                	closingDate = localize("closingDate")+ ": "+DateUtils.format(space.getClosingDate());
                }
                listItem.add(new Label("closingDate", closingDate));
                Link prefixLink = new BookmarkablePageLink("prefixLink", NewItemPage.class, new PageParameters("spaceCode=" + space.getPrefixCode()));
                listItem.add(prefixLink.add(new Label("prefixLinkLabel",  "Apply")));
                boolean active = true;
                if(space.getClosingDate() != null){
                	active = space.getClosingDate().after(new Date());
                }
                if(!active || usedSpacePrefixes.contains(space.getPrefixCode())){
                	listItem.setVisible(false);
                }
                else{
                	noSpaces.setVisible(false);
                }
            }
        });
        add(noSpaces);
	}
	public class UserSpaceRoleComparator implements Comparator<UserSpaceRole>{

		@Override
		public int compare(UserSpaceRole o1, UserSpaceRole o2) {
			int result = 0;
			try{
				
				result = o1.getSpaceRole().getSpace().getPrefixCode().compareTo(o2.getSpaceRole().getSpace().getPrefixCode());
			}
			catch(RuntimeException e){
				logger.error(e);
			}
			return result;
		}
		
	}
	

}

