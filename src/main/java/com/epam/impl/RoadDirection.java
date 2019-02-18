package main.java.com.epam.impl;

/** 
 * Describes road: destiny, length and cost.
 */
class RoadDirection {

	public String destiny;
	public int length;
	public int cost;

	public RoadDirection(String direction, int length, int cost){
		this.destiny = direction;
		this.length = length;
		this.cost = cost;
	}
}
