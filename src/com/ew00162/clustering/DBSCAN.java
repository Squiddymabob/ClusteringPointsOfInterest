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

 *         DBSCAN requires two parameters:   
 *         eps - the min distance between two points, so that if the distance between
 *         two points is lower than or equal to this value, these points are neighbours
 *         minPoints - the minimum number of points to form a dense region
 *         
 *         STEP 1: Find the eps neighbours of every point and identify the CORE POINTS with more than minPoints neighbours
 *         STEP 2: Find the connected components of CORE POINTS on the neighbour graph, ignoring all non-core points
 *         STEP 3: Assign each non-core point to a nearby cluster if the cluster is an eps neighbour, otherwise assign it to OUTLIER POINTS
 *         
 *         A point is a CORE POINT if it has more than minPoints within eps
 *         
 *         A BORDER POINT has fewer than minPoints within eps, but is in the neighbourhood of a CORE POINT
 *         
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
 *         STEP 4: If p is a BORDER POINT, no points are DENSITY-REACHABLE from p, so vist next point
 *         STEP 5: Repeat until all data points have been processed
 *         
 *         DBSCAN is not a centroid-based clustering algorithm so clusters will have no centroids
 *         Modify DBSCAN so that a cluster's centroid can be calculated for POI purposes
 *         
 *         Density-Based Spatial Clustering Algorithm with Noise
 * 
 */

public class DBSCAN {

	// Min distance between neighbours
	private double eps = 0;
	
	// Min points to form dense region
	private int minPoints = 0;
	
	//private double dist = 0;
	
	// Max X and Y bounds of graph
	// Max lat and long for GPS
	private double maxY = 0;
	private double maxX = 0;

	// Origins
	private double originX = 0;
	private double originY = 0;
	
	// List of points
	private ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
	
	// Possible point statuses
	private enum Status {
		CLUSTER, OUTLIER
	}
	
	// For setting the status of each visited point
	Map<Point2D.Double, Status> visitedPoints = new HashMap<Point2D.Double, Status>();
	
	// For finding centroids for POI
	ArrayList<ArrayList<Point2D.Double>> output = new ArrayList<ArrayList<Point2D.Double>>();
	
	/**
	 * Constructor
	 * 
	 * @param dist
	 * @param eps
	 * @param minPoints
	 * @param maxY
	 * @param maxX
	 * @param originX
	 * @param originY
	 * @param points
	 */
	public DBSCAN(double eps, int minPoints, double maxY, double maxX, double originX, double originY, ArrayList<Point2D.Double> points) {
		super();
		//this.dist = dist;
		this.eps = eps;
		this.minPoints = minPoints;
		this.maxY = maxY;
		this.maxX = maxX;
		this.originX = originX;
		this.originY = originY;
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
	 * @return
	 */
	private ArrayList<Point2D.Double> mergeLists(ArrayList<Point2D.Double> a, ArrayList<Point2D.Double> b) {
		
		ArrayList<Point2D.Double> merged = new ArrayList<Point2D.Double>();
		
		for (Point2D.Double point : b) {
			if (!a.contains(point)) {
				a.add(point);
			}
		}
		return a;
	}
	
	
	
	/**
	 * Perform DBSCAN
	 * 
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
	 *  To generate centroids for each cluster so that these can be POIs
	 */
	public void generatePOIS() {
		
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
		
	}
	
	
	
	
	
	
	
}
