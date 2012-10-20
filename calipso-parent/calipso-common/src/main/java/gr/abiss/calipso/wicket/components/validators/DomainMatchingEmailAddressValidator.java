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

package gr.abiss.calipso.wicket.components.validators;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.log4j.Logger;

/**
 * Custom validator to check if an email address domain part is acceptable as a
 * formal email for an organization (i.e. if it matches one of the organization domains)
 * 
 * @author manos
 * 
 */
public class DomainMatchingEmailAddressValidator extends AbstractValidator {

	private static final long serialVersionUID = 1L;
	
	protected static final Logger logger = Logger.getLogger(DomainMatchingEmailAddressValidator.class);

	private Set<String> acceptableDomains = null;

	/**
	 * Single domain constructor
	 * @param websiteAddress
	 */
	public DomainMatchingEmailAddressValidator(String websiteAddress) {
		this.reset(websiteAddress);
	}

	/**
	 * Multiple domain constructor
	 * @param websiteAddresses
	 */
	public DomainMatchingEmailAddressValidator(
			Collection<String> websiteAddresses) {
		this.reset(websiteAddresses);
	}

	/**
	 * Reset to the given list of acceptable domains
	 * @param websiteAddresses will clear/disable validation if empty
	 */
	public void reset(Collection<String> websiteAddresses) {
		this.acceptableDomains = new HashSet<String>();
		try {
			if (!websiteAddresses.isEmpty()) {
				Iterator<String> iter = websiteAddresses.iterator();
				while (iter.hasNext()) {
					// add both host and authority to the checklist
					URL url = new URL((String) iter.next());
					if(logger.isDebugEnabled()){
						logger.debug("Reset, adding website address "+url.toString());
					}
					String domain = url.getAuthority().toLowerCase();
					if(domain.startsWith("https://")){
						domain = domain.substring(8);
					}
					else if(domain.startsWith("http://")){
						domain = domain.substring(7);
					}
					if(domain.startsWith("www.")){
						domain = domain.substring(4);
					}
					acceptableDomains.add(domain);
				}
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("A malformed URL was given", e);
		}
	}

	/**
	 * Reset to use the given acceptable domain only
	 * @param websiteAddress will clear/disable validation if null
	 */
	public void reset(String websiteAddress) {
		List<String> websiteAddresses = new ArrayList<String>(1);
		if(websiteAddress != null){
			websiteAddresses.add(websiteAddress);
		}
		this.reset(websiteAddresses);
	}

	/**
	 * Checks a an email address matches any of the acceptable organization domains.
	 * Suppose email a and domain b, a match is met if one of the following is 
	 * true: a.equals(b), a.endsWith(b), b.endsWith(a)
	 * domains
	 * 
	 * @param validatable
	 *            the <code>IValidatable</code> to check
	 */
	protected void onValidate(IValidatable validatable) {
		if(acceptableDomains == null){
			throw new RuntimeException("Validator has not been initialized, the reset method must be called first.");
		}
		if(logger.isDebugEnabled()){
			logger.debug("onValidate, checking against acceptable domains: "+this.acceptableDomains);
		}
		boolean matches = false;
		if (!acceptableDomains.isEmpty()) {
			String addy = (String) validatable.getValue();
			String emailDomain = addy.substring(addy.indexOf("@") + 1)
					.toLowerCase();
			Iterator<String> iter = acceptableDomains.iterator();
			while (!matches && iter.hasNext()) {
				String acceptableDomain = iter.next();
				logger.debug("Comparing acceptable domain '"+acceptableDomain+"' with email domain '"+emailDomain+"'");
				if (emailDomain.equals(acceptableDomain) 
						|| acceptableDomain.endsWith(emailDomain)
						|| emailDomain.endsWith(acceptableDomain)) {
					matches = true;
				}
			}
		}
		else{
			matches = true;
		}
		if (!matches) {
			error(validatable);
		}
	}

}