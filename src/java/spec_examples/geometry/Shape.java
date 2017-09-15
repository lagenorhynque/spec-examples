package spec_examples.geometry;

public interface Shape {
    double surfaceArea();
    double volume();

    default void show() {
        System.out.println(this);
        System.out.println("  surface area: " + surfaceArea());
        System.out.println("  volume: " + volume());
    }
}
