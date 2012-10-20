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
package gr.abiss.calipso.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;


public class CalipsoPropertiesEditor implements Serializable{
	private static final Logger logger = Logger.getLogger(CalipsoPropertiesEditor.class);
	private Properties prop = null;
	private File propFile = null;
	
	public CalipsoPropertiesEditor(){
		openFile();
		
	    try {
	        prop = loadProps(propFile);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }        
	}

    private static Properties loadProps(InputStream is) throws Exception {
        Properties props = new Properties();
        try {
            props.load(is);
        } finally {
            is.close();
        }
        return props;
    }
    
    private static Properties loadProps(File file) throws Exception{
    	if(file == null){
    		return null;
    	}
    	
        InputStream is = null;
        Properties props = new Properties();
        try {
            is = new FileInputStream(file);
            props.load(is);
        } 
        finally {
        	is.close();
        }
        return props;
    }
    
    public static String getHomeFolder() {
    	return getHomeFolder(null);
    }
    
    public static String getHomeFolder(String path) {
    	String homeFolder = null;
		
		// calipso-init.properties assumed to exist
    	InputStream is =  CalipsoPropertiesEditor.class.getResourceAsStream("/calipso-init.properties");
        Properties props = null;
        try{
        	props = loadProps(is);
        }catch (Exception e) {
        	e.printStackTrace();
		}
        
        if(props != null){
        	homeFolder = props.getProperty("calipso.home");
        }
        
        if (homeFolder == null) {
            homeFolder = System.getProperty("user.home") + File.separator + ".calipso";
        }
        
        if(homeFolder == null){
        	return null;
        }
        else{
        	if(path == null){
        		return homeFolder;
        	}
        	else{
        		return homeFolder + File.separator + path;
        	}
        }
	}
    
	private void openFile(){
		String calipsoPropertiesFilePath = getHomeFolder("calipso.properties");
        
        if(calipsoPropertiesFilePath != null){
        	propFile = new File(calipsoPropertiesFilePath);
        	logger.info("Propfile path: "+propFile.getAbsolutePath());
        }
	}
	
	
	public Set<String> getParams(){
		if(prop == null){
			return null;
		}
		
		Set<String> params = new LinkedHashSet<String>();
		
	    Enumeration propertyNames = prop.propertyNames();
	    
	    while (propertyNames.hasMoreElements()) {
	        String propertyName = (String)propertyNames.nextElement();
	        
	        //don't save username and password
	        if(!(propertyName.equals("database.username") || propertyName.equals("database.password"))){
	        	params.add(propertyName);
	        }
	    }
	    
	    return params;
	}
	
	public String getValue(String param){
		if(prop == null){
			return null;
		}
		
		return prop.getProperty(param);		
	}
	
	public void setValue(String param, String value){
		if(prop == null){
			return;
		}
		
		if(value == null){
			prop.setProperty(param, "");
		}
		else{
			prop.setProperty(param, value);
		}
			
	}
	
	public void save(){
		if(prop == null || propFile == null){
			return;
		}
		
	    try {
	        prop.store(new FileOutputStream(propFile), null);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
}
