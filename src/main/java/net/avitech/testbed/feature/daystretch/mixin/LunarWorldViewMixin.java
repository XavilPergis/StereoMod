package net.avitech.testbed.feature.daystretch.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.avitech.testbed.feature.daystretch.DimensionTypeFixedTimeAccessor;
import net.avitech.testbed.feature.daystretch.TimeTickableWorld;
import net.avitech.testbed.feature.daystretch.TimeTicker;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LunarWorldView;
import net.minecraft.world.dimension.DimensionType;

@Mixin(LunarWorldView.class)
public interface LunarWorldViewMixin {

    @Inject(method = "getSkyAngle", at = @At("HEAD"), cancellable = true)
    default void wrapGetSkyAngle(float tickDelta, CallbackInfoReturnable<Float> info) {
        LunarWorldView self = (LunarWorldView) (Object) this;

        if (self instanceof TimeTickableWorld) {
            TimeTicker timeTicker = ((TimeTickableWorld) self).getTimeTicker();

            double time = self.getLunarTime();
            time += timeTicker.getRemainder();
            time += tickDelta * timeTicker.getSpeedFactor();

            info.setReturnValue(getSkyAngleFromWorld(self, time));
        } else {
            info.setReturnValue(getSkyAngleFromWorld(self, self.getLunarTime()));
        }
    }

    private static float getSkyAngleFromWorld(LunarWorldView worldView, double timeOfDay) {
        return getSkyAngleFromWorld(worldView.getDimension(), timeOfDay);
    }

    private static float getSkyAngleFromWorld(DimensionType dimensionType, double timeOfDay) {
        double time = dimensionType.fixedTime().isPresent()
                ? (double) dimensionType.fixedTime().getAsLong()
                : timeOfDay;

        double a = MathHelper.fractionalPart(time / 24000.0 - 0.25);
        double b = 0.5 - Math.cos(a * Math.PI) / 2.0;
        return (float) (a * 2.0 + b) / 3.0f;
    }

}
