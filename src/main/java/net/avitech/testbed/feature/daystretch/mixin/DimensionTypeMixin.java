package net.avitech.testbed.feature.daystretch.mixin;

import java.util.OptionalLong;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.avitech.testbed.feature.daystretch.DimensionTypeFixedTimeAccessor;
import net.minecraft.world.dimension.DimensionType;

@Mixin(DimensionType.class)
public abstract class DimensionTypeMixin implements DimensionTypeFixedTimeAccessor {

    @Accessor
    protected abstract OptionalLong getFixedTime();

    @Override
    public OptionalLong getDimensionFixedTime() {
        return getFixedTime();
    }

}
