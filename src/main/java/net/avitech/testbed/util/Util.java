package net.avitech.testbed.util;

import net.minecraft.util.math.MathHelper;

public final class Util {

    public static boolean approximatelyEqual(float a, float b) {
        return a - b < Math.ulp(1.0);
    }

    public static boolean approximatelyEqual(double a, double b) {
        return a - b < Math.ulp(1.0);
    }

    public static float asin(float n) {
        return (float) Math.asin(n);
    }

    public static float length(float a) {
        return a;
    }

    public static float length(float a, float b) {
        return MathHelper.sqrt(a * a + b * b);
    }

    public static float length(float a, float b, float c) {
        return MathHelper.sqrt(a * a + b * b + c * c);
    }

}
