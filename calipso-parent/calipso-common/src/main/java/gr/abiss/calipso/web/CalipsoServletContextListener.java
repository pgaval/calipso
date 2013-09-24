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

package gr.abiss.calipso.web;

import gr.abiss.calipso.CalipsoService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class CalipsoServletContextListener extends HttpServlet {
	protected static final Logger logger = Logger
			.getLogger(CalipsoServletContextListener.class);
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		logger.info("starting...");
		ServletContext ctx = config.getServletContext();
		WebApplicationContext springContext = WebApplicationContextUtils
				.getWebApplicationContext(ctx);
		CalipsoService calipso = (CalipsoService) springContext
				.getBean("calipsoService");
		calipso.runStartupPlugins();

		logger.info("finished");
	}


}
