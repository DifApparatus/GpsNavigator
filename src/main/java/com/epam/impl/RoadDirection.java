package main.java.com.epam.impl;

/** 
 * Describes road: destiny, length and cost.
 */
public class RoadDirection<T> {

	public T destiny;
	public int length;
	public int cost;

	public RoadDirection(T direction, int length, int cost){
		this.destiny = direction;
		this.length = length;
		this.cost = cost;
	}
}
