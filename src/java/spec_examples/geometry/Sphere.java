package spec_examples.geometry;

public class Sphere implements Shape {
    public final double radius;

    public Sphere(double radius) {
        this.radius = radius;
    }

    @Override
    public double surfaceArea() {
        return 4.0 * Math.PI * Math.pow(radius, 2);
    }

    @Override
    public double volume() {
        return 4.0 / 3.0 * Math.PI * Math.pow(radius, 3);
    }

    @Override
    public String toString() {
        return String.format("Sphere(%s)", radius);
    }
}
