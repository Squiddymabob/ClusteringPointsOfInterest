/**
 * 
 */
package com.ew00162.clustering;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import com.google.gson.Gson;

/**
 * @author Emily
 *
 */
public class RunOwnAlgorithm {
	
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
		
		/**-------------------------------------------------------START OF OWN ALGORITHM-------------------------------------------------------*/
		System.out.println("---OWN ALGORITHM---");
		
		/*-------------------------------------------------------PERFORMING OWN ALGORITHM CLUSTERING-------------------------------------------------------*/
		
		// Create new Own Algorithm
		OwnAlgorithm testOwnAlgorithm = new OwnAlgorithm(51.245809, -0.582948, 51.240449, -0.596284, locations, 12, 12);
		
		System.out.println(memories);
		
		testOwnAlgorithm.generateCentroids();
		
		testOwnAlgorithm.createClusters();
		
		System.out.println("CREATE CLUSTERS: " + testOwnAlgorithm.getClusters());
		
		System.out.println("BEFORE: " + testOwnAlgorithm.getCentroids());
		
		testOwnAlgorithm.updateCentroids();
		
		System.out.println("AFTER: " + testOwnAlgorithm.getCentroids());
		
		testOwnAlgorithm.printAllClusters();
		
		System.out.println("---");
		
		/*-------------------------------------------------------CALCULATING SILHOUETTE COEFFICIENT-------------------------------------------------------*/
		
		double silhouetteCoefficientCOLBOB= 0.0;
		
		ArrayList<Point2D.Double> COLBOBCentroids = testOwnAlgorithm.getCentroids();
		
		ArrayList<ArrayList<Point2D.Double>> COLBOBClusters = testOwnAlgorithm.getClusters();
		
		ArrayList<Integer> COLBOBPointsPerCluster = new ArrayList<Integer>();
		
		int iteratorCOLBOB = 0;
		
		double pointsPerCluster = 0.0;
		
		// Calculate the Silhouette Coefficient for all of the points in the dataset
		for (Point2D.Double c : COLBOBCentroids) {
			
			int pointCount = 0;
			
			for (Point2D.Double p : COLBOBClusters.get(iteratorCOLBOB)) {
				
				ArrayList<Point2D.Double> COLBOBcluster = COLBOBClusters.get(iteratorCOLBOB);
				
				pointCount++;
				
				SilhouetteCoefficient scCOLBOB = new SilhouetteCoefficient(p, COLBOBcluster, COLBOBClusters, COLBOBCentroids);
				
				silhouetteCoefficientCOLBOB = silhouetteCoefficientCOLBOB + scCOLBOB.calculateSilhouetteCoefficient();
				
			}
			
			COLBOBPointsPerCluster.add(pointCount);
			
			pointsPerCluster = pointsPerCluster + pointCount;
			
			iteratorCOLBOB++;
			
		}
		
		pointsPerCluster = pointsPerCluster / COLBOBPointsPerCluster.size();
		
		System.out.println("COLBOB AVERAGE POINTS PER CLUSTER: " + pointsPerCluster);
		
		System.out.println("COLBOB MAX POINTS PER CLUSTER: " + Collections.max(COLBOBPointsPerCluster));
		System.out.println("COLBOB MIN POINTS PER CLUSTER: " + Collections.min(COLBOBPointsPerCluster));
		
		silhouetteCoefficientCOLBOB = silhouetteCoefficientCOLBOB / locations.size();
		
		// Calculate the average Silhouette Coefficient
		System.out.println("COLBOB OVERALL AVERAGE SILHOUETTE COEFFICIENT: " + silhouetteCoefficientCOLBOB);
		
		/*-------------------------------------------------------CALCULATING DAVIES-BOULDIN INDEX-------------------------------------------------------*/
		
		DaviesBouldinIndex COLBOBDB = new DaviesBouldinIndex(COLBOBCentroids, COLBOBClusters);
		
		System.out.println("OWN ALGORITHM DB: " + COLBOBDB.calculateDaviesBouldinIndex());
		
		/*-------------------------------------------------------WRITING THE JSON FILE-------------------------------------------------------*/
		
		// Produced POIs
		ArrayList<POI> COLBOBPOIs = new ArrayList<POI>();
		
		// For the ID field
		int count = 1;
		
		// For each final centroid
		for (Point2D.Double centroid : COLBOBCentroids) {
			
			// TESTING
			System.out.println("CENTROID: " + centroid);
			
			// Generate a new POI object with the lat long of that centroid
			POI poi = new POI();
			
			poi.setLat(String.valueOf(centroid.getX()));
			poi.setLng(String.valueOf(centroid.getY()));
			
			// Set the images of the POI to be the images associated with the points in that centroid's cluster
			ArrayList<String> images = new ArrayList<String>();
			
			ArrayList<Point2D.Double> clusterContents = COLBOBClusters.get(count - 1);
			
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
			poi.setId(String.valueOf(count));
			poi.setName("name");
			
			count++;
			
			// Add to produced POIs if images contained in cluster
			if (!images.isEmpty()) {
				COLBOBPOIs.add(poi);
			}
		}
		
		// Writing the JSON file
		String COLBOBJSON = new Gson().toJson(COLBOBPOIs);
				
		// Formatting for js
		COLBOBJSON = "var myJsonData = " + COLBOBJSON + ";";
				
		// TESTING - print the JSON string
		System.out.println("DBSCAN JSON: " + COLBOBJSON);
		
		System.out.println("---");
		
		/**-------------------------------------------------------END OF OWN ALGORITHM-------------------------------------------------------*/
		
	}

}
