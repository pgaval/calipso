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

package gr.abiss.calipso.wicket.regexp;

import gr.abiss.calipso.domain.Language;
import gr.abiss.calipso.domain.ValidationExpression;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.wicket.BasePanel;
import gr.abiss.calipso.wicket.ErrorHighlighter;
import gr.abiss.calipso.wicket.CalipsoFeedbackMessageFilter;
import gr.abiss.calipso.wicket.MandatoryPanel;
import gr.abiss.calipso.wicket.OrganizationPanel;
import gr.abiss.calipso.wicket.components.validators.RegexpValidator;

import org.apache.commons.collections.MapUtils;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;


/**
 * 
 *
 */
public class ValidationExpressionFormPanel extends BasePanel {
	
	
	@Override
	public String getTitle() {
		if (this.isEdit){
			return localize("validation_Expression.edit");
		}

		return localize("validation_Expression.create");
	}
	
	private static final long serialVersionUID = 1L;

	private ValidationExpression validationExpression;
	private boolean isEdit;
	
	public ValidationExpressionFormPanel(String id, final IBreadCrumbModel breadCrumbModel) {
		super(id, breadCrumbModel);
		this.isEdit = false;
		add(new Label("label", localize("validation_Expression.create")));

		validationExpression = new ValidationExpression();
		add(new ValidationExpressionForm("form", validationExpression));
	}

	public ValidationExpressionFormPanel(String id, final IBreadCrumbModel breadCrumbModel, ValidationExpression validationExpression) {
		super(id, breadCrumbModel);
		this.isEdit = true;
		add(new Label("label", localize("validation_Expression.edit")));
		this.validationExpression = validationExpression;
		add(new ValidationExpressionForm("form", validationExpression));
	}

	private class ValidationExpressionForm extends Form{
		private static final long serialVersionUID = 1L;
		private FeedbackPanel feedback;
		private ValidationExpression validationExpression=null;
		private CalipsoFeedbackMessageFilter filter = null;
		
		
		public ValidationExpressionForm(String id, ValidationExpression validationExpression) {
			super(id);
			this.validationExpression = validationExpression;
			final CompoundPropertyModel model = new CompoundPropertyModel(this.validationExpression);
			setModel(model);
			
			// Feedback 
			FeedbackPanel feedback = new FeedbackPanel("feedback");

            filter = new CalipsoFeedbackMessageFilter();
            feedback.setFilter(filter);
            add(feedback);
            
			//Set mandatory
			// add(new MandatoryPanel("mandatoryPanel"));

			//Name
			TextField name = new TextField("name");
			name.setRequired(true);
			name.add(new ErrorHighlighter());
			add(name);
			name.setLabel(new ResourceModel("validation_Expression.name"));
			add(new SimpleFormComponentLabel("nameLabel", name));

			//expression
			TextField expression = new TextField("expression");
			expression.setRequired(true);
			expression.add(new ErrorHighlighter());
			// TODO: check if expression is a valid java regexp
			expression.add(new RegexpValidator());
			add(expression);			
			expression.setLabel(new ResourceModel("validation_Expression.expression"));
			add(new SimpleFormComponentLabel("expressionLabel", expression));
			
			//description translations

			if(MapUtils.isEmpty(validationExpression.getPropertyTranslations(ValidationExpression.DESCRIPTION))){
				validationExpression.setPropertyTranslations(ValidationExpression.DESCRIPTION, getCalipso().getPropertyTranslations(ValidationExpression.DESCRIPTION, validationExpression));	
			}
			add(new ListView("descriptionTranslations", getCalipso().getSupportedLanguages()){
				protected void populateItem(ListItem listItem) {
					//logger.debug("Building translation fields for space field: "+fieldInternalName);
					Language language = (Language) listItem.getModelObject();
					TextField description = new TextField(ValidationExpression.DESCRIPTION);
					description.setType(String.class);
					// name translations are required.
					description.setRequired(true);
					description.add(new ErrorHighlighter());
					listItem.add(description);
					
					String exp = 
							new StringBuffer("translations[")
								.append(ValidationExpression.DESCRIPTION)
								.append("][")
								.append(language.getId())
								.append("]")
								.toString();
					description.setModel(new PropertyModel(model.getObject(), exp));
					// form label for name
					StringBuffer labelBuff = new StringBuffer(localize("validation_Expression.description")).append(" (").append(localize("language."+language.getId())).append(')');
					description.setLabel(new Model(labelBuff.toString()));
					listItem.add(new SimpleFormComponentLabel("descriptionLabel", description));
				}
			});

		}

		@Override
		protected void onSubmit() {
			/*
			final String defaultValue = new String("");

			if (this.validationExpression.getName()==null){
				this.validationExpression.setName(defaultValue);
			}

			if (this.validationExpression.getDescription()==null){
				this.validationExpression.setDescription(defaultValue);
			}
			if (this.validationExpression.getExpression()==null){
				this.validationExpression.setExpression(defaultValue);
			}
			*/
			validationExpression.setDescription(validationExpression.getPropertyTranslations(ValidationExpression.DESCRIPTION).get(getCalipso().getDefaultLocale()));
			
			if (isEdit){
				activate(new IBreadCrumbPanelFactory(){
					public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
						BreadCrumbUtils.popPanels(2, breadCrumbModel);
						ValidationExpressionPanel validationExpressionPanel = new ValidationExpressionPanel(id, breadCrumbModel);
						validationExpressionPanel.setSelectedValidationExpressionId(validationExpression.getId());
						getCalipso().storeValidationExpression(validationExpression);
						return validationExpressionPanel;
					}
				});
			}
			else{
				BreadCrumbUtils.removeActiveBreadCrumbPanel(getBreadCrumbModel());
				activate(new IBreadCrumbPanelFactory(){
					public BreadCrumbPanel create(String id, IBreadCrumbModel breadCrumbModel) {
						// Check if name validation Expression name already exists
						getCalipso().storeValidationExpression(validationExpression);
						return new ValidationExpressionPanel(id, breadCrumbModel);
					}
				});
			}
			
		}
	}
}