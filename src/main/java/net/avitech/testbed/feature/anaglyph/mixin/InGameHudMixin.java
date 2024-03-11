package net.avitech.testbed.feature.anaglyph.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.avitech.testbed.feature.anaglyph.StereoConfig;
import net.avitech.testbed.feature.anaglyph.StereoRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/gui/DrawContext;)V"))
	private void guardMatrixStackPre(DrawContext context, float tickDelta, CallbackInfo info) {
		context.getMatrices().push();
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/gui/DrawContext;)V", shift = At.Shift.AFTER))
	private void guardMatrixStackPost(DrawContext context, float tickDelta, CallbackInfo info) {
		context.getMatrices().pop();
	}

	@Inject(method = "renderCrosshair", at = @At("HEAD"))
	private void offsetCrosshair(DrawContext context, CallbackInfo info) {
		final var mode = StereoConfig.INSTANCE.stereoModeOption.getValue();
		if (!mode.isEnabled())
			return;
		final var offset = StereoRenderer.INSTANCE.getCrosshairOffset();
		context.getMatrices().translate(offset, 0, 0);
	}

}
