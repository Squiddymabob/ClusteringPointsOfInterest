/**
 * 
 */
package com.ew00162.clustering;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Emily
 *
 */
public class ReadCSV {
	
	private String csvFile = "data.csv";
	private BufferedReader br = null;
	private String line = "";
	private String cvsSplitBy = ",";

	/**
	 * 
	 * @return
	 */
    public Map<String, Point2D.Double> readCSV() {
    	
        ArrayList<String> imageNames = new ArrayList<String>();
        ArrayList<Point2D.Double> locationPoints = new ArrayList<Point2D.Double>();
    	
        try {

            br = new BufferedReader(new InputStreamReader( new FileInputStream(csvFile), "UTF-8"));
            
            while ((line = br.readLine()) != null) {

                // Use comma as separator
                String[] data = line.split(cvsSplitBy);

                // Add data to array
                imageNames.add(data[0]);
                
                double lat = Double.valueOf(data[1]);
                double lng = Double.valueOf(data[2]);
                
                Point2D.Double newPoint = new Point2D.Double(lat, lng);
                
                locationPoints.add(newPoint);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        // Create the HashMap to return
    	// Images need to have associated GPS locations, these are the memories to cluster
    	Map<String, Point2D.Double> memories = new HashMap<String, Point2D.Double>();
    	
    	for (int i = 0; i < imageNames.size(); i++) {
    		
    		memories.put(imageNames.get(i), locationPoints.get(i));
    		
    	}
        
		return memories;
    	
    }
    


}
