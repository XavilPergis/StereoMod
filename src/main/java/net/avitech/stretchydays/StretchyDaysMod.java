package net.avitech.stretchydays;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StretchyDaysMod implements ModInitializer {
	public static final String MODID = "stretchydays";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static enum StretchyMode {
		SPEEDUP,
		SLOWDOWN;
	}

	public static record StretchFactor(StretchyMode mode, int factor) {
	}

	public static StretchFactor getStretchFactor(long timeOfDay) {
		return new StretchFactor(StretchyMode.SLOWDOWN, 2);
	}

	public static long getTimeAdvance(long timeOfDay, int ticksSinceLastUpdate) {
		StretchFactor factor = getStretchFactor(timeOfDay);
		if (factor.mode() == StretchyMode.SPEEDUP) {
			return (long) factor.factor();
		} else if (factor.mode() == StretchyMode.SLOWDOWN) {
			return ticksSinceLastUpdate >= factor.factor() ? 1L : 0L;
		} else {
			return 0L;
		}
	}

	@Override
	public void onInitialize() {
	}
}
