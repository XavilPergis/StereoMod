package net.avitech.testbed.feature.daystretch;

import java.util.OptionalLong;

import org.jetbrains.annotations.NotNull;

public interface DimensionTypeFixedTimeAccessor {
    @NotNull
    OptionalLong getDimensionFixedTime();
}
