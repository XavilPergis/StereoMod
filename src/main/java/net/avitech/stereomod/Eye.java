package net.avitech.stereomod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum Eye {

	LEFT,
	RIGHT;

	public Eye opposite() {
		return this == LEFT ? RIGHT : LEFT;
	}

}
