package net.avitech.testbed.feature.anaglyph;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Window;

@Environment(EnvType.CLIENT)
public interface WindowAccessor {

	int testbed_getPhysicalWidth();

	static int getPhysicalWidth(Window window) {
		return ((WindowAccessor) (Object) window).testbed_getPhysicalWidth();
	}
	
}
