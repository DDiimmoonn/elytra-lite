package ru.ddiimmoonn.elytraspeed;

public class GlideState {
    public double baseSpeed;
    public double multiplier;
    public double dirX;
    public double dirZ;
    public double currentSpeed;

    public GlideState(double baseSpeed, double multiplier, double dirX, double dirZ, double currentSpeed) {
        this.baseSpeed = baseSpeed;
        this.multiplier = multiplier;
        this.dirX = dirX;
        this.dirZ = dirZ;
        this.currentSpeed = currentSpeed;
    }
}
