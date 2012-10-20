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
 * 
 * This file incorporates work released by the JTrac project and  covered 
 * by the following copyright and permission notice:  
 * 
 *   Copyright 2002-2005 the original author or authors.
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *   
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package gr.abiss.calipso.wicket;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.domain.CalipsoBreadCrumbBar;
import gr.abiss.calipso.domain.Country;
import gr.abiss.calipso.domain.Item;
import gr.abiss.calipso.domain.ItemRenderingTemplate;
import gr.abiss.calipso.domain.ItemSearch;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.domain.i18n.I18nResourceTranslatable;
import gr.abiss.calipso.util.BreadCrumbUtils;
import gr.abiss.calipso.util.DateUtils;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.BufferedWebResponse;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.IStringResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.tester.TagTester;
import org.apache.wicket.velocity.markup.html.VelocityPanel;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * base class for all wicket panels, this provides
 * a way to access the spring managed service layer
 */
public class BasePanel extends BreadCrumbPanel {
	
	private static final String NAME = "name";
    
	protected static final Logger logger = Logger.getLogger(BasePanel.class);
	private InfoPanel infoPanel;
//	private boolean addInfo;
	private BackLinkPanel backLinkPanel;
	
    protected CalipsoService getCalipso() {
        return ComponentUtils.getCalipso(this);
    }          
    
    protected User getPrincipal() {
        return ComponentUtils.getPrincipal(this);
    }
    
    protected void setCurrentSpace(Space space) {
        ComponentUtils.setCurrentSpace(this, space);
    }      
    
    protected Space getCurrentSpace() {
        return ComponentUtils.getCurrentSpace(this);
    }      
    
    protected void setCurrentItemSearch(ItemSearch itemSearch) {
        ComponentUtils.setCurrentItemSearch(this, itemSearch);
    }      
    
    protected ItemSearch getCurrentItemSearch() {
        return ComponentUtils.getCurrentItemSearch(this);
    }    

    protected String localize(String key) {
        return ComponentUtils.localize(this, key);
    }
    protected String localize(Country country) {
        return ComponentUtils.localize(this, country);
    }
    
    protected String localize(String key, Object... params) {
        return ComponentUtils.localize(this, key, params);
    }
    
    protected String localize(I18nResourceTranslatable rt){
    	return localize(rt, NAME);
    }
    
	protected String localize(I18nResourceTranslatable rt, String propertyName) {
		String localized = null;
		if(MapUtils.isNotEmpty(rt.getPropertyTranslations(propertyName))){
			localized = rt.getPropertyTranslations(propertyName).get(getPrincipal().getLocale());
			//localized = rt.getPropertyTranslations(propertyName).get(getSession().getLocale().getCountry().toLowerCase());
        }
        if(StringUtils.isBlank(localized)){
        	localized = localize(rt.getPropertyTranslationResourceKey(propertyName));
        }
		return localized;
	} 
    
    protected void refreshPrincipal(User user) {
        ComponentUtils.refreshPrincipal(this, user);
    }
    
    protected void refreshPrincipal() {
        ComponentUtils.refreshPrincipal(this);
    }
    
    /**
     * Returns the absolute file path for the session users's 
     * temporary upload directory. The path ends with File.separator 
     * (i.e. '/' or '\').
     * @return
     */
    protected String getPrincipalsTempDirPath(){
    	return new StringBuffer().
			append(getCalipso().getCalipsoHome()).
			append(File.separator).
			append("attachments").
			append(File.separator).
			append("TEMP").
			append(File.separator).
			append(getPrincipal().getId()).
			append(File.separator).toString();
    }
    
    protected void setInfo(String info){
        if (infoPanel!=null){
        	infoPanel.setInfo(info);
        }
        else{
//        	if (addInfo){
	        	InfoPanel infoPanel = new InfoPanel();
	        	infoPanel.setRenderBodyOnly(true);
//	        	try{
//	        		remove(infoPanel);
//	        	}
//	        	catch(Exception e){}
	        	addOrReplace(infoPanel);
	        	infoPanel.setInfo(info);
//        	}//if
        }//else
    }
    
    public BasePanel(String id) {
        this(id, null);
    }
    
    public BasePanel(String id, IBreadCrumbModel breadCrumbModel) {
        super(id, breadCrumbModel);
        //put back link.
        if(breadCrumbModel != null){
	        backLinkPanel = new BackLinkPanel("backLink", getBreadCrumbModel());
	        add(backLinkPanel);
        }
        refreshParentPageHeader();       
        // should refresh menu and headings
//        MarkupStream ms = null;
//        try{
//        	ms = getAssociatedMarkupStream(false);
//        }catch(IllegalStateException e){
//        	// see https://issues.apache.org/jira/browse/WICKET-3590
//        }
//        addInfo = ms != null && ms.findComponentIndex(null, InfoPanel.COMPONENT_ID)>-1;
        setInfo("");
    }

    
	public String getTitle() {
		return null;
	}

	public String getInfo(){
		return "";
	}
	
	public void setBackLinkPanel(BackLinkPanel backLinkPanel) {
		remove(this.backLinkPanel);
		add(backLinkPanel);
		this.backLinkPanel = backLinkPanel;
	}

	public BackLinkPanel getBackLinkPanel() {
		return backLinkPanel;
	}

	public BasePage getParentPage(){
		IBreadCrumbModel model = getBreadCrumbModel();
		
		if(model==null || !CalipsoBreadCrumbBar.class.isInstance(model)){
			return null;
		}
		
		return ((CalipsoBreadCrumbBar)model).getParentPage();
	}

	public void refreshParentPageHeader(){
		BasePage parent = getParentPage();
		
		if(parent==null){
			return;
		}
		parent.refreshHeader();
	}

	public void refreshParentMenu(){
		BasePage parent = getParentPage();
		
		if(parent==null){
			return;
		}
		parent.refreshMenu();		
	}

	public void refreshParentMenu(IBreadCrumbModel breadCrumbModel){
		BasePage parent = getParentPage();
		
		if(parent==null){
			return;
		}
		if(breadCrumbModel != null){
			parent.refreshMenu(breadCrumbModel);
		}else{
			parent.refreshMenu();
		}
	}
	// TODO: remove
	public DropDownChoice getCountriesDropDown(String markupKey, List<Country> countries){
		return new DropDownChoice(markupKey, countries, new IChoiceRenderer(){

			public Object getDisplayValue(Object object) {
				Country country = (Country) object;
				//TODO: remove temporary solution to add to messages country.
				return country.getId()+" - "+ localize(country);
			}

			public String getIdValue(Object object, int index) {
				return String.valueOf(index);
			}
		});
	}

	protected FormComponent<?> setUpAndAdd(FormComponent<?> field, WebMarkupContainer container) {
		field.add(new ErrorHighlighter());
		container.add(field.setLabel(new ResourceModel(field.getId())));
		container.add(new SimpleFormComponentLabel(field.getId()+"Label", field));
		return field;
	}

	protected FeedbackPanel getFeedbackPanel(String id) {
		FeedbackPanel feedback = new FeedbackPanel(id);
		feedback.setOutputMarkupId(true);
	    IFeedbackMessageFilter filter = new CalipsoFeedbackMessageFilter();
	    feedback.setFilter(filter);
	    return feedback;
	}

	/**
	 * &lt;div wicket:id="velocityPanelContainer"&gt;
	 *      &lt;div wicket:id="velocityPanel"&gt;&lt;/div&gt;
	 * &lt;/div&gt;
	 * @param tmpl
	 * @param context
	 */
	protected void addVelocityTemplatePanel(ItemRenderingTemplate tmpl, HashMap<String, Object> context) {
		addVelocityTemplatePanel(tmpl != null ? tmpl.getTemplateText() : null, context, false);
	}

	/**
	 * &lt;div wicket:id="velocityPanelContainer"&gt;
	 *      &lt;div wicket:id="velocityPanel"&gt;&lt;/div&gt;
	 * &lt;/div&gt;
	 * @param tmpl
	 * @param context
	 * @param parseGeneratedMarkup 
	 */
	protected void addVelocityTemplatePanel(final String tmpl, HashMap<String, Object> context, final boolean parseGeneratedMarkup){
		addVelocityTemplatePanel("velocityPanelContainer", "velocityPanel", tmpl, context, parseGeneratedMarkup);
	}

	/**
	 * &lt;div wicket:id="velocityPanelContainer"&gt;
	 *      &lt;div wicket:id="velocityPanel"&gt;&lt;/div&gt;
	 * &lt;/div&gt;
	 * @param tmpl
	 * @param context
	 * @param parseGeneratedMarkup 
	 */
	protected void addVelocityTemplatePanel(String containerId, String id,
			String tmpl, HashMap<String, Object> context,
			boolean parseGeneratedMarkup) {
		addVelocityTemplatePanel(this, containerId, id, tmpl, context, parseGeneratedMarkup);
		
	}

	/**
	 * &lt;div wicket:id="velocityPanelContainer"&gt;
	 *      &lt;div wicket:id="velocityPanel"&gt;&lt;/div&gt;
	 * &lt;/div&gt;
	 * @param tmpl
	 * @param context
	 * @param parseGeneratedMarkup 
	 */
	protected void addVelocityTemplatePanel(WebMarkupContainer container, String containerId, String panelId, final String tmpl, HashMap<String, Object> context, final boolean parseGeneratedMarkup) {
		WebMarkupContainer velocityPanelContainer = new WebMarkupContainer(containerId);
		Panel velocityPanel;
		if(StringUtils.isNotBlank(tmpl)){
			if(context == null){
				context = new HashMap<String, Object>();
			}
			context.put("DateUtils", DateUtils.class);
			
			//context.put("dateTool", new new org.apache.velocity.tools.generic.DateTool());
			velocityPanel = new VelocityPanel(panelId, new Model(context)){
					@Override
					protected IStringResourceStream getTemplateResource() {
						return new StringResourceStream(tmpl);
					}
					@Override
					protected boolean parseGeneratedMarkup(){
						return parseGeneratedMarkup;
					}
			};
		}
		else{
			velocityPanel = new EmptyPanel(panelId);
		}
		velocityPanelContainer.add(velocityPanel);
		container.add(velocityPanelContainer.setRenderBodyOnly(true));
	
	}
	
	protected void addVelocityTemplatePanel(ItemRenderingTemplate tmpl, Item item) {
		HashMap<String,Object> context = new HashMap<String,Object>();
		context.put("item", item);
		addVelocityTemplatePanel(tmpl, context);
	}
	

	public String renderPageHtmlInNewRequestCycle(Class<? extends WebPage> pageClass, PageParameters pageParameters) {
		RequestCycle requestCycle = null;
		String html = null;
		try {
			BufferedWebResponse bufferedWebResponse = new BufferedWebResponse(null);
			
//		
			// call constructor with page parameters
			Constructor constructor =
					pageClass.getConstructor(new Class[]{PageParameters.class});
			// obtain page instance
			WebPage webPage = (WebPage) constructor.newInstance(pageParameters);
			webPage.getRequestCycle().setResponse(bufferedWebResponse);
			webPage.render();
			html = bufferedWebResponse.getText().toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if(requestCycle != null){
				requestCycle.getResponse().close();
			}
		}
		return html;
		
	}

	protected void renderSighslideDirScript() {
		final String hs_graphicsDir_script = "hs.graphicsDir = '"
				+ getRequest().getPrefixToContextPath()
				+ "resources/js/highslide/graphics/';";
		add(new Behavior(){
			public void renderHead(Component component, IHeaderResponse response) {
		        response.renderJavaScript(hs_graphicsDir_script, "hs_graphicsDir_script");
			}
		});
	}
}