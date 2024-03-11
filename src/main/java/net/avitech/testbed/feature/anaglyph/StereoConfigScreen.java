package net.avitech.testbed.feature.anaglyph;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class StereoConfigScreen extends Screen {

	private Screen previousScreen;

	protected StereoConfigScreen(@Nullable Screen previousScreen) {
		super(Text.translatable("testbed.screen.config.stereo_render"));
		this.previousScreen = previousScreen;
	}

	@Override
	public void close() {
		this.client.setScreen(this.previousScreen);
	}

	@Override
	protected void init() {
		super.init();
	}
	
}
