package net.avitech.stretchydays.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.avitech.stretchydays.StretchyDaysMod;
import net.minecraft.client.world.ClientWorld;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
    @Invoker("setTimeOfDay(L)V")
    protected abstract void invokeSetTimeOfDay(long time);

    private int ticksSinceLastTimeAdvance = 0;

    @Redirect(method = "tickTime()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;setTimeOfDay(J)V"))
    private void redirectTimeOfDayTick(ClientWorld clientWorld, long requestedTime) {
        long advance = StretchyDaysMod.getTimeAdvance(clientWorld.getLevelProperties().getTimeOfDay(),
                ++ticksSinceLastTimeAdvance);
        if (advance != 0L) {
            invokeSetTimeOfDay(clientWorld.getLevelProperties().getTimeOfDay() + advance);
            ticksSinceLastTimeAdvance = 0;
        }
    }
}
