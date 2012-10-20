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

import org.apache.log4j.Logger;

import gr.abiss.calipso.domain.GeoLocationPoint;

public class GeoUtils {

	/**
	 * Please make proper use of logging, see http://www.owasp.org/index.php/Category:Logging_and_Auditing_Vulnerability
	 */
	private static final Logger logger = Logger.getLogger(GeoUtils.class);
	
	public static final short DISTANCE_UNIT_KILOMETERS = 1;
	public static final short DISTANCE_UNIT_MILES = 2;
	private static double EARTH_RADIUS_KM = 6371.009;
	
	/**
	 * Method used to convert the value form radians to degrees
	 * 
	 * @param radians
	 * @return value in degrees
	 */
	private static double toDegrees(double radians) {
		return (radians * 180.0 / Math.PI);
	}

	/**
	 * Converts the value from Degrees to radians
	 * 
	 * @param degrees
	 * @return value in radians
	 */
	private static double toRadians(double degrees) {
		return (degrees * Math.PI / 180.0);
	}

	/**
	 * Returns the difference in degrees of longitude corresponding to the
	 * distance from the center GeoLocationPoint. This distance can be used to find the
	 * extreme GeoLocationPoints.
	 * 
	 * @param p1
	 * @param distance
	 * @return
	 */
	private static double getExtremeLongitudesDiffForGeoLocationPoint(GeoLocationPoint p1,
			double distance) {
		double lat1 = p1.getLatitude();
		lat1 = toRadians(lat1);
		double longitudeRadius = Math.cos(lat1) * EARTH_RADIUS_KM;
		double diffLong = (distance / longitudeRadius);
		diffLong = toDegrees(diffLong);
		return diffLong;
	}

	/**
	 * Returns the difference in degrees of latitude corresponding to the
	 * distance from the center GeoLocationPoint. This distance can be used to find the
	 * extreme GeoLocationPoints.
	 * 
	 * @param p1
	 * @param distance
	 * @return
	 */
	private static double getExtremeLatitudesDiffForGeoLocationPoint(GeoLocationPoint p1,
			double distance) {
		double latitudeRadians = distance / EARTH_RADIUS_KM;
		double diffLat = toDegrees(latitudeRadians);
		return diffLat;
	}

	/**
	 * Returns an array of two extreme GeoLocationPoints corresponding to center GeoLocationPoint and
	 * the distance from the center GeoLocationPoint. These extreme GeoLocationPoints are the GeoLocationPoints
	 * with max/min latitude and longitude.
	 * 
	 * @param GeoLocationPoint
	 * @param distance
	 * @return
	 */
	private static GeoLocationPoint[] getExtremeGeoLocationPointsFrom(GeoLocationPoint GeoLocationPoint, Double distance) {
		double longDiff = getExtremeLongitudesDiffForGeoLocationPoint(GeoLocationPoint, distance);
		double latDiff = getExtremeLatitudesDiffForGeoLocationPoint(GeoLocationPoint, distance);
		GeoLocationPoint p1 = new GeoLocationPoint(GeoLocationPoint.getLatitude() - latDiff, GeoLocationPoint.getLongitude()
				- longDiff);
		p1 = validateGeoLocationPoint(p1);
		GeoLocationPoint p2 = new GeoLocationPoint(GeoLocationPoint.getLatitude() + latDiff, GeoLocationPoint.getLongitude()
				+ longDiff);
		p2 = validateGeoLocationPoint(p2);

		return new GeoLocationPoint[] { p1, p2 };
	}

	/**
	 * Validates if the given GeoLocationPoint instance passed has valid values 
	 * in degrees i.g. latitude lies between -90 and +90
	 * 
	 * @param GeoLocationPoint
	 * @return
	 */
	private static GeoLocationPoint validateGeoLocationPoint(GeoLocationPoint GeoLocationPoint) {
		if (GeoLocationPoint.getLatitude() > 90)
			GeoLocationPoint.setLatitude(90 - (GeoLocationPoint.getLatitude() - 90));
		if (GeoLocationPoint.getLatitude() < -90)
			GeoLocationPoint.setLatitude(-90 - (GeoLocationPoint.getLatitude() + 90));
		if (GeoLocationPoint.getLongitude() > 180)
			GeoLocationPoint.setLongitude(-180 + (GeoLocationPoint.getLongitude() - 180));
		if (GeoLocationPoint.getLongitude() < -180)
			GeoLocationPoint.setLongitude(180 + (GeoLocationPoint.getLongitude() + 180));

		return GeoLocationPoint;
	}

	/**
	 * Returns the distance between two GeoLocationPoint instances
	 * 
	 * @param p1
	 * @param p2
	 * @param unit The unit in which the returned distance is to be expressed which can be 
	 * one of kilometers or miles. The value of the parameter may be one of the public static DISTANCE_UNIT_XX members.
	 * @return
	 */
	private static double getDistanceBetweenGeoLocationPoints(GeoLocationPoint p1, GeoLocationPoint p2, Short unit) {
		double theta = p1.getLongitude() - p2.getLongitude();
		double dist = Math.sin(toRadians(p1.getLatitude()))
				* Math.sin(toRadians(p2.getLatitude()))
				+ Math.cos(toRadians(p1.getLatitude()))
				* Math.cos(toRadians(p2.getLatitude())) * Math.cos(toRadians(theta));
		dist = Math.acos(dist);
		dist = toDegrees(dist);
		dist = dist * 60 * 1.1515;
		if (unit == DISTANCE_UNIT_KILOMETERS) {
			dist = dist * 1.609344;
		} else if (unit == DISTANCE_UNIT_MILES) {
			dist = dist * 0.8684;
		}
		return (dist);
	}

}

