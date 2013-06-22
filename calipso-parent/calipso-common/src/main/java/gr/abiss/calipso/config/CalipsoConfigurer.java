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
 * This file incorporates work released by the Calipso project and  covered 
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
package gr.abiss.calipso.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.jfree.util.Log;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.context.ServletContextAware;

/**
 * <p>
 * Custom extension of the Spring PropertyPlaceholderConfigurer that
 * sets up the calipso.home System property (creates if required) and also creates
 * a default calipso.properties file for HSQLDB - useful for those who want
 * to quickly evaluate Calipso.  Just dropping the war into a servlet container
 * would work without the need to even configure a datasource.
 * </p>
 * <p>
 * This class would effectively do nothing if a <code>calipso.properties</code> file exists in calipso.home
 * </p>
 * <ol>
 *   <li>A "calipso.home" property is looked for in <code>/WEB-INF/classes/calipso-init.properties</code></li>
 *   <li>if not found, then a <code>calipso.home</code> system property is checked for</li>
 *   <li>then a servlet context init-parameter called <code>calipso.home</code> is looked for</li>
 *   <li>last resort, a <code>.calipso</code> folder is created in the <code>user.home</code> and used as <code>calipso.home</code></li>
 * </ol>
 * 
 * <p>
 * Other tasks
 * </p>
 * <ul>
 *   <li>initialize the "test" query for checking idle database connections</li>
 *   <li>initialize list of available locales based on the properties files available</li>
 * </ul>
 * 
 * <p>
 * Also playing an important role during startup are the following factory beans:
 * </p>
 * <ul>
 *   <li>DataSourceFactoryBean:</li>
 *     <ul>
 *       <li>switches between embedded HSQLDB or Apache DBCP (connection pool)</li>
 *       <li>performs graceful shutdown of database if embedded HSQLDB</li>
 *     </ul>
 *   <li>ProviderManagerFactoryBean</li>
 *     <ul>
 *       <li>conditionally includes LDAP authentication if requested</li>
 *     </ul>
 * </ul>
 * 
 * <p>
 * Note that later on during startup, the HibernateJtracDao would check if
 * database tables exist, and if they don't, would proceed to create them.
 * </p>
 */

public class CalipsoConfigurer extends PropertyPlaceholderConfigurer implements ServletContextAware {    

    private ServletContext servletContext;

    @Override
	public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // do our custom configuration before spring gets a chance to
        try {
            configureCalipso(beanFactory);
        } catch(Exception e) {
            throw new BeanCreationException("CalipsoConfigurer failed", e);
        }
        super.postProcessBeanFactory(beanFactory);
    }

    private void configureCalipso(ConfigurableListableBeanFactory beanFactory) throws Exception {
        String calipsoHome = null;
        InputStream is = this.getClass().getResourceAsStream("/calipso-init.properties");
        Properties props = loadProps(is);
        logger.info("found 'calipso-init.properties' on classpath, processing...");
        calipsoHome = props.getProperty("calipso.home");
        if(calipsoHome.equals("${calipso.home}")){
        	calipsoHome = null;
        }
        if (StringUtils.isBlank(calipsoHome)) {
            logger.info("valid 'calipso.home' property not available in 'calipso-init.properties', trying system properties.");
            calipsoHome = System.getProperty("calipso.home");
            if (StringUtils.isNotBlank(calipsoHome)) {
                logger.info("'calipso.home' property initialized from system properties as '" + calipsoHome + "'");
            }
        }
        if (StringUtils.isBlank(calipsoHome)) {
            logger.info("valid 'calipso.home' property not available in system properties, trying servlet init paramters.");
            calipsoHome = servletContext.getInitParameter("calipso.home");
            if (StringUtils.isNotBlank(calipsoHome)) {
                logger.info("Servlet init parameter 'calipso.home' exists: '" + calipsoHome + "'");
            }
        }
        if (StringUtils.isBlank(calipsoHome)) {
            calipsoHome = System.getProperty("user.home") + "/.calipso";
            logger.warn("Servlet init paramter  'calipso.home' does not exist.  Will use 'user.home' directory '" + calipsoHome + "'");
        }
        if (StringUtils.isNotBlank(calipsoHome) && !calipsoHome.equals("${calipso.home}")) {
            logger.info("'calipso.home' property initialized from 'calipso-init.properties' as '" + calipsoHome + "'");
        }
        //======================================================================
        FilenameFilter ff = new FilenameFilter() {
            @Override
			public boolean accept(File dir, String name) {
                return name.startsWith("messages_") && name.endsWith(".properties");
            }
        };
        //File[] messagePropsFiles = jtracInitResource.getFile().getParentFile().listFiles(ff);
		String locales = props.getProperty("calipso.locales", "en,el,ja");
//        for(File f : messagePropsFiles) {
//            int endIndex = f.getName().indexOf('.');
//            String localeCode = f.getName().substring(9, endIndex);
//            locales += "," + localeCode;
//        }
        logger.info("locales available configured are '" + locales + "'");
        props.setProperty("calipso.locales", locales);
        //======================================================================
        
        //======================================================================

        File calipsoHomeDir = new File(calipsoHome);
        createIfNotExisting(calipsoHomeDir);
        props.setProperty("calipso.home", calipsoHomeDir.getAbsolutePath());
        //======================================================================
        File attachmentsFile = new File(calipsoHome + "/attachments");
        createIfNotExisting(attachmentsFile);
        File indexesFile = new File(calipsoHome + "/indexes");
        createIfNotExisting(indexesFile);
        //======================================================================
        File propsFile = new File(calipsoHomeDir, "calipso.properties");
        if (!propsFile.exists()) {
            logger.info("properties file does not exist, creating '" + propsFile.getPath() + "'");
            propsFile.createNewFile();
            OutputStream os = new FileOutputStream(propsFile);
            Writer out = new PrintWriter(os);
            try {
                out.write("database.driver=org.hsqldb.jdbcDriver\n");
                out.write("database.url=jdbc:hsqldb:file:${calipso.home}/db/calipso\n");
                out.write("database.username=sa\n");
                out.write("database.password=\n");
                out.write("hibernate.dialect=org.hibernate.dialect.HSQLDialect\n");
                out.write("hibernate.show_sql=false\n");
                // Can be used to set mysql as default, commenting out 
                // to preserve HSQLDB as default
            	// out.write("database.driver=com.mysql.jdbc.Driver\n");
            	// out.write("database.url=jdbc:mysql://localhost/calipso21\n");
            	// out.write("database.username=root\n");
                // out.write("database.password=\n");
            	// out.write("hibernate.dialect=org.hibernate.dialect.MySQLDialect\n");
            	// out.write("hibernate.show_sql=false\n");
            } finally {
                out.close();
                os.close();
            }
            logger.info("HSQLDB will be used.  Finished creating '" + propsFile.getPath() + "'");
        } else {
            logger.info("'calipso.properties' file exists: '" + propsFile.getPath() + "'");
        }
        //======================================================================
        
        String version = getClass().getPackage().getImplementationVersion();
        String timestamp = "0000";
//        ClassPathResource versionResource = new ClassPathResource("calipso-version.properties");
//        if(versionResource.exists()) {
//            logger.info("found 'calipso-version.properties' on classpath, processing...");
//            Properties versionProps = loadProps(versionResource.getFile());
//            version = versionProps.getProperty("calipso.version");
//            timestamp = versionProps.getProperty("calipso.timestamp");
//        } else {
//            logger.info("did not find 'calipso-version.properties' on classpath");
//        }
        props.setProperty("calipso.version", version);
        props.setProperty("calipso.timestamp", timestamp);
        
        /*
         * TODO: A better way (default value) to check the database should be used for Apache DBCP.
         * The current "SELECT...FROM DUAL" only works on Oracle (and MySQL).
         * Other databases also support "SELECT 1+1" as query
         * (e.g. PostgreSQL, Hypersonic 2 (H2), MySQL, etc.).
         */
        props.setProperty("database.validationQuery", "SELECT 1 FROM DUAL");
        props.setProperty("ldap.url", "");
        props.setProperty("ldap.activeDirectoryDomain", "");
        props.setProperty("ldap.searchBase", "");
        props.setProperty("database.datasource.jndiname", "");
        // set default properties that can be overridden by user if required
        setProperties(props);
        // finally set the property that spring is expecting, manually
        FileSystemResource fsr = new FileSystemResource(propsFile);
        setLocation(fsr);
        Log.info("Calipso configured, calling postProcessBeanFactory with:" + beanFactory);
    }

	private void createIfNotExisting(File attachmentsFile) {
		if (!attachmentsFile.exists()) {
            attachmentsFile.mkdir();
            logger.info("directory does not exist, created '" + attachmentsFile.getPath() + "'");
        } else {
            logger.info("directory already exists: '" + attachmentsFile.getPath() + "'");
        }
	}

    private Properties loadProps(File file) throws Exception {
        InputStream is = null;
        Properties props = new Properties();
        try {
            is = new FileInputStream(file);
            props.load(is);
        } finally {
            is.close();
        }
        return props;
    }
    private Properties loadProps(InputStream is) throws Exception {
        Properties props = new Properties();
        try {
            props.load(is);
        } finally {
            is.close();
        }
        return props;
    }

}
