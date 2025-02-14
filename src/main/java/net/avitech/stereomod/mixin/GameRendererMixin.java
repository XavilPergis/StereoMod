package net.avitech.stereomod.mixin;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.avitech.stereomod.Eye;
import net.avitech.stereomod.StereoConfig;
import net.avitech.stereomod.StereoRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;

@Mixin(GameRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class GameRendererMixin {

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	public abstract Matrix4f getBasicProjectionMatrix(double fov);

	@Shadow
	public abstract void renderWorld(float tickDelta, long limitTime, MatrixStack matrices);

	@Unique
	private Eye currentEye = null;

	/**
	 * This is a redirect in {@link GameRenderer#renderWorld} instead of an inject
	 * in {@link GameRenderer#getBasicProjectionMatrix} because Minecraft's frustum
	 * code freaks out and gets stuck in an infinite loop when you give it an
	 * asymmetric frustum. There are two calls to
	 * {@link GameRenderer#getBasicProjectionMatrix} in
	 * {@link GameRenderer#renderWorld}, the second one is what's used to update the
	 * frustum, so we dont touch it.
	 * 
	 * This might actually cause problems though! If the view frustums for each eye
	 * differ enough from the vanilla frustum used for chunk culling, then chunks
	 * that we *can* actually see might be culled. We'll have to see if this is an
	 * actual issue or not.
	 */
	@Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;getBasicProjectionMatrix(D)Lorg/joml/Matrix4f;", ordinal = 0))
	private Matrix4f handleWorldProjectionMatrix(GameRenderer renderer, double verticalFov) {
		// just forward to vanilla if stereo rendering is not enabled.
		if (this.currentEye == null) {
			return getBasicProjectionMatrix(verticalFov);
		}

		return StereoRenderer.INSTANCE.getProjectionMatrixForEye(this.currentEye, verticalFov);
	}

	// FIXME: when a camera's position cross an 8x8 boundary, built chunks will be
	// discarded if out of view, and new chunks in view will be meshed if in view.
	// When the eye positions straddle this boundary, chunks are constantly built
	// and discarded, causing significant slowdowns and flickering in the distance.
	//
	// it would probably be best just to use the center of the two eyes as the
	// singular position to load around.

	@Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V"))
	private void setCameraPositionToCurrentEye(Camera camera, BlockView area, Entity focusedEntity, boolean thirdPerson,
			boolean inverseView, float tickDelta) {

		camera.update(area, focusedEntity, thirdPerson, inverseView, tickDelta);
		StereoRenderer.INSTANCE.offsetCamera(this.currentEye, camera, tickDelta);
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V"))
	private void renderWorldMaybeStereo(GameRenderer renderer, float tickDelta, long limitTime, MatrixStack matrices) {
		final var mode = StereoConfig.INSTANCE.stereoModeOption.getValue();
		if (!mode.isEnabled()) {
			// just forward the call as usual if stereo rendering is disabled.
			renderWorld(tickDelta, limitTime, matrices);
		} else {
			this.currentEye = StereoRenderer.INSTANCE.currentEye;
			renderWorld(tickDelta, limitTime, matrices);
			this.currentEye = null;
		}
	}

}
