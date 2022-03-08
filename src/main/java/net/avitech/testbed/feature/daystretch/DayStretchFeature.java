package net.avitech.testbed.feature.daystretch;

public class DayStretchFeature {

    // public static enum StretchyMode {
    // SPEEDUP,
    // SLOWDOWN;
    // }

    // public static record StretchFactor(StretchyMode mode, int factor) {
    // }

    // public static StretchFactor getStretchFactor(long timeOfDay) {
    // return new StretchFactor(StretchyMode.SLOWDOWN, 2);
    // }

    // public static long getTimeAdvance(long timeOfDay, int ticksSinceLastUpdate) {
    // StretchFactor factor = getStretchFactor(timeOfDay);
    // if (factor.mode() == StretchyMode.SPEEDUP) {
    // return (long) factor.factor();
    // } else if (factor.mode() == StretchyMode.SLOWDOWN) {
    // return ticksSinceLastUpdate >= factor.factor() ? 1L : 0L;
    // } else {
    // return 0L;
    // }
    // }

}
