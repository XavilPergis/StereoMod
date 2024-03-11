package net.avitech.testbed.feature.anaglyph.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.avitech.testbed.feature.anaglyph.StereoConfig;
import net.avitech.testbed.feature.anaglyph.StereoRenderer;
import net.avitech.testbed.feature.anaglyph.WindowAccessor;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.MathHelper;

@Mixin(Window.class)
public abstract class WindowMixin implements AutoCloseable, WindowAccessor {

	private int width;
	private int framebufferWidth;
	private int scaledWidth;

	@Override
	public int testbed_getPhysicalWidth() {
		return this.framebufferWidth;
	}

	@Overwrite
	public int getFramebufferWidth() {
		final var mode = StereoConfig.INSTANCE.stereoModeOption.getValue();
		return mode.isSideBySide() ? MathHelper.ceilDiv(this.framebufferWidth, 2) : this.framebufferWidth;
	}
	
	@Overwrite
	public int getWidth() {
		final var mode = StereoConfig.INSTANCE.stereoModeOption.getValue();
		return mode.isSideBySide() ? MathHelper.ceilDiv(this.width, 2) : this.width;
	}

	@Overwrite
	public int getScaledWidth() {
		final var mode = StereoConfig.INSTANCE.stereoModeOption.getValue();
		return mode.isSideBySide() ? MathHelper.ceilDiv(this.scaledWidth, 2) : this.scaledWidth;
	}

}
