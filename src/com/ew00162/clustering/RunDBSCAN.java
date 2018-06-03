/**
 * 
 */
package com.ew00162.clustering;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import com.google.gson.Gson;

/**
 * @author Emily
 *
 */
public class RunDBSCAN {
	
	/* Images need to have associated GPS locations, these are the memories to cluster */
	static Map<String, Point2D.Double> memories = new HashMap<String, Point2D.Double>();
	
	/* Input array of points for clustering */
	static ArrayList<Point2D.Double> locations = new ArrayList<Point2D.Double>();
	
	/**
	 * Loading the dataset of memories so that they can be clustered by the clustering algorithms
	 * 
	 * image1 is the image for location1 and so on
	 * 
	 * Both image names and locations MUST BE UNIQUE
	 */
	public static void loadMemories() {
		
		ReadCSV readCSV = new ReadCSV();
		memories = readCSV.readCSV();
		
		// Creating location points array for clustering algorithms
		for (String key: memories.keySet()) {
		    locations.add(memories.get(key));   
		}		
	}
	
	/**
	 * Finding the image for a location
	 * 
	 * @param map
	 * @param value
	 * @return the key (image) for that value (location)
	 */
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (Objects.equals(value, entry.getValue())) {
	        	
	        	String result = (String) entry.getKey();
	        	
	            return (T) (result);
	        }
	    }
	    return null;
	}	
	
	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Prepare the memories to be clustered
		loadMemories();
		
		/**-------------------------------------------------------START OF DBSCAN-------------------------------------------------------*/
		System.out.println("---DBSCAN---");
		
		/*-------------------------------------------------------PERFORMING DBSCAN CLUSTERING-------------------------------------------------------*/
		
		// Create new DBSCAN
		DBSCAN testDBSCAN = new DBSCAN(0.00040, 2, locations);
		//DBSCAN testDBSCAN = new DBSCAN(2, 1, locations);
		
		ArrayList<ArrayList<Point2D.Double>> DBSCANclusters = testDBSCAN.performDBSCAN();
		
		// Get the final centroids produced by DBSCAN so that this algorithm can be used to generate POIs
		ArrayList<Point2D.Double> DBSCANCentroids = testDBSCAN.generatePOIS();
		
		/*-------------------------------------------------------CALCULATING SILHOUETTE COEFFICIENT-------------------------------------------------------*/
		
		double silhouetteCoefficientDBSCAN = 0.0;
		
		int iterator = 0;
		
		// Keeps track of how many points from the dataset have been assigned to clusters as DBSCAN disregards noise
		int pointCount = 0;
		
		for (Point2D.Double c : DBSCANCentroids) {
			
			for (Point2D.Double p : DBSCANclusters.get(iterator)) {
				
				ArrayList<Point2D.Double> DBSCANcluster = DBSCANclusters.get(iterator);
				
				SilhouetteCoefficient scDBSCAN = new SilhouetteCoefficient(p, DBSCANcluster, DBSCANclusters, DBSCANCentroids);
				
				silhouetteCoefficientDBSCAN = silhouetteCoefficientDBSCAN + scDBSCAN.calculateSilhouetteCoefficient();
				
				pointCount++;		
			}		
			iterator++;		
		}
		
		// Calculate average Silhouette Coefficient
		silhouetteCoefficientDBSCAN = silhouetteCoefficientDBSCAN / pointCount;
		
		System.out.println("DBSCAN OVERALL AVERAGE SILHOUETTE COEFFICIENT: " + silhouetteCoefficientDBSCAN);
		
		System.out.println("COUNT: " + pointCount);
		
		/*-------------------------------------------------------CALCULATING DAVIES-BOULDIN INDEX-------------------------------------------------------*/
		
		DaviesBouldinIndex DBSCANDB = new DaviesBouldinIndex(DBSCANCentroids, DBSCANclusters);
		
		System.out.println("DBSCAN DB: " + DBSCANDB.calculateDaviesBouldinIndex());
		
		/*-------------------------------------------------------CALCULATING COVERAGE-------------------------------------------------------*/
		
		double coverage = 0.0;
		double size = locations.size();
		
		// Calculate coverage as a percentage
		coverage = (pointCount / size) * 100;
		
		System.out.println("DBSCAN COVERAGE: " + coverage + "%");
		
		/*-------------------------------------------------------WRITING THE JSON FILE-------------------------------------------------------*/
		
		// Get the final centroids produced by DBSCAN
		//ArrayList<Point2D.Double> DBSCANCentroids = testDBSCAN.generatePOIS();
		
		// Produced POIs
		ArrayList<POI> DBSCANPOIs = new ArrayList<POI>();
		
		// For the ID field
		int countDBSCAN = 1;
		
		// For each final centroid
		for (Point2D.Double centroid : DBSCANCentroids) {
			
			// TESTING
			System.out.println("CENTROID: " + centroid);
			
			// Generate a new POI object with the lat long of that centroid
			POI poi = new POI();
			
			poi.setLat(String.valueOf(centroid.getX()));
			poi.setLng(String.valueOf(centroid.getY()));
			
			// Set the images of the POI to be the images associated with the points in that centroid's cluster
			ArrayList<String> images = new ArrayList<String>();
			
			ArrayList<Point2D.Double> clusterContents = DBSCANclusters.get(countDBSCAN - 1);
			
			for (Point2D.Double point : clusterContents) {
				
				images.add(getKeyByValue(memories, point));
				
			}	
			
			// TESTING
			System.out.println("IMAGES: " + images);
			
			// Make images into a string
			StringBuilder builder = new StringBuilder();
			
			for (String str : images) {
			    if (builder.length() > 0) {
			        builder.append(".jpg, ");
			    }
			    builder.append(str);
			}
			
			String imagesString = builder.toString();
			
			imagesString = imagesString + ".jpg";
			
			poi.setImages(imagesString);
			
			// Set other values
			poi.setAltitude("100.0");
			poi.setDescription("description");
			poi.setId(String.valueOf(countDBSCAN));
			poi.setName("name");
			
			countDBSCAN++;
			
			// Add to produced POIs if images contained in cluster
			if (!images.isEmpty()) {
				DBSCANPOIs.add(poi);
			}
		}
		
		System.out.println(DBSCANPOIs);
		
		// Writing the JSON file
		String DBSCANJSON = new Gson().toJson(DBSCANPOIs);
		
		// Formatting for js
		DBSCANJSON = "var myJsonData = " + DBSCANJSON + ";";
		
		// TESTING - print the JSON string
		System.out.println("DBSCAN JSON: " + DBSCANJSON);
		
		System.out.println("---");
		
		/**-------------------------------------------------------END OF DBSCAN-------------------------------------------------------*/	
		
	}
}
