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
 */
public class DaviesBouldinIndex {
	
	
	private ArrayList<Point2D.Double> centroids;
	
	private ArrayList<ArrayList<Point2D.Double>> clusters;
	
	
	/**
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
	 * @return
	 */
	private double calculateAverageDistance(ArrayList<Point2D.Double> iCluster, Point2D.Double iCentroid) {
		
		double sum = 0.0;
		
		for (Point2D.Double point : iCluster) {
			sum = sum + iCentroid.distance(point);
		}
		
		double size = iCluster.size();
		
		return sum / size;
	}
	
	private double calculateCentroidDistance(Point2D.Double iCentroid, Point2D.Double jCentroid) {
		
		return iCentroid.distance(jCentroid);
	}

	
	/**
	 * Calculate Davies-Bouldin Index
	 * @return
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
			
			sum = sum + Collections.max(DB);
			
		}
		
		double size = clusters.size();
		
		return (1 / size) * sum;
	}
	
	

}
