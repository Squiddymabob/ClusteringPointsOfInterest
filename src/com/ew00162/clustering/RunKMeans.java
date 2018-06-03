/**
 * 
 */
package com.ew00162.clustering;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
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
public class RunKMeans {
	
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
		
		/**-------------------------------------------------------START OF K MEANS-------------------------------------------------------*/
		System.out.println("---K MEANS---");
		
		/*-------------------------------------------------------PERFORMING K MEANS CLUSTERING-------------------------------------------------------*/
		
		// Create new K Means
		//KMeansClustering testmeans = new KMeansClustering(3, 51.244957, -0.583740, 51.241006, -0.597834, locations);
		KMeansClustering testmeans = new KMeansClustering(7, 51.242, -0.586, 51.241, -0.592, locations);
		
		// Initialise centroids
		testmeans.createCentroids();
		System.out.println("INITIAL CENTROIDS: " + testmeans.getCentroids());
		
		// Create the clusters
		testmeans.createClusters();	
		
		System.out.println("FINAL CENTROIDS: " + testmeans.getCentroids());
		System.out.println("POINTS: " + testmeans.getPoints());
		System.out.println("NEAREST CENTROIDS: " + testmeans.getNearestCentroids());
		
		testmeans.printAllClusters();
		
		/*-------------------------------------------------------CALCULATING SILHOUETTE COEFFICIENT-------------------------------------------------------*/
		
		double silhouetteCoefficientKMeans = 0.0;
		
		ArrayList<ArrayList<Double>> KMeansClusters = new ArrayList<ArrayList<Double>>();
		
		// ArrayList for DB calculations that doesn't contain duplicate clusters
		ArrayList<ArrayList<Double>> KMeansClustersDB = new ArrayList<ArrayList<Double>>();
		
		// ArrayList for DB calculations that doesn't contain duplicate points
		ArrayList<Point2D.Double> KMeansPointsDB = new ArrayList<Double>();
		
		// ArrayList for DB calculations that doesn't contain duplicate centroids
		ArrayList<Point2D.Double> KMeansCentroidsDB = new ArrayList<Double>();
		
		ArrayList<Double> KMeansCentroids = testmeans.getCentroids();
		
		// For keeping track of which centroids have already been added to the overall silhouette coefficient
		ArrayList<Double> VisitedCentroids = new ArrayList<Double>();

		for (Point2D.Double c : testmeans.getCentroids()) {
			
			KMeansClusters.add(testmeans.getClusterValuesForCentroid(c));
			
		}
		
		for (Point2D.Double c : testmeans.getCentroids()) {
			
			// If the centroid has not already been visited, add to visited list and calculate silhouette coefficient for each of its points
			if(!(VisitedCentroids.contains(c))) {
				VisitedCentroids.add(c);
				
				// If centroid hasn't been visited, is not duplicate, so add to DB ArrayList
				KMeansCentroidsDB.add(c);
				
				ArrayList<Point2D.Double> clusterPoints = testmeans.getClusterValuesForCentroid(c);

				// Calculate Silhouette Coefficient for all of the points in the dataset
				for (Point2D.Double p : clusterPoints) {

					ArrayList<Double> cluster = testmeans.getClusterValuesForCentroid(c);

					SilhouetteCoefficient sc = new SilhouetteCoefficient(p, cluster, KMeansClusters, KMeansCentroids);
					
					silhouetteCoefficientKMeans = silhouetteCoefficientKMeans + sc.calculateSilhouetteCoefficient();
								
					// Add the points for this cluster to the ArrayList for the DB calculations
					KMeansPointsDB.add(p);
				
				}
				// Add the cluster to the ArrayList of clusters for the DB calculations
				KMeansClustersDB.add(KMeansPointsDB);
			}
			
		}
		
		// Calculate average Silhouette Coefficient
		silhouetteCoefficientKMeans = silhouetteCoefficientKMeans / locations.size();
		
		
		System.out.println("K-MEANS OVERALL AVERAGE SILHOUETTE COEFFICIENT: " + silhouetteCoefficientKMeans);
		
		/*-------------------------------------------------------CALCULATING DAVIES-BOULDIN INDEX-------------------------------------------------------*/
		
		DaviesBouldinIndex KMeansDB = new DaviesBouldinIndex(KMeansCentroidsDB, KMeansClustersDB);
		
		System.out.println("K-MEANS DB: " + KMeansDB.calculateDaviesBouldinIndex());

		
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
		
	}

}
