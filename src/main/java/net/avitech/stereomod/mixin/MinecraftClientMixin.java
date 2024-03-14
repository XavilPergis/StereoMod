package net.avitech.stereomod.mixin;

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

import net.avitech.stereomod.Eye;
import net.avitech.stereomod.StereoConfig;
import net.avitech.stereomod.StereoRenderer;
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
	private void drawProfilerResults(DrawContext context, ProfileResult profileResult) {
		throw new AssertionError("mixin shadow method called");
	}

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

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V", shift = At.Shift.AFTER))
	private void renderGameTwice(boolean tick, CallbackInfo info) {
		final var mode = StereoConfig.INSTANCE.stereoModeOption.getValue();
		if (!mode.isEnabled())
			return;

		final var self = (MinecraftClient) (Object) this;
		final var partialTick = this.paused ? this.pausedTickDelta : this.renderTickCounter.tickDelta;

		// the vanilla call leaves the finished image in the main framebuffer, so we
		// copy it to one of the eye buffers for later.
		StereoRenderer.INSTANCE.finishRenderingEye(framebuffer);

		this.framebuffer.beginWrite(true);
		BackgroundRenderer.clearFog();
		RenderSystem.enableCull();
		StereoRenderer.INSTANCE.currentEye = Eye.LEFT;
		self.gameRenderer.render(partialTick, capturedMeasuringTime, tick);
		StereoRenderer.INSTANCE.finishRenderingEye(framebuffer);
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;draw(II)V"))
	private void blitRightEye(Framebuffer framebuffer, int width, int height, boolean tick) {
		final var mode = StereoConfig.INSTANCE.stereoModeOption.getValue();
		if (!mode.isEnabled()) {
			framebuffer.draw(width, height);
			return;
		}

		StereoRenderer.INSTANCE.blitEyes();
	}

}
