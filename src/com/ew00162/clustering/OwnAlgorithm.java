/**
 * 
 */
package com.ew00162.clustering;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author Emily
 * 
 *         STEP 1: Generate initial centroids, one in centre of each square
 *         STEP 2: Find nearest centroid to each point and assign the point to the centroid to form clusters
 *         STEP 3: Average the clusters to find the new centroid for each square
 *
 */
public class OwnAlgorithm {
	
	
	// Max X and Y bounds of graph
	// Max lat and long for GPS
	private double maxY = 0;
	private double maxX = 0;

	// Origins
	private double originX = 0;
	private double originY = 0;

	// List of points
	private ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();

	// Box size
	private double boxWidth = 0.0;
	private double boxHeight = 0.0;
	
	private double border = 0.0;
	//private double innerSquareSize = squareSize - border;
	
	// Number of squares
	private int boxRows = 0;
	private int boxColumns = 0;
	
	// Centroids
	private ArrayList<Point2D.Double> centroids = new ArrayList<Point2D.Double>();
	
	// Clusters
	private ArrayList<ArrayList<Point2D.Double>> clusters = new ArrayList<ArrayList<Point2D.Double>>();
	
	
	/**
	 * @return the clusters
	 */
	public ArrayList<ArrayList<Point2D.Double>> getClusters() {
		return clusters;
	}


	/**
	 * @return the centroids
	 */
	public ArrayList<Point2D.Double> getCentroids() {
		return centroids;
	}


	/**
	 * @param maxY
	 * @param maxX
	 * @param originX
	 * @param originY
	 * @param points
	 * @param squareSize
	 */
	public OwnAlgorithm(double maxY, double maxX, double originX, double originY, ArrayList<Point2D.Double> points, double boxWidth, double boxHeight, int boxRows, int boxColumns) {
		super();
		this.maxY = maxY;
		this.maxX = maxX;
		this.originX = originX;
		this.originY = originY;
		this.points = points;
		this.boxWidth = boxWidth;
		this.boxHeight = boxHeight;
		this.boxRows = boxRows;
		this.boxColumns = boxColumns;
	}


	/**
	 * STEP 1
	 * Generate centroids in centre of each square
	 * Generates row by row
	 */
	public void generateCentroids() {
		
		double newX = originX + (boxWidth/2);
		double newY = originY + (boxHeight/2);
		
		// TESTING
		int count = 0;
		
		for (int j = 0; j < boxRows; j++) {
			
			for (int i = 0; i < boxColumns; i++) {
			
				Point2D.Double newCentroid = new Point2D.Double(newX,newY);
			
				centroids.add(newCentroid);
				
				newX = newX + boxWidth;
			
				count++;
				
			}
		
			newY = newY + boxHeight;
			newX = originX + (boxWidth/2);
		
		}
		
		System.out.println("GENERATED CENTROIDS: " + centroids);
		
		// TESTING
		System.out.println("COUNT: " + count);
		
	}
	
	/**
	 * STEP 2
	 * Find the nearest centroid to a point, based on the box structure
	 * @param point
	 * @return nearestCentroid
	 */
	public Point2D.Double findNearestCentroid(Point2D.Double point) {
		
		Point2D.Double nearestCentroid = null;
		
		double pointX = point.getX();
		double pointY = point.getY();
		
		int column = (int) Math.ceil((pointX - originX) / boxWidth);
		int row = (int) Math.ceil((pointY - originY) / boxHeight);
		
		//System.out.println(column);
		//System.out.println(row);
		
		// To get correct index in array of centroids if not in the first row, add all boxes from rows below and additional boxes from columns left of point
		nearestCentroid = centroids.get(boxColumns * (row - 1) + (column - 1));

		// Will always return bottom left box if in 4 way intersection between boxes
		// This stops it going outside bounds if point added is at (maxX, maxY)
		return nearestCentroid;
		
	}
	
	/**
	 * STEP 2
	 * Perform clustering
	 * @return clusters
	 */
	public void createClusters(){
		
		ArrayList<ArrayList<Point2D.Double>> createdClusters = new ArrayList<ArrayList<Point2D.Double>>();
		
		Point2D.Double currentCentroid = null;

		
		// For current centroid, find all points with that as nearest centroid, add all these to arraylist, then add this arraylist to clusters
		for (int i = 0; i < centroids.size(); i++) {
			
			ArrayList<Point2D.Double> currentCluster = new ArrayList<>();
			
			currentCentroid = centroids.get(i);
			
			for (int j = 0; j < points.size(); j++) {
				
				if (findNearestCentroid(points.get(j)) == currentCentroid) {
					
					currentCluster.add(points.get(j));
				}	
			}
			
			if (!currentCluster.isEmpty()) {
				createdClusters.add(currentCluster);
			}
			
			//System.out.println("CURRENT CLUSTER: " + currentCluster);
			
		}
		
		clusters = createdClusters;
	
	}
	
	
	/**
	 * STEP 3
	 * Update centroids based on the created clusters
	 * Unused centroids are not needed as these have no memories and are therefore not POIs
	 */
	public void updateCentroids() {
		
		ArrayList<Point2D.Double> newCentroids = new ArrayList<Point2D.Double>();
		
		for (ArrayList<Point2D.Double> cluster : clusters) {
			
			double sumX = 0.0;
			double sumY = 0.0;
			int count = 0;
			
			for (Point2D.Double point : cluster) {
				
				sumX = sumX + point.getX();
				sumY = sumY + point.getY();
				
				count++;
				
			}
			
			if (count > 0) {
				
				Point2D.Double newCentroid = new Point2D.Double((sumX / count), (sumY / count));
				
				newCentroids.add(newCentroid);
				
			}
			

			
		}
		
		centroids = newCentroids;
		
	}
	
	
	
	/**
	 * For each centroid, print all points in that centroid's cluster
	 */
	public void printAllClusters() {
		
		for (int i = 0; i < clusters.size(); i++) {
			
			System.out.println("CLUSTER FOR CENTROID " + centroids.get(i) + ": " + clusters.get(i));

		}
		
		
	}
	
	
	
	

}
