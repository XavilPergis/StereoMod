package net.avitech.stereomod;

import java.util.EnumMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL32C;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;

import net.avitech.stereomod.mixin.CameraAccessorMixin;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import virtuoel.pehkui.api.ScaleTypes;

public final class StereoRenderer {

	public static final StereoRenderer INSTANCE = new StereoRenderer();

	private final MinecraftClient client = MinecraftClient.getInstance();
	public Eye currentEye = null;

	private Matrix4f witnessedProjection;
	private final Map<Eye, Framebuffer> eyeBuffers = new EnumMap<>(Eye.class);

	public Framebuffer getEyeBuffer(Eye eye) {
		final var winWidth = this.client.getWindow().getFramebufferWidth();
		final var winHeight = this.client.getWindow().getFramebufferHeight();

		Framebuffer buffer = this.eyeBuffers.get(eye);
		if (buffer == null) {
			buffer = new SimpleFramebuffer(winWidth, winHeight, false, false);
			this.eyeBuffers.put(eye, buffer);
		} else if (buffer.textureWidth != winWidth || buffer.textureHeight != winHeight) {
			if (buffer != null)
				buffer.delete();
			buffer = new SimpleFramebuffer(winWidth, winHeight, false, false);
			this.eyeBuffers.put(eye, buffer);
		}

		return buffer;
	}

	public void offsetCamera(Eye eye, Camera camera, float tickDelta) {
		if (eye == null)
			return;

		float camOffsetX = (float) StereoConfig.INSTANCE.getEyeOffset(tickDelta);
		if (eye == Eye.LEFT)
			camOffsetX *= -1f;

		// i don't know what's up with this method. X seems to control Z, and Z seems to
		// control X. And at least X seems to be reversed, with positive values moving
		// the camera to the *left*.
		((CameraAccessorMixin) camera).stereo_moveBy(0f, 0f, -camOffsetX);
	}

	public Matrix4f getProjectionMatrixForEye(Eye eye, double verticalFovDeg) {
		final var window = this.client.getWindow();
		final var aspectRatio = window.getFramebufferWidth() / (float) window.getFramebufferHeight();

		float planeNear = 0.05f;
		final var planeFar = this.client.gameRenderer.getFarPlaneDistance();

		// since we're overriding the projection matrix, Pehkui's modifincations to the
		// near clipping plane are discarded, so we apply them here instead. This
		// prevents the player view clipping through blocks when very small.
		if (FabricLoader.getInstance().isModLoaded("pehkui")) {
			final var camEntity = this.client.cameraEntity != null ? this.client.cameraEntity : this.client.player;
			final var scale = ScaleTypes.WIDTH.getScaleData(camEntity).getScale(this.client.getTickDelta());
			planeNear *= Math.min(scale, 1);
		}

		// http://paulbourke.net/stereographics/stereorender/
		final var nearHalfHeight = planeNear * (float) Math.tan(Math.toRadians(verticalFovDeg) / 2.0);

		float planeTop = nearHalfHeight, planeBottom = planeTop;
		float planeRight = planeTop * aspectRatio, planeLeft = planeRight;

		// this offset can be calculated by considering the rectangle that touches each
		// plane of a center perspective frustum (but is parallel to the near and far
		// plane). If you then construct a frustum by offseting the position of the
		// camera vertex by the eye distance and fixing that rectangle in place.
		final var offset = planeNear * StereoConfig.INSTANCE.getEyeOffset() / StereoConfig.INSTANCE.getFocalLength();

		// each eye is symmetric (left plane of left eye is the same as the right plane
		// of the right eye and vyse vyrsa ;p)
		if (eye == Eye.LEFT) {
			planeLeft -= offset;
			planeRight += offset;
		} else {
			planeLeft += offset;
			planeRight -= offset;
		}

		// haha yes i love funky ass code like this
		return this.witnessedProjection = new Matrix4f().frustum(
				-planeLeft, planeRight,
				-planeBottom, planeTop,
				planeNear, planeFar);
	}

	public void blitEyes() {
		GlStateManager._glBindFramebuffer(GL32C.GL_FRAMEBUFFER, 0);
		RenderSystem.clearColor(0, 0, 0, 0);
		RenderSystem.clear(GL32C.GL_COLOR_BUFFER_BIT, false);
		blitEye(Eye.LEFT, getEyeBuffer(Eye.LEFT));
		blitEye(Eye.RIGHT, getEyeBuffer(Eye.RIGHT));
	}

	public void blitEye(Eye eye, Framebuffer srcFramebuffer) {
		RenderSystem.assertOnRenderThread();

		final var client = MinecraftClient.getInstance();

		final var physWidth = WindowAccessor.getPhysicalWidth(client.getWindow());
		final var eyeWidth = client.getWindow().getFramebufferWidth();
		final var height = client.getWindow().getFramebufferHeight();

		float xl = 0, yl = 0;
		float xh = eyeWidth, yh = height;
		final var mode = StereoConfig.INSTANCE.stereoModeOption.getValue();
		if (mode.getOutputSide(eye) == Eye.RIGHT) {
			xl += eyeWidth;
			xh += eyeWidth;
		}

		final var shader = client.gameRenderer.blitScreenProgram;
		shader.addSampler("DiffuseSampler", srcFramebuffer.getColorAttachment());

		final var projectionMatrix = new Matrix4f().setOrtho(0f, physWidth, height, 0f, 1000f, 3000f);
		RenderSystem.setProjectionMatrix(projectionMatrix, VertexSorter.BY_Z);
		if (shader.modelViewMat != null)
			shader.modelViewMat.set(new Matrix4f().setTranslation(0.0f, 0.0f, -2000.0f));
		if (shader.projectionMat != null)
			shader.projectionMat.set(projectionMatrix);
		shader.bind();

		// setup color mask
		if (!mode.isSideBySide()) {
			final var filters = StereoConfig.INSTANCE.getFilters();
			final var enableRed = filters.red() == null || filters.red() == eye;
			final var enableGreen = filters.green() == null || filters.green() == eye;
			final var enableBlue = filters.blue() == null || filters.blue() == eye;
			GlStateManager._colorMask(enableRed, enableGreen, enableBlue, false);
		} else {
			GlStateManager._colorMask(true, true, true, false);
		}

		GlStateManager._enableBlend();
		GlStateManager._blendEquation(GL32C.GL_FUNC_ADD);
		GlStateManager._blendFunc(GL32C.GL_ONE, GL32C.GL_ONE);
		GlStateManager._disableDepthTest();
		GlStateManager._depthMask(false);
		GlStateManager._viewport(0, 0, physWidth, height);

		final var builder = RenderSystem.renderThreadTesselator().getBuffer();
		builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		builder.vertex(xl, yh, 0f).texture(0, 0).color(255, 255, 255, 255).next();
		builder.vertex(xh, yh, 0f).texture(1, 0).color(255, 255, 255, 255).next();
		builder.vertex(xh, yl, 0f).texture(1, 1).color(255, 255, 255, 255).next();
		builder.vertex(xl, yl, 0f).texture(0, 1).color(255, 255, 255, 255).next();
		BufferRenderer.draw(builder.end());

		shader.unbind();
		GlStateManager._depthMask(true);
		GlStateManager._colorMask(true, true, true, true);
		RenderSystem.defaultBlendFunc();
		GlStateManager._disableBlend();
	}

	public void finishRenderingEye(Framebuffer framebuffer) {
		// copy the contents of the given framebuffer (which will probably be the main
		// framebuffer :P) into the eye buffer for the current eye. It would be more
		// ideal to just render directly into the eye buffer in the first place instead
		// of doing this copy, but that would be a *much* more involved change.
		// Especially since the main framebuffer field of the client is final.
		final var eyeBuffer = getEyeBuffer(this.currentEye);
		GlStateManager._glBindFramebuffer(GL32C.GL_READ_FRAMEBUFFER, framebuffer.fbo);
		GlStateManager._glBindFramebuffer(GL32C.GL_DRAW_FRAMEBUFFER, eyeBuffer.fbo);
		GL32C.glBlitFramebuffer(
				0, 0, framebuffer.textureWidth, framebuffer.textureHeight,
				0, 0, eyeBuffer.textureWidth, eyeBuffer.textureHeight,
				// the eye buffers only have color attachments
				GL32C.GL_COLOR_BUFFER_BIT,
				// src and dst sizes should be the same so we just do the fastest option.
				GL32C.GL_NEAREST);
		framebuffer.clear(false);
	}

	public double getCrosshairOffset() {
		if (this.witnessedProjection == null || this.client.crosshairTarget == null)
			return 0;

		// we can use distanceTo to find the depth of the hit ray since its x and y
		// components will be 0 when interpreted in view space.
		final var hitPos = this.client.crosshairTarget.getPos();
		final var distance = hitPos.distanceTo(this.client.gameRenderer.getCamera().getPos());

		final var eyeOffset = StereoConfig.INSTANCE.getEyeOffset() * (this.currentEye == Eye.LEFT ? 1 : -1);
		// the eye offset seems reversed here because its the position of the hit pos in
		// view space. so for the left eye, (-X) the hit pos is actually to the right
		// (+X)
		final var posView = new Vector3f((float) eyeOffset, 0f, (float) -distance);

		// the space the crosshair is in has (0,0) at the origin and is half the screen
		// size in all 4 directions.
		return 0.5 * this.client.getWindow().getScaledWidth()
				* this.witnessedProjection.transformProject(posView).x;
	}

}
