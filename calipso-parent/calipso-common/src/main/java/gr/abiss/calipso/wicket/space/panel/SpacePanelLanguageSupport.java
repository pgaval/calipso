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

package gr.abiss.calipso.wicket.space.panel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;

import gr.abiss.calipso.domain.ItemRenderingTemplate;
import gr.abiss.calipso.domain.Language;
import gr.abiss.calipso.domain.Metadata;
import gr.abiss.calipso.domain.RoleSpaceStdField;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.SpaceRole;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.util.SpaceUtils;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.CalipsoFeedbackMessageFilter;
import gr.abiss.calipso.wicket.ConfirmPanel;
import gr.abiss.calipso.wicket.CalipsoApplication;
import gr.abiss.calipso.wicket.ErrorHighlighter;
import gr.abiss.calipso.wicket.SpaceFormPanel;
import gr.abiss.calipso.wicket.SpacePermissionsPanel;
import gr.abiss.calipso.wicket.form.AbstractSpaceform;

/**
 *
 */
public class SpacePanelLanguageSupport  extends BasePanel{

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(SpacePanelLanguageSupport.class);
	
	private Space space;
	private boolean isEdit;

	/**
	 * @param id
	 * @param breadCrumbModel
	 */
	public SpacePanelLanguageSupport(String id) {
		this(id, null, null);
	}
	
	
	public SpacePanelLanguageSupport(String id, IBreadCrumbModel breadCrumbModel) {
		this(id, breadCrumbModel, null);
	}
	
	
	public SpacePanelLanguageSupport(String id, IBreadCrumbModel breadCrumbModel, Space space) {
		super(id, breadCrumbModel);

		isEdit = space != null;
		if(!isEdit){
			space = new Space();
			this.getCalipso().storeUnpublishedSpace(space);
			space.setItemRenderingTemplates(new LinkedList<ItemRenderingTemplate>());
		}
		else{
			List<ItemRenderingTemplate> tmplList = this.getCalipso().getItemRenderingTemplates(space);
			space.setItemRenderingTemplates(tmplList);
		}
		// init lazy roles collection
		List<SpaceRole> roleList = getCalipso().findSpaceRolesForSpace(
				space);
		Set<SpaceRole> spaceRolesSet = new HashSet<SpaceRole>();
		if (roleList != null && !roleList.isEmpty()) {
			spaceRolesSet.addAll(roleList);
			for(SpaceRole spaceRole : roleList){
				spaceRole.setItemRenderingTemplates(getCalipso().loadSpaceRoleTemplates(spaceRole.getId()));
			}
		}
		space.setSpaceRoles(spaceRolesSet);
		
		//SpaceUtils.initSpaceSpaceRoles(getCalipso(), space);
		this.space = space;
		add(new SpaceForm("form", this.space));
		getBackLinkPanel().makeCancel();
	}
	
	private class SpaceForm extends AbstractSpaceform{

		private static final long serialVersionUID = 1L;
		private CalipsoFeedbackMessageFilter filter;
		/**
		 * @param id
		 */
		public SpaceForm(String id, final Space space) {
			super(id, space);
			FeedbackPanel feedback = new FeedbackPanel("feedback");
			filter = new CalipsoFeedbackMessageFilter();
			feedback.setFilter(filter);
			add(feedback);
			// palette
			List<Language> languages = new ArrayList<Language>(new LinkedHashSet<Language>(getCalipso().getSupportedLanguages()));
			Language defaultApplicationLanguage = null;
			String defaultLocale = getCalipso().getDefaultLocale();
			for(Language language : languages){
				if(language.getId().equals(defaultLocale)){
					defaultApplicationLanguage = language;
					break;
				}
			}

			List<Language> spaceLanguages = space.getSupportedLanguages();
			if(!spaceLanguages.contains(defaultApplicationLanguage)){
				spaceLanguages.add(defaultApplicationLanguage);
			}
			Palette supportedLanguages = new Palette("space.supportedLanguages", 
					 new PropertyModel(space,"supportedLanguages"), new Model((Serializable) languages), 
					 new IChoiceRenderer(){

						private static final long serialVersionUID = 1L;

						public Object getDisplayValue(Object object) {
							Language lang = (Language)object;
							return (lang.getId() + " - " + localize("language."+lang.getId()));
						}

						public String getIdValue(Object object, int index) {
							Language lang = (Language) object;
							return lang.getId();
						}
					
			}, 10, true);
			// must choose at least one
			/*
			supportedLanguages.getRecorderComponent().setRequired(true);
			supportedLanguages.getRecorderComponent().add(new IValidator(){

				public void validate(IValidatable validatable) {
					logger.debug("validatable value: "+validatable.getValue());
					
				}
				
			});*/
			add(supportedLanguages);
			add(new Label("space.supportedLanguagesLabel",new ResourceModel("space_form.spaceSupportedLanguages")).setRenderBodyOnly(true));
			
			// date formats
//			@SuppressWarnings("serial")
			ListView<String> dateFormatsListView = new ListView<String>("dateFormatsListView", Metadata.DATE_FORMAT_KEYS) {
			    protected void populateItem(ListItem<String> dateformatKeyItem) {
			        String dfKey = (String) dateformatKeyItem.getModelObject();
			        //logger.info("DATEFORMAT Model string: "+"dateFormats["+dfKey+"]");
			        // date format field
			    	TextField<String> dateFormatField = 
			    			new TextField<String>("dateFormatField", new PropertyModel<String>(space.getMetadata(),"dateFormats["+dfKey+"]"));
			    	dateFormatField.setRequired(true);
			    	dateFormatField.add(new ErrorHighlighter());
			    	dateformatKeyItem.add(dateFormatField);
			    	// date format label
			    	dateFormatField.setLabel(new ResourceModel("space_form.dateFormat."+dfKey)); 
			    	dateformatKeyItem.add(new SimpleFormComponentLabel("dateFormatLabel", dateFormatField)); 
			    }
			};
			add(dateFormatsListView);
			
			
			deleteLink(space);
			
		}
		/**
		 * @see org.apache.wicket.markup.html.form.Form#onSubmit()
		 */
		@Override
		protected void onSubmit() {
			activate(new IBreadCrumbPanelFactory() {
				
				private static final long serialVersionUID = 1L;

				public BreadCrumbPanel create(String componentId,
						IBreadCrumbModel breadCrumbModel) {
					
					return new SpaceFormPanel(componentId, getBreadCrumbModel(), space);
				}
				
			});
		}
		
	}
	
	private void deleteLink(final Space space) {

		boolean canBeDeleted = 
			Boolean.parseBoolean(((CalipsoApplication)Application.get()).
					getCalipsoPropertyValue("allow.delete.customField")) && isEdit;
		if (!canBeDeleted) {
			add(new WebMarkupContainer("delete").setVisible(false));
		}
		else {
			add(new Link("delete") {
			
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					final String heading = localize("space_delete.confirm");
					final String warning = localize("space_delete.line3");
					final String line1 = localize("space_delete.line1");
					final String line2 = localize("space_delete.line2");

					activate(new IBreadCrumbPanelFactory() {
						
						private static final long serialVersionUID = 1L;

						public BreadCrumbPanel create(String componentId, final IBreadCrumbModel breadCrumbModel) {
							ConfirmPanel confirm = 
								new ConfirmPanel(componentId, breadCrumbModel, heading, warning,
										new String[] { line1, line2 }) {
										
								private static final long serialVersionUID = 1L;

								public void onConfirm() {
									getCalipso().removeSpace(space);
									SpacePanelLanguageSupport.this.refreshPrincipal();
									BreadCrumbUtils.removePreviousBreadCrumbPanel(breadCrumbModel);
									activate(new IBreadCrumbPanelFactory() {
										
										private static final long serialVersionUID = 1L;

										public BreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {
											return (BreadCrumbPanel) breadCrumbModel.getActive();
										}
									});
								}
							};
							return confirm;
						}
					});
				}
			});
		}
	}

	public String getTitle() {
		if (isEdit)
			return localize("space_form.titleEditLanguageSupport", localize(space.getNameTranslationResourceKey()));
		else
			return localize("space_form.titleCreateLanguageSupport");
	}

}
