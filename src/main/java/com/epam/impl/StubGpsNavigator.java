package main.java.com.epam.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import main.java.com.epam.api.GpsNavigator;
import main.java.com.epam.api.Path;

/**
 * Implementation of GpsNavigator. Provides roads as directed graph which presented as HashMap.
 */
public class StubGpsNavigator implements GpsNavigator {
	/**
	 * Presenting directed graph of roads as HashMap,
	 * where key is a title of node and value is an ArrayList of available directions.
	 */
	private HashMap<String, ArrayList<RoadDirection>> roadMap = new HashMap<>();
    @Override
    public void readData(String filePath) {
    	FileInputStream fileInputStream = null;
    	try {
    		fileInputStream = new FileInputStream(filePath);
    		BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
    		String strLine;
    		while((strLine = br.readLine()) != null){
    			String[] values = strLine.split(" ");
    			RoadDirection direction = new RoadDirection(values[1], Integer.valueOf(values[2]),Integer.valueOf(values[3]));
    			if(!roadMap.containsKey(values[0])) {
        			ArrayList<RoadDirection> node = new ArrayList<>();
    				node.add(direction);
    				roadMap.put(values[0], node);
    			} else {
    				roadMap.get(values[0]).add(direction);
    			}
    			if(!roadMap.containsKey(values[1])) {
    				ArrayList<RoadDirection> node = new ArrayList<>();
    				roadMap.put(values[1], node);
    			}
    		}
    	}catch (FileNotFoundException e) {
    		e.printStackTrace();
    	}
    	catch (IOException e) {
    		System.out.println("Error readind data.");
    		e.printStackTrace();
    	}finally {
    		try {
				if(fileInputStream != null) fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    /**
     * Implementation of findPath(...). Uses Dijkstra's algorithm.
     */
    @Override
    public Path findPath(String pointA, String pointB) {
    	if(!roadMap.containsKey(pointA) || !roadMap.containsKey(pointB)) {
    		throw new IllegalArgumentException("No such node.");
    	}
    	HashMap<String, RoadPath> nodes = new HashMap<String, RoadPath>();
    	roadMap.forEach((k,v)-> {
    		if (k.equals(pointA)) {
    			ArrayList<String> path = new ArrayList<String>();
    			path.add(pointA);
    			nodes.put(k, new RoadPath(path, 0));
    			nodes.get(k).isOptimal = true;
    		}
    		else nodes.put(k, new RoadPath(new ArrayList<String>(), -1));
    	});
    	String activeNode = pointA;
    	while(!nodes.get(pointB).isOptimal) {
    		String pActiveNode = activeNode;
    		ArrayList<RoadDirection> node = roadMap.get(activeNode);
    		for(RoadDirection direction: node) {
    			if (nodes.get(direction.destiny).cost >= 0) {
    				if (nodes.get(direction.destiny).cost > nodes.get(activeNode).cost + direction.cost) {
    					nodes.get(direction.destiny).cost = nodes.get(activeNode).cost + direction.cost;
    					nodes.get(direction.destiny).path = (ArrayList<String>) nodes.get(pActiveNode).path.clone();
    					nodes.get(direction.destiny).path.add(direction.destiny);
    				}
    			}
    			else {
    				nodes.get(direction.destiny).cost = nodes.get(activeNode).cost + direction.cost;
    				nodes.get(direction.destiny).path = (ArrayList<String>) nodes.get(activeNode).path.clone();
    				nodes.get(direction.destiny).path.add(direction.destiny);
    			}
    		}
			int minValue = -1;
    		for(Map.Entry<String, RoadPath> entry : nodes.entrySet()) {
    		    String k = entry.getKey();
    		    RoadPath v = entry.getValue();
				if (!v.isOptimal && v.cost >= 0) {						
					if((minValue >= 0 && minValue > v.cost) || minValue < 0) {
							minValue = v.cost;
							activeNode = k;						
					}
				}
			};
			if(activeNode.equals(pActiveNode)) throw new IllegalArgumentException("No available path.");
			nodes.get(activeNode).isOptimal = true;
			
    	}
    	return new Path(nodes.get(activeNode).path, nodes.get(activeNode).cost);
    }
}
