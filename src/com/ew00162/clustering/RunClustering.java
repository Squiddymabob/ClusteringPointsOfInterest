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
		//KMeansClustering testmeans = new KMeansClustering(3, 51.244957, -0.583740, 51.241006, -0.597834, locations);
		KMeansClustering testmeans = new KMeansClustering(12, 51.242, -0.586, 51.241, -0.592, locations);
		
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

				for (Point2D.Double p : clusterPoints) {

					ArrayList<Double> cluster = testmeans.getClusterValuesForCentroid(c);

					// SilhouetteCoefficient(Double point, ArrayList<Double> cluster, ArrayList<ArrayList<Double>> clusters, ArrayList<Double> centroids)
					SilhouetteCoefficient sc = new SilhouetteCoefficient(p, cluster, KMeansClusters, KMeansCentroids);
					
					silhouetteCoefficientKMeans = silhouetteCoefficientKMeans + sc.calculateSilhouetteCoefficient();
								
					// Add the points for this cluster to the ArrayList for the DB calculations
					KMeansPointsDB.add(p);
				
				}
				// Add the cluster to the ArrayList of clusters for the DB calculations
				KMeansClustersDB.add(KMeansPointsDB);
			}
			
		}
		
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
		
		/**-------------------------------------------------------START OF DBSCAN-------------------------------------------------------*/
		System.out.println("---DBSCAN---");
		
		/*-------------------------------------------------------PERFORMING DBSCAN CLUSTERING-------------------------------------------------------*/
		
		// Create new DBSCAN
		DBSCAN testDBSCAN = new DBSCAN(0.0004, 4, locations);
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
				
				// SilhouetteCoefficient(Double point, ArrayList<Double> cluster, ArrayList<ArrayList<Double>> clusters, ArrayList<Double> centroids)
				SilhouetteCoefficient scDBSCAN = new SilhouetteCoefficient(p, DBSCANcluster, DBSCANclusters, DBSCANCentroids);
				
				silhouetteCoefficientDBSCAN = silhouetteCoefficientDBSCAN + scDBSCAN.calculateSilhouetteCoefficient();
				
				pointCount++;
				
			}
			
			iterator++;
			
		}
		
		
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
		
		/**-------------------------------------------------------START OF OWN ALGORITHM-------------------------------------------------------*/
		System.out.println("---OWN ALGORITHM---");
		
		/*-------------------------------------------------------PERFORMING OWN ALGORITHM CLUSTERING-------------------------------------------------------*/
		
		// Create new Own Algorithm
		// Box sizes should be set based on Geofence radius for application
		
		//OwnAlgorithm(double maxX, double maxY, double originX, double originY, ArrayList<Point2D.Double> points, double boxWidth, double boxHeight, int boxRows, int boxColumns) 
		//OwnAlgorithm testOwnAlgorithm = new OwnAlgorithm(16, 16, 0, 0, locations, 2, 2, 8, 8);
		OwnAlgorithm testOwnAlgorithm = new OwnAlgorithm(51.3, -0.58, 51.2, -0.6, locations, 10, 16);
		
		System.out.println(memories);
		
		testOwnAlgorithm.generateCentroids();
		
		//System.out.println(testOwnAlgorithm.findNearestCentroid(newPoint11));
		
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
		
		int iteratorCOLBOB = 0;
		
		for (Point2D.Double c : COLBOBCentroids) {
			
			for (Point2D.Double p : COLBOBClusters.get(iteratorCOLBOB)) {
				
				ArrayList<Point2D.Double> COLBOBcluster = COLBOBClusters.get(iteratorCOLBOB);
				
				// SilhouetteCoefficient(Double point, ArrayList<Double> cluster, ArrayList<ArrayList<Double>> clusters, ArrayList<Double> centroids)
				SilhouetteCoefficient scCOLBOB = new SilhouetteCoefficient(p, COLBOBcluster, COLBOBClusters, COLBOBCentroids);
				
				silhouetteCoefficientCOLBOB = silhouetteCoefficientCOLBOB + scCOLBOB.calculateSilhouetteCoefficient();
				
				pointCount++;
				
			}
			
			iteratorCOLBOB++;
			
		}
		
		
		silhouetteCoefficientCOLBOB = silhouetteCoefficientCOLBOB / locations.size();
		
		System.out.println("COLBOB OVERALL AVERAGE SILHOUETTE COEFFICIENT: " + silhouetteCoefficientCOLBOB);
		
		/*-------------------------------------------------------CALCULATING DAVIES-BOULDIN INDEX-------------------------------------------------------*/
		
		DaviesBouldinIndex COLBOBDB = new DaviesBouldinIndex(COLBOBCentroids, COLBOBClusters);
		
		System.out.println("OWN ALGORITHM DB: " + COLBOBDB.calculateDaviesBouldinIndex());
		
		/*-------------------------------------------------------WRITING THE JSON FILE-------------------------------------------------------*/
		
		
		
		
		/**-------------------------------------------------------END OF OWN ALGORITHM-------------------------------------------------------*/
		
	}

}
