package net.avitech.testbed.feature.daystretch;

import javax.annotation.Nonnull;

import net.minecraft.world.World;

public interface TimeTickableWorld {
    @Nonnull
    TimeTicker getTimeTicker();

    @Nonnull
    World asMinecraftWorld();
}
