/**
 * 
 */
package com.ew00162.clustering;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.google.gson.*;

/**
 * @author Emily
 *
 */

public class RunClustering {
	
	// Images need to have associated GPS locations, these are the memories to cluster
	static Map<String, Point2D.Double> memories = new HashMap<String, Point2D.Double>();
	
	// Input array of points for clustering
	static ArrayList<Point2D.Double> locations = new ArrayList<Point2D.Double>();
	
	/**
	 * Loading the dataset of memories so that they can be clustered by the clustering alogorithms
	 * 
	 * image1 is the image for location1 and so on
	 * 
	 * Both image names and locations MUST BE UNIQUE
	 */
	public static void loadMemories() {
		
//		// Image filenames
//		String image1 = "image1";
//		String image2 = "image2";
//		String image3 = "image3";
//		String image4 = "image4";
//		String image5 = "image5";
//		String image6 = "image6";
//		String image7 = "image7";
//		String image8 = "image8";
//		String image9 = "image9";
//		String image10 = "image10";
//		String image11 = "image11";
//		
//		// GPS locations of the images
//		Point2D.Double location1 = new Point2D.Double(8.2,11.1);
//		Point2D.Double location2 = new Point2D.Double(8.3,11.2);
//		Point2D.Double location3 = new Point2D.Double(6.0,1.3);
//		Point2D.Double location4 = new Point2D.Double(12.4,15.3);
//		Point2D.Double location5 = new Point2D.Double(8.2,11.3);
//		Point2D.Double location6 = new Point2D.Double(8.9,7.2);
//		Point2D.Double location7 = new Point2D.Double(12.3,15.9);
//		Point2D.Double location8 = new Point2D.Double(3.5,5.3);
//		Point2D.Double location9 = new Point2D.Double(1.6,0.2);
//		Point2D.Double location10 = new Point2D.Double(4.4,4.7);
//		Point2D.Double location11 = new Point2D.Double(6.0,4.0);
//		
//		// Adding the dataset of memories
//		memories.put(image1, location1);
//		memories.put(image2, location2);
//		memories.put(image3, location3);
//		memories.put(image4, location4);
//		memories.put(image5, location5);
//		memories.put(image6, location6);
//		memories.put(image7, location7);
//		memories.put(image8, location8);
//		memories.put(image9, location9);
//		memories.put(image10, location10);
//		memories.put(image11, location11);
//		
		
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
		
		/**-------------------------------------------------------START OF K MEANS-------------------------------------------------------*/
		System.out.println("---K MEANS---");
		
		/*-------------------------------------------------------PERFORMING K MEANS CLUSTERING-------------------------------------------------------*/
		
		// Create new K Means
		KMeansClustering testmeans = new KMeansClustering(3, 16, 16, 0, 0, locations);
		
		// Initialise centroids
		testmeans.createCentroids();
		System.out.println("INITIAL CENTROIDS: " + testmeans.getCentroids());
		
		// Create the clusters
		testmeans.createClusters();	
		
		System.out.println("FINAL CENTROIDS: " + testmeans.getCentroids());
		System.out.println("POINTS: " + testmeans.getPoints());
		System.out.println("NEAREST CENTROIDS: " + testmeans.getNearestCentroids());
		
		testmeans.printAllClusters();
		
		/*-------------------------------------------------------WRITING THE JSON FILE-------------------------------------------------------*/
		
		// Get the final centroids produced by K Means
		ArrayList<Point2D.Double> kMeansCentroids = testmeans.getCentroids();
		
		// Produced POIs
		ArrayList<POI> kMeansPOIs = new ArrayList<POI>();
		
		// For the ID field
		int count = 1;
		
		// For each final centroid
		for (Point2D.Double centroid : kMeansCentroids) {
			
			// TESTING
			System.out.println("CENTROID: " + centroid);
			
			// Generate a new POI object with the lat long of that centroid
			POI poi = new POI();
			
			poi.setLat(String.valueOf(centroid.getX()));
			poi.setLng(String.valueOf(centroid.getY()));
			
			// Set the images of the POI to be the images associated with the points in that centroid's cluster
			ArrayList<Point2D.Double> kMeansClusterContents = testmeans.getClusterValuesForCentroid(centroid);
			
			ArrayList<String> images = new ArrayList<String>();
			
			for (Point2D.Double point : kMeansClusterContents) {
				
				images.add(getKeyByValue(memories, point));
				
			}	
			
			// TESTING
			System.out.println("IMAGES: " + images);
			
			// Make images into a string
			StringBuilder builder = new StringBuilder();
			
			for (String str : images) {
			    if (builder.length() > 0) {
			        builder.append(", ");
			    }
			    builder.append(str);
			}
			
			String imagesString = builder.toString();
			
			poi.setImages(imagesString);
			
			// Set other values
			poi.setAltitude("100.0");
			poi.setDescription("description");
			poi.setId(String.valueOf(count));
			poi.setName("name");
			
			count++;
			
			// Add to produced POIs if images contained in cluster
			if (!images.isEmpty()) {
				kMeansPOIs.add(poi);
			}
		}
		
		System.out.println(kMeansPOIs);
		
		// Writing the JSON file
		String kMeansJSON = new Gson().toJson(kMeansPOIs);
		
		// Formatting for js
		kMeansJSON = "var myJsonData = " + kMeansJSON + ";";
		
		// TESTING - print the JSON string
		System.out.println("K Means JSON: " + kMeansJSON);
		
		System.out.println("---");
		
		/**-------------------------------------------------------END OF K MEANS-------------------------------------------------------*/
		
		/**-------------------------------------------------------START OF DBSCAN-------------------------------------------------------*/
		System.out.println("---DBSCAN---");
		
		/*-------------------------------------------------------PERFORMING DBSCAN CLUSTERING-------------------------------------------------------*/
		
		// Create new DBSCAN
		DBSCAN testDBSCAN = new DBSCAN(0.0005, 2, locations);
		//DBSCAN testDBSCAN = new DBSCAN(2, 1, locations);
		
		ArrayList<ArrayList<Double>> DBSCANclusters = testDBSCAN.performDBSCAN();
		
		// testDBSCAN.generatePOIS();
		
		/*-------------------------------------------------------WRITING THE JSON FILE-------------------------------------------------------*/
		
		// Get the final centroids produced by DBSCAN
		ArrayList<Point2D.Double> DBSCANCentroids = testDBSCAN.generatePOIS();
		
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
			        builder.append(", ");
			    }
			    builder.append(str);
			}
			
			String imagesString = builder.toString();
			
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
		
		
		
		
//		/**
//		 * For outputting JSON
//		 */
//		ArrayList<String> images = new ArrayList<String>();
//		//ArrayList<String> names = new ArrayList<String>();
//		//ArrayList<String> course = new ArrayList<String>();
//		
//		// Images need to have associated GPS locations, these are the memories to cluster
//		Map<String, Point2D.Double> memories = new HashMap<String, Point2D.Double>();
//		
//		// Adding images to array
//		// Images should be added in same order as their GPS locations are added to testset
//		images.add("image1.jpg");
//		images.add("image2.jpg");
//		images.add("image3.jpg");
//		images.add("image4.jpg");
//		images.add("image5.jpg");
//		images.add("image6.jpg");
//		images.add("image7.jpg");
//		images.add("image8.jpg");
//		images.add("image9.jpg");
//		images.add("image10.jpg");
//
//		/**
//		 * Setting up test dataset
//		 */
//		
//		// Input array of points
//		ArrayList<Point2D.Double> testset = new ArrayList<Point2D.Double>();
//		
//		// Create new points
//		Point2D.Double newPoint1 = new Point2D.Double(8.2,11.1);
//		Point2D.Double newPoint2 = new Point2D.Double(8.3,11.2);
//		Point2D.Double newPoint3 = new Point2D.Double(6.0,1.3);
//		Point2D.Double newPoint4 = new Point2D.Double(12.4,15.3);
//		Point2D.Double newPoint5 = new Point2D.Double(8.2,11.1);
//		Point2D.Double newPoint6 = new Point2D.Double(8.9,7.2);
//		Point2D.Double newPoint7 = new Point2D.Double(12.3,15.9);
//		Point2D.Double newPoint8 = new Point2D.Double(3.5,5.3);
//		Point2D.Double newPoint9 = new Point2D.Double(1.6,0.2);
//		Point2D.Double newPoint10 = new Point2D.Double(4.4,4.7);
//		Point2D.Double newPoint11 = new Point2D.Double(6.0,4.0);
//					
//		// Add new points to the list of points
//		testset.add(newPoint1);
//		testset.add(newPoint2);
//		testset.add(newPoint3);
//		testset.add(newPoint4);
//		testset.add(newPoint5);
//		testset.add(newPoint6);
//		testset.add(newPoint7);
//		testset.add(newPoint8);
//		testset.add(newPoint9);
//		testset.add(newPoint10);
//		
////		// Creating memories
////		for (int i = 0; i < images.size(); i++) {
////			
////			memories.put(images.get(i), testset.get(i));
////			
////		}
////		
////		System.out.println("MEMORIES: " + memories);
//		
//		/**
//		 * K Means
//		 */
//		System.out.println("---K MEANS---");
//		
//		// Create new K Means
//		KMeansClustering testmeans = new KMeansClustering(3, 16, 16, 0, 0, testset);
//		
//		// Initialise centroids
//		testmeans.createCentroids();
//		System.out.println("INITIAL CENTROIDS: " + testmeans.getCentroids());
//		
//		// TEST
//		//System.out.println(testmeans.euclideanDistance(newPoint1, newPoint2));
//		
//		testmeans.createClusters();	
//		
//		//System.out.println("NEW CLUSTERS");
//		
//		//testmeans.createClusters();
//		
//		System.out.println("FINAL CENTROIDS: " + testmeans.getCentroids());
//
//		System.out.println("POINTS: " + testmeans.getPoints());
//		System.out.println("NEAREST CENTROIDS: " + testmeans.getNearestCentroids());
//		
//		//System.out.println("---");
//		
//		testmeans.printAllClusters();
//		
//		System.out.println("---");
//		
//		/**
//		 * DBSCAN
//		 */
//		System.out.println("---DBSCAN---");
//		
//		// Create new DBSCAN
//		DBSCAN testDBSCAN = new DBSCAN(2, 1, testset);
//		System.out.println("DBSCAN CLUSTERS: " + testDBSCAN.performDBSCAN());
//		
//		testDBSCAN.generatePOIS();
//		
//		System.out.println("---");
//		
//		/**
//		 * Own Algorithm
//		 */
//		
//		System.out.println("---OWN ALGORITHM---");
//		
//		// Create new Own Algorithm
//		// Box sizes should be set based on Geofence radius for application
//		OwnAlgorithm testOwnAlgorithm = new OwnAlgorithm(16, 16, 0, 0, testset, 2, 2, 8, 8);
//		
//		testOwnAlgorithm.generateCentroids();
//		
//		System.out.println(testOwnAlgorithm.findNearestCentroid(newPoint11));
//		
//		testOwnAlgorithm.createClusters();
//		
//		System.out.println("CREATE CLUSTERS: " + testOwnAlgorithm.getClusters());
//		
//		System.out.println("BEFORE: " + testOwnAlgorithm.getCentroids());
//		
//		testOwnAlgorithm.updateCentroids();
//		
//		System.out.println("AFTER: " + testOwnAlgorithm.getCentroids());
//		
//		testOwnAlgorithm.printAllClusters();
//		
//		System.out.println("---");
		
	}

}
