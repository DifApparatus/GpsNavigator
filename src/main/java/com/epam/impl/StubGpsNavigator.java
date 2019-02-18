package main.java.com.epam.impl;

import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.com.epam.api.GpsNavigator;
import main.java.com.epam.api.Path;

public class StubGpsNavigator implements GpsNavigator {

    private static final int PARTS_OF_ONE_ROAD = 4;

    /**
     * Imaging directed graph of roads as HashMap,
     * where key is a title of node and value is an ArrayList of available directions.
     */
    private HashMap<String, List<RoadDirection>> roadMap = new HashMap<>();

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
                RoadDirection direction = new RoadDirection(endPoint, length, cost);
                if (roadMap.containsKey(startPoint)) {
                    roadMap.get(startPoint).add(direction);
                } else {
                    List<RoadDirection> node = new ArrayList<>();
                    node.add(direction);
                    roadMap.put(startPoint, node);
                }
                // adding end point
                if (!roadMap.containsKey(endPoint)) {
                    ArrayList<RoadDirection> node = new ArrayList<>();
                    roadMap.put(endPoint, node);
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
    public Path findPath(String pointA, String pointB) {
        if (!roadMap.containsKey(pointA) || !roadMap.containsKey(pointB)) {
            throw new IllegalArgumentException("No such node.");
        }

        HashMap<String, RoadPath> nodes = new HashMap<>();
        initPath(nodes, pointA);

        String activeNode = pointA;
        while (!nodes.get(pointB).isOptimal) {
            String prevActiveNode = activeNode;
            List<RoadDirection> node = roadMap.get(activeNode);

            for (RoadDirection direction : node) {
                RoadPath roadPathOfDestiny = nodes.get(direction.destiny);
                RoadPath roadPathOfActiveNode = nodes.get(activeNode);
                if (roadPathOfDestiny.cost >= 0) {
                    if (roadPathOfDestiny.cost > roadPathOfActiveNode.cost + direction.cost) {
                        roadPathOfDestiny.cost = roadPathOfActiveNode.cost + direction.cost;
                        roadPathOfDestiny.path = new ArrayList<>(nodes.get(prevActiveNode).path);
                        roadPathOfDestiny.path.add(direction.destiny);
                    }
                } else {
                    roadPathOfDestiny.cost = nodes.get(activeNode).cost + direction.cost;
                    roadPathOfDestiny.path = new ArrayList<>(nodes.get(activeNode).path);
                    roadPathOfDestiny.path.add(direction.destiny);
                }
            }

            int minValue = -1;
            for (Map.Entry<String, RoadPath> entry : nodes.entrySet()) {
                RoadPath v = entry.getValue();
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

        return new Path(nodes.get(activeNode).path, nodes.get(activeNode).cost);
    }

    private void initPath(HashMap<String, RoadPath> nodes, String startPoint) {
        roadMap.forEach((k, v) -> {
            if (k.equals(startPoint)) {
                List<String> path = new ArrayList<>();
                path.add(startPoint);
                RoadPath roadPath = new RoadPath(path, 0);
                roadPath.isOptimal = true;
                nodes.put(k, roadPath);
            } else {
                nodes.put(k, new RoadPath(new ArrayList<>(), -1));
            }
        });
    }
}
