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


package gr.abiss.calipso.domain;

import gr.abiss.calipso.wicket.BasePage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.breadcrumb.BreadCrumbLink;
import org.apache.wicket.extensions.breadcrumb.DefaultBreadCrumbsModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModelListener;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.LoadableDetachableModel;

public class CalipsoBreadCrumbBar  extends Panel implements IBreadCrumbModel {
	private BasePage parentPage;
	
	/** Default crumb component. */
	private static final class BreadCrumbComponent extends Panel
	{
		private static final long serialVersionUID = 1L;
		
		/**
		 * Construct.
		 * 
		 * @param id
		 *            Component id
		 * @param index
		 *            The index of the bread crumb
		 * @param breadCrumbModel
		 *            The bread crumb model
		 * @param breadCrumbParticipant
		 *            The bread crumb
		 * @param enableLink
		 *            Whether the link should be enabled
		 */
		public BreadCrumbComponent(String id, int index, IBreadCrumbModel breadCrumbModel,
			final IBreadCrumbParticipant breadCrumbParticipant, boolean enableLink, String crumbSeparator)
		{
			super(id);
			
			crumbSeparator = StringUtils.isBlank(crumbSeparator) ? "/" : crumbSeparator;
			add(new Label("sep", crumbSeparator).setEscapeModelStrings(false)
				.setRenderBodyOnly(true));
			BreadCrumbLink link = new BreadCrumbLink("link", breadCrumbModel)
			{
				private static final long serialVersionUID = 1L;

				protected IBreadCrumbParticipant getParticipant(String componentId)
				{
					return breadCrumbParticipant;
				}
			};
			link.setEnabled(enableLink);
			add(link);
			link.add(new Label("label", breadCrumbParticipant.getTitle()).setRenderBodyOnly(true));
		}
	}

	/**
	 * List view for rendering the bread crumbs.
	 */
	protected class BreadCrumbsListView extends ListView implements IBreadCrumbModelListener
	{
		private static final long serialVersionUID = 1L;

		private transient boolean dirty = false;

		private transient int size;
		
		private String crumbSeparator;

		/**
		 * Construct.
		 * 
		 * @param id
		 *            Component id
		 * @param crumbSeparator
		 *            Separation String for BreadCrumbs
		 */
		public BreadCrumbsListView(String id, String crumbSeparator)
		{
			super(id);
			
			this.crumbSeparator = crumbSeparator;
			
			setReuseItems(false);
			setModel(new LoadableDetachableModel()
			{
				private static final long serialVersionUID = 1L;

				protected Object load()
				{
					// save a copy
					List l = new ArrayList(allBreadCrumbParticipants());
					size = l.size();
					return l;
				}
			});
		}
		
		public BreadCrumbsListView(String id){
			this(id, null);
		}

		/**
		 * @see org.apache.wicket.extensions.breadcrumb.IBreadCrumbModelListener#breadCrumbActivated(org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant,
		 *      org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant)
		 */
		public void breadCrumbActivated(IBreadCrumbParticipant previousParticipant,
			IBreadCrumbParticipant breadCrumbParticipant)
		{
			signalModelChange();
		}

		/**
		 * @see org.apache.wicket.extensions.breadcrumb.IBreadCrumbModelListener#breadCrumbAdded(org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant)
		 */
		public void breadCrumbAdded(IBreadCrumbParticipant breadCrumbParticipant)
		{
		}

		/**
		 * @see org.apache.wicket.extensions.breadcrumb.IBreadCrumbModelListener#breadCrumbRemoved(org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant)
		 */
		public void breadCrumbRemoved(IBreadCrumbParticipant breadCrumbParticipant)
		{
		}

		/**
		 * Signal model change.
		 */
		private void signalModelChange()
		{
			// else let the listview recalculate it's children immediately;
			// it was attached, but it needs to go through that again now
			// as the signaling component attached after this
			getModel().detach();
			//super.internalOnAttach();
		}

		/**
		 * @see org.apache.wicket.markup.html.list.ListView#onBeforeRender()
		 */
		protected void onBeforeRender()
		{
			super.onBeforeRender();
			if (dirty)
			{
				dirty = false;
			}
		}

		/**
		 * @see org.apache.wicket.markup.html.list.ListView#populateItem(org.apache.wicket.markup.html.list.ListItem)
		 */
		protected void populateItem(ListItem item)
		{
			int index = item.getIndex();
			IBreadCrumbParticipant breadCrumbParticipant = (IBreadCrumbParticipant)item.getModelObject();
			item.add(newBreadCrumbComponent("crumb", index, size, breadCrumbParticipant, crumbSeparator).setRenderBodyOnly(true));
		}
	}

	private static final long serialVersionUID = 1L;

	private final IBreadCrumbModel decorated;

	/**
	 * Construct.
	 * 
	 * @param id
	 *            Component id
	 */
//	public BreadCrumbBar(String id)
//	{
//		super(id);
//		decorated = new DefaultBreadCrumbsModel();
//		BreadCrumbsListView breadCrumbsListView = new BreadCrumbsListView("crumbs");
//		addListener(breadCrumbsListView);
//		add(breadCrumbsListView);
//	}


	/**
	 * @see org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel#addListener(org.apache.wicket.extensions.breadcrumb.IBreadCrumbModelListener)
	 */
	public void addListener(IBreadCrumbModelListener listener)
	{
		decorated.addListener(listener);
	}

	/**
	 * @see org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel#allBreadCrumbParticipants()
	 */
	public List allBreadCrumbParticipants()
	{
		return decorated.allBreadCrumbParticipants();
	}

	/**
	 * @see org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel#getActive()
	 */
	public IBreadCrumbParticipant getActive()
	{
		return decorated.getActive();
	}

	/**
	 * @see org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel#removeListener(org.apache.wicket.extensions.breadcrumb.IBreadCrumbModelListener)
	 */
	public void removeListener(IBreadCrumbModelListener listener)
	{
		decorated.removeListener(listener);
	}

	/**
	 * @see org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel#setActive(org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant)
	 */
	public void setActive(IBreadCrumbParticipant breadCrumbParticipant)
	{
		decorated.setActive(breadCrumbParticipant);
	}

	/**
	 * Gets whether the current bread crumb should be displayed as a link (e.g. for refreshing) or
	 * as a disabled link (effectively just a label). The latter is the default. Override if you
	 * want different behavior.
	 * 
	 * @return Whether the current bread crumb should be displayed as a link; this method returns
	 *         false
	 */
	protected boolean getEnableLinkToCurrent()
	{
		return false;
	}

	/**
	 * Creates a new bread crumb component. That component will be rendered as part of the bread
	 * crumbs list (which is a &lt;ul&gt; &lt;li&gt; structure).
	 * 
	 * @param id
	 *            The component id
	 * @param index
	 *            The index of the bread crumb
	 * @param total
	 *            The total number of bread crumbs in the current model
	 * @param breadCrumbParticipant
	 *            the bread crumb
	 * @return A new bread crumb component
	 */
	protected Component newBreadCrumbComponent(String id, int index, int total,
		IBreadCrumbParticipant breadCrumbParticipant, String crumbSeparator)
	{
		boolean enableLink = getEnableLinkToCurrent() || (index < (total - 1));
		return new BreadCrumbComponent(id, index, this, breadCrumbParticipant, enableLink, crumbSeparator);
	}

	/**
	 * @see org.apache.wicket.Component#onDetach()
	 */
	protected void onDetach()
	{
		super.onDetach();
		for (Iterator i = decorated.allBreadCrumbParticipants().iterator(); i.hasNext();)
		{
			IBreadCrumbParticipant crumb = (IBreadCrumbParticipant)i.next();
			if (crumb instanceof Component)
			{
				((Component)crumb).detach();
			}
			else if (crumb instanceof IDetachable)
			{
				((IDetachable)crumb).detach();
			}
		}
	}
	
	public BasePage getParentPage() {
		return parentPage;
	}

	public CalipsoBreadCrumbBar(String id, BasePage parentPage) {
		this(id, parentPage, "");
	}

	public CalipsoBreadCrumbBar(String id, BasePage parentPage, String crumbSeparator) {
		super(id);
		
		decorated = new DefaultBreadCrumbsModel();
		BreadCrumbsListView breadCrumbsListView = new BreadCrumbsListView("crumbs", crumbSeparator);
		addListener(breadCrumbsListView);
		add(breadCrumbsListView);
		
		this.parentPage = parentPage;
	}
}
