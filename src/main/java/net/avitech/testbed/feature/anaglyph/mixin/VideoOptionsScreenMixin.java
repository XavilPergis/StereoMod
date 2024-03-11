package net.avitech.testbed.feature.anaglyph.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.avitech.testbed.feature.anaglyph.StereoConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

@Mixin(VideoOptionsScreen.class)
public abstract class VideoOptionsScreenMixin extends GameOptionsScreen {

	public VideoOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
		super(parent, gameOptions, title);
		throw new AssertionError("mixin constructor called");
	}


	// @Inject(method = "getOptions", at = @At(value = "RETURN"), cancellable = true)
	// private void addStereoOptions(GameOptions gameOptions, CallbackInfoReturnable<SimpleOption<?>[]> info) {
	// 	info.getReturnValue();
	// }

	// TODO: sodium compat

	@Shadow
	private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
		throw new AssertionError("mixin shadow method called");
	}

	@Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/option/VideoOptionsScreen;getOptions(Lnet/minecraft/client/option/GameOptions;)[Lnet/minecraft/client/option/SimpleOption;"))
	private SimpleOption<?>[] appendStereoOptions(GameOptions gameOptions) {
		final var vanillaOptions = getOptions(gameOptions);
		final var moddedOptions = StereoConfig.getOptions();
		final var res = new SimpleOption<?>[vanillaOptions.length + moddedOptions.length];
		System.arraycopy(vanillaOptions, 0, res, 0, vanillaOptions.length);
		System.arraycopy(moddedOptions, 0, res, vanillaOptions.length, moddedOptions.length);
		return res;
	}

}
