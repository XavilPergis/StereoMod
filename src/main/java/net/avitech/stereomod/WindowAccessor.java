package net.avitech.stereomod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Window;

@Environment(EnvType.CLIENT)
public interface WindowAccessor {

	int stereo_getPhysicalWidth();

	static int getPhysicalWidth(Window window) {
		return ((WindowAccessor) (Object) window).stereo_getPhysicalWidth();
	}
	
}
