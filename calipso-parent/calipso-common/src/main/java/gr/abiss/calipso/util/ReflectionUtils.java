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

package gr.abiss.calipso.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;



public class ReflectionUtils {

	/**
	 * Creates a "getter" or a "setter" for an object
	 * based on the given object property
	 * 
	 * @param prefix possible values: "get" for "getter" or "set" for "setter"
	 * @param property the property name
	 * 
	 * @return a concatenation from prefix and property where the first letter from property be transformed in upper case  
	 * 
	 * */
	public static String buildFromProperty(String prefix, String property){
		return prefix + property.substring(0, 1).toUpperCase() + property.substring(1, property.length());
	}

	//---------------------------------------------------------------------------------------------

	public static Object getValue(Object object, String methodName, String[]parameters){
		try {
			Class[] classes;
			if (parameters==null){
				parameters = new String[0];
				classes = new Class[0];
			}			
			else{
				classes = new Class[parameters.length];
				for (int i=0; i<classes.length; i++){
					classes[i] = String.class;
				}
			}
			Method method = object.getClass().getMethod(methodName, classes);
			return method.invoke((Object)object, parameters);
		} 
		catch(Exception exception){
			return null;
		}
	} 
	
	//---------------------------------------------------------------------------------------------
	
	public static List<String> getPublicMethodsForClass(String className){
		List<String> methodsList = new ArrayList<String>();
		
		try{
			Class clazz = Class.forName(className);
			
			for (int m=0; m<clazz.getDeclaredMethods().length; m++){
				Method method = clazz.getDeclaredMethods()[m];
				
				if (Modifier.isPublic(method.getModifiers())){
					methodsList.add(method.getName());
				}
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return methodsList;
	}
}