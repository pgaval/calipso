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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CustomCriteria {

	public static final String EQ = "==";
	public static final String GT = ">";
	public static final String LT = "<";
	public static final String GET = ">=";
	public static final String LET = "<=";

	public static final String AND = "&&";
	public static final String OR = "||";

	/**
	 * List of expresssions 
	 * */
	private List<Expression> expressionList;
	
	/**
	 * Represends the logic operator between expression's list expressions.
	 * Possible values are: 
	 *  "AND" for conjunction between the expressions, which is the default logic operator
	 *  "OR" disjunction between the expressions;
	 * 
	 **/
	private String logicOperator = AND;

	//////////////////////////////////////////

	/**
	 * 
	 * Represends (almost) all parts that requries in order to build a logical expression 
	 * @author marcello
	 *	
	 *	i.e. myNumber > 12.
	 *	where: 
	 *	"myNumber" => variable
	 *	       ">" => operator
	 *	      "12" => value
	 *	
	 *	 "Integer" => className
	 * */
	private class Expression{

		private String varible;
		private String operator;
		private String value;
		private String className;

		public Expression(String variable, String operator, String value, String className) {
			this.varible = variable;
			this.operator = operator;
			this.value = value;
			this.className = className;
		}

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getVarible() {
			return varible;
		}

		public void setVarible(String varible) {
			this.varible = varible;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}
		
	}//Expression
	
	//////////////////////////////////////////
	
	public CustomCriteria() {
		expressionList = new ArrayList<Expression>();
	}

	//------------------------------------------------------------

	public CustomCriteria(String logicOperator) {
		expressionList = new ArrayList<Expression>();
		this.logicOperator = logicOperator;
	}

	//------------------------------------------------------------

	/**
	 * Adds a new expression to criteria list.
	 * @author marcello
	 * @param variable variable name
	 * @param operator expession's operator (i.e. ">", "<", "==", etc) 
	 * @param value expession's value
	 * @param className The class name of the variable. Useful for values definition.
	 *        It can be null, if variable's type is primitive (i.e. int, long, etc.) 
	 * 
	 * */
	public void add(String variable, String operator, String value, String className){
		expressionList.add(new Expression(variable, operator, value, className));
	}
	//------------------------------------------------------------
	

	public String getCriteria(){

		StringBuilder criteria = new StringBuilder("");

		for (int i=0; i<expressionList.size(); i++){
			Expression expression = expressionList.get(i);

			criteria.append(expression.getVarible() + expression.getOperator() + expression.getValue());
			if (i<expressionList.size()-1){
				criteria.append(" " + logicOperator + " ");
			}//if

		}//for

		return criteria.toString();
	}//getCriteria
	
	//------------------------------------------------------------
	
	/**
	 * Returns a valid logical expression that can be evaluated by a java interpreter.
	 * 
	 * @param objectName 
	 * @return Valid logical expression
	 * 
	 * */
	public String getCriteriaExpression(String objectName){

		StringBuilder criteriaExpression = new StringBuilder("");

		for (int i=0; i<expressionList.size(); i++){
			Expression expression = expressionList.get(i);

			criteriaExpression
				.append(objectName).append(".").append("get").append(expression.getVarible().substring(0, 1).toUpperCase()).append(expression.getVarible().substring(1, expression.getVarible().length())).append("()")
				.append(expression.getOperator())
				.append(expression.getValue());

			if (i<expressionList.size()-1){
				criteriaExpression.append(" " + logicOperator + " ");
			}//if

		}//for

		return criteriaExpression.toString();
	}
	
	//------------------------------------------------------------
	
	public String getValuesExpression(Object object){

		StringBuilder valuesExpression = new StringBuilder("");
		//expr = "gr.abiss.calipso.domain.Item item = new gr.abiss.calipso.domain.Item(); item.setTotalResponseTime(new Double(" + itemsList.get(i).getTotalResponseTime() + ")); ";
		
		valuesExpression.append(buildNewInstance(object));
		
		for (int i=0; i<expressionList.size(); i++){
			Expression expression = expressionList.get(i);
			valuesExpression.append(getObjectName(object))
			.append(".")
			.append(buildSetter(object, expression))
			;
		}//for
		
		return valuesExpression.toString();
	}
	
	//------------------------------------------------------------
	
	private static String getObjectClass(Object object){
		return object.getClass().getCanonicalName();
	}
	
	//------------------------------------------------------------
	
	private static String getObjectName(Object object){
		return object.getClass().getSimpleName().toLowerCase();
	}
	
	//------------------------------------------------------------
	
	private static String buildSetter(Object object, Expression expression){
		
		StringBuilder setter = new  StringBuilder("");
		
		setter
		.append("set")
		.append(expression.getVarible().substring(0, 1).toUpperCase())
		.append(expression.getVarible().substring(1, expression.getVarible().length()))
		.append("(");
		
		if (expression.getClassName()!=null){
			setter.append("new ").append(expression.getClassName()).append("(");
		}//if
		
		//setter.append(expression.getValue());
		Method method = null;
		try{
			method = object.getClass().getMethod(buildGetter(expression), null);
			setter.append((String)method.invoke(object).toString());			
		}
		catch(NoSuchMethodException noSuchMethodException){
			
		}
		catch(InvocationTargetException invocationTargetException){
			
		}
		catch(IllegalAccessException illegalAccessException){
			
		}
		
		if (expression.getClassName()!=null){
			setter.append(")");
		}//if

		setter.append(");");

		return setter.toString();
	}
	
	//------------------------------------------------------------

	private static String buildGetter(Expression expression){
		StringBuilder getter = new  StringBuilder("");
		
		getter
		.append("get")
		.append(expression.getVarible().substring(0, 1).toUpperCase())
		.append(expression.getVarible().substring(1, expression.getVarible().length()));

		return getter.toString();
	}
	
	//------------------------------------------------------------
	
	private static String buildNewInstance(Object object){
		//gr.abiss.calipso.domain.Item item = new gr.abiss.calipso.domain.Item();
		StringBuilder newInstance = new StringBuilder("");

		newInstance.append(getObjectClass(object))
		.append(" ")
		.append(getObjectName(object))
		.append(" = new ")
		.append(getObjectClass(object))
		.append("();")
		;
		
		return newInstance.toString();
	}
	
}