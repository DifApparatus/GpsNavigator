package main.java.com.epam;

import main.java.com.epam.api.GpsNavigator;
import main.java.com.epam.api.Path;
import main.java.com.epam.impl.StubGpsNavigator;

/**
 * This class app demonstrates how your implementation of {@link com.epam.api.GpsNavigator} is intended to be used.
 */
public class ExampleApp {

	private static final String FILE_PATH = "src/RoadMap";
	private static final String pointA = "A";
	private static final String pointB = "Pizza";
	public static void main(String[] args) {
        final GpsNavigator navigator = new StubGpsNavigator();
        navigator.readData(FILE_PATH);

        final Path path = navigator.findPath(pointA, pointB);
        System.out.println(path);        
    }
}
