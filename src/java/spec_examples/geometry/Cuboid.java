package spec_examples.geometry;

public class Cuboid implements Shape {
    public final double sideA;
    public final double sideB;
    public final double sideC;

    public Cuboid(double sideA, double sideB, double sideC) {
        this.sideA = sideA;
        this.sideB = sideB;
        this.sideC = sideC;
    }

    @Override
    public double surfaceArea() {
        return 2 * (sideA * sideB + sideB * sideC + sideC * sideA);
    }

    @Override
    public double volume() {
        return sideA * sideB * sideC;
    }

    @Override
    public String toString() {
        return String.format("Cuboid(%s, %s, %s)", sideA, sideB, sideC);
    }
}
