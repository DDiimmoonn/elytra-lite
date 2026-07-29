package ru.ddiimmoonn.elytraspeed;

public class GlideState {
    public double baseSpeed;
    public double multiplier;
    public double dirX;
    public double dirZ;

    public GlideState(double baseSpeed, double multiplier, double dirX, double dirZ) {
        this.baseSpeed = baseSpeed;
        this.multiplier = multiplier;
        this.dirX = dirX;
        this.dirZ = dirZ;
    }
}
