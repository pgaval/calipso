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

import gr.abiss.calipso.CalipsoService;
import gr.abiss.calipso.acegi.CalipsoCasProxyTicketValidator;
import gr.abiss.calipso.config.CalipsoPropertiesEditor;
import gr.abiss.calipso.domain.I18nStringIdentifier;
import gr.abiss.calipso.domain.I18nStringResource;
import gr.abiss.calipso.domain.RoleType;
import gr.abiss.calipso.domain.Space;
import gr.abiss.calipso.domain.User;
import gr.abiss.calipso.wicket.components.formfields.FieldConfig;
import gr.abiss.calipso.wicket.register.RegisterAnonymousUserFormPage;
import gr.abiss.calipso.wicket.register.RegisterUserFormPage;
import gr.abiss.calipso.wicket.yui.TestPage;

import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.app.Velocity;
import org.apache.wicket.Component;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.apache.wicket.settings.IExceptionSettings.ThreadDumpStrategy;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.cookies.CookieUtils;
import org.apache.wicket.util.time.Duration;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * main wicket application for calipsoService holds singleton service layer
 * instance pulled from spring
 */
public class CalipsoApplication extends WebApplication {

	private final static Logger logger = Logger
			.getLogger(CalipsoApplication.class);

	public static final String REMEMBER_ME = "calipsoService";

	private CalipsoService calipsoService;
	private ApplicationContext applicationContext;
	private CalipsoCasProxyTicketValidator calipsoCasProxyTicketValidator;
	private CalipsoPropertiesEditor calipsoPropertiesEditor;

	private final boolean runStartupPlugins = true;

	public CalipsoService getCalipso() {
		return calipsoService;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	// used only by CasLoginPage
	public String getCasLoginUrl() {
		if (calipsoCasProxyTicketValidator == null) {
			return null;
		}
		return calipsoCasProxyTicketValidator.getLoginUrl();
	}

	// used only by logout link in HeaderPanel
	public String getCasLogoutUrl() {
		if (calipsoCasProxyTicketValidator == null) {
			return null;
		}
		return calipsoCasProxyTicketValidator.getLogoutUrl();
	}

	// @Override
	// public RequestCycle newRequestCycle(Request request, Response response) {
	// return new WebRequestCycle(this, (WebRequest)request, response){
	// @Override
	// public Page onRuntimeException(Page page, RuntimeException e) {
	// return new CalipsoErrorPage(e);
	// }
	// };
	// }

	@Override
	public void init() {

		super.init();
		// DEVELOPMENT or DEPLOYMENT
		RuntimeConfigurationType configurationType = this.getConfigurationType();
		if (RuntimeConfigurationType.DEVELOPMENT.equals(configurationType)) {
			logger.info("You are in DEVELOPMENT mode");
			getResourceSettings().setResourcePollFrequency(Duration.ONE_SECOND);
			getDebugSettings().setComponentUseCheck(true);
			// getDebugSettings().setSerializeSessionAttributes(true);
			// getMarkupSettings().setStripWicketTags(false);
			// getExceptionSettings().setUnexpectedExceptionDisplay(
			// UnexpectedExceptionDisplay.SHOW_EXCEPTION_PAGE);
			// getAjaxSettings().setAjaxDebugModeEnabled(true);
		} else if (RuntimeConfigurationType.DEPLOYMENT.equals(configurationType)) {
			getResourceSettings().setResourcePollFrequency(null);
			getDebugSettings().setComponentUseCheck(false);
			// getDebugSettings().setSerializeSessionAttributes(false);
			// getMarkupSettings().setStripWicketTags(true);
			// getExceptionSettings().setUnexpectedExceptionDisplay(
			// UnexpectedExceptionDisplay.SHOW_INTERNAL_ERROR_PAGE);
			// getAjaxSettings().setAjaxDebugModeEnabled(false);
		}
		// initialize velocity
		try {
			Velocity.init();
			if (logger.isInfoEnabled()) {
				logger.info("Initialized Velocity engine");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Failed to initialize velocity engine", e);
		}

		// Set custom page for internal errors
		getApplicationSettings().setInternalErrorPage(CalipsoErrorPage.class);

		// don't break down on missing resources
		getResourceSettings().setThrowExceptionOnMissingResource(false);

		// Redirect to PageExpiredError Page if current page is expired
		getApplicationSettings().setPageExpiredErrorPage(CalipsoPageExpiredErrorPage.class);

		// get hold of spring managed service layer (see BasePage, BasePanel etc
		// for how it is used)
		ServletContext sc = getServletContext();
		applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(sc);
		calipsoService = (CalipsoService) applicationContext
				.getBean("calipsoService");

		calipsoPropertiesEditor = new CalipsoPropertiesEditor();

		// check if acegi-cas authentication is being used, get reference to
		// object to be used
		// by wicket authentication to redirect to right pages for login /
		// logout
		try {
			calipsoCasProxyTicketValidator = (CalipsoCasProxyTicketValidator) applicationContext
					.getBean("casProxyTicketValidator");
			logger.info("casProxyTicketValidator retrieved from application context: "
					+ calipsoCasProxyTicketValidator);
		} catch (NoSuchBeanDefinitionException nsbde) {
			logger.info("casProxyTicketValidator not found in application context, CAS single-sign-on is not being used");
		}
		// delegate wicket i18n support to spring i18n
		getResourceSettings().getStringResourceLoaders().add(
				new IStringResourceLoader() {

					@Override
					public String loadStringResource(Class<?> clazz,
							String key, Locale locale, String style,
							String variation) {
						return applicationContext.getMessage(key, null, null,
								locale);
					}

					@Override
					public String loadStringResource(Component component,
							String key, Locale locale, String style,
							String variation) {
						return applicationContext.getMessage(key, null, null,
								locale);
					}
				});

		// add DB i18n resources
		getResourceSettings().getStringResourceLoaders().add(
				new IStringResourceLoader() {
					@Override
					public String loadStringResource(Class<?> clazz,
							String key, Locale locale, String style,
							String variation) {
						if (StringUtils.isNotBlank(locale.getVariant())) {
							// always ignore the variant
							locale = new Locale(locale.getLanguage(), locale
									.getCountry());
						}
						String lang = locale.getLanguage();
						I18nStringResource resource = CalipsoApplication.this.calipsoService
								.loadI18nStringResource(new I18nStringIdentifier(
										key, lang));
						if (resource == null && !lang.equalsIgnoreCase("en")) {
							resource = CalipsoApplication.this.calipsoService
									.loadI18nStringResource(new I18nStringIdentifier(
											key, "en"));
						}
						return resource != null ? resource.getValue() : null;
					}

					@Override
					public String loadStringResource(Component component,
							String key, Locale locale, String style,
							String variation) {
						locale = component == null ? Session.get()
								.getLocale() : component.getLocale();
						if (StringUtils.isNotBlank(locale.getVariant())) {
							// always ignore the variant
							locale = new Locale(locale.getLanguage(), locale
									.getCountry());
						}
						String lang = locale.getLanguage();
						I18nStringResource resource = CalipsoApplication.this.calipsoService
								.loadI18nStringResource(new I18nStringIdentifier(
										key, lang));
						if (resource == null && !lang.equalsIgnoreCase("en")) {
							resource = CalipsoApplication.this.calipsoService
									.loadI18nStringResource(new I18nStringIdentifier(
											key, "en"));
						}
						return resource != null ? resource.getValue() : null;
					}
				});
		// cache resources. resource cache is cleared when creating/updating a space
		getResourceSettings().getLocalizer().setEnableCache(true);
		getSecuritySettings().setAuthorizationStrategy(
				new IAuthorizationStrategy() {
					@Override
					public boolean isActionAuthorized(Component c, Action a) {
						return true;
					}

					@Override
					public boolean isInstantiationAuthorized(Class clazz) {
						if (BasePage.class.isAssignableFrom(clazz)) {
							if (((CalipsoSession) Session.get())
									.isAuthenticated()) {
								return true;
							}
							if (calipsoCasProxyTicketValidator != null) {
								// attempt CAS authentication
								// ==========================
								// logger.debug("checking if context contains CAS authentication");
								Authentication authentication = SecurityContextHolder
										.getContext().getAuthentication();
								if (authentication != null
										&& authentication.isAuthenticated()) {
									// logger.debug("security context contains CAS authentication, initializing session");
									((CalipsoSession) Session.get())
											.setUser((User) authentication
													.getPrincipal());
									return true;
								}
							}
							// attempt remember-me auto login
							// ==========================
							if (attemptRememberMeAutoLogin()) {
								return true;
							}

							// attempt *anonymous* guest access if there are
							// spaces that allow it
							if (((CalipsoSession) Session.get()).getUser() == null) {
								List<Space> anonymousSpaces = getCalipso()
										.findSpacesWhereAnonymousAllowed();
								if (anonymousSpaces.size() > 0) {
									// logger.debug("Found "+anonymousSpaces.size()
									// +
									// " anonymousSpaces allowing ANONYMOUS access, initializing anonymous user");
									User guestUser = new User();//getCalipso().loadUser(2);
									guestUser.setLoginName("guest");
									guestUser.setName("Anonymous");
									guestUser.setLastname("Guest");
									guestUser.setLocale(Session.get()
											.getLocale().getLanguage());
									getCalipso()
											.initImplicitRoles(guestUser,
													anonymousSpaces,
													RoleType.ANONYMOUS);
									// store user in session
									((CalipsoSession) Session.get())
											.setUser(guestUser);
									return true;
								} else {
									if (logger.isDebugEnabled()) {
										// logger.debug("Found no public spaces.");
									}
								}
							}

							// allow registration
							if (clazz.equals(RegisterUserFormPage.class)) {
								return true;
							}
							// not authenticated, go to login page
							// logger.debug("not authenticated, forcing login, page requested was "
							// + clazz.getName());
							if (calipsoCasProxyTicketValidator != null) {
								String serviceUrl = calipsoCasProxyTicketValidator.getLoginUrl();
//										.getServiceProperties().getService();
								String loginUrl = calipsoCasProxyTicketValidator
										.getLoginUrl();
								// logger.debug("cas authentication: service URL: "
								// + serviceUrl);
								String redirectUrl = loginUrl + "?service="
										+ serviceUrl;
								// logger.debug("attempting to redirect to: " +
								// redirectUrl);
								throw new RestartResponseAtInterceptPageException(
										new RedirectPage(redirectUrl));
							} else {
								throw new RestartResponseAtInterceptPageException(
										LoginPage.class);
							}
						}
						return true;
					}
				});
		// TODO: create friendly URLs for all created pages
		// friendly URLs for selected pages
		if (calipsoCasProxyTicketValidator != null) {
			mountPage("/login", CasLoginPage.class);
		} else {
			mountPage("/login", LoginPage.class);
		}
		mountPage("/register", RegisterAnonymousUserFormPage.class);
		mountPage("/logout", LogoutPage.class);
		mountPage("/svn", SvnStatsPage.class);
		mountPage("/test", TestPage.class);
		mountPage("/casError", CasLoginErrorPage.class);
		mountPage("/item/", ItemViewPage.class);
		mountPage("/item/${itemId}", ItemViewPage.class);
		mountPage("/itemreport/", ItemTemplateViewPage.class);
		mountPage("/newItem/${spaceCode}", NewItemPage.class);
//		MixedParamUrlCodingStrategy newItemUrls = new MixedParamUrlCodingStrategy(
//                "/newItem",
//                NewItemPage.class,
//                new String[]{"spaceCode"}
//        );
//        mount(newItemUrls);
		
		//fix for tinyMCE bug, see https://github.com/wicketstuff/core/issues/113
		SecurePackageResourceGuard guard = (SecurePackageResourceGuard) getResourceSettings().getPackageResourceGuard();
		guard.addPattern("+*.htm");

		this.getRequestCycleSettings().setTimeout(Duration.minutes(6));
		this.getPageSettings().setVersionPagesByDefault(true);
		this.getExceptionSettings().setThreadDumpStrategy(
				ThreadDumpStrategy.THREAD_HOLDING_LOCK);
	}

	@Override
	public Session newSession(Request request, Response response) {
		return new CalipsoSession(request);
	}

	@Override
	public Class getHomePage() {
		return DashboardPage.class;
	}

	public User authenticate(String loginName, String password) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
				loginName, password);
		AuthenticationManager am = (AuthenticationManager) applicationContext
				.getBean("authenticationManager");
		User user = null;
		try {
			Authentication authentication = am.authenticate(token);
			user = (User) authentication.getPrincipal();
		} catch (AuthenticationException ae) {
			logger.error("Acegi authentication failed", ae);
			ae.printStackTrace();
		}
		return user;
	}

	private boolean attemptRememberMeAutoLogin() {
		String value = new CookieUtils().load(REMEMBER_ME);
		if(StringUtils.isNotBlank(value)){
			int index = value.indexOf(':');
			if (index > -1) {
				String loginName = value.substring(0, index);
				String encodedPassword = value.substring(index + 1);
				// logger.debug("valid cookie, attempting authentication");
				User user = (User) getCalipso().loadUserByUsername(loginName);
				if (encodedPassword.equals(user.getPassword())) {
					((CalipsoSession) Session.get()).setUser(user);
					// user.setRoleSpaceStdFieldList(getJtrac().findSpaceFieldsForUser(user));
					// ((CalipsoSession) Session.get()).setUser(user);
		
					// logger.debug("remember me login success");
					return true;
				}
			}
		}
		// no valid cookies were found
		return false;
	}

	public String getCalipsoPropertyValue(String property) {
		return this.calipsoPropertiesEditor.getValue(property);
	}

	@Override
	protected IConverterLocator newConverterLocator() {
        ConverterLocator locator = (ConverterLocator) super.newConverterLocator();
        locator.set(FieldConfig.class, new IConverter<FieldConfig>(){

        	@Override
			public FieldConfig convertToObject(String value, Locale locale) {
				return FieldConfig.fromXML(value);
			}

        	@Override
			public String convertToString(FieldConfig value, Locale locale) {
				return FieldConfig.toXML(value);
			}
        
		});
        return locator;
	}
	
	

}
