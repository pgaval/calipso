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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class InforamaDocument implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String webServiceURL;
	private String projectName;
	private String documentName;
	private String commandDescription;
	private PageDictionary pageDictionary;
	
	private Set<InforamaDocumentParameter> parameters;
	private Set<Space>spaces;

	private static final String webServiceVerbal = "WebService";
	private static final String projectVerbal = "Project";
	private static final String documentVerbal = "Document";
	private static final String parametersVerbal = "Parameters";

	///////////////////////////////////////////////////////////////////////////////////////////////
	
	public InforamaDocument() {
		this.parameters = new LinkedHashSet<InforamaDocumentParameter>();
		this.spaces = new LinkedHashSet<Space>();
		this.pageDictionary = new PageDictionary(); 
	}

	// --------------------------------------------------------------------------------------------

	public InforamaDocument(String webServiceURL, String projectName,
			String documentName, Set<InforamaDocumentParameter> parameters) {
		this.webServiceURL = webServiceURL;
		this.projectName = projectName;
		this.documentName = documentName;
		this.parameters = parameters;
		this.pageDictionary = new PageDictionary();
		this.spaces = new LinkedHashSet<Space>();
	}

	// --------------------------------------------------------------------------------------------

	public InforamaDocument(String webServiceURL, String projectName,
			String documentName) {
		this.webServiceURL = webServiceURL;
		this.projectName = projectName;
		this.documentName = documentName;
		this.parameters = new LinkedHashSet<InforamaDocumentParameter>();
		this.spaces = new LinkedHashSet<Space>();
	}	

	// --------------------------------------------------------------------------------------------

	public InforamaDocument(String webServiceURL, String projectName,
			String documentName, Set<InforamaDocumentParameter> parameters,
			Set<Space> spaces) {
		this.webServiceURL = webServiceURL;
		this.projectName = projectName;
		this.documentName = documentName;
		this.parameters = parameters;
		this.spaces = spaces;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getWebServiceURL() {
		return webServiceURL;
	}

	public void setWebServiceURL(String webServiceURL) {
		this.webServiceURL = webServiceURL;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public Set<Space> getSpaces() {
		return spaces;
	}

	public void setSpaces(Set<Space> spaces) {
		this.spaces = spaces;
	}

	public Map<String, Object> getParametersMap() {
		Map<String, Object> parametersMap = new HashMap<String, Object>();
		if (this.parameters!=null){
			for (InforamaDocumentParameter inforamaDocumentParameter: parameters){
//				try{
//					parametersMap.put(inforamaDocumentParameter.getParameterKey(), Unescape.unescape(inforamaDocumentParameter.getParameterValue().toString()));
//				}
//				catch(Exception exception){
//					parametersMap.put(inforamaDocumentParameter.getParameterKey(), inforamaDocumentParameter.getParameterValue());
//				}
				parametersMap.put(inforamaDocumentParameter.getParameterKey(), inforamaDocumentParameter.getParameterValue());
			}//for
		}
		return parametersMap;
	}

	public InforamaDocumentParameter getInforamaDocumentParameter(String parameterKey){
		for (InforamaDocumentParameter inforamaDocumentParameter: parameters){
			if (inforamaDocumentParameter.getParameterKey().equals(parameterKey)){
				return inforamaDocumentParameter;
			}
		}
		
		return null;
	}

	public void add(InforamaDocumentParameter inforamaDocumentParameter){
		InforamaDocumentParameter inforamaDocumentParameter2 = getInforamaDocumentParameter(inforamaDocumentParameter.getParameterKey());
		if (inforamaDocumentParameter2!=null){
			this.parameters.remove(inforamaDocumentParameter2);
		}
		this.parameters.add(inforamaDocumentParameter);
	}
	
	public void remove(InforamaDocumentParameter inforamaDocumentParameter){
		if (this.parameters!=null){
			this.parameters.remove(inforamaDocumentParameter);
		}
	}

	public void add(Space space){
		this.spaces.add(space);
	}

	public void remove(Space space){
		this.spaces.remove(space);
	}

	public boolean remove(ValuePair<Object> valuePair){
		return this.parameters.remove(valuePair);
	}

	public Set<InforamaDocumentParameter> getParameters() {
		return parameters;
	}

	public void setParameters(Set<InforamaDocumentParameter> parameters) {
		this.parameters = parameters;
	}

	public String getCommandDescription() {
		return commandDescription;
	}

	public void setCommandDescription(String commandDescription) {
		this.commandDescription = commandDescription;
	}

	public PageDictionary getPageDictionary() {
		return pageDictionary;
	}

	public void setPageDictionary(PageDictionary pageDictionary) {
		this.pageDictionary = pageDictionary;
	}

	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append(webServiceVerbal).append("=").append(this.webServiceURL).append(" ")
				.append(projectVerbal).append("=").append(this.projectName).append(" ")
				.append(documentVerbal).append("=").append(this.documentName);
		
		if (!this.parameters.isEmpty()){
			string.append(" ").append(parametersVerbal).append("=");
			Set<String> keys = this.getParametersMap().keySet();

			for (String key:keys){
				string.append(key).append(":").append(this.getParametersMap().get(key)).append(" ");
			}//for
		}//if

		return string.toString();
	}
	 
	public static String getWebServiceVerbal() {
		return webServiceVerbal;
	}
	
	public static String getProjectVerbal() {
		return projectVerbal;
	}
	
	public static String getDocumentVerbal() {
		return documentVerbal;
	}

	public static String getParametersVerbal() {
		return parametersVerbal;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		InforamaDocument inforamaDocument = (InforamaDocument)super.clone();
		
		return inforamaDocument;
	}

	@Override
	public int hashCode() {
		return String.valueOf(this.id).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof InforamaDocument)){
			return false;
		}
		
		InforamaDocument inforamaDocument = (InforamaDocument)o;
		return inforamaDocument.getId()==this.id;
		
	}
}