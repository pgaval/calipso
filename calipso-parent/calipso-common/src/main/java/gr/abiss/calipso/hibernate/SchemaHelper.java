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

package gr.abiss.calipso.hibernate;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/**
 * Utilities to create the database schema, drop and create tables
 * Uses Hibernate Schema tools
 * Used normally at application first start
 */
public class SchemaHelper {
    
	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(SchemaHelper.class);
    
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String hibernateDialect;
    private String dataSourceJndiName;
    private String[] mappingResources;    

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void setHibernateDialect(String hibernateDialect) {
        this.hibernateDialect = hibernateDialect;
    }
    
    public void setMappingResources(String[] mappingResources) {
        this.mappingResources = mappingResources;
    }    
    
    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDataSourceJndiName(String dataSourceJndiName) {
        this.dataSourceJndiName = dataSourceJndiName;
    }        
    
    /**
     * create tables using the given Hibernate configuration
     */
    public void updateSchema() {
        Configuration cfg = new Configuration();
        if(StringUtils.hasText(dataSourceJndiName)) {
            cfg.setProperty("hibernate.connection.datasource", dataSourceJndiName);
        } else {
            cfg.setProperty("hibernate.connection.driver_class", driverClassName);        
            cfg.setProperty("hibernate.connection.url", url);
            cfg.setProperty("hibernate.connection.username", username);
            cfg.setProperty("hibernate.connection.password", password);
        }
        cfg.setProperty("hibernate.dialect", hibernateDialect);        
        for (String resource : mappingResources) {
            cfg.addResource(resource);
        }
        logger.info("begin database schema creation =========================");
        new SchemaUpdate(cfg).execute(true, true);
        logger.info("end database schema creation ===========================");
    }
    
}
