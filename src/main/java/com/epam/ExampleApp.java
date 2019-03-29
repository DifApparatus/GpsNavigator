package main.java.com.epam;

import main.java.com.epam.api.GpsNavigator;
import main.java.com.epam.api.Path;
import main.java.com.epam.impl.StubGpsNavigator;

/**
 * This class app demonstrates how your implementation of {@link GpsNavigator} is intended to be used.
 */
public class ExampleApp {

    private static final String FILE_PATH = "src/RoadMap";
    private static final String START_POINT = "A";
    private static final String END_POINT = "Pizza";

    public static void main(String[] args) {
        final GpsNavigator navigator = new StubGpsNavigator<>((obj)->obj.cost*obj.length);
        navigator.readData(FILE_PATH);

        try {
            final Path path = navigator.findPath(START_POINT, END_POINT);
            System.out.println(path);
        } catch (IllegalArgumentException e) {
            System.out.printf("Path: %s %s; Exception with finding path.", START_POINT, END_POINT);
        }
    }
}
