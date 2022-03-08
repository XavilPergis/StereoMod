package net.avitech.testbed.feature.daystretch;

import java.util.OptionalLong;

import javax.annotation.Nonnull;

public interface DimensionTypeFixedTimeAccessor {
    @Nonnull
    OptionalLong getDimensionFixedTime();
}
