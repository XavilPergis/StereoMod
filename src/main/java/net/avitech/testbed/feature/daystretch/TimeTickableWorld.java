package net.avitech.testbed.feature.daystretch;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.World;

public interface TimeTickableWorld {
    @NotNull
    TimeTicker getTimeTicker();

    @NotNull
    World asMinecraftWorld();
}
