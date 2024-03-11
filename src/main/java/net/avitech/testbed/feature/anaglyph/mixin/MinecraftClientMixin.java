package net.avitech.testbed.feature.anaglyph.mixin;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.avitech.testbed.feature.anaglyph.Eye;
import net.avitech.testbed.feature.anaglyph.StereoConfig;
import net.avitech.testbed.feature.anaglyph.StereoRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.thread.ReentrantThreadExecutor;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin extends ReentrantThreadExecutor<Runnable> implements WindowEventHandler {

	public MinecraftClientMixin(String string) {
		super(string);
		throw new IllegalStateException("mixin constructor called");
	}

	@Nullable
	@Shadow
	private ProfileResult tickProfilerResult;

	@Shadow
	private volatile boolean paused;
	@Shadow
	private float pausedTickDelta;
	@Shadow
	@Final
	private RenderTickCounter renderTickCounter;
	@Shadow
	@Final
	private Framebuffer framebuffer;
	@Shadow
	private Profiler profiler;
	@Shadow
	@Final
	private BufferBuilderStorage bufferBuilders;

	@Unique
	private static long capturedMeasuringTime;

	@Unique
	private StereoConfig.Mode lastMode;

	@Shadow
	abstract void drawProfilerResults(DrawContext context, ProfileResult profileResult);

	@Shadow
	public abstract void onResolutionChanged();

	@Inject(method = "render", at = @At("HEAD"))
	private void captureMeasuringTime(CallbackInfo info) {
		// idk why im bothering, its unused
		capturedMeasuringTime = Util.getMeasuringTimeNano();

		final var mode = StereoConfig.INSTANCE.stereoModeOption.getValue();
		if (lastMode == null || mode.isSideBySide() != this.lastMode.isSideBySide()) {
			this.lastMode = mode;
			onResolutionChanged();
		}
	}

	// cursed cursed cursed cursed cursed
	@Inject(method = "render", at = @At("HEAD"))
	private void setup(boolean tick, CallbackInfo info) {
		StereoRenderer.INSTANCE.setup();
		StereoRenderer.INSTANCE.currentEye = Eye.RIGHT;
	}

	// @Inject(method = "render", at = @At(value = "INVOKE_STRING", target =
	// "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args =
	// "ldc=updateDisplay"))
	// private void renderLeftEye(boolean tick, CallbackInfo info) {
	// final var config = StereoRenderer.INSTANCE.getConfig();
	// if (!config.mode.isEnabled())
	// return;

	// StereoRenderer.INSTANCE.currentEye = Eye.LEFT;

	// final var self = (MinecraftClient) (Object) this;
	// final var framebuffer = self.getFramebuffer();
	// final var profiler = self.getProfiler();
	// final var toastManager = self.getToastManager();

	// MatrixStack matrixStack = RenderSystem.getModelViewStack();
	// matrixStack.push();
	// RenderSystem.applyModelViewMatrix();
	// framebuffer.beginWrite(true);
	// BackgroundRenderer.clearFog();
	// profiler.push("display");
	// RenderSystem.enableCull();
	// profiler.pop();
	// if (!self.skipGameRender) {
	// profiler.swap("gameRenderer");
	// self.gameRenderer.render(this.paused ? this.pausedTickDelta :
	// this.renderTickCounter.tickDelta,
	// capturedMeasuringTime, tick);
	// profiler.swap("toasts");
	// toastManager.draw(new MatrixStack());
	// profiler.pop();
	// }
	// if (this.tickProfilerResult != null) {
	// profiler.push("fpsPie");
	// this.drawProfilerResults(new MatrixStack(), this.tickProfilerResult);
	// profiler.pop();
	// }
	// profiler.push("blit");
	// framebuffer.endWrite();
	// matrixStack.pop();
	// matrixStack.push();
	// RenderSystem.applyModelViewMatrix();
	// StereoRenderer.INSTANCE.blitEyeToBackbuffer(framebuffer);
	// matrixStack.pop();
	// RenderSystem.applyModelViewMatrix();
	// }

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;draw(II)V"))
	private void blitRightEye(Framebuffer framebuffer, int width, int height, boolean tick) {
		final var mode = StereoConfig.INSTANCE.stereoModeOption.getValue();
		if (!mode.isEnabled()) {
			framebuffer.draw(width, height);
			return;
		} else {
			StereoRenderer.INSTANCE.blitEyeToBackbuffer(framebuffer);
		}

		this.profiler.pop();
		StereoRenderer.INSTANCE.currentEye = Eye.LEFT;

		final var self = (MinecraftClient) (Object) this;

		this.framebuffer.beginWrite(true);
		BackgroundRenderer.clearFog();
		this.profiler.push("display");
		RenderSystem.enableCull();
		this.profiler.pop();
		if (!self.skipGameRender) {
			this.profiler.swap("gameRenderer");
			self.gameRenderer.render(this.paused ? this.pausedTickDelta : this.renderTickCounter.tickDelta,
					this.capturedMeasuringTime, tick);
			this.profiler.pop();
		}

		if (this.tickProfilerResult != null) {
			this.profiler.push("fpsPie");
			final var drawContext = new DrawContext(self, this.bufferBuilders.getEntityVertexConsumers());
			drawProfilerResults(drawContext, this.tickProfilerResult);
			drawContext.draw();
			this.profiler.pop();
		}

		this.profiler.push("blit");
		this.framebuffer.endWrite();
		StereoRenderer.INSTANCE.blitEyeToBackbuffer(framebuffer);
	}

}
