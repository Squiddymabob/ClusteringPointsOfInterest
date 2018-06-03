/**
 * 
 */
package com.ew00162.clustering;

import java.util.ArrayList;

/**
 * @author Emily
 *
 *         This class defines a POI object so that clustering results can produce POIs for the JSON file
 *
 */
public class POI {
	
    private String id;

    private String longitude;

    private String latitude;

    private String description;

    private String altitude;

    private String name;

    private String images;
    
    /**=============================================== SETTERS ===============================================**/

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param lng the lng to set
	 */
	public void setLng(String lng) {
		this.longitude = lng;
	}

	/**
	 * @param lat the lat to set
	 */
	public void setLat(String lat) {
		this.latitude = lat;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param altitude the altitude to set
	 */
	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param images the images to set
	 */
	public void setImages(String images) {
		this.images = images;
	}

	/**
	 * Default constructor
	 */
	public POI() {
		super();
	}
    
	
    
    
}
