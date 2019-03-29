package main.java.com.epam.impl;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import main.java.com.epam.api.GpsNavigator;
import main.java.com.epam.api.Path;
//T - node
public class StubGpsNavigator<T, V extends RoadDirection<T>> implements GpsNavigator<T> {

    private static final int PARTS_OF_ONE_ROAD = 4;

    /**
     * Imaging directed graph of roads as HashMap,
     * where key is a title of node and value is an ArrayList of available directions.
     */
    private HashMap<T, List<V>> roadMap = new HashMap<>();
   CostCounterable<V> costCounter;
    public StubGpsNavigator(CostCounterable<V> counter) {
    	costCounter = counter;
    }
    /**
     * Well, this one probably should be generalized
     */
    @Override
    public void readData(String filePath) {
        File inputFile = new File(filePath);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(inputFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                String[] partsOfOneRoad = strLine.split(" ");
                if (partsOfOneRoad.length != PARTS_OF_ONE_ROAD) {
                    throw new IllegalArgumentException();
                }
                String startPoint = partsOfOneRoad[0];
                String endPoint = partsOfOneRoad[1];
                int length = Integer.parseInt(partsOfOneRoad[2]);
                int cost = Integer.parseInt(partsOfOneRoad[3]);

                // adding start point and forward direction
                RoadDirection<T> direction = new RoadDirection(endPoint, length, cost);
                if (roadMap.containsKey(startPoint)) {
                    roadMap.get(startPoint).add( (V) direction);
                } else {
                    List<RoadDirection<T>> node = new ArrayList<>();
                    node.add(direction);
                    roadMap.put((T) startPoint, (List<V>) node);
                }
                // adding end point
                if (!roadMap.containsKey(endPoint)) {
                    List<RoadDirection<T>> node = new ArrayList<>();
                    roadMap.put((T) endPoint, (List<V>) node);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("Error reading data.");
        } catch (IllegalArgumentException e) {
            // The exception handle all exceptions according with incorrect data
            System.out.println("Incorrect data at RoadMap.");
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                System.out.println("Error with closing input stream.");
            }
        }
    }

    /**
     * Implementation of findPath(...). Uses Dijkstra's algorithm.
     */
    @Override
    public Path findPath(T pointA, T pointB) {
        if (!roadMap.containsKey(pointA) || !roadMap.containsKey(pointB)) {
            throw new IllegalArgumentException("No such node.");
        }
        
        HashMap<T, RoadPath<T>>nodes = new HashMap<>();
        initPath(nodes, pointA);

        T activeNode = pointA;
        while (!nodes.get(pointB).isOptimal) {
            T prevActiveNode = activeNode;
            List<V> node =  roadMap.get(activeNode);

            for (V direction : node) {
                RoadPath<T> roadPathOfDestiny = nodes.get(direction.destiny);
                RoadPath<T> roadPathOfActiveNode = nodes.get(activeNode);
                if (roadPathOfDestiny.cost >= 0) {
                    if (roadPathOfDestiny.cost > roadPathOfActiveNode.cost + costCounter.getCost(direction)) {
                        roadPathOfDestiny.cost = roadPathOfActiveNode.cost + costCounter.getCost(direction);
                        roadPathOfDestiny.path = new ArrayList<>(nodes.get(prevActiveNode).path);
                        roadPathOfDestiny.path.add((T) direction.destiny);
                    }
                } else {
                    roadPathOfDestiny.cost = nodes.get(activeNode).cost + costCounter.getCost(direction);
                    roadPathOfDestiny.path = new ArrayList<>(nodes.get(activeNode).path);
                    roadPathOfDestiny.path.add((T) direction.destiny);
                }
            }

            int minValue = -1;
            for (Entry<T, RoadPath<T>> entry : nodes.entrySet()) {
                RoadPath<T> v = entry.getValue();
                if (!v.isOptimal && v.cost >= 0) {
                    if (minValue < 0 || minValue > v.cost) {
                        minValue = v.cost;
                        activeNode = entry.getKey();
                    }
                }
            }
            if (activeNode.equals(prevActiveNode)) {
                throw new IllegalArgumentException("No available path.");
            }
            nodes.get(activeNode).isOptimal = true;
        }
        //This return should be changed
        return new Path((List<String>) nodes.get(activeNode).path, nodes.get(activeNode).cost);
    }

    private void initPath(HashMap<T, RoadPath<T>> nodes, T startPoint) {
        roadMap.forEach((k, v) -> {
            if (k.equals(startPoint)) {
                List<T> path = new ArrayList<>();
                path.add(startPoint);
                RoadPath<T> roadPath = new RoadPath<T>(path, 0);
                roadPath.isOptimal = true;
                nodes.put( (T) k, roadPath);
            } else {
                nodes.put( (T) k, new RoadPath<T>(new ArrayList<>(), -1));
            }
        });
    }
}
