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

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Location point geographic information, see also the W3C Geo Vocabularies and WGS84
 */
public class GeoLocationPoint {

	private Long id;
	
	/**
	 * The WGS84 Latitude of the Point
	 */
	private double latitude;
	
	/**
	 * The WGS84 Longitude of the Point
	 */
	private double longitude;
	
	/**
	 * The WGS84 Altitude of the Point
	 */
	private double altitude;

	public GeoLocationPoint(double latitude, double longitude, double altitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	public GeoLocationPoint(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = 0;
	}
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}



	/**
	 * @return the altitude
	 */
	public double getAltitude() {
		return altitude;
	}

	/**
	 * @param altitude the altitude to set
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	@Override
	public String toString() {
		return "Latitude: " + latitude + " Longitude: " + longitude+ " Altitude: " + altitude;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) { return false; }
		if (o == this) { return true; }
		if (!(o instanceof GeoLocationPoint)) {
			return false;
		}
		// for some reason, this only works
		// when using getters for the 'other',
		// maybe because of Hibernate proxies
		GeoLocationPoint other = (GeoLocationPoint) o;
		return new EqualsBuilder()
			.append(this.getLatitude(), other.getLatitude())
	        .append(this.getLongitude(), other.getLongitude())
	        .append(this.getAltitude(), other.getAltitude())
	        .isEquals();
	}
}