package net.avitech.stereomod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.avitech.stereomod.StereoConfig;
import net.avitech.stereomod.WindowAccessor;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.MathHelper;

@Mixin(Window.class)
public abstract class WindowMixin implements AutoCloseable, WindowAccessor {

	private int width;
	private int framebufferWidth;
	private int scaledWidth;

	@Override
	public int stereo_getPhysicalWidth() {
		return this.framebufferWidth;
	}

	/**
	 * Overwritten because this value is used pervasively and I'd like to avoid an
	 * allocation here.
	 */
	@Overwrite
	public int getFramebufferWidth() {
		final var mode = StereoConfig.INSTANCE.stereoModeOption.getValue();
		return mode.isSideBySide() ? MathHelper.ceilDiv(this.framebufferWidth, 2) : this.framebufferWidth;
	}

	/**
	 * Overwritten because this value is used pervasively and I'd like to avoid an
	 * allocation here.
	 */
	@Overwrite
	public int getWidth() {
		final var mode = StereoConfig.INSTANCE.stereoModeOption.getValue();
		return mode.isSideBySide() ? MathHelper.ceilDiv(this.width, 2) : this.width;
	}

	/**
	 * Overwritten because this value is used pervasively and I'd like to avoid an
	 * allocation here.
	 */
	@Overwrite
	public int getScaledWidth() {
		final var mode = StereoConfig.INSTANCE.stereoModeOption.getValue();
		return mode.isSideBySide() ? MathHelper.ceilDiv(this.scaledWidth, 2) : this.scaledWidth;
	}

}
