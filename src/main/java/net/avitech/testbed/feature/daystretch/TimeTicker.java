package net.avitech.testbed.feature.daystretch;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TimeTicker {
    private final World world;
    private double remainder = 0.0;
    private double speedFactor = 2.33;

    public TimeTicker(World world) {
        this.world = world;
    }

    public long tick() {
        remainder += getSpeedFactor();
        long timeToAdvance = (long) MathHelper.floor(remainder);
        remainder -= (double) timeToAdvance;
        return world.getTimeOfDay() + timeToAdvance;
    }

    public double getSpeedFactor() {
        return 0.8;
    }

    public double getRemainder() {
        return remainder;
    }
}
