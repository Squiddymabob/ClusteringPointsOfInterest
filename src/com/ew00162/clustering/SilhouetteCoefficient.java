/**
 * 
 */
package com.ew00162.clustering;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

/**
 * @author Emily
 *
 * Calculates the Silhouette Coefficient for the specified data point
 *
 */
public class SilhouetteCoefficient {
	
	/* a point */
	private Point2D.Double point;
	
	/* ArrayList of points for a cluster */
	private ArrayList<Point2D.Double> cluster;
	
	/* ArrayList of clusters */
	private ArrayList<ArrayList<Point2D.Double>> clusters;
	
	/* ArrayList of centroids */
	private ArrayList<Point2D.Double> centroids;
	
	
	/**
	 * Constructor for the Silhouette Coefficient
	 * @param point
	 * @param cluster
	 * @param clusters
	 * @param centroids
	 */
	public SilhouetteCoefficient(Point2D.Double point, ArrayList<Point2D.Double> cluster, ArrayList<ArrayList<Point2D.Double>> clusters,
			ArrayList<Point2D.Double> centroids) {
		super();
		this.point = point;
		this.cluster = cluster;
		this.clusters = clusters;
		this.centroids = centroids;
	}


	/**
	 * Calculate the average distance of the data point i to all other points in the cluster it belongs to
	 * @return a
	 */
	private double aCalculate() {
		
		double total = 0.0;
		
		// For each point in the cluster that the point is in, add the distance between it and the point to a total
		for(Point2D.Double p : cluster) {
			
			total = total + p.distance(point);
			
		}
		
		// Divide the total by the size of the cluster to get an average distance
		double a = total / cluster.size();
		
		return a;
		
	}
	
	
	/**
	 * Calculate point's nearest neighbouring cluster
	 * @return nearestCluster
	 */
	private ArrayList<Point2D.Double> getNearestNeighbouringCluster() {
		
		Point2D.Double secondNearestCentroid = centroids.get(0);
		ArrayList<Point2D.Double> nearestCluster = clusters.get(0);
		
		// Use parallel arrays
		for (int i = 0; i < centroids.size(); i++)
		{
			
			// As long as the cluster doesn't contain the point, it may be the nearest neighbouring cluster
			if(!(clusters.get(i).contains(point)))
			{
				if (centroids.get(i).distance(point) >= secondNearestCentroid.distance(point))
				{
					secondNearestCentroid = centroids.get(i);
					nearestCluster = clusters.get(i);
				}
			}
		}

		return nearestCluster;
		
	}
	
	
	/**
	 * Calculate the average distance of the data point to all points in its nearest neighbouring cluster
	 * @return b
	 */
	private double bCalculate() {
		
		double total = 0.0;
		
		// For each point in the nearest neighbouring cluster to the point, add the distance between it and the point to a total
		for (Point2D.Double p : getNearestNeighbouringCluster()) {
			
			total = total + p.distance(point);
			
		}
		
		// Divide the total by the size of the nearest neighbouring cluster to get an average distance
		double b = total / getNearestNeighbouringCluster().size();
		
		return b;
		
	}
	
	/**
	 * Calculate the Silhouette Coefficient
	 * @return the Silhouette Coefficient for the given data point
	 */
	public double calculateSilhouetteCoefficient() {
		
		double silhouetteCoefficient = 0.0;
		
		// Silhouette Coefficient equation
		silhouetteCoefficient = (bCalculate() - aCalculate()) / (Math.max(bCalculate(), aCalculate()));
		
		
		return silhouetteCoefficient;
		
	}
	

}
