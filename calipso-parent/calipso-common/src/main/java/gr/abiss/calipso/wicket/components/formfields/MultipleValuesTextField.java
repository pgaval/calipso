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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
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
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
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
	private int rowToEditIndex = -1;
	
	private String originalValues = "";
	private WebMarkupContainer mainContainer;
	private FieldConfig fieldConfig;
	private final List<Serializable> newSubFieldValues = new LinkedList<Serializable>();
	private final MultipleValuesTextFieldValidator subFieldNoCommasValidator = new MultipleValuesTextFieldValidator();
	private EditableFragment editableFragment;

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
		List<FieldConfig> subFieldConfigs = getSubFieldConfigs(this.fieldConfig);
		if(valuesField == null){
			valuesField = new HiddenField<String>("valuesField", this.getModel());
			mainContainer.add(valuesField);
		
			final Form form = new Form("addValueform");
			form.setOutputMarkupId(true);
			form.add(new EmptyPanel("feedback").setOutputMarkupId(true));

			mainContainer.add(form);

			final FieldSummaryHelper helper = new FieldSummaryHelper(fieldConfig);
			// create a backing model for each subfield label using a list
			// editableFragment =
			// form.addOrReplace(editableFragment.setRenderBodyOnly(true));
			

			// logger.info("rowToEditIndex: " + rowToEditIndex);
			// if (rowToEditIndex == -1) {
			// logger.info("adding editable fragment as heading");
			// editableFragment = new EditableFragment(
			// "rowLabel",
			// getSubFieldConfigs(MultipleValuesTextField.this.fieldConfig),
			// form, helper);
			// form.add(editableFragment.setRenderBodyOnly(true));
			// } else {
			// logger.info("adding label fragment as heading");
				form.add(new LabelFragment(
						"rowLabel",
						"labelFragment",
						getSubFieldConfigs(MultipleValuesTextField.this.fieldConfig),
						form, helper).setRenderBodyOnly(true));
			// }
			// show sub values table
			paintSubValuesTable(form);

	
			String s = this.getModelValue();
			this.valuesField.setModelObject(s);

		}
	}



	private List<FieldConfig> getSubFieldConfigs(FieldConfig fieldConfig) {
		List<FieldConfig> subFieldConfigs = fieldConfig != null 
				? fieldConfig.getSubFieldConfigs() 
				: new LinkedList<FieldConfig>();
		if (CollectionUtils.isEmpty(subFieldConfigs)) {
			if (fieldConfig != null) {
				subFieldConfigs.add(fieldConfig);
			} else {
				subFieldConfigs.add(FieldConfig.getFallBackFieldConfig());
			}
		}
		return subFieldConfigs;
	}

	/**
	 * Read the List<String> backing the the fields, concat the values, 
	 * append them to the hidden field's model and refresh the display of the 
	 * input thas far
	 * @param form
	 */
	private void updateModelAndRepaint(Form<?> form) {
		// logger.info("updateModelAndRepaint rowToEditIndex: " +
		// rowToEditIndex);
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
				if (StringUtils.isNotBlank(newSubFieldValues.get(i)+"")) {
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
					String[] existingValuesArr = existingValues
							.split("\\r?\\n");
					StringBuffer newValues = new StringBuffer();
					for (int i = 0; i < existingValuesArr.length; i++) {
						if (rowToEditIndex == i) {
							newValues.append(newSubValue).append("\n");
						}
						else{
							newValues.append(existingValuesArr[i]).append("\n");
						}
					}
					if (rowToEditIndex == -1) {
						newValues.append(newSubValue).append("\n");
					}
					valuesField.setModelObject(newValues.toString());
				} else {
					valuesField.setModelObject(newSubValue);
				}
				rowToEditIndex = -1;
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
	@SuppressWarnings({ "unchecked", "serial" })
	private void paintSubValuesTable(final Form form) {
		// show existing values table
		String currentValue = valuesField.getModelObject();
		final List<String> originalValueRows = MultipleValuesTextField
				.getValueRows(currentValue);

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
		final SimpleAttributeModifier cssTextAlignRight = new SimpleAttributeModifier(
				"class", "left");
		final FieldSummaryHelper helper = new FieldSummaryHelper(fieldConfig);
		form.addOrReplace(new ListView("row", originalValueRows) {

			@Override
			protected void populateItem(ListItem rowItem) {
				int rowIndex = rowItem.getIndex();
				if(rowToEditIndex == rowIndex){
					editableFragment = new EditableFragment(
							"rowContent",
							getSubFieldConfigs(MultipleValuesTextField.this.fieldConfig),
							form, helper);
					rowItem.add(editableFragment.setRenderBodyOnly(true));
				}
				else{
					rowItem.add(new ReadOnlyFragment("rowContent",
						"readOnlyFragment",
						form,
						originalValueRows, cssTextAlignRight, helper, rowItem)
						.setRenderBodyOnly(true));
				}
			}



		});

		if (rowToEditIndex == -1) {
			// logger.info("adding editable fragment as heading");
			editableFragment = new EditableFragment(
					"editableRow",
					getSubFieldConfigs(MultipleValuesTextField.this.fieldConfig),
					form, helper);
			form.addOrReplace(editableFragment);
		} else {

			form.addOrReplace(new EmptyPanel("editableRow")
					.setRenderBodyOnly(true));
		}

		form.addOrReplace(new ListView<FieldConfig>("summary", fieldConfig != null ? fieldConfig.getSubFieldConfigs() : new ArrayList<FieldConfig>(0)) {
					@Override
					protected void populateItem(ListItem<FieldConfig> cellItem) {
						int fieldConfigIndex = cellItem.getIndex();
						FieldConfig subConfig = fieldConfig.getSubFieldConfigs().get(fieldConfigIndex);
						String summary = helper.getCalculatedSummary(subConfig);
						if(StringUtils.isNotBlank(helper.getSummary(subConfig))){
							summary = getLocalizer().getString(helper.getSummary(subConfig), MultipleValuesTextField.this) + ": " + summary;
						}
						cellItem.add(new Label("cellValue", summary));
						if(subConfig.isNumberType()){
							cellItem.add(cssTextAlignRight);
						}
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
			IValidator<Serializable> {
		private void error(IValidatable<Serializable> validatable, String errorKey) {
			ValidationError error = new ValidationError();
			error.addMessageKey("MultipleValuesTextFieldValidator." + errorKey);
			validatable.error(error);
		}
		@Override
		public void validate(IValidatable<Serializable> validatable) {
			Serializable value = validatable.getValue();
			if (value != null && value.toString().contains(SEPARATOR_LINE_SUBVALUE)) {
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
	 * Return the value a raw HTML <code>&lt;table&gt;</code> without headings or summary footer. The original input is HTML escaped.
	 * @param input
	 * @return
	 */
	public static String toHtmlSafeTable(String input){
		return toHtmlSafeTable(input, null, null, null);
	}

	/**
	 * Return the value a raw HTML <code>&lt;table&gt;</code> with headings and summary. The original input is HTML escaped.
	 * @param input
	 * @return
	 */
	public static String toHtmlSafeTable(String input, FieldConfig fieldConfig, Localizer localizer, Component callerComponent){
		// logger.info("toHtmlSafeTable input: "+input+", fieldConfig: "+fieldConfig+", localizer: "+localizer+", component: "+callerComponent);
		StringBuffer html = new StringBuffer();
		List<String> escapedLines = MultipleValuesTextField.getValueRows(HtmlUtils.htmlEscape(input));
		final FieldSummaryHelper helper = new FieldSummaryHelper(fieldConfig);
		if(CollectionUtils.isNotEmpty(escapedLines)){
			html.append("<table cellspacing=\"1\" cellpadding=\"0\" class=\"custom-attribute-tabular\">");
			if(fieldConfig != null && localizer != null && callerComponent != null){
				 List<FieldConfig> configs = fieldConfig.getSubFieldConfigs();
				if(CollectionUtils.isNotEmpty(configs)){
					html.append("<thead><tr>");
					int styleMinWidth = ((100 / configs.size()) / 3) * 2;
						for(FieldConfig config :configs){
							html.append("<th style=\"min-width:" + styleMinWidth + "%\">");
							html.append(localizer.getString(config.getLabelKey(), callerComponent));
							html.append("</th>");
						}
							
					html.append("</tr></thead>");
				}
			}
			html.append("<tbody>");
			int lineIndex = 1;
			for(String line : escapedLines){
				List<String> escapedLineSubvalues = MultipleValuesTextField.getValueRowSubvalues(line);
				if(CollectionUtils.isNotEmpty(escapedLineSubvalues)){
					int styleWidth = 100/escapedLineSubvalues.size();
					html.append(lineIndex % 2 == 0 ? "<tr class=\"even\">" :  "<tr class=\"odd\">");
					int cellIndex = 0;
					for(String subValue : escapedLineSubvalues){
						FieldConfig subconfig = fieldConfig != null && CollectionUtils.isNotEmpty(fieldConfig.getSubFieldConfigs()) 
								? fieldConfig.getSubFieldConfigs().get(cellIndex) 
								: FieldConfig.FALLBACK_SUBCONFIG;
						html.append("<td ").append(subconfig.isNumberType()?" class=\"right\">":">");
						html.append(helper.parseFormat(subconfig, subValue, callerComponent != null ? callerComponent.getSession().getLocale():Locale.ENGLISH));
						html.append("</td>");
						helper.updateSummary(subconfig, subValue);
						cellIndex++;
					}
					html.append("</tr>");
				}
				lineIndex++;
			}
			html.append("</tbody>");
			if(fieldConfig !=null && CollectionUtils.isNotEmpty(fieldConfig.getSubFieldConfigs())){
				html.append("<tfoot>");
				html.append((helper.getSummaryEntriesCount()) % 2 == 0 ? "<tr class=\"even\">" :  "<tr class=\"odd\">");
				for(FieldConfig subConfig : fieldConfig.getSubFieldConfigs()){
					html.append("<td").append(subConfig.isNumberType()?" class=\"right\">":">");
					if(callerComponent != null && StringUtils.isNotBlank(subConfig.getSummary())){
						html.append(callerComponent.getLocalizer().getString(subConfig.getSummary(), callerComponent)).append(": ");
					}
					html.append(helper.getCalculatedSummary(subConfig));
					html.append("</td>");
				}
				html.append("</tr></tfoot>");
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

	public class LabelFragment extends Fragment {

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public LabelFragment(String id, String markupId,
				List<FieldConfig> subFieldConfigs, final Form form,
				final FieldSummaryHelper helper) {
			super(id, markupId, form);
			add(new ListView("subFieldsListView", subFieldConfigs) {

				@Override
				protected void populateItem(ListItem item) {
					int index = item.getIndex();
					FieldConfig fieldConfig = (FieldConfig) item
							.getModelObject();

					//
					item.add(new Label("newValueLabel", fieldConfig
							.getLabelKey() != null ? new ResourceModel(
							fieldConfig.getLabelKey()) : new Model(""))
							.setVisible(fieldConfig
							.getLabelKey() != null));
					item.add(new Label(
							"newValueHelp",
							fieldConfig.getHelpKey() != null ? new ResourceModel(
									fieldConfig.getHelpKey()) : new Model(""))
							.setVisible(fieldConfig.getHelpKey() != null));
				}

			}.setReuseItems(true));
		}

	}

	public class EditableFragment extends Fragment {

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public EditableFragment(String id,
				List<FieldConfig> subFieldConfigs, final Form form,
				final FieldSummaryHelper helper) {
			super(id, "editableFragment", form);
			// logger.info("editableFragment, subFieldConfigs: "
			// + subFieldConfigs.size());
			final IndicatingAjaxButton addButton = new IndicatingAjaxButton(
					"add") {

				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					FeedbackPanel feedback = getNewFeedbackPanel(form);
					target.addComponent(feedback);
				}

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					// clear lines counter temporarily for the inner form
					// validation to
					// work
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
			add(new ListView("subFieldsListView", subFieldConfigs) {

				@Override
				protected void populateItem(ListItem item) {
					int index = item.getIndex();
					FieldConfig fieldConfig = (FieldConfig) item
							.getModelObject();

					// initialize our model object: a list item
					if (index >= newSubFieldValues.size()) {
						newSubFieldValues.add("");

					}
					final TextField newValueField = new TextField(
							"newValueField", new PropertyModel(
									newSubFieldValues, "[" + index + "]")) {
						@Override
						protected void onComponentTag(ComponentTag tag) {
							super.onComponentTag(tag);
							// we intercept ancestor form submission and
							// explicitly click this form's submit button
							// instead
							tag.put("onkeypress",
									"if(event.keyCode == 13){document.getElementById('"
											+ addButton.getMarkupId()
											+ "').click();return false;}");
						}

						@Override
						public boolean isRequired() {
							return super.isRequired() && linesCount <= 0;
						}
					};
					newValueField.setOutputMarkupId(true);
					String fieldType = fieldConfig.getType();
					if (StringUtils.isNotBlank(fieldType)
							&& !fieldType
									.equalsIgnoreCase(FieldConfig.TYPE_STRING)) {
						if (fieldType
								.equalsIgnoreCase(FieldConfig.TYPE_DECIMAL)) {
							newValueField.setType(Double.class);
						} else if (fieldType
								.equalsIgnoreCase(FieldConfig.TYPE_INTEGER)) {
							newValueField.setType(Integer.class);
						}
					} else {
						newValueField.setType(String.class);
					}
					newValueField.add(new ErrorHighlighter());
					newValueField.setRequired(MultipleValuesTextField.this
							.isRequired() || !fieldConfig.isOptional());
					newValueField.add(new MultipleValuesTextFieldValidator());
					List<IValidator> validators = helper
							.getValidators(fieldConfig);
					if (CollectionUtils.isNotEmpty(validators)) {
						for (IValidator validator : validators) {
							newValueField.add(validator);
						}
					}
					FieldUtils.appendFieldStyles(fieldConfig, newValueField);
					item.add(newValueField);
					// TODO: add validator and field size etc.
//					newValueField
//							.setLabel(fieldConfig.getLabelKey() != null ? new ResourceModel(
//									fieldConfig.getLabelKey()) : new Model(""));
					if (fieldConfig.getSize() != null) {
						newValueField.add(new SimpleAttributeModifier("size",
								fieldConfig.getSize().toString()));
					}
					if (fieldConfig.getMaxLength() != null) {
						newValueField.add(new SimpleAttributeModifier(
								"maxlength", fieldConfig.getMaxLength()
										.toString()));
					}
//					item.add(new SimpleFormComponentLabel("newValueLabel",
//							newValueField).setVisible(fieldConfig.getLabelKey() != null));
//					item.add(new Label(
//							"newValueHelp",
//							fieldConfig.getHelpKey() != null ? new ResourceModel(
//									fieldConfig.getHelpKey()) : new Model(""))
//							.setVisible(fieldConfig.getHelpKey() != null));
				}

			}.setReuseItems(true));
			WebMarkupContainer submitControls = new WebMarkupContainer(
					"submitControls");
			submitControls.add(new SimpleAttributeModifier("colspan",
					subFieldConfigs.size() + ""));
			add(submitControls);
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
		}

	}

	public class ReadOnlyFragment extends Fragment {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public ReadOnlyFragment(String id, String markupId, final Form form,
				final List<String> originalValueRows,
				final SimpleAttributeModifier cssTextAlignRight,
				final FieldSummaryHelper helper, ListItem rowItem) {
			super(id, markupId, form);
			final int rowIndex = rowItem.getIndex();
			String subValuesString = rowItem.getModelObject().toString();
			// fix adjacent separators for lines
			subValuesString = subValuesString.replaceAll(
					SEPARATOR_LINE_SUBVALUE + SEPARATOR_LINE_SUBVALUE,
					SEPARATOR_LINE_SUBVALUE + " " + SEPARATOR_LINE_SUBVALUE);
			List<String> subValues = Arrays.asList(StringUtils
					.splitByWholeSeparator(" " + subValuesString + " ",
							SEPARATOR_LINE_SUBVALUE));
			add(new ListView("cell", subValues) {
				@Override
				protected void populateItem(ListItem cellItem) {
					int fieldConfigIndex = cellItem.getIndex();
					String subValue = cellItem.getModelObject().toString();
					subValue = StringUtils.isBlank(subValue) ? "" : subValue;
					FieldConfig subConfig = fieldConfig.getSubFieldConfigs()
							.get(fieldConfigIndex);
					// logger.info("rendering cell index: " + fieldConfigIndex
					// + " for config: " + subConfig.getLabelKey()
					// + ", got helper for label: " + helper.getLabel());
					helper.updateSummary(subConfig, subValue);
					subValue = helper.parseFormat(subConfig, subValue,
							MultipleValuesTextField.this.getSession()
									.getLocale());

					cellItem.add(new Label("cellValue", subValue));
					if (subConfig.isNumberType()) {
						cellItem.add(cssTextAlignRight);
					}
				}

			});
			add(new IndicatingAjaxLink("remove") {

				@Override
				public void onClick(AjaxRequestTarget target) {
					rowToEditIndex = -1;
					// remove the line
					List<String> currentValueRows = Arrays.asList(valuesField
							.getModelObject().split("\\r?\\n"));

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
			add(new IndicatingAjaxLink("moveup") {

				@Override
				public void onClick(AjaxRequestTarget target) {

					rowToEditIndex = -1;
					// remove the line
					List<String> currentValueRows = Arrays.asList(valuesField
							.getModelObject().split("\\r?\\n"));

					// update the model
					StringBuffer subValues = new StringBuffer();
					if (CollectionUtils.isNotEmpty(currentValueRows)) {
						// obtain and clear sub-values
						for (int i = 0; i < currentValueRows.size(); i++) {
							if (i == (rowIndex - 1)) {
								subValues.append(currentValueRows.get(i + 1));
								if (i + 1 < currentValueRows.size()) {
									subValues.append('\n');
								}
								subValues.append(currentValueRows.get(i));
								i++;
								if (i + 1 < currentValueRows.size()) {
									subValues.append('\n');
								}
							} else {
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
			}.setVisible(rowIndex > 0));
			add(new IndicatingAjaxLink("movedown") {

				@Override
				public void onClick(AjaxRequestTarget target) {

					rowToEditIndex = -1;
					// remove the line
					List<String> currentValueRows = Arrays.asList(valuesField
							.getModelObject().split("\\r?\\n"));

					// update the model
					StringBuffer subValues = new StringBuffer();
					if (CollectionUtils.isNotEmpty(currentValueRows)) {
						// obtain and clear sub-values
						for (int i = 0; i < currentValueRows.size(); i++) {
							if (i == rowIndex) {
								subValues.append(currentValueRows.get(i + 1));
								if (i + 1 < currentValueRows.size()) {
									subValues.append('\n');
								}
								subValues.append(currentValueRows.get(i));
								i++;
								if (i + 1 < currentValueRows.size()) {
									subValues.append('\n');
								}
							} else {
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
			}.setVisible(rowIndex < (originalValueRows.size() - 1)));
			add(new IndicatingAjaxLink("edit") {

				@Override
				public void onClick(AjaxRequestTarget target) {

					rowToEditIndex = rowIndex;
					// remove the line
					List<String> currentValueRows = Arrays.asList(valuesField
							.getModelObject().split("\\r?\\n"));

					// update the model
					StringBuffer subValues = new StringBuffer();
					if (CollectionUtils.isNotEmpty(currentValueRows)) {
						// obtain and clear sub-values
						for (int i = 0; i < currentValueRows.size(); i++) {
							// if (i != rowIndex) {
							subValues.append(currentValueRows.get(i));
							if (i + 1 < currentValueRows.size()) {
								subValues.append('\n');
							}
							// } else {
							if (i == rowIndex) {
								List<String> editableValues = new ArrayList(
										Arrays.asList(StringUtils.splitByWholeSeparator(
												" " + currentValueRows.get(i)
														+ " ",
												SEPARATOR_LINE_SUBVALUE)));
								newSubFieldValues.clear();
								newSubFieldValues.addAll(editableValues);
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
	}
}
