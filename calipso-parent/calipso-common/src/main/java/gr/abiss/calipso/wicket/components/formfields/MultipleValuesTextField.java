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

import gr.abiss.calipso.wicket.ErrorHighlighter;
import gr.abiss.calipso.wicket.form.FieldUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.behavior.AttributeAppender;
import org.springframework.web.util.HtmlUtils;

public class MultipleValuesTextField extends FormComponentPanel {

	private static final Logger logger = Logger.getLogger(MultipleValuesTextField.class);
	
	public static final String SEPARATOR_LINE_SUBVALUE = "-,,,,-";
	public static final String SEPARATOR_LINE_SUBVALUE_REGEXP = "\\-,,,,\\-";
	
	//protected static final String SEPARATOR_LINE_REGEXP = SEPARATOR_LINE_SUBVALUE_REGEXP+SEPARATOR_LINE_SUBVALUE_REGEXP;
	
	
	private HiddenField<String> valuesField;
	
	// used to control inner/parent form validation
	private boolean originalLinesCountSet = false;
	private int originalLinesCount = 0;
	private int linesCount = 0;
	
	
	private String originalValues = "";
	private WebMarkupContainer mainContainer;
	private FieldConfig fieldConfig;
	private List<String> newSubFieldValues = new LinkedList<String>();
	private MultipleValuesTextFieldValidator subFieldNoCommasValidator = new MultipleValuesTextFieldValidator();

	public MultipleValuesTextField(String id, IModel<String> model,
			FieldConfig config) {
		super(id, model);
		this.fieldConfig = config;
		this.setOutputMarkupId(true);
	}

	public MultipleValuesTextField(String id, IModel<String> model) {
		this(id, model, null);

	}

	public MultipleValuesTextField(String id, FieldConfig config) {
		this(id, null, config);

	}

	private MultipleValuesTextField(String id) {
		super(id);
	}

	@Override
	protected void convertInput() {
		String s = valuesField.getModelObject();
		// TODO: add the subvalues entered in the fields
		setConvertedInput(s);
	}

	
	/*
	 * Here we pull out each field from the User if it exists and put the
	 * contents into the fields.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		if(mainContainer == null){
			mainContainer = new WebMarkupContainer("mainContainer");
			mainContainer.setOutputMarkupId(true);
			add(mainContainer);
		}

		// subValuesString = this.getModelValue();
		originalValues = this.getModelValue();// subValuesString;
		List<FieldConfig> subFieldConfigs = this.fieldConfig != null ? this.fieldConfig
				.getSubFieldConfigs() : new LinkedList<FieldConfig>();
		if (CollectionUtils.isEmpty(subFieldConfigs)) {
			if (this.fieldConfig != null) {
				subFieldConfigs.add(fieldConfig);
			} else {
				subFieldConfigs.add(FieldConfig.getFallBackFieldConfig());
			}
		}
		if(valuesField == null){
			valuesField = new HiddenField<String>("valuesField", this.getModel());
			mainContainer.add(valuesField);
		
			final Form form = new Form("addValueform");
			form.setOutputMarkupId(true);
			form.add(new EmptyPanel("feedback").setOutputMarkupId(true));
			final IndicatingAjaxButton addButton = new IndicatingAjaxButton("add") {
	
				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					FeedbackPanel feedback = getNewFeedbackPanel(form);
					target.addComponent(feedback);
				}
	
				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					// clear lines counter temporarily for the inner form validation to work
					int tmpLineCount = linesCount;
					linesCount = 0;
					// clear feedback
					EmptyPanel feedback = new EmptyPanel("feedback");
					form.addOrReplace(feedback.setOutputMarkupId(true));
					target.addComponent(feedback);
					// restore lines counter
					linesCount = tmpLineCount;
					updateModelAndRepaint(form);
					
					// repaint component
					target.addComponent(mainContainer);
				}
			};
			mainContainer.add(form);
			// create a backing model for each subfield label using a list
			form.add(new ListView("subFieldsListView", subFieldConfigs) {
	
				@Override
				protected void populateItem(ListItem item) {
					int index = item.getIndex();
					FieldConfig fieldConfig = (FieldConfig) item.getModelObject();
	
					// initialize our model object: a list item
					if (index >= newSubFieldValues.size()) {
						newSubFieldValues.add("");
						
					}
					final TextField<String> newValueField = 
							new TextField<String>("newValueField", new PropertyModel(newSubFieldValues, "[" + index + "]")){
						@Override
					    protected void onComponentTag(ComponentTag tag){
					            super.onComponentTag(tag);
					            // we intercept ancestor form submission and explicitly click this form's submit button instead
					            tag.put("onkeypress", "if(event.keyCode == 13){document.getElementById('"  + addButton.getMarkupId() + "').click();return false;}");
					    }
						
						@Override
						public boolean isRequired(){
							return super.isRequired() && linesCount <= 0;
						}
					};
					newValueField.setOutputMarkupId(true);
					newValueField.setType(String.class);
					newValueField.add(new ErrorHighlighter());
					newValueField.setRequired(MultipleValuesTextField.this.isRequired());
					newValueField.add(new MultipleValuesTextFieldValidator());
					FieldUtils.appendFieldStyles(fieldConfig, newValueField);
					item.add(newValueField);
					// TODO: add validator and field size etc.
					newValueField.setLabel(fieldConfig.getLabelKey() != null ? new ResourceModel(
							fieldConfig.getLabelKey()) : new Model(""));
					if (fieldConfig.getSize() != null) {
						newValueField.add(new SimpleAttributeModifier("size",
								fieldConfig.getSize().toString()));
					}
					if (fieldConfig.getMaxLength() != null) {
						newValueField.add(new SimpleAttributeModifier("maxlength",
								fieldConfig.getMaxLength().toString()));
					}
					item.add(new SimpleFormComponentLabel("newValueLabel",
							newValueField).setVisible(fieldConfig.getLabelKey() != null));
					item.add(new Label("newValueHelp",
							fieldConfig.getHelpKey() != null ? new ResourceModel(
									fieldConfig.getHelpKey()) : new Model(""))
							.setVisible(fieldConfig.getHelpKey() != null));
				}
	
				
	
			}.setReuseItems(true));
	
			// show sub values table
			paintSubValuesTable(form);
			WebMarkupContainer submitControls = new WebMarkupContainer(
					"submitControls");
			submitControls.add(new SimpleAttributeModifier("colspan",
					subFieldConfigs.size() + ""));
			form.add(submitControls);
			submitControls.add(addButton);
			// make the add button the default submission button
			form.setDefaultButton(addButton);
			submitControls.add(new IndicatingAjaxButton("reset") {
				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					valuesField.setModelObject(originalValues);
					target.add(mainContainer);
				}

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				}
			}.setVisible(false));
	
			String s = this.getModelValue();
			this.valuesField.setModelObject(s);

		}
	}

	/**
	 * Read the List<String> backing the the fields, concat the values, 
	 * append them to the hidden field's model and refresh the display of the 
	 * input thas far
	 * @param form
	 */
	private void updateModelAndRepaint(Form<?> form) {
		// get the new subvalues
		boolean foundNonBlankValue = false;
		StringBuffer values = new StringBuffer();
		if (CollectionUtils.isNotEmpty(newSubFieldValues)) {
			// obtain and clear sub-values
			for (int i = 0; i < newSubFieldValues.size(); i++) {

				// paralel form later in document order
				// submits the first in the page
				// for some reason, unless it's own submit is used
				// e.g. instead of pressing enter in a field
				if (StringUtils.isNotBlank(newSubFieldValues.get(i))) {
					foundNonBlankValue = true;
				}
				values.append(newSubFieldValues.get(i));

				if (i + 1 < newSubFieldValues.size()) {
					values.append(SEPARATOR_LINE_SUBVALUE);
				}
			}
		}
		if (foundNonBlankValue) {
			String newSubValue = values.toString();
			// update model
			if (StringUtils.isNotBlank(newSubValue)) {
				String existingValues = valuesField.getModelObject();
				if (StringUtils.isNotBlank(existingValues)) {
					valuesField.setModelObject(new StringBuffer(existingValues)
							.append("\n").append(newSubValue).toString());
				} else {
					valuesField.setModelObject(newSubValue);
				}
				// subValuesString = (String) valuesField.getModelObject();
				
			}
			// update new subvalue's model
			for (int i = 0; i < newSubFieldValues.size(); i++) {
				newSubFieldValues.set(i, "");
			}
		}
		// update subvalues table
		paintSubValuesTable(form);
	}

	/**
	 * Read the hidden field value, i.e. the input thus far, then render it 
	 * appropriately in a table
	 * @param form
	 */
	@SuppressWarnings("unchecked")
	private void paintSubValuesTable(final Form form) {
		// show existing values table
		String currentValue = valuesField.getModelObject();
		List<String> originalValueRows = MultipleValuesTextField.getValueRows(currentValue);

		// record the line count, used for validation
		linesCount = originalValueRows.size();
		if(!originalLinesCountSet){
			originalLinesCount = linesCount;
			originalLinesCountSet = true;
		}
		
		// mark as empty
		if(linesCount == 0){
			clearInput();
		}
		
		form.addOrReplace(new ListView("row", originalValueRows) {

			@Override
			protected void populateItem(ListItem rowItem) {
				final int rowIndex = rowItem.getIndex();
				String subValuesString = rowItem.getModelObject().toString();
				// fix adjacent separators for lines
				subValuesString = subValuesString.replaceAll(SEPARATOR_LINE_SUBVALUE+SEPARATOR_LINE_SUBVALUE, SEPARATOR_LINE_SUBVALUE+" "+SEPARATOR_LINE_SUBVALUE);
				List<String> subValues = Arrays.asList(
						StringUtils.splitByWholeSeparator(" "+subValuesString+" ", SEPARATOR_LINE_SUBVALUE));
				rowItem.add(new ListView("cell", subValues) {
					@Override
					protected void populateItem(ListItem cellItem) {
						String subValue = cellItem.getModelObject().toString();
						cellItem.add(new Label("cellValue", StringUtils.isBlank(subValue)?"":subValue));
					}

				});
				rowItem.add(new IndicatingAjaxLink("remove") {

					@Override
					public void onClick(AjaxRequestTarget target) {
						// remove the line
						List<String> currentValueRows = Arrays
								.asList(valuesField.getModelObject().split(
										"\\r?\\n"));
						
						// update the model
						StringBuffer subValues = new StringBuffer();
						if (CollectionUtils.isNotEmpty(currentValueRows)) {
							// obtain and clear sub-values
							for (int i = 0; i < currentValueRows.size(); i++) {
								if (i != rowIndex) {
									subValues.append(currentValueRows.get(i));
									if (i + 1 < currentValueRows.size()) {
										subValues.append('\n');
									}
								}
							}
						}
						valuesField.setModelObject(subValues.toString());

						// repaint component
						paintSubValuesTable(form);
						target.addComponent(mainContainer);
					}
				});
			}

		});
	}

	private static List<String> getValueRows(String currentValue) {
		return StringUtils.isNotBlank(currentValue) ? Arrays
				.asList(currentValue.split("\\r?\\n"))
				: new LinkedList<String>();
	}
	
	/**
	 * Updates the feedbackpanel
	 * TODO: is this actually needed?
	 * @param form
	 * @return
	 */
	private FeedbackPanel getNewFeedbackPanel(Form<?> form) {
		FeedbackPanel feedback = new FeedbackPanel("feedback",
				new ContainerFeedbackMessageFilter(form));
		form.addOrReplace(feedback.setOutputMarkupId(true));
		return feedback;
	}

	/**
	 * Validates the sub-value fields' input to ensure they do not contain
	 * our seperator character sequence
	 * @author manos
	 *
	 */
	private class MultipleValuesTextFieldValidator implements
			IValidator<String> {
		private void error(IValidatable<String> validatable, String errorKey) {
			ValidationError error = new ValidationError();
			error.addMessageKey("MultipleValuesTextFieldValidator." + errorKey);
			validatable.error(error);
		}
		public void validate(IValidatable<String> validatable) {
			if (validatable.getValue().contains(SEPARATOR_LINE_SUBVALUE)) {
				error(validatable, "noCommasAllowed");
			}
		}
	}

	/**
	 * Return the value as raw HTML lines using <code>&lt;br /&gt;</code>. The original input is HTML escaped.
	 * @param input
	 * @return
	 */
	public static String toHtmlSafeLines(String input){
		String escapedInput = HtmlUtils.htmlEscape(input);
		String html = escapedInput.replaceAll("\\n", "<br />")
				.replaceAll(MultipleValuesTextField.SEPARATOR_LINE_SUBVALUE_REGEXP, " ");
		return html;
	}
	/**
	 * Return the value a raw HTML <code>&lt;table&gt;</code>. The original input is HTML escaped.
	 * @param input
	 * @return
	 */
	public static String toHtmlSafeTable(String input){
		StringBuffer html = new StringBuffer();
		List<String> escapedLines = MultipleValuesTextField.getValueRows(HtmlUtils.htmlEscape(input));
		if(CollectionUtils.isNotEmpty(escapedLines)){
			html.append("<table cellspacing=\"0\">");
			int lineIndex = 0;
			for(String line : escapedLines){
				List<String> escapedLineSubvalues = MultipleValuesTextField.getValueRowSubvalues(line);
				if(CollectionUtils.isNotEmpty(escapedLineSubvalues)){
					html.append(lineIndex % 2 == 0 ? "<tr class=\"even\">" :  "<tr class=\"odd\">");
					for(String subValue : escapedLineSubvalues){
						html.append("<td>");
						html.append(subValue);
						html.append("</td>");
					}
					html.append("</tr>");
				}
				lineIndex++;
			}
			html.append("</table>");
		}
//		String html = escapedInput.replaceAll("\\n", "<br />")
//				.replaceAll(MultipleValuesTextField.SEPARATOR_LINE_SUBVALUE_REGEXP, " ");
		return html.toString();
	}
	
	private static List<String> getValueRowSubvalues(String line) {
		String[] values = line.split(MultipleValuesTextField.SEPARATOR_LINE_SUBVALUE_REGEXP);
		List<String> list = Arrays.asList(values);
		return list;
	}

	/**
	 * Return the value as toPreformatedText. The original input is HTML escaped.
	 * @param input
	 * @return
	 */
	public static String toHtmlSafePreformatedText(String input){
		String escapedInput = HtmlUtils.htmlEscape(input);
		String html = escapedInput//.replaceAll("\\n", "<br />")
				.replaceAll(MultipleValuesTextField.SEPARATOR_LINE_SUBVALUE_REGEXP, "\t");
		return html;
	}
}
