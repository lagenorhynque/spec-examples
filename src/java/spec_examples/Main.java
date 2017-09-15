package spec_examples;

import java.util.Arrays;
import java.util.List;

import spec_examples.geometry.Cube;
import spec_examples.geometry.Cuboid;
import spec_examples.geometry.Shape;
import spec_examples.geometry.Sphere;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, Java!");

        List<Shape> shapes = Arrays.asList(
            new Sphere(3),
            new Cube(3),
            new Cuboid(3, 4, 5)
        );
        shapes.stream().forEach(Shape::show);
    }
}
