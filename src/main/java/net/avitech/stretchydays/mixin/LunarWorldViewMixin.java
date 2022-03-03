package net.avitech.stretchydays.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.avitech.stretchydays.StretchyDaysMod;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LunarWorldView;

@Mixin(LunarWorldView.class)
public interface LunarWorldViewMixin {
    @Inject(method = "getSkyAngle", at = @At("RETURN"), cancellable = true)
    default void wrapGetSkyAngle(float tickDelta, CallbackInfoReturnable<Float> info) {
        LunarWorldView view = (LunarWorldView) (Object) this;
        StretchyDaysMod.StretchFactor factor = StretchyDaysMod.getStretchFactor(view.getLunarTime());

        float currentUpdateAngle = view.getDimension().getSkyAngle(view.getLunarTime());
        float nextUpdateAngle = factor.mode() == StretchyDaysMod.StretchyMode.SPEEDUP
                ? view.getDimension().getSkyAngle(view.getLunarTime() + factor.factor())
                : view.getDimension().getSkyAngle(view.getLunarTime() + 1)
                        / ((float) factor.factor());

        // so uh, using lerp here like normal doesnt really woek, because at noon, the
        // skyAngle function jumps from its maximum value to its minimum value.
        // intermediate values near 0.5 are interpreted as "night", so the sky would
        // very briefly flash to night.
        //
        // It would be nice to turn the """skyAngle""" into a continuous function, lerp,
        // then map back, but there is not a 1:1 correspondance between the continuous
        // function and the skyAngle function.
        //
        // The "fix" I use here is to just make the sun jump past the problem area lol.
        float angle;
        if (factor.mode() == StretchyDaysMod.StretchyMode.SPEEDUP && nextUpdateAngle >= currentUpdateAngle) {
            angle = MathHelper.lerpAngleDegrees(tickDelta, currentUpdateAngle, nextUpdateAngle);
        } else {
            angle = currentUpdateAngle;
        }

        info.setReturnValue(angle);
    }
}
