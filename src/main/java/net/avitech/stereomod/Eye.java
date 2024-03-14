package net.avitech.stereomod;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum Eye {
	LEFT,
	RIGHT;

	public Eye opposite() {
		return this == LEFT ? RIGHT : LEFT;
	}

	// http://paulbourke.net/stereographics/stereorender/
	public @NotNull Matrix4f getProjectionMatrixAsymmetric(@NotNull StereoCameraParams params) {
		float nearHalfHeight = params.planeNear * (float) Math.tan(Math.toRadians(params.verticalFov) / 2.0);

		float planeTop = nearHalfHeight;
		float planeRight = planeTop * params.aspectRatio;
		float planeBottom = planeTop;
		float planeLeft = planeRight;

		// this offset can be calculated by considering the rectangle that touches each
		// plane of a center perspective frustum (but is parallel to the near and far
		// plane). If you then construct a frustum by offseting the position of the
		// camera vertex by the eye distance and fixing that rectangle in place.
		final var offset = params.eyeDistance * params.planeNear / params.focalLength;

		// each eye is symmetric (left plane of left eye is the same as the right plane
		// of the right eye and vyse vyrsa ;p)
		if (this == LEFT) {
			planeLeft -= offset;
			planeRight += offset;
		} else {
			planeLeft += offset;
			planeRight -= offset;
		}

		return new Matrix4f().frustum(
				-planeLeft, planeRight,
				-planeBottom, planeTop,
				params.planeNear, params.planeFar);
	}

}
