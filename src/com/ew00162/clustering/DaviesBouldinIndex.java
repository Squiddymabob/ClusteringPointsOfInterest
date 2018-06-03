/**
 * 
 */
package com.ew00162.clustering;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Emily
 * 
 * Calculates the Davies-Bouldin Index for the specified dataset
 *
 */
public class DaviesBouldinIndex {
	
	/* ArrayList of centroids */
	private ArrayList<Point2D.Double> centroids;
	
	/* ArrayList of clusters */
	private ArrayList<ArrayList<Point2D.Double>> clusters;
	
	
	/**
	 * Constructor for the DB Index
	 * @param centroids
	 * @param clusters
	 */
	public DaviesBouldinIndex(ArrayList<Point2D.Double> centroids, ArrayList<ArrayList<Point2D.Double>> clusters) {
		super();
		this.centroids = centroids;
		this.clusters = clusters;
	}

	/**
	 * Calculate distance between every point in the ith cluster to the ith centroid
	 * @param iCluster
	 * @param iCentroid
	 * @return average distance between every point of iCluster to iCentroid
	 */
	private double calculateAverageDistance(ArrayList<Point2D.Double> iCluster, Point2D.Double iCentroid) {
		
		double sum = 0.0;
		
		for (Point2D.Double point : iCluster) {
			sum = sum + iCentroid.distance(point);
		}
		
		double size = iCluster.size();
		
		return sum / size;
	}
	
	/**
	 * Calculate distance between one centroid and another
	 * @param iCentroid
	 * @param jCentroid
	 * @return distance between iCentroid and jCentroid
	 */
	private double calculateCentroidDistance(Point2D.Double iCentroid, Point2D.Double jCentroid) {
		
		return iCentroid.distance(jCentroid);
	}

	
	/**
	 * Calculate Davies-Bouldin Index
	 * @return DB Index of given clustering algorithm and dataset
	 */
	public double calculateDaviesBouldinIndex() {
		
		// For all of the centroids, calculate DB
		int iCount = 0;
		int jCount = 0;
		double sum = 0.0;
		for (Point2D.Double iCentroid : centroids) {
			
			ArrayList<Double> DB = new ArrayList<Double>();
			
			for (Point2D.Double jCentroid : centroids) {
				
				// j must not be equal to i
				if (jCentroid != iCentroid) {
					
					// Numerator of DB equation
					double numerator = calculateAverageDistance(clusters.get(iCount), iCentroid) + calculateAverageDistance(clusters.get(jCount), jCentroid);

					// Denominator of DB equation
					double denominator = calculateCentroidDistance(iCentroid, jCentroid);

					// Add result to arraylist so that maximum for ith centroid can be determined
					DB.add((numerator / denominator));
				}				
							
			}
			
			try {
				
				// Add the maximum value for the ith centroid to the total sum
				sum = sum + Collections.max(DB);
			} catch (Exception e) {
				e.printStackTrace();
				
				// If only one cluster is generated, there are no neighbouring clusters so the DB Index cannot be calculated
				System.out.println("No neighbouring clusters, so cannot calculate DB!");
			}
			
		}
		
		double size = clusters.size();
		
		return (1 / size) * sum;
	}
	
	

}
