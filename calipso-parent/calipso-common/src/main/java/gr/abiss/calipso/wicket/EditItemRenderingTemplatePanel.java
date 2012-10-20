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

import gr.abiss.calipso.domain.ItemRenderingTemplate;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.ajax.TinyMceAjaxSubmitModifier;
import wicket.contrib.tinymce.settings.ContextMenuPlugin;
import wicket.contrib.tinymce.settings.DateTimePlugin;
import wicket.contrib.tinymce.settings.DirectionalityPlugin;
import wicket.contrib.tinymce.settings.EmotionsPlugin;
import wicket.contrib.tinymce.settings.FullScreenPlugin;
import wicket.contrib.tinymce.settings.IESpellPlugin;
import wicket.contrib.tinymce.settings.MediaPlugin;
import wicket.contrib.tinymce.settings.PastePlugin;
import wicket.contrib.tinymce.settings.PreviewPlugin;
import wicket.contrib.tinymce.settings.PrintPlugin;
import wicket.contrib.tinymce.settings.SavePlugin;
import wicket.contrib.tinymce.settings.SearchReplacePlugin;
import wicket.contrib.tinymce.settings.TablePlugin;
import wicket.contrib.tinymce.settings.TinyMCESettings;
import wicket.contrib.tinymce.settings.TinyMCESettings.Align;
import wicket.contrib.tinymce.settings.TinyMCESettings.EntityEncoding;
import wicket.contrib.tinymce.settings.TinyMCESettings.Location;

public abstract class EditItemRenderingTemplatePanel extends BasePanel {

	private static Logger log = Logger
			.getLogger(EditItemRenderingTemplatePanel.class);

	protected static final String BUTTON_SAVE = "save";
	protected static final String BUTTON_CANCEL = "cancel";
	private static TinyMCESettings settings;
	private ContextMenuPlugin contextMenuPlugin;

	public EditItemRenderingTemplatePanel(String id,
			final ModalWindow modalWindow,
			final ItemRenderingTemplate itemRenderingTemplate) {
		super(id);
		modalWindow.setInitialHeight(470);
		modalWindow.setInitialWidth(600);

		initTinyMce();
		log.info("itemRenderingTemplate: " + itemRenderingTemplate);

		Form<ItemRenderingTemplate> itemRenderingTemplateForm = new Form<ItemRenderingTemplate>(
				"itemRenderingTemplateForm",
				new CompoundPropertyModel<ItemRenderingTemplate>(
						itemRenderingTemplate));
		add(itemRenderingTemplateForm);
		FeedbackPanel itemRenderingTemplateFormFeedback = getFeedbackPanel("itemRenderingTemplateFormFeedback");
		itemRenderingTemplateForm.add(itemRenderingTemplateFormFeedback);

		// itemRenderingTemplate.getName().getDescription();
		setUpAndAdd(new RequiredTextField<String>("description"),
				itemRenderingTemplateForm);
		// itemRenderingTemplate.getPriority()
		setUpAndAdd(new RequiredTextField<String>("priority"),
				itemRenderingTemplateForm);
		// itemRenderingTemplate.getHideOverview()
		setUpAndAdd(new CheckBox("hideOverview"), itemRenderingTemplateForm);
		// itemRenderingTemplate.getHideHistory()
		setUpAndAdd(new CheckBox("hideHistory"), itemRenderingTemplateForm);
		// itemRenderingTemplate.getTemplateText()
		TextArea templateText = new TextArea<String>("templateText");
		templateText.add(new TinyMceBehavior(settings)); 
		templateText.setMarkupId("templateText");
		templateText.setOutputMarkupId(true);
		setUpAndAdd(templateText, itemRenderingTemplateForm);

		itemRenderingTemplateForm.add(getSaveButton(modalWindow,
				itemRenderingTemplateForm, itemRenderingTemplateFormFeedback));
		itemRenderingTemplateForm.add(getCancelButton(modalWindow,
				itemRenderingTemplateForm));

	}

	private static void initTinyMce() {
		if(settings == null){

			settings = new TinyMCESettings(TinyMCESettings.Theme.advanced);
			//contextMenuPlugin = new ContextMenuPlugin();
			//settings.register(contextMenuPlugin);
			settings.setToolbarLocation(Location.top);
			settings.setToolbarAlign(Align.left);
			// first toolbar
//			SavePlugin savePlugin = new SavePlugin();
//			settings.add(savePlugin.getSaveButton(), TinyMCESettings.Toolbar.first,
//					TinyMCESettings.Position.before);
//			settings.add(wicket.contrib.tinymce.settings.Button.newdocument,
//					TinyMCESettings.Toolbar.first, TinyMCESettings.Position.before);
//			settings.add(wicket.contrib.tinymce.settings.Button.separator,
//					TinyMCESettings.Toolbar.first, TinyMCESettings.Position.before);
//			settings.add(wicket.contrib.tinymce.settings.Button.fontselect,
//					TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
//			settings.add(wicket.contrib.tinymce.settings.Button.fontsizeselect,
//					TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);

			// second toolbar
			PastePlugin pastePlugin = new PastePlugin();
//			SearchReplacePlugin searchReplacePlugin = new SearchReplacePlugin();
//			DateTimePlugin dateTimePlugin = new DateTimePlugin();
//			dateTimePlugin.setDateFormat("Date: %m-%d-%Y");
//			dateTimePlugin.setTimeFormat("Time: %H:%M");
//			PreviewPlugin previewPlugin = new PreviewPlugin();
			settings.setEntityEncoding(EntityEncoding.raw);
			settings.add(wicket.contrib.tinymce.settings.Button.cut,
					TinyMCESettings.Toolbar.first, TinyMCESettings.Position.before);
			settings.add(wicket.contrib.tinymce.settings.Button.copy,
					TinyMCESettings.Toolbar.first, TinyMCESettings.Position.before);
			settings.add(pastePlugin.getPasteButton(),
					TinyMCESettings.Toolbar.first, TinyMCESettings.Position.before);
			settings.add(pastePlugin.getPasteTextButton(),
					TinyMCESettings.Toolbar.first, TinyMCESettings.Position.before);
			settings.add(pastePlugin.getPasteWordButton(),
					TinyMCESettings.Toolbar.first, TinyMCESettings.Position.before);
			settings.add(wicket.contrib.tinymce.settings.Button.separator,
					TinyMCESettings.Toolbar.first, TinyMCESettings.Position.before);
//			settings.add(searchReplacePlugin.getSearchButton(),
//					TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
//			settings.add(searchReplacePlugin.getReplaceButton(),
//					TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
//			settings.add(wicket.contrib.tinymce.settings.Button.separator,
//					TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
//			settings.add(wicket.contrib.tinymce.settings.Button.separator,
//					TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);
//			settings.add(dateTimePlugin.getDateButton(),
//					TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);
//			settings.add(dateTimePlugin.getTimeButton(),
//					TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);
//			settings.add(wicket.contrib.tinymce.settings.Button.separator,
//					TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);
//			settings.add(previewPlugin.getPreviewButton(),
//					TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);
//			settings.add(wicket.contrib.tinymce.settings.Button.separator,
//					TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);
//			settings.add(wicket.contrib.tinymce.settings.Button.forecolor,
//					TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);
//			settings.add(wicket.contrib.tinymce.settings.Button.backcolor,
//					TinyMCESettings.Toolbar.second, TinyMCESettings.Position.after);

			// third toolbar
//			TablePlugin tablePlugin = new TablePlugin();
//			EmotionsPlugin emotionsPlugin = new EmotionsPlugin();
//			IESpellPlugin iespellPlugin = new IESpellPlugin();
//			MediaPlugin mediaPlugin = new MediaPlugin();
//			PrintPlugin printPlugin = new PrintPlugin();
			//FullScreenPlugin fullScreenPlugin = new FullScreenPlugin();
//			DirectionalityPlugin directionalityPlugin = new DirectionalityPlugin();
//			settings.add(tablePlugin.getTableControls(),
//					TinyMCESettings.Toolbar.third, TinyMCESettings.Position.before);
//			settings.add(emotionsPlugin.getEmotionsButton(),
//					TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
//			settings.add(iespellPlugin.getIespellButton(),
//					TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
//			settings.add(mediaPlugin.getMediaButton(),
//					TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
//			settings.add(wicket.contrib.tinymce.settings.Button.separator,
//					TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
//			settings.add(printPlugin.getPrintButton(),
//					TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
//			settings.add(wicket.contrib.tinymce.settings.Button.separator,
//					TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
//			settings.add(directionalityPlugin.getLtrButton(),
//					TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
//			settings.add(directionalityPlugin.getRtlButton(),
//					TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
//			settings.add(wicket.contrib.tinymce.settings.Button.separator,
//					TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);
//			settings.add(fullScreenPlugin.getFullscreenButton(),
//					TinyMCESettings.Toolbar.first, TinyMCESettings.Position.after);

		
		}
		
	}

	/**
	 * Implement to persist data on submit
	 * 
	 * @param target
	 * @param form
	 */
	protected abstract void persist(AjaxRequestTarget target, Form form);

	/**
	 * just closes the window, override to actually persist
	 * 
	 * @param modalWindow
	 * @param itemRenderingTemplateForm
	 * @return
	 */
	protected AjaxButton getSaveButton(final ModalWindow modalWindow,
			Form<ItemRenderingTemplate> itemRenderingTemplateForm,
			final FeedbackPanel feedbackPanel) {
		AjaxButton save = new IndicatingAjaxButton(BUTTON_SAVE,
				itemRenderingTemplateForm) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {
				log.warn("method getSaveButton was given as an example but not overriden.");
				persist(target, form);
				if (target != null) {
					modalWindow.close(target);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form form) {
				log.warn("method getSaveButton was given as an example but not overriden.");
				if (target != null) {
					target.addComponent(feedbackPanel);
				}
			}
		};
		save.add(new TinyMceAjaxSubmitModifier());
		return save;
	}

	protected AjaxButton getCancelButton(final ModalWindow modalWindow,
			Form<ItemRenderingTemplate> itemRenderingTemplateForm) {
		AjaxButton cancel = new AjaxButton(BUTTON_CANCEL,
				itemRenderingTemplateForm) {

					@Override
					protected void onSubmit(AjaxRequestTarget target,
							Form<?> form) {
						if (target != null) {
							modalWindow.close(target);
						}
					}

					@Override
					protected void onError(AjaxRequestTarget target,
							Form<?> form) {
					}

		};
		return cancel;
	}

}