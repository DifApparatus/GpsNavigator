package main.java.com.epam.impl;

import java.util.List;

/**
 * Describes path from the start to the destiny. Contains path, its cost and optimality.
 */
class RoadPath {

    /**
     * Contains titles of all nodes of the path.
     * First element is a beginning, last element is a destiny.
     */
    public List<String> path;
    /**
     * Describes if the path is optimal (has a minimal cost).
     */
    public boolean isOptimal = false;
    /**
     * Total cost of all nodes of the path.
     */
    public int cost;

    public RoadPath(List<String> path, int cost) {
        this.path = path;
        this.cost = cost;
    }
}
