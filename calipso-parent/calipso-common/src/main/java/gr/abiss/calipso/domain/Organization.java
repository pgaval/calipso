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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Organization implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private String address;
	private String zip;
	private String phone;
	private String alternativePhone;
	private String web;
	private String email;

	private Country country;
	private String fax;
	private String vatNumber;
	private String lastUpdateComment;
	// system properties
	private Date dateCreated;
	private Date dateLastUpdated;
	private User createdBy;
	private User lastUpdatedBy;
	private Set<User> users;

	public Organization() {
	}

	public Organization(long id, String name, String address, String zip,
			String phone, String web) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.zip = zip;
		this.phone = phone;
		this.web = web;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}
	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the country
	 */
	public Country getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(Country country) {
		this.country = country;
	}

	/**
	 * @return the alternativePhone
	 */
	public String getAlternativePhone() {
		return alternativePhone;
	}

	/**
	 * @param alternativePhone
	 *            the alternativePhone to set
	 */
	public void setAlternativePhone(String alternativePhone) {
		this.alternativePhone = alternativePhone;
	}

	/**
	 * @return the fax
	 */
	public String getFax() {
		return fax;
	}

	/**
	 * @param fax
	 *            the fax to set
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}

	/**
	 * @return the vatNumber
	 */
	public String getVatNumber() {
		return vatNumber;
	}

	/**
	 * @param vatNumber
	 *            the vatNumber to set
	 */
	public void setVatNumber(String vatNumber) {
		this.vatNumber = vatNumber;
	}

	/**
	 * @return the lastUpdatedComment
	 */
	public String getLastUpdateComment() {
		return lastUpdateComment;
	}

	/**
	 * @param lastUpdatedComment
	 *            the lastUpdatedComment to set
	 */
	public void setLastUpdateComment(String lastUpdatedComment) {
		this.lastUpdateComment = lastUpdatedComment;
	}

	/**
	 * @return the dateCreated
	 */
	public Date getDateCreated() {
		return dateCreated;
	}

	/**
	 * @param dateCreated
	 *            the dateCreated to set
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * @return the dateLastUpdated
	 */
	public Date getDateLastUpdated() {
		return dateLastUpdated;
	}

	/**
	 * @param dateLastUpdated
	 *            the dateLastUpdated to set
	 */
	public void setDateLastUpdated(Date dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
	}

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the lastUpdatedBy
	 */
	public User getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	/**
	 * @param lastUpdatedBy
	 *            the lastUpdatedBy to set
	 */
	public void setLastUpdatedBy(User lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	/**
	 * @return the users
	 */
	public Set<User> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(Set<User> users) {
		this.users = users;
	}
	
	public void addUser(User user){
		if(this.users == null){
			this.users = new HashSet<User>();
		}
		user.setOrganization(this);
		this.users.add(user);
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("name", this.getName())
			.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o == this) { return true; }
		if (!(o instanceof Organization)) {
			return false;
		}
		// for some reason, this only works
		// when using getters for the 'other',
		// maybe because of Hibernate proxies
		Organization other = (Organization) o;
		return new EqualsBuilder()
		.append(this.getName(), other.getName())
		.append(this.getVatNumber(), other.getVatNumber())
	        .isEquals();
	}

}
