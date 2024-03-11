package net.avitech.testbed.feature.anaglyph;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL32C;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;

import net.avitech.testbed.feature.anaglyph.mixin.CameraAccessorMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;

public final class StereoRenderer {

	public static final StereoRenderer INSTANCE = new StereoRenderer();

	private final MinecraftClient client = MinecraftClient.getInstance();
	public Eye currentEye = null;

	private Matrix4f witnessedProjection;
	private StereoCameraParams witnessedParams;

	public interface WorldRenderCallbacks {
		void renderWorld(Eye eye, RenderParams params);
	}

	public static final class RenderParams {
		public final MinecraftClient client;
		public final Profiler profiler;
		public final GameRenderer gameRenderer;
		public final float tickDelta;
		public final long limitTime;

		public RenderParams(float tickDelta, long limitTime) {
			this.client = MinecraftClient.getInstance();
			this.gameRenderer = this.client.gameRenderer;
			this.profiler = this.client.getProfiler();
			this.tickDelta = tickDelta;
			this.limitTime = limitTime;
		}
	}

	public void onRenderWorld(WorldRenderCallbacks callbacks, RenderParams params) {
		params.profiler.push("stereo_" + this.currentEye.toString().toLowerCase());
		// render the world to the main framebuffer
		callbacks.renderWorld(this.currentEye, params);
		params.profiler.pop();
	}

	public void offsetCamera(Eye eye, Camera camera, float tickDelta) {
		if (eye == null)
			return;

		float camOffsetX = (float) StereoConfig.INSTANCE.getEyeOffset(tickDelta);
		if (eye == Eye.LEFT)
			camOffsetX *= -1f;

		// // i don't know what's up with this method. X seems to control Z, and Z seems
		// to
		// // control X. And at least X seems to be reversed, with positive values
		// moving
		// // the camera to the *left*.
		// ((CameraAccessorMixin) camera).stereo_moveBy(0f, 0f, -camOffsetX);

		final var camPos = camera.getPos();
		final var rightDir = new Vec3d(camera.getDiagonalPlane());
		final var newPos = camPos.add(rightDir.multiply(-camOffsetX));
		((CameraAccessorMixin) camera).stereo_setPos(newPos.x, newPos.y, newPos.z);
	}

	public Matrix4f getProjectionMatrixForEye(Eye eye, double verticalFov) {
		final var params = new StereoCameraParams();

		final var window = this.client.getWindow();
		params.aspectRatio = (float) window.getFramebufferWidth() / (float) window.getFramebufferHeight();
		params.verticalFov = (float) verticalFov;
		params.planeNear = 0.05f;
		params.planeFar = this.client.gameRenderer.getFarPlaneDistance();

		// not sure of the best place to put these. They should be user-configurable.
		params.eyeDistance = (float) StereoConfig.INSTANCE.getEyeOffset();
		params.focalLength = (float) StereoConfig.INSTANCE.getFocalLength();

		final var projection = this.currentEye.getProjectionMatrixAsymmetric(params);
		this.witnessedProjection = projection;
		this.witnessedParams = params;
		return projection;
	}

	public void blitEyeToBackbuffer(Framebuffer framebuffer) {
		RenderSystem.assertOnRenderThread();

		final var client = MinecraftClient.getInstance();

		final var physWidth = WindowAccessor.getPhysicalWidth(client.getWindow());
		final var eyeWidth = client.getWindow().getFramebufferWidth();
		final var height = client.getWindow().getFramebufferHeight();

		float xl = 0, yl = 0;
		float xh = eyeWidth, yh = height;
		final var mode = StereoConfig.INSTANCE.stereoModeOption.getValue();
		if (mode.getOutputSide(this.currentEye) == Eye.RIGHT) {
			xl += eyeWidth;
			xh += eyeWidth;
		}

		final var shader = client.gameRenderer.blitScreenProgram;
		shader.addSampler("DiffuseSampler", framebuffer.getColorAttachment());

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
			final var enableRed = filters.red() == this.currentEye;
			final var enableGreen = filters.green() == this.currentEye;
			final var enableBlue = filters.blue() == this.currentEye;
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
		GlStateManager._blendEquation(GL32C.GL_FUNC_ADD);
		GlStateManager._blendFunc(GL32C.GL_SRC_ALPHA, GL32C.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager._disableBlend();
	}

	public static void copyFramebuffer(@NotNull Framebuffer src, @NotNull Framebuffer dst) {
		dst.beginWrite(true);
		src.draw(dst.textureWidth, dst.textureHeight, false);
		dst.endWrite();
	}

	@NotNull
	public static Matrix4f perspectiveProjectionMatrix(
			float left, float right,
			float bottom, float top,
			float near, float far) {

		final var mat = new Matrix4f();
		mat.m00((2f * near) / (right - left));
		mat.m11((2f * near) / (top - bottom));
		mat.m22(-(far + near) / (far - near));
		mat.m32(-1f);
		mat.m03(-near * (right + left) / (right - left));
		mat.m13(-near * (top + bottom) / (top - bottom));
		mat.m23(-(2f * far * near) / (far - near));
		mat.transpose();

		return mat;
	}

	public void setup() {
	}

	public double getCrosshairOffset() {
		if (this.witnessedProjection == null)
			return 0;
		if (this.client.crosshairTarget == null)
			return 0;
		// if (this.client.crosshairTarget == null ||
		// this.client.crosshairTarget.getType() == HitResult.Type.MISS)
		// return 0;

		final var hitPos = this.client.crosshairTarget.getPos();
		final var distance = hitPos.distanceTo(this.client.gameRenderer.getCamera().getPos());
		// final var distance = 5d;

		final var eyeOffset = this.currentEye == Eye.LEFT
				? StereoConfig.INSTANCE.getEyeOffset()
				: -StereoConfig.INSTANCE.getEyeOffset();

		final var mat = this.witnessedProjection;
		final var projected = mat.transformProject(new Vector3f((float) eyeOffset, 0f, (float) -distance));
		// final var x = mat.m00() * eyeOffset + mat.m02() * -distance;
		// final var w = mat.m30() * eyeOffset + mat.m32() * -distance + mat.m33();

		// final var n2 = eyeOffset * 0.05 / StereoConfig.INSTANCE.getFocalLength();
		// // final var n3 = 1 - n2;
		// final var n3 = n2;

		// 0 offset -> crosshair on focal plane

		// return -0.1 * 0.5 * this.client.getWindow().getScaledWidth() * x / w;
		// return 0.5 * this.client.getWindow().getScaledWidth() * x / w;
		return 0.5 * this.client.getWindow().getScaledWidth() * projected.x;
		// return 0.5 * this.client.getWindow().getScaledWidth() * n3;
		// return 0.5 * this.client.getWindow().getScaledWidth();
	}

}
