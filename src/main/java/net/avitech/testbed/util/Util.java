package net.avitech.testbed.util;

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

}
