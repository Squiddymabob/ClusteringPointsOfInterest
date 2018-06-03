/**
 * 
 */
package com.ew00162.clustering;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Emily
 *
 *         Density-Based Spatial Clustering Algorithm with Noise
 *        
 *         DBSCAN requires two parameters:   
 *         eps - the min distance between two points, so that if the distance between
 *         two points is lower than or equal to this value, these points are neighbours
 *         minPoints - the minimum number of points to form a dense region
 *         
 *         A point is a CORE POINT if it has more than minPoints within eps
 *         A BORDER POINT has fewer than minPoints within eps, but is in the neighbourhood of a CORE POINT
 *         An OUTLIER POINT is any point that is not either of the above points
 *         
 *         A point p is DENSITY-REACHABLE from a point q if there is a chain of points p1, p2, ..., pn where p1 = q and pn = p such that pi+1
 *         is DIRECTLY DENSITY-REACHABLE from p
 *         
 *         A point p, which does not need to be a CORE POINT, is DIRECTLY DENSITY-REACHABLE from a point q if p is an element of the neighbourhood of q
 *         
 *         STEP 1: Randomly choose a point p
 *         STEP 2: Retrieve all points that are DENSITY-REACHABLE from p with respect to eps and minPoints
 *         STEP 3: If p is a CORE POINT, a cluster is formed
 *         STEP 4: If p is a BORDER POINT, no points are DENSITY-REACHABLE from p, so visit next point
 *         STEP 5: Repeat until all data points have been processed
 *         
 *         DBSCAN is not a centroid-based clustering algorithm so clusters will have no centroids
 *         Modify DBSCAN so that a cluster's centroid can be calculated for POI purposes
 *         
 */

public class DBSCAN {

	/* Min distance between neighbours */
	private double eps = 0;
	
	/* Min points to form dense region */
	private int minPoints = 0;
	
	/* List of points */
	private ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
	
	/* Possible point statuses */
	private enum Status {
		CLUSTER, OUTLIER
	}
	
	/* For setting the status of each visited point */
	Map<Point2D.Double, Status> visitedPoints = new HashMap<Point2D.Double, Status>();
	
	/* For finding centroids for POI */
	private ArrayList<ArrayList<Point2D.Double>> output = new ArrayList<ArrayList<Point2D.Double>>();
	
	/**
	 * Constructor for DBSCAN
	 * 
	 * @param eps
	 * @param minPoints
	 * @param points
	 */
	public DBSCAN(double eps, int minPoints, ArrayList<Point2D.Double> points) {
		super();
		this.eps = eps;
		this.minPoints = minPoints;
		this.points = points;
	}
	
	/**
	 * Find the eps neighbours of a point
	 * Returns a list of reachable neighbours from a point
	 * 
	 * @param point
	 * @param points
	 * @return possibleNeighbours
	 */
	public ArrayList<Point2D.Double> getPossibleNeighbours(Point2D.Double point, ArrayList<Point2D.Double> points) {
		
		ArrayList<Point2D.Double> possibleNeighbours = new ArrayList<Point2D.Double>();
		
		for (Point2D.Double neighbour : points) {
			
			// If the current point in the points list does not equal the point that is the input
			// AND the distance between input point and current point is <= eps
			// The point is a possible neighbour
			if (point != neighbour && neighbour.distance(point) <= eps) {
				possibleNeighbours.add(neighbour);
			}
		}
		return possibleNeighbours;
	}
	
	/**
	 * Merge two lists into one list
	 * 
	 * @param a
	 * @param b
	 * @return a, the merged list
	 */
	private ArrayList<Point2D.Double> mergeLists(ArrayList<Point2D.Double> a, ArrayList<Point2D.Double> b) {
		
		for (Point2D.Double point : b) {
			if (!a.contains(point)) {
				a.add(point);
			}
		}
		return a;
	}
	
	/**
	 * Perform DBSCAN
	 * @return clusters
	 */
	public ArrayList<ArrayList<Point2D.Double>> performDBSCAN() {
		
		ArrayList<ArrayList<Point2D.Double>> clusters = new ArrayList<ArrayList<Point2D.Double>>();
		
		for (Point2D.Double point : points) {
			
			// If the point has been visited before then continue as point already visited
			if (visitedPoints.get(point) != null) {
				continue;
			}
			
			// If the current point has >= neighbours than minPoints then create the cluster
			if (getPossibleNeighbours(point, points).size() >= minPoints) {
				ArrayList<Point2D.Double> cluster = new ArrayList<Point2D.Double>();
				clusters.add(createCluster(cluster, point));
			}
			else {
				visitedPoints.put(point, Status.OUTLIER);
			}
		}	
		
		// TESTING - Making sure each point has been visited and assigned a status
		System.out.println("DBSCAN PERFORM DBSCAN: " + visitedPoints);
		
		output = clusters;
		
		return clusters;
	}
	
	
	/**
	 * Create the cluster for a specified point
	 * 
	 * @param cluster
	 * @param point
	 * @return cluster
	 */
	private ArrayList<Point2D.Double> createCluster(ArrayList<Point2D.Double> cluster, Point2D.Double point) {
		
		cluster.add(point);
		
		// Current point has now been visited so set status as such
		visitedPoints.put(point, Status.CLUSTER);
		
		// A point's neighbours
		ArrayList<Point2D.Double> neighbours = getPossibleNeighbours(point, points);
		
		int i = 0;
		
		while (i < neighbours.size()) {
			
			Point2D.Double currentPoint = neighbours.get(i);
			
			Status status = visitedPoints.get(currentPoint);
			
			// Check to see if currentPoint has already been visited
			if (status == null) {
				
				// Get list of neighbours for the current point
				ArrayList<Point2D.Double> currentPointNeighbours = getPossibleNeighbours(currentPoint, points);
				
				// If current point has >= minPoints neighbours then merge the lists
				if (currentPointNeighbours.size() >= minPoints) {
					neighbours = mergeLists(neighbours, currentPointNeighbours);
				}
			}
			
			if (status != Status.CLUSTER) {
				
				// Point has now been visited and is part of cluster, so set status accordingly
				visitedPoints.put(currentPoint, Status.CLUSTER);
				
				// Add the current point to the cluster
				cluster.add(currentPoint);
			}
			i++;
		}
		
		// TESTING - Making sure the clusters have been created
		System.out.println("DBSCAN CREATE CLUSTER OUTPUT: " + cluster);
	
		return cluster;
		
	}
	
	/**
	 * To generate centroids for each cluster so that these can be POIs
	 * @return an ArrayList of all the centroids with index 0 being the centroid for index 0 of the output of DBSCAN
	 */
	public ArrayList<Point2D.Double> generatePOIS() {
		
		ArrayList<ArrayList<Point2D.Double>> clusters = output;
		
		ArrayList<Point2D.Double> centroids = new ArrayList<Point2D.Double>();
		
		// Average each cluster in clusters to find the centroid of the cluster	
		int count = 0;
		double sumX = 0.0;
		double sumY = 0.0;
		
		for (int i = 0; i < clusters.size(); i++) {
			
			for (int j = 0; j < clusters.get(i).size(); j++) {
				
				sumX = sumX + clusters.get(i).get(j).getX();
				sumY = sumY + clusters.get(i).get(j).getY();
				
				count++;
			}
			
			sumX = sumX / count;
			sumY = sumY /count;
			
			Point2D.Double newPoint = new Point2D.Double(sumX,sumY);
			centroids.add(newPoint);
			
			sumX = 0.0;
			sumY = 0.0;
			count = 0;
		}	
		
		// Print the cluster contents for each centroid
		for (int k = 0; k < clusters.size(); k++) {
			
		System.out.println("DBSCAN CLUSTER FOR CENTROID " + centroids.get(k) + ": " + clusters.get(k));
		
		}
		return centroids;	
	}	
	
}
