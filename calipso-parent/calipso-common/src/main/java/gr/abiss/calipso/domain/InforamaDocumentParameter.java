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

package gr.abiss.calipso.domain;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class InforamaDocumentParameter implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private static final String SEPARATOR = "||"; 
	
	private int id;
	private InforamaDocument inforamaDocument;
	private String parameterKey;
	private Object parameterValue;
	private String parameterValueParameters;

	public InforamaDocumentParameter() {
		
	}

	public InforamaDocumentParameter(InforamaDocument inforamaDocument, String parameterKey, Object parameterValue) {
		this.id = 0;
		this.inforamaDocument = inforamaDocument;
		this.parameterKey = parameterKey;
		this.parameterValue = parameterValue;
		this.parameterValueParameters = null;
	}

	public InforamaDocumentParameter(int id, InforamaDocument inforamaDocument, String parameterKey, Object parameterValue) {
		this.id = id;
		this.inforamaDocument = inforamaDocument;
		this.parameterKey = parameterKey;
		this.parameterValue = parameterValue;
		this.parameterValueParameters = null;
	}

	public InforamaDocumentParameter(int id, InforamaDocument inforamaDocument, String parameterKey, Object parameterValue, String parameterValueParameters) {
		this.id = id;
		this.inforamaDocument = inforamaDocument;
		this.parameterKey = parameterKey;
		this.parameterValue = parameterValue;
		this.parameterValueParameters = parameterValueParameters;
	}

	public InforamaDocumentParameter(InforamaDocument inforamaDocument, String parameterKey, Object parameterValue, String parameterValueParameters) {
		this.id = 0;
		this.inforamaDocument = inforamaDocument;
		this.parameterKey = parameterKey;
		this.parameterValue = parameterValue;
		this.parameterValueParameters = parameterValueParameters;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public InforamaDocument getInforamaDocument() {
		return inforamaDocument;
	}

	public void setInforamaDocument(InforamaDocument inforamaDocument) {
		this.inforamaDocument = inforamaDocument;
	}


	public String getParameterKey() {
		return parameterKey;
	}


	public void setParameterKey(String parameterKey) {
		this.parameterKey = parameterKey;
	}


	public Object getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(Object parameterValue) {
		this.parameterValue = parameterValue;
	}

	public String getParameterValueParameters() {
		return parameterValueParameters;
	}

	public void setParameterValueParameters(String parameterValueParameters) {
		this.parameterValueParameters = parameterValueParameters;
	}

	public String[] getParameterValueParametersAsArray(){
		List<String> parameterList = new ArrayList<String>();

		if (this.parameterValueParameters!=null){
			StringTokenizer stringTokenizer = new StringTokenizer(this.parameterValueParameters);
			if (stringTokenizer!=null){
				while (stringTokenizer.hasMoreTokens()){
					parameterList.add(stringTokenizer.nextToken(SEPARATOR));
				}
			}
		}

		return (String[]) parameterList.toArray(new String[parameterList.size()]);
	}

	public boolean hasParameterValueParameters(){
		return (this.parameterValueParameters!=null) || 
			(this.parameterValueParameters!=null && !this.parameterValueParameters.equals(""));
	}
	
	public static String getParameterValueParametersSeparator(){
		return SEPARATOR;
	}
	
	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer("");
		
		stringBuffer.append("id=" + String.valueOf(this.id))
					.append(" key=" + this.parameterKey);
		if (this.parameterValue!=null){
			stringBuffer.append(" value=" + this.parameterValue + " (of type " + this.parameterValue.getClass().getSimpleName() + ")");
		}//if 
		
		return stringBuffer.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o==null){
			return false;
		}//if
		
		InforamaDocumentParameter parameter = (InforamaDocumentParameter)o;
		
		return this.id==parameter.getId();
	}
}