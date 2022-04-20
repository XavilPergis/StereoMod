package net.avitech.testbed.feature.anaglyph;

import java.util.Set;

import com.google.common.collect.Sets;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AnaglyphConfig {

    /**
     * Describes the properties of a physical anaglyph filter. Each color channel
     * can be let through at most one eye, or else the effect does not work.
     */
    public static record EyeFilters(Eye red, Eye green, Eye blue) {
        public static EyeFilters RED_CYAN = new EyeFilters(Eye.LEFT, Eye.RIGHT, Eye.RIGHT);
        public static EyeFilters GREEN_MAGENTA = new EyeFilters(Eye.RIGHT, Eye.LEFT, Eye.RIGHT);
        public static EyeFilters AMBER_BLUE = new EyeFilters(Eye.LEFT, Eye.LEFT, Eye.RIGHT);
        public static EyeFilters RED_BLUE = new EyeFilters(Eye.LEFT, null, Eye.RIGHT);
        public static EyeFilters GREEN_RED = new EyeFilters(Eye.RIGHT, Eye.LEFT, null);
    }

    public EyeFilters filters = EyeFilters.RED_CYAN;
    public double eyeDistance = 0.4;
    public double focalLength = 1d / 10d;

    public Set<Eye> enabledEyes = Sets.newHashSet(Eye.LEFT, Eye.RIGHT);
    public boolean anaglyphEnabled = false;

    private static AnaglyphConfig INSTANCE = new AnaglyphConfig();

    public static AnaglyphConfig getConfig() {
        return INSTANCE;
    }

    public boolean isEyeEnabled(Eye eye) {
        // return eye == Eye.LEFT;
        return this.enabledEyes.contains(eye);
    }

}
