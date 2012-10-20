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


package gr.abiss.calipso.wicket.components.formfields;

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.CustomAttribute;
import gr.abiss.calipso.domain.CustomAttributeLookupValue;
import gr.abiss.calipso.wicket.ComponentUtils;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.tree.AbstractTree;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.LabelTree;
import org.apache.wicket.markup.html.tree.LinkIconPanel;
import org.apache.wicket.markup.html.tree.WicketTreeModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.springframework.util.CollectionUtils;

public class TreeChoice extends FormComponentPanel implements IHeaderContributor {

	private static final long serialVersionUID = 1L;
	

	private static final Logger logger = Logger.getLogger(TreeChoice.class);
			
	//private final WebMarkupContainer fieldContainer;
	//private TextField<CustomAttributeLookupValue> hiddenfield;
	
	//private CustomAttributeLookupValue optionValue;

	boolean visibleMenuLinks = false;
	private List<CustomAttributeLookupValue> optionValues;

	private BaseTree tree = null;


	protected Label selectedLabel;


	private String attributeNameTranslationResourceKey;


	private WebMarkupContainer treeMenuLinks;

	/**
	 * Returns the tree on this pages. This is used to collapse, expand the tree
	 * and to switch the rootless mode.
	 * TreeChoice
	 * @return Tree instance on this page
	 */
	protected AbstractTree getTree() {
		return tree;
	}

	@SuppressWarnings("unused")
	private TreeChoice(String id) {
		super(id, null);
	}

	@SuppressWarnings("unused")
	private TreeChoice(String id, IModel model) {
		super(id, null);
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 * @param model
	 * @param lookupValues 
	 */
	@SuppressWarnings("unchecked")
	public TreeChoice(String id, IModel<CustomAttributeLookupValue> model, List<CustomAttributeLookupValue> lookupValues, CustomAttribute attribute, CalipsoService calipso) {
		// we keep the selected as our model and the list of all options in a collection
		super(id, model);
		this.attributeNameTranslationResourceKey = attribute != null ? attribute.getNameTranslationResourceKey() : null;
		this.optionValues = lookupValues;
		// load values if empty
		if(CollectionUtils.isEmpty(this.optionValues)){
			this.optionValues = calipso.findLookupValuesByCustomAttribute(attribute);
		}
		// fix validation messages
		//this.setLabel(new ResourceModel(optionValue.getAttribute().getNameTranslationResourceKey()));
		CustomAttributeLookupValue optionValue = (CustomAttributeLookupValue) this.getModel().getObject();
		// build tree model of available options hang it on 
		// a root (options may have many level 1 nodes) that will not be
		// displayed
		DefaultMutableTreeNode selectedNode = null;
		if(optionValue != null){
			selectedNode = new DefaultMutableTreeNode(optionValue);
		}
		addNavigationLinks();
		add(tree = new LinkTree("tree", createTreeModel(selectedNode)));
		if(selectedNode != null){
		    expandToNode(selectedNode);
		    tree.getTreeState().selectNode(selectedNode, true);
		}
		tree.setRootLess(true);
		
	}
	

	@Override
	protected void onBeforeRender() {

		super.onBeforeRender();
	}


	private void expandToNode(DefaultMutableTreeNode node) {
		tree.getTreeState().expandNode(node);
		if(!node.isRoot()){
			expandToNode((DefaultMutableTreeNode) node.getParent());
		}
	}

	

	public CustomAttributeLookupValue getLookupValue() {
		CustomAttributeLookupValue optionValue = (CustomAttributeLookupValue) this.getModelObject();
		return (optionValue != null) ? optionValue : null;
	}

	
	/**
	 * @see org.apache.wicket.markup.html.form.FormComponent#getInput()
	 */
	@Override
	public String getInput() {
		// since we override convertInput, we can let this method return a value
		// that is just suitable for error reporting
		CustomAttributeLookupValue optionValue = (CustomAttributeLookupValue) this.getModelObject();
		return optionValue != null ? optionValue.getId()+"" : null;
	}


	/**
	 * Sets the converted input. In this case, we're really just interested in
	 * the nested lookup value hidden field, as that is the element that
	 * receives the real user input. So we're just passing that on.
	 * <p>
	 * Note that overriding this method is a better option than overriding
	 * {@link #updateModel()} like the first versions of this class did. The
	 * reason for that is that this method can be used by form validators
	 * without having to depend on the actual model being updated, and this
	 * method is called by the default implementation of {@link #updateModel()}
	 * anyway (so we don't have to override that anymore).
	 * </p>
	 * 
	 * 
	 * @see org.apache.wicket.markup.html.form.FormComponent#convertInput()
	 * 
	 */
	@Override
	protected void convertInput(){
		setConvertedInput(this.getModelObject());
	}

	// unused
	private void addNavigationLinks() {
		this.treeMenuLinks = new WebMarkupContainer("treeMenuLinks");
		/*
		add(new AjaxLink<Void>("expandAll") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				getTree().getTreeState().expandAll();
				getTree().updateTree(target);
			}
		});

		add(new AjaxLink<Void>("collapseAll") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				getTree().getTreeState().collapseAll();
				getTree().updateTree(target);
			}
		});

		add(new AjaxLink<Void>("switchRootless") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				getTree().setRootLess(!getTree().isRootLess());
				getTree().updateTree(target);
			}
		});*/
		StaticLink helpLink = new StaticLink("helpLink", new Model(new StringBuffer("/calipso/").append(attributeNameTranslationResourceKey).append(".").append(Session.get().getLocale().getLanguage()).append(".html").toString())); 
		//helpLink.add(new Label("helpLinkLabel", ComponentUtils.localize(this, "help")).setRenderBodyOnly(true));
		treeMenuLinks.add(helpLink);
		add(treeMenuLinks.setVisible(this.visibleMenuLinks && attributeNameTranslationResourceKey != null));
	}

	/**
	 * Creates the model that feeds the tree.
	 * @param object 
	 * 
	 * @return New instance of tree model.
	 */
	protected TreeModel createTreeModel(final DefaultMutableTreeNode selectedNode) {
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("ROOT");
		for(CustomAttributeLookupValue value : this.optionValues){
			if(value.getLevel() == 1){
				createTreeSubModel(rootNode, selectedNode, value);
			}
			else{
				logger.warn("Only root (level 1) values are processed as root nodes. Ignoring: "+value);
			}
		}
		TreeModel model = new DefaultTreeModel(rootNode);
		return model;
	}

	/**
	 * Recursive method to create the model that feeds the tree.
	 */
	private void createTreeSubModel(DefaultMutableTreeNode parentNode,
			DefaultMutableTreeNode selectedNode,
			CustomAttributeLookupValue childValue) {
		DefaultMutableTreeNode childNode;
		if(selectedNode != null && selectedNode.getUserObject().equals(childValue)){
			childNode = selectedNode;
		}
		else{
			childNode = new DefaultMutableTreeNode(childValue);
		}
		parentNode.add(childNode);
		//logger.info("Children of '"+childValue.getName()+"', "+childValue.getLevel());
		//for(CustomAttributeLookupValue grandChildValue : childValue.getChildren()){
			//logger.info("Child '"+grandChildValue.getName()+"', level "+grandChildValue.getLevel());
		//}
		for(CustomAttributeLookupValue grandChildValue : childValue.getChildren()){
			createTreeSubModel(childNode, selectedNode, grandChildValue);
		}
	}

	/**
	 * Simple tree component that provides node panel with link allowing user to select individual
	 * nodes.
	 */
	public class LinkTree extends LabelTree{
		private static final long serialVersionUID = 1L;

		/**
		 * Construct.
		 * 
		 * @param id
		 */
		private LinkTree(String id){
			super(id);
		}

		/**
		 * 
		 * Construct.
		 * 
		 * @param id
		 * @param model
		 *            model that provides the {@link TreeModel}
		 */
		public LinkTree(String id, IModel<TreeModel> model)	{
			super(id, model);
		}

		/**
		 * 
		 * Construct.
		 * 
		 * @param id
		 * @param model
		 *            Tree model
		 */
		public LinkTree(String id, TreeModel model)	{
			super(id, new WicketTreeModel());
			setModelObject(model);
		}

		/**
		 * 
		 * {@inheritdoc}
		 * @see org.apache.wicket.markup.html.tree.BaseTree#newNodeComponent(java.lang.String,
		 *      org.apache.wicket.model.IModel)
		 */
		@Override
		protected Component newNodeComponent(String id, IModel<Object> model){
			super.newNodeComponent(id, model);
			return new LinkIconPanel(id, model, LinkTree.this){
				private static final long serialVersionUID = 1L;

				/**
				 * {@inheritdoc}
				 */
				@Override
				protected void onNodeLinkClicked(Object node, BaseTree tree, AjaxRequestTarget target){
					super.onNodeLinkClicked(node, tree, target);
					LinkTree.this.onNodeLinkClicked(node, tree, target);
				}

				/**
				 * {@inheritdoc}
				 */
				@Override
				protected Component newContentComponent(String componentId, BaseTree tree, IModel<?> model){
					//logger.debug("newContentComponent model: "+model+", object:"+ model.getObject());
					CustomAttributeLookupValue lookupValue = (CustomAttributeLookupValue) ((DefaultMutableTreeNode) model.getObject()).getUserObject();
					Label label = new Label(componentId, new ResourceModel(lookupValue.getNameTranslationResourceKey())); 
					
					// keep a reference to scroll tree to selected node
					// on document load
					CustomAttributeLookupValue selected = (CustomAttributeLookupValue) TreeChoice.this.getModelObject();
					if(lookupValue.equals(selected)){
						label.setOutputMarkupId(true);
						TreeChoice.this.selectedLabel = label;
					}
					return label;
				}
			};
		}

		/**
		 * {@inheritdoc}
		 */
        @SuppressWarnings("unchecked")
		protected void onNodeLinkClicked(Object node, BaseTree tree, AjaxRequestTarget target) {
            if (node instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
                if (treeNode.isLeaf()) {
                	CustomAttributeLookupValue nodeUserObject = (CustomAttributeLookupValue) treeNode.getUserObject();
                	// toggle between select/deselect
					CustomAttributeLookupValue oldSelection = (CustomAttributeLookupValue) TreeChoice.this.getModelObject();
                	if(oldSelection != null && oldSelection.equals(nodeUserObject)){
                		TreeChoice.this.setModelObject(null);
                	}
                	else{
                		TreeChoice.this.setModelObject(nodeUserObject);	
                	}
                	//target.addComponent(fieldContainer);
                }
            }
        }
	}

	/**
	 * Make sure the selected tree node will be scrolled into view
	 * {@inheritdoc}
	 */
	@Override
	public void renderHead(IHeaderResponse response) {
		CustomAttributeLookupValue selected = (CustomAttributeLookupValue) TreeChoice.this.getModelObject();
		//logger.info("selected: "+selected);
		if(selected != null && this.selectedLabel != null){
			// put the selected node into view
            response.renderOnLoadJavaScript("document.getElementById('" + this.selectedLabel.getMarkupId() + "').scrollIntoView(false);");
		}
		
	}
	
    private class StaticLink extends WebMarkupContainer{
        public StaticLink(String id, IModel<?> model){
            super(id, model);
            add(new AttributeModifier("href", true, model));
        }
    }

	public boolean isVisibleMenuLinks() {
		return visibleMenuLinks;
	}

	public void setVisibleMenuLinks(boolean visibleMenuLinks) { 
		this.visibleMenuLinks = visibleMenuLinks;
		this.treeMenuLinks.setVisible(this.visibleMenuLinks);
	}
}
