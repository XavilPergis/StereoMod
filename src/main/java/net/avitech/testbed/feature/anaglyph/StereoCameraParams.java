package net.avitech.testbed.feature.anaglyph;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class StereoCameraParams {
    public float aspectRatio;
    public float verticalFov;
    public float planeNear;
    public float planeFar;
    public float eyeDistance;
    public float focalLength;
}
