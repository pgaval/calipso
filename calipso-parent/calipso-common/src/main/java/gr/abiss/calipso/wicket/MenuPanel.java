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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.calipsomenu.MenuItem;
import gr.abiss.calipso.domain.calipsomenu.MenuModelBean;
import gr.abiss.calipso.domain.calipsomenu.MenuTreeItemColumn;
import gr.abiss.calipso.domain.calipsomenu.MenuTreeTable;
import gr.abiss.calipso.domain.calipsomenu.NotSummaryMenuException;
import gr.abiss.calipso.wicket.asset.AssetCustomAttributesPanel;
import gr.abiss.calipso.wicket.asset.AssetTypesPanel;
import gr.abiss.calipso.wicket.asset.AssetsPage;
import gr.abiss.calipso.wicket.asset.AssetsPanel;

import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation;
import org.apache.wicket.extensions.markup.html.tree.table.IColumn;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation.Alignment;
import org.apache.wicket.extensions.markup.html.tree.table.ColumnLocation.Unit;

/**
 * @author marcello
 */
public class MenuPanel extends BasePanel {
	private static final long serialVersionUID = 1L;
	
	private MenuTreeTable treeTable;
	private String pageClassName;
	private User user;

	public MenuPanel(String id, final IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);
		this.pageClassName = breadCrumbModel.getActive().getComponent().getClass().getName();
		this.user = getPrincipal();

		add(getMenuTreeTable());
	}
	
	// --------------------------------------------------------------------------------------------
	
	public MenuPanel(String id, String pageClassName) {
		super(id);
		this.pageClassName = pageClassName;
		this.user = getPrincipal();

		add(getMenuTreeTable());
	}

	// --------------------------------------------------------------------------------------------
	
	//  Support methods for tree creation
	private MenuTreeTable getMenuTreeTable(){

		IColumn[] columns = new IColumn[]{
				new MenuTreeItemColumn(new ColumnLocation(Alignment.MIDDLE, 8, Unit.PROPORTIONAL), "", "userObject", pageClassName)
		};

		treeTable = new MenuTreeTable("calipsoMenuTable", createTreeModel(), columns);
		treeTable.setRootLess(true);
		treeTable.getTreeState().setAllowSelectMultiple(false);
		treeTable.getTreeState().collapseAll();

		return treeTable;
	}//getDBTreeTable

	// --------------------------------------------------------------------------------------------

	private TreeModel createTreeModel(){
		List<MenuItem> treeItemList = loadMenuList();

		return convertToTreeModel(treeItemList);
	}

	// --------------------------------------------------------------------------------------------

	private TreeModel convertToTreeModel(List<MenuItem> list){
		TreeModel treeModel = null;

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new MenuModelBean());
		add(rootNode, list);
		treeModel = new DefaultTreeModel(rootNode);
		return treeModel;
	}

	// --------------------------------------------------------------------------------------------

	private void add(DefaultMutableTreeNode parent, List<MenuItem> list){
		for (Iterator<MenuItem> i = list.iterator(); i.hasNext();){
			MenuItem menuItem = i.next();
			
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(new MenuModelBean(menuItem));
			parent.add(child);
			
			if (menuItem.isMenuSummary()){
				add(child, menuItem.getSubmenuList());
			}//if
			
		}//for
		

	}//add

	// --------------------------------------------------------------------------------------------

	private List<MenuItem> loadMenuList(){
		final User  user = getPrincipal();
		List<MenuItem> treeItemList = new ArrayList<MenuItem>();
		final List<Space> spaces = new ArrayList<Space>(user.getSpaces());
        final boolean isAdmin = user.isGlobalAdmin();
        final boolean isSpaceAdmin = user.isSpaceAdmin();

		//Dashboard ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		treeItemList.add(new MenuItem(localize("header.dashboard"), DashboardPage.class.getName(), new PackageResourceReference(MenuPanel.class, "resources/dashboard.gif")){
			@Override
			public void onClick() {
				
                // if only one space, that would remain "selected" across all navigation.
                if(spaces.size() == 1) {
                    ((CalipsoSession) getSession()).setCurrentSpace(spaces.get(0));
                } else {
                    ((CalipsoSession) getSession()).setCurrentSpace(null);
                }                
                setResponsePage(DashboardPage.class);				
			}
		});

		//Search ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		treeItemList.add(new MenuItem(localize("header.search"), ItemSearchFormPage.class.getName(), new PackageResourceReference(MenuPanel.class, "resources/search.gif")){
			@Override
			public void onClick() {
                // if only one space don't use generic search screen
                if(spaces.size() == 1) {
                    Space current = spaces.get(0);
                    setCurrentSpace(current);
                } else {
                    setCurrentSpace(null);  // may have come here with back button
                }
                setResponsePage(ItemSearchFormPage.class);
			}
		});


		//Options ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		MenuItem options = new MenuItem(localize("header.options"), OptionsPanel.class.getName(), true, new PackageResourceReference(MenuPanel.class, "resources/text_list_bullets.png")){
			@Override
			public void onClick() {
                ((CalipsoSession) getSession()).setCurrentSpace(null);
                setResponsePage(OptionsPage.class);				
			}
		};


		try{
			//Options - User Profile ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			options.add(new MenuItem(localize("options.editYourProfile"), UserFormPanel.class.getName(), new PackageResourceReference(MenuPanel.class, "resources/user.gif")/*, optionsPage.getBreadCrumbBar()*/){
				@Override
				public void onClick() {
					final OptionsPage optionsPage = new OptionsPage();
					this.setBreadCrumbBar(optionsPage.getBreadCrumbBar());
					optionsPage.activate(new IBreadCrumbPanelFactory(){
						public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
							MenuPanel.this.setBreadCrumbModel(breadCrumbModel);
							return new UserFormPanel(breadCrumbModel.getActive().getComponent().getId(), breadCrumbModel, user, true);
						}
					});
					setResponsePage(optionsPage);
				}
			});

			
			//Options - Users ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if (isAdmin || isSpaceAdmin){
				options.add(new MenuItem(localize("options.manageUsers"), UserListPanel.class.getName(), new PackageResourceReference(MenuPanel.class, "resources/users.gif")){
					@Override
					public void onClick() {
						final OptionsPage optionsPage = new OptionsPage();
						this.setBreadCrumbBar(optionsPage.getBreadCrumbBar());
						optionsPage.activate(new IBreadCrumbPanelFactory(){
							public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
								return new UserListPanel(breadCrumbModel.getActive().getComponent().getId(), breadCrumbModel);
							}
						});
						setResponsePage(optionsPage);					
					}
				});
			}//if

			//Options - Spaces ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if (isAdmin || isSpaceAdmin){
				options.add(new MenuItem(localize("options.manageSpaces"), SpaceListPanel.class.getName(), new PackageResourceReference(MenuPanel.class, "resources/spaces.gif")){
					@Override
					public void onClick() {
						final OptionsPage optionsPage = new OptionsPage();
						this.setBreadCrumbBar(optionsPage.getBreadCrumbBar());
						optionsPage.activate(new IBreadCrumbPanelFactory(){
							public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
								return new SpaceListPanel(breadCrumbModel.getActive().getComponent().getId(), breadCrumbModel);
							}
						});
						setResponsePage(optionsPage);					
					}
				});
			}//if

			//Options - Settings ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if (isAdmin){
				options.add(new MenuItem(localize("options.manageSettings"), ConfigListPanel.class.getName(), new PackageResourceReference(MenuPanel.class, "resources/settings.gif")){
					@Override
					public void onClick() {
						final OptionsPage optionsPage = new OptionsPage();
						this.setBreadCrumbBar(optionsPage.getBreadCrumbBar());
						optionsPage.activate(new IBreadCrumbPanelFactory(){
							public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
								return new ConfigListPanel(breadCrumbModel.getActive().getComponent().getId(), breadCrumbModel);
							}
						});
						setResponsePage(optionsPage);					
					}
				});
			}//if

			//Options - Index ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if (isAdmin){
				options.add(new MenuItem(localize("options.rebuildIndexes"), IndexRebuildPanel.class.getName(), new PackageResourceReference(MenuPanel.class, "resources/refresh.gif")){
					@Override
					public void onClick() {
						final OptionsPage optionsPage = new OptionsPage();
						this.setBreadCrumbBar(optionsPage.getBreadCrumbBar());
						optionsPage.activate(new IBreadCrumbPanelFactory(){
							public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
								return new IndexRebuildPanel(breadCrumbModel.getActive().getComponent().getId(), breadCrumbModel);
							}
						});
						setResponsePage(optionsPage);
					}
				});
			}//if

			//Options - Assets ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if (isAdmin || isSpaceAdmin){
				MenuItem assetManagement = new MenuItem("Asset Management", AssetsPanel.class.getName(), true, new PackageResourceReference(MenuPanel.class, "resources/assets.gif")){
					@Override
					public void onClick() {
						final OptionsPage optionsPage = new OptionsPage();
						this.setBreadCrumbBar(optionsPage.getBreadCrumbBar());
						optionsPage.activate(new IBreadCrumbPanelFactory(){
							public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
								return new AssetsPanel(breadCrumbModel.getActive().getComponent().getId(), breadCrumbModel);
							}
						});
						setResponsePage(optionsPage);
					}
				};
	
				//Options - Assets - Custom Attributes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				assetManagement.add(new MenuItem("Custom Attributes", AssetCustomAttributesPanel.class.getName(), new PackageResourceReference(MenuPanel.class, "resources/assets.gif")){
					@Override
					public void onClick() {
						final OptionsPage optionsPage = new OptionsPage();
						final AssetsPage assetSpacePage = new AssetsPage();
	
						this.setBreadCrumbBar(assetSpacePage.getBreadCrumbBar());
						assetSpacePage.activate(new IBreadCrumbPanelFactory(){
							private static final long serialVersionUID = 1L;
	
							public BreadCrumbPanel create(final String id, final IBreadCrumbModel breadCrumbModel) {
								breadCrumbModel.allBreadCrumbParticipants().add(0, optionsPage.optionsPanel);
								return new AssetCustomAttributesPanel(breadCrumbModel.getActive().getComponent().getId(), breadCrumbModel);
							}
						});
						setResponsePage(assetSpacePage);
					}
				});
	
				//Options - Assets - Asset Types ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
				assetManagement.add(new MenuItem("Asset Types", AssetTypesPanel.class.getName(), new PackageResourceReference(MenuPanel.class, "resources/assets.gif")){
					@Override
					public void onClick() {
						final OptionsPage optionsPage = new OptionsPage();
						final AssetsPage assetSpacePage = new AssetsPage();
						this.setBreadCrumbBar(assetSpacePage.getBreadCrumbBar());
						
						assetSpacePage.activate(new IBreadCrumbPanelFactory(){
							public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
								breadCrumbModel.allBreadCrumbParticipants().add(0, optionsPage.optionsPanel);
								return new AssetTypesPanel(breadCrumbModel.getActive().getComponent().getId(), breadCrumbModel);
							}
						});
						setResponsePage(assetSpacePage);
					}
				});
	
				options.add(assetManagement);
			}//if

			//Options - Organization ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if (isAdmin){
				options.add(new MenuItem("Manage Organization", OrganizationPanel.class.getName(), new PackageResourceReference(MenuPanel.class, "resources/building.png")){
					@Override
					public void onClick() {
						final OptionsPage optionsPage = new OptionsPage();
						this.setBreadCrumbBar(optionsPage.getBreadCrumbBar());
						optionsPage.activate(new IBreadCrumbPanelFactory(){
							public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
								return new OrganizationPanel(breadCrumbModel.getActive().getComponent().getId(), breadCrumbModel);
							}
						});
						setResponsePage(optionsPage);					
					}				
				});
			}//if
		}//try
		catch(NotSummaryMenuException e){
			//Throws only if options is not a summary (has submenus) Menu Item
			throw new RuntimeException(e);
		}//catch

		treeItemList.add(options);

		return treeItemList;
	}
}