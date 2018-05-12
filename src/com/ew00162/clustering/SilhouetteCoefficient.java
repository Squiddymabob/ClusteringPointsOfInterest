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
	
	private Point2D.Double point;
	
	private ArrayList<Point2D.Double> cluster;
	
	private ArrayList<ArrayList<Point2D.Double>> clusters;
	
	private ArrayList<Point2D.Double> centroids;
	
	
	/**
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
		
		for(Point2D.Double p : cluster) {
			
			total = total + p.distance(point);
			
		}
		
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
		// use parallel arrays
		for (int i = 0; i < centroids.size(); i++)
		{
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
		
		for (Point2D.Double p : getNearestNeighbouringCluster()) {
			
			total = total + p.distance(point);
			
		}
		
		double b = total / getNearestNeighbouringCluster().size();
		
		return b;
		
	}
	
	
	public double calculateSilhouetteCoefficient() {
		
		double silhouetteCoefficient = 0.0;
		
		
		silhouetteCoefficient = (bCalculate() - aCalculate()) / (Math.max(bCalculate(), aCalculate()));
		
		
		return silhouetteCoefficient;
		
	}
	

}
